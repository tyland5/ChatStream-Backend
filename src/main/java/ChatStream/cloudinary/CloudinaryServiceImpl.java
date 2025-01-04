package ChatStream.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl {

    @Resource
    private Cloudinary cloudinary;

    public String uploadFile(String media, String mediaName, String folderName) {
        String base64Data = media.split(",")[1]; // Extract the base64 part
        byte[] decodedMediaBytes = Base64.getDecoder().decode(base64Data);
        // get rid of extension bc then it be like blah.jpg.jpg. need extension as part of name for local
        String newMediaName = mediaName.split("\\.")[0];

        try{
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("public_id", newMediaName);

            Map uploadedFile = cloudinary.uploader().upload(decodedMediaBytes, options);
            String publicId = (String) uploadedFile.get("public_id");

            String url = cloudinary.url().secure(true).generate(publicId);
            url = url.substring(0, url.length() - 5); // remove ?_a=E

            return url;

        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
    }

    public void deleteFile(String mediaName, String folderName){
        String[] urlParts = mediaName.split("/");
        String publicId = folderName + "/" + urlParts[urlParts.length-1];

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }catch (IOException e){
            return;
        }
    }

    public String replaceFile(String oldUrl, String newMedia, String newMediaName, String folderName) throws Exception{
        if(!oldUrl.isEmpty()) {
            this.deleteFile(oldUrl, folderName);
        }
        return this.uploadFile(newMedia, newMediaName, folderName);
    }
}
