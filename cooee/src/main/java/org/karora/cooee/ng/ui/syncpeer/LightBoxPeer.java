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
package org.karora.cooee.ng.ui.syncpeer;

import org.karora.cooee.app.Component;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Style;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

import org.karora.cooee.ng.ComponentEx;
import org.karora.cooee.ng.EPNG;
import org.karora.cooee.ng.LightBox;
import org.karora.cooee.ng.ui.util.ImageManager;
import org.karora.cooee.ng.ui.util.RenderingContext;

/**
 * <code>ComponentSynchronizePeer</code> implementation for the
 * <code>LightBox</code> component.  LightBoxPeer is a pure client side
 * implemented component.
 */
public class LightBoxPeer implements ComponentSynchronizePeer {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service LB_SERVICE = JavaScriptService.forResource("EPNG.LightBox", "/org/karora/cooee/ng/ui/resource/js/lightbox.js");

	static {
		WebRenderServlet.getServiceRegistry().add(LB_SERVICE);
	}
	
	protected PartialUpdateManager partialUpdateManager = new PartialUpdateManager();

	/**
	 * Constructs a <code>LightBoxPeer</code>
	 */
	public LightBoxPeer() {
		super();
		partialUpdateManager.add(LightBox.PROPERTY_HIDDEN, new PartialUpdateParticipant() {

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				renderHiddenDirective(rc, update.getParent());
			}
			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
		});
	}
	
	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
	 */
	public String getContainerId(Component component) {
		throw new UnsupportedOperationException("Component does not support children.");
	}
	
	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(LB_SERVICE.getId());
		renderInitDirective(new RenderingContext(rc,update,component), targetId, component);
	}

	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(LB_SERVICE.getId());
		renderDisposeDirective(rc, component);
	}
	
	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		boolean fullReplace = false;
    	if (update.hasUpdatedProperties()) {
    		if (! partialUpdateManager.canProcess(rc, update)) {
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
    	return fullReplace;
	}

	/**
	 * Renders a dispose directive.
	 */
	private void renderDisposeDirective(RenderContext rc, Component component) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPLightBox.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	private void renderHiddenDirective(RenderContext rc, Component component) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPLightBox.MessageProcessor",
				"hidden", new String[0], new String[0]);
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		boolean hidden = ComponentEx.getRenderProperty(component,LightBox.PROPERTY_HIDDEN,false);
		itemElement.setAttribute("hidden", String.valueOf(hidden));
	}

	
	/**
	 * Renders an initialization directive.
	 */
	private void renderInitDirective(RenderingContext rc, String containerId, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		
		String elementId = ContainerInstance.getElementId(component);
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemizedUpdateElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, "EPLightBox.MessageProcessor", "init");
		
		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		
		itemElement.setAttribute("eid", elementId);
		itemElement.setAttribute("container-eid", containerId);
		itemElement.setAttribute("hidden", String.valueOf(rc.getRP(LightBox.PROPERTY_HIDDEN,fallbackStyle,false)));
		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		
		ImageReference translucentImage = (ImageReference) rc.getRP(LightBox.PROPERTY_TRANSLUCENT_IMAGE,fallbackStyle);
		if (translucentImage != null) {
			itemElement.setAttribute("translucentImage", ImageManager.getURI(rc,translucentImage));
		}
	}

}