package org.karora.cooee.app.util;

import org.karora.cooee.app.util.osgi.OSGIPeerFactory;
import org.karora.cooee.osgi.OSGIDetector;

/**
 * Facade of a Peerfactory that delegates to an environment
 * specific PeerFactory
 * 
 * 
 * @author dmurley
 *
 */
public class PeerFactory implements IPeerFactory
{
    
    IPeerFactory peerFactory = null;
    
    public PeerFactory(String resourceName, ClassLoader classLoader)
    {
        if (OSGIDetector.isOSGIEnvironment())
        {
            peerFactory = (IPeerFactory) new OSGIPeerFactory(resourceName);
        }
        else
        {
            peerFactory = (IPeerFactory) new DefaultPeerFactory(resourceName, Thread.currentThread().getContextClassLoader());
        }
    }

    public Object getPeerForObject(Class objectClass, boolean searchSuperClasses)
    {
        return peerFactory.getPeerForObject(objectClass, searchSuperClasses);
    }
}
