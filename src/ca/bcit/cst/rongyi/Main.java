package ca.bcit.cst.rongyi;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Main
 *
 * @author Rongyi Chen
 * @version 2017
 */
public class Main extends Application {
    
    private final static double MIN_WINDOW_WIDTH = 900.0;
    private final static double MIN_WINDOW_HEIGHT = 700.0;
    
    private Image image;
    private ImageView imgView;
    private ScrollPane displayPane;
    private Label imageRatioLabel;
    private Label imageSizeLabel;
    private ProgressBar progressBar;
    private Label progressLabel;
    private HBox statusBar;
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();
        
        MenuBar menuBar = this.getMenuBar();
        
        imgView = new ImageView();
        displayPane = new ScrollPane(imgView);
        
        statusBar = this.getStatusBar();
        
        root.getChildren().addAll(menuBar, displayPane, statusBar);
        VBox.setVgrow(displayPane, Priority.ALWAYS);
        root.setPrefSize(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Image Blur");
        primaryStage.show();
        promptForReadFile();
    }
    
    private MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");
        MenuItem readeFromFileMenuItem = new MenuItem("Read From File");
        readeFromFileMenuItem.setOnAction(this::actionReadFromFile);
        file.getItems().addAll(readeFromFileMenuItem);

        Menu effect = new Menu("Effect");
        MenuItem boxBlurItem = new MenuItem("Box Blur");
        boxBlurItem.setOnAction(event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    updateMessage("Box Blur");
                    image = ImageProcesser.boxBlur(image);
                    imgView.setImage(image);
                    updateProgress(1, 1);
                    updateMessage("Finished");
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.messageProperty());
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        });
        
        MenuItem mosaicItem = new MenuItem("Mosaic");
        mosaicItem.setOnAction(event -> {
            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() {
                    updateMessage("Mosaic");
                    image = ImageProcesser.mosaic(image, 20);
                    imgView.setImage(image);
                    updateProgress(1, 1);
                    updateMessage("Finished");
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            progressLabel.textProperty().bind(task.messageProperty());
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        });
        
        effect.getItems().addAll(boxBlurItem, mosaicItem);

        menuBar.getMenus().addAll(file, effect);
        return menuBar;
    }
    
    private HBox getStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setSpacing(5.0);

        imageRatioLabel = new Label();
        imageRatioLabel.setMinWidth(100.0);
        imageSizeLabel = new Label();
        imageSizeLabel.setMinWidth(100.0);
        
        progressBar = new ProgressBar(0.0);
        progressLabel = new Label();
        
        statusBar.getChildren().addAll(
                imageRatioLabel, separator(Orientation.VERTICAL),
                imageSizeLabel, separator(Orientation.VERTICAL),
                progressBar, progressLabel);
        
        return statusBar;
    }
    
    private Separator separator(Orientation o) {
        Separator separator = new Separator();
        separator.setOrientation(o);
        
        return separator;
    }

    private void actionReadFromFile(ActionEvent event) {
        promptForReadFile();
    }
    
    private void loadImage(File file) {
        image = new Image(file.toURI().toString());
        imgView.setImage(image);
        
        double w = image.getWidth();
        double h = image.getHeight();
        double ratio = 1.0;
        
        if (w > h) {
            if (displayPane.getWidth() < w) 
                ratio = (displayPane.getWidth()) / w;
        } else {
            if (displayPane.getHeight() < h)
                ratio = (displayPane.getHeight()) / h;
        }
        
        imgView.setFitWidth(w * ratio);
        
        NumberFormat fmt = DecimalFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);
        imageRatioLabel.setText(String.valueOf(fmt.format(ratio)));
        imageSizeLabel.setText(String.format("%s x %s px", (int) w, (int) h));
        
        imgView.setPreserveRatio(true);
    }
    
    /**
     * Prompt a dialog to ask for location to read file.
     * after submit.
     */
    private void promptForReadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open From File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("JPEG", "*.jpg"), 
                new ExtensionFilter("PNG", "*.png"), 
                new ExtensionFilter("BMP", "*.bmp"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        loadImage(selectedFile);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

}
