package net.grayfield.spb.hobbylog.domain.essay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.service.EssayService;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.essay.struct.EssayInput;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EssayController {
    private final EssayService essayService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result createEssayLog(@Argument EssayInput essayInput) {
        try {
            Essay essay = this.essayService.createEssay(essayInput);

            return Result.builder().id(essay.getId()).success(true).build();
        }  catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder()
                    .id(null).success(false)
                    .message(ex.getMessage())
                    .status(500)
                    .build();
        }
    }

    @QueryMapping
    public Essay getOneEssay(@Argument String id) {
        return this.essayService.getOneEssayById(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result updateEssayLog(@Argument EssayInput essayInput) {
        try {
            Essay essay = this.essayService.updateEssay(essayInput);

            return Result.builder().id(essay.getId()).success(true).build();
        }  catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder().id(null).success(false).build();
        }
    }
}
