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
      System.loadLibrary("hello"); // Load native library at runtime
                                   // hello.dll (Windows) or libhello.so (Unixes)
   }
 
   // Declare native methods.
   private native void initServer();
   private native void initClient();
   private native void enqueue();
   private native void dequeue();
   
   public void initializeServer()
   {
       initServer();
   }
   
   public void initializeClient()
   {
       initClient();
   }
   
   public void enqueueData()
   {
       enqueue();
   }
   
   public void dequeueData()
   {
       dequeue();
   }
 
   // Optional Test Driver
   public static void main(String[] args)
   {
      // invoke the native methods
   }
}
