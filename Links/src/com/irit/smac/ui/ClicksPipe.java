package com.irit.smac.ui;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

public class ClicksPipe extends Thread implements ViewerListener{
	protected boolean loop = true;
	
	private Graph graph;
	private  Viewer viewer;
	private LinksApplication links;
	
	public ClicksPipe(Graph graph, Viewer viewer, LinksApplication links) {
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
		
	}

	public void buttonReleased(String id) {
		AgentVizFrame f = new AgentVizFrame(links.getDisplayedGraph().getCurrentSnap().getAgent(id,links.getCurrentSnapNumber()),links.getSnapCol(),links);
		links.registerObserver(f);
	}
}
