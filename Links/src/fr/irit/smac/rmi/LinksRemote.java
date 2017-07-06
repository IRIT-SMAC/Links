package fr.irit.smac.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import fr.irit.smac.core.Links;
import fr.irit.smac.model.Snapshot;
import fr.irit.smac.ui.LinksWindows;

/**
 * 
 * @author Marcillaud Guilhem
 * 
 * Interface with the methods who can be used with in RMI.
 *
 */
public interface LinksRemote extends Remote{

	/**
	 * Return the Object Links
	 * @return Links
	 * @throws RemoteException
	 */
	public Links getLinks() throws RemoteException;
	
	public void createNewLinksWindow(String xpName) throws RemoteException;
	
	public void runLinks() throws RemoteException;
	
	/**
	 * Build an experience
	 * @param xpName
	 * 			The name of the experience
	 * @throws RemoteException
	 */
	public void buildExperiment(String xpName) throws RemoteException;
	
	/**
	 * Drop an experience
	 * @param xpName
	 * 			The name of the experience
	 * @throws RemoteException
	 */
	public void dropExperiment(String xpName) throws RemoteException;
	
	/**
	 * Add a snapshot.
	 * @param s
	 * 		The snapshot to add.
	 * @param xpName
	 * 		The name of the experience.
	 * @throws RemoteException
	 */
	public void addSnapshot(Snapshot s, String xpName) throws RemoteException;
	
	
}
