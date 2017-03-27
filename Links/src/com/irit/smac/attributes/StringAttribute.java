package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

public class StringAttribute implements Attribute,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6726345506370602566L;

	private String name;
	
	private String value;
	
	public StringAttribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String type() {
		return "string";
	}
	
	public String toString(){
		return "["+name+"] String:= " + value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String getTypeToDraw() {
		return "String";
	}

}
