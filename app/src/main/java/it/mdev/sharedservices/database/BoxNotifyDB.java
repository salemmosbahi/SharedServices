package it.mdev.sharedservices.database;

/**
 * Created by salem on 23/04/16.
 */
public class BoxNotifyDB {
    private String service, idService, name, token, username, status;
    private boolean main;
    private String usernameMain;

    public BoxNotifyDB(String service, String idService, String name, String token, String username, String status, boolean main, String usernameMain) {
        this.service = service;
        this.idService = idService;
        this.name = name;
        this.token = token;
        this.username = username;
        this.status = status;
        this.main = main;
        this.usernameMain = usernameMain;
    }

    public String getService() {
        return service;
    }

    public String getIdService() {
        return idService;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public boolean isMain() {
        return main;
    }

    public String getUsernameMain() {
        return usernameMain;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public void setUsernameMain(String usernameMain) {
        this.usernameMain = usernameMain;
    }
}
