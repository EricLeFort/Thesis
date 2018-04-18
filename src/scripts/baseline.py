#!/usr/local/bin/python2
from utilities import *

acceptances = []
totals = []
rates = []
for i in range(9, 15+1):
	_, _, _, y_test = load(year=i)
	acceptances.append(y_test[y_test == 1].shape[0])
	totals.append(y_test.shape[0])
	rates.append(1.0*acceptances[i-9] / totals[i-9])

predictedRates = []
predictedCounts = []
accuracies = []
for i in range(12, 15+1):
	predictedRates.append((rates[i-10] + rates[i-11] + rates[i-12]) / 3)
	predictedCounts.append(predictedRates[i-12] * totals[i-9])
	accuracies.append(1-abs(predictedCounts[i-12] - acceptances[i-9])/acceptances[i-9])

print(accuracies)