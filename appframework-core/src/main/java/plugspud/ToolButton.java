/*-- 

 Copyright (C) 2003 Brett Smith.
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.
 
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows 
    these conditions in the documentation and/or other materials 
    provided with the distribution.

 3. The name "Plugspud" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact t_magicthize@users.sourceforge.net.
 
 4. Products derived from this software may not be called "Plugspud", nor
    may "Plugspud" appear in their name, without prior written permission.
 
 In addition, we request (but do not require) that you include in the 
 end-user documentation provided with the redistribution and/or in the 
 software itself an acknowledgement equivalent to the following:
     "This product includes software developed for the Gruntspud
     "Project (http://gruntspud.sourceforge.net/)."

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE PLUGSPUD AUTHORS OR THE PROJECT
 CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE. 
 */
package plugspud;

import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * <p>
 * An extension of <code>JButton</code> that looks nicer on the tool bar
 * </p>
 */
public class ToolButton extends JButton {
	//
	private final static Insets INSETS = new Insets(0, 0, 0, 0);
	private boolean hideText;

	/**
	 * Construct a new <code>IconPanel</code> given an icon and a component
	 *
	 * @param action icon
	 */
	public ToolButton(Action action) {
		this(action, true);
	}

	/**
	 * Construct a new <code>IconPanel</code> given an icon and a component
	 *
	 * @param action icon
	 * @param hideText hide the text
	 */
	public ToolButton(Action action, boolean hideText) {
		super(action);
		setMargin(INSETS);
		setRequestFocusEnabled(false);
		setFocusPainted(false);
		setHideText(hideText);
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (isEnabled()) {
					setBorderPainted(true);
					setContentAreaFilled(true);
				}
			}

			public void mouseExited(MouseEvent e) {
				setBorderPainted(false);
				setContentAreaFilled(false);
			}
		});
		setBorderPainted(false);
		setContentAreaFilled(false);
	}

	/**
	 * Determines if the button can retrieve focus
	 *
	 * @return always returns <tt>false</tt>
	 */
	public boolean isFocusable() {
		return false;
	}

	/**
	 * Sets the hide text property of the buttin
	 *
	 * @param hideText <tt>true</tt> if the text is to be hidden otherwies
	 *            <tt>false</tt>
	 */
	public void setHideText(boolean hideText) {
		if (this.hideText != hideText) {
			firePropertyChange("hideText", this.hideText, hideText);
		}
		this.hideText = hideText;
		repaint();
	}

	/**
	 * Gets the text for the button
	 *
	 * @return the button text if not hidden otherwise <tt>null</tt>
	 */
	public String getText() {
		return hideText ? null : super.getText();
	}
}
