package main.java.binarization;

import main.java.binarization.GTBinarization.CompareMode;
import main.java.general.Color;
import main.java.interfaces.Binarization;
import main.java.utils.ImageConverter;

import java.util.ArrayList;

public class OtsuBinarization implements Binarization {

    CompareMode compareMode;

    public OtsuBinarization(CompareMode compareMode) {
        super();
        this.compareMode = compareMode;
    }

    @Override
    public boolean[][] binarize(Color[][] imageRGB) {

        //Calculate Threshold
        int[][] grayscale = ImageConverter.calculateGrayscale(imageRGB);
        double[] histogram = calculateRelativeHistogram(grayscale);

        double w0 = histogram[0];
        double w1 = 1 - w0;

        ArrayList<Double> variances = new ArrayList<>();
        //Class 0: from 0 -> t-1 | Class 1: from t -> L-1
        for (int t = 1; t < histogram.length; t++) {
            double mean0 = mean(histogram, w0, 0, t - 1);
            double mean1 = mean(histogram, w1, t, histogram.length - 1);

            double v0 = variance(histogram, w0, mean0, 0, t - 1);
            double v1 = variance(histogram, w1, mean1, t, histogram.length - 1);
            double vw = w0 * v0 + w1 * v1;
            if (!Double.isNaN(vw)) {
                variances.add(vw);
            }

            w0 += histogram[t];
            w1 = 1 - w0;
        }

        int minIndex = 0;
        double min = variances.get(0);

        for (int x = 1; x < variances.size(); x++) {
            if (min > variances.get(x)) {
                min = variances.get(x);
                minIndex = x;
            }
        }

        System.out.println(minIndex);
        GTBinarization gt = new GTBinarization(minIndex + 1, compareMode);
        return gt.binarize(imageRGB);
    }

    private double mean(double[] histogram, double N, int start, int end) {

        double mean = 0;

        for (int x = start; x <= end; x++) {
            mean += (x + 1) * histogram[x];
        }

        return mean / N;
    }

    private double variance(double[] histogram, double N, double mean, int start, int end) {

        double variance = 0;

        for (int i = start; i <= end; i++) {
            variance += Math.pow((i + 1) - mean, 2) * histogram[i];
        }

        return variance / N;
    }

    public double[] calculateRelativeHistogram(int[][] grayscale) {

        double[] histogram = new double[256];
        for (int x = 0; x < grayscale.length; x++) {
            for (int y = 0; y < grayscale[x].length; y++) {
                histogram[grayscale[x][y]] += 1;
            }
        }

        for (int x = 0; x < histogram.length; x++) {
            histogram[x] /= grayscale.length * grayscale[0].length;
        }

        return histogram;
    }

}
