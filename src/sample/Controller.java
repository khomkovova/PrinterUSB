package sample;



import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.apache.commons.io.FileUtils;

import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.*;




public class Controller {
//    All it for static interface
    @FXML
    GridPane gridPanel;
    @FXML
    GridPane gridButtonRight;
    @FXML
    GridPane gridButtonLeft;
    @FXML
    GridPane gridPanelList;

    @FXML
    GridPane gridImageIcon;

    @FXML
    GridPane gridButtonCentr;
    @FXML
    GridPane gridbuttonLeftUp;
    @FXML
    GridPane gridbuttonLeftDown;
    @FXML
    GridPane gridButtonRightImage;
    @FXML
    GridPane gridButtonCentrImage;
    @FXML
    GridPane gridButtonLeftImage;

    @FXML
    TextField studentIDViewStatic;
    @FXML
    Label dialogLabel;
    @FXML
    Label allPagePrint;

    //    All it for dynamic interface
    @FXML
    public Scene scene;
    private TextField studentIDView = new TextField(); //Field for reading data with QR scanner
    private Button updateButton = new Button();
    private Button nextButton = new Button();
    private Button backButton = new Button();
    private Button upButton = new Button();
    private Button upButton2 = new Button();
    private Button downButton = new Button();
    private Button nextButtonImage = new Button();
    private Button backButtonImage = new Button();
    private Button printButton = new Button();
    private Button printButtonImage = new Button();
    private Button reviewButton = new Button();
    private ListView listView = new ListView(); //List for view data on USB
    private Label printText = new Label();
    private ImageView printImage = new ImageView();
    private ImageView imageIcon = new ImageView();
    private Image imageReview = null;
    private int numberImage = 1;
    private Dictionary<String, String> studentInfo = new Hashtable<String, String>();
    private BufferedImage image;
    private HostServices hostServices ;
    private String printPdfName = "";
    private String nameImage ;
    private String allPage;
    private String nameFileForm;
    private String dirChild = "";
    static ArduinoRead arduinoRead;
    String allPages;
    public void keyHandler(javafx.scene.input.KeyEvent keyEvent) throws  InterruptedException {
        System.out.println(keyEvent.getCode());

        //Back on main menu
        if (keyEvent.getCode() == KeyCode.SHIFT) {
//            PrinterSend printerSend = new PrinterSend();
//            printerSend.print_file("123.docx");

//            setAllPagePrint(0);
//            Arduino arduino = new Arduino();
//            arduino.sendComand(5);
            arduinoRead = new ArduinoRead();
            arduinoRead.start();

            checkPreviousAndNowPages();
//            System.out.println("str = " + backDirChildren("1234/567/789"));
            removeStudentInfo();
            dirChild = "";
//
            removedAll();
            createLoginForm();
            dialogLabel.setText("Hello insert student card and USB flash drive");
            System.out.println("remove all"+printPdfName);
        }

        if (keyEvent.getCode() == KeyCode.F4) {
            if(backButton.getText() != null)
                backButton.fire();
        }

        if (keyEvent.getCode() == KeyCode.F5) {
            if(nextButton.getText() != null)
                nextButton.fire();
        }

        if (keyEvent.getCode() == KeyCode.F3) {
            if(updateButton.getText() != null)
                updateButton.fire();
        }


        if (keyEvent.getCode() == KeyCode.F7) {
            if(backButtonImage.getText() != null)
                backButtonImage.fire();
        }

        if (keyEvent.getCode() == KeyCode.F8) {
            if(nextButtonImage.getText() != null)
                nextButtonImage.fire();
        }

        if (keyEvent.getCode() == KeyCode.F6) {
            if(printButtonImage.getText() != null)
                printButtonImage.fire();
        }



        //Read name file with list USB or send data of QR scanner
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
    //Write a number of pages in file
    private void setAllPagePrint(int plusPages)  {
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

    //Get data of a user from the databases
    private void getUserInfo(String codeID) throws SQLException {
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
                dialogLabel.setText(studentInfo.get("name") + " !!!"  + '\n' + " You have " + studentInfo.get("numberPage") + " pages for print.");
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            connection.close();
        }
    }

    private void createLoginForm(){
        studentIDView.setText("");
        studentIDView.setStyle("-fx-font-size: 3px ;");
        studentIDView.setId("StudentID");
        gridPanel.add(studentIDView,1,1);
    }

    private void createFileListPage() throws InterruptedException {
        upButton.setText("Up");
        upButton.setId("upButton");
        upButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        upButton.setPrefSize(200, 100);

//        upButton2.setText("");
//        upButton2.setId("upButton2");
//        upButton2.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
//        upButton2.setPrefSize(200, 100);

        downButton.setText("Down");
        downButton.setId("downButton");
        downButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        downButton.setPrefSize(200, 100);


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


        backButton.setText("Back");
        backButton.setId("backButton");
        backButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        backButton.setPrefSize(200, 100);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {

                try {
                    dirChild = backDirChildren(dirChild);
                    removeFileListPage();
                    createFileListPage();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();

                }
            }
        });

        BufferedImage bufferedImageNo = null;
        try {
            bufferedImageNo = ImageIO.read(new File("noname.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bufferedImageNo != null) {
            imageReview = SwingFXUtils.toFXImage(bufferedImageNo, null);

            imageIcon.setImage(imageReview);
            imageIcon.setFitWidth(210.5 * 2);
            imageIcon.setFitHeight(297.5 * 2);

        }



        nextButton.setText("Next");
        nextButton.setId("nextButton");
        nextButton.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        nextButton.setPrefSize(200, 100);
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (printPdfName != null) {

                    // If the print name is a folder then change child directory and restruct FileListPage.
                    if(printPdfName.contains("Folder : ")){
                        try {
                            dirChild += printPdfName.replace("Folder : ", "") + "/";
                            System.out.println("DirChild = " + dirChild);
                            removedAll();
                            createFileListPage();

                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    // Create imagePage and send the file on convert
                    else {
                        nameFileForm = printPdfName;
                        backButton.setText(null);
                        updateButton.setText(null);
                        nextButton.setText(null);
                        removeFileListPage();
                        String namePage = String.valueOf(numberImage) + ".jpg";
                        System.out.println(namePage);
                        ConvertPdfToJPG convertPdfToJPG = new ConvertPdfToJPG();
                        File fileRead = null;
                        try {
                            fileRead = new File(getUsbDir() + nameFileForm);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        File dir = new File("FilePrint");
                        dir.mkdir();
                        File fileWrite = new File("FilePrint");
                        try {
                            FileUtils.copyFileToDirectory(fileRead, fileWrite);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

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
                                convertPdfToJPG.convertPdfJpg("PDF/" + printPdfName);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        createImagePage();
                    }

                }
            }
        });

        try {
            gridButtonCentr.add(backButton, 1,0);
            gridButtonLeft.add(updateButton, 0, 0);
            gridButtonRight.add(nextButton, 5, 0);
            gridbuttonLeftUp.add(upButton, 1, 0);
//            gridbuttonLeftUp.add(upButton2, 1, 1);
            gridbuttonLeftDown.add(downButton, 2, 0);
            gridImageIcon.add(imageIcon, 0, 0);

        }catch (Exception e){
            System.out.println("updateButton it is");
        }

        listView.setMinWidth(700);
        listView.setLayoutY(200);
        listView.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5);;-fx-control-inner-background: transparent;-fx-font-size: 25px;-fx-text-fill: #CCA300;");
        String usbDir = getUsbDir();
        if(usbDir != null) {
            List<String> listUsb = usbList(getUsbDir() + dirChild);
            if (listUsb != null) {
                ObservableList<String> items = FXCollections.observableArrayList(listUsb);
                listView.setItems(items);
            }
        }
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                gridImageIcon.getChildren().remove(imageIcon);

                printPdfName = newValue.toString();
                String name = "";
                if(printPdfName.contains(""))  name = "noname.jpg";
                if(printPdfName.contains(".pdf"))  name = "pdf.jpg";
                if(printPdfName.contains(".doc"))  name = "doc.jpg";
                if(printPdfName.contains("Folder"))  name = "folder.jpg";
                if(!name.equals("")){
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(new File(name));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(bufferedImage != null){
                imageReview = SwingFXUtils.toFXImage(bufferedImage, null);

                imageIcon.setImage(imageReview);
                imageIcon.setFitWidth(210.5*2);
                imageIcon.setFitHeight(297.5*2);
                gridImageIcon.add(imageIcon,0,0);
                }
                }
            }
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

    private void createImagePage(){
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

//                printPdfName
                dirChild = "";
                removeStudentInfo();
                removedAll();
                createLoginForm();
                try {
                    checkPreviousAndNowPages();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
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
                nameImage = "JPG/"+String.valueOf(numberImage) + ".jpg";
                System.out.println(nameImage);
                createImagePage();
            }
        });



        backButtonImage.setText("Back");
        backButtonImage.setId("BackButtonPage");
        backButtonImage.setStyle("-fx-background-color: #8B4513;-fx-font-size: 30px ;");
        backButtonImage.setPrefSize(200, 100);
        backButtonImage.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if(numberImage > 2) {
                    backButtonImage.setText(null);
                    removePrintList();
                    removeImagePage();

                    numberImage-=2;
                    nameImage = "JPG/" + String.valueOf(numberImage) + ".jpg";
                    System.out.println(nameImage);
                    createImagePage();
                }
            }
        });




        System.out.println("name image = " + nameImage);
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(nameImage));
            imageReview = SwingFXUtils.toFXImage(bufferedImage, null);

            printImage.setImage(imageReview);
            printImage.setFitWidth(210*3.5);
            printImage.setFitHeight(297*3.5);
            gridPanel.add(printImage,0,0);

            imageReview.cancel();
            numberImage++;
            System.out.println("ok open image = " + nameImage);
            gridButtonLeftImage.getChildren().remove(printButtonImage);
            gridButtonRightImage.getChildren().remove(nextButtonImage);
            gridButtonCentrImage.getChildren().remove(backButtonImage);
            gridButtonCentrImage.add(backButtonImage,1,1);
            gridButtonLeftImage.add(printButtonImage,0,1);
            gridButtonRightImage.add(nextButtonImage,5,1);
        }
        catch (Exception e){
            System.out.println("not image");
            if(numberImage > 1){
                numberImage = 1;
                removeImagePage();
                nameImage = "JPG/" +String.valueOf(numberImage)  + ".jpg";
                System.out.println(nameImage);
                createImagePage();
                gridButtonLeftImage.getChildren().remove(printButtonImage);
                gridButtonRightImage.getChildren().remove(nextButtonImage);
                gridButtonCentrImage.getChildren().remove(backButtonImage);
                gridButtonCentrImage.add(backButtonImage,1,1);
                gridButtonLeftImage.add(printButtonImage,0,1);
                gridButtonRightImage.add(nextButtonImage,5,1);
            }
            else {
                gridButtonLeftImage.getChildren().remove(printButtonImage);
                gridButtonRightImage.getChildren().remove(nextButtonImage);
                gridButtonCentr.getChildren().remove(backButtonImage);
//                removeImagePage();
                createLoginForm();
                dialogLabel.setText("This format is not supported");
            }


        }



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
                if(printPdfName.equals(".pdf")) {
                    ConvertPdfToJPG convertPdfToJPG = new ConvertPdfToJPG();
                    try {
                        convertPdfToJPG.convertPdfJpg(getUsbDir() + printPdfName);
                        createImagePage();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                else {

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

    private void  checkPreviousAndNowPages() throws InterruptedException {
        String previousPage = "0";
        String nowPage = "0";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("AllPagesPrint.txt"));
            nowPage = reader.readLine();
            reader.close();
        }catch (Exception e){
            System.out.println("not AllPagesPrint.txt");
            File fileAllPages = new File("AllPagesPrint.txt");
            try {
                fileAllPages.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader("FirstArduinoPrint.txt"));
            previousPage = reader.readLine();
            reader.close();
        }catch (Exception e){
            System.out.println("not FirstArduinoPrint.txt");
            File fileFirstPages = new File("FirstArduinoPrint.txt");
            try {
                fileFirstPages.createNewFile();
                BufferedWriter writer = null;
                writer = new BufferedWriter(new FileWriter("FirstArduinoPrint.txt"));
                writer.write('0');
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("firs = " + previousPage + " now page = "+nowPage );
        int diferencePages = 0;
        diferencePages = Integer.parseInt(nowPage) - Integer.parseInt(previousPage);

        if(diferencePages > 2) {
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("FirstArduinoPrint.txt"));
                writer.write(nowPage);
                writer.close();
                Arduino arduino = new Arduino();
                arduino.sendComand(diferencePages);
                dialogLabel.setText("arduino send comand");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("not FirstArduinoPrint.txt");
            }
        }
    }

    private String getUsbDir() throws InterruptedException {
        List<String> trustedUsb;
        trustedUsb = Arrays.asList("2CD225F7D225C5C4", "E4C2D7712B4CAF38", "51DF4EA45A129B6B", "CFB60A6580FE86CE");
        List<String> results;

        File[] files;
        for(int m = 0; m < 5; m++){
            results = new ArrayList<String>();
            files = new File("/media/vova/").listFiles();

            for (File file : files)
                if (file.isDirectory()) {
                    BasicFileAttributes attr = null;
                    try {
                        attr = Files.readAttributes(Paths.get(file.getPath()), BasicFileAttributes.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("total spase = "+attr.);

                results.add(file.getName());
                }

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
//
//    private String getUsbDir() {
//        File media_file;
//        media_file = new File("/media/");
//        for (int m = 0; m < 5; m++) {
//            File[] files = media_file.listFiles();
//            File user_file = files[0];
//            File[] usb_file = user_file.listFiles();
//            if (usb_file[0].getPath() != null) {
//                System.out.println(usb_file[0].getPath());
//                return usb_file[0].getPath() + "/";
//            }
//
//
//        }
//        return null;
//    }


    private List<String> usbList(String usbDirectory) {
        try {
            List<String> results = new ArrayList<String>();
            File[] files = new File(usbDirectory).listFiles();
            for (File file : files) {
                if ((!file.getName().contains(" ")) && file.isFile() && ((file.getName().contains(".pdf") || file.getName().contains(".doc") || file.getName().contains(".docx")))) {
                    results.add(file.getName());
                }
                else if(file.isDirectory()){
                    results.add("Folder : " + file.getName());
                }
            }
            System.out.print(results);
            return results;
        } catch (Exception e) {
            System.out.print("exept");
            return null;
        }
    }

    private void printImage() throws SQLException, IOException {
        File[] files = new File("JPG").listFiles();
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
                PrinterSend printerSend = new PrinterSend();
                printerSend.print_file(nameFileForm);


                setAllPagePrint(files.length);


            }
            catch (Exception e) {
                System.out.println("Sql not work");
            }
            dialogLabel.setText("Hello insert student card and USB flash drive");
            connection.close();

            System.out.println("OK PRINT");
            }
            else{
            dialogLabel.setText("You have exceeded the limit. You access to " + numberPage + " pages ");
            setAllPagePrint(0);

        }
    }

//    public void removeMenu(){
//        gridButtonLeft.getChildren().remove(buttonMenu);
//    }

    private String backDirChildren(String child){
        String[] arrChild= child.split("/");
        String dirBack = "";
        for(int i = 0; i < arrChild.length -1; i++){
            dirBack += arrChild[i];
        }
        return dirBack;

    }

    private void removeStudentIDPage(){
        gridPanel.getChildren().remove(studentIDView);
        gridPanel.getChildren().remove(studentIDViewStatic);
        System.out.println("Student removed");
    }

    private void removeStudentInfo(){
        studentInfo.remove("name");
        studentInfo.remove("studentID");
        studentInfo.remove("numberPage");
    }

    private void removedAll(){
        numberImage = 1;

        removePDFDir();
        removeStudentIDPage();
        removeImageDir();
        removeImagePage();
        removeFileListPage();
        removePrintList();
        removeDOCDir();
    }

    private void removeDOCDir(){
        try {
            File[] files = new File("FilePrint").listFiles();
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

    private void removePDFDir(){
        try {
            File[] files = new File("PDF").listFiles();
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

    private void removeImageDir(){
        try {
            File[] files = new File("JPG").listFiles();
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

    private void removeImagePage(){
        try {
//            printButtonImage.setText("");
//            backButtonImage.setText("");
//            nextButtonImage.setText("");
            gridButtonLeftImage.getChildren().remove(printButtonImage);
            gridButtonCentrImage.getChildren().remove(backButtonImage);
            gridButtonRightImage.getChildren().remove(nextButtonImage);
            gridPanel.getChildren().remove(printImage);

        }
        catch (Exception e){
            System.out.println("is removeImagePage remove");
        }
    }

    private void removeFileListPage(){
        try {
            gridButtonRight.getChildren().remove(nextButton);
            gridButtonLeft.getChildren().remove(updateButton);
            gridButtonCentr.getChildren().remove(backButton);
            gridPanelList.getChildren().remove(listView);
            gridbuttonLeftUp.getChildren().remove(upButton);
            gridbuttonLeftDown.getChildren().remove(downButton);
            gridImageIcon.getChildren().remove(imageIcon);
        }
        catch (Exception e){
            System.out.println("is removeButtonList remove");
        }
    }

    private void removePrintList(){
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
