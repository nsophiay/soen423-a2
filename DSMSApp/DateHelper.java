package DSMSApp;


/**
* DSMSApp/DateHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DSMS.idl
* Thursday, October 15, 2020 12:45:40 o'clock AM EDT
*/

abstract public class DateHelper
{
  private static String  _id = "IDL:DSMSApp/Date:1.0";

  public static void insert (org.omg.CORBA.Any a, DSMSApp.Date that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DSMSApp.Date extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [3];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
          _members0[0] = new org.omg.CORBA.StructMember (
            "Day",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
          _members0[1] = new org.omg.CORBA.StructMember (
            "Month",
            _tcOf_members0,
            null);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
          _members0[2] = new org.omg.CORBA.StructMember (
            "Year",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (DSMSApp.DateHelper.id (), "Date", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DSMSApp.Date read (org.omg.CORBA.portable.InputStream istream)
  {
    DSMSApp.Date value = new DSMSApp.Date ();
    value.Day = istream.read_short ();
    value.Month = istream.read_short ();
    value.Year = istream.read_short ();
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DSMSApp.Date value)
  {
    ostream.write_short (value.Day);
    ostream.write_short (value.Month);
    ostream.write_short (value.Year);
  }

}
