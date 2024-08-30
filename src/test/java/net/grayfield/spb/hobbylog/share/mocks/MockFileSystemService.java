package net.grayfield.spb.hobbylog.share.mocks;

import net.grayfield.spb.hobbylog.domain.image.FileSystemService;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;

import java.time.LocalDateTime;

public class MockFileSystemService extends FileSystemService {
    public String path = "";
    public String storePath = "";

    private String classPath() {
        this.path = "abc";
        return "abc";
    }

    private void execMkdir(String folderPath) {
        this.path = this.classPath() + folderPath;
        this.storePath = folderPath;
    }

    @Override
    public String makeMovieFolder (Long movieId, String movieTitle) {
        this.execMkdir("/def/ghi");
        return "def/ghi";
    }

    @Override
    public String makeCategoryImageFolder (Category category, LocalDateTime logAt) {
        this.execMkdir("/def/ghi");
        return "def/ghi";
    }
}
