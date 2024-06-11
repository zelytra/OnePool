package fr.zelytra.poolpoint.simulation;

import fr.zelytra.user.UserEntity;
import jakarta.persistence.Entity;

@Entity
public class PoolSimulationUser extends UserEntity {

    private int weight;

    public PoolSimulationUser(String username,int weight) {
        super(username);
        this.weight = weight;
    }

    public PoolSimulationUser(PoolSimulationUser user) {
        super(user);
        weight = user.getWeight();
    }

    public PoolSimulationUser() {

    }

    public int getWeight() {
        return weight;
    }
}
