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
package plugspud;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

@SuppressWarnings("serial")
public class SortableTableHeader extends JTableHeader {
	private ChangeEvent changeEvent;
	private SortCriteria criteria;
	private int direction;
	private int index;

	/**
	 * Constructor.
	 * 
	 * @param table table
	 */
	public SortableTableHeader(JTable table) {
		this(table, null);
	}

	public SortableTableHeader(final JTable table, SortCriteria criteria) {
		super(table.getColumnModel());
		this.criteria = criteria;
		setDefaultRenderer(new SortableHeaderRenderer(table.getColumnModel(), true, criteria));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				int sel = columnAtPoint(evt.getPoint());
				direction = SortCriteria.NO_SORT;
				for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
					if (i == sel) {
						direction = ((SortableHeaderRenderer) getDefaultRenderer()).nextSort(i);
					} else {
						((SortableHeaderRenderer) getDefaultRenderer()).setSort(i, SortCriteria.NO_SORT);
					}
				}
				index = table.convertColumnIndexToModel(sel);
				if (SortableTableHeader.this.criteria != null) {
					SortableTableHeader.this.criteria.setSortType(index);
					SortableTableHeader.this.criteria.setSortDirection(direction);
				}
				fireStateChanged();
			}
		});
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public int getdirection() {
		return direction;
	}

	public int getindex() {
		return index;
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	public void setCriteria(SortCriteria criteria) {
		this.criteria = criteria;
		((SortableHeaderRenderer) getDefaultRenderer()).setCriteria(criteria);
	}

	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}
}
