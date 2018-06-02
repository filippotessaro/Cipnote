package com.cipnote.data;

public class ImagesUrl {
    String backgroundColor;
    String localBackgroundUrl, cloudBackgroundUrl, drawFirebaseUrl;

    public ImagesUrl() {
    }

    public ImagesUrl(String backgroundColor, String localBackgroundUrl, String cloudBackgroundUrl, String drawFirebaseUrl) {
        this.backgroundColor = backgroundColor;
        this.localBackgroundUrl = localBackgroundUrl;
        this.cloudBackgroundUrl = cloudBackgroundUrl;
        this.drawFirebaseUrl = drawFirebaseUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getLocalBackgroundUrl() {
        return localBackgroundUrl;
    }

    public void setLocalBackgroundUrl(String localBackgroundUrl) {
        this.localBackgroundUrl = localBackgroundUrl;
    }

    public String getCloudBackgroundUrl() {
        return cloudBackgroundUrl;
    }

    public void setCloudBackgroundUrl(String cloudBackgroundUrl) {
        this.cloudBackgroundUrl = cloudBackgroundUrl;
    }

    public String getDrawFirebaseUrl() {
        return drawFirebaseUrl;
    }

    public void setDrawFirebaseUrl(String drawFirebaseUrl) {
        this.drawFirebaseUrl = drawFirebaseUrl;
    }
}
