package com.darkorbitstudio.ffchooser;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageInfo;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FFChooser_SecondaryDialogFragment extends DialogFragment {

    private Activity activity;
    private FFChooser_PrimaryDialogFragment ffChooser_primaryDialogFragment;
    private static FFChooser_PrimaryDialogFragment.OnSelectListener onSelectListener;
    private int selectType = FFChooser.Select_Type_File;
    private boolean multiSelect = false;
    private boolean showHidden = false;
    private boolean showThumbnails = false;

    RecyclerView recyclerView;
    LinearLayout emptyFolder;
    private ArrayList<FFChooser_HistoryModel> history;
    private ArrayList<FFChooser_ItemModel> files;
    private FileFilter fileFilter;

    AsyncTask<Integer, Void, Bitmap> loadThumbnailAsyncTask;

    public FFChooser_SecondaryDialogFragment() {

    }

    public static FFChooser_SecondaryDialogFragment newInstance(int selectType, String rootPath) {
        FFChooser_SecondaryDialogFragment ffChooserNew = new FFChooser_SecondaryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("selectType", selectType);
        bundle.putString("rootPath", rootPath);
        ffChooserNew.setArguments(bundle);
        return ffChooserNew;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    FFChooser_ListAdapter listAdapter;
    File currentFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflaterView = inflater.inflate(R.layout.dialog_secondary_chooser, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        activity = getActivity();
        selectType = getArguments().getInt("selectType", FFChooser.Select_Type_File);
        File rootFile = new File(getArguments().getString("rootPath"));
        fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return showHidden || !file.isHidden();
            }
        };
        recyclerView = inflaterView.findViewById(R.id.dialog_secondary_recyclerView);
        emptyFolder = inflaterView.findViewById(R.id.dialog_secondary_emptyFolder);
        if (!multiSelect && selectType == FFChooser.Select_Type_File) {
            inflaterView.findViewById(R.id.dialog_secondary_chooser_footer).setVisibility(View.GONE);
        }
        switch (selectType) {
            case FFChooser.Select_Type_File:
                ((TextView) inflaterView.findViewById(R.id.dialog_secondary_chooser_title)).setText("Select File");
                break;
            case FFChooser.Select_Type_Folder:
                ((TextView) inflaterView.findViewById(R.id.dialog_secondary_chooser_title)).setText("Select Folder");
                break;
        }
        ((TextView) inflaterView.findViewById(R.id.dialog_secondary_chooser_subtitle)).setText("" + rootFile.getPath());
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                layoutParams.width = recyclerView.getWidth();
                recyclerView.setLayoutParams(layoutParams);
            }
        });

        history = new ArrayList<>();
        history.add(new FFChooser_HistoryModel(rootFile, null));
        currentFile = rootFile;
        files = new ArrayList<>();
        for (File f : rootFile.listFiles(fileFilter)) {
            files.add(new FFChooser_ItemModel(f));
        }
        Sort(files);

        listAdapter = new FFChooser_ListAdapter(files);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);

        recyclerView.addOnItemTouchListener(
                new FFChooser_RecyclerViewOnItemClickListener(activity.getApplicationContext(), new FFChooser_RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        File file = files.get(position).getFile();
                        if (file.isDirectory()) {
                            ((TextView) inflaterView.findViewById(R.id.dialog_secondary_chooser_subtitle)).setText(file.getPath());
                            history.add(new FFChooser_HistoryModel(files.get(position).getFile(), linearLayoutManager.onSaveInstanceState()));
                            if (loadThumbnailAsyncTask != null && loadThumbnailAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                                loadThumbnailAsyncTask.cancel(true);
                            files = new ArrayList<>();
                            for (File f : file.listFiles(fileFilter)) {
                                files.add(new FFChooser_ItemModel(f));
                            }
                            Sort(files);
                            loadThumbnail();
                            listAdapter.setFiles(files);
                            recyclerView.scrollToPosition(0);
                        } else if (file.isFile()) {
                            if (selectType == FFChooser.Select_Type_File) {
                                if (multiSelect) {
                                    files.get(position).setSelected(!files.get(position).getSelected());
                                    listAdapter.setSelected(position, files.get(position).getSelected());
                                } else {
                                    onSelectListener.onSelect(FFChooser.Type_Local_Storage, file.getPath());
                                    ffChooser_primaryDialogFragment.dismiss();
                                    dismiss();
                                }
                            }
                        }
                    }
                })
        );
        inflaterView.findViewById(R.id.dialog_secondary_chooser_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history.size() > 1) {
                    if (loadThumbnailAsyncTask != null && loadThumbnailAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                        loadThumbnailAsyncTask.cancel(true);
                    files = new ArrayList<>();
                    ((TextView) inflaterView.findViewById(R.id.dialog_secondary_chooser_subtitle)).setText(history.get(history.size() - 2).getFile().getPath());
                    for (File f : history.get(history.size() - 2).getFile().listFiles(fileFilter)) {
                        files.add(new FFChooser_ItemModel(f));
                    }
                    Sort(files);
                    loadThumbnail();
                    listAdapter.setFiles(files);
                    linearLayoutManager.onRestoreInstanceState(history.get(history.size() - 1).getParcelable());
                    history.remove(history.size() - 1);
                } else if (history.size() == 1) {
                    dismiss();
                }
            }
        });

        return inflaterView;
    }

    private void loadThumbnail() {
        if (showThumbnails) {
            if (loadThumbnailAsyncTask != null && loadThumbnailAsyncTask.getStatus() != AsyncTask.Status.FINISHED)
                loadThumbnailAsyncTask.cancel(true);
            loadThumbnailAsyncTask = new AsyncTask<Integer, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Integer... integers) {
                    for (int i = 0; i < files.size(); i++) {
                        final int j = i;
                        File file = files.get(i).getFile();
                        String name = file.getName().toLowerCase();
                        if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".bmp")) {
                            files.get(i).setThumbnail(getThumbnail(1, file.getPath()));
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (j <= files.size())
                                        listAdapter.setFile(j, files.get(j));
                                }
                            });
                        } else if (name.endsWith(".mp4") || name.endsWith(".3gp") || name.endsWith(".tc") || name.endsWith(".webm")) {
                            files.get(i).setThumbnail(getThumbnail(0, file.getPath()));
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (j <= files.size())
                                        listAdapter.setFile(j, files.get(j));
                                }
                            });
                        } else if (name.endsWith(".apk")) {
                            PackageManager packageManager = activity.getPackageManager();
                            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(file.getPath(), 0);
                            packageInfo.applicationInfo.sourceDir = file.getPath();
                            packageInfo.applicationInfo.publicSourceDir = file.getPath();
                            files.get(i).setThumbnail(drawableToBitmap(packageInfo.applicationInfo.loadIcon(packageManager)));
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (j <= files.size())
                                        listAdapter.setFile(j, files.get(j));
                                }
                            });
                        }
                    }
                    return null;
                }
            };
            loadThumbnailAsyncTask.execute();
        }
    }

    private static Bitmap getThumbnail(int type, String path) {
        Bitmap bitmap = null;
        if (type == 1) {
            bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 96, 96);
        } else {
            bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
        }
        if (bitmap == null)
            return null;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = dpToPx(8);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private void Sort(ArrayList<FFChooser_ItemModel> files) {
        if (files.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyFolder.setVisibility(View.VISIBLE);
        } else {
            emptyFolder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Collections.sort(files, new SortFileName());
            Collections.sort(files, new SortFolder());
        }
    }

    private class SortFileName implements Comparator<FFChooser_ItemModel> {
        @Override
        public int compare(FFChooser_ItemModel f1, FFChooser_ItemModel f2) {
            return f1.getFile().getName().toLowerCase().compareTo(f2.getFile().getName().toLowerCase());
        }
    }

    private class SortFolder implements Comparator<FFChooser_ItemModel> {
        @Override
        public int compare(FFChooser_ItemModel f1, FFChooser_ItemModel f2) {
            if (f1.getFile().isDirectory() == f2.getFile().isDirectory())
                return 0;
            else if (f1.getFile().isDirectory() && !f2.getFile().isDirectory())
                return -1;
            else
                return 1;
        }
    }

    private void show() {
        FFChooser_PrimaryDialogFragment ffChooser_primaryDialogFragment = FFChooser_PrimaryDialogFragment.newInstance(selectType);
        ffChooser_primaryDialogFragment.show(getFragmentManager(), "Tag");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        onSelectListener.onSelect(FFChooser.Type_None, null);
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                //onSelectListener.onSelect(FFChooser.Type_None, null);
                dismiss();
            }
        };
    }

    public void setFfChooser_primaryDialogFragment(FFChooser_PrimaryDialogFragment ffChooser_primaryDialogFragment) {
        this.ffChooser_primaryDialogFragment = ffChooser_primaryDialogFragment;
    }

    public void setOnSelectListener(FFChooser_PrimaryDialogFragment.OnSelectListener onSelectListener) {
        FFChooser_SecondaryDialogFragment.onSelectListener = onSelectListener;
    }

    private FFChooser_PrimaryDialogFragment.OnSelectListener getDefaultOnSelectListener() {
        return new FFChooser_PrimaryDialogFragment.OnSelectListener() {
            @Override
            public void onSelect(int type, String path) {
                Log.e("Exc", "No OnSelectListener found.\nType : " + type + "\nPath : " + path);
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
}