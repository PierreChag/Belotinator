package com.poulpinou.belotinator.core;

import android.util.Log;

import androidx.annotation.Nullable;

import com.poulpinou.belotinator.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.IOException;

public class Storage {

    public static final String PLAYERS_LIST_FILE_NAME = "players_list.json";
    public static final String BELOTES_FOLDER_NAME = "belotes";
    private static String belotesDirectory;

    /**
     * @param directory is the address where the files are located. Use getMainDirectoryFile() to get the address of the main folder.
     * @return a list containing the content of the folder in String format.
     */
    public static ArrayList<String> getAllJSONStringFromDirectory(@Nullable String directory){
        ArrayList<String> list = new ArrayList<>();
        if(directory != null){
            File folder = new File(directory);
            File[] files = folder.listFiles();
            if(files != null){
                for(File file : files){
                    list.add(getJSONStringFromFile(directory, file.getName()));
                }
            }
        }
        return list;
    }

    /**
     * @param directory is the address where the file is located. Use getMainDirectoryFile() to get the address of the main folder.
     * @param fileName is the full name of the file to be loaded (including the type ".json").
     * @return the content of the file in String format.
     */
    @Nullable
    public static String getJSONStringFromFile(@Nullable String directory, String fileName){
        if (directory != null) {
            File file = new File(directory, fileName);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                Log.e("FileUtils", "Error reading file: " + fileName, e);
            }
        }
        return null;
    }

    /**
     * Deletes the file with the given name in the directory.
     * @param directory where the file is located.
     * @param fileName the name of the file to be deleted.
     */
    public static void deleteFile(@Nullable String directory, String fileName){
        if (fileName != null && !fileName.isEmpty() && directory != null) {
            File file = new File(directory, fileName);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    Log.e("FileUtils", "Failed to delete file: " + fileName);
                }
            } else {
                Log.w("FileUtils", "File not found: " + fileName);
            }
        } else {
            Log.e("FileUtils", "File name is null or empty. Cannot delete file.");
        }
    }

    /**
     * @param directory is the address where the file must be saved. Use getMainDirectoryFile() to get the address of the main folder.
     * @param fileName is the full name of the file to be saved (including the type ".json").
     * @param jsonInString is the JSON content to be saved, converted in String format.
     */
    public static void saveJSONStringToFile(String directory, String fileName, String jsonInString){
        if (directory != null) {
            File file = new File(directory, fileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(jsonInString);
            } catch (IOException e) {
                Log.e("FileUtils", "Error saving file: " + fileName, e);
            }
        }
    }

    /**
     * @return the String address of the directory where the Belote games are saved.
     */
    @Nullable
    public static String getBelotesDirectory(){
        if(belotesDirectory == null){
            File dir;
            belotesDirectory = MainActivity.mainDirectory + "/" + BELOTES_FOLDER_NAME;
            dir = new File(belotesDirectory);
            // We need to check if this directory exists
            if(!dir.exists()) {
                // Make it, if it doesn't exit
                if(!dir.mkdirs()){
                    belotesDirectory = null;
                }
            }
        }
        return belotesDirectory;
    }
}
