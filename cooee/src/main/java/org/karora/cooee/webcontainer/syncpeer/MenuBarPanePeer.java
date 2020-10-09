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

package org.karora.cooee.webcontainer.syncpeer;

import org.karora.cooee.app.Border;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.MenuBarPane;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.image.ImageTools;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.FillImageRender;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.output.CssStyle;
import org.w3c.dom.Element;

/**
 * <code>ComponentSynchronizePeer</code> implementation for synchronizing
 * <code>MenuBarPane</code> components.
 */
public class MenuBarPanePeer extends AbstractMenuPeer {

    /**
     * @see org.karora.cooee.webcontainer.syncpeer.AbstractMenuPeer#getImage(org.karora.cooee.app.Component, java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        ImageReference image = super.getImage(component, imageId);
        if (image != null) {
            return image;
        }
        
        FillImage fillImage = null;
        if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            fillImage = (FillImage) component.getRenderProperty(MenuBarPane.PROPERTY_BACKGROUND_IMAGE);
        } else if (IMAGE_ID_MENU_BACKGROUND.equals(imageId)) {
            fillImage = (FillImage) component.getRenderProperty(MenuBarPane.PROPERTY_MENU_BACKGROUND_IMAGE);
        } else if (IMAGE_ID_SELECTION_BACKGROUND.equals(imageId)) {
            fillImage = (FillImage) component.getRenderProperty(MenuBarPane.PROPERTY_SELECTION_BACKGROUND_IMAGE);
        }
        return fillImage == null ? null : fillImage.getImage();
    }

   /**
     * Renders a dispose directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param menu the <code>MenuBarPane</code> being rendered
     */
    void renderDisposeDirective(RenderContext rc, Component component) {
        MenuBarPane menu = (MenuBarPane) component;
        String elementId = ContainerInstance.getElementId(menu);
        ServerMessage serverMessage = rc.getServerMessage();
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, 
                "ExtrasMenuBarPane.MessageProcessor", "dispose");
        initElement.setAttribute("eid", elementId);
    }

    /**
     * Renders an initialization directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param menu the <code>MenuBarPane</code> being rendered
     */
    void renderInitDirective(RenderContext rc, Component component, String targetId) {
        MenuBarPane menu = (MenuBarPane) component;
        String elementId = ContainerInstance.getElementId(menu);
        ServerMessage serverMessage = rc.getServerMessage();
        Element partElement = serverMessage.addPart(ServerMessage.GROUP_ID_UPDATE, "ExtrasMenuBarPane.MessageProcessor");
        Element initElement = serverMessage.getDocument().createElement("init");
        initElement.setAttribute("container-eid", targetId);
        initElement.setAttribute("eid", elementId);
        if (!menu.isRenderEnabled()) {
            initElement.setAttribute("enabled", "false");
        }
        
        Color background = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_BACKGROUND);
        if (background != null) {
            initElement.setAttribute("background", ColorRender.renderCssAttributeValue(background));
        }
        FillImage backgroundImage = (FillImage) menu.getRenderProperty(MenuBarPane.PROPERTY_BACKGROUND_IMAGE);
        if (backgroundImage != null) {
            CssStyle cssStyle = new CssStyle();
            FillImageRender.renderToStyle(cssStyle, rc, this, menu, IMAGE_ID_BACKGROUND, backgroundImage, 0);
            initElement.setAttribute("background-image", cssStyle.renderInline());
        }
        Border border = (Border) menu.getRenderProperty(MenuBarPane.PROPERTY_BORDER);
        if (border != null) {
            if (border.getColor() != null) {
                initElement.setAttribute("border-color", 
                        ColorRender.renderCssAttributeValue(border.getColor()));
            }
            if (border.getSize() != null && border.getSize().getUnits() == Extent.PX) {
                initElement.setAttribute("border-size", Integer.toString(border.getSize().getValue()));
            }
            initElement.setAttribute("border-style", BorderRender.getStyleValue(border.getStyle())); 
        }
        Color foreground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_FOREGROUND);
        if (foreground != null) {
            initElement.setAttribute("foreground", ColorRender.renderCssAttributeValue(foreground));
        }
        Color menuBackground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_MENU_BACKGROUND);
        if (menuBackground != null) {
            initElement.setAttribute("menu-background", ColorRender.renderCssAttributeValue(menuBackground));
        }
        FillImage menuBackgroundImage = (FillImage) menu.getRenderProperty(MenuBarPane.PROPERTY_MENU_BACKGROUND_IMAGE);
        if (menuBackgroundImage != null) {
            CssStyle cssStyle = new CssStyle();
            FillImageRender.renderToStyle(cssStyle, rc, this, menu, IMAGE_ID_MENU_BACKGROUND, menuBackgroundImage, 0);
            initElement.setAttribute("menu-background-image", cssStyle.renderInline());
        }
        Border menuBorder = (Border) menu.getRenderProperty(MenuBarPane.PROPERTY_MENU_BORDER);
        if (menuBorder != null) {
            if (menuBorder.getColor() != null) {
                initElement.setAttribute("menu-border-color", 
                        ColorRender.renderCssAttributeValue(menuBorder.getColor()));
            }
            if (menuBorder.getSize() != null && menuBorder.getSize().getUnits() == Extent.PX) {
                initElement.setAttribute("menu-border-size", Integer.toString(menuBorder.getSize().getValue()));
            }
            initElement.setAttribute("menu-border-style", BorderRender.getStyleValue(menuBorder.getStyle())); 
        }
        Color menuForeground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_MENU_FOREGROUND);
        if (menuForeground != null) {
            initElement.setAttribute("menu-foreground", ColorRender.renderCssAttributeValue(menuForeground));
        }
        Color selectionBackground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_SELECTION_BACKGROUND);
        if (selectionBackground != null) {
            initElement.setAttribute("selection-background", ColorRender.renderCssAttributeValue(selectionBackground));
        }
        FillImage selectionBackgroundImage = (FillImage) menu.getRenderProperty(MenuBarPane.PROPERTY_SELECTION_BACKGROUND_IMAGE);
        if (selectionBackgroundImage != null) {
            CssStyle cssStyle = new CssStyle();
            FillImageRender.renderToStyle(cssStyle, rc, this, menu, IMAGE_ID_SELECTION_BACKGROUND, selectionBackgroundImage, 0);
            initElement.setAttribute("selection-background-image", cssStyle.renderInline());
        }
        Color selectionForeground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_SELECTION_FOREGROUND);
        if (selectionForeground != null) {
            initElement.setAttribute("selection-foreground", ColorRender.renderCssAttributeValue(selectionForeground));
        }
        Color disabledBackground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_DISABLED_BACKGROUND);
        if (disabledBackground != null) {
            initElement.setAttribute("disabled-background", ColorRender.renderCssAttributeValue(disabledBackground));
        }
        FillImage disabledBackgroundImage = (FillImage) menu.getRenderProperty(MenuBarPane.PROPERTY_DISABLED_BACKGROUND_IMAGE);
        if (disabledBackgroundImage != null) {
            CssStyle cssStyle = new CssStyle();
            FillImageRender.renderToStyle(cssStyle, rc, this, menu, IMAGE_ID_DISABLED_BACKGROUND, disabledBackgroundImage, 0);
            initElement.setAttribute("disabled-background-image", cssStyle.renderInline());
        }
        Color disabledForeground = (Color) menu.getRenderProperty(MenuBarPane.PROPERTY_DISABLED_FOREGROUND);
        if (disabledForeground != null) {
            initElement.setAttribute("disabled-foreground", ColorRender.renderCssAttributeValue(disabledForeground));
        }
        
        initElement.setAttribute("submenu-image", ImageTools.getUri(rc, this, menu, IMAGE_ID_SUBMENU_RIGHT));

        initElement.setAttribute("icon-toggle-off", ImageTools.getUri(rc, this, menu, IMAGE_ID_TOGGLE_OFF));
        initElement.setAttribute("icon-toggle-on", ImageTools.getUri(rc, this, menu, IMAGE_ID_TOGGLE_ON));
        initElement.setAttribute("icon-radio-off", ImageTools.getUri(rc, this, menu, IMAGE_ID_RADIO_OFF));
        initElement.setAttribute("icon-radio-on", ImageTools.getUri(rc, this, menu, IMAGE_ID_RADIO_ON));
        
        renderModel(rc, menu, menu.getModel(), initElement);
        
        partElement.appendChild(initElement);
    }
}
