#!/usr/local/bin/python2

import numpy as np
import pandas as pd

def mapEng(x):
	for i in range(1, 13):
		if x['CRS' + str(i)] == "ENG4U" or x['CRS' + str(i)] == "EAE4U":
			return x['MRK' + str(i)]
	return 0

def mapCalc(x):
	for i in range(1, 13):
		if x['CRS' + str(i)] == "MCV4U":
			return x['MRK' + str(i)]
	return 0

def mapChem(x):
	for i in range(1, 13):
		if x['CRS' + str(i)] == "SCH4U":
			return x['MRK' + str(i)]
	return 0

def mapPhys(x):
	for i in range(1, 13):
		if x['CRS' + str(i)] == "SPH4U":
			return x['MRK' + str(i)]
	return 0

full = pd.DataFrame()
for i in range(9, 15+1):
	#Load the csv file for each year
	if i < 10:
		raw = pd.read_csv("../../data/csv/RawData_0" + str(i) + ".csv")
	else:
		raw = pd.read_csv("../../data/csv/RawData_" + str(i) + ".csv")

	#Remove applicants who were not given an acceptance offer
	raw = raw[((raw.UNI1 == 95) & (raw.PGPD1 != 99))
		| ((raw.UNI2 == 95) & (raw.PGPD2 != 99))
		| ((raw.UNI3 == 95) & (raw.PGPD3 != 99))
		| ((raw.UNI4 == 95) & (raw.PGPD4 != 99))
		| ((raw.UNI5 == 95) & (raw.PGPD5 != 99))
		| ((raw.UNI6 == 95) & (raw.PGPD6 != 99))
		| ((raw.UNI7 == 95) & (raw.PGPD7 != 99))
		| ((raw.UNI8 == 95) & (raw.PGPD8 != 99))
		| ((raw.UNI9 == 95) & (raw.PGPD9 != 99))
		| ((raw.UNI10 == 95) & (raw.PGPD10 != 99))
		| ((raw.UNI11 == 95) & (raw.PGPD11 != 99))
		| ((raw.UNI12 == 95) & (raw.PGPD12 != 99))
		| ((raw.UNI13 == 95) & (raw.PGPD13 != 99))
		| ((raw.UNI14 == 95) & (raw.PGPD14 != 99))
		| ((raw.UNI15 == 95) & (raw.PGPD15 != 99))
		| ((raw.UNI16 == 95) & (raw.PGPD16 != 99))
		| ((raw.UNI17 == 95) & (raw.PGPD17 != 99))
		| ((raw.UNI18 == 95) & (raw.PGPD18 != 99))
		| ((raw.UNI19 == 95) & (raw.PGPD19 != 99))
		| ((raw.UNI20 == 95) & (raw.PGPD20 != 99))]

	#Construct dataframe
	clean = raw.iloc[:, 0:9]
	clean = pd.concat([clean, raw.iloc[:, 10:17]], axis=1)
	#Always 4 courses, English, Calculus, Chemistry, Physics
	clean['ENG'] = raw.apply(mapEng, axis=1)
	clean['CALC'] = raw.apply(mapCalc, axis=1)
	clean['CHEM'] = raw.apply(mapChem, axis=1)
	clean['PHYS'] = raw.apply(mapPhys, axis=1)
	clean = pd.concat([clean, raw.iloc[:, 65:70]], axis=1)
	#Always 5 applications fill missing ones with 0's
	#CHS: 1 for McMaster, 0 for another university
	clean['CHS1'] = np.where(raw['UNI1'] == 95, 1, 0)
	clean['CHS2'] = np.where(raw['UNI2'] == 95, 1, 0)
	clean['CHS3'] = np.where(raw['UNI3'] == 95, 1, 0)
	clean['CHS4'] = np.where(raw['UNI4'] == 95, 1, 0)
	clean['CHS5'] = np.where(raw['UNI5'] == 95, 1, 0)
	clean = pd.concat([clean, raw['SEQNO']], axis=1)
	temp = pd.DataFrame()
	#Whether the applicant accepted an offer to go to this university
	#ACCPT: 1 if they did, 0 if they didn't
	temp['ACCPT'] = 0
	temp['ACCPT'] = np.where(raw['CNFUNI'] == 95, 1, 0)

	#Reset the index so merging works, remove any rows missing data
	clean.reset_index(drop=True, inplace=True)
	clean = pd.concat([temp, clean], axis=1)
	clean = clean.dropna()

	#Write the dataframe to file, append this year's data to the full dataset
	full = full.append(clean, ignore_index=True)
	print("Year " + str(i) + " complete.")

full.to_csv(path_or_buf="../../data/csv/clean.csv", index=False)