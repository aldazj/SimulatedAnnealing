package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aldazj on 24.10.15.
 */
public class WriteDatFile {
    private String filename;
    private BufferedWriter bw;
    private String folder = "src/results";

    public WriteDatFile(String filename) {
        this.filename = folder+"/"+filename+".dat";
    }

    /**
     * Write the results in a file .dat
     * @param path
     */
    public void writeResults(ArrayList<City> path){
        try {
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < path.size(); i++) {
                bw.write(path.get(i).getCityName()+" ");
                bw.write(path.get(i).getPosX()+" ");
                bw.write(path.get(i).getPosY()+" ");
                bw.write("\n");
            }
            bw.write(path.get(0).getCityName()+" ");
            bw.write(path.get(0).getPosX()+" ");
            bw.write(path.get(0).getPosY()+" ");
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
