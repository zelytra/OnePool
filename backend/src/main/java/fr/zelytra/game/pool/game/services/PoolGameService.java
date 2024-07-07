package fr.zelytra.game.pool.game.services;

import fr.zelytra.game.pool.game.PoolGameManager;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PoolGameService implements PanacheMongoRepository<PoolGameManager> {
}
