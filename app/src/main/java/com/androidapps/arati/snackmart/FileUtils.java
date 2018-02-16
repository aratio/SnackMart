package com.androidapps.arati.snackmart;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by aogale on 2/15/18.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String readTextFile(File file) {
        try {
            return readTextFile(new FileReader(file));
        } catch(java.io.FileNotFoundException e) {
            Log.d(TAG, "Unable to open file. Error: " + e.getMessage());
        }
        return null;
    }

    public static String readTextFile(InputStream stream) {
        return readTextFile(new InputStreamReader(stream));
    }

    public static String readTextFile(Reader reader) {

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            char buffer[] = new char[256];
            int len;
            do {

                len = bufferedReader.read(buffer, 0, buffer.length);
                if(len != -1) stringBuilder.append(buffer, 0, len);

            } while(len != -1);

            return stringBuilder.toString();
        } catch(java.io.IOException e) {
            Log.d(TAG, "Error reading from file . Error: " + e.getMessage());
        }
        return null;
    }

    public static boolean writeTextFile(File file, String string) {

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(string);
            FileOutputStream outputStream = new FileOutputStream(file);
            return true;
        } catch(java.io.IOException e) {
            Log.d(TAG, "Error writing to file . Error: " + e.getMessage());
            return false;
        }

    }
}
