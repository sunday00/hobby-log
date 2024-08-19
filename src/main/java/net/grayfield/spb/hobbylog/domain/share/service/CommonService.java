package net.grayfield.spb.hobbylog.domain.share.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.repository.HobbyTemplateRepository;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.UpdateStatusInput;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    private final HobbyTemplateRepository hobbyTemplateRepository;

    public Result updateStatus(UpdateStatusInput updateStatusInput) {
        try {
            this.hobbyTemplateRepository.updateStatus(updateStatusInput.getCategory(), updateStatusInput.getId(), updateStatusInput.getStatus());
            return Result.builder().id(updateStatusInput.getId()).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            log.error("{}", ex.getCause());
            log.error("{}", Arrays.stream(ex.getStackTrace()).toList());
            return Result.builder().id(updateStatusInput.getId()).success(false).build();
        }
    }
}
