package com.skorobahatko.webcamcapture;

import com.skorobahatko.webcamcapture.view.MainWindow;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

public class App {

    public static void main(String[] args) throws Exception {
        loadLib();

        EventQueue.invokeLater(() -> {
            MainWindow window = new MainWindow();
        });
    }

    private static void loadLib() throws Exception {

        String path = "\\lib\\libxuggle-5.dll";

        // Path to .dll file when run app from jar file
//        String parentPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation()
//                .toURI()).getParent();

        // Path to .dll file when run app from ide
        String parentPath = new File(App.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile().getParentFile().toString();

        path = parentPath + path;

        System.setProperty("java.library.path", parentPath + "\\lib");
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);


        try {
            // load native library
            System.load(path);
        } catch (Exception e) {
            throw new Exception("Exception while loading native library: " + e.getMessage());
        }

    }

}
