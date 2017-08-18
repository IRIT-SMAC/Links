package fr.irit.smac.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import fr.irit.smac.model.Attribute;
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
	private Map<Entity,Double> mapValues;

	/**
	 * Create a new DisplayedGraph.
	 * @param snapColl The reference to the snapshot collection. 
	 * @param linkToCss The link to the graphstream css file.
	 * @param layout 
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
	public synchronized void loadGraph(long snapNumber) {
		mapValues = new HashMap<Entity,Double>();
		currentSnapNumber = snapNumber;
		Snapshot s = snapColl.getSnaptshot(snapNumber);
		
		if (s != null) {
			removeEntitiesToGraphFromSnapshot(s);
			addEntitiesToGraphView(s);
		}
		refreshColor();
	}

	/**
	 * All entities exiting in snapshot but not found in graph  are created.
	 * */
	private void addEntitiesToGraphView(Snapshot s) {
		/* Ajout des noeuds */
		for (Entity snapshotEntity : s.getEntityList()) {
			Node gsNode = graph.getNode(snapshotEntity.getName());
			//new node in graph
			if (gsNode == null) {
				gsNode = graph.addNode(snapshotEntity.getName());
				gsNode.addAttribute("ui.label", snapshotEntity.getName());
				gsNode.addAttribute("ui.class", snapshotEntity.getType());
				setNodeAttributes(snapshotEntity, gsNode);
			} else {
				//attributes update
				gsNode.addAttribute("ui.class", snapshotEntity.getType());
				setNodeAttributes(snapshotEntity, gsNode);
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


	private void setNodeAttributes(Entity snapshotEntity, Node gsNode) {
		
		for(String str : snapshotEntity.getAttributes().keySet()){
			for(Attribute att : snapshotEntity.getAttributes().get(str)){
				if(att.getValue() instanceof Double){
					/*if(((Double)att.getValue()) > 100)
						graph.getNode(a.getName()).addAttribute("ui.color", 1);
					else
						graph.getNode(a.getName()).addAttribute("ui.color", ((Double)att.getValue())/100);	*/
					mapValues.put(snapshotEntity, (Double)att.getValue());
				}
			}
		}
		
		//if the entity position isn't the default value => it's the user position ! => the auto layout not move it ! 
		if(snapshotEntity.getCoorX() != -10000.0 && snapshotEntity.getCoorY() != -10000.0){
			gsNode.addAttribute("layout.frozen");
			gsNode.setAttribute("x", snapshotEntity.getCoorX());
			gsNode.setAttribute("y", snapshotEntity.getCoorY());
		}
	}

	/**
	 * All entities exiting in graph but not found in snapshot are removed.
	 * */
	private void removeEntitiesToGraphFromSnapshot(Snapshot s) {
		/* Retrait des noeuds */
		Iterator<Node> it = graph.getNodeIterator();
		List<String> toRemove = new ArrayList<String>();
		while (it.hasNext()) {
			String nodeName = it.next().getId();
			if (s.getEntity(nodeName) == null) {
				toRemove.add(nodeName);
			}
		}
		for(String str : toRemove){
			graph.removeNode(str);
		}

		/* Retrait des liens */
		Iterator<Edge> it2 = graph.getEdgeIterator();
		toRemove = new ArrayList<String>();
		while (it2.hasNext()) {
			String nodeName = it2.next().getId();
			if (s.getRelation(nodeName) == null) {
				toRemove.add(nodeName);
			}
		}
		for(String str : toRemove){
			graph.removeEdge(str);
		}
	}

	public boolean viewSnapshot(Snapshot s){
		boolean ret = true;
		if (s != null) {
			removeEntitiesToGraphFromSnapshot(s);

			/* Ajout des noeuds */
			for (Entity snapshotEntity : s.getEntityList()) {
				Node gsNode = graph.getNode(snapshotEntity.getName());
				if (gsNode == null) {
					gsNode = graph.addNode(snapshotEntity.getName());
					gsNode.addAttribute("ui.label", snapshotEntity.getName());
					gsNode.addAttribute("ui.class", snapshotEntity.getType());
					setNodeAttributes(snapshotEntity, gsNode);
		
				} else {
					if (!gsNode.getAttribute("ui.class").equals(snapshotEntity.getType())) {
						gsNode.setAttribute("ui.class", snapshotEntity.getType());
					}
					setNodeAttributes(snapshotEntity, gsNode);
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
		refreshColor();
		graph.display();
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

	/**
	 * Refresh the color and the size of each node
	 */
	private synchronized void refreshColor(){
		SortedSet<Double> set = new TreeSet<Double>();
		set.addAll(this.mapValues.values());
		if(set.size() > 0){
			double quo = 100/set.size();
			double size = 30/set.size()+5;
			for(Entity a : this.mapValues.keySet()){
				double ind = 0;
				boolean found = false;
				for(Double d : set){
					if(this.mapValues.get(a) == d && !found){
						Node na = graph.getNode(a.getName());
						na.setAttribute("ui.color", ind*quo/100.0);
						na.setAttribute("ui.size", ind*size);
						found = true;
					}
					else
						ind++;
				}
			}
		}
	}

}
