package io.katharsis.core.resource.annotations;

/**
 * Defines the relationship look up strategy for a document(s) relationship field.
 *
 * @see JsonApiRelation
 * @since 3.0
 */
public enum LookupIncludeBehavior {
	/**
	 * Defines that relationship document is never called.
	 */
	NONE,
	/**
	 * Defines that relationship document is called if the field is null.
	 */
	AUTOMATICALLY_WHEN_NULL,
	/**
	 * Defines that relationship document is always called.
	 */
	AUTOMATICALLY_ALWAYS
}
