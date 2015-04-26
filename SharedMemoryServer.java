/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jessica
 */
public class SharedMemoryServer
{
    private SharedMemoryJNI shm;
    
    public SharedMemoryServer()
    {
       shm = new SharedMemoryJNI();
       shm.initializeServer();
    }
    
    public void run()
    {
        while( true )
        {
            //System.out.println( "Server: Waiting for notification" );
            shm.waitForNotification();
            //System.out.println( "Server: Notification received, retrieving message." );
            
            processMsg( shm.dequeueData() );
        }
    }
    
    private void processMsg( String msg )
    {
        //System.out.println( "Server received message " + msg + "." );
        shm.enqueueResponse( "Server: Received message: " + msg );
        shm.responseNotification();
    }
    
    public static void main(String[] args)
    {
        //System.out.println("Entering shared memory server main.");
        SharedMemoryServer s = new SharedMemoryServer();
        try
	{
            (new File( "/tmp/smInitialized" )).createNewFile();
        }
	catch( IOException ioe )
	{
            System.out.println( "Failed to create tmp/smInitialized: " + ioe.getMessage() );
	    System.exit( 5 );
        }

        s.run();
    }
}
