package net.grayfield.spb.hobbylog.domain.essay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayRepository;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayTemplateRepository;
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
    private final EssayTemplateRepository essayTemplateRepository;

    public String storeThumbnail(EssayInput essayInput, LocalDateTime logAt) throws FileNotFoundException {
        if(essayInput.getThumbnail() == null) { return null; }
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
        essay.setStatus(essayInput.getStatus() != null ? essayInput.getStatus() : Status.DRAFT);
        essay.setContent(essayInput.getContent());
        essay.setWritingType(essayInput.getWritingType());

        if (essayInput.getSeriesName() != null) {
            essay.setSeriesName(essayInput.getSeriesName());
        }

        if(essayInput.getSeriesKey() != null) {
            essay.setSeriesKey(essayInput.getSeriesKey());
        }

        essay.setLogAt(logAt);

        Essay stored = this.essayRepository.save(essay);
        if(essayInput.getSeriesName() != null && essayInput.getSeriesKey() == null) {
            stored.setSeriesKey(stored.getId());
            stored = this.essayRepository.save(stored);
        }

        return stored;
    }

    public Essay getOneEssayById(String id) {
        return this.essayTemplateRepository.getOneEssayByIdWithSeries(id);
    }

    public Essay getOneEssayById(String id, String userId) {
        return this.essayRepository.findByIdAndUserId(id, userId).orElseThrow();
    }

    public Essay updateEssay(EssayInput essayInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();
        Essay essay = this.getOneEssayById(essayInput.getId(), userId);

        LocalDateTime logAt = StaticHelper.generateLogAt(essayInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(essayInput, logAt);

        essay.setTitle(essayInput.getTitle());
        essay.setContent(essayInput.getContent());
        essay.setWritingType(essayInput.getWritingType());
        essay.setSeriesName(essayInput.getSeriesName());
        essay.setThumbnail(thumbnail);

        if(essayInput.getLogAtStr() != null) {
            essay.setLogAt(logAt);
        }

        if(essayInput.getStatus() != null) {
            essay.setStatus(essayInput.getStatus());
        }

        this.essayRepository.save(essay);

        return essay;
    }
}
