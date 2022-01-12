package com.digquant.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {


    public static boolean CreateFile(File dir, String filePath) {
        File file = new File(dir, filePath);
        return CreateFile(file);
    }

    public static boolean CreateFile(String filePath) {
        File file = new File(filePath);
        return CreateFile(file);
    }

    public static boolean CreateFile(File file) {
        try {
            if (file.exists()) {
                return true;
            }
            String dirPath = GetFileDir(file.getAbsolutePath());
            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                boolean bSucceed = dir.mkdirs();
                if (!bSucceed) {
                    return false;
                }
            }
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean IsFileExist(File dir, String filePath) {
        File file = new File(dir, filePath);
        return file.exists();
    }

    public static boolean IsFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static String ReadFileAsString(String filePath) {
        File file = new File(filePath);
        return ReadFileAsString(file);
    }

    public static String ReadFileAsString(File file) {
        if (!file.exists()) {
            return "";
        }
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            long fileSize = file.length();

            if (fileSize > Integer.MAX_VALUE) {
                throw new RuntimeException("文件过大: 最多只能读取4G文本文件!");
            }
            byte[] buffer = new byte[(int) fileSize];

            int readSize = fileInputStream.read(buffer);

            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static boolean WriteStringToFile(File dir, String filePath, String content) {

        boolean bSucceed = CreateFile(dir, filePath);
        if (!bSucceed) {
            return false;
        }

        File file = new File(dir, filePath);
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean WriteByteToNewFile(String filePath, byte[] bytes) {
        boolean bSucceed = CreateFile(filePath);
        if (!bSucceed) {
            return false;
        }
        File file = new File(filePath);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String GetFileDir(String filePath) {
        String[] pathArr = filePath.split("[/\\\\]");
        if (pathArr.length == 1) {
            return "." + File.separator;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int len = pathArr.length - 1;
        for (int i = 0; i < len; ++i) {
            stringBuilder.append(pathArr[i]);
            stringBuilder.append(File.separator);
        }
        stringBuilder.setLength(stringBuilder.length() - File.separator.length());
        return stringBuilder.toString();

    }

    public static boolean DeleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }

        return false;
    }


    public static boolean DeleteFile(File dir, String filePath) {
        File file = new File(dir, filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }
}
