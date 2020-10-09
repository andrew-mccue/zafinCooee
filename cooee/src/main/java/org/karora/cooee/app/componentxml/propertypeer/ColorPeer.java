/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

package org.karora.cooee.app.componentxml.propertypeer;

import org.karora.cooee.app.Color;
import org.karora.cooee.app.componentxml.InvalidPropertyException;
import org.karora.cooee.app.componentxml.PropertyXmlPeer;
import org.w3c.dom.Element;



/**
 * <code>PropertyXmlPeer</code> implementation for 
 * <code>org.karora.cooee.app.Color</code> properties.
 */
public class ColorPeer 
implements PropertyXmlPeer {
    
    /**
     * Converts the given string value to a <code>Color</code> equivalent.
     * 
     * @param value a string representation of an <code>Color</code>
     * @return the <code>Color</code>
     */
    public static Color toColor(String value) 
    throws InvalidPropertyException {
        try {
            int colorValue = Integer.parseInt(value.substring(1), 16);
            return new Color(colorValue);
        } catch (IndexOutOfBoundsException ex) {
            throw new InvalidPropertyException("Invalid color value: " + value, ex);
        } catch (NumberFormatException ex) {
            throw new InvalidPropertyException("Invalid color value: " + value, ex);
        }
    }
    
    /**
     * @see org.karora.cooee.app.componentxml.PropertyXmlPeer#getValue(java.lang.ClassLoader, 
     *      java.lang.Class, org.w3c.dom.Element)
     */
    public Object getValue(ClassLoader classLoader, Class objectClass, Element propertyElement)
    throws InvalidPropertyException {
        String value = propertyElement.getAttribute("value");
        return toColor(value);
    }
}
