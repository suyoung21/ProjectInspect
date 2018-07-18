package com.glink.utils;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    public static File createFile(String filePath, boolean deleteIfExist) throws IOException {

        File file = new File(filePath);

        if (deleteIfExist) {
            file.deleteOnExit();
        } else if (file.exists()) {
            return file;
        }

        File parentDir = file.getParentFile();
        boolean exist = parentDir.exists();
        if (!exist) {
            parentDir.mkdirs();
        }
        file.createNewFile();
        return file;
    }

    public static String createDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
