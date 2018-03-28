import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.*;

public class FTP extends Application {
    private static ArrayList<String> listOfFiles = new ArrayList<>();
    private static final int WINDOW_HEIGHT = 650;
    private static final int WINDOW_WIDTH = 900;
    private static final String BUTTON_STYLE = "-fx-background-color: linear-gradient(#ff5400, #be1d00); -fx-background-radius: 30; -fx-background-insets: 0;-fx-text-fill: white; -fx-font : 26 arial;";
    private static final String LABEL_STYLE = "-fx-font-size: 27px; -fx-font-weight: bold;-fx-text-fill: #541313; -fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 ); ";
    private static final String UPLOAD_BUTTON_STYLE = "-fx-background-color: white; -fx-background-radius: 30; -fx-background-insets: 0;-fx-text-fill: white; -fx-font : 25 arial;-fx-text-fill : linear-gradient(#ff5400, #be1d00);-fx-font-weight: bold;";
    private static final String UPLOAD_TEXT_STYLE = "-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #541313; -fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 ); ";
    private static final String BROWSE_BUTTON_STYLE = "-fx-background-color: white; -fx-background-radius: 6; -fx-background-insets: 0; -fx-font : 18 arial;-fx-text-fill : linear-gradient(#ff5400, #be1d00);-fx-font-weight: bold;";
    
    public static void main(String[] args) {
        listOfFiles.add("Harry potter.mp4");
        listOfFiles.add(" GTA 5.exe");
        listOfFiles.add(" Simpsons.mp4");
        listOfFiles.add(" F.R.I.E.N.D.S.zip");
        listOfFiles.add(" Batman.zip");
        listOfFiles.add(" WWE.zip ");
        listOfFiles.add(" Dark Secrets.zip");
        listOfFiles.add(" Big Data.zip");
        listOfFiles.add(" XXX-Sunny Leone.mp4");
        listOfFiles.add(" Amanda Cerny Vines.mp4");
        listOfFiles.add(" BigTits.mp4");
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        int n = listOfFiles.size();
        ScrollPane scroll = new ScrollPane();
        Button[] buttons = new Button[n];
        Label[] labels = new Label[n];
        GridPane hbox = new GridPane();
        VBox vbox = new VBox();
        
        for (int i = 0; i < n; i++) {
            hbox.setPadding(new Insets(10, 10, 10, 10));
            labels[i] = new Label(i + 1 + ". " + listOfFiles.get(i));
            labels[i].setStyle(LABEL_STYLE);
            hbox.add(labels[i], 0, 2 * i, 2, 2);
            
            buttons[i] = new Button("Download");
            buttons[i].setStyle(BUTTON_STYLE);
            buttons[i].setLayoutX(WINDOW_WIDTH / 2);
            
            hbox.add(buttons[i], 2, 2 * i, 2, 2);
            hbox.setHgap(100);
            hbox.setVgap(20);
        }
        vbox.getChildren().add(hbox);
        scroll.setContent(vbox);
        scroll.setPrefHeight(WINDOW_HEIGHT * 3 / 4);
        scroll.setPrefWidth(WINDOW_WIDTH);
        scroll.setPadding(new Insets(0, 0, 0, 100));
        scroll.setStyle("-fx-background-color: linear-gradient(#ff5400, #be1d00);");
        
        Button upload = new Button("Upload");
        upload.setStyle(UPLOAD_BUTTON_STYLE);
        upload.setOnAction((ActionEvent e) -> {
            ProgressForm pForm = new ProgressForm("Uploading...");
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    for (int i = 0; i < 100; i++) {
                        updateProgress(i, 100);
                        Thread.sleep(40);
                    }
                    updateProgress(100, 100);
                    return null;
                }
            };
            
            pForm.activateProgressBar(task);
            
            task.setOnSucceeded(event -> pForm.getDialogStage().close());
            
            pForm.getDialogStage().show();
            
            Thread thread = new Thread(task);
            thread.start();
        });
        upload.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            // Shadow appearance when mouse enters the 'Upload' button
            DropShadow shadow = new DropShadow();
            upload.setEffect(shadow);
        });
        upload.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            // Shadow disappearance when mouse leaves the 'Upload' button
            upload.setEffect(null);
        });
        upload.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            upload.setTranslateY(2);
            upload.setTranslateX(-1);
        });
        upload.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            upload.setTranslateY(-2);
            upload.setTranslateX(1);
        });
        
        TextField filename = new TextField("Enter file's path to be uploaded");
        filename.setPrefWidth(450);
        filename.setStyle(UPLOAD_TEXT_STYLE);
        filename.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            DropShadow shadow = new DropShadow();
            filename.setEffect(shadow);
            if (filename.getText().contains("Enter file")) {
                filename.setText("");
            }
        });
        filename.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            DropShadow shadow = new DropShadow();
            filename.setEffect(shadow);
            if (filename.getText().compareTo("") == 0) {
                filename.setText("Enter file's path to be uploaded");
            }
        });
        
        Button browse = new Button("Browse");
        browse.setStyle(BROWSE_BUTTON_STYLE);
        browse.setOnAction((ActionEvent e) -> {
            FileChooser fc = new FileChooser();
            File file = fc.showOpenDialog(null);
            String path = file.getAbsolutePath();
            path = path.replace("\\", "/");
            filename.setText(path);
        });
        browse.setTranslateY(0.4);
        
        browse.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            DropShadow shadow = new DropShadow();
            browse.setEffect(shadow);
        });
        browse.addEventHandler(MouseEvent.MOUSE_EXITED, event -> browse.setEffect(null));
        
        browse.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            browse.setTranslateY(2);
            browse.setTranslateX(-1);
        });
        
        browse.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            browse.setTranslateY(-2);
            browse.setTranslateX(1);
        });
    
        HBox footerPane = new HBox(40);
        footerPane.setStyle("-fx-background-color: linear-gradient(#ff5400,#ff5400,#be1d00);");
        footerPane.setPrefHeight(WINDOW_HEIGHT / 4);
        footerPane.setPrefWidth(WINDOW_WIDTH);
        footerPane.setPadding(new Insets(50, 50, 50, 100));
        footerPane.getChildren().addAll(upload, filename, browse);
        
        VBox rootPane = new VBox();
        rootPane.setStyle("-fx-background-color : blue");
        rootPane.getChildren().addAll(scroll, footerPane);
        
        primaryStage.setTitle(" Client Machine ");
        primaryStage.setScene(new Scene(rootPane, 900, 650));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("iconFile.png")));
        primaryStage.show();
    }
}

class ProgressForm {
    private final Stage dialogStage;
    private final ProgressBar pb = new ProgressBar();
//    private final ProgressIndicator pin = new ProgressIndicator(); // Circle progress bar includes the % indicator
    
    public ProgressForm(String title) {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Blocks any interaction with the main window until dialog box is dealt with
        
        pb.setProgress(-1F);
//        pin.setProgress(-1F);
        
        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pb);
        
        Scene scene = new Scene(hb, 200, 100);
        dialogStage.setScene(scene);
        dialogStage.setTitle(title);
    }
    
    public void activateProgressBar(final Task<?> task) {
        pb.progressProperty().bind(task.progressProperty());
//        pin.progressProperty().bind(task.progressProperty());
        dialogStage.show();
    }
    
    public Stage getDialogStage() {
        return dialogStage;
    }
}
