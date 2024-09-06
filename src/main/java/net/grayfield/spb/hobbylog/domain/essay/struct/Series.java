package net.grayfield.spb.hobbylog.domain.essay.struct;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Series {
    private String id;
    private String seriesKey;
    private String seriesName;
    private String title;
    private LocalDateTime logAt;
}
