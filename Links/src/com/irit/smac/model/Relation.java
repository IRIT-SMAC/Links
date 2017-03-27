package com.irit.smac.model;

import java.io.Serializable;

public class Relation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4939449662544024687L;
	private Agent A;
	private Agent B;
	private String name;
	private String type;
	private Snapshot s;

	private boolean isDirectional;

	public Relation(Agent a, Agent b,String name, String type, boolean directional, Snapshot s) {
		A = a;
		B = b;
		this.name = name;
		this.isDirectional = directional;
		this.s = s;
		this.type = type;
	}
	
	public String getName(){
		return name;
	}

	public Agent getA() {
		return A;
	}

	public Agent getB() {
		return B;
	}

	public boolean equals(Relation obj) {
		if (type.equals(getType())) {
			if (A.equals(obj.getA()) && B.equals(obj.getB())) {
				return true;
			} else {
				if (isDirectional) {
					if (B.equals(obj.getA()) && A.equals(obj.getB())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getType() {
		return type;
	}

	public boolean isDirectional() {
		return isDirectional;
	}

}
