

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

import DSMSApp.*;

public class DSMSDriver {
	static boolean begin = true;
	private static final java.lang.Object lock = new java.lang.Object();

	// Print manager operations
	public static void printOptionsM() {
		System.out.println("Select an operation:");
		System.out.println("\t1 Add an item"
				+ "\n\t2 Remove an item"
				+ "\n\t3 List items"
				+ "\n\t-1 Done");
	}

	// Print customer operations
	public static void printOptionsU() {
		System.out.println("Select an operation:");
		System.out.println("\t4 Purchase an item"
				+ "\n\t5 Find an item"
				+ "\n\t6 Return an item"
				+ "\n\t7 Exchange an item"
				+ "\n\t8 Test concurrent exchange"
				+ "\n\t-1 Done");
	}

	// Enter customer or manager ID
	public synchronized static String enterID(Scanner in) {
		if(!begin) {
			try {
				synchronized(lock) {
					lock.wait();
				}	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		boolean verified = false;
		String ID = "";	

		do {
			System.out.print("Enter the ID of the customer or manager performing the operation: ");
			ID = in.nextLine();
			if(ID.matches("(ON|BC|QC)M[0-9]{4}")) {
				verified = true;
				printOptionsM();
			}
			else if(ID.matches("(ON|BC|QC)U[0-9]{4}")) {
				verified = true;
				printOptionsU();
			}
			else {
				System.out.println("Invalid ID. Please try again.");
			}
		}while(!verified);

		return ID;

	}

	// Choose an action
	public static int enterChoice(Scanner in) {
		if(!begin) {
			try {
				synchronized(lock) {
					lock.wait();
				}	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		synchronized(lock) {
			int choice = in.nextInt();
			return choice;
		}
	}


	// Verify the format of an ID
	public static String verifyItemID(Scanner in) {
		String itemID = "";
		do {
			System.out.print("ID: ");
			itemID = in.nextLine();
			if(!itemID.matches("(ON|BC|QC)[0-9]{4}")) {
				System.out.println("Invalid ID");
			}
		}while(!itemID.matches("(ON|BC|QC)[0-9]{4}"));

		return itemID;
	}


	public static void main(String[] args) {
		
		DSMSServer.main(args);

		// Keep track of managers and clients created
		HashMap<String, DSMSManagerClient> managers = new HashMap<String, DSMSManagerClient>();
		HashMap<String, DSMSCustomerClient> customers = new HashMap<String, DSMSCustomerClient>();

		boolean running = true;
		Scanner in = new Scanner(System.in);

		System.out.println("----- Welcome to the system! -----");

		do {

			String ID = enterID(in); // Enter customer or manager ID
			int choice = enterChoice(in); // Select operation
			in.nextLine();
			begin = false;

			switch(choice) {
			case 1: { // Add item

				// Gather user input

				System.out.println("Enter the ID, name, quantity, and price of the item.");

				String itemID = verifyItemID(in);

				System.out.print("Name: ");
				String itemName = in.nextLine();

				System.out.print("Quantity: ");
				short quantity = in.nextShort();

				System.out.print("Price: ");
				double price = in.nextDouble();

				if(managers.containsKey(ID)) { // Check if manager exists

					// Run requested operation in a thread
					Runnable r = () -> {
						System.out.println((managers.get(ID).add(itemID, itemName, quantity, price))?"Item successfully added":"Item could not be added");
						// Make sure that the console does not print out of order
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};

					// Start thread
					Thread operation = new Thread(r);
					operation.start();
				} else { // If the manager doesn't already exist, create one

					DSMSManagerClient newMC = new DSMSManagerClient(args, ID);
					managers.put(ID, newMC);

					// Run requested operation in a thread
					Runnable r = () -> {
						System.out.println((newMC.add(itemID, itemName, quantity, price))?"Item successfully added":"Item could not be added");
						// Make sure that the console does not print out of order
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};

					// Start thread
					Thread operation = new Thread(r);
					operation.start();
				}

				in.nextLine();
				break;}
			case 2:{ // Remove item

				System.out.println("Enter the ID and quantity of the item: ");

				String itemID = verifyItemID(in);

				System.out.print("Quantity: ");
				short quantity = in.nextShort();

				if(managers.containsKey(ID)) {
					// Run requested operation in a thread
					Runnable r = () -> {
						if(managers.get(ID).remove(itemID, quantity)) System.out.println("Item successfully removed");
						else System.out.println("Item could not be removed");
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				} else {

					DSMSManagerClient newMC = new DSMSManagerClient(args, ID);
					managers.put(ID, newMC);
					// Run requested operation in a thread
					Runnable r = () -> {
						if(newMC.remove(itemID, quantity)) System.out.println("Item successfully removed");
						else System.out.println("Item could not be removed");
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				}
				in.nextLine();
				break;}
			case 3:{ // List items

				if(managers.containsKey(ID)) {
					// Run requested operation in a thread
					Runnable r = () -> {
						managers.get(ID).list();
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				} else {
					DSMSManagerClient newMC = new DSMSManagerClient(args, ID);
					managers.put(ID, newMC);

					// Run requested operation in a thread
					Runnable r = () -> {
						newMC.list();
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				}
				break;}
			case 4:{ // Purchase item

				System.out.println("Enter the ID of the item: ");

				String itemID = verifyItemID(in);

				System.out.print("\nEnter the date of the purchase (dd-mm-yyyy) or 'today'): ");

				String date = in.nextLine();
				DSMSApp.Date d;

				if(date.equalsIgnoreCase("today")) { d = DSMSApp.Date.getCurrentDate(); }
				else {
					String[] parse = date.split("-");

					Short day = Short.parseShort(parse[0]);
					Short month = Short.parseShort(parse[1]);
					Short year = Short.parseShort(parse[2]);
					d = new DSMSApp.Date(day, month, year);
				}

				DSMSCustomerClient temp;

				// Check if customer already exists; if not, create one
				if(customers.containsKey(ID)) {
					temp = customers.get(ID);
				} else {
					temp = new DSMSCustomerClient(args, ID);
					customers.put(ID, temp);
					System.out.println("Customer created");
				}

				// Run requested operation in a thread
				Runnable r = () -> {
					
					int status = temp.purchase(itemID, d.toString2()); // Attempt to purchase item
					if(status == -1) { // If -1 is returned, the item is not in stock.

						// Ask the user if they would like to be added to the waiting list
						System.out.println("This item is not in stock. Would you like to be added to a waiting list?\nOnce the item becomes available, it will be automatically purchased.");
						String answer = in.nextLine();

						if(answer.equalsIgnoreCase("yes")) {
							temp.purchase(itemID+" wait", d.toString2()); // The server will now wait until the item is in stock to allow the purchase
						}

					}
					else if(status == 2) System.out.println("User " + temp.customerID + " does not have enough money for this item."); // If 2 is returned, the purchaser doesn't have enough money
					else System.out.println("User " + temp.customerID + " has $" + temp.budget); // Otherwise print remaining budget
					synchronized(lock) {
						lock.notify();
						begin = true;
					}	
				};
				Thread operation = new Thread(r);
				operation.start();

				break;}
			case 5:{ // Find item

				System.out.print("Enter the name of the item: ");
				String itemName = in.nextLine();
				if(customers.containsKey(ID)) {
					// Run requested operation in a thread
					Runnable r = () -> {
						if(!(customers.get(ID).find(itemName))) {
							System.out.println("No item was found.");
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				} else {
					DSMSCustomerClient newCC = new DSMSCustomerClient(args, ID);
					customers.put(ID, newCC);
					// Run requested operation in a thread
					Runnable r = () -> {
						if(!(customers.get(ID).find(itemName))) {
							System.out.println("No item was found.");
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				}
				break;}
			case 6:{ // Return item

				System.out.println("Enter the ID of the item: ");
				String itemID = verifyItemID(in);

				System.out.print("\nEnter the date of the return (dd-mm-yyyy) or 'today'): ");

				String date = in.nextLine();
				DSMSApp.Date d;

				if(date.equalsIgnoreCase("today")) { d = DSMSApp.Date.getCurrentDate(); }
				else {
					String[] parse = date.split("-");
					Short day = Short.parseShort(parse[0]);
					Short month = Short.parseShort(parse[1]);
					Short year = Short.parseShort(parse[2]);
					d = new DSMSApp.Date(day, month, year);
				}


				if(customers.containsKey(ID)) {
					// Run requested operation in a thread
					Runnable r = () -> {
						if(customers.get(ID).returnItem(itemID, d.toString2())) {
							System.out.println("Item successfully returned");
						}
						else {
							System.out.println("Item could not be returned");
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				} else {
					DSMSCustomerClient newCC = new DSMSCustomerClient(args, ID);
					customers.put(ID, newCC);
					// Run requested operation in a thread
					Runnable r = () -> {
						newCC.returnItem(itemID, d.toString2());
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				}
				break;}
			case 7:{ // Exchange item

				System.out.println("Enter the ID of the old item: ");
				String oldItemID = verifyItemID(in);

				System.out.println("Enter the ID of the new item: ");
				String newItemID = verifyItemID(in);

				if(customers.containsKey(ID)) {
					// Run requested operation in a thread
					Runnable r = () -> {
						if(customers.get(ID).exchangeItem(ID, newItemID, oldItemID)) {
							System.out.println("Item successfully exchanged");
						}
						else {
							System.out.println("Item could not be exchanged");
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);
					operation.start();

				} else {
					System.out.println("Item could not be exchanged");
					synchronized(lock) {
						lock.notify();
						begin = true;
					}
				}
				break;}
			case 8:{ // Exchange item

				System.out.println("Enter the ID of the old item: ");
				String oldItemID = verifyItemID(in);

				System.out.println("Enter the ID of the new item: ");
				String newItemID = verifyItemID(in);

				System.out.print("Enter the ID of the second user: ");
				String secondCustomer = in.nextLine();

				System.out.println("\n" + secondCustomer + " will simultaneously try to make this exchange\n");

				if(customers.containsKey(ID) && customers.containsKey(secondCustomer)) {
					// Run requested operation in a thread
					Runnable r = () -> {

						if(customers.get(ID).exchangeItem(ID, newItemID, oldItemID)) {
							System.out.println(ID + " has successfully exchanged " + oldItemID + " for " + newItemID);
						}
						else {
							System.out.println(ID + " could not exchange " + oldItemID + " for " + newItemID);
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation = new Thread(r);

					Runnable r2 = () -> {

						if(customers.get(secondCustomer).exchangeItem(secondCustomer, newItemID, oldItemID)) {
							System.out.println(secondCustomer + " has successfully exchanged " + oldItemID + " for " + newItemID);
						}
						else {
							System.out.println(secondCustomer + " could not exchange " + oldItemID + " for " + newItemID);
						}
						synchronized(lock) {
							lock.notify();
							begin = true;
						}	
					};
					Thread operation2 = new Thread(r2);

					operation.start();
					operation2.start();

				} else{
					System.out.println("Cannot perform exchange. Both customers must have purchased an item");
					synchronized(lock) {
						lock.notify();
						begin = true;
					}	
				}
				break;
			}
			default: System.out.println("Thanks for using the system!"); running=false; ;
			}
		}while(running);

	}


}
