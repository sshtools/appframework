/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
package plugspud;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class SortableHeaderRenderer extends JLabel implements TableCellRenderer {
  private Border border;
  private boolean showSortIcons;
  private int[] sorts;
  private Icon upSortIcon;
  private Icon downSortIcon;
  private Dimension lastSize;
  private TableColumnModel model;

  public SortableHeaderRenderer(TableColumnModel model, boolean showSortIcons,
      SortCriteria sortCriteria) {
    super("");
    this.model = model;
    //  Init
    upSortIcon = new ArrowIcon(ArrowIcon.NORTH);
    downSortIcon = new ArrowIcon(ArrowIcon.SOUTH);
    setForeground(UIManager.getColor("TableHeader.foreground"));
    setBackground(UIManager.getColor("TableHeader.background"));
    setFont(getFont().deriveFont(10f));
    setBorder(BorderFactory.createCompoundBorder(UIManager
        .getBorder("TableHeader.cellBorder"), BorderFactory.createEmptyBorder(
        0, 2, 0, 2)));
    //
    setHorizontalTextPosition(JLabel.LEFT);
    setCriteria(sortCriteria);
  }

  /**
   * @param criteria
   */
  public void setCriteria(SortCriteria criteria) {
    sorts = new int[criteria == null ? 0 : model.getColumnCount()];
    if (sorts.length > 0) {
      sorts[criteria.getSortType()] = criteria.getSortDirection();
    }
  }

  public Dimension getMinimumSize() {
    return new Dimension(1, 1);
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
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

  public void setShowSortIcons(boolean showSortIcons) {
    this.showSortIcons = showSortIcons;
  }

  public void clearSort(int col) {
    sorts[col] = SortCriteria.NO_SORT;
  }

  public boolean isShowSortIcons() {
    return showSortIcons;
  }

  public int reverseSort(int col) {
    return sorts[col] = ((sorts[col] == SortCriteria.SORT_ASCENDING) ? SortCriteria.SORT_DESCENDING
        : SortCriteria.SORT_ASCENDING);
  }

  public int nextSort(int col) {
    return sorts[col] = ((sorts[col] == SortCriteria.SORT_ASCENDING) ? SortCriteria.SORT_DESCENDING
        : ((sorts[col] == SortCriteria.SORT_DESCENDING) ? SortCriteria.NO_SORT
            : SortCriteria.SORT_ASCENDING));
  }

  public void setSort(int col, int sortType) {
    sorts[col] = sortType;
  }

  public int getSort(int i) {
    return sorts[i];
  }
}
