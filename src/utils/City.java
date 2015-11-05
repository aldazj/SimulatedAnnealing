package utils;

/**
 * Created by aldazj on 19.10.15.
 */
public class City {

    private String cityName, posX, posY;    //Nom de la ville, position x et y
    private boolean visited;                //Pour vérifier si un ville est visitée
    private double distanceNextCity;        //Distance pour la ville suivante
    private String nextCity;                //La prochaine ville

    public City(String cityName, String posX, String posY) {
        this.cityName = cityName;
        this.posX = posX;
        this.posY = posY;
        this.visited = false;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public double getDistanceNextCity() {
        return distanceNextCity;
    }

    public void setDistanceNextCity(double distanceNextCity) {
        this.distanceNextCity = distanceNextCity;
    }

    public String getNextCity() {
        return nextCity;
    }

    public void setNextCity(String nextCity) {
        this.nextCity = nextCity;
    }

    public String getPosX() {
        return posX;
    }

    public String getPosY() {
        return posY;
    }
}
