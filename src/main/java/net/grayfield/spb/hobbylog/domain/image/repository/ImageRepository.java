package net.grayfield.spb.hobbylog.domain.image.repository;

import net.grayfield.spb.hobbylog.domain.image.struct.ImageEntity;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends MongoRepository<ImageEntity, String> {
    Optional<ImageEntity> findOneImageEntityByUsedBy(String usedBy);

    Optional<ImageEntity> findOneImageEntityByUsedByAndFlag(String usedBy, String flag);

    List<ImageEntity> findAllByUsedByAndUsedAs(String mainId, ImageUsedAs usedAs);

    Optional<ImageEntity> deleteByPath(String path);
}
