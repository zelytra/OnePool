package fr.zelytra.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "user_pool")
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
    private LocalDateTime createdAt;

    @Column(columnDefinition = "integer")
    private int pp;

    @Column(columnDefinition = "integer")
    @JsonIgnore
    private int gamePlayed;

    public UserEntity() {
    }

    public UserEntity(String username){
        Log.info("New user created : " + username);
        this.pp = 1200;
        this.gamePlayed = 0;
        this.authUsername=username;
        this.username=username;
        this.online=true;
    }

    public UserEntity(UserEntity user) {
        this.authUsername = user.authUsername;
        this.username = user.username;
        this.icon = user.icon;
        this.online = user.online;
        this.createdAt = user.createdAt;
        this.pp = user.pp;
        this.gamePlayed = user.gamePlayed;
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

    public int getGamePlayed() {
        return gamePlayed;
    }

    public void setGamePlayed(int gamePlayed) {
        this.gamePlayed = gamePlayed;
    }
}
