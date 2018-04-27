package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class Main extends Application {
    public Button button = new Button();
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        Button button = new Button("Button in AnchorPane");
//        root.getChildren().add(button);

//        System.out.print(root.getId());
//        Rectangle r = new Rectangle(25,25,250,250);
//        Controller controller = fxmlLoader.getController();
////        controller.showDateTime(primaryStage);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1920, 1240));
        primaryStage.setFullScreen(true);
        primaryStage.show();


//        System.out.print(nameField);
    }



    public static void main(String[] args) {
        launch(args);
    }
}
