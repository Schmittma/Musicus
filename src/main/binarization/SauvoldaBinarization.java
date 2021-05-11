package main.binarization;

import main.general.Color;
import main.general.Color.Grayscale;
import main.interfaces.Binarization;

public class SauvoldaBinarization implements Binarization {

    private int window_size;
    private double weight;
    private double range;

    public SauvoldaBinarization(int window_size, double weight, double range) {
        this.window_size = window_size;
        if (window_size % 2 == 0) {    //Ensure that the window size is odd
            window_size += 1;
        }
        this.weight = weight;
        this.range = range;
    }

    @Override
    public boolean[][] binarize(Color[][] imageRGB) {

        boolean[][] binaryImage = new boolean[imageRGB.length][imageRGB[0].length];

        for (int x = 0; x < imageRGB.length; x++) {
            for (int y = 0; y < imageRGB[x].length; y++) {
                double m = calculateMeanInWindow(imageRGB, x, y, window_size);
                double s = calculateStandardDeviationInWindow(imageRGB, x, y, window_size, m);

                int threshold = (int) (m * (1 + weight * ((s / range) - 1)));
                binaryImage[x][y] = imageRGB[x][y].getGrayscale(Grayscale.AVERAGE) < threshold;
            }
        }

        return binaryImage;
    }

    private double calculateMeanInWindow(Color[][] pixels, int centerX, int centerY, int w) {
        double mean = 0;
        int numOfPixels = w * w;

        for (int y = centerY - (w / 2); y <= centerY + (w / 2); y++) {
            if (y < 0 || y >= pixels.length) {
                numOfPixels -= w;
                continue;
            }

            for (int x = centerX - (w / 2); x <= centerX + (w / 2); x++) {
                if (x < 0 || x >= pixels[0].length) {
                    numOfPixels -= 1; //One pixel less in the mean calcualation
                    continue;
                }

                mean += pixels[y][x].getGrayscale(Grayscale.AVERAGE); //Sum all the viable pixels
            }
        }
        return mean / (double) numOfPixels;
    }

    private double calculateStandardDeviationInWindow(Color[][] pixels, int centerX, int centerY, int w, double mean) {
        int numOfPixels = w * w;

        double sumOfErrors = 0;

        for (int y = centerY - (w / 2); y <= centerY + (w / 2); y++) {
            if (y < 0 || y >= pixels.length) {
                numOfPixels -= w;
                continue;
            }

            for (int x = centerX - (w / 2); x <= centerX + (w / 2); x++) {
                if (x < 0 || x >= pixels[0].length) {
                    numOfPixels -= 1; //One pixel less in the mean calcualation
                    continue;
                }

                sumOfErrors += Math.pow(pixels[y][x].getGrayscale(Grayscale.AVERAGE) - mean, 2); //Sum all the viable pixels
            }
        }

        return Math.sqrt(sumOfErrors / numOfPixels);
    }

}
