#read data from csv file
# data = readFromCsv("train.csv")
readFromCsv = function(csv) {
  return(read.csv(file=csv, sep=",", head=TRUE))
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
  
  #Duration_previous (replace missing value by mean value)
  newData$duration_previous[is.na(newData$duration_previous)] =  
    mean(newData$duration_previous[!is.na(newData$duration_previous)])
  
  return(newData)
}

preprocess = function(csv="train.csv", resultCsv = "preprocessed.csv") {
  data = readFromCsv(csv);
  
  data = getPurchaseRecord(data);
  
  data = removeColumns(data, c("shopping_pt", "record_type", "day", "time", "state", "location", "C_previous","cost"));
  
  data = combineColumns(data, c("age_oldest","age_youngest"), "age_average", TRUE)
  
  data = removeOutlier(data)
  
  data = handleMissingValue(data)
  
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