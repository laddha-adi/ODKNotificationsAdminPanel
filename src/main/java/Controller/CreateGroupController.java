package Controller;

import Model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import static Data.LoginData.ANDROID_APP_PACKAGE_NAME;
import static Data.LoginData.DYNAMIC_LINK_DOMAIN;
import static Data.LoginData.FIREBASE_INVITES_URL;

public class CreateGroupController implements Initializable {
    public TextField name_field;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    public Button clipboardButton;
    private String groupLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipboardButton.setVisible(false);
    }

    public void createButtonClicked(MouseEvent mouseEvent) {
        progressIndicator.setProgress(-1.0f);
        progressIndicator.setVisible(true);

        if (mouseEvent.getClickCount() == 1)
            if (!name_field.getText().trim().equals("")) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        updateMessage("Please Wait...");
                        updateProgress(-1,100);
                        String name = name_field.getText();
                        String link = createNewGroup(name);
                        if(link!=null) {
                            updateMessage("Group Link: " + link);
                            updateProgress(100,100);
                        }
                        else{
                            updateMessage("There is some error in creating the group. Please try again.");
                            updateProgress(0,100);
                        }
                        return null;
                    }
                };
                task.setOnSucceeded(taskFinishEvent -> clipboardButton.setVisible(true));
                progressIndicator.progressProperty().bind(task.progressProperty());
                statusLabel.textProperty().bind(task.messageProperty());
                new Thread(task).start();
            }
    }

    private String createNewGroup(String groupName) {

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        DatabaseReference pushedPostRef = groupRef.push();
        groupRef.child(pushedPostRef.getKey()).setValueAsync(new Group(pushedPostRef.getKey(), groupName, null));
        return createGroupLink(groupName, pushedPostRef.getKey());
    }

    private String createGroupLink(String groupName, String groupId) {
        String link = null;
        try {
             link = createDynamicLink(groupName, groupId);
            System.out.println("LINK: " + link);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }

    private String createDynamicLink(String groupName, String groupId) {

        JSONObject androidInfo = new JSONObject();
        androidInfo.put("androidPackageName", ANDROID_APP_PACKAGE_NAME);
        JSONObject dynamicLinkInfo = new JSONObject();
        dynamicLinkInfo.put("dynamicLinkDomain", DYNAMIC_LINK_DOMAIN);
        dynamicLinkInfo.put("link", "https://odknotificatons/id="+groupId);
        dynamicLinkInfo.put("androidInfo", androidInfo);

        JSONObject mainObject = new JSONObject();
        mainObject.put("dynamicLinkInfo", dynamicLinkInfo);

        // System.out.println("JSON Object->" + String.valueOf(mainObject));
         HttpClient client = new DefaultHttpClient();
         HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000); //Timeout Limit
         HttpResponse response;

         try {
              HttpPost post = new HttpPost(FIREBASE_INVITES_URL);
              StringEntity se = new StringEntity(mainObject.toString());
              post.setEntity(se);
              response = client.execute(post);

              if (response != null) {
                   InputStream in = response.getEntity().getContent(); //Get the data in the entity
                   BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                   StringBuilder result = new StringBuilder();
                   String line;
                   while((line = reader.readLine()) != null) {
                        result.append(line);
                   }
                   JSONObject responseJSON = new JSONObject(result.toString());
                   groupLink = responseJSON.getString("shortLink");
                   progressIndicator.setVisible(false);

              }

         } catch (Exception e) {
               e.printStackTrace();
               System.out.println("Error"+ " Cannot Establish Connection");
         }
        return groupLink;
     }

    public void copyToClipboardClicked(MouseEvent mouseEvent) {
        if(groupLink!=null){
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(groupLink);
            clipboard.setContent(content);
            clipboardButton.setText("Copied");
        }
    }
}

