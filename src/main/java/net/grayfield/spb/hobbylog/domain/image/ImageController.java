package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.Category;
import net.grayfield.spb.hobbylog.domain.share.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @QueryMapping
    public Result addSubImage(@Argument Category category, @Argument Long id, @Argument String url, @Argument int serial)  {
        if( url.startsWith("data:image/") ) {
            String path = this.imageService.storeFromBase64(category, id, url, serial);
            log.info("path: {}", path);
        }

        return Result.builder().id(id).success(true).build();
    }
}
