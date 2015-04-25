/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Test driver for shared memory client server program.
 */
public class SharedMemoryClientServer extends Thread
{
    private final String threadType;
    private final int clientNum;
    
    public SharedMemoryClientServer( String threadType, int clientNum )
    {
        this.threadType = threadType;
        this.clientNum  = clientNum;
    }
    
    public static void main(String[] args)
    {
        // *** FOR TESTING ***
        int numClients = 1;
        //int numClients = 10; // Set number of clients.
        //int numClients = 100; 
        //int numClients = 1000;
        
        ArrayList<SharedMemoryClientServer> l = new ArrayList<>();
        
        //l.add( new TCPClientServer( "Server", -1 ) );
        SharedMemoryClientServer serverThread = new SharedMemoryClientServer( "Server", -1 );
       /* 
        // Start n number of clients and assign unique id per client.
        for( int i = 0; i < numClients; i++ )
        {
            l.add( new SharedMemoryClientServer( "Client", i ) );
        }
        */
        serverThread.start();
        /*
        for( SharedMemoryClientServer c : l )
        {
            c.start();
        }
        
        for( SharedMemoryClientServer c : l )
        {
            try
            {
                c.join();
            }
            catch( InterruptedException ex )
            {
                System.out.println( "join interrupted: " + ex );
            }
        }
        
        System.out.println( "Finished client execution." );
        */
        try
        {
            serverThread.join();
        }
        catch( InterruptedException ex )
        {
            System.out.println( "join interrupted for server: " + ex );
        }
        
        System.out.println( "Server finished execution." );
    }
    
    // Starts process using ProcessBuilder.
    public static void startProcess( ArrayList<String> command )
    {
            //System.out.println( "command 0 = " + command.get( 0 ) );
            //System.out.println( "command 1 = " + command.get( 1 ) );
            //System.out.println( "command 2 = " + command.get( 2 ) );
            //System.out.println( "command 3 = " + command.get( 3 ) );
            //System.out.println( "command 4 = " + command.get( 4 ) );
            
        ProcessBuilder pb = new ProcessBuilder( command );
        pb.directory( new File( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\TCPClientServer\\dist" ) );
        
        try
        {
            Process        p   = pb.start();
            BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
            String         line;

            BufferedReader err = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
            
            while( (line = err.readLine()) != null )
            {
                System.out.println( "ERROR: " + command.get( 3 ) + (command.get( 3 ).equals( "clientserver.SharedMemoryClient" ) ? command.get( 4 ) : "") + ": " + line );
            }
            
            while( (line = br.readLine()) != null )
            {
                System.out.println( command.get( 3 ) + (command.get( 3 ).equals( "clientserver.SharedMemoryClient" ) ? command.get( 4 ) : "") + ": " + line );
            }
            
           // p.waitFor();
            
            //System.out.println( "Process from command <" + command + "> exited with exit value " + p.exitValue() );
        }
        catch( IOException ioe )
        {
            System.out.println("IO Exception: " + ioe.getMessage() );
        }
 /*       catch( InterruptedException ie )
        {
            System.out.println( "Process interrupted: " + ie.getMessage() );
        }*/
    }
    
    public static ArrayList<String> buildArglist( String className, String arg )
    {
        ArrayList<String> argList = new ArrayList<>();
        argList.add( "java" );
        argList.add( "-cp" );
        argList.add( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\TCPClientServer\\dist\\TCPClientServer.jar" );
        argList.add( "clientserver." + className );
        if( !arg.equals( "" ) )
        {
            argList.add( arg );
        }

        return( argList );
    }
    
    // Starts server process.
    public static void startServer()
    {
        System.out.println( "Starting Server." );
        startProcess( TCPClientServer.buildArglist( "SharedMemoryServer", "" ) );
    }
    
    // Starts client process with unique id per process.
    public static void startClient( int num )
    {
        System.out.println( "Starting Client #" + num );
        startProcess( TCPClientServer.buildArglist( "SharedMemoryClient", Integer.toString( num ) ) );
    }

    @Override
    public void run()
    {
       if( threadType.equals( "Server" ) )
       {
           startServer();
       }
       else
       {
           startClient( clientNum );
       }
    }
 /*
    public static void main(String[] args)
    {
        // *** FOR TESTING *** 
        int numClients = 10; // Set number of clients.
        //int numClients = 100; 
        //int numClients = 1000;
        
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
        for( String s : command )
        {
            System.out.println( s );
        }
        ProcessBuilder pb = new ProcessBuilder( command );
        try
        {
            Process p = pb.start();
            System.out.println( "Exit value = " + p.exitValue() );
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
        argList.add( "-cp" );
        argList.add( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\TCPClientServer\\dist\\TCPClientServer.jar" );
        argList.add("SharedMemoryServer");
        startProcess( argList );
        System.out.println("Starting server process.");
    }

    // Starts client process with unique id per process.
    public static void startClient( int num )
    {
        ArrayList<String> argList = new ArrayList<String>();
        argList.add("java");
        argList.add( "-cp" );
        argList.add( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\TCPClientServer\\dist\\TCPClientServer.jar" );
        argList.add("SharedMemoryClient");
        argList.add( Integer.toString( num ) );
        startProcess( argList );
        System.out.println("Starting client process.");
    }
   */ 
}
