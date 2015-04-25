/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

/**
 *
 * @author Jessica
 */
public class SharedMemoryServer
{
    SharedMemoryJNI shm;
    
    public SharedMemoryServer()
    {
       shm = new SharedMemoryJNI();
       shm.initializeServer();
    }
    
    public void run()
    {
        while( true )
        {
            shm.waitForNotification();
            
            processMsg( shm.dequeueData() );
        }
    }
    
    private void processMsg( String msg )
    {
        shm.enqueueResponse( "Received message: " + msg );
    }
    
    public static void main(String[] args)
    {
        System.out.println("Entering shared memory client main.");
        SharedMemoryServer s = new SharedMemoryServer();
        s.run();
    }
}
