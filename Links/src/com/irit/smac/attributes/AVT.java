package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

/**
 * This class models an AVT as an attribute. For more information on AVRT, see
 * http://thesesups.ups-tlse.fr/3249/.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * 
 *
 */
public class AVT extends Attribute implements Serializable {
	
	private double delta;
	
	/**
	 * Construct an AVT as an attirubte.
	 * @param name The name of the AVT
	 * @param delta The delta value
	 * @param value The start value of the AVT
	 */
	public AVT(String name, double delta, double value) {
		super(name,value);
		this.delta = delta;
		this.setValue(value);
	}

	/**
	 * Get the delta value.
	 * @return The delta value.
	 */
	public double getDelta(){
		return delta;
	}
	
	@Override
	public String getType() {
		return "AVT";
	}

	@Override
	public Object getValue() {
		return this.delta;
	}
	
	@Override
	public String toString() {
		return "[" + this.getName() + "] AVT:= " + String.valueOf(delta) + ":" + this.getValue();
	}

	@Override
	public AttributeStyle getTypeToDraw() {
		return AttributeStyle.AVRT;
	}

}
