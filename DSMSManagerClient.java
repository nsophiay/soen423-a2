package a2;

import java.io.File; 
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.omg.CosNaming.*;
import org.omg.CORBA.*;

import DSMSApp.*;

public class DSMSManagerClient {

	String managerID;
	String store;
	File log;
	DSMS dsmsServant;
	private ORB orb;

	public DSMSManagerClient(String[] args, String ID) {
		if(ID.charAt(2) != 'M') {
			System.out.println("Manager IDs must be in the format: [provinceAcronym]M[4-digit ID]");
			return;
		}
		managerID = ID;
		store = ID.substring(0,2);
		try {
			log = new File(ID + "log.txt");
			if(log.createNewFile()) {
				System.out.println("File created for manager #" + ID);
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


	public String add(String itemID, String itemName, short quantity, double price) {

		boolean status = false;

		status = (dsmsServant.addItem(this.managerID, itemID, itemName, quantity, price)).equals("true");


		// Write to file
		synchronized(log) {
			try {
				FileWriter writeManager = new FileWriter(log, true);
				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: add" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem name: " + itemName
						+ "\n\tItem quantity: " + quantity
						+ "\n\tItem price: " + price);
				writeManager.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeManager.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return status?"success,"+itemID+","+itemName+","+quantity+","+price:"failure,"+itemID+","+itemName+","+quantity+","+price;
	}

	public String remove(String itemID, short quantity) {
		boolean status = false;

		status = dsmsServant.removeItem(this.managerID, itemID, quantity).equals("true");

		// Write to file
		synchronized(log) {
			try {

				FileWriter writeManager = new FileWriter(log, true);
				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: remove" + "\n\tSubmitted by: " + managerID
						+ "\n\tItem ID: " + itemID
						+ "\n\tItem quantity: " + quantity);
				writeManager.append(status?"\n\tStatus: successful":"\n\tStatus: unsuccessful\n");
				writeManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return status?"success,"+itemID+","+quantity:"failure,"+itemID+","+quantity;
	}
	public String list() {

		String result = "success(";
		String items = dsmsServant.listItemAvailability(this.managerID);
		String[] parseItems = items.split(":\\s|\n");

		System.out.println(items);
		
		for(int i = 0; i < parseItems.length; i+=9) {
			result += "\n" + parseItems[i+1] + "," + parseItems[i+3]
					+ "," + parseItems[i+7] + "," + parseItems[i+5];
		}
		
		result += ")";

		// Write to file
		synchronized(log) {
			try {
				FileWriter writeManager = new FileWriter(log, true);

				writeManager.append("\nREQUEST:\n" + "\tDate: " + new Date()
						+ "\n\tType: list inventory" + "\n\tSubmitted by: " + managerID);
				writeManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}


}
