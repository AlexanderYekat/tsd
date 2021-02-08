package ru.ttmf.mark.Helpers;

import android.content.Context;
import android.os.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Helpers {
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static String ToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String JsonBeautify(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Object obj = mapper.readValue(json, Object.class);
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    public static void writeToFile(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MarkScanPositions");
            if (!root.exists()) {
                root.mkdirs();
            }


            File gpxfile = new File(root, sFileName.replace(":"," - ").replace("/", "-"));
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLastScanFile() {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "MarkScanPositions");
            String[] files = root.list();

            File myFile = new File(root, files[files.length - 1]);
            myFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
