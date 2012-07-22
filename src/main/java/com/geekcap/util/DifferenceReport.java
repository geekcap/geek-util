package com.geekcap.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A difference report contains the differences between two (or more) artifacts, whether
 * an artifact is a file system directory, a JAR file, or a widget. It contains
 * a list of items that are unique in each artifact (e.g. files that exist in'
 * directory 1, but not in directory 2 and vice versa) as well as a list of items
 * that are different between the two. The DifferenceReport is just a container
 * for this data and it is the responsibility of the utility class generating 
 * the report to determine differences.
 * 
 * @author shaines
 */
public class DifferenceReport implements Serializable 
{
	/**
	 * Mapping of an artifact name to a List of unique items within that artifact  
	 */
	private Map<String,List<String>> uniqueItems = new HashMap<String,List<String>>();
	
	/**
	 * A List of items that have changed between the various artifacts
	 */
	private List<String> changed = new ArrayList<String>();
	
	/**
	 * Default constructor
	 */
	public DifferenceReport()
	{
	}
	
	/**
	 * Returns the names of the artifacts contained within this difference report
	 * 
	 * @return	The names of the artifacts contained within this difference report
	 */
	public Set<String> getArtifactNames()
	{
		return uniqueItems.keySet();
	}
	
	/**
	 * Returns a List of unique items for the specified artifact
	 * 
	 * @param artifact		The artifact for which to return unique items
	 * 
	 * @return				A List of unique items for the specified artifact 
	 */
	public List<String> getUniqueItems( String artifact )
	{
		return uniqueItems.get( artifact );
	}
	
	/**
	 * Adds a unique item to an artifact
	 * 
	 * @param artifact		The artifact that has the unique item
	 * @param item			The item that is unique to the artifact
	 */
	public void addUniqueItem( String artifact, String item )
	{
		if( !uniqueItems.containsKey( artifact ) )
		{
			// Create an artifact entry in the uniqueItems map
			uniqueItems.put( artifact, new ArrayList<String>() );
		}
		
		// Load the list of unique items and add the item to it
		List<String> items = uniqueItems.get( artifact );
		items.add( item );
	}
	
	/**
	 * Returns the list of items that have changed between the artifacts
	 * 
	 * @return		A list of items that have changed between the artifacts
	 */
	public List<String> getChanged() 
	{
		return changed;
	}
	
	/**
	 * Adds an item to the changed list
	 * 
	 * @param item		The item that has changed
	 */
	public void addChangedItem( String item )
	{
		changed.add( item );
	}
}
