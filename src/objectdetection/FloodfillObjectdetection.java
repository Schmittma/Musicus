package objectdetection;

import general.Objektausschnitt;
import general.Point;
import interfaces.ObjectFinder;
import utils.Util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class FloodfillObjectdetection implements ObjectFinder {

    private int fd;

    public FloodfillObjectdetection(int fd) {
        super();
        this.fd = fd;
    }

    @Override
    public ArrayList<Objektausschnitt> findObjects(boolean[][] image) {

        ArrayList<Objektausschnitt> objects = new ArrayList<>();

        //Copy the array
        boolean[][] map = Util.copyArray(image);

        //False means either, that the pixel is white (And thus cannot be an object)
        // or it means, that we allready visited it.
        //In both cases we need to ignore it

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == true) {
                    Objektausschnitt ausschnitt = new Objektausschnitt();
                    floodFillIterative(x, y, map, ausschnitt);

                    objects.add(ausschnitt);
                }
            }
        }
        return objects;
    }

    //Searches for coordinates connected to this object
    private void floodFill(int x, int y, boolean[][] map, Objektausschnitt object) {
        //Check bounds
        if (x < 0 || y < 0 || x >= map.length || y >= map[x].length) {
            return;
        }

        //Check if we allready visited or if the pixel is white
        if (map[x][y] == false) {
            return;
        }

        object.addCoordinate(new Point(x, y));
        map[x][y] = false;

        for (int i = -1 * fd; i <= fd; i++) {
            for (int j = -1 * fd; j <= fd; j++) {
                if (!(i == 0 && j == 0)) {
                    floodFill(x + i, y + j, map, object);
                }
            }
        }
    }

    private void floodFillIterative(int xStart, int yStart, boolean[][] map, Objektausschnitt object) {

        Queue<SimpleEntry<Integer, Integer>> queue = new LinkedList<>();

        queue.add(new SimpleEntry<Integer, Integer>(xStart, yStart));
        map[xStart][yStart] = false;

        while (!queue.isEmpty()) {
            SimpleEntry<Integer, Integer> coord = queue.remove();

            int x = coord.getKey();
            int y = coord.getValue();

            object.addCoordinate(new Point(x, y));

            for (int i = -1 * fd; i <= fd; i++) {
                for (int j = -1 * fd; j <= fd; j++) {
                    if (!(i == 0 && j == 0) && isValid(x + i, y + j, map)) {
                        queue.add(new SimpleEntry<Integer, Integer>(x + i, y + j));
                        map[x + i][y + j] = false;
                    }
                }
            }

        }
    }

    private boolean isValid(int x, int y, boolean[][] map) {
        if (x < 0 || y < 0 || x >= map.length || y >= map[x].length) {
            return false;
        }

        //Check if we allready visited or if the pixel is white
        if (map[x][y] == false) {
            return false;
        }

        return true;
    }
}
