package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

public class StringAttribute extends Attribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6726345506370602566L;
	
	/**
	 * @param name Name of the attribute
	 * @param value Value
	 */
	public StringAttribute(String name, String value) {
		super(name,value);
		this.setValue(value);
	}

	@Override
	public String getType() {
		return "string";
	}
	
	public String toString(){
		return "["+this.getName()+"] String:= " + this.getValue();
	}

	@Override
	public AttributeStyle getTypeToDraw() {
		return AttributeStyle.STRING;
	}

}
