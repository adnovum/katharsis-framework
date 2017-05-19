package io.katharsis.core.resource.annotations;

/**
 * Defines the serialization strategy for a document(s) relationship field.
 * There are two things to consider. Whether related resources should be added 
 * to the ``include`` section of the response document. And whether the id of 
 * related resources should be serialized along with the document in the
 * corresponding ``relationships.[name].data`` section. 
 *
 * @see JsonApiRelation
 * @since 3.0
 */
public enum SerializeType {
	/**
	 * Defines that relationship document(s) are lazily serialized by default, meaning
	 * when explicitly requested by the ``include`` URL parameter.
	 */
	LAZY,
	/**
	 * Defines that only relationship document(s) id(s) are serialized.
	 * An inclusion can be requested with the the ``include`` URL parameter.
	 */
	ONLY_ID,
	/**
	 * Defines to always fully serialize relationship document(s), both as ID and as inclusion.
	 */
	EAGER
}
