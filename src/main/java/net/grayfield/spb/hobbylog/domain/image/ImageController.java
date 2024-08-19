package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.struct.AddSubImageInput;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @MutationMapping
    public Result addSubImage(@Argument AddSubImageInput addSubImageInput)  {
        String path = "";

        if( addSubImageInput.getUrl().startsWith("data:image/") ) {
            path = this.imageService.storeFromBase64(
                    addSubImageInput.getCategory(),
                    addSubImageInput.getId(),
                    addSubImageInput.getUrl(),
                    addSubImageInput.getSerial()
            );
        } else {
            path = this.imageService.storeFromRemoteUrlSub(
                    addSubImageInput.getCategory(),
                    addSubImageInput.getId(),
                    addSubImageInput.getUrl(),
                    addSubImageInput.getSerial()
            );
        }

        log.info("path: {}", path);

        return Result.builder().id(addSubImageInput.getId()).success(true).build();
    }
}
