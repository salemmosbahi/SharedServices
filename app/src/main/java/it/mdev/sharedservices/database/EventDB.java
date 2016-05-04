package it.mdev.sharedservices.database;

/**
 * Created by salem on 01/05/16.
 */
public class EventDB {
    private String id, name, city, date;

    public EventDB(String id, String name, String city, String date) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
