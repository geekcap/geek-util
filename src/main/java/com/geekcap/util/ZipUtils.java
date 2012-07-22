package com.geekcap.util;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;

import com.geekcap.util.zip.*;
import com.geekcap.util.zip.event.*;

public class ZipUtils implements ZipFileListener
{
    private Set<ZipFileListener> listeners = new TreeSet<ZipFileListener>();
    private String filename;
    private boolean done = false;

    public ZipUtils()
    {
    }

    public ZipUtils( String filename )
    {
        this.filename = filename;
        addZipFileListener( this );
    }

    /**
     * Decompresses the initialized zip file to the specified destination
     */
    public void decompress( String destination )
    {
        UnZipThread thread = new UnZipThread( filename, destination );
        thread.start();
    }
    
    /**
     * Decompresses the file and blocks until it is done
     * @param destination
     */
    public void decompressBlocked( String destination )
    {
    	UnZipThread thread = new UnZipThread( filename, destination );
        thread.start();
        done = false;
        while( !done )
        {
        	try
        	{
        		Thread.sleep( 1000 );
        	}
        	catch( Exception e )
        	{
        	}
        }
        //removeZipFileListener( this );
    }
    
    //
    // ZipfileListener Methods
    //
    private boolean error = false;
	public void compressionComplete(ZipFileEvent e) { done = true; }
	public void decompressionComplete(ZipFileEvent e) { done = true; }
	public void fileCompressed(ZipFileEvent e) {}
	public void fileDecompressed(ZipFileEvent e) {}
	public void decompressionError( ZipFileEvent e ) 
	{
		error = true;
		done = true; 
	}
	
	public boolean isError()
	{
		return error;
	}




    /**
     * Compresses the specified directory to the ZipUtils' zip file
     * 
     * @param directory     Directory to compress
     * @param recurse       true: add subdirectories; false: no
     */
    public void compressDir( String directory, boolean recurse )
    {
        List files = new ArrayList();
        addFilesToList( files, directory, recurse );
        compress( files );
    }

    /**
     * Add the files in the specified directory to the list of Strings; if recurse
     * is true then also add all files in all subdirectories
     */
    protected void addFilesToList( List files, String directory, boolean recurse )
    {
        try
        {
            File f = new File( directory );
            File[] fileArray = f.listFiles();
            for( int i=0; i<fileArray.length; i++ )
            {
                if( fileArray[ i ].isDirectory() )
                {
                    if( recurse )
                    {
                        addFilesToList( files, fileArray[ i ].getAbsolutePath(), recurse );
                    }
                }
                else
                {
                    String filename = fileArray[ i ].getAbsolutePath();
                    files.add( filename );
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }	

    }

    /**
     * Compresses the list of absolute filenames and/or directories
     */
    public void compress( List files )
    {
        // Get the list of files to compress
        ZipThread thread = new ZipThread( filename, files );
        thread.start();
    }

    /**
     * Compresses the list of absolute filenames and/or directories
     */
    public void compressBlocked( List files )
    {
    	addZipFileListener( this );
        // Get the list of files to compress
        ZipThread thread = new ZipThread( filename, files );
        thread.start();
        done = false;
        while( !done )
        {
        	try
        	{
        		Thread.sleep( 1000 );
        	}
        	catch( Exception e )
        	{
        	}
        }
        removeZipFileListener( this );
    }

    /**
     * Returns a list of Entries: name, date, size, and path, sorted by path
     */
    public List list()
    {
        ArrayList list = new ArrayList();
        try
        {
            FileInputStream fis = new FileInputStream( this.filename );
            ZipInputStream zis = new ZipInputStream( new BufferedInputStream( fis ) );

            // Loop over all of the entries in the zip file
            ZipEntry entry;
            DateFormat df = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT );
            while( ( entry = zis.getNextEntry() ) != null )
            {
                if( !entry.isDirectory() )
                {
                    Entry e = new Entry( this.getName( entry.getName() ),
                                         df.format( new Date( entry.getTime() ) ),
                                         Long.toString( entry.getSize() ),
                                         Long.toString( entry.getCompressedSize() ),
                                         this.getPath( entry.getName() ) );
                    list.add( e );
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return list;
    }

    protected String getName( String filename )
    {
        // Handle the case where there is no path
        if( filename.indexOf( "/" ) == -1 &&
            filename.indexOf( "\\" ) == -1 )
        {
            return filename;
        }

        // Get the index of the last file separator
        int index = filename.lastIndexOf( "/" );
        if( index == -1 )
        {
            index = filename.lastIndexOf( "\\" );
        }

        return filename.substring( index + 1 );
    }

    protected String getPath( String filename )
    {
        // Handle the case where there is no path
        if( filename.indexOf( "/" ) == -1 &&
            filename.indexOf( "\\" ) == -1 )
        {
            return "";
        }

        // Get the index of the last file separator
        int index = filename.lastIndexOf( "/" );
        if( index == -1 )
        {
            index = filename.lastIndexOf( "\\" );
        }

        return filename.substring( 0, index );
    }

    public void addZipFileListener( ZipFileListener l )
    {
        this.listeners.add( l );
    }

    public void removeZipFileListener( ZipFileListener l )
    {
        this.listeners.remove( l );
    }

    protected void fireFileCompressed( ZipFileEvent e )
    {
        for( Iterator i=this.listeners.iterator(); i.hasNext(); )
        {
            ZipFileListener l = ( ZipFileListener )i.next();
            l.fileCompressed( e );
        }
    }

    protected void fireFileDecompressed( ZipFileEvent e )
    {
        for( Iterator i=this.listeners.iterator(); i.hasNext(); )
        {
            ZipFileListener l = ( ZipFileListener )i.next();
            l.fileDecompressed( e );
        }
    }

    protected void fireCompressionComplete( ZipFileEvent e )
    {
        for( Iterator i=this.listeners.iterator(); i.hasNext(); )
        {
            ZipFileListener l = ( ZipFileListener )i.next();
            l.compressionComplete( e );
        }
    }

    protected void fireDecompressionComplete( ZipFileEvent e )
    {
        for( Iterator i=this.listeners.iterator(); i.hasNext(); )
        {
            ZipFileListener l = ( ZipFileListener )i.next();
            l.decompressionComplete( e );
        }
    }

    protected void fireDecompressionError( ZipFileEvent e )
    {
        for( Iterator i=this.listeners.iterator(); i.hasNext(); )
        {
            ZipFileListener l = ( ZipFileListener )i.next();
            l.decompressionError( e );
        }
    }

    public static void main( String[] args )
    {
        if( args.length == 0 )
        {
            System.out.println( "Usage: ZipUtils <command> <arg1> <arg2> ..." );
            System.out.println( "\tWhere <command> can be:" );
            System.out.println( "\t\tUnZip <zipfile> <destination>" );
            System.out.println( "\t\tZip <zipfile> <source>" );
            System.out.println( "\t\tList <zipfile>" );
            System.exit( 0 );
        }

        String command = args[ 0 ];
        if( command.equalsIgnoreCase( "unzip" ) )
        {
            if( args.length < 3 )
            {
                System.out.println( "Usage: ZipUtils UnZip <zipfile> <destination>" );
                System.exit( 0 );
            }

            String zipfile = args[ 1 ];
            String destination = args[ 2 ];

            ZipUtils zip = new ZipUtils( zipfile );
            zip.decompress( destination );
        }
        else if( command.equalsIgnoreCase( "zip" ) )
        {
            if( args.length < 3 )
            {
                System.out.println( "Usage: ZipUtils Zip <zipfile> <source-dir>" );
                System.exit( 0 );
            }

            String zipfile = args[ 1 ];
            String source = args[ 2 ];

            ZipUtils zip = new ZipUtils( zipfile );
            zip.compressDir( source, true );
            
            //zip.compress( ... );
        }
        else if( command.equalsIgnoreCase( "list" ) )
        {
            if( args.length < 2 )
            {
                System.out.println( "Usage: ZipUtils List <zipfile>" );
                System.exit( 0 );
            }
            String zipfile = args[ 1 ];
            ZipUtils zip = new ZipUtils( zipfile );
            List l = zip.list();
            System.out.println( "Listing for archive: " + zipfile );
            for( Iterator i=l.iterator(); i.hasNext(); )
            {
                Entry e = ( Entry )i.next();
                System.out.println( e );
            }
        }
    }

    /**
     * Internal thread that performs the actual unzipping
     */
    class UnZipThread extends Thread
    {
        /**
         * The buffer size to read bytes from the zip file and write
         * to the destination file
         */
        public static final int BUFFER_SIZE = 8192;

        private String filename;
        private String destination;

        public UnZipThread( String filename, String destination )
        {
            this.filename = filename;
            this.destination = destination;
        }

        /**
         * Sets up the destination file so that if it will be extracted
         * to a directory that the directory actually exists
         * 
         * @param root      Expansion directory
         * @param filename  The name of the file to expand; this will contain
         *                  the folder name that the file is contained in the 
         *                  zip file
         */
        private void prepareFileDirectories( String root, String filename )
        {
            if( filename.indexOf( "\\" ) == -1 &&
                filename.indexOf( "/" ) == -1 )
            {
                return;
            }
            char separator = '/';
            if( filename.indexOf( "\\" ) != -1 )
            {
                separator = '\\';
            }

            String path = filename.substring( 0, filename.lastIndexOf( separator ) );
            StringBuffer currentPath = new StringBuffer( root );
            StringTokenizer st = new StringTokenizer( path, "\\/", false );
            while( st.hasMoreTokens() )
            {
                String token = st.nextToken();
                currentPath.append( File.separator + token );
                File f = new File( currentPath.toString() );
                if( !f.exists() )
                {
                    f.mkdir();
                }
            }
        }

        public void run()
        {
            try
            {
                // Ensure that the destination folder exists
                File destinationDir = new File( destination );
                if( !destinationDir.exists() )
                {
                    destinationDir.mkdir();
                }

                // Create a ZipInputStream to read the zip file
                BufferedOutputStream dest = null;
                FileInputStream fis = new FileInputStream( filename );
                ZipInputStream zis = new ZipInputStream( new BufferedInputStream( fis ) );

                // Loop over all of the entries in the zip file
                int count;
                byte data[] = new byte[ BUFFER_SIZE ];
                ZipFileEvent event = new ZipFileEvent( this );
                ZipEntry entry;
                while( ( entry = zis.getNextEntry() ) != null )
                {
                    if( !entry.isDirectory() )
                    {
                        String entryName = entry.getName();
                        prepareFileDirectories( destination, entryName );
                        String destFN = destination + File.separator + entry.getName();

                        // Write the file to the file system
                        FileOutputStream fos = new FileOutputStream( destFN );
                        dest = new BufferedOutputStream( fos, BUFFER_SIZE );
                        while( (count = zis.read( data, 0, BUFFER_SIZE ) )  != -1 )
                        {
                            dest.write( data, 0, count );
                        }
                        dest.flush();
                        dest.close();

                        // Fire an update to our listeners
                        event.setFilename( destFN );
                        fireFileDecompressed( event );
                    }
                }
                zis.close();
                event.setFilename( filename );
                fireDecompressionComplete( event );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                ZipFileEvent event = new ZipFileEvent( this );
                event.setFilename( filename );
                fireDecompressionError( event );
            }
        }
    }
    
    /**
     * Internal thread that performs the actual zipping 
     */
    class ZipThread extends Thread
    {
        /**
         * The buffer size to read bytes from the zip file and write
         * to the destination file
         */
        public static final int BUFFER_SIZE = 8192;

        /**
         * The name of the zip file to create
         */
        private String filename;

        /**
         * A List of filenames (as Strings)
         */
        private List files;

        /**
         * Create a new ZipThread
         */
        public ZipThread( String filename, List files )
        {
            this.filename = filename;
            this.files = files;
        }

        public void run()
        {
            try
            {
                // Reference to the file we will be adding to the zipfile
                BufferedInputStream origin = null;

                // Reference to our zip file
                FileOutputStream dest = new FileOutputStream( this.filename );

                // Wrap our destination zipfile with a ZipOutputStream
                ZipOutputStream out = new ZipOutputStream( new BufferedOutputStream( dest ) );

                // Create a byte[] buffer that we will read data from the source
                // files into and then transfer it to the zip file
                byte[] data = new byte[ BUFFER_SIZE ];

                // Iterate over all of the files in our list
                ZipFileEvent event = new ZipFileEvent( this );
                for( Iterator i=files.iterator(); i.hasNext(); )
                {
                    // Get a BufferedInputStream that we can use to read the source file
                    String filename = ( String )i.next();
                    System.out.println( "Adding: " + filename );
                    FileInputStream fi = new FileInputStream( filename );
                    origin = new BufferedInputStream( fi, BUFFER_SIZE );

                    // Strip off the drive information in Windows
                    if( filename.indexOf( ':' ) != -1 )
                    {
                        filename = filename.substring( filename.indexOf( ':' ) + 2 );
                    }

                    // Likewise strip off the leading '/' in Unix
                    else if( filename.startsWith( "/" ) )
                    {
                        filename = filename.substring( 1 );
                    }

                    // Setup the entry in the zip file
                    ZipEntry entry = new ZipEntry( filename );
                    out.putNextEntry( entry );

                    // Read data from the source file and write it out to the zip file
                    int count;
                    while( ( count = origin.read(data, 0, BUFFER_SIZE ) ) != -1 )
                    {
                        out.write(data, 0, count);
                    }

                    // Close the source file
                    origin.close();

                    // Fire an update to our listeners
                    event.setFilename( filename );
                    fireFileCompressed( event );
                }

                // Close the zip file
                out.close();
                fireCompressionComplete( event );
            }
            catch( Exception e )
            {
                e.printStackTrace();
            }
        }
    }

}
