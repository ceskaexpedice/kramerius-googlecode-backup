/*
 * Copyright (C) 2010 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.Kramerius.views.inc.details.tabs;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.LexerException;
import cz.incad.kramerius.utils.pid.PIDParser;

/**
 * View object for images
 * @author pavels
 */
public class ImageViewObject {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ImageViewObject.class.getName());
    
    private static final String PID = "pid";

    private static final String RIGHT_MSG = "rightMsg";

    @Inject
    TextsService textsService;

    @Inject
    ResourceBundleService resourceBundleService;
    
    @Inject
    Provider<Locale> localeProvider;
    
    @Inject
    Provider<HttpServletRequest> requestProvider;
    
    
    @Inject
    KConfiguration configuration;
    
    public String getNotAllowedMessageText() throws IOException {
        Locale locale = this.localeProvider.get();
        if (textsService.isAvailable(RIGHT_MSG, locale)) {
            return textsService.getText(RIGHT_MSG, locale);
        } else return this.resourceBundleService.getResourceBundle("labels", locale).getString(RIGHT_MSG);
    }
    
    
    public int getPageInt() {
        return isPagePid() ? Integer.parseInt(getPage())-1 : 0;
     }
    
    public String getPage() {
        try {
            String pidParam = this.requestProvider.get().getParameter(PID);
            if (pidParam != null) {
                PIDParser pidParser = new PIDParser(pidParam);
                pidParser.objectPid();
                if (pidParser.isPagePid()) {
                    return pidParser.getPage();
                } 
            } 
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
        }
        return "";
    }
    
    
    public boolean isPagePid() {
        try {
            String pidParam = this.requestProvider.get().getParameter(PID);
            if (pidParam != null) {
                PIDParser pidParser = new PIDParser(pidParam);
                pidParser.objectPid();
                return pidParser.isPagePid();
            } else return false;
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            return false;
        }
    }
    
    
    public ZoomViewer getZoomingViewer() {
        // There are only two options :  zoomify and deepzoom
        String zoomViewer = this.configuration.getConfiguration().getString("zoom.viewer", "zoomify");
        return ZoomViewer.valueOf(zoomViewer);
    }

    public List<String> getScripturls() {
        return getZoomingViewer().getScripturls();
    }

    public String getDivContainer() {
        return getZoomingViewer().getDivContainer();
    }
    
    static enum ZoomViewer {

        zoomify(new String[] {"js/zoom/OpenLayers.js","js/zoom/zoomify.js"},"ol-wrapper"), 
        deepzoom( new String[] {"js/zoom/deepzoom.js"}, "container");

        private String[] scripturls;
        private String divcontainer;
        
        private ZoomViewer(String[] scripturls, String divcontainer) {
            this.scripturls = scripturls;
            this.divcontainer = divcontainer;
        }
        
        /**
         * @return the scripturls
         */
        public List<String> getScripturls() {
            return Arrays.asList(scripturls);
        }
        
        /**
         * @return the divcontainer
         */
        public String getDivContainer() {
            return divcontainer;
        }
    }
}
