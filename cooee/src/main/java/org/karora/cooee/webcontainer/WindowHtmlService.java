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

package org.karora.cooee.webcontainer;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.karora.cooee.app.ApplicationInstance;
import org.karora.cooee.webrender.BaseHtmlDocument;
import org.karora.cooee.webrender.Connection;
import org.karora.cooee.webrender.ContentType;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.service.CoreServices;
import org.w3c.dom.Element;



/**
 * Completely re-renders a browser window.
 * This is the default service invoked when the user visits an application.
 */
public class WindowHtmlService 
implements Service {
    
    public static final WindowHtmlService INSTANCE = new WindowHtmlService();

    /**
     * Root element identifier.
     */
    public static final String ROOT_ID = "c_root";
    
    /**
     * @see org.karora.cooee.webrender.Service#getId()
     */
    public String getId() {
        return WebRenderServlet.SERVICE_ID_DEFAULT;
    }

    /**
     * @see org.karora.cooee.webrender.Service#getVersion()
     */
    public int getVersion() {
        return DO_NOT_CACHE;
    }

    /**
     * @see org.karora.cooee.webrender.Service#service(org.karora.cooee.webrender.Connection)
     */
    public void service(Connection conn) throws IOException {
        ContainerInstance ci = (ContainerInstance) conn.getUserInstance();
        conn.setContentType(ContentType.TEXT_HTML);
        
        boolean debug = !("false".equals(conn.getServlet().getInitParameter("echo2.debug")));

        BaseHtmlDocument baseDoc = new BaseHtmlDocument(ROOT_ID);
     
        //System.out.println("Cooee:WindowHtmlService: Adding Link for icon");
        Element iconLinkElement = baseDoc.getDocument().createElement("link");
        iconLinkElement.setAttribute("rel", "icon");
        iconLinkElement.setAttribute("type", "image/png"); 
    	// Zafin
    	// Andrew McCue
        // Browsers do not respect cache control for favIcons so we need to change the favicon
        // 'name' periodically. So I append the yyyymmddhhmm value to the name as a query parameter
        // The miOSS servlet will process this request directly and ignore the yyyymmddhhmm parameter
        String iconLink = null;
        if( ci.getServletUri().endsWith("/") ){
        	iconLink = ci.getServletUri()+"tabIcon.png?"+getTimeToMinute();
        }else{
        	iconLink = ci.getServletUri()+"/tabIcon.png?"+getTimeToMinute();
        }
        iconLinkElement.setAttribute("href", iconLink);
        baseDoc.getHeadElement().appendChild(iconLinkElement);
        //System.out.println("Cooee:WindowHtmlService: Added Link for icon["+iconLink+"]");
        
        
        baseDoc.setGenarator(ApplicationInstance.ID_STRING);
        baseDoc.addJavaScriptInclude(ci.getServiceUri(CoreServices.CLIENT_ENGINE));

        // Add initialization directive.
        baseDoc.getBodyElement().setAttribute("onload", "EchoClientEngine.init('" + ci.getServletUri() + "', " 
                + debug + ");");
        
        Element bodyElement = baseDoc.getBodyElement(); 
        
        // Set body element CSS style.
        CssStyle cssStyle = new CssStyle();
        cssStyle.setAttribute("position", "absolute");
        cssStyle.setAttribute("font-family", "verdana, arial, helvetica, sans-serif");
        cssStyle.setAttribute("font-size", "10pt");
        cssStyle.setAttribute("height", "100%");
        cssStyle.setAttribute("width", "100%");
        cssStyle.setAttribute("padding", "0px");
        cssStyle.setAttribute("margin", "0px");
        cssStyle.setAttribute("overflow", "hidden");
        bodyElement.setAttribute("style", cssStyle.renderInline());
        
        // Render.
        baseDoc.render(conn.getWriter());
    }
    
    private String getTimeToMinute(){
		long year;
		long month;
		long day;
		long hour;
		long minute;
		long ymdhm;
		String result;
    	Date d = new Date();
    	Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		ymdhm = (year * 100000000) + (month * 1000000) + (day * 10000) + (hour * 100) + minute;
		result = String.valueOf(ymdhm);
		return( result );
		
    }
}
