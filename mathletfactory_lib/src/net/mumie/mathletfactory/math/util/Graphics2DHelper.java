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

package net.mumie.mathletfactory.math.util;


/**
 * This class contains some helper functions for the usage with Graphics2D.
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class Graphics2DHelper {
  public static final int INTERSECTION_POINT = 0;
  public static final int INTERSECTION_IDENTICAL = 1;
  public static final int INTERSECTION_NONE = 2;

  public static final double EPSILON = 1.0E-10;
  public static final int LOWER_LEFT_2_UPPER_LEFT = 0;
  public static final int LOWER_RIGHT_2_UPPER_RIGHT = 1;
  public static final int LOWER_LEFT_2_LOWER_RIGHT = 2;
  public static final int UPPER_LEFT_2_UPPER_RIGHT = 3;
  /**
   * Calculates intersection of two lines. The lines are described as:
   * f(x) = lineX[0] * x + lineX[1]
   * for X in {1,2}. If the line should be parallel to y-axis then lineX[0]
   * has to be positive or negative infinity and lineX[1] should contain
   * intersection of the line with the x-Axis.<br> The method return an int
   * describing the type of intersection: INTERSECTION_POINT: lines have one
   * intersetion point INTERSECTION_IDENTICAL: lines are identical
   * INTERSECTION_NONE: lines are parallel and don't intersect If the result is
   * INTERSECTION_POINT the the param intersection gives the point of
   * intersection. The array must be allocated by caller. If the method returns
   * eihter INTERSECTION_IDENTICAL or INTERSECTION_NONE the param intersection
   * is left unchanged.
   * @param line1 line equation of line 1
   * @param line2 line equation of line 1
   * @param intersection placeholder for intersection point
   * @return int
   */
  public static int lineIntersection2D(double[] line1, double[] line2, double[] intersection) {
    if ((line1[0] == line2[0]) ||
        (Double.isInfinite(line1[0]) && Double.isInfinite(line2[0]))) { //gleicher Anstieg
      if (line1[1] == line2[1]) { //identische geraden
        return INTERSECTION_IDENTICAL;
      } else { //parallele Geraden
        return INTERSECTION_NONE;
      }
    }

    if (Double.isInfinite(line1[0])) {
      intersection[0] = line1[1];
    } else {
      if (Double.isInfinite(line2[0])) {
        intersection[0] = line2[1];
      } else {
        intersection[0] = (line2[1] -line1[1]) / (line1[0] - line2[0]);
      }
    }
    if (Double.isInfinite(line1[0])) {
      intersection[1] = line2[0] * intersection[0] + line2[1];
    } else {
      intersection[1] = line1[0] * intersection[0] + line1[1];
    }
    return INTERSECTION_POINT;
  }

  public static double[] points2Line(double x1, double y1, double x2, double y2) {
    /*
     * check for small variance in x or y coordinates. Points are handled as
     * equal when variance is smaller than EPSILON
     */
    if ((Math.abs(x1-x2)-EPSILON) < 0)
      x2 = x1;
    if ((Math.abs(y1-y2)-EPSILON) < 0)
      y2 = y1;

    double[] result = new double[2];
    result[0] = (y2-y1) / (x2-x1); // this may be infinity
    result[1] = Double.isInfinite(result[0]) ? x1 : y1 - result[0] * x1;
    return result;
  }

  public static double pointDistance(double x1, double y1, double x2, double y2) {
    double dx = x1-x2;
    double dy = y1-y2;
    return Math.sqrt(dx*dx + dy*dy);
  }

  public static boolean pointInRect(double[] point, double[] rect) {
    if (point[0] + EPSILON < rect[0])
      return false;
    if (point[0] - EPSILON > rect[2])
      return false;
    if (point[1] + EPSILON < rect[1])
      return false;
    if (point[1] - EPSILON > rect[3])
      return false;
    return true;
  }

  /**
   * Puts the intersection coordinates into the <code>intersection</code> array and returns the number
   * of intersection points of the given line (specified by the array {x1,y1,x2,y2}) and rectangle
   * (specified by the array {lowerLeftX, lowerLeftY,         canvasRect.getX(),
          canvasRect.getY(),
          canvasRect.getX() + canvasRect.getWidth(),
          canvasRect.getY() + canvasRect.getHeight()},
.
   * The returned points are ordered in the right orientation.
   */
  public static int lineRectIntersection2D(double[] line, double[] rect, double[] intersection) {
    int result = calclineRectIntersection2D(line, rect, intersection);
    //maybe we have to exchange the intersection points to keep orientation
    if (result == 2) {
      if (Graphics2DHelper.dotProduct(
          new double[] {line[2] - line[0], line[3] - line[1]},
          new double[] {intersection[2] - intersection[0], intersection[3] - intersection[1]}) < 0) {
        double tempX = intersection[0];
        double tempY = intersection[1];
        intersection[0] = intersection[2];
        intersection[1] = intersection[3];
        intersection[2] = tempX;
        intersection[3] = tempY;
      }
    }
    return result;
  }

  /**
   * Puts the intersection coordinates into the <code>intersection</code> array and returns the number
   * of intersection points of the given line and rectangle.
   */
  private static int calclineRectIntersection2D(double[] line, double[] rect, double[] intersection) {
    //das Rechteck wird in vier Geraden zerlegt:

    double[][] rectLines = new double[][] {
        points2Line(rect[0], rect[1], rect[0], rect[3]),
        points2Line(rect[2], rect[1], rect[2], rect[3]),
        points2Line(rect[0], rect[1], rect[2], rect[1]),
        points2Line(rect[0], rect[3], rect[2], rect[3])};

    double[] realLine = points2Line(line[0],line[1],line[2],line[3]);
    int[] intersectionTypes = new int[4];
    double[][] intersections = new double[4][2];
//    intersectionTypes[0] = lineIntersection2D(line, rectLines[0], intersections[0]);
//    intersectionTypes[1] = lineIntersection2D(line, rectLines[1], intersections[1]);
//    intersectionTypes[2] = lineIntersection2D(line, rectLines[2], intersections[2]);
//    intersectionTypes[3] = lineIntersection2D(line, rectLines[3], intersections[3]);
    intersectionTypes[0] = lineIntersection2D(realLine, rectLines[0], intersections[0]);
    intersectionTypes[1] = lineIntersection2D(realLine, rectLines[1], intersections[1]);
    intersectionTypes[2] = lineIntersection2D(realLine, rectLines[2], intersections[2]);
    intersectionTypes[3] = lineIntersection2D(realLine, rectLines[3], intersections[3]);

    //Linie auf einer der vier Rechteckkanten
    if (intersectionTypes[LOWER_LEFT_2_UPPER_LEFT] == INTERSECTION_IDENTICAL) {
      intersection[0] = rect[0];
      intersection[1] = rect[1];
      intersection[2] = rect[0];
      intersection[3] = rect[3];
      return 2;
    }

    if (intersectionTypes[LOWER_RIGHT_2_UPPER_RIGHT] == INTERSECTION_IDENTICAL) {
      intersection[0] = rect[2];
      intersection[1] = rect[1];
      intersection[2] = rect[2];
      intersection[3] = rect[3];
      return 2;
    }

    if (intersectionTypes[LOWER_LEFT_2_LOWER_RIGHT] == INTERSECTION_IDENTICAL) {
      intersection[0] = rect[0];
      intersection[1] = rect[1];
      intersection[2] = rect[2];
      intersection[3] = rect[1];
      return 2;
    }

    if (intersectionTypes[UPPER_LEFT_2_UPPER_RIGHT] == INTERSECTION_IDENTICAL) {
      intersection[0] = rect[0];
      intersection[1] = rect[3];
      intersection[2] = rect[2];
      intersection[3] = rect[3];
      return 2;
    }

    //linie senkrecht
    if (intersectionTypes[LOWER_LEFT_2_UPPER_LEFT] == INTERSECTION_NONE) {
      if (realLine[0] == Double.POSITIVE_INFINITY) {
        intersection[0] = intersections[LOWER_LEFT_2_LOWER_RIGHT][0];
        intersection[1] = intersections[LOWER_LEFT_2_LOWER_RIGHT][1];
        intersection[2] = intersections[UPPER_LEFT_2_UPPER_RIGHT][0];
        intersection[3] = intersections[UPPER_LEFT_2_UPPER_RIGHT][1];
      } else {
        intersection[2] = intersections[LOWER_LEFT_2_LOWER_RIGHT][0];
        intersection[3] = intersections[LOWER_LEFT_2_LOWER_RIGHT][1];
        intersection[0] = intersections[UPPER_LEFT_2_UPPER_RIGHT][0];
        intersection[1] = intersections[UPPER_LEFT_2_UPPER_RIGHT][1];
      }

      if ((intersections[LOWER_LEFT_2_LOWER_RIGHT][0] > rect[0]) &&
          (intersections[LOWER_LEFT_2_LOWER_RIGHT][0] < rect[2])) {
        return 2;
      }
      return 0;
    }

    //linie waagerecht
    if (intersectionTypes[LOWER_LEFT_2_LOWER_RIGHT] == INTERSECTION_NONE) {
      if (realLine[0] == Double.POSITIVE_INFINITY) {
        intersection[2] = intersections[LOWER_LEFT_2_UPPER_LEFT][0];
        intersection[3] = intersections[LOWER_LEFT_2_UPPER_LEFT][1];
        intersection[0] = intersections[LOWER_RIGHT_2_UPPER_RIGHT][0];
        intersection[1] = intersections[LOWER_RIGHT_2_UPPER_RIGHT][1];
      } else {
        intersection[0] = intersections[LOWER_LEFT_2_UPPER_LEFT][0];
        intersection[1] = intersections[LOWER_LEFT_2_UPPER_LEFT][1];
        intersection[2] = intersections[LOWER_RIGHT_2_UPPER_RIGHT][0];
        intersection[3] = intersections[LOWER_RIGHT_2_UPPER_RIGHT][1];
      }
      if ((intersections[LOWER_LEFT_2_UPPER_LEFT][1] > rect[1]) &&
          (intersections[LOWER_LEFT_2_UPPER_LEFT][1] < rect[3])) {
        return 2;
      }
      return 0;
    }

    if (pointDistance(
        intersections[LOWER_LEFT_2_LOWER_RIGHT][0],
        intersections[LOWER_LEFT_2_LOWER_RIGHT][1],
        intersections[LOWER_LEFT_2_UPPER_LEFT][0],
        intersections[LOWER_LEFT_2_UPPER_LEFT][1]) < EPSILON) {
      intersectionTypes[LOWER_LEFT_2_UPPER_LEFT] = INTERSECTION_NONE;
    }
    if (pointDistance(
        intersections[LOWER_LEFT_2_LOWER_RIGHT][0],
        intersections[LOWER_LEFT_2_LOWER_RIGHT][1],
        intersections[LOWER_RIGHT_2_UPPER_RIGHT][0],
        intersections[LOWER_RIGHT_2_UPPER_RIGHT][1]) < EPSILON) {
      intersectionTypes[LOWER_RIGHT_2_UPPER_RIGHT] = INTERSECTION_NONE;
    }
    if (pointDistance(
        intersections[UPPER_LEFT_2_UPPER_RIGHT][0],
        intersections[UPPER_LEFT_2_UPPER_RIGHT][1],
        intersections[LOWER_LEFT_2_UPPER_LEFT][0],
        intersections[LOWER_LEFT_2_UPPER_LEFT][1]) < EPSILON) {
      intersectionTypes[LOWER_LEFT_2_UPPER_LEFT] = INTERSECTION_NONE;
    }
    if (pointDistance(
        intersections[UPPER_LEFT_2_UPPER_RIGHT][0],
        intersections[UPPER_LEFT_2_UPPER_RIGHT][1],
        intersections[LOWER_RIGHT_2_UPPER_RIGHT][0],
        intersections[LOWER_RIGHT_2_UPPER_RIGHT][1]) < EPSILON) {
      intersectionTypes[LOWER_RIGHT_2_UPPER_RIGHT] = INTERSECTION_NONE;
    }
    int num = 0;
    for (int i = 0; i < 4; i++) {
      if ((intersectionTypes[i] == INTERSECTION_POINT) &&
          pointInRect(intersections[i], rect)) {
        intersection[2*num] = intersections[i][0];
        intersection[2*num + 1] = intersections[i][1];
        num++;
        if (num > 1) {
          break;
        }
      }
    }

    return num;
  }

	public static double dotProduct(double[] vector1, double[] vector2) {
		double result = 0;
		for (int i = 0; i < vector1.length; i++)
			result += vector1[i] * vector2[i];
		return result;
	}
}
