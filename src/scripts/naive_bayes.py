#!/usr/local/bin/python2

import numpy as np
from sklearn.naive_bayes import GaussianNB

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 235945778
np.random.seed(seed)

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = GaussianNB()

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)

#Display results
display_results(t_train, t_test, confusion, model.predict_proba(x_test)[:,1])

#Plot ROC curve
plot_roc_curve("Naive Bayes", "naive_bayes_ROC", class_pred, model.predict_proba(x_test)[:,1], y_test)