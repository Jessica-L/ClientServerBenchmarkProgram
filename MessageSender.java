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
public interface MessageSender<D> {
    public void sendMessage(D data);
}