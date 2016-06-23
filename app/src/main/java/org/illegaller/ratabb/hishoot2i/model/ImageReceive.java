package org.illegaller.ratabb.hishoot2i.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageReceive implements Parcelable {

  public static final Parcelable.Creator<ImageReceive> CREATOR =
      new Parcelable.Creator<ImageReceive>() {
        @Override public ImageReceive createFromParcel(Parcel source) {
          return new ImageReceive(source);
        }

        @Override public ImageReceive[] newArray(int size) {
          return new ImageReceive[size];
        }
      };
  ///* image file path*/
  //public final String imagePath;
  /* image create*/
  private final Uri mImageUri;
  /* type image for background or not*/
  private final boolean mIsBackground;

  public ImageReceive(/*String imagePath*/Uri imageUri, boolean isBackground) {
    //this.imagePath = imagePath;
    this.mImageUri = imageUri;
    this.mIsBackground = isBackground;
  }

  private ImageReceive(Parcel in) {
    this.mImageUri = in.readParcelable(Uri.class.getClassLoader());
    this.mIsBackground = in.readByte() != 0;
  }

  public boolean isBackground() {
    return mIsBackground;
  }

  public Uri getImageUri() {
    return mImageUri;
  }

  @Override public String toString() {
    return "ImageReceive{" +
        //"imagePath='" + imagePath + '\'' +
        ", mIsBackground=" + mIsBackground +
        '}';
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.mImageUri, flags);
    dest.writeByte(mIsBackground ? (byte) 1 : (byte) 0);
  }
}
