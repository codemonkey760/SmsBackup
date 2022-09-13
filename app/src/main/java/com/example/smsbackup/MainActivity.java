package com.example.smsbackup;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] projection = new String[]{
                Telephony.TextBasedSmsColumns.ADDRESS,
                Telephony.TextBasedSmsColumns.CREATOR,
                Telephony.TextBasedSmsColumns.DATE_SENT,
                Telephony.TextBasedSmsColumns.BODY,
                Telephony.TextBasedSmsColumns.SUBJECT
        };

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_SMS},
                1
        );

        ContentResolver cr = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    Telephony.Sms.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
        } catch (SecurityException securityException) {
            Log.e("MY_APP", securityException.toString());
        }

        if (cursor != null) {
            int addressCol = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS);
            int creatorCol = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.CREATOR);
            int dateSentCol = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.DATE_SENT);
            int bodyCol = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.BODY);
            int subjectCol = cursor.getColumnIndex(Telephony.TextBasedSmsColumns.SUBJECT);

            int maxMsgs = 10;
            while (cursor.moveToNext() && maxMsgs-- > 0) {
                String address = cursor.getString(addressCol);
                String creator = cursor.getString(creatorCol);
                String dateSent = cursor.getString(dateSentCol);
                String body = cursor.getString(bodyCol);
                String subject = cursor.getString(subjectCol);

                StringBuffer sb = new StringBuffer();
                sb.append(address);
                sb.append(", ");
                sb.append(creator);
                sb.append(", ");
                sb.append(dateSent);
                sb.append(", ");
                sb.append(body);
                sb.append(", ");
                sb.append(subject);

                Log.d("MY_APP", sb.toString());
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