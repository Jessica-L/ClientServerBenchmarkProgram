/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test driver to start server process and n number of client processes
 * (to establish contention).
 */
public class TCPClientServer extends Thread
{
    private final String threadType;
    private final int clientNum;
    
    public TCPClientServer( String threadType, int clientNum )
    {
        this.threadType = threadType;
        this.clientNum  = clientNum;
    }

    private static void printFinalReport()
    {
        try
	{
            RandomAccessFile raf = new RandomAccessFile( "/tmp/TCPDataFile", "rw" );
	    long             t   = raf.readLong();
	    System.out.println( "t = " + t );
	    raf.close();
	    System.out.println( "Total TCP Client to Server to Client run time is " + t + " nanoseconds = " + t / 1000000 + " milliseconds." );
        }
	catch (FileNotFoundException ex)
	{
            System.out.println( "Failed to open TCPDataFile for reading: " + ex.getMessage() );
        }
	catch( IOException ex )
	{
            System.out.println( "Failed to read from TCPDataFile: " + ex.getMessage() );
        }
    }
    
    public static void main(String[] args)
    {
        // *** FOR TESTING ***
        int numClients = 10;
        //int numClients = 10; // Set number of clients.
        //int numClients = 100; 
        //int numClients = 1000;

	//File dataFile = new File( "/tmp/TCPDataFile" );
        try
	{
            //dataFile.createNewFile();
            System.out.println( "Writing 0 to the file." );
	    //(new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "/tmp/TCPDataFile" ) ) ) ).write( "0" );
	    //(new FileWriter( "/tmp/TCPDataFile" )).write( "0" );
	    RandomAccessFile raf = new RandomAccessFile( "/tmp/TCPDataFile", "rw" );
	    raf.writeLong( 0 );
	    raf.close();
        }
	catch( IOException ex )
	{
            System.out.println( "Failed to create TCPDataFile." );
	    System.exit( 5 );
        }

        ArrayList<TCPClientServer> l = new ArrayList<>();
        
        //l.add( new TCPClientServer( "Server", -1 ) );
        TCPClientServer serverThread = new TCPClientServer( "Server", -1 );
        
        // Start n number of clients and assign unique id per client.
        for( int i = 0; i < numClients; i++ )
        {
            l.add( new TCPClientServer( "Client", i ) );
        }
        
        serverThread.start();
        
        for( TCPClientServer c : l )
        {
            c.start();
        }
        
        for( TCPClientServer c : l )
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
        
        try
        {
            serverThread.join();
        }
        catch( InterruptedException ex )
        {
            System.out.println( "join interrupted for server: " + ex );
        }
        
        System.out.println( "Server finished execution." );

	(new File( "/tmp/serverReady" )).delete();
	printFinalReport();
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
        //pb.directory( new File( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\TCPClientServer\\dist" ) );
        pb.directory( new File( "/home/christopher/helpJess/concProgProject" ) );
        
        try
        {
            Process        p   = pb.start();
            BufferedReader br = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
            String         line;

            BufferedReader err = new BufferedReader( new InputStreamReader( p.getErrorStream() ) );
            
            while( (line = err.readLine()) != null )
            {
                System.out.println( "ERROR: " + command.get( 3 ) + (command.get( 3 ).equals( "clientserver.TCPClient" ) ? command.get( 4 ) : "") + ": " + line );
            }
            
            while( (line = br.readLine()) != null )
            {
                System.out.println( command.get( 3 ) + (command.get( 3 ).equals( "clientserver.TCPClient" ) ? command.get( 4 ) : "") + ": " + line );
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
        argList.add( "C:/home/christopher/helpJess/concProgProject/queueProject.jar" );
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
        startProcess( TCPClientServer.buildArglist( "TCPServer", "" ) );
    }
    
    // Starts client process with unique id per process.
    public static void startClient( int num )
    {
        System.out.println( "Starting Client #" + num );
        startProcess( TCPClientServer.buildArglist( "TCPClient", Integer.toString( num ) ) );
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
}
