package com.irit.smac.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;

import org.bson.Document;

import com.irit.smac.attributes.AVRT;
import com.irit.smac.attributes.AVT;
import com.irit.smac.attributes.DoubleAttribute;
import com.irit.smac.attributes.StringAttribute;
import com.irit.smac.core.Links;
import com.irit.smac.ui.LinksWindows;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class SnapshotsCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -718664063045743706L;

	private Integer maxNum = 1;

	private LinksWindows links;

	private MongoCollection<Document> collection = Links.database.getCollection(LinksWindows.xpName);

	private Snapshot currentSnap;

	public void setLinksWindows(LinksWindows links) {
		this.links = links;
	}

	public SnapshotsCollection() {
		Document myXP = collection.find(Filters.eq("xpName", LinksWindows.xpName)).first();
		if (myXP != null) {
			Iterator<Entry<String, Object>> it = myXP.entrySet().iterator();
			String _id = (String) it.next().toString();
			String xpName = (String) it.next().toString();
			this.maxNum = (Integer) it.next().getValue();
		}
	}

	public void addSnapshot(Snapshot s) {

		Document myXP = collection.find(Filters.eq("xpName", LinksWindows.xpName)).first();

		collection.deleteMany(Filters.eq("snapNum", maxNum));

		Document doc = new Document("snapNum", maxNum);
		Document caract;
		Document attributeList;
		Document relationCaract;

		for (Agent a : s.getAgentsList()) {
			caract = new Document("Type", "Agent").append("Name", a.getName()).append("Class", a.getType().toString());
			attributeList = new Document();
			for (String atName : a.getAttributes().keySet()) {
				for (Attribute t : a.getAttributes().get(atName)) {
					attributeList.append(t.getName(),
							new Document("TypeToDraw", t.getTypeToDraw()).append("toString", t.toString()));
				}
				caract.append(atName, attributeList);
			}
			doc.append(a.getName(), caract);
		}

		for (Relation a : s.getRelations()) {
			attributeList = new Document("Type", "Relation").append("RelationName", a.getName())
					.append("A", a.getA().getName()).append("B", a.getB().getName())
					.append("isDirectionnal", a.isDirectional()).append("Class", a.getType().toString());
			relationCaract = new Document();
			for (String atName : a.getAttributes().keySet()) {
				for (Attribute t : a.getAttributes().get(atName)) {
					relationCaract.append(t.getName(),
							new Document("TypeToDraw", t.getTypeToDraw()).append("toString", t.toString()));
				}
				attributeList.append(atName, relationCaract);
			}
			doc.append(a.getName(), attributeList);
		}
		collection.insertOne(doc);

		links.newSnap(maxNum);
		maxNum++;

		BasicDBObject newDocument = new BasicDBObject().append("$inc", new BasicDBObject().append("maxNum", 1));

		collection.findOneAndUpdate(new BasicDBObject().append("xpName", LinksWindows.xpName), newDocument);

	}

	public Snapshot getSnaptshot(long s) {
		/* recreate snapshot */
		Snapshot snap = new Snapshot();

		/* Récupération de la snapshot */
		Document myDoc = collection.find(Filters.eq("snapNum", s)).first();

		if (myDoc == null)
			return null;

		Iterator<Entry<String, Object>> it = myDoc.entrySet().iterator();

		String id = (String) it.next().getValue().toString();
		Integer snapNum = (Integer) it.next().getValue();

		while (it.hasNext()) {
			Document d = (Document) it.next().getValue();
			addToSnap(snap, d);
		}

		return snap;
	}

	private void addToSnap(Snapshot snap, Document d) {
		Iterator<Entry<String, Object>> it = d.entrySet().iterator();
		String type = (String) it.next().getValue();
		switch (type) {
		case "Agent":
			String name = (String) it.next().getValue();
			String uiClass = (String) it.next().getValue();
			Agent a = snap.addAgent(name, uiClass);

			while (it.hasNext()) {
				/* For any caracteristic list */
				Entry<String, Object> attributeList = it.next();
				String attListName = attributeList.getKey();
				Document list = (Document) attributeList.getValue();
				Iterator<Entry<String, Object>> entryList = list.entrySet().iterator();
				while (entryList.hasNext()) {
					/* For any caract in this list */
					Entry<String, Object> caract = entryList.next();
					String caracName = caract.getKey();
					Document value = (Document) caract.getValue();
					Iterator<Entry<String, Object>> myValues = value.entrySet().iterator();
					String typeToDraw = (String) myValues.next().getValue();
					String toString = (String) myValues.next().getValue();
					a.addOneAttribute(attListName, buildAttribute(caracName, typeToDraw, toString));
				}
			}

			break;

		case "Relation":
			name = (String) it.next().getValue();
			String A = (String) (String) it.next().getValue();
			String B = (String) it.next().getValue();
			boolean isDirectional = (boolean) it.next().getValue();
			uiClass = (String) it.next().getValue();

			Relation r = snap.addRelation(A, B, name, isDirectional, uiClass);

			while (it.hasNext()) {
				/* For any caracteristic list */
				Entry<String, Object> attributeList = it.next();
				String attListName = attributeList.getKey();
				Document list = (Document) attributeList.getValue();
				Iterator<Entry<String, Object>> entryList = list.entrySet().iterator();
				while (entryList.hasNext()) {
					/* For any caract in this list */
					Entry<String, Object> caract = entryList.next();
					String caracName = caract.getKey();
					Document value = (Document) caract.getValue();
					Iterator<Entry<String, Object>> myValues = value.entrySet().iterator();
					String typeToDraw = (String) myValues.next().getValue();
					String toString = (String) myValues.next().getValue();
					r.addOneAttribute(attListName, buildAttribute(caracName, typeToDraw, toString));
				}
			}
			break;
		}
	}

	private Attribute buildAttribute(String caracName, String typeToDraw, String toString) {
		Attribute t = null;
		if (toString.contains("Double")) {
			t = new DoubleAttribute(caracName,
					Double.valueOf(toString.substring(toString.indexOf("=") + 1, toString.length() - 1)), typeToDraw);
		} else {
			if (toString.contains("String")) {
				t = new StringAttribute(caracName,
						(toString.substring(toString.indexOf("=") + 1, toString.length() - 1)));
			} else {
				if (toString.contains("AVRT")) {
					String value = (toString.substring(toString.indexOf("=") + 1, toString.length() - 1));
					Scanner sc = new Scanner(value);
					sc.useDelimiter(" | ");
					Double lowerValue = Double.valueOf(sc.next());
					Double downcValue = Double.valueOf(sc.next());
					Double downdelta = Double.valueOf(sc.next());
					Double upcValue = Double.valueOf(sc.next());
					Double updelta = Double.valueOf(sc.next());
					Double upperValue = Double.valueOf(sc.next());
					t = new AVRT(caracName, new AVT("Up", updelta, upcValue), new AVT("Down", downdelta, downcValue),
							upperValue, lowerValue);
				}
			}
		}
		return t;
	}

	public boolean containsSnap(long number) {
		return false;
	}

	public Agent getAgent(String id, long snap) {
		return getSnaptshot(snap).getAgent(id);
	}

	public Relation getRelation(String id, long snap) {
		return getSnaptshot(snap).getRelation(id);
	}

	public long getMaxNum() {
		return maxNum;
	}

	public ArrayList<Relation> getRelations(String aname, long num) {
		Snapshot s = this.getSnaptshot(num);
		ArrayList<Relation> relations = new ArrayList<Relation>();
		for (Relation r : s.getRelations()) {
			if (r.getA().getName().equals(aname) || r.getB().getName().equals(aname)) {
				relations.add(r);
			}
		}
		return relations;
	}
}
