package io.katharsis.core.engine.security;

public interface SecurityProvider {

	boolean isUserInRole(String role);
}
