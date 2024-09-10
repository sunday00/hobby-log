package net.grayfield.spb.hobbylog.domain.image.struct;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

@Data
@Document
public class ImageEntity {
    @Id
    private String id;

    private String path;
    private String usedBy;
    private ImageUsedAs usedAs;

    @Nullable
    private String flag;
}
