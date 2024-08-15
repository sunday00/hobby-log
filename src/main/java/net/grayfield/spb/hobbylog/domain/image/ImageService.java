package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final String CLASS_PATH;

    public String storeFromUrl (String url) {
        String fullFilePath = "";

        try {
            BufferedImage image = ImageIO.read(URI.create(url).toURL());

            String folder = this.makeFolder();
            String newFileName = this.generateNewName();
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(CLASS_PATH + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String makeFolder () {
        String folderPath = "/images/upload/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

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
}
