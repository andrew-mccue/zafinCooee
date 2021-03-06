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

import java.util.HashMap;
import java.util.Map;

import org.karora.cooee.app.Border;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.Font;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.ListBox;
import org.karora.cooee.app.list.AbstractListComponent;
import org.karora.cooee.app.list.ListCellRenderer;
import org.karora.cooee.app.list.ListModel;
import org.karora.cooee.app.list.ListSelectionModel;
import org.karora.cooee.app.list.StyledListCell;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ActionProcessor;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.FocusSupport;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PropertyUpdateProcessor;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.FontRender;
import org.karora.cooee.webcontainer.propertyrender.InsetsRender;
import org.karora.cooee.webrender.ClientProperties;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.servermessage.WindowUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;
import org.karora.cooee.webrender.util.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



/**
 * <code>ComponentSynchronizePeer</code> implementation for 
 * <code>AbstractListComponent</code>-based components.
 * <p>
 * This peer renders the content of list components in the
 * <code>ServerMessage</code>'s initialization section 
 * (<code>ServerMessage.GROUP_ID_INIT</code>) such that a single
 * rendering of content may be used by multiple list components
 * if possible. 
 * <p>
 * This class should not be extended or used by classes outside of the
 * Echo framework.
 */
public class ListComponentPeer 
implements ActionProcessor, ComponentSynchronizePeer, FocusSupport, PropertyUpdateProcessor {

    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service LIST_COMPONENT_SERVICE = JavaScriptService.forResource("Echo.ListComponent",
            "/org/karora/cooee/webcontainer/resource/js/ListComponent.js");

    static {
        WebRenderServlet.getServiceRegistry().add(LIST_COMPONENT_SERVICE);
    }
    
    private static final String PROPERTY_SELECTION = "selection";
    
    // Default Colors
    private static final Color DEFAULT_BACKGROUND = Color.WHITE;
    private static final Color DEFAULT_FOREGROUND = Color.BLACK;

    // Default Sizes
    private static final Extent DEFAULT_WIDTH = new Extent(100, Extent.PERCENT);
    private static final Insets DEFAULT_INSETS = new Insets(new Extent(0), new Extent(0));

    /**
     * Key for <code>Connection</code> property containing a <code>Map</code> which maps
     * RenderedModelData instances to ids.
     */
    private static final String RENDERED_MODEL_MAP_KEY 
            = "org.karora.cooee.webcontainer.syncpeer.ListComponentPeer.RenderedModelMap";

    private PartialUpdateManager partialUpdateManager;
    
    /**
     * A representation of the content of a list component, i.e., the 
     * contents of the <code>ListModel</code> after having been run through a 
     * <code>ListCellRenderer</code>.
     */
    private class RenderedModelData { 
        
        /**
         * An array containing the String values of the list component.
         */
        private String[] values;
        
        /**
         * An array containing the CSS style values of the list component.
         * May be null, or contain nulls representing list items without style.
         */
        private String[] styles;
        
        /**
         * Cached hash code. 
         */
        private int hashCode;

        /**
         * Creates a new <code>RenderedModelData</code> instance.
         * 
         * @param model the <code>ListModel</code>
         * @param renderer the <code>ListCellRenderer</code>
         */
        private RenderedModelData(AbstractListComponent listComponent) {
            ListModel model = listComponent.getModel();
            ListCellRenderer renderer = listComponent.getCellRenderer();
            
            int size = model.size();
            values = new String[size];
            for (int i = 0; i < values.length; ++i) {
                Object renderedValue = renderer.getListCellRendererComponent(listComponent, model.get(i), i);
                values[i] = renderedValue.toString();
                
                if (renderedValue instanceof StyledListCell) {
                    StyledListCell styledListCell = (StyledListCell) renderedValue;
                    CssStyle itemStyle = new CssStyle();
                    ColorRender.renderToStyle(itemStyle, styledListCell.getForeground(), styledListCell.getBackground());
                    FontRender.renderToStyle(itemStyle, styledListCell.getFont());
                    if (itemStyle.hasAttributes()) {
                        if (styles == null) {
                            styles = new String[size];
                        }
                        styles[i] = itemStyle.renderInline();
                    }
                }
            }
        }
        
        /**
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            if (hashCode == 0) {
                hashCode = values.length;
                for (int i = 0; i < values.length; ++i) {
                    if (values[i]  != null) {
                        hashCode ^= values[i].hashCode();
                    }
                }
                if (hashCode == 0) {
                    hashCode = 1;
                }
            }
            return hashCode;
        }
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            if (!(o instanceof RenderedModelData)) {
                return false;
            }
            RenderedModelData that = (RenderedModelData) o;
            if (this.values.length != that.values.length) {
                return false;
            }
            for (int i = 0; i < this.values.length; ++i) {
                if (!(this.values[i] == that.values[i] || (this.values[i] != null && this.values[i].equals(that.values[i])))) {
                    return false;
                }
            }
            if (this.styles != null || that.styles != null) {
                if (this.styles == null || that.styles == null) {
                    return false;
                }
                for (int i = 0; i < this.styles.length; ++i) {
                    if (!(this.styles[i] == that.styles[i] || (this.styles[i] != null && this.styles[i].equals(that.styles[i])))) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * Default constructor.
     */
    public ListComponentPeer() {
        super();
        partialUpdateManager = new PartialUpdateManager();   
    }

    /**
     * Appends the base style to the given style based off of properties on the
     * given <code>org.karora.cooee.app.AbstractListComponent</code>
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param listComponent the <code>org.karora.cooee.app.AbstractListComponent</code>
     */
    private CssStyle createListComponentCssStyle(RenderContext rc, AbstractListComponent listComponent) {
        CssStyle cssStyle = new CssStyle();

        boolean renderEnabled = listComponent.isRenderEnabled();

        Border border;
        Color foreground, background;
        Font font;
        if (!renderEnabled) {
            // Retrieve disabled style information.
            background = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_DISABLED_BACKGROUND);
            border = (Border) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_DISABLED_BORDER);
            font = (Font) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_DISABLED_FONT);
            foreground = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_DISABLED_FOREGROUND);

            // Fallback to normal styles.
            if (background == null) {
                background = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_BACKGROUND,
                        DEFAULT_BACKGROUND);
            }
            if (border == null) {
                border = (Border) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_BORDER);
            }
            if (font == null) {
                font = (Font) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_FONT);
            }
            if (foreground == null) {
                foreground = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_FOREGROUND,
                        DEFAULT_FOREGROUND);
            }
        } else {
            border = (Border) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_BORDER);
            foreground = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_FOREGROUND, DEFAULT_FOREGROUND);
            background = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_BACKGROUND, DEFAULT_BACKGROUND);
            font = (Font) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_FONT);
        }
        
        BorderRender.renderToStyle(cssStyle, border);
        ColorRender.renderToStyle(cssStyle, foreground, background);
        FontRender.renderToStyle(cssStyle, font);
        
        Extent width = (Extent) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_WIDTH, DEFAULT_WIDTH);
        if (rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_IE_SELECT_PERCENT_WIDTH)
                && width.getUnits() == Extent.PERCENT) {
            // Render default width. 
            width = null;
        }
        Extent height = (Extent) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_HEIGHT);
        Insets insets = (Insets) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_INSETS, DEFAULT_INSETS);

        InsetsRender.renderToStyle(cssStyle, "padding", insets);
        ExtentRender.renderToStyle(cssStyle, "width", width);
        ExtentRender.renderToStyle(cssStyle, "height", height);
        cssStyle.setAttribute("position", "relative");

        return cssStyle;
    }
    
    /**
     * Creates the rollover style based off of properties on the given
     * <code>org.karora.cooee.app.AbstractListComponent</code>
     * 
     * @param listComponent the <code>AbstractListComponent</code> instance
     * @return the style
     */
    private CssStyle createRolloverCssStyle(AbstractListComponent listComponent) {
        CssStyle style = new CssStyle();
        Color rolloverForeground = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_ROLLOVER_FOREGROUND);
        Color rolloverBackground = (Color) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_ROLLOVER_BACKGROUND);
        ColorRender.renderToStyle(style, rolloverForeground, rolloverBackground);
        FontRender.renderToStyle(style, (Font) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_ROLLOVER_FONT));
        return style;
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see org.karora.cooee.webcontainer.ActionProcessor#processAction(org.karora.cooee.webcontainer.ContainerInstance, 
     *      org.karora.cooee.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, AbstractListComponent.INPUT_ACTION, null);
    }

    /**
     * @see org.karora.cooee.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(org.karora.cooee.webcontainer.ContainerInstance, 
     *      org.karora.cooee.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        if (PROPERTY_SELECTION.equals(propertyName)) {
            Element[] itemElements = DomUtil.getChildElementsByTagName(propertyElement, "item");
            int[] selectedIndices = new int[itemElements.length];
            for (int i = 0; i < itemElements.length; ++i) {
                selectedIndices[i] = Integer.parseInt(itemElements[i].getAttribute("index"));
            }
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    AbstractListComponent.SELECTION_CHANGED_PROPERTY, selectedIndices);
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(
     *      org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.update.ServerComponentUpdate, 
     *      java.lang.String, org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(LIST_COMPONENT_SERVICE.getId());
        AbstractListComponent listComponent = (AbstractListComponent) component;
        renderInitDirective(rc, listComponent, targetId);
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(LIST_COMPONENT_SERVICE.getId());
        renderDisposeDirective(rc, (AbstractListComponent) component);
    }
    
    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to 
     * dispose the state of the <code>AbstractListComponent</code>, performing
     * tasks such as unregistering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param listComponent the <code>AbstractListComponent</code>
     */
    private void renderDisposeDirective(RenderContext rc, AbstractListComponent listComponent) {
        String elementId = ContainerInstance.getElementId(listComponent);
        ServerMessage serverMessage = rc.getServerMessage();
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, 
                "EchoListComponent.MessageProcessor", "dispose");
        initElement.setAttribute("eid", elementId);
    }

    /**
     * Renders content for an <code>AbstractListComponent</code> (if necessary); 
     * returns the content id.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param listComponent the <code>AbstractListComponent</code>
     * @return the content id
     */
    private String renderContent(RenderContext rc, AbstractListComponent listComponent) {
        RenderedModelData renderedModelData = new RenderedModelData(listComponent);
        
        Map renderedModelDataToIdMap = (Map) rc.getConnection().getProperty(RENDERED_MODEL_MAP_KEY);
        if (renderedModelDataToIdMap == null) {
            renderedModelDataToIdMap = new HashMap();
            rc.getConnection().setProperty(RENDERED_MODEL_MAP_KEY, renderedModelDataToIdMap);
        }
        String contentId = (String) renderedModelDataToIdMap.get(renderedModelData);
        if (contentId == null) {
            contentId = Integer.toString(renderedModelDataToIdMap.size());
            renderedModelDataToIdMap.put(renderedModelData, contentId);
            renderLoadContentDirective(rc, renderedModelData, contentId);
        }
        return contentId;
    }
    
    /**
     * Renders a directive to load the content (a rendered version of model)
     * to the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param renderedModelData the <code>RenderedModelData</code> object to render
     * @param contentId the content id to associate with the rendered content
     */
    private void renderLoadContentDirective(RenderContext rc, RenderedModelData renderedModelData, String contentId) {
        ServerMessage serverMessage = rc.getServerMessage();
        Element partElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_INIT, 
                "EchoListComponent.MessageProcessor", "load-content");
        partElement.setAttribute("content-id", contentId);
        Document document = serverMessage.getDocument();
        
        if (renderedModelData.styles != null) {
            partElement.setAttribute("styled", "true");
        }

        for (int i = 0; i < renderedModelData.values.length; ++i) {
            Element itemElement = document.createElement("item");
            itemElement.setAttribute("value", renderedModelData.values[i] == null 
                    ? "" : renderedModelData.values[i].toString());
            if (renderedModelData.styles != null) {
                itemElement.setAttribute("style", renderedModelData.styles[i] == null 
                        ? "" : renderedModelData.styles[i].toString());
            }
            
            partElement.appendChild(itemElement);
        }
    }
    
    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to 
     * render and intialize the state of a list component.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param listComponent the component
     * @param targetId the id of the container element
     */
    private void renderInitDirective(RenderContext rc, AbstractListComponent listComponent, String targetId) {
        String contentId = renderContent(rc, listComponent);
        String elementId = ContainerInstance.getElementId(listComponent);
        ServerMessage serverMessage = rc.getServerMessage();
        Document document = serverMessage.getDocument();

        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, 
                "EchoListComponent.MessageProcessor", "init");
        initElement.setAttribute("container-eid", targetId);
        initElement.setAttribute("eid", elementId);
        initElement.setAttribute("content-id", contentId);
        
        initElement.setAttribute("enabled", listComponent.isRenderEnabled() ? "true" : "false");
    
        if (listComponent.hasActionListeners()) {
            initElement.setAttribute("server-notify", "true");
        }
        
        CssStyle cssStyle = createListComponentCssStyle(rc, listComponent);
        initElement.setAttribute("style", cssStyle.renderInline());

        Boolean rolloverEnabled = (Boolean) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_ROLLOVER_ENABLED);
        if (Boolean.TRUE.equals(rolloverEnabled)) {
            CssStyle rolloverCssStyle = createRolloverCssStyle(listComponent);
            initElement.setAttribute("rollover-style", rolloverCssStyle.renderInline());
        }
        
        boolean multipleSelect = false;
        if (listComponent instanceof ListBox) {
            initElement.setAttribute("type", "list-box");
            ListBox listBox = (ListBox) listComponent;
            if (listBox.getSelectionMode() == ListSelectionModel.MULTIPLE_SELECTION) {
                initElement.setAttribute("multiple", "true");
                multipleSelect = true;
            }
        }

        if (listComponent.isFocusTraversalParticipant()) {
            initElement.setAttribute("tab-index", Integer.toString(listComponent.getFocusTraversalIndex()));
        } else {
            initElement.setAttribute("tab-index", "-1");
        }
        
        String toolTipText = (String) listComponent.getRenderProperty(AbstractListComponent.PROPERTY_TOOL_TIP_TEXT);
        if (toolTipText != null) {
            initElement.setAttribute("tool-tip", toolTipText);
        }
        
        // Render selection state.
        ListSelectionModel selectionModel = listComponent.getSelectionModel();
        int minIndex = selectionModel.getMinSelectedIndex();
        if (minIndex >= 0) {
            if (multipleSelect) {
                Element selectionElement = document.createElement("selection");
                int maxIndex = selectionModel.getMaxSelectedIndex();
                for (int i = minIndex; i <= maxIndex; ++i) {
                    if (selectionModel.isSelectedIndex(i)) {
                        Element itemElement = document.createElement("item");
                        itemElement.setAttribute("index", Integer.toString(i));
                        selectionElement.appendChild(itemElement);
                    }
                }
                initElement.appendChild(selectionElement);
            } else {
                initElement.setAttribute("selection-index", Integer.toString(minIndex));
            }
        }
    }
    
    /**
     * @see org.karora.cooee.webcontainer.FocusSupport#renderSetFocus(org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.Component)
     */
    public void renderSetFocus(RenderContext rc, Component component) {
        if (component.isEnabled()) {
            WindowUpdate.renderSetFocus(rc.getServerMessage(), ContainerInstance.getElementId(component));
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(
     *      org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    final static Boolean trueBoolean = new Boolean(true);// reduce garbabe collection
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        // Determine if this is a ListBox
		if( (update != null) && (update.getParent() != null) && (update.getParent().getClass() == ListBox.class) ){
			// this is a list box update
			Object obj = null;
			Boolean currentState = null;
			// is the application using the update control feature
			obj = update.getParent().getProperty("UPDATES_ARE_LOCKED");
			if( (obj != null) && (obj.getClass().equals(Boolean.class)) ){
				currentState = (Boolean) obj;
			}
			if( currentState != null ){
				// the feature is being used, have we done an update since the application unlocked the updates
				if( currentState.booleanValue() == true){
					// we are not to update the display, this prevents the list jumping around on
					// ActionListeners, Asynchronous tasks, etc.
					// System.out.println( "ListComponentPeer:renderUpdate:UPDATES_ARE_LOCKED is true");
					return true;
				}else{
					// set the list so that no further update will happen until the application unlocks the listbox
					// System.out.println( "ListComponentPeer:renderUpdate:setting UPDATES_ARE_LOCKED to true");
					update.getParent().setProperty("UPDATES_ARE_LOCKED", trueBoolean);
				}
			}
		}
        // Determine if fully replacing the component is required.
		if (partialUpdateManager.canProcess(rc, update)) {
            partialUpdateManager.process(rc, update);
        } else {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        }
        
        return true;
    }
}
