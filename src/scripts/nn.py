#!/usr/local/bin/python2

import numpy as np
from sklearn.neural_network import MLPClassifier

from utilities import *

#Pseudorandom seed made by mashing my keyboard
seed = 59785065
np.random.seed(seed)

#Load data
x_train, y_train, x_test, y_test = load(year=15, scale_x=True, balance=False)

#Define model
model = MLPClassifier(
	#hidden_layer_sizes=(17, 17, 17),	#Best for: 14, 15
	hidden_layer_sizes=(27, 27),		#Best for: 12, 13
	learning_rate_init=0.1,
	momentum=0.9,
	batch_size=32,
	max_iter=3000,
	solver="sgd",
	learning_rate="adaptive",
	tol=1E-5,
	verbose=True)

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)

#Display results
display_results(t_train, t_test, confusion, model.predict_proba(x_test)[:,1])

#Plot ROC curve
plot_roc_curve("Neural Network", "neural_net_ROC", class_pred, model.predict_proba(x_test)[:,1], y_test, show=True)