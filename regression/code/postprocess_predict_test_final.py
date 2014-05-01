"""Merge customer/insurance data and probability data to select one insurance
for each customer.

Usage:
  python predict_test_final.py test_customer_insurances.data test_predict_prob.txt
"""

import sys

def main():
  customer_insurances = []

  # Load customer + insurance options
  for line in open(sys.argv[1], 'rb'):
    line = line.strip()
    customer_insurances.append(line.split(";"))

  # Load probability
  count = 0
  for line in open(sys.argv[2], 'rb'):
    line = line.strip()
    customer_insurances[count].append(float(line))
    count += 1

  last_customer_id = ''
  best_prediction = ['', '', 0]
  for one_prediction in customer_insurances:
    if one_prediction[0] != last_customer_id:
      if last_customer_id != '':
        print best_prediction[0] + ',' + best_prediction[1]
      best_prediction = one_prediction
    else:
      if one_prediction[2] > best_prediction[2]:
        best_prediction = one_prediction
    last_customer_id = one_prediction[0]

  # Last customer
  print best_prediction[0] + ',' + best_prediction[1]


if __name__ == '__main__':
  main()
