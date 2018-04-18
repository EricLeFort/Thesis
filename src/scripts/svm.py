#!/usr/local/bin/python2
from utilities import *
import models

#Load data
x_train, y_train, x_test, y_test = load(year=15, balance=False)

#Define model
model = models.svmModel()

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)
pred = model.predict_proba(x_test)[:,1]

#Display results
display_results(t_train, t_test, confusion, pred)

#Plot diagrams
plot_roc_curve("Support Vector Machine", "svm", class_pred, pred, y_test)
plot_prediction_histogram("Support Vector Machine", "svm", pred)

#KPCA: Hard no