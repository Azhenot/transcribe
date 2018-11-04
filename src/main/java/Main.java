import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tristan on 23-04-18.
 */
public class Main {

    public static void main(String[] args) {




    }

    List<List<Integer>> nearestXsteakHouses(int totalSteakhouses,
                                            List<List<Integer>> allLocations,
                                            int numSteakhouses)
    {
        List<List> all = new ArrayList<List>(allLocations);

        List<RestaurantDistance> restaurants = new ArrayList<RestaurantDistance>();

        int cpt = 0;
        while(cpt < all.size()){
            List<Integer> temp = new ArrayList<Integer>(all.get(cpt));
            restaurants.add(new RestaurantDistance(temp.get(0),temp.get(1)));
            ++cpt;
        }

        Collections.sort(restaurants);
        List<List<Integer>> solutions = new ArrayList<List<Integer>>();
        cpt = 0;
        while(cpt < numSteakhouses){
            List<Integer> restaurant = new ArrayList<Integer>();
            restaurant.add(restaurants.get(cpt).x);
            restaurant.add(restaurants.get(cpt).y);
            solutions.add(restaurant);
        }

        return solutions;

    }
    private class RestaurantDistance implements Comparable<RestaurantDistance> {

        public Integer x;
        public Integer y;
        public double distance;

        RestaurantDistance(Integer x, Integer y){
            this.x = x;
            this.y = y;
            this.distance = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        }

        @Override
        public int compareTo(RestaurantDistance other) {
            if(other.distance > this.distance){
                return 1;
            }else if(other.distance < this.distance){
                return -1;
            }else{
                return 0;
            }
        }
    }

    /*public class SimpleEx extends JFrame {

        public SimpleEx() {

            initUI();
        }

        private void initUI() {
-
            setTitle("Simple example");
            setSize(300, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }*/

}
