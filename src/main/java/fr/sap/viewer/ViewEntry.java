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

import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.lang.Validate;

/**
 * Represent a set of jobs (ProjectWrapper)
 * <p>
 * @author I310911
 */
public class ViewEntry {

    private final HashSet<ProjectWrapper> projects = new HashSet<ProjectWrapper>();
    private final String prefix;
    private ViewEntryColor currentState;
    private final BuildViewer bv;

    ViewEntry(BuildViewer bv, ProjectWrapper proj) {
        this.bv = bv;
        projects.add(proj);
        this.prefix = (bv.getPrefixesSeparators() != null && bv.getPrefixesSeparators().size() >= 1) ? proj.getPrefix() : ProjectWrapper.NO_PREFIX;
        this.refreshState();
    }

    //**************************************************************************
    // Getters/setters
    //**************************************************************************
    public HashSet<ProjectWrapper> getProjects() {
        return projects;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getBackgroundColor() {
        // TODO refresh
        return currentState.getVe_backgroundColor();
    }

    public int getCount() {
        return projects.size();
    }

    //**************************************************************************
    // projects handlers
    //**************************************************************************
    /**
     * Add project only if it has the same prefix of the view
     * <p>
     * @param entry
     */
    public void addProject(ProjectWrapper entry) {
        if (entry != null && entry.getPrefix().equals(this.prefix)) {
            projects.add(entry);
            this.refreshState();
        }
    }

    /**
     *
     * @return The first job, if it exists
     */
    public ProjectWrapper getFirstJob() {
        Iterator<ProjectWrapper> it = projects.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    /**
     * Refresh values of the ViewEntryColor field based on projects results
     * <p>
     * @return
     */
    private void refreshState() {

        int highestPriority = 0;//TODO
        int cursor = 0;
        String projectResult = "";

        for ( ProjectWrapper p : projects ) {// Go trough projects in this view
            projectResult = p.getResult();
            for ( ViewEntryColor state : bv.getViewEntryColors() ) {// Find the corresponding status
                if (projectResult.equals(state.getVe_state())) {

                    if (cursor >= highestPriority) {
                        highestPriority = cursor;// Keep the index of the highest priority state. Highest it is, highest the priority is
                        currentState = state;
                        break;
                    }
                }
                cursor++;
            }
            cursor = 0;
        }
    }

}
