/* 
 * This file is part of the Echo2 Extras Project.
 * Copyright (C) 2005-2006 NextApp, Inc.
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

package org.karora.cooee.app;

import org.karora.cooee.app.Component;
import org.karora.cooee.app.FillImageBorder;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.Pane;
import org.karora.cooee.app.PaneContainer;

/**
 * A container which renders a <code>FillImageBorder</code> around its
 * content.
 */
public class BorderPane extends Component 
implements Pane, PaneContainer {
    
    public static final String PROPERTY_BORDER = "border";
    public static final String PROPERTY_INSETS = "insets";
    
    /**
     * Returns the configured border.
     * 
     * @return the border
     */
    public FillImageBorder getBorder() {
        return (FillImageBorder) getProperty(PROPERTY_BORDER);
    }
    /**
     * Returns the inset margin.
     * 
     * @return the inset margin
     */
    public Insets getInsets() {
        return (Insets) getProperty(PROPERTY_INSETS);
    }
    
    /**
     * Sets the border.
     * 
     * @param newValue the new border
     */
    public void setBorder(FillImageBorder newValue) {
        setProperty(PROPERTY_BORDER, newValue);
    }
    
    /**
     * Sets the inset margin.
     * 
     * @param newValue the new inset margin
     */
    public void setInsets(Insets newValue) {
        setProperty(PROPERTY_INSETS, newValue);
    }
}
