/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Create TCP Client process.
 */
public class TCPClient
{
    private static final AtomicLong totalTime = new AtomicLong();
    private final int clientId;  // Used to create unique message per client.
    private Socket socket; // Used to establish connection between client and server processes.
    
    // TCPClient constructor.
    public TCPClient( int id )
    {
        clientId = id;
        socket = null;
    }

    // Open connection between client process and server.
    public void openConnection( int port, String ip )
    {
        int retries = 0;
        while( retries++ < 5 )
        {
            try
            {
                socket = new Socket( ip, port );
                break;
            }
            catch( IOException e )
            {
                System.out.println( "Socket failed for client " + clientId + ": " + e.getMessage() );
                try
                {
                    Thread.sleep( 100 );
                    //throw new TCPException("Socket failed client " + clientId + ": " + e.getMessage() );
                }
                catch(InterruptedException ex)
                {
                }
            }
        }
    }
    
    // Send message to and receive response from server.
    public void sendAndReceiveMessage(byte[] data )
    {
        int numBytes = 0;
        String str = ""; // Only for testing.
        int len = 0;
        try 
        {
            DataOutputStream output = new DataOutputStream( socket.getOutputStream() );
            DataInputStream input = new DataInputStream( socket.getInputStream() );
            len = data.length;
            
            //***SEND MESSAGE***//
            //Step 1 -- send length.
            //System.out.println("client " + clientId + ": Write Length: " + len );
            output.writeInt( len );
            //Step 2 -- send byte.
            //System.out.println("client " + clientId + ": Writing ...");
            output.write( data, 0, len ); //UTF is a string encoding.
            
            //***RECEIVE RESPONSE (IF APPLICABLE)***//
            //Step 1 -- read length.
            numBytes = input.readInt();
            byte[] digit = new byte[numBytes];
            //Step 2 -- read byte.
            for( int i = 0; i < numBytes; i++ )
            {
                digit[i] = input.readByte();
            }
            // Next two lines only for testing:
            str = new String( digit );
            System.out.println("client " + clientId + ": Received: " + str);
        }
        catch( UnknownHostException e )
        {
            System.out.println("client " + clientId + ": Socket: " + e.getMessage() );
        }
        catch( EOFException e )
        {
            System.out.println("client " + clientId + ": EOF: " + e.getMessage() );
        }
        catch( IOException e )
        {
            System.out.println("client " + clientId + ": IO: " + e.getMessage() );
        }
    }
    
    // Get client id.
    public int getTCPClientId( )
    {
        return clientId;
    }
    
    // Convert integer to byte array.
    public byte[] intToByteArray( int num )
    {
        return ByteBuffer.allocate( 4 ).putInt( num ).array();
    }
    
    // Close connection between client process and server.
    public void closeConnection() throws TCPException
    {
        if( socket != null )
        {
            try
            {
                socket.close();
            }
            catch( IOException e )
            {
                throw new TCPException("client " + clientId + ": Close socket failed: " + e.getMessage() );
            }
        }
    }

    private static void waitToStart()
    {
        File f = new File( "/tmp/serverReady" );

        while( !f.exists() )
        {
            try
	    {
                Thread.sleep( 2 );
            }
	    catch( InterruptedException ex )
	    {
            }
	}
    }

    public static void reportTotalTime()
    {
        System.out.println( "Total Time is " + totalTime + "nanoseconds = "
		          + totalTime.get() / 1000000 + "milliseconds" );

	try
	{
            RandomAccessFile dataFile = new RandomAccessFile( "/tmp/TCPDataFile", "rw" );
	    FileLock         lock     = dataFile.getChannel().lock();

	    try
	    {
	        long t = dataFile.readLong();
		System.out.println( "t = " + t );
	        t += totalTime.get();

	        try
	        {
	            dataFile.writeLong( t );
	            dataFile.close();
	        }
	        catch( IOException ioe )
	        {
                    System.out.println( "Failed to write data: " + ioe.getMessage() );
	            System.exit( 5 );
	        }
	    }
            catch( IOException ioe )
	    {
                System.out.println( "Failed to read data: " + ioe.getMessage() );
	        System.exit( 5 );
            }
	}
	catch( FileNotFoundException fnfe )
	{
            System.out.println( "Failed to open TCPDataFile: " + fnfe.getMessage() );
	    System.exit( 5 );
        }
	catch( IOException ioe )
        {
            System.out.println( "Failed to lock file: " + ioe.getMessage() );
            System.exit( 5 );
        }
    }
    
    // Starts connection between a single client process and server process 
    // where client sends message to server and receives response (if applicable)
    // up to some number of events, numEvents.
    public static void main( String args[] )
    {
        // *** FOR TESTING *** Set number of events to experiment with frequency
        // of messages (Message Load setting).
        int numEvents = 5;
        //int numEvents = 10;
        //int numEvents = 250;
        //int numEvents = 1000;
        
        TCPClient tcpClient = null;
        int serverPort = 6880;
        String ip = "localhost";
        int id;
        byte[] dataStream;
	long startTime;

	waitToStart();
        
        try
        {
            tcpClient = new TCPClient(Integer.parseInt(args[0]));
        }
        catch( NumberFormatException nfe )
        {
            System.out.println("Invalid parameter passed: " + nfe.getMessage() );
            System.exit(1);
        }
        
        for( int i = 0; i < numEvents; i++ )
        {
            System.out.println( "Executing Client " + args[0] + ": iteration #" + i );

	    startTime = System.nanoTime();

            tcpClient.openConnection( serverPort, ip );

            id = tcpClient.getTCPClientId();
            // dataStream = tcpClient.intToByteArray( id );
            dataStream = (byte[])(("" + id).getBytes());

            tcpClient.sendAndReceiveMessage( dataStream );
            
            try
            {
                tcpClient.closeConnection();
            }
            catch( TCPException tcpe )
            {
                System.out.println("Close connection failed: " + tcpe.getMessage() );
                System.exit(3);
            }
            
	    totalTime.addAndGet( System.nanoTime() - startTime );

	    reportTotalTime();
        }
        
    }   
}
