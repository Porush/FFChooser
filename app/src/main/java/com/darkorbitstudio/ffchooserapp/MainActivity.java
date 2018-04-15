package com.darkorbitstudio.ffchooserapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.darkorbitstudio.ffchooser.FFChooser;

import java.util.Arrays;

/**
 * Created by Porush Manjhi on 12-05-2017.
 */

public class MainActivity extends AppCompatActivity {

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
        final CheckBox checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        final CheckBox checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        final TextView textView = (TextView) findViewById(R.id.textView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //Log.e("Exc", "" + System.getenv("EXTERNAL_STORAGE"));
                //Log.e("Exc", "" + System.getenv("SECONDARY_STORAGE"));
                //Log.e("Exc", "" + System.getenv("USBOTG_STORAGE"));
                Log.e("Exc", "" + System.getenv());

                FFChooser ffChooser = new FFChooser(MainActivity.this, radioButton1.isChecked() ? FFChooser.Select_Type_File : FFChooser.Select_Type_Folder);
                ffChooser.setShowHidden(checkBox1.isChecked());
                ffChooser.setShowThumbnails(checkBox2.isChecked());
                ffChooser.setMultiSelect(checkBox3.isChecked());
                ffChooser.setShowGoogleDrive(checkBox4.isChecked());
                ffChooser.setShowOneDrive(checkBox5.isChecked());
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
                                textView.setText(textView.getText().equals("") ? "> Google drive selected" : textView.getText() + "\n\n" + "> Google drive selected");
                                Log.e("Exc", type + "\n" + path);
                                break;
                            case FFChooser.Type_One_Drive_Storage:
                                textView.setText(textView.getText().equals("") ? "> One drive selected" : textView.getText() + "\n\n" + "> One drive selected");
                                Log.e("Exc", type + "\n" + path);
                                break;
                        }
                    }
                });
                ffChooser.show();
            }
        });

        /*final FFChooser_ProgressView ffChooser_progressView = findViewById(R.id.progress);
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
        }, delay);*/
    }
}
