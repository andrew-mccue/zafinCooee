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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.karora.cooee.app.Component;
import org.karora.cooee.app.ResourceImageReference;
import org.karora.cooee.app.StreamImageReference;
import org.karora.cooee.app.TransitionPane;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webrender.Connection;
import org.karora.cooee.webrender.ContentType;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;
import org.w3c.dom.Element;

/**
 * <code>ComponentSynchronizePeer</code> implementation for synchronizing
 * <code>TransitionPane</code> components.
 */
public class TransitionPanePeer
implements ComponentSynchronizePeer {

    private PartialUpdateParticipant placeholderUpdateParticipant = new PartialUpdateParticipant() {
    
        /**
         * @see org.karora.cooee.webcontainer.PartialUpdateParticipant#renderProperty(org.karora.cooee.webcontainer.RenderContext,
         *       org.karora.cooee.app.update.ServerComponentUpdate)
         */
        public void renderProperty(RenderContext rc, ServerComponentUpdate update) { 
             // Do nothing.
        }
    
        /**
         * @see org.karora.cooee.webcontainer.PartialUpdateParticipant#canRenderProperty(org.karora.cooee.webcontainer.RenderContext, 
         *      org.karora.cooee.app.update.ServerComponentUpdate)
         */
        public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
            return true;
        }
    };
    
    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service TRANSITION_PANE_SERVICE = JavaScriptService.forResource("Echo2Extras.TransitionPane",
            "/org/karora/cooee/webcontainer/resource/js/TransitionPane.js");
    
    private static final Map IMAGE_ID_TO_RESOURCE = new HashMap();
    static {
        for (int i = 1; i <= 16; ++i) {
            IMAGE_ID_TO_RESOURCE.put("blind-black-" + i, new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/blindblack/Frame" + i + ".gif"));
        }
        IMAGE_ID_TO_RESOURCE.put("fade-000000-927", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_000000/Alpha927.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-000000-787", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_000000/Alpha787.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-000000-700", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_000000/Alpha700.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-000000-420", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_000000/Alpha420.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-000000-230", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_000000/Alpha230.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-ffffff-927", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_ffffff/Alpha927.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-ffffff-787", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_ffffff/Alpha787.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-ffffff-700", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_ffffff/Alpha700.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-ffffff-420", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_ffffff/Alpha420.png"));
        IMAGE_ID_TO_RESOURCE.put("fade-ffffff-230", new ResourceImageReference(
                    "/org/karora/cooee/webcontainer/resource/image/transition/fade_ffffff/Alpha230.png"));
    }
    
    public static final Service IMAGE_SERVICE = new Service() {
    
        /**
         * @see org.karora.cooee.webrender.Service#getId()
         */
        public String getId() {
            return "Echo2Extras.TransitionPane.Image";
        }           
    
        /**
         * @see org.karora.cooee.webrender.Service#getVersion()
         */
        public int getVersion() {
            return 0;
        }
    
        /**
         * @see org.karora.cooee.webrender.Service#service(org.karora.cooee.webrender.Connection)
         */
        public void service(Connection conn) 
        throws IOException {
            String imageId = conn.getRequest().getParameter("imageId");
            StreamImageReference imageReference = (StreamImageReference) IMAGE_ID_TO_RESOURCE.get(imageId);
            conn.setContentType(new ContentType(imageReference.getContentType(), true));
            imageReference.render(conn.getOutputStream());
        }
    };
    
    static {
        WebRenderServlet.getServiceRegistry().add(TRANSITION_PANE_SERVICE);
        WebRenderServlet.getServiceRegistry().add(IMAGE_SERVICE);
    }
    
    /**
     * The <code>PartialUpdateManager</code> for this synchronization peer.
     */
    private PartialUpdateManager partialUpdateManager;
    
    /**
     * Default constructor.
     */
    public TransitionPanePeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(TransitionPane.PROPERTY_TYPE, placeholderUpdateParticipant);
        partialUpdateManager.add(TransitionPane.PROPERTY_DURATION, placeholderUpdateParticipant);
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        return ContainerInstance.getElementId(child.getParent()) + "_content_" + child.getRenderId();
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String, org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(TRANSITION_PANE_SERVICE.getId());
        TransitionPane transitionPane = (TransitionPane) component;
        
        renderInitDirective(rc, transitionPane, targetId);
        if (transitionPane.getVisibleComponentCount() > 0) {
            Component child = transitionPane.getVisibleComponent(0);
            renderAddChild(rc, update, transitionPane, child);
        }
    }
    
    private void renderAddChild(RenderContext rc, ServerComponentUpdate update, 
            TransitionPane transitionPane, Component child) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        Element addChildElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_UPDATE, 
                "ExtrasTransitionPane.MessageProcessor", "add-child");
        addChildElement.setAttribute("eid", elementId);
        addChildElement.setAttribute("child-id", child.getRenderId());
        
        ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
        syncPeer.renderAdd(rc, update, getContainerId(child), child);
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(TRANSITION_PANE_SERVICE.getId());
        renderDisposeDirective(rc, (TransitionPane) component);
    }

    /**
     * Renders a dispose directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param transitionPane the <code>TransitionPane</code> being rendered
     */
    private void renderDisposeDirective(RenderContext rc, TransitionPane transitionPane) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        ServerMessage serverMessage = rc.getServerMessage();
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, 
                "ExtrasTransitionPane.MessageProcessor", "dispose");
        initElement.setAttribute("eid", elementId);
    }
    
    private String getTransitionTypeString(TransitionPane transitionPane) {
        Integer transitionType = (Integer) transitionPane.getProperty(TransitionPane.PROPERTY_TYPE);
        if (transitionType != null) {
            switch (transitionType.intValue()) {
            case TransitionPane.TYPE_CAMERA_PAN_LEFT:
                return "camera-pan-left";
            case TransitionPane.TYPE_CAMERA_PAN_RIGHT:
                return "camera-pan-right";
            case TransitionPane.TYPE_CAMERA_PAN_UP:
                return "camera-pan-up";
            case TransitionPane.TYPE_CAMERA_PAN_DOWN:
                return "camera-pan-down";
            case TransitionPane.TYPE_BLIND_BLACK_IN:
                return "blind-black-in";
            case TransitionPane.TYPE_BLIND_BLACK_OUT:
                return "blind-black-out";
            case TransitionPane.TYPE_FADE_TO_BLACK:
                return "fade-to-black";
            case TransitionPane.TYPE_FADE_TO_WHITE:
                return "fade-to-white";
            case TransitionPane.TYPE_FADE:
                return "fade";
            }
        }
        return null;
    }
    
    /**
     * Renders an initialization directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param transitionPane the <code>TransitionPane</code> being rendered
     */
    private void renderInitDirective(RenderContext rc, TransitionPane transitionPane, String targetId) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        ServerMessage serverMessage = rc.getServerMessage();
        Element partElement = serverMessage.addPart(ServerMessage.GROUP_ID_UPDATE, "ExtrasTransitionPane.MessageProcessor");
        Element initElement = serverMessage.getDocument().createElement("init");
        initElement.setAttribute("container-eid", targetId);
        initElement.setAttribute("eid", elementId);
        String type = getTransitionTypeString(transitionPane);
        if (type != null) {
            initElement.setAttribute("type", type);
        }
        partElement.appendChild(initElement);
    }
    
    private void renderRemoveChild(RenderContext rc, ServerComponentUpdate update, 
            TransitionPane transitionPane, Component child) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        Element removeChildElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_REMOVE, 
                "ExtrasTransitionPane.MessageProcessor", "remove-child");
        removeChildElement.setAttribute("eid", elementId);
        removeChildElement.setAttribute("child-id", child.getRenderId());
    }

    private void renderUpdateDirective(RenderContext rc, ServerComponentUpdate update, TransitionPane transitionPane) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        Element updateElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_UPDATE, 
                "ExtrasTransitionPane.MessageProcessor", "update");
        updateElement.setAttribute("eid", elementId);
        String type = getTransitionTypeString(transitionPane);
        if (type != null) {
            updateElement.setAttribute("type", type);
        }
        Integer duration = (Integer) transitionPane.getRenderProperty(TransitionPane.PROPERTY_DURATION);
        if (duration != null) {
            updateElement.setAttribute("duration", duration.toString());
        }
    }
    
    private void renderTransitionDirective(RenderContext rc, ServerComponentUpdate update, TransitionPane transitionPane) {
        String elementId = ContainerInstance.getElementId(transitionPane);
        Element transitionElement = rc.getServerMessage().appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, 
                "ExtrasTransitionPane.MessageProcessor", "transition");
        transitionElement.setAttribute("eid", elementId);
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        TransitionPane transitionPane = (TransitionPane) update.getParent();
        
        // Determine if fully replacing the component is required.
        boolean fullReplace = false;
        if (update.hasUpdatedLayoutDataChildren()) {
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
            if (update.hasUpdatedProperties()) {
                partialUpdateManager.process(rc, update);
                String[] propertyNames = update.getUpdatedPropertyNames();
                for (int i = 0; i < propertyNames.length; ++i) {
                    if (TransitionPane.PROPERTY_DURATION.equals(propertyNames[i]) ||
                            TransitionPane.PROPERTY_TYPE.equals(propertyNames[i])) {
                        renderUpdateDirective(rc, update, transitionPane);
                        break;
                    }
                }
            }
            
            // Perform incremental updates.
            if (update.hasRemovedChildren() || update.hasAddedChildren()) {
                if (update.hasRemovedChildren()) {
                    Component[] removedChildren = update.getRemovedChildren();
                    for (int i = 0; i < removedChildren.length; ++i) {
                        renderRemoveChild(rc, update, transitionPane, removedChildren[i]);
                    }
                }
                if (update.hasAddedChildren()) {
                    Component[] addedChildren = update.getAddedChildren();
                    if (addedChildren.length > 1) {
                        // Should not occur, sanity check.
                        throw new IllegalStateException("Multiple added children.:");
                    }
                    renderAddChild(rc, update, transitionPane, addedChildren[0]);
                }
                renderTransitionDirective(rc, update, transitionPane);
            }
        }
        
        return fullReplace;
    }
}
