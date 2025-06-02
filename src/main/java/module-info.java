module ru.vksender.vksender {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sdk;
    requires javafx.graphics;
    requires javafx.base;
    requires org.slf4j;
    // Зависимости SQLite JDBC
    requires org.xerial.sqlitejdbc;
    requires java.desktop; // имя модуля sqlite-jdbc (если есть в JAR)


    opens ru.vksender.vksender to javafx.graphics, javafx.fxml, javafx.base;
}
