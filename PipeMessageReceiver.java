/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 *
 * @author christopher
 */
public class PipeMessageReceiver extends Thread implements MessageSender<String>
{
    private String outputPipeName;
    private String id;
    private String message;
 
    public PipeMessageReceiver(String line) {
        String[] components = line.split(":",2);
        setup(components[0],components[1]);
    }
 
    public PipeMessageReceiver(String id, String message) {
        setup(id,message);
    }
 
    private void setup(String id, String message) {
        this.id = id;
        this.message = message;
        this.outputPipeName = "/tmp/"+id;
        this.start();
    }
 
    @Override
    public void run() {
        MessageQueue.queue.add(new MessageContainer<String>(this,message));
    }
 
    @Override
    public void sendMessage(String str) {
        try {
            PrintWriter printWriter=new PrintWriter(new BufferedOutputStream(new FileOutputStream(outputPipeName)));
            printWriter.println(str);
            printWriter.flush();
            printWriter.close();
        } catch (Exception e) {
            System.out.println("Cannot write to " + outputPipeName +".");
        }
    }
}
