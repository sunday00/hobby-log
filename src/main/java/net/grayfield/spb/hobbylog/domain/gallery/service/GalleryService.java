package net.grayfield.spb.hobbylog.domain.gallery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryRepository;
import net.grayfield.spb.hobbylog.domain.gallery.repository.GalleryTemplateRepository;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.gallery.struct.GalleryInput;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {
    private final GalleryRepository galleryRepository;
    private final GalleryTemplateRepository galleryTemplateRepository;
    private final ImageService imageService;

    public String storeThumbnail(GalleryInput galleryInput) {
        LocalDateTime localDateTime = galleryInput.getLogAt() != null ? LocalDateTime.parse(galleryInput.getLogAt()) : LocalDateTime.now();
        return this.imageService.storeFromUrl(Category.GALLERY, galleryInput.getThumbnail(), localDateTime);
    }

    public Gallery storeGallery(GalleryInput galleryInput, String thumbnail) {
        LocalDateTime localDateTime = galleryInput.getLogAt() != null ? LocalDateTime.parse(galleryInput.getLogAt()) : LocalDateTime.now();

        Gallery gallery = new Gallery();

        gallery.setTitle(galleryInput.getTitle());
        gallery.setGalleryType(galleryInput.getGalleryType());
        gallery.setLocation(galleryInput.getLocation());
        gallery.setThumbnail(thumbnail);
        gallery.setOverview(galleryInput.getOverview());
        gallery.setContent(galleryInput.getContent());
        gallery.setRatings(galleryInput.getRatings());
        gallery.setLogAt(localDateTime);

        String updateResultId = this.galleryTemplateRepository.upsertGallery(gallery);

        gallery.setId(updateResultId);

        return gallery;
    }

    public Gallery getOneGalleryById(String id) {
        String userid = StaticHelper.getUserId();

        return this.galleryRepository.findGalleryByIdAndUserId(id, userid).orElseThrow();
    }

    public Gallery updateOneGallery(GalleryInput galleryInput, String thumbnail) {
        Gallery gallery = this.getOneGalleryById(galleryInput.getId());

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
