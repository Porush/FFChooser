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

        final RecyclerView recyclerView = inflaterView.findViewById(R.id.dialog_primary_chooser_recyclerView);
        final ArrayList<File> files = new ArrayList<>();
        for (File file : new File("/storage").listFiles()) {
            String name = file.getName();
            if (!name.equals("knox-emulated") && !name.equals("emulated") && !name.equals("self") && !name.equals("container"))
                files.add(file);
        }
        if (selectType == FFChooser.Select_Type_File && appInstalledOrNot("com.google.android.apps.docs")) {
            files.add(new File("com.google.android.apps.docs"));
        }
        if (selectType == FFChooser.Select_Type_File && appInstalledOrNot("com.microsoft.skydrive")) {
            files.add(new File("com.microsoft.skydrive"));
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
                        File file = files.get(position);
                        if (file.getPath().equals("com.google.android.apps.docs")) {
                            onSelectListener.onSelect(FFChooser.Type_Google_Drive_Storage, null);
                            dismiss();
                        } else if (file.getPath().equals("com.microsoft.skydrive")) {
                            onSelectListener.onSelect(FFChooser.Type_One_Drive_Storage, null);
                            dismiss();
                        } else {
                            showStorage(FFChooser.Type_Local_Storage, file);
                        }
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

        //System.getenv("EXTERNAL_STORAGE")
        //System.getenv("USBOTG_STORAGE")

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
