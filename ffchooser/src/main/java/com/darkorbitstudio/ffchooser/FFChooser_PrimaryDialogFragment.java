package com.darkorbitstudio.ffchooser;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

        activity = getActivity();
        selectType = getArguments().getInt("selectType", FFChooser.Select_Type_File);
        rootStorage = new File("/");
        internalStorage = Environment.getDataDirectory();
        externalStorage = new File("/sdcard");

        ImageView close = inflaterView.findViewById(R.id.dialog_primary_chooser_close);
        FrameLayout frameLayout_internalStorage = inflaterView.findViewById(R.id.dialog_primary_chooser_internal_storage);
        TextView internalStorageSubtitle = inflaterView.findViewById(R.id.dialog_primary_chooser_internal_storage_subtitle);
        FrameLayout frameLayout_externalStorage = inflaterView.findViewById(R.id.dialog_primary_chooser_external_storage);
        TextView externalStorageSubtitle = inflaterView.findViewById(R.id.dialog_primary_chooser_external_storage_subtitle);
        FrameLayout frameLayout_otgStorage = inflaterView.findViewById(R.id.dialog_primary_chooser_otg_storage);
        TextView otgStorageSubtitle = inflaterView.findViewById(R.id.dialog_primary_chooser_otg_storage_subtitle);
        FrameLayout frameLayout_googleDriveStorage = inflaterView.findViewById(R.id.dialog_primary_chooser_google_drive);
        TextView googleDriveStorageSubtitle = inflaterView.findViewById(R.id.dialog_primary_chooser_google_drive_subtitle);
        FrameLayout frameLayout_oneDriveStorage = inflaterView.findViewById(R.id.dialog_primary_chooser_one_drive);
        TextView oneDriveStorageSubtitle = inflaterView.findViewById(R.id.dialog_primary_chooser_one_drive_subtitle);

        frameLayout_internalStorage.setVisibility(View.GONE);
        frameLayout_externalStorage.setVisibility(View.GONE);
        frameLayout_otgStorage.setVisibility(View.GONE);
        frameLayout_googleDriveStorage.setVisibility(View.GONE);
        frameLayout_oneDriveStorage.setVisibility(View.GONE);

        if (System.getenv("USBOTG_STORAGE") != null && !System.getenv("USBOTG_STORAGE").equals("") && new File(System.getenv("USBOTG_STORAGE")).exists()) {
            frameLayout_otgStorage.setVisibility(View.VISIBLE);
            otgStorage = new File(System.getenv("USBOTG_STORAGE"));
            otgStorageSubtitle.setText(getSize(otgStorage));
        }
        File[] storageDirectory = new File("/storage").listFiles();
        for (File file : storageDirectory) {
            if (otgStorage == null && file.getName().equals("UsbDriveA") || file.getName().equals("USBstorage1") || file.getName().equals("usbdisk") || file.getName().equals("usbotg") || file.getName().equals("UDiskA") || file.getName().equals("usb-storage") || file.getName().equals("usbcard") || file.getName().equals("usb")) {
                frameLayout_otgStorage.setVisibility(View.VISIBLE);
                otgStorage = file;
                otgStorageSubtitle.setText(getSize(otgStorage));
            } else if (!file.getName().equals(otgStorage == null ? "" : otgStorage.getName())) {
                if (isDirectoryWritable(file)) {
                    //if (System.getenv("EXTERNAL_STORAGE") != null && System.getenv("EXTERNAL_STORAGE").equals(file.getPath())) {
                    externalStorage = file;
                    externalStorageSubtitle.setText(getSize(externalStorage));
                    frameLayout_externalStorage.setVisibility(View.VISIBLE);
                } else {
                    internalStorage = file;
                    internalStorageSubtitle.setText(getSize(internalStorage));
                    frameLayout_internalStorage.setVisibility(View.VISIBLE);
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
        /*if (System.getenv("EXTERNAL_STORAGE") != null && !System.getenv("EXTERNAL_STORAGE").equals("") && new File(System.getenv("EXTERNAL_STORAGE")).exists()) {
            externalStorageSubtitle.setText(getSize(externalStorage));
            frameLayout_externalStorage.setVisibility(View.VISIBLE);
        }*/
        if (appInstalledOrNot("com.google.android.apps.docs")) {
            googleDriveStorageSubtitle.setText("Installed");
            frameLayout_googleDriveStorage.setVisibility(View.VISIBLE);
        }
        if (appInstalledOrNot("com.microsoft.skydrive")) {
            oneDriveStorageSubtitle.setText("Installed");
            frameLayout_oneDriveStorage.setVisibility(View.VISIBLE);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        });
        frameLayout_internalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Exc", "" + Environment.getDataDirectory().getPath());
                showStorage(FFChooser.Type_Local_Storage, internalStorage);
            }
        });
        frameLayout_externalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStorage(FFChooser.Type_Local_Storage, externalStorage);
            }
        });
        frameLayout_otgStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStorage(FFChooser.Type_Local_Storage, otgStorage);
            }
        });
        frameLayout_googleDriveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(FFChooser.Type_Google_Drive_Storage, null);
                dismiss();
            }
        });
        frameLayout_oneDriveStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectListener.onSelect(FFChooser.Type_One_Drive_Storage, null);
                dismiss();
            }
        });

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
