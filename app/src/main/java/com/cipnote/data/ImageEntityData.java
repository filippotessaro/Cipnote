package com.cipnote.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageEntityData implements Parcelable {

    private float x;
    private float y;
    private int logo;
    private int deg;
    private float scale;

    public ImageEntityData() {
    }

    public ImageEntityData(float x, float y, int logo, int deg, float scale) {
        this.x = x;
        this.y = y;
        this.logo = logo;
        this.deg = deg;
        this.scale = scale;
    }


    public static final Creator<ImageEntityData> CREATOR = new Creator<ImageEntityData>() {
        @Override
        public ImageEntityData createFromParcel(Parcel in) {
            return new ImageEntityData(in);
        }

        @Override
        public ImageEntityData[] newArray(int size) {
            return new ImageEntityData[size];
        }
    };

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


    protected ImageEntityData(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        logo = in.readInt();
        deg = in.readInt();
        scale = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeInt(logo);
        dest.writeInt(deg);
        dest.writeFloat(scale);
    }
}