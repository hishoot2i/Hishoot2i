package org.illegaller.ratabb.hishoot2i.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageReceive implements Parcelable {
    public static final int TYPE_SS = 0x0001;
    public static final int TYPE_BG = 0x0002;

    public final String imagePath;
    public final int imageType;

    public ImageReceive(String imagePath, int imageType) {
        this.imagePath = imagePath;
        this.imageType = imageType;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
        dest.writeInt(this.imageType);
    }

    protected ImageReceive(Parcel in) {
        this.imagePath = in.readString();
        this.imageType = in.readInt();
    }

    public static final Parcelable.Creator<ImageReceive> CREATOR = new Parcelable.Creator<ImageReceive>() {
        public ImageReceive createFromParcel(Parcel source) {
            return new ImageReceive(source);
        }

        public ImageReceive[] newArray(int size) {
            return new ImageReceive[size];
        }
    };
}
