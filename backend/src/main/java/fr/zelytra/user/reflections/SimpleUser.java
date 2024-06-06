package fr.zelytra.user.reflections;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record SimpleUser(String authUsername, String username, String icon) {

}
