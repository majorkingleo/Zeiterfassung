/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.*;
import java.util.Vector;

/**
 *
 * @author martin
 */
public class DBOvertimeRule extends DBEnum 
{
    public static enum SCHEMAS
    {
        KEINES,
        PAUSCHALIST,
        ÜBERSTUNDENSCHEMA_01,
        FLATRATE_SFR_01,
    };
           
    
    public static class FlagEnumHandler extends DBEnum.EnumHandler
    {
        SCHEMAS types;

        public FlagEnumHandler()
        {
            types = types.KEINES;
        }
        
        @Override
        public int getMaxSize() {
            int max=0;
            
            for( SCHEMAS val : SCHEMAS.values() )
            {
                if( max < val.toString().length() )
                    max = val.toString().length();
            }
            
            return max;
        }

        @Override
        public boolean setValue(String val) {
            try {
                types  = SCHEMAS.valueOf(val);
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
            return new FlagEnumHandler();
        }

        @Override
        public Vector<String> getPossibleValues() {
            Vector<String> res = new Vector<String>();
            
            for( SCHEMAS t : SCHEMAS.values() )
                res.add( t.toString() );
            
            return res;
        }
    }
    
    public DBOvertimeRule( String name, String title )
    {
        super( name, title, new FlagEnumHandler() );
    }

    public DBOvertimeRule getNewOne()
    {
        return new DBOvertimeRule( name, title );
    }
}
