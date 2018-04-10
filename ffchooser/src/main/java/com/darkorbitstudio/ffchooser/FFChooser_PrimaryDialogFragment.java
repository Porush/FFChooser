package com.darkorbitstudio.ffchooser;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FFChooser_PrimaryDialogFragment extends DialogFragment {

    private File rootStorage;
    private File internalStorage;
    private File externalStorage;
    private File otgStorage;

    private Activity activity;
    private static OnSelectListener onSelectListener;
    private int selectType;
    private boolean multiSelect = false;
    private boolean showHidden = false;
    private boolean showThumbnails = false;

    public FFChooser_PrimaryDialogFragment() {

    }

    public static FFChooser_PrimaryDialogFragment newInstance(int selectType) {
        FFChooser_PrimaryDialogFragment ffChooserNew = new FFChooser_PrimaryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("selectType", selectType);
        ffChooserNew.setArguments(bundle);
        return ffChooserNew;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.dialog_primary_chooser, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

        activity = getActivity();
        selectType = getArguments().getInt("selectType", FFChooser.Select_Type_File);
        rootStorage = new File("/");
        internalStorage = Environment.getDataDirectory();
        externalStorage = new File("/sdcard");

        final RecyclerView recyclerView = inflaterView.findViewById(R.id.dialog_primary_chooser_recyclerView);
        final ArrayList<File> files = new ArrayList<>();
        for (File file : new File("/storage").listFiles()) {
            String name = file.getName();
            if (!name.equals("knox-emulated") && !name.equals("emulated") && !name.equals("self") && !name.equals("container"))
                files.add(file);
        }
        final FFChooser_Primary_ListAdapter listAdapter = new FFChooser_Primary_ListAdapter(files);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);

        recyclerView.addOnItemTouchListener(
                new FFChooser_RecyclerViewOnItemClickListener(activity.getApplicationContext(), new FFChooser_RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        showStorage(FFChooser.Type_Local_Storage, files.get(position));
                    }
                })
        );
        inflaterView.findViewById(R.id.dialog_primary_chooser_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        });

        if (System.getenv("USBOTG_STORAGE") != null && !System.getenv("USBOTG_STORAGE").equals("") && new File(System.getenv("USBOTG_STORAGE")).exists()) {
            otgStorage = new File(System.getenv("USBOTG_STORAGE"));
        }
        File[] storageDirectory = new File("/storage").listFiles();
        for (File file : storageDirectory) {
            String name = file.getName();
            Log.e("Exc", name + " : " + isDirectoryWritable(file));
            if (!name.equals("knox-emulated") && !name.equals("emulated") && !name.equals("self") && !name.equals("container")) {
                if (otgStorage == null && name.equals("UsbDriveA") || name.equals("USBstorage1") || name.equals("usbdisk") || name.equals("usbotg") || name.equals("UDiskA") || name.equals("usb-storage") || name.equals("usbcard") || name.equals("usb")) {
                    otgStorage = file;
                } else if (!name.equals(otgStorage == null ? "" : otgStorage.getName())) {
                    if (isDirectoryWritable(file)) {
                        //if (System.getenv("EXTERNAL_STORAGE") != null && System.getenv("EXTERNAL_STORAGE").equals(file.getPath())) {
                        externalStorage = file;
                    } else {
                        internalStorage = file;
                    }
                }
            }
            /*if (otgStorage == null)
                if (file.getName().equals("UsbDriveA") || file.getName().equals("USBstorage1") || file.getName().equals("usbdisk") || file.getName().equals("usbotg") || file.getName().equals("UDiskA") || file.getName().equals("usb-storage") || file.getName().equals("usbcard") || file.getName().equals("usb")) {
                    otgStorageSubtitle.setText(getSize(file));
                    frameLayout_otgStorage.setVisibility(View.VISIBLE);
                    otgStorage = file;
                }*/
        }
        /*if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externalStorageSubtitle.setText(getSize(externalStorage));
            frameLayout_externalStorage.setVisibility(View.VISIBLE);
        }*/
        if (System.getenv("EXTERNAL_STORAGE") != null && !System.getenv("EXTERNAL_STORAGE").equals("") && new File(System.getenv("EXTERNAL_STORAGE")).exists()) {
            externalStorage = new File(System.getenv("EXTERNAL_STORAGE"));
        }
        if (appInstalledOrNot("com.google.android.apps.docs")) {
        }
        if (appInstalledOrNot("com.microsoft.skydrive")) {
        }

        return inflaterView;
    }

    private void showStorage(int type, File rootFile) {
        FFChooser_SecondaryDialogFragment ffChooser_secondaryDialogFragment = FFChooser_SecondaryDialogFragment.newInstance(selectType, rootFile.getPath());
        ffChooser_secondaryDialogFragment.setFfChooser_primaryDialogFragment(this);
        ffChooser_secondaryDialogFragment.setShowThumbnails(showThumbnails);
        ffChooser_secondaryDialogFragment.setMultiSelect(multiSelect);
        ffChooser_secondaryDialogFragment.setShowHidden(showHidden);
        ffChooser_secondaryDialogFragment.setOnSelectListener(new FFChooser_PrimaryDialogFragment.OnSelectListener() {
            @Override
            public void onSelect(int type, String path) {
                onSelectListener.onSelect(type, path);
            }
        });
        ffChooser_secondaryDialogFragment.show(getFragmentManager(), "Tag");
    }

    private String getSize(File file) {
        long size = file.getTotalSpace();
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
        //decimalFormat.setMinimumIntegerDigits(3);
        if (size < 1024) {
            return size + "bytes";
        } else if (size < 1048576) {
            return decimalFormat.format((float) size / 1024) + "KiB";
        } else if (size < 1073741824) {
            return decimalFormat.format((float) size / 1048576) + "MiB";
        } else {
            return decimalFormat.format((float) size / 1073741824) + "GiB";
        }
    }

    private boolean isDirectoryReadble(File file) {
        if (file != null && file.exists()) {
            return file.listFiles() != null;
        } else {
            return false;
        }
    }

    private boolean isDirectoryWritable(File file) {
        if (file != null && file.exists()) {
            file = new File(file.getPath() + "/writeTest");
            if (file.mkdir()) {
                file.delete();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        };
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        FFChooser_PrimaryDialogFragment.onSelectListener = onSelectListener;
    }

    private OnSelectListener getDefaultOnSelectListener() {
        return new OnSelectListener() {
            @Override
            public void onSelect(int type, String path) {
                Log.e("Exc", "No OnSelectListener found.\nType : " + type + "\nPath : " + path);
            }
        };
    }

    public interface OnSelectListener {
        void onSelect(int type, String path);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
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
}
