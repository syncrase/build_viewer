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
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;

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

    private final String NO_PREFIX_FOUND = "NO_PREFIX_FOUND";
    private final String NO_PREFIX_AVAILABLE = "NO_PREFIX_AVAILABLE";

    Dashboard(List<ProjectImpl> contents, double dashboardHeightInPixels, double dashboardWidthInPixels) {
//        this.rows = toRows;

        rows = toRows(this.toViewList(contents));
//        this.margin = 0.2;
        this.dashboardHeightInPixel = dashboardHeightInPixels;
        double[] a = DashboardUtils.getCountOfRows(DashboardUtils.getCountOfViews(rows));
        this.viewsHeight = dashboardHeightInPixels / a[0];
        this.multiplier = a[1];
        this.viewsWidth = dashboardWidthInPixels / DashboardUtils.getCountOfViewsPerRow(DashboardUtils.getCountOfViews(rows));
        this.margin = Math.ceil(viewsHeight / 100);
        this.viewsHeight -= this.margin * 2 * a[0];
        this.viewsWidth -= this.margin * 2 * DashboardUtils.getCountOfViewsPerRow(DashboardUtils.getCountOfViews(rows));
    }

//    private final List<DashboardEntity[]> dashboardEntityMatrix;
//    Dashboard(List<Integer> repartitionComputed) {
//        //Create the dashboard pattern
//        dashboardEntityMatrix = new ArrayList<DashboardEntity[]>();
//
//        for ( Integer i : repartitionComputed ) {
//            dashboardEntityMatrix.add(new DashboardEntity[i]);
//        }
//    }
//    void addDashboardEntity(DashboardEntity dashboardEntity, int index) {
//        
//        int[] coord = DashboardUtils.getPosition(index);
//        dashboardEntityMatrix.get(coord[0])[coord[1]] = dashboardEntity;
//        
//        
//    }
//    public int getCountOfEntities() {
//        int count = 0;
//        for ( DashboardEntity[] tab : dashboardEntityMatrix ) {
//            count += tab.length;
//        }
//        return count;
//    }
    public Collection<Collection<ViewEntry>> getRows() {
        return rows;
    }

    public double getViewsHeight() {
        // max height divided per the count od rows
//        int countOfViews = 0;
        return viewsHeight;

        // Default : 90%
//        double mawWidthMinusMargin = ((100 - 2 * margin) / (double)100);
//        int countOfViews = DashboardUtils.getCountOfViews(rows);
//        double maxWidth = 100 / DashboardUtils.getViewsPerColumn(countOfViews);
//        
//        double computedWidth = maxWidth * mawWidthMinusMargin;
//        int countOfViews = DashboardUtils.getCountOfViews(rows);
//        int viewsPerColumn = DashboardUtils.getViewsPerColumn(countOfViews);
//        double heightPercentage = (100 / viewsPerColumn );
//        return heightPercentage-2*margin;
    }

    public double getViewsWidth() {
//        double mawWidthMinusMargin = ((100 - 2 * margin) / 100);
//        int countOfViews = DashboardUtils.getCountOfViews(rows);

        return viewsWidth;
    }

    public double getMargin() {

        return margin;
    }

    public double getDashboardHeightInPixel() {
        return dashboardHeightInPixel;
    }

    /**
     * Converts a list of jobs to a list of list of jobs, suitable for display
     * as rows in a table.
     * <p>
     * @param views
     *              the jobs to include.
     * <p>
     * @return a list of fixed size view entry lists.
     */
    private Collection<Collection<ViewEntry>> toRows(Collection<ViewEntry> views) {
        int jobsPerRow = 0;

        jobsPerRow = DashboardUtils.getCountOfViewsPerRow(views.size());

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

    private Collection<ViewEntry> toViewList(List<ProjectImpl> contents) {
        Collection<ViewEntry> views = new ArrayList<ViewEntry>();

        //Rassemblement des projets portant les mêmes préfixes dans les views entry
        // deux cas le nom du projet possède, ou pas, un préfixe
        // plusieurs séparateurs possibles (- | . | * | ... ) ceux-ci sont définis dans l'écran de configuration
        // 
        //
        //
        //      StringUtils.substringBefore(name, separator)
        for ( ProjectImpl proj : contents ) {
            views.add(new ViewEntry(proj));
        }
        return views;
    }

    /**
     *
     * @param name The name of the job
     * <p>
     * @return the prefix use for this job<br/>null if no prefix has been found
     *         in the project name
     */
    private String getPrefix(String name) {
        HashSet<String> prefixes = BuildViewer.getPrefixes();
        if (prefixes != null) {
            if (prefixes.size() > 0) {
                for ( String prefix : prefixes ) {
                    if (name.contains(prefix)) {
                        return StringUtils.substringBefore(name, prefix);
                    }
                }
                return this.NO_PREFIX_FOUND;
            } else {
                return this.NO_PREFIX_AVAILABLE;
            }
        }
        throw new RuntimeException("There's no prefixes list. Initialization issue.");

    }

}
