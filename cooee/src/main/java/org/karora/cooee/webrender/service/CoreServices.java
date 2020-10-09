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

package org.karora.cooee.webrender.service;

import javax.servlet.UnavailableException;

import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.ServiceRegistry;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.util.Resource;

/**
 * Static instances of the 'core' Web Render Engine <code>Service</code>s.
 */
public class CoreServices {

	/** Root path to core service <code>CLASSPATH</code> resources. */
	private static final String RESOURCE_PATH = "/org/karora/cooee/webrender/resource/";

	/**
	 * Store <code>ServiceRegistry</code> for reloading Client Engine service
	 */
	private static ServiceRegistry services;
	
	/**
	 * Custom JavaScript code to be loaded after Client Engine
	 * Following is a sample JavaScript code for replacing the application loading animation:
	 * <code>EchoClientEngine.renderCustomLoadStatus = function(loadStatusDivElement) {
	 *     var text = "";
	 *     for (var i = 0; i < EchoClientEngine.loadStatus; ++i) {
	 *         text += ".";
	 *     }
	 *     loadStatusDivElement.removeChild(loadStatusElement.firstChild);
	 *     loadStatusDivElement.appendChild(document.createTextNode(text));
	 * };</code>
	 */
	public static String CLIENT_ENGINE_EXTRAS = null;

	/** Client Engine JavaScript code. */
	public static final Service CLIENT_ENGINE = JavaScriptService.forResource("Echo.ClientEngine", RESOURCE_PATH + "ClientEngine.js");

	/**
	 * Installs the core services in the specified <code>ServiceRegistry</code>.
	 * 
	 * services the <code>ServiceRegistry</code>
	 */
	public static void install(ServiceRegistry services) {
		CoreServices.services = services;
		services.add(getClientEngine());
	}

	/** Non-instantiable class. */
	private CoreServices() {
	}

	/**
	 * This implementation is in place to reduce extra external request to server
	 * if there is any extra JavaScript code in {@link #CLIENT_ENGINE_EXTRAS}.
	 * 
	 * Extra code will be append to the end of the Client Engine {@link #CLIENT_ENGINE}
	 * 
	 * @return Client Engine service plus extras code if any ({@link #CLIENT_ENGINE_EXTRAS})
	 */
	private static Service getClientEngine() {
		if (CLIENT_ENGINE_EXTRAS!=null) {
	        String contentCE = Resource.getResourceAsString(RESOURCE_PATH + "ClientEngine.js") + CLIENT_ENGINE_EXTRAS;
	        return new JavaScriptService("Echo.ClientEngine", contentCE);
		}
		return CLIENT_ENGINE;
	}
	
	public static void reloadClientEngine() {
		if (services == null) return;
		if (services.get("Echo.ClientEngine") != null)
			services.remove(services.get("Echo.ClientEngine"));
		services.add(CoreServices.getClientEngine());
	}
}
