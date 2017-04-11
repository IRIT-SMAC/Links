package com.irit.smac.attributes;

import java.io.Serializable;

import com.irit.smac.model.Attribute;

/**
 * This class models an AVRT as an attribute. For more information on AVRT, see
 * http://thesesups.ups-tlse.fr/3249/.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * 
 *
 */
public class AVRT extends Attribute implements Serializable {

	private AVT up;
	private AVT down;
	private double upperValue;
	private double lowerValue;

	/**
	 * Construct an AVRT attribute.
	 * 
	 * @param name
	 *            The name of the AVRT
	 * @param up
	 *            The upper AVT.
	 * @param lower
	 *            The lower AVT
	 * @param upperValue
	 *            The maximum upper bound value.
	 * @param lowerValue
	 *            The minimum lower bound value.
	 */
	public AVRT(String name, AVT up, AVT lower, double upperValue, double lowerValue) {
		super(name, null);
		this.up = up;
		this.down = lower;
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
	}

	@Override
	public String getType() {
		return "AVRT";
	}

	@Override
	public Object getValue() {
		Double tab[] = new Double[6];
		tab[0] = lowerValue;
		tab[1] = (Double) down.getValue();
		tab[2] = down.getDelta();
		tab[3] = (Double) up.getValue();
		tab[4] = up.getDelta();
		tab[5] = upperValue;
		return tab;
	}

	@Override
	public String toString() {
		return "[" + this.getName() + "] AVRT:= " + String.valueOf(lowerValue) + ":" + String.valueOf(down.getValue()) + ":"
				+ String.valueOf(down.getDelta()) + ":" + String.valueOf(up.getValue()) + ":"
				+ String.valueOf(up.getDelta()) + ":" + String.valueOf(upperValue);
	}

	@Override
	public AttributeStyle getTypeToDraw() {
		return AttributeStyle.AVRT;
	}

}
