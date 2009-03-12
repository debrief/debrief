/** TimerListener.java
* 
* Copyright (C) 1998  NetBeans, Inc.
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
*/

package MWC.Utilities.Timer;

/** The TimerListener interface must be implemented by
* a class that wants to be notified about time events.
*
* @version  1.00, Jul 20, 1998
*/
public interface TimerListener extends java.util.EventListener {

  /** Called when a new timer event occurs */
  public void onTime (java.awt.event.ActionEvent event);

}

