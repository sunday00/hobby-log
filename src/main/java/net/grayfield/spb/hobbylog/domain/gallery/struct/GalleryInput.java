package net.grayfield.spb.hobbylog.domain.gallery.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class GalleryInput {
    @Nullable
    private String id;

    private String title;
    private GalleryType galleryType;
    private String location;
    private String thumbnail;
    private String overview;
    private String content;
    private int ratings;

    @Nullable
    private Status status;

    @Nullable
    private String logAtStr;
}
