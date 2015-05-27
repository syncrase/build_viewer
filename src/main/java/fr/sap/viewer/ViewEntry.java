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

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.lang.Validate;

/**
 * Represent a set of jobs (ProjectImpl)
 * <p>
 * @author I310911
 */
public class ViewEntry {

    // Constructs a new, empty tree set, sorted according to the specified comparator.
//    private TreeSet<IViewEntry> jobs = new TreeSet<IViewEntry>(new TreeSetComparatorOverride());
    //private TreeSet<AbstractProject> projects = new TreeSet<AbstractProject>();
    HashSet<ProjectImpl> projects = new HashSet<ProjectImpl>();

    private final String prefixe;
    private ViewEntryColors currentState;

    ViewEntry(BuildViewer bv, ProjectImpl proj) {
//        projects = new HashSet<ProjectImpl>();
        projects.add(proj);
//        if (prefixUSed()) {
//            this.prefixe = proj.getPrefixe();
//        } else {
//            this.prefixe = "NO_PREFIX";
//        }
this.prefixe ="";
        this.refreshState(bv);
    }

    /**
     * Add project only if it has the same prefix of the view
     * <p>
     * @param entry
     */
    void addProject(ProjectImpl entry) {
        Validate.notNull(entry);
        if (entry.getPrefixe().equals(this.prefixe)) {
            projects.add(entry);
        }

    }

    /**
     *
     * @return The first job, if it exists
     */
    public ProjectImpl getMyJob() {
        Iterator<ProjectImpl> it = projects.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public String getPrefixe() {
        return this.prefixe;
    }

    public String getBackgroundColor() {

        return currentState.getVe_backgroundColor();

    }

    public int getCount() {
        return projects.size();
    }

    public HashSet<ProjectImpl> getProjects() {
        return projects;
    }

//    /**
//     * Return the state of the Entry, representative of a group of projects or a
//     * single project
//     * <p>
//     * @return
//     */
//    public String getResult() {
//
//        int maxRank = 0;
//        int index = 0;
//        String result = "";
//
//        for ( ProjectImpl p : projects ) {// Go trough projects
//
//            for ( ViewEntryColors state : BuildViewer.getCOLOR_SETTINGS() ) {// Find the corresponding status
//                if (p.getResult().equals(state.getVe_state())) {
//
//                    
//                    if (index >= maxRank) {
//                        maxRank = index;// Keep the index of the highest priority state. Highest it is, highest the priority is
//                        result = state.getVe_state();
//                    }
//                }
//                index++;
//            }
//            index = 0;
//        }
//        return result;
//    }
    /**
     * Refresh values of the ViewEntryColor field
     * <p>
     * @return
     */
    private void refreshState(BuildViewer bv ) {

        int maxRank = 0;
        int index = 0;
        String result = "";

        for ( ProjectImpl p : projects ) {// Go trough projects

            for ( ViewEntryColors state : bv.getCOLOR_SETTINGS() ) {// Find the corresponding status
                if (p.getResult().equals(state.getVe_state())) {

                    if (index >= maxRank) {
                        maxRank = index;// Keep the index of the highest priority state. Highest it is, highest the priority is
//                        result = state.getVe_state();
                        currentState = state;
                    }
                }
                index++;
            }
            index = 0;
        }
    }

//    private boolean prefixUSed() {
//
////        if(BuildViewer.getPrefixesSeparators()==null){
////            return false;
////        }else{
////            return BuildViewer.getPrefixesSeparators().size()>0;
////        }
//        return (BuildViewer.getPrefixesSeparators() == null) ? false : (BuildViewer.getPrefixesSeparators().size() > 0);
//    }
}
