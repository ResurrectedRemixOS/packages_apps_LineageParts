/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import lineageos.providers.LineageSettings;

import org.lineageos.internal.statusbar.NetworkTraffic;
import org.lineageos.lineageparts.R;

public class NetworkTrafficPreviewPreference extends Preference {

    private static final float ZOOM = 1.8f;

    public NetworkTrafficPreviewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.network_traffic_preview);
        setSelectable(false);
    }

    public void refresh() {
        notifyChanged();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final Context context = getContext();
        final boolean off = LineageSettings.Secure.getInt(context.getContentResolver(),
                LineageSettings.Secure.NETWORK_TRAFFIC_MODE, 0) == 0;

        final View strip = holder.findViewById(R.id.network_traffic_preview_holder);
        strip.setBackground(TrafficUi.rounded(context, 16, 0xFF000000));

        final NetworkTraffic traffic =
                (NetworkTraffic) holder.findViewById(R.id.network_traffic_preview);
        traffic.setPreview(true);
        if (traffic.getTag() == null) {
            traffic.setTag(Boolean.TRUE);
            traffic.setScaleX(ZOOM);
            traffic.setScaleY(ZOOM);
            traffic.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
                v.setPivotX(v.getWidth());
                v.setPivotY(v.getHeight() / 2f);
            });
        }

        final TextView caption = (TextView) holder.findViewById(android.R.id.summary);
        caption.setText(off ? R.string.network_traffic_preview_off
                : R.string.network_traffic_preview_summary);
        caption.setVisibility(View.VISIBLE);
    }
}
