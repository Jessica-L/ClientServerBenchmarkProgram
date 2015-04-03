/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue
{

	public static ConcurrentLinkedQueue<MessageContainer<String>> queue = new ConcurrentLinkedQueue<MessageContainer<String>>();

}

