package net.grayfield.spb.hobbylog.domain.essay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayRepository;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.essay.struct.EssayInput;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EssayService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final EssayRepository essayRepository;

    public String storeThumbnail(EssayInput essayInput, LocalDateTime logAt) throws FileNotFoundException {
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.ESSAY, logAt);
        return this.imageService.storeMainImage(Category.ESSAY, folder, essayInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public Essay createEssay(EssayInput essayInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();

        LocalDateTime logAt = StaticHelper.generateLogAt(essayInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(essayInput, logAt);

        Essay essay = new Essay();
        essay.setUserId(userId);
        essay.setCategory(Category.ESSAY);
        essay.setTitle(essayInput.getTitle());
        essay.setThumbnail(thumbnail);
        essay.setRatings(0);
        essay.setStatus(Status.DRAFT);
        essay.setTitle(essayInput.getTitle());
        essay.setContent(essayInput.getContent());
        essay.setWritingType(essayInput.getWritingType());
        essay.setSeriesName(essay.getSeriesName());
        essay.setLogAt(logAt);

        return this.essayRepository.save(essay);
    }
}
