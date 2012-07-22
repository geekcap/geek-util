package com.geekcap.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.regexp.RE;


public class FileSystemTools 
{
	public FileSystemTools()
	{
		
	}

	/**
	 * TODO: create a map of filename -> checksum for that file
	 * @param dirName1
	 * @param dirName2
	 * @return
	 */
	public DifferenceReport compareDirectoriesDR( String dirName1, String dirName2 )
	{
		// To hold our results
		DifferenceReport diff = new DifferenceReport();
		
		try
		{
			// Create file objects for the directories we're going to compare
			File dir1 = new File( dirName1 );
			File dir2 = new File( dirName2 );
			
			if( dir1.isDirectory() && dir2.isDirectory() )
			{
				// Find all files in both directories
				Set<String> files1 = findFiles( dir1, dirName1 );
				Set<String> files2 = findFiles( dir2, dirName2 );
				
				// Create a map to house non-unique files: filename to checksum
				Map<String,String> nonUniqueFiles = new TreeMap<String,String>();
				
				// Find files in the first directory that are not in the second directory
				for( String file : files1 )
				{
					if( !files2.contains( file ) )
					{
						diff.addUniqueItem( dirName1, file );
					}
					else
					{
						nonUniqueFiles.put( file, FileUtils.getFileChecksum( new File( dir1, file ) ) );
					}
				}
				
				// Find files in the second directory that are not in the first directory
				for( String file : files2 )
				{
					if( !files1.contains( file ) ) 
					{
						diff.addUniqueItem( dirName2, file );
					}
					else
					{
						// Find it it in our non-unique map and compare checksums
						if( nonUniqueFiles.containsKey( file ) )
						{
							String value = nonUniqueFiles.get( file );
							if( value.equalsIgnoreCase( FileUtils.getFileChecksum( new File( dir2, file ) ) ) )
							{
								// They have the same checksum, so they're the same
								nonUniqueFiles.remove( file );
							}
						}
						
					}
				}
				
				// Compare non-unique files for differences
				//System.out.println( "Non-Unique files: " );
				for( String file : nonUniqueFiles.keySet() )
				{
					diff.addChangedItem( file );
					//System.out.println( "\t" + file );
				}
				
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		// Return our results
		return diff;
	}

	public String compareDirectories( String dirName1, String dirName2 )
	{
		StringBuilder sb = new StringBuilder();
		
		try
		{
			// Create file objects for the directories we're going to compare
			File dir1 = new File( dirName1 );
			File dir2 = new File( dirName2 );
			List<String> nonUniqueFiles = new ArrayList<String>();
			
			if( dir1.isDirectory() && dir2.isDirectory() )
			{
				// Find all files in both directories
				Set<String> files1 = findFiles( dir1, dirName1 );
				Set<String> files2 = findFiles( dir2, dirName2 );
				
				// Holds our list of files that are in one or the other directory,
				// but no in both
				Set<String> f1Only = new TreeSet<String>();
				Set<String> f2Only = new TreeSet<String>();
				
				// Find files in the first directory that are not in the second directory
				for( String file : files1 )
				{
					if( !files2.contains( file ) )
					{
						f1Only.add( file );
					}
					else
					{
					    nonUniqueFiles.add( file );
					}
				}
				
				// Find files in the second directory that are not in the first directory
				for( String file : files2 )
				{
					if( !files1.contains( file ) ) 
					{
						f2Only.add( file );
					}
				}
				
				sb.append( "Files that are in both directories: \n" );
				for( String file : nonUniqueFiles )
				{
				    sb.append( "\t" + file + "\n" );
				}
				
				// Build our results
				sb.append( "\nFiles only in dir: " + dirName1 + "\n" );
				for( String file : f1Only )
				{
					sb.append( "\t" + file + "\n" );
				}
				sb.append( "\nFiles only in dir: " + dirName2 + "\n" );
				for( String file : f2Only )
				{
					sb.append( "\t" + file + "\n" );
				}
			}
		}
		catch( Exception e )
		{
			
		}
		
		return sb.toString();
	}
	
	public Set<String> findFiles( File dir, String prefix ) throws Exception
	{
		int prefixLength = prefix.length();
		Set<String> filenames = new TreeSet<String>();
		
		File[] files = dir.listFiles();
		for( File file : files )
		{
			if( file.isDirectory() )
			{
				filenames.addAll( findFiles( file, prefix ) );
			}
			else
			{
				// Strip off the prefix
				filenames.add( file.getAbsolutePath().substring( prefixLength ) );
			}
		}
		
		return filenames;
	}
	
	/**
	 * From the specified directory, this method searches (optionally recursive)
	 * all files that match the matchFilename criteria for the specified searchString.
	 * It returns a map of all matching filenames to a list of String snippets of all
	 * lines with the specified searchString 
	 * 
	 * @param dirName
	 * @param searchString
	 * @param matchFilename
	 * @param caseSensitive
	 * @param recurse
	 * 
	 * @return
	 */
	public Map<String,List<Line>> findInFiles( String dirName, String searchString, String matchFilename, boolean caseSensitive, boolean recurse )
	{
		// Our results
		Map<String,List<Line>> results = new TreeMap<String,List<Line>>();
		
		try
		{
			// Default to the current working directory
			File dir = new File( dirName );
			if( dir.isDirectory() )
			{
				// Find all files that match our filename search criteria
				List<String> matchingFiles = findMatchingFiles( dir, matchFilename, recurse );
				
				// See if we need to match case
				if( !caseSensitive )
				{
					// We don't need to match case, so we'll do lower case comparisons
					searchString = searchString.toLowerCase();
				}
				
				// Iterate over all matching files
				for( String filename : matchingFiles )
				{
					// TODO: Debug, remove
					//System.out.println( "Examining file: " + filename );
					
					// Holds the list of lines that match our search criteria
					List<Line> matchingLines = new ArrayList<Line>();
					
					// Read through the file line-by-line
					BufferedReader br = new BufferedReader( new FileReader( filename ) );
					String line = br.readLine();
					int lineNo = 0;
					while( line != null )
					{
						// compareLine will either be the line (case sensitive) or a 
						// lower case version of the line (case insensitive)
						String compareLine = line;
						if( !caseSensitive )
						{
							compareLine = line.toLowerCase();
						}
						
						if( compareLine.indexOf( searchString ) != -1 )
						{
							// Found a match
							matchingLines.add( new Line( lineNo, line ) );
						}
						
						// Read the next line
						line = br.readLine();
						lineNo++;
					}

					// See if we found anything in this file
					if( matchingLines.size() > 0 )
					{
						// Found a matching file with a matching line
						results.put( filename, matchingLines );
					}
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		// Return what we found
		return results;
	}
	
	/**
	 * Searches for files whose names match the specified criteria (regex)
	 * 
	 * @param dir			The directory to search
	 * @param criteria		The regex matching criteria for file names
	 * @param recurse		Recurse down subdirectories?
	 * 
	 * @return				A List of matching filenames
	 */
	public List<String> findMatchingFiles( File dir, String criteria, boolean recurse )
	{
		// Results: our matching filenames
		List<String> results = new ArrayList<String>();
		
		try
		{
			// Regular expression matcher
			RE re = new RE( criteria );
			
			// List all files in the directory
			File[] files = dir.listFiles();
			for( File file : files )
			{
				// TODO: Debug, remove
				//System.out.println( "Examining file: " + file.getAbsolutePath() );
				
				if( file.isDirectory() && recurse )
				{
					// Recurse down the sub directory
					results.addAll( findMatchingFiles( file, criteria, true ) );
				}
				else
				{
					// Perform a regular expressions match
					if( re.match( file.getName() ) )
					{
						// Found a match!
						results.add( file.getAbsolutePath() );
					}
				}
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		// Return the matching filenames
		return results;
	}
	
	public static void deltree( String dirName )
	{
		deltree( new File( dirName ) );
	}
	
	public static void deltree( File dir )
	{
		try
		{
			File[] files = dir.listFiles();
			for( File file : files )
			{
				if( file.isDirectory() )
				{
					deltree( file );
					file.delete();
				}
				else
				{
					file.delete();
				}
			}
			dir.delete();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Copies the specified file to the specified destination (destination includes filename)
	 * 
	 * @param f				The file to copy
	 * @param destination	The fully qualified destination path, including filename, e.g. C:\temp\myfile.txt
	 */
	public static void copyFile( File f, String destination )
    {
        try
        {
            FileInputStream fis  = new FileInputStream( f );
            FileOutputStream fos = new FileOutputStream( destination );
            byte[] buf = new byte[1024];
            int i = 0;
            while((i=fis.read(buf))!=-1) {
                fos.write(buf, 0, i);
            }
            fis.close();
            fos.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
	
	public static void main( String[] args )
	{
		if( args.length == 0 )
		{
			System.out.println( "Usage: FileSystemTools <command> <args...>" );
			System.out.println( "\tWhere command: " );
			System.out.println( "\t\tlist" );
            System.out.println( "\t\tcompare" );
            System.out.println( "\t\tfindinfiles" );
			System.exit( 0 );
		}
		
		String cmd = args[ 0 ];
		FileSystemTools fst = new FileSystemTools();
		
		try
		{
			if( cmd.equalsIgnoreCase( "list" ) )
			{
				if( args.length < 2 )
				{
					System.out.println( "Usage FileSystemTools list <dir> {<regex-match> <recurse:true|false,default=true>}" );
					System.exit( 0 );
				}
				else if( args.length == 2 )
				{
					Set<String> files = fst.findFiles( new File( args[ 1 ] ), args[ 1 ] );
					
					System.out.println( "Files in directory: " + args[ 1 ] );
					for( String file : files )
					{
						System.out.println( file );
					}
				}
				else if( args.length > 2 )
				{
					String dir = args[ 1 ];
					String criteria = args[ 2 ];
					boolean recurse = true;
					if( args.length == 4 )
					{
						if( args[ 3 ].equalsIgnoreCase( "false" ) )
						{
							recurse = false;
						}
					}
					
					List<String> matchingFiles = fst.findMatchingFiles( new File( dir ), criteria, recurse );
					System.out.println( "Matching files in directory: " + dir );
					for( String file : matchingFiles )
					{
						System.out.println( file );
					}
				}
			}
			else if( cmd.equalsIgnoreCase( "compare" ) )
			{
				if( args.length < 3 )
				{
					System.out.println( "Usage FileSystemTools compare <dir1> <dir2>" );
					System.exit( 0 );
				}
				System.out.println( fst.compareDirectories( args[ 1 ], args[ 2 ] ) );
				/*
				DifferenceReport report = fst.compareDirectoriesDR( args[ 1 ], args[ 2 ] );
				List<String> changedItems = report.getChanged();
				System.out.println( "Items in both directories:" );
				for( String s : changedItems )
				{
				    System.out.println( "\t" + s );
				}
				
				Set<String> artifactNames = report.getArtifactNames();
				for( String artifactName : artifactNames )
				{
				    System.out.println( "\nUnique Items in directory: " + artifactName );
				    List<String> uniqueItems = report.getUniqueItems( artifactName );
				    for( String item : uniqueItems )
				    {
				        System.out.println( "\t" + item );
				    }
				}
				*/
			}
			else if( cmd.equalsIgnoreCase( "findinfiles" ) )
			{
				if( args.length < 2 )
				{
					System.out.println( "Usage FileSystemTools findinfiles <search-string> {match-filename-regex=.} {verbose=true} {search-dir=.} {case-sensitive-search=false} {recurse=true}" );
					System.exit( 0 );
				}
				
				String searchString = args[ 1 ];

				String matchFilenameRegex = ".";
				if( args.length > 2 )
				{
					matchFilenameRegex = args[ 2 ];
				}
				boolean verbose = true;
				if( args.length > 3 )
				{
					if( args[ 3 ].equals( "false" ) )
					{
						verbose = false;
					}
				}
				String dir = ".";
				if( args.length > 4 )
				{
					dir = args[ 4 ];
				}
 				boolean caseSensitive = false;
				if( args.length > 5 )
				{
					if( args[ 5 ].equalsIgnoreCase( "true" ) )
					{
						caseSensitive = true;
					}
				}
				boolean recurse = true;
				if( args.length > 6 )
				{
					if( args[ 6 ].equals( "false" ) )
					{
						recurse = false;
					}
				}
				
				System.out.println( "DEBUG: findinfiles-> " + dir + ", " + searchString + ", " + matchFilenameRegex + ", " + caseSensitive + ", " + recurse );
				
				Map<String,List<Line>> results = fst.findInFiles( dir, searchString, matchFilenameRegex, caseSensitive, recurse );
				
				System.out.println( "Found \"" + searchString + "\" in the following files:" );
				for( String filename : results.keySet() )
				{
					System.out.print( filename );
					if( verbose )
					{
						System.out.println( ":" );
						List<Line> snippets = results.get( filename );
						for( Line snippet : snippets )
						{
							System.out.println( "  " + snippet.getNumber() + ": \t" + snippet.getContent() );
						}
					}
					else
					{
						System.out.println();
					}
				}
				
				//String searchString, String matchFilename, boolean caseSensitive, boolean recurse )				
			}
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	
	class Line 
	{
		private int number;
		private String content;
		
		public Line() 
		{
		}
		
		public Line(int number, String content) 
		{
			this.content = content;
			this.number = number;
		}

		public int getNumber() 
		{
			return number;
		}
		
		public void setNumber(int number) 
		{
			this.number = number;
		}
		
		public String getContent() 
		{
			return content;
		}
		
		public void setContent(String content) 
		{
			this.content = content;
		}
	}
}
