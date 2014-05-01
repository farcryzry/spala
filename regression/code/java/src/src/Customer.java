import java.util.Arrays;
import java.util.List;

/**
 * Contains all customer related information: customerId + 12 profile attributes.
 */
public class Customer {
  private static List<String> allStates = Arrays.asList("AL","AR","CO","CT","DC","DE","FL","GA",
      "IA","ID","IN","KS","KY","MD","ME","MO","MS","MT","ND","NE","NH","NM","NV","NY","OH","OK",
      "OR","PA","RI","SD","TN","UT","WA","WI","WV","WY");
  private  String customerId;
  private  String state;
  private  String location;
  private  Integer groupSize;
  private  Boolean homeOwner;
  private  Integer carAge;
  private  Integer carValue;
  private  Integer riskFactor;
  private  Integer ageOldest;
  private  Integer ageYoungest;
  private  Boolean marriedCouple;
  private  Integer cPrevious;  // Integer or String
  private  Integer durationPrevious;

  public Customer(String customerId, String state, String location, Integer groupSize,
      Boolean homeOwner, Integer carAge, Integer carValue, Integer riskFactor, Integer ageOldest,
      Integer ageYoungest, Boolean marriedCouple, Integer cPrevious, Integer durationPrevious) {
    this.customerId = customerId;
    this.state = state;
    this.location = location ;
    this.groupSize = groupSize;
    this.homeOwner = homeOwner;
    this.carAge = carAge;
    this.carValue = carValue;
    this.riskFactor = riskFactor;
    this.ageOldest = ageOldest;
    this.ageYoungest = ageYoungest;
    this.marriedCouple = marriedCouple;
    this.cPrevious = cPrevious;
    this.durationPrevious = durationPrevious;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getGroupSize() {
    return groupSize;
  }

  public void setGroupSize(Integer groupSize){
    this.groupSize = groupSize;
  }

  public Boolean isHomeOwner() {
    return this.homeOwner;
  }

  public void setHomeOwner(Boolean homeOwner) {
    this.homeOwner = homeOwner;
  }

  public Integer getCarAge() {
    return this.carAge;
  }

  public void setCarAge(Integer carAge) {
    this.carAge = carAge;
  }

  public Integer getCarValue() {
    return this.carValue;
  }

  public void setCarValue(Integer carValue) {
    this.carValue = carValue;
  }

  public Integer getRiskFactor() {
    return this.riskFactor;
  }

  public void setRiskFactor(Integer riskFactor) {
    this.riskFactor = riskFactor;
  }

  public Integer getAgeOldest() {
    return this.ageOldest;
  }

  public void setAgeOldest(Integer ageOldest) {
    this.ageOldest = ageOldest;
  }

  public Integer getAgeYoungest() {
    return this.ageYoungest;
  }

  public void setAgeYoungest(Integer ageYoungest ){
    this.ageYoungest = ageYoungest;
  }

  public Boolean getMarriedCouple() {
    return this.marriedCouple;
  }

  public void setMarriedCouple(Boolean marriedCouple) {
    this.marriedCouple = marriedCouple;
  }

  public Integer getCPrevious() {
    return this.cPrevious;
  }

  public void setCPrevious(Integer cPrevious ) {
    this.cPrevious =  cPrevious;
  }

  public Integer getDurationPrevious() {
    return this.durationPrevious;
  }

  public void setDurationPrevios(Integer durationPrevious ) {
    this.durationPrevious = durationPrevious;
  }

  static public String header() {
    return "customerId,state,location,groupSize,homeOwner,carAge,carValue,riskFactor,ageOldest," +
        "ageYoungest,marriedCouple,cPrevious,durationPrevious";
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    //s.append(customerId);
    //s.append(",");
    //s.append(state);
    //s.append(",");
    //s.append(location);
    //s.append(",");

    // state
    for (int i = 0; i < allStates.size(); i++) {
      if (allStates.get(i).equals(state)) {
        s.append("1");
      } else {
        s.append("0");
      }
      s.append(" ");
    }

    double normalizedGroupSize = groupSize / 4.0;
    s.append(normalizedGroupSize);
    s.append(" ");
    //s.append(normalizedGroupSize * normalizedGroupSize);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedGroupSize));
    //s.append(" ");

    s.append(homeOwner ? "1" : "0");
    s.append(" ");

    double normalizedCarAge = (carAge + 1) / 50.0;
    s.append(normalizedCarAge);
    s.append(" ");
    //s.append(normalizedCarAge * normalizedCarAge);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedCarAge));
    //s.append(" ");

    double normalizedCarValue = (carValue + 1) / 10.0;
    s.append(normalizedCarValue);
    s.append(" ");
    //s.append(normalizedCarValue * normalizedCarValue);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedCarValue));
    //s.append(" ");

    double normalizedRiskFactor = riskFactor / 4.0;
    s.append(normalizedRiskFactor);
    s.append(" ");
    //s.append(normalizedRiskFactor * normalizedRiskFactor);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedRiskFactor));
    //s.append(" ");

    double normalizedAgeOldest = (ageOldest - 15) / 70.0;
    s.append(normalizedAgeOldest);
    s.append(" ");
    //s.append(normalizedAgeOldest * normalizedAgeOldest);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedAgeOldest));
    //s.append(" ");

    double normalizedAgeYoungest = (ageYoungest - 15) / 70.0;
    s.append(normalizedAgeYoungest);
    s.append(" ");
    //s.append(normalizedAgeYoungest * normalizedAgeYoungest);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedAgeYoungest));
    //s.append(" ");

    s.append(marriedCouple ? "1" : "0");
    s.append(" ");
    s.append(cPrevious / 5.0);
    s.append(" ");

    double normalizedDurationPrevious = durationPrevious / 20.0;
    s.append(normalizedDurationPrevious);
    //s.append(" ");
    //s.append(normalizedDurationPrevious * normalizedDurationPrevious);
    //s.append(" ");
    //s.append(Math.sqrt(normalizedDurationPrevious));

    return s.toString();
  }
}
