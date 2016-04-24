import java.util.Vector;

public class TestVector {
  public static void main(String args[]) {
    Vector v1 = new Vector();
    v1.add("A");
    v1.add("B");
    v1.add("C");
    Object[] array = v1.toArray();

    for (int i = 0; i < array.length; i++) {
      System.out.println(array[i]);
    }
  }
}