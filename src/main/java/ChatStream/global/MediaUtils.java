package ChatStream.global;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class MediaUtils {
    public static String uploadLocal(String media, String mediaName) throws Exception{
        String base64Data = media.split(",")[1]; // Extract the base64 part
        byte[] decodedMediaBytes = Base64.getDecoder().decode(base64Data);

        Path path = Paths.get("C://xampp//htdocs//chatstream-local-images/" + mediaName); //uri.getPath()
        Files.write(path, decodedMediaBytes);

        return "http://localhost/chatstream-local-images/" + mediaName;
    }

    public static void deleteLocal(String url) throws Exception{
        String[] temp = url.split("/");
        String mediaName = temp[temp.length - 1];

        Path path = Paths.get("C://xampp//htdocs//chatstream-local-images/" + mediaName);
        Files.delete(path);
    }

    public static String replaceLocal(String oldUrl, String newMedia, String newMediaName) throws Exception{
        if(!oldUrl.isEmpty()) {
            deleteLocal(oldUrl);
        }
        return uploadLocal(newMedia,  newMediaName);
    }
}
