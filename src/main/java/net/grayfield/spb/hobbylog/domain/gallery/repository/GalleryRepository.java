package net.grayfield.spb.hobbylog.domain.gallery.repository;

import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GalleryRepository extends MongoRepository<Gallery, Long> {
    Optional<Gallery> findGalleryById(String id);

    Optional<Gallery> findGalleryByIdAndUserId(String id, String userid);
}
