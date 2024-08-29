package net.grayfield.spb.hobbylog.domain.share.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.repository.HobbyTemplateRepository;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.UpdateStatusInput;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    private final HobbyTemplateRepository hobbyTemplateRepository;

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
            return this.hobbyTemplateRepository.findByMonth(yyyy, mm);
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

    public Result deleteOneLog(Category category, String id) {
        try {
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
