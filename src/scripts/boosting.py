#!/usr/local/bin/python2

import numpy as np
from sklearn.ensemble import GradientBoostingClassifier

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 51396235
np.random.seed(seed)

#Set hyperparameters
n_trees = 200
max_features = "auto"
max_depth = 6
min_samples_leaf = 40
max_leaf_nodes = 15

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = GradientBoostingClassifier(
	n_estimators=n_trees,
	max_features=max_features,
	max_depth=max_depth,
	min_samples_leaf=min_samples_leaf,
	max_leaf_nodes=max_leaf_nodes)

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)

#Display results
display_results(t_train, t_test, confusion, model.predict_proba(x_test)[:,1])

#Plot ROC curve
plot_roc_curve("Boosting", "boosting_ROC", class_pred, model.predict_proba(x_test)[:,1], y_test)