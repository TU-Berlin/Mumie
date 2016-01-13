/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
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

package net.mumie.font;

import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class OutlineParser
{
  /**
   * Represents a tokenized source
   */

  protected static class TokenList
  {
    /**
     * Pattern to split the source into tokens.
     */

    protected static final Pattern splitPattern = Pattern.compile("[\\s,]");

    /**
     * Position of the current token.
     */

    protected int pos;

    /**
     * The tokens.
     */

    protected String[] tokens;

    /**
     * Returns whether there exists at least one more token.
     */

    public boolean hasNext ()
    {
      return this.pos < this.tokens.length - 1;
    }

    /**
     * Returns the next token
     */

    public String getNext ()
      throws OutlineParseException
    {
      if ( !this.hasNext() )
        throw new OutlineParseException("Unexpected end of data");
      return this.tokens[++this.pos];
    }

    /**
     * Returns the next token as a <code>double</code>.
     */

    public double getNextAsDouble ()
      throws OutlineParseException
    {
      return Double.parseDouble(this.getNext());
    }

    /**
     * Returns the current position.
     */

    public final int getPos ()
    {
      return this.pos;
    }

    /**
     * Changes the current position by the specified number.
     */

    public void movePos (int step)
    {
      this.pos += step;
    }

    /**
     * Creates a new instance with the specified source.
     */

    public TokenList (String source)
    {
      this.tokens = splitPattern.split(source);
      this.pos = -1;
    }
  }

  /**
   * Parses the specified SVG glyph source.
   */

  public Outline parse (String source)
    throws OutlineParseException
  {
    TokenList tokenList = new TokenList(source);
    List<OutlineSegment> path = new ArrayList<OutlineSegment>();

    // Command:
    int command = OutlineSegmentType.UNDEFINED;

    // End point:
    double x = 0;
    double y = 0;

    // Control points:
    double x1 = 0;
    double y1 = 0;
    double x2 = 0;
    double y2 = 0;

    while ( tokenList.hasNext() )
      {
        // Last command, end point, and control points:
        int lastCommand = command;
        double lastX = x;
        double lastY = y;
        double lastX1 = x1;
        double lastY1 = y1;
        double lastX2 = x2;
        double lastY2 = y2;

        // Check if a new command starts:
        int newCommand = OutlineSegmentType.getType(tokenList.getNext());
        if ( newCommand != OutlineSegmentType.UNDEFINED )
          command = newCommand;
        else
          tokenList.movePos(-1);

        switch ( command )
          {
          case OutlineSegmentType.MOVE:
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.MOVE, x, y));
            break;
          case OutlineSegmentType.MOVE_REL:
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.MOVE, x, y));
            break;
          case OutlineSegmentType.CLOSE:
            path.add(new OutlineSegment(OutlineSegmentType.CLOSE));
            break;
          case OutlineSegmentType.LINE:
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.LINE_REL:
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.HOR_LINE:
            x = tokenList.getNextAsDouble();
            y = lastY;
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.HOR_LINE_REL:
            x = tokenList.getNextAsDouble() + lastX;
            y = lastY;
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.VERT_LINE:
            x = lastX;
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.VERT_LINE_REL:
            x = lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.LINE, x, y));
            break;
          case OutlineSegmentType.CUBIC_CURVE:
            x1 = tokenList.getNextAsDouble();
            y1 = tokenList.getNextAsDouble();
            x2 = tokenList.getNextAsDouble();
            y2 = tokenList.getNextAsDouble();
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.CUBIC_CURVE, x1, y1, x2, y2, x, y));
            break;
          case OutlineSegmentType.CUBIC_CURVE_REL:
            x1 = tokenList.getNextAsDouble() + lastX;
            y1 = tokenList.getNextAsDouble() + lastY;
            x2 = tokenList.getNextAsDouble() + lastX;
            y2 = tokenList.getNextAsDouble() + lastY;
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.CUBIC_CURVE, x1, y1, x2, y2, x, y));
            break;
          case OutlineSegmentType.CUBIC_CURVE_SMOOTH:
            if ( OutlineSegmentType.isCubicCurve(lastCommand) )
              {
                x1 = 2*lastX - lastX2;
                y1 = 2*lastY - lastY2;
              }
            else
              {
                x1 = lastX;
                x2 = lastY;
              }
            x2 = tokenList.getNextAsDouble();
            y2 = tokenList.getNextAsDouble();
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.CUBIC_CURVE, x1, y1, x2, y2, x, y));
            break;
          case OutlineSegmentType.CUBIC_CURVE_SMOOTH_REL:
            if ( OutlineSegmentType.isCubicCurve(lastCommand) )
              {
                x1 = 2*lastX - lastX2;
                y1 = 2*lastY - lastY2;
              }
            else
              {
                x1 = lastX;
                x2 = lastY;
              }
            x2 = tokenList.getNextAsDouble() + lastX;
            y2 = tokenList.getNextAsDouble() + lastY;
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.CUBIC_CURVE, x1, y1, x2, y2, x, y));
            break;
          case OutlineSegmentType.QUAD_CURVE:
            x1 = tokenList.getNextAsDouble();
            y1 = tokenList.getNextAsDouble();
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.QUAD_CURVE, x1, y1, x, y));
            break;
          case OutlineSegmentType.QUAD_CURVE_REL:
            x1 = tokenList.getNextAsDouble() + lastX;
            y1 = tokenList.getNextAsDouble() + lastY;
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.QUAD_CURVE, x1, y1, x, y));
            break;
          case OutlineSegmentType.QUAD_CURVE_SMOOTH:
            if ( OutlineSegmentType.isQuadCurve(lastCommand) )
              {
                x1 = 2*lastX - lastX1;
                y1 = 2*lastY - lastY1;
              }
            else
              {
                x1 = lastX;
                x2 = lastY;
              }
            x = tokenList.getNextAsDouble();
            y = tokenList.getNextAsDouble();
            path.add(new OutlineSegment(OutlineSegmentType.QUAD_CURVE, x1, y1, x, y));
            break;
          case OutlineSegmentType.QUAD_CURVE_SMOOTH_REL:
            if ( OutlineSegmentType.isQuadCurve(lastCommand) )
              {
                x1 = 2*lastX - lastX1;
                y1 = 2*lastY - lastY1;
              }
            else
              {
                x1 = lastX;
                x2 = lastY;
              }
            x = tokenList.getNextAsDouble() + lastX;
            y = tokenList.getNextAsDouble() + lastY;
            path.add(new OutlineSegment(OutlineSegmentType.QUAD_CURVE, x1, y1, x, y));
            break;
          }
      }

    
    return new Outline(path.toArray(new OutlineSegment[path.size()]));
  }
}
