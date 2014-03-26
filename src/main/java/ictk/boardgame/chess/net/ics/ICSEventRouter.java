/*
 * ictk - Internet Chess ToolKit
 * More information is available at http://jvarsoke.github.io/ictk
 * Copyright (c) 1997-2014 J. Varsoke <ictk.jvarsoke [at] neverbox.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package ictk.boardgame.chess.net.ics;
import ictk.boardgame.chess.net.ics.event.*;
import java.util.HashMap;

/** Routes ICSEvent messages to ICSEventListeners. It is possible to
 *  use one router for many live connections to different servers. At least
 *  that's an intended goal.
 */
public class ICSEventRouter {
      /** key offset for integers so they can be put in the hash */
   protected static int OFFSET = 1000;
      /** the default listener receives all events or those not sent to a 
       ** exclusive listener (depending on set options). */
   ICSEventListener defaultListener;
      /** subscribers to ICSEvents */
   protected ICSEventListener[][] subscribers;

      /** subscribers to individual channels.
       ** the key is an int[2] with [0] set to the ChannelType (shout, TCh etc)
       ** and the [1] set the the channel number,
       ** the value is a ICSEventListener[] */
   protected HashMap<Integer, ICSEventListener[]> chSubscribers;

      /** determines if the channel(shout, tournament channel,  etc) 
       ** events will be sent only to the listener subscribing
       ** specifically to the channel number.  If this is false
       ** the CHANNEL_EVENT or SHOUT_EVENT listener will also 
       ** receive the event. */
   protected HashMap<Integer, Boolean> chExclusive;

      /** a list of which events are exclusively listed to by the 
       ** subscriber (event if null) instead of also being sent 
       ** to the defaultListener */
   protected boolean[] exclusive;


   public ICSEventRouter () {
      subscribers = new ICSEventListener[ICSEvent.NUM_EVENTS][];
      chSubscribers = new HashMap<>();
      chExclusive = new HashMap<>();
      exclusive = new boolean[ICSEvent.NUM_EVENTS];
   }

   /* setDefaltListener *****************************************************/
   /** This listener receives all message that are not exclusively
    *  processed by some other listener.  Uncategorized output is also
    *  sent to the default listener.
    */
   public void setDefaultListener (ICSEventListener eh) {
      defaultListener = eh;
   }

   /* getDefaltListener *****************************************************/
   /** This listener receives all message that are not exclusively
    *  processed by some other listener.  Uncategorized output is also
    *  sent to the default listener.
    */
   public ICSEventListener getDefaultListener () {
      return defaultListener;
   }

   /* dispatch **************************************************************/
   /** an event sent to this method will be relayed to all the listeners
    *  who are interested in it.
    */
   public void dispatch (ICSEvent evt) {
      Integer key = null;
      int type = evt.getEventType(),
          i = 0;
      ICSEventListener[] listeners = null;
      boolean done = false,
              done2 = false;

      switch (type) {
         //channel events
         case ICSEvent.CHANNEL_EVENT:
         case ICSEvent.TOURNAMENT_CHANNEL_EVENT:
         case ICSEvent.SHOUT_EVENT:
	    key = new Integer(type * OFFSET 
	                      + ((ICSChannelEvent) evt).getChannel());

	    listeners = chSubscribers.get(key);
	    done = (listeners != null) 
	         && isChannelExclusive(type, 
		       ((ICSChannelEvent) evt).getChannel());

	    break;

         //board events
	 case ICSEvent.BOARD_UPDATE_EVENT:
	 case ICSEvent.KIBITZ_EVENT:
	 case ICSEvent.WHISPER_EVENT:
	 case ICSEvent.BOARD_SAY_EVENT:

	 default:
	    break;
      }

      //send to the specific listerners from the switch
      if (listeners != null)
         for (i = 0; i < listeners.length; i++)
	    listeners[i].icsEventDispatched(evt);
       
      //send to the event subscribers if the switch listener wasn't exclusive
      if (!done 
          && (listeners = subscribers[type]) != null) {
         for (i=0; i < listeners.length; i++)
	    listeners[i].icsEventDispatched(evt);
	 done2 = true;
      }

      //send to the default route if this event isn't exclusive
      if (!exclusive[type] && defaultListener != null)
         defaultListener.icsEventDispatched(evt);
   }

   /* addEventListener ******************************************************/
   /** tells the router that this listener would like to hear a particular
    *  type of event.
    *
    *  @param icsEventNumber an ICSEvent.<FOO>_EVENT
    */
   public void addEventListener (int icsEventNumber,
                                 ICSEventListener eh) {

      subscribers[icsEventNumber] 
         = _addListener(subscribers[icsEventNumber], eh);
   }

   /* setExclusive **********************************************************/
   /** should the event be routed only to listeners subscribed to this
    *  event, or also to the default listener.  
    *
    *  @param t if true then the default listener will not receive the 
    *           event even if there are no listeners for this event.
    */
   public void setExclusive (int eventType, boolean t) {
      exclusive[eventType] = t;
   }

   /* isExclusive ***********************************************************/
   public boolean isExclusive (int eventType) {
      return exclusive[eventType];
   }

   /* addBoardListener ******************************************************/
   /** adding this type of listener will subscribe the listener to
    *  the following types of events for this board number:<br>
    *  type 1: board updates, moves forward and back, resignations<br>
    *  type 2: takeback requests, draw offers, adjourn and pause requests<br>
    *  type 3: kibitzes, whispers, and board says<br>
    *  NOTE: all types must be registered independently
    */
   public void addBoardListener (ICSEventListener eh,
                                 int boardNumber,
				 int type) {
   }

   /* addChannelListener ***************************************************/
   /** adds a listener to a particular channel.  This is useful if you want
    *  to log particular channels, or send them to different display 
    *  areas.  If a listener wish to listen to all channel events then
    *  it would be better to subscribe via addEventListener().
    *
    *  @param channelType is the EventType for this sort of channel.  For
    *                     example: ICSEvent.CHANNEL_EVENT is for normal
    *                     channel tells, ICSEvent.SHOUT_EVENT is for
    *                     shouts.
    *  @param channelNumber is number of the channel, or in the case of 
    *                     shouts is the type of shout.
    */
   public void addChannelListener (int channelType,
                                   int channelNumber,
				   ICSEventListener eh) {

      Integer key = new Integer(channelType * OFFSET + channelNumber);
      ICSEventListener[] list;

      list = chSubscribers.get(key);
      list = _addListener(list, eh);

      chSubscribers.put(key, list);
   }

   /* removeChannelListener *************************************************/
   /** removes a listener to a particular channel.
    *
    *  @param channelType is the EventType for this sort of channel.  For
    *                     example: ICSEvent.CHANNEL_EVENT is for normal
    *                     channel tells, ICSEvent.SHOUT_EVENT is for
    *                     shouts.
    *  @param channelNumber is number of the channel, or in the case of 
    *                     shouts is the type of shout.
    */
   public void removeChannelListener (int channelType,
                                   int channelNumber,
				   ICSEventListener eh) {
      Integer key = new Integer(channelType * OFFSET + channelNumber);
      ICSEventListener[] list;

      list = chSubscribers.get(key);
      list = _removeListener(list, eh);

      if (list == null)
         chSubscribers.remove(key);
      else
         chSubscribers.put(key, list);
   }

   /* isChannelExclusive ****************************************************/
   /** are channel events for this channel only routed to this channel's
    *  listener(s), or are they also send to the CHANNEL_EVENT listener.
    *  This setting has no bearing on whether the defaultListener
    *  receives the event or not.
    */
   public void setChannelExclusive (int channelType,
                                    int channelNumber,
				    boolean t) {
      Integer key = new Integer(channelType * OFFSET + channelNumber);
      chExclusive.put(key, ((t) ? Boolean.TRUE : Boolean.FALSE));
   }

   /* isChannelExclusive ****************************************************/
   /** are channel events for this channel only routed to this channel's
    *  listener(s), or are they also send to the CHANNEL_EVENT listener.
    *  This setting has no bearing on whether the defaultListener
    *  receives the event or not.
    */
   public boolean isChannelExclusive (int channelType,
                                      int channelNumber) {
      Integer key = new Integer(channelType * OFFSET + channelNumber);
      Boolean b = null;
      return ((b = chExclusive.get(key)) != null) 
             && b == Boolean.TRUE;
   }

   /* _addListener **********************************************************/
   protected ICSEventListener[] _addListener (ICSEventListener[] list,
                                              ICSEventListener   evt) {
      ICSEventListener[] tmp = null;

      if (list == null) {
         tmp = new ICSEventListener[1];
	 tmp[0] = evt;
      }
      else {
         tmp = new ICSEventListener[list.length+1];
	 System.arraycopy(list, 0, tmp, 0, list.length);
	 tmp[list.length] = evt;
      }
      return tmp;
   }

   /* _removeListener *******************************************************/
   protected ICSEventListener[] _removeListener (ICSEventListener[] list,
                                                 ICSEventListener   evt) {
      ICSEventListener[] tmp = null; 
      if (list != null && list.length > 1) {
         tmp = new ICSEventListener[list.length - 1];
	 int count = 0;
	 for (int i=0; i < list.length; i++)
	    if (list[i] != evt)
	       tmp[count++] = list[i];
      }
      return tmp;
   }
						 
}
