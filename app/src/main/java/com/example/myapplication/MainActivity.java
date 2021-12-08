package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button download;
    TextView downloadStatus;
    ImageView instaImg;
    String URL;

    LinearLayout downloadPage;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        download = findViewById(R.id.download);
        downloadStatus = findViewById(R.id.downloadStatus);
        instaImg = findViewById(R.id.instaImg);
        downloadPage = findViewById(R.id.downloadPage);

        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //on intent
        if (savedInstanceState == null && Intent.ACTION_SEND.equals(getIntent().getAction())
                && getIntent().getType() != null && "text/plain".equals(getIntent().getType()))
        {
            String URL = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            editText.setText(URL);
        }


        //on click
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    if (!isInternetAvailable()) {
                        showInternetDialog();
                    } else {

                    URL = editText.getText().toString().trim();
                    if (editText.equals("NULL")) {

                        Toast.makeText(MainActivity.this, "Please Enter URL", Toast.LENGTH_SHORT).show();
                    } else {
                        String result2 = StringUtils.substringBefore(URL, "/?");
                        URL = result2 + "/?__a=1";
                        InstaVideo.downloadVideo(MainActivity.this, URL, instaImg, downloadStatus, true, dialog);

                        registerReceiver(onComplete,
                                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
                } else {
                    Toast.makeText(MainActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Download Complete", Toast.LENGTH_LONG).show();
            downloadStatus.setText("Download Complete");
            if (dialog.isShowing()){
                dialog.dismiss();
            }

            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Download Complete");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogg, int which) {
                    dialogg.dismiss();
                }
            });
            builder.create().show();
        }
    };

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet !")
                .setMessage("this app need internet for downloading.")
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}