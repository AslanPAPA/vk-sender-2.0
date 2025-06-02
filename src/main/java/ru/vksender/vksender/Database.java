package ru.vksender.vksender;

import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;

public class Database {
    private static final String DB_NAME = "vksender.db";
    private static final String APP_DATA_DIR = "data";  // Папка для базы данных, изменил на "data"
    private static final String DB_PATH = APP_DATA_DIR + "/" + DB_NAME;
    private static final String CONNECTION_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        initDatabase();
    }

    private static void initDatabase() {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            try {
                Path path = Paths.get(APP_DATA_DIR);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }

                dbFile.createNewFile();

                System.out.println("Database file created at " + dbFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Database already exists at " + dbFile.getAbsolutePath());
        }
    }

    public static Connection connectDb() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connect = DriverManager.getConnection(CONNECTION_URL);

            connect.createStatement().execute("PRAGMA foreign_keys = ON");

            initializeDatabase(connect);
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS admin (" +
                        "email TEXT NOT NULL, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL, " +
                        "CONSTRAINT pk_admin PRIMARY KEY (username))"
        );

        conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS vkaccounts (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "token TEXT, " +
                        "accountname TEXT NOT NULL, " +
                        "description TEXT, " +
                        "image TEXT, " +
                        "first_name TEXT, " +
                        "last_name TEXT, " +
                        "admin_username TEXT NOT NULL, " +
                        "CONSTRAINT fk_admin FOREIGN KEY (admin_username) " +
                        "REFERENCES admin(username) ON DELETE CASCADE, " +
                        "CONSTRAINT uk_account UNIQUE (accountname, admin_username))"
        );
    }

    public static boolean backupDatabase(String backupPath) {
        try {
            Path source = Paths.get(DB_PATH);
            Path target = Paths.get(backupPath);
            Files.copy(source, target);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getConnectionUrl() {
        return CONNECTION_URL;
    }
}
