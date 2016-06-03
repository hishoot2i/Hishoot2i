package org.illegaller.ratabb.hishoot2i.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageReceive implements Parcelable {
  public static final Creator<ImageReceive> CREATOR = new Creator<ImageReceive>() {
    @Override public ImageReceive createFromParcel(Parcel source) {
      return new ImageReceive(source);
    }

    @Override public ImageReceive[] newArray(int size) {
      return new ImageReceive[size];
    }
  };
  /* image file path*/
  public final String imagePath;
  /* type image for background or not*/
  public final boolean isBackground;

  public ImageReceive(String imagePath, boolean isBackground) {
    this.imagePath = imagePath;
    this.isBackground = isBackground;
  }

  protected ImageReceive(Parcel in) {
    this.imagePath = in.readString();
    this.isBackground = in.readByte() != 0;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.imagePath);
    dest.writeByte(isBackground ? (byte) 1 : (byte) 0);
  }

  @Override public String toString() {
    return "ImageReceive{" +
        "imagePath='" + imagePath + '\'' +
        ", isBackground=" + isBackground +
        '}';
  }
}
