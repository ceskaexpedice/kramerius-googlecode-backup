package cz.incad.kramerius.pdf.pdfpages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.incad.kramerius.pdf.Outline;

public class OutlineItem {
	
	private OutlineItem parent;
	private List<OutlineItem> children = new ArrayList<OutlineItem>();
	
	private String title;
	private String destination;
	private int level;
	
	public void addChild(OutlineItem item) {
		this.children.add(item);
	}
	
	public void removeChild(OutlineItem item) {
		this.children.remove(item);
	}
	
	public OutlineItem[] getChildren() {
		return (OutlineItem[]) this.children.toArray(new OutlineItem[this.children.size()]);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public OutlineItem getParent() {
		return parent;
	}

	public void setParent(OutlineItem parent) {
		this.parent = parent;
	}

	
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void debugInformations(StringBuffer buffer, int level) {
		for (int i = 0; i < level; i++) { buffer.append(' '); }
		buffer.append('\'').append(this.title).append('\'').append(this.destination).append('\n');
		for (OutlineItem item : this.children) {
			item.debugInformations(buffer, level+1);
		}
	}	
	
	public OutlineItem createNewTill(String uuid) {
		OutlineItem thisItem = new OutlineItem();
		for (OutlineItem itm : this.children) {
			if (itm.getDestination().equals(uuid)) {
				OutlineItem chItm = new OutlineItem();
				chItm.setLevel(itm.getLevel());
				chItm.setParent(thisItem);
				chItm.setTitle(itm.getTitle());
				thisItem.addChild(chItm);
				// zahodit vsechny stranky
				return thisItem;
			} else {
				thisItem.addChild(itm.createNewTill(uuid));
			}
		}
		return thisItem;
	}
	
	
	public boolean removeTill(String uuid) {
		for (int i = 0; i < this.children.size(); i++) {
			//OutlineItem item = 
		}
		return false;
	}
}
