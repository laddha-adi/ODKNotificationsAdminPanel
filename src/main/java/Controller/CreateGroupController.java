package Controller;

import Model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateGroupController implements Initializable {
    public TextField name_field;
    public ProgressIndicator progressIndicator;
    public Label statusLabel;
    private String groupLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // statusLabel.setText("");
       // progressIndicator.setProgress(0);
       // progressIndicator.setVisible(false);
    }

    public void createButtonClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==1)
            if(!name_field.getText().trim().equals(""))
                createNewGroup(name_field.getText());
        
    }

    private void createNewGroup(String groupName) {
        progressIndicator.setProgress(-1.0f);
        progressIndicator.setVisible(true);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        DatabaseReference pushedPostRef = groupRef.push();
        groupRef.child(pushedPostRef.getKey()).setValueAsync(new Group(pushedPostRef.getKey(),groupName,null));
        progressIndicator.setVisible(false);
    }

}
