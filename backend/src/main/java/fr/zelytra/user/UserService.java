package fr.zelytra.user;

import fr.zelytra.poolpoint.UserLeaderBoardPosition;
import fr.zelytra.user.reflections.LeaderBoardUser;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class UserService implements PanacheRepository<UserEntity> {

    @PersistenceContext
    EntityManager em;

    public UserEntity getOrCreateUserByName(String username) {
        UserEntity user = UserEntity.findById(username);

        //Creating user if it not exists
        if (user == null) {
            user = new UserEntity(username);
        }

        return user;
    }

    public UserEntity getUserByName(String username) {
        return UserEntity.findById(username);
    }

    public List<LeaderBoardUser> getUsersOrderByPoolPoint() {
        return UserEntity.find("ORDER BY pp").page(0, 100).project(LeaderBoardUser.class).list();
    }

    public UserLeaderBoardPosition getUsersLeaderboardPosition(String username) {
        UserEntity userEntity = getUserByName(username);
        // Count the number of users with higher pp
        Long position = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.pp > :userPp", Long.class)
                .setParameter("userPp", userEntity.getPp())
                .getSingleResult();
        return new UserLeaderBoardPosition(userEntity.getAuthUsername(), userEntity.getPp(), position + 1);
    }
}
