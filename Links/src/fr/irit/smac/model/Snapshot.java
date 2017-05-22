package fr.irit.smac.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Snapshot is used to model the current state of a set of agents and their
 * relations. A Snaphost is then composed of a list of agents and a list of
 * relations between those agents.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 *
 */
public class Snapshot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1925330023989139722L;

	/**
	 * The agent list.
	 */
	private ArrayList<Entity> entityList = new ArrayList<Entity>();

	/**
	 * The agents relations.
	 */
	private ArrayList<Relation> relations = new ArrayList<Relation>();

	/**
	 * Construct a new empty Snapshot.
	 */
	public Snapshot() {
		super();

	}

	/**
	 * Get the agent list.
	 * 
	 * @return An ArrayList containing the agents. May be empty if there is no
	 *         agent on the Snapshot.
	 */
	public ArrayList<Entity> getEntityList() {
		return entityList;
	}

	/**
	 * Get the agents relations.
	 * 
	 * @return An ArrayList containing the agent relation. May be empty if there
	 *         is no relation on the Snapshot.
	 */
	public ArrayList<Relation> getRelations() {
		return relations;
	}

	/**
	 * Add a new agent to the Snapshot.
	 * 
	 * @param name
	 *            The name of the agent. Must be unique.
	 * @param type
	 *            The type of the agent (used to determine its css class).
	 * @return A reference to the newly created entity.
	 */
	public Entity addEntity(String name, String type) {
		Entity a = new Entity(name, type);
		if (!entityList.contains(a)) {
			entityList.add(a);
		}
		return a;
	}

	/**
	 * Test if an agent in the agent list has the name passed in parameter.
	 * 
	 * @param name
	 *            The name of the agent to seek for.
	 * @return True if an agent is found, false otherwise.
	 */
	public boolean containsEntity(String name) {
		for (Entity a : entityList) {
			if (a.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * Test if an agent in the agent list has the name passed in parameter.
	 * 
	 * @param name
	 *            The name of the agent to seek for.
	 * @return True if an agent is found, false otherwise.
	 */
	public boolean containsRelation(String name) {
		for (Relation a : relations) {
			if (a.getName().equals(name))
				return true;
		}
		return false;
	}

	/**
	 * Return a reference to an agent found by its name.
	 * 
	 * @param A
	 *            The name of the agent.
	 * @return A reference to the agent. May be null if no agent has been found.
	 */
	public Entity getEntity(String A) {
		for (Entity a : entityList) {
			if (a.getName().equals(A))
				return a;
		}
		return null;
	}

	/**
	 * Add a new relation between two agents in the Snapshot.
	 * 
	 * @param A
	 *            The name of the first agent.
	 * @param B
	 *            The name of the second agent.
	 * @param name
	 *            The name of the relation (must be unique).
	 * @param isDirectional
	 *            True if the relation is directional (from A to B), false if
	 *            the relation is bidirectional (A--B).
	 * @param type
	 *            The type of the relation (used to determine its class in the
	 *            css).
	 * @return A reference to the newly created relation.
	 */
	public Relation addRelation(String A, String B, String name, boolean isDirectional, String type) {
		if (containsEntity(A) && containsEntity(B)) {
			Relation a = new Relation(getEntity(A), getEntity(B), name, type, isDirectional);
			if (!relations.contains(a)) {
				relations.add(a);
			}
			return a;
		} else {
			return null;
		}
	}

	/**
	 * Return a relation found by its name.
	 * 
	 * @param nodeName
	 *            The name of the relation to seek for.
	 * @return A reference to the relation. May be null if no agent has been
	 *         found.
	 */
	public Relation getRelation(String nodeName) {
		for (Relation r : this.relations) {
			if (r.getName().equals(nodeName)) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Remove an entity from its name.
	 * 
	 * @param name
	 *            The name of the entity to remove.
	 * 
	 */
	public void removeEntity(String name) {
		Entity entity = null;
		for (Entity e : entityList) {
			if (e.getName().equals(name)) {
				entity = e;
			}
		}
		if (entity != null)
			entityList.remove(entity);
	}

	/**
	 * Remove a relation from its name.
	 * 
	 * @param name
	 *            The name of the relation to remove.
	 * 
	 */
	public void removeRelation(String name) {
		Relation entity = null;
		for (Relation e : relations) {
			if (e.getName().equals(name)) {
				entity = e;
			}
		}
		if (entity != null)
			relations.remove(entity);
	}

}
