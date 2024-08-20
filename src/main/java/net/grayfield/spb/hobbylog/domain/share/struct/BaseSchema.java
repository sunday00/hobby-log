package net.grayfield.spb.hobbylog.domain.share.struct;

import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class BaseSchema {
    @Id
    protected String id;

    protected String userId;

    protected Category category;

    protected String title;

    protected String thumbnail;

    protected int ratings;

    protected LocalDateTime logAt;

    protected Status status;
}
