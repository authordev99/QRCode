package com.teddybrothers.qrcodereader;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class IconView extends AppCompatTextView {

    public IconView(Context context) {
        super(context);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context);
    }

    public boolean setCustomFont(Context ctx) {
        Typeface tf;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/icons.ttf");
        } catch (Exception e) {
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
