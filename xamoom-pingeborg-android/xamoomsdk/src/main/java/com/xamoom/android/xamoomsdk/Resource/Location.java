package com.xamoom.android.xamoomsdk.Resource;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * location model.
 */
public class Location implements Parcelable {
  @SerializedName("lat")
  private double mLatitude;
  @SerializedName("lon")
  private double mLongitude;

  public Location() {
  }

  protected Location(Parcel in) {
    mLatitude = in.readDouble();
    mLongitude = in.readDouble();
  }

  public static final Creator<Location> CREATOR = new Creator<Location>() {
    @Override
    public Location createFromParcel(Parcel in) {
      return new Location(in);
    }

    @Override
    public Location[] newArray(int size) {
      return new Location[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(mLatitude);
    dest.writeDouble(mLongitude);
  }

  public double getLatitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public void setLatitude(double latitude) {
    mLatitude = latitude;
  }

  public void setLongitude(double longitude) {
    mLongitude = longitude;
  }
}
