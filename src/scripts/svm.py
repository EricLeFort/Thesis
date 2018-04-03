#!/usr/local/bin/python2

import numpy as np
from sklearn.svm import LinearSVR

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 235945778
np.random.seed(seed)

#The year to test and how many previous years to look at for training
year = 15
num_years = 3

#Set Hyperparameters
stop_criteria = 1E-10
eps = 0.5

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = LinearSVR(tol=stop_criteria, epsilon=eps, max_iter=1000)

#Train and test model
pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test, binary=False)
class_pred = np.where(pred > 0.5, 1, 0)

#Display results
display_results(t_train, t_test, confusion, pred)

#Plot ROC curve
plot_roc_curve("Support Vector Machine", "svm_ROC", class_pred, pred, y_test)