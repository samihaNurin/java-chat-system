import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AppData.init();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChatView.fxml"));
        Scene scene = new Scene(loader.load(), 980, 660);
        scene.getStylesheets().add(getClass().getResource("/dark.css").toExternalForm());

        stage.setTitle("Chat Engine Application (CEA)");
        stage.setScene(scene);
        stage.setMinWidth(780);
        stage.setMinHeight(520);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}