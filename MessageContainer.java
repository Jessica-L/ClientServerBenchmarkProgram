/*
 * Project: Client Server Performance Measurement Program
 * Authors: Jessica Lynch and Andrew Arnopoulos
 * Date:    27-Apr-2015
 */
package clientserver;

class MessageContainer<D>
{
    private MessageSender<D> sender = null;
    private D data = null;

    public MessageContainer(MessageSender<D> sender,D data) {
            this.sender = sender;
            this.data = data;
    }

    public MessageSender<D> getSender() {
            return sender;
    }

    public D getData() {
            return data;
    }
}
