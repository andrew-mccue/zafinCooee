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

import org.karora.cooee.app.Component;
import org.karora.cooee.app.Window;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.RootSynchronizePeer;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.WindowHtmlService;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.servermessage.WindowUpdate;


/**
 * Synchronization peer for <code>org.karora.cooee.app.Window</code> components.
 * <p>
 * This class should not be extended or used by classes outside of the
 * Echo framework.
 */
public class WindowPeer 
implements RootSynchronizePeer {

    private PartialUpdateManager partialUpdateManager;

    /**
     * Default constructor.
     */
    public WindowPeer() {
        super();
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(Window.PROPERTY_TITLE, new PartialUpdateParticipant() {
            
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
                Window window = (Window) update.getParent();
                String title = (String) window.getRenderProperty(Window.PROPERTY_TITLE);
                WindowUpdate.renderSetWindowTitle(rc.getServerMessage(), title);
            }
        });
    }
       
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String, org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update,
            String targetId, Component component) {
        throw new UnsupportedOperationException("Cannot add window.");
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component child) {
        return WindowHtmlService.ROOT_ID;
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        // Do nothing.
    }
    
    /**
     * @see org.karora.cooee.webcontainer.RootSynchronizePeer#renderRefresh(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.karora.cooee.app.Component)
     */
    public void renderRefresh(RenderContext rc, ServerComponentUpdate update, Component component) {
        Window window = (Window) component;
        
        String title = (String) window.getRenderProperty(Window.PROPERTY_TITLE);
        if (title != null) {
            WindowUpdate.renderSetWindowTitle(rc.getServerMessage(), title);
        }
        
        DomUpdate.renderElementRemoveChildren(rc.getServerMessage(), WindowHtmlService.ROOT_ID);
        Component[] addedChildren = window.getVisibleComponents();
        for (int i = 0; i < addedChildren.length; ++i) {
            ComponentSynchronizePeer childSyncPeer = SynchronizePeerFactory.getPeerForComponent(addedChildren[i].getClass());
            childSyncPeer.renderAdd(rc, update, WindowHtmlService.ROOT_ID, addedChildren[i]);
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullRefresh;
        if (update.hasAddedChildren() || update.hasRemovedChildren() || update.hasUpdatedLayoutDataChildren()) {
            fullRefresh = true;
        } else if (update.hasUpdatedProperties() && partialUpdateManager.canProcess(rc, update)) {
            fullRefresh = false;
        } else {
            fullRefresh = true;
        }
        
        if (fullRefresh) {
            renderRefresh(rc, update, update.getParent());
        } else {
            partialUpdateManager.process(rc, update);
        }
        
        return fullRefresh;
    }
}
