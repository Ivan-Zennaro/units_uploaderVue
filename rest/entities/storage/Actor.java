package org.example.rest.entities.storage;
import org.example.rest.entities.proxies.ActorProxy;
import org.example.rest.utils.Roles;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;



@Entity
public class Actor {

    private static final String defaultPath = "https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorImage/default.jpg";
    private static final String defaultPathAdmin = "https://storage.googleapis.com/progweb2020-272915.appspot.com/ActorImage/defaultAdmin.jpg";

    @Id
    private String username;
    private String email;
    private String name;
    private String password;
    private Roles role;
    private String imgPath;

    public Actor(){}

    public Actor(String username, String email, String name, String password, Roles role) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        if (role.equals(Roles.ADMIN))
            this.imgPath = defaultPathAdmin;
        else this.imgPath = defaultPath;
    }

    public Actor (ActorProxy actorProxy){
        this.email = actorProxy.getEmail();
        this.name = actorProxy.getName();
        this.username = actorProxy.getUsername();
        switch (actorProxy.getRole().toUpperCase()){
            case "CONSUMER": this.role = Roles.CONSUMER; break;
            case "UPLOADER":  this.role = Roles.UPLOADER; break;
            case "ADMIN": this.role = Roles.ADMIN; break;
        }

        if (actorProxy.getRole().toUpperCase().equals("ADMIN"))
            this.imgPath = defaultPathAdmin;
        else this.imgPath = defaultPath;
    }

    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Roles getRole() {
        return role;
    }
    public void setRole(Roles role) {
        this.role = role;
    }

}
