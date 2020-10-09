package org.karora.cooee.webrender.util;

public class AbstractResourceLocator
{
    protected static final int BUFFER_SIZE = 4096;
    
    
    /**
     * A RuntimeException exception that will be thrown in the event that
     * problems are encountered obtaining a resource.
     */
    public static class ResourceException extends RuntimeException {
    
        /**
         * Creates a resource exception.
         *
         * @param description A description of the error.
         */
        protected ResourceException(String description) {
            super(description);
        }
    }
}
