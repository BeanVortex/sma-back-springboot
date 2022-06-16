package ir.darkdeveloper.sma.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class IOUtils {
    
    /**
     * 
     * @param file MultipartFile
     * @param path after img/
     * @return saved file name or null if file is null
     * @throws IOException io
     */
    public String saveFile(MultipartFile file, String path) throws IOException {
        if (file != null) {
            // first it may not upload and save file in the path. should create static/img
            // folder in resources
            String location = ResourceUtils.getFile("classpath:static/img/" + path).getAbsolutePath();
            byte[] bytes = file.getBytes();
            String fileName = UUID.randomUUID() + "." + file.getContentType().split("/")[1];
            Files.write(Paths.get(location + File.separator + fileName), bytes);
            return fileName;
        }
        return null;
    }


    public String getImagePath(ImageUtil model, String path) throws Exception{
        return ResourceUtils.getFile("classpath:static/img/" + path).getAbsolutePath() + File.separator
                        + model.getImage();
    }

}
