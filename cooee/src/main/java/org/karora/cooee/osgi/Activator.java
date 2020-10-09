package org.karora.cooee.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    public void start(BundleContext context) throws Exception
    {
        BundleServices.setBundleContext(context);
        
    }

    public void stop(BundleContext context) throws Exception
    {
        // TODO Auto-generated method stub
        
    }

    
    
    
    
}
