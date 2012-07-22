package com.geekcap.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import com.geekcap.util.FieldComparator.SortMode;

/**
 * A List implementation that supports sorting and re-sorting by arbitrary fields of the objects it contains
 * @author shaines
 *
 * @param <E>		The Generic type that is to be contained by the ResortableList
 */
public class ResortableList<E> extends AbstractList<E> {
	
	/**
	 * Maintains all of our objects in a HashMap of the element's integer hashcode
	 * to the object itself.
	 */
	private HashMap<Integer,E> objectMap = new HashMap<Integer,E>();
	
	/**
	 * This is the list that maintains the order of the list. Each integer in the index
	 * is the hashcode for an item contained in the objectMap.
	 */
	private ArrayList<Integer> indexList = new ArrayList<Integer>();
	
	/**
	 * The FieldComparator is used to sort objects in the objectMap 
	 */
	private FieldComparator<E> comparator;
	
	/**
	 * Creates a new ResortableList that contains the specified class.
	 * @param clazz		The class of the objects contained in this list
	 */
	public ResortableList( Class clazz, String comparableProperty ) {
		comparator = new FieldComparator<E>( clazz );
		comparator.setComparableProperty( comparableProperty );
	}
	
	/**
	 * Creates a new ResortableList from an existing collection.
	 * 
	 * @param c						The collection to initialize our List from
	 * @param comparableProperty	The comparable property to sort the list by
	 */
	public ResortableList( Collection<? extends E> c, String comparableProperty ) {
		
		// Iterate over all of the elements in the collection and add them to our list
		for( E e : c ) {
			if( comparator == null ) {
				// Lazy build the comparator
				comparator = new FieldComparator<E>( e.getClass() );
				comparator.setComparableProperty( comparableProperty );
			}
			
			// Add the element to our list
			add( e );
		}
	}

	/**
	 * Locates the hashcode in the index list at the specified index and then returns the
	 * object from the objectMap that has that hashcode
	 */
	public E get(int index) {
		int hashcode = indexList.get( index );
		return objectMap.get( hashcode );
	}

	/**
	 * Returns the number of items in the list
	 */
	public int size() {
		return objectMap.size();
	}
	
	/**
	 * Updates the value of the object at the specified index; looks up the object's hashcode 
	 * from the indexList at the specified index and then updates the objectMap whose key is 
	 * the hashcode.
	 */
	public E set( int index, E element ) {
		
		// Remove the item from the map and index list
		remove( index );
		
		// Add the item back to the list (which will preserve the sorted order) 
		add(index, element);
		
		// Return the element back to the user
		return element;
	}

	/**
	 * Adds an element to the end of the list
	 */
	public void add( int index, E element ) {
		
		// See if we have already setup the comparator
		if( comparator == null ) {
			// Create the comparator
			comparator = new FieldComparator<E>( element.getClass() );
			
			// Set the comparable property to the first field (it should be set by the user!!) 
			comparator.setComparableProperty( comparator.getPropertyNames().iterator().next() );
		}
		
		// Get the hashcode for the element and use it as the key into the objectHashMap
		int hashcode = element.hashCode();
		objectMap.put( hashcode, element );
		
		// Find the location to insert the item
		for( int i=0; i<indexList.size(); i++ ) {
			E existingElement = objectMap.get( indexList.get( i ) );
			if( comparator.compare( element, existingElement ) == -1 ) {
				// Insert the hashcode here
				indexList.add( i, hashcode );
				return;
			}
		}

		// We've reached the end of the list without adding the item, so add it to the end
		indexList.add( hashcode );
		
	}

	/**
	 * Removes the item at the specified index: looks up the hashcode from the indexList,
	 * removes the object from the objectMap with that hashcode, and then removes the hashcode
	 * from the indexList.
	 */
	public E remove( int index ) {
		int hashcode = indexList.remove( index );
		E element = objectMap.remove( hashcode );
		return element;
	}
	
	/**
	 * Returns a Set of sortable properties for the object contained in this list
	 * @return
	 */
	public Set<String> getSortablePropertyNames() {
		return comparator.getPropertyNames();
	}
	
	/**
	 * Sets the sortable property
	 * @param propertyName
	 */
	public void setSortableProperty( String propertyName ) {
		if( comparator != null ) {
			comparator.setComparableProperty( propertyName );
		}
	}
	
    /**
     * Sorts the list in descending order by the sortable property
     */
	public void sortDescending()
	{
	    comparator.setSortMode( SortMode.DESCENDING );
	    internalSort();
	}
	
    /**
     * Sorts the list by the sortable property
     */
	public void sort()
	{
        comparator.setSortMode( SortMode.ASCENDING );
        internalSort();
	}
	
	/**
	 * Sorts the list by the sortable property
	 */
	protected void internalSort() 
	{
		// Build the sortable list
		E[] array = ( E[] )objectMap.values().toArray();
		
		// Sort the list
		SortingUtils<E> sorter = new SortingUtils<E>();
		sorter.quickSort( array, comparator );
		
		// Build our index list with the hash codes of the sorted objects
		indexList.clear();
		for( int i=0; i<array.length; i++ ) {
			indexList.add( array[ i ].hashCode() );
		}
	}
}

