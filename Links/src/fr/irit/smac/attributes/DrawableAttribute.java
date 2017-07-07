package fr.irit.smac.attributes;

import fr.irit.smac.model.Attribute;

/**
 * 
 * @author Nicolas Verstaevel
 *
 *Class who define for an attribute how it will be draw
 *
 */
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

	/**
	 * 
	 * @return type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return caracList
	 */
	public String getCaracList(){
		return caracList;
	}

	/**
	 * 
	 * @return t
	 */
	public Attribute getAttribute() {
		return t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caracList == null) ? 0 : caracList.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((t == null) ? 0 : t.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DrawableAttribute other = (DrawableAttribute) obj;
		if (caracList == null) {
			if (other.caracList != null)
				return false;
		} else if (!caracList.equals(other.caracList))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (t == null) {
			if (other.t != null)
				return false;
		} else if (!t.equals(other.t))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}