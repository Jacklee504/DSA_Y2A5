package topic6;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // give the user the choice between continuing an old game or starting a new one
        System.out.println("Would you like to load an old game (l) or play a new game (p)?");
        Scanner scanner = new Scanner(System.in);
        String choice = scanner.next().toLowerCase();

        // create a new empty tree
        BinaryTree<String> tree = new BinaryTree<>();

        if (choice.equals("l")) {
            // user wants to load the old game
            try (BufferedReader reader = new BufferedReader(new FileReader("BinaryTree.txt"))) {
                // attempt to load the root node from file
                BinaryNodeInterface<String> loadedRoot = load(reader);

                if (loadedRoot != null) {
                    // if successfully loaded, assign it to the current tree
                    tree.setRootNode(loadedRoot);
                    System.out.println("Game loaded successfully.\n");
                } else {
                    // if loading failed or file was empty, create a new tree instead
                    System.out.println("Error loading the game file. A new game was created.");
                    tree = createTree();
                }
            } catch (IOException e) {
                // if file doesn't exist or another error happens, start a new game
                System.out.println("Error loading the tree: " + e.getMessage() + ". A new game was created.");
                tree = createTree();
            }

        } else if (choice.equals("p")) {
            // user wants to start a new game
            playGame();
        }

        // check if the tree is still empty
        if (tree.getRootNode() == null) {
            System.out.println("Error: Loaded tree is empty. Starting a new game.");
            playGame(); // start fresh if the tree is null
        } else {
            askQuestions(tree); // start asking questions using the loaded or new tree
        }
    }


    public static void playGame() {
        // create the initial tree and then start asking questions, starting with the animal question
        BinaryTree<String> animalQuestion = createTree();
        askQuestions(animalQuestion);
    }

    public static void askQuestions(BinaryTree<String> tree) {

        boolean done = false; // boolean to store whether the game is over or not
        Scanner scanner = new Scanner(System.in);
        BinaryNodeInterface<String> currentNode = tree.getRootNode();
        String whatNext = ""; // string to store what the user wants to do after the game iteration

        while(!done) {
            if(currentNode.isLeaf()) {
                // ask the user if your guess is correct if the current node is a leaf
                System.out.println("Are you thinking of a " + currentNode.getData() + "? (yes/no):");
            }
            else {
                // if the current node isnt a leaf ask the user the next question
                System.out.print(currentNode.getData() + " (yes/no): ");
            }

            String answer = scanner.nextLine();
            if (!currentNode.isLeaf()){
                // if its not a leaf go to the leaf node depending on the users answer to the previous question
                if (answer.equals("yes")) {
                    currentNode = currentNode.getLeftChild();
                } else {
                    currentNode = currentNode.getRightChild();
                }
            } else {
                // if it is a leaf node check if the game got the answer correct
                if (answer.equals("yes")) {
                    // if the game got it correct ask the user what they want to do next and store this in whatNext
                    done = true;
                    System.out.println("Great! Would you like to:\nPlay again? (P)\nSave the tree? (S)\nLoad a previously saved tree? (L)\nQuit?(Q)");
                    whatNext = scanner.nextLine().toLowerCase();

                } else {
                    // the game got the users answer wrong so get the details to differentiate between the users answer and the one the game thought was correct
                    System.out.println("I dont know, what is the correct answer?: ");
                    String reply = scanner.nextLine();

                    System.out.println("What yes/no question can be used to differentiate my answer from yours?: ");
                    String newQuestion = scanner.nextLine();

                    System.out.println("What is the answer for " + reply + "?: ");
                    String newSide = scanner.nextLine();

                    System.out.println("Answer saved.");

                    BinaryTree<String> newAnswerNode = new BinaryTree<>(reply);
                    BinaryNodeInterface<String> oldNode = new BinaryNode<>(currentNode.getData());
                    currentNode.setData(newQuestion);

                    // store the users answer on a side depending on the question the user gave for their yes no question and the games guess on the other side
                    if (newSide.equals("yes")) {
                        currentNode.setLeftChild(newAnswerNode.getRootNode());
                        currentNode.setRightChild(oldNode); // keep old wrong guess
                    } else {
                        currentNode.setLeftChild(oldNode); // keep old wrong guess
                        currentNode.setRightChild(newAnswerNode.getRootNode());
                    }
                    currentNode = tree.getRootNode();
                }

                // switch for what to do when the game is over
                switch (whatNext) {
                    case "p" -> {
                        // play again, so set done to false and go to the top of the tree again
                        done = false;
                        currentNode = tree.getRootNode();
                    }
                    case "s" -> {
                        // save tree, call save function and then ask the user if they want to play again after saving
                        try (PrintWriter writer = new PrintWriter(new FileWriter("BinaryTree.txt"))) {
                            save(tree.getRootNode(), writer);
                        } catch (IOException e) {
                            System.out.println("Error saving the tree: " + e.getMessage());
                        }
                        System.out.println("Would you like to play again? (yes/no): ");
                        String reply = scanner.nextLine().toLowerCase();
                        if (reply.equals("yes")) {
                            // user wants to play again, go to the top of the tree
                            done = false;
                            currentNode = tree.getRootNode();
                        }
                        else if (reply.equals("no")) {
                            // user doesnt want to play again, quit
                            done = true;
                            break;
                        }
                    }
                    case "l" -> {
                        // load, so load the old saved game file then
                            try (BufferedReader reader = new BufferedReader(new FileReader("BinaryTree.txt"))) {
                                tree.setRootNode(load(reader)); // call the load method and sets the loaded tree to the current one
                                currentNode = tree.getRootNode();
                                done = false;
                            } catch (IOException e) {
                                System.out.println("Error loading tree: " + e.getMessage());
                            }
                    }
                    case "q" -> {
                        // quit, set done to true and break
                        done = true;
                        break;
                    }
                }
            }

        }
    }

    public static BinaryTree<String> createTree() {
        // potential answers
        BinaryTree<String> dog = new BinaryTree<>("dog");
        BinaryTree<String> cat = new BinaryTree<>("cat");
        BinaryTree<String> tree = new BinaryTree<>("tree");
        BinaryTree<String> rock = new BinaryTree<>("rock");
        BinaryTree<String> bird = new BinaryTree<>("bird");
        BinaryTree<String> cow = new BinaryTree<>("cow");
        BinaryTree<String> fish = new BinaryTree<>("fish");

        // questions to ask the user
        BinaryTree<String> farmQuestion = new BinaryTree<>("Is it found on the farm?", cow, fish);
        BinaryTree<String> barkQuestion = new BinaryTree<>("Does it bark?", dog, cat);
        BinaryTree<String> plantQuestion = new BinaryTree<>("Is it a plant?", tree, rock);
        BinaryTree<String> flyQuestion = new BinaryTree<>("Does it fly?", bird, farmQuestion);
        BinaryTree<String> petQuestion = new BinaryTree<>("Is it a common pet?", barkQuestion, flyQuestion);

        // initial question
        BinaryTree<String> animalQuestion = new BinaryTree<>("Is it an animal?", petQuestion, plantQuestion);

        return animalQuestion; // return the root node of the tree
    }


    public static void save(BinaryNodeInterface<String> node, PrintWriter writer) {
            if (node == null) return;

            if (node.isLeaf()) {
                // if its a leaf just save it with an A at the start of the line to separate leaves from questions
                writer.println("A:" + node.getData());
            } else {
                // if its not a leaf save it with a q at the start along with its child nodes
                writer.println("Q:" + node.getData());
                save(node.getLeftChild(), writer);
                save(node.getRightChild(), writer);
            }

    }

    public static BinaryNodeInterface<String> load(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) return null;

        if (line.startsWith("A:")) {
            return new BinaryNode<>(line.substring(2)); // ignore the a: then return the rest of the line
        } else if (line.startsWith("Q:")) {
            BinaryNode<String> node = new BinaryNode<>(line.substring(2)); // ignore the q: then return the rest of the line
            node.setLeftChild(load(reader));  // recursively load the left and right child nodes
            node.setRightChild(load(reader));
            return node;
        }
        return null;
    }



}