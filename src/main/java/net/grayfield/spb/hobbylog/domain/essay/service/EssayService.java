package net.grayfield.spb.hobbylog.domain.essay.service;

import graphql.schema.SelectedField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayRepository;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayTemplateRepository;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.essay.struct.EssayInput;
import net.grayfield.spb.hobbylog.domain.essay.struct.Series;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EssayService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final EssayRepository essayRepository;
    private final EssayTemplateRepository essayTemplateRepository;

    public String storeThumbnail(EssayInput essayInput, LocalDateTime logAt) throws FileNotFoundException {
        if(essayInput.getThumbnail() == null || essayInput.getThumbnail().isEmpty()) { return null; }
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
        essay.setSeriesKey("");
        essay.setSeriesName("");

        if (essayInput.getSeriesName() != null) {
            essay.setSeriesName(essayInput.getSeriesName());
        }

        if(essayInput.getSeriesKey() != null) {
            essay.setSeriesKey(essayInput.getSeriesKey());
        }

        essay.setLogAt(logAt);

        Essay stored = this.essayRepository.save(essay);
        if(!essay.getSeriesName().isEmpty() && essay.getSeriesKey().isEmpty()) {
            stored.setSeriesKey(stored.getId());
            stored = this.essayRepository.save(stored);
        }
        imageService.storeToDatabase(thumbnail, stored.getId(), ImageUsedAs.MAIN, "thumbnail");

        return stored;
    }

    public List<Series> getSeriesListByKeyword(String search) {
        return this.essayTemplateRepository.getSeriesListByKeyword(search);
    }

    public Essay getOneEssayById(String id, List<SelectedField> selectedFields) {
        Essay essay = this.essayTemplateRepository.getOneEssayByIdWithSeries(id);

        if(!selectedFields.isEmpty()) {
            essay.setSubImages(this.imageService.getAllSubImagesByMainId(essay.getId()));
        }

        return essay;
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
        essay.setSeriesKey("");
        essay.setSeriesName("");

        if (essayInput.getSeriesName() != null) {
            essay.setSeriesName(essayInput.getSeriesName());
        }

        if(essayInput.getSeriesKey() != null) {
            essay.setSeriesKey(essayInput.getSeriesKey());
        }

        if(!essay.getSeriesName().isEmpty() && essay.getSeriesKey().isEmpty()) {
            essay.setSeriesKey(essayInput.getId());
        }

        if(essayInput.getLogAtStr() != null) {
            essay.setLogAt(logAt);
        }

        if(essayInput.getStatus() != null) {
            essay.setStatus(essayInput.getStatus());
        }

        this.essayRepository.save(essay);
        this.imageService.updateToDatabase(thumbnail, essay.getId(), "thumbnail");

        return essay;
    }

    public Result deleteSeries(String id) {
        String userId = StaticHelper.getUserId();

        Essay essay = this.getOneEssayById(id, userId);

        if (essay.getId().equals(essay.getSeriesKey())) {
            this.essayRepository.deleteAllBySeriesKey(id);
        } else {
            this.essayRepository.deleteOneById(id);
        }

        return Result.builder().id(essay.getId()).success(true).build();
    }
}
