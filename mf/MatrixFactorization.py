__author__ = 'lan'
from pyspark import SparkContext, SparkConf
from pyspark.mllib.recommendation import ALS
from numpy import array
from operator import add, mul


class mf:
    def __init__(self, file_path):
        self.file_path = file_path
        config = SparkConf().setMaster("local").setAppName("Kaggle")\
            .set("spark.executor.memory", "1g")\
            .set("spark.storage.memoryFraction", "1")
        self.sc = SparkContext(conf=config)

    def train(self, file_name, rank=3, iterations=20, lambda_=0.01, blocks=-1, alpha=0.01):

        data = self.sc.textFile(self.file_path + file_name).cache()
        ratings = data.map(lambda line: array([float(x) for x in line.split(',')]))
        model = ALS.trainImplicit(ratings, rank, iterations, lambda_, blocks, alpha)
        data.unpersist()
        return model


    def evaluate(self, model, real_file, test_file):
        # Evaluate the model on training data
        #testdata = ratings.map(lambda p: (int(p[0]), int(p[1])))

        test_data = self.sc.textFile(self.file_path + test_file).cache()\
            .map(lambda line: [float(x) for x in line.split(',')])
        print test_data.first()

        predictions = model.predictAll(test_data).map(lambda r: ((r[0], int(r[1])/10), (int(r[1])%10, r[2])))\
            .reduceByKey(lambda a, b: a if a[1] > b[1] else b, 50)
        test_data.unpersist()
        print predictions.first()

        real_data = self.sc.textFile(self.file_path + real_file).cache()\
            .map(lambda line: [float(x) for x in line.split(',')]).map(lambda r: ((r[0], r[1]), r[2]))
        print real_data.first()

        reals_preds = real_data.join(predictions)
        print reals_preds.first()

        reals_preds.saveAsTextFile(self.file_path)

        eval_by_Option = reals_preds.map(lambda row: (row[0][1], row[1][0] == row[1][1][0])).reduceByKey(add, 5)
        print eval_by_Option.first()

        total = real_data.count()/7
        # evaluate accuracy for each option [A:G]
        for e in eval_by_Option.collect():
            print chr(ord('A') + e[0] - 1)
            print "total: {0}, correct: {1}, accuracy: {2}%".format(total, e[1], e[1]*100.0/total)


        eval_by_cid = reals_preds.map(lambda row: (row[0][0], row[1][0] == row[1][1][0]))\
            .reduceByKey(mul,10)
        print eval_by_cid.first()

        correct = eval_by_cid.map(lambda row: row[1]).reduce(add)
        print "Aggregate all options:"
        print "total: {0}, correct: {1}, accuracy: {2}%".format(total, correct, correct*100.0/total)


nmf = mf("file:/Users/lan/documents/sjsu/239/project/")
model = nmf.train("train_data.csv")
nmf.evaluate(model, "purchase_data.csv", "test_data.csv")
print "end"
