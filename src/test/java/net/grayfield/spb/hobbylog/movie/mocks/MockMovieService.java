package net.grayfield.spb.hobbylog.movie.mocks;

import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieInput;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawCredit;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawDetail;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawKeyword;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;

import java.util.ArrayList;
import java.util.List;

public class MockMovieService extends MovieService {
    public List<String> called;

    public MockMovieService() {
        super(null, null, null, null, null);
        this.called = new ArrayList<String>();
    }

    @Override
    public MovieRawDetail getMovieDetail(Long movieId, String language) {
        this.called.add("detail");
        return new MovieRawDetail();
    }

    @Override
    public MovieRawCredit getMovieCredits(Long movieId, String language) {
        this.called.add("credit");
        return new MovieRawCredit();
    }

    @Override
    public MovieRawKeyword getMovieKeywords(Long movieId) {
        this.called.add("keyword");
        return new MovieRawKeyword();
    }

    @Override
    public void storeRemote(Long movieId, Integer ratings) {
        this.called.add("storeRemote");
    }

    @Override
    public Result store (MovieInput movieInput, MovieRawDetail koDetailRaw, MovieRawDetail enDetailRaw,
                         MovieRawCredit creditRaw, MovieRawKeyword keywordRaw) {
        this.called.add("store");
        return Result.builder().id("1").success(true).build();
    }
}
