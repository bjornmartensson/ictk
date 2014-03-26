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

package ictk.boardgame.chess.net.ics.event;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* ICSEventParser ***********************************************************/
/** Parses server messages and produces ICSEvent objects.  All parsers 
 *  implement the Singleton design pattern. 
 */
public abstract class ICSEventParser {
   public static final long DEBUG = Log.ICSEventParser;
   
   public static final String 
         /*Sun Nov  3, 21:11 CET 2002*/
      REGEX_date      = "((\\w{3})\\s(\\w{3})\\s+"
                      + "(\\d+),\\s(\\d+):(\\d{2})\\s"
		      + "(\\w+)\\s(\\d{4}))";

      /** each event has a type for easy casting.  This is stored in
       ** the parser, so the parser knows what event type it is 
       ** generating. */
   protected int eventType = ICSEvent.UNKNOWN_EVENT;

      /** this is a link to the masterPattern, which is a static 
       ** variable for all EventParsers.  This variable just allows
       ** the masterPattern to be used in instance methods and work
       ** appropriately with inheritance. */
   protected Pattern pattern;

      /** set debugging on for this instance of the parser */
   protected boolean debug;

   //constructor//////////////////////////////////////////////////////////////
   protected ICSEventParser (Pattern master) {
      pattern = master;
   }

   /* setDebug **************************************************************/
   /** turns debugging on for this parser.  You must also set Log.debug to
    *  true and Log.addMask() to see debugging output.
    */
   public void setDebug (boolean t) { debug = t; }

   /* getPattern ************************************************************/
   /** returns the pattern being used for this EventParser
    */
   public Pattern getPattern () {
      return pattern;
   }

   /* getEventType **********************************************************/
   /** returns the event type this parser corrisponds to
    */
   public int getEventType () {
      return eventType;
   }

   /* match *****************************************************************/
   /** This method detects if the input CharSequence is a message of the
    *  type this parser can handle.  If the return is !null then the 
    *  Matcher can be passed to createICSEvent(Matcher) to get the 
    *  ICSEvent objects form of this messasge.  You only need use this
    *  two step process (instead of just createICSEvent(CharSequence) if
    *  you need to know where the match started and ended in the 
    *  CharSequence.
    *
    *  @param s is the CharSequence containing the Event this parser handles
    *  @return null if there is no match
    *  @return !null if there is a match
    */
   public Matcher match (CharSequence s) {
      Matcher m = pattern.matcher(s);

      if (m.find()) {
         if (Log.debug && debug) 
	    Log.debug(DEBUG, "matched: " + s, m);
         return m;
      }
      else {
         if (Log.debug && debug) 
	    Log.debug(DEBUG, "failed: " + s);
         return null;
      }
   }

   /* createICSEvent ********************************************************/
   /** Instantiates a new ICSEvent of the appropriate type for this parser
    *  if and only if the CharSequence matches something this parser can
    *  understand.
    *
    *  @return null if this CharSequence is not parsable by this parser
    */
   public ICSEvent createICSEvent (CharSequence s) {
      Matcher m = match(s);
      if (m != null)
         return createICSEvent(m);
      else
         return null;
   }

   /* createICSEvent ********************************************************/
   /** Instantiantes a new ICSEvent of the appropriate type for this
    *  parser.  If the Matcher passed in is not from this parser the results
    *  are undefined.
    */
   public abstract ICSEvent createICSEvent (Matcher match);

   /* assignMatches *********************************************************/
   /** Takes the data from the Matcher object and assigns them to the 
    *  data fields of the ICSEvent object.  This will clear all data in the
    *  ICSEvent object if there was any to begin with.  This function can
    *  be used if you wish to recycle ICSEvent objects for some reason.
    *
    *  @throws exceptions if you pass in the wrong type of ICSEvent for this
    *  parser, or a bad matcher.  This is obviously loose coupling 
    *  --caveat programmer
    */
   public abstract void assignMatches (Matcher m, ICSEvent evt);

   /* detectFake ************************************************************/
   /** Is this ICSEvent something faked by a Bot?
    */
   public boolean detectFake (CharSequence s) {
      return (s.charAt(0) == ':');
   }

   public abstract String toNative (ICSEvent evt);

   /* parseICSAccountType ***************************************************/
   /** Parses an account type and logs the correct errors if one is
    *  encountered.
    *
    *  @param match - the whole Matcher object (needed for error reporting)
    *  @param index - which match group is the AccountType ?
    *  @return always returns a valid ICSAccountType object, never null
    */
   protected ICSAccountType parseICSAccountType (Matcher match, int index) {
      ICSAccountType acct = null;
         try {
            if (match.group(index) != null) 
               acct = new ICSAccountType(match.group(index));
	    else
	       acct = new ICSAccountType();
         }
         catch (IOException e) {
            Log.error(Log.PROG_WARNING,
               "Can't parse account type: "
               + match.group(index) + " of " + match.group(0));
            acct = new ICSAccountType();
	    if (Log.debug && debug)
	       Log.debug(DEBUG, "regex:", match);
         }
      return acct;
   }

   /* parseICSRating *********************************************************/
   /** Parses a rating and logs the correct errors if one is encountered.
    *
    *  @param match - the whole Matcher object (needed for error reporting)
    *  @param index - which match group is the Rating
    *  @return null - if no rating was encountered or an error occured
    */
   protected ICSRating parseICSRating (Matcher match, int index) {
      ICSRating rating = null;

	 try {
	    if (match.group(index) != null)
	       rating = new ICSRating(match.group(index));
	 }
	 catch (NumberFormatException e) {
	    Log.error(Log.PROG_WARNING, 
	      "Can't parse rating" 
	      + match.group(index) + " of " + match.group(0));
	    if (Log.debug && debug)
	       Log.debug(DEBUG, "regex:", match);
	 }
      return rating;
   }
}
