package net.grayfield.spb.hobbylog.domain.gallery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.service.GalleryService;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.gallery.struct.GalleryInput;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result createGalleryLog (@Argument GalleryInput galleryInput) {
        String thumbnail = this.galleryService.storeThumbnail(galleryInput);

        Gallery gallery = this.galleryService.storeGallery(galleryInput, thumbnail);

        return Result.builder().id(gallery.getId()).success(true).build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public Gallery getOneGallery (@Argument String id) {
        return this.galleryService.getOneGalleryById(id);
    }
}
