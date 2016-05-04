package it.mdev.sharedservices.database;

/**
 * Created by salem on 29/04/16.
 */
public class PaperIndexDB {
    private String name;

    public PaperIndexDB(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
