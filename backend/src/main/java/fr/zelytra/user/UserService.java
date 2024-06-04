package fr.zelytra.user;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {

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
}
