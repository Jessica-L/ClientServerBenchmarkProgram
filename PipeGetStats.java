/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author christopher
 */
public class PipeGetStats
{
   private static void printFinalReport( int numClients, int numEvents )
   {
	System.out.println( "Statistics for " + numClients + " Clients and " + numEvents + " messages." );
        try
	{
	    FileInputStream fis = new FileInputStream( "/tmp/PipeDataFile" );
	    DataInputStream dis = new DataInputStream( fis );
	    /* This total time, t, is the total time of execution for everything.
	     * Divide t by the number of clients to determine the average time each client took to execute.
	     * Divide the average time each client took to execute by the number of messages sent to find
	     * the average time to send one message round trip.  Divide this final average by 2 to get the
	     * average time to transfer one message.
	     */
	    long            t   = dis.readLong();

	    System.out.println( "Total Pipe Client to Server to Client run time is "
		              + t + " nanoseconds = " + t / 1000000 + " milliseconds." );
	    long avgProc = t / numClients;
	    System.out.println( "Average time per process = " + avgProc + " nanoseconds" );
	    long avgRoundTrip = avgProc / numEvents;
	    System.out.println( "Average time per message (round trip) = " + avgRoundTrip + " nanoseconds" );
	    System.out.println( "Average time per message (one way) = " + avgRoundTrip / 2 + " nanoseconds" );
	}
	catch( FileNotFoundException ex )
	{
            System.out.println( "Failed to open PipeDataFile: " + ex.getMessage() );
	}
	catch( IOException ex )
	{
	    System.out.println( "Failed to read PipeDataFile: " + ex.getMessage() );
	}
    }

   public static void main(String[] args)
   {
      printFinalReport( Integer.parseInt( args[0] ), Integer.parseInt( args[1] ) );
   }
}
