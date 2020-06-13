package com.company;

import java.util.LinkedList;

public class Person {
    private
    String name;
    int weight = 1;
    boolean seen = false;
    int guildWeight = 0;

public Person(String personName){ //Build person from file
    name = personName;
    if(name.contains(",")){
        weight = 2;
    }
}
    int getWeight(){ //Quickly check if married
        return  weight;
    }

    String getName(){
        return name;
    }

    boolean getSeen() {return seen; }

    int getGuild() { return guildWeight; }
}
