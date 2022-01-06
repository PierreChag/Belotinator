package com.poulpinou.belotinator.core;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileHandler {

    public static final String FOLDER_NAME = "belotinator";
    public static final String PLAYERS_LIST_FILE_NAME = "players_list.json";
    private static String storage_dir;

    @Nullable
    public static String getDirectoryFile(){
        if(storage_dir == null){
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                storage_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FOLDER_NAME;
            } else {
                storage_dir = Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME;
            }
            dir = new File(storage_dir);
            // We need to check if this directory exists
            if(!dir.exists()) {
                // Make it, if it doesn't exit
                if(!dir.mkdirs()){
                    storage_dir = null;
                }
            }
        }
        return storage_dir;
    }

    @Nullable
    public static String getJSONStringFromFile(String fileName){
        if(getDirectoryFile() != null){
            try {
                File file = new File(getDirectoryFile(), fileName);
                FileInputStream stream = new FileInputStream(file);
                String fileToString = null;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    stream.close();
                    fileToString = Charset.defaultCharset().decode(bb).toString();
                } catch(Exception e){
                    e.printStackTrace();
                }
                return fileToString;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean saveJSONStringToFile(String fileName, String jsonInString){
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            //If it isn't mounted - we can't write into it.
            Log.e(TAG, "Impossible to save the file " + fileName + ". External storage isn't mounted.", null);
            return false;
        }
        if(getDirectoryFile() == null){
            Log.e(TAG, "Impossible to save the file " + fileName + ". The root to belotinator's folder couldn't be accessed.", null);
            return false;
        }
        File file = new File(getDirectoryFile(), fileName);
        try {
            file.createNewFile();
            FileOutputStream outputStream;
            outputStream = new FileOutputStream(file, false);
            outputStream.write(jsonInString.getBytes());
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "Impossible to save the file " + fileName + ".", null);
        return false;
    }
}
