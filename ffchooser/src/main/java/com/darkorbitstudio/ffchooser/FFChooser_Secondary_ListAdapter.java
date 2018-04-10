package com.darkorbitstudio.ffchooser;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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

class FFChooser_Secondary_ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FFChooser_ItemModel> files;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final FrameLayout root;
        public final ImageView icon;
        public final TextView title;
        public final TextView subtitle;
        public ItemViewHolder(View view) {
            super(view);
            root = (FrameLayout) view.findViewById(R.id.layout_secondary_chooser_list_item_root);
            icon = (ImageView) view.findViewById(R.id.layout_secondary_chooser_list_item_icon);
            title = (TextView) view.findViewById(R.id.layout_secondary_chooser_list_item_title);
            subtitle = (TextView) view.findViewById(R.id.layout_secondary_chooser_list_item_subtitle);
        }
    }

    public FFChooser_Secondary_ListAdapter(ArrayList<FFChooser_ItemModel> files) {
        this.files = files;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_secondary_chooser_list_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) viewHolder;
        final FFChooser_ItemModel itemModel = files.get(position);
        final File file = itemModel.getFile();

        if (itemModel.getSelected()) {
            holder.root.setBackgroundColor(Color.parseColor("#cccccc"));
        } else {
            holder.root.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }

        holder.icon.setBackgroundResource(R.drawable.ffchooser_background_black);
        holder.icon.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
        holder.title.setText(file.getName());
        if (file.isDirectory()) {
            /*holder.icon.setBackground(null);
            holder.icon.setImageResource(R.drawable.ffchooser_icon_folder_black_24dp);
            holder.icon.setPadding(0, 0, 0, 0);*/
            setIcon(holder.icon, R.drawable.ffchooser_background_black, R.drawable.ffchooser_icon_folder_black_24dp, 14);
            holder.subtitle.setText(file.listFiles().length + " Files");
        } else if (file.isFile()) {
            String name = file.getName().toLowerCase();
            switch (name.contains(".") ? name.substring(name.lastIndexOf(".")) : "") {
                case ".png":
                case ".jpg":
                case ".jpeg":
                case ".gif":
                case ".bmp":
                case ".webp":
                    if (itemModel.getThumbnail() == null) {
                        setIcon(holder.icon, R.drawable.ffchooser_background_blue, R.drawable.ffchooser_icon_image_white_24dp, 14);
                    } else {
                        holder.icon.setPadding(0, 0, 0, 0);
                        holder.icon.setImageBitmap(itemModel.getThumbnail());
                    }
                    break;
                case ".mp4":
                case ".webm":
                case ".ts":
                case ".mkv":
                case ".avi":
                case ".flv":
                case ".3gp":
                    if (itemModel.getThumbnail() == null) {
                        setIcon(holder.icon, R.drawable.ffchooser_background_blue, R.drawable.ffchooser_icon_video_white_24dp, 14);
                    } else {
                        holder.icon.setPadding(0, 0, 0, 0);
                        holder.icon.setImageBitmap(itemModel.getThumbnail());
                    }
                    break;
                case ".mp3":
                case ".ogg":
                case ".3gpp":
                    setIcon(holder.icon, R.drawable.ffchooser_background_blue, R.drawable.ffchooser_icon_music_white_24dp, 14);
                    break;
                case ".txt":
                    setIcon(holder.icon, R.drawable.ffchooser_background_blue_dark, R.drawable.ffchooser_icon_text_white_24dp, 14);
                    break;
                case ".java":
                case ".cs":
                case ".html":
                case ".css":
                case ".js":
                    setIcon(holder.icon, R.drawable.ffchooser_background_blue_dark, R.drawable.ffchooser_icon_code_white_24dp, 14);
                    break;
                case ".pdf":
                    setIcon(holder.icon, R.drawable.ffchooser_background_red, R.drawable.ffchooser_icon_pdf_white_24dp, 14);
                    break;
                case ".ttf":
                case ".otf":
                case ".woff":
                    setIcon(holder.icon, R.drawable.ffchooser_background_blue, R.drawable.ffchooser_icon_font_white_24dp, 14);
                    break;
                case ".apk":
                    if (itemModel.getThumbnail() == null) {
                        setIcon(holder.icon, R.drawable.ffchooser_background_green, R.drawable.ffchooser_icon_apk_white_24dp, 14);
                    } else {
                        holder.icon.setBackground(null);
                        holder.icon.setPadding(0, 0, 0, 0);
                        holder.icon.setImageBitmap(itemModel.getThumbnail());
                    }
                    break;
                default:
                    setIcon(holder.icon, R.drawable.ffchooser_background_grey, R.drawable.ffchooser_icon_file_black_24dp, 14);
                    break;
            }
            holder.subtitle.setText(getSize(file));
        }
    }

    private void setIcon(ImageView imageView, int background, int icon, int padding) {
        imageView.setBackgroundResource(background);
        imageView.setImageResource(icon);
        padding = dpToPx(padding);
        imageView.setPadding(padding, padding, padding, padding);
    }

    private static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    private String getSize(File file) {
        long size = file.length();
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
        /*if (size < 1024) {
            return size + "bytes";
        } else if (size >= 1024 && size < 1048576) {
            return decimalFormat.format((float) size / 1024) + "KiB";
        } else if (size >= 1048576 && size < 1073741824) {
            return decimalFormat.format((float) size / 1048576) + "MiB";
        } else if (size >= 1073741824) {
            return decimalFormat.format((float) size / 1073741824) + "GiB";
        }
        return "";*/
    }

    public void setFile(int position, FFChooser_ItemModel ffChooser_itemModel) {
        this.files.set(position, ffChooser_itemModel);
        notifyItemChanged(position);
    }

    public void setFiles(ArrayList<FFChooser_ItemModel> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setSelected(int position, boolean selected) {
        files.get(position).setSelected(selected);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}
