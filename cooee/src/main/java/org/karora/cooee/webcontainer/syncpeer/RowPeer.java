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
import org.karora.cooee.app.Font;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.LayoutData;
import org.karora.cooee.app.Row;
import org.karora.cooee.app.layout.RowLayoutData;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.RenderState;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.image.ImageRenderSupport;
import org.karora.cooee.webcontainer.partialupdate.BorderUpdate;
import org.karora.cooee.webcontainer.partialupdate.ColorUpdate;
import org.karora.cooee.webcontainer.partialupdate.InsetsUpdate;
import org.karora.cooee.webcontainer.propertyrender.AlignmentRender;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.CellLayoutDataRender;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.FontRender;
import org.karora.cooee.webcontainer.propertyrender.InsetsRender;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * Synchronization peer for <code>org.karora.cooee.app.Row</code> components.
 * <p>
 * This class should not be extended or used by classes outside of the
 * Echo framework.
 */
public class RowPeer 
implements ComponentSynchronizePeer, DomUpdateSupport, ImageRenderSupport {

    /**
     * <code>RenderState</code> implementation.
     */
    private static class RowPeerRenderState 
    implements RenderState {
        
        /**
         * The child <code>Component</code> which had the highest index during 
         * the last rendering.  This information is necessary when rendering 
         * cell spacing, as the last component will not have a "spacing" row
         * beneath it.  Thus, if it is no longer the last component due to an
         * add, one will need to be added beneath it. 
         */
        public Component lastChild;
    }
    
    private PartialUpdateManager partialUpdateManager;
    
    /**
     * Default constructor.
     */
    public RowPeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(Row.PROPERTY_BORDER, new BorderUpdate(Row.PROPERTY_BORDER, null,
                BorderUpdate.CSS_BORDER));
        partialUpdateManager.add(Row.PROPERTY_FOREGROUND, new ColorUpdate(Row.PROPERTY_FOREGROUND, null, 
                ColorUpdate.CSS_COLOR));
        partialUpdateManager.add(Row.PROPERTY_BACKGROUND, new ColorUpdate(Row.PROPERTY_BACKGROUND, null, 
                ColorUpdate.CSS_BACKGROUND_COLOR));
        partialUpdateManager.add(Row.PROPERTY_INSETS, new InsetsUpdate(Row.PROPERTY_INSETS, null, 
                InsetsUpdate.CSS_PADDING));
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        return ContainerInstance.getElementId(child.getParent()) + "_cell_" + ContainerInstance.getElementId(child);
    }
    
    /**
     * @see org.karora.cooee.webcontainer.image.ImageRenderSupport#getImage(org.karora.cooee.app.Component, java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        // Only source of ImageReferences from this component are CellLayoutData background images:
        // Delegate work to CellLayoutDataRender convenience method.
        return CellLayoutDataRender.getCellLayoutDataBackgroundImage(component, imageId);
    }

    /**
     * Returns the <code>RowLayoutData</code> of the given child, or null if
     * it does not provide layout data.
     * 
     * @param child the child component
     * @return the layout data
     * @throws java.lang.RuntimeException if the the provided
     *         <code>LayoutData</code> is not a <code>RowLayoutData</code>
     */
    private RowLayoutData getLayoutData(Component child) {
        LayoutData layoutData = (LayoutData) child.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
        if (layoutData == null) {
            return null;
        } else if (layoutData instanceof RowLayoutData) {
            return (RowLayoutData) layoutData;
        } else {
            throw new RuntimeException("Invalid LayoutData for Row Child: " + layoutData.getClass().getName());
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String, org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }
    
    /**
     * Renders a child component.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the update
     * @param parentElement the HTML element which should contain the child
     * @param child the child component to render
     */
    private void renderAddChild(RenderContext rc, ServerComponentUpdate update, Element parentElement, Component child) {
        ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
        if (syncPeer instanceof DomUpdateSupport) {
            ((DomUpdateSupport) syncPeer).renderHtml(rc, update, parentElement, child);
        } else {
            syncPeer.renderAdd(rc, update, getContainerId(child), child);
        }
    }
    
    /**
     * Renders child components which were added to a <code>Row</code>, as 
     * described in the provided <code>ServerComponentUpdate</code>.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the update
     */
    private void renderAddChildren(RenderContext rc, ServerComponentUpdate update) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        Row row = (Row) update.getParent();
        String elementId = ContainerInstance.getElementId(row);
        String trElementId = elementId + "_tr";
        
        Component[] components = update.getParent().getVisibleComponents();
        Component[] addedChildren = update.getAddedChildren();
        
        for (int componentIndex = components.length - 1; componentIndex >= 0; --componentIndex) {
            boolean childFound = false;
            for (int addedChildrenIndex = 0; !childFound && addedChildrenIndex < addedChildren.length; ++addedChildrenIndex) {
                if (addedChildren[addedChildrenIndex] == components[componentIndex]) {
                    DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
                    renderChild(rc, update, htmlFragment, row, components[componentIndex]);
                    if (componentIndex == components.length - 1) {
                        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, trElementId, htmlFragment);
                    } else {
                        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, trElementId, 
                                elementId + "_cell_" + ContainerInstance.getElementId(components[componentIndex + 1]), 
                                htmlFragment);
                    }
                    childFound = true;
                }
            }
        }
        
        // Special case: Recall the child which was rendered at the last index of the row on the previous
        // rendering.  If this child is still present but is no longer at the last index, render a spacing
        // cell after it (if necessary).
        RowPeerRenderState renderState = (RowPeerRenderState) rc.getContainerInstance().getRenderState(row);
        if (renderState != null && renderState.lastChild != null) {
            int previousLastChildIndex = row.visibleIndexOf(renderState.lastChild);
            if (previousLastChildIndex != -1 && previousLastChildIndex != row.getVisibleComponentCount() - 1) {
                // At this point it is known that the child which was previously last is present, but is no longer last.

                // In the event the child was removed and re-added, the special case is unnecessary.
                boolean lastChildMoved = false;
                for (int i = 0; i < addedChildren.length; ++i) {
                    if (renderState.lastChild == addedChildren[i]) {
                        lastChildMoved = true;
                    }
                }
                if (!lastChildMoved) {
                    DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
                    renderSpacingCell(htmlFragment, row, renderState.lastChild);
                    DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, trElementId,
                            elementId + "_cell_" + ContainerInstance.getElementId(components[previousLastChildIndex + 1]),
                            htmlFragment);
                }
            }
        }
    }
    
    /**
     * Renders an individual child component of the <code>Row</code>.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the <code>ServerComponentUpdate</code> being performed
     * @param parentNode the containing node to which the child should be 
     *        appended
     * @param child The child <code>Component</code> to be rendered
     */
    private void renderChild(RenderContext rc, ServerComponentUpdate update, Node parentNode, 
            Component component, Component child) {
        Document document = parentNode.getOwnerDocument();
        String childId = ContainerInstance.getElementId(child);
        Element tdElement = document.createElement("td");
        String cellId = ContainerInstance.getElementId(component) + "_cell_" + childId;
        tdElement.setAttribute("id", cellId);
        
        // Configure cell style.
        CssStyle cssStyle = new CssStyle();
        RowLayoutData layoutData = getLayoutData(child);
        CellLayoutDataRender.renderToElementAndStyle(tdElement, cssStyle, child, layoutData, "0px");
        CellLayoutDataRender.renderBackgroundImageToStyle(cssStyle, rc, this, component, child);
        if (layoutData != null) {
            ExtentRender.renderToStyle(cssStyle, "width", layoutData.getWidth());
        }
        tdElement.setAttribute("style", cssStyle.renderInline());
        
        parentNode.appendChild(tdElement);
        
        renderSpacingCell(parentNode, (Row) component, child);
        
        renderAddChild(rc, update, tdElement, child);
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) { }

    /**
     * @see org.karora.cooee.webcontainer.DomUpdateSupport#renderHtml(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.w3c.dom.Node, org.karora.cooee.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        Row row = (Row) component;
        Border border = (Border) row.getRenderProperty(Row.PROPERTY_BORDER);
        String elementId = ContainerInstance.getElementId(row);
        Document document = parentNode.getOwnerDocument();
        
        Element divElement = document.createElement("div");
        divElement.setAttribute("id", elementId);
        parentNode.appendChild(divElement);

        CssStyle divCssStyle = new CssStyle();
        BorderRender.renderToStyle(divCssStyle, border);
        ColorRender.renderToStyle(divCssStyle, (Color) row.getRenderProperty(Row.PROPERTY_FOREGROUND), 
                (Color) row.getRenderProperty(Row.PROPERTY_BACKGROUND));
        FontRender.renderToStyle(divCssStyle, (Font) row.getRenderProperty(Row.PROPERTY_FONT));
        Insets insets = (Insets) row.getRenderProperty(Row.PROPERTY_INSETS);
        if (insets == null) {
            divCssStyle.setAttribute("padding", "0px");
        } else {
            InsetsRender.renderToStyle(divCssStyle, "padding", insets);
        }
        divElement.setAttribute("style", divCssStyle.renderInline());
        
        Element tableElement = document.createElement("table");
        tableElement.setAttribute("id", elementId + "_table");
        tableElement.setAttribute("style", "padding:0px;border-collapse:collapse;");
        
        AlignmentRender.renderToElement(divElement, 
                ((Alignment) row.getRenderProperty(Row.PROPERTY_ALIGNMENT)), row);
        
        divElement.appendChild(tableElement);
        
        Element tbodyElement = document.createElement("tbody");
        tbodyElement.setAttribute("id", elementId + "_tbody");
        tableElement.appendChild(tbodyElement);
        
        Element trElement = document.createElement("tr");
        trElement.setAttribute("id", elementId + "_tr");
        tbodyElement.appendChild(trElement);
        
        Component[] children = row.getVisibleComponents();
        for (int i = 0; i < children.length; ++i) {
            renderChild(rc, update, trElement, component, children[i]);
        }
        
        storeRenderState(rc, row);
    }
    
    /**
     * Renders removal operations for child components which were removed from 
     * a <code>Row</code>, as described in the provided 
     * <code>ServerComponentUpdate</code>.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param update the update
     */
    private void renderRemoveChildren(RenderContext rc, ServerComponentUpdate update) {
        Component[] removedChildren = update.getRemovedChildren();
        Component parent = update.getParent();
        String parentId = ContainerInstance.getElementId(parent);
        for (int i = 0; i < removedChildren.length; ++i) {
            String childId = ContainerInstance.getElementId(removedChildren[i]);
            DomUpdate.renderElementRemove(rc.getServerMessage(), 
                    parentId + "_cell_" + childId);
            DomUpdate.renderElementRemove(rc.getServerMessage(), 
                    parentId + "_spacing_" + childId);
        }

        int componentCount = parent.getVisibleComponentCount();
        if (componentCount > 0) {
            DomUpdate.renderElementRemove(rc.getServerMessage(), parentId + "_spacing_" 
                    + ContainerInstance.getElementId(parent.getVisibleComponent(componentCount - 1)));
        }
    }
    
    /**
     * Renders a "spacing cell" beneath a row cell to provide
     * cell spacing.
     * 
     * @param parentNode the containing node to which the child
     *        should be appended
     * @param row the <code>Row</code> being updated
     * @param child the child preceeding the spacing row
     */
    private void renderSpacingCell(Node parentNode, Row row, Component child) {
        Extent cellSpacing = (Extent) row.getRenderProperty(Row.PROPERTY_CELL_SPACING);
        if (!ExtentRender.isZeroLength(cellSpacing) && row.visibleIndexOf(child) != row.getVisibleComponentCount() - 1) {
            Element spacingElement = parentNode.getOwnerDocument().createElement("td");
            spacingElement.setAttribute("id", ContainerInstance.getElementId(row) + "_spacing_" 
                    + ContainerInstance.getElementId(child));
            CssStyle spacingCssStyle = new CssStyle();
            spacingCssStyle.setAttribute("width", ExtentRender.renderCssAttributeValue(cellSpacing));
            spacingCssStyle.setAttribute("font-size", "1px");
            spacingCssStyle.setAttribute("line-height", "0px");
            spacingElement.setAttribute("style", spacingCssStyle.renderInline());
            parentNode.appendChild(spacingElement);
        }
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        // Determine if fully replacing the component is required.
        boolean fullReplace = false;
        if (update.hasUpdatedLayoutDataChildren()) {
            // TODO: Perform fractional update on LayoutData change instead of full replace.
            fullReplace = true;
        } else if (update.hasUpdatedProperties()) {
            if (!partialUpdateManager.canProcess(rc, update)) {
                fullReplace = true;
            }
        }
        
        if (fullReplace) {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        } else {
            // Perform incremental updates.
            if (update.hasRemovedChildren()) {
                renderRemoveChildren(rc, update);
            }
            if (update.hasUpdatedProperties()) {
                partialUpdateManager.process(rc, update);
            }
            if (update.hasAddedChildren()) {
                renderAddChildren(rc, update);
            }
        }
        
        storeRenderState(rc, update.getParent());
        return fullReplace;
    }

    /**
     * Update the stored <code>RenderState</code>.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param component the <code>Row</code> component
     */
    private void storeRenderState(RenderContext rc, Component component) {
        int componentCount = component.getVisibleComponentCount();
        RowPeerRenderState renderState = new RowPeerRenderState();
        if (componentCount > 0) {
            renderState.lastChild = component.getVisibleComponent(componentCount - 1);
        }
        rc.getContainerInstance().setRenderState(component, renderState);
    }
}
