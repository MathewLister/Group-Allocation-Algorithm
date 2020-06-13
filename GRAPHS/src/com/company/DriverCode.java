package com.company;

//Graph libraries
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import scala.util.parsing.combinator.testing.Str;
//*****************************************************************
import java.io.IOException;
import java.io.*;
import java.util.*;




public class DriverCode {

    //Creates a random number with a range from zero to the "size" parameter
    //Parameters: Size holds the size of the people list
    //Returns: Random number ranging from zero to the "size" parameter
    static int createRandom(int size){
        double newRandomDouble = Math.random(); //Store a random number in a double
        newRandomDouble = newRandomDouble * size;   //Multiply that random double by the input size
        int newRandomInt = (int) newRandomDouble;   //Convert the double into an int
        return newRandomInt;    //Return the random number
    }

    public static void main(String[] args) throws IOException {
        //Menu
        String menu = "*************MENU*************\n1. Groups size - 16\n2. Group size - 29\n3. Group size - 34\n";

        //Three required files
        File file1 = new File("group1.txt"); //16 people
        File file2 = new File("group2.txt"); //29 people
        File file3 = new File("group3.txt"); //34 people

        //Get console input
        Scanner myInput = new Scanner(System.in);

        //Variables
        int groupSize = 0; //Read in from file
        // to determine size of groups
        int amountOfGroups = 0; //How many groups we should expect for each iteration
        int totalWeight = 0; // Total weight of community/people
        //int currentNumOfGroups = 0; //How many established groups we have at the current moment
        //int totalDone = 0; //Keeps track of current total weight of graph
        int groupWeight = 0; //Weight of current group being built
        //int overflow = 0; //Amount of remainders to expect
        int visitors = 0;   //Count of the amount of people currently in the hash table
        int finish = 0;     //Count of the amount of people that should be in the hash table
        int rand = 0;   //Used to create new heads
        int fileNameCounter = 1;    //Used to create PNG file names
        int iterations = 0;

        //Storage Containers
        ArrayList<Person> people = new ArrayList<>(); //Holds the list of people read from file
        Map<String, List<String>> hosts = new HashMap<>(); //Key = host, Value(s) = visitor(s)
        Graph groups = new SingleGraph("Small Groups");  //Graph for each iteration
        Graph clique = new DefaultGraph("Everything");
        ArrayList<Person> currentHosts = new ArrayList<>(); //Holds current hosts for reference
        // Picture Stuff
        FileSinkImages pic = new FileSinkImages(FileSinkImages.OutputType.png, FileSinkImages.Resolutions.HD1080);
        pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        pic.setQuality(FileSinkImages.Quality.HIGH);


        // Choose a file
        System.out.println(menu);
        int option = -1;    //Default option to -1
        System.out.print("Choose an option: "); //Prompt user to enter their selection of text file
        option = myInput.nextInt(); //Read in the input option
        switch (option) {
            case 1:
                Scanner readFile = new Scanner(file1);  //Start reading file
                groupSize = Integer.parseInt(readFile.nextLine());  //Set groupSize to num from first line

                //Parse rest of file and turn them into a person
                //Add persons into an array
                while (readFile.hasNextLine()) {
                    Person temp = new Person(readFile.nextLine());  //Make person obj
                    totalWeight += temp.getWeight();    //How many people we are reading in total
                    people.add(temp);   //Add the newly created temp person type to the people list
                }
                break;
            case 2:
                Scanner readFileTwo = new Scanner(file2);   //Start reading file
                groupSize = Integer.parseInt(readFileTwo.nextLine());   //Set groupSize to num from first line

                //Parse rest of file and turn them into a person
                //Add persons into an array
                while (readFileTwo.hasNextLine()) {
                    Person temp = new Person(readFileTwo.nextLine());   //Make person obj
                    totalWeight += temp.getWeight();    //How many people we are reading in total
                    people.add(temp);   //Add the newly created temp person type to the people list
                }
                break;
            case 3:
                Scanner readFileThree = new Scanner(file3);     //Start reading file
                groupSize = Integer.parseInt(readFileThree.nextLine());     //Set groupSize to num from first line

                //Parse rest of file and turn them into a person
                //Add persons into an array
                while (readFileThree.hasNextLine()) {
                    Person temp = new Person(readFileThree.nextLine());     //Make person obj
                    totalWeight += temp.getWeight();    //How many people we are reading in total
                    people.add(temp);   //Add the newly created temp person type to the people list
                }
                break;
        }




        amountOfGroups = totalWeight / groupSize;   //amountOfGroups holds how many groups we will have
        //overflow = totalWeight % groupSize;     //overflow holds how many remainders to expect

        finish = ((people.size()) * (people.size() - 1));   //finish holds the total number of values that should be in the hash table when complete


        //Where g is groupsize
        //Where n is list size
        // OVERALL TIME COMPLEXITY: O( 3gn + n + n^2 )

        while (visitors < finish) {     //UNSIMPLIFIED OVERALL TIME COMPLEXITY: O(g * (n + n)) + (n * g) + n + (n * (n-1)) + n)
            System.out.println("Selecting new hosts");


            while (currentHosts.size() != amountOfGroups) {   //ASSIGN HOSTS    //TIME COMPLEXITY: O(g)
                //System.out.println("Creating host");
                rand = createRandom(people.size()); //Select random number to use in selecting a random host
                while (people.get(rand).getSeen() == true) {  //If currentHosts already has the new host we are planning to create    //TIME COMPLEXITY: O(n)
                    rand = createRandom(people.size()); //Select new random number to get a different random new host
                    //System.out.println("Selecting new random number");
                }
                groupWeight = people.get(rand).getWeight(); //Add this new host to the groupWeight
                groups.addNode(people.get(rand).getName()).addAttribute("ui.label", people.get(rand).getName()); //Add the new host node

                if(clique.getNode(people.get(rand).getName()) == null){
                    clique.addNode(people.get(rand).getName()).addAttribute("ui.label", people.get(rand).getName()); //Add the new host node
                }
                System.out.print("Head: ");     //Print head
                System.out.println(people.get(rand).getName());      //Print head
                people.get(rand).seen = true;   //Mark as seen
                currentHosts.add(people.get(rand));     //Add this new host to the list of existing hosts

                // Iterate j through entire list j = member/visitor
                //Prioritize assigning members that have not yet been seen by the host
                for (int j = 0; j < people.size(); j++) {   //TIME COMPLEXITY: O(n)
                    if (hosts.containsKey(people.get(rand).getName())) {  //Check if host exists in map
                        List<String> hashRet = hosts.get(people.get(rand).getName()); //The host exists --> lets see if this person has already visited
                        if (!hashRet.contains(people.get(j).getName())) { //If they havent visited this host
                            if (people.get(j).getSeen() == false){ //While person is not seen
                                if ((groupWeight + people.get(j).getWeight()) <= groupSize) {   // If adding the new person does not exceed the group size

                                    groups.addNode(people.get(j).getName()).addAttribute("ui.label", people.get(j).getName());    //Add node for visitor
                                    if(clique.getNode(people.get(j).getName()) == null){
                                        clique.addNode(people.get(j).getName()).addAttribute("ui.label", people.get(j).getName());    //Add node for visitor
                                    }
                                    System.out.print("Visitor: ");     //Print visitor
                                    System.out.println(people.get(j).getName());      //Print visitor
                                    String edgeName = people.get(rand).getName() + people.get(j).getName(); //Name edge connecting two nodes
                                    String otherWay = people.get(j).getName() + people.get(rand).getName();
                                    if(clique.getEdge(edgeName) == null && clique.getEdge(otherWay) == null){
                                        clique.addEdge(edgeName, people.get(rand).getName(), people.get(j).getName());  //Add edge connecting two nodes
                                    }
                                    groups.addEdge(edgeName, people.get(rand).getName(), people.get(j).getName());  //Add edge connecting two nodes

                                    hosts.computeIfAbsent(people.get(rand).getName(), k -> new ArrayList<>()).add(people.get(j).getName()); //Add to hashmap, if there is a hash collision create an array list to hold the collisions
                                    people.get(j).seen = true;  //Mark visitor as seen
                                    groupWeight += people.get(j).getWeight();   //Update group weight to include new person
                                }
                            }
                        }
                    }
                    else{ //First time host --> try other conditions to see if we can add the visitor 
                        if (people.get(j).getSeen() == false){  //While person is not seen
                            if ((groupWeight + people.get(j).getWeight()) <= groupSize) {   // If adding the new person does not exceed the group size

                                if(clique.getNode(people.get(j).getName()) == null){
                                    clique.addNode(people.get(j).getName()).addAttribute("ui.label", people.get(j).getName());    //Add node for visitor
                                }

                                groups.addNode(people.get(j).getName()).addAttribute("ui.label", people.get(j).getName());    //Add node for visitor

                                System.out.print("Visitor: ");     //Print visitor
                                System.out.println(people.get(j).getName());      //Print visitor
                                String edgeName = people.get(rand).getName() + people.get(j).getName(); //Name edge connecting two nodes
                                String otherWay = people.get(j).getName() + people.get(rand).getName();
                                if(clique.getEdge(edgeName) == null && clique.getEdge(otherWay) == null){
                                    clique.addEdge(edgeName, people.get(rand).getName(), people.get(j).getName());  //Add edge connecting two nodes
                                }
                                groups.addEdge(edgeName, people.get(rand).getName(), people.get(j).getName());  //Add edge connecting two nodes

                                hosts.computeIfAbsent(people.get(rand).getName(), k -> new ArrayList<>()).add(people.get(j).getName()); //Add to hashmap, if there is a hash collision create an array list to hold the collisions
                                people.get(j).seen = true;  //Mark visitor as seen
                                groupWeight += people.get(j).getWeight();   //Update group weight to include new person
                            }
                        }
                    }
                }
                people.get(rand).guildWeight = groupWeight; //The head's guildWeight represents the weight of this iteration's grouping
            }

            //Overflow
            //People left unseen here have already been seen by current hosts
            //Attempt to place them into groups where they will fit within the groupsize
            for (int q = 0; q < people.size(); q++) {   //Find the people not on the graph //TIME COMPLEXITY: O(n)
                if (people.get(q).getSeen() == false) {     //If that person is not yet seen they are not on the graph
                    insertOverflow: for(int y = 0; y < currentHosts.size(); y++) {      //TIME COMPLEXITY: O(g)
                        groupWeight = currentHosts.get(y).getGuild();   //The weight of the group we're considering is held in the host
                        if ((groupWeight + people.get(q).getWeight()) <= groupSize) {   //If adding this new person will not go over the groupsize
                            System.out.print("Overflow: "); //Print who is being added from overflow
                            System.out.println(people.get(q).getName());    //Print who is being added from overflow
                            groups.addNode(people.get(q).getName()).addAttribute("ui.label", people.get(q).getName());    //Create node for the overflow name
                            people.get(q).seen = true;  //Mark this person as now seen
                            String edgeName = currentHosts.get(y).getName() + people.get(q).getName();  //Create an edge linking the overflow person to the host
                            groups.addEdge(edgeName, currentHosts.get(y).getName(), people.get(q).getName());   //Use this edge to link the overflow person to the host
                            currentHosts.get(y).guildWeight += people.get(q).getWeight();  //Update the guildWeight to include the overflow person
                            if (hosts.containsKey(currentHosts.get(y).getName())) {     //Check if the hashmap has this person as a key
                                List<String> hashRet = hosts.get(currentHosts.get(y).getName());    //If the person is a key, retrieve their value list
                                if (!hashRet.contains(people.get(q).getName())) {   //If the value list does not contain the person we want to add
                                    hosts.computeIfAbsent(currentHosts.get(y).getName(), k -> new ArrayList<>()).add(people.get(q).getName());  //Add this person as a key value pair
                                    if(clique.getNode(people.get(q).getName()) == null){
                                        clique.addNode(people.get(q).getName()).addAttribute("ui.label", people.get(q).getName());    //Create node for the overflow name
                                    }
                                    if(clique.getEdge(edgeName) == null){
                                        clique.addEdge(edgeName, currentHosts.get(y).getName(), people.get(q).getName());   //Use this edge to link the overflow person to the host
                                    }
                                }
                            }
                            break insertOverflow;   //Once the overflow person is inserted, look for the next overflow person
                        }
                    }
                }
            }

            //SUPER OVERFLOW
            //People unseen at this point do not fit on the graph within a group, there are not enough people to make another group, so they must be appended to already full groups
            int counter = currentHosts.size();  //The amount of existing hosts/groups is in counter
            for (int q = 0; q < people.size(); q++) {   //Loop to find the person not yet seen //TIME COMPLEXITY: O(n)
                if (people.get(q).getSeen() == false) {         //If you find the person not yet on the graph
                    if (counter == 0) {     //If the counter is zero, it will try to insert into a group that doesn't exist.
                        counter = currentHosts.size();  //Reset counter to the last group
                    }
                        System.out.print("Super overflow: ");   //Print what had to be appended and does not fit in a group
                        System.out.println(people.get(q).getName());    //Print what had to be appended and does not fit in a group
                        groups.addNode(people.get(q).getName()).addAttribute("ui.label", people.get(q).getName());    //Create node for the overflow name
                        people.get(q).seen = true;  //Mark the overflow as seen
                        String edgeName = currentHosts.get(counter - 1).getName() + people.get(q).getName();    //Create an edge to connect the overflow with the group host
                        String otherWay = people.get(q).getName() + currentHosts.get(counter - 1).getName();
                        groups.addEdge(edgeName, currentHosts.get(counter - 1).getName(), people.get(q).getName());     //Use this edge to connect the overflow with the group host
                        List<String> hashRet = hosts.get(currentHosts.get(counter - 1).getName());  //List of values from the given key
                        if (!hashRet.contains(people.get(q).getName())) {   //If the person is not in the list of values from the given key
                            hosts.computeIfAbsent(currentHosts.get(counter - 1).getName(), k -> new ArrayList<>()).add(people.get(q).getName());    //Add to hashmap, if there is a hash collision create an array list to hold the collisions
                            if(clique.getNode(people.get(q).getName()) == null){
                                clique.addNode(people.get(q).getName()).addAttribute("ui.label", people.get(q).getName());    //Create node for the overflow name
                            }
                            if(clique.getEdge(edgeName) == null && clique.getEdge(otherWay) == null){
                                clique.addEdge(edgeName, currentHosts.get(counter - 1).getName(), people.get(q).getName());     //Use this edge to connect the overflow with the group host
                            }
                        }
                    counter--;  //Move to the previous to the current group
                }
            }


            //TAKE PICTURE
            pic.writeAll(groups, fileNameCounter + ".png"); // Takes a snap shot
            System.out.print("Created graph number: "); //Let the user know what the graph png number is
            System.out.println(fileNameCounter);    //Let the user know what the graph png number is
            fileNameCounter++;  //Increment this counter to get ready for the next png

            //***********PRINT MAP OF HOSTS+VISITORS***********
            System.out.println("HASH TABLE");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
            String myFormat = "| %-20s | %-110s |%n";
            visitors = 0;   //Start from zero
            for (Map.Entry<String, List<String>> entry : hosts.entrySet()) {    //For each entry set in the hashmap  //TIME COMPLEXITY: Worst Case -> O(n)
                String key = entry.getKey();    //Grab the key
                List<String> value = entry.getValue();  //Grab the list of values
                StringBuilder list = new StringBuilder(new String());
                for (String lString : value) {      //For each value in the list of values    //TIME COMPLEXITY: O(n-1)
                    list.append(lString).append(" "); // collect the list attached to the key
                    visitors++;     //Increment visitors for each value
                }
                System.out.format(myFormat, key, list);
            }
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
                //Reinitialize everyone to unseen
                for (int i = 0; i < people.size(); i++) {    //Reinitialize all people to be unseen   //TIME COMPLEXITY: O(n)
                    people.get(i).seen = false;     //Set the person to be unseen
                }

                //Reinitialize groups to be clear
                groups.clear();

                currentHosts.clear();   //Reset all of the currentHosts to be empty

                System.out.print("Number of visitors is: ");
                System.out.println(visitors);
                System.out.print("Number of finish is: ");
                System.out.println(finish);
                iterations++;
        }
        System.out.println("TOTAL ITERATIONS: " + iterations);
        pic.writeAll(clique, "Clique.png"); // Takes a snap shot
        clique.display();
    }
}
