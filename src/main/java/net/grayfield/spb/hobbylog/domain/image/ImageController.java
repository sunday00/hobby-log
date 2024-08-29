package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.struct.AddSubImageInput;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result addSubImage(@Argument AddSubImageInput addSubImageInput)  {
        this.imageService.storeSubImage(
                addSubImageInput.getFolder(),
                addSubImageInput.getUrl(),
                addSubImageInput.getId(),
                addSubImageInput.getSubId()
        );

        return Result.builder().id(addSubImageInput.getId()).success(true).build();
    }
}
