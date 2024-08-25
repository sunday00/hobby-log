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
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class EssayController {
    private final EssayService essayService;

    @MutationMapping
    public Result createEssayLog(@Argument EssayInput essayInput) {
        Essay essay = this.essayService.createEssay(essayInput);

        return Result.builder().id(essay.getId()).success(true).build();
    }
}
