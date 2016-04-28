package it.mdev.sharedservices.database;

/**
 * Created by salem on 28/04/16.
 */
public class CarDB {
    private String id, model, depart, destination, date;

    public CarDB(String id, String model, String depart, String destination, String date) {
        this.id = id;
        this.model = model;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getDepart() {
        return depart;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
