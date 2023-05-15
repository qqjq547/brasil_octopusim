package framework.telegram.ui.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import framework.telegram.support.BaseApp;

public class FileUtils {

    public static void deleteFiles(File root) {
        if (root != null && root.isDirectory() && root.exists()) { // 判断是否存在
            File files[] = root.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (!f.isDirectory() && f.exists()) { // 判断是否存在
                        try {
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public static void deleteQuietly(File f) {
        if (f != null && !f.isDirectory() && f.exists()) { // 判断是否存在
            try {
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveBitmap(Bitmap bitmap, File file) {
        if (bitmap != null) {
            OutputStream outputStream;
            try {
                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                bitmap.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkFile(File f) {
        return f != null && f.exists() && f.canRead() && (f.isDirectory() || f.isFile() && f.length() > 0L);
    }

    public static String getBitmapThumbFilePath( File file) {
        return BaseApp.app.getCacheDir().getAbsolutePath() + "/" + file.getName() + "___thumb";
    }

    public static File getEncryptTmpFile( File file) {
        return  new File(BaseApp.app.getExternalFilesDir(null).getAbsolutePath() + "/" + file.getName() + "___encrypt___" + System.currentTimeMillis());
    }

    public static File getEncryptFile( File file) {
        return  new File(BaseApp.app.getCacheDir().getAbsolutePath() + "/" + file.getName() + "___encrypt}");
    }

}