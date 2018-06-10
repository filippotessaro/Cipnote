package com.cipnote.data;

public class ImageEntityData {

    private float x,y;
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
}

