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
package org.karora.cooee.ng.model;

import org.karora.cooee.app.Color;

/**
 * The <code>ColorSwatchModel</code> is used to generate a series of "color
 * swatches" possibly with descriptive names.
 * 
 */
public interface ColorSwatchModel {

	/**
	 * This is called to return an array of colors to be used as color swatches
	 * 
	 * @return - an array of colors to be used as color swatches
	 */
	public Color[] getColorSwatches();

	/**
	 * This is called to return a descriptive string for a color. For example
	 * the color 0x6495ED might be called "Cornflower". If there is no
	 * description for a color then simply return null.
	 * 
	 * @param color -
	 *            the color to get a description for
	 * @return a String description or null if there isnt one
	 */
	public String getColorDescription(Color color);
}
