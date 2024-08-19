package net.grayfield.spb.hobbylog.domain.share;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.service.CommonService;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.UpdateStatusInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result updateStatus (@Argument UpdateStatusInput updateStatusInput, GraphQLContext context, DataFetchingEnvironment e) {
        return this.commonService.updateStatus(updateStatusInput);
    }
}
