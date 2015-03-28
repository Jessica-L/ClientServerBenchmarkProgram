/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
//package tcpclientserver; // package for Netbeans project
import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * TCPClient process.
 */
public class TCPClient
{
    private int clientId;  // Used to create unique message per client.
    private Socket socket; // Used to establish connection between client and server processes.
    
    // TCPClient constructor.
    public TCPClient( int id )
    {
        clientId = id;
        socket = null;
    }

    // Open connection between client process and server.
    public void openConnection( int port, String ip ) throws TCPException
    {
        try
        {
            socket = new Socket( ip, port );
        }
        catch( IOException e )
        {
            throw new TCPException("Socket failed: " + e.getMessage() );
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
            System.out.println("Write Length: " + len );
            output.writeInt( len );
            //Step 2 -- send byte.
            System.out.println("Writing ...");
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
            System.out.println("Received: " + str);
        }
        catch( UnknownHostException e )
        {
            System.out.println("Socket: " + e.getMessage() );
        }
        catch( EOFException e )
        {
            System.out.println("EOF: " + e.getMessage() );
        }
        catch( IOException e )
        {
            System.out.println("IO: " + e.getMessage() );
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
                    throw new TCPException("Close socket failed: " + e.getMessage() );
                }
            }
    }
    
    // Starts connection between a single client process and server process 
    // where client sends message to server and receives response (if applicable)
    // up to some number of events, numEvents.
    public static void main( String args[] )
    {
        // Set number of events to experiment with frequency of messages (Message Load setting).
        int numEvents = 1000;
        
        TCPClient tcpClient = null;
        int serverPort = 6880;
        String ip = "localhost";
        int id;
        byte[] dataStream;
        
        for( int i = 0; i < numEvents; i++ )
        {
            try
            {
                tcpClient = new TCPClient(Integer.parseInt(args[0]));
            }
            catch( NumberFormatException nfe )
            {
                System.out.println("Invalid parameter passed: " + nfe.getMessage() );
                System.exit(1);
            }

            try
            {
                tcpClient.openConnection( serverPort, ip );
            }
            catch( TCPException tcpe )
            {
                System.out.println("Connection failed: " + tcpe.getMessage() );
                System.exit(2);
            }

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
            
        }
        
    }   
}
