package net.grayfield.spb.hobbylog.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.movie.repository.MovieRepository;
import net.grayfield.spb.hobbylog.domain.movie.repository.MovieTemplateRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.*;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    @Value("${tmdb.api_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api_token}")
    private String tmdbApiToken;

    @Value("${tmdb.user_email}")
    private String tmdbUserEmail;

    private final FileSystemService fileSystemService;
    private final ImageService imageService;
    private final RestClient movieBaseClient;
    private final MovieRepository movieRepository;
    private final MovieTemplateRepository movieTemplateRepository;

    public MovieRawPage searchMovieFromTMDB(String search, Long page) {
        return movieBaseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/search/movie")
                        .queryParam("query", search.replace(" ", "+"))
                        .queryParam("language", "ko")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("page", page)
                        .build()
                )
                .retrieve()
                .body(MovieRawPage.class);
    }

    public MovieRawDetail getMovieDetail(Long movieId, String language) {
        MovieRawDetail result = null;

        try {
            result = this.movieBaseClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/3/movie/" + movieId)
                            .queryParam("language", language)
                            .build()
                    )
                    .header("Authorization", "Bearer " + tmdbApiToken)
                    .retrieve()
                    .body(MovieRawDetail.class);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }

        return result;
    }

    public MovieRawCredit getMovieCredits(Long movieId, String language) {
        MovieRawCredit result = null;

        try {
            result = this.movieBaseClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/3/movie/" + movieId + "/credits")
                            .queryParam("language", language)
                            .build()
                    )
                    .header("Authorization", "Bearer " + tmdbApiToken)
                    .retrieve()
                    .body(MovieRawCredit.class);
        } catch (Exception ex) {
            log.error("{}", Arrays.toString(ex.getStackTrace()));
            log.error(String.valueOf(ex.getCause()));
        }
        return result;
    }

    public MovieRawKeyword getMovieKeywords(Long movieId) {
        return this.movieBaseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/movie/" + movieId + "/keywords")
                        .build()
                )
                .header("Authorization", "Bearer " + tmdbApiToken)
                .retrieve()
                .body(MovieRawKeyword.class);
    }

    public Result store(
            MovieInput movieInput,
            MovieRawDetail koDetailRaw, MovieRawDetail enDetailRaw, MovieRawCredit creditRaw, MovieRawKeyword keywordRaw
    ) {
        try {
            List<Crew> directors = creditRaw.getCrews().stream().filter(c -> c.getJob().equals("Director")).toList();
            List<Casting> castings = creditRaw.getCasts().stream().filter(c -> c.getDepartment().equals("Acting")).toList();
            List<String> genres = koDetailRaw.getGenres().stream().map(Genre::getName).toList();
            List<String> keywords = keywordRaw.getKeywords().stream().map(Keyword::getName).toList();
            List<String> productions = enDetailRaw.getProductionCompaniesName();

            Long movieId = movieInput.getMovieId();

            Movie movie = new Movie();
            movie.setMovieId(movieId);

            if(movieInput.getId() != null) {
                movie.setId(movieInput.getId());
            }

            String folder = this.fileSystemService.makeMovieFolder(movieId, enDetailRaw.getTitle());
            String localPosterImage = this.imageService.storeMainImage(
                    Category.MOVIE,
                    folder,
                    "https://image.tmdb.org/t/p/w200"
                        + koDetailRaw.getPosterPath(),
                    movieId.toString()
            );

            movie.setAdult(koDetailRaw.getAdult());
            movie.setTitle(koDetailRaw.getTitle());
            movie.setOriginalTitle(enDetailRaw.getOriginalTitle());
            movie.setThumbnail(localPosterImage);
            movie.setBudget(koDetailRaw.getBudget());
            movie.setRevenue(koDetailRaw.getRevenue());
            movie.setOriginalCountry(enDetailRaw.getOriginalCountry());
            movie.setLanguage(koDetailRaw.getOriginalLanguage());
            movie.setDirectors(directors);
            movie.setActors(castings);
            movie.setGenres(genres);
            movie.setKeywords(keywords);
            movie.setSynopsis(koDetailRaw.getOverview());
            movie.setOriginalSynopsis(enDetailRaw.getOverview());
            movie.setContents(movieInput.getContent());
            movie.setRatings(movieInput.getRatings());
            movie.setPopularity(koDetailRaw.getPopularity());
            movie.setVoteAverage(koDetailRaw.getVoteAverage());
            movie.setVoteCount(koDetailRaw.getVoteCount());
            movie.setReleaseDate(koDetailRaw.getReleaseDate());
            movie.setProductions(productions);
            movie.setRuntime(koDetailRaw.getRuntime());
            movie.setTagline(koDetailRaw.getTagline());
            movie.setOriginalTagline(enDetailRaw.getTagline());
            movie.setStatus(Status.DRAFT);
            movie.setLogAt(StaticHelper.generateLogAt(movieInput.getLogAtStr()));

            String resultId = this.movieTemplateRepository.upsertMovie(movie);

            return Result.builder().id(resultId).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder().id(null).success(false).build();
        }
    }

    public void storeRemote(Long id, Integer ratings) {
        if(!StaticHelper.getUserEmail().equals(this.tmdbUserEmail)) return;

        float rating = ratings.floatValue() / 10;
        float underOne = rating - (ratings / 10);

        if (underOne < 0.3) {
            rating = ratings / 10;
        } else if (underOne < 0.7) {
            rating = ratings / 10 + 0.5f;
        } else {
            rating = ratings / 10 + 1f;
        }

        this.movieBaseClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/movie/" + id + "/rating")
                        .build()
                )
                .body(Map.of("value", rating))
                .header("Authorization", "Bearer " + tmdbApiToken)
                .retrieve()
            ;
    }

    public Movie getOneMovie(String id) {
        String userid = StaticHelper.getUserId();

        return this.movieRepository.findMovieByIdAndUserId(id, userid).orElseThrow();
    }

    public Movie updateOneMovie(MovieInput movieInput) {
        Movie movie = this.getOneMovie(movieInput.getId());

        movie.setContents(movieInput.getContent());
        movie.setRatings(movieInput.getRatings());
        movie.setLogAt(StaticHelper.generateLogAt(movieInput.getLogAtStr()));

        this.movieRepository.save(movie);

        return movie;
    }
}
