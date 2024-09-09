package net.grayfield.spb.hobbylog.domain.share.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.service.EssayService;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.share.repository.HobbyTemplateRepository;
import net.grayfield.spb.hobbylog.domain.share.struct.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    private final HobbyTemplateRepository hobbyTemplateRepository;
    private final MovieService movieService;
    private final EssayService essayService;

    public Result updateStatus(UpdateStatusInput updateStatusInput) {
        try {
            BaseSchema result = this.hobbyTemplateRepository.updateStatus(updateStatusInput.getCategory(), updateStatusInput.getId(), updateStatusInput.getStatus());

            if (result == null) {
                return Result.builder()
                        .id(updateStatusInput.getId())
                        .success(false)
                        .status(404)
                        .message("NotFound")
                        .build();
            }

            return Result.builder().id(updateStatusInput.getId()).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", ex.getCause());
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return Result.builder()
                    .id(updateStatusInput.getId())
                    .success(false)
                    .status(500)
                    .message(ex.getMessage())
                    .build();
        }
    }

    public List<BaseSchema> findByMonth(String yyyy, String mm) {
        try {
            // TODO:
            // add only mine
            // add only my followed user
            // currently working all people logs
            return this.hobbyTemplateRepository.findByMonth(yyyy, mm, null);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return List.of();
        }
    }

    public List<BaseSchema> findNonActiveByMonth(String yyyy, String mm) {
        try {
            return this.hobbyTemplateRepository.findNonActiveByMonth(yyyy, mm);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return List.of();
        }
    }

    public List<BaseSchema> findByYearAndCategory(String yyyy, Category category) {
        try {
            // TODO:
            // add only mine
            // add only my followed user
            // currently working all people logs
            return this.hobbyTemplateRepository.findByYearAndCategory(yyyy, category);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return List.of();
        }
    }

    public SearchPagination searchHobby(String search, Long page, @Nullable Category category) {
        try {
            // TODO:
            // add only mine
            // add only my followed user
            // currently working all people logs
            SearchPagination results = this.hobbyTemplateRepository.searchHobby(search, page, category);

            results.setHobbies(results.getHobbies().stream().map(hobby -> {
                if(hobby.getCategory() == Category.ESSAY) {
                    Essay essay = (Essay) hobby;

                    essay.setTitle(
                            essay.getSeriesName() != null
                                    ? "[" + essay.getSeriesName() + "] " + essay.getTitle()
                                    : essay.getTitle()
                    );

                    return essay;
                }

                return hobby;
            }).toList());

            return results;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            SearchPagination result = new SearchPagination();
            result.setHobbies(List.of());
            result.setTotalCount(0L);

            return result;
        }
    }

    public Result deleteOneLog(Category category, String id, String flag) {
        try {
            if(category == Category.MOVIE && !flag.equals("skipRemote")) {
                this.movieService.deleteRemote(id);
            }

            if(category == Category.ESSAY) {
                return this.essayService.deleteSeries(id);
            }

            BaseSchema result = this.hobbyTemplateRepository.deleteOneHobby(category, id);

            if (result == null) {
                return Result.builder()
                        .id(id)
                        .success(false)
                        .status(404)
                        .message("NotFound")
                        .build();
            }

            return Result.builder().id(id).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", ex.getCause());
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return Result.builder()
                    .id(id).success(false)
                    .status(500)
                    .message(ex.getMessage())
                    .build();
        }
    }
}
