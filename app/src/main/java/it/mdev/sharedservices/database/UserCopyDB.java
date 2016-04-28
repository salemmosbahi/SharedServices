package it.mdev.sharedservices.database;

/**
 * Created by salem on 21/04/16.
 */
public class UserCopyDB {
    private String token, username, age, service, idService;

    public UserCopyDB(String token, String username, String age, String service, String idService) {
        this.token = token;
        this.username = username;
        this.age = age;
        this.service = service;
        this.idService = idService;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getAge() {
        return age;
    }

    public String getService() {
        return service;
    }

    public String getIdService() {
        return idService;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }
}
