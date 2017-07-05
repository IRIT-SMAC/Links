package fr.irit.smac.ui;

import java.io.Serializable;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import fr.irit.smac.model.Attribute.AttributeStyle;

public class ClicksPipe extends Thread implements ViewerListener, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6081772850762199542L;

	protected boolean loop = true;

	private Graph graph;
	private  Viewer viewer;
	private LinksWindows links;

	public ClicksPipe(Graph graph, Viewer viewer, LinksWindows links) {
		this.graph = graph;
		this.viewer = viewer;
		this.links = links;
		this.start();
	}


	public void run(){

		ViewerPipe fromViewer = viewer.newViewerPipe();
		fromViewer.addViewerListener(this);
		fromViewer.addSink(graph);

		while(loop) {
			try {
				Thread.sleep(10);
				fromViewer.pump(); 

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void viewClosed(String id) {
		loop = false;
	}

	public void buttonPushed(String id) {
		this.links.setMouseMove(true);
	}

	public void buttonReleased(String id) {
		if(this.links.getDrawing()){
			this.links.constructDraw(links.getDisplayedGraph().getSnapCol().getEntity(id,links.getCurrentSnapNumber()),null,100,true);
			//this.links.isDraw();
		}
		if(!this.links.getMoving()){
			AgentVizFrame f = new AgentVizFrame(links.getDisplayedGraph().getSnapCol().getEntity(id,links.getCurrentSnapNumber()),links.getSnapCol(),links);
			links.registerObserver(f);
		}
		this.links.setMouseMove(false);
	}
}
