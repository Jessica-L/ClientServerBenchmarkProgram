/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TCPClientServer program starts server process and n number of client processes (to establish contention).
 */
public class TCPClientServer {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        int numClients = 50; // Set number of clients.
        startServer();
        // Start n number of clients and assign unique id per client.
        for( int i = 0; i < numClients; i++ )
        {
            startClient( i );
        }
    }
    
    // Starts process using ProcessBuilder.
    public static void startProcess( ArrayList<String> command )
    {
        ProcessBuilder p = new ProcessBuilder( command );
        try
        {
            p.start();
        }
        catch( IOException e )
        {
            System.out.println("IO: " + e.getMessage() );
        }
        
    }
    
    // Starts server process.
    public static void startServer()
    {
        ArrayList<String> argList = new ArrayList<String>();
        argList.add("java");
        argList.add("TCPServer");
        startProcess( argList );
    }
    
    // Starts client process with unique id per process.
    public static void startClient( int num )
    {
        ArrayList<String> argList = new ArrayList<String>();
        argList.add("java");
        argList.add("TCPClient");
        argList.add( Integer.toString( num ) );
        startProcess( argList );
    }
    
}

/* My old code.
public class TCPClientServer {
    public static void main(String[] args)
    {
        int numClients = 50; // Set number of clients.
        startServer();
        // Start n number of clients and assign unique id per client.
        for( int i = 0; i < numClients; i++ )
        {
            startClient( i );
        }
    }
    
    // Starts process using ProcessBuilder.
    public static void startProcess( ArrayList<String> command )
    {
        ProcessBuilder p = new ProcessBuilder( command );
        try
        {
            p.start();
        }
        catch( IOException e )
        {
            System.out.println("IO: " + e.getMessage() );
        }
        
    }
    
    // Starts server process.
    public static void startServer()
    {
        ArrayList<String> argList = new ArrayList();
        argList.add("java");
        argList.add("TCPServer");
        startProcess( argList );
    }
    
    // Starts client process with unique id per process.
    public static void startClient( int num )
    {
        ArrayList<String> argList = new ArrayList();
        argList.add("java");
        argList.add("TCPClient");
        argList.add( Integer.toString( num ) );
        startProcess( argList );
    }   
}
*/
