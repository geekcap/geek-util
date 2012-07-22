package com.geekcap.util;

// Import the image classes
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.util.Iterator;

public class ImageUtils
{
    /**
     * Rotates the specified image 90 degrees in the specified direction
     * 
     * @param src           The source image
     * @param clockwise     True = rotate clockwise, false = rotate counter clockwise
     */
    public static BufferedImage rotate( BufferedImage src, boolean clockwise )
    {
        try
        {
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int destWidth = srcHeight;
            int destHeight = srcWidth;

            // Build the new image
            BufferedImage dest = new BufferedImage( destWidth, destHeight, BufferedImage.TYPE_USHORT_565_RGB );
            if( clockwise )
            {
                // Rotate clockwise
                for( int srcX=0, destY=0; srcX<srcWidth; srcX++, destY++ )
                {
                    for( int srcY=srcHeight-1, destX=0; srcY>=0; srcY--, destX++ )
                    {
                        int rgb = src.getRGB( srcX, srcY );
                        dest.setRGB( destX, destY, rgb );
                    }
                }
            }
            else
            {
                // Rotate counter-clockwise
                for( int srcX=srcWidth-1, destY=0; srcX>=0; srcX--, destY++ )
                {
                    for( int srcY=0, destX=0; srcY<srcHeight; srcY++, destX++ )
                    {
                        int rgb = src.getRGB( srcX, srcY );
                        dest.setRGB( destX, destY, rgb );
                    }
                }
            }
            return dest;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * Flips the specified image in the specified direction
     * 
     * @param src           The source image
     * @param horizontal    The axis the flip the image around; 
     *                          true  = turn the image upsidedown
     *                          false = create a mirror of the image 
     * 
     */
    public static BufferedImage flip( BufferedImage src, boolean horizontal )
    {
        try
        {
            int width = src.getWidth();
            int height = src.getHeight();

            // Build the new image
            BufferedImage dest = new BufferedImage( width, height, BufferedImage.TYPE_USHORT_565_RGB );
            if( horizontal )
            {
            }
            else
            {
            }
            return dest;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return src;
    }

    public static void save( BufferedImage bi, String destFilename )
    {
        try
        {
            // Get Writer and set compression
            Iterator iter = ImageIO.getImageWritersByFormatName( "JPG" );
            if( iter.hasNext() ) 
            {
                ImageWriter writer = (ImageWriter)iter.next();
                ImageWriteParam iwp = writer.getDefaultWriteParam();
                iwp.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
                iwp.setCompressionQuality( 0.95f );
                //System.out.println( "Creating file: " + destFilename );
                //File file = new File( destFilename );
                //file.createNewFile();
                MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream( new FileOutputStream( destFilename ) );
                writer.setOutput( mos );
                IIOImage image = new IIOImage( bi, null, null);
                writer.write(null, image, iwp);
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }


    public static void constrain( String srcFilename, String destFilename, int boxSize )
    {
        try
        {
            FileInputStream fis = new FileInputStream( srcFilename );
            MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream( new FileOutputStream( destFilename ) );
            constrain( fis, mos, boxSize );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static byte[] constrain( String srcFilename, int boxSize )
    {
        try
        {
            FileInputStream fis = new FileInputStream( srcFilename );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream( baos );
            constrain( fis, mos, boxSize );
            return baos.toByteArray();
            //ByteArrayInputSteam bais = new ByteArrayInputStream( baos.toByteArray() );
            //return bais;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return new byte[]{};
    }
    
    public static byte[] constrain( InputStream is, int boxSize )
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream( baos );
            constrain( is, mos, boxSize );
            return baos.toByteArray();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static void constrain( String srcFilename, OutputStream os, int boxSize )
    {
        try
        {
            FileInputStream fis = new FileInputStream( srcFilename );
            MemoryCacheImageOutputStream mos = new MemoryCacheImageOutputStream( os );
            constrain( fis, mos, boxSize );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static void constrain( InputStream is, ImageOutputStream os, int boxSize )
    {
        try
        {
            // Read the source file
            BufferedImage input = ImageIO.read( is );

            // Get the original size of the image
            int srcHeight = input.getHeight();
            int srcWidth = input.getWidth();

            // Constrain the thumbnail to a predefined box size
            int height = boxSize;
            int width = boxSize;
            if( srcHeight > srcWidth )
            {
                width = ( int )( ( ( float )height / ( float )srcHeight ) * ( float )srcWidth );
            }
            else if( srcWidth > srcHeight )
            {
                height = ( int )( ( ( float )width / ( float )srcWidth ) * ( float )srcHeight );
            }

            // Create a new thumbnail BufferedImage
            BufferedImage thumb = new BufferedImage( width, height, BufferedImage.TYPE_USHORT_565_RGB );
            Graphics g = thumb.getGraphics();
            g.drawImage( input, 0, 0, width, height, null );

            // Get Writer and set compression
            Iterator iter = ImageIO.getImageWritersByFormatName( "JPG" );
            if( iter.hasNext() ) 
            {
                ImageWriter writer = (ImageWriter)iter.next();
                ImageWriteParam iwp = writer.getDefaultWriteParam();
                iwp.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
                //iwp.setCompressionQuality( 0.75f );
                iwp.setCompressionQuality( 0.95f );
                writer.setOutput( os );
                IIOImage image = new IIOImage(thumb, null, null);
                writer.write(null, image, iwp);
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Constrains an Image (or BufferedImage) to a specific width and heigth; if the source
     * image is smaller than width X height then its size is increased to fit
     */
    public static BufferedImage constrain( Image src, int width, int height )
    {
        try
        {
            // Get the source Image dimensions
            int srcWidth = src.getWidth( null );
            int srcHeight = src.getHeight( null );

            // Compute the width to height ratios of both the source and the constrained window
            double srcRatio = ( double )srcWidth / ( double )srcHeight;
            double windowRatio = ( double )width / ( double )height;

            // These variables will hold the destination dimensions
            int destWidth = width;
            int destHeight = height;

            //if( windowRatio > srcRatio )
            if( windowRatio < srcRatio )
            {
                // Bind the height image to the height of the window
                destHeight = ( int )( ( ( double )width / ( double )srcWidth ) * ( double )srcHeight );
            }
            else
            {
                // Bind the width of the image to the width of the window
                destWidth = ( int )( ( ( double )height / ( double )srcHeight ) * ( double )srcWidth );
            }

            // Create a new BufferedImage and paint our source image to it
            BufferedImage destImage = new BufferedImage( destWidth, destHeight, BufferedImage.TYPE_USHORT_565_RGB );
            Graphics g = destImage.getGraphics();
            g.drawImage( src, 0, 0, destWidth, destHeight, null );

            // Return the new BufferedImage
            return destImage;
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main( String[] args )
    {
        if( args.length < 1 )
        {
            System.out.println( "Usage: ImageUtils <command> <options...>" );
            System.exit( 0 );
        }

        String command = args[ 0 ];
        
        if( command.equalsIgnoreCase( "constrain" ) )
        {
            if( args.length < 2 ) 
            {
                System.out.println( "Usage: ImageUtils constrain {file|folder} <src> <dest> <size>" );
                System.exit( 0 );
            }

            String mode = args[ 1 ];
            String src = args[ 2 ];
            String dest = args[ 3 ];
            int size = Integer.parseInt( args[ 4 ] );
            if( mode.equalsIgnoreCase( "file" ) )
            {
                ImageUtils.constrain( src, dest, size );
            }
            else
            {
                File sourceDir = new File( src );
                File destDir = new File( dest );
                if( sourceDir.exists() && sourceDir.isDirectory() )
                {
                    String[] srcFns = sourceDir.list();
                }
            }
        }

        String src = args[ 0 ];
        String dest = args[ 1 ];
        constrain( src, dest, 128 );
    }

}
