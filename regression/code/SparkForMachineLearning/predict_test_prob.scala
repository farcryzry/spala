import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.mllib.regression.RidgeRegressionModel
import org.apache.spark.mllib.regression.LabeledPoint

// Load model parameters from a file
val model_parameters = ArrayBuffer[Double]()
for (line <- Source.fromFile("../data/model.txt").getLines) {
  model_parameters += line.toDouble
}

val model_weights = model_parameters.slice(0, model_parameters.size - 1).toArray
val model_intercept = model_parameters.apply(model_parameters.size - 1)

// Create a model
val model = new RidgeRegressionModel(model_weights, model_intercept)

// Load and parse the data
val testData = sc.textFile("../data/test_cleaned.data")
val parsedTestData = testData.map { line =>
  val parts = line.split(',')
  LabeledPoint(parts(0).toDouble, parts(1).split(' ').map(x => x.toDouble).toArray)
}

// Predict probability for test data
val valuesAndPreds = parsedTestData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}

// Output probability
import java.io._

val pw = new PrintWriter(new File("../data/test_predict_prob.txt"))
for (w<-valuesAndPreds.toArray) pw.println(w._2)
pw.close

