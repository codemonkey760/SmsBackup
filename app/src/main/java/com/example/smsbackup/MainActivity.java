package com.example.smsbackup;

import android.Manifest;
import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smsbackup.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        Manifest.permission.READ_SMS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                1
        );

        ContentResolver cr = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    Telephony.Sms.CONTENT_URI,
                    new String[]{"*"},
                    null,
                    null,
                    null
            );
        } catch (SecurityException securityException) {
            Log.e("MY_APP", securityException.toString());
        }

        if (cursor != null) {
            File smsFile = null;
            String path =
                    Environment.getExternalStorageDirectory() +
                            File.separator +
                            "Download" +
                            File.separator +
                            "sms_backup.txt";
            Log.d("MY_APP", "path: " + path);
            smsFile = new File(path);

            try {
                if (!smsFile.exists()) {
                    Log.d("MY_APP", "sms_backup.txt does not exist, trying to create");
                    smsFile.createNewFile();
                } else {
                    Log.d("MY_APP", "sms_backup.txt does exist, skipping creation");
                }

                FileWriter fw = new FileWriter(smsFile, false);

                String[] columnNames = cursor.getColumnNames();
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < columnNames.length; ++i) {
                    header.append(columnNames[i]);
                    if (i < columnNames.length - 1) {
                        header.append(", ");
                    }
                }
                header.append("\n");
                fw.write(header.toString());

                int numTexts = cursor.getCount();

                int curMsg = 0;
                while (cursor.moveToNext()) {
                    int numCols = cursor.getColumnCount();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < numCols; ++i) {
                        sb.append(cursor.getString(i));
                        if (i < numCols-1) {
                            sb.append(", ");
                        }
                    }
                    sb.append("\n");

                    fw.write(sb.toString());
                    Log.d("MY_APP", curMsg++ + "/" + numTexts);
                }

                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}