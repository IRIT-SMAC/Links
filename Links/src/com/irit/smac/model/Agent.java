package com.irit.smac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Agent: This class models an agent. It possesses a type (used to determine its
 * ui.class) and a list of Attributes.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 29/03/2017
 *
 */
public class Agent implements Serializable {

	/**
	 * Auto-Genearted UID.
	 */
	private static final long serialVersionUID = 1498345709452103648L;
	/**
	 * The agent name.
	 */
	private String name;
	/**
	 * The special type of the agent (use to target an agent on the view).
	 */
	private String stype;
	/**
	 * The agent type.
	 */
	private String ctype;
	/**
	 * A reference to the snapshot the agent belongs to.
	 */
	private Snapshot s;

	/**
	 * The list of attributes.
	 */
	private HashMap<String, ArrayList<Attribute>> attributes = new HashMap<String, ArrayList<Attribute>>();

	/**
	 * Construct un agent.
	 * 
	 * @param name
	 *            Its name.
	 * @param type
	 *            Its type.
	 * @param s
	 *            The snapshot it belongs to.
	 */
	public Agent(String name, String type, Snapshot s) {
		this.name = name;
		this.stype = type;
		this.ctype = type;
		this.s = s;
	}

	/**
	 * Get the agent name.
	 * 
	 * @return Its name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the agent type.
	 * 
	 * @return Its type.
	 */
	public String getType() {
		return ctype;
	}

	/**
	 * Get the attribute list.
	 * 
	 * @return The attribute list.
	 */
	public HashMap<String, ArrayList<Attribute>> getAttributes() {
		return attributes;
	}

	/**
	 * Add a list of attribute.
	 * 
	 * @param name
	 *            The name of the list.
	 * @param list
	 *            The list of attributes.
	 * @deprecated
	 */
	public void addAttribute(String name, ArrayList<Attribute> list) {
		attributes.put(name, list);
	}

	/**
	 * Add one attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param t
	 *            The attribute to add.
	 */
	public void addOneAttribute(String attributeListName, Attribute t) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(t);
	}

	/**
	 * Compares if two agent are equals.
	 * 
	 * @param obj
	 *            The agent to be compared with.
	 * @return True if they have the same name, false otherwise.
	 */
	public boolean equals(Agent obj) {
		return name.equals(obj.getName()) && stype.equals(obj.getType());
	}

	/**
	 * Get an attribute with its name.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @return The attributed if found. null otherwise.
	 */
	public Attribute getAttributesWithName(String name) {
		for (String key : attributes.keySet()) {
			for (Attribute a : attributes.get(key)) {
				if (name.equals(a.getName())) {
					return a;
				}
			}
		}
		return null;
	}

	/**
	 * Determine if the agent is targeted by the vizualisation.
	 * 
	 * @param b
	 *            True if it is, false otherwise.
	 */
	public void isTargeted(boolean b) {
		if (b) {
			ctype = "Targeted";
		} else {
			ctype = stype;
		}
	}

}
