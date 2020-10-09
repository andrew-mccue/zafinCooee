package org.karora.cooee.osgi;

public class OSGIDetector
{
    public static boolean isOSGIEnvironment()
    {
        try
        {
            if (BundleServices.getBundleContext() != null)
            {
                return true;
            }
            return false;
        }
        catch (Throwable t)
        {
            return false;
        }
    }
}
