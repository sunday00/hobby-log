package net.grayfield.spb.hobbylog.domain.walk.service;

import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import net.grayfield.spb.hobbylog.domain.walk.repository.WalkRepository;
import net.grayfield.spb.hobbylog.domain.walk.struct.Walk;
import net.grayfield.spb.hobbylog.domain.walk.struct.WalkInput;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalkService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final WalkRepository walkRepository;

    public String storeThumbnail(WalkInput walkInput, LocalDateTime logAt) throws FileNotFoundException {
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.WALK, logAt);
        return this.imageService.storeMainImage(Category.WALK, folder, walkInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public Walk createWalk(WalkInput walkInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();

        LocalDateTime logAt = StaticHelper.generateLogAt(walkInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(walkInput, logAt);

        Walk walk = new Walk();
        walk.setUserId(userId);
        walk.setCategory(Category.WALK);
        walk.setTitle(walkInput.getTitle());
        walk.setThumbnail(thumbnail);
        walk.setRatings(0);
        walk.setStatus(walkInput.getStatus() != null ? walkInput.getStatus() : Status.DRAFT);
        walk.setContent(walkInput.getContent());
        walk.setLogAt(logAt);
        walk.setSteps(walkInput.getSteps());
        walk.setDistance(walkInput.getDistance());
        walk.setDuration(walkInput.getDuration());

        Walk stored = this.walkRepository.save(walk);
        this.imageService.storeToDatabase(thumbnail, walk.getId(), ImageUsedAs.MAIN, "thumbnail");

        return stored;
    }

    public Walk getOneWalkById(String id, List<SelectedField> selectedFields) {
        Walk walk = this.walkRepository.findById(id).orElseThrow();

        if(!selectedFields.isEmpty()) {
            walk.setSubImages(this.imageService.getAllSubImagesByMainId(walk.getId()));
        }

        return walk;
    }

    public Walk getOneWalkById(String id, String userId) {
        return this.walkRepository.findByIdAndUserId(id, userId).orElseThrow();
    }

    public Walk updateWalk(WalkInput walkInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();
        Walk walk = this.getOneWalkById(walkInput.getId(), userId);

        LocalDateTime logAt = StaticHelper.generateLogAt(walkInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(walkInput, logAt);

        walk.setTitle(walkInput.getTitle());
        walk.setThumbnail(thumbnail);
        walk.setContent(walkInput.getContent());
        walk.setSteps(walkInput.getSteps());
        walk.setDistance(walkInput.getDistance());
        walk.setDuration(walkInput.getDuration());

        if(walkInput.getLogAtStr() != null) {
            walk.setLogAt(logAt);
        }

        if(walkInput.getStatus() != null) {
            walk.setStatus(walkInput.getStatus());
        }

        this.walkRepository.save(walk);
        this.imageService.updateToDatabase(thumbnail, walk.getId(), "thumbnail");

        return walk;
    }
}
