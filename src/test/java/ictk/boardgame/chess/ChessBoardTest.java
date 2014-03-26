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

package ictk.boardgame.chess;

import junit.framework.*;

import ictk.util.Log;
import ictk.boardgame.*;
import ictk.boardgame.io.*;

public class ChessBoardTest extends TestCase {
   ChessBoard board, board2;
   ChessMove   move;
   char[][] default_position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

/*
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P','N',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
*/


   public ChessBoardTest (String name) {
      super(name);
   }

   public void setUp () {
      board = new ChessBoard();
   }

   public void tearDown () {
      board = null;
      board2 = null;
      move = null;
      Log.removeMask(ChessBoard.DEBUG);
   }

   //////////////////////////////////////////////////////////////////////
   public void testFileNRankTranslation () {
      Piece p = null;
      p = board.squares[0][0].getOccupant();
      assertTrue(p instanceof Rook);

   }

   //////////////////////////////////////////////////////////////////////
   public void testSetPositionDataReset () {
      //Log.addMask(ChessBoard.DEBUG);
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P','N',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
      board.setPosition(position);
      assertTrue(board.staleLegalDests);
      board.setWhiteCastleableKingside(false);
      board.setWhiteCastleableQueenside(false);
      board.setBlackCastleableKingside(false);
      board.setBlackCastleableQueenside(false);
      board.setEnPassantFile(2);
      board.set50MoveRulePlyCount(37);
      board.setPositionDefault();
      assertTrue(board.staleLegalDests);
      assertTrue(board.isWhiteCastleableKingside());
      assertTrue(board.isWhiteCastleableQueenside());
      assertTrue(board.isBlackCastleableKingside());
      assertTrue(board.isBlackCastleableQueenside());
      assertTrue(board.get50MoveRulePlyCount() == 0);
      assertTrue(board.getEnPassantFile() == ChessBoard.NO_ENPASSANT);
      assertTrue(board.equals(new ChessBoard()));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEquals () {
      board2 = new ChessBoard();
      assertTrue(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualsPly () {
      board2 = new ChessBoard();
      board.set50MoveRulePlyCount(32);
      assertTrue(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualsNotPieceType () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ','N',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
      char[][] position2={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ','Q',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
      board.setPosition(position);
      board2 = new ChessBoard(position2);

      assertFalse(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualsNotPieceColor () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ','N',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
      char[][] position2={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ','n',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};
      board.setPosition(position);
      board2 = new ChessBoard(position2);

      assertFalse(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualsNotCastle () {
      board2 = new ChessBoard();
      board.setWhiteCastleableKingside(false);

      assertFalse(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testEqualsNotEnPassant () {
      board2 = new ChessBoard();
      board.setEnPassantFile(2);

      assertFalse(board.equals(board2));
   }

   //////////////////////////////////////////////////////////////////////
   public void testLegalMoves () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      assertTrue(board.getLegalMoveCount() == 20);
   }

   //////////////////////////////////////////////////////////////////////
   public void testSetPositionCastle () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'Q','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.isWhiteCastleableQueenside());
      assertTrue(board.isWhiteCastleableKingside());
      assertTrue(board.isBlackCastleableQueenside());
      assertTrue(board.isBlackCastleableKingside());
   }

   //////////////////////////////////////////////////////////////////////
   public void testSetPositionCastle2 () {
      char[][] position={{' ','P',' ',' ',' ',' ','p','r'},
                        {'R','P',' ',' ',' ',' ','p','n'},
                        {' ','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','q'},
                        {'K','P',' ',' ',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertFalse(board.isWhiteCastleableQueenside());
      assertTrue(board.isWhiteCastleableKingside());
      assertTrue(board.isBlackCastleableQueenside());
      assertTrue(board.isBlackCastleableKingside());
   }

   //////////////////////////////////////////////////////////////////////
   public void testSetPositionCastle3 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                        {' ','P',' ',' ',' ',' ','p','n'},
                        {' ','P',' ',' ',' ',' ','p','b'},
                        {' ','P',' ',' ',' ',' ','p','q'},
                        {' ','P',' ','K',' ',' ','p','k'},
                        {'B','P',' ',' ',' ',' ','p','b'},
                        {'N','P',' ',' ',' ',' ','p','n'},
                        {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertFalse(board.isWhiteCastleableQueenside());
      assertFalse(board.isWhiteCastleableKingside());
      assertTrue(board.isBlackCastleableQueenside());
      assertTrue(board.isBlackCastleableKingside());
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount1 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'Q','P',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 39);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount2 () {
      char[][] position={{' ','P',' ',' ',' ',' ','p','r'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'Q','P',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 34);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount3 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                         {' ','P',' ',' ',' ',' ','p','n'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'Q','P',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 36);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount4 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {' ','P',' ',' ',' ',' ','p','b'},
                         {'Q','P',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 36);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount5 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {' ','P',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 30);
   }

   //////////////////////////////////////////////////////////////////////
   public void testMaterialCount6 () {
      char[][] position={{'R','P',' ',' ',' ',' ','p','r'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'Q',' ',' ',' ',' ',' ','p','q'},
                         {'K','P',' ',' ',' ',' ','p','k'},
                         {'B','P',' ',' ',' ',' ','p','b'},
                         {'N','P',' ',' ',' ',' ','p','n'},
                         {'R','P',' ',' ',' ',' ','p','r'}};

      board.setPosition(position);
      assertTrue(board.getMaterialCount(true) == 39);
      assertTrue(board.getMaterialCount(false) == 38);
   }

   //////////////////////////////////////////////////////////////////////
   public void testGetCaptured () 
          throws IllegalMoveException,
	         AmbiguousChessMoveException {
      assertTrue(board.getCapturedPieces(true) == null);
      assertTrue(board.getCapturedPieces(false) == null);

      assertTrue(board.getUnCapturedPieces(true).length == 16);
      assertTrue(board.getUnCapturedPieces(false).length == 16);

      move = (ChessMove) board.san.stringToMove(board, "e4");
      board.playMove(move);
      move = (ChessMove) board.san.stringToMove(board, "d5");
      board.playMove(move);
      move = (ChessMove) board.san.stringToMove(board, "exd5");
      board.playMove(move);

      assertTrue(board.getCapturedPieces(true).length == 1);
      assertTrue(board.getCapturedPieces(false) == null);

      assertTrue(board.getUnCapturedPieces(true).length == 15);
      assertTrue(board.getUnCapturedPieces(false).length == 16);
   }

   //////////////////////////////////////////////////////////////////////
   public void testListenersAdd () {
      BoardListener bl = new BoardListener() {
                            public void boardUpdate (Board b, int c) { } 
			 }, 
                    bl2 = new BoardListener() {
                            public void boardUpdate (Board b, int c) { } 
			 }; 
		         
      board.addBoardListener(bl);
      assertTrue( board.getBoardListeners().length == 1);

      //no dupes
      board.addBoardListener(bl);
      assertTrue( board.getBoardListeners().length == 1);

      board.addBoardListener(bl2);
      assertTrue( board.getBoardListeners().length == 2);

      //assertTrue( board.getBoardListeners()[0] == bl);
      //assertTrue( board.getBoardListeners()[1] == bl2);
   }

   //////////////////////////////////////////////////////////////////////
   public void testListenersArrayRemove () {
      BoardListener bl = new BoardListener() {
                            public void boardUpdate (Board b, int c) { } 
			 }, 
                    bl2 = new BoardListener() {
                            public void boardUpdate (Board b, int c) { } 
			 }; 
		         
      board.addBoardListener(bl);
      assertTrue( board.getBoardListeners().length == 1);

      board.addBoardListener(bl2);
      assertTrue( board.getBoardListeners().length == 2);

      board.removeBoardListener(bl);
      assertTrue( board.getBoardListeners().length == 1);

      assertTrue( board.getBoardListeners()[0] == bl2);
   }
}
