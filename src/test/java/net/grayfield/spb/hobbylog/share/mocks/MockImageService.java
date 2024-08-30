package net.grayfield.spb.hobbylog.share.mocks;

import net.grayfield.spb.hobbylog.domain.image.ImageService;

import java.awt.image.BufferedImage;

public class MockImageService extends ImageService {
    public String path = "";
    public boolean resized = false;

    private String classPath() {
        this.path = "abc";
        return "abc";
    }

    @Override
    public BufferedImage resizeImage(BufferedImage image, int size) {
        this.resized = true;
        return image;
    }

    @Override
    public String execStore(BufferedImage image, String folder, String identifier, int size) {
        this.resizeImage(image, size);
        this.path += this.classPath() + "/" + folder + "/" + identifier + ".png";
        return folder + "/" + identifier + ".png";
    }

    @Override
    public String storeMovieImage(String movieId, String folder, String urlLikeStr) {
        return this.execStore(
                new BufferedImage(1, 1, 5),
                folder, "movie" + movieId, 1
        );
    }

    @Override
    public String storeCategoryImageFromUrl(String identifier, String folder, String url, int size) {
        return this.execStore(
                new BufferedImage(1, 1, 1),
                folder, "image-" + identifier, 1
        );
    }

    @Override
    public String storeCategoryImageFromBase64(String identifier, String folder, String urlLikeStr, int size) {
        return this.execStore(
                new BufferedImage(1, 1, 0),
                folder, "image-" + identifier, 1
        );
    }
}
