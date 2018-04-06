package com.darkorbitstudio.ffchooser;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Ghanshyam Raikwar on 26-03-2018.
 */

public class FFChooser_ItemModel {

    private File file;
    private Bitmap thumbnail;
    private boolean selected;

    public FFChooser_ItemModel(File file) {
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public File getFile() {
        return file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public boolean getSelected() {
        return selected;
    }

}
