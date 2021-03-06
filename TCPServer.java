/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Create TCP Server process.
 */
public class TCPServer {
    public static void main (String args[])
    {
        System.out.println( "Executing server." );
        
        //for (int i = 0; i < 5; i++) {
            //MessageFetcher f = new MessageFetcher();
        //}

        try
        {
            int serverPort = 6880;
            ServerSocket listenSocket = new ServerSocket( serverPort );

	    try
	    {
                (new File( "/tmp/serverReady" )).createNewFile();
            }
	    catch( IOException ioe )
	    {
                System.out.println( "Failed to create tmp/serverReady: " + ioe.getMessage() );
	        System.exit( 5 );
            }

            System.out.println("Server start listening now ...");
            while( true ) {
                try
                {
                    //Create socket and wait to receive message.
                    Socket clientSocket = listenSocket.accept();
                    //Spawn new thread to receive message.
                    //System.out.println(clientSocket);
                    MessageReceiver mrThread = new MessageReceiver( clientSocket );
                }
                catch( IOException e )
                {
                    System.out.println("Listen: " + e.getMessage());
                }
            }
        }
        catch( IOException e )
        {
            System.out.println("Listen: " + e.getMessage());
        }
    }
}