package com.golike.customviews.utilities;

/**
 * Created by admin on 2017/8/10.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    private static String TAG = "FileUtils";

    public FileUtils() {
    }

    public static InputStream getFileInputStream(String path) {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(new File(path));
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

        return fileInputStream;
    }

    public static byte[] getByteFromUri(Uri uri) {
        InputStream input = getFileInputStream(uri.getPath());

        Object bytes;
        try {
            int e = 0;

            while(true) {
                if(e == 0) {
                    e = input.available();
                    if(e != 0) {
                        continue;
                    }
                }

                byte[] bytes1 = new byte[e];
                input.read(bytes1);
                byte[] e1 = bytes1;
                return e1;
            }
        } catch (Exception var14) {
            bytes = null;
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch (IOException var13) {
                    ;
                }
            }

        }

        return (byte[])bytes;
    }

    public static void writeByte(Uri uri, byte[] data) {
        File fileFolder = new File(uri.getPath().substring(0, uri.getPath().lastIndexOf("/")));
        fileFolder.mkdirs();
        File file = new File(uri.getPath());

        try {
            BufferedOutputStream e = new BufferedOutputStream(new FileOutputStream(file));
            e.write(data);
            e.close();
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }

    public static File convertBitmap2File(Bitmap bm, String dir, String name) {
        File file = new File(dir);
        if(!file.exists()) {
            Log.e(TAG, "convertBitmap2File: dir does not exist! -" + file.getAbsolutePath());
            file.mkdirs();
        }

        file = new File(file.getPath() + File.separator + name);

        try {
            BufferedOutputStream e = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, e);
            e.flush();
            e.close();
        } catch (IOException var5) {
            var5.printStackTrace();
            Log.e(TAG, "convertBitmap2File: Exception!");
        }

        return file;
    }

    public static File copyFile(File src, String path, String name) {
        File dest = null;
        if(!src.exists()) {
            Log.e(TAG, "copyFile: src file does not exist! -" + src.getAbsolutePath());
            return dest;
        } else {
            dest = new File(path);
            if(!dest.exists()) {
                Log.d(TAG, "copyFile: dir does not exist!");
                dest.mkdirs();
            }

            dest = new File(path + name);

            try {
                FileInputStream e = new FileInputStream(src);
                FileOutputStream fos = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];

                int length;
                while((length = e.read(buffer)) != -1) {
                    fos.write(buffer, 0, length);
                }

                fos.flush();
                fos.close();
                e.close();
                return dest;
            } catch (IOException var8) {
                var8.printStackTrace();
                Log.e(TAG, "copyFile: Exception!");
                return dest;
            }
        }
    }

    public static byte[] file2byte(File file) {
        if(!file.exists()) {
            Log.e(TAG, "file2byte: src file does not exist! -" + file.getAbsolutePath());
            return null;
        } else {
            byte[] buffer = null;

            try {
                FileInputStream e1 = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];

                int n;
                while((n = e1.read(b)) != -1) {
                    bos.write(b, 0, n);
                }

                e1.close();
                bos.close();
                buffer = bos.toByteArray();
            } catch (Exception var6) {
                var6.printStackTrace();
                Log.e(TAG, "file2byte: Exception!");
            }

            return buffer;
        }
    }

    public static File byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;

        try {
            File e = new File(filePath);
            if(!e.exists()) {
                Log.d(TAG, "byte2File: dir does not exist!");
                e.mkdirs();
            }

            file = new File(e.getPath() + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception var19) {
            var19.printStackTrace();
            Log.e(TAG, "byte2File: Exception!");
        } finally {
            if(bos != null) {
                try {
                    bos.close();
                } catch (IOException var18) {
                    var18.printStackTrace();
                }
            }

            if(fos != null) {
                try {
                    fos.close();
                } catch (IOException var17) {
                    var17.printStackTrace();
                }
            }

        }

        return file;
    }

    public static String getCachePath(Context context) {
        return getCachePath(context, "\'");
    }

    public static String getCachePath(Context context, @NonNull String dir) {
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        File cacheDir = context.getExternalCacheDir();
        if(!sdCardExist || cacheDir == null || !cacheDir.exists() || !cacheDir.mkdirs()) {
            cacheDir = context.getCacheDir();
        }

        File tarDir = new File(cacheDir.getPath() + File.separator + dir);
        if(!tarDir.exists()) {
            boolean result = tarDir.mkdir();
            Log.w(TAG, "getCachePath = " + tarDir.getPath() + ", result = " + result);
            if(!result) {
                tarDir = new File("/sdcard/cache/" + dir);
                if(!tarDir.exists()) {
                    result = tarDir.mkdirs();
                }

                Log.e(TAG, "change path = " + tarDir.getPath() + ", result = " + result);
            }
        }

        return tarDir.getPath();
    }

    public static String getMediaDownloadDir(Context context) {
        boolean sdCardExist = Environment.getExternalStorageState().equals("mounted");
        String path = "/sdcard";
        if(sdCardExist) {
            File e = Environment.getExternalStorageDirectory();
            path = e.getPath();
        }

        try {
            Resources e1 = context.getResources();
            String filePath = e1.getString(e1.getIdentifier("rc_media_message_default_save_path", "string", context.getPackageName()));
            Log.i(TAG, "getMediaDownloadDir: filePath=" + filePath);
            path = path + filePath;
            File file = new File(path);
            if(!file.exists() && !file.mkdirs()) {
                path = "/sdcard";
            }
        } catch (Resources.NotFoundException var6) {
            var6.printStackTrace();
            path = "/sdcard";
        }

        return path;
    }
}
