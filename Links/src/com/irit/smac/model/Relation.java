package com.irit.smac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Relation: This class models a relation between two agents. It possesses a type
 * (used to determine its ui.class) and a list of Attributes.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 29/03/2017
 *
 */
public class Relation implements Serializable {

	/**
	 * Auto-Generated UID.
	 */
	private static final long serialVersionUID = -4939449662544024687L;
	/**
	 * The reference to the the Agent A.
	 */
	private Entity A;
	/**
	 * The reference to the Agent B.
	 */
	private Entity B;
	/**
	 * The name of the relation (must be unique).
	 */
	private String name;
	/**
	 * The type of the relation (used to determine the ui.class).
	 */
	private String type;
	/**
	 * A reference to the snapshot it belongs to.
	 */
	private Snapshot s;

	/**
	 * The collection of attributes associated with this relation.
	 */
	private HashMap<String, ArrayList<Attribute>> attributes = new HashMap<String, ArrayList<Attribute>>();

	/**
	 * Boolean to determine if the relation is directed or bidirectional.
	 */
	private boolean isDirectional;

	/**
	 * Creates a new Relation between two agents A and B.
	 * 
	 * @param a
	 *            The reference to the agent A.
	 * @param b
	 *            The reference to the agent B.
	 * @param name
	 *            The name of the relation (must be unique)
	 * @param type
	 *            The type of the relation (used to determine its ui.class).
	 * @param directional
	 *            True if the relation is directional (A to B) false if it is
	 *            bidirectional (A--B).
	 * @param s
	 *            The reference to the snapshot the relation belongs to.
	 */
	public Relation(Entity a, Entity b, String name, String type, boolean directional, Snapshot s) {
		A = a;
		B = b;
		this.name = name;
		this.isDirectional = directional;
		this.s = s;
		this.type = type;
	}

	/**
	 * Get the name of the relation.
	 * 
	 * @return The name of the relation.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the reference to the agent A.
	 * 
	 * @return The reference to the agent A.
	 */
	public Entity getA() {
		return A;
	}

	/**
	 * Get the reference to the agent B.
	 * 
	 * @return The reference to the agent B.
	 */
	public Entity getB() {
		return B;
	}

	/**
	 * Add the specified attribute to the specified attribute list.
	 * 
	 * @param attributeListName
	 *            The name of the attribute list.
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
	 * Compares two relations to determine if they are equals
	 * (R1.A==R1.A and R1.B==R2.B and R1.isDirectional==R2.isDirectional)
	 * 
	 * @param obj
	 *            The relation to be compared with.
	 * @return True if they are equals, false otherwise.
	 */
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

	/**
	 * Get the type of the relation (used to determine the ui.class)
	 * 
	 * @return The type of the relation.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Determine if the relation is directional or not.
	 * 
	 * @return the isDirectional boolean.
	 */
	public boolean isDirectional() {
		return isDirectional;
	}

	public HashMap<String, ArrayList<Attribute>> getAttributes() {
		return attributes;
	}

}
