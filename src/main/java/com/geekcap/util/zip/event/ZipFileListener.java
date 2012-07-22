package com.geekcap.util.zip.event;

/**
 * Listeners will be notified of the status of a compression or decompression
 * operation
 */
public interface ZipFileListener
{
    /**
     * A file in the compression operation was compressed
     */
    public void fileCompressed( ZipFileEvent e );

    /**
     * The compression operation has completed
     */
    public void compressionComplete( ZipFileEvent e );

    /**
     * A file in the decompression operation was decompressed
     */
    public void fileDecompressed( ZipFileEvent e );

    /**
     * The decompression operation has completed
     */
    public void decompressionComplete( ZipFileEvent e );
    
    public void decompressionError( ZipFileEvent e );
}
