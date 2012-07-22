package com.geekcap.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Encrypts and decrypts Strings. Based initially on the code written by Jeffrey
 * M Hunter from http://www.idevelopment.info, but modified heavily.
 */
public class EncryptionUtils
{
    /**
     * The String that is used to specify the Data Encryption Standard algorithm
     */
    public static final String DES = "DES";

    /**
     * The String that is used to specify the Triple Data Encryption Standard algorithm
     */
    public static final String TRIPLE_DES = "DESede";

    /**
     * The String that is used to specify the Blowfish algorithm
     */
    public static final String BLOWFISH = "Blowfish";

    /**
     * Encryption Cipher
     */
    private Cipher encryptionCipher;

    /**
     * Decryption Cipher
     */
    private Cipher decryptionCipher;

    /**
     * Constructor used to create this object. Responsible for setting and
     * initializing this object's encryption and decryption Cipher instances
     * given a Secret Key and algorithm.
     *
     * @param key
     *            Secret Key used to initialize both the encrypter and decrypter
     *            instances.
     * @param algorithm
     *            Which algorithm to use for creating the encrypter and
     *            decrypter instances.
     */
    public EncryptionUtils( SecretKey key, String algorithm )
    {
        try
        {
            // Create our ciphers
            encryptionCipher = Cipher.getInstance( algorithm );
            decryptionCipher = Cipher.getInstance( algorithm );

            // Initialze our ciphers
            encryptionCipher.init( Cipher.ENCRYPT_MODE, key );
            decryptionCipher.init( Cipher.DECRYPT_MODE, key );
        }
        catch( NoSuchPaddingException e )
        {
            System.out.println("EXCEPTION: NoSuchPaddingException");
        }
        catch( NoSuchAlgorithmException e )
        {
            System.out.println("EXCEPTION: NoSuchAlgorithmException");
        }
        catch( InvalidKeyException e )
        {
            System.out.println("EXCEPTION: InvalidKeyException");
            e.printStackTrace();
        }
    }

    /**
     * Constructor used to create this object. Responsible for setting and
     * initializing this object's encrypter and decrypter Chipher instances
     * given a Pass Phrase and algorithm.
     *
     * @param passPhrase    Pass Phrase used to initialize both the encrypter and
     *                      decrypter instances.
     */
    public EncryptionUtils( String passPhrase )
    {
        // 8-bytes Salt
        byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56,
                        (byte) 0x34, (byte) 0xE3, (byte) 0x03 };

        // Iteration count
        int iterationCount = 19;

        try
        {
            // Create a new secret key
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret( keySpec );

            encryptionCipher = Cipher.getInstance( key.getAlgorithm() );
            decryptionCipher = Cipher.getInstance( key.getAlgorithm() );

            // Prepare the parameters to the ciphers
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec( salt, iterationCount );

            encryptionCipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            decryptionCipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        }
        catch( InvalidAlgorithmParameterException e )
        {
            System.out.println("EXCEPTION: InvalidAlgorithmParameterException");
        }
        catch( InvalidKeySpecException e )
        {
            System.out.println("EXCEPTION: InvalidKeySpecException");
        }
        catch( NoSuchPaddingException e )
        {
            System.out.println("EXCEPTION: NoSuchPaddingException");
        }
        catch( NoSuchAlgorithmException e )
        {
            System.out.println("EXCEPTION: NoSuchAlgorithmException");
        }
        catch( InvalidKeyException e )
        {
            System.out.println("EXCEPTION: InvalidKeyException");
        }
    }

    /**
     * Takes a single String as an argument and returns an Encrypted version of
     * that String.
     *
     * @param str
     *            String to be encrypted
     * @return <code>String</code> Encrypted version of the provided String
     */
    public String encrypt( String str )
    {
        try
        {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = encryptionCipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new String(Base64Coder.encode(enc));
        }
        catch( BadPaddingException e )
        {
            e.printStackTrace();
        }
        catch( IllegalBlockSizeException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes a encrypted String as an argument, decrypts and returns the
     * decrypted String.
     *
     * @param str   Encrypted String to be decrypted
     *
     * @return <code>String</code> Decrypted version of the provided String
     */
    public String decrypt( String str )
    {
        try
        {
            // Decode base64 to get bytes
            byte[] dec = Base64Coder.decode( str );

            // Decrypt
            byte[] utf8 = decryptionCipher.doFinal( dec );

            // Decode using utf-8
            return new String( utf8, "UTF8" );
        }
        catch( BadPaddingException e )
        {
            e.printStackTrace();
        }
        catch( IllegalBlockSizeException e )
        {
            e.printStackTrace();
        }
        catch( UnsupportedEncodingException e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a SecretKey to a String by getting the encoding (byte array) from the
     * secret key and then encoding it with Base64 encoding.
     *
     * @param secretKey     The SecretKey to convert to a String
     *
     * @return              A Base64 encoded version of the SecretKey
     */
    public static String convertSecretKeyToString( SecretKey secretKey )
    {
        return new String( Base64Coder.encode( secretKey.getEncoded() ) );
    }

    /**
     * Converts a Base64 encoded String into a SecretKey for the specified algorthim
     *
     * @param source        The Base64 encoded String
     * @param algorithm     The encryption algorithm, e.g. Blowfish, DES, DESEde
     *
     * @return              The resultant SecretKey
     */
    public static SecretKey convertStringToSecretKey( String source, String algorithm )
    {
        return new SecretKeySpec( Base64Coder.decode( source ), algorithm );
    }

    /**
     * Generates a new secret key
     *
     * @param algorithm     The name of the algorithm to use when generating the secret key
     *
     * @return              A new SecretKey
     */
    public static SecretKey generateSecretKey( String algorithm )
    {
        try
        {
            return KeyGenerator.getInstance( algorithm ).generateKey();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Builds an XML key file for the specified key, encrypted with the specified passphrase
     *
     * @param secretKey     The key to encrypt and store in the XML file
     * @param passphrase    The passphrase to encrypt the key with
     * @param filename      The name of the file to write the XML document
     */
    public static void writeSecretKeyToXmlFile( SecretKey secretKey, String passphrase, String filename )
    {
        try
        {
            // Encrypt the SecretKey using the user's passphrase
            String secretKeyString = EncryptionUtils.convertSecretKeyToString( secretKey );
            EncryptionUtils se = new EncryptionUtils( passphrase );
            String encryptedKeyString = se.encrypt( secretKeyString );

            // Build a JDOM document
            Element root = new Element( "keyfile" );
            root.setAttribute( "algorithm", secretKey.getAlgorithm() );
            root.addContent( encryptedKeyString );

            // Write the document to the file
            FileOutputStream fos = new FileOutputStream( filename );
            XMLOutputter out = new XMLOutputter( Format.getPrettyFormat() );
            out.output( root, fos );
            fos.flush();
            fos.close();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    /**
     * Loads the SecretKey from the specified XML file
     *
     * @param filename      The name of the XML file
     * @param passphrase    The passphrase with which the SecretKey is encrypted
     *
     * @return              The SecretKey contained in the file
     */
    public static SecretKey readSecretKeyFromXmlFile( String filename, String passphrase )
    {
        try
        {
            // Load the XML document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build( new FileInputStream( filename ) );
            Element root = doc.getRootElement();

            // Load the encrypted string
            String algorithm = root.getAttributeValue( "algorithm" );
            String encryptedKeyString = root.getText();
            if( encryptedKeyString != null )
            {
                // Decrypt the encrypted key
                EncryptionUtils se = new EncryptionUtils( passphrase );
                String secretKeyString = se.decrypt( encryptedKeyString );

                // Convert the secret key string to a SecretKey
                return EncryptionUtils.convertStringToSecretKey( secretKeyString, algorithm );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sole entry point to the class and application used for testing the String
     * Encrypter class.
     *
     * @param args
     *            Array of String arguments.
     */
    public static void main(String[] args)
    {
        if( args.length == 0 )
        {
            System.out.println("Usage: StringEncrypter <cmd> <args>");
            System.out.println("\tcmd: ");
            System.out.println("\t\tgenerate-key <passphrase> <algorithm>");
            System.out.println("\t\tencrypt <fromFile> <toFile>");
            System.out.println("\t\tdecrypt <fromFile> <toFile>");
            System.exit(0);
        }
        String command = args[0];
        if( command.equalsIgnoreCase("generate-key") )
        {
            if( args.length < 3 )
            {
                System.out.println( "Usage: StringEncrypter generate-key <passphrase> <algorithm> {file=none}" );
                System.exit(0);
            }
            String passphrase = args[ 1 ];
            String algorithm = args[ 2 ];

            // Create a new
            SecretKey secretKey = EncryptionUtils.generateSecretKey( algorithm );
            String secretKeyString = EncryptionUtils.convertSecretKeyToString( secretKey );
            EncryptionUtils se = new EncryptionUtils( passphrase );
            String encryptedKeyString = se.encrypt( secretKeyString );

            System.out.println( "Passphrase: " + passphrase );
            System.out.println( "Algorithm:  " + algorithm );
            System.out.println( "Secret Key String: " + secretKeyString );
            System.out.println( "Encrypted Key String: " + encryptedKeyString );

            if( args.length > 3 )
            {
                // Write the key to a file
                String fn = args[ 3 ];
                EncryptionUtils.writeSecretKeyToXmlFile( secretKey, passphrase, fn );
            }
        }
        else if( command.equalsIgnoreCase( "encrypt-string" ) )
        {
            if( args.length < 4 )
            {
                System.out.println( "Usage: StringEncrypter encrypt-string <keyfile=xmlfile> <passphrase> <string-to-encrypt>" );
                System.exit( 0 );
            }

            String keyfile = args[ 1 ];
            String passphrase = args[ 2 ];
            String plaintext = args[ 3 ];

            SecretKey secretKey = EncryptionUtils.readSecretKeyFromXmlFile( keyfile, passphrase );
            EncryptionUtils se = new EncryptionUtils( secretKey, secretKey.getAlgorithm() );
            String cipherText = se.encrypt( plaintext );

            System.out.println( " Plain text: " + plaintext );
            System.out.println( "Cipher text: " + cipherText );
        }
        else if( command.equalsIgnoreCase( "decrypt-string" ) )
        {
            if( args.length < 4 )
            {
                System.out.println( "Usage: StringEncrypter decrypt-string <keyfile> <passphrase> <string-to-decrypt>" );
                System.exit( 0 );
            }

            String keyfile = args[ 1 ];
            String passphrase = args[ 2 ];
            String cipherText = args[ 3 ];

            SecretKey secretKey = EncryptionUtils.readSecretKeyFromXmlFile( keyfile, passphrase );
            EncryptionUtils se = new EncryptionUtils( secretKey, secretKey.getAlgorithm() );
            String plaintext = se.decrypt( cipherText );

            System.out.println( "Cipher text: " + cipherText );
            System.out.println( " Plain text: " + plaintext );
        }
        else
        {
            System.out.println("Unknown Command: " + command);
            EncryptionUtils se = new EncryptionUtils( "My name is my passport" );
            String secret = "This is my secret";
            String encryptedSecret = se.encrypt( secret );

            EncryptionUtils se2 = new EncryptionUtils( "My name is my passport" );
            String decryptedSecret = se2.decrypt( encryptedSecret );

            System.out.println( "Secret: " + secret );
            System.out.println( "Encrypted Secret: " + encryptedSecret );
            System.out.println( "Decrypted Secret: " + decryptedSecret );


        }

    }


}
