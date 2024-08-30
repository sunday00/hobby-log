package net.grayfield.spb.hobbylog.domain.read;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.read.service.ReadService;
import net.grayfield.spb.hobbylog.domain.read.struct.Read;
import net.grayfield.spb.hobbylog.domain.read.struct.ReadInput;
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
public class ReadController {
    private final ReadService readService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result createReadLog(@Argument ReadInput readInput) {
        try {
            Read read = this.readService.createRead(readInput);

            return Result.builder().id(read.getId()).success(true).build();
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
    public Read getOneRead(@Argument String id) {
        return this.readService.getOneReadById(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result updateReadLog(@Argument ReadInput readInput) {
        try {
            Read read = this.readService.updateRead(readInput);

            return Result.builder().id(read.getId()).success(true).build();
        }  catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder().id(null).success(false).build();
        }
    }
}
