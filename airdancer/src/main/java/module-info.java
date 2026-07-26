module airdancer {
    requires javafx.controls;
    requires javafx.media;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    opens com.komahito.app to com.almasb.fxgl.core, javafx.fxml;
    exports com.komahito.app;
}