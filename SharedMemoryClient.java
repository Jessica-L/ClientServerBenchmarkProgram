/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;


/**
 * Create shared memory client process.
 */
public class SharedMemoryClient
{   
    SharedMemoryJNI shm;
    
    public SharedMemoryClient()
    {
        shm = new SharedMemoryJNI();
        shm.initializeClient();
    }
    
    public void sendMsg( String msg )
    {
        shm.enqueueData( msg );
        shm.notification();
    }
    
    public String getResponse()
    {
        return( shm.dequeueResponse() );
    }
    
    public void run( int numIterations )
    {
        String s = null;
        
        for( int i = 0; i < numIterations; i++ )
        {
            sendMsg( "Message: " + i ); //Change this message to translator msg.
            System.out.println( "Response: " + getResponse() );
        }
    }
    
    public static void main(String[] args)
    { 
        System.out.println("Entering shared memory client main.");
        SharedMemoryClient c = new SharedMemoryClient();
        //*** FOR TESTING *** Message load (number of messages sent)
        c.run( 10 );
        //c.run( 250 );
        //c.run( 1000 );
         
    }
}
