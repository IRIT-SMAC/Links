package com.irit.smac.core;

import java.io.Serializable;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import com.irit.smac.model.Agent;
import com.irit.smac.model.Relation;
import com.irit.smac.model.Snapshot;
import com.irit.smac.model.SnapshotsCollection;

public class DisplayedGraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7697345043271592254L;
	private Graph graph;
	private SnapshotsCollection snapColl;
	private long currentSnapNumber;

	public DisplayedGraph(SnapshotsCollection snapColl, String linkToCss) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new MultiGraph("embedded");
		graph.addAttribute("ui.stylesheet", "url('" + linkToCss + "')");
		currentSnapNumber = 0;
		this.snapColl = snapColl;
	}

	public Graph getGraph() {
		return graph;
	}

	public void loadGraph(long snapNumber) {
		System.err.println(snapNumber);
		currentSnapNumber = snapNumber;
		Snapshot s = snapColl.getSnaptshot(snapNumber);

		/* Retrait des noeuds */
		Iterator<Node> it = graph.getNodeIterator();
		while (it.hasNext()) {
			String nodeName = it.next().getId();
			if (s.getAgent(nodeName) == null) {
				graph.removeNode(nodeName);
			}
		}

		/* Retrait des liens */
		Iterator<Edge> it2 = graph.getEdgeIterator();
		while (it2.hasNext()) {
			String nodeName = it2.next().getId();
			if (s.getRelation(nodeName) == null) {
				graph.removeEdge(nodeName);
			}
		}

		/* Ajout des noeuds */
		for (Agent a : s.getAgentsList()) {
			Node n = graph.getNode(a.getName());
			if (n == null) {
				graph.addNode(a.getName());
				graph.getNode(a.getName()).addAttribute("ui.class", a.getType());
				graph.getNode(a.getName()).addAttribute("ui.label", a.getName());
			} else {
				if (!n.getAttribute("ui.class").equals(a.getType())) {
					n.setAttribute("ui.class", a.getType());
				}
			}
		}

		/* Ajout des liens */
		for (Relation r : s.getAgentsRelations()) {
			if (graph.getEdge(r.getName()) == null) {
				graph.addEdge(r.getName(), r.getA().getName(), r.getB().getName(), r.isDirectional());
				graph.getEdge(r.getName()).addAttribute("ui.class", r.getType());
			} else {
				if (!graph.getEdge(r.getName()).getAttribute("ui.class").equals(r.getType())) {
					graph.getEdge(r.getName()).setAttribute("ui.class", r.getType());
				}
			}
		}
	}

	public void addNode(String name, String type) {
		Node n = graph.getNode("name");
		if (n == null) {
			graph.addNode("name");
			n = graph.getNode("name");
			n.addAttribute("type", type);
		}
	}

	public boolean containsSnap(long number) {
		return this.snapColl.containsSnap(number);
	}

	public SnapshotsCollection getCurrentSnap() {
		return snapColl;
	}

	public SnapshotsCollection getSnapCol() {
		return snapColl;
	}

	public void refresh(String agent, String type) {
		graph.getNode(agent).addAttribute("ui.class", type);
		for (Edge e : graph.getNode(agent).getLeavingEdgeSet()) {
			if (!type.equals("Targeted")) {
				e.addAttribute("ui.class",
						this.snapColl.getSnaptshot(this.currentSnapNumber).getRelation(e.getId()).getType());
			} else {
				e.addAttribute("ui.class", "Targeted");
			}
		}
	}
}
