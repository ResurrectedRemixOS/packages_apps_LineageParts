/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import lineageos.providers.LineageSettings;

import org.lineageos.internal.statusbar.NetworkTrafficArrows;
import org.lineageos.lineageparts.R;

public class NetworkTrafficArrowsPreference extends Preference {

    public NetworkTrafficArrowsPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.network_traffic_arrows_preference);
        setSelectable(false);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Context context = getContext();
        final ContentResolver resolver = context.getContentResolver();
        final LinearLayout row = (LinearLayout) holder.findViewById(R.id.network_traffic_arrows);
        row.removeAllViews();

        final TypedArray a = context.obtainStyledAttributes(new int[] {
                android.R.attr.textColorPrimary, android.R.attr.colorAccent });
        final int ink = a.getColor(0, 0xFFFFFFFF);
        final int accent = a.getColor(1, 0xFF808080);
        a.recycle();

        final String[] names = context.getResources()
                .getStringArray(R.array.network_traffic_arrows_entries);
        final int current = LineageSettings.Secure.getInt(resolver,
                LineageSettings.Secure.NETWORK_TRAFFIC_ARROWS, 0);
        final boolean enabled = isEnabled();
        final int height = dp(48);
        final int pad = dp(11);
        final int margin = dp(2);

        for (int i = 0; i < NetworkTrafficArrows.COUNT; i++) {
            final int index = i;
            final boolean selected = i == current;
            final GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(dp(12));
            if (selected) {
                bg.setColor((accent & 0x00FFFFFF) | 0x33000000);
                bg.setStroke(dp(2), accent);
            } else {
                bg.setColor((ink & 0x00FFFFFF) | 0x14000000);
            }

            final ImageView view = new ImageView(context);
            view.setImageDrawable(NetworkTrafficArrows.pairDrawable(i, ink));
            view.setBackground(bg);
            view.setPadding(pad, pad, pad, pad);
            view.setContentDescription(names[i]);
            view.setSelected(selected);
            view.setEnabled(enabled);
            view.setAlpha(enabled ? 1f : 0.4f);
            view.setOnClickListener(v -> {
                LineageSettings.Secure.putInt(resolver,
                        LineageSettings.Secure.NETWORK_TRAFFIC_ARROWS, index);
                notifyChanged();
            });

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height, 1f);
            lp.setMarginStart(margin);
            lp.setMarginEnd(margin);
            row.addView(view, lp);
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                getContext().getResources().getDisplayMetrics());
    }
}
