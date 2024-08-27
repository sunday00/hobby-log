package net.grayfield.spb.hobbylog.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryRepository;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryTemplateRepository;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.gallery.struct.GalleryInput;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        gallery.setLogAt(logAt);

        String updateResultId = this.galleryTemplateRepository.upsertGallery(gallery);

        gallery.setId(updateResultId);

        return gallery;
    }

    public Gallery getOneGalleryById(String id) {
        String userid = StaticHelper.getUserId();

        return this.galleryRepository.findGalleryByIdAndUserId(id, userid).orElseThrow();
    }

    public Gallery updateOneGallery(GalleryInput galleryInput) throws FileNotFoundException {
        Gallery gallery = this.getOneGalleryById(galleryInput.getId());

        LocalDateTime logAt = StaticHelper.generateLogAt(galleryInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(galleryInput, logAt);

        gallery.setTitle(galleryInput.getTitle());
        gallery.setThumbnail(thumbnail);
        gallery.setRatings(galleryInput.getRatings());
        gallery.setGalleryType(galleryInput.getGalleryType());
        gallery.setLocation(galleryInput.getLocation());
        gallery.setOverview(galleryInput.getOverview());
        gallery.setContent(galleryInput.getContent());

        this.galleryRepository.save(gallery);

        return gallery;
    }
}
