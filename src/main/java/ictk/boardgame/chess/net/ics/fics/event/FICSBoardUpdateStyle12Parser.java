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

package ictk.boardgame.chess.net.ics.fics.event;

import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;

/* FICSBoardUpdateStyle12Parser **********************************************/
/** This class parses ICS Style12 Board Update events.
 *  <br>
 * <pre>
 * <12> r-bq-rk- pp-nppbp -----np- --pp---- ---P---- --PBPN-- PP-N-PPP R-BQR-K- B -1 0 0 0 0 2 2 bbbb Simpan -2 3 0 39 39 177810 171259 8 R/f1-e1 (0:00.000) Re1 0 0 0
 * </pre>
 */
public class FICSBoardUpdateStyle12Parser extends ICSEventParser {

   //static/////////////////////////////////////////////////////////////////
   public static FICSBoardUpdateStyle12Parser singleton;
   public static final Pattern masterPattern;

   static {
/**Style 12: <12> rnbqkb-r pppppppp -----n-- -------- ----P--- -------- PPPPKPPP RNBQ-BNR B -1 0 0 1 1 0 7 Newton Einstein 1 2 12 39 39 119 122 2 K/e1-e2 (0:06) Ke2 0 1 123

//with iset ms 1  (time in milliseconds)
<12> r-bq-rk- pp-nppbp -----np- --pp---- ---P---- --PBPN-- PP-N-PPP R-BQR-K- B -1 0 0 0 0 2 2 bbbb Simpan -2 3 0 39 39 177810 171259 8 R/f1-e1 (0:00.000) Re1 0 0 0
 **/
      masterPattern = Pattern.compile("^:?(<12>\\s" //beginning
	 + "([rnbqkpRNBQKP-]{8})"  //8th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //7th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //6th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //5th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //4th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //3th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //2th rank
	 + "\\s"
	 + "([rnbqkpRNBQKP-]{8})"  //1th rank
	 + "\\s"
	 + "([BW])"                //who's move
	 + "\\s"
	 + "(-?[0-7])"             //enpassant file
	 + "\\s"
	 + "([01])"                //white O-O?
	 + "\\s"
	 + "([01])"                //white O-O-O?
	 + "\\s"
	 + "([01])"                //black O-O?
	 + "\\s"
	 + "([01])"                //black O-O-O?
	 + "\\s"
	 + "(\\d+)"                //moves since irreversable
	 + "\\s"
	 + "(\\d+)"                //game number
	 + "\\s"
	 + "([\\w]+)"              //white player
	 + "\\s"
	 + "([\\w]+)"              //black player
	 + "\\s"
	 + "([-]?[0-3])"           //relation to game
	 + "\\s"
	 + "(\\d+)"                //initial time
	 + "\\s"
	 + "(\\d+)"                //incr
	 + "\\s"
	 + "(\\d+)"                //white's material
	 + "\\s"
	 + "(\\d+)"                //black's material
	 + "\\s"
	 + "(-?\\d+)"              //white's time
	 + "\\s"
	 + "(-?\\d+)"              //black's time
	 + "\\s"
	 + "(\\d+)"                //move number
	 + "\\s"
	 + "(\\S+)"                //last move
	 + "\\s"
	 + "\\((\\d+)"             //last move minutes
	 + ":(\\d+)"               //          seconds
	 + "\\.?(\\d+)?\\)"        //    milli-seconds
	 + "\\s"
	 + "(\\S+)"                //SAN last move
	 + "\\s"
	 + "([01])"                //flip board?
	 + "\\s"
	 + "([01])"                //clock moving
	 + "\\s"
	 + "(\\d+)"                //timeseal delta
	 + ")"                     //end match
          , Pattern.MULTILINE);

      singleton = new FICSBoardUpdateStyle12Parser();
   }

   //instance///////////////////////////////////////////////////////////////
   protected FICSBoardUpdateStyle12Parser () {
      super(masterPattern);
   }

   /* getInstance ***********************************************************/
   public static ICSEventParser getInstance () {
       return singleton;
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSBoardUpdateEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSBoardUpdateEvent evt = (ICSBoardUpdateEvent) event;

      if (Log.debug && debug)
         Log.debug(DEBUG, "assigning matches", m);

      int rank = 7;
      int gr = 2;
      String srank = null;
      char[][] board = new char[8][8];

      while (rank < 8 && rank >= 0) {
         srank = m.group(gr);
	 for (int file=0; file < 8; file++) {
	    if (srank.charAt(file) != '-')
	       board[file][rank] = srank.charAt(file);
	    else
	       board[file][rank] = ' ';
	 }
	 rank--;
	 gr++;
      }
      evt.setBoardArray(board);

      if (m.group(10).charAt(0) == 'B')
         evt.setBlackMove(true);

      evt.setWhitePlayer(m.group(18));
      evt.setBlackPlayer(m.group(19));

      evt.setWhiteCastleableKingside(m.group(12).charAt(0) == '1');
      evt.setWhiteCastleableQueenside(m.group(13).charAt(0) == '1');
      evt.setBlackCastleableKingside(m.group(14).charAt(0) == '1');
      evt.setBlackCastleableQueenside(m.group(15).charAt(0) == '1');

      if (!"none".equals(m.group(28)))
         evt.setVerboseMove(m.group(28));

      if (!"none".equals(m.group(32)))
         evt.setSAN(m.group(32));

      evt.setFlipBoard(m.group(33).charAt(0) == '1');
      evt.setClockMoving(m.group(34).charAt(0) == '1');

      //numbers
      int i = 0;
      try {
         evt.setEnPassantFile(
	    Integer.parseInt(m.group(i = 11)) + 1); //enpassant
	 evt.setPlySinceLastIrreversableMove(
	    Integer.parseInt(m.group(i = 16))); //100-ply rule
	 evt.setBoardNumber(Integer.parseInt(m.group(i = 17)));
	 evt.setRelation(Integer.parseInt(m.group(i=20)));
	 evt.setInitialTime(Integer.parseInt(m.group(i=21)));
	 evt.setIncrement(Integer.parseInt(m.group(i=22)));
	 evt.setWhiteMaterial(Integer.parseInt(m.group(i=23)));
	 evt.setBlackMaterial(Integer.parseInt(m.group(i=24)));
	 evt.setWhiteClock(Integer.parseInt(m.group(i=25)));
	 evt.setBlackClock(Integer.parseInt(m.group(i=26)));
	 evt.setMoveNumber(Integer.parseInt(m.group(i=27)));

	 evt.setMoveTime(Integer.parseInt(m.group(i=29)) * 60000
	          + Integer.parseInt(m.group(i=30)) * 1000
	          + Integer.parseInt(m.group(i=31))
		  );
	 evt.setLag(Integer.parseInt(m.group(i=35)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
	   "threw NumberFormatException"
	   + "for(" + i + "): " + m.group(i));
	 evt.setEventType(ICSEvent.UNKNOWN_EVENT);
	 evt.setMessage(m.group(0));
	 if (Log.debug)
	    Log.debug(ICSEventParser.DEBUG, "regex", m);
	 return;
      }
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSBoardUpdateEvent evt = (ICSBoardUpdateEvent) event;
      StringBuffer sb = new StringBuffer(180);
      
      if (evt.isFake()) sb.append(":");

      sb.append("<12> ");

      //board
      char[][] board = evt.getBoardArray();

      for (int r=7; r >= 0; r--) {
         for (int f=0; f < board.length; f++) {
	    if (board[f][r] == ' ')
	       sb.append('-');
	    else
	       sb.append(board[f][r]);
	 }
	 sb.append(' ');
      }

      //color to play
      if (evt.isBlackMove())
         sb.append('B');
      else
         sb.append('W');

      sb.append(" ")
        .append(evt.getEnPassantFile() -1)
	.append((evt.isWhiteCastleableKingside()) ? " 1" : " 0")
	.append((evt.isWhiteCastleableQueenside()) ? " 1" : " 0")
	.append((evt.isBlackCastleableKingside()) ? " 1" : " 0")
	.append((evt.isBlackCastleableQueenside()) ? " 1" : " 0")
	.append(" ")
	.append(evt.getPlySinceLastIrreversableMove())
	.append(" ")
	.append(evt.getBoardNumber())
	.append(" ")
	.append(evt.getWhitePlayer())
	.append(" ")
	.append(evt.getBlackPlayer())
	.append(" ")
	.append(evt.getRelation())
	.append(" ")
        .append(evt.getInitialTime())
        .append(" ")
        .append(evt.getIncrement())
	.append(" ")
	.append(evt.getWhiteMaterial())
	.append(" ")
	.append(evt.getBlackMaterial())
	.append(" ")
	.append(evt.getWhiteClock())
	.append(" ")
	.append(evt.getBlackClock())
	.append(" ")
	.append(evt.getMoveNumber())
	.append(" ");

      if (evt.getVerboseMove() == null)
         sb.append("none");
      else
	sb.append(evt.getVerboseMove());

      sb.append(" (")
	.append(getClockAsString(evt.getMoveTime(), true))
	.append(") ");

      if (evt.getSAN() == null) 
         sb.append("none");
      else
         sb.append(evt.getSAN());

      sb.append((evt.isFlipBoard()) ? " 1" : " 0")
	.append((evt.isClockMoving()) ? " 1 " : " 0 ")
	.append(evt.getLag());

      return sb.toString();
   }

   protected String getClockAsString (int clock, boolean move) {
      StringBuffer sb = new StringBuffer(7);
      int h, m, s, ms;

      h = clock / 3600000;
      m = (clock % 3600000) / 60000;
      s = (clock % 60000) / 1000;
      ms = clock % 1000;

      if (move && h > 1) {
         sb.append(h).append(":");
         if (m < 10)
            sb.append(0);
      }
      sb.append(m).append(":");
      if (s < 10)
         sb.append(0);
      sb.append(s).append(".");
      if (ms < 100)
         sb.append(0);
      if (ms < 10)
         sb.append(0);
      sb.append(ms);
      return sb.toString();
   }


}
