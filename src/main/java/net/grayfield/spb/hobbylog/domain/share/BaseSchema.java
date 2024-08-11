package net.grayfield.spb.hobbylog.domain.share;

import lombok.Data;
import java.util.Date;
import org.springframework.data.annotation.Id;

@Data
public class BaseSchema {
    @Id
    protected String id;

    protected String userId;

    protected Category category;

    protected String title;

    protected Date logAt;
}
