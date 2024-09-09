package net.grayfield.spb.hobbylog.domain.share.struct;

import lombok.Data;

import java.util.List;

@Data
public class SearchPagination {
    private Long totalCount;
    private List<BaseSchema> hobbies;
}
