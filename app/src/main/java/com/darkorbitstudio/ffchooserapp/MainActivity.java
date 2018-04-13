package com.darkorbitstudio.ffchooserapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.darkorbitstudio.ffchooser.FFChooser;
import com.darkorbitstudio.ffchooser.FFChooser_PrimaryDialogFragment;
import com.darkorbitstudio.ffchooser.FFChooser_ProgressView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.ChangeListener;
import com.google.android.gms.tasks.Task;
import com.microsoft.onedrivesdk.picker.IPicker;
import com.microsoft.onedrivesdk.picker.IPickerResult;
import com.microsoft.onedrivesdk.picker.LinkType;
import com.microsoft.onedrivesdk.picker.Picker;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    IPicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Typeface robotoRegular = Typeface.create("sans-serif", Typeface.NORMAL);

        try {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                View view = toolbar.getChildAt(i);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    if (tv.getText().equals(toolbar.getTitle())) {
                        tv.setTypeface(robotoRegular);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        final RadioButton radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radioButton2);
        final CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        final CheckBox checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        final TextView textView = (TextView) findViewById(R.id.textView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
                //intent.setType("*/*");
                //Intent i = Intent.createChooser(intent, "File");
                //startActivityForResult(i, 2);

                //Log.e("Exc", "" + System.getenv("EXTERNAL_STORAGE"));
                //Log.e("Exc", "" + System.getenv("SECONDARY_STORAGE"));
                //Log.e("Exc", "" + System.getenv("USBOTG_STORAGE"));
                Log.e("Exc", "" + System.getenv());

                FFChooser ffChooser = new FFChooser(MainActivity.this, radioButton1.isChecked() ? FFChooser.Select_Type_File : FFChooser.Select_Type_Folder);
                ffChooser.setShowHidden(checkBox1.isChecked());
                ffChooser.setShowThumbnails(checkBox2.isChecked());
                ffChooser.setMultiSelect(checkBox3.isChecked());
                ffChooser.setOnSelectListener(new FFChooser.OnSelectListener() {
                    @Override
                    public void onSelect(int type, String path) {
                        switch (type) {
                            case FFChooser.Type_None:
                                textView.setText(textView.getText().equals("") ? "> Canceled" : textView.getText() + "\n\n" + "> Canceled");
                                Log.e("Exc", "Canceled");
                                break;
                            case FFChooser.Type_Local_Storage:
                                if (checkBox3.isChecked()) {
                                    String[] paths = path.split(",");
                                    textView.setText(textView.getText().equals("") ? "> " + path : textView.getText() + "\n\n> " + Arrays.toString(paths));
                                } else {
                                    textView.setText(textView.getText().equals("") ? "> " + path : textView.getText() + "\n\n> " + path);
                                }
                                Log.e("Exc", type + "\n" + path);
                                break;
                            case FFChooser.Type_Google_Drive_Storage:
                                /*DriveFile driveFile = null;
                                DriveResourceClient.openFile (driveFile, 1);
                                @SuppressLint("RestrictedApi") GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                Task<DriveContents> openFileTask = Drive.getDriveResourceClient(getApplicationContext(), googleSignInAccount).openFile(driveFile, DriveFile.MODE_READ_ONLY);*/
                                textView.setText(textView.getText().equals("") ? "> Google drive selected" : textView.getText() + "\n\n" + "> Google drive selected");
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("*/*");
                                //startActivityForResult(intent, 2);
                                Log.e("Exc", type + "\n" + path);
                                break;
                            case FFChooser.Type_One_Drive_Storage:
                                textView.setText(textView.getText().equals("") ? "> One drive selected" : textView.getText() + "\n\n" + "> One drive selected");
                                mPicker = Picker.createPicker("7809402b-9301-4cb5-ae32-dfaa14e0c824");
                                mPicker.startPicking((Activity) view.getContext(), LinkType.DownloadLink);
                                Log.e("Exc", type + "\n" + path);
                                break;
                        }
                    }
                });
                ffChooser.show();
            }
        });

        final FFChooser_ProgressView ffChooser_progressView = findViewById(R.id.progress);
        final Handler handler = new Handler();
        final int delay = 2000; //milliseconds
        handler.postDelayed(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ffChooser_progressView.setProgress(((new Random()).nextInt((100 - 0) + 1)));
                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                    .getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d("Exc", "HTTP response cache is unavailable.");
        }
    }

    public File DownloadFiles(String url, String name){
        File file = new File(getCacheDir() + "/" + name);
        try {
            URL u = new URL(url);
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(file);
            while ((length = dis.read(buffer))>0) {
                fos.write(buffer, 0, length);
            }
        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
        return file;
    }

    @SuppressLint("RestrictedApi")
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        //enableHttpResponseCache();
        final IPickerResult result = mPicker.getPickerResult(requestCode, resultCode, data);
        // Handle the case if nothing was picked
        if (result != null) {
            // Do something with the picked file
            for (File file : getCacheDir().listFiles()) {
                Log.e("Exc", "Files : " + file.getName());
            }
            Log.e("Exc", "Link to file '" + result.getName() + ": " + result.getLink());

            new Thread(new Runnable() {
                public void run() {
                    File file = DownloadFiles(result.getLink().toString(), result.getName());
                    Log.e("Exc", "New : " + file.getName());
                    Log.e("Exc", "New : " + file.length());
                }
            }).start();

            return;
        }

        // check if the request code is same as what is passed  here it is 2
        if(requestCode == 2 && data != null)
        {
            Log.e("Exc", "" + data.getDataString());
        }
    }

    /*@Override
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
    }*/
}
