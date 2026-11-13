/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.lineageparts.statusbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.lineageos.internal.statusbar.LineageStatusBarItem;

import java.util.ArrayList;

public class TrafficPreviewHolder extends FrameLayout implements LineageStatusBarItem.Manager {

    private final ArrayList<LineageStatusBarItem.VisibilityReceiver> mVisibilityReceivers =
            new ArrayList<>();
    private final ArrayList<Rect> mAreas = new ArrayList<>();
    private boolean mVisible;

    public TrafficPreviewHolder(Context context) {
        this(context, null);
    }

    public TrafficPreviewHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible == mVisible) {
            return;
        }
        mVisible = isVisible;
        for (LineageStatusBarItem.VisibilityReceiver r : mVisibilityReceivers) {
            r.onVisibilityChanged(mVisible);
        }
    }

    @Override
    public void addDarkReceiver(LineageStatusBarItem.DarkReceiver darkReceiver) {
        darkReceiver.onDarkChanged(mAreas, 0f, Color.WHITE);
    }

    @Override
    public void removeDarkReceiver(LineageStatusBarItem.DarkReceiver darkReceiver) {
    }

    @Override
    public void addVisibilityReceiver(LineageStatusBarItem.VisibilityReceiver receiver) {
        mVisibilityReceivers.add(receiver);
        receiver.onVisibilityChanged(mVisible);
    }

    @Override
    public void removeVisibilityReceiver(LineageStatusBarItem.VisibilityReceiver receiver) {
        mVisibilityReceivers.remove(receiver);
    }
}
