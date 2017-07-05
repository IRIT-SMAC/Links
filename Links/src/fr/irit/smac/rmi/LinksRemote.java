package fr.irit.smac.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.ui.LinksWindows;

public interface LinksRemote extends Remote{

	public Links getLinks() throws RemoteException;
	
	public void createNewLinksWindow(String xpName) throws RemoteException;
	
	public void runLinks() throws RemoteException;
	
	public void buildExperiment(String xpName) throws RemoteException;
	
	public void dropExperiment(String xpName) throws RemoteException;
	
	public void addSnapshot(Snapshot s, String xpName) throws RemoteException;
	
	
}
