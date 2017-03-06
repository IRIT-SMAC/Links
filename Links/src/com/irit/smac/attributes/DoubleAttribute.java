package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class DoubleAttribute implements Attribute {

	private String name;
	
	public double value;
	
	public DoubleAttribute(String name, double value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String type() {
		return "double";
	}
	

	public String toString(){
		return "["+name+"] Double:= " + value;
	}

}
