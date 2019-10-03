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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class SortableHeaderRenderer extends JLabel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private Icon downSortIcon;
	private TableColumnModel model;
	private boolean showSortIcons;
	private int[] sorts;
	private Icon upSortIcon;

	/**
	 * Constructor.
	 * 
	 * @param model model
	 * @param showSortIcons show sort icons
	 * @param sortCriteria sort criteria
	 */
	public SortableHeaderRenderer(TableColumnModel model, boolean showSortIcons, SortCriteria sortCriteria) {
		super("");
		this.model = model;
		// Init
		upSortIcon = new ArrowIcon(SwingConstants.NORTH);
		downSortIcon = new ArrowIcon(SwingConstants.SOUTH);
		setForeground(UIManager.getColor("TableHeader.foreground"));
		setBackground(UIManager.getColor("TableHeader.background"));
		setFont(getFont().deriveFont(10f));
		setBorder(BorderFactory.createCompoundBorder(UIManager.getBorder("TableHeader.cellBorder"),
				BorderFactory.createEmptyBorder(0, 2, 0, 2)));
		//
		setHorizontalTextPosition(SwingConstants.LEFT);
		setCriteria(sortCriteria);
	}

	public void clearSort(int col) {
		sorts[col] = SortCriteria.NO_SORT;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(1, 1);
	}

	public int getSort(int i) {
		return sorts[i];
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		//
		if (sorts.length > 0) {
			switch (sorts[column]) {
			case SortCriteria.SORT_ASCENDING:
				setIcon(upSortIcon);
				break;
			case SortCriteria.SORT_DESCENDING:
				setIcon(downSortIcon);
				break;
			default:
				setIcon(null);
				break;
			}
		}
		//
		setText(value.toString());
		return this;
	}

	public boolean isShowSortIcons() {
		return showSortIcons;
	}

	public int nextSort(int col) {
		return sorts[col] = ((sorts[col] == SortCriteria.SORT_ASCENDING) ? SortCriteria.SORT_DESCENDING
				: ((sorts[col] == SortCriteria.SORT_DESCENDING) ? SortCriteria.NO_SORT : SortCriteria.SORT_ASCENDING));
	}

	public int reverseSort(int col) {
		return sorts[col] = ((sorts[col] == SortCriteria.SORT_ASCENDING) ? SortCriteria.SORT_DESCENDING
				: SortCriteria.SORT_ASCENDING);
	}

	/**
	 * Set criteria.
	 * 
	 * @param criteria criteria
	 */
	public void setCriteria(SortCriteria criteria) {
		sorts = new int[criteria == null ? 0 : model.getColumnCount()];
		if (sorts.length > 0) {
			sorts[criteria.getSortType()] = criteria.getSortDirection();
		}
	}

	public void setShowSortIcons(boolean showSortIcons) {
		this.showSortIcons = showSortIcons;
	}

	public void setSort(int col, int sortType) {
		sorts[col] = sortType;
	}
}
