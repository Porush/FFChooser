package com.darkorbitstudio.ffchooser;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Porush Manjhi on 16-02-2018.
 */

class FFChooser_Primary_ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<File> files;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout root;
        public final ImageView icon;
        public final TextView title;
        public final TextView readable;
        public final TextView writable;
        public final FFChooser_ProgressView progressView;
        public final TextView totalSpace;
        public final TextView freeSpace;
        public ItemViewHolder(View view) {
            super(view);
            root = (FrameLayout) view.findViewById(R.id.layout_primary_chooser_list_item_root);
            icon = (ImageView) view.findViewById(R.id.layout_primary_chooser_list_item_icon);
            title = (TextView) view.findViewById(R.id.layout_primary_chooser_list_item_title);
            readable = (TextView) view.findViewById(R.id.layout_primary_chooser_list_item_readable);
            writable = (TextView) view.findViewById(R.id.layout_primary_chooser_list_item_writable);
            progressView = (FFChooser_ProgressView) view.findViewById(R.id.layout_primary_chooser_list_item_progress);
            totalSpace = (TextView) view.findViewById(R.id.layout_primary_chooser_list_item_total);
            freeSpace = (TextView) view.findViewById(R.id.layout_primary_chooser_list_item_free);
        }
    }

    public FFChooser_Primary_ListAdapter(ArrayList<File> files) {
        this.files = files;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_primary_chooser_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) viewHolder;
        final File file = files.get(position);

        holder.title.setText(file.getName());
        if (isDirectoryReadble(file)) {
            holder.readable.setVisibility(View.VISIBLE);
        } else {
            holder.readable.setVisibility(View.GONE);
        }
        if (isDirectoryWritable(file)) {
            holder.writable.setVisibility(View.VISIBLE);
        } else {
            holder.writable.setVisibility(View.GONE);
        }
        /*long totalSpace = file.getTotalSpace();
        long freeSpace = file.getFreeSpace();
        holder.totalSpace.setText(formatSize(totalSpace) + " Total");
        holder.freeSpace.setText(formatSize(freeSpace) + " Free");
        if (totalSpace == 0) {
            holder.progressView.setProgress(100);
        } else {
            holder.progressView.setProgress(100 - (int) (freeSpace * 100 / totalSpace));
        }*/
        showSpace(holder, file);
    }

    private void showSpace(final ItemViewHolder holder, final File file) {
        AsyncTask<Integer, Void, long[]> asyncTask = new AsyncTask<Integer, Void, long[]>() {
            @Override
            protected long[] doInBackground(Integer... integers) {
                long[] result = new long[2];
                result[0] = file.getTotalSpace();
                result[1] = file.getFreeSpace();
                return result;
            }

            protected void onPostExecute(long[] result) {
                holder.totalSpace.setText(formatSize(result[0]) + " Total");
                holder.freeSpace.setText(formatSize(result[1]) + " Free");
                if (result[0] == 0) {
                    holder.progressView.setProgress(100);
                } else {
                    holder.progressView.setProgress(100 - (int) (result[1] * 100 / result[0]));
                }
            }
        };
        asyncTask.execute();
    }

    private String formatSize(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("###.##");
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
    public int getItemCount() {
        return files.size();
    }

}
