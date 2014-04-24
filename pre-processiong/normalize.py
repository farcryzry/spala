__author__ = 'lan'
from pyspark import SparkContext, SparkConf


def min_max(row, selected, new_min, new_max, old_min, old_max):
    """
    Given a list of data, normalize the selected item to fit in the new range[new_min, new_max]
    """
    item = row[selected]
    old_range = float(old_max - old_min)
    new_range = new_max - new_min

    if item.isdigit():
        row[selected] = str((float(item) - old_min) * new_range / old_range + new_min)

    return row


def z_score(row, selected, mean, stddev):
    """
    Given a list of data, using z_score normalization: (x - mean)/stddev to normalize the selected item
    """
    item = row[selected]
    if item.isdigit():
        row[selected] = str((float(item) - mean) / stddev)

    return row


class Normalization:
    def __init__(self, file_path):
        config = SparkConf().setMaster("local").setAppName("Kaggle").set("spark.executor.memory", "2g")
        sc = SparkContext(conf=config)
        raw_data = sc.textFile(file_path).cache()
        self.raw_data = raw_data.map(lambda x: [y.encode('ascii', 'ignore') for y in x.split(',')])
        self.header_index = {k: v for v, k in enumerate(self.raw_data.first())}

    def get_nums(self, col):
        """
        retrieve all the numbers out of the given column
        """
        column = self.raw_data.map(lambda x: x[col])
        nums = column.filter(lambda x: x.isdigit()).map(lambda x: float(x))
        return nums

    def z_score_norm_col(self, feature):
        """
        given feature, using z_score to normalize the values of the feature
        """
        selected_col = self.header_index[feature]
        numbers = self.get_nums(selected_col)
        mean = numbers.mean()
        stddev = numbers.stdev()

        self.raw_data = self.raw_data.map(lambda x: z_score(x, selected_col, mean, stddev))
        print "z-score", feature

        return self

    def min_max_norm_col(self, feature, new_min, new_max, old_min=None, old_max=None):
        """
        given feature, using min_max to normalize the values of the feature
        """
        selected_col = self.header_index[feature]
        numbers = self.get_nums(selected_col).collect()
        if old_min is None:
            old_min = min(numbers)
        if old_max is None:
            old_max = max(numbers)

        self.raw_data = self.raw_data.map(lambda x: min_max(x, selected_col, new_min, new_max, old_min, old_max))
        print "min-max", feature

        return self

    def save_as_csv(self, file_path):
        """
        save all partitions into one file
        """
        self.raw_data.map(lambda x: ','.join(x)).coalesce(1).saveAsTextFile(file_path)



###tests
# file_path = "file:/Users/lan/documents/sjsu/239/project/testfile.csv"
# Normalization(file_path).z_score_norm_col("income").z_score_norm_col("car age").min_max_norm_col("age", 0, 1, 20, 60).save_as_csv("file:/Users/lan/documents/sjsu/239/project/")


file_path = "file:/Users/lan/documents/sjsu/239/project/train.csv"
Normalization(file_path) \
    .z_score_norm_col("age_oldest") \
    .z_score_norm_col("age_youngest") \
    .min_max_norm_col("group_size", 0, 1, 1) \
    .min_max_norm_col("car_age", 0, 1, 0) \
    .min_max_norm_col("duration_previous", 0, 1, 0) \
    .z_score_norm_col("cost") \
    .save_as_csv("file:/Users/lan/documents/sjsu/239/project/")

print "end"