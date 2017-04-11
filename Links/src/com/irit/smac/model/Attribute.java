package com.irit.smac.model;

public abstract class Attribute {

	/**
	 * This enum describes the type of graphic to draw with this attribute.
	 * 
	 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
	 *
	 */
	public enum AttributeStyle {
		LINEAR, BAR, AVRT, AVT, STRING;
	};

	/**
	 * This enum describes the type of attribute.
	 * 
	 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
	 *
	 */
	public enum AttributeType {
		AVRT, AVT, STRING, DOUBLE;
	};

	private Object value;

	private String name;

	/**
	 * Construct a new attribute
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            Its value.
	 */
	public Attribute(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get the name of the attribute.
	 * 
	 * @return The name of the attribute.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the type of the attribute.
	 * 
	 * @return The type of the attribute.
	 */
	public abstract String getType();

	public Object getValue() {
		return value;
	}

	/**
	 * Set the value of the attribute.
	 * 
	 * @param value
	 *            The value of the attribute.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	public abstract AttributeStyle getTypeToDraw();

}