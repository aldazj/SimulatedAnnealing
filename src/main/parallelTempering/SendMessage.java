package main.parallelTempering;

import java.util.concurrent.BlockingQueue;

/**
 * Created by aldazj on 25.10.15.
 */
public class SendMessage extends Thread{

    private BlockingQueue<Message> queue;       //Queue où le messages seront envoyés

    public SendMessage(BlockingQueue<Message> q){
        this.queue=q;
    }

    @Override
    public void run() {

    }

    /**
     * Nous envoyons un message à notre voisin
      * @param msg
     */
    public void send(Message msg){
        try {
            Message my_msg = new Message(msg.getCities());
            queue.put(my_msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
