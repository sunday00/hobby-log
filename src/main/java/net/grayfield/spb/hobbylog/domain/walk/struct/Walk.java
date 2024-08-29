package net.grayfield.spb.hobbylog.domain.walk.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Walk extends BaseSchema {
    private String content;
    private int steps;
    private int distance;
    private int duration;
}
