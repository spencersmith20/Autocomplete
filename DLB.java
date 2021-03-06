import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// !!! TO DO !!!!
// info sheet + approach.txt

class DLB{

  public Node root;       // root of DLB
  public int printCount;  // keeps track of words printed by autocomp. recursion
  public String[] words;  // words generated by autocomplete
  public int size;        // number of entries in the trie

  // node class
  public class Node{

    private Object value; // value key-value pairing
    public char letter;  //move across the trie

    private Node sib;     // move across the Trie (alternate character)
    private Node next;    // move down the Trie

    // no char constructor for root creation
    private Node(){
    }

    public Node(char c){
      this.letter = c;
    }

    public Node getNext(){
      return next;
    }

    public Node getSib(){
      return sib;
    }
  }

  //create a new DLB trie
  public DLB(){
    this.printCount = 1;
    this.root = new Node();
    this.words = new String[5];
  }

  // add a key-value pair to the DLB
  public void add(String key, Object value) throws InterruptedException{

    int n = key.length(); int i = 0;

    // preNode keeps track of  Node before  currentNode. this makes it easy
    // to add things as .sib or .next b/c of easier referencing
    Node preNode = root; Node currentNode = root.next;

    while(i < n){

      boolean duplicate = false;
      char currentChar = key.charAt(i);

      // currentNode is a null ref. set preNode.next to a new Node w/ currentChar
      // increment the string & move the Node references down into the trie
      if (currentNode == null){
        // create a Node reference
        currentNode = new Node(currentChar); preNode.next = currentNode;

        // increment Nodes & i
        preNode = currentNode; currentNode = currentNode.next; i++;
      }

      // currentNode's letter is equal to currentChar, char is found
      // move downward in the trie & iterate as done above
      else if (currentNode.letter == currentChar){
        //ystem.out.println(currentChar);
        preNode = currentNode; currentNode = currentNode.next; i++;
      }

      // the following cases are under the assertion that
      // currentNode's letter does NOT correspond w/ currentChar

      // currentNode.sib is null AND currentChar is not here. add a Node for
      // currentChar as currentNode.sib. set preNode to sibling and currentNode to child
      else if (currentNode.sib == null){
        currentNode.sib = new Node(currentChar);

        // set new Node references
        preNode = currentNode.sib; currentNode = preNode.next; i++;
      }

      // currentChar has not been found in the level, but sibling Node(s) still
      // exist so we will continue traversing. increment Nodes by using .sib
      else {
        preNode = currentNode; currentNode = currentNode.sib;
      }

    // if iterating in the loop caused i to go over n, this will be executed
    // as the final step in the while loop. create a new Node with "$" letter
    if (i == n){

      // the key is not a prefix to any key. add a .next reference to
      // a new Node with the terminating character
      if (currentNode == null){
        currentNode = new Node('$'); preNode.next = currentNode;
      }

      // a Node already follows this key! the key is a prefix. iterate
      // through siblings until we find a null node
      else{

        while (currentNode != null){

          // watch for duplicate words
          if (currentNode.letter == '$')
            duplicate = true;

          preNode = currentNode; currentNode = currentNode.sib;
        }

        // set letter at final node to terminating character
        if (!duplicate)
          currentNode = new Node('$'); preNode.sib = currentNode;
      }

      // set currentNode's value to provided value & increase size
      if (!duplicate)
        currentNode.value = value; size++;

    }
  }
}

//this gets us to the node with the character we want, the new root.
public Node search(char c){

  Node currentNode = root.next;

  // while there are more Nodes to check on this level
  while (currentNode != null){

    // terminating character found on this level
    if (currentNode.letter == c)
      return currentNode;

    // move along level
    currentNode = currentNode.sib;
  }

  return root;
}

// recursively print five of the children and siblings of the Trie
public void printChild(Node node, String str, BufferedWriter writer) throws IOException{

  if (printCount > 5)
    return;

  // print string if reached end character
  if(node.letter == '$'){

    if (writer == null){
      System.out.println("(" + (printCount) +  ") " + str);
      this.words[printCount-1] = str;
      printCount++;
    }
    else{
      writer.write(str);
      writer.newLine();
    }
  }

  // print .next nodes recursively
  if (node.next != null)
    printChild(node.next, (str+node.letter), writer);

  // print .sib nodes recursively
  if (node.sib != null)
    printChild(node.sib, str, writer);
}

}
