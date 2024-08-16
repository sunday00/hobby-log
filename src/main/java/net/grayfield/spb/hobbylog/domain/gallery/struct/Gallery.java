package net.grayfield.spb.hobbylog.domain.gallery.struct;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.grayfield.spb.hobbylog.domain.share.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.Category;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Gallery extends BaseSchema {
    public Gallery () { this.category = Category.GALLERY; }

    private GalleryType galleryType;

    private String location;

    private String thumbnail;

    private String overview;

    private String content;

    private int ratings;
}
