package net.grayfield.spb.hobbylog.domain.gallery.service;

import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryRepository;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryTemplateRepository;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.gallery.struct.GalleryInput;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {
    private final GalleryRepository galleryRepository;
    private final GalleryTemplateRepository galleryTemplateRepository;
    private final FileSystemService fileSystemService;
    private final ImageService imageService;

    public String storeThumbnail(GalleryInput galleryInput, LocalDateTime logAt) throws FileNotFoundException {
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.GALLERY, logAt);
        return this.imageService.storeMainImage(Category.GALLERY, folder, galleryInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public Gallery storeGallery(GalleryInput galleryInput) throws FileNotFoundException {
        LocalDateTime logAt = StaticHelper.generateLogAt(galleryInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(galleryInput, logAt);

        Gallery gallery = new Gallery();
        gallery.setTitle(galleryInput.getTitle());
        gallery.setGalleryType(galleryInput.getGalleryType());
        gallery.setLocation(galleryInput.getLocation());
        gallery.setThumbnail(thumbnail);
        gallery.setOverview(galleryInput.getOverview());
        gallery.setContent(galleryInput.getContent());
        gallery.setRatings(galleryInput.getRatings());
        gallery.setStatus(galleryInput.getStatus() != null ? galleryInput.getStatus() : Status.DRAFT);
        gallery.setLogAt(logAt);

        String updateResultId = this.galleryTemplateRepository.upsertGallery(gallery);
        gallery.setId(updateResultId);
        imageService.storeToDatabase(thumbnail, updateResultId, ImageUsedAs.MAIN, "thumbnail");

        return gallery;
    }

    public Gallery getOneGalleryById(String id, List<SelectedField> selectedFields) {
        Gallery gallery = this.galleryRepository.findGalleryById(id).orElseThrow();

        if(!selectedFields.isEmpty()) {
            gallery.setSubImages(this.imageService.getAllSubImagesByMainId(gallery.getId()));
        }

        return gallery;
    }

    public Gallery getOneGalleryById(String id, String userId) {
        return this.galleryRepository.findGalleryByIdAndUserId(id, userId).orElseThrow();
    }

    public Gallery updateOneGallery(GalleryInput galleryInput) throws FileNotFoundException {
        String userid = StaticHelper.getUserId();
        Gallery gallery = this.getOneGalleryById(galleryInput.getId(), userid);

        LocalDateTime logAt = StaticHelper.generateLogAt(galleryInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(galleryInput, gallery.getLogAt());

        gallery.setTitle(galleryInput.getTitle());
        gallery.setThumbnail(thumbnail);
        gallery.setRatings(galleryInput.getRatings());
        gallery.setGalleryType(galleryInput.getGalleryType());
        gallery.setLocation(galleryInput.getLocation());
        gallery.setOverview(galleryInput.getOverview());
        gallery.setContent(galleryInput.getContent());

        if(galleryInput.getLogAtStr() != null) {
            gallery.setLogAt(logAt);
        }

        if(galleryInput.getStatus() != null) {
            gallery.setStatus(galleryInput.getStatus());
        }

        this.galleryRepository.save(gallery);
        this.imageService.updateToDatabase(thumbnail, gallery.getId(), "thumbnail");

        return gallery;
    }
}
