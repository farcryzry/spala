library("ggplot2")
library("clue")
library("class")
source("DataHandler.R")

#preprocess("train_purchase_records.csv", "train_purchase_records_preprocessed.csv")

kkPredict = function(trainCsv, testCsv, predictedCsv, k1=20, k2=5) {
  train_purchase_records = readCsvToTable(trainCsv)
  
  data1 = train_purchase_records
  m = as.matrix(data1)
  data_test1 = data1[,-c(8:14)]
  data.p = as.matrix(data_test1)
  
  kdata <- scale(data.p)
  
  cl = kmeans(kdata, k1)
  
  train_purchase_records = readFromCsv(trainCsv)
  train_purchase_records_clustered = as.data.frame(cbind(train_purchase_records, "cluster"=cl$cluster))
  train_purchase_records_clustered = replaceNumberWithChar(train_purchase_records_clustered)
  
  #############################################################
  #kmeans prediction
  #############################################################
  
  testData = readCsvToTable(testCsv)
  
  m2 = as.matrix(testData)
  data_test2 = testData[,-c(8:14)]
  tdata <- scale(as.matrix(data_test2))
  
  p = cl_predict(cl, tdata)
  
  testData = readFromCsv(testCsv)

  result = as.data.frame(cbind(testData,"predicted_cluster"=as.vector(p)))
  
  #writeToCsv(result, predictedCsv)

  #return(result)
  
  #############################################################
  #knn prediction
  #############################################################

  
  clustered_results = train_purchase_records_clustered[,-c(17)]
  clustered_results = clustered_results[-c(1:nrow(clustered_results)),]
  
  for(i in 1:k1) {
  
    clustered_train = train_purchase_records_clustered[train_purchase_records_clustered$cluster==i,-c(17)]
    clustered_test = result[result$predicted_cluster==i,-c(9:15, 17)]
    
    train_input <- as.matrix(clustered_train[,-c(1,9:15)])
    test_input <- as.matrix(clustered_test[,-c(1)])
    
    train_output <- as.vector(clustered_train[,9])
    p.a = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,10])
    p.b = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,11])
    p.c = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,12])
    p.d = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,13])
    p.e = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,14])
    p.f = knnPredict(train_input, train_output, test_input, k2)
    
    train_output <- as.vector(clustered_train[,15])
    p.g = knnPredict(train_input, train_output, test_input, k2)
    
    predicted_options = data.frame(p.a, p.b, p.c, p.d, p.e, p.f, p.g)
    names(predicted_options) <- c("A", "B", "C", "D", "E", "F", "G")
    
    clustered_result = cbind(clustered_test, predicted_options)
    
    clustered_results = rbind(clustered_results, clustered_result)
  }
  
  return(clustered_results)
}

###################################################################

showWSSPlot = function(data, sample_size=5000, max_k = 100) {
  sample_data = randomRows(data, sample_size)
  wss <- (nrow(sample_data)-1)*sum(apply(sample_data,2,var))
  for (i in 2:max_k) {
    wss[i] <- sum(kmeans(sample_data, centers=i, iter.max = 100)$withinss)
    #wss[i] <- medim(kmeans(sample_data, centers=i)$withinss)
  }
  
  #plot(1:max_k, wss, type="b", xlab="Number of Clusters",ylab="Within groups sum of squares")

  ggplot(data.frame(k=1:100, wss=wss), aes(x=k, y=wss)) + geom_point(size=3,colour = "#3366FF") + geom_line(colour = "#3366FF") +
    labs(x = "Number of Clusters", y="Within groups sum of squares")
}

kmeans_evaluate = function(){

  showWSSPlot(kdata)
  
  
  critframe <- data.frame(k=1:100, wss=wss)
  ggplot(critframe, aes(x=k, y=wss)) + geom_point() + geom_line()
}

####################################################################


#
#knn
#
#
#

knn_data_prepare = function() {
  clustered_data = readFromCsv("train_purchase_records_clustered.csv")
  clustered_data = replaceNumberWithChar(clustered_data)
  
  sample_data_knn = clustered_data[clustered_data$cluster==1,]
  
  writeToCsv(sample_data_knn, "train_purchase_records_clustered_sample_knn.csv")
  
  test_data = preprocess_test("test_v2.csv", "test_preprocessed.csv")

}

knnPredict = function(train_input, train_output, test_input, k=5) {

  prediction <- knn(train_input, test_input, train_output, k)
  return(prediction)
}

predict = function(k1=20, k2=5) {

  test_data_predicted = kkPredict("train_purchase_records_preprocessed.csv", "test_preprocessed.csv", "test_predicted.csv", k1, k2)
  
  p = replaceCharWithNumber(test_data_predicted)
  p = p[order(p$customer_ID),-c(2:9)]
  
  options = do.call(paste, c(p[c(2:8)], sep = ''))
  
  results = cbind("customer_ID"=p$customer_ID, "plan"=options)
  
  writeToCsv(results, "kk_results.csv") 
}

validate = function(k1=20, k2=5, fold=10) {
  
  test_data_predicted = kkPredict("train_purchase_records_preprocessed.csv", "train_purchase_records_preprocessed.csv", "test_predicted.csv", k1, k2)
  
  p = replaceCharWithNumber(test_data_predicted)
  p = p[order(p$customer_ID),-c(2:9)]
  
  options = do.call(paste, c(p[c(2:8)], sep = ''))
  
  results = cbind("customer_ID"=p$customer_ID, "plan"=options)
  
  train_purchase_records = readCsvToTable("train_purchase_records_preprocessed.csv")
  
  options1 = do.call(paste, c(train_purchase_records[c(8:14)], sep = ''))
  results =  cbind(results, "predicted"=options1)
  
  correct_count = 0 
  for(i in 1:nrow(results)) {if(results[i,2] == results[i,3]) correct_count = correct_count + 1}
  
  accuracy = correct_count/nrow(results)
  
  print(c(k1,k2,accuracy))
  
  writeToCsv(results, "kk_validate_results.csv") 
  
  return(results)
}

#predict(k2=3)

validate(k2=1)
validate(k2=2)
validate(k2=3)
validate(k2=4)
