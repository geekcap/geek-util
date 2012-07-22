package com.geekcap.util.grep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import org.apache.regexp.RE;

/**
 * Implements regular expression grep-like functionality
 * 
 * @author shaines
 */
public class Grep extends Thread
{
	private Set<GrepListener> listeners = new HashSet<GrepListener>();
	private BufferedReader reader;
	
	private RE regex;
	
	public Grep()
	{
	}
	
	public Grep( InputStream in, String criteria )
	{
		init( in, criteria );
	}

	public Grep( Reader reader, String criteria )
	{
		init( reader, criteria );
	}
	
	public void init( InputStream in, String criteria )
	{
		reader = new BufferedReader( new InputStreamReader( in ) );
		regex = new RE( criteria );
	}

	public void init( Reader reader, String criteria )
	{
		this.reader = new BufferedReader( reader );
		regex = new RE( criteria );
	}

	
	public void addListener( GrepListener listener )
	{
		listeners.add( listener );
	}
	
	public void removeListener( GrepListener listener )
	{
		listeners.remove( listener );
	}
	
	protected void fireMatch( String line )
	{
		for( GrepListener listener : listeners )
		{
			listener.lineMatch( line );
		}
	}
	
	protected void fireStart()
	{
		for( GrepListener listener : listeners )
		{
			listener.grepStart();
		}
	}
	
	protected void fireComplete()
	{
		for( GrepListener listener : listeners )
		{
			listener.grepComplete();
		}
	}

	protected void fireException( GrepException e )
	{
		for( GrepListener listener : listeners )
		{
			listener.grepException( e );
		}
	}

	public void run()
	{
		// Validate reader
		if( reader == null )
		{
			fireException( new GrepException( "Reader not initialized" ) );
			return;
		}
		
		// Validate the regex criteria
		if( regex == null )
		{
			fireException( new GrepException( "Regular Expression Criteria is not initialized" ) );
			return;
		}
		
		try
		{
			// Note that we're starting
			fireStart();
			
			// Stream through the reader line-by-line
			String line = reader.readLine();
			while( line != null )
			{
				if( regex.match( line ) ) 
				{
					// Found a match
					fireMatch( line );
				}
				line = reader.readLine();
			}
			
			// Note that we're complete
			fireComplete();
		}
		catch( IOException e )
		{
			fireException( new GrepException( "An error occurred while reading the specified reader: " + e.getMessage(), e ) );
			return;
		}
	}
	
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			System.out.println( "Usage: grep <regex-criteria> {file}" );
			System.exit( 0 );
		}
		
		try
		{
			InputStream in = null;
			if( args.length == 2 )
			{
				in = new FileInputStream( args[ 1 ] );
			}
			else
			{
				in = System.in;
			}
			
			// Create a new grep instance
			Grep grep = new Grep( in, args[ 0 ] );
			
			// Build an inline listener
			grep.addListener( new GrepListener() {
				public void grepStart() {}
				public void grepComplete() {}
				public void grepException( GrepException e ) {
					e.printStackTrace();
				}
				public void lineMatch( String line ) {
					System.out.println( line );
				}
			} );
			
			// Start the grep
			grep.start();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
}
