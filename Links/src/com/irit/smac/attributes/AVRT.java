package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class AVRT implements Attribute {

	public AVT up;
	public AVT down;
	public double upperValue;
	public double lowerValue;
	
	String name;
	
	public AVRT(String name, AVT up, AVT down, double upperValue, double lowerValue) {
		super();
		this.up = up;
		this.down = down;
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String type() {
		return "AVRT";
	}

}
