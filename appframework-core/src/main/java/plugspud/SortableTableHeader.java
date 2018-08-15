package plugspud;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.JTableHeader;

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
