module ru.vksender.vksender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sdk;
    requires javafx.graphics;
    requires javafx.base;



    opens ru.vksender.vksender to javafx.graphics, javafx.fxml, javafx.base;
}
