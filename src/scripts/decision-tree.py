#!/usr/local/bin/python2

import numpy as np
from sklearn.tree import DecisionTreeClassifier

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 97169653
np.random.seed(seed)

#Set hyperparameters
max_depth = 10
min_samples_leaf = 20
max_leaf_nodes = 25
min_impurity_decrease = 0.0006

#Load data
x_train, y_train, x_test, y_test = load(year=14)

#Define model
model = DecisionTreeClassifier(
	max_depth=max_depth,
	min_samples_leaf=min_samples_leaf,
	max_leaf_nodes=max_leaf_nodes,
	min_impurity_decrease=min_impurity_decrease)

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)

#Display results
display_results(t_train, t_test, confusion, model.predict_proba(x_test)[:,1])

#Plot ROC curve
plot_roc_curve("Decision Tree", "decision_tree_ROC", class_pred, model.predict_proba(x_test)[:,1], y_test)