package com.geekcap.util.zip;

import java.io.File;

public class Entry
{
    //* Returns a list of Entries: name, date, size, and path, sorted by path
    private String filename;
    private String date;
    private String size;
    private String compressedSize;
    private String path;

    public Entry()
    {
    }

    public Entry( String filename,
                  String date,
                  String size,
                  String compressedSize,
                  String path )
    {
        this.filename = filename;
        this.date = date;
        this.size = size;
        this.compressedSize = compressedSize;
        this.path = path;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public String getDate()
    {
        return this.date;
    }

    public String getSize()
    {
        return this.size;
    }

    public String getCompressedSize()
    {
        return this.compressedSize;
    }

    public String getPath()
    {
        return this.path;
    }

    public String toString()
    {
        return path + File.separator + filename + " " + size + "(" + compressedSize + ")\t" + date;
        //return filename + "\t" + size + "(" + compressedSize + ")\t" + date + "\t" + path;
    }

    
}
