#!/usr/local/bin/python2

import numpy as np
from sklearn.linear_model import LogisticRegression

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 235945778
np.random.seed(seed)

#Set hyperparameters
stop_criteria = 1E-10	#Minimum progress
reg_strength = 0.1 		#Regularization strength -- Smaller -> stronger regularization

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = LogisticRegression(tol=stop_criteria, C=reg_strength, max_iter=1000)

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)

#Display results
display_results(t_train, t_test, confusion, model.predict_proba(x_test)[:,1])

#Plot ROC curve
plot_roc_curve("Logistic Regression", "logistic_regression_ROC", class_pred, model.predict_proba(x_test)[:,1], y_test)