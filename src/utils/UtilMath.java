package utils;

public class UtilMath {

    /**
     * Takes any amount of integers and returns the maxmimum value
     * @param values
     * @return
     */
    public static int max(int ... values) {

        int maxVal = values[0];
        for(int x = 1; x < values.length; x++) {
            maxVal = Math.max(maxVal, values[x]);
        }

        return maxVal;
    }

    /**
     * Takes any amount of integers and returns the minimum value
     * @param values
     * @return
     */
    public static int min(int ... values) {

        int minVal = values[0];
        for(int x = 1; x < values.length; x++) {
            minVal = Math.min(minVal, values[x]);
        }

        return minVal;
    }

    /**
     * Takes any amount of integers and returns the average value
     * @param values
     * @return
     */
    public static double average(int ... values) {

        int avg = 0;
        for(int x = 0; x < values.length; x++) {
            avg = values[x];
        }

        return (double)avg / (double)values.length;
    }

    /**
     * Interpolation methods from http://paulbourke.net/miscellaneous/interpolation/
     */
    public static double LinearInterpolate(double y1,double y2,double mu){
        return(y1*(1-mu)+y2*mu);
    }

    public static double CosineInterpolate(double y1,double y2, double mu){
        double mu2;

        mu2 = (1-Math.cos(mu*Math.PI))/2;
        return(y1*(1-mu2)+y2*mu2);
    }

    public static double CubicInterpolate(double y0, double y1, double y2, double y3, double mu){
        double a0,a1,a2,a3,mu2;

        mu2 = mu*mu;
        a0 = y3 - y2 - y0 + y1;
        a1 = y0 - y1 - a0;
        a2 = y2 - y0;
        a3 = y1;

        return(a0*mu*mu2+a1*mu2+a2*mu+a3);
    }

}
