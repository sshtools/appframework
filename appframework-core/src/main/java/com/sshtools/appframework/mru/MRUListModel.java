/**
 * Maverick Application Framework - Application framework
 * Copyright © ${project.inceptionYear} SSHTOOLS Limited (support@sshtools.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
			if (mru.get(i).equals(f)) {
				mru.remove(i);
			}
		}
//		if (mru.size() > 15) {
//			for (int i = mru.size() - 1; i >= 15; i--) {
//				mru.remove(i);
//			}
//		}
		fireContentsChanged(this, 0, getSize() - 1);

	}

	@Override
	public Object getElementAt(int i) {
		return mru.get(i);
	}

	public MRUList getMRUList() {
		return mru;
	}

	@Override
	public int getSize() {
		return (mru == null) ? 0 : mru.size();
	}

	public void setMRUList(MRUList mru) {
		this.mru = mru;
		fireContentsChanged(this, 0, getSize());
	}

}
