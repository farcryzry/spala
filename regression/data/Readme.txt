This fold contains almost all datasets created in the whole process, including intermedia dataset and target dataset.

I named folders with process-relavant. For example, the folder named as After_preprocess, 
that means all datasets which are output of preprocess are in this folder. 
This folder contains test dataset(after preprocess) and traniing dataset(after preprocess).


The prediction result in: After_prediction.

There is a special on in After_training folder: 
that file is the output of the Regression Model parameters.(Algorithm output after training);


I named all files with the step number.

Data exploration
0.train_small.data: data exploration training dataset;
0.validate_small.data:for evaluation algorithm after training the model on train_small.data;


Training dataset:
1.train_cleaned.spark.data: this is the cleaned and normalized training dataset;

After training the model parameter output;
2.model.output

Before prediction, after preprocess on test dataset:
3.1.test_cleaned.data: this one without customer ID;
3.2.test_customer_insurances.data: insurance and customer ID;

The prediction output
4.1test_predict_prob.withoutCustomerId.Data: intermedia dataset;
4.2.test_predict_final(4.1+3.2).withCustomer.Data: Final submission of prediction;