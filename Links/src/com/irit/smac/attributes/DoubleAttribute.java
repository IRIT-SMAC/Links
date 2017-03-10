package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class DoubleAttribute implements Attribute {

	private String name;

	public double value;

	private String ttd;

	public DoubleAttribute(String name, double value) {
		super();
		this.name = name;
		this.value = value;
		ttd = "linear";
	}

	public DoubleAttribute(String string, Double i, String string2) {
		super();
		this.name = string;
		this.value = i;
		ttd = string2;
	}

	public String getName() {
		return name;
	}

	public String type() {
		return "double";
	}

	public String toString() {
		return "[" + name + "] Double:= " + value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setTypeToDraw(String s) {
		ttd = s;
	}

	@Override
	public String getTypeToDraw() {
		return ttd;
	}

}
