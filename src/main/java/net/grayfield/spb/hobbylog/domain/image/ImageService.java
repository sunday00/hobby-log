package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.Category;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final String CLASS_PATH;

    public String storeFromUrl (String url) {
        String fullFilePath = "";

        try {
            BufferedImage image = ImageIO.read(URI.create(url).toURL());

            String folder = this.makeFolder(true);
            String newFileName = this.generateNewName();
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(CLASS_PATH + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeFromUrl (String url, Movie movie) {
        String fullFilePath = "";

        try {
            BufferedImage image = ImageIO.read(URI.create(url).toURL());

            String folder = this.makeFolder(false);
            String newFileName = this.generateMovieImageName(movie);
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(CLASS_PATH + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeFromUrl (Category category, String url, LocalDateTime localDateTime) {
        String fullFilePath = "";

        try {

            BufferedImage image = ImageIO.read(URI.create(url).toURL());

            image = this.resizeImage(image);

            String folder = this.makeFolder(false);
            String newFileName = this.generateCategoryImageName(category, localDateTime);
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(CLASS_PATH + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeFromBase64(Category category, Long id, String base64Str, int serial) {
        String fullFilePath = "";

        try {
            String[] sourceArr = base64Str.split(",");

            byte[] decoded = Base64.getDecoder().decode(sourceArr[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            image = this.resizeImage(image);
            String folder = this.makeFolder(false);
            String newFileName = this.generateSubImageName(category, id, serial);

            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(CLASS_PATH + fullFilePath));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return fullFilePath;
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

    public String makeFolder (boolean withDateFolder) {
        String folderPath = "/images/upload/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        if(withDateFolder)  {
            folderPath = folderPath + FileSystems.getDefault().getSeparator() +  LocalDate.now().format(DateTimeFormatter.ofPattern("dd"));
        }

        File uploadPathFolder = new File(CLASS_PATH + folderPath);

        if(!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }

        return folderPath;
    }

    public String generateNewName() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getId();

        String hms = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

        Faker faker = new Faker();
        String randomStr = faker.lorem().characters(8);

        return userId +"-"+ hms +"-"+ randomStr;
    }

    public String generateMovieImageName(Movie movie) {
        return "movie-poster-" + movie.getId();
    }

    public String generateCategoryImageName(Category category, LocalDateTime localDateTime) {
        return category.name().toLowerCase() + "-" + localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd-HH"));
    }

    public String generateSubImageName(Category category, Long id, int serial) {
        return category.name().toLowerCase() + "-" + id + "-sub-" + serial;
    }
}
