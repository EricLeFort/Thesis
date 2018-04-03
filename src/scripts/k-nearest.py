#!/usr/local/bin/python2

import numpy as np
from sklearn.neighbors import KNeighborsRegressor

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 361582214
np.random.seed(seed)

#Set hyperparameters
k = 9					#Number of neighbours
algo = "kd_tree"		#neighbour locating algorithm: one of {ball_tree, kd_tree, brute}
leaf_size = 30			#Leaf node size (if using one of the tree algorithms)

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = KNeighborsRegressor(n_neighbors=k, weights="distance", algorithm=algo)

#Train and test model
pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test, binary=False)
class_pred = np.where(pred > 0.5, 1, 0)

#Display results
display_results(t_train, t_test, confusion, pred)

#Plot ROC curve
plot_roc_curve("k-NN", "k-NN_ROC", class_pred, pred, y_test)