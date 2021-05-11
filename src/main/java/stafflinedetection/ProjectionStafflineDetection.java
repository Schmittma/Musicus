package main.java.stafflinedetection;

import main.java.general.Point;
import main.java.general.Staffline;
import main.java.interfaces.StafflineDetection;

import java.util.ArrayList;

public class ProjectionStafflineDetection implements StafflineDetection {

    double threshold;

    public ProjectionStafflineDetection(double threshold) {
        super();
        this.threshold = threshold;
    }

    @Override
    public ArrayList<Staffline> detectStafflines(boolean[][] system) {
        ArrayList<Staffline> stafflines = new ArrayList<>();

        int[] horizontalProjection = new int[system[0].length];

        for (int x = 0; x < system.length; x++) {
            for (int y = 0; y < system[x].length; y++) {
                if (system[x][y]) {
                    horizontalProjection[y]++;
                }
            }
        }

        int count = 0;
        for (int i = 0; i < horizontalProjection.length; i++) {
            if (horizontalProjection[i] >= system.length * threshold) {
                //If this line is part of an existing staffline
                if (i != 0 && (horizontalProjection[i - 1] >= system.length * threshold)) {
                    for (int x = 0; x < system.length; x++) {
                        stafflines
                            .get(count - 1)
                            .addPoint(new Point(x, i));
                    }
                    stafflines
                        .get(count - 1)
                        .setWidth(stafflines
                                      .get(count - 1)
                                      .getWidth() + 1);
                } else {
                    stafflines.set(count, new Staffline());
                    for (int x = 0; x < system.length; x++) {
                        stafflines
                            .get(count)
                            .addPoint(new Point(x, i));
                    }
                    count++;
                }
            }
        }

        return stafflines;
    }

}
