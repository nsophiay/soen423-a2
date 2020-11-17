package DSMSApp;


/**
* DSMSApp/DSMSPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DSMS.idl
* Thursday, October 15, 2020 12:45:40 o'clock AM EDT
*/

public abstract class DSMSPOA extends org.omg.PortableServer.Servant
 implements DSMSApp.DSMSOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addItem", new java.lang.Integer (0));
    _methods.put ("removeItem", new java.lang.Integer (1));
    _methods.put ("listItemAvailability", new java.lang.Integer (2));
    _methods.put ("purchaseItem", new java.lang.Integer (3));
    _methods.put ("findItem", new java.lang.Integer (4));
    _methods.put ("returnItem", new java.lang.Integer (5));
    _methods.put ("exchangeItem", new java.lang.Integer (6));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // DSMSApp/DSMS/addItem
       {
         String managerID = in.read_string ();
         String itemID = in.read_string ();
         String itemName = in.read_string ();
         short quantity = in.read_short ();
         double price = in.read_double ();
         boolean $result = false;
         $result = this.addItem (managerID, itemID, itemName, quantity, price);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // DSMSApp/DSMS/removeItem
       {
         String managerID = in.read_string ();
         String itemID = in.read_string ();
         short quantity = in.read_short ();
         boolean $result = false;
         $result = this.removeItem (managerID, itemID, quantity);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  // DSMSApp/DSMS/listItemAvailability
       {
         String managerID = in.read_string ();
         String $result = null;
         $result = this.listItemAvailability (managerID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 3:  // DSMSApp/DSMS/purchaseItem
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         DSMSApp.Date dateOfPurchase = DSMSApp.DateHelper.read (in);
         double $result = (double)0;
         $result = this.purchaseItem (customerID, itemID, dateOfPurchase);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 4:  // DSMSApp/DSMS/findItem
       {
         String customerID = in.read_string ();
         String itemName = in.read_string ();
         String $result = null;
         $result = this.findItem (customerID, itemName);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // DSMSApp/DSMS/returnItem
       {
         String customerID = in.read_string ();
         String itemID = in.read_string ();
         DSMSApp.Date dateOfReturn = DSMSApp.DateHelper.read (in);
         double $result = (double)0;
         $result = this.returnItem (customerID, itemID, dateOfReturn);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       case 6:  // DSMSApp/DSMS/exchangeItem
       {
         String customerID = in.read_string ();
         String newItemID = in.read_string ();
         String oldItemID = in.read_string ();
         boolean $result = false;
         $result = this.exchangeItem (customerID, newItemID, oldItemID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DSMSApp/DSMS:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DSMS _this() 
  {
    return DSMSHelper.narrow(
    super._this_object());
  }

  public DSMS _this(org.omg.CORBA.ORB orb) 
  {
    return DSMSHelper.narrow(
    super._this_object(orb));
  }


} // class DSMSPOA