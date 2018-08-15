/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */
package com.sshtools.appframework.api.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import com.sshtools.ui.swing.XTextField;

/**
 *
 *
 * @author $author$
 */
public class NumericTextField extends XTextField {
	// Supporting classes
	class ADocument extends PlainDocument {
		boolean checkingEnabled = true;
		Number currentVal;
		Number max;
		Number min;
		boolean rightJustify = true;

		public ADocument(Number min, Number max) {
			this.min = min;
			this.max = max;
			if (min.getClass().equals(Byte.class)) {
				currentVal = new Byte((byte) 0);
			} else {
				if (min.getClass().equals(Short.class)) {
					currentVal = new Short((short) 0);
				} else {
					if (min.getClass().equals(Integer.class)) {
						currentVal = new Integer(0);
					} else {
						if (min.getClass().equals(Long.class)) {
							currentVal = new Long(0L);
						} else {
							if (min.getClass().equals(Float.class)) {
								currentVal = new Float(0f);
							} else {
								currentVal = new Double(0d);
							}
						}
					}
				}
			}
		}

		public Number getValue() {
			return currentVal;
		}

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (str == null) {
				return;
			}
			if (!checkingEnabled) {
				super.insertString(offs, str, a);
				return;
			}
			String proposedResult = null;
			if (getLength() == 0) {
				proposedResult = str;
			} else {
				StringBuffer currentBuffer = new StringBuffer(getText(0, getLength()));
				currentBuffer.insert(offs, str);
				proposedResult = currentBuffer.toString();
			}
			try {
				currentVal = parse(proposedResult);
				super.insertString(offs, str, a);
			} catch (Exception e) {
			}
		}

		public boolean isRightJustify() {
			return rightJustify;
		}

		public Number parse(String proposedResult) throws NumberFormatException {
			Double d = new Double(0d);
			// See if the proposed result matches the number format (if any)
			if (!proposedResult.equals(String.valueOf(symbols.getMinusSign())) && (proposedResult.length() != 0)) {
				if (getNumberFormat() != null) {
					// Strip out everything from the proposed result other than
					// the
					// numbers and and decimal separators
					StringBuffer sB = new StringBuffer();
					for (int i = 0; i < proposedResult.length(); i++) {
						char ch = proposedResult.charAt(i);
						if ((ch == symbols.getDecimalSeparator()) || ((ch >= '0') && (ch <= '9'))) {
							sB.append(ch);
						}
					}
					String s = sB.toString();
					// Find out how many digits there are before the decimal
					// place
					int i = 0;
					for (; (i < s.length()) && (s.charAt(i) != symbols.getDecimalSeparator()); i++) {
						;
					}
					int before = i;
					int after = 0;
					if (before < s.length()) {
						after = s.length() - i - 1;
					}
					if (before > getNumberFormat().getMaximumIntegerDigits()) {
						throw new NumberFormatException("More digits BEFORE the decimal separator than allowed:" + proposedResult);
					}
					if (after > getNumberFormat().getMaximumFractionDigits()) {
						throw new NumberFormatException("More digits AFTER the decimal separator than allowed:" + proposedResult);
					}
					// Now try to parse the field against the number format
					try {
						d = new Double(getNumberFormat().parse(proposedResult).doubleValue());
					} catch (ParseException pE) {
						throw new NumberFormatException("Failed to parse. " + proposedResult + pE.getMessage());
					}
				}
				// Just use the default parse
				else {
					d = new Double(proposedResult);
				}
			}
			// Now determine if the number if within range
			if ((d.doubleValue() >= min.doubleValue()) && (d.doubleValue() <= max.doubleValue())) {
				// Now create the real type
				if (min.getClass().equals(Byte.class)) {
					return new Byte(d.byteValue());
				}
				if (min.getClass().equals(Short.class)) {
					return new Short(d.shortValue());
				}
				if (min.getClass().equals(Integer.class)) {
					return new Integer(d.intValue());
				}
				if (min.getClass().equals(Long.class)) {
					return new Long(d.longValue());
				}
				if (min.getClass().equals(Float.class)) {
					return new Float(d.floatValue());
				}
				return d;
			}
			throw new NumberFormatException(
					d + " Is out of range. Minimum is " + min.doubleValue() + ", Maximum is " + max.doubleValue());
		}

		@Override
		public void remove(int offs, int len) throws BadLocationException {
			if (!checkingEnabled) {
				super.remove(offs, len);
				return;
			}
			String currentText = getText(0, getLength());
			String beforeOffset = currentText.substring(0, offs);
			String afterOffset = currentText.substring(len + offs, currentText.length());
			String proposedResult = beforeOffset + afterOffset;
			try {
				currentVal = parse(proposedResult);
				super.remove(offs, len);
			} catch (Exception e) {
			}
		}

		public void setMaximum(Number max) {
			this.max = max;
		}

		public void setMinimum(Number min) {
			this.min = min;
		}

		public void setRightJustify(boolean rightJustify) {
			this.rightJustify = rightJustify;
		}
	}
	// Private instance variables
	private NumberFormat numberFormat;
	private Color positiveBackground;
	private boolean selectAllOnFocusGain;
	private DecimalFormatSymbols symbols;

	private int wColumnWidth;

	/**
	 * Creates a new NumericTextField object.
	 *
	 * @param min min
	 * @param max max
	 */
	public NumericTextField(Number min, Number max) {
		this(min, max, min);
	}

	/**
	 * Creates a new NumericTextField object.
	 *
	 * @param min min
	 * @param max max
	 * @param initial initial
	 */
	public NumericTextField(Number min, Number max, Number initial) {
		this(min, max, initial, true);
	}

	/**
	 * Creates a new NumericTextField object.
	 *
	 * @param min min
	 * @param max max
	 * @param initial initial
	 * @param rightJustify right justify
	 */
	public NumericTextField(Number min, Number max, Number initial, boolean rightJustify) {
		this(min, max, initial, rightJustify, null);
	}

	/**
	 * Creates a new NumericTextField object.
	 *
	 * @param min min
	 * @param max max
	 * @param initial initial
	 * @param rightJustify right justify
	 * @param numberFormat number format
	 *
	 * @throws IllegalArgumentException
	 */
	public NumericTextField(Number min, Number max, Number initial, boolean rightJustify, NumberFormat numberFormat) {
		super(Math.max(min.toString().length(), max.toString().length()));
		setNumberFormat(numberFormat);
		if (min.getClass().equals(max.getClass()) && max.getClass().equals(initial.getClass())) {
			setDocument(new ADocument(min, max));
			setValue(initial);
		} else {
			throw new IllegalArgumentException("All arguments must be of the same class");
		}
		setRightJustify(rightJustify);
	}

	public Number getMaximum() {
		return ((ADocument) getDocument()).max;
	}

	public Number getMinimum() {
		return ((ADocument) getDocument()).min;
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public Number getValue() {
		return ((ADocument) getDocument()).getValue();
	}

	public boolean isRightJustify() {
		return getHorizontalAlignment() == SwingConstants.RIGHT;
	}

	public boolean isSelectAllOnFocusGain() {
		return selectAllOnFocusGain;
	}

	public void setMaximum(Number max) {
		((ADocument) getDocument()).setMaximum(max);
	}

	public void setMinimum(Number min) {
		((ADocument) getDocument()).setMinimum(min);
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
		if (numberFormat instanceof DecimalFormat) {
			symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
		} else {
			symbols = new DecimalFormatSymbols();
		}
	}

	public void setRightJustify(boolean rightJustify) {
		setHorizontalAlignment(rightJustify ? SwingConstants.RIGHT : SwingConstants.LEFT);
	}

	public void setSelectAllOnFocusGain(boolean selectAllOnFocusGain) {
		this.selectAllOnFocusGain = selectAllOnFocusGain;
	}

	@Override
	public void setText(String s) {
		ADocument doc = (ADocument) getDocument();
		Number oldValue = doc.currentVal;
		try {
			doc.currentVal = doc.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (oldValue != doc.currentVal) {
			doc.checkingEnabled = false;
			super.setText(s);
			doc.checkingEnabled = true;
		}
	}

	/**
	 *
	 *
	 * @param i
	 */
	public void setValue(Number i) {
		setText(i.toString());
	}

	/*
	 * Overides <code>JTextFields</codes> calculation of the width of a single
	 * character (M space)
	 *
	 * @return column width based on '9' public int getColumnWidth() { if
	 * (wColumnWidth==0) { FontMetrics metrics = getFontMetrics(getFont());
	 * wColumnWidth = metrics.charWidth('W'); } return wColumnWidth; }
	 */
	@Override
	protected void processFocusEvent(FocusEvent e) {
		super.processFocusEvent(e);
		if (!e.isTemporary()) {
			switch (e.getID()) {
			case FocusEvent.FOCUS_LOST:
				if (getNumberFormat() != null) {
					String s = getNumberFormat().format(getValue()).toString();
					if (!getText().equals(s)) {
						setText(s);
					}
				}
				break;
			case FocusEvent.FOCUS_GAINED:
				if (isSelectAllOnFocusGain()) {
					selectAll();
				}
				break;
			}
		}
	}
}