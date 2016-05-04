package it.mdev.sharedservices.database;

/**
 * Created by salem on 29/04/16.
 */
public class PaperDB {
    private String id, name, place;

    public PaperDB(String id, String name, String place) {
        this.id = id;
        this.name = name;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
