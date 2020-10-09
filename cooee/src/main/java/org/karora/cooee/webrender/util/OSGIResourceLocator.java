package org.karora.cooee.webrender.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.karora.cooee.osgi.BundleServices;
import org.karora.cooee.webcontainer.SynchronizePeerFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OSGIResourceLocator extends AbstractResourceLocator
{
    /**
     * <b>This method is internal to the framework and should not be
     * used by any other class than <code>Resource</code>.</b>
     * 
     * An internal method used to retrieve a resource as a
     * <code>ByteArrayOutputStream</code>.
     *
     * @param resourceName The name of the resource to be retrieved.
     * @return A <code>ByteArrayOutputStream</code> of the content of the
     *         resource.
     */
    protected static ByteArrayOutputStream getResource(String resourceName) {
        InputStream in = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream out = null;
        int bytesRead = 0;
        
        try {
            in = findResource(resourceName);
            if (in == null) {
                throw new ResourceException("Resource does not exist: \"" + resourceName + "\"");
            }
            out = new ByteArrayOutputStream();
            do {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } while (bytesRead > 0);
        } catch (IOException ex) {
            throw new ResourceException("Cannot get resource: \"" + resourceName + "\": " + ex);
        } finally {
            if (in != null) { try { in.close(); } catch (IOException ex) { } } 
        }
        
        return out;
    }
    
    
    /**
     * Returns only the FIRST found resource - as such, if you've got a whole heap of Javascript files spread out
     * over the place, make sure they have unique names for your purposes.
     * 
     * @param resourceName
     * @return
     */
    private static InputStream findResource(String resourceName)
    {
        BundleContext ctx = BundleServices.getBundleContext();
        
        Bundle[] bundles = ctx.getBundles();
        for (Bundle b: bundles)
        {
            InputStream stream = null;
            try
            {
                stream = getStreamForResource(b, resourceName);
            }
            catch (IOException e)
            {
                System.err.println ("Object for [" + resourceName + "] possibly located in [" + b.getSymbolicName() + "], however received an IOException when getting stream");
                e.printStackTrace(System.err);
            }
            if (stream != null)
                return stream;
        }
        return null;
    }
    
    private static InputStream getStreamForResource(Bundle bundle, String resourceName) throws IOException
    {
        URL resource = null;
        try
        {
            resource = bundle.getResource(resourceName);
        }
        catch (Exception e)
        {}
        
        if (resource == null)
        {
            return null;
        }    
        else
        {
            return resource.openStream();
        }
    }
}
