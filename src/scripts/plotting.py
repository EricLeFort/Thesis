#!/usr/local/bin/python2
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

#Load data
accuracy = pd.read_csv("../../data/results/accuracy.csv")
training_time = pd.read_csv("../../data/results/trainingTime.csv")
testing_time = pd.read_csv("../../data/results/testingTime.csv")
importances = pd.read_csv("../../data/results/treeImportances.csv")

#Define bar graph variables
yLabels = importances.columns[1:]
yPos = np.arange(len(yLabels))
h = 0.8
label = ["Boosting", "Decision Tree", "Random Forest"]
filename = ["boosting", "decisionTree", "randomForest"]

#Plot bar graphs of relative feature importances
for i in range(0, 3):
	plt.figure(figsize=(6, 7))
	plt.gca().xaxis.grid(True)
	plt.title('%s Model Feature Importances' % label[i])
	plt.barh(yPos, importances.iloc[i, 1:], height=h, color="blue", align='center', zorder=3)
	plt.yticks(yPos, yLabels)
	plt.xlabel('Relative Importance')
	plt.ylabel('Feature')
	plt.savefig('../../resources/featureImportancePlots/%s' % filename[i])
	plt.close()

#Convert from loss to accuracy
accuracy['year12'] = 1 - accuracy['year12']
accuracy['year13'] = 1 - accuracy['year13']
accuracy['year14'] = 1 - accuracy['year14']
accuracy['year15'] = 1 - accuracy['year15']
accuracy = accuracy.set_index('ModelName')

#Segment training times so they appear better when plotted
training_time = training_time.set_index('ModelName')
training_time_slow = training_time.iloc[[0, 5]]
training_time_med = training_time.iloc[[3, 6, 7]]
training_time_fast = training_time.iloc[[1, 2, 4]]

#Also segment the testing times
testing_time = testing_time.set_index('ModelName')
testing_time_med = testing_time.iloc[[0, 2, 6, 7]]
testing_time_fast = testing_time.iloc[[1, 3, 4, 5]]

#Plot box plots for training and testing times
training_time_slow.T.boxplot(figsize=(6, 5))
plt.title('Model Training Times')
plt.ylabel('Time (s)')
plt.savefig('../../resources/timeBoxplots/trainingSlowTimeBoxplot')
plt.close()
training_time_med.T.boxplot(figsize=(6, 5))
plt.title('Model Training Times')
plt.ylabel('Time (s)')
plt.savefig('../../resources/timeBoxplots/trainingMedTimeBoxplot')
plt.close()
training_time_fast.T.boxplot(figsize=(6, 5))
plt.title('Model Training Times')
plt.ylabel('Time (s)')
plt.savefig('../../resources/timeBoxplots/trainingFastTimeBoxplot')
plt.close()
testing_time_med.T.boxplot(figsize=(6, 5))
plt.title('Model Testing Times')
plt.ylabel('Time (s)')
plt.savefig('../../resources/timeBoxplots/testingMedTimeBoxplot')
plt.close()
testing_time_fast.T.boxplot(figsize=(6, 5))
plt.title('Model Testing Times')
plt.ylabel('Time (s)')
plt.savefig('../../resources/timeBoxplots/testingFastTimeBoxplot')
plt.close()
