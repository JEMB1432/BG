package jemb.bistrogurmand.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

public class ImgBBUploader {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String API_KEY = dotenv.get("API_KEY");
    private static final String UPLOAD_URL = dotenv.get("UPLOAD_URL");

    public static String uploadImage(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        HttpPost post = new HttpPost(UPLOAD_URL + "?key=" + API_KEY);
        post.setEntity(new UrlEncodedFormEntity(List.of(
                new BasicNameValuePair("image", encodedString)
        )));

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(post);
            var responseEntity = response.getEntity();

            if (responseEntity != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(responseEntity.getContent());

                if (jsonNode.has("data")) {
                    return jsonNode.get("data").get("url").asText();
                } else {
                    System.err.println("Error en la respuesta: " + jsonNode);
                }
            }
        }
        return null;
    }
}
