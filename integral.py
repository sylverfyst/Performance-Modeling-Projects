import math, random, numpy 

pie = math.pi

#generates a list of values from 0 to 1 in increments of 0.001, 1.0 non-inclusive 
range = numpy.linspace(0.0,1.0,1000,endpoint=False)

#this is the constant value outside the integral
constant = (1/math.sqrt(2*pie))

#this will be where the results of our simulation are stored
results = []

#number of times to repeat the sampling
numreps = 1000

while (i < numreps):
	#choose a number between 0 and 1, increment by 0.001, from range, thought about using uniform, but it got to be a little complex
	x = random.choice(range)
	#y is the inside of the integral, with x being our random variable.  math.exp(x) evaluates to e ** x
	y = math.exp(-x^2/2)
	#add the result to our list
	results.append(y)
	i+= 1

#aggregate results (Monte Carlo Method) 
integral_value = sum(values)/len(values)
print(integral_value)
