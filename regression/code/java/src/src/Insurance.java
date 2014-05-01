import com.google.common.base.Objects;

/**
 * Contains all 7 insurance options information.
 */
public class Insurance {
  private Integer optionA;  // 0, 1, 2
  private Integer optionB;  // 0, 2
  private Integer optionC;  // 1, 2, 3, 4
  private Integer optionD;  // 1, 2, 3
  private Integer optionE;  // 0, 1
  private Integer optionF;  // 0, 1, 2, 3
  private Integer optionG;  // 1, 2, 3, 4

  public Insurance(Integer optionA, Integer optionB, Integer optionC, Integer optionD,
      Integer optionE, Integer optionF, Integer optionG) {
    this.optionA = optionA;
    this.optionB = optionB;
    this.optionC = optionC;
    this.optionD = optionD;
    this.optionE = optionE;
    this.optionF = optionF;
    this.optionG = optionG;
  }

  public Integer getOptionA() {
    return this.optionA;
  }

  public void setOptionA(Integer optionA) {
    this.optionA = optionA;
  }

  public Integer getOptionB() {
    return this.optionB;
  }

  public void setOptionB(Integer optionB) {
    this.optionB = optionB;
  }

  public Integer getOptionC() {
    return this.optionC;
  }

  public void setOptionC(Integer optionC) {
    this.optionC = optionC;
  }

  public Integer getOptionD() {
    return this.optionD;
  }

  public void setOptionD(Integer optionD) {
    this.optionD = optionD;
  }

  public Integer getOptionE() {
    return this.optionE;
  }

  public void setOptionE(Integer optionE) {
    this.optionE = optionE;
  }

  public Integer getOptionF() {
    return this.optionF;
  }

  public void setOptionF(Integer optionF) {
    this.optionF = optionF;
  }

  public Integer getOptionG() {
    return this.optionG;
  }

  public void setOptionG(Integer optionG) {
    this.optionG = optionG;
  }

  static public String header() {
    //return "insurance";
    return "A,B,C,D,E,F,G";
  }

  public String toString2() {
    StringBuffer s = new StringBuffer();
    s.append(optionA);
    s.append(optionB);
    s.append(optionC);
    s.append(optionD);
    s.append(optionE);
    s.append(optionF);
    s.append(optionG);

    return s.toString();
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append((optionA + 1) / 4.0 + " ");
    s.append((optionB + 1) / 3.0 + " ");
    s.append((optionC + 1) / 6.0 + " ");
    s.append(optionD / 4.0 + " ");
    s.append((optionE + 1) / 3.0 + " ");
    s.append((optionF + 1) / 5.0 + " ");
    s.append(optionG / 5.0);

    return s.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Insurance) {
      Insurance that = (Insurance) obj;
      return this.optionA.equals(that.optionA)
          && this.optionB.equals(that.optionB)
          && this.optionC.equals(that.optionC)
          && this.optionD.equals(that.optionD)
          && this.optionE.equals(that.optionE)
          && this.optionF.equals(that.optionF)
          && this.optionG.equals(that.optionG);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(optionA, optionB, optionC, optionD, optionE, optionF, optionG);
  }
}
