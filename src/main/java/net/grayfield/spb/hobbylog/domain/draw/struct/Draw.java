package net.grayfield.spb.hobbylog.domain.draw.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Draw extends BaseSchema {
    private String content;
    private DrawType drawType;
    private String mainImage;
}
