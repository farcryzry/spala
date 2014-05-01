#read data from csv file
# data = readFromCsv("train.csv")
readCsvToTable = function(csv) {
  return(read.table(file=csv, sep=',', header=TRUE, row.names=1))
}

readFromCsv = function(csv) {
  return(read.csv(file=csv, sep=',', header=TRUE))
}

#write data to csv file
# writeToCsv(data, "new_data.csv")
writeToCsv = function(data, csv) {
  write.table(data, csv, sep=",", quote=FALSE, row.names=FALSE, col.names=TRUE)
}


# show the columns which contain NA value
# listColumnsContainNA(data)
listColumnsContainNA = function(data) {
  columns = c()
  for(i in 1:ncol(data)) {
    if(any(is.na(data[,i])) | any(data[,i]=='')) {
      columns = append(columns,i)
      print(names(data)[i])
    }
  }
  return(columns)
}

#purchase records
getPurchaseRecord = function(data) {
  return(data[data$record_type==1, ])
}

#combine two columns to one
#combineColumns(data, c("age_oldest","age_youngest"), "age_average", true)
combineColumns = function(data, sCols, tCol, needRemoveSCols=FALSE) {
  if(length(which(names(data) %in% sCols)) != length(sCols)) {
    return(data)
  }
  if(any(which(names(data) == tCol))) {
    return(data)
  }
  
  newcol = numeric(nrow(data))
  
  for(col in sCols) {
    newcol = newcol + data[,col]
  }
  
  newcol = newcol / length(sCols)
  
  newData = cbind(data, newcol)
  
  names(newData)[ncol(newData)] = tCol
  
  if(needRemoveSCols) {
    newData = removeColumns(newData, sCols)
  }
  
  return(newData)
  
}

#remove columns shopping_pt/record_type/day/time/state/location/c_previous/cost
# removeColumns(data, c("shopping_pt", "record_type", "day", "time", "state", "location", "C_previous", "cost"))
removeColumns = function(data, cols) {
  if(!is.vector(cols) | length(cols) == 0) {
    return(data)
  }
  colIndexs = which(names(data) %in% cols)
  return(data[,-colIndexs])
}

# remove outlier car_age
removeOutlier = function(data) {
  bp <- boxplot(data$car_age, outline = FALSE)
  fence = min(bp$out)
  
  return(data[data$car_age<fence,])
}

#handle missing value Car_value / Risk_factor / C_previous / Duration_previous
handleMissingValue = function(data) {
  newData = data
  
  #risk_factor (replace missing value by 0)
  newData$risk_factor[is.na(newData$risk_factor)] = 0
  
  #car_value (replace missing value by median value)
  empty_records = which(newData$car_value == '')
  for(i in 1:length(empty_records)) {
    newData[empty_records[i],"car_value"] = 'e'
  }
  
  #Duration_previous (replace missing value by mean value)
  newData$duration_previous[is.na(newData$duration_previous)] =  
    mean(newData$duration_previous[!is.na(newData$duration_previous)])
  
  return(newData)
}

replaceCharWithNumber = function(data) {
  newData = within(data, car_value <- factor(car_value, labels = c(1,2,3,4,5,6,7,8,9)))
  
  newData = within(newData, A <- factor(A, labels = c(0:2)))
  newData = within(newData, B <- factor(B, labels = c(0:1)))
  newData = within(newData, C <- factor(C, labels = c(1:4)))
  newData = within(newData, D <- factor(D, labels = c(1:3)))
  newData = within(newData, E <- factor(E, labels = c(0:1)))
  newData = within(newData, F <- factor(F, labels = c(0:3)))
  newData = within(newData, G <- factor(G, labels = c(1:4)))
  return(newData)
}

replaceNumberWithChar = function(data) {
  newData = within(data, A <- factor(A, labels = c('A','B','C')))
  newData = within(newData, B <- factor(B, labels = c('A','B')))
  newData = within(newData, C <- factor(C, labels = c('B','C','D','E')))
  newData = within(newData, D <- factor(D, labels = c('B','C','D')))
  newData = within(newData, E <- factor(E, labels = c('A','B')))
  newData = within(newData, F <- factor(F, labels = c('A','B','C','D')))
  newData = within(newData, G <- factor(G, labels = c('B','C','D','E')))
  return(newData)
}

removeDuplication = function(data) {
  return(subset(data, !duplicated(data[,1])))
}

preprocess = function(csv="train.csv", resultCsv = "preprocessed.csv") {
  data = readFromCsv(csv);
  
  data = getPurchaseRecord(data);
  
  data = removeColumns(data, c("shopping_pt", "record_type", "day", "time", "state", "location", "C_previous","cost"));
  
  data = combineColumns(data, c("age_oldest","age_youngest"), "age_average", TRUE)
  
  data = removeOutlier(data)
  
  data = handleMissingValue(data)
  
  data = replaceCharWithNumber(data)
  
  writeToCsv(data, resultCsv)
  
  return(data)
  
}

preprocess_test = function(csv="test_v2.csv", resultCsv = "test_preprocessed.csv") {
  data = readFromCsv(csv);
  
  data = removeColumns(data, c("shopping_pt", "record_type", "day", "time", "state", "location", "C_previous","cost"));
  
  data = combineColumns(data, c("age_oldest","age_youngest"), "age_average", TRUE)
  
  data = handleMissingValue(data)
  
  data = replaceCharWithNumber(data)
  
  data = replaceNumberWithChar(data)
  
  data = removeDuplication(data)
  
  writeToCsv(data, resultCsv)
  
  return(data)
  
}

generateTrainingDataForEachOption = function() {
  data = preprocess();
  dataA = removeColumns(data, c("B","C","D","E","F","G"));
  dataB = removeColumns(data, c("A","C","D","E","F","G"));
  dataC = removeColumns(data, c("A","B","D","E","F","G"));
  dataD = removeColumns(data, c("A","B","C","E","F","G"));
  dataE = removeColumns(data, c("A","B","C","D","F","G"));
  dataF = removeColumns(data, c("A","B","C","D","E","G"));
  dataG = removeColumns(data, c("A","B","C","D","E","F"));
  writeToCsv(dataA, "Train_A.csv")
  writeToCsv(dataB, "Train_B.csv")
  writeToCsv(dataC, "Train_C.csv")
  writeToCsv(dataD, "Train_D.csv")
  writeToCsv(dataE, "Train_E.csv")
  writeToCsv(dataF, "Train_F.csv")
  writeToCsv(dataG, "Train_G.csv")
  return(data)
}

generateTestDataForEachOption = function() {
  data = preprocess_test();
  dataA = removeColumns(data, c("B","C","D","E","F","G"));
  dataB = removeColumns(data, c("A","C","D","E","F","G"));
  dataC = removeColumns(data, c("A","B","D","E","F","G"));
  dataD = removeColumns(data, c("A","B","C","E","F","G"));
  dataE = removeColumns(data, c("A","B","C","D","F","G"));
  dataF = removeColumns(data, c("A","B","C","D","E","G"));
  dataG = removeColumns(data, c("A","B","C","D","E","F"));
  writeToCsv(dataA, "test_A.csv")
  writeToCsv(dataB, "test_B.csv")
  writeToCsv(dataC, "test_C.csv")
  writeToCsv(dataD, "test_D.csv")
  writeToCsv(dataE, "test_E.csv")
  writeToCsv(dataF, "test_F.csv")
  writeToCsv(dataG, "test_G.csv")
  return(data)
}

randomRows = function(data, n){
  return(data[sample(nrow(data),n),])
}

getLastQuotes = function() {
  
}

data_test = function() {

  data = readFromCsv("train.csv")
  
  data1 = data[,-c(4:17)]
  
  quoterecords = data1[data1$record_type!='1',]
  
  purchasedrecords = data1[data1$record_type=='1',]
  
  maxquotes = aggregate (as.numeric(quoterecords$shopping_pt), by=list(quoterecords$customer_ID), max)
  colnames(maxquotes) = c('customer_ID', 'max_shopping_pt')
  
  quoterecords = merge(quoterecords, maxquotes, all.x = TRUE)
  
  lastquoterecords = quoterecords[quoterecords$shopping_pt == quoterecords$max_shopping_pt,]
  quoterecords = quoterecords[,-c(12)]
  lastquoterecords = lastquoterecords[,-c(2,3,11)]
  purchasedrecords = purchasedrecords[,-c(2,3,11)]
  
  cors = c()
  
  for(i in 1:nrow(purchasedrecords)) {
    a = purchasedrecords[i,]
    a = as.vector(t(a))
    b = lastquoterecords[i,]
    b = as.vector(t(b))
    print(i)
    cors = append(cors, cor(a,b))
  }
  
  count = 0
  diffpurchaseuser = c()
  
  for(i in 2:nrow(data1)) {
    if(data1[i,]$record_type == '1') { 
      cor = cor(as.vector(t(data1[i, 4:10])), as.vector(t(data1[i-1, 4:10])))
      if( !isTRUE(all.equal(cor,1))) { 
        print(data1[i,]$customer_ID)
        diffpurchaseuser = append(diffpurchaseuser, data1[i,]$customer_ID)
        count = count + 1
      }
    }
  }
  
  print(count/nrow(data1))
  
  for(i in 1:nrow(lastquoterecords)) {
    if(is.na(lastquoterecords[i,]$C_previous)) { 
      lastquoterecords[i,]$C_previous = lastquoterecords[i,]$C
    }
  }
}
