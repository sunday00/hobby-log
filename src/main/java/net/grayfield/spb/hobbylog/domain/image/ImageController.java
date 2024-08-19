package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Result addSubImage(@Argument Category category, @Argument Long id, @Argument String url, @Argument int serial)  {
        String path = "";

        if( url.startsWith("data:image/") ) {
            path = this.imageService.storeFromBase64(category, id, url, serial);
        } else {
            path = this.imageService.storeFromRemoteUrlSub(category, id, url, serial);
        }

        log.info("path: {}", path);

        return Result.builder().id(id).success(true).build();
    }
}
