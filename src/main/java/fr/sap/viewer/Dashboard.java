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
    private final List<ProjectImpl> contents;
    private Collection<ViewEntry> viewEntriesCol;

    Dashboard(BuildViewer bv, List<ProjectImpl> contents) {
        this.contents = contents;
        this.bv = bv;
        viewEntriesCol = this.toViewEntryCollection(contents);
    }

    //**************************************************************************
    // Getters / setters
    //**************************************************************************
    public List<ProjectImpl> getContents() {
        return contents;
    }

    public Collection<ViewEntry> getViewEntriesCol() {
        return Collections.unmodifiableCollection(viewEntriesCol);
    }

    public int getCountOfViews() {
        return viewEntriesCol.size();
    }

    /**
     * Gather projects in ViewEntry based on theirs prefixes
     * <p>
     * @param contents
     *                 <p>
     * @return
     */
    private Collection<ViewEntry> toViewEntryCollection(List<ProjectImpl> contents) {
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
        this.viewEntriesCol = views;
        return views;
    }

}
