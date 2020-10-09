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

package org.karora.cooee.webcontainer.syncpeer;

import org.karora.cooee.app.Alignment;
import org.karora.cooee.app.Border;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.Font;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.TextArea;
import org.karora.cooee.app.text.TextComponent;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ActionProcessor;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.FocusSupport;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.PropertyUpdateProcessor;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.image.ImageRenderSupport;
import org.karora.cooee.webcontainer.partialupdate.BorderUpdate;
import org.karora.cooee.webcontainer.partialupdate.ColorUpdate;
import org.karora.cooee.webcontainer.partialupdate.InsetsUpdate;
import org.karora.cooee.webcontainer.propertyrender.AlignmentRender;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.FillImageRender;
import org.karora.cooee.webcontainer.propertyrender.FontRender;
import org.karora.cooee.webcontainer.propertyrender.InsetsRender;
import org.karora.cooee.webcontainer.propertyrender.LayoutDirectionRender;
import org.karora.cooee.webrender.ClientProperties;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.servermessage.WindowUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;
import org.karora.cooee.webrender.util.DomUtil;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;



/**
 * Abstract base synchronization peer for the built-in
 * <code>org.karora.cooee.app.text.TextComponent</code> -derived components.
 * <p>
 * This class should not be extended or used by classes outside of the Echo
 * framework.
 */
public abstract class TextComponentPeer 
implements ActionProcessor, ComponentSynchronizePeer, DomUpdateSupport, FocusSupport, ImageRenderSupport, PropertyUpdateProcessor {

    private static final String IMAGE_ID_BACKGROUND = "background";

    /**
     * Service to provide supporting JavaScript library.
     */
    static final Service TEXT_COMPONENT_SERVICE = JavaScriptService.forResource("Echo.TextComponent",
            "/org/karora/cooee/webcontainer/resource/js/TextComponent.js");

    static {
        WebRenderServlet.getServiceRegistry().add(TEXT_COMPONENT_SERVICE);
    }
    
    /**
     * A <code>PartialUpdateParticipant</code> to update the text of
     * a text component.
     */
    private class TextUpdate
    implements PartialUpdateParticipant {
    
        /**
         * @see org.karora.cooee.webcontainer.PartialUpdateParticipant#canRenderProperty(org.karora.cooee.webcontainer.RenderContext, 
         *      org.karora.cooee.app.update.ServerComponentUpdate)
         */
        public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
            return true;
        }
    
        /**
         * @see org.karora.cooee.webcontainer.PartialUpdateParticipant#renderProperty(
         *      org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.update.ServerComponentUpdate)
         */
        public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
            TextComponent textComponent = (TextComponent) update.getParent();
            String elementId = ContainerInstance.getElementId(textComponent);
            ServerMessage serverMessage = rc.getServerMessage();
            Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                    "EchoTextComponent.MessageProcessor", "set-text", new String[0], new String[0]);
            Element itemElement = serverMessage.getDocument().createElement("item");
            itemElement.setAttribute("eid", elementId);
            itemElement.setAttribute("text", textComponent.getText());
            itemizedUpdateElement.appendChild(itemElement);
            
        }
    }

    private PartialUpdateManager partialUpdateManager;

    /**
     * Default constructor.
     */
    public TextComponentPeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(TextComponent.PROPERTY_FOREGROUND,
                new ColorUpdate(TextComponent.PROPERTY_FOREGROUND, null, ColorUpdate.CSS_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BACKGROUND,
                new ColorUpdate(TextComponent.PROPERTY_BACKGROUND, null, ColorUpdate.CSS_BACKGROUND_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BORDER,
                new BorderUpdate(TextComponent.PROPERTY_BORDER, null, BorderUpdate.CSS_BORDER));
        partialUpdateManager.add(TextComponent.PROPERTY_INSETS,
                new InsetsUpdate(TextComponent.PROPERTY_INSETS, null, InsetsUpdate.CSS_PADDING));
        partialUpdateManager.add(TextComponent.TEXT_CHANGED_PROPERTY, new TextUpdate());
    }

    /**
     * Creates a base <code>CssStyle</code> for properties common to text
     * components.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the text component
     * @return the style
     */
    protected CssStyle createBaseCssStyle(RenderContext rc, TextComponent textComponent) {
        CssStyle cssStyle = new CssStyle();

        boolean renderEnabled = textComponent.isRenderEnabled();

        Border border;
        Color foreground, background;
        Font font;
        FillImage backgroundImage;
        if (!renderEnabled) {
            // Retrieve disabled style information.
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BORDER);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FONT);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FOREGROUND);

            // Fallback to normal styles.
            if (background == null) {
                background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
                if (backgroundImage == null) {
                    // Special case:
                    // Disabled background without disabled background image will render disabled background instead of
                    // normal background image.
                    backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (border == null) {
                border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            }
            if (font == null) {
                font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            }
            if (foreground == null) {
                foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            }
        } else {
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
        }
        
        Alignment alignment = (Alignment) textComponent.getRenderProperty(TextComponent.PROPERTY_ALIGNMENT);
        if (alignment != null) {
            int horizontalAlignment = AlignmentRender.getRenderedHorizontal(alignment, textComponent);
            switch (horizontalAlignment) {
            case Alignment.LEFT:
                cssStyle.setAttribute("text-align", "left");
                break;
            case Alignment.CENTER:
                cssStyle.setAttribute("text-align", "center");
                break;
            case Alignment.RIGHT:
                cssStyle.setAttribute("text-align", "right");
                break;
            }
        }
        
        LayoutDirectionRender.renderToStyle(cssStyle, textComponent.getLayoutDirection(), textComponent.getLocale());
        BorderRender.renderToStyle(cssStyle, border);
        ColorRender.renderToStyle(cssStyle, foreground, background);
        FontRender.renderToStyle(cssStyle, font);
        FillImageRender.renderToStyle(cssStyle, rc, this, textComponent, IMAGE_ID_BACKGROUND, backgroundImage, 
                FillImageRender.FLAG_DISABLE_FIXED_MODE);
        
        InsetsRender.renderToStyle(cssStyle, "padding", (Insets) textComponent.getRenderProperty(TextComponent.PROPERTY_INSETS));
        
        Extent width = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_WIDTH);
        Extent height = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HEIGHT);

        if (width != null) {
            cssStyle.setAttribute("width", ExtentRender.renderCssAttributeValue(width));
        }

        if (height != null) {
            cssStyle.setAttribute("height", ExtentRender.renderCssAttributeValue(height));
        }
        return cssStyle;
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see org.karora.cooee.webcontainer.image.ImageRenderSupport#getImage(org.karora.cooee.app.Component,
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage;
            if (component.isRenderEnabled()) {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
            } else {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
                if (backgroundImage == null) {
                    backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else {
            return null;
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ActionProcessor#processAction(org.karora.cooee.webcontainer.ContainerInstance, 
     *      org.karora.cooee.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, TextComponent.INPUT_ACTION, null);
    }

    /**
     * @see org.karora.cooee.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
     *      org.karora.cooee.webcontainer.ContainerInstance,
     *      org.karora.cooee.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        if (TextComponent.TEXT_CHANGED_PROPERTY.equals(propertyName)) {
            String propertyValue = DomUtil.getElementText(propertyElement);
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.TEXT_CHANGED_PROPERTY, propertyValue);
        } else if (TextComponent.PROPERTY_HORIZONTAL_SCROLL.equals(propertyName)) {
            Extent propertyValue = new Extent(Integer.parseInt(
                    propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.PROPERTY_HORIZONTAL_SCROLL, propertyValue);
        } else if (TextComponent.PROPERTY_VERTICAL_SCROLL.equals(propertyName)) {
            Extent propertyValue = new Extent(Integer.parseInt(
                    propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE)));
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.PROPERTY_VERTICAL_SCROLL, propertyValue);
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
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate,
     *      org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        rc.getServerMessage().addLibrary(TEXT_COMPONENT_SERVICE.getId());
        renderDisposeDirective(rc, (TextComponent) component);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * dispose the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderDisposeDirective(RenderContext rc, TextComponent textComponent) {
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE,
                "EchoTextComponent.MessageProcessor", "dispose", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * initialize the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderInitDirective(RenderContext rc, TextComponent textComponent) {
        Extent horizontalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HORIZONTAL_SCROLL);
        Extent verticalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_VERTICAL_SCROLL);
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();

        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                "EchoTextComponent.MessageProcessor", "init", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        if (horizontalScroll != null && horizontalScroll.getValue() != 0) {
            itemElement.setAttribute("horizontal-scroll", ExtentRender.renderCssAttributePixelValue(horizontalScroll, "0"));
        }
        if (verticalScroll != null && verticalScroll.getValue() != 0) {
            itemElement.setAttribute("vertical-scroll", ExtentRender.renderCssAttributePixelValue(verticalScroll, "0"));
        }
        if (textComponent instanceof TextArea) {
            Integer maximumLength = (Integer) textComponent.getProperty(TextComponent.PROPERTY_MAXIMUM_LENGTH);
            if (maximumLength != null) {
                itemElement.setAttribute("maximum-length", maximumLength.toString());
            }
        }
        if (textComponent instanceof TextArea && rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_TEXTAREA_CONTENT)) {
            String value = textComponent.getText();
            if (value != null) {
                itemElement.setAttribute("text", value);
            }
        }
        if (!textComponent.isRenderEnabled()) {
            itemElement.setAttribute("enabled", "false");
        }
        if (textComponent.hasActionListeners()) {
            itemElement.setAttribute("server-notify", "true");
        }

        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * @see org.karora.cooee.webcontainer.FocusSupport#renderSetFocus(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.Component)
     */
    public void renderSetFocus(RenderContext rc, Component component) {
        WindowUpdate.renderSetFocus(rc.getServerMessage(), ContainerInstance.getElementId(component));
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullReplace = false;
        if (update.hasUpdatedProperties()) {
            if (!partialUpdateManager.canProcess(rc, update)) {
                fullReplace = true;
            }
        }

        if (fullReplace) {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        } else {
            partialUpdateManager.process(rc, update);
        }

        return false;
    }
}