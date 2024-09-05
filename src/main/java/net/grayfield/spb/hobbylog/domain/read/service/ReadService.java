package net.grayfield.spb.hobbylog.domain.read.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.read.repository.ReadRepository;
import net.grayfield.spb.hobbylog.domain.read.struct.Read;
import net.grayfield.spb.hobbylog.domain.read.struct.ReadInput;
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
public class ReadService {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;
    private final ReadRepository readRepository;

    public String storeThumbnail(ReadInput readInput, LocalDateTime logAt) throws FileNotFoundException {
        String folder = this.fileSystemService.makeCategoryImageFolder(Category.READ, logAt);
        return this.imageService.storeMainImage(Category.READ, folder, readInput.getThumbnail(), logAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH")));
    }

    public Read createRead(ReadInput readInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();

        LocalDateTime logAt = StaticHelper.generateLogAt(readInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(readInput, logAt);

        Read read = new Read();
        read.setUserId(userId);
        read.setCategory(Category.READ);
        read.setTitle(readInput.getTitle());
        read.setThumbnail(thumbnail);
        read.setRatings(readInput.getRatings());
        read.setStatus(readInput.getStatus() != null ? readInput.getStatus() : Status.DRAFT);
        read.setWriter(readInput.getWriter());
        read.setOverview(readInput.getOverview());
        read.setContent(readInput.getContent());
        read.setReadType(readInput.getReadType());
        read.setLogAt(logAt);

        return this.readRepository.save(read);
    }

    public Read getOneReadById(String id) {
        return this.readRepository.findById(id).orElseThrow();
    }

    public Read getOneReadById(String id, String userId) {
        return this.readRepository.findByIdAndUserId(id, userId).orElseThrow();
    }

    public Read updateRead(ReadInput readInput) throws FileNotFoundException {
        String userId = StaticHelper.getUserId();
        Read read = this.getOneReadById(readInput.getId(), userId);

        LocalDateTime logAt = StaticHelper.generateLogAt(readInput.getLogAtStr());
        String thumbnail = this.storeThumbnail(readInput, logAt);

        read.setTitle(readInput.getTitle());
        read.setWriter(readInput.getWriter());
        read.setOverview(readInput.getOverview());
        read.setContent(readInput.getContent());
        read.setReadType(readInput.getReadType());
        read.setRatings(readInput.getRatings());
        read.setThumbnail(thumbnail);

        if(readInput.getLogAtStr() != null) {
            read.setLogAt(logAt);
        }

        if(readInput.getStatus() != null) {
            read.setStatus(readInput.getStatus());
        }

        this.readRepository.save(read);

        return read;
    }
}
