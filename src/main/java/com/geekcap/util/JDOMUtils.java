package com.geekcap.util;

import java.net.*;
import java.io.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

public class JDOMUtils
{
    private static SAXBuilder builder;

    public static Element getRootElement( String filename )
    {
        try
        {
            if( builder == null )
            {
                builder = new SAXBuilder();
            }
            Document doc = builder.build( filename );
            return doc.getRootElement();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     * Returns a JDOM Document built from the supplied String or 
     * null if there is a problem building the document
     *
     * @param str   The String to build the document from
     * @return      The built document or null
     */
    public static Document getDocument( String str )
    {
        try
        {
            // Build a JDOM Document from the String
            SAXBuilder builder = new SAXBuilder();
            StringReader reader = new StringReader( str );
            return builder.build( reader );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    /**
     * Returns the root Element contained for the XML document contained in the specified string 
     */
    public static Element getRootElementForString( String str )
    {
       Document doc = getDocument( str );
       return doc.getRootElement();
    }
    
    /**
     * Returns the root element of a JDOM Object built from the supplied
     * filename or null if there is a problem building the document
     *
     * @param str   The String to build the document from
     * @return      The root of the built document or null
     */
    public static Element getRootElementForFile( String filename )
    {
        try
        {
            // Build a JDOM Document from the String
            SAXBuilder builder = new SAXBuilder();
            FileInputStream fis = new FileInputStream( filename );
            Document doc = builder.build( fis );
            fis.close();

            // Return the root element
            return doc.getRootElement();
        }
        catch( Exception e )
        {
	    e.printStackTrace();
            return null;
        }
    }

    public static void saveToFile( String filename, Element root ) throws IOException
    {
        //System.out.println( "Saving XML file to: " + filename );
        XMLOutputter out = new XMLOutputter();
        FileOutputStream fos = new FileOutputStream( filename );
        out.output( root, fos );
        fos.close();
    }
    
    public static void writeToStream( java.io.OutputStream os, Element root ) throws IOException
    {
        XMLOutputter out = new XMLOutputter();
        out.output( root, os );
    }

    /**
     * Returns a String built from the supplied JDOM Element or
     * null if there is a problem building the document
     *
     * @param element   The element to convert to a String
     * @return          The String representation of the element
     *                  or null if there is a problem
     */
    public static String getString( Element element )
    {
        try
        {
            // Create an XMLOutputter
            XMLOutputter out = new XMLOutputter();

            // Ask the XMLOutputter to convert the element to a String
            return out.outputString( element );
        }
        catch( Exception e )
        {
            return null;
        }
    }

   public static Element getRootFromURL( String urlName )
   {
       try
       {
           URL url = new URL( makeURL( urlName ) );
               
           // Build a JDOM Document from the String
           SAXBuilder builder = new SAXBuilder();
           Document doc = builder.build( url.openStream() );

           // Return the root element
           return doc.getRootElement();
       }
       catch( Exception e )
       {
           e.printStackTrace();
           return null;
       }
   }

   /**
    * This method ensures that the URL is properly uuencoded
    */
   private static String makeURL( String url )
   {
       url.replace( ' ', '+' );
       return url;
   }
   
   public static Element getRootFromStream( InputStream is )
   {
       try
       {
           // Build a JDOM Document from the String
           SAXBuilder builder = new SAXBuilder();
           Document doc = builder.build( is );

           // Return the root element
           return doc.getRootElement();
       }
       catch( Exception e )
       {
           e.printStackTrace();
           return null;
       }
   }


    public static int getChildInt( Element e, String childName, int defaultValue )
    {
        try
        {
            String value = e.getChildTextTrim( childName );
            int intVal = Integer.parseInt( value );
            return intVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }

    public static float getChildFloat( Element e, String childName, float defaultValue )
    {
        try
        {
            String value = e.getChildTextTrim( childName );
            float floatVal = Float.parseFloat( value );
            return floatVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }
    
    public static long getLongAttribute( Element e, String attr, long defaultValue )
    {
        try
        {
            String value = e.getAttributeValue( attr );
            long longVal = Long.parseLong( value );
            return longVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue; 
    }

    public static int getIntAttribute( Element e, String attr, int defaultValue )
    {
        try
        {
            String value = e.getAttributeValue( attr );
            int intVal = Integer.parseInt( value );
            return intVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue; 
    }
    
    public static double getDoubleAttribute( Element e, String attr, double defaultValue )
    {
        try
        {
            String value = e.getAttributeValue( attr );
            double doubleVal = Double.parseDouble( value );
            return doubleVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }

    public static float getFloatAttribute( Element e, String attr, float defaultValue )
    {
        try
        {
            String value = e.getAttributeValue( attr );
            float floatVal = Float.parseFloat( value );
            return floatVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }
    
    public static boolean getBooleanAttribute( Element e, String attr, boolean defaultValue )
    {
        try
        {
            String value = e.getAttributeValue( attr );
            if( value.equalsIgnoreCase( "true" ) )
            {
                return true;
            }
            return false;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }

    public static int getInt( Element e, int defaultValue )
    {
        try
        {
            String value = e.getTextTrim();
            int intVal = Integer.parseInt( value );
            return intVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }
    
    public static float getFloat( Element e, float defaultValue )
    {
        try
        {
            String value = e.getTextTrim();
            float floatVal = Float.parseFloat( value );
            return floatVal;
        }
        catch( Exception ex )
        {
        }
        return defaultValue;
    }
}                 
