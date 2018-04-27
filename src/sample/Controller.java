package sample;



import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Controller {
    @FXML
    public Button buttonMenu;
    @FXML
    GridPane gridPanel;
    @FXML
    GridPane gridButtonRight;
    @FXML
    GridPane gridButtonLeft;


    @FXML
    public Scene scene;
    public TextField studentIDView = new TextField();
    public Button updateButton = new Button();
    public Button nextButton = new Button();
    public Button nextButtonImage = new Button();
    public Button printButton = new Button();
    public Button printButtonImage = new Button();
    public Button reviewButton = new Button();
    public ListView listView = new ListView();
    public Label printText = new Label();
    public ImageView printImage = new ImageView();
    public int numberImage = 1;
    public BufferedImage image;
    private HostServices hostServices ;
    public String printPdfName;
    public String  nameImage = "1.jpg";
    @FXML
    public void submit() {
        updateButton.setText("12341234");
        System.out.print(updateButton.getText());

    }

    public void keyHandler(javafx.scene.input.KeyEvent keyEvent) throws InterruptedException, IOException, SQLException {
        System.out.println(keyEvent.getCode());
        if (keyEvent.getCode() == KeyCode.RIGHT) {
            nextButton.fire();
            System.out.println("asdf"+printPdfName);
        }
        if (keyEvent.getCode() == KeyCode.LEFT) {
            buttonMenu.setDefaultButton(true);
            buttonMenu.fire();
            System.out.println(keyEvent.getText());
        }
        if (keyEvent.getCode() == KeyCode.ENTER) {
//            try{
               List<String> studentNameID;
               studentNameID = getUserInfo(studentIDView.getText());
//               if(studentNameID.toString()){

//                   createFileList();

               System.out.println("student = " + studentNameID);
//            }
//            catch (Exception e){
//                System.out.println("12345 = ");
//            }
            createFileList();
            System.out.println(keyEvent.getText());
        }


    }

    public List<String> getUserInfo(String codeID) throws SQLException {
        Connection connection = null;
        ArrayList<String> studentNameID = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/UserPrint?" + "user=root&password=12345");
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from UserPrint where Student_ID ="+"'"+codeID+"'");
            while(resultSet.next()) {
                System.out.println(resultSet.getString(2) + "  " + resultSet.getString(4));
                studentNameID.add(resultSet.getString(2));
                studentNameID.add(resultSet.getString(4));
                connection.close();
                return studentNameID;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            connection.close();
        }

        return null;
    }

    public void createLoginForm(){
        removeMenu();
        studentIDView.setText("");
        studentIDView.setId("StudentID");
        gridPanel.add(studentIDView,1,1);
    }


    public void createFileList() throws InterruptedException {
        updateButton.setText("Update");
        updateButton.setId("updateButton");
        updateButton.setPrefSize(200, 100);
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
//                    removeButtonList();
                    createFileList();

                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });


        nextButton.setText("Next");
        nextButton.setId("nextButton");
        nextButton.setPrefSize(200, 100);
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
//                removeButtonList();
                creatPritList();
            }
        });
        try {

            gridPanel.add(updateButton, 0, 0);
            gridPanel.add(nextButton, 5, 0);
        }catch (Exception e){
            System.out.println("asdfasdfsdfsd");

        }

        listView.setMinWidth(500);
        String usbDir = getUsbDir();
        if(usbDir != null) {
            List<String> listUsb = usbList(getUsbDir());
            if (listUsb != null) {
                ObservableList<String> items = FXCollections.observableArrayList(listUsb);
                listView.setItems(items);
            }
        }
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            printPdfName = newValue.toString();
        });
//        creatButtonList();
        gridPanel.add(listView,3,1);
    }

//    public void creatButtonList(){
//
//        updateButton.setText("Update");
//        updateButton.setId("updateButton");
//        updateButton.setPrefSize(200, 100);
//        updateButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent e) {
//                try {
//                    removeButtonList();
//                    createList();
//
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        });
//
//
//        nextButton.setText("Next");
//        nextButton.setId("nextButton");
//        nextButton.setPrefSize(200, 100);
//        nextButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent e) {
//                removeButtonList();
//                creatPritList();
//            }
//        });
//        gridPanel.add(updateButton,0,0);
//        gridPanel.add(nextButton,5,0);
//
//    }



    public void createImagePage(){

        printButtonImage.setText("Print2");
        printButtonImage.setId("printButton");
        printButtonImage.setPrefSize(200, 100);
        printButtonImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                removePrintList();
                removeImageDir();

            }
        });


        nextButtonImage.setText("Next2");
        nextButtonImage.setId("nextButton2");
        nextButtonImage.setPrefSize(200, 100);
        nextButtonImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                removePrintList();
                removeImagePage();

                nameImage = String.valueOf(numberImage)  + ".jpg";
                System.out.println(nameImage);
                createImagePage();
            }
        });

        Image imageReview = null;

        System.out.println(nameImage);
        try {
            imageReview = new Image(nameImage);
            printImage.setImage(imageReview);
            gridPanel.add(printImage,0,0);
            numberImage++;

        }
        catch (Exception e){
            System.out.println("not image");
            numberImage = 1;
            removeImagePage();
            nameImage = String.valueOf(numberImage)  + ".jpg";
            System.out.println(nameImage);
            createImagePage();

        }
        gridPanel.getChildren().remove(printButtonImage);
        gridPanel.getChildren().remove(nextButtonImage);
        gridPanel.add(printButtonImage,0,1);
        gridPanel.add(nextButtonImage,5,1);

    }





    public void creatPritList(){
        printButton.setText("Print");
        printButton.setId("printButton");
        printButton.setPrefSize(200, 100);
        printButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                removePrintList();
                removeImageDir();
            }
        });


        reviewButton.setText("Review");
        reviewButton.setId("reviewButton");
        reviewButton.setPrefSize(200, 100);
        reviewButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                removePrintList();
                String namePage = String.valueOf(numberImage)  + ".jpg";
                System.out.println(namePage);
//                createImagePage(namePage );
                ConvertPdfToJPG convertPdfToJPG = new ConvertPdfToJPG();
                try {
                    convertPdfToJPG.convertPdfJpg(getUsbDir()+printPdfName);
                   createImagePage();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

            }
        });

        printText.setText("   Do_you want print or review?  "+ printPdfName);
        printText.setId("printText");
        printText.setPrefSize(700,75 );
        printText.setFont(Font.font("Cambria", 25));
        gridPanel.add(printText,3,0);
        gridPanel.add(printButton,0,1);
        gridPanel.add(reviewButton,5,1);
    }

    public String getUsbDir() throws InterruptedException {
        List<String> trustedUsb;
        trustedUsb = Arrays.asList("2CD225F7D225C5C4", "E4C2D7712B4CAF38", "51DF4EA45A129B6B", "CFB60A6580FE86CE");
        List<String> results;

        File[] files;
        for(int m = 0; m < 10; m++){
            results = new ArrayList<String>();
            files = new File("/media/vova/").listFiles();

            for (File file : files)
                if (file.isDirectory()) results.add(file.getName());

            for (int i = 0; i < trustedUsb.size(); i++)
                for (int j = 0; j < results.size(); j++)
                    if ((trustedUsb.get(i)).equals(results.get(j))) {
                        results.remove(j);
                        j--;
                    }
            if ((results.size() != 0)&&(results.get(0) != null)) {
                return "/media/vova/" + results.get(0) + "/";
            }
            Thread.sleep(500);
        }
        return null;
    }

    public List<String> usbList(String usbDirectory) {
        try {
            List<String> results = new ArrayList<String>();
            File[] files = new File(usbDirectory).listFiles();
            for (File file : files) {
                if ((file.isFile()) && ((file.getName().contains(".pdf") || file.getName().contains(".doc") || file.getName().contains(".docx")))) {
                    results.add(file.getName());
                }
            }
            System.out.print(results);
            return results;
        } catch (Exception e) {
            System.out.print("exept");
            return null;
        }
    }
    public void printImage(){
        try {
            List<String> results = new ArrayList<String>();
            File[] files = new File("/home/vova/IdeaProjects/Printer_PDF_DOC/out/production/Printer_PDF_DOC/").listFiles();
            for (File file : files) {
                if (file.isFile()){
                    results.add(file.getName());
                    

                }
            }
            System.out.print(results);
            return ;
        } catch (Exception e) {
            System.out.print("exept");
            return ;
        }

    }

    public void removeMenu(){
        gridButtonLeft.getChildren().remove(buttonMenu);

    }
    public void removedAll(){
        removeImageDir();
        removeImagePage();
        removeButtonList();
        removePrintList();
    }

    public void removeImageDir(){
        try {
            List<String> results = new ArrayList<String>();
            File[] files = new File("/home/vova/IdeaProjects/Printer_PDF_DOC/out/production/Printer_PDF_DOC/").listFiles();
            for (File file : files) {
                if (file.isFile()){
                    results.add(file.getName());
                    file.delete();
                }
            }
            System.out.print(results);
            return ;
        } catch (Exception e) {
            System.out.print("exept");
            return ;
        }


    }

    public void removeImagePage(){
        try {


            gridPanel.getChildren().remove(printButtonImage);
            gridPanel.getChildren().remove(nextButtonImage);
            gridPanel.getChildren().remove(printImage);
        }
        catch (Exception e){
            System.out.println("is removeImagePage remove");
        }
    }
    public void removeButtonList(){
        try {
            gridPanel.getChildren().remove(nextButton);
            gridPanel.getChildren().remove(updateButton);
            gridPanel.getChildren().remove(listView);
        }
        catch (Exception e){
            System.out.println("is removeButtonList remove");
        }
    }
    public void removePrintList(){
        try {


            gridPanel.getChildren().remove(reviewButton);
            gridPanel.getChildren().remove(printButton);
            gridPanel.getChildren().remove(printText);
        }
        catch (Exception e){
            System.out.println("is removePrintList remove");
        }
    }
}
