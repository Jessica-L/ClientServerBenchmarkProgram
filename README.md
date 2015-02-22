# TCP Client Server Program
Client Server program that implements TCP with performance measurements.  
  
  This program is in progress:  
    The Message processor program needs to process each message received.  That is, it must  
    make some kid of change to the data received from MessageReceiver within a locked  
    critical section.  The ciritical section will be a queue where data received is  
    added to the queue (this must be locked) and queued data is pulled off to be processed (this  
    also must be locked).  The data will be queued in FCFS order.  
    Things to do to make program concurrent:  
     * The queue class still needs to be created.  
     * Message Processor class needs to be implemented after queue class created.  
     * MessageReceiver needs to add data to the queue which needs to be locked.  
     * All other classes need modified as needed after the above changes are made.  
  


