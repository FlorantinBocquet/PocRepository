package tree;

import java.util.Comparator;
import java.util.function.ToIntBiFunction;
import tree.data.DefaultStoredData;
import tree.data.IntStoredData;
import tree.nbinary.NBinaryTree;
import tree.nbinary.operation.Operation;

public class Test {
  public static void main(String[] args) {
    testSearchBinary();
  }

  private static void testSearchBinary() {
    final NBinaryTree<String> tree = NBinaryTree.builder()
        .addLayer(Comparator.comparing(String::toString))
        .addLayer(Comparator.comparing(Integer::intValue))
        .build();

    tree.add("Value 1", "Hello", 1);
    tree.add("Value 2", "Bye", 1);
    tree.add("Value 3", "Hello", 2);
    tree.add("Value 4", "Hello", 3);
    tree.add("Value 5", "Bye", 4);
    tree.add("Value 6", "Hello", 2);
    tree.add("Value 7", "Duck", -1);

    /* content of tree
      Bye
        1 -> Value 2
        4 -> Value 5
      Duck
        -1 -> Value 7
      Hello
        1 -> Value 1
        2 -> Value 3, Value 6
        3 -> Value 4
     */

    tree.rebalance();

    System.out.println(tree.search(Operation.equalz("Hello"), Operation.equalz(2)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.in(1, 2)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.in(1, 3)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.in(1, 10)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.superiorOrEquals(2)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.superiorStrict(2)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.inferiorOrEquals(2)));
    System.out.println(tree.search(Operation.equalz("Hello"), Operation.inferiorStrict(2)));
    System.out.println(tree.search(Operation.equalz("Not_a_key"), Operation.inferiorStrict(2)));
    System.out.println(tree.search(Operation.inferiorStrict("Hello"), Operation.inferiorStrict(2)));
    System.out.println(tree.search(Operation.in(), Operation.inferiorStrict(2)));
    System.out.println(tree.search(Operation.notEquals("Hello"), Operation.all()));
  }

  private static void testNBinaryWIthPlausibleData() {
    final NBinaryTree<String> nBinaryTree = NBinaryTree.builder()
        .addLayer(Comparator.comparing(Integer::intValue)) // buCode
        .addLayer(Comparator.comparing(Integer::intValue)) // storeCode
        .addLayer(Comparator.comparing(String::toString)) // addressId
        .build();

    nBinaryTree.add("11111111", 5, 9, "01-SFA-1-1-0-0");
  }

  private static void testNBinaryTree() {
    final NBinaryTree<String> nBinary = NBinaryTree.builder()
        .addLayer(Comparator.comparing(String::toString))
        .addLayer(Comparator.comparing(Integer::intValue))
        .addLayer(Comparator.comparing(String::toString))
        .build();

    nBinary.add("Value 1", "Key1", 1, "Key3");
    nBinary.add("Value 2", "Key1", 1, "Key3");
    nBinary.add("Value 3", "Key1", 1, "Key4");
    nBinary.add("Value 4", "Key1", 1, "Key5");
    nBinary.add("Value 5", "Key1", 1, "Key6");

    System.out.println(nBinary.get("Key1", 1, "Key3"));
    System.out.println(nBinary.get("Key1", 1, "Key4"));

    nBinary.rebalance();

    nBinary.add("Value 6", "Key2", 1, "Key3");
    nBinary.add("Value 7", "Key3", 1, "Key3");

    nBinary.rebalance();

    System.out.println(nBinary.get("Key1", 1, "Key3"));
    System.out.println(nBinary.get("Key1", 1, "Key4"));
  }

  private static void testNBinary2() {
    final NBinary2<String> nBinary = new NBinary2<>(
        Comparator.comparing(String::toString),
        Comparator.comparing(Integer::intValue),
        Comparator.comparing(String::toString)
    );

    nBinary.add("Value 1", "Key1", 1, "Key3");
    nBinary.add("Value 2", "Key1", 1, "Key3");
    nBinary.add("Value 3", "Key1", 1, "Key4");
    nBinary.add("Value 4", "Key1", 1, "Key5");
    nBinary.add("Value 5", "Key1", 1, "Key6");

    System.out.println(nBinary.get("Key1", 1, "Key3"));
    System.out.println(nBinary.get("Key1", 1, "Key4"));

    nBinary.rebalance();

    System.out.println("After rebalance:");
  }

  private static void testNBinary() {
    final NBinary<String, String> nBinary = new NBinary<>(String.class, String.class, String.class);

    nBinary.add("Value 1", "Key1", "Key2", "Key3");
    nBinary.add("Value 2", "Key1", "Key2", "Key3");
    nBinary.add("Value 3", "Key1", "Key2", "Key4");

    System.out.println(nBinary.get("Key1", "Key2", "Key3"));
    System.out.println(nBinary.get("Key1", "Key2", "Key4"));
  }

  private static void test2() {
    final Binary<Integer, Integer, IntStoredData<Integer>> binaryTree = new Binary<>(IntStoredData::new);

    for (int i = 1; i <= 360; i++) {
      binaryTree.addData(i, i);
    }

    System.out.println(binaryTree);

    binaryTree.balanceBinary();

    System.out.println();
    System.out.println(binaryTree);
  }

  private static void test1() {
    final Binary<String, String, DefaultStoredData<String, String>> binaryTree = Binary.binary();

    binaryTree.addData("005-007", "66666666");
    binaryTree.addData("005-008", "44444444");
    binaryTree.addData("005-009", "11111111");
    binaryTree.addData("005-010", "33333333");
    binaryTree.addData("005-014", "88888888");
    binaryTree.addData("005-015", "88888888");
    binaryTree.addData("005-016", "88888888");

    binaryTree.addData("005-007", "22222222");

    binaryTree.addData("005-020", "11111111");
    binaryTree.addData("005-021", "11111111");
    binaryTree.addData("005-022", "11111111");
    binaryTree.addData("005-023", "11111111");
    binaryTree.addData("005-024", "11111111");
    binaryTree.addData("005-025", "11111111");
    binaryTree.addData("005-026", "11111111");
    binaryTree.addData("005-027", "11111111");
    binaryTree.addData("005-028", "11111111");
    binaryTree.addData("005-029", "11111111");
    binaryTree.addData("005-030", "11111111");
    binaryTree.addData("005-031", "11111111");
    binaryTree.addData("005-032", "11111111");
    binaryTree.addData("005-033", "11111111");
    binaryTree.addData("005-034", "11111111");
    binaryTree.addData("005-035", "11111111");
    binaryTree.addData("005-036", "11111111");
    binaryTree.addData("005-037", "11111111");
    binaryTree.addData("005-038", "11111111");
    binaryTree.addData("005-039", "11111111");
    binaryTree.addData("005-040", "11111111");


    System.out.println("RESULT : Data for 005-007: " + binaryTree.get("005-007"));
    System.out.println("RESULT : Data for 005-008: " + binaryTree.get("005-008"));
    System.out.println("RESULT : Data for 005-009: " + binaryTree.get("005-009"));
    System.out.println("RESULT : Data for 005-010: " + binaryTree.get("005-010"));
    System.out.println("RESULT : Data for 005-014: " + binaryTree.get("005-014"));
    System.out.println("RESULT : Data for 005-015: " + binaryTree.get("005-015"));
    System.out.println("RESULT : Data for 005-016: " + binaryTree.get("005-016"));
    System.out.println();

    System.out.println("Binary Tree :");
    System.out.println(binaryTree);


    binaryTree.balanceBinary();

    System.out.println();
    System.out.println("Balanced Binary Tree :");
    System.out.println(binaryTree);

//    System.out.println();
//    System.out.println("Superior or equals to 005-010:");
//    binaryTree.superiorOrEquals("005-010").forEach(data -> System.out.println("Data: " + data));
//
//    System.out.println();
//    System.out.println("Superior strict to 005-010:");
//    binaryTree.superiorStrict("005-010").forEach(data -> System.out.println("Data: " + data));
//
//    System.out.println();
//    System.out.println("Inferior or equals to 005-010:");
//    binaryTree.inferiorOrEquals("005-010").forEach(data -> System.out.println("Data: " + data));
//
//    System.out.println();
//    System.out.println("Inferior strict to 005-010:");
//    binaryTree.inferiorStrict("005-010").forEach(data -> System.out.println("Data: " + data));
//
//    System.out.println();
//    System.out.println("Between 005-008 and 005-010:");
//    binaryTree.betweenInclusive("005-008", "005-010").forEach(data -> System.out.println("Data: " + data));
//
//    System.out.println();
//    System.out.println("Between 005-008 and 005-010 (strict):");
//    binaryTree.betweenExclusive("005-008", "005-010").forEach(data -> System.out.println("Data: " + data));
  }
}
