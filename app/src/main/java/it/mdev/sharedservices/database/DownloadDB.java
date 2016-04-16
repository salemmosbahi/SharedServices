package it.mdev.sharedservices.database;

/**
 * Created by salem on 15/04/16.
 */
public class DownloadDB {
    private String id, name, date;
    private int size;
    private boolean status;

    public DownloadDB(String id, String name, String date, int size, boolean status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.size = size;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int getSize() {
        return size;
    }

    public boolean isStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
