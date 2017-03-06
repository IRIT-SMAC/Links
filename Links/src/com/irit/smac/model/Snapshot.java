package com.irit.smac.model;

import java.util.ArrayList;

/**
 * A Snapshot is used to model the current state of a set of agents and their
 * relations. A Snaphost is then composed of a list of agents and a list of
 * relations between those agents.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 *
 */
public class Snapshot {

	/**
	 * The agent list.
	 */
	private ArrayList<Agent> agentsList = new ArrayList<Agent>();

	/**
	 * The agents relations.
	 */
	private ArrayList<Relation> agentsRelations = new ArrayList<Relation>();

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
	public ArrayList<Agent> getAgentsList() {
		return agentsList;
	}

	/**
	 * Get the agents relations.
	 * 
	 * @return An ArrayList containing the agent relation. May be empty if there
	 *         is no relation on the Snapshot.
	 */
	public ArrayList<Relation> getAgentsRelations() {
		return agentsRelations;
	}

	/**
	 * Add a new agent to the Snapshot.
	 * 
	 * @param name
	 *            The name of the agent. Must be unique.
	 * @param type
	 *            The type of the agent (used to determine its css class).
	 */
	public void addAgent(String name, String type) {
		Agent a = new Agent(name, type, this);
		if (!agentsList.contains(a)) {
			agentsList.add(a);
		}
	}

	/**
	 * Test if an agent in the agent list has the name passed in parameter.
	 * 
	 * @param name
	 *            The name of the agent to seek for.
	 * @return True if an agent is found, false otherwise.
	 */
	public boolean containsAgent(String name) {
		for (Agent a : agentsList) {
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
	public Agent getAgent(String A) {
		for (Agent a : agentsList) {
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
	 *            True if the relation is directional (from A->B), false if the
	 *            relation is bidirectional (A<->B).
	 * @param type
	 *            The type of the relation (used to determine its class in the
	 *            css).
	 */
	public void addRelation(String A, String B, String name, boolean isDirectional, String type) {
		if (containsAgent(A) && containsAgent(B)) {
			Relation a = new Relation(getAgent(A), getAgent(B), name, type, isDirectional, this);
			if (!agentsRelations.contains(a)) {
				agentsRelations.add(a);
			}
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
		for (Relation r : this.agentsRelations) {
			if (r.getName().equals(nodeName)) {
				return r;
			}
		}
		return null;
	}

}
