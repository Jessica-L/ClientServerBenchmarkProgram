/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */

/**
 * MessageProcessor program starts each thread to process a message.
 */
public class MessageProcessor<D>
{
    private Mapper<D> mapper = null;
    private MessageContainer<D> container = null;

    public MessageProcessor(Mapper<D> mapper, MessageContainer<D> container) {
        this.mapper = mapper;
        this.container = container;
    }

    public void process() {
        D mapping = mapper.apply(container.getData());
        MessageSender<D> sender = container.getSender();
        sender.sendMessage(mapping);
    }
}
