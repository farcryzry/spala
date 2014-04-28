__author__ = 'lan'
from pyspark import SparkContext, SparkConf
from operator import add


# def pre_map(x):
#     global header
#     record = x.split(',')
#     key = record[0]
#     value = record[1:]
#
#     return (key, value)
#
# def createListCombiner(x):
#     return map(lambda x:[x.encode('ascii','ignore')], x)
#
# def mergeListValue(xs, x):
#     xs[0][0] = max(int(xs[0][0]), int(x[0][0]))
#     for i in range(1, len(x), 1):
#         if x[i] not in xs[i]:
#             xs[i].append(x[i].encode('ascii','ignore'))
#     return xs
#
# def mergeListCombiners(a, b):
#     result = []
#     result.append([max(int(a[0][0]), int(b[0][0]))])
#     for i in range(1, len(a), 1):
#         set1 = set(a[i])
#         set2 = set(b[i])
#         result.append(list(set1.union(set2)))
#     return result

def flat(line):
    """
    convert record "cid, op1_value, op2_value..." to [((cid,11), 1), ((cid, 22), 1)...]
    """
    row = line.encode('ascii', 'ignore').split(',')
    result = []
    for i in range(1, len(row)):
        result.append((','.join((row[0], str(i)+row[i])), 1))
    return result

def map_to_kv(line):
    """
    transform record "cid, item, quote#" into ("cid, item", quote#)
    """
    row = line.split(',')
    return ",".join(row[:2]), int(row[2])


def map_add(row):
    """
    transform record ("cid, item", [v1, v2]) into ("cid, item, v")
    v1 could be None or an integer, v2 is integer
    """
    if row[1][0] is None:
        return ",".join([row[0], str(row[1][1])])
    else:
        return ",".join([row[0], str(sum(row[1]))])


def get_train_data(sc):
    """
    convert quote data to training data format(cid, item, quote#)
    """
    data = sc.textFile("file:/Users/lan/documents/sjsu/239/project/testQuote.csv").cache()
    train_data = data.flatMap(flat).reduceByKey(add)
    # print train_data.first()
    train_data.map(lambda row : ','.join((row[0],str(row[1]))))\
        .coalesce(1).saveAsTextFile("file:/Users/lan/documents/sjsu/239/dataset/")


def get_test_data(sc):
    """
    generate data input (cid, item) for prediction
    """
    item_list = sc.textFile("file:/Users/lan/documents/sjsu/239/project/ITEMS.csv").map(lambda line: line.encode('ascii', 'ignore').split(',')[0]).collect()
    sample = sc.textFile("file:/Users/lan/documents/sjsu/239/project/purchase.csv").cache().map(lambda line: line.split(',')[0])

    test_data = sample.flatMap(lambda x: [','.join([x, item]) for item in item_list])
    test_data.coalesce(1).saveAsTextFile("file:/Users/lan/documents/sjsu/239/dataset/")


def fill_missing_value(sc):
    """
    for items not quoted by a user, insert (cid, item, 0)
    """
    data1 = sc.textFile("file:/Users/lan/documents/sjsu/239/dataset/sample_train_data.csv")\
        .cache().map(map_to_kv)
    data2 = sc.textFile("file:/Users/lan/documents/sjsu/239/dataset/sample_test_data.csv")\
        .cache().map(lambda line: (line, 0))
    data1.rightOuterJoin(data2).map(map_add)\
        .coalesce(1).saveAsTextFile("file:/Users/lan/documents/sjsu/239/dataset/")


def get_eval_data(sc):
    """
    convert purchase date to (cid, option, value) format for evaluation purpose
    """
    purchase = sc.textFile("file:/Users/lan/documents/sjsu/239/project/purchase.csv").cache().map(lambda line: line.split(','))
    purchase.flatMap(lambda row: [(row[0], str(i), row[i]) for i in range(1, len(row))])\
        .map(lambda row : ','.join(row))\
        .coalesce(1).saveAsTextFile("file:/Users/lan/documents/sjsu/239/project/dataset")


config = SparkConf().setMaster("local").setAppName("Kaggle").set("spark.executor.memory", "2g")
sc = SparkContext(conf=config)
#get_test_data(sc)
fill_missing_value(sc)