package com.caco3.elijars.javafxsample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Sample extends Application {
    @Override
    public void start(Stage primaryStage) {
        Platform.exit();
    }

    public static void main(String[] args) throws Throwable {
        launch(args);

        String className = "javafx.geometry.Insets";
        Class.forName(className);
        System.out.println("Found class 'javafx.geometry.Insets'");
        System.out.println("End of main");
    }
}
