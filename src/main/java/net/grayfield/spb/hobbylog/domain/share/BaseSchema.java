package net.grayfield.spb.hobbylog.domain.share;

import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class BaseSchema {
    @Id
    protected Long id;

    protected String userId;

    protected Category category;

    protected String title;

    protected LocalDateTime logAt;
}
