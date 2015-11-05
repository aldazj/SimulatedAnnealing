package main;

import utils.City;
import utils.ReadFile;
import utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by aldazj on 12.10.15.
 */
public class SimulatedAnnealing {

    private double[][] distances;
    private ArrayList<City> cities;
    private int N;
    LinkedHashMap<String, Integer> citiesNamed;
    ArrayList<City> path;
    double energy;

    public SimulatedAnnealing(ArrayList<City> cities, double[][] distances) {
        this.N = cities.size();
        this.distances = distances;
        this.cities = cities;
        citiesNamed = new LinkedHashMap<String, Integer>();
        Utilities.initHashtable(citiesNamed, cities);
    }

    private void simulatedAnnelingAlgorithm(double factorT){
        int nbTransformations = 100;
        ArrayList<City> initCities;
        double deltaEnergy = 0.0, temperatureInit;
        int nbAccepted = 0, nbPerturbation = 0;
        boolean frozen = false;
//        double factorReduceTemperature = 0.9;
        double factorReduceTemperature = factorT;
        double epsilonTemperature = Math.pow(10.0, -20);
        ArrayList<City> my_cities;

        do{
            //Solution initiale random
            my_cities = Utilities.randomInitSolution(cities);
            initCities = new ArrayList<City>(my_cities);
            double temperature_energy_x = Utilities.computeEnergy(my_cities, distances, citiesNamed);

            //Caldul de la température initiale
            for (int i = 0; i < nbTransformations; i++) {
                int[] indexPermute = Utilities.getIndexPermute(initCities);
                Utilities.permuteCities(initCities, indexPermute);
                double temperature_energy_new = Utilities.computeEnergy(initCities, distances, citiesNamed);
                deltaEnergy += (temperature_energy_new - temperature_energy_x);
                initCities = new ArrayList<City>(my_cities);
            }
            deltaEnergy = deltaEnergy/nbTransformations;
            temperatureInit = -deltaEnergy/Math.log(0.5);
        }while (temperatureInit < 0.0);

        initCities = my_cities;
        do{
            double energy_x = Utilities.computeEnergy(initCities, distances, citiesNamed);
            ArrayList<City> currentSolution = new ArrayList<City>(initCities);

            //Génération d'un voisin
            int[] indexPermute = Utilities.getIndexPermute(currentSolution);
            Utilities.permuteCities(currentSolution, indexPermute);
            double energy_xnew = Utilities.computeEnergy(currentSolution, distances, citiesNamed);

            //Calcule de la variation de temperature
            double deltaE = energy_xnew - energy_x;

            //Probabilité d'acceptation
            if (deltaE < 0.0) {
                initCities = currentSolution;
                nbAccepted += 1;
            }else{
                if(Math.random() < Math.exp((-deltaE/temperatureInit))){
                    initCities = currentSolution;
                    nbAccepted += 1;
                }else{
                    nbPerturbation += 1;
                }
            }

            //Etat d'équilibre
            if(nbAccepted > (12*N) || nbPerturbation > (100*N)){
                if(temperatureInit > epsilonTemperature){
                    //Temperature Reduce
                    temperatureInit = factorReduceTemperature*temperatureInit;
                    nbAccepted = 0;
                    nbPerturbation = 0;
                }else{
                    //Etat de gèle
                    frozen = true;
                }
            }
        }while(!frozen || nbPerturbation == 0);

        double temperature_energy_x = Utilities.computeEnergy(initCities, distances, citiesNamed);
        System.out.println("Energie total: "+temperature_energy_x);
        path = initCities;
        energy = temperature_energy_x;
    }

    public ArrayList<City> getPath() {
        return path;
    }

    public double getEnergy() {
        return energy;
    }

    public void main(double factorT){
        simulatedAnnelingAlgorithm(factorT);
    }

    public static void main(String[] args) {
//        String pathFiles = "src"+File.separator+"data"+ File.separator;
//        Utilities.generateRandomCityFile(pathFiles);

//---------------------------------------------------------------------------------
        //Facteur de réduction de la température pour l'algorithme Recuit simulé
//        double[] desc_temperature = {0.9, 0.7, 0.5, 0.3, 0.1};
        double[] desc_temperature = {0.9};

//---------------------------------------------------------------------------------
        //Choisir les fichier à exécuter
//        String[] filenames = {"cities.dat", "cities2.dat", "cities50.dat",
//                "cities60.dat", "cities80.dat", "cities100.dat"};
        String[] filenames = {"cities.dat"};
//---------------------------------------------------------------------------------

        for (int indexFile = 0; indexFile < filenames.length; indexFile++) {
            for (int t = 0; t < desc_temperature.length; t++) {
                int nbExecutions = 10;
                String filename = "src" + File.separator + "data" + File.separator + filenames[indexFile];
//---------------------------------------------------------------------------------
                //Choisir l'algorithe à exécuter
                String[] algo = {"GreedyAlgorithm", "SimulatedAnnealing"};
//                String[] algo = {"SimulatedAnnealing"};
//---------------------------------------------------------------------------------
                long[] tempsExec = new long[nbExecutions];
                long startTime, stopTime, meanTime = 0;
                System.out.println("###################  " + filenames[indexFile] + "  ###################");
                for (int k = 0; k < algo.length; k++) {
                    double moyenne = 0.0, variance = 0.0;
                    ArrayList<City> bestPath = null;
                    double tmpMoyenne = Double.MAX_VALUE, bestDistance = 0;
                    double[] fitness = new double[nbExecutions];
                    System.out.println("\t#########  " + algo[k] + " t_"+desc_temperature[t]+" #########");
                    for (int i = 0; i < nbExecutions; i++) {
                        ReadFile readFile = new ReadFile(filename);
                        double[][] distance = readFile.getMatrix_D();
                        ArrayList<City> cities = readFile.getCities();
                        startTime = System.currentTimeMillis();
                        if (algo[k].equals("GreedyAlgorithm")) {
                            GreedyAlgorithm greedy = new GreedyAlgorithm(cities, distance);
                            greedy.main();
                            if (greedy.getDistance() < tmpMoyenne) {
                                tmpMoyenne = greedy.getDistance();
                                bestPath = greedy.getPath();
                                bestDistance = greedy.getDistance();
                            }
                            fitness[i] = greedy.getDistance();
                            moyenne += greedy.getDistance();
                        } else {
                            SimulatedAnnealing SA = new SimulatedAnnealing(cities, distance);
                            SA.main(desc_temperature[t]);
                            if (SA.getEnergy() < tmpMoyenne) {
                                tmpMoyenne = SA.getEnergy();
                                bestPath = SA.getPath();
                                bestDistance = SA.getEnergy();
                            }
                            fitness[i] = SA.getEnergy();
                            moyenne += SA.getEnergy();
                        }
                        stopTime = System.currentTimeMillis();
                        tempsExec[i] = stopTime - startTime;
                    }
                    moyenne = moyenne / nbExecutions;
                    variance = Utilities.computeVariance(moyenne, fitness);
                    meanTime = Utilities.computeMean(tempsExec);

                    Utilities.printPath(bestPath);
                    if (algo[k].equals("GreedyAlgorithm")) {
                        System.out.println(" : " + bestDistance);
                        System.out.println("Distance Moyenne: " + moyenne);
                    } else {
                        System.out.println(" : " + bestDistance);
                        System.out.println("Energie Moyenne: " + moyenne);
                    }
                    System.out.println("Execution time mean : " + Utilities.elapsedToString(meanTime));
                    System.out.println("The standard deviation " + Math.sqrt(variance));
//---------------------------------------------------------------------------------
                    //Pour écrire nos résultats dans un fichier
//                    WriteDatFile writeFile = new WriteDatFile(filenames[indexFile] + "_" + algo[k]);
//                    writeFile.writeResults(bestPath);
                }
            }
        }
    }
}
