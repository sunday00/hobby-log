package net.grayfield.spb.hobbylog.domain.share;

import java.util.Date;
import org.springframework.data.annotation.Id;

public abstract class BaseSchema {
    @Id
    protected String id;

    protected String userId;

    protected Category category;

    protected String title;

    protected Date logAt;
}
