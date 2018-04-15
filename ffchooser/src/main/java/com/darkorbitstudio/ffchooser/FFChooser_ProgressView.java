package com.darkorbitstudio.ffchooser;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Porush Manjhi on 12-05-2017.
 */

public class FFChooser_ProgressView extends LinearLayout {

    FrameLayout frameLayout1;
    FrameLayout frameLayout2;

    public FFChooser_ProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setOrientation(LinearLayout.HORIZONTAL);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(layoutTransition);

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FFChooser_ProgressView, 0, 0);
        int progress = typedArray.getInt(R.styleable.FFChooser_ProgressView_progress, 0);
        int color = typedArray.getColor(R.styleable.FFChooser_ProgressView_color, Color.parseColor("#373737"));
        typedArray.recycle();

        frameLayout1 = new FrameLayout(context);
        frameLayout2 = new FrameLayout(context);
        frameLayout1.setBackgroundResource(R.drawable.ffchooser_background_progress);
        setBackgroundResource(R.drawable.ffchooser_background_progress);

        setColor(color);
        setProgress(progress);

        addView(frameLayout1);
        addView(frameLayout2);
    }

    private static int lightColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) + factor);
        int g = Math.round(Color.green(color) + factor);
        int b = Math.round(Color.blue(color) + factor);
        return Color.argb(a,
                Math.min(Math.max(r, 0), 255),
                Math.min(Math.max(g, 0), 255),
                Math.min(Math.max(b, 0), 255));
    }

    public void setProgress(int progress) {
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                100 - progress
        );
        frameLayout1.setLayoutParams(layoutParams1);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                progress
        );
        frameLayout2.setLayoutParams(layoutParams2);
    }

    public void setColor(int color) {
        frameLayout1.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        getBackground().setColorFilter(lightColor(color, 105), PorterDuff.Mode.SRC_IN);
    }
}
