__author__ = 'lan'
from pyspark import SparkContext, SparkConf
from pyspark.mllib.recommendation import ALS
from numpy import array
from operator import add, mul


class mf:
    def __init__(self, file_path, train_file, test_file, real_file=None):
        self.file_path = file_path
        config = SparkConf().setMaster("local").setAppName("Kaggle")\
            .set("spark.executor.memory", "2g")\
            .set("spark.storage.memoryFraction", "0.5")

        sc = SparkContext(conf=config)

        self.train_data = sc.textFile("file:" + self.file_path + train_file).cache()\
            .map(lambda line: array([float(x) for x in line.split(',')]))

        self.test_data = sc.textFile("file:" + self.file_path + test_file).cache()\
            .map(lambda line: [float(x) for x in line.split(',')])

        if real_file:
            self.real_data = sc.textFile("file:" + self.file_path + real_file).cache()\
                .map(lambda line: [float(x) for x in line.split(',')]).map(lambda r: ((r[0], r[1]), r[2]))

    def train(self,  rank=3, iterations=20, lambda_=0.01, alpha=0.01, blocks=-1):
        model = ALS.trainImplicit(self.train_data, rank, iterations, lambda_, blocks, alpha)
        return model

    def predict(self, model):
        predictions = model.predictAll(self.test_data)\
            .map(lambda r: ((r[0], int(r[1])/10), (int(r[1])%10, r[2]))).reduceByKey(lambda a, b: a if a[1] > b[1] else b)

        predictions.saveAsTextFile("file:" + self.file_path)
        return predictions

    def evaluate(self, pred, output):
        # Evaluate the model on training data
        #testdata = ratings.map(lambda p: (int(p[0]), int(p[1])))

        real_pred = self.real_data.join(pred)

        real_pred.saveAsTextFile("file:" + self.file_path)

        eval_by_option = real_pred.map(lambda row: (row[0][1], row[1][0] == row[1][1][0])).reduceByKey(add)

        eval_by_cid = real_pred.map(lambda row: (row[0][0], row[1][0] == row[1][1][0]))\
            .reduceByKey(mul)

        correct = eval_by_cid.map(lambda row: row[1]).reduce(add)
        total = self.real_data.count()/7
        # evaluate accuracy for each option [A:G]

        for e in eval_by_option.collect():
            output.write(chr(ord('A') + int(e[0]) - 1) + " total: {0}, correct: {1}, accuracy: {2}%\n".format(total, e[1], e[1]*100.0/total))

        output.write("Aggregate all options: total: {0}, correct: {1}, accuracy: {2}%\n".format(total, correct, correct*100.0/total))

