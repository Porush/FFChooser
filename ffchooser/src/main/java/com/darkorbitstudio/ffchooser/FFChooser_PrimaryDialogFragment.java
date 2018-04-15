package com.darkorbitstudio.ffchooser;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;

import com.darkorbitstudio.ffchooser.FFChooser.OnSelectListener;

/**
 * Created by Porush Manjhi on 12-05-2017.
 */

public class FFChooser_PrimaryDialogFragment extends DialogFragment {

    private Activity activity;
    private OnSelectListener onSelectListener;
    private int selectType;
    private boolean multiSelect = false;
    private boolean showHidden = false;
    private boolean showThumbnails = false;
    private boolean showGoogleDrive = false;
    private boolean showOneDrive = false;

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

        final RecyclerView recyclerView = inflaterView.findViewById(R.id.dialog_primary_chooser_recyclerView);
        final ArrayList<File> files = new ArrayList<>();
        for (File file : new File("/storage").listFiles()) {
            String name = file.getName();
            if (!name.equals("knox-emulated") && !name.equals("emulated") && !name.equals("self") && !name.equals("container"))
                files.add(file);
        }
        if (showGoogleDrive && selectType == FFChooser.Select_Type_File && appInstalledOrNot("com.google.android.apps.docs")) {
            files.add(new File("com.google.android.apps.docs"));
        }
        if (showOneDrive && selectType == FFChooser.Select_Type_File && appInstalledOrNot("com.microsoft.skydrive")) {
            files.add(new File("com.microsoft.skydrive"));
        }

        final FFChooser_Primary_ListAdapter listAdapter = new FFChooser_Primary_ListAdapter(files, new FFChooser_OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                File file = files.get(position);
                if (file.getPath().equals("com.google.android.apps.docs")) {
                    if (onSelectListener != null)
                        onSelectListener.onSelect(FFChooser.Type_Google_Drive_Storage, null);
                    dismiss();
                } else if (file.getPath().equals("com.microsoft.skydrive")) {
                    if (onSelectListener != null)
                        onSelectListener.onSelect(FFChooser.Type_One_Drive_Storage, null);
                    dismiss();
                } else {
                    showStorage(file);
                }
            }
        });
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);

        inflaterView.findViewById(R.id.dialog_primary_chooser_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectListener != null)
                    onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        });

        //System.getenv("EXTERNAL_STORAGE")
        //System.getenv("USBOTG_STORAGE")

        return inflaterView;
    }

    private void showStorage(File rootFile) {
        FFChooser_SecondaryDialogFragment ffChooser_secondaryDialogFragment = FFChooser_SecondaryDialogFragment.newInstance(selectType, rootFile.getPath());
        ffChooser_secondaryDialogFragment.setFfChooser_primaryDialogFragment(this);
        ffChooser_secondaryDialogFragment.setShowThumbnails(showThumbnails);
        ffChooser_secondaryDialogFragment.setMultiSelect(multiSelect);
        ffChooser_secondaryDialogFragment.setShowHidden(showHidden);
        ffChooser_secondaryDialogFragment.setOnSelectListener(onSelectListener);
        ffChooser_secondaryDialogFragment.show(getFragmentManager(), "Tag");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                if (onSelectListener != null)
                    onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        };
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
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

    public void setShowGoogleDrive(boolean showGoogleDrive) {
        this.showGoogleDrive = showGoogleDrive;
    }

    public void setShowOneDrive(boolean showOneDrive) {
        this.showOneDrive = showOneDrive;
    }

}
