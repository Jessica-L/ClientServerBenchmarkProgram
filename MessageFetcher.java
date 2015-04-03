/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

public class MessageFetcher extends Thread
{
    public MessageFetcher() {
            this.start();
    }

    public void run() {
        while (true)
        {
            MessageContainer<String> container = MessageQueue.queue.poll();
            if (container != null) {
                    System.out.println("jfjdksl");
                    new MessageProcessor<String>(new Mapper<String>(new NothingMapping()),container).process();
            } 
            else
            {
                try
                {
                    Thread.sleep((long)(Math.random() % 1000));
                }
                catch (Exception e)
                {
                    System.out.println("Thread cannot go to sleep. Can you read it a bed time story?");
                }

            }
        }
    }
}
