package main.parallelTempering;

import utils.City;

import java.util.ArrayList;

/**
 * Created by aldazj on 25.10.15.
 */
public class Message {
    private ArrayList<City> cities;

    public Message(ArrayList<City> cities){
        this.cities=cities;
    }

    public ArrayList<City> getCities() {
        return cities;
    }
}