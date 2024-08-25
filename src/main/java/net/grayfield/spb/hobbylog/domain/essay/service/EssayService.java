package net.grayfield.spb.hobbylog.domain.essay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.repository.EssayRepository;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.essay.struct.EssayInput;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EssayService {
    private final ImageService imageService;
    private final EssayRepository essayRepository;

    public Essay createEssay(EssayInput essayInput) {
        String userId = StaticHelper.getUserId();

        Essay essay = new Essay();
        essay.setUserId(userId);
        essay.setCategory(Category.ESSAY);
        essay.setTitle(essayInput.getTitle());
        essay.setThumbnail(imageService.storeWithDefault(Category.ESSAY, essayInput.getThumbnail(), essayInput.getLogAtString()));
        essay.setRatings(0);
        essay.setStatus(Status.DRAFT);
        essay.setTitle(essayInput.getTitle());
        essay.setContent(essayInput.getContent());
        essay.setWritingType(essayInput.getWritingType());
        essay.setSeriesName(essay.getSeriesName());
        essay.setLogAt(StaticHelper.generateLogAt(essayInput.getLogAtString()));

        return this.essayRepository.save(essay);
    }
}
