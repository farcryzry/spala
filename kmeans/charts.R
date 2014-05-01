library("ggplot2")

data = readFromCsv("train.csv")

purchase_records = getPurchaseRecord(data)


m1 = ggplot(purchase_records, aes(x=age_youngest))
m1 + geom_histogram(aes(fill = ..count..))

m2 = ggplot(purchase_records, aes(x=age_oldest))
m2 + geom_histogram(aes(fill = ..count..))

m3 = ggplot(purchase_records, aes(x=car_age))
m3 + geom_histogram(aes(fill = ..count..))

m4 = ggplot(purchase_records, aes(x=duration_previous ))
m4 + geom_histogram(aes(fill = ..count..))


p <- ggplot(group_size, car_age, data = purchase_records, geom = "boxplot")
p + geom_boxplot(outlier.colour = "red", fill = "grey80", colour = "#3366FF", outlier.size = 3, notch = TRUE, notchwidth = 2) 



#MF
#
#
mf_results = readFromCsv("mf_results.csv")

ggplot(mf_results, aes(x=rank, y=Aggregate, size = iterations, color = iterations)) + geom_point()

rank30 =  mf_results[mf_results$rank == 30,]
ggplot(rank30, aes(x=iterations, y=Aggregate, size = lambda_, color = lambda_)) + geom_point()


#LR
lr_results = readFromCsv("lr_results.csv")
ggplot(data=lr_results, aes(x=iterations, y=accuracy)) + geom_line(colour="#3366FF", linetype="dotted", size=1.5) + geom_point(colour="#3366FF", size=4, shape=21, fill="white")

