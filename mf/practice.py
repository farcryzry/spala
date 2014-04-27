__author__ = 'lan'
from MatrixFactorization import mf
import csv

file_path = "/Users/lan/documents/sjsu/239/project/"
parameters = open(file_path + "parameters.csv", 'Ur')
reader = csv.reader(parameters, delimiter=',')
result = open(file_path + "results", 'a')

nmf = mf(file_path, "train_data.csv", "test_data.csv", "purchase_data.csv")

for row in reader:
    args = [int(row[0]), int(row[1]), float(row[2]), float(row[3])]
    result.write("\nrank={0}, iterations={1}, lambda_={2}, alpha={3}\n".format(*args))
    model = nmf.train(*args)
    pred = nmf.predict(model)
    nmf.evaluate(pred, result)

parameters.close()
result.close()

print "end"