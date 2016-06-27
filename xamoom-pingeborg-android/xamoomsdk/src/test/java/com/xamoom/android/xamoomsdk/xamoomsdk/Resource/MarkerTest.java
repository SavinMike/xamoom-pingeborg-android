package com.xamoom.android.xamoomsdk.xamoomsdk.Resource;

import android.os.Parcel;
import android.test.suitebuilder.annotation.SmallTest;

import com.google.android.gms.maps.MapView;
import com.xamoom.android.xamoomsdk.BuildConfig;
import com.xamoom.android.xamoomsdk.Resource.Location;
import com.xamoom.android.xamoomsdk.Resource.Marker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "AndroidManifest.xml")
public class MarkerTest {

  private Marker mMarker;

  @Before
  public void setup() {
    mMarker = new Marker();
    mMarker.setQr("qr");
    mMarker.setNfc("nfc");
    mMarker.setBeaconUUID("UUID");
    mMarker.setBeaconMajor("major");
    mMarker.setBeaconMinor("minor");
    mMarker.setEddystoneUrl("www.eddystone.url");
  }

  @Test
  public void testConstructor() {
    Marker marker = new Marker();

    assertNotNull(marker);
  }

  @Test
  public void testParcelable() {
    Parcel parcel = Parcel.obtain();
    mMarker.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);
    Marker createdFromParcel = Marker.CREATOR.createFromParcel(parcel);

    assertEquals(mMarker.getId(), createdFromParcel.getId());
    assertEquals(mMarker.getQr(), createdFromParcel.getQr());
    assertEquals(mMarker.getNfc(), createdFromParcel.getNfc());
    assertEquals(mMarker.getBeaconUUID(), createdFromParcel.getBeaconUUID());
    assertEquals(mMarker.getBeaconMajor(), createdFromParcel.getBeaconMajor());
    assertEquals(mMarker.getBeaconMinor(), createdFromParcel.getBeaconMinor());
    assertEquals(mMarker.getEddystoneUrl(), createdFromParcel.getEddystoneUrl());
  }

}
