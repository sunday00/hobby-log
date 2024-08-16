package net.grayfield.spb.hobbylog.domain.gallery.repository;

import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GalleryRepository extends MongoRepository<Gallery, Long> {

}
