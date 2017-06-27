package fr.irit.smac.model;

import java.io.Serializable;

public abstract class Attribute implements Serializable{

	/**
	 * This enum describes the type of graphic to draw with this attribute.
	 * 
	 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
	 *
	 */
	public enum AttributeStyle implements Serializable{
		LINEAR, BAR, AVRT, AVT, STRING, PIE;
	};

	/**
	 * This enum describes the type of attribute.
	 * 
	 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
	 *
	 */
	public enum AttributeType implements Serializable{
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attribute other = (Attribute) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}