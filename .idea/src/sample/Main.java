package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

import static sample.SharedFunctions.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage stage = new Stage();
        PrepareLoginWindow(stage);
        if (successfulLogin) {
            PrepareWindow(primaryStage);
        }else{
           Platform.exit();
           System.exit(0);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    //region Main Screen

    public class message{
        private String myMessageText = "";
        private String friendMessageText = "";
        private String messageAuthor = "";

        public message(){
            friendMessageText = "";
            myMessageText = "";
        }
        public message(String msg){
                myMessageText = msg.replaceAll("(.{33})", "$1\n");
                friendMessageText = "";
                messageAuthor = userName;
        }

        public message(String msg, String msgAuthor){
                friendMessageText = msg.replaceAll("(.{33})", "$1\n");
                myMessageText = "";
                messageAuthor = msgAuthor;
        }

        public String getMyMessageText() {
            return myMessageText;
        }

        public void setMyMessageText(String myMessageText) {
            this.myMessageText = myMessageText;
        }

        public String getFriendMessageText() {
            return friendMessageText;
        }

        public void setFriendMessageText(String friendMessageText) {
            this.friendMessageText = friendMessageText;
        }
    }

    TextField txtMessageToSend;
    ListView<String> subscribers = new ListView<String>();
    private TableView<message> messages = new TableView<message>();
    ObservableList<message> messagesWritten = FXCollections.observableArrayList();

    private void PrepareWindow(Stage primaryStage){
        Pane dialogPane = new Pane();
        Scene dialogScene = new Scene(dialogPane,800,600);

        txtMessageToSend = new TextField();
        txtMessageToSend.setPromptText("Type your message here");
        txtMessageToSend.setLayoutY(500);
        txtMessageToSend.setLayoutX(200);
        txtMessageToSend.setPrefSize(600,100);

        //dialogScene.setUserAgentStylesheet("styles.css");
        dialogScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (txtMessageToSend.getText().length()>0) {
                    String message = txtMessageToSend.getText();
                    if (foreverAlone) {
                        insertMessageInLV(new message(message, friendUserName));
                    }else {
                        insertMessageInLV(new message(message));
                    }
                    txtMessageToSend.clear();
                }
            }
        });

        primaryStage.setTitle("Logged in as " + userName);
        primaryStage.setScene(dialogScene);
        primaryStage.getIcons().add(new Image("file:C:\\Users\\valentin.asparuhov\\IdeaProjects\\Skype\\src\\sample\\skype.png"));

        ObservableList<String> items = FXCollections.observableArrayList (
                "Single", "Double", "Triple");
        subscribers.setItems(items);
        subscribers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Your action here
                getMessagesArch();
            }
        });

        subscribers.setLayoutX(0);
        subscribers.setLayoutY(0);
        subscribers.setPrefWidth(200);
        subscribers.setPrefHeight(600);

        TableColumn friendMessage = new TableColumn("Friend messages");
        friendMessage.setMinWidth(299);
        friendMessage.setCellFactory(TextFieldTableCell.forTableColumn());
        friendMessage.setCellValueFactory(new PropertyValueFactory<message,String>("friendMessageText"));
        friendMessage.setResizable(false);
        friendMessage.setSortable(false);

        // opit za pramqna na cveta na sistemniq text + alignvane na texta sprqmo tova ot koi potrebitel e izpraten
        /*friendMessage.setCellFactory(new Callback<TableColumn<message, String>, TableCell<message, String>>() {
            @Override
            public TableCell<message, String> call(TableColumn<message, String> param) {
                final TableCell<message, String> cell = new TableCell<message, String>() {

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!isEmpty()) {
                            Label message = new Label();
                            message.setTextAlignment(TextAlignment.RIGHT);

                            String tmp = item;
                            tmp = tmp.substring(0,tmp.indexOf(':'));

                            Text timeOfMessage =new Text(tmp);
                            timeOfMessage.setStyle("-fx-font-weight: bold");

                            tmp = item.substring(item.indexOf(':')+1);

                            Text msg =new Text(" "+tmp);
                            timeOfMessage.setStyle("-fx-font-weight: regular");

                            TextFlow flow = new TextFlow();
                            flow.getChildren().addAll(timeOfMessage, msg);

                            VBox vbTable = new VBox();
                            vbTable.getChildren().add(flow);
                            setGraphic(vbTable);
                        }
                    }
                };
                return cell;
            }
        });*/

        TableColumn myMessage = new TableColumn("My messages");
        myMessage.setMinWidth(299);
        myMessage.setCellFactory(TextFieldTableCell.forTableColumn());
        myMessage.setCellValueFactory(new PropertyValueFactory<message,String>("myMessageText"));
        myMessage.setResizable(false);
        myMessage.setSortable(false);

        messages.getColumns().addAll(friendMessage, myMessage);
        messages.setLayoutX(200);
        messages.setLayoutY(0);
        messages.setPrefWidth(600);
        messages.setPrefHeight(498);
        messages.setEditable(true);
        messages.setFixedCellSize(Region.USE_COMPUTED_SIZE);
        messages.setPlaceholder(new Label("No messages sent yet!"));
        messages.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        messages.setRowFactory(tv -> new TableRow<message>() {
            @Override
            public void updateItem(message item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                    setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
                } else if (item.getMyMessageText().length()>0) {
                    setBackground(new Background(new BackgroundFill(Color.SKYBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
                    setTextAlignment(TextAlignment.RIGHT);
                    setAlignment(Pos.CENTER_RIGHT);
                } else {
                    setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
                    setTextAlignment(TextAlignment.LEFT);
                    setAlignment(Pos.CENTER_LEFT);
                }
                setFont(new Font("Times new roman",12));
                setWrapText(true);
                //setHeight(100);
            }
        });
        messages.getItems().addListener(new ListChangeListener<message>(){
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends message> c) {
                messages.scrollTo(c.getList().size()-1);
            }
        });

        dialogPane.getChildren().add(subscribers);
        dialogPane.getChildren().add(messages);
        dialogPane.getChildren().add(txtMessageToSend);

        primaryStage.show();
        dialogPane.requestLayout();
    }

    private void getMessagesArch(){
        messages.getItems().clear();
        //messages = new TableView<message>();
        if (subscribers.getSelectionModel().getSelectedItem()!=null){
            String displayText = subscribers.getSelectionModel().getSelectedItem().toString();
            message[] TestMessages = new message[]{};
            friendUserName = displayText;
            if (friendUserName.equals("Single")){
                 TestMessages = new message[]{
                    new message("zdr",friendUserName),
                    new message("zdr we",friendUserName),
                    new message("ko iskash we"),
                    new message("nishto we",friendUserName),
                    new message("ok")
                };
            }else if (friendUserName.equals("Double")){
                 TestMessages = new message[]{
                        new message("here",friendUserName),
                        new message("?",friendUserName),
                        new message("no"),
                        new message("pfpfpf",friendUserName),
                        new message("PF"),
                };
            }else if (friendUserName.equals("Triple")){
                TestMessages = new message[]{
                        new message("hi",friendUserName),
                        new message(":D",friendUserName),
                        new message("zadara"),
                        new message("kp",friendUserName),
                        new message("nishto"),
                        new message("ti ?"),
                        new message("i az",friendUserName),
                        new message("ok"),
                        new message(":D",friendUserName),
                        new message("zadara"),
                        new message("kp",friendUserName),
                        new message("nishto"),
                        new message("ti ?"),
                        new message("i az",friendUserName),
                        new message("ok")
                };
            }
            for (int i=0;i< TestMessages.length;i++){
                       insertMessageInLV(TestMessages[i]);
            }
        }
        messages.scrollTo(messages.getItems().size());
    }

    boolean foreverAlone = true;

    private void insertMessageInLV(message newMessage){
        Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String date = formatter.format(new Date());
        if (newMessage.myMessageText.length()>0) {
            newMessage.setMyMessageText(userName + " (" + date + ") : \n" + newMessage.myMessageText);
        }else if(newMessage.friendMessageText.length()>0) {
            newMessage.setFriendMessageText(friendUserName + " (" + date + ") : \n" + newMessage.friendMessageText);
        }else {
            //friend is typing
        }
        foreverAlone = !foreverAlone;
        messagesWritten.add(newMessage);
        messages.setItems(messagesWritten);
        messages.scrollTo(messages.getItems().size());
    }
    //endregion Main Screen

    //region Login Screen
    Label lblUserNameLogin;
    Label lblPasswordLogin;
    TextField txtUserNameLogin;
    PasswordField txtPassLogin;
    Button btnLogin;
    Button btnCancelLogin;
    Boolean successfulLogin = false;
    String userName;
    String friendUserName;

    private void PrepareLoginWindow(Stage primaryStage){
        Pane dialogPane = new Pane();
        Scene dialogScene = new Scene(dialogPane,300,200);

        primaryStage.setTitle("Skype login");
        primaryStage.setScene(dialogScene);
        primaryStage.getIcons().add(new Image("file:C:\\Users\\valentin.asparuhov\\IdeaProjects\\Skype\\src\\sample\\skype.png"));

        btnLogin = new Button();
        btnLogin.setLayoutY(165);
        btnLogin.setLayoutX(100);
        btnLogin.setText("Login");
        btnLogin.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                if (txtUserNameLogin.getText().toString().equals("root") & txtPassLogin.getText().toString().equals("toor")) {
                    MessageBox(Alert.AlertType.INFORMATION, "Successful login", "You logged in successfully", "");
                    primaryStage.close();
                    successfulLogin = true;
                    userName = txtUserNameLogin.getText().toString();
                }else{
                    MessageBox(Alert.AlertType.ERROR, "Unsuccessful login", "Wrong password or user name!", txtUserNameLogin.getText().toString().toLowerCase() + " " + txtPassLogin.getText().toString().toLowerCase());
                }
            }
        });

        btnCancelLogin = new Button();
        btnCancelLogin.setLayoutY(165);
        btnCancelLogin.setLayoutX(150);
        btnCancelLogin.setText("Cancel");
        btnCancelLogin.setOnAction(new EventHandler<>() {
            @Override
            public void handle(ActionEvent event) {
                primaryStage.close();
                successfulLogin = false;
            }
        });

        lblUserNameLogin = new Label();
        lblUserNameLogin.setText("User name:");
        lblUserNameLogin.setLayoutY(50);
        lblUserNameLogin.setLayoutX(25);
        lblUserNameLogin.setPrefSize(60,25);

        lblPasswordLogin = new Label();
        lblPasswordLogin.setText("Password:");
        lblPasswordLogin.setLayoutY(100);
        lblPasswordLogin.setLayoutX(25);
        lblPasswordLogin.setPrefSize(60,25);

        txtUserNameLogin = new TextField();
        txtUserNameLogin.setPromptText("Your user name");
        txtUserNameLogin.setLayoutY(50);
        txtUserNameLogin.setLayoutX(90);
        txtUserNameLogin.setPrefSize(175,25);

        txtPassLogin = new PasswordField();
        txtPassLogin.setPromptText("Your password");
        txtPassLogin.setLayoutY(100);
        txtPassLogin.setLayoutX(90);
        txtPassLogin.setPrefSize(175,25);

        dialogPane.getChildren().add(btnLogin);
        dialogPane.getChildren().add(btnCancelLogin);
        dialogPane.getChildren().add(lblUserNameLogin);
        dialogPane.getChildren().add(lblPasswordLogin);
        dialogPane.getChildren().add(txtUserNameLogin);
        dialogPane.getChildren().add(txtPassLogin);
        primaryStage.showAndWait();
        dialogPane.requestLayout();
    }
    //endregion Login Screen
}
