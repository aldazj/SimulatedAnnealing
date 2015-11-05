package utils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by aldazj on 19.10.15.
 */
public class ReadFile {

    private BufferedReader br;
    private File file;
    private String path_filename;
    private double[][] matrix_D;
    private ArrayList<City> cities;

    public ReadFile(String path_filename) {
        this.path_filename = path_filename;
        read_data();
    }

    /**
     * Lecture d'un fichier
     */
    public void read_data(){
        file = new File(path_filename);
        ArrayList<String[]> data = new ArrayList<String[]>();
        String line = "";
        try {
            br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                data.add(line.replaceAll(" +", " ").split(" "));
            }
            br.close();
            build_matrix_d(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construction d'un matrice de distances entre les villes
     * @param data
     */
    private void build_matrix_d(ArrayList<String[]> data){
        matrix_D = new double[data.size()][data.size()];
        cities = new ArrayList<City>();
        Utilities.init_matrix(matrix_D);
        for (int i = 0; i < data.size(); i++) {
            cities.add(new City(data.get(i)[0], data.get(i)[1], data.get(i)[2]));
            for (int j = 0; j < data.size(); j++) {
                if(i != j){
                    double posX = Double.parseDouble(data.get(i)[1]);
                    double posY = Double.parseDouble(data.get(i)[2]);
                    double nextCityPosX = Double.parseDouble(data.get(j)[1]);
                    double nextCityPosY = Double.parseDouble(data.get(j)[2]);
                    double distance = Utilities.dEuclidian(nextCityPosX, nextCityPosY, posX, posY);
                    matrix_D[i][j] = distance;
                }
            }
        }
    }

    public double[][] getMatrix_D() {
        return matrix_D;
    }

    public ArrayList<City> getCities() {
        return cities;
    }
}