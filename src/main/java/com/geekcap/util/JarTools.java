package com.geekcap.util;

import java.io.File;

public class JarTools 
{
	public static DifferenceReport compareJarFiles( String jar1Filename, String jar2Filename )
	{
		// A report to host our results
		DifferenceReport diff = new DifferenceReport();
		
		try
		{
			// Find the temporary directory
			String tmpDir = System.getProperty( "java.io.tmpdir" );
			System.out.println( "Temporary Directory: " + tmpDir );
			
			// Decompress the first JAR file
			String jar1ShortName = jar1Filename;
			if( jar1ShortName.indexOf( File.separatorChar ) != -1 )
			{
				jar1ShortName = jar1ShortName.substring( jar1ShortName.lastIndexOf( File.separator ) + 1 ) + "1";
			}
			//System.out.println( "Decompressing: " + jar1Filename );
			ZipUtils zip1 = new ZipUtils( jar1Filename );
			String decompressedDir1 = tmpDir + File.separator + jar1ShortName;
			zip1.decompressBlocked( decompressedDir1 );
			
			// Decompress the second JAR file
			String jar2ShortName = jar2Filename;
			if( jar2ShortName.indexOf( File.separatorChar ) != -1 )
			{
				jar2ShortName = jar2ShortName.substring( jar2ShortName.lastIndexOf( File.separator ) + 1 ) + "2";
			}
			//System.out.println( "Decompressing: " + jar2Filename );
			ZipUtils zip2 = new ZipUtils( jar2Filename );
			String decompressedDir2 = tmpDir + File.separator + jar2ShortName;
			zip2.decompressBlocked( decompressedDir2 );

			//System.out.println( "Comparing..." );
			FileSystemTools fst = new FileSystemTools();
			DifferenceReport dr = fst.compareDirectoriesDR( decompressedDir1, decompressedDir2 );
			
			// Debug: dump...
			if( dr.getArtifactNames().size() == 0 && dr.getChanged().size() == 0 )
			{
				System.out.println( "Files are identical" );
			}
			else
			{
				for( String artifact : dr.getArtifactNames() )
				{
					System.out.println( "Unique items in artifact: " + artifact );
					for( String uniqueItem : dr.getUniqueItems( artifact ) )
					{
						System.out.println( "\t" + uniqueItem );
					}
				}
				System.out.println( "Changed Items: " );
				for( String changed : dr.getChanged() )
				{
					System.out.println( "\t" + changed );
				}
			}
			
			
			// Clean up the temporary directories
			FileSystemTools.deltree( decompressedDir1 );
			FileSystemTools.deltree( decompressedDir2 );

		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		
		// Return our results
		return diff;
	}
	
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			System.out.println( "Usage: JarTools <command> <args>" );
			System.out.println( "\tCommands:" );
			System.out.println( "\t  compare <jar1> <jar2>" );
			System.exit( 0 );
		}
		
		String cmd = args[ 0 ];
		if( cmd.equalsIgnoreCase( "compare" ) )
		{
			if( args.length < 3 )
			{
				System.out.println( "Usage: JarTools compare <jar1> <jar2>" );
				System.exit( 0 );
			}
			
			// Find our JAR files
			String jar1 = args[ 1 ];
			String jar2 = args[ 2 ];
			
			DifferenceReport diff = JarTools.compareJarFiles( jar1, jar2 );
			
		}
		
	}

}
