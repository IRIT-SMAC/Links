package com.irit.smac.core;

import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import com.irit.smac.model.Agent;
import com.irit.smac.model.Relation;
import com.irit.smac.model.Snapshot;
import com.irit.smac.model.SnapshotsCollection;

public class DisplayedGraph {
	private Graph graph;
	private SnapshotsCollection snapColl;

	public DisplayedGraph(SnapshotsCollection snapColl) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new MultiGraph("embedded");
		String s = DisplayedGraph.class.getResource("/graphStream.css").toString();
		graph.addAttribute("ui.stylesheet", "url('" + s + "')");

		this.snapColl = snapColl;
	}

	public Graph getGraph() {
		return graph;
	}

	public void loadGraph(long snapNumber) {
		Snapshot s = snapColl.getSnaptshot(snapNumber);
		;

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
			if (graph.getNode(a.getName()) == null) {
				graph.addNode(a.getName());
				graph.getNode(a.getName()).addAttribute("ui.class", a.getType());
				graph.getNode(a.getName()).addAttribute("ui.label", a.getName());
			}
		}

		/* Ajout des liens */
		for (Relation r : s.getAgentsRelations()) {
			if (graph.getEdge(r.getName()) == null) {
				graph.addEdge(r.getName(), r.getA().getName(), r.getB().getName(), r.isDirectional());
				graph.getEdge(r.getName()).addAttribute("ui.class", r.getType());
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
}
