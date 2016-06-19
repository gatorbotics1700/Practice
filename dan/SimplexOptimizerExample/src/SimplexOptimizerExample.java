import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

public class SimplexOptimizerExample {
	public static void main(String[] args) {
		solveSimple();
		solveLine();
		solveCircle();
	}
	
	public static void solveSimple() {
		// Builds an optimizer. The arguments are the stopping criteria, or
		// "How close do we need to be to the solution to accept it." Generally,
		// the optimizer is considered finished when it stops moving it's best
		// guess. The first argument is the relative threshold, the second is
		// the absolute threshold. The relative threshold is satisfied when the
		// error moves a small amount relative to what it thinks the solution
		// is, and the absolute threshold checks for the change in the error
		// regardless of what the magnitude of it is. If either threshold is
		// crossed, the solver is considered done.
		SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);
		
		// convex is the problem we are solving.
		final Convex convex = new Convex();

		// optimum will contain both the point we think the solution is at,
		// and the value of our function there.
		final PointValuePair optimum = optimizer.optimize(
				// Takes at most 10k steps
				new MaxEval(10000),
				// Uses the "convex" class as it's objective function.
				// An objective function is the function we're trying to
				// minimize.
				new ObjectiveFunction(convex),
				// We want to minimize, we can also maximize. It's the same
				// thing in the end.
				GoalType.MINIMIZE,
				// Start the solver at a random point. If we have a better
				// idea, we can use that. For vision, we definitely will have
				// a better idea.
				new InitialGuess(new double[] {
						Math.random(), Math.random() }), 
				// NedlerMeadSimplex is the standard one. The "2" tells it we
				// are solving a 2-D problem.
				new NelderMeadSimplex(2));

		// Note that even though we start at a random point, the solution is
		// always near 0!
		System.out.println(Arrays.toString(
				optimum.getPoint()) + " : " + optimum.getSecond());
	}

	private static class Convex implements MultivariateFunction {
		// Our objective function is simply the 2D parabola z = x^2 + y^2, so
		// the minimum will be at (0,0).
		//
		// "variables" contains the solver's current guess. You just need to 
		// tell it how wrong it is.
		public double value(double[] variables) {
			final double x = variables[0];
			final double y = variables[1];
			return x * x + y * y;
		}
	}
	
	public static void solveLine() {
		SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);

		// The best fit should be the line y = x
		double[] xVals = {-1.0, 0.0, 1.0};
		double[] yVals = {-1.0, 0.0, 1.0};
		
		final LineFit line = new LineFit(xVals, yVals);

		final PointValuePair optimum = optimizer.optimize(
				new MaxEval(10000),
				new ObjectiveFunction(line),
				GoalType.MINIMIZE,
				new InitialGuess(new double[] {
						Math.random(), Math.random()}), 
				new NelderMeadSimplex(2));

		System.out.println(Arrays.toString(
				optimum.getPoint()) + " : " + optimum.getSecond());
	}

	private static class LineFit implements MultivariateFunction {
		// This time, we'll fit a circle to a series of points.
		private double[] xVals, yVals;
		
		LineFit(double[] xVals, double[] yVals) {
			super();
			this.xVals = xVals;
			this.yVals = yVals;
		}
		
		// Here, the variables will be the parameters for a circle.
		public double value(double[] variables) {
			final double slope = variables[0];
			final double intercept = variables[1];
			double error = 0;
			for (int point_index = 0; point_index < xVals.length; point_index++) {
				double pointX = xVals[point_index];
				double pointY = yVals[point_index];
				
				double lineY = slope * pointX + intercept;
				double errorY = lineY - pointY;
				double pointError = Math.sqrt(errorY * errorY);
				
				// The error for the guess is the sum of the errors for each point.
				error += pointError;
			}
			return error;
		}
	}
	
	public static void solveCircle() {
		SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);

		// The best fit should be a circle centered at (0,0) with radius 1.
		double[] xVals = {1.0, -1.0, 0.0, 0.0};
		double[] yVals = {0.0, 0.0, 1.0, -1.0};
		
		final CircleFit circle = new CircleFit(xVals, yVals);

		final PointValuePair optimum = optimizer.optimize(
				new MaxEval(10000),
				new ObjectiveFunction(circle),
				GoalType.MINIMIZE,
				new InitialGuess(new double[] {
						Math.random(), Math.random(), Math.random()}), 
				new NelderMeadSimplex(3));

		System.out.println(Arrays.toString(
				optimum.getPoint()) + " : " + optimum.getSecond());
	}

	private static class CircleFit implements MultivariateFunction {
		// This time, we'll fit a circle to a series of points.
		private double[] xVals, yVals;
		
		CircleFit(double[] xVals, double[] yVals) {
			super();
			this.xVals = xVals;
			this.yVals = yVals;
		}
		
		// Here, the variables will be the parameters for a circle.
		public double value(double[] variables) {
			final double centerX = variables[0];
			final double centerY = variables[1];
			final double radius = variables[2];
			double error = 0;
			for (int point_index = 0; point_index < xVals.length; point_index++) {
				double pointX = xVals[point_index];
				double pointY = yVals[point_index];
				
				// This block projects the vector between the center of the
				// circle and the point we're checking onto the circle.
				double deltaX = pointX - centerX;
				double deltaY = pointY - centerY;
				double magnitude = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				double closestX = centerX + deltaX / magnitude * radius;
				double closestY = centerX + deltaY / magnitude * radius;
				
				// We take the error between the closest point on the circle and the point.
				double errorX = closestX - pointX;
				double errorY = closestY - pointY;
				double pointError = Math.sqrt(errorX * errorX + errorY * errorY);
				
				// The error for the guess is the sum of the errors for each point.
				error += pointError;
			}
			return error;
		}
	}
}