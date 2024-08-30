package net.grayfield.spb.hobbylog.domain.read.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Read extends BaseSchema {
    private String writer;
    private String overview;
    private String content;
    private ReadType readType;
}
