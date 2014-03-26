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

import java.util.regex.Matcher;
import java.util.StringTokenizer;
import java.io.IOException;

/* ICSAccountType ************************************************************/
/** Every user on an ICS system may have different account attributes 
 *  associated with it.  These attributes are held in this object as 
 *  flags for the account.  Some flags are mutually exclusive on the 
 *  server, but this object does not take that into account.
 */
public class ICSAccountType {

   public final static int UNREGISTERED        = 0,
                           COMPUTER            = 1,
                           BLIND               = 2,
			   TEAM                = 3,
			   ADMIN	       = 4,
			   SERVICE_REP         = 5,
			   HELPER              = 6,
			   CHESS_ADVISOR       = 7,
			   TOURNAMENT_MANAGER  = 8,
			   TOURNAMENT_DIRECTOR = 9,
			   DISPLAY_MANAGER     = 10,
			   WFM		       = 11,
			   FM                  = 12,
			   WIM                 = 13,
			   IM                  = 14,
			   WGM                 = 15,
			   GM                  = 16,
			   DEMO                = 17,
			   NUM_FLAGS	       = 18;

   protected boolean[] flag;

   public ICSAccountType () {
      flag = new boolean[NUM_FLAGS];
   }

   public ICSAccountType (String s) throws IOException {
      this();
      if (s != null)
         set(s);
   }

   /* set ********************************************************************/
   /** sets a flag either on or off.
    */
   public void set (int type, boolean t) {
      flag[type] = t;
   }

   /* is *********************************************************************/
   /** tests to see if a particular lfag is set to true or not.
    */
   public boolean is (int type) {
      return flag[type];
   }

   /* set ********************************************************************/
   /** converts a string in the format "(SR)(TM)" to set the currect 
    *  flags.
    *
    *  @throws IOException if an account type isn't recognized.
    */
   public void set (String s) throws IOException {
      StringTokenizer st = new StringTokenizer(s, "()", false);
      String tok = null;

      while (st.hasMoreTokens()) {
         tok = st.nextToken();
	 if ("U".equals(tok)) flag[UNREGISTERED] = true;
	 else if ("*".equals(tok)) flag[ADMIN] = true;
	 else if ("C".equals(tok)) flag[COMPUTER] = true;
	 else if ("SR".equals(tok)) flag[SERVICE_REP] = true;
	 else if ("H".equals(tok)) flag[HELPER] = true;
	 else if ("TD".equals(tok)) flag[TOURNAMENT_DIRECTOR] = true;
	 else if ("TM".equals(tok)) flag[TOURNAMENT_MANAGER] = true;
	 else if ("FM".equals(tok)) flag[FM] = true;
	 else if ("IM".equals(tok)) flag[IM] = true;
	 else if ("B".equals(tok)) flag[BLIND] = true;
	 else if ("T".equals(tok)) flag[TEAM] = true;
	 else if ("D".equals(tok)) flag[DEMO] = true;
	 else if ("WIM".equals(tok)) flag[WIM] = true;
	 else if ("WFM".equals(tok)) flag[WGM] = true;
	 else if ("WGM".equals(tok)) flag[WGM] = true;
	 else if ("GM".equals(tok)) flag[GM] = true;
	 else if ("CA".equals(tok)) flag[CHESS_ADVISOR] = true;
	 else if ("DM".equals(tok)) flag[DISPLAY_MANAGER] = true;
	 else 
	    throw new IOException ("Unrecognized account type: " + tok);
      }
   }

   /* typeToString ***********************************************************/
   /** converts a flag into its string representation. For example:
    *  SERVICE_REP will return "SR"
    */
   public static String typeToString (int type) {
      String s = null;
      
      switch (type) {
         case UNREGISTERED:        s = "U"; break;
	 case COMPUTER:            s = "C"; break;
	 case BLIND:		   s = "B"; break;
	 case TEAM:		   s = "T"; break;
	 case ADMIN:               s = "*"; break;
	 case TOURNAMENT_MANAGER:  s = "TM"; break;
	 case TOURNAMENT_DIRECTOR: s = "TD"; break;
	 case SERVICE_REP:         s = "SR"; break;
	 case HELPER:              s = "H"; break;
	 case CHESS_ADVISOR:       s = "CA"; break;
	 case DISPLAY_MANAGER:     s = "DM"; break;
	 case WFM:                 s = "WFM"; break;
	 case FM:                  s = "FM"; break;
	 case WIM:                 s = "WIM"; break;
	 case IM:                  s = "IM"; break;
	 case WGM:                 s = "WGM"; break;
	 case GM:                  s = "GM"; break;
	 case DEMO:		   s = "D"; break;
	 default:
	    System.err.println("Received illegal account type: " + type);
      }
      return s;
   }

   /* toString ***************************************************************/
   /** converts the account flags into their string representation. Such as
    *  "(SR)(TM)"
    */
   public String toString () {
      StringBuffer sb = null;

      for (int i = 0; i < flag.length; i++) {
          if (flag[i]) {
	     if (sb == null)
	        sb = new StringBuffer();
	     sb.append("(").append(typeToString(i)).append(")");
	  }
            
      }
      if (sb == null) return "";
      else   return sb.toString();
   }
}
