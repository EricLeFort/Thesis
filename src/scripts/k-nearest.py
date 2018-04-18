#!/usr/local/bin/python2
from utilities import *
import models

#Load data
x_train, y_train, x_test, y_test = load(year=15, kpca=True)

#Define model
model = models.kNNModel()

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)
pred = model.predict_proba(x_test)[:,1]

#Display results
display_results(t_train, t_test, confusion, pred)

#Plot diagrams
plot_roc_curve("k-NN", "k-NN", class_pred, pred, y_test)
plot_prediction_histogram("k-NN", "k-NN", pred)

#k-NN went from 96.63% to 97.30% using KPCA