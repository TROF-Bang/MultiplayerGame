package test;

import java.io.Serializable;
import java.util.LinkedList;

import javafx.scene.input.MouseEvent;


public class ClientInfoSeirialized implements Serializable {

    private final static long serialVersionUID = 1;//Adding a serialVersionUID
    //to the class protects against a problem when new fields being added

    String id;
    
    MouseEvent me;
    
    LinkedList<String> commends;
    
    public ClientInfoSeirialized() {
    	commends = new LinkedList<String>();
	}
    
//    String recipient; //Holds other client's name to send a private message
    //or 'all' to send a message to everyone

//    boolean showOnline; //If it's true server returns a string with all the 
    //online user

}
