package main.parallelTempering;

import utils.City;
import utils.ReadFile;
import utils.Utilities;
import utils.WriteDatFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by aldazj on 25.10.15.
 */
public class Parallel_Tempering extends Thread{

    private RecieveMessage receiveMsg;                          //Thread de lecture de messages
    private SendMessage sendMsg;                                //Thread d'écriture de messages
    private double temperatureInit;                             //Temperature initiale
    private double[][] distances;                               //Matrice de distance entre les villes
    private ArrayList<City> cities;                             //nos villes
    private int iteration_max, indexNeighbord, indexThread;     //variables
    LinkedHashMap<String, Integer> citiesNamed;                 //Correspondance entre nom de ville et indice
    private ArrayList<Parallel_Tempering> arrayParallelT;       //List de tous les thread existants
    private double energy_x = 0.0, energy_xnew = 0.0;			//Energies
    private double b = 0.0;					                    //Inverse de la temperature
    ArrayList<City> initCities;                                 //Solution courante
    private boolean finish = false;

    public Parallel_Tempering(int indexThread, int indexNeighbord,
                              BlockingQueue<Message> bufferToRead, BlockingQueue<Message> bufferToWrite,
                              double temperatureInit, ArrayList<City> cities, double[][] distances,
                              int itermax) {
        this.indexThread = indexThread;
        this.indexNeighbord = indexNeighbord;
        this.temperatureInit = temperatureInit;
        this.b = 1/(temperatureInit);
        this.distances = distances;
        this.cities = cities;
        this.iteration_max = itermax;
        citiesNamed = new LinkedHashMap<String, Integer>();
        Utilities.initHashtable(citiesNamed, cities);
        receiveMsg = new RecieveMessage(this, bufferToRead);
        sendMsg = new SendMessage(bufferToWrite);
    }

    @Override
    public void run() {
        receiveMsg.start();
        sendMsg.start();
        for (int i = 0; i < iteration_max; i++) {
            initCities = new ArrayList<City>(cities);
            int nbAccepted = 0, nbPerturbation = 0;
            boolean equilibre = false;
            do {
                energy_x = Utilities.computeEnergy(initCities, distances, citiesNamed);
                ArrayList<City> currentSolution = new ArrayList<City>(initCities);

                //Génération d'un voisin
                int[] indexPermute = Utilities.getIndexPermute(currentSolution);
                Utilities.permuteCities(currentSolution, indexPermute);
                energy_xnew = Utilities.computeEnergy(currentSolution, distances, citiesNamed);

                //Calcule de la variation de temperature
                double deltaE = energy_xnew - energy_x;

                //Probabilité d'acceptation
                if (deltaE < 0.0) {
                    initCities = currentSolution;
                    nbAccepted += 1;
                } else {
                    if (Math.random() < Math.exp((-deltaE / temperatureInit))) {
                        initCities = currentSolution;
                        nbAccepted += 1;
                    } else {
                        nbPerturbation += 1;
                    }
                }
                //Etat d'équilibre
                if (nbAccepted > (12 * cities.size()) || nbPerturbation > (100 * cities.size())) {
                    equilibre = true;
                }
            } while (!equilibre);

            //Récupération des paramètres qui influencent un changement de donnée
            double Ei, Ej, Bi, Bj;
            Parallel_Tempering neighbor = arrayParallelT.get(indexNeighbord);
            Ei = energy_x;
            Ej = neighbor.getEnergy_x();
            Bi = b;
            Bj = neighbor.getB();

            //Changement accepté si on respecte la probabilité suivante
            if (Math.random() < Math.min(1, Math.exp((Ei - Ej) * (Bi - Bj)))) {
                sendMsg.send(new Message(initCities));
            }
        }

        //Fin de l'algo
        System.out.print("Thread: ");
        energy_x = Utilities.computeEnergy(initCities, distances, citiesNamed);
        System.out.println(" Energie total: "+energy_x);
        finish = true;
    }

    public ArrayList<City> getInitCities() {
        return initCities;
    }

    public boolean isFinish() {
        return finish;
    }
    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }

    public double getEnergy_x() {
        return energy_x;
    }

    public double getB() {
        return b;
    }

    public void setArrayParallelT(ArrayList<Parallel_Tempering> arrayParallelT) {
        this.arrayParallelT = arrayParallelT;
    }

    public static void main(String[] args) {
        ArrayList<Parallel_Tempering> myReplicas = new ArrayList<Parallel_Tempering>();

        //Lecture du fichier de donnée
        String[] filenames = {"cities.dat"};
        String filename = "src" + File.separator + "data" + File.separator + filenames[0];
        ReadFile readFile = new ReadFile(filename);
        double[][] distances = readFile.getMatrix_D();
        ArrayList<City> cities = readFile.getCities();
        int nbReplica = (int)Math.sqrt(cities.size());
        double t0 = Math.pow(10.0, -30);
        LinkedHashMap<String, Integer> citiesNamed = new LinkedHashMap<String, Integer>();
        Utilities.initHashtable(citiesNamed, cities);

        //Calcule de la température initiale plus grande
        int nbTransformations = 100, iterMax = 20;
        ArrayList<City> initCities;
        double deltaEnergy = 0.0, temperatureInit;
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

        //Distribution de l'énergie
        double step = (temperatureInit-t0)/(nbReplica-1);
        double energies[] = new double[nbReplica];
        for (int k = 0; k < nbReplica; k++) {
            energies[k] = t0 + (k * step);
        }

        //Initialisation de chaque thread
        for (int i = 0; i < nbReplica-1; i++) {
            //Buffers pour la communication
            BlockingQueue<Message> bufferToRead12 = new ArrayBlockingQueue<Message>(10);
            BlockingQueue<Message> bufferToWrite12 = new ArrayBlockingQueue<Message>(10);
            //Initialisation de nos threads
            Parallel_Tempering t1 = new Parallel_Tempering(i, (i+1),
                    bufferToRead12, bufferToWrite12, energies[i], cities, distances, iterMax);
            Parallel_Tempering t2 = new Parallel_Tempering((i+1), i,
                    bufferToWrite12,bufferToRead12, energies[i+1], cities, distances, iterMax);
            myReplicas.add(t1);
            myReplicas.add(t2);
        }

        //Chaque thread reçoit la liste de threads existants
        for (int i = 0; i < myReplicas.size(); i++) {
            myReplicas.get(i).setArrayParallelT(myReplicas);
        }

        //Demarrage des threads
        for (int j = 0; j < myReplicas.size(); j++) {
            myReplicas.get(j).start();
        }

        boolean terminated = false;
        int nbOk = 0;
        ArrayList<City> bestPath = null;
        double tmpEnergie = Double.MAX_VALUE;

        while (!terminated){
            try {
                for (int i = 0; i < myReplicas.size(); i++) {
                    if(myReplicas.get(i).isFinish()){
                        nbOk += 1;
                        if(myReplicas.get(i).getEnergy_x() < tmpEnergie){
                            tmpEnergie = myReplicas.get(i).getEnergy_x();
                            bestPath = myReplicas.get(i).getInitCities();
                        }
                        myReplicas.get(i).join();
                    }
                    Thread.sleep(100);
                    if(nbOk == nbReplica){
                        terminated = true;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.print("-------------------------");
        Utilities.printCities(bestPath);
        System.out.println("Meilleure energie: " + tmpEnergie);

        //Pour écrire nos résultats dans un fichier
        WriteDatFile writeFile = new WriteDatFile("Parallel_cities.dat");
        writeFile.writeResults(bestPath);
    }
}