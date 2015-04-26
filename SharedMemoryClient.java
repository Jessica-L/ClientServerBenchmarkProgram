/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Create shared memory client process.
 */
public class SharedMemoryClient
{   
    private SharedMemoryJNI shm;
    private int clientId;
    
    public SharedMemoryClient( int clientId )
    {
        shm = new SharedMemoryJNI();
        shm.initializeClient();
        this.clientId = clientId;
    }
    
    public void sendMsg( String msg )
    {
        //System.out.println( "enqueuing message: " + msg + "." );
        shm.enqueueData( msg );
        //System.out.println( "Sending notification" );
        shm.notification();
    }
    
    public String getResponse()
    {
        //System.out.println( "Waiting for notification" );
        shm.waitForResponseNotification();
        //System.out.println( "Received notification." );
        return( shm.dequeueResponse() );
    }
    
    public void run( int numIterations )
    {
        String s = null;
        
        for( int i = 0; i < numIterations; i++ )
        {
            //System.out.println( "client " + clientId + ": sending message." );
            sendMsg( "Message: " + i ); //Change this message to translator msg.
            //System.out.println( "client " + clientId + ": getting response." );
            //System.out.println( "Response: " + getResponse() );
	    getResponse();
        }
    }

    private static void waitToStart()
    {
        File f = new File( "/tmp/smInitialized" );

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
    
    public static void main(String[] args)
    { 
	waitToStart();
        //System.out.println("Entering shared memory client main.");
        SharedMemoryClient c = new SharedMemoryClient( Integer.parseInt(args[0] ) );
        //*** FOR TESTING *** Message load (number of messages sent)
        c.run( 10 );
        //c.run( 250 );
        //c.run( 1000 );
         
    }
}
