package org.karora.cooee.osgi;

import java.util.List;

import org.osgi.framework.BundleContext;

public class BundleServices
{
    private static BundleContext bundleContext;
    
    public static BundleContext getBundleContext()
    {
        return bundleContext;
    }
   
    protected static void setBundleContext(BundleContext bundleContext)
    {
        BundleServices.bundleContext = bundleContext;
    }
    
    
}
