import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class MessageQueue {

	public static ConcurrentLinkedQueue<MessageContainer<String>> queue = new ConcurrentLinkedQueue<MessageContainer<String>>();

}