package com.steadfastinnovation.mediarouter;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.widget.TextView;

import java.util.Random;

public class ColorPresentation extends Presentation {
    final Random mRandom = new Random(System.currentTimeMillis());

    public ColorPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public ColorPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(getContext());
        tv.setText("Display - " + getDisplay().getName());
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
        tv.setBackgroundColor(generateRandomColor());
        tv.setGravity(Gravity.CENTER);

        setContentView(tv);
    }

    public int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        final int baseColor = Color.DKGRAY;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + mRandom.nextInt(256)) / 2;
        final int green = (baseGreen + mRandom.nextInt(256)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(256)) / 2;

        return Color.rgb(red, green, blue);
    }
}
