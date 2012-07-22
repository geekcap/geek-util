package com.geekcap.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

public class FileUtils 
{
    public static String getFileChecksum( String filename )
    {
    	return getFileChecksum( new File( filename ) );
    }

    public static String getFileChecksum( File file )
    {
    	// Byte array to hold file bytes for checksumming
    	byte[] bytes = new byte[ 1024 ];
    	
        try
        {
            // Create a CRC32 checksum instance that will perform checksums on our behalf
            CRC32 checksum = new CRC32();

            // Create a BufferedInputStream that we'll use to read the source file
            BufferedInputStream is = new BufferedInputStream( new FileInputStream( file ) );

            // Read through the file
            int len = 0;
            while( ( len = is.read( bytes ) ) >= 0 )
            {
                checksum.update( bytes, 0, len );
            }

            // Close the file
            is.close();

            // Return the checksum
            return Long.toString( checksum.getValue() );

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
