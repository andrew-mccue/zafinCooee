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

import org.karora.cooee.app.Border;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.Extent;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.Insets;
import org.karora.cooee.app.LayoutData;
import org.karora.cooee.app.Style;
import org.karora.cooee.app.Table;
import org.karora.cooee.app.layout.TableLayoutData;
import org.karora.cooee.app.table.TableColumnModel;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ActionProcessor;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.DomUpdateSupport;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.karora.cooee.webcontainer.image.ImageRenderSupport;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.CellLayoutDataRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.InsetsRender;
import org.karora.cooee.webrender.ClientProperties;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.service.JavaScriptService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.karora.cooee.ng.EPNG;
import org.karora.cooee.ng.LiveTable;
import org.karora.cooee.ng.table.LiveTableRenderer;
import org.karora.cooee.ng.ui.resource.Resources;
import org.karora.cooee.ng.ui.util.CssStyleEx;
import org.karora.cooee.ng.ui.util.RenderingContext;

/**
 * <code>LiveTablePeer</code> is the peer for <code>LiveTable</code>
 */
public class LiveTablePeer extends AbstractEchoPointContainerPeer implements ActionProcessor {

	/**
	 * Service to provide supporting JavaScript library.
	 */
	public static final Service LIVETABLE_SERVICE = JavaScriptService.forResource("EPNG.LiveTableJS", "/org/karora/cooee/ng/ui/resource/js/livetable.js");

	static {
		WebRenderServlet.getServiceRegistry().add(LIVETABLE_SERVICE);
	}

	public LiveTablePeer() {
		super();
	}

	/**
	 * Disposes of the LiveTable on the client.
	 */
	protected void createDisposeDirective(ServerMessage serverMessage, Component component) {
		Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE, "EPLiveTable.MessageProcessor",
				"dispose", new String[0], new String[0]);
		Element itemElement = serverMessage.getDocument().createElement("item");
		itemElement.setAttribute("eid", ContainerInstance.getElementId(component));
		itemizedUpdateElement.appendChild(itemElement);
	}

	/**
	 * Renders a directive to the outgoing <code>ServerMessage</code> to
	 * initialize the state of a LiveTable, performing tasks such as registering
	 * event listeners on the client and creating the JS object.
	 */
	protected void createInitDirective(RenderingContext rc, LiveTable liveTable, Style fallbackStyle) {
		Element itemizedUpdateElement = rc.getServerMessage().getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE, "EPLiveTable.MessageProcessor",
				"init", new String[0], new String[0]);

		LiveTableRenderer liveTableRenderer = (LiveTableRenderer) rc.getRP(LiveTable.PROPERTY_RENDERER, fallbackStyle);
		Document doc = rc.getServerMessage().getDocument();

		int pageFetchSize = rc.getRP(LiveTable.PROPERTY_PAGE_FETCH_SIZE, fallbackStyle, 2);
		int rowsPerPage = rc.getRP(LiveTable.PROPERTY_ROWS_PER_PAGE, fallbackStyle, 100);
		int totalRows = liveTable.getModel().getRowCount();

		Element itemElement = doc.createElement("item");
		itemElement.setAttribute("eid", rc.getElementId());
		itemElement.setAttribute("enabled", String.valueOf(liveTable.isRenderEnabled()));
		itemElement.setAttribute("rowsPerPage", String.valueOf(rowsPerPage));
		itemElement.setAttribute("totalRows", String.valueOf(totalRows));
		itemElement.setAttribute("pageFetchSize", String.valueOf(pageFetchSize));
		itemElement.setAttribute("isHeaderVisible", String.valueOf(liveTable.isHeaderVisible()));
		//----------------------
		// Style support
		//
		// These will be dynamically added on the client
		Element styles = doc.createElement("styles");
		itemElement.appendChild(styles);
		liveTableRenderer.renderStyles(liveTable, styles);

		//-------------------
		// Rollover support
		//
		boolean rolloverEnabled = rc.getRP(LiveTable.PROPERTY_ROLLOVER_ENABLED, fallbackStyle, false);
		itemElement.setAttribute("rolloverEnabled", String.valueOf(rolloverEnabled));
		if (rolloverEnabled) {
			String rolloverStyleName = liveTableRenderer.renderRolloverStyle(liveTable, styles, rc);
			itemElement.setAttribute("rolloverStyleName", String.valueOf(rolloverStyleName));
		}

		//-------------------
		// Selection support
		//
		boolean selectionEnabled = rc.getRP(LiveTable.PROPERTY_SELECTION_ENABLED, fallbackStyle, false);
		itemElement.setAttribute("selectionEnabled", String.valueOf(selectionEnabled));
		if (selectionEnabled) {
			String selectionStyleName = liveTableRenderer.renderSelectionStyle(liveTable, styles, rc);
			itemElement.setAttribute("selectionStyleName", String.valueOf(selectionStyleName));
		}

		//----------------------
		// Empty row example. Its replicated on the
		// the client so we dont have to send down 1000's of
		// empty rows.
		//
		Element emptyRow = doc.createElement("emptyRow");
		itemElement.appendChild(emptyRow);
		liveTableRenderer.renderEmptyRow(liveTable, emptyRow);

		itemizedUpdateElement.appendChild(itemElement);
	}

	private TableLayoutData getLayoutData(Component child) {
		LayoutData layoutData = (LayoutData) child.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
		if (layoutData == null) {
			return null;
		} else if (layoutData instanceof TableLayoutData) {
			return (TableLayoutData) layoutData;
		} else {
			throw new RuntimeException("Invalid LayoutData for LiveTable Child: " + layoutData.getClass().getName());
		}
	}

	/**
	 * @see org.karora.cooee.webcontainer.ActionProcessor#processAction(org.karora.cooee.webcontainer.ContainerInstance,
	 *      org.karora.cooee.app.Component, org.w3c.dom.Element)
	 */
	public void processAction(ContainerInstance ci, Component component, Element actionElement) {
		String actionName = actionElement.getAttribute(ActionProcessor.ACTION_NAME);
		String actionValue = actionElement.getAttribute(ActionProcessor.ACTION_VALUE);
		ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, actionName, actionValue);
	}

	/**
	 * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate,
	 *      org.karora.cooee.app.Component)
	 */
	public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
		super.renderDispose(rc, update, component);
		 rc.getServerMessage().addLibrary(Resources.EP_SCRIPT_SERVICE.getId());
		 rc.getServerMessage().addLibrary(LIVETABLE_SERVICE.getId());
		createDisposeDirective(rc.getServerMessage(), component);
	}

	private void renderHeaderRow(RenderingContext rc, Element tbodyElement, LiveTable liveTable, String defaultInsetsAttributeValue) {
		Document document = tbodyElement.getOwnerDocument();
		String elementId = ContainerInstance.getElementId(liveTable);

		Extent rowHeight = (Extent) EPNG.getRP(liveTable,LiveTable.PROPERTY_ROW_HEIGHT);
		String heightCss = "height:" + ExtentRender.renderCssAttributeValue(rowHeight) + ";";
		
		Element trElement = document.createElement("tr");
		tbodyElement.appendChild(trElement);
		trElement.setAttribute("id", elementId + "_tr_header");
		trElement.setAttribute("style", heightCss);

		int columns = liveTable.getColumnModel().getColumnCount();
		for (int columnIndex = 0; columnIndex < columns; ++columnIndex) {
			Component childComponent = liveTable.getCellComponent(columnIndex, Table.HEADER_ROW);
			Element tdElement = document.createElement("td");
			tdElement.setAttribute("id", elementId + "header_cell_" + childComponent.getRenderId());

			CssStyle tdCssStyle = new CssStyle();
			tdCssStyle.setAttribute("border-collapse","collapse");
			BorderRender.renderToStyle(tdCssStyle, (Border) EPNG.getRP(liveTable,Table.PROPERTY_BORDER));
			CellLayoutDataRender.renderToElementAndStyle(tdElement, tdCssStyle, childComponent, getLayoutData(childComponent),
					defaultInsetsAttributeValue);

			ImageRenderSupport irs = new ImageRenderSupport() {
				/**
				 * @see org.karora.cooee.webcontainer.image.ImageRenderSupport#getImage(org.karora.cooee.app.Component,
				 *      java.lang.String)
				 */
				public ImageReference getImage(Component component, String imageId) {
					return CellLayoutDataRender.getCellLayoutDataBackgroundImage(component, imageId);
				}
			};
			CellLayoutDataRender.renderBackgroundImageToStyle(tdCssStyle, rc, irs, liveTable, childComponent);
			tdElement.setAttribute("style", tdCssStyle.renderInline());

			trElement.appendChild(tdElement);

			if (childComponent.isRenderVisible()) {
				ComponentSynchronizePeer syncPeer = SynchronizePeerFactory.getPeerForComponent(childComponent.getClass());
				if (syncPeer instanceof DomUpdateSupport) {
					((DomUpdateSupport) syncPeer).renderHtml(rc, rc.getServerComponentUpdate(), tdElement, childComponent);
				} else {
					syncPeer.renderAdd(rc, rc.getServerComponentUpdate(), getContainerId(childComponent), childComponent);
				}
			}
		}
	}

	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointPeer#renderHtml(org.karora.cooee.ng.ui.util.RenderingContext,
	 *      Node, org.karora.cooee.app.Component)
	 */
	public void renderHtml(RenderingContext rc, Node parent, Component component) {
		Style fallbackStyle = EPNG.getFallBackStyle(component);

		LiveTable liveTable = (LiveTable) component;
		LiveTableRenderer liveTableRenderer = (LiveTableRenderer) rc.getRP(LiveTable.PROPERTY_RENDERER, fallbackStyle);

		createInitDirective(rc, liveTable, fallbackStyle);

		rc.addLibrary(Resources.EP_SCRIPT_SERVICE);
		rc.addLibrary(LIVETABLE_SERVICE);

		String elementId = rc.getElementId();
		CssStyleEx style = new CssStyleEx(component, fallbackStyle);
		//style.setAttribute("border", null);

		Element scrollerDivE = rc.createE("div");
		scrollerDivE.setAttribute("id", elementId);
		scrollerDivE.setAttribute("style", style.renderInline());
		rc.addStandardWebSupport(scrollerDivE);
		parent.appendChild(scrollerDivE);

		Element tableE = rc.createE("table");
		scrollerDivE.appendChild(tableE);
		tableE.setAttribute("cellpadding", "0");
		tableE.setAttribute("cellspacing", "0");
		tableE.setAttribute("border", "0");

		CssStyle tableCssStyle = new CssStyle();
		tableCssStyle.setAttribute("border-collapse", "collapse");
		tableCssStyle.setAttribute("width", "95%");

		Insets tableInsets = (Insets) rc.getRP(Table.PROPERTY_INSETS, fallbackStyle);
		String defaultInsetsAttributeValue = tableInsets == null ? "0px" : InsetsRender.renderCssAttributeValue(tableInsets);

		Border border = (Border) rc.getRP(Table.PROPERTY_BORDER, fallbackStyle);
		Extent borderSize = border == null ? null : border.getSize();

		if (rc.getRP(Table.PROPERTY_SELECTION_ENABLED, fallbackStyle, false)) {
			tableCssStyle.setAttribute("cursor", "pointer");
		}
		BorderRender.renderToStyle(tableCssStyle, border);
		if (borderSize != null) {
			if (!rc.getContainerInstance().getClientProperties().getBoolean(ClientProperties.QUIRK_CSS_BORDER_COLLAPSE_INSIDE)) {
				tableCssStyle.setAttribute("margin", ExtentRender.renderCssAttributeValueHalf(borderSize));
			}
		}
		tableE.setAttribute("style", tableCssStyle.renderInline());

		TableColumnModel columnModel = liveTable.getColumnModel();
		int columnCount = columnModel.getColumnCount();

		boolean someColumnsHaveWidths = false;
		for (int i = 0; i < columnCount; ++i) {
			if (columnModel.getColumn(i).getWidth() != null) {
				someColumnsHaveWidths = true;
			}
		}
		if (false && someColumnsHaveWidths) {
			Element colGroupElement = rc.createE("colgroup");
			tableE.appendChild(colGroupElement);
			for (int i = 0; i < columnCount; ++i) {
				Element colElement = rc.createE("col");
				Extent columnWidth = columnModel.getColumn(i).getWidth();
				if (columnWidth != null) {
					colElement.setAttribute("width", ExtentRender.renderCssAttributeValue(columnWidth));
				}
				colGroupElement.appendChild(colElement);
			}
		}

		//
		// draw the first n pages of data based on pageFetchSize
		int rowsPerPage = rc.getRP(LiveTable.PROPERTY_ROWS_PER_PAGE, fallbackStyle, 100);
		int pageFetchSize = rc.getRP(LiveTable.PROPERTY_PAGE_FETCH_SIZE, fallbackStyle, 2);

		for (int i = 0; i < pageFetchSize; i++) {
			Element tbodyE = rc.createE("tbody");
			tableE.appendChild(tbodyE);

			// do we have a header
			if (false && i == 0 && liveTable.isHeaderVisible()) {
				renderHeaderRow(rc, tbodyE, liveTable, defaultInsetsAttributeValue);
			}
			int startRow = i * rowsPerPage;
			int endRow = startRow + rowsPerPage;
			liveTableRenderer.renderRows(liveTable, tbodyE, startRow, endRow);
		}
	}

	/**
	 * @see org.karora.cooee.ng.ui.syncpeer.AbstractEchoPointContainerPeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
	 *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
	 */
	public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
		return renderUpdateBaseImpl(rc, update, targetId, true);
	}
}
