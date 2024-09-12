package net.grayfield.spb.hobbylog.schedule.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageImages {
    @Value("${spring.config.activate.on-profile}")
    private String env;

    @Value("${storage.path}")
    private String storagePath;

    private final ImageBatchTemplateRepository imageBatchTemplateRepository;

    private String classPath() throws FileNotFoundException {
        String parent;
        if(env.equals("default")) parent = ResourceUtils.getFile("classpath:static").toString();
        else parent = ResourceUtils.getFile(storagePath).toString();

        return parent
                + FileSystems.getDefault().getSeparator()
                + "images"
                + FileSystems.getDefault().getSeparator()
                + "upload";
    }

    private String rootPath() throws FileNotFoundException {
        return new File(this.classPath()).getAbsolutePath();
    }

    // TODO update time once per week
    @Scheduled(cron = "*/10 * * * * *")
    public void deleteNonUsingImages() throws FileNotFoundException {
        log.info("Deleting non-using images schedule start");
        this.findChildFiles(this.classPath());
        log.info("Deleting non-using images schedule end");
    }

    private void findChildFiles(String path) throws FileNotFoundException {
        File parentDir = new File(path);

        File[] childrenRaw = parentDir.listFiles();
        if(childrenRaw == null) { return; }

        List<File> children = Arrays.stream(childrenRaw).toList();

        for (File child : children) {
            if(child.isDirectory()) {
                if(child.listFiles() == null || child.listFiles().length == 0) {
                    log.info("delete: {}", child.getAbsolutePath());
                    child.delete();
                    continue;
                }
                this.findChildFiles(child.getAbsolutePath());
            } else  {
                String cutPath = child.getAbsolutePath()
                        .replace(this.rootPath() + FileSystems.getDefault().getSeparator(), "");

                String filePath = "/images/upload/" + cutPath;

                if(!this.imageBatchTemplateRepository.isExistsByCategoryAndPath(filePath)) {
                    log.info("delete: {}", child.getAbsolutePath());
                    child.delete();
                }
            }
        }
    }
}
