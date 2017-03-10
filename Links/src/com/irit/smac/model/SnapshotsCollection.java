package com.irit.smac.model;

import java.util.HashMap;

import com.irit.smac.ui.LinksApplication;

public class SnapshotsCollection {

	private long maxNum = 1;

	private LinksApplication links;

	HashMap<Long, Snapshot> collection = new HashMap<Long, Snapshot>();

	public void setLinksWindows(LinksApplication links) {
		this.links = links;
	}

	public void addSnapshot(Snapshot s) {
		collection.put(maxNum, s);
		links.newSnap(maxNum);
		maxNum++;
	}

	public Snapshot getSnaptshot(long s) {
		return collection.get(s);
	}

	public boolean containsSnap(long number) {
		return collection.containsKey(number);
	}

	public Agent getAgent(String id, long snap) {
		if (collection.containsKey(snap)) {
			return collection.get(snap).getAgent(id);
		}
		
		return null;
	}

	public long getMaxNum() {
		return maxNum;
	}
}
