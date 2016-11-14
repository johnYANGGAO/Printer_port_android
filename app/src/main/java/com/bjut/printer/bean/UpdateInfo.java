package com.bjut.printer.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by johnsmac on 9/15/16.
 */
public class UpdateInfo implements Parcelable {

    private String softwareVersion;
    private String downloadAddress;
    private String versionDesc;
    private String versionForce;
    private String versionNum;

    public UpdateInfo() {
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getDownloadAddress() {
        return downloadAddress;
    }

    public void setDownloadAddress(String downloadAddress) {
        this.downloadAddress = downloadAddress;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public String getVersionForce() {
        return versionForce;
    }

    public void setVersionForce(String versionForce) {
        this.versionForce = versionForce;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    @Override
    public String toString() {

        return softwareVersion + "==>" +
                downloadAddress + "==>" + versionDesc + "==>" +
                versionForce + "==>" + versionNum;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(softwareVersion);
        out.writeString(downloadAddress);
        out.writeString(versionDesc);
        out.writeString(versionForce);
        out.writeString(versionNum);

    }

    public static final Parcelable.Creator<UpdateInfo> CREATOR = new Creator<UpdateInfo>() {
        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }

        @Override
        public UpdateInfo createFromParcel(Parcel in) {
            return new UpdateInfo(in);
        }
    };

    public UpdateInfo(Parcel in) {
        downloadAddress = in.readString();
        softwareVersion = in.readString();
        versionNum = in.readString();
        versionDesc = in.readString();
        versionForce = in.readString();
    }
}

