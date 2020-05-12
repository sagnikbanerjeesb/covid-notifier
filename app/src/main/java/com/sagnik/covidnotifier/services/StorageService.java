package com.sagnik.covidnotifier.services;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageService {
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    public String readFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append('\n');
                    line = reader.readLine();
                }
                return stringBuilder.toString();
            } catch (IOException e) {}
        } catch (Exception e) {}
        finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {}
            }
        }
        return null;
    }

    public void writeToFile(Context context, String fileName, String content) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while saving data to file", e);
        }
    }
}
