spala
=====

Course Project for CMPE 239 - Allstate Purchase Prediction Challenge (Kaggle competition)

https://www.kaggle.com/c/allstate-purchase-prediction-challenge

Data Visulization
=====
https://github.com/farcryzry/spala/commit/cb126c86a4efe3c4ace6d824d889c3b915cf2563

Regression (Java + Spark)
=====

Matrix Factorization (Python + Spark)
=====


KMeans + KNN (R)
=====

Preprocess:
1.	Filter out purchase records. (record_type = 1)
2.	Remove columns: shopping_pt/record_type/day/time/state/location/c_previous/cost
3.	Combine columns: age_oldest / age_youngest => age_average
4.	Remove outlier: car_age
5.	Handle missing value:  Risk_factor (use 0)  Duration_previous (use mean value)

Code:
https://github.com/farcryzry/spala/blob/master/pre-processiong/DataHandler.R

Data:
https://github.com/farcryzry/spala/blob/master/data/train.csv



