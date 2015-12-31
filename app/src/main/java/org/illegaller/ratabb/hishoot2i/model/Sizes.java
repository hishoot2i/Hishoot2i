package org.illegaller.ratabb.hishoot2i.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Sizes{

    public int width;
    public int height;

    /**
     * {@code <b>No construction</b><\n>
     * use {@link Sizes#create(int, int)}}
     */
    protected Sizes() {
    }

    public static Sizes create(int width, int height) {
        Sizes sizes = new Sizes();
        sizes.width = width;
        sizes.height = height;
        return sizes;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sizes sizes = (Sizes) o;

        return width == sizes.width && height == sizes.height;

    }

    @Override public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override public String toString() {
        return "Sizes{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }


}
