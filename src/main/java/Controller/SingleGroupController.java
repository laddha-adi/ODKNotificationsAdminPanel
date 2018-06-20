package Controller;

import Model.Group;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class SingleGroupController implements Initializable {
    private Group group;
    @FXML
    private Label num_members;

    @FXML
    private ListView membersListView;

    public SingleGroupController(Group group) {
        this.group = group;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        num_members.setText(String.valueOf(group.getTotalMembers())+" Member(s)");
        ObservableList<String> observableList = FXCollections.observableList(group.getMembersName());
        membersListView.setItems(observableList);
    }

    public void deleteGroupButtonClicked(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this group permanently ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            //do stuff
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group").child(group.getId());
            groupRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    System.out.println(error.getMessage());
                }
            });

        }
    }
    public void renameGroupButtonClicked(){

    }
}
