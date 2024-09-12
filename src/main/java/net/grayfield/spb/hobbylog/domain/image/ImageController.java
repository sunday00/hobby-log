package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.struct.AddSubImageInput;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final FileSystemService fileSystemService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result addSubImage(@Argument AddSubImageInput addSubImageInput) throws FileNotFoundException {
        LocalDateTime logAt = StaticHelper.generateLogAt(addSubImageInput.getLogAtStr());
        String folder = this.fileSystemService.makeCategoryImageFolder(addSubImageInput.getCategory(), logAt);

        String path = this.imageService.storeSubImage(
                folder,
                addSubImageInput.getUrl(),
                addSubImageInput.getId(),
                addSubImageInput.getSubId(),
                addSubImageInput.getWidth() != null ? addSubImageInput.getWidth() : 1024
        );

        this.imageService.storeToDatabase(path, addSubImageInput.getId(), ImageUsedAs.SUB, addSubImageInput.getSubId() + "");

        return Result.builder().id(addSubImageInput.getId()).success(true).message(path).build();
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result deleteImage(@Argument String path) {
        return this.imageService.deleteByPath(path);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Result clearUseless () throws FileNotFoundException {
        return this.imageService.clearUseless();
    }
}
