import time
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix
from sklearn.metrics import roc_auc_score
from sklearn.metrics import roc_curve
from sklearn.preprocessing import scale

#Loads data, separate into x and y for train and test
#year - the year to test
#num_years - the number of previous years to consider
#balance - whether to balance the number of acceptances and denials
#
#returns x_train, y_train, x_test, y_test
def load(year, num_years=3, scale_x=False, balance=False):
	data = pd.read_csv("../../data/csv/clean.csv")
	test = data[data["REFYR"] == year]

	#Balances the data to have an equal number of acceptances and denials
	if(balance):
		accepted = data[data["ACCPT"] == 1]
		didnt = data[data["ACCPT"] == 0].sample(frac=0.4)
		data = pd.concat([accepted, didnt], axis=0)

	train = data[(data["REFYR"] > year - num_years - 1) & (data["REFYR"] < year)]
	
	x_train = train.drop(["SEQNO", "ACCPT"], axis=1)
	y_train = train["ACCPT"]
	x_test = test.drop(["SEQNO", "ACCPT"], axis=1)
	y_test = test["ACCPT"]

	if(scale_x):
		return scale(x_train), y_train, scale(x_test), y_test

	return x_train, y_train, x_test, y_test

#Perform the training and testing for this model.
#model - The model to use
#x_train, y_train, x_test, y_test - The data to use
#
#returns pred (the resulting predictions), confusion (the resulting confusion matrix), t_train, t_test
def train_and_test(model, x_train, y_train, x_test, y_test, binary=True):
	t_train = time.clock()
	model.fit(x_train, y_train)
	t_train = time.clock() - t_train

	t_test = time.clock()
	class_pred = model.predict(x_test)
	t_test = time.clock() - t_test

	if(not binary):
		class_pred = np.where(class_pred > 0.5, 1, 0)
		
	confusion = confusion_matrix(y_test, class_pred)
	return class_pred, confusion, t_train, t_test

#Displays a summary of the results of a model's performance
#t_train - The recorded training time
#t_test - The recorded testing time
#confusion - The resulting confusion matrix
#class_probs - The resulting class probabilities
def display_results(t_train, t_test, confusion, class_probs):
	print("\nTraining time: " + str(t_train * 1000) + "ms")	#Time in milliseconds
	print("Testing time: " + str(t_test * 1000) + "ms")
	print("\nPredicted acceptances (hard clustering): " + str(confusion[0,1] + confusion[1,1]))
	print("Predicted acceptances (probabilities): " + str(sum(class_probs)))
	print("Actual Acceptances: " + str(confusion[1,0] + confusion[1,1]))
	print("Accuracy: " + str(1.0*(confusion[0,0] + confusion[1,1]) / class_probs.shape[0]))
	print(confusion)

#Plots the roc curve for the given results
#label - A string containing the label displayed on the graph
#filename - The name of the file to save the plot to
#class_pred - The class predictions
#class_probs - The predicted class probabilities
#targets - The targets for the testing data
#show - Whether to show the plot
def plot_roc_curve(label, filename, class_pred, class_probs, targets, show=False):
	roc_auc = roc_auc_score(targets, class_pred)
	false_pos_rate, true_pos_rate, thresholds = roc_curve(targets, class_probs)
	plt.figure()
	plt.plot(false_pos_rate, true_pos_rate, label='%s (area = %0.2f)' % (label, roc_auc))
	plt.plot([0, 1], [0, 1],'r--')
	plt.xlim([0.0, 1.0])
	plt.ylim([0.0, 1.05])
	plt.xlabel('False Positive Rate')
	plt.ylabel('True Positive Rate')
	plt.title('Receiver Operating Characteristic (ROC)')
	plt.legend(loc="lower right")
	plt.savefig('../../resources/%s_ROC' % (filename))

	if(show):
		plt.show()