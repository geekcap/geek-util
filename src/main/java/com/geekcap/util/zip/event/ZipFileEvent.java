package com.geekcap.util.zip.event;

public class ZipFileEvent extends java.util.EventObject
{
    private String filename;

    public ZipFileEvent( Object source )
    {
        super( source );
    }

    public ZipFileEvent( Object source, String filename )
    {
        super( source );
        this.filename = filename;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }
}
