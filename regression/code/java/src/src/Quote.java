import java.util.List;

/**
 * Contains information of one quote:
 *  - Customer profile
 *  - Insurance option
 *  - Shopping point
 *  - Purchase or not
 *  - Day of week
 *  - Time
 *  - Cost
 */
public class Quote {
  private Customer customer;
  private Insurance insurance;
  private Integer shoppingPoint;
  private Boolean purchased;
  private Integer dayOfWeek;
  private String time;
  private Integer cost;

  // derived variables for training
  private Boolean isLastQuote;
  private Integer quoteTimes;

  public Quote(Customer customer, Insurance insurance, Integer shoppingPoint, Boolean purchased,
      Integer dayOfWeek, String time, Integer cost) {
    this.customer = customer;
    this.insurance = insurance;
    this.shoppingPoint = shoppingPoint;
    this.purchased = purchased;
    this.dayOfWeek = dayOfWeek;
    this.time = time;
    this.cost = cost;
  }

  public Quote(Customer customer, Insurance insurance, Boolean purchased, Boolean isLastQuote,
      Integer quoteTimes) {
    this.customer = customer;
    this.insurance = insurance;
    this.purchased = purchased;
    this.isLastQuote = isLastQuote;
    this.quoteTimes = quoteTimes;
  }

  /**
   * Construct a quote from a string list, which contains fields:
   *   customer_ID, shopping_pt, record_type, day, time, state, location, group_size, homeowner,
   *   car_age, car_value, risk_factor, age_oldest, age_youngest, married_couple, C_previous,
   *   duration_previous, A, B, C, D, E, F, G, cost
   */
  public Quote(List<String> fields) {
    // First, parse all fields
    String customerId = fields.get(0);
    Integer shoppingPoint = Integer.parseInt(fields.get(1));
    Boolean purchased = fields.get(2).equals("1");
    Integer dayOfWeek = Integer.parseInt(fields.get(3));
    String time = fields.get(4);
    String state = fields.get(5);
    String location = fields.get(6);
    Integer groupSize = Integer.parseInt(fields.get(7));
    Boolean homeOwner = fields.get(8).equals("1");
    Integer carAge = Integer.parseInt(fields.get(9));
    // carValue might be empty
    Integer carValue = null;
    if (fields.get(10).length() > 0) {
      carValue = fields.get(10).toCharArray()[0] - 'a';
    }
    // riskFactor might be "NA".
    Integer riskFactor = null;
    if (!fields.get(11).equals("NA")) {
      riskFactor = Integer.parseInt(fields.get(11));
    }
    Integer ageOldest = Integer.parseInt(fields.get(12));
    Integer ageYoungest = Integer.parseInt(fields.get(13));
    Boolean marriedCouple = fields.get(14).equals("1");
    // cPrevious = 0 if input is "NA"
    Integer cPrevious = 1;  // 0;
    if (!fields.get(15).equals("NA")) {
      cPrevious = Integer.parseInt(fields.get(15));
    }
    // durationPrevious may be "NA"
    Integer durationPrevious = null;
    if (!fields.get(16).equals("NA")) {
      durationPrevious = Integer.parseInt(fields.get(16));
    }
    Integer optionA = Integer.parseInt(fields.get(17));
    Integer optionB = Integer.parseInt(fields.get(18));
    Integer optionC = Integer.parseInt(fields.get(19));
    Integer optionD = Integer.parseInt(fields.get(20));
    Integer optionE = Integer.parseInt(fields.get(21));
    Integer optionF = Integer.parseInt(fields.get(22));
    Integer optionG = Integer.parseInt(fields.get(23));
    Integer cost = Integer.parseInt(fields.get(24));

    customer = new Customer(customerId, state, location, groupSize, homeOwner, carAge, carValue,
      riskFactor, ageOldest, ageYoungest, marriedCouple, cPrevious, durationPrevious);
    insurance = new Insurance(optionA, optionB, optionC, optionD, optionE, optionF, optionG);
    this.shoppingPoint = shoppingPoint;
    this.purchased = purchased;
    this.dayOfWeek = dayOfWeek;
    this.time = time;
    this.cost = cost;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public Insurance getInsurance() {
    return insurance;
  }

  public void setInsurance(Insurance insurance) {
    this.insurance = insurance;
  }

  public Integer getShoppingPoint() {
    return shoppingPoint;
  }

  public void setShoppingPoint(Integer shoppingPoint) {
    this.shoppingPoint = shoppingPoint;
  }

  public Boolean isPurchased() {
    return purchased;
  }

  public void setPurchased(Boolean purchased) {
    this.purchased = purchased;
  }

  public Integer getDayOfWeek() {
    return dayOfWeek;
  }

  public void setDayOfWeek(Integer dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public Integer getCost() {
    return cost;
  }

  public void setCost(Integer cost) {
    this.cost = cost;
  }

  public Boolean isLastQuote() {
    return isLastQuote;
  }

  public void setIsLastQuote(Boolean isLastQuote) {
    this.isLastQuote = isLastQuote;
  }

  public Integer getQuoteTimes() {
    return quoteTimes;
  }

  public void setQuoteTimes(Integer quoteTimes) {
    this.quoteTimes = quoteTimes;
  }

  static public String header() {
    return Customer.header() + "," + Insurance.header() + ",cost";
  }

  @Override
  public String toString() {
    //return customer + "," + insurance + "," + cost;
    // Format for spark

    // this is for cost prediction.
    // return cost / 1000.0 + "," + customer + " " + insurance;

    // this is for purchase prediction training.
    return (purchased ? "1" : "0") + "," + (isLastQuote ? "1" : "0") + " " + quoteTimes + " "
        + customer + " " + insurance;
  }

  public String toString2() {
    // this is for purchase prediction test.
    return customer.getCustomerId() + ";" + insurance.toString2() + ";"
        + (purchased ? "1" : "0") + "," + (isLastQuote ? "1" : "0") + " " + quoteTimes + " "
        + customer + " " + insurance;
  }
}
