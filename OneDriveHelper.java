import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class OneDriveHelper {
    private static final String BASE_URL = "https://graph.microsoft.com/v1.0/me/drive/root:/path/to/your/folder:/children";
    private OkHttpClient client = new OkHttpClient();

    public String listFiles(String accessToken) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }

    public void downloadFile(String accessToken, String fileUrl, String localPath) throws IOException {
        Request request = new Request.Builder()
                .url(fileUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            FileOutputStream fos = new FileOutputStream(new File(localPath));
            fos.write(response.body().bytes());
            fos.close();
        }
    }
}
