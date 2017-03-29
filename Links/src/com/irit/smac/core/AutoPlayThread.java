package com.irit.smac.core;

import java.io.Serializable;

import com.irit.smac.ui.LinksWindows;

public class AutoPlayThread extends Thread implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9207211192530365228L;

	private LinksWindows links;

	private boolean isActivated = false;

	public long frameRate = 1;

	public int frameSpeed = 500;

	public AutoPlayThread(LinksWindows linksApplication) {
		this.links = linksApplication;
		this.start();
	}

	public void setActivated(boolean isIt) {
		isActivated = isIt;
	}

	public void setFrameRateAndSpeed(int rate, int speed) {
		this.frameRate = rate;
		this.frameSpeed = speed;
	}

	public boolean getActivated() {
		return isActivated;
	}

	@Override
	public void run() {
		while (true) {
			if (isActivated) {
				if (this.frameRate > 0) {
					links.switchToSnap(Math.min((links.getCurrentSnapNumber() + this.frameRate),links.getMaxSnapNumber()-1));
				} else {
					links.switchToSnap(Math.max((links.getCurrentSnapNumber() + this.frameRate),1));
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
