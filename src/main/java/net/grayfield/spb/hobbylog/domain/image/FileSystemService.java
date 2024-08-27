package net.grayfield.spb.hobbylog.domain.image;

import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class FileSystemService {
    private String classPath() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:static").toString();
    }

    private void execMkdir(String folderPath) throws FileNotFoundException {
        File uploadPathFolder = new File(classPath() + folderPath);

        boolean result = true;
        if(!uploadPathFolder.exists()) {
            result = uploadPathFolder.mkdirs();
        }

        if(!result) throw new InternalError("Mkdir " + folderPath + " failed");
    }

    public String makeMovieFolder (Long movieId, String movieTitle) throws FileNotFoundException {
        String folderPath = "/images/upload/movie/";

        if (movieId < 1_000_000_000) {
            folderPath += "0/";
        } else if (movieId < 2_000_000_000) {
            folderPath += "1/";
        } else {
            folderPath += "2/";
        }

        String id09 = String.format("%010d", movieId).substring(1, 10);
        folderPath += id09.charAt(0) + FileSystems.getDefault().getSeparator();

        String id08 = id09.substring(1, 9);
        folderPath += id08.charAt(0) + FileSystems.getDefault().getSeparator();

        String id04 = id08.substring(4, 8);
        folderPath += id04.charAt(0) + FileSystems.getDefault().getSeparator();

        folderPath += movieTitle.toLowerCase().charAt(0);

        this.execMkdir(folderPath);

        return folderPath;
    }

    public String makeCategoryImageFolder (Category category, LocalDateTime logAt) throws FileNotFoundException {
        String folderPath = "/images/upload/" + category.toString().toLowerCase() + FileSystems.getDefault().getSeparator();

        folderPath += logAt.format(DateTimeFormatter.ofPattern("yyyy/MM/"));

        this.execMkdir(folderPath);

        return folderPath;
    }
}
