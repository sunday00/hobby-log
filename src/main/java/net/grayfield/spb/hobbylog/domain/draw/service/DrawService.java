package net.grayfield.spb.hobbylog.domain.draw.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.draw.repository.DrawRepository;
import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import net.grayfield.spb.hobbylog.domain.draw.struct.DrawInput;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final DrawRepository drawRepository;

    public String storeThumbnail(DrawInput drawInput, String folder, LocalDateTime logAt) {
        return this.imageService.storeMainImage(Category.DRAW, folder, drawInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public String storeDrawImage(DrawInput drawInput, String folder) {
        String id = NanoIdUtils.randomNanoId();
        return this.imageService.storeSubImage(folder, drawInput.getMainImage(), id, 0, 1024);
    }

    public Draw createDraw(DrawInput drawInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();

        LocalDateTime logAt = StaticHelper.generateLogAt(drawInput.getLogAtStr());
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.DRAW, logAt);
        String thumbnail = this.storeThumbnail(drawInput, folder, logAt);
        String mainImage = this.storeDrawImage(drawInput, folder);

        Draw draw = new Draw();
        draw.setUserId(userId);
        draw.setCategory(Category.DRAW);
        draw.setTitle(drawInput.getTitle());
        draw.setThumbnail(thumbnail);
        draw.setRatings(0);
        draw.setStatus(Status.DRAFT);
        draw.setContent(drawInput.getContent());
        draw.setMainImage(mainImage);
        draw.setLogAt(logAt);
        draw.setDrawType(drawInput.getDrawType());

        Draw stored = this.drawRepository.save(draw);
        this.imageService.storeToDatabase(thumbnail, stored.getId(), ImageUsedAs.MAIN, "thumbnail");
        this.imageService.storeToDatabase(mainImage, stored.getId(), ImageUsedAs.MAIN, "body");

        return stored;
    }

    public Draw getOneDrawById(String id, List<SelectedField> selectedFields) {
        Draw draw = this.drawRepository.findById(id).orElseThrow();

        if(!selectedFields.isEmpty()) {
            draw.setSubImages(this.imageService.getAllSubImagesByMainId(draw.getId()));
        }

        return draw;
    }

    public Draw getOneDrawById(String id, String userId) {
        return this.drawRepository.findByIdAndUserId(id, userId).orElseThrow();
    }

    public Draw updateDraw(DrawInput drawInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();
        Draw draw = this.getOneDrawById(drawInput.getId(), userId);

        LocalDateTime logAt = StaticHelper.generateLogAt(drawInput.getLogAtStr());
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.DRAW, logAt);
        String thumbnail = this.storeThumbnail(drawInput, folder, logAt);
        String mainImage = this.storeDrawImage(drawInput, folder);

        draw.setTitle(drawInput.getTitle());
        draw.setThumbnail(thumbnail);
        draw.setContent(drawInput.getContent());
        draw.setMainImage(mainImage);
        draw.setDrawType(drawInput.getDrawType());

        if(drawInput.getLogAtStr() != null) {
            draw.setLogAt(logAt);
        }

        if(drawInput.getStatus() != null) {
            draw.setStatus(drawInput.getStatus());
        }

        this.drawRepository.save(draw);
        this.imageService.updateToDatabase(thumbnail, draw.getId(), "thumbnail");
        this.imageService.updateToDatabase(mainImage, draw.getId(), "body");

        return draw;
    }
}
