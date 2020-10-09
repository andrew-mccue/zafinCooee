package org.karora.cooee.ng.ui.syncpeer;

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

/*
 * Much of this code was taken from the base Echo2 project on the 16/5/05 and
 * hence is Copyright (C) 2002-2005 NextApp, Inc..  This was done to re-use the 
 * base LabelEx code but with our EchoPoint differences  
 */

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

import org.karora.cooee.app.Alignment;
import org.karora.cooee.app.Border;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.Font;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Style;
import org.karora.cooee.app.text.TextComponent;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ActionProcessor;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.PropertyUpdateProcessor;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.propertyrender.AlignmentRender;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;
import org.karora.cooee.webrender.util.DomUtil;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.karora.cooee.ng.ButtonEx;
import org.karora.cooee.ng.EPNG;
import org.karora.cooee.ng.EditableLabelEx;
import org.karora.cooee.ng.LabelEx;
import org.karora.cooee.ng.ui.resource.Resources;
import org.karora.cooee.ng.ui.util.CssRolloverStyleEx;
import org.karora.cooee.ng.ui.util.ImageManager;
import org.karora.cooee.ng.ui.util.Render;
import org.karora.cooee.ng.ui.util.RenderingContext;
import org.karora.cooee.ng.ui.util.TriCellTable;
import org.karora.cooee.ng.util.HtmlKit;
import org.karora.cooee.ng.xhtml.XhtmlFragment;

/**
 * <code>LabelExPeer</code> is a peer for <code>LabelEx</code>
 */

public class LabelExPeer extends AbstractEchoPointContainerPeer implements ActionProcessor, PropertyUpdateProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service LABEL_SERVICE = JavaScriptService.forResource("EPNG.LabelEx", "/org/karora/cooee/ng/ui/resource/js/labelex.js");

	static {
		WebRenderServlet.getServiceRegistry().add(LABEL_SERVICE);
	}
	
	/**
	 * @see org.karora.cooee.webcontainer.SynchronizePeer#getContainerId(org.karora.cooee.app.Component)
	 */
	public String getContainerId(Component child) {
		return super.getContainerId(child);
	}

	/**
	 * @see org.karora.cooee.webcontainer.ActionProcessor#processAction(org.karora.cooee.webcontainer.ContainerInstance,
	 *      org.karora.cooee.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		throw new UnsupportedOperationException("Component does not support actions.");
	}
	
	/**
	 * @see org.karora.cooee.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(org.karora.cooee.webcontainer.ContainerInstance, org.karora.cooee.app.Component, org.w3c.dom.Element)
	 */
	public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
		String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
		if ("text".equals(propertyName)) {
			String propertyValue = DomUtil.getElementText(propertyElement);
			ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, "text", propertyValue);
		}
	}
	
	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderAdd(org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String, org.karora.cooee.app.Component)
	 */
	public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
		super.renderAdd(rc, update, targetId, component);
	}
	
	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext, org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		Component component = update.getParent();
		if (component instanceof EditableLabelEx) {
			// full replace always
			String removeTagetId = ContainerInstance.getElementId(component);
			DomUpdate.renderElementRemove(rc.getServerMessage(), removeTagetId);
			// we also have to get rid of the EditableContainer since its added directly next to the
			// Label and not in its on "container"
			if (component instanceof EditableLabelEx) {
				DomUpdate.renderElementRemove(rc.getServerMessage(),ContainerInstance.getElementId(component) + "|EditableContainer");
			}
			renderAdd(rc, update, targetId, component);
			return true;
		} else {
			return super.renderUpdate(rc,update,targetId);
		}
	}

	/**
	 * @see org.karora.cooee.webcontainer.SynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		 rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		 rc.getServerMessage().addLibrary(LABEL_SERVICE.getId());
		
		createDisposeDirective(rc.getServerMessage(),component);
		
		// we also have to get rid of the EditableContainer since its added directly next to the
		// Label and not in its on "container"
		if (component instanceof EditableLabelEx) {
			DomUpdate.renderElementRemove(rc.getServerMessage(),ContainerInstance.getElementId(component) + "|EditableContainer");
		}
	}

	/**
	 * 
	 */
	protected void createInitDirective(RenderingContext rc, Component component, Style fallbackStyle) {
		
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPLabel.MessageProcessor",
				"init", new String[0], new String[0]);

		Element itemElement = rc.getServerMessage().getDocument().createElement("item");
		itemizedUpdateElement.appendChild(itemElement);
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(component.isRenderEnabled()));
		
		if (component instanceof EditableLabelEx) {
			EditableLabelEx editableLabelEx = (EditableLabelEx) component;
			Component target = editableLabelEx.getTarget();

			boolean isTextComponent = TextComponent.class.isAssignableFrom(target.getClass());
			itemElement.setAttribute("editable", "true");
			itemElement.setAttribute("activationMethod", String.valueOf(rc.getRP(EditableLabelEx.PROPERTY_ACTIVATION_METHOD,fallbackStyle,EditableLabelEx.ACTIVATE_ON_DBLCLICK)));
			itemElement.setAttribute("isTextComponent", String.valueOf(isTextComponent));
			itemElement.setAttribute("synchronisedWithTarget", String.valueOf(rc.getRP(EditableLabelEx.PROPERTY_SYNCHRONIZED_WITH_TARGET,true)));
			itemElement.setAttribute("text", editableLabelEx.getText());
			itemElement.setAttribute("interpretNewlines", String.valueOf(rc.getRP(LabelEx.PROPERTY_INTERPRET_NEWLINES,fallbackStyle,false)));
			if (target != null) {
				itemElement.setAttribute("targetId", ContainerInstance.getElementId(target));
			}
		}
		
		renderTopLevelStyleSupport(rc,component,fallbackStyle,itemElement);
	}

	/**
	 *  
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		serverMessage.addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		serverMessage.addLibrary(LABEL_SERVICE.getId());

		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPLabel.MessageProcessor", "dispose",
				new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}
	
	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(org.karora.cooee.ng.ui.util.RenderingContext,
	 *      Node, org.karora.cooee.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);
		String id = ContainerInstance.getElementId(component);
		
		//
		// render label part
		Element labelContentE = renderLabelContent(rc,component,fallbackStyle);
		labelContentE.setAttribute("id", id);
		rc.addStandardWebSupport(labelContentE);
		CssStyle cssStyle = renderTopLevelStyleSupport(rc, component, fallbackStyle, null);
		labelContentE.setAttribute("style", cssStyle.renderInline());
		
		if (component instanceof EditableLabelEx) {
			// all into the one container
			Element editableContainerE = rc.createE("bdo");
			editableContainerE.setAttribute("id",id + "|EditableContainer");
			editableContainerE.setAttribute("style","display:none");
			
			Component[] children = component.getVisibleComponents();
			for (int i = 0; i < children.length; i++) {
				Component child = children[i];
				String containerId = getContainerId(child);
				Element containerTagElement = rc.createE("bdo");
				containerTagElement.setAttribute("id",containerId);
				editableContainerE.appendChild(containerTagElement);
				
				ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(child.getClass());
				if (syncPeer instanceof DomUpdateSupport) {
					((DomUpdateSupport) syncPeer).renderHtml(rc, rc.getServerComponentUpdate(), containerTagElement, child);
				} else {
					syncPeer.renderAdd(rc, rc.getServerComponentUpdate(), containerId, child);
				}
			}
			parent.appendChild(labelContentE);
			parent.appendChild(editableContainerE);
		} else {
			parent.appendChild(labelContentE);
		}
		
		// Register client script only if needed
		boolean roEnabled = rc.getRP(LabelEx.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false) && component.isRenderEnabled();
		
		if (roEnabled || component instanceof EditableLabelEx) {
			rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
			rc.addLibrary(LABEL_SERVICE);
			createInitDirective(rc, component, fallbackStyle);
		}
	}

	/**
	 * Called to render the CssStyle for the top level LabelEx element.  If the itemXML
	 * parameter is present then it also fills out the styles for rollovers etc..
	 * into that Element. 
	 */
	protected CssRolloverStyleEx renderTopLevelStyleSupport(RenderingContext rc, Component component, Style fallbackStyle, Element itemXML) {
		Object text = rc.getRP(LabelEx.PROPERTY_TEXT, fallbackStyle);
		ImageReference icon = (ImageReference) rc.getRP(LabelEx.PROPERTY_ICON, fallbackStyle);
		
		CssRolloverStyleEx cssStyle = new CssRolloverStyleEx(component, fallbackStyle);
		Render.asFillImage(cssStyle, component,LabelEx.PROPERTY_BACKGROUND_IMAGE, fallbackStyle,rc);
		if (! component.isRenderEnabled()) {
			Render.asColors(cssStyle,component,LabelEx.PROPERTY_DISABLED_BACKGROUND,ButtonEx.PROPERTY_DISABLED_FOREGROUND, fallbackStyle);
			Render.asFont(cssStyle,component,LabelEx.PROPERTY_DISABLED_FONT, fallbackStyle);
			Render.asBorder(cssStyle,component,LabelEx.PROPERTY_DISABLED_BORDER, fallbackStyle);
			Render.asFillImage(cssStyle,component,LabelEx.PROPERTY_DISABLED_BACKGROUND_IMAGE, fallbackStyle,rc);
			
			icon = (ImageReference) rc.getRP(LabelEx.PROPERTY_DISABLED_ICON, fallbackStyle);
		}
		if (!rc.getRP(LabelEx.PROPERTY_LINE_WRAP, fallbackStyle, false)) {
			cssStyle.setAttribute("white-space", "nowrap");
		}
		if (text != null && icon == null) {
			AlignmentRender.renderToStyle(cssStyle, (Alignment) rc.getRP(LabelEx.PROPERTY_TEXT_ALIGNMENT));
		}
		
		if (itemXML != null) {
			itemXML.setAttribute("rolloverEnabled","false");
			itemXML.setAttribute("defaultStyle",cssStyle.renderInline());
			if (icon != null) {
				itemXML.setAttribute("defaultIcon",ImageManager.getURI(rc,icon));
			}
		
			boolean rolloverEnabled = rc.getRP(LabelEx.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
			if (component.isRenderEnabled() && rolloverEnabled) {
				cssStyle.setRolloverBackground((Color) rc.getRP(LabelEx.PROPERTY_ROLLOVER_BACKGROUND,fallbackStyle));
				cssStyle.setRolloverForeground((Color) rc.getRP(LabelEx.PROPERTY_ROLLOVER_FOREGROUND, fallbackStyle));
				cssStyle.setRolloverFont((Font) rc.getRP(LabelEx.PROPERTY_ROLLOVER_FONT, fallbackStyle));
				cssStyle.setRolloverBorder((Border) rc.getRP(LabelEx.PROPERTY_ROLLOVER_BORDER, fallbackStyle));
				cssStyle.setRolloverBackgroundImage((FillImage) rc.getRP(LabelEx.PROPERTY_ROLLOVER_BACKGROUND_IMAGE, fallbackStyle));
				
				ImageReference rolloverIcon = null;
				if (icon != null) {
					rolloverIcon = (ImageReference) rc.getRP(LabelEx.PROPERTY_ROLLOVER_ICON, fallbackStyle);
				}			
				itemXML.setAttribute("rolloverEnabled","true");
				itemXML.setAttribute("rolloverStyle",cssStyle.renderRolloverSupportInline());
				if (rolloverIcon != null) {
					itemXML.setAttribute("rolloverIcon",ImageManager.getURI(rc,rolloverIcon));
				}
			}
		}
		return cssStyle;
	}


	/**
	 * Renders the content of the LabelEx, i.e., its text, icon, and/or state
	 * icon.
	 * 
	 * @param rc
	 *            the relevant <code>RenderingContext</code>
	 * @param component
	 *            the <code>LabelEx</code> being rendered
	 */
	protected Element renderLabelContent(RenderingContext rc, Component component, Style fallbackStyle) {
		boolean enabled = component.isRenderEnabled(); 
		Element contentNode = null;
		String elementId = ContainerInstance.getElementId(component);


		Object text = rc.getRP(LabelEx.PROPERTY_TEXT, fallbackStyle);
		ImageReference icon = (ImageReference) rc.getRP(LabelEx.PROPERTY_ICON, fallbackStyle);
		if (! enabled) {
			icon = (ImageReference) rc.getRP(LabelEx.PROPERTY_DISABLED_ICON, fallbackStyle);
		}

		// Text
		Node textNodes[] = null;
		if (text instanceof String) {
			int newLinePolicy = HtmlKit.NEWLINE_TRANSPARENT;
			if (rc.getRP(LabelEx.PROPERTY_INTERPRET_NEWLINES,fallbackStyle,false)) {
				newLinePolicy = HtmlKit.NEWLINE_TO_BR;
			}
			textNodes = HtmlKit.encodeNewLines(rc.getDocument(),(String) text,newLinePolicy);
		} else if (text instanceof XhtmlFragment) {
			//
			// parse the XhtmlFramement into
			XhtmlFragment fragment = (XhtmlFragment) text;
			try {
				textNodes = fragment.toDOM(rc.getDocument());
			} catch (Exception e) {
				throw new RuntimeException("The XhtmlFragment is not valid XHTML : " + fragment.getFragment(), e);
			}
		}
		// Icon
		Element iconElement = null;
		if (icon != null) {
			iconElement = ImageManager.createImgE(rc, icon);
			iconElement.setAttribute("id", elementId + "_icon");
		}

		int entityCount = (textNodes == null ? 0 : 1) + (iconElement == null ? 0 : 1);


		switch (entityCount) {
		case 1:
			if (textNodes != null) {
				Element spanE = rc.createE("span");
				spanE.setAttribute("id",elementId + "|Text");
				for (int i = 0; i < textNodes.length; i++) {
					renderCellText(rc, spanE, textNodes[i], component);
				}
				Element textE = rc.createE("div");
				textE.appendChild(spanE);
				contentNode = textE;
			} else {
				contentNode = iconElement;
			}
			break;
		case 2:
			Extent iconTextMargin = (Extent) rc.getRP(LabelEx.PROPERTY_ICON_TEXT_MARGIN, fallbackStyle);
			Alignment textPosition = (Alignment) rc.getRP(LabelEx.PROPERTY_TEXT_POSITION, fallbackStyle);
			int orientation = convertIconTextPositionToOrientation(textPosition, component);

			TriCellTable tct = new TriCellTable(rc.getDocument(), elementId, orientation, iconTextMargin);
			// render text
			for (int i = 0; i < textNodes.length; i++) {
				renderCellText(rc, tct.getTdElement(0), textNodes[i], component);
			}
			tct.getTdElement(0).setAttribute("id",elementId + "|Text");
			// icon next
			renderCellIcon(tct, iconElement, 1);

			Element tableElement = tct.getTableElement();
			tableElement.setAttribute("id", elementId + "_table");
			tableElement.setAttribute("cellpadding", "0");
			tableElement.setAttribute("cellspacing", "0");
			contentNode = tableElement;
			break;
		default:
			// 0 element label.
			Element spanE = rc.createE("span");
			spanE.setAttribute("id",elementId + "|Text");
			contentNode = spanE;
		}
		return contentNode;
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the label's icon.
	 * 
	 * @param tct
	 *            the <code>TriCellTable</code> to update
	 * @param textNode
	 *            the text
	 * @param cellIndex
	 *            the index of the cell in the <code>TriCellTable</code> that
	 *            should contain the icon
	 */
	protected void renderCellIcon(TriCellTable tct, Element iconElement, int cellIndex) {
		Element iconTdElement = tct.getTdElement(cellIndex);
		iconTdElement.appendChild(iconElement);
	}

	/**
	 * Renders the content of the <code>TriCellTable</code> cell which
	 * contains the label's text. Text is always rendered in cell #0 of the
	 * table.
	 * 
	 */
	protected void renderCellText(RenderingContext rc, Element parentE, Node textNode, Component label) {
		CssStyle textTdCssStyle = new CssStyle();
		AlignmentRender.renderToStyle(textTdCssStyle, (Alignment) rc.getRP(LabelEx.PROPERTY_TEXT_ALIGNMENT));
		if (rc.getRP(LabelEx.PROPERTY_LINE_WRAP, false) == false) {
			textTdCssStyle.setAttribute("white-space", "nowrap");
		}
		parentE.setAttribute("style", textTdCssStyle.renderInline());
		parentE.appendChild(textNode);
	}

	protected int convertIconTextPositionToOrientation(Alignment alignment, Component component) {
		if (alignment == null) {
			return TriCellTable.RIGHT_LEFT;
		}
		if (alignment.getVertical() == Alignment.DEFAULT) {
			if (alignment.getHorizontal() == Alignment.LEFT) {
				return TriCellTable.LEFT_RIGHT;
			} else {
				return TriCellTable.RIGHT_LEFT;
			}
		} else {
			if (alignment.getVertical() == Alignment.TOP) {
				return TriCellTable.TOP_BOTTOM;
			} else {
				return TriCellTable.BOTTOM_TOP;
			}
		}
	}
}
