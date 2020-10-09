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

import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.PaneContainer;
import org.karora.cooee.app.Style;
import org.karora.cooee.ng.able.BackgroundImageable;
import org.karora.cooee.ng.able.Positionable;
import org.karora.cooee.ng.able.ScrollBarProperties;
import org.karora.cooee.ng.able.Scrollable;
import org.karora.cooee.ng.able.Stretchable;

/** 
 * <code>ContainerEx</code> is a component that can be positioned anywhere on the screen 
 * with an specified size attributes.  It does this by implementing the <code>Positionable</code>
 * interface.
 * <p>
 * By default, the children of <code>ContainerEx</code> are layed out one after the other, left to right and
 * without any other specified processing.  Therefore to get more precise layout within the
 * <code>ContainerEx</code>, you may want to consider using a Column/Row/Grid as the child 
 * of this component or you can associated a <code>DisplayLayoutData</code> object with each child component
 * and use that to position the children where you want.  
 * <p>
 * This component is a <code>PaneContainer</code> and hence can have components that implement <code>Pane</code>
 * as a child.  However many <code>Pane</code>s, such as <code>SplitPane</code>, require a definite height to 
 * be set in order to work properly.  So make sure you call setHeight() if one of the children implements  
 * <code>Pane</code>
 * 
 * @see org.karora.cooee.ng.layout.DisplayLayoutData
 */

public class ContainerEx extends AbleComponent implements PaneContainer, Positionable, Scrollable, BackgroundImageable, Stretchable {
	
	public static final Style DEFAULT_STYLE;
	public static final String PROPERTY_BACKGROUND_IMAGE = "backgroundImage";
	public static final String PROPERTY_HORIZONTAL_SCROLL = "horizontalScroll";
	public static final String PROPERTY_VERTICAL_SCROLL = "verticalScroll";
	static {
		MutableStyleEx style = new MutableStyleEx();
		
		style.setProperty(PROPERTY_POSITION, RELATIVE);
		DEFAULT_STYLE  = style;
	}	
	/**
	 * Constructs a <code>ContainerEx</code>. 
	 */
	public ContainerEx() {
		super();
	}

	/**
	 * Constructs a <code>ContainerEx</code> with the
	 * specified component as its first child.
	 */
	public ContainerEx(Component child) {
		this();
		add(child);
	}
	
	/**
	 * @see org.karora.cooee.ng.able.Positionable#clear()
	 */
	public void clear() {
		setZIndex(Integer.MIN_VALUE);
		setPosition(RELATIVE);
		setLeft(null);
		setRight(null);
		setTop(null);
		setBottom(null);
	
	}

	/**
	 * Returns the background image.
	 * 
	 * @return the background image
	 */
	public FillImage getBackgroundImage() {
	    return (FillImage) getProperty(PROPERTY_BACKGROUND_IMAGE);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getBottom()
	 */
	public Extent getBottom() {
		return (Extent) getProperty(PROPERTY_BOTTOM);
	}

	/**
	 * Returns the horizontal scrollbar position.
	 * 
	 * @return the horizontal scrollbar position
	 */
	public Extent getHorizontalScroll() {
	    return (Extent) getProperty(PROPERTY_HORIZONTAL_SCROLL);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getLeft()
	 */
	public Extent getLeft() {
		return (Extent) getProperty(PROPERTY_LEFT);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getPosition()
	 */
	public int getPosition() {
		return getProperty(PROPERTY_POSITION,RELATIVE);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getRight()
	 */
	public Extent getRight() {
		return (Extent) getProperty(PROPERTY_RIGHT);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#getScrollBarBaseColor()
	 */
	public Color getScrollBarBaseColor() {
		return (Color) getProperty(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#getScrollBarPolicy()
	 */
	public int getScrollBarPolicy() {
		return getProperty(Scrollable.PROPERTY_SCROLL_BAR_POLICY,Scrollable.AUTO);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#getScrollBarProperties()
	 */
	public ScrollBarProperties getScrollBarProperties() {
		return (ScrollBarProperties) getProperty(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getTop()
	 */
	public Extent getTop() {
		return (Extent) getProperty(PROPERTY_TOP);
	}

	/**
	 * Returns the vertical scrollbar position.
	 * 
	 * @return the vertical scrollbar position
	 */
	public Extent getVerticalScroll() {
	    return (Extent) getProperty(PROPERTY_VERTICAL_SCROLL);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#getZIndex()
	 */
	public int getZIndex() {
		return getProperty(PROPERTY_Z_INDEX,Integer.MIN_VALUE);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#isPositioned()
	 */
	public boolean isPositioned() {
		return getPosition() != STATIC;
	}

	/**
	 * @see org.karora.cooee.app.Component#processInput(java.lang.String, java.lang.Object)
	 */
	public void processInput(String inputName, Object inputValue) {
	    if (PROPERTY_HORIZONTAL_SCROLL.equals(inputName)) {
	        setHorizontalScroll((Extent) inputValue);
	    } else if (PROPERTY_VERTICAL_SCROLL.equals(inputName)) {
	        setVerticalScroll((Extent) inputValue);
	    }
	}

	/**
	 * Sets the background image of the <code>ContainerEx</code>.
	 * 
	 * @param newValue the new background image
	 */
	public void setBackgroundImage(FillImage newValue) {
	    setProperty(PROPERTY_BACKGROUND_IMAGE, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setBottom(org.karora.cooee.app.Extent)
	 */
	public void setBottom(Extent newValue) {
		setProperty(PROPERTY_BOTTOM,newValue);
	}

	/**
	 * Sets the horizontal scrollbar position  of the <code>ContainerEx</code>.
	 * 
	 * Values must be in pixel units.
	 * 
	 * A value of -1px indicates that the scrollbar should be positioned
	 * at the end of the range. 
	 * 
	 * @param newValue the new horizontal scrollbar position
	 */
	public void setHorizontalScroll(Extent newValue) {
	    setProperty(PROPERTY_HORIZONTAL_SCROLL, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setLeft(org.karora.cooee.app.Extent)
	 */
	public void setLeft(Extent newValue) {
		setProperty(PROPERTY_LEFT,newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setPosition(int)
	 */
	public void setPosition(int newPositioning) {
		setProperty(PROPERTY_POSITION,newPositioning);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setRight(org.karora.cooee.app.Extent)
	 */
	public void setRight(Extent newValue) {
		setProperty(PROPERTY_RIGHT,newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#setScrollBarBaseColor(org.karora.cooee.app.Color)
	 */
	public void setScrollBarBaseColor(Color newValue) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR,newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#setScrollBarPolicy(int)
	 */
	public void setScrollBarPolicy(int newScrollBarPolicy) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_POLICY,newScrollBarPolicy);
	}

	/**
	 * @see org.karora.cooee.ng.able.Scrollable#setScrollBarProperties(org.karora.cooee.ng.able.ScrollBarProperties)
	 */
	public void setScrollBarProperties(ScrollBarProperties newValue) {
		setProperty(Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES,newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setTop(org.karora.cooee.app.Extent)
	 */
	public void setTop(Extent newValue) {
		setProperty(PROPERTY_TOP,newValue);
	}

	/**
	 * Sets the vertical scrollbar position of the <code>ContainerEx</code>.
	 * 
	 * Values must be in pixel units.
	 * 
	 * A value of -1px indicates that the scrollbar should be positioned
	 * at the end of the range. 
	 * 
	 * @param newValue the new vertical scrollbar position
	 */
	public void setVerticalScroll(Extent newValue) {
	    setProperty(PROPERTY_VERTICAL_SCROLL, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Positionable#setZIndex(int)
	 */
	public void setZIndex(int newValue) {
		setProperty(PROPERTY_Z_INDEX,newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#getMaximumStretchedHeight()
	 */
	public Extent getMaximumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT) ;
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#getMinimumStretchedHeight()
	 */
	public Extent getMinimumStretchedHeight() {
		return (Extent) getProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT) ;
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#isHeightStretched()
	 */
	public boolean isHeightStretched() {
		return getProperty(PROPERTY_HEIGHT_STRETCHED, false);
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#setHeightStretched(boolean)
	 */
	public void setHeightStretched(boolean newValue) {
		setProperty(PROPERTY_HEIGHT_STRETCHED, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#setMaximumStretchedHeight(org.karora.cooee.app.Extent)
	 */
	public void setMaximumStretchedHeight(Extent newValue) {
        Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MAXIMUM_STRETCHED_HEIGHT, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.Stretchable#setMinimumStretchedHeight(org.karora.cooee.app.Extent)
	 */
	public void setMinimumStretchedHeight(Extent newValue) {
        Extent.validate(newValue, Extent.PX);
		setProperty(PROPERTY_MINIMUM_STRETCHED_HEIGHT, newValue);
	}
}
