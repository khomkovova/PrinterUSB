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
import javafx.scene.layout.GridPane;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.newObjectType;


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
    GridPane gridPanelList;
    @FXML
    TextField studentIDViewStatic;
    @FXML
    Label dialogLabel;
    @FXML
    Label allPagePrint;


    @FXML
    public Scene scene;
    private TextField studentIDView = new TextField();
    private Button updateButton = new Button();
    private Button nextButton = new Button();
    private Button nextButtonImage = new Button();
    private Button printButton = new Button();
    private Button printButtonImage = new Button();
    private Button reviewButton = new Button();
    private ListView listView = new ListView();
    private Label printText = new Label();
    private ImageView printImage = new ImageView();
    private Image imageReview = null;
    private int numberImage = 1;
    private Dictionary<String, String> studentInfo = new Hashtable<String, String>();
    public BufferedImage image;
    private HostServices hostServices ;
    private String printPdfName;
    private String  nameImage ;
    private String allPage;
    String allPages;
    public void keyHandler(javafx.scene.input.KeyEvent keyEvent) throws InterruptedException, IOException, SQLException {
        System.out.println(keyEvent.getCode());
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            Arduino arduino = new Arduino();
            arduino.sendComand();
            setAllPagePrint(0);
            dialogLabel.setText("Hello insert student card and USB flash drive");
            checkPreviousAndNowPages();

            removedAll();
            createLoginForm();
            System.out.println("remove all"+printPdfName);
        }

        if (keyEvent.getCode() == KeyCode.D) {
            if (nextButton.getText() != null) {
                nextButton.fire();

            }
            else if (nextButtonImage.getText() != null) {
                printButtonImage.setText(null);
                nextButtonImage.fire();
            }
            System.out.println("remove all"+printPdfName);
        }

        if (keyEvent.getCode() == KeyCode.A) {
            if (updateButton.getText() != null) {
                nextButton.setText(null);
                updateButton.fire();
            }
            else if (printButtonImage.getText() != null) {
                nextButtonImage.setText(null);
                printButtonImage.fire();
            }
            System.out.println("remove all"+printPdfName);
        }


        if (keyEvent.getCode() == KeyCode.ENTER) {
            try {
                allPagePrint.setText("");
                String studentID;
                System.out.println("student ID dinamic= " + studentIDView.getText());
                System.out.println("student ID static= " + studentIDViewStatic.getText());
                if (studentIDView.getText().equals("") || studentIDView.getText() == null) {
                    studentID = studentIDViewStatic.getText();
                } else studentID = studentIDView.getText();
                if(!studentID.equals("") && studentID != null) {
                    getUserInfo(studentID);
                    studentIDView.setText("");
                    studentIDViewStatic.setText("");
                    System.out.println("stutent inf" + studentInfo);
                    if (!studentInfo.isEmpty()) {
                        removeStudentIDPage();
                        createFileListPage();
                    }
                    else {
                        dialogLabel.setText("This student card is not in the database");
                        setAllPagePrint(0);
                    }
                    System.out.println(keyEvent.getText());
                }
            }
            catch (Exception e ){
                System.out.println("Not StudentViev12345");
            }
        }


    }
//    public void getAllPagePrint() throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader("AllPagesPrint.txt"));
//        allPage = reader.readLine();
//        reader.close();
//        System.out.println("all page = " + allPage);
//        allPagePrint.setText(allPage);
//    }

    public void setAllPagePrint(int plusPages)  {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AllPagesPrint.txt"));
            allPage = reader.readLine();
            reader.close();
        }catch (Exception e){
            System.out.println("not AllPagesPrint.txt");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("AllPagesPrint.txt"));

            if(allPage == null) allPage = "0";
                String str = String.valueOf(Integer.parseInt(allPage) + plusPages);
                writer.write(str);
                writer.close();

                allPagePrint.setText(String.valueOf(Integer.parseInt(allPage) + plusPages));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("not AllPagesPrint.txt");
        }
    }

    public void getUserInfo(String codeID) throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/UserPrint?" + "user=root&password=12345");
            Statement statement=connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from UserPrint where Student_ID ="+"'"+codeID+"'");
            while(resultSet.next()) {
                System.out.println(resultSet.getString(2) + "  " + resultSet.getString(4));
                studentInfo.put("name", resultSet.getString(2));
                studentInfo.put("studentID", resultSet.getString(3));
                studentInfo.put("numberPage", resultSet.getString(4));
                dialogLabel.setText("Hi " + studentInfo.get("name") + " !!!"  + " You have " + studentInfo.get("numberPage") + " pages for print.");
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            connection.close();
        }
    }

    public void createLoginForm(){
        studentIDView.setText("");
        studentIDView.setStyle("-fx-font-size: 3px ;");
        studentIDView.setId("StudentID");
        gridPanel.add(studentIDView,1,1);
    }


    public void createFileListPage() throws InterruptedException {

        updateButton.setText("Update");
        updateButton.setId("updateButton");
        updateButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        updateButton.setPrefSize(200, 100);
        updateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                updateButton.setText(null);
                try {
                    removeFileListPage();
                    createFileListPage();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        });


        nextButton.setText("Next");
        nextButton.setId("nextButton");
        nextButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        nextButton.setPrefSize(200, 100);
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (printPdfName != null) {
                    updateButton.setText(null);
                    nextButton.setText(null);
                    removeFileListPage();
                    String namePage = String.valueOf(numberImage) + ".jpg";
                    System.out.println(namePage);
                    ConvertPdfToJPG convertPdfToJPG = new ConvertPdfToJPG();
                    if (printPdfName.contains(".pdf")) {

                        try {
                            System.out.println("print pdf = " + printPdfName);
                            convertPdfToJPG.convertPdfJpg(getUsbDir() + printPdfName);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    } else if (printPdfName.contains(".doc")) {
                        System.out.println("convert start");
                        System.out.println("print pdf = " + printPdfName);
                        WorldToPDF worldToPDF = new WorldToPDF();
                        try {
                            worldToPDF.conwertWorldPdf(getUsbDir() + printPdfName);
                            printPdfName = printPdfName.substring(0, printPdfName.indexOf('.')) + ".pdf";
                            System.out.println("print pdf = " + printPdfName);
                            convertPdfToJPG.convertPdfJpg("out/production/Printer_PDF_DOC/PDF/" + printPdfName);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    createImagePage();

                }
            }
        });

        try {
            gridButtonLeft.add(updateButton, 0, 0);
            gridButtonRight.add(nextButton, 5, 0);
        }catch (Exception e){
            System.out.println("updateButton it is");
        }

        listView.setMinWidth(700);
        listView.setLayoutY(200);
        listView.setStyle("-fx-background-color: transparent;-fx-control-inner-background: transparent;-fx-font-size: 25px;-fx-text-fill: #CCA300;");
        String usbDir = getUsbDir();
        if(usbDir != null) {
            List<String> listUsb = usbList(getUsbDir());
            if (listUsb != null) {
                ObservableList<String> items = FXCollections.observableArrayList(listUsb);
                listView.setItems(items);
            }
        }
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) printPdfName = newValue.toString();
            else printPdfName = null;
        });
        gridPanelList.add(listView,3,1);
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
        nameImage = "JPG/"+String.valueOf(numberImage)  + ".jpg";
        dialogLabel.setText("");
        gridPanel.getChildren().remove(printImage);
        printButtonImage.setText("Print2");
        printButtonImage.setId("printButton");
        printButtonImage.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        printButtonImage.setPrefSize(200, 100);
        printButtonImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                printButtonImage.setText(null);
                try {
                    printImage();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                removedAll();
                createLoginForm();
            }
        });


        nextButtonImage.setText("Next2");
        nextButtonImage.setId("nextButton2");
        nextButtonImage.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        nextButtonImage.setPrefSize(200, 100);
        nextButtonImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                nextButtonImage.setText(null);
                removePrintList();
                removeImagePage();

                nameImage = "JPG/"+String.valueOf(numberImage)  + ".jpg";
                System.out.println(nameImage);
                createImagePage();
            }
        });



        System.out.println("name image = " + nameImage);
        try {
            imageReview = new Image(nameImage);

            printImage.setImage(imageReview);
            printImage.setFitWidth(210*3.5);
            printImage.setFitHeight(297*3.5);
            gridPanel.add(printImage,0,0);

            imageReview.cancel();
            numberImage++;
            System.out.println("ok open image = " + nameImage);
            gridButtonLeft.getChildren().remove(printButtonImage);
            gridButtonRight.getChildren().remove(nextButtonImage);
            gridButtonLeft.add(printButtonImage,0,1);
            gridButtonRight.add(nextButtonImage,5,1);
        }
        catch (Exception e){
            System.out.println("not image");
            if(numberImage > 1){
                numberImage = 1;
                removeImagePage();
                nameImage = "JPG/" +String.valueOf(numberImage)  + ".jpg";
                System.out.println(nameImage);
                createImagePage();
                gridButtonLeft.getChildren().remove(printButtonImage);
                gridButtonRight.getChildren().remove(nextButtonImage);
                gridButtonLeft.add(printButtonImage,0,1);
                gridButtonRight.add(nextButtonImage,5,1);
            }
            else {
                gridButtonLeft.getChildren().remove(printButtonImage);
                gridButtonRight.getChildren().remove(nextButtonImage);
//                removeImagePage();
                createLoginForm();
                dialogLabel.setText("This format is not supported");
            }


        }



    }

//    public void creatPritList(){
//        printButton.setText("Print");
//        printButton.setId("printButton");
//        printButton.setPrefSize(200, 100);
//        printButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent e) {
//                removePrintList();
//                removeImageDir();
//            }
//        });
//
//
//        reviewButton.setText("Review");
//        reviewButton.setId("reviewButton");
//        reviewButton.setPrefSize(200, 100);
//        reviewButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent e) {
//                removePrintList();
//                String namePage = String.valueOf(numberImage)  + ".jpg";
//                System.out.println(namePage);
////                createImagePage(namePage );
//                if(printPdfName.equals(".pdf")) {
//                    ConvertPdfToJPG convertPdfToJPG = new ConvertPdfToJPG();
//                    try {
//                        convertPdfToJPG.convertPdfJpg(getUsbDir() + printPdfName);
//                        createImagePage();
//                    } catch (IOException e1) {
//                        e1.printStackTrace();
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//                else {
//
//                }
//
//            }
//        });
//
//        printText.setText("   Do_you want print or review?  "+ printPdfName);
//        printText.setId("printText");
//        printText.setPrefSize(700,75 );
//        printText.setFont(Font.font("Cambria", 25));
//        gridPanel.add(printText,3,0);
//        gridPanel.add(printButton,0,1);
//        gridPanel.add(reviewButton,5,1);
//    }

    public void  checkPreviousAndNowPages(){
        String previousPage = "0";
        String nowPage = "0";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AllPagesPrint.txt"));
            nowPage = reader.readLine();
            reader.close();
        }catch (Exception e){
            System.out.println("not AllPagesPrint.txt");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("FirstArduinoPrint.txt"));
            previousPage = reader.readLine();
            reader.close();
        }catch (Exception e){
            System.out.println("not FirstArduinoPrint.txt");
        }
        System.out.println("firs = " + previousPage + " now page = "+nowPage );

        if((Integer.parseInt(nowPage) - Integer.parseInt(previousPage)) > 50) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("FirstArduinoPrint.txt"));
                writer.write(nowPage);
                writer.close();
                dialogLabel.setText("arduino send comand");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("not FirstArduinoPrint.txt");
            }
        }
    }

    public String getUsbDir() throws InterruptedException {
        List<String> trustedUsb;
        trustedUsb = Arrays.asList("2CD225F7D225C5C4", "E4C2D7712B4CAF38", "51DF4EA45A129B6B", "CFB60A6580FE86CE");
        List<String> results;

        File[] files;
        for(int m = 0; m < 5; m++){
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
                if ((!file.getName().contains(" ")) && file.isFile() && ((file.getName().contains(".pdf") || file.getName().contains(".doc") || file.getName().contains(".docx")))) {
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

    public void printImage() throws SQLException, IOException {
        File[] files = new File("/home/vova/IdeaProjects/Printer_PDF_DOC/out/production/Printer_PDF_DOC/JPG").listFiles();
        System.out.println("number = " + studentInfo.get("numberPage"));
        int numberPage = Integer.parseInt(studentInfo.get("numberPage"));
        if (files.length <= numberPage) {
            System.out.println(studentInfo);
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/UserPrint?" + "user=root&password=12345");
                String comand = "UPDATE UserPrint SET Number_Page = ? WHERE Student_ID = ?";
                PreparedStatement preparedStmt = connection.prepareStatement(comand);
                preparedStmt.setInt   (1, (numberPage - files.length));
                preparedStmt.setString(2, studentInfo.get("studentID"));
                preparedStmt.executeUpdate();

            }
            catch (Exception e) {
                System.out.println("Sql not work");
            }
            setAllPagePrint(files.length);
            dialogLabel.setText("Hello insert student card and USB flash drive");

//            PrinterSend printerSend = new PrinterSend();
//            printerSend.print();
            connection.close();

            System.out.println("OK PRINT");
            }
            else{
            dialogLabel.setText("You have exceeded the limit. You access to " + numberPage + " pages ");
            setAllPagePrint(0);

        }
    }

    public void removeMenu(){
        gridButtonLeft.getChildren().remove(buttonMenu);
    }

    public void removeStudentIDPage(){
        gridPanel.getChildren().remove(studentIDView);
        gridPanel.getChildren().remove(studentIDViewStatic);
        System.out.println("Student removed");
    }

    public void removedAll(){
        numberImage = 1;
        studentInfo.remove("name");
        studentInfo.remove("studentID");
        studentInfo.remove("numberPage");
        removePDFDir();
        removeStudentIDPage();
        removeImageDir();
        removeImagePage();
        removeFileListPage();
        removePrintList();
    }

    public void removePDFDir(){
        try {
            File[] files = new File("/home/vova/IdeaProjects/Printer_PDF_DOC/out/production/Printer_PDF_DOC/PDF").listFiles();
            for (File file : files) {
                if (file.isFile()){
                    file.delete();
                }
            }
            return ;
        } catch (Exception e) {
            System.out.print("exept");
            return ;
        }
    }

    public void removeImageDir(){
        try {
            File[] files = new File("/home/vova/IdeaProjects/Printer_PDF_DOC/out/production/Printer_PDF_DOC/JPG").listFiles();
            for (File file : files) {
                if (file.isFile()){
                    file.delete();
                }
            }
            return ;
        } catch (Exception e) {
            System.out.print("exept");
            return ;
        }


    }

    public void removeImagePage(){
        try {


            gridButtonLeft.getChildren().remove(printButtonImage);
            gridButtonRight.getChildren().remove(nextButtonImage);
            gridPanel.getChildren().remove(printImage);
        }
        catch (Exception e){
            System.out.println("is removeImagePage remove");
        }
    }

    public void removeFileListPage(){
        try {
            gridButtonRight.getChildren().remove(nextButton);
            gridButtonLeft.getChildren().remove(updateButton);
            gridPanelList.getChildren().remove(listView);
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
