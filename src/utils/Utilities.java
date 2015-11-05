package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by aldazj on 19.10.15.
 */
public class Utilities {

    /**
     * Calcul de la distance d'euclidienne
     * @param nextCityPosX : prochaine position x
     * @param nextCityPosY : prochaine position y
     * @param localPosX : position local x
     * @param localPosY : position local y
     * @return : distace de ha
     */
    public static double dEuclidian(double nextCityPosX, double nextCityPosY, double localPosX, double localPosY){
        return Math.sqrt(Math.pow(nextCityPosX-localPosX, 2)+Math.pow(nextCityPosY-localPosY, 2));
    }

    /**
     * Initialisation une matrice tout à zero
     * @param matrix
     */
    public static void init_matrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = 0.0;
            }
        }
    }

    /**
     * Affiche les villes
     * @param cities
     */
    public static void printCities(ArrayList<City> cities){
        System.out.println("############  Cities  ############");
        for (int i = 0; i < cities.size(); i++) {
            System.out.print(cities.get(i).getCityName() + " ");
        }
        System.out.println("");
    }

    /**
     * Affiche la matrice de distances
     * @param distances
     */
    public static void printDistances(double[][] distances){
        System.out.println("############  Distances  ############");
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[0].length; j++) {
                System.out.print(distances[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    /**
     * Crée une solution random aléatoire
     * @param cities
     * @return
     */
    public static ArrayList<City> randomInitSolution(ArrayList<City> cities){
        ArrayList<City> init_sol = new ArrayList<City>(cities);
        Collections.shuffle(init_sol);
        return  init_sol;
    }

    /**
     * Récupère une ville
     * @param city : nom de la ville
     * @param my_cities : ensemble de villes
     * @return : une ville
     */
    public static City getCity(String city, ArrayList<City> my_cities){
        for (int i = 0; i < my_cities.size(); i++) {
            if(city.equals(my_cities.get(i).getCityName())){
                return  my_cities.get(i);
            }
        }
        return null;
    }

    /**
     * Met à jour si une ville est visitée
     * @param currentCity : ville courante
     * @param my_cities : ensemble de villes
     */
    public static void update_CityVisited(City currentCity, ArrayList<City> my_cities){
        for (int i = 0; i < my_cities.size(); i++) {
            if(currentCity.getCityName().equals(my_cities.get(i).getCityName())){
                my_cities.get(i).setVisited(true);
            }
        }
    }

    /**
     * Affiche le chemin parcouru
     * @param path : chemin parcouru
     */
    public static void printPath(ArrayList<City> path){
        System.out.print("[");
        for (int i = 0; i < path.size(); i++) {
//            System.out.print("("+path.get(i).getCityName() + " -> " + path.get(i).getNextCity()+") ");
            System.out.print(path.get(i).getCityName()+"->" + path.get(i).getNextCity()+";");
            if(i < path.size()-1){
                System.out.print(",");
            }
        }
        System.out.print("]");
    }

    /**
     * Calcule de la distance
     * @param path
     * @return
     */
    public static double computeDistance(ArrayList<City> path){
        double distance = 0.0;
        for (int i = 0; i < path.size(); i++) {
            distance += path.get(i).getDistanceNextCity();
        }
        return distance;
    }

    /**
     * Retourn vers la ville de départ
     * @param currentCity : ville courante
     * @param nextCity : prochaine ville
     * @param cities : ensemble de villes
     * @param distances : distances entre les villes
     * @param path : chemin actuel parcouru
     * @param citiesNamed : correspondance entre le nom d'une ville et sa position
     */
    public static void comeBack(City currentCity, City nextCity, ArrayList<City> cities,
                                double[][] distances, ArrayList<City> path,
                                LinkedHashMap<String, Integer> citiesNamed){

        int index_DistSource = citiesNamed.get(currentCity.getCityName());
        int index_DistDest = citiesNamed.get(nextCity.getCityName());

        double tmpDistance = distances[index_DistSource][index_DistDest];
        currentCity.setDistanceNextCity(tmpDistance);
        currentCity.setNextCity(nextCity.getCityName());
        path.add(currentCity);
        update_CityVisited(currentCity, cities);
    }

    /**
     * Trouve la ville plus proche
     * @param currentCity : ville courante
     * @param cities : ensemble de villes
     * @param distances : matrice de distances
     * @param path : les chemins actuels parcourus
     * @param citiesNamed : correspondance entre le nom d'une ville et sa position
     * @return
     */
    public static City getNextCity(City currentCity, ArrayList<City> cities,
                                   double[][] distances, ArrayList<City> path,
                                   LinkedHashMap<String, Integer> citiesNamed) {

        int index_DistSource = citiesNamed.get(currentCity.getCityName());
        double tmpDistance = Double.MAX_VALUE;
        City nextCity = null;
        for (int index_DistDest = 0; index_DistDest < distances[index_DistSource].length; index_DistDest++) {
            if (index_DistDest != index_DistSource) {
                if (distances[index_DistSource][index_DistDest] < tmpDistance) {

                    City city = getCity(getHashNameCity(index_DistDest, citiesNamed), cities);
                    if (!city.isVisited()) {
                        tmpDistance = distances[index_DistSource][index_DistDest];
                        nextCity = city;
                    }
                }
            }
        }
        currentCity.setDistanceNextCity(tmpDistance);
        currentCity.setNextCity(nextCity.getCityName());
        path.add(currentCity);
        update_CityVisited(currentCity, cities);
        return nextCity;
    }

    /**
     * Retourne le nom d'un ville à partir d'un indice
     * @param indexCity : indice d'une ville
     * @param citiesNamed : correspondance entre le nom d'une ville et sa position
     * @return
     */
    public static String getHashNameCity(int indexCity, LinkedHashMap<String, Integer> citiesNamed){
        for (Map.Entry<String, Integer> entry : citiesNamed.entrySet()) {
            if(entry.getValue() == indexCity){
                return entry.getKey();
            }
        }
        return "";
    }

    /**
     * Calcul l'énergie
     * @param cities : villes
     * @param distances : matrice de distances
     * @param citiesNamed : correspondance entre le nom d'une ville et sa position
     * @return
     */
    public static double computeEnergy(ArrayList<City> cities, double[][] distances, LinkedHashMap<String, Integer> citiesNamed){
        double energy = 0.0;
        int indexSource, indexDest;

        for(int i =0; i < cities.size()+1; i++){
            indexSource = citiesNamed.get(cities.get(i % cities.size()).getCityName());
            indexDest = citiesNamed.get(cities.get((i + 1) % cities.size()).getCityName());
            energy += distances[indexSource][indexDest];
        }
        return energy;
    }

    /**
     * Génère une valeur aléatoire entre zéro et max
     * @param max
     * @return
     */
    public static int randomIndex(int max){
        return 0 + (int)(Math.random() * ((max - 0) + 1));
    }

    /**
     * Génère deux indices de deux villes à permuter
     * @param cities
     * @return
     */
    public static int[] getIndexPermute(ArrayList<City> cities){
        int[] indexPermute = new int[2];
        int cityLenght = cities.size()-1;
        int index_i = randomIndex(cityLenght);
        int index_j;
        do{
            index_j = randomIndex(cityLenght);
        }while (index_i == index_j);
        indexPermute[0] = index_i;
        indexPermute[1] = index_j;
        return  indexPermute;
    }

    /**
     * Permute deux villes
     * @param cities : villes existantes
     * @param index : indices des objets à permuter
     */
    public static void permuteCities(ArrayList<City> cities, int[] index){
        int indexSource = index[0], indexDest = index[1];
        City cityTmp = cities.get(indexSource);
        cities.set(indexSource, cities.get(indexDest));
        cities.set(indexDest, cityTmp);
    }

    /**
     * Crée une correspondance entre les nom de villes et des indice
     * @param cityNamed
     * @param cities
     */
    public static void initHashtable(LinkedHashMap<String, Integer> cityNamed, ArrayList<City> cities){
        for (int i = 0; i < cities.size(); i++) {
            cityNamed.put(cities.get(i).getCityName(), i);
        }
    }

    /**
     * Génère un fichier de villes
     * @param filename
     * @param size
     */
    public static void generateFile(String filename, int size){
        try {
            String c = "a";
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            for (int i = 0; i < size; i++) {
                bw.write(c+i+" "+String.valueOf(randomIndex(size))+" "+String.valueOf(randomIndex(size)));
                bw.write('\n');
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Genère une matrice aléatoire et l'écrit dans un fichier
     * @param pathFiles
     */
    public static void generateRandomCityFile(String pathFiles){
        int[] dimension = {50, 60, 80, 100};
        for (int i = 0; i < dimension.length; i++) {
            Utilities.generateFile(pathFiles+"cities"+dimension[i]+".dat", dimension[i]);
        }
    }

    /**
     * Affiche un elapsedTimeMillis timer sous la forme hh:mm:ss
     * @param elapsedTimeMillis
     * @return
     */
    public static String elapsedToString(long elapsedTimeMillis) {
        long seconds = (elapsedTimeMillis + 500) / 1000; // round
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%1$02d:%2$02d:%3$02d",
                hours,
                minutes % 60,
                seconds % 60);
    }

    /***
     * Calcule la variance
     * @param timesExec : time to exec
     * @return
     */
    public static long computeMean(long[] timesExec){
        long mean = 0;
        for (int i = 0; i < timesExec.length; i++) {
            mean += timesExec[i];
        }
        return  mean/timesExec.length;
    }

    /**
     * Calcule la variance
     * @param mean : moyenne
     * @param fitness : fitness
     * @return : valeur de la variance
     */
    public static double computeVariance(double mean, double[] fitness){
        double var = 0.0;
        for (int i = 0; i < fitness.length; i++) {
            var += Math.pow(fitness[i]-mean, 2);
        }
        return (var/(fitness.length));
    }
}
