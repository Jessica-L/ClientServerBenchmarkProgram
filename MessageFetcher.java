/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclientserver;

/**
 *
 * @author Jessica
 */
public class MessageFetcher extends Thread
{

	public MessageFetcher() {
		this.start();
	}
	
	public void run() {
		while (true) {
			MessageContainer<String> container = MessageQueue.queue.poll();
			if (container != null) {
				System.out.println("jfjdksl");
				new MessageProcessor<String>(new Mapper<String>(new NothingMapping()),container).process();
			} else {
				try {
					Thread.sleep((long)(Math.random() % 1000));
				} catch (Exception e) {
					System.out.println("Thread cannot go to sleep. Can you read it a bed time story?");
				}
				
			}
		}
	}
}