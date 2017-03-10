package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class AVT implements Attribute {
	
	private double delta;
	private double cValue;
	
	private String name;

	public AVT(String name, double delta, double cValue) {
		super();
		this.delta = delta;
		this.cValue = cValue;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String type() {
		return "AVT";
	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeToDraw() {
		// TODO Auto-generated method stub
		return null;
	}

}
