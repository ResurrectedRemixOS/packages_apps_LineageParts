/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import lineageos.providers.LineageSettings;

import org.lineageos.lineageparts.R;

public class TrafficSegmentedPreference extends Preference {

    private final CharSequence[] mEntries;
    private final int[] mValues;
    private final CharSequence[] mSummaries;
    private final int mDefault;

    public TrafficSegmentedPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.network_traffic_segmented);
        setSelectable(false);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                new int[] { android.R.attr.entries, android.R.attr.entryValues });
        mEntries = a.getTextArray(0);
        final CharSequence[] values = a.getTextArray(1);
        a.recycle();
        mValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            mValues[i] = Integer.parseInt(values[i].toString());
        }

        final TypedArray b = context.obtainStyledAttributes(attrs,
                R.styleable.TrafficSegmentedPreference);
        mDefault = b.getInt(R.styleable.TrafficSegmentedPreference_segmentDefault, mValues[0]);
        final int summaries =
                b.getResourceId(R.styleable.TrafficSegmentedPreference_segmentSummaries, 0);
        b.recycle();
        mSummaries = summaries != 0 ? context.getResources().getTextArray(summaries) : null;
    }

    public int getValue() {
        return LineageSettings.Secure.getInt(getContext().getContentResolver(), getKey(),
                mDefault);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Context context = getContext();
        final boolean enabled = isEnabled();
        final int current = getValue();
        final int accent = TrafficUi.accent(context);
        final int ink = TrafficUi.ink(context);

        final TextView title = (TextView) holder.findViewById(android.R.id.title);
        title.setVisibility(getTitle() == null ? View.GONE : View.VISIBLE);

        final LinearLayout row = (LinearLayout) holder.findViewById(R.id.network_traffic_segments);
        row.removeAllViews();
        row.setBackground(TrafficUi.rounded(context, 22, TrafficUi.withAlpha(ink, 0x14)));
        final int pad = TrafficUi.dp(context, 4);
        row.setPadding(pad, pad, pad, pad);
        row.setAlpha(enabled ? 1f : 0.4f);

        int selectedIndex = 0;
        for (int i = 0; i < mEntries.length; i++) {
            final int value = mValues[i];
            final boolean selected = value == current;
            if (selected) {
                selectedIndex = i;
            }
            final TextView button = new TextView(context);
            button.setText(mEntries[i]);
            button.setGravity(Gravity.CENTER);
            button.setTextSize(14);
            button.setTypeface(android.graphics.Typeface.create(
                    android.graphics.Typeface.DEFAULT, 600, false));
            button.setTextColor(selected ? TrafficUi.onColor(accent) : ink);
            button.setBackground(selected ? TrafficUi.rounded(context, 18, accent) : null);
            button.setSelected(selected);
            button.setEnabled(enabled);
            button.setClickable(true);
            button.setFocusable(true);
            button.setOnClickListener(v -> {
                LineageSettings.Secure.putInt(context.getContentResolver(), getKey(), value);
                notifyChanged();
                callChangeListener(value);
            });
            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    TrafficUi.dp(context, 40), 1f);
            row.addView(button, lp);
        }

        final TextView summary = (TextView) holder.findViewById(android.R.id.summary);
        if (mSummaries != null && selectedIndex < mSummaries.length) {
            summary.setText(mSummaries[selectedIndex]);
            summary.setVisibility(View.VISIBLE);
        } else if (getSummary() != null) {
            summary.setVisibility(View.VISIBLE);
        } else {
            summary.setVisibility(View.GONE);
        }
    }
}
