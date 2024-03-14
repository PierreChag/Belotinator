package com.poulpinou.belotinator;

import static com.poulpinou.belotinator.core.Storage.CONNECTION_CALLBACKS;
import static com.poulpinou.belotinator.core.Storage.ON_CONNECTION_FAILED_LISTENER;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import com.poulpinou.belotinator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements ActionBarTitleSetter{

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize GoogleApiClient
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(CONNECTION_CALLBACKS)
                .addOnConnectionFailedListener(ON_CONNECTION_FAILED_LISTENER)
                .build();

        googleApiClient.connect();

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
}