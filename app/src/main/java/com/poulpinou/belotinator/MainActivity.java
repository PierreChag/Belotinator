package com.poulpinou.belotinator;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.poulpinou.belotinator.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ActionBarTitleSetter{

    private AppBarConfiguration appBarConfiguration;
    public static String mainDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainDirectory = this.getMainDirectoryFile();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        this.appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, this.appBarConfiguration);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the file picker (if used)
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, this.appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void setActionBarTitle(String title) {
        if(this.getSupportActionBar() != null){
            this.getSupportActionBar().setTitle(title);
        }
    }

    /**
     * @return the String address of the directory where files are saved.
     */
    @Nullable
    private String getMainDirectoryFile(){
        File directory = this.getFilesDir();
        if (directory != null) {
            return directory.getAbsolutePath();
        } else {
            Log.e("FileUtils", "Failed to get the internal files directory.");
            return null;
        }
    }
}