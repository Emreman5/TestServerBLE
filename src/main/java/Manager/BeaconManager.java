package Manager;

import Maths.*;

import Models.Beacon;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class BeaconManager {
    public static double[] getLocationWithTrilateration(double[][] positions, double[] distances){

        double[][] positionss = new double[][] { { 0, 0 }, { 100, 200 }, { 200, 0 }};
        double[] distancess = new double[] { 100, 200, 100 };

        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions,
                distances), new LevenbergMarquardtOptimizer());
        Optimum optimum = solver.solve();

        // the answer
        double[] calculatedPosition = optimum.getPoint().toArray();

        // error and geometry information
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);

        return calculatedPosition;
    }
}
