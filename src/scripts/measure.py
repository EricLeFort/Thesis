#!/usr/local/bin/python2
import pandas as pd
import numpy as np

from utilities import *
from models import *

#Create a structure to easily access the correct model to work with
models = [
	['Boosting', 'DecisionTree' ,'k-NN', 'LogisticRegression',
		'NaiveBayes', 'NeuralNet', 'RandomForest', 'SVM'],
	[boostingModel(), decisionTreeModel(), kNNModel(), logRegModel(),
		naiveBayesModel(), nnModel(), randomForestModel(), svmModel()]
]
scale = [False, False, False, False, False, True, False, False]
kpca = [False, False, True, False, True, False, False, False]

#Prepare the dataframes
accuracy = pd.DataFrame(columns=['ModelName'])
for i in range(0, len(models[0])):
	accuracy.loc[i, 'ModelName'] = models[0][i]
training_time = accuracy.copy(deep=True)
testing_time = accuracy.copy(deep=True)

#Test year 2012-2015 and record their accuracies
print("Measuring model accuracies..")
for i in range(12, 15+1):
	for j in range(0, len(models[0])):
		x_train, y_train, x_test, y_test = load(year=i, scale_x=scale[j], kpca=kpca[j])
		_, confusion, _, _ = train_and_test(models[1][j], x_train, y_train, x_test, y_test)
		pred = sum(models[1][j].predict_proba(x_test)[:,1])
		actual = confusion[1,0] + confusion[1,1]
		accuracy.loc[j, 'year' + str(i)] = abs((pred-actual)/actual)
	print("Year " + str(i) + " finished.")

#Compute average and standard deviation of accuracy for each model
accuracy['mean'] = accuracy.mean(axis=1)
accuracy['stddev'] = accuracy.std(axis=1)

#Write the measured accuracies to file
accuracy.to_csv(path_or_buf="../../data/results/accuracy.csv", index=False)

#Record the time (in seconds) to train and test year 2015 25 times per model
print("Measuring model training and testing times..")
for i in range(0, len(models[0])):
	for j in range(1, 25+1):
		_, _, t_train, t_test = train_and_test(models[1][i], x_train, y_train, x_test, y_test)
		training_time.loc[i, 'trial' + str(j)] = t_train
		testing_time.loc[i, 'trial' + str(j)] = t_test
	
	print(models[0][i] + " timings measured.")

#Write the measured times to file
training_time.to_csv(path_or_buf="../../data/results/trainingTime.csv", index=False)
testing_time.to_csv(path_or_buf="../../data/results/testingTime.csv", index=False)

#Measure feature importances for boosting, decision trees and random forests
columns = list(x_train)
importances = pd.DataFrame(models[1][0].feature_importances_).T
importances = importances.append(
	pd.DataFrame(models[1][1].feature_importances_).T)
importances = importances.append(
	pd.DataFrame(models[1][6].feature_importances_).T)
importances.columns = columns
importances.insert(0, 'ModelName', ['Boosting', 'DecisionTree', 'RandomForest'])

#Write the measured importances to file
importances.to_csv(path_or_buf="../../data/results/treeImportances.csv", index=False)