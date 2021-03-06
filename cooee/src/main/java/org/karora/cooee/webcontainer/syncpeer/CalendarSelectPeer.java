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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.karora.cooee.app.Border;
import org.karora.cooee.app.CalendarSelect;
import org.karora.cooee.app.Color;
import org.karora.cooee.app.Component;
import org.karora.cooee.app.FillImage;
import org.karora.cooee.app.Font;
import org.karora.cooee.app.ImageReference;
import org.karora.cooee.app.ResourceImageReference;
import org.karora.cooee.app.update.ServerComponentUpdate;
import org.karora.cooee.app.util.DomUtil;
import org.karora.cooee.webcontainer.ComponentSynchronizePeer;
import org.karora.cooee.webcontainer.ContainerInstance;
import org.karora.cooee.webcontainer.PartialUpdateManager;
import org.karora.cooee.webcontainer.PartialUpdateParticipant;
import org.karora.cooee.webcontainer.PropertyUpdateProcessor;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.image.ImageRenderSupport;
import org.karora.cooee.webcontainer.image.ImageTools;
import org.karora.cooee.webcontainer.propertyrender.BorderRender;
import org.karora.cooee.webcontainer.propertyrender.ColorRender;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webcontainer.propertyrender.FillImageRender;
import org.karora.cooee.webcontainer.propertyrender.FontRender;
import org.karora.cooee.webrender.ServerMessage;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;
import org.karora.cooee.webrender.servermessage.DomUpdate;
import org.karora.cooee.webrender.service.JavaScriptService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <code>ComponentSynchronizePeer</code> implementation for the
 * <code>CalendarSelect</code> component.
 */
public class CalendarSelectPeer
implements ComponentSynchronizePeer, ImageRenderSupport, PropertyUpdateProcessor {
    
    private static final String IMAGE_ID_BACKGROUND = "background";
    private static final String IMAGE_ID_SELECTED_DATE_BACKGROUND = "selectedDateBackground";
    private static final String IMAGE_ID_ARROW_LEFT = "arrowLeft";
    private static final String IMAGE_ID_ARROW_RIGHT = "arrowRight";

    private static final String IMAGE_PREFIX = "/org/karora/cooee/webcontainer/resource/image/";
    private static final ImageReference DEFAULT_ICON_ARROW_LEFT = new ResourceImageReference(IMAGE_PREFIX + "ArrowLeft.gif");
    private static final ImageReference DEFAULT_ICON_ARROW_RIGHT = new ResourceImageReference(IMAGE_PREFIX + "ArrowRight.gif");

    private static final String MESSAGE_RESOURCE = "META-INF/org/karora/cooeeextras/Calendar";
    
    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service CALENDAR_SELECT_SERVICE = JavaScriptService.forResource("Echo2Extras.CalendarSelect",
            "/org/karora/cooee/webcontainer/resource/js/CalendarSelect.js");

    static {
        WebRenderServlet.getServiceRegistry().add(CALENDAR_SELECT_SERVICE);
    }
    
    /**
     * The <code>PartialUpdateManager</code> for this synchronization peer.
     */
    private PartialUpdateManager partialUpdateManager;
    
    /**
     * <code>PartialUpdateParticipant</code> to set date.
     */
    private PartialUpdateParticipant setDateUpdateParticipant = new PartialUpdateParticipant() {
    
        /**
         * @see org.karora.cooee.webcontainer.PartialUpdateParticipant#renderProperty(org.karora.cooee.webcontainer.RenderContext,
         *       org.karora.cooee.app.update.ServerComponentUpdate)
         */
        public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
            renderSetDateDirective(rc, (CalendarSelect) update.getParent());
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
     * Default constructor.
     */
    public CalendarSelectPeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(CalendarSelect.DATE_CHANGED_PROPERTY, setDateUpdateParticipant);
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#getContainerId(org.karora.cooee.app.Component)
     */
    public String getContainerId(Component component) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see org.karora.cooee.webcontainer.image.ImageRenderSupport#getImage(org.karora.cooee.app.Component, java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_ARROW_RIGHT.equals(imageId)) {
            return DEFAULT_ICON_ARROW_RIGHT;
        } else if (IMAGE_ID_ARROW_LEFT.equals(imageId)) {
            return DEFAULT_ICON_ARROW_LEFT;
        } else if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage = (FillImage) component.getRenderProperty(CalendarSelect.PROPERTY_BACKGROUND_IMAGE);
            return backgroundImage == null ? null : backgroundImage.getImage();
        } else if (IMAGE_ID_SELECTED_DATE_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage = (FillImage) component.getRenderProperty(
                    CalendarSelect.PROPERTY_SELECTED_DATE_BACKGROUND_IMAGE);
            return backgroundImage == null ? null : backgroundImage.getImage();
        } else {
            return null;
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderAdd(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String,
     *      org.karora.cooee.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        rc.getServerMessage().addLibrary(CALENDAR_SELECT_SERVICE.getId());
        renderInitDirective(rc, targetId, (CalendarSelect) component);
    }

    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderDispose(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate,
     *      org.karora.cooee.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        rc.getServerMessage().addLibrary(CALENDAR_SELECT_SERVICE.getId());
        renderDisposeDirective(rc, (CalendarSelect) component);
    }

    /**
     * Renders a dispose directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param calendarSelect the <code>CalendarSelect</code> being rendered
     */
    private void renderDisposeDirective(RenderContext rc, CalendarSelect calendarSelect) {
        String elementId = ContainerInstance.getElementId(calendarSelect);
        ServerMessage serverMessage = rc.getServerMessage();
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_PREREMOVE, 
                "ExtrasCalendarSelect.MessageProcessor", "dispose");
        initElement.setAttribute("eid", elementId);
    }

    /**
     * Renders an initialization directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param containerId the container element id
     * @param calendarSelect the <code>CalendarSelect</code> being rendered
     */
    private void renderInitDirective(RenderContext rc, String containerId, CalendarSelect calendarSelect) {
        String elementId = ContainerInstance.getElementId(calendarSelect);
        ServerMessage serverMessage = rc.getServerMessage();
        Document document = serverMessage.getDocument();
        Element initElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, 
                "ExtrasCalendarSelect.MessageProcessor", "init");
        initElement.setAttribute("eid", elementId);
        initElement.setAttribute("container-eid", containerId);

        Calendar calendar = new GregorianCalendar();
        if (calendarSelect.getDate() != null) {
            calendar.setTime(calendarSelect.getDate());
        }
        initElement.setAttribute("year", Integer.toString(calendar.get(Calendar.YEAR)));
        initElement.setAttribute("month", Integer.toString(calendar.get(Calendar.MONTH)));
        initElement.setAttribute("date", Integer.toString(calendar.get(Calendar.DATE)));
        if (!calendarSelect.isRenderEnabled()) {
            initElement.setAttribute("enabled", "false");
        }
        
        Color background = (Color) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_BACKGROUND);
        if (background != null) {
            initElement.setAttribute("background", ColorRender.renderCssAttributeValue(background));
        }
        Color foreground = (Color) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_FOREGROUND);
        if (foreground != null) {
            initElement.setAttribute("foreground", ColorRender.renderCssAttributeValue(foreground));
        }
        Font font =  (Font) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_FONT);
        if (font != null) {
            if (font.getTypeface() != null) {
                initElement.setAttribute("font-family", FontRender.renderFontFamilyCssAttributeValue(font.getTypeface()));
            }
            if (font.getSize() != null) {
                initElement.setAttribute("font-size", ExtentRender.renderCssAttributeValue(font.getSize()));
            }
            initElement.setAttribute("font-style", FontRender.renderFontStyleCssAttributeValue(font));
            initElement.setAttribute("font-weight", FontRender.renderFontWeightCssAttributeValue(font));
            initElement.setAttribute("text-decoration", FontRender.renderTextDecorationCssAttributeValue(font));
        }
        FillImage backgroundImage = (FillImage) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_BACKGROUND_IMAGE);
        if (backgroundImage != null) {
            CssStyle backgroundImageStyle = new CssStyle();
            FillImageRender.renderToStyle(backgroundImageStyle, rc, this, calendarSelect, IMAGE_ID_BACKGROUND, 
                    backgroundImage, 0);
            initElement.setAttribute("background-image", backgroundImageStyle.renderInline());
        }
        
        Color selectedDateBackground = (Color) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_SELECTED_DATE_BACKGROUND);
        if (selectedDateBackground != null) {
            initElement.setAttribute("selected-date-background", ColorRender.renderCssAttributeValue(selectedDateBackground));
        }
        Color selectedDateForeground = (Color) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_SELECTED_DATE_FOREGROUND);
        if (selectedDateForeground != null) {
            initElement.setAttribute("selected-date-foreground", ColorRender.renderCssAttributeValue(selectedDateForeground));
        }
        FillImage selectedDateBackgroundImage = (FillImage) calendarSelect.getRenderProperty(
                CalendarSelect.PROPERTY_SELECTED_DATE_BACKGROUND_IMAGE);
        if (selectedDateBackgroundImage != null) {
            CssStyle backgroundImageStyle = new CssStyle();
            FillImageRender.renderToStyle(backgroundImageStyle, rc, this, calendarSelect, IMAGE_ID_SELECTED_DATE_BACKGROUND, 
                    selectedDateBackgroundImage, 0);
            initElement.setAttribute("selected-date-background-image", backgroundImageStyle.renderInline());
        }

        Color adjacentMonthDateForeground = (Color) calendarSelect.getRenderProperty(
                CalendarSelect.PROPERTY_ADJACENT_MONTH_DATE_FOREGROUND);
        if (adjacentMonthDateForeground != null) {
            initElement.setAttribute("adjacent-month-date-foreground", 
                    ColorRender.renderCssAttributeValue(adjacentMonthDateForeground));
        }
        
        initElement.setAttribute("arrow-left-image", ImageTools.getUri(rc, this, calendarSelect, IMAGE_ID_ARROW_LEFT));
        initElement.setAttribute("arrow-right-image", ImageTools.getUri(rc, this, calendarSelect, IMAGE_ID_ARROW_RIGHT));

        Border border = (Border) calendarSelect.getRenderProperty(CalendarSelect.PROPERTY_BORDER);
        if (border != null) {
            initElement.setAttribute("border", BorderRender.renderCssAttributeValue(border));
        }
        
        Locale locale = calendarSelect.getRenderLocale();
        if (!Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
            try {
                ResourceBundle bundle = ResourceBundle.getBundle(MESSAGE_RESOURCE, locale);
                if (bundle != null) {
                    if (bundle.getString("DayOfWeek0") != null) {
                        Element dayNamesElement = document.createElement("day-names");
                        for (int i = 0; i < 7; ++i) {
                            String dayName = bundle.getString("DayOfWeek" + i);
                            if (dayName == null) {
                                throw new IllegalStateException("No day name for day #" + i);
                            }
                            Element dayNameElement = document.createElement("day-name");
                            dayNameElement.setAttribute("value", dayName);
                            dayNamesElement.appendChild(dayNameElement);
                        }
                        initElement.appendChild(dayNamesElement);
                    }
                    if (bundle.getString("Month0") != null) {
                        Element monthNamesElement = document.createElement("month-names");
                        for (int i = 0; i < 12; ++i) {
                            String monthName = bundle.getString("Month" + i);
                            if (monthName == null) {
                                throw new IllegalStateException("No month name for month #" + i);
                            }
                            Element monthNameElement = document.createElement("month-name");
                            monthNameElement.setAttribute("value", monthName);
                            monthNamesElement.appendChild(monthNameElement);
                        }
                        initElement.appendChild(monthNamesElement);
                    }
                }
            } catch (MissingResourceException ex) {
                // Do nothing.
            }
        }
    }

    /**
     * @see org.karora.cooee.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(org.karora.cooee.webcontainer.ContainerInstance,
     *      org.karora.cooee.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element element) {
        Element selectionElement = DomUtil.getChildElementByTagName(element, "calendar-selection");
        int month = Integer.parseInt(selectionElement.getAttribute("month"));
        int year = Integer.parseInt(selectionElement.getAttribute("year"));
        int date = selectionElement.hasAttribute("date") ? Integer.parseInt(selectionElement.getAttribute("date")) : -1;
        
        if (date == -1) {
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    CalendarSelect.DATE_CHANGED_PROPERTY, null);
        } else {
            Calendar calendar = new GregorianCalendar(year, month, date);
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    CalendarSelect.DATE_CHANGED_PROPERTY, calendar.getTime());
        }
    }

    /**
     * Renders a set-date directive.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param calendarSelect the <code>CalendarSelect</code> being rendered
     */
    private void renderSetDateDirective(RenderContext rc, CalendarSelect calendarSelect) {
        String elementId = ContainerInstance.getElementId(calendarSelect);
        ServerMessage serverMessage = rc.getServerMessage();
        Element setDateElement = serverMessage.appendPartDirective(ServerMessage.GROUP_ID_UPDATE, 
                "ExtrasCalendarSelect.MessageProcessor", "set-date");
        setDateElement.setAttribute("eid", elementId);
        
        Calendar calendar = new GregorianCalendar();
        if (calendarSelect.getDate() != null) {
            calendar.setTime(calendarSelect.getDate());
            setDateElement.setAttribute("year", Integer.toString(calendar.get(Calendar.YEAR)));
            setDateElement.setAttribute("month", Integer.toString(calendar.get(Calendar.MONTH)));
            setDateElement.setAttribute("date", Integer.toString(calendar.get(Calendar.DATE)));
        }
    }
    
    /**
     * @see org.karora.cooee.webcontainer.ComponentSynchronizePeer#renderUpdate(org.karora.cooee.webcontainer.RenderContext,
     *      org.karora.cooee.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        // Determine if fully replacing the component is required.
        if (partialUpdateManager.canProcess(rc, update)) {
            partialUpdateManager.process(rc, update);
        } else {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        }
        
        return true;
    }
}