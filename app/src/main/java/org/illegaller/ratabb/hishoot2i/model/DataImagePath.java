package org.illegaller.ratabb.hishoot2i.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DataImagePath implements Parcelable {
  public static final Creator<DataImagePath> CREATOR = new Creator<DataImagePath>() {
    public DataImagePath createFromParcel(Parcel source) {
      return new DataImagePath(source);
    }

    public DataImagePath[] newArray(int size) {
      return new DataImagePath[size];
    }
  };
  public final String pathImageScreen1;
  public final String pathImageScreen2;
  public final String pathImageBackground;

  public DataImagePath(String pathImageScreen1, String pathImageScreen2,
      String pathImageBackground) {
    this.pathImageScreen1 = pathImageScreen1;
    this.pathImageScreen2 = pathImageScreen2;
    this.pathImageBackground = pathImageBackground;
  }

  protected DataImagePath(Parcel in) {
    this.pathImageScreen1 = in.readString();
    this.pathImageScreen2 = in.readString();
    this.pathImageBackground = in.readString();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.pathImageScreen1);
    dest.writeString(this.pathImageScreen2);
    dest.writeString(this.pathImageBackground);
  }

  @Override public String toString() {
    return "DataImagePath{"
        + "pathImageScreen1='"
        + pathImageScreen1
        + '\''
        + ", pathImageScreen2='"
        + pathImageScreen2
        + '\''
        + ", pathImageBackground='"
        + pathImageBackground
        + '\''
        + '}';
  }
}
