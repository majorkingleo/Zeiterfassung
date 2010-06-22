/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBSqlAsInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBValue;

/**
 *
 * @author martin
 */
public class DBCustomerAddresses extends DBStrukt implements NameIdLockedInterface
{
      public DBInteger id        = new DBInteger("id");
      public DBValue   customer  = new DBInteger("customer", "Kunde" );
      public DBString  name      = new DBString( "name", "Name", 50);
      public DBString  state     = new DBString( "state", "Land", 100 );
      public DBString  country   = new DBString( "country", "Bundesland", 100 );
      public DBString  city      = new DBString( "city", "Stadt", 100 );
      public DBString  street    = new DBString( "street", "Straße", 300 );
      public DBString  postcode  = new DBString( "postcode", "PLZ", 50 );
      public DBString  tel       = new DBString( "tel", "Telefonnummer", 50 );
      public DBString  mobile    = new DBString( "mobile", "Mobiltelefonnummer", 50 );
      public DBString  email     = new DBString( "email", "Email", 200 );
      public DBString  contact   = new DBString( "contact", "Ansprechpartner", 100 );
      public DBString  company   = new DBString( "company", "Firma", 100 );
      public DBFlagJaNein locked = new DBFlagJaNein( "locked", "Gesperrt" );
      public DBHistory hist      = new DBHistory("hist" );

      public static String table_name = "CUSTOMERADDRESSES";

      public DBCustomerAddresses( DBSqlAsInteger.SqlQuery query_customer )
      {
        super( table_name );

        customer = new DBSqlAsInteger( "customer", "Kunde", query_customer );

        add_all();
      }

      public DBCustomerAddresses()
      {
        super( table_name );

        add_all();
      }

      private void add_all()
      {
         add(id);
         add(customer);
         add(name);
         add(state);
         add(country);
         add(city);
         add(street);
         add(postcode);         
         add(mobile);
         add(email);
         add(contact);
         add(company);
         add(tel);
         add(locked);
         add(hist);

         id.setAsPrimaryKey();
      }

    @Override
    public DBStrukt getNewOne() {

        if( customer instanceof DBInteger )
            return new DBCustomerAddresses();
        else
            return new DBCustomerAddresses(((DBSqlAsInteger)customer).query );
    }

    public String getNameName() {
        return name.getName();
    }

    public String getNameValue() {
        return name.getValue();
    }

    public String getIdName() {
        return id.getName();
    }

    public Integer getIdValue() {
        return id.getValue();
    }

    public String getLockedName() {
        return locked.getName();
    }
}
