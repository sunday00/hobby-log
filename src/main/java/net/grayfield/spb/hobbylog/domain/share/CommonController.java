package net.grayfield.spb.hobbylog.domain.share;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.service.CommonService;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.UpdateStatusInput;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommonController {
    private final CommonService commonService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result updateStatus (@Argument UpdateStatusInput updateStatusInput, GraphQLContext context, DataFetchingEnvironment e) {
        return this.commonService.updateStatus(updateStatusInput);
    }

    @QueryMapping
    public List<BaseSchema> monthHobby (@Argument String yyyy, @Argument String mm) {
        return this.commonService.findByMonth(yyyy, mm);
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @QueryMapping
    public List<BaseSchema> monthNonActiveHobby (@Argument String yyyy, @Argument String mm) {
        return this.commonService.findNonActiveByMonth(yyyy, mm);
    }

    @QueryMapping
    public List<BaseSchema> yearByCategory (@Argument String yyyy, @Argument Category category) {
        return this.commonService.findByYearAndCategory(yyyy, category);
    }

    @QueryMapping
    public List<BaseSchema> searchHobby(@Argument String search, @Argument Long page) {
        return this.commonService.searchHobby(search, page);
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result deleteLog(@Argument Category category, @Argument String id, @Argument String flag) {
        return this.commonService.deleteOneLog(category, id, flag);
    }
}
