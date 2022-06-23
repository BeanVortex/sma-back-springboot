package ir.darkdeveloper.sma.utils;

import ir.darkdeveloper.sma.model.PostModel;
import ir.darkdeveloper.sma.model.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class IOUtils {

    private static final String DEFAULT_PROFILE_IMAGE = "noProfile.jpeg";
    public static final String USER_IMAGE_PATH = "classpath:static/img/profiles/";
    public static final String POST_IMAGE_PATH = "classpath:static/img/posts/";


    /**
     * @return a saved file name or empty if file is null
     */
    public Optional<String> saveFile(MultipartFile file, String path) {
        if (file != null) {
            // first it may not upload and save file in the path. should create static/img/profiles/ and static/img/posts
            // folder in resources
            try {
                var location = ResourceUtils.getFile(path).getAbsolutePath();

                var bytes = file.getBytes();
                var fileName = UUID.randomUUID() + "." +
                        Objects.requireNonNull(file.getContentType()).split("/")[1];
                Files.write(Paths.get(location + File.separator + fileName), bytes);

                return Optional.of(fileName);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return Optional.empty();
    }


    public String getImagePath(ImageUtil model, String defaultDirPath) {
        try {
            return ResourceUtils.getFile(defaultDirPath).getAbsolutePath() + File.separator + model.getImage();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void deleteUserImages(UserModel user) {
        if (!user.getImage().equals(DEFAULT_PROFILE_IMAGE))
            deleteAnImage(user, USER_IMAGE_PATH);
    }

    public void deletePostImagesOfUser(PostModel post) {
        if (post.getImage() != null) {
            deleteAnImage(post, POST_IMAGE_PATH);
        }
    }

    private void deleteAnImage(ImageUtil model, String path) {
        var imgPath = getImagePath(model, path);
        if (imgPath != null)
            deleteAFile(imgPath);
    }

    public void deleteAFile(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void saveUserImages(UserModel user) {


        var profileFileExists = user.getProfileFile() != null;

        if (!profileFileExists) {
            user.setProfilePicture(DEFAULT_PROFILE_IMAGE);
            return;
        }

        saveFile(user.getProfileFile(), USER_IMAGE_PATH).ifPresent(user::setProfilePicture);

    }

    public void updatePostFile(PostModel post) {
        var file = post.getFile();
        if (file != null) {
            deleteAnImage(post, POST_IMAGE_PATH);
            saveFile(file, POST_IMAGE_PATH).ifPresent(post::setImage);
        }
    }
}
