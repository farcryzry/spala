import org.apache.spark.mllib.regression.RidgeRegressionWithSGD
import org.apache.spark.mllib.regression.LabeledPoint

// Load and parse the data
val trainData = sc.textFile("../data/train_small.data")
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
val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - (if (p>0.5) 1 else 0)), 2)}.reduce(_ + _)/valuesAndPreds.count
println("training Mean Squared Error = " + MSE)

// Evaluate model on validating examples and compute error
val validateData = sc.textFile("../data/validate_small.data")
val parsedValidateData = validateData.map { line =>
  val parts = line.split(',')
  LabeledPoint(parts(0).toDouble, parts(1).split(' ').map(x => x.toDouble).toArray)
}

val valuesAndPreds = parsedValidateData.map { point =>
  val prediction = model.predict(point.features)
  (point.label, prediction)
}
val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - (if (p>0.5) 1 else 0)), 2)}.reduce(_ + _)/valuesAndPreds.count
println("training Mean Squared Error = " + MSE)
