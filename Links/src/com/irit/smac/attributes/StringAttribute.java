package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class StringAttribute implements Attribute {

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

}
