import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.InterruptedException;

class ac_test extends DLB{

  public static void main(String[] args) throws IOException, InterruptedException {

      // read dictionary
      BufferedReader readFile = new BufferedReader(new FileReader("dictionary.txt"));
      String line;

      // master DLB trie
      DLB master = new DLB(); int j = 0;

      // add all words in dictionary to DLB
      while((line = readFile.readLine()) != null){
        master.add(line, j); j++;
      }
      readFile.close();

      DLB user = new DLB(); j = 0;

      // open user_history file, if applicable
      File f = new File("user_history.txt");

      if (f.exists()){

        BufferedReader readFile2 = new BufferedReader(new FileReader(f));
        while((line = readFile2.readLine()) != null){
          line = line.trim();
          user.add(line, j); j++;
        }

        readFile.close();
      }

      // save roots to reset for new words
      Node originalRoot = master.root; Node userRoot = user.root;

      // initializations
      char nextChar; int nextInt;
      String str = ""; boolean newWord = false;

      while (true){

        // get user input
        Scanner kb = new Scanner(System.in);
        System.out.print("Enter the next character: ");
        nextChar = kb.nextLine().charAt(0);

        // break condition
        if (nextChar == '!'){

          user.root = userRoot;

          // open/create file
          File writeFile = new File("user_history.txt");

          // create file writer + pass to printChild to enable file write
          BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile, false));
          user.printChild(user.root, "", writer);
          writer.close(); break;
        }

        // check if autocomplete is being executed
        else if (Character.isDigit(nextChar)){

          // transform character to int and get word from previous
          nextInt = Character.getNumericValue(nextChar);

          // add to user history trie & print
          user.root = userRoot;

          // print from user trie
          if (nextInt < user.printCount){
            str = user.words[nextInt-1];
          }

          // print from dictionary trie
          else{
            str = master.words[nextInt-1];
            user.add(str, user.size+1);
          }

          // print completed word
          System.out.println("WORD COMPLETED: " + str);

          // reset
          str = ""; master.root = originalRoot;

        }

        // user-completed word added to the dictioanry
        else if (nextChar == '$'){
          user.root = userRoot; user.add(str, user.size+1);
          System.out.println(str + " added to user dictionary!");

          // reset
          newWord = false; str = ""; master.root = originalRoot;
        }

        // default is letter
        else{

          // start stopwatch
          long timeIn = System.nanoTime();

          // look for character on next level
          Node levelNode = master.search(nextChar);
          Node levelNodeUser = user.search(nextChar);

          // append the found character to the string
          str += nextChar;

          // reset counter variable
          master.printCount = 1; user.printCount = 1;

          if (!newWord){

            //System.out.println(user.root.getNext().letter);

            // print words from user dictionary
            if (levelNodeUser != user.root)
              user.printChild(levelNodeUser.getNext(), str, null);

            // adjust count to maintain correct number of prints
            master.printCount += (user.printCount - 1);

            // print words from dictionary
            if (levelNode != master.root){
              master.printChild(levelNode.getNext(), str, null);
            }

            else{
              newWord = true;
              System.out.println("No predictions found...");
            }

            // set the new roots as the character entered
            master.root = levelNode; user.root = levelNodeUser;
          }

          else
            System.out.println("No predictions found...");

          // stop stopwatch and calculate time ellapsed
          long timeOut = System.nanoTime();
          double timeDiff = (timeOut - timeIn) * 1E-9;

          // print time dfiference
          System.out.printf("(%1.6f s)\n", timeDiff);

        }
      }
    }
  }
