package net.grayfield.spb.hobbylog.domain.walk;

import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.walk.service.WalkService;
import net.grayfield.spb.hobbylog.domain.walk.struct.Walk;
import net.grayfield.spb.hobbylog.domain.walk.struct.WalkInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WalkController {
    private final WalkService walkService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result createWalkLog(@Argument WalkInput walkInput) {
        try {
            Walk walk = this.walkService.createWalk(walkInput);

            return Result.builder()
                    .id(walk.getId()).success(true)
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
    public Walk getOneWalk(@Argument String id, DataFetchingEnvironment e) {
        return this.walkService.getOneWalkById(id, e.getSelectionSet().getFields("subImages"));
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result updateWalkLog(@Argument WalkInput walkInput) {
        try {
            Walk walk = this.walkService.updateWalk(walkInput);

            return Result.builder()
                    .id(walk.getId()).success(true)
                    .build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder()
                    .id(walkInput.getId()).success(false)
                    .message(ex.getMessage())
                    .status(500)
                    .build();
        }
    }
}
