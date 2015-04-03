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
