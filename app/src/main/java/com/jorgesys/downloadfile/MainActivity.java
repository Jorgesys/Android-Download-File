package com.jorgesys.downloadfile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        (findViewById(R.id.myButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //* Define your own source and target content.
                String urlSource = "https://bit.ly/2IEpVwe";
                new DownloadFile(getApplicationContext()).execute(urlSource, Environment.getExternalStorageDirectory() + "/myTablature.pdf");

            }
        });


    }


    public class DownloadFile extends AsyncTask<String, Void, Boolean> {

        private Context mContext;

        public DownloadFile (Context context){
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                //Check permissions Android 6.0+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    checkExternalStoragePermission();
                }

                //Download url
                String url  = strings[0];
                //Target Path
                String outputPath  = strings[1];
                Log.i(TAG, "* Url source: " + url);
                Log.i(TAG, "* output Path: " + outputPath);

                File outputFile = new File(outputPath);

                HttpURLConnection conn = null;
                URL u = new URL(url);
                conn = (HttpURLConnection)  u.openConnection();
                int contentLength = conn.getContentLength();

                DataInputStream stream = new DataInputStream(u.openStream());

                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch(FileNotFoundException e) {
                Log.e(TAG, "* FileNotFoundException: " + e.getMessage());
                showSnackBar("Error downloading file, FileNotFoundException!");
                return false;
            } catch (IOException e) {
                Log.e(TAG, "* IOException: " + e.getMessage());
                showSnackBar("Error downloading file, IOException!");
                return false;
            }
            showSnackBar("Download succesfully!");
            return true;
        }

        private void checkExternalStoragePermission() {
            int permissionCheck = ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission required, requesting permissions!.");
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
            } else {
                Log.i(TAG, "You already have permissions set!");
            }
        }

    }

    private void showSnackBar(String msg){
        Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG).show();
    }



}
