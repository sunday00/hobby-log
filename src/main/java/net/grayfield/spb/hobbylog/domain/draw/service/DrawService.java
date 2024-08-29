package net.grayfield.spb.hobbylog.domain.draw.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.draw.repository.DrawRepository;
import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import net.grayfield.spb.hobbylog.domain.draw.struct.DrawInput;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final DrawRepository drawRepository;

    public String storeThumbnail(DrawInput drawInput, String folder, LocalDateTime logAt) throws FileNotFoundException {
        return this.imageService.storeMainImage(Category.DRAW, folder, drawInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public String storeDrawImage(DrawInput drawInput, String folder) throws FileNotFoundException {
        String id = NanoIdUtils.randomNanoId();
        String path = this.imageService.storeSubImage(folder, drawInput.getMainImage(), id, 0, 1024);
        return String.format("<div><img src=\"%s\" alt=\"%s\" class=\"main-image\" /></div>\n", path, drawInput.getTitle());
    }

    public Draw createDraw(DrawInput drawInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();

        LocalDateTime logAt = StaticHelper.generateLogAt(drawInput.getLogAtStr());
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.DRAW, logAt);
        String thumbnail = this.storeThumbnail(drawInput, folder, logAt);

        Draw draw = new Draw();
        draw.setUserId(userId);
        draw.setCategory(Category.DRAW);
        draw.setTitle(drawInput.getTitle());
        draw.setThumbnail(thumbnail);
        draw.setRatings(0);
        draw.setStatus(Status.DRAFT);
        draw.setContent(this.storeDrawImage(drawInput, folder) + drawInput.getContent());
        draw.setLogAt(logAt);
        draw.setDrawType(drawInput.getDrawType());

        return this.drawRepository.save(draw);
    }

    public Draw getOneDrawById(String id) {
        return this.drawRepository.findById(id).orElseThrow();
    }

    public Draw updateDraw(DrawInput drawInput) throws FileNotFoundException {
        Draw draw = this.getOneDrawById(drawInput.getId());

        LocalDateTime logAt = StaticHelper.generateLogAt(drawInput.getLogAtStr());
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.DRAW, logAt);
        String thumbnail = this.storeThumbnail(drawInput, folder, logAt);

        draw.setTitle(drawInput.getTitle());
        draw.setThumbnail(thumbnail);
        draw.setContent(this.storeDrawImage(drawInput, folder) + drawInput.getContent());
        draw.setDrawType(drawInput.getDrawType());

        if(drawInput.getLogAtStr() != null) {
            draw.setLogAt(logAt);
        }

        if(drawInput.getStatus() != null) {
            draw.setStatus(drawInput.getStatus());
        }

        this.drawRepository.save(draw);

        return draw;
    }
}
