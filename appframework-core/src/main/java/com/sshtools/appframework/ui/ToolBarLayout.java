/**
 * Appframework
 * Copyright (C) 2003-2016 SSHTOOLS Limited
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 */
/*
 */
package com.sshtools.appframework.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ToolBarLayout extends BorderLayout {
  
  private ArrayList north = new ArrayList(1);
  private ArrayList south = new ArrayList(1);
  private ArrayList east = new ArrayList(1);
  private ArrayList west = new ArrayList(1);
  private Component center = null;
  private boolean wrap;
  private int overunIndex;

  private int northHeight, southHeight, eastWidth, westWidth;

  public static final int TOP = SwingConstants.TOP;

  public static final int BOTTOM = SwingConstants.BOTTOM;

  public static final int LEFT = SwingConstants.LEFT;

  public static final int RIGHT = SwingConstants.RIGHT;
  
  public ToolBarLayout() {
  }


  public void addLayoutComponent(String con, Component c) {
    synchronized (c.getTreeLock()) {
      String s = con == null ? NORTH : con.toString();
      if (NORTH.equals(s)) {
        north.add(c);
      } else if (SOUTH.equals(s)) {
        south.add(c);
      } else if (EAST.equals(s)) {
        east.add(c);
      } else if (WEST.equals(s)) {
        west.add(c);
      } else if (CENTER.equals(s)) {
        center = c;
      }
      c.getParent().validate();
    }
  }

  public void removeLayoutComponent(Component c) {
    north.remove(c);
    south.remove(c);
    east.remove(c);
    west.remove(c);
    if (c == center) center = null;
    flipSeparators(c, SwingConstants.VERTICAL);
  }

  public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int top = insets.top;
      int bottom = target.getHeight() - insets.bottom;
      int left = insets.left;
      int right = target.getWidth() - insets.right;
      northHeight = getPreferredDimension(north).height;
      southHeight = getPreferredDimension(south).height;
      eastWidth = getPreferredDimension(east).width;
      westWidth = getPreferredDimension(west).width;
      placeComponents(target, north, left, top, right - left, northHeight, TOP);
      top += (northHeight + getVgap());
      placeComponents(target, south, left, bottom - southHeight, right - left,
          southHeight, BOTTOM);
      bottom -= (southHeight + getVgap());
      placeComponents(target, east, right - eastWidth, top, eastWidth, bottom
          - top, RIGHT);
      right -= (eastWidth + getHgap());
      placeComponents(target, west, left, top, westWidth, bottom - top, LEFT);
      left += (westWidth + getHgap());
      if (center != null) {
        center.setBounds(left, top, right - left, bottom - top);
      }
    }
  }

  // Returns the ideal width for a vertically oriented toolbar
  // and the ideal height for a horizontally oriented tollbar:
  private Dimension getPreferredDimension(ArrayList comps) {
    int w = 0, h = 0;
    for (int i = 0; i < comps.size(); i++) {
      Component c = (Component) (comps.get(i));
      Dimension d = c.getPreferredSize();
      w = Math.max(w, d.width);
      h = Math.max(h, d.height);
    }
    return new Dimension(w, h);
  }

  private void flipSeparators(Component c, int orientn) {
    if (c != null
        && c instanceof JToolBar
        && UIManager.getLookAndFeel().getName().toLowerCase()
            .indexOf("windows") != -1) {
      JToolBar jtb = (JToolBar) c;
      Component comps[] = jtb.getComponents();
      if (comps != null && comps.length > 0) {
        for (int i = 0; i < comps.length; i++) {
          try {
            Component component = comps[i];
            if (component != null) {
              if (component instanceof JSeparator) {
                jtb.remove(component);
                JSeparator separ = new JSeparator();
                if (orientn == SwingConstants.VERTICAL) {
                  separ.setOrientation(SwingConstants.VERTICAL);
                  separ.setMinimumSize(new Dimension(2, 6));
                  separ.setPreferredSize(new Dimension(2, 6));
                  separ.setMaximumSize(new Dimension(2, 100));
                } else {
                  separ.setOrientation(SwingConstants.HORIZONTAL);
                  separ.setMinimumSize(new Dimension(6, 2));
                  separ.setPreferredSize(new Dimension(6, 2));
                  separ.setMaximumSize(new Dimension(100, 2));
                }
                jtb.add(separ, i);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  private void placeComponents(Container target, ArrayList comps, int x, int y,
      int w, int h, int orientation) {
    int offset = 0;
    Component c = null;
    if (orientation == TOP || orientation == BOTTOM) {
      offset = x;
      int totalWidth = 0;
      for (int i = 0; i < comps.size(); i++) {
        c = (Component) (comps.get(i));
        flipSeparators(c, SwingConstants.VERTICAL);
        int cwidth = c.getPreferredSize().width;
        totalWidth += cwidth;
        if (w < totalWidth && i != 0) {
          offset = x;
          if (orientation == TOP) {
            y += h;
            northHeight += h;
          } else if (orientation == BOTTOM) {
            southHeight += h;
            y -= h;
          }
          totalWidth = cwidth;
        }
        Rectangle bounds = new Rectangle(x + offset, y, cwidth, h);
        c.setBounds(bounds);
        offset += cwidth;
      }
      flipSeparators(c, SwingConstants.VERTICAL);
    } else {
      int totalHeight = 0;
      for (int i = 0; i < comps.size(); i++) {
        c = (Component) (comps.get(i));
        int cheight = c.getPreferredSize().height;
        totalHeight += cheight;
        if (h < totalHeight && i != 0) {
          if (orientation == LEFT) {
            x += w;
            westWidth += w;
          } else if (orientation == RIGHT) {
            eastWidth += w;
            x -= w;
          }
          totalHeight = cheight;
          offset = 0;
        }
        if (totalHeight > h) cheight = h - 1;
        Rectangle bounds = new Rectangle(x, y + offset, w, cheight);
        c.setBounds(bounds);
        offset += cheight;
      }
      flipSeparators(c, SwingConstants.HORIZONTAL);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
   */
  public Dimension minimumLayoutSize(Container target) {
    return super.minimumLayoutSize(target);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
   */
  public Dimension preferredLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int top = insets.top;
      int bottom = target.getHeight() - insets.bottom;
      int left = insets.left;
      int right = target.getWidth() - insets.right;
      
      Dimension ns = calcWidth(north,target);
      Dimension ss = calcWidth(south,target);
      Dimension es = calcHeight(east,target);
      Dimension ws = calcHeight(west,target);
      int w = insets.left + insets.right + Math.max(Math.max(ns.width, ss.width), ( center == null ? 0 : center.getPreferredSize().width ) + es.width + ws.width);
      int h = insets.top + insets.bottom + Math.max(Math.max(es.height, ws.height), ( center == null ? 0 : center.getPreferredSize().height ) + ns.height + ss.height);      
      Dimension d = new Dimension(w, h);
      return d;
    }
  }
  
  private Dimension calcWidth(ArrayList components, Container target) {      
      Dimension ns = new Dimension();
      Insets insets = target.getInsets();
      Dimension ts = new Dimension(target.getSize().width - insets.top - insets.bottom, 
          target.getSize().height - insets.left - insets.right);
      for (Iterator i = components.iterator(); i.hasNext();) {
        Component c = (Component) i.next();
        ns.width += c.getPreferredSize().width;
        ns.height = Math.max(ns.height, c.getPreferredSize().height);
      }      
      if(ns.width > ts.width) {
        int hadjust = ( (int)( ns.getWidth() / ts.getWidth() ) * ns.height );  
        ns.height += hadjust;
        ns.width = ts.width;
      }
      return ns;
  }
  
  private Dimension calcHeight(ArrayList components, Container  target) {      
      Dimension ns = new Dimension();
      Insets insets = target.getInsets();
      Dimension ts = new Dimension(target.getSize().width - insets.top - insets.bottom, 
          target.getSize().height - insets.left - insets.right);
      for (Iterator i = components.iterator(); i.hasNext();) {
        Component c = (Component) i.next();
        ns.height += c.getPreferredSize().height;
        ns.width = Math.max(ns.width, c.getPreferredSize().width);
      }      
      if(wrap && ns.height > ts.height) {
        int wadjust = (int)( ( ns.getHeight() / ts.getHeight() ) * ns.width ); 
        ns.width += wadjust;
        ns.height = ts.height;
      }
      return ns;
  }

  /**
   * @param wrap wrap
   */
  public void setWrap(boolean wrap) {
    this.wrap = wrap;    
  }
  
  public boolean isWrap() {
    return wrap;
  }
  
  public int getOverunIndex() {
    return overunIndex;
  }

  
}