/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

/**
 * Custom exception class.
 */
public class TCPException extends Exception
{
    public TCPException( )
    {
        super( );
    }
    
    public TCPException( String msg )
    {
        super( msg );
    }
    
    public TCPException( String msg, Throwable cause )
    {
        super( msg, cause );
    }
    
    public TCPException( Throwable cause )
    {
        super( cause );
    }
}
