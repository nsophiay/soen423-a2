package a2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DSMSApp.DSMS;
import DSMSApp.DSMSHelper;

public class DSMSCustomerClient {

	String customerID;
	double budget;
	String store;
	File log;
	DSMS dsmsServant;
	private ORB orb;

	public DSMSCustomerClient(String[] args, String ID) {
		if(ID.charAt(2) != 'U') {
			System.out.println("User IDs must be in the format: [provinceAcronym]U[4-digit ID]");
			return;
		}
		customerID = ID;
		budget = 1000.00;
		store = ID.substring(0,2);
		try {
			log = new File(ID + "log.txt");
			if(log.createNewFile()) {
				System.out.println("File created for customer #" + ID);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try{
			// create and initialize the ORB
			orb = ORB.init(args, null);

			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is 
			// part of the Interoperable naming Service.  
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// resolve the Object Reference in Naming
			dsmsServant = (DSMS)DSMSHelper.narrow(ncRef.resolve_str(store));

			dsmsServant.shutdown();

		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}

	}

	public int purchase(String itemID, DSMSApp.Date dateOfPurchase) {

		int status = 1;

		String custAndBudget = this.customerID + budget;

		double price = dsmsServant.purchaseItem(custAndBudget, itemID, dateOfPurchase);

		if(price > 0 && this.budget > price) { // If purchase was successful, subtract from budget
			this.budget -= price;
		}
		else if(price == 0.0) {
			status = 0;
			System.out.println("User " + this.customerID + " could not purchase item " + itemID);
		}
		else if(price == -1){ // If the item is not in stock, ask the customer if they want to be added to the list
			status = -1;
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);
				writeUser.append("\nREQUEST:\n" + "\tDate of purchase: " + dateOfPurchase
						+ "\n\tType: purchase" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				writeUser.append(status==1?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status;
	}

	public boolean find(String itemName) {

		boolean nothingFound = true;
		String found = dsmsServant.findItem(this.customerID,itemName);
		if(!found.equals("")) {
			String[] founds = found.split(",");
			for(String i : founds) {
				i.trim();

				//if(i.matches("[\\s\\[\\]]*[0-9A-Za-z\\s.]+")) {
				System.out.println(i);
				nothingFound = false;
				//}
			}
		}

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: search" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem name: " + itemName);
				writeUser.append(nothingFound?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return !nothingFound;
	}

	public boolean returnItem(String itemID, DSMSApp.Date dateOfReturn) {

		boolean status = true;
		double price = dsmsServant.returnItem(this.customerID, itemID, dateOfReturn);
		if(price > 0) this.budget+=price;
		else status = false;

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate of return: " + dateOfReturn
						+ "\n\tType: return" + "\n\tSubmitted by: " + customerID
						+ "\n\tItem ID: " + itemID);
				writeUser.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return status;
	}

	public boolean exchangeItem(String customerID, String newItemID, String oldItemID) {

		boolean status = true;
		String custAndBudget = this.customerID + budget;

		status = dsmsServant.exchangeItem(custAndBudget, newItemID, oldItemID);


		// Write to file
		synchronized(log) {
			try {
				FileWriter writeUser = new FileWriter(log, true);

				writeUser.append("\nREQUEST:\n" + "\tDate of exchange: " + DSMSApp.Date.getCurrentDate()
				+ "\n\tType: return" + "\n\tSubmitted by: " + customerID
				+ "\n\tOld Item ID: " + oldItemID
				+ "\n\tNew Item ID: " + newItemID
						);
				writeUser.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeUser.close();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		return status;
	}


}
