package org.karora.cooee.app.util.osgi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.karora.cooee.app.util.IPeerFactory;
import org.karora.cooee.app.util.PropertiesDiscovery;
import org.karora.cooee.osgi.BundleServices;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * OSGI Enabled version of the Peer Factory
 * This implementation simply looks through every bundle for the properties resource specified
 * 
 * A slightly more performant solution would be to mark each 3rd party component bundle as being
 * an Cooee bundle / have a property file indicating where the peers are
 * 
 * Currently not sure how this is going to perform with dynamic bundles - and could pose a bit
 * of a problem
 * 
 * @author dmurley
 *
 */
public class OSGIPeerFactory implements IPeerFactory
{
    private final Map objectClassNameToPeerMap = new HashMap();
    
    
    public OSGIPeerFactory(String resourceName)
    {
        BundleContext ctx = BundleServices.getBundleContext();
        Bundle[] bundles = ctx.getBundles();
        for (Bundle b: bundles)
        {
            loadPeers(resourceName, b);
        }
    }
    
    
    /**
     * Creates a new <code>PeerFactory</code>.
     * 
     * @param resourceName the name of the resource properties file from which
     *        the peer bindings may be retrieved (this file will be retrieved
     *        using the <code>PropertiesDiscovery</code> system, so multiple
     *        instances of the file within the <code>CLASSPATH</code> will be
     *        automatically discovered.
     * @param classLoader the <code>ClassLoader</code> to use for retrieving the
     *        resource file and for instantiating the peer singleton instances
     */
    public void loadPeers(String resourceName, Bundle bundle) {
        try {
            Map peerNameMap = OSGIPropertiesDiscovery.loadProperties(resourceName, bundle);
            if (peerNameMap == null)
                return;
            
            Iterator it = peerNameMap.keySet().iterator();
            while (it.hasNext()) {
                String objectClassName = (String) it.next();
                String peerClassName = (String) peerNameMap.get(objectClassName);
                Class peerClass = bundle.loadClass(peerClassName);
                Object peer = peerClass.newInstance();
                objectClassNameToPeerMap.put(objectClassName, peer);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Unable to load synchronize peer bindings: " + ex);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load synchronize peer bindings: " + ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException("Unable to load synchronize peer bindings: " + ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to load synchronize peer bindings: " + ex);
        }
    }
    
    /**
     * Retrieves the appropriate peer instance for a given object 
     * <code>Class</code>.  Returns null in the event that no peer is provided
     * to support the specified class.
     * 
     * @param objectClass the supported object class
     * @param searchSuperClasses flag indicating whether superclasses
     *        of <code>objectClass</code> should be searched for peers if
     *        none can be found for <code>objectClass</code> itself
     * @return the relevant peer, or null if none can be found
     */
    public Object getPeerForObject(Class objectClass, boolean searchSuperClasses) {
        Object peer = null;
        do {
            peer = objectClassNameToPeerMap.get(objectClass.getName());
            if (peer != null) {
                return peer;
            }
            objectClass = objectClass.getSuperclass();
        } while (searchSuperClasses && objectClass != null);
        return null;
    }
}
