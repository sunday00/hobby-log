package net.grayfield.spb.hobbylog.domain.draw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.draw.service.DrawService;
import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import net.grayfield.spb.hobbylog.domain.draw.struct.DrawInput;
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
public class DrawController {
    private final DrawService drawService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result createDrawLog(@Argument DrawInput drawInput) {
        try {
            Draw draw = this.drawService.createDraw(drawInput);

            return Result.builder()
                    .id(draw.getId()).success(true)
                    .build();
        } catch (Exception ex) {
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
    public Draw getOneDraw(@Argument String id) {
        return this.drawService.getOneDrawById(id);
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result updateDrawLog(@Argument DrawInput drawInput) {
        try {
            Draw draw = this.drawService.updateDraw(drawInput);

            return Result.builder()
                    .id(draw.getId()).success(true)
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder()
                    .id(drawInput.getId()).success(false)
                    .message(ex.getMessage())
                    .status(500)
                    .build();
        }
    }
}
