package com.irit.smac.core;

import java.io.Serializable;

import com.irit.smac.ui.LinksWindows;

/**
 * This Thread class enable to automatically control the view according to the
 * frame rate and speed selected in the view.
 * 
 * @author Nicolas Verstaevel - nicolas.verstaevel@irit.fr
 *
 */
public class AutoPlayThread extends Thread implements Serializable {

	private static final long serialVersionUID = -9207211192530365228L;

	private LinksWindows links;

	private boolean isActivated = false;

	private long frameRate = 1;

	private int frameSpeed = 500;

	/**
	 * Create a new AutoPlayThread.
	 * 
	 * @param linksApplication
	 *            The reference to the Link view.
	 */
	public AutoPlayThread(LinksWindows linksApplication) {
		this.links = linksApplication;
		this.start();
	}

	/**
	 * Activate or deactivate the thread.
	 * 
	 * @param isIt
	 *            True to activate, false otherwise.
	 */
	public void setActivated(boolean isIt) {
		isActivated = isIt;
	}

	/**
	 * Adjust frame rate and speed.
	 * 
	 * @param rate
	 *            The new frame rate.
	 * @param speed
	 *            The new frame speed.
	 */
	public void setFrameRateAndSpeed(int rate, int speed) {
		this.frameRate = rate;
		this.frameSpeed = speed;
	}

	/**
	 * Get the current state of the thread.
	 * 
	 * @return True if the thread is activated, false otherwise.
	 */
	public boolean getActivated() {
		return isActivated;
	}

	@Override
	public void run() {
		while (true) {
			if (isActivated) {
				if (this.frameRate > 0) {
					links.switchToSnap(
							Math.min((links.getCurrentSnapNumber() + this.frameRate), links.getMaxSnapNumber() - 1));
				} else {
					links.switchToSnap(Math.max((links.getCurrentSnapNumber() + this.frameRate), 1));
				}
				try {
					Thread.sleep(frameSpeed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
