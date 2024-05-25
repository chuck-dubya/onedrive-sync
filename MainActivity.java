import com.microsoft.identity.client.*;
import com.microsoft.identity.client.exception.*;

public class MainActivity extends AppCompatActivity {
    private static final String[] SCOPES = {"Files.ReadWrite.All"};
    private PublicClientApplication msalApp;
    private IAuthenticationResult authResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msalApp = PublicClientApplication.createPublicClientApplication(this.getApplicationContext(), R.raw.auth_config);

        msalApp.acquireToken(this, SCOPES, getAuthInteractiveCallback());
    }

    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                authResult = authenticationResult;
                // Proceed to sync files
                syncOneDriveFolder();
            }

            @Override
            public void onError(MsalException exception) {
                // Handle error
            }

            @Override
            public void onCancel() {
                // Handle cancel
            }
        };
    }

    private void syncOneDriveFolder() {
        // Implement sync logic here
    }
}

private void syncOneDriveFolder() {
    OneDriveHelper oneDriveHelper = new OneDriveHelper();
    String accessToken = authResult.getAccessToken();

    new Thread(() -> {
        try {
            String fileListJson = oneDriveHelper.listFiles(accessToken);
            // Parse the JSON and download each file
            // Example: Gson can be used to parse the JSON response

            Gson gson = new Gson();
            FileListResponse fileList = gson.fromJson(fileListJson, FileListResponse.class);

            for (FileItem fileItem : fileList.value) {
                String fileUrl = fileItem.getDownloadUrl();
                String localPath = getExternalFilesDir(null) + "/" + fileItem.name;
                oneDriveHelper.downloadFile(accessToken, fileUrl, localPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }).start();
}

public class FileListResponse {
    public List<FileItem> value;
}

public class FileItem {
    public String name;
    public String id;

    public String getDownloadUrl() {
        return "https://graph.microsoft.com/v1.0/me/drive/items/" + id + "/content";
    }
}