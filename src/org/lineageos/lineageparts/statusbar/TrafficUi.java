/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;

final class TrafficUi {

    private TrafficUi() {}

    static int dp(Context context, float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                context.getResources().getDisplayMetrics());
    }

    static int ink(Context context) {
        return attrColor(context, android.R.attr.textColorPrimary, Color.WHITE);
    }

    static int secondary(Context context) {
        return attrColor(context, android.R.attr.textColorSecondary, Color.GRAY);
    }

    static int accent(Context context) {
        return attrColor(context, android.R.attr.colorAccent, Color.GRAY);
    }

    static int onColor(int color) {
        return Color.luminance(color) > 0.5f ? Color.BLACK : Color.WHITE;
    }

    static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    static GradientDrawable rounded(Context context, float radiusDp, int fill) {
        final GradientDrawable d = new GradientDrawable();
        d.setCornerRadius(dp(context, radiusDp));
        d.setColor(fill);
        return d;
    }

    static GradientDrawable tile(Context context, float radiusDp, boolean selected) {
        final int accent = accent(context);
        final GradientDrawable d = rounded(context, radiusDp,
                selected ? withAlpha(accent, 0x33) : withAlpha(ink(context), 0x14));
        if (selected) {
            d.setStroke(dp(context, 2), accent);
        }
        return d;
    }

    private static int attrColor(Context context, int attr, int fallback) {
        final TypedArray a = context.obtainStyledAttributes(new int[] { attr });
        final int color = a.getColor(0, fallback);
        a.recycle();
        return color;
    }
}
