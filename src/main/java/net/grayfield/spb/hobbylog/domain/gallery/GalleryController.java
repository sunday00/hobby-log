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

import java.io.FileNotFoundException;
import java.util.Arrays;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GalleryController {
    private final GalleryService galleryService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result createGalleryLog (@Argument GalleryInput galleryInput) throws FileNotFoundException {
        Gallery gallery = this.galleryService.storeGallery(galleryInput);

        return Result.builder().id(gallery.getId()).success(true).build();
    }

    @QueryMapping
    public Gallery getOneGallery (@Argument String id) {
        return this.galleryService.getOneGalleryById(id);
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result updateGalleryLog (@Argument GalleryInput galleryInput) {
        try {
            Gallery gallery = this.galleryService.updateOneGallery(galleryInput);

            return Result.builder().id(gallery.getId()).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));

            return Result.builder().id(null).success(false).build();
        }
    }
}
