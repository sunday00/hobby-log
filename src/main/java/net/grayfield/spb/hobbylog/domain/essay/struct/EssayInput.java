package net.grayfield.spb.hobbylog.domain.essay.struct;

import lombok.Data;

@Data
public class EssayInput {
    private String id;
    private String title;
    private String content;
    private WritingType writingType;
    private String seriesName;
    private String thumbnail;
    private String logAtStr;
}
