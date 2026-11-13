/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import lineageos.providers.LineageSettings;

import org.lineageos.lineageparts.R;

public class TrafficSliderPreference extends Preference {

    private final int mMin;
    private final int mMax;
    private final int mStep;
    private final int mDefault;
    private final String mUnit;
    private final boolean mReset;

    public TrafficSliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.network_traffic_slider);
        setSelectable(false);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.TrafficSliderPreference);
        mMin = a.getInt(R.styleable.TrafficSliderPreference_sliderMin, 0);
        mMax = a.getInt(R.styleable.TrafficSliderPreference_sliderMax, 100);
        mStep = Math.max(1, a.getInt(R.styleable.TrafficSliderPreference_sliderStep, 1));
        mDefault = a.getInt(R.styleable.TrafficSliderPreference_sliderDefault, mMin);
        final String unit = a.getString(R.styleable.TrafficSliderPreference_sliderUnit);
        mUnit = unit == null ? "" : unit.startsWith("%") ? unit : " " + unit;
        mReset = a.getBoolean(R.styleable.TrafficSliderPreference_sliderReset, false);
        a.recycle();
    }

    private int getValue() {
        final int value = LineageSettings.Secure.getInt(getContext().getContentResolver(),
                getKey(), mDefault);
        return value < mMin || value > mMax ? mDefault : value;
    }

    private void setValue(int value) {
        LineageSettings.Secure.putInt(getContext().getContentResolver(), getKey(), value);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Context context = getContext();
        final boolean enabled = isEnabled();
        final int accent = TrafficUi.accent(context);

        final TextView value = (TextView) holder.findViewById(R.id.network_traffic_slider_value);
        value.setTextColor(accent);
        value.setText(getValue() + mUnit);

        final TextView reset = (TextView) holder.findViewById(R.id.network_traffic_slider_reset);
        reset.setVisibility(mReset ? View.VISIBLE : View.GONE);
        reset.setBackground(TrafficUi.rounded(context, 16,
                TrafficUi.withAlpha(TrafficUi.ink(context), 0x14)));
        reset.setEnabled(enabled);

        final SeekBar bar = (SeekBar) holder.findViewById(R.id.network_traffic_slider_bar);
        bar.setOnSeekBarChangeListener(null);
        bar.setMax((mMax - mMin) / mStep);
        bar.setProgress((getValue() - mMin) / mStep);
        bar.setEnabled(enabled);
        bar.setProgressTintList(ColorStateList.valueOf(accent));
        bar.setThumbTintList(ColorStateList.valueOf(accent));
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                final int v = mMin + progress * mStep;
                value.setText(v + mUnit);
                setValue(v);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        reset.setOnClickListener(v -> {
            setValue(mDefault);
            notifyChanged();
        });

        final View ticks = holder.findViewById(R.id.network_traffic_slider_ticks);
        ticks.setVisibility(mReset ? View.VISIBLE : View.GONE);
        ((TextView) holder.findViewById(R.id.network_traffic_slider_min)).setText(mMin + mUnit);
        ((TextView) holder.findViewById(R.id.network_traffic_slider_mid)).setText(
                mDefault + mUnit);
        ((TextView) holder.findViewById(R.id.network_traffic_slider_max)).setText(mMax + mUnit);

        holder.itemView.setAlpha(enabled ? 1f : 0.45f);
    }
}
