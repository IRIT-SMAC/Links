package fr.irit.smac.core;

import java.io.Serializable;
import java.util.Iterator;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import fr.irit.smac.model.Entity;
import fr.irit.smac.model.Relation;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.model.SnapshotsCollection;
/**
 * This class controls the graph vizualisation.
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 *
 */
public class DisplayedGraph implements Serializable {
	
	private static final long serialVersionUID = -7697345043271592254L;
	private Graph graph;
	private SnapshotsCollection snapColl;
	private long currentSnapNumber;

	/**
	 * Create a new DisplayedGraph.
	 * @param snapColl The reference to the snapshot collection. 
	 * @param linkToCss The link to the graphstream css file.
	 */
	public DisplayedGraph(SnapshotsCollection snapColl, String linkToCss) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new MultiGraph("embedded");
		graph.addAttribute("ui.stylesheet", "url('" + linkToCss + "')");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		currentSnapNumber = 0;
		this.snapColl = snapColl;
	}
	
	public void resetSnapNumber(){
		currentSnapNumber = 0;
		snapColl.resetNumber();
	}

	/**
	 * Get the current displayed graph.
	 * @return The reference to the displayed graph.
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Load a graph from the snapshot number. 
	 * @param snapNumber The number of the snapshot to be displayed.
	 */
	public synchronized boolean loadGraph(long snapNumber) {
		currentSnapNumber = snapNumber;
		Snapshot s = snapColl.getSnaptshot(snapNumber);
		boolean ret = true;
		if (s != null) {
			/* Retrait des noeuds */
			Iterator<Node> it = graph.getNodeIterator();
			while (it.hasNext()) {
				String nodeName = it.next().getId();
				if (s.getEntity(nodeName) == null) {
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
			for (Entity a : s.getEntityList()) {
				Node n = graph.getNode(a.getName());
				if (n == null) {
					graph.addNode(a.getName());
					graph.getNode(a.getName()).addAttribute("ui.class", a.getType());
					graph.getNode(a.getName()).addAttribute("ui.label", a.getName());
					if(a.getCoorX() != -10000 && a.getCoorY() != -10000){
						graph.getNode(a.getName()).setAttribute("x", a.getCoorX());
						graph.getNode(a.getName()).setAttribute("y", a.getCoorY());
						ret = false;
					}
				} else {
					if (!n.getAttribute("ui.class").equals(a.getType())) {
						n.setAttribute("ui.class", a.getType());
					}
					if(a.getCoorX() != -10000 && a.getCoorY() != -10000){
						graph.getNode(a.getName()).setAttribute("x", a.getCoorX());
						graph.getNode(a.getName()).setAttribute("y", a.getCoorY());
						ret = false;
					}
				}
			}

			/* Ajout des liens */
			for (Relation r : s.getRelations()) {
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
		return ret;
	}
	
	public boolean viewSnapshot(Snapshot s){
		boolean ret = true;
		if (s != null) {
			/* Retrait des noeuds */
			Iterator<Node> it = graph.getNodeIterator();
			while (it.hasNext()) {
				String nodeName = it.next().getId();
				if (s.getEntity(nodeName) == null) {
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
			for (Entity a : s.getEntityList()) {
				Node n = graph.getNode(a.getName());
				if (n == null) {
					graph.addNode(a.getName());
					graph.getNode(a.getName()).addAttribute("ui.class", a.getType());
					graph.getNode(a.getName()).addAttribute("ui.label", a.getName());
					if(a.getCoorX() != -10000 && a.getCoorY() != -10000){
						graph.getNode(a.getName()).setAttribute("x", a.getCoorX());
						graph.getNode(a.getName()).setAttribute("y", a.getCoorY());
						ret = false;
					}
				} else {
					if (!n.getAttribute("ui.class").equals(a.getType())) {
						n.setAttribute("ui.class", a.getType());
					}
					if(a.getCoorX() != -10000 && a.getCoorY() != -10000){
						graph.getNode(a.getName()).setAttribute("x", a.getCoorX());
						graph.getNode(a.getName()).setAttribute("y", a.getCoorY());
						ret = false;
					}
				}
			}

			/* Ajout des liens */
			for (Relation r : s.getRelations()) {
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
		return ret;
	}

	/**
	 * Add a node to the graph.
	 * @param name The name of the node (must be unique).
	 * @param type The type of the node (ui.class).
	 */
	public void addNode(String name, String type) {
		Node n = graph.getNode("name");
		if (n == null) {
			graph.addNode("name");
			n = graph.getNode("name");
			n.addAttribute("type", type);
		}
	}

	/**
	 * Get the snapshot collection. 
	 * @return The reference to the snapshot collection.
	 */
	public SnapshotsCollection getSnapCol() {
		return snapColl;
	}

	/**
	 * This method is called when an node is targeted.
	 * It refreshes the graph to redraw the node outgoing edges.
	 * @param node The name of the node.
	 * @param type The type of the node.
	 */
	public void refreshNeighbouring(String node, String type) {
		graph.getNode(node).addAttribute("ui.class", type);
		for (Edge e : graph.getNode(node).getLeavingEdgeSet()) {
			if (!type.equals("Targeted")) {
				e.addAttribute("ui.class",
						this.snapColl.getSnaptshot(currentSnapNumber).getRelation(e.getId()).getType());
			} else {
				e.addAttribute("ui.class", "Targeted");
			}
		}
	}
}
