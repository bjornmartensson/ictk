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

package ictk.boardgame.chess.io;

import junit.framework.*;
import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;
import ictk.boardgame.chess.*;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class PGNReaderTest extends TestCase {
   public static String dataDir = "./";
   String pgn_nonvariation = "test_nonvariation.pgn",
          pgn_variation    = "test_variation.pgn",
          pgn_annotation   = "test_annotation.pgn",
	  pgn_bad          = "test_bad.pgn",
	  pgn_debug        = "test_debug.pgn";
   SAN         san;
   ChessBoard  board;
   ChessResult res;
   ChessMove   move;
   ChessReader in;
   Game        game;
   List        games;
   ChessAnnotation anno;

   public PGNReaderTest (String name) {
      super(name);
   }

   public void setUp () {
      san = new SAN();
   }

   public void tearDown () {
      san = null;
      board = null;
      res = null;
      move = null;
      game = null;
      in = null;
      games = null;
      anno = null;
      Log.removeMask(san.DEBUG);
      Log.removeMask(ChessBoard.DEBUG);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkNonVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_nonvariation, false, -1);
      assertTrue(games.size() > 0);
      
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_variation, false, -1);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBulkAnnotation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, -1);
      assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   /** this is used for testing new PGNs that have revealed bugs.
    *  After the bug is squashed the PGN should be moved to another
    *  file to become a permenent member of the regression testing
    *  suite.
    */
   public void testDebug () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_debug, false, -1);
      //assertTrue(games.size() > 0);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationCommentAfterMove () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {

      games = loadGames(dataDir + pgn_annotation, false, 0);

	 game = (Game) games.get(0);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationExclaimation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 1);

	 game = (Game) games.get(1);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next();
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();
	 assertTrue(anno.getSuffix() == (short) 1);
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationCommentBeforeGame () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 2);

	 game = (Game) games.get(2);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Comment Before Game"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationComment2ndMoveWithCommentAfter1st () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 3);

	 game = (Game) games.get(3);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

         //check post-notation of first move
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after1"));

         //check pre-notation of second move
	 history.next();
	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("before2"));

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after2"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation2CommentsAfter1stOneBefore2nd () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 4);

	 game = (Game) games.get(4);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

         //check post-notation of first move
	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after1 after1a"));

         //check pre-notation of second move
	 history.next();
	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("before2"));

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("after2"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationEndLineComment () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 6);

	 game = (Game) games.get(6);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Best by test"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotation2EndLineCommentsInARow () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 7);

	 game = (Game) games.get(7);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getAnnotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Best by test so says Fischer"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationHeadingVariation () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 8);

	 game = (Game) games.get(8);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next(1);

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Sicilian"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testAnnotationHeadingVariationEOL () 
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      games = loadGames(dataDir + pgn_annotation, false, 9);

	 game = (Game) games.get(9);
	 assertTrue(game != null);
	 History history = game.getHistory();

	 history.rewind();
	 history.next();
	 history.next(1);

	 assertTrue(history.getCurrentMove() != null);

	 anno = (ChessAnnotation) history.getCurrentMove().getPrenotation();

	 assertTrue(anno != null);
	 assertTrue(anno.getComment().equals("Sicilian"));
   }

   ///////////////////////////////////////////////////////////////////////////
   public void testBadPGNs () 
          throws FileNotFoundException,
	         InvalidGameFormatException,
		 Exception {
      //Log.addMask(SAN.DEBUG);
      //Log.addMask(PGNReader.DEBUG);
      int count = 0;

	 in = new PGNReader(
		 new FileReader(
		    new File(dataDir + pgn_bad)));

         try {
	    game = in.readGame();
	    fail("read in bad game but no error?");
	 }
	 catch (IOException e) {
	    game = in.getGame(); //from failed read
	    assertTrue(28 == game.getHistory().size());
	 }
	 catch (IllegalMoveException e) {
	    fail("wrong error for game 1: " + e);
	 }
	 catch (AmbiguousMoveException e) {
	    fail("wrong error for game 1: " + e);
	 }
	 finally {
	    Log.removeMask(SAN.DEBUG);
	    Log.removeMask(PGNReader.DEBUG);
	 }
   }

   //Helper///////////////////////////////////////////////////////////////////
   /** loads the games into a list so aspects of the games can be tested */
   protected List loadGames (String file, boolean debug, int gameToDebug)
          throws FileNotFoundException,
	  	 IOException, 
	         InvalidGameFormatException,
		 IllegalMoveException,
		 AmbiguousMoveException,
		 Exception {
      List list = new LinkedList();

      if (debug && gameToDebug < 0) {
        Log.addMask(SAN.DEBUG);
        Log.addMask(PGNReader.DEBUG);
      }

      try {
	 int count = 0;

	    Log.debug(PGNReader.DEBUG, "Reading file: " + file);
	    in = new PGNReader(
		    new FileReader(
		       new File(file)));

	    while ((game = in.readGame()) != null) {
	       game.getHistory().goToEnd();
	       list.add(game);

               //turn off single game debugging
	       if (debug && gameToDebug == count) {
		  Log.removeMask(SAN.DEBUG);
		  Log.removeMask(PGNReader.DEBUG);
	       }

	       count++;

               //turn on single game debugging for next read
	       if (debug && gameToDebug == count) {
	          System.out.println("turing logs on");
		  Log.addMask(SAN.DEBUG);
		  Log.addMask(PGNReader.DEBUG);
	       }
	       game = null;
	    }
      }
      catch (Exception e) {
         throw e;
      }
      finally {
	 if (debug) {
	    Log.removeMask(SAN.DEBUG);
	    Log.removeMask(PGNReader.DEBUG);
	 }
      }
      return list;
   }
}