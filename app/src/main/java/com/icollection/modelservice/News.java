package com.icollection.modelservice;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mounzer on 8/22/2017.
 */

public class News implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String date;
    private String image;
    private String category;
    private boolean isMain;
    private int numberOfLikes;
    private int numberOfViewer;
    private String icon;

    public News() {
    }

    private News(Parcel in) {
        super();
        this.id = in.readInt();
        this.title = in.readString();
        this.description = in.readString();
        this.date = in.readString();
        this.image = in.readString();
        this.category = in.readString();
        this.isMain = in.readByte() != 0x00;
        this.numberOfLikes = in.readInt();
        this.numberOfViewer = in.readInt();
        this.icon = in.readString();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfViewer() {
        return numberOfViewer;
    }

    public void setNumberOfViewer(int numberOfViewer) {
        this.numberOfViewer = numberOfViewer;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeString(getTitle());
        dest.writeString(getDescription());
        dest.writeString(getDate());
        dest.writeString(getImage());
        dest.writeString(getCategory());
        dest.writeByte((byte) (isMain ? 0x01 : 0x00));
        dest.writeInt(getNumberOfLikes());
        dest.writeInt(getNumberOfViewer());
        dest.writeString(getIcon());
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
