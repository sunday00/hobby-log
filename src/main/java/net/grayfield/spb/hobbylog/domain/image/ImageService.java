package net.grayfield.spb.hobbylog.domain.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.repository.ImageRepository;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageEntity;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageUsedAs;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.schedule.image.ManageImages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    @Value("${spring.config.activate.on-profile}")
    private String env;

    @Value("${storage.path}")
    private String storagePath;

    private final ImageRepository imageRepository;
    private final ManageImages manageImages;

    private String classPath() throws FileNotFoundException {
        if(env.equals("default")) return ResourceUtils.getFile("classpath:static").toString();
        return ResourceUtils.getFile(storagePath).toString();
    }

    public BufferedImage resizeImage(BufferedImage image, int size) {
        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) size / width;

        height = (int) (height * ratio);

        BufferedImage outputImage = new BufferedImage(size, height, image.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, size, height, null);
        graphics2D.dispose();

        return outputImage;
    }

    public String storeToDatabase (String path, String usedBy, ImageUsedAs usedAs, @Nullable String flag) {
        ImageEntity image = new ImageEntity();
        image.setPath(path);
        image.setUsedBy(usedBy);
        image.setUsedAs(usedAs);

        if(flag != null) { image.setFlag(flag);}

        ImageEntity stored = this.imageRepository.save(image);

        return stored.getId();
    }

    public String updateToDatabase (String path, String usedBy, @Nullable String flag) {
        Optional<ImageEntity> image;

        if(flag == null) {
            image = this.imageRepository.findOneImageEntityByUsedBy(usedBy);
        } else {
            image = this.imageRepository.findOneImageEntityByUsedByAndFlag(usedBy, flag);
        }

        if(image.isEmpty()) return null;

        ImageEntity imageEntity = image.get();
        imageEntity.setPath(path);
        this.imageRepository.save(imageEntity);

        return imageEntity.getId();
    }

    public String execStore(BufferedImage image, String ext, String folder, String identifier, int size) throws IOException {
        BufferedImage resizedImage = this.resizeImage(image, size);
        String newFileName = StaticHelper.getUserId() + "-" + identifier;

        String fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

        ImageIO.write(resizedImage, ext, new File(classPath() + fullFilePath));

        return fullFilePath;
    }

    public String storeMainImage (Category category, String folder, String urlLikeStr, String identifier) {
        String fullFilePath = "";
        boolean isBase64 = urlLikeStr.startsWith("data:image/");

        if (urlLikeStr.startsWith("/")) return urlLikeStr;

        try {
            if (Objects.requireNonNull(category) == Category.MOVIE) {
                fullFilePath = this.storeMovieImage(identifier, folder, urlLikeStr);
            } else {
                if (isBase64) {
                    fullFilePath = this.storeCategoryImageFromBase64(identifier, folder, urlLikeStr, 200);
                } else {
                    fullFilePath = this.storeCategoryImageFromUrl(identifier, folder, urlLikeStr, 200);
                }
            }
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeSubImage(String folder, String urlLikeStr, String id, int subId, int size) {
        String fullFilePath = "";
        boolean isBase64 = urlLikeStr.startsWith("data:image/");

        try {
            if (isBase64) {
                fullFilePath = this.storeCategoryImageFromBase64(id + "-" + subId, folder, urlLikeStr, size);
            } else {
                fullFilePath = this.storeCategoryImageFromUrl(id + "-" + subId, folder, urlLikeStr, size);
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

            image = this.resizeImage(image, 200);

            String newFileName = "movie-" + movieId;
            fullFilePath = folder + FileSystems.getDefault().getSeparator() + newFileName + ".jpg";

            ImageIO.write(image, "jpg", new File(classPath() + fullFilePath));
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeCategoryImageFromUrl(String identifier, String folder, String url, int size) {
        String fullFilePath = "";

        try {
            URL urlObj = URI.create(url).toURL();
            BufferedImage image = ImageIO.read(urlObj);
            String ext = urlObj.openConnection().getContentType().replace("image/", "");

            fullFilePath = this.execStore(image, ext ,folder, identifier, size);
        } catch (Exception ex)  {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public String storeCategoryImageFromBase64(String identifier, String folder, String urlLikeStr, int size) {
        String fullFilePath = "";

        try {
            String[] sourceArr = urlLikeStr.split(",");

            String ext = sourceArr[0].replace("data:image/", "")
                    .replace(";base64", "");
            byte[] decoded = Base64.getDecoder().decode(sourceArr[1]);
            ByteArrayInputStream bis = new ByteArrayInputStream(decoded);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            fullFilePath = this.execStore(image, ext, folder, identifier, size);
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return fullFilePath;
    }

    public List<ImageEntity> getAllSubImagesByMainId(String mainId) {
        return this.imageRepository.findAllByUsedByAndUsedAs(mainId, ImageUsedAs.SUB);
    }

    public Result deleteByPath(String path) {
        Optional<ImageEntity> deleted = this.imageRepository.deleteByPath(path);

        if(deleted.isPresent()) {
            return Result.builder().id(deleted.get().getId()).success(true).build();
        }

        return Result.builder().id("0").success(false).build();
    }

    public Result clearUseless() throws FileNotFoundException {
        this.manageImages.deleteNonUsingImages();

        return Result.builder().id("0").success(true).build();
    }
}
