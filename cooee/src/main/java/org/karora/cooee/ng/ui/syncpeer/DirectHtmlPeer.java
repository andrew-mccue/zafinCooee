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
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Element;

import org.karora.cooee.ng.DirectHtml;

/**
 * <code>ComponentSynchronizePeer</code> implementation for the
 * <code>DirectHtml</code> component.
 */
public class DirectHtmlPeer implements ComponentSynchronizePeer {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service DH_SERVICE = JavaScriptService.forResource("EPNG.DirectHtml", "/org/karora/cooee/ng/ui/resource/js/directhtml.js");

	static {
		WebRenderServlet.getServiceRegistry().add(DH_SERVICE);
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
		serverMessage.addLibrary(DH_SERVICE.getId());
		renderInitDirective(rc, targetId, component);
	}

	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		/*
		 * We dont have to do this actually because there is no state 
		 * to be kept on the client.  But lets keep the code just in case.
		 *
		ServerMessage serverMessage = rc.getServerMessage();
		serverMessage.addLibrary(DH_SERVICE.getId());
		renderDisposeDirective(rc, component);
		 */
	}

	/**
	 * Renders a dispose directive.
	private void renderDisposeDirective(RenderContext rc, Component component) {
		String elementId = ContainerInstance.getElementId(component);
		ServerMessage serverMessage = rc.getServerMessage();
		Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPDirectHtml.MessageProcessor", "dispose");
		initElement.setAttribute("eid", elementId);
	}
	*/
	
	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		// Perform full update.
		DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
		renderAdd(rc, update, targetId, update.getParent());
		return true;
	}
	
	/**
	 * Renders an initialization directive.
	 */
	private void renderInitDirective(RenderContext rc, String containerId, Component component) {
		String elementId = ContainerInstance.getElementId(component);
		ServerMessage serverMessage = rc.getServerMessage();
		Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, "EPDirectHtml.MessageProcessor", "init");
		initElement.setAttribute("eid", elementId);
		initElement.setAttribute("container-eid", containerId);
		if (!component.isRenderEnabled()) {
			initElement.setAttribute("enabled", "false");
		}
		String text = (String) component.getRenderProperty(DirectHtml.PROPERTY_TEXT,"");
		CDATASection cdata = rc.getServerMessage().getDocument().createCDATASection(text);
		initElement.appendChild(cdata);
	}

}