package io.katharsis.core.engine.internal.dispatcher;

import java.util.Set;

import io.katharsis.core.engine.internal.dispatcher.controller.BaseController;

/**
 * Gets the instances of the {@link BaseController}'s.
 */
public interface ControllerLookup {

    /**
     * @return the instances of the {@link BaseController}'s.
     */
    Set<BaseController> getControllers();
}
