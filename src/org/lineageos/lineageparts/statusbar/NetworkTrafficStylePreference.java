/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import lineageos.providers.LineageSettings;

import org.lineageos.lineageparts.R;

public class NetworkTrafficStylePreference extends Preference {

    private static final int COLUMNS = 4;

    public NetworkTrafficStylePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.network_traffic_styles);
        setSelectable(false);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Context context = getContext();
        final boolean enabled = isEnabled();
        final int ink = TrafficUi.ink(context);
        final int accent = TrafficUi.accent(context);
        final int muted = TrafficUi.secondary(context);
        final String[] names = context.getResources()
                .getStringArray(R.array.network_traffic_style_entries);
        final int current = LineageSettings.Secure.getInt(context.getContentResolver(),
                LineageSettings.Secure.NETWORK_TRAFFIC_STYLE, 0);

        final LinearLayout grid = (LinearLayout) holder.findViewById(R.id.network_traffic_styles);
        grid.removeAllViews();
        grid.setAlpha(enabled ? 1f : 0.4f);
        final int gap = TrafficUi.dp(context, 4);

        LinearLayout row = null;
        for (int i = 0; i < names.length; i++) {
            if (i % COLUMNS == 0) {
                row = new LinearLayout(context);
                row.setOrientation(LinearLayout.HORIZONTAL);
                final LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    rlp.topMargin = gap * 2;
                }
                grid.addView(row, rlp);
            }
            final int index = i;
            final boolean selected = i == current;

            final LinearLayout tile = new LinearLayout(context);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackground(TrafficUi.tile(context, 14, selected));
            tile.setClickable(true);
            tile.setFocusable(true);
            tile.setEnabled(enabled);
            tile.setSelected(selected);
            tile.setContentDescription(names[i]);
            tile.setOnClickListener(v -> {
                LineageSettings.Secure.putInt(context.getContentResolver(),
                        LineageSettings.Secure.NETWORK_TRAFFIC_STYLE, index);
                notifyChanged();
            });

            final TextView tag = new TextView(context);
            tag.setText(String.valueOf(i + 1));
            tag.setTextSize(12);
            tag.setTypeface(Typeface.create(Typeface.DEFAULT, 700, false));
            tag.setTextColor(selected ? accent : muted);
            tag.setGravity(Gravity.CENTER);
            tile.addView(tag);

            final TextView name = new TextView(context);
            name.setText(names[i]);
            name.setTextSize(12);
            name.setTextColor(ink);
            name.setGravity(Gravity.CENTER);
            name.setMaxLines(2);
            tile.addView(name);

            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    TrafficUi.dp(context, 60), 1f);
            lp.setMarginStart(gap);
            lp.setMarginEnd(gap);
            row.addView(tile, lp);
        }
    }
}
