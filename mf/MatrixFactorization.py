__author__ = 'lan'
from pyspark import SparkContext, SparkConf
from pyspark.mllib.recommendation import ALS
from numpy import array
from operator import add, mul


class mf:
    def __init__(self, file_path, train_file, test_file, real_file=None):
        """
        file_path: the folder where data files reside
        train_file: (user, item, rating) quote records
        test_file: (user, item) records, preferences to be predicted
        real_file: (user, option, value) real purchase records, can be none if it doesn't exist
        For this specific project:
        item here is the combination of options with their values,
            e.g. item 10 denotes option A with choice 0; item 21 denotes option B with choice 1
        rating is the number of quotes for a certain item by a user
        """
        self.file_path = file_path
        config = SparkConf().setMaster("local").setAppName("Kaggle")\
            .set("spark.executor.memory", "2g")\
            .set("spark.storage.memoryFraction", "1")

        sc = SparkContext(conf=config)

        self.train_data = sc.textFile("file:" + self.file_path + train_file).cache()\
            .map(lambda line: array([float(x) for x in line.split(',')]))

        self.test_data = sc.textFile("file:" + self.file_path + test_file).cache()\
            .map(lambda line: [float(x) for x in line.split(',')])

        if real_file:
            self.real_data = sc.textFile("file:" + self.file_path + real_file).cache()\
                .map(lambda line: [float(x) for x in line.split(',')]).map(lambda r: ((r[0], r[1]), r[2]))

    def train(self, rank=3, iterations=20, lambda_=0.01, alpha=None, blocks=-1):
        """
        train a mf model against the given parameters
        """
        if alpha:
            model = ALS.trainImplicit(self.train_data, rank, iterations, lambda_, blocks, alpha)
        else:
            model = ALS.train(self.train_data, rank, iterations, lambda_)

        return model

    def predict(self, model):
        """
        predict each customer's preference towards each item,
        and keep the most preferable choice for each option as the result
        """
        predictions = model.predictAll(self.test_data)\
            .map(lambda r: ((r[0], int(r[1])/10), (int(r[1])%10, r[2]))).reduceByKey(lambda a, b: a if a[1] > b[1] else b)

        return predictions

    def evaluate(self, pred, output):
        """
        evaluate the prediction against the purchase data from two perspectives:
        1: for each option A, B, ..., G, calculate the accuracy
        2: for each customer, evaluate the accuracy of the predicted (A,B,..,G) combination
        """
        real_pred = self.real_data.join(pred)
        eval_by_option = real_pred.map(lambda row: (row[0][1], row[1][0] == row[1][1][0])).reduceByKey(add)
        eval_by_cid = real_pred.map(lambda row: (row[0][0], row[1][0] == row[1][1][0]))\
            .reduceByKey(mul)

        correct = eval_by_cid.map(lambda row: row[1]).reduce(add)
        total = self.real_data.count()/7

        # evaluate accuracy for each option [A:G] and output total, correct and accuracy values
        for e in eval_by_option.collect():
            output.write(chr(ord('A') + int(e[0]) - 1) + ", {0}, {1}, {2}%\n".format(total, e[1], e[1]*100.0/total))

        output.write("Aggregate, {0}, {1}, {2}%\n".format(total, correct, correct*100.0/total))

    def output_predict(self, pred):
        """
        save to file in the sample format(cid, 1111111)
        """
        # map to (cid, (option, 'value'))
        outputs = pred.map(lambda r: (int(r[0][0]), (int(r[0][1]), str(int(r[1][0])))))
        # map to (cid, 'values')
        outputs = outputs.groupByKey().map(lambda x: (x[0], "".join(zip(*sorted(x[1], key=lambda pair: pair[0]))[1])))
        outputs.sortByKey().map(lambda x: ",".join([str(x[0]), x[1]])).saveAsTextFile("file:" + self.file_path)
