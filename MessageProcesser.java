/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
//package tcpclientserver; // package for Netbeans project

/**
 * MessageProcessor program starts each thread to process a message.
 */
public class MessageProcesser
{
    /* This program needs to process each message received.  That is, it needs to
     * do something to the data received from MessageReceiver within a locked  
     * critical section.  This data needs therefore to be organized in a queue in
     * FCFS order... this is the locked cirtical section. Adding things to and
     * pulling things off the queue need to be locked.  
     * Things to do: 
     * (1) The queue class still needs to be created.
     * (2) This class Message Processor needs to be implemented after queue class
     * created.
     * (3) MessageReceiver needs to add data to the queue which needs to be locked.
    */
    
}
