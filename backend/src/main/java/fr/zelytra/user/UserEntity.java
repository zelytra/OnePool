package fr.zelytra.user;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Table(name = "user")
public class UserEntity extends PanacheEntityBase {

    @Id
    private long id;

    @Column(columnDefinition = "text")
    private String username;

    @Column(columnDefinition = "text")
    private String icon;

    @Column(columnDefinition = "boolean")
    private boolean online;

    @Column(columnDefinition = "date")
    public Date creationDate;

    @Column(columnDefinition = "integer")
    public int pp;

    public UserEntity() {
    }

    public long getId() {
        return id;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getPp() {
        return pp;
    }

    public void setPp(int pp) {
        this.pp = pp;
    }
}
