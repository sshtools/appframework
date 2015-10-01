package com.sshtools.appframework.mru;

import java.io.File;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class MRUListModel extends AbstractListModel {

	private MRUList mru;

	public MRUListModel() {
		super();
		setMRUList(new MRUList());
	}

	public void add(File f) {

		if (f.exists())
			mru.add(0, f);

		for (int i = mru.size() - 1; i >= 1; i--) {
			if (((File) mru.get(i)).equals(f)) {
				mru.remove(i);
			}
		}
		if (mru.size() > 15) {
			for (int i = mru.size() - 1; i >= 15; i--) {
				mru.remove(i);
			}
		}
		fireContentsChanged(this, 0, getSize() - 1);

	}

	public Object getElementAt(int i) {
		return mru.get(i);
	}

	public int getSize() {
		return (mru == null) ? 0 : mru.size();
	}

	public void setMRUList(MRUList mru) {
		this.mru = mru;
		fireContentsChanged(this, 0, getSize());
	}

	public MRUList getMRUList() {
		return mru;
	}

}
