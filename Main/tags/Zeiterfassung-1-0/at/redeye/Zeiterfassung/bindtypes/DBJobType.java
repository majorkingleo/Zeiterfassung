/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import java.util.Vector;

import at.redeye.FrameWork.base.bindtypes.DBEnum;
import at.redeye.FrameWork.base.bindtypes.DBFlagJaNein;
import at.redeye.FrameWork.base.bindtypes.DBHistory;
import at.redeye.FrameWork.base.bindtypes.DBInteger;
import at.redeye.FrameWork.base.bindtypes.DBString;
import at.redeye.FrameWork.base.bindtypes.DBStrukt;
import at.redeye.FrameWork.base.bindtypes.DBEnum.EnumHandler;

/**
 *
 * @author martin
 */
public class DBJobType extends DBStrukt 
{
    public static enum JOBTYPES
    {
        LZ,
        NLZ
    };
           
    
    public static class JobEnumHandler extends DBEnum.EnumHandler
    {
        JOBTYPES types;

        public JobEnumHandler()
        {
            types = types.LZ;
        }
        
        @Override
        public int getMaxSize() {
            int max=0;
            
            for( JOBTYPES val :JOBTYPES.values() )
            {
                if( max < val.toString().length() )
                    max = val.toString().length();
            }
            
            return max;
        }

        @Override
        public boolean setValue(String val) {
            try {
                types  = JOBTYPES.valueOf(val);
            } catch( IllegalArgumentException ex ) {
                return false;
            }
            return true;
        }

        @Override
        public String getValue() {
            return types.toString();
        }

        @Override
        public EnumHandler getNewOne() {
            return new JobEnumHandler();
        }

        @Override
        public Vector<String> getPossibleValues() {
            
            Vector<String> res = new Vector<String>();
            
            for( JOBTYPES val :JOBTYPES.values() )
                res.add(val.toString());
            
            return res;
        }
        
    }
    
    public DBInteger  id     = new DBInteger( "id", "Id" );
    public DBEnum     type   = new DBEnum( "type", "Typ", new JobEnumHandler() );
    public DBString   name   = new DBString( "name", "Name", 20 );
    public DBString   help   = new DBString( "help", "Hilfetext", 20 );
    public DBFlagJaNein     locked = new DBFlagJaNein( "locked", "gesperrt" );
    public DBHistory  hist   = new DBHistory( "hist" );

    public DBJobType()
    {
        super( "JOBTYPE" );
        
        add( id );
        add( type );
        add( name );
        add( help );
        add( locked );
        add( hist );
        
        id.setAsPrimaryKey();
    }
    
    @Override
    public DBStrukt getNewOne() {
       return new DBJobType();
    }

}
