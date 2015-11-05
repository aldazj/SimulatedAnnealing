package main;

import utils.City;
import utils.Utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aldazj on 24.10.15.
 */
public class GreedyAlgorithm {

    private double[][] distances;
    private ArrayList<City> cities;
    private int N;
    LinkedHashMap<String, Integer> citiesNamed;
    ArrayList<City> path;
    double distance;

    public GreedyAlgorithm(ArrayList<City> cities, double[][] distances) {
        this.N = cities.size();
        this.distances = distances;
        this.cities = cities;
        citiesNamed = new LinkedHashMap<String, Integer>();
        Utilities.initHashtable(citiesNamed, cities);
    }

    public void main(){
        path = new ArrayList<City>();

        //Solution initiale random
        ArrayList<City> my_cities = Utilities.randomInitSolution(cities);
        City city = my_cities.get(0);

        //Déplacement vers la ville la plus proche
        for (int i = 0; i < N-1; i++) {
            city = Utilities.getNextCity(city, my_cities, distances, path, citiesNamed);
        }

        //Retour à la ville de départ
        Utilities.comeBack(city, my_cities.get(0), my_cities, distances, path, citiesNamed);

        //Calcul de la energy parcourue
        distance = Utilities.computeDistance(path);
        System.out.println("Distance: "+distance);
    }

    public ArrayList<City> getPath() {
        return path;
    }

    public double getDistance() {
        return distance;
    }
}
