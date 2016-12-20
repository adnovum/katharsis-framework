package io.katharsis.example.spring.jersey.intf;


import io.katharsis.example.spring.jersey.model.Actor;
import io.katharsis.repository.ResourceRepositoryV2;

public interface ActorRepository extends ResourceRepositoryV2<Actor, String> {

}
