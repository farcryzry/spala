import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Preprocess...
 */
public class Preprocessor {
  private List<Quote> quotes = Lists.newArrayList();
  private List<Quote> cleanedQuotes = Lists.newArrayList();

  /**
   * Process train data.
   * Input a CSV data file. Output another CSV data file.
   */
  public void processTrainData(String inputDataFileName, String outputDataFileName) {
    // Step 1: Load input data to a list.
    try {
      loadData(inputDataFileName);
    } catch (IOException e) {
      System.out.format("Error when loading file: %s", inputDataFileName);
      return;
    }

    // Step 2: preprocess
    //cleanData();
    cleanData2();
    //cleanData3();

    // Step 3: output
    try {
      outputData(outputDataFileName);
    } catch (IOException e) {
      System.out.format("Error when writing file: %s", outputDataFileName);
      return;
    }
  }

  private void loadData(String inputDataFileName) throws IOException {
    System.out.format("Loading input file: %s\n", inputDataFileName);

    BufferedReader in = new BufferedReader(new FileReader(inputDataFileName));
    String s;
    int lineCount = 0;
    while ((s = in.readLine()) != null) {
      lineCount++;
      // Skip the first line, which is CSV header.
      if (lineCount == 1) {
        continue;
      }

      List<String> fields = Arrays.asList(s.split(","));
      if (fields.size() != 25) {
        System.out.format("Field number isn't 25: %s\n", s);
        continue;
      }
      quotes.add(new Quote(fields));
    }
    in.close();

    System.out.format("Totally loaded %d records.\n", lineCount - 1);
  }

  // Apply simple approach.
  // (1) Only keep the last purchase record for each customer.
  // (2) Fill empty value with average value.
  //     There are three fields may have empty value:
  //       carValue, riskFactor, durationPrevious
  private void cleanData() {
    // Filter non-purchased record
    for (Quote quote : quotes) {
      //if (quote.isPurchased()) {
        cleanedQuotes.add(quote);
      //}
    }

    System.out.format("Total %d unique customers\n", cleanedQuotes.size());

    // Calculate carValue, riskFactor, durationPrevious average.
    Integer carValueSum = 0;
    Integer carValueCount = 0;
    Integer riskFactorSum = 0;
    Integer riskFactorCount = 0;
    Integer durationPreviousSum = 0;
    Integer durationPreviousCount = 0;
    for (Quote quote : cleanedQuotes) {
      Customer customer = quote.getCustomer();
      if (customer.getCarValue() != null) {
        carValueSum += customer.getCarValue();
        carValueCount ++;
      }
      if (customer.getRiskFactor() != null) {
        riskFactorSum += customer.getRiskFactor();
        riskFactorCount ++;
      }
      if (customer.getDurationPrevious() != null) {
        durationPreviousSum += customer.getDurationPrevious();
        durationPreviousCount ++;
      }
    }

    Integer carValueAvg = carValueSum / carValueCount;
    Integer riskFactorAvg = riskFactorSum / riskFactorCount;
    Integer durationPreviousAvg = durationPreviousSum / durationPreviousCount;

    System.out.format("carValueSum=%d, carValueCount=%d, carValueAvg=%d\n", carValueSum,
      carValueCount, carValueAvg);
    System.out.format("riskFactorSum=%d, riskFactorCount=%d, riskFactorAvg=%d\n",
      riskFactorSum, riskFactorCount, riskFactorAvg);
    System.out.format("durationPreviousSum=%d, durationPreviousCount=%d, durationPreviousAvg=%d\n",
      durationPreviousSum, durationPreviousCount, durationPreviousAvg);

    // Fill all empty fields.
    for (Quote quote : cleanedQuotes) {
      Customer customer = quote.getCustomer();

      if (customer.getCarValue() == null) {
        customer.setCarValue(carValueAvg);
      }
      if (customer.getRiskFactor() == null) {
        customer.setRiskFactor(riskFactorAvg);
      }
      if (customer.getDurationPrevious() == null) {
        customer.setDurationPrevios(durationPreviousAvg);
      }
    }
  }

  private List<Quote> consolidateQuotes(List<Quote> quotes) {
    List<Quote> uniqueCustomerInsurance = Lists.newArrayList();
    int len = quotes.size();

    // if there is no non-purchased quote record, return empty list.
    //if (len < 2) {
    //  return uniqueCustomerInsurance;
    //}

    // Customer profile in the purchased record is the most trustable.
    Customer customer = quotes.get(len - 1).getCustomer();

    // Find unique insurance, and count number
    Multiset<Insurance> uniqueInsurances = LinkedHashMultiset.create();
    for (Quote quote : quotes) {
      uniqueInsurances.add(quote.getInsurance());
    }

    Boolean isLastPurchased = quotes.get(len - 1).isPurchased();
    Insurance purchasedInsurance = isLastPurchased ? quotes.get(len - 1).getInsurance() : null;
    Insurance lastQuoteInsurance = quotes.get(len - (isLastPurchased ? 2 : 1)).getInsurance();
    for (Multiset.Entry<Insurance> entry : uniqueInsurances.entrySet()) {
      Insurance insurance = entry.getElement();
      Boolean isLastQuote = insurance.equals(lastQuoteInsurance);
      Boolean purchased = insurance.equals(purchasedInsurance);
      int count = entry.getCount();
      // Remove the purchased record count.
      if (purchased) {
        count--;
      }

      uniqueCustomerInsurance.add(new Quote(customer, insurance, purchased, isLastQuote, count));
    }

    return uniqueCustomerInsurance;
  }

  // Clean data for purchase prediction training.
  private void cleanData2() {
    List<Quote> quotesForOneCustomer = Lists.newArrayList();
    for (Quote quote : quotes) {
      quotesForOneCustomer.add(quote);
      if (quote.isPurchased()) {
        cleanedQuotes.addAll(consolidateQuotes(quotesForOneCustomer));
        quotesForOneCustomer.clear();
      }
    }

    System.out.format("Total %d unique customer + insurance\n", cleanedQuotes.size());

    // Calculate carValue, riskFactor, durationPrevious average.
    Integer carValueSum = 0;
    Integer carValueCount = 0;
    Integer riskFactorSum = 0;
    Integer riskFactorCount = 0;
    Integer durationPreviousSum = 0;
    Integer durationPreviousCount = 0;
    for (Quote quote : cleanedQuotes) {
      Customer customer = quote.getCustomer();
      if (customer.getCarValue() != null) {
        carValueSum += customer.getCarValue();
        carValueCount ++;
      }
      if (customer.getRiskFactor() != null) {
        riskFactorSum += customer.getRiskFactor();
        riskFactorCount ++;
      }
      if (customer.getDurationPrevious() != null) {
        durationPreviousSum += customer.getDurationPrevious();
        durationPreviousCount ++;
      }
    }

    Integer carValueAvg = carValueSum / carValueCount;
    Integer riskFactorAvg = riskFactorSum / riskFactorCount;
    Integer durationPreviousAvg = durationPreviousSum / durationPreviousCount;

    System.out.format("carValueSum=%d, carValueCount=%d, carValueAvg=%d\n", carValueSum,
      carValueCount, carValueAvg);
    System.out.format("riskFactorSum=%d, riskFactorCount=%d, riskFactorAvg=%d\n",
      riskFactorSum, riskFactorCount, riskFactorAvg);
    System.out.format("durationPreviousSum=%d, durationPreviousCount=%d, durationPreviousAvg=%d\n",
      durationPreviousSum, durationPreviousCount, durationPreviousAvg);

    // Fill all empty fields.
    for (Quote quote : cleanedQuotes) {
      Customer customer = quote.getCustomer();

      if (customer.getCarValue() == null) {
        customer.setCarValue(carValueAvg);
      }
      if (customer.getRiskFactor() == null) {
        customer.setRiskFactor(riskFactorAvg);
      }
      if (customer.getDurationPrevious() == null) {
        customer.setDurationPrevios(durationPreviousAvg);
      }
    }
  }

  // Clean data for purchase prediction testing.
  private void cleanData3() {
    List<Quote> quotesForOneCustomer = Lists.newArrayList();
    String lastCustomerId = null;
    for (Quote quote : quotes) {
      String currentCustomerId = quote.getCustomer().getCustomerId();
      if (!currentCustomerId.equals(lastCustomerId)) {
        if (!quotesForOneCustomer.isEmpty()) {
          cleanedQuotes.addAll(consolidateQuotes(quotesForOneCustomer));
        }
        quotesForOneCustomer.clear();
      }
      quotesForOneCustomer.add(quote);
      lastCustomerId = currentCustomerId;
    }
    // Last customer
    cleanedQuotes.addAll(consolidateQuotes(quotesForOneCustomer));

    System.out.format("Total %d unique customer + insurance\n", cleanedQuotes.size());

    // These are calculated from training.
    Integer carValueAvg = 4;
    Integer riskFactorAvg = 2;
    Integer durationPreviousAvg = 5;

    // Fill all empty fields.
    for (Quote quote : cleanedQuotes) {
      Customer customer = quote.getCustomer();

      if (customer.getCarValue() == null) {
        customer.setCarValue(carValueAvg);
      }
      if (customer.getRiskFactor() == null) {
        customer.setRiskFactor(riskFactorAvg);
      }
      if (customer.getDurationPrevious() == null) {
        customer.setDurationPrevios(durationPreviousAvg);
      }
    }
  }

  private void outputData(String outputFileName) throws IOException {
    StringBuffer s = new StringBuffer();
    //s.append(Quote.header());

    for (Quote quote : cleanedQuotes) {
      s.append("\n");
      s.append(quote.toString2());
    }

    BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
    bw.write(s.toString());
    bw.close();
  }

  // args[0]: csv file containing input training/testing data
  // args[1]: plain text file containing processed data
  public static void main(String[] args) {
    Preprocessor preprocessor = new Preprocessor();
    preprocessor.processTrainData(args[0], args[1]);
  }
}
