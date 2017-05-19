package io.katharsis.core.engine.properties;

import io.katharsis.core.engine.internal.utils.StringUtils;

/**
 * Just an empty properties provider. Always returns an empty String.
 */
public class EmptyPropertiesProvider implements PropertiesProvider {
    @Override
    public String getProperty(String key) {
        return StringUtils.EMPTY;
    }
}
