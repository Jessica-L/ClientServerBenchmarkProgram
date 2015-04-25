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
public class SharedMemoryJNI
{
   static
   {
       System.loadLibrary("libSharedMemoryQueue"); // Load native library at runtime
      // System.load( "C:\\Users\\Jessica\\Documents\\NetBeansProjects\\SharedMemoryQueue\\dist\\Debug\\Cygwin_4.x-Windows\\libSharedMemoryQueue.dll" );
   }

   // Declare native methods.
   private native void initServer();
   private native void initClient();
   private native void enqueue( String data );
   private native void enqueueResp( String data );
   private native String dequeue();
   private native String dequeueResp();
   private native void shmNotify();
   private native void shmWait();
   private native void shmNotifyResp();
   private native void shmWaitResp();
   
   public void initializeServer()
   {
       initServer();
   }
   
   public void initializeClient()
   {
       initClient();
   }
   
   public void enqueueData( String data )
   {
       enqueue( data );
   }
   
   public String dequeueData()
   {
       return dequeue();
   }
   
   public void enqueueResponse( String response )
   {
       enqueueResp( response );
   }
   
   public String dequeueResponse()
   {
       return dequeueResp();
   }   
   
   public void notification()
   {
       shmNotify();
   }
   
   public void waitForNotification()
   {
       shmWait();
   }
   
   public void responseNotification()
   {
       shmNotifyResp();
   }
   
   public void waitForResponseNotification()
   {
       shmWaitResp();
   }
 
   // Optional Test Driver
   public static void main(String[] args)
   {
      // invoke the native methods
   }
}
