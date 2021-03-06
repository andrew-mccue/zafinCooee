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
import org.karora.cooee.app.TextArea;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webrender.ClientProperties;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.output.CssStyle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * Synchronization peer for <code>org.karora.cooee.app.TextArea</code> components.
 * <p>
 * This class should not be extended or used by classes outside of the
 * Echo framework.
 */
public class TextAreaPeer extends TextComponentPeer {

    /**
     * @see org.karora.cooee.webcontainer.DomUpdateSupport#renderHtml(org.karora.cooee.webcontainer.RenderContext, 
     *      org.karora.cooee.app.update.ServerComponentUpdate, org.w3c.dom.Node, org.karora.cooee.app.Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate addUpdate, Node parentNode, Component component) {
        TextArea textArea = (TextArea) component;
        String elementId = ContainerInstance.getElementId(component);
        
        ServerMessage serverMessage = rc.getServerMessage();
        serverMessage.addLibrary(TEXT_COMPONENT_SERVICE.getId());
        
        Element textAreaElement = parentNode.getOwnerDocument().createElement("textarea");
        textAreaElement.setAttribute("id", elementId);
        
        if (textArea.isFocusTraversalParticipant()) {
            textAreaElement.setAttribute("tabindex", Integer.toString(textArea.getFocusTraversalIndex()));
        } else {
            textAreaElement.setAttribute("tabindex", "-1");
        }
        
        String toolTipText = (String) textArea.getRenderProperty(TextArea.PROPERTY_TOOL_TIP_TEXT);
        if (toolTipText != null) {
            textAreaElement.setAttribute("title", toolTipText);
        }

        String value = textArea.getText();
        if (value != null) {
            if (!rc.getContainerInstance().getClientProperties().getBoolean(
                    ClientProperties.QUIRK_TEXTAREA_CONTENT)) {
                textAreaElement.appendChild(rc.getServerMessage().getDocument().createTextNode(value));
            }
        }

        CssStyle cssStyle = createBaseCssStyle(rc, textArea);
        if (cssStyle.hasAttributes()) {
            textAreaElement.setAttribute("style", cssStyle.renderInline());
        }
        
        parentNode.appendChild(textAreaElement);

        renderInitDirective(rc, textArea);
    }
}
