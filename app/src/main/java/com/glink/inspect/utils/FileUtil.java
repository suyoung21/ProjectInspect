package com.glink.inspect.utils;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.util.Base64;

import com.glink.R;
import com.glink.inspect.data.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {

    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format(ResUtil.getString(R.string.app_name) + "_%s.jpg", timeStamp);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageFileName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    public static String getRecorderPathDir() {
        String storageDir = Const.APP_AUDIO_FILES_PATH;
        return FileUtil.createDirectory(storageDir);
    }

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

    /**
     * encodeBase64File:(将文件转成base64 字符串). <br/>
     *
     * @param path 文件路径
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    /**
     * decoderBase64File:(将base64字符解码保存文件). <br/>
     *
     * @param base64Code 编码后的字串
     * @param savePath   文件保存路径
     * @throws Exception
     */
    public static void decoderBase64File(String base64Code, String savePath) throws Exception {
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(savePath);
        out.write(buffer);
        out.close();
    }

    // 清空指定目录下的所有文件
    public static void clearAllFilesFromDir(File file) throws Exception {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] filelist = file.listFiles();
                for (int i = 0; i < filelist.length; i++) {
                    clearAllFilesFromDir(filelist[i]);
                }
            } else {
                file.deleteOnExit();
            }
        }
    }
}
