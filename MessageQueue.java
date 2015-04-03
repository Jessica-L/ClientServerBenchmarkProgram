/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclientserver;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Jessica
 */
public class MessageQueue {

	public static ConcurrentLinkedQueue<MessageContainer<String>> queue = new ConcurrentLinkedQueue<MessageContainer<String>>();

}
