package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

/**
 * This class models a double as an attribute.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * 
 *
 */
public class DoubleAttribute extends Attribute implements Serializable {

	private double value;

	private AttributeStyle myStyle = AttributeStyle.LINEAR;

	/**
	 * Construct a double as an attribute.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 */
	public DoubleAttribute(String name, double value) {
		super(name,value);
		this.setValue(value);
	}

	/**
	 * Construct a double as an attribute an precise its drawing style.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @param style
	 *            The style of the attribute : "linear" or "bar".
	 */
	public DoubleAttribute(String name, Double value, AttributeStyle style) {
		super(name,value);
		this.setValue(value);
		myStyle = style;
	}

	@Override
	public String getType() {
		return "double";
	}

	@Override
	public String toString() {
		return "[" + this.getName() + "] Double:= " + value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * Change the style of the graph.
	 * 
	 * @param s
	 *            The new style.
	 */
	public void setTypeToDraw(AttributeStyle s) {
		myStyle = s;
	}

	@Override
	public AttributeStyle getTypeToDraw() {
		return myStyle;
	}

}
