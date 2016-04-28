package it.mdev.sharedservices.database;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadDB {
    private String id, picture, name, date, status;
    private int size;

    public DownloadDB(String id, String picture, String name, String date, String status, int size) {
        this.id = id;
        this.picture = picture;
        this.name = name;
        this.date = date;
        this.status = status;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getSize() {
        return size;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
