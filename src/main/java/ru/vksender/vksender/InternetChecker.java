package ru.vksender.vksender;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

public class InternetChecker {
    public static boolean isInternetAvailable() {
        String[] hosts = {
                "vk.com",
                "google.com",
                "8.8.8.8",
                "1.1.1.1"
        };

        for (String host : hosts) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, 80), 2000);
                return true;
            } catch (Exception e) {
                continue;
            }
        }
        return false;
    }
}
