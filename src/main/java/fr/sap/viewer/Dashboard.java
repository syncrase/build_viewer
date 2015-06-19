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
import java.util.Collections;
import java.util.List;

/**
 * This represents the container of the Dashboard (under the caption) which
 * contains all entries, either projects or projects grouped by prefix
 * <p>
 * @author I310911
 */
public class Dashboard {

    private final BuildViewer bv;
    private Collection<ViewEntry> viewEntries;

    Dashboard(BuildViewer bv, List<ProjectWrapper> contents) {
//        this.contents = contents;
        this.bv = bv;
        viewEntries = this.toViewEntryCollection(contents);
    }

    //**************************************************************************
    // Getters / setters
    //**************************************************************************
    public Collection<ViewEntry> getViewEntries() {//TODO
        return Collections.unmodifiableCollection(viewEntries);
    }

    public int getViewsCount() {//TODO
        return viewEntries.size();
    }

    /**
     * Gather projects in ViewEntry based on theirs prefixes
     * <p>
     * @param contents
     *                 <p>
     * @return
     */
    private Collection<ViewEntry> toViewEntryCollection(List<ProjectWrapper> contents) {
        Collection<ViewEntry> views = new ArrayList<ViewEntry>();
        goThroughProjects:
        for ( ProjectWrapper proj : contents ) {
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
        this.viewEntries = views;
        return views;
    }

}
