import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.stat.Tally;
import umontreal.iro.lecuyer.util.*;
import umontreal.iro.lecuyer.randvar.*;

public class asiancalloption {

	double [] S;
	double [] Y;
	double r;
	double sigma;
	double K;
	double initial;
	double m;
	int counter;


	public asiancalloption (double r, double sigma, double m, double initial, double K) {
		this.r = r;
		this.sigma = sigma;
		this.m = m;
		this.initial = initial;
		this.K = K;
		counter = 1;
		this.S = new double[(int)m];
		this.Y = new double[(int)m];
	}
/*
Pseudo code:
Start by generating the prices over time.  Recursively.
Price(previous price) {
	if(< m)
		St[k] = previous * (risk-free interest rate * volatility^2/2) * delta + volatility * √delta * Z
		Price(St[k])
	else
		return St
}
Next generate the payout value.
Payout(St []) {
	for(m) {
		sum += St[i]
	}
	payout = (1/m * sum) - K
	if (1/m*sum > K) 
		return payout
	else
		return 0
}
Next generate the fair price 
fairPrice(payout) {
	Time = 1/250
	fair price = exp(-risk free interest rate * Time) * payout
	return fair price
}
Next Simulate the number of runs 
use standard method from other examples to simulate the runs.  
set the parameters for the asian call option in the main method, use a tally object to 
record stats, and a timer to record CPU time.  
*/
	public void St(double previous, RandomStream Z) {
	 if(counter <= m) {
	 	
	 	RandomVariateGen zNormal = new NormalGen(Z);
	 	double normal = zNormal.nextDouble();
	 	double delta = (double)1/250;
	 	
	 	double exponent = (r - (sigma * sigma/2)) * delta + sigma * Math.sqrt(delta) * normal;
		
		double Stk = previous * Math.exp(exponent);
		S[counter-1] = Stk;
		counter++;
		Z.resetNextSubstream();
		St(Stk, Z);
	 }
	 else {
	 	counter = 1;
	 }
	}
	public double X(double [] S) {
		double sumS = 0;
		double oneOverM = 1/m;
		for(int i = 0; i < S.length; i++) {
			sumS += S[i];
		}
		double averageSum = (oneOverM * sumS);
		double x = averageSum - K;
		
		if(averageSum > K) {
			return x;
		}
		else {
			x = 0;
			return x;
		}
	}
	public double Y(double X) {
		double T = m/250;
		double rtimesT = -r * T;
		double Y = Math.exp(rtimesT) * X;
		return Y;
	}
	public void simulateRuns (int n, RandomStream stream, Tally statValue) {
	   statValue.init();
	   for (int i=0; i<n; i++) {
		  St(initial, stream);
		  double Xval = X(S);
		  double Yval = Y(Xval);
		  statValue.add(Yval);
		  
		  stream.resetNextSubstream();
	   }
	}
	public static void main (String[] args) {
		double m = 50.0;
		//r, sigma, m, S0, K
		asiancalloption process = new asiancalloption(0.03, 0.4, m, 100, 95);
		Tally statValue = new Tally("Stats on Asian Call Option Process");

		Chrono timer = new Chrono();
		int n = 10000;
		process.simulateRuns(n,new MRG32k3a(),statValue);
		System.out.println("p is approximately: " + statValue.average());
		System.out.println(statValue.formatCINormal(0.95));
		System.out.println ("Total CPU time:      " + timer.format() + "\n");
	}
}  