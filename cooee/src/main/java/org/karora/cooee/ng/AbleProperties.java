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

import java.io.Serializable;

import org.karora.cooee.app.Alignment;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.ng.able.Alignable;
import org.karora.cooee.ng.able.BackgroundImageable;

/**
 * <code>AbleProperties</code> is a collection of UI properties that are related to the
 * various *.able interfaces. This allows you to specify many individual
 * properties in one object.
 * <p>
 * This class is really design to be used NOT as a fully fledged component in its own right
 * but rather as a holder of specific properties.  Being derived from <code>Component</code> allows
 * it to re-use much of the Style/Property/Rendering code infrastructure of Echo2.
 * <p>
 * Because <code>AbleProperties</code> is ultimately derived from
 * <code>Component</code>, then you can set properties in here via
 * <code>Style</code> objects. However to get full <code>StyleSheet</code> support,
 * the <code>AbleProperties</code> must be part of the Echo2 component
 * hiearchy.
 */
public class AbleProperties extends AbleComponent implements Serializable, Alignable, BackgroundImageable {

	/**
	 * Constructs a <code>AbleProperties</code>
	 */
	public AbleProperties() {
		super();
	}
	
	/**
	 * We dont allow anu children to be added to the component.
	 * 
	 * @see org.karora.cooee.app.Component#isValidChild(org.karora.cooee.app.Component)
	 */
	public boolean isValidChild(Component child) {
		return false;
	}

	/**
	 * @see org.karora.cooee.ng.able.Alignable#getAlignment()
	 */
	public Alignment getAlignment() {
		return (Alignment) getProperty(Alignable.PROPERTY_ALIGNMENT);
	}

	/**
	 * @see org.karora.cooee.ng.able.Alignable#setAlignment(org.karora.cooee.app.Alignment)
	 */
	public void setAlignment(Alignment newValue) {
		setProperty(Alignable.PROPERTY_ALIGNMENT, newValue);
	}

	/**
	 * @see org.karora.cooee.ng.able.BackgroundImageable#getBackgroundImage()
	 */
	public FillImage getBackgroundImage() {
		return (FillImage) getProperty(BackgroundImageable.PROPERTY_BACKGROUND_IMAGE);
	}

	/**
	 * @see org.karora.cooee.ng.able.BackgroundImageable#setBackgroundImage(org.karora.cooee.app.FillImage)
	 */
	public void setBackgroundImage(FillImage newValue) {
		setProperty(BackgroundImageable.PROPERTY_BACKGROUND_IMAGE, newValue);
	}
}
