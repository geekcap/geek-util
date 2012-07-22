package com.geekcap.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A comparator that compares objects of type T by the specified field
 * 
 * @author shaines
 *
 * @param <T>		The type of object to compare
 */
public class FieldComparator<T> implements Comparator<T> 
{
    /**
     * Enumaration that defines constants so that the user can sort items in ascending (default) or descending order
     * 
     * @author shaines
     */
    public enum SortMode { ASCENDING, DESCENDING };
	
	/**
	 * Maps property names to the method that can be used to invoke the getter for the property
	 */
	private Map<String,Method> propertyMethodMap = new HashMap<String,Method>();
	
	/**
	 * The name of the comparable property to use by this comparable
	 */
	private String comparableProperty;
	
	/**
	 * If true then we'll keep true values in all cases (return 1), if false then we return 0 if items are equal
	 * for the same property, which *may* cause the item to be removed, such as in Sets that do not allow duplicate keys
	 */
	private boolean keepEqual = false;
	
	/**
	 * Should items be sorted in ascending or descending order?
	 */
	private SortMode sortMode = SortMode.ASCENDING;
	
	/**
	 * Creates a new FieldComparator.
	 * 
	 * @param clazz		The class is needed for reflection. This is redundant information because
	 * 					we should know the type from T, but there is no easy way to get to that info
	 */
	public FieldComparator( Class clazz ) 
	{
		init( clazz );
	}
	
	public FieldComparator( Class clazz, String property )
	{
		init( clazz );
		comparableProperty = property;
	}
	
	public FieldComparator( Class clazz, String property, boolean keepEqual )
	{
		init( clazz );
		comparableProperty = property;
		this.keepEqual = keepEqual;
	}
	
	private void init( Class clazz )
	{
		
		// Load all public methods for this class as well as its super classes
		Method[] methods = clazz.getMethods();
		
		// Find all getters
		for( int i=0; i<methods.length; i++ ) 
		{
			String name = methods[ i ].getName();
			if( name.startsWith( "get" ) || name.startsWith(  "is" ) ) 
			{
				
				// TODO: check to make sure this object is comparable or a primitive type - otherwise
				// don't make it available as a comparison property

				// Derive the property name
			    String propertyName = null;
			    if( name.startsWith( "get" ) )
			    {
			        propertyName = Character.toLowerCase( name.charAt( 3 ) ) + name.substring( 4 );
			    }
			    else
			    {
                    propertyName = Character.toLowerCase( name.charAt( 2 ) ) + name.substring( 3 );
			    }
				
				// Ignore getClass() but process the rest...
				if( !propertyName.equals( "class" ) ) 
				{
					//System.out.println( "Adding property " + propertyName + " to the comparable fields, returns: " + methods[ i ].getReturnType().getName() );
					propertyMethodMap.put( propertyName, methods[ i ] );
				}
			}
		}
	}
	
	/**
	 * Returns a Set containing all comparable properties for this object
	 * @return
	 */
	public Set<String> getPropertyNames() {
		return propertyMethodMap.keySet();
	}

	/**
	 * Returns the parameter of T that is being compared
	 * @return
	 */
	public String getComparableProperty() {
		return comparableProperty;
	}

	/**
	 * Sets the parameter of T that should be compared
	 * @param comparableProperty
	 */
	public void setComparableProperty(String comparableProperty) {
		this.comparableProperty = comparableProperty;
	}
	
	/**
	 * Sets the sort mode, which determines the order that items should be returned, valid values are: 
	 *     ASCENDING (default) 
	 *     DESCENDING
	 *     
	 * @param sortMode     Should items be returned in ascending or descending order
	 */
	public void setSortMode( SortMode sortMode )
	{
	    this.sortMode = sortMode;
	}
	
	/**
	 * Returns the sort mode: should items be sorted in ascending or descending order?
	 * 
	 * @return             Will items be sorted in ascending or descending order?
	 */
	public SortMode getSortMode()
	{
	    return sortMode;
	}
	
	public void setKeepEqual( boolean keepEqual )
	{
		this.keepEqual = keepEqual;
	}

	/**
	 * Comparison implementation
	 */
	@Override
	public int compare(Object o1, Object o2) 
	{
		Method method = null;
		if( comparableProperty != null && propertyMethodMap.containsKey( comparableProperty ) ) 
		{
			
			// Load the method that returns the field value that we should use in our comparisons
			method = propertyMethodMap.get( comparableProperty );
		}
		else {
			
			// We don't return 0 for equality because if this is used to build a set then all 
			// equal objects would be eliminated, so let's just line them up...
			return 1;
		}
		
		try {
			// Obtain the values of the objects' property
			Object result1 = method.invoke( o1, new Object[]{} );
			Object result2 = method.invoke( o2, new Object[]{} );
			
			// Compare those values
			int result = ( ( Comparable )result1 ).compareTo( result2 );
			if( result == 0 && keepEqual )
			{
				// If we want to keep records that return have the same value, then return 1 so
				// that it won't end up being thrown out
				return 1;
			}
			
			if( sortMode == SortMode.ASCENDING )
			    return result;
			else
			    return ( ( -1 ) * result );
		}
		catch( Exception e ) {
			return 1;
		}
	}
}