package fr.zelytra.friend;

import fr.zelytra.user.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "friend")
public class FriendEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @ManyToOne
    @JoinColumn(name = "user_id1", nullable = false)
    public UserEntity user1;

    @ManyToOne
    @JoinColumn(name = "user_id2", nullable = false)
    public UserEntity user2;

    @Column(name = "created_at")
    public LocalDateTime createdAt;

    public FriendEntity(){}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getUser2() {
        return user2;
    }

    public void setUser2(UserEntity user2) {
        this.user2 = user2;
    }

    public UserEntity getUser1() {
        return user1;
    }

    public void setUser1(UserEntity user1) {
        this.user1 = user1;
    }
}
