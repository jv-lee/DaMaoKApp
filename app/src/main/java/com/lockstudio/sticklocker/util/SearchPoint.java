package com.lockstudio.sticklocker.util;

import android.graphics.Path;

// compiled from: SearchPoint.java
public final class SearchPoint {
    public int a;
    public SearchPoint b;

    public SearchPoint(int i) {
        this.a = i;
    }

    public final int getWidth() {
        return this.a & 4095;
    }

    public final int getHeight() {
        return (this.a & 16773120) >>> 12;
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SearchPoint)) {
            return false;
        }
        SearchPoint searchPoint = (SearchPoint) obj;
        return searchPoint.getWidth() == getWidth() && searchPoint.getHeight() == getHeight();
    }


    public final void search(Path path) {
        path.moveTo((float) getWidth(), (float) getHeight());
        SearchPoint searchPoint = this.b;
        while (searchPoint != null && !equals(searchPoint)) {
            path.lineTo((float) searchPoint.getWidth(), (float) searchPoint.getHeight());
            searchPoint = searchPoint.b;
        }
        path.lineTo((float) getWidth(), (float) getHeight());
    }
}