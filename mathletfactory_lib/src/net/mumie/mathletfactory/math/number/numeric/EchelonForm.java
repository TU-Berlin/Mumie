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

package net.mumie.mathletfactory.math.number.numeric;

import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 *  This class calculates the echelon form of a given number matrix by using
 *  the gauss algorithm for linear equation systems.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 *  @deprecated use class {@link net.mumie.mathletfactory.math.util.MatrixUtils} instead
 */
public final class EchelonForm{


  private static final boolean ROW_FORM = false;
  private static final boolean COLUMN_FORM = true;
  private static double EPSILON = 1e-14;

  /**
   *  Returns the reduced echelon form of a {@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix}.
   */
  public static NumberMatrix getREFMatrix(NumberMatrix matrix){
    int cols = matrix.getColumnCount();
    if( cols == 0)
      throw new IllegalArgumentException("empty matrix!");

    NumberMatrix m = new NumberMatrix(matrix);

    MNumber[] outputMatrix = new MNumber[matrix.getRowCount()*matrix.getColumnCount()];

    try{
      EchelonForm.toReducedRowEchelonForm(m.getEntriesAsNumberRef(), m.getRowCount(),
                                          m.getColumnCount(),
                                          new MNumber[m.getRowCount() * m.getRowCount()],
                                          outputMatrix);
    } catch( Exception e){ e.printStackTrace();}

    return new NumberMatrix(matrix.getNumberClass(), cols, outputMatrix);
  }

  /**
   *  Returns the echelon form of a {@link NumberMatrix}.
   */
 public static NumberMatrix getEFMatrix(NumberMatrix matrix){
    int cols = matrix.getColumnCount();
    if( cols == 0)
      throw new IllegalArgumentException("empty matrix!");

    NumberMatrix m = new NumberMatrix(matrix);

    MNumber[] outputMatrix = new MNumber[matrix.getRowCount()*matrix.getColumnCount()];

    try{
      EchelonForm.toRowEchelonForm(m.getEntriesAsNumberRef(), m.getRowCount(),
                                   m.getColumnCount(),
                                   new MNumber[m.getRowCount() * m.getRowCount()],
                                   outputMatrix);
    } catch( Exception e){ e.printStackTrace();}

    return new NumberMatrix(matrix.getNumberClass(), cols, outputMatrix);
  }

  /**
   * This method converts an arbitrary matrix into row echelon form.
   * a is the input matrix represented as an array of MMNumbers, where
   * the rows of the matrix are stored successively. m and n are the
   * dimensions of the matrix. If the absolute value of an element of
   * the input matrix is less or equal than the absolute value of eps
   * the element is considered to be zero. After returning p holds the
   * product of the elementary matrices used to transform the input
   * matrix into row echelon form and e holds the matrix in row echelon
   * form. Note that the user is him-/herself responsible for allocating
   * corresponding arrays for e and p.
   *
   */
  public static void toRowEchelonForm(MNumber[] inputMatrix, int m,int n,
                                      MNumber[] productOfElmntMatrices,
                                      MNumber[] outputMatrix) throws Exception{
    echForm(ROW_FORM,inputMatrix,m,n,productOfElmntMatrices,outputMatrix);
  }


  /**
   * This method converts an arbitrary matrix into column echelon form.
   * a is the input matrix represented as an array of MMNumbers, where
   * the rows of the matrix are stored successively. m and n are the
   * dimensions of the matrix. If the absolute value of an element of
   * the input matrix is less or equal than the absolute value of eps
   * the element is considered to be zero. After returning p holds the
   * product of the elementary matrices used to transform the input
   * matrix into column echelon form and e holds the matrix in column
   * echelon form. Note that the user is him-/herself responsible for
   * allocating corresponding arrays for e and p.
   *
   */
  public static void toColumnEchelonForm(MNumber[] inputMatrix, int m, int n,
                                         MNumber[] productOfElmntMatrices,
                                         MNumber[] outputMatrix) throws Exception{
    echForm(COLUMN_FORM,inputMatrix,m,n,productOfElmntMatrices,outputMatrix);
  }


  /**
   * This method converts an arbitrary matrix into reduced row echelon
   * form. a is the input matrix represented as an array of MMNumbers,
   * where the rows of the matrix are stored successively. m and n are
   * the dimensions of the matrix. If the absolute value of an element
   * of the input matrix is less or equal than the absolute value of eps
   * the element is considered to be zero. After returning p holds the
   * product of the elementary matrices used to transform the input
   * matrix into reduced row echelon form and e holds the matrix in
   * reduced row echelon form. Note that the user is him-/herself
   * responsible for allocating corresponding arrays for e and p.
   *
   */
  public static void toReducedRowEchelonForm(MNumber[] inputMatrix, int m, int n,
                                             MNumber[] productOfElmntMatrices,
                                             MNumber[] outputMatrix) throws Exception{
    toReducedEchelonForm(ROW_FORM,inputMatrix,m,n,productOfElmntMatrices,outputMatrix);
  }


  /**
   * This method converts an arbitrary matrix into reduced column
   * echelon form. a is the input matrix represented as an array of
   * MMNumbers, where the rows of the matrix are stored successively.
   * m and n are the dimensions of the matrix. If the absolute value
   * of an element of the input matrix is less or equal than the absolute
   * value of eps the element is considered to be zero. After returning
   * p holds the product of the elementary matrices used to transform
   * the input matrix into reduced column echelon form and e holds the
   * matrix in reduced row echelon form. Note that the user is
   * him-/herself responsible for allocating corresponding arrays for
   * e and p.
   *
   */
  public static void toReducedColumnEchelonForm(MNumber[] inputMatrix, int m, int n,
                                                MNumber[] productOfElmntMatrices,
                                                MNumber[] outputMatrix) throws Exception{
    toReducedEchelonForm(COLUMN_FORM,inputMatrix,m,n,productOfElmntMatrices,outputMatrix);
  }


  /**
   * This private method converts a matrix in the echelon form to the
   * reduced echelon form. rowOrColumn is a flag indicating the row
   * or column echelon forms. a is the input matrix represented
   * as an array of MMNumbers, where the rows of the matrix are stored
   * successively. m and n are the dimensions of the matrix. After
   * returning p holds the product of the elementary matrices used to
   * transform the input matrix into the reduced echelon form and e holds
   * the matrix in reduced echelon form. Note that the user is
   * him-/herself responsible for allocating corresponding arrays for
   * e and p.
   */
  protected static void toReducedEchelonForm(boolean rowOrColumn, MNumber[] inputMatrix,
                                           int n, int m, MNumber[] productOfElmntMatrices,
                                           MNumber[] outputMatrix) throws Exception{

    // first convert the input matrix into the (row or column) echelon form
    // and get the pivot positions...
    int[] pivots = echForm(rowOrColumn,inputMatrix,n,m,productOfElmntMatrices,outputMatrix);

    // if the type is not row echelon, swap the dimensions of the matrix...
    if(rowOrColumn != ROW_FORM){
      int tmp = n;
      n = m;
      m = tmp;
    }

    // set the dimension of the product of elementary matrices to be
    // the first dimension of the input matrix...
    int pdim = n;

    // now loop through the rows (or columns if rowOrColumn is 1) of the matrix
    // and make it reduced row (column) echelon...
    for(int row=0; row<n; row++){

      // if the array of pivot positions holds a negative value for
      // some row (or column) it means there are no more pivots and
      // the loop should break...
      if( pivots[row] < 0 ){
        break;
      }

      // now loop upwards from the current row (column) and make
      // all the entries in the pivot column (row) 0-s...
      for(int i=row-1; i>=0; i--){

        MNumber mult = inputMatrix[0].create();
        mult.setDouble(-1.0); // create a new MNumber mult...

        // mult will hold the alpha value for the elementary matrix...
        mult.mult(getElementRef(outputMatrix, n, m, i, pivots[row], rowOrColumn)).
          div(getElementRef(outputMatrix, n, m, row, pivots[row], rowOrColumn));

        // now add to the current row (column) the pivot row (column)
        // multiplied by mult. this will result in 0-s in the pivot
        // column (row)...
        MNumber s;

        for(int j=0; j<m; j++){

          s = getElement(outputMatrix,n,m,i,j,rowOrColumn);
          s = s.add(getElement(outputMatrix,n,m,row,j,rowOrColumn).mult(mult));
          setElementRef(outputMatrix,n,m,i,j,s,rowOrColumn);
        }

        // now protocol this change in the product of elementary matrices by
        // multiplying it by a new elementary matrix corrsponding to this change...

        // this array will hold the sums...
        MNumber[] t=new MNumber[pdim];

        // now the matrix multiplication...
        for(int j=0; j<pdim; j++){
          for(int k=0; k<pdim; k++){

            t[k] = inputMatrix[0].create();
            t[k].setDouble(0.0); // initialize the k-th sum to 0...
            for(int l=0; l<pdim; l++){
              // calculate the k-th sum
              t[k] = t[k].add(elemMatrix(3,mult,i,row,k,l).
                                  mult(getElementRef(productOfElmntMatrices,pdim,pdim,l,j,rowOrColumn)));
            }
          }

          // now store the sums in the appropriate column of the pruduct
          // of elementary matrices
          for(int k=0; k<pdim; k++)
            setElementRef(productOfElmntMatrices,pdim,pdim,k,j,t[k],rowOrColumn);
        }

      }

      // now set the pivots to 1-s...

      // initialize auxiliary variable s to the inverse of the pivot...
      MNumber s = inputMatrix[0].create();
      s.setDouble(1.0).div(getElementRef(outputMatrix,n,m,row,pivots[row],rowOrColumn));
      MNumber w;

      // now loop through the columns and multiply the elements in the current
      // row by s. This will make the current pivot 1...
      for(int j=pivots[row]; j<m; j++){

        w = getElement(outputMatrix,n,m,row,j,rowOrColumn).mult(s);
        setElementRef(outputMatrix,n,m,row,j,w,rowOrColumn);
      }

      // now protocol the change by multiplying the product of elementary
      // matrices by the appropriate elementary matrix...

      // matrix multiplication here as above...
      MNumber[] t=new MNumber[pdim];
      for(int i=0; i<pdim; i++){
        for(int j=0; j<pdim; j++){

          t[j] = inputMatrix[0].create();
          t[j].setDouble(0.0);
          for(int k=0; k<pdim; k++)
            t[j] = t[j].add(elemMatrix(2,s,row,row,j,k).
                                mult(getElementRef(productOfElmntMatrices,pdim,pdim,k,i,rowOrColumn)));

        }
        for(int j=0; j<pdim; j++){
          setElementRef(productOfElmntMatrices,pdim,pdim,j,i,t[j],rowOrColumn);
        }
      }
      //debug(e,n,m);
    }
  }


  /**
   * This private method converts a an arbitrary matrix into the echelon
   * form. rowOrColumn is a flag indicating the row or column
   * echelon forms. The Gauss elimination method is used here. a is the
   * input matrix represented as an array of MMNumbers, where the rows of
   * the matrix are stored successively. m and n are the dimensions of the
   * matrix. After returning p holds the product of the elementary matrices
   * used to transform the input matrix into the echelon form and e holds
   * the matrix in the echelon form. The method returns an array of pivot
   * positions. Note that the user is him-/herself responsible for
   * allocating corresponding arrays for e and p.
   */
  protected static int[] echForm(boolean rowOrColumn, MNumber[] inputMatrix, int n, int m,
                               MNumber[] productOfElmntMatrices,
                               MNumber[] outputMatrix) throws Exception{
    // if the type is not row echelon, swap the dimensions of the matrix...
    if(rowOrColumn != ROW_FORM){
      int k=n;
      n=m;
      m=k;
    }

    // set the dimension of the product of elementary matrices to be
    // the first dimension of the input matrix...
    int pdim = n;

    // check the dimensions of the matrices... throw an exception
    // if something is wrong...
    if(outputMatrix.length < n*m)
      throw new Exception("Output matrix e was not allocated properly!");
    if(productOfElmntMatrices.length < pdim*pdim)
      throw new Exception("Output matrix p was not allocated properly!");

    // initialize the array of pivot positions to some negative values and
    // the product of elementary matrices to the identity matrix...
    int pivots[]=new int[n];
    MNumber s;
    for(int i=0; i<pdim; i++){

      s = inputMatrix[0].create();
      s.setDouble(1.0);
      setElementRef(productOfElmntMatrices,pdim,pdim,i,i,s,rowOrColumn);
      for(int j=i+1; j<pdim; j++){

        s = inputMatrix[0].create();
        s.setDouble(0.0);
        setElementRef(productOfElmntMatrices,pdim,pdim,i,j,s,rowOrColumn);
        setElementRef(productOfElmntMatrices,pdim,pdim,j,i,s,rowOrColumn);
      }
      pivots[i] = -1;
    }


    // copy the input matrix a into the output matrix
    // handling thereby any errors, which might occur...
    try{
      for(int i=0; i<n; i++){
        for(int j=0; j<m; j++){
          setElementRef(outputMatrix,n,m,i,j,getElement(inputMatrix,n,m,i,j,rowOrColumn),rowOrColumn);
        }
      }
    }
    catch(Exception ex){
      throw new Exception("Input matrix a could not be read properly!\nCheck "
                            +"the dimensions of the matrix.");
    }


    // here the main stuff begins... we loop through submatrices of the input
    // matrix and perform the Gauss elimination...

    // set the initial row and column indeces to 0-s...
    int row = 0;
    int col = 0;

    // loop while there are submatrices to consider...
    while(row < n && col < m){

      // get the row and column of the element with the greatest
      // absolute value in the first nonzero column of the
      // current submatrix...

      int[] indicesOfMax = findAbsMax(outputMatrix,col,row,n,m,rowOrColumn);

      // if there is no such element, we are done and return the
      // pivot array...
      if(indicesOfMax == null){
        return pivots;
      }

      // set the column of the current submatrix to be the column
      // of the absolute maximum element we've just found...
      col = indicesOfMax[1];

      // now if the row of the maximum element is different from
      // that of the first row of the current submatrix,
      // exchange the rows in this submatrix and protocol changes
      // by multiplying the product of elementary matrices by the appropriate
      // elementary matrix...
      if(indicesOfMax[0] != row){

        for(int i=col; i<m; i++)
          swapElements(outputMatrix, n, m, row, i, indicesOfMax[0], i, rowOrColumn);


        // p is multiplicated with the elementary matrix corresponding to the
        // step performed: a type 1 matrix (swap of two rows)

        for(int i=0; i<pdim; i++){
          MNumber t[]=new MNumber[pdim]; // the new i\th row of p
          for(int j=0; j<pdim; j++){

            t[j] = inputMatrix[0].create().setDouble(0.0);

            for(int k=0; k<pdim; k++)
              t[j] = t[j].add(elemMatrix(1, inputMatrix[0].create().setDouble(0.0),
                                         row, indicesOfMax[0], j, k).
                                mult(getElementRef(productOfElmntMatrices, pdim, pdim, k, i, rowOrColumn)));
          }

          for(int j=0; j<pdim; j++)
            setElementRef(productOfElmntMatrices, pdim, pdim, j, i, t[j], rowOrColumn);
        }
      }

      // now eliminate all the elements in the pivot column...
      for(int i=row+1; i<n; i++){

        // set the multiplier for Gauss elimination...
        MNumber mult = inputMatrix[0].create().setDouble(-1.0);
        mult.mult(getElement(outputMatrix, n, m, i, col, rowOrColumn)).
          div(getElement(outputMatrix, n, m, row, col, rowOrColumn));

        // now loop downwards in the current column and make
        // all the entries in this column 0-s...
        for(int j=col; j<m; j++){
          s = getElement(outputMatrix, n, m, i, j, rowOrColumn);
          s = s.add(getElement(outputMatrix, n, m, row, j, rowOrColumn).mult(mult));
          setElementRef(outputMatrix, n, m, i, j, s, rowOrColumn);
        }

        // protocol the changes in the elementary matrix product by multiplying
        // it with the corresponding elementary matrix: addition of two rows (3)
        for(int j=0; j<pdim; j++){
          MNumber t[] = new MNumber[pdim]; // the new i\th row of p
          for(int k=0; k<pdim; k++){

            t[k] = inputMatrix[0].create().setDouble(0.0);
            for(int l=0; l<pdim; l++){
              t[k] = t[k].add(elemMatrix(3, mult, i, row, k, l).
                          mult(getElementRef(productOfElmntMatrices, pdim, pdim, l, j, rowOrColumn)));
            }
          }

          for(int k=0; k<pdim; k++)
            setElementRef(productOfElmntMatrices, pdim, pdim, k, j, t[k], rowOrColumn);
        }

      }

      // store the current pivot position and descend to the next submatrix...
      pivots[row] = col;
      //debug(e,n,m);
      row++;
      col++;
    }

    return pivots;
  }

  /**
   * This private method searches the absolute maximum element in the first
   * nonzero column (row) of a submatrix whose upper left element is given
   * by the indices row and col. a is the initial input matrix represented as
   * an array of MMNumbers, where the rows of the matrix are stored successively.
   * m and n are the dimensions of the matrix. If the absolute value
   * of an element of the input matrix is less or equal than the absolute
   * value of eps the element is considered to be zero. the parameter typ
   * indicates whether the columns or the rows should be searched. The method
   * returns an array of two elements holding the row and column of the maximum
   * element. If all the elements in the submatrix were zero, the method returns
   * null.
   */
  protected static int[] findAbsMax(MNumber[] inputMatrix, int col, int row, int n, int m,
                                  boolean rowOrColumn){
    // initialize the flag indicating
    // a zero column to be true...
    boolean zeroCol = true;

    // this variable will be used for comparison...
    MNumber compare;

    // allocate an array of two int-s and
    // initialize it to the indices of the first element
    // of the submatrix...
    int[] rowcol = new int[2];
    rowcol[0] = row;
    rowcol[1] = col;


    // begin here searching the nonzero columns in the submatrix...
    for(int cl=col; cl<m; cl++){

      // get the uppermost element in the current column of the
      // submatrix and store it in comp...
      compare = getElement(inputMatrix, n, m, row, cl, rowOrColumn);

      // another auxiliary variable for comparison...
      MNumber tmp;

      // search down the column and find the absolute maximum element
      // in that column. Store the row as the first element of rowcol array...
      for(int i=row+1; i<n; i++){

        tmp = getElement(inputMatrix, n, m, i, cl, rowOrColumn);

       // if(Math.abs(tmp.getDouble()) > Math.abs(compare.getDouble())){ // for performance reasons we use the double value for comparison
        if(tmp.absed().getDouble() > compare.absed().getDouble()){
        compare = tmp;
          rowcol[0] = i;
        }
      }
      // store the current column as the second element in rowcol...
      rowcol[1] = cl;

      // now check whether the found element is zero or not.
      // if it is, continue with the next column, if it is not
      // return the indices of this element...
      if( compare.absed().getDouble() > EPSILON) // for performance reasons we use the double value for comparison
        return rowcol;

    }

    // if there are no more columns to consider and all the elements were
    // zero, return null...
    return null;
  }


  /**
   * This private method returns elements of an elementary matrix. The parameter
   * type indicates the type of the elementary matrix. The parameter alf is used
   * for certain elementary matrices such as the scaling elementary matrix e.g.
   * Parameters first and second indicate which rows should be swapped in the
   * elementary matrix which performs the row exchange. Parameters i and j
   * indicate the row and column of the element of the matrix we are interested
   * in.
   */
  protected static MNumber elemMatrix(int type, MNumber alpha, int first,
                                     int second, int i, int j){

    switch(type){

      // the type 1 matrix. we just swap the rows first and second...
      case 1:
        if(i == first)
          i = second;
        else if(i == second)
          i = first;
        break;

        // type 2 matrix is scaling of a row by alpha,
        // so return alpha if the element is diagonal and
        // the indices correspond to this row...
      case 2:
        if(i == first && j == first)
          return alpha.copy();
        break;

        // type 3 is addition of a multiple of a row to another
        // row. return alpha if i and j correspond to these rows.
      case 3:
        if(i==first && j==second)
          return alpha.copy();
        break;

      default:
          return alpha.create().setDouble(0.0);
    }

    if(i == j)
      return alpha.create().setDouble(1.0);

    return alpha.create().setDouble(0.0);
  }

  /**
   * This private method sets an element of a matrix to the specified value. a
   * is a matrix represented as an array of MMNumbers, where the rows of the
   * matrix are stored successively. m and n are the dimensions of the matrix.
   * i and j represent the indices of the element to be set. Parameter elem is
   * the new element. If the absolute value of the element is less or equal
   * than the absolute value of eps the element is considered to be zero. the
   * parameter typ indicates wheather the matrix should be considered as
   * transposed.
   */
  protected static void setElementRef(MNumber[] a, int n, int m, int i, int j,
                                 MNumber elem, boolean rowOrColumn){
    /*
    if(new MDouble().setDouble(elem.abs().getDouble()).lessOrEqualThan(eps.abs())){
      elem.setDouble(0.0);
     } */
    if(elem.absed().getDouble() < EPSILON)
    //	if(Math.abs(elem.getDouble()) < EPSILON)
      elem.setDouble(0);
    if(rowOrColumn == ROW_FORM){
      a[i*m+j]=elem;
    }
    else{
      a[i+j*n]=elem;
    }
  }


  /**
   * This private method gets an element of a matrix. a is a matrix represented
   * as an array of MMNumbers, where the rows of the matrix are stored
   * successively. m and n are the dimensions of the matrix. i and j represent
   * the indices of the element to be returned. If the absolute value of the
   * element is less or equal than the absolute value of eps the element is
   * considered to be zero. the parameter rowOrColumn indicates wheather the
   * matrix should be considered as transposed.
   */
  protected static MNumber getElementRef(MNumber[] a, int n, int m, int i, int j,
                                     boolean rowOrColumn){
    MNumber t;
    if(rowOrColumn == ROW_FORM)
      t = a[i*m+j];
    else
      t = a[i+j*n];

    /*if(new MDouble().setDouble(t.abs().getDouble()).lessOrEqualThan(eps.abs()))
      t.setDouble(0.0);
     */
    return t;
  }

  /**
   * This private method gets an element of a matrix. a is a matrix represented
   * as an array of MMNumbers, where the rows of the matrix are stored
   * successively. m and n are the dimensions of the matrix. i and j represent
   * the indices of the element to be returned. If the absolute value of the
   * element is less or equal than the absolute value of eps the element is
   * considered to be zero. the parameter rowOrColumn indicates wheather the
   * matrix should be considered as transposed.
   */
  protected static MNumber getElement(MNumber[] a, int n, int m, int i, int j,
                                     boolean rowOrColumn){
    return getElementRef(a, n, m, i, j, rowOrColumn).copy();
  }


  /**
   * This private method gets an element of a matrix. a is a matrix represented
   * as an array of MMNumbers, where the rows of the matrix are stored
   * successively. m and n are the dimensions of the matrix. i1,j1 and i2,j2
   * represent the indices of the elements to be swapped respectively. The
   * parameter rowOrColumns indicates whether the matrix should be considered as
   * transposed.
   */
  protected static void swapElements(MNumber[] a, int n, int m, int i1, int j1,
                                   int i2, int j2, boolean rowOrColumn){
    int ind1 = 0;
    int ind2 = 0;
    if(rowOrColumn == ROW_FORM){
      ind1 = i1*m+j1;
      ind2 = i2*m+j2;
    }
    else{
      ind1 = i1+j1*n;
      ind2 = i2+j2*n;
    }
    // we can use references here without any harm:
    MNumber t = a[ind1];
    a[ind1] = a[ind2];
    a[ind2] = t;
  }

  /**
   *  For debugging purposes: output the matrix values
   */
  public static void debug(MNumber[] a, int m, int n){

    System.out.println("");
    for(int i=0; i<m; i++)
      for(int j=0; j<n; j++)
         System.out.print(" "+a[i*n+j].toString()+ (j/(n-1)!=0 ? "\n" : ", "));
    System.out.println("");
  }
}







