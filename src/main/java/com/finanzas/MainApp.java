package com.finanzas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MainApp extends Application {

    private static ConfigurableApplicationContext springContext;
    private static StackPane rootPane;
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        String os = System.getProperty("os.name").toLowerCase();
        String perfil = os.contains("mac") ? "mac" : "windows";
        System.setProperty("spring.profiles.active", perfil);
        springContext = SpringApplication.run(MainApp.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        rootPane = new StackPane();
        Scene scene = new Scene(rootPane, 800, 600);
        stage.setTitle("Finanzas Granja");
        stage.setScene(scene);
        stage.show();
        navegarA("/fxml/principal-view.fxml", null);
    }

    public static void navegarA(String fxml, NavigationCallback callback) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
            loader.setControllerFactory(springContext::getBean);
            rootPane.getChildren().clear();
            rootPane.getChildren().add(loader.load());
            if (callback != null) {
                callback.onLoad(loader.getController());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface NavigationCallback {
        void onLoad(Object controller);
    }

    @Override
    public void stop() {
        springContext.close();
    }
}