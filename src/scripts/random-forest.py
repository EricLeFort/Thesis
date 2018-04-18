#!/usr/local/bin/python2
from utilities import *
import models

#Load data
x_train, y_train, x_test, y_test = load(year=15)

#Define model
model = models.randomForestModel()

#Train and test model
class_pred, confusion, t_train, t_test = train_and_test(model, x_train, y_train, x_test, y_test)
pred = model.predict_proba(x_test)[:,1]

#Display results
display_results(t_train, t_test, confusion, pred)

#Plot diagrams
plot_roc_curve("Random Forest", "random_forest", class_pred, pred, y_test)
plot_prediction_histogram("Random Forest", "random_forest", pred)

#KPCA was only 0.18% better
#not worth the extra training time and loss of interpretability