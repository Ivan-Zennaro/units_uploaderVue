package org.example.rest.entities.proxies;
import org.example.rest.entities.storage.Actor;

public class ActorProxy {

    private String username;
    private String email;
    private String name;
    private String role;
    private String imagePath;
    private String password;

    public ActorProxy(){}

    public ActorProxy(String email, String password,String name, String username, String role, String imagePath) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.username = username;
        this.role = role;
        this.imagePath = imagePath;
    }

    public ActorProxy(Actor actor){
        this.username = actor.getUsername();
        this.email = actor.getEmail();
        this.name = actor.getName();
        this.password = actor.getPassword();
        this.role = actor.getRole().toString();
        this.imagePath = actor.getImgPath();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
