package net.grayfield.spb.hobbylog.domain.essay.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Essay extends BaseSchema {
    private String content;
    private WritingType writingType;
    private String seriesKey;
    private String seriesName;

    private List<Series> series;
}
