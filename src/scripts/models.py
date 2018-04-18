from sklearn.decomposition import KernelPCA
from sklearn.ensemble import GradientBoostingClassifier, RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.naive_bayes import GaussianNB
from sklearn.neural_network import MLPClassifier
from sklearn.svm import SVC

#Defines a KPCA model
def kpcaModel():
	return KernelPCA(
		n_components = 5,
		degree=2,
		kernel="poly")

#Defines a boosting model
def boostingModel():
	return GradientBoostingClassifier(
		learning_rate=0.01,
		n_estimators=200,
		max_features="auto",
		max_depth=6,
		min_samples_leaf=40,
		max_leaf_nodes=15)

#Defines a decision tree model
def decisionTreeModel():
	return DecisionTreeClassifier(
		max_depth=10,
		min_samples_leaf=20,
		max_leaf_nodes=25,
		min_impurity_decrease=0.0006)

#Defines a k-NN model
def kNNModel():
	return KNeighborsClassifier(
		n_neighbors=9,
		weights="distance",
		algorithm="kd_tree")

#Defines a logistic regression model
def logRegModel():
	return LogisticRegression(
		tol=1E-10,
		C=0.1,
		max_iter=1000)

#Defines a naive bayes model
def naiveBayesModel():
	return GaussianNB()

#Defines a neural network model
def nnModel(verbose=False):
	return MLPClassifier(
		hidden_layer_sizes=(20, 20),
		learning_rate_init=0.1,
		momentum=0.9,
		batch_size=32,
		max_iter=3000,
		solver="sgd",
		learning_rate="adaptive",
		tol=1E-5,
		verbose=verbose)

#Defines a random forest model
def randomForestModel():
	return RandomForestClassifier(
		n_estimators=200,
		max_features="auto",
		max_depth=6,
		min_samples_leaf=40,
		max_leaf_nodes=15)

#Defines a SVM model
def svmModel():
	return SVC(
		kernel='poly',
		degree=4,
		gamma=0.2,
		tol=1E-10,
		max_iter=600,
		probability=True)