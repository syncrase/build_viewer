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
 * This represents the container of the Dashboard (under the caption) which
 * contains all entries, either projects or projects grouped by prefix
 * <p>
 * @author I310911
 */
public class Dashboard {

    private Collection<Collection<ViewEntry>> rows;
    /**
     * Margin of a view in this dashboard, expressed in percentage
     */
    private double margin;
    private double dashboardHeightInPixel;

    private double viewsHeight;
    private double multiplier;
    private double viewsWidth;
    private BuildViewer bv;

    Dashboard(BuildViewer bv, List<ProjectImpl> contents) {
        this.bv = bv;
        rows = toRows(this.toViewList(contents));
        this.dashboardHeightInPixel = this.bv.getDEFAULT_SCREEN_HEIGHT() - this.bv.getCaptionSize();
        double[] a = Utils.getCountOfRows(Utils.getCountOfViews(rows));
        this.viewsHeight = dashboardHeightInPixel / a[0];
        this.multiplier = a[1];
        this.viewsWidth = this.bv.getDEFAULT_SCREEN_WIDTH() / Utils.getCountOfViewsPerRow(Utils.getCountOfViews(rows));
        this.margin = Math.ceil(viewsHeight / 100);
        this.viewsHeight -= this.margin * 2 * a[0];
        this.viewsWidth -= this.margin * 2 * Utils.getCountOfViewsPerRow(Utils.getCountOfViews(rows));

    }

    public Collection<Collection<ViewEntry>> getRows() {
        return rows;
    }

    public double getViewsHeight() {
        // max height divided per the count od rows
//        int countOfViews = 0;
        return viewsHeight;

        // Default : 90%
//        double mawWidthMinusMargin = ((100 - 2 * margin) / (double)100);
//        int countOfViews = Utils.getCountOfViews(rows);
//        double maxWidth = 100 / Utils.getViewsPerColumn(countOfViews);
//        
//        double computedWidth = maxWidth * mawWidthMinusMargin;
//        int countOfViews = Utils.getCountOfViews(rows);
//        int viewsPerColumn = Utils.getViewsPerColumn(countOfViews);
//        double heightPercentage = (100 / viewsPerColumn );
//        return heightPercentage-2*margin;
    }

    public double getViewsWidth() {
//        double mawWidthMinusMargin = ((100 - 2 * margin) / 100);
//        int countOfViews = Utils.getCountOfViews(rows);

        return viewsWidth;
    }

    public double getMargin() {

        return margin;
    }

    public double getDashboardHeightInPixel() {
        return dashboardHeightInPixel;
    }

    /**
     * Converts a list of ViewEntries to a list of list of jobs, suitable for
     * display
     * as rows in a table.
     * <p>
     * @param views
     *              the jobs to include.
     * <p>
     * @return a list of fixed size view entry lists.
     */
    private Collection<Collection<ViewEntry>> toRows(Collection<ViewEntry> views) {
        int jobsPerRow = 0;

        jobsPerRow = Utils.getCountOfViewsPerRow(views.size());

        Collection<Collection<ViewEntry>> rows = new ArrayList<Collection<ViewEntry>>();
        Collection<ViewEntry> current = null;
        int i = 0;
        for ( ViewEntry view : views ) {
            if (i == 0) {
                current = new ArrayList<ViewEntry>();
                rows.add(current);
            }
            current.add(view);
            i++;
            if (i >= jobsPerRow) {
                i = 0;
            }
        }
        return rows;
    }

    /**
     * Gather projects in ViewEntry based on theirs prefixes
     * <p>
     * @param contents
     *                 <p>
     * @return
     */
    private Collection<ViewEntry> toViewList(List<ProjectImpl> contents) {

        Collection<ViewEntry> views = new ArrayList<ViewEntry>();
        goThroughProjects:
        for ( ProjectImpl proj : contents ) {

            if (proj.getPrefix() != null) {// If the project has a prefix
                for ( ViewEntry view : views ) {
                    if (proj.getPrefix().equals(view.getPrefix())) {// If a viewEntry with the same prefix already exists
                        view.addProject(proj);
                        continue goThroughProjects;
                    }
                }
                //No ViewEntry with no one of these prefixs
            }
            views.add(new ViewEntry(bv, proj));//Set the project (either the prefix is not yet available or it doesn't exists) in just one view
        }
        return views;
    }

}

class Utils {

    /**
     *
     * @param totalViews Number of ViewEntry contained in the dashboard
     * <p>
     * @return the number with the square directly superior or equal
     */
    public static int getCountOfViewsPerRow(int totalViews) {
        int i = 0;
        while (totalViews > i * i) {
            i++;
        }
        return i;
    }

    /**
     * @param countOfViews
     * <p>
     * @return the number of rows {0} and the multiplier coefficient for each
     *         view in the last row {1}. -1 if the rate
     *         countOfViews/countOfViewsPerRows is a whole integer
     */
    public static double[] getCountOfRows(int countOfViews) {
        double[] returnedTab = new double[2];

        double rate = (double) countOfViews / getCountOfViewsPerRow(countOfViews);
        returnedTab[0] = Math.floor(rate);

        if (rate != returnedTab[0]) {
            // if there's a decimal part

            returnedTab[1] = rate - returnedTab[0];
            returnedTab[0] += 1;
        } else {
            returnedTab[1] = -1;
        }

        // For instance, fi there's 7 views => 3 views per rows
        // 7/3 = 3.5
        // The width of the last views will divided per 0.5
        return returnedTab;
    }

    public static int getViewsPerColumn(int totalViews) {
        int columnCount = getCountOfViewsPerRow(totalViews);
//        if(totalViews > 2*columnCount){
//            return columnCount;
//        }else{
//            return columnCount-1;
//        }
        return columnCount - ((totalViews > 2 * columnCount) ? 0 : 1);
    }

    public static int getCountOfViews(Collection<Collection<ViewEntry>> rows) {
        int count = 0;
        for ( Collection<ViewEntry> col : rows ) {
            for ( ViewEntry ve : col ) {
                count++;
            }
        }
        return count;
    }

}
