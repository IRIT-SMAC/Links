package com.irit.smac.attributes;

import com.irit.smac.model.Attribute;

public class DrawableAttribute {

	public enum Type {
		ENTITY, RELATION;
	};

	private Type type;

	private Attribute t;

	private String name;

	private String caracList;

	public DrawableAttribute(Type type, String entityName, String caracList, Attribute t) {
		this.type = type;
		this.name = entityName;
		this.caracList = caracList;
		this.t = t;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public String getCaracList(){
		return caracList;
	}

	public Attribute getAttribute() {
		return t;
	}
}