package com.poulpinou.belotinator.core;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.io.IOException;

public class Storage {

    public static final String PLAYERS_LIST_FILE_NAME = "players_list.json";

    /**
     * Creates a file in the application data folder.
     *
     * @return Created file's Id.
     */
    public static String uploadAppData() throws IOException {
    /*Load pre-authorized user credentials from the environment.
    TODO(developer) - See https://developers.google.com/identity for
    guides on implementing OAuth2 for your application.*/
        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.getApplicationDefault()
                    .createScoped(Arrays.asList(DriveScopes.DRIVE_APPDATA));
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
                credentials);

        // Build a new authorized API client service.
        Drive service = new Drive.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName("Drive samples")
                .build();
        try {
            // File's metadata.
            File fileMetadata = new File();
            fileMetadata.setName("config.json");
            fileMetadata.setParents(Collections.singletonList("appDataFolder"));
            java.io.File filePath = new java.io.File("files/config.json");
            FileContent mediaContent = new FileContent("application/json", filePath);
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();
            System.out.println("File ID: " + file.getId());
            return file.getId();
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create file: " + e.getDetails());
            throw e;
        }
    }

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
        if(directory != null){
            try {
                File file = new File(directory, fileName);
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

    /**
     * Deletes the file with the given name in the directory.
     * @param directory where the file is located.
     * @param fileName the name of the file to be deleted.
     */
    public static void deleteFile(@Nullable String directory, String fileName){
        if(directory != null){
            try {
                File file = new File(directory, fileName);
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param directory is the address where the file must be saved. Use getMainDirectoryFile() to get the address of the main folder.
     * @param fileName is the full name of the file to be saved (including the type ".json").
     * @param jsonInString is the JSON content to be saved, converted in String format.
     * @return True if the file could be saved, false otherwise.
     */
    public static boolean saveJSONStringToFile(String directory, String fileName, String jsonInString){
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            //If it isn't mounted - we can't write into it.
            Log.e(TAG, "Impossible to save the file " + fileName + ". External storage isn't mounted.", null);
            return false;
        }
        if(directory == null){
            Log.e(TAG, "Impossible to save the file " + fileName + ". The root to belotinator's folder couldn't be accessed.", null);
            return false;
        }
        File file = new File(directory, fileName);
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

    /**
     * @return the String address of the directory where files are saved.
     */
    @Nullable
    public static String getMainDirectoryFile(){
        if(mainStorageDirectory == null){
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mainStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + MAIN_FOLDER_NAME;
            } else {
                mainStorageDirectory = Environment.getExternalStorageDirectory() + "/" + MAIN_FOLDER_NAME;
            }
            dir = new File(mainStorageDirectory);
            // We need to check if this directory exists
            if(!dir.exists()) {
                // Make it, if it doesn't exit
                if(!dir.mkdirs()){
                    mainStorageDirectory = null;
                }
            }
        }
        return mainStorageDirectory;
    }

    /**
     * @return the String address of the directory where the Belote games are saved.
     */
    @Nullable
    public static String getBelotesDirectory(){
        if(belotesDirectory == null){
            File dir;
            belotesDirectory = getMainDirectoryFile() + "/" + BELOTES_FOLDER_NAME;
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
