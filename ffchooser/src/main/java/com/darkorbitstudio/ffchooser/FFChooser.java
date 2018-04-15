package com.darkorbitstudio.ffchooser;

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.MimeTypeFilter;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Porush Manjhi on 22-03-2018.
 */

public class FFChooser {

    public final static int Select_Type_File = 0;
    public final static int Select_Type_Folder = 1;

    public final static int Type_None = 0;
    public final static int Type_Local_Storage = 1;
    public final static int Type_Google_Drive_Storage = 2;
    public final static int Type_One_Drive_Storage = 3;

    private Activity activity;
    private static OnSelectListener onSelectListener;
    private int selectType;
    private boolean multiSelect = false;
    private boolean showHidden = false;
    private boolean showThumbnails = false;
    private boolean showGoogleDrive = false;
    private boolean showOneDrive = false;

    public FFChooser(Activity activity, int selectType) {
        this.activity = activity;
        this.selectType = selectType;
    }

    public void show() {

        if (onSelectListener == null)
            onSelectListener = getDefaultOnSelectListener();

        FFChooser_PrimaryDialogFragment ffChooser_primaryDialogFragment = FFChooser_PrimaryDialogFragment.newInstance(selectType);
        ffChooser_primaryDialogFragment.setShowThumbnails(showThumbnails);
        ffChooser_primaryDialogFragment.setMultiSelect(multiSelect);
        ffChooser_primaryDialogFragment.setShowHidden(showHidden);
        ffChooser_primaryDialogFragment.setShowGoogleDrive(showGoogleDrive);
        ffChooser_primaryDialogFragment.setShowOneDrive(showOneDrive);
        ffChooser_primaryDialogFragment.setOnSelectListener(onSelectListener);
        ffChooser_primaryDialogFragment.show(activity.getFragmentManager(), "FFChooser");

    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        FFChooser.onSelectListener = onSelectListener;
    }

    private OnSelectListener getDefaultOnSelectListener() {
        return new OnSelectListener() {
            @Override
            public void onSelect(int type, String path) {
                Log.e("FFChooser", "No OnSelectListener found.\nType : " + type + "\nPath : " + path);
            }
        };
    }

    public interface OnSelectListener {
        void onSelect(int type, String path);
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
    }

    public void setShowThumbnails(boolean showThumbnails) {
        this.showThumbnails = showThumbnails;
    }

    public void setShowGoogleDrive(boolean showGoogleDrive) {
        this.showGoogleDrive = showGoogleDrive;
    }

    public void setShowOneDrive(boolean showOneDrive) {
        this.showOneDrive = showOneDrive;
    }

}
