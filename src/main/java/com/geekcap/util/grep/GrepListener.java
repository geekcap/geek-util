package com.geekcap.util.grep;

/**
 * A listener interface for classes that want to asynchronously use the Grep class
 * 
 * @author shaines
 */
public interface GrepListener 
{
	/**
	 * Called when the grep class starts parsing the input stream
	 */
	public void grepStart();
	
	/**
	 * Called when the Grep class finds a line that matches the search criteria
	 * 
	 * @param line		The matched line
	 */
	public void lineMatch( String line );
	
	/**
	 * Called when the Grep class completes, which is typically when the stream is closed
	 */
	public void grepComplete();
	
	/**
	 * Denotes that an exception occurred during the grep process
	 * 
	 * @param e			The underlying exception
	 */
	public void grepException( GrepException e );

}
