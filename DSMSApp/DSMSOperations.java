package DSMSApp;

/**
* DSMSApp/DSMSOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DSMS.idl
* Thursday, October 15, 2020 12:45:40 o'clock AM EDT
*/

public interface DSMSOperations 
{
  boolean addItem (String managerID, String itemID, String itemName, short quantity, double price);
  boolean removeItem (String managerID, String itemID, short quantity);
  String listItemAvailability (String managerID);
  double purchaseItem (String customerID, String itemID, DSMSApp.Date dateOfPurchase);
  String findItem (String customerID, String itemName);
  double returnItem (String customerID, String itemID, DSMSApp.Date dateOfReturn);
  boolean exchangeItem (String customerID, String newItemID, String oldItemID);
  void shutdown();
} // interface DSMSOperations
