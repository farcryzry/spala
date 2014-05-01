import org.apache.spark.mllib.regression.RidgeRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint

// Load and parse the data
val trainData = sc.textFile("../data/train_cleaned.spark.data")
val parsedTrainData = trainData.map { line =>
  val parts = line.split(',')
  LabeledPoint(parts(0).toDouble, parts(1).split(' ').map(x => x.toDouble).toArray)
}

// Building the model
val numIterations = 20
val model = RidgeRegressionWithSGD.train(parsedTrainData, numIterations)

// Evaluate model on training examples and compute training error
val valuesAndPreds = parsedTrainData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - (if (p>0.5) 1 else 0)), 2)}.reduce(_ + _)/valuesAndPreds.count  // 0.45 is better, MSE = 0.2439778514267238
println("training Mean Squared Error = " + MSE)

// Output the model
import java.io._

val pw = new PrintWriter(new File("../data/model.txt"))
for (w<-model.weights) pw.println(w)
pw.println(model.intercept)
pw.close
