package main.parallelTempering;

import utils.City;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by aldazj on 25.10.15.
 */
public class RecieveMessage extends Thread{

    private BlockingQueue<Message> queue;   //Queue d'attente pour les messages reçus
    private Parallel_Tempering replica;     //Notre replique

    public RecieveMessage(Parallel_Tempering replica, BlockingQueue<Message> q){
        this.queue=q;
        this.replica = replica;
    }

    /**
     * Lorsque nous recevons la sequence de villes de notre voisin,
     * nous remplaçons notre sequence actuelle par la dernière reçue
     */
    @Override
    public void run() {
        try{
            Message msg;
            ArrayList<City> cities;
            while((msg = queue.take()) != null){
                cities = msg.getCities();
                replica.setCities(cities);
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}