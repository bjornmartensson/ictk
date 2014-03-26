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

/**This interface is for those Events that are triggered as a response
 *  to the client's command.  The ResponseKey is an arbitrary identifier
 *  used to link back to the caller of the command.
 */
 /* this is primarily for FICS BLOCK_MODE */
public interface ICSResponse {

   /**Determines if this Event was generated in response to a client's command*/
   public boolean isResponse ();

   /**FICS uses an integer response key*/
   public void setResponseKey (int key);
   public int getResponseKey ();

   /**ICC uses an arbrtrary string key*/
   public void setResponseKey (String key);
   public String getResponseKeyString ();

}
