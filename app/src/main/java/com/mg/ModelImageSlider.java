package com.mg;

public class ModelImageSlider {

    String d;
    String imageUrl;

    public ModelImageSlider() {

    }

    public ModelImageSlider(String d, String imageUrl) {
        this.d = d;
        this.imageUrl = imageUrl;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
