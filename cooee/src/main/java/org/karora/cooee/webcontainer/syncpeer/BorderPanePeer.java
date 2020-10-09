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

import org.karora.cooee.app.BorderPane;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.FillImageBorder;
import org.karora.cooee.app.Font;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.Pane;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.image.ImageRenderSupport;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.FillImageRender;
import org.karora.cooee.webcontainer.propertyrender.FontRender;
import org.karora.cooee.webcontainer.propertyrender.InsetsRender;
import org.karora.cooee.webrender.ClientProperties;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.servermessage.VirtualPosition;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <code>ComponentSynchronizePeer</code> implementation for the 
 * <code>BorderPane</code> component.
 */
public class BorderPanePeer 
implements ComponentSynchronizePeer, DomUpdateSupport, ImageRenderSupport {

    /**
     * A boolean property which may be assigned to <code>BorderPane</code>s
     * in order to enable the proprietary Internet Explorer transparent PNG
     * alpha renderer for rendering the <code>border</code> property of the
     * border pane.
     */
    public static final String PROPERTY_IE_ALPHA_RENDER_BORDER
            = "org.karora.cooee.webcontainer.BorderPanePeer.ieAlphaRenderBorder";
    
    private static final String IMAGE_ID_BORDER_TOP_LEFT = "borderTopLeft";
    private static final String IMAGE_ID_BORDER_TOP = "borderTop";
    private static final String IMAGE_ID_BORDER_TOP_RIGHT = "borderTopRight";
    private static final String IMAGE_ID_BORDER_LEFT = "borderLeft";
    private static final String IMAGE_ID_BORDER_RIGHT = "borderRight";
    private static final String IMAGE_ID_BORDER_BOTTOM_LEFT = "borderBottomLeft";
    private static final String IMAGE_ID_BORDER_BOTTOM = "borderBottom";
    private static final String IMAGE_ID_BORDER_BOTTOM_RIGHT = "borderBottomRight";

    private static final Insets DEFAULT_CONTENT_INSETS = new Insets(3);
    private static final FillImageBorder DEFAULT_BORDER 
            = new FillImageBorder(new Color(0x00007f), new Insets(20), DEFAULT_CONTENT_INSETS);

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        return ContainerInstance.getElementId(child.getParent()) + "_content";
    }

    /**
     * @see org.karora.cooee.webcontainer.image.ImageRenderSupport#getImage(org.karora.cooee.app.Component,
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_BORDER_TOP_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP_LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_TOP.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_TOP_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.TOP_RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM_LEFT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM_LEFT);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM);
            return fillImage == null ? null : fillImage.getImage();
        } else if (IMAGE_ID_BORDER_BOTTOM_RIGHT.equals(imageId)) {
            FillImageBorder fillImageBorder = ((FillImageBorder) component.getRenderProperty(BorderPane.PROPERTY_BORDER));
            FillImage fillImage = fillImageBorder == null ? null : fillImageBorder.getFillImage(FillImageBorder.BOTTOM_RIGHT);
            return fillImage == null ? null : fillImage.getImage();
        } else {
            return null;
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String,
     *      org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }

    /**
     * Renders the <code>FillImageBorder</code> as HTML.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param windowDivElement the main DIV element of the window
     * @param borderPane the <code>WindowPane</code> instance
     */
    private void renderBorder(RenderContext rc, Element windowDivElement, BorderPane borderPane) {
        FillImageBorder border = (FillImageBorder) borderPane.getRenderProperty(BorderPane.PROPERTY_BORDER, DEFAULT_BORDER);
        Color borderColor = border.getColor();
        Insets borderInsets = border.getBorderInsets() == null ? new Insets(0) : border.getBorderInsets();
        Document document = rc.getServerMessage().getDocument();
        String elementId = ContainerInstance.getElementId(borderPane);
        ServerMessage serverMessage = rc.getServerMessage();

        if (rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_CSS_POSITIONING_ONE_SIDE_ONLY)) {
            VirtualPosition.renderRegister(serverMessage, elementId + "_border_t");
            VirtualPosition.renderRegister(serverMessage, elementId + "_border_l");
            VirtualPosition.renderRegister(serverMessage, elementId + "_border_r");
            VirtualPosition.renderRegister(serverMessage, elementId + "_border_b");
        }
        
        int borderTopPixels = ExtentRender.toPixels(borderInsets.getTop(), 0);
        int borderLeftPixels = ExtentRender.toPixels(borderInsets.getLeft(), 0);
        int borderRightPixels = ExtentRender.toPixels(borderInsets.getRight(), 0);
        int borderBottomPixels = ExtentRender.toPixels(borderInsets.getBottom(), 0);
        
        int fillImageRenderFlags = ((Boolean) borderPane.getRenderProperty(PROPERTY_IE_ALPHA_RENDER_BORDER, 
                Boolean.FALSE)).booleanValue() ? FillImageRender.FLAG_ENABLE_IE_PNG_ALPHA_FILTER : 0;

        Element borderDivElement;
        CssStyle borderCssStyle;
        
        // Top
        if (borderTopPixels > 0) {
            // Top Left Corner
            if (borderLeftPixels > 0) {
                borderDivElement = document.createElement("div");
                borderDivElement.setAttribute("id", elementId + "_border_tl");
                borderCssStyle = new CssStyle();
                borderCssStyle.setAttribute("font-size", "1px");
                ColorRender.renderToStyle(borderCssStyle, null, borderColor);
                borderCssStyle.setAttribute("position", "absolute");
                borderCssStyle.setAttribute("top", "0px");
                borderCssStyle.setAttribute("left", "0px");
                borderCssStyle.setAttribute("height", borderTopPixels + "px");
                borderCssStyle.setAttribute("width", borderLeftPixels + "px");
                FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_TOP_LEFT, border
                        .getFillImage(FillImageBorder.TOP_LEFT), fillImageRenderFlags);
                borderDivElement.setAttribute("style", borderCssStyle.renderInline());
                windowDivElement.appendChild(borderDivElement);
            }
    
            // Top Side
            borderDivElement = document.createElement("div");
            borderDivElement.setAttribute("id", elementId + "_border_t");
            borderCssStyle = new CssStyle();
            borderCssStyle.setAttribute("font-size", "1px");
            ColorRender.renderToStyle(borderCssStyle, null, borderColor);
            borderCssStyle.setAttribute("position", "absolute");
            borderCssStyle.setAttribute("top", "0px");
            borderCssStyle.setAttribute("left", borderLeftPixels + "px");
            borderCssStyle.setAttribute("height", borderTopPixels + "px");
            borderCssStyle.setAttribute("right", borderRightPixels + "px");
            FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_TOP, border
                    .getFillImage(FillImageBorder.TOP), fillImageRenderFlags);
            borderDivElement.setAttribute("style", borderCssStyle.renderInline());
            windowDivElement.appendChild(borderDivElement);
    
            // Top Right Corner
            if (borderRightPixels > 0) {
                borderDivElement = document.createElement("div");
                borderDivElement.setAttribute("id", elementId + "_border_tr");
                borderCssStyle = new CssStyle();
                borderCssStyle.setAttribute("font-size", "1px");
                ColorRender.renderToStyle(borderCssStyle, null, borderColor);
                borderCssStyle.setAttribute("position", "absolute");
                borderCssStyle.setAttribute("top", "0px");
                borderCssStyle.setAttribute("right", "0px");
                borderCssStyle.setAttribute("height", borderTopPixels + "px");
                borderCssStyle.setAttribute("width", borderRightPixels + "px");
                FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_TOP_RIGHT, border
                        .getFillImage(FillImageBorder.TOP_RIGHT), fillImageRenderFlags);
                borderDivElement.setAttribute("style", borderCssStyle.renderInline());
                windowDivElement.appendChild(borderDivElement);
            }
        }

        // Left Side
        if (borderLeftPixels > 0) {
            borderDivElement = document.createElement("div");
            borderDivElement.setAttribute("id", elementId + "_border_l");
            borderCssStyle = new CssStyle();
            borderCssStyle.setAttribute("font-size", "1px");
            ColorRender.renderToStyle(borderCssStyle, null, borderColor);
            borderCssStyle.setAttribute("position", "absolute");
            borderCssStyle.setAttribute("top", borderTopPixels + "px");
            borderCssStyle.setAttribute("left", "0px");
            borderCssStyle.setAttribute("width", borderLeftPixels + "px");
            borderCssStyle.setAttribute("bottom", borderBottomPixels + "px");
            FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_LEFT, border
                    .getFillImage(FillImageBorder.LEFT), fillImageRenderFlags);
            borderDivElement.setAttribute("style", borderCssStyle.renderInline());
            windowDivElement.appendChild(borderDivElement);
        }

        // Right Side
        if (borderRightPixels > 0) {
            borderDivElement = document.createElement("div");
            borderDivElement.setAttribute("id", elementId + "_border_r");
            borderCssStyle = new CssStyle();
            borderCssStyle.setAttribute("font-size", "1px");
            ColorRender.renderToStyle(borderCssStyle, null, borderColor);
            borderCssStyle.setAttribute("position", "absolute");
            borderCssStyle.setAttribute("top", borderTopPixels + "px");
            borderCssStyle.setAttribute("right", "0px");
            borderCssStyle.setAttribute("width", borderRightPixels + "px");
            borderCssStyle.setAttribute("bottom", borderBottomPixels + "px");
            FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_RIGHT, border
                    .getFillImage(FillImageBorder.RIGHT), fillImageRenderFlags);
            borderDivElement.setAttribute("style", borderCssStyle.renderInline());
            windowDivElement.appendChild(borderDivElement);
        }

        // Bottom
        if (borderBottomPixels > 0) {
            // Bottom Left Corner
            if (borderLeftPixels > 0) {
                borderDivElement = document.createElement("div");
                borderDivElement.setAttribute("id", elementId + "_border_bl");
                borderCssStyle = new CssStyle();
                borderCssStyle.setAttribute("font-size", "1px");
                ColorRender.renderToStyle(borderCssStyle, null, borderColor);
                borderCssStyle.setAttribute("position", "absolute");
                borderCssStyle.setAttribute("bottom", "0px");
                borderCssStyle.setAttribute("left", "0px");
                borderCssStyle.setAttribute("height", borderBottomPixels + "px");
                borderCssStyle.setAttribute("width", borderLeftPixels + "px");
                FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_BOTTOM_LEFT, border
                        .getFillImage(FillImageBorder.BOTTOM_LEFT), fillImageRenderFlags);
                borderDivElement.setAttribute("style", borderCssStyle.renderInline());
                windowDivElement.appendChild(borderDivElement);
            }
    
            // Bottom Side
            borderDivElement = document.createElement("div");
            borderDivElement.setAttribute("id", elementId + "_border_b");
            borderCssStyle = new CssStyle();
            borderCssStyle.setAttribute("font-size", "1px");
            ColorRender.renderToStyle(borderCssStyle, null, borderColor);
            borderCssStyle.setAttribute("position", "absolute");
            borderCssStyle.setAttribute("bottom", "0px");
            borderCssStyle.setAttribute("left", borderLeftPixels + "px");
            borderCssStyle.setAttribute("height", borderBottomPixels + "px");
            borderCssStyle.setAttribute("right", borderRightPixels + "px");
            FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_BOTTOM, border
                    .getFillImage(FillImageBorder.BOTTOM), fillImageRenderFlags);
            borderDivElement.setAttribute("style", borderCssStyle.renderInline());
            windowDivElement.appendChild(borderDivElement);
    
            // Bottom Right Side
            if (borderRightPixels > 0) {
                borderDivElement = document.createElement("div");
                borderDivElement.setAttribute("id", elementId + "_border_br");
                borderCssStyle = new CssStyle();
                borderCssStyle.setAttribute("font-size", "1px");
                ColorRender.renderToStyle(borderCssStyle, null, borderColor);
                borderCssStyle.setAttribute("position", "absolute");
                borderCssStyle.setAttribute("bottom", "0px");
                borderCssStyle.setAttribute("right", "0px");
                borderCssStyle.setAttribute("height", borderBottomPixels + "px");
                borderCssStyle.setAttribute("width", borderRightPixels + "px");
                FillImageRender.renderToStyle(borderCssStyle, rc, this, borderPane, IMAGE_ID_BORDER_BOTTOM_RIGHT, border
                        .getFillImage(FillImageBorder.BOTTOM_RIGHT), fillImageRenderFlags);
                borderDivElement.setAttribute("style", borderCssStyle.renderInline());
                windowDivElement.appendChild(borderDivElement);
            }
        }
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate,
     *      org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        // Do nothing.
    }

    /**
     * @see org.karora.cooee.webcontainer.DomUpdateSupport#renderHtml(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.w3c.dom.Node, org.karora.cooee.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        BorderPane borderPane = (BorderPane) component;
        String elementId = ContainerInstance.getElementId(borderPane);
        String bodyElementId = elementId + "_body";
        Component child = null;
        if (borderPane.getComponentCount() != 0) {
            child = borderPane.getComponent(0);
        }

        ServerMessage serverMessage = rc.getServerMessage();
        Document document = serverMessage.getDocument();
        Element borderPaneDivElement = document.createElement("div");
        borderPaneDivElement.setAttribute("id", elementId);

        // Create main window DIV element.
        CssStyle borderPaneDivCssStyle = new CssStyle();
        borderPaneDivCssStyle.setAttribute("padding", "0px");
        borderPaneDivCssStyle.setAttribute("overflow", "hidden");
        borderPaneDivCssStyle.setAttribute("position", "absolute");
        borderPaneDivCssStyle.setAttribute("top", "0px");
        borderPaneDivCssStyle.setAttribute("left", "0px");
        borderPaneDivCssStyle.setAttribute("width", "100%");
        borderPaneDivCssStyle.setAttribute("height", "100%");
        borderPaneDivElement.setAttribute("style", borderPaneDivCssStyle.renderInline());
        parentNode.appendChild(borderPaneDivElement);

        // Render BorderPane border.
        renderBorder(rc, borderPaneDivElement, borderPane);

        // Create BorderPane body DIV element (container of title and content
        // areas).
        Element borderPaneBodyDivElement = document.createElement("div");
        borderPaneBodyDivElement.setAttribute("id", bodyElementId);
        CssStyle windowBodyDivCssStyle = new CssStyle();
        ColorRender.renderToStyle(windowBodyDivCssStyle, (Color) borderPane.getRenderProperty(BorderPane.PROPERTY_FOREGROUND),
                (Color) borderPane.getRenderProperty(BorderPane.PROPERTY_BACKGROUND));
        FontRender.renderToStyle(windowBodyDivCssStyle, (Font) borderPane.getRenderProperty(BorderPane.PROPERTY_FONT));
        if (windowBodyDivCssStyle.getAttribute("background-color") == null) {
            windowBodyDivCssStyle.setAttribute("background-color", "white");
        }
        windowBodyDivCssStyle.setAttribute("position", "absolute");
        windowBodyDivCssStyle.setAttribute("overflow", "auto");
        windowBodyDivCssStyle.setAttribute("z-index", "2");
        FillImageBorder border = (FillImageBorder) borderPane.getRenderProperty(BorderPane.PROPERTY_BORDER, DEFAULT_BORDER);
        Insets contentInsets = border.getContentInsets() == null ? new Insets(0) : border.getContentInsets();
        int left = ExtentRender.toPixels(contentInsets.getLeft(), 0);
        int right = ExtentRender.toPixels(contentInsets.getRight(), 0);
        int top = ExtentRender.toPixels(contentInsets.getTop(), 0);
        int bottom = ExtentRender.toPixels(contentInsets.getBottom(), 0);
        windowBodyDivCssStyle.setAttribute("top", top + "px");
        windowBodyDivCssStyle.setAttribute("left", left + "px");
        windowBodyDivCssStyle.setAttribute("bottom", bottom + "px");
        windowBodyDivCssStyle.setAttribute("right", right + "px");
        
        borderPaneBodyDivElement.setAttribute("style", windowBodyDivCssStyle.renderInline());
        borderPaneDivElement.appendChild(borderPaneBodyDivElement);
        VirtualPosition.renderRegister(serverMessage, bodyElementId);

        // Create inset content DIV Element.
        Element contentDivElement = document.createElement("div");
        contentDivElement.setAttribute("id", elementId + "_content");
        CssStyle contentDivCssStyle = new CssStyle();
        if (child instanceof Pane) {
            contentDivCssStyle.setAttribute("position", "absolute");
            contentDivCssStyle.setAttribute("width", "100%");
            contentDivCssStyle.setAttribute("height", "100%");
        } else {
            // Render inset padding only if pane content is not itself a Pane.
            InsetsRender.renderToStyle(contentDivCssStyle, "padding", 
                    (Insets) borderPane.getRenderProperty(BorderPane.PROPERTY_INSETS));
        }
        contentDivElement.setAttribute("style", contentDivCssStyle.renderInline());
        borderPaneBodyDivElement.appendChild(contentDivElement);
        
        // Render child.
        if (child != null) {
            ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
            if (syncPeer instanceof DomUpdateSupport) {
                ((DomUpdateSupport) syncPeer).renderHtml(rc, update, contentDivElement, child);
            } else {
                syncPeer.renderAdd(rc, update, getContainerId(child), child);
            }
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return true;
    }
}
