package ru.vksender.vksender;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    public static Connection connectDb() {
        try {
            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://localhost:5432/moviebook";
            String user = "postgres";
            String password = "Papawanttodeneg";

            Connection connect = DriverManager.getConnection(url, user, password);
            System.out.println("Подключение к базе данных успешно!");
            return connect;

        }catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
