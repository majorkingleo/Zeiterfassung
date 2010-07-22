/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.redeye.Zeiterfassung.bindtypes;

import at.redeye.FrameWork.base.bindtypes.DBStrukt;

/**
 *
 * @author martin
 */
public interface NameIdLockedInterface
{
    public String getNameName();
    public String getNameValue();

    public String getIdName();
    public Integer getIdValue();

    public String getLockedName();

    public DBStrukt getNewOne();
}
