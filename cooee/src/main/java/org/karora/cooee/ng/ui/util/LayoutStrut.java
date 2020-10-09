package org.karora.cooee.ng.ui.util;

import org.karora.cooee.app.Extent;
import org.karora.cooee.webcontainer.RenderContext;
import org.karora.cooee.webcontainer.propertyrender.ExtentRender;
import org.karora.cooee.webrender.Service;
import org.karora.cooee.webrender.WebRenderServlet;
import org.karora.cooee.webrender.output.CssStyle;

import org.w3c.dom.Element;

/**
 * <code>LayoutStrut</code> can be used to generate transparent spacing between Elements.
 */
public class LayoutStrut {
	
	/**
	 * A transparent 1x1 GIF as a service
	 */
	public static final Service PIXEL_SERVICE = StaticImageService.forResource("EP_1x1PX",
			"/org/karora/cooee/ng/ui/resource/images/transparent1x1.gif",new Extent(1),new Extent(1));

    static {
        WebRenderServlet.getServiceRegistry().add(PIXEL_SERVICE);
    }
    
    /**
     * Creates a strut spacing with the specified width and height.
     *  
     * @param rc - the RenderContext in question
     * @param width - the spacing width in pixels
     * @param height - the spacing height in pixels
     * @return an transparent IMG element sized according
     */
    public static Element createStrut(RenderContext rc, int width, int height) {
    	return createStrut(rc, new Extent(width), new Extent(height));
    }

    /**
     * Creates a strut spacing with the specified width and height.
     *  
     * @param rc - the RenderContext in question
     * @param width - the spacing width
     * @param height - the spacing height
     * @return an transparent IMG element sized according
     */
    public static Element createStrut(RenderContext rc, Extent width, Extent height) {
    	String uri = rc.getContainerInstance().getServiceUri(PIXEL_SERVICE);
    	Element img = rc.getServerMessage().getDocument().createElement("img");
        img.setAttribute("src", uri);
        
        CssStyle cssStyle = new CssStyle();
        cssStyle.setAttribute("border", "none");
        ExtentRender.renderToStyle(cssStyle,"width",width);
        ExtentRender.renderToStyle(cssStyle,"height",height);
        img.setAttribute("style", cssStyle.renderInline());
    	return img;
    	
    }
    
}
