/*
 * The MIT License
 *
 * Copyright 2015 I310911.
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
package fr.sap.viewer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author I310911
 */
public class DashboardUtils {

//    /**
//     * This method compute the best possible repartition of n elements in a
//     * square
//     * Doesn't works!!
//     * <p>
//     * @return The representative list of the square, the index is the line and
//     *         the value is the number of elements it contains
//     */
//    public static List<Integer> computeTheBestSquareRepartition(int initialNumberOfProjects) {
//        List<Integer> matrix = new ArrayList<Integer>();
//        // System.out.println("Hello");
//        // int initialNumberOfProjects = 7;
//        int nbProjects = initialNumberOfProjects;
//        int nbColumn = 0;
//        // int nbLines;
//
//        int i;
//        int projectsRemains = initialNumberOfProjects;
//        do {
//            // If I can set remains projects on one line, I set these and I quit the loop
//            projectsRemains = initialNumberOfProjects - sum(matrix);
//            if (projectsRemains <= nbColumn) {
//                matrix.add(new Integer(projectsRemains));
//                break;
//            }
//            i = 0;
//            while (nbProjects > i * i) {
//                i++;
//
//            }
//            if (nbColumn == 0) {
//                nbColumn = i;
//            }
//            nbProjects -= i;
//            matrix.add(new Integer(i));
//        } while (true);
//
//        // System.out.println(matrix);
//        return matrix;
//    }

//    public static int sum(List<Integer> l) {
//        int sum = 0;
//        for ( Integer i : l ) {
//            sum += i.intValue();
//        }
//        return sum;
//    }

//    public static int[] getPosition(int index) {
//        //index désiré  /  largeur  =  n et reste r
//        //si r == 0  => ligne = n largeur==nbColumn
//        //si r != 0  => ligne = n largeur == r
//        return new int[]{1, 2};
//    }
    
    
    
    
    
    
//    
//    /**
//     * 
//     * @param totalViews Number of ViewEntry contained in the dashboard
//     * @return the number with the square directly superior or equal
//     */
//    public static int getCountOfViewsPerRow(int totalViews) {
//        int i = 0;
//        while (totalViews > i * i) {
//            i++;
//        }
//        return i;
//    }
//    
//    /**
//     * @param countOfViews
//     * @return the number of rows {0} and the multiplier coefficient for each view in the last row {1}. -1 if the rate countOfViews/countOfViewsPerRows is a whole integer
//     */
//    public static double[] getCountOfRows(int countOfViews){
//        double[] returnedTab = new double[2];
//        
//        
//        double rate = (double)countOfViews/getCountOfViewsPerRow(countOfViews);
//        returnedTab[0] = Math.floor(rate);
//        
//        if(rate != returnedTab[0]){
//            // if there's a decimal part
//            
//            returnedTab[1] = rate - returnedTab[0];
//            returnedTab[0] += 1 ;
//        }else{
//            returnedTab[1] = -1;
//        }
//        
//        // For instance, fi there's 7 views => 3 views per rows
//        // 7/3 = 3.5
//        // The width of the last views will divided per 0.5
//         return returnedTab;
//    }
//
//    public static int getViewsPerColumn(int totalViews) {
//        int columnCount = getCountOfViewsPerRow(totalViews);
////        if(totalViews > 2*columnCount){
////            return columnCount;
////        }else{
////            return columnCount-1;
////        }
//        return columnCount - ((totalViews > 2 * columnCount) ? 0 : 1);
//    }
//
//    
//
//    public static int getCountOfViews(Collection<Collection<ViewEntry>> rows) {
//        int count = 0;
//        for ( Collection<ViewEntry> col : rows ) {
//            for ( ViewEntry ve : col ) {
//                count++;
//            }
//        }
//        return count;
//    }

}
