/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ictk.boardgame.chess.net.ics;
import ictk.boardgame.chess.net.ics.event.*;

public class ICSEventRouter {
      /** the default router receives all events that aren't sent elsewhere*/
   ICSEventListener defaultRoute;
      /** subscribers to ICSEvents */
   protected ICSEventListener[][] subscribers;

      /** subscribers to individual channels.
       ** the key is the channel number,
       ** the value is a ICSEventListener[] */
   protected HashMap chSubscribers,
      /** subscribers to individual boards.
       ** the key is the board number.
       ** the value is a 3 element array of 
       ** ICSEventListener arrays.  The 3 elements
       ** indicate types of events the listener wants*/
                     boardSubscribers;

   public ICSEventRouter () {
      subscribers = new ICSEventListener[ICSEvent.NUM_EVENTS][];
      chSubscribers = new HashMap();
      boardSubscribers = new HashMap();
   }

   public void setDefaultRoute (ICSEventListener eh) {
      defaultRoute = eh;
   }

   public ICSEventListener getDefaultRoute () {
      return defaultRoute;
   }

   public void dispatch (ICSEvent evt) {
      if (subscribers[evt.getEventType()] != null) {
      }
      else {
         defaultRoute.icsEventDispatched(evt);
      }
   }

   /* addEventListener ******************************************************/
   public void addEventListener (ICSEventListener eh,
                                 int icsEventNumber) {
   }

   /* addBoardListener ******************************************************/
   /** adding this type of listener will subscribe the listener to
    *  the following types of events for this board number:<br>
    *  type 1: board updates and moves, resignations<br>
    *  type 2: tackbacks, draw offers, adjourn and pause requests<br>
    *  type 3: kibitzes, whispers, and board says<br>
    *  NOTE: all types must be registered independently
    */
   public void addBoardListener (ICSEventListener eh,
                                 int boardNumber
				 int type) {
   }

   /* addChannelListener ***************************************************/
   public void addChannelListener (ICSEventListener eh,
                                   int channelNumber) {
   }
}