__author__ = 'lan'
from MatrixFactorization import mf
import csv

file_path = "/home/ubuntu/spala/mf/"
parameters = open(file_path + "parameters.csv", 'Ur')
reader = csv.reader(parameters, delimiter=',')
result = open(file_path + "results", 'a')

## build model from complete dateset including train and test dataset
#nmf = mf(file_path, "train_data.csv", "purchase_test_data.csv", "purchase_data.csv")
#nmf = mf(file_path, "train_data.csv", "sample_test_data.csv")

## build model from train dataset
#nmf = mf(file_path, "purchase_train_data.csv", "purchase_test_data.csv", "purchase_data.csv")

## build model from test dataset
nmf = mf(file_path, "sample_train_data.csv", "sample_test_data.csv")

for row in reader:
    args = [int(row[0]), int(row[1]), float(row[2]), float(row[3])]
#    result.write("\nrank={0}, iterations={1}, lambda_={2}, alpha={3}\n".format(*args))
    model = nmf.train(*args)
    pred = nmf.predict(model)
    nmf.evaluate(pred, result)
    # nmf.output_predict(pred)

parameters.close()
result.close()

print "end"
