package fr.irit.smac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import fr.irit.smac.attributes.AVRTAttribute;
import fr.irit.smac.attributes.AVTAttribute;
import fr.irit.smac.attributes.DoubleAttribute;
import fr.irit.smac.attributes.StringAttribute;
import fr.irit.smac.model.Attribute.AttributeStyle;

/**
 * Agent: This class models an agent. It possesses a type (used to determine its
 * ui.class) and a list of Attributes.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 * @version 1.0
 * @since 29/03/2017
 *
 */
public class Entity implements Serializable {

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
	protected Entity(String name, String type, Snapshot s) {
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
	void addOneAttribute(String attributeListName, Attribute t) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(t);
	}

	/**
	 * Add one string attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param aname
	 *            The name of the attribute.
	 * @param s
	 *            The string attribute to add.
	 */
	public void addOneAttribute(String attributeListName, String aname, String s) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(new StringAttribute(aname, s));
	}

	/**
	 * Add one double attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param aname
	 *            The name of the attribute.
	 * @param d
	 *            The double attribute to add.
	 */
	public void addOneAttribute(String attributeListName, String aname, double d) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(new DoubleAttribute(aname, d));
	}

	/**
	 * Add one avt attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param aname
	 *            The name of the attribute.
	 * @param delta
	 *            The delta value of the attribute to add.
	 * @param cvalue
	 *            The current value of the attribute to add.
	 */
	public void addOneAttribute(String attributeListName, String aname, double delta, double cvalue) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(new AVTAttribute(aname, delta, cvalue));
	}

	/**
	 * Add one avrt attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param aname
	 *            The name of the attribute.
	 * @param updelta
	 *            The delta value of the upper AVT attribute to add.
	 * @param upcvalue
	 *            The current value of the upper AVT attribute to add.
	 * @param downdelta
	 *            The delta value of the lower AVT attribute to add.
	 * @param downcvalue
	 *            The current value of the lower AVT attribute to add.
	 * @param uppervalue
	 *            The upper value of the range.
	 * @param lowervalue
	 *            The lower value of the range.
	 */
	public void addOneAttribute(String attributeListName, String aname, double updelta, double upcvalue,
			double downdelta, double downcvalue, double uppervalue, double lowervalue) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName)
				.add(new AVRTAttribute(aname, new AVTAttribute(aname + "UpAVT", updelta, upcvalue),
						new AVTAttribute(aname + "DownAVT", downdelta, downcvalue), uppervalue, lowervalue));
	}
	
	/**
	 * Add one double attribute to the precised list.
	 * 
	 * @param attributeListName
	 *            The name of the list.
	 * @param aname
	 *            The name of the attribute.
	 * @param d
	 *            The double attribute to add.
	 * @param style
	 *            The drawing style of the attribute.
	 */
	public void addOneAttribute(String attributeListName, String aname, double d, AttributeStyle style) {
		if (!attributes.containsKey(attributeListName)) {
			attributes.put(attributeListName, new ArrayList<Attribute>());
		}
		attributes.get(attributeListName).add(new DoubleAttribute(aname, d, style));
	}

	/**
	 * Compares if two agent are equals.
	 * 
	 * @param obj
	 *            The agent to be compared with.
	 * @return True if they have the same name, false otherwise.
	 */
	public boolean equals(Entity obj) {
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
	public void setTargeted(boolean b) {
		if (b) {
			ctype = "Targeted";
		} else {
			ctype = stype;
		}
	}
	
	/**
	 * Set the type of the entity (use to determine ui.class)
	 * @param type The type of the entity.
	 */
	public void setType(String type){
		ctype = type;
	}

}
