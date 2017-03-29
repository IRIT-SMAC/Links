package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

public class AVRT implements Attribute,Serializable {

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

	@Override
	public Object getValue() {
		Double tab [] = new Double[6];
		tab[0] = lowerValue;
		tab[1] = down.cValue;
		tab[2] = down.delta;
		tab[3] = up.cValue;
		tab[4] = up.delta;
		tab[5] = upperValue;
		return tab;
	}
	
	public String toString(){
		return "["+name+"] AVRT:= " +lowerValue + " | " + down.cValue + " | " + down.delta + " | " + up.cValue + " | " + up.delta + " | " + upperValue;
	}

	@Override
	public String getTypeToDraw() {
		return "AVRT";
	}

}
