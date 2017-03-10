package com.irit.smac.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Agent {
	
	private String name;
	private String stype;
	private String ctype;
	private Snapshot s;
	
	private HashMap<String, ArrayList<Attribute>> attributes = new HashMap<String,ArrayList<Attribute>>();
	
	public Agent(String name, String type, Snapshot s){
		this.name = name;
		this.stype = type;
		this.ctype = type;
		this.s = s;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return ctype;
	}

	public HashMap<String, ArrayList<Attribute>> getAttributes() {
		return attributes;
	}
	
	public void addAttribute(String name, ArrayList<Attribute> list){
		attributes.put(name, list);
	}

	public boolean equals(Agent obj) {
		return name.equals(obj.getName()) && stype.equals(obj.getType());
	}

	public Attribute getAttributesWithName(String name) {
		for(String key : attributes.keySet()){
			for(Attribute a: attributes.get(key) ){
				if(name.equals(a.getName())){
					return a;
				}
			}
		}
		return null;
	}

	public void isTargeted(boolean b) {
		if(b){
			ctype = "Targeted";
		}else{
			ctype = stype;
		}
	}
	
	

}
