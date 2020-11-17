package a2;

import java.util.ArrayList; 
import java.util.concurrent.PriorityBlockingQueue;

///////////////////////////////
//  ASSIGNMENT ONE, SOEN 423 //
//     Name: Saffia Niro     //
//       ID: #40054733       //
//    Date: October 9, 2020  //
///////////////////////////////

public class Item {
	
	String itemID;
	String itemName;
	int quantity;
	double price;
	DSMSApp.Date dateOfPurchase;
	ArrayList<String> IDOfBuyer;
	PriorityBlockingQueue<String> waitingList;
	
	public Item(String ID, String name, int quantity, double price) {
		itemID = ID;
		itemName = name;
		this.quantity = quantity;
		this.price = price;
		dateOfPurchase = null;
		IDOfBuyer = new ArrayList<String>();
		waitingList = new PriorityBlockingQueue<String>();
	}
	
	
}
