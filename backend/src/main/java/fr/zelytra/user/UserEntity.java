package fr.zelytra.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "'user'")
@Entity
public class UserEntity extends PanacheEntityBase {

    @Id
    @Column(columnDefinition = "text", name = "auth_username")
    private String authUsername;

    @Column(columnDefinition = "text")
    private String username;

    @Column(columnDefinition = "text")
    private String icon;

    @Column(columnDefinition = "boolean")
    private boolean online;

    @Column(columnDefinition = "date", name = "created_at")
    public LocalDateTime createdAt;

    @Column(columnDefinition = "integer")
    public int pp;

    public UserEntity() {
    }
    public UserEntity(String username){
        Log.info("New user created : " + username);
        this.pp = 100;//TODO Need to be modify when rating system is implemented
        this.authUsername=username;
        this.username=username;
        this.online=true;
        this.persistAndFlush();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LocalDateTime getCreationDate() {
        return createdAt;
    }

    public void setCreationDate(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }

}
