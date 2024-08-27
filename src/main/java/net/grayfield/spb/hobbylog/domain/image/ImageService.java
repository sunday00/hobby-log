package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileSystemService fileSystemService;

    private String classPath() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:static").toString();
    }

    public BufferedImage resizeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) 200 / width;

        height = (int) (height * ratio);

        BufferedImage outputImage =
                new BufferedImage(200, height, image.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, 200, height, null);
        graphics2D.dispose();

        return outputImage;
    }

    public String storeMainImage (Category category, String folder, String urlLikeStr, String identifier) {
        String fullFilePath = "";
        boolean isBase64 = urlLikeStr.startsWith("data:image/");
        LocalDateTime dt = category != Category.MOVIE ? LocalDateTime.parse(identifier) : LocalDateTime.now(ZoneOffset.UTC);

        try {
            switch (category) {
                case MOVIE:
                    fullFilePath = this.storeMovieImage(identifier, folder, urlLikeStr);
                    break;
                case GALLERY:
//                    fullFilePath = this.storeFromUrl(Category.GALLERY, identifier, folder, urlLikeStr);
                    break;
                default:
                    if(isBase64){

                    } else {

                    }
            }
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeMovieImage(String movieId, String folder, String urlLikeStr) {
        String fullFilePath = null;
        try {
            BufferedImage image = ImageIO.read(URI.create(urlLikeStr).toURL());

            image = this.resizeImage(image);

            String newFileName = "movie-" + movieId;
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(classPath() + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }


}
