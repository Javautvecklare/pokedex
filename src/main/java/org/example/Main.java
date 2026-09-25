package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Type myType = Type.FIRE;
        System.out.println("My type: " + myType);

        for (Type t : Type.values()) {   // for-each från L3: "för varje Type t bland alla värden"
            System.out.println(t);
        }

    }
}