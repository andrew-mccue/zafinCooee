/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
package org.karora.cooee.ng;

import org.karora.cooee.app.Border;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.Insets;
import org.karora.cooee.ng.able.AccessKeyable;
import org.karora.cooee.ng.able.Borderable;
import org.karora.cooee.ng.able.Insetable;
import org.karora.cooee.ng.able.MouseCursorable;
import org.karora.cooee.ng.able.Sizeable;
import org.karora.cooee.ng.able.ToolTipable;

/**
 * <code>AbleComponent</code> is an abstract Component that implements a
 * number of the 'able interfaces and serves as the base for other components
 */

public abstract class AbleComponent extends ComponentEx implements
		AccessKeyable, Borderable, MouseCursorable, Insetable, Sizeable,
		ToolTipable {

	/**
	 * @see org.karora.cooee.ng.able.AccessKeyable#getAccessKey()
	 */
	public String getAccessKey() {
		return (String) getProperty(PROPERTY_ACCESS_KEY);
	}

	/**
	 * 
	 * @see org.karora.cooee.ng.able.Borderable#getBorder()
	 */
	public Border getBorder() {
		return (Border) getProperty(PROPERTY_BORDER);
	}

	/**
	 * @see org.karora.cooee.ng.able.Heightable#getHeight()
	 */
	public Extent getHeight() {
		return (Extent) getProperty(PROPERTY_HEIGHT);
	}

	/**
	 * @see org.karora.cooee.ng.able.Insetable#getInsets()
	 */
	public Insets getInsets() {
		return (Insets) getProperty(PROPERTY_INSETS);
	}

	/**
	 * @see org.karora.cooee.ng.able.MouseCursorable#getMouseCursor()
	 */
	public int getMouseCursor() {
		return getProperty(PROPERTY_MOUSE_CURSOR, CURSOR_AUTO);
	}

	/**
	 * @see org.karora.cooee.ng.able.MouseCursorable#getMouseCursorUri()
	 */
	public String getMouseCursorUri() {
		return (String) getProperty(PROPERTY_MOUSE_CURSOR_URI);
	}

	/**
	 * @see org.karora.cooee.ng.able.Insetable#getOutsets()
	 */
	public Insets getOutsets() {
		return (Insets) getProperty(PROPERTY_OUTSETS);
	}

	/**
	 * @see org.karora.cooee.ng.able.Widthable#getWidth()
	 */
	public Extent getWidth() {
		return (Extent) getProperty(PROPERTY_WIDTH);
	}

	/**
	 * @see org.karora.cooee.ng.able.AccessKeyable#setAccessKey(java.lang.String)
	 */
	public void setAccessKey(String newValue) {
		setProperty(PROPERTY_ACCESS_KEY, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Borderable#setBorder(org.karora.cooee.app.Border)
	 */
	public void setBorder(Border newValue) {
		setProperty(PROPERTY_BORDER, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Heightable#setHeight(org.karora.cooee.app.Extent)
	 */
	public void setHeight(Extent newValue) {
		setProperty(PROPERTY_HEIGHT, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Insetable#setInsets(org.karora.cooee.app.Insets)
	 */
	public void setInsets(Insets newValue) {
		setProperty(PROPERTY_INSETS, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.MouseCursorable#setMouseCursor(int)
	 */
	public void setMouseCursor(int mouseCursor) {
		setProperty(PROPERTY_MOUSE_CURSOR, mouseCursor);
	}

	/**
	 * @see org.karora.cooee.ng.able.MouseCursorable#setMouseCursorUri(java.lang.String)
	 */
	public void setMouseCursorUri(String mouseCursorURI) {
		setProperty(PROPERTY_MOUSE_CURSOR_URI, mouseCursorURI);
	}

	/**
	 * @see org.karora.cooee.ng.able.Insetable#setOutsets(org.karora.cooee.app.Insets)
	 */
	public void setOutsets(Insets newValue) {
		setProperty(PROPERTY_OUTSETS, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Widthable#setWidth(org.karora.cooee.app.Extent)
	 */
	public void setWidth(Extent newValue) {
		setProperty(PROPERTY_WIDTH, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.ToolTipable#getToolTipText()
	 */
	public String getToolTipText() {
		return (String) getProperty(PROPERTY_TOOL_TIP_TEXT);
	}

	/**
	 * @see org.karora.cooee.ng.able.ToolTipable#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String newValue) {
		setProperty(PROPERTY_TOOL_TIP_TEXT, newValue);

	}
}
