package net.grayfield.spb.hobbylog.domain.share.struct;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageEntity;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

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

    @Nullable
    protected List<ImageEntity> subImages;
}
