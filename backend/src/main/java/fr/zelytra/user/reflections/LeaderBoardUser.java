package fr.zelytra.user.reflections;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record LeaderBoardUser(String username, String icon, int pp) {
}
