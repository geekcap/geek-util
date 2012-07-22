package com.geekcap.util;

import java.util.Comparator;

/**
 * Provides implementations of various sorting algorithms (currently only quick sort.)
 * 
 * @author shaines
 *
 * @param <T>		The type of object that this sorter will be sorting
 */
public class SortingUtils<T>
{
	/**
	 * Sorts the T[] using the quick sort algorithm
	 * 
	 * @param a				An array of T objects to sort
	 * @param comparator	The comparator to sort T with
	 */
  public void quickSort( T[] a, Comparator<T> comparator )
  {
    q_sort( a, 0, a.length - 1, comparator );
  }


  /**
   * Internal recursive quick sort method
   * @param a				The array to sort
   * @param left			The left index to start the sort
   * @param right			The right index to end the sort
   * @param comparator	The comparator to sort with
   */
  private void q_sort( T[] a, int left, int right, Comparator<T> comparator )
  {
    int l_hold, r_hold;

    l_hold = left;
    r_hold = right;
    T pivot = a[left];
    while( left < right )
    {
    	// Walk from the right index as far to the left as possible
      while( comparator.compare( a[ right ], pivot ) >= 0 && (left < right) )
        right--;
      
      if( left != right )
      {
        a[left] = a[right];
        left++;
      }

      // Walk from the left index as far to the right as possible
      while( comparator.compare( a[ left ], pivot ) <= 0 && (left < right) )
        left++;

      if( left != right )
      {
        a[right] = a[left];
        right--;
      }
    }
    a[left] = pivot;

    // Calculate a new pivot position
    int pivotPos = left;
    left = l_hold;
    right = r_hold;

    // Quicksort the appropriate sublist
    if( left < pivotPos )
      q_sort(a, left, pivotPos-1, comparator);
    if( right > pivotPos )
      q_sort(a, pivotPos+1, right, comparator);
  }
}