package org.karora.cooee.app.util;

/**
 * Common interface for Cooee Peer Factories
 * 
 * @author dmurley
 *
 */
public interface IPeerFactory
{
        public Object getPeerForObject(Class objectClass, boolean searchSuperClasses);
}
