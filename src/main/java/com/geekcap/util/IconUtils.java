package com.geekcap.util;

import javax.swing.*;
import java.net.URL;

/**
 * Utility class for reading icons from the Java Look-And-Feel 
 * Graphics Respository
 */
public class IconUtils
{
    private static IconUtils iconUtils = null;

    /**
     * Returns an image icon by specifying a category, name and size.
     * 
     * @param category      One of the following: development, general,
     *                      media, navigation, table, or text; you can
     *                      use one of the other specific methods to skip
     *                      this parameter
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public ImageIcon getIcon( String category,
                              String name,
                              int size )
    {
        // Validation
        if( size != 16 && size != 24 )
        {
            return null;
        }

        // Build the URL String
        String imageName = "/toolbarButtonGraphics/" + category +
                           "/" + name + size + ".gif";

        // Get a URL pointing to the image
        URL iconURL = this.getClass().getResource( imageName );

        // Build and return a new ImageIcon built from this URL
        return new ImageIcon( iconURL );
    }

    /**
     * Returns the development image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getDevelopmentIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "development", name, size );
    }

    /**
     * Returns the general image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getGeneralIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "general", name, size );
    }
    
    /**
     * Returns the media image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getMediaIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "media", name, size );
    }
    
    /**
     * Returns the navigation image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getNavigationIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "navigation", name, size );
    }

    /**
     * Returns the table image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getTableIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "table", name, size );
    }

    /**
     * Returns the text image icon with the specified name and size.
     * 
     * @param name          The name of the image; application specific
     * @param size          Either 16 for 16x16 and 24 for 24x24
     */
    public static ImageIcon getTextIcon( String name, int size )
    {
        if( iconUtils == null )
        {
            iconUtils = new IconUtils();
        }

        return iconUtils.getIcon( "text", name, size );
    }
}
