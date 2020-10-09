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
import org.karora.cooee.app.ContentPane;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.Style;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.PropertyUpdateProcessor;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.service.JavaScriptService;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.karora.cooee.ng.ContainerEx;
import org.karora.cooee.ng.ContentPaneEx;
import org.karora.cooee.ng.EPNG;
import org.karora.cooee.ng.HttpPaneEx;
import org.karora.cooee.ng.able.BackgroundImageable;
import org.karora.cooee.ng.able.Stretchable;
import org.karora.cooee.ng.ui.resource.Resources;
import org.karora.cooee.ng.ui.util.CssStyleEx;
import org.karora.cooee.ng.ui.util.Render;
import org.karora.cooee.ng.ui.util.RenderingContext;

/**
 * <code>ContainerExPeer</code> is a peer for <code>ContainerEx</code> and <code>ContentPaneEx</code>
 */
public class ContainerExPeer extends AbstractEchoPointContainerPeer implements PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service CONTAINEREX_SERVICE = JavaScriptService.forResource("EPNG.ContainerEx", "/org/karora/cooee/ng/ui/resource/js/containerex.js");
	static {
		WebRenderServlet.getServiceRegistry().add(CONTAINEREX_SERVICE);
	}

	public ContainerExPeer() {
		super();
		partialUpdateManager.add(ContainerEx.PROPERTY_HORIZONTAL_SCROLL, new PartialUpdateParticipant() {

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				renderScrollDirective(rc, update.getParent(), true);
			}

			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
		});
		partialUpdateManager.add(ContentPane.PROPERTY_VERTICAL_SCROLL, new PartialUpdateParticipant() {

			public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
				renderScrollDirective(rc, update.getParent(), false);
			}

			public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
				return true;
			}
		});
	}

	/**
	 * @see org.karora.cooee.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(org.karora.cooee.webcontainer.ContainerInstance,
	 *      org.karora.cooee.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		if ("horizontalScroll".equals(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME))) {
			Extent newValue = ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE));
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, ContainerEx.PROPERTY_HORIZONTAL_SCROLL, newValue);
		} else if ("verticalScroll".equals(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME))) {
			Extent newValue = ExtentRender.toExtent(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE));
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, ContainerEx.PROPERTY_VERTICAL_SCROLL, newValue);
		}
	}

	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(org.karora.cooee.ng.ui.util.RenderingContext,
	 *      org.w3c.dom.Node, org.karora.cooee.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parentNode, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		CssStyleEx style = new CssStyleEx(component, fallbackStyle);
		Render.asBackgroundImageable(rc,style,(BackgroundImageable) component,fallbackStyle);
		//
		// this peer is shared by ContentPaneEx and hence it needs slightly different appearance
		// ContentPanes always fill their parents containers 100%!
		if (component instanceof ContentPaneEx) {
			style.setAttribute("position","absolute");
			style.setAttribute("width","100%");
			style.setAttribute("height","100%");
			style.setAttribute("overflow","auto");
		}
		Render.layoutFix(rc, style);
		
		Element paneE = rc.createE("div");
		parentNode.appendChild(paneE);
		paneE.setAttribute("id", rc.getElementId());
		paneE.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(paneE);
		
		if (component instanceof HttpPaneEx) {
			// render an IFRAME to display the URI
			style = new CssStyleEx();
			style.setAttribute("position","relative");
			style.setAttribute("width","100%");
			style.setAttribute("height","100%");
			style.setAttribute("border-style","none");

			Element iframeE = rc.createE("iframe");
			iframeE.setAttribute("style",style.renderInline());
			iframeE.setAttribute("src",(String)rc.getRP(HttpPaneEx.PROPERTY_URI,fallbackStyle));
			paneE.appendChild(iframeE);
		}

		Component[] visibleChildren = component.getVisibleComponents();
		for (int i = 0; i < visibleChildren.length; i++) {
			renderReplaceableChild(rc, rc.getServerComponentUpdate(), paneE, visibleChildren[i]);
		}

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(Resources.EP_STRETCH_SERVICE);
		rc.addLibrary(CONTAINEREX_SERVICE);
		renderInitDirective(rc, component);
	}
	
	/**
	 * a replacement that uses DIV instead of BDO for containership
	 * 
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderReplaceableChild(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate, org.w3c.dom.Node,
	 *      org.karora.cooee.app.Component)
	 */
	protected Element renderReplaceableChild(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component child) {
		Element containerTagElement = parentNode.getOwnerDocument().createElement("div");
		String containerId = getContainerId(child);
		containerTagElement.setAttribute("id", containerId);
		parentNode.appendChild(containerTagElement);
		ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
		if (syncPeer instanceof DomUpdateSupport) {
			((DomUpdateSupport) syncPeer).renderHtml(rc, update, containerTagElement, child);
		} else {
			syncPeer.renderAdd(rc, update, containerId, child);
		}
		CssStyle containerStyle = Render.itsDisplayLayoutData(rc,child);
		if (containerStyle.hasAttributes()) {
			Render.itsDisplayLayoutData(rc,child,containerTagElement);
			//
			// set the style on the outer container tag div
			containerTagElement.setAttribute("style", containerStyle.renderInline());
		}

		return containerTagElement;
	}

	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		rc.getServerMessage().addLibrary(Resources.EP_STRETCH_SERVICE.getId());
		rc.getServerMessage().addLibrary(CONTAINEREX_SERVICE.getId());
		renderDisposeDirective(rc, component);
	}

	/**
	 * 
	 */
	private void renderScrollDirective(RenderContext rc, Component component, boolean horizontal) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element scrollElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPContainerEx.MessageProcessor",
				horizontal ? "scroll-horizontal" : "scroll-vertical");
		Extent position = (Extent) component.getRenderProperty(horizontal ? ContainerEx.PROPERTY_HORIZONTAL_SCROLL
				: ContainerEx.PROPERTY_VERTICAL_SCROLL, new Extent(0));
		scrollElement.setAttribute("eid", ContainerInstance.getElementId(component));
		scrollElement.setAttribute("position", ExtentRender.renderCssAttributeValue(position));
	}

	/**
	 */
	private void renderDisposeDirective(RenderContext rc, Component component) {
		ServerMessage serverMessage = rc.getServerMessage();
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPContainerEx.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * 
	 */
	private void renderInitDirective(RenderingContext rc, Component component) {
		String elementId = ContainerInstance.getElementId(component);
		ServerMessage serverMessage = rc.getServerMessage();

		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPContainerEx.MessageProcessor",
				"init", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", elementId);
		Extent horizontalScroll = (Extent) component.getRenderProperty(ContainerEx.PROPERTY_HORIZONTAL_SCROLL);
		if (horizontalScroll != null && horizontalScroll.getValue() != 0) {
			itemElement.setAttribute("horizontal-scroll", ExtentRender.renderCssAttributeValue(horizontalScroll));
		}
		Extent verticalScroll = (Extent) component.getRenderProperty(ContainerEx.PROPERTY_VERTICAL_SCROLL);
		if (verticalScroll != null && verticalScroll.getValue() != 0) {
			itemElement.setAttribute("vertical-scroll", ExtentRender.renderCssAttributeValue(verticalScroll));
		}
		itemElement.setAttribute("heightStretched", String.valueOf(rc.getRP(Stretchable.PROPERTY_HEIGHT_STRETCHED, false)));
		itemElement.setAttribute("minimumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MINIMUM_STRETCHED_HEIGHT, null)));
		itemElement.setAttribute("maximumStretchedHeight", getExtentPixels(rc.getRP(Stretchable.PROPERTY_MAXIMUM_STRETCHED_HEIGHT, null)));
				
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	private String getExtentPixels(Object extent) {
		if (extent instanceof Extent) {
			return String.valueOf(((Extent)extent).getValue());
		}
		return null;
	}

}
