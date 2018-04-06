package com.darkorbitstudio.ffchooser;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.File;

class FFChooser_HistoryModel {

    private File file;
    private Parcelable parcelable;

    public FFChooser_HistoryModel(File file, Parcelable parcelable) {
        this.file = file;
        this.parcelable = parcelable;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setParcelable(Parcelable parcelable) {
        this.parcelable = parcelable;
    }

    public File getFile() {
        return file;
    }

    public Parcelable getParcelable() {
        return parcelable;
    }
}
