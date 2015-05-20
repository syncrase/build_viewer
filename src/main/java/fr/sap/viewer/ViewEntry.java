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
 * @author I310911
 */
public class ViewEntry {

    // Constructs a new, empty tree set, sorted according to the specified comparator.
//    private TreeSet<IViewEntry> jobs = new TreeSet<IViewEntry>(new TreeSetComparatorOverride());
    //private TreeSet<AbstractProject> projects = new TreeSet<AbstractProject>();
    HashSet<ProjectImpl> projects = new HashSet<ProjectImpl>();

    ViewEntry(ProjectImpl proj) {
//        projects = new HashSet<ProjectImpl>();
        projects.add(proj);
    }


    public String getProjectsNames() {
        for(ProjectImpl project:projects) {
            
        }
        
        Iterator<ProjectImpl> it = projects.iterator();
        ProjectImpl p;
        String returnedStr = "";

        while (it.hasNext()) {
            p = it.next();
//            returnedStr += p.getName();
            //returnedStr += p.abstractProject_Info();
            //returnedStr += p.run_Info();
            //returnedStr += p.build_Info();
            returnedStr += p.test();
        }

        return returnedStr;
    }



    public String getDefect() throws URISyntaxException {
        String defect = "";

//        try {
//            //XmlFile xmlFile = new XmlFile(XSTREAM, getDataFile("junitResult.xml"));
//            hudson.FilePath.getHomeDirectory(null);
//            
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ViewEntry.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ViewEntry.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
        return "no bug";
    }

    void addBuild(ProjectImpl entry) {
        Validate.notNull(entry);
        projects.add(entry);
    }


//    public Job<?, ?> getJob() {
//        // Used in the TreeSetComparatorOverride
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }


//    public String getName() {
//        // Used in the TreeSetComparatorOverride and in job.jelly
//        return "MyName";
//    }
    
    /**
     * 
     * @return The first job, if it exists
     */
    public ProjectImpl getMyJob(){
        Iterator<ProjectImpl> it = projects.iterator();
        if(it.hasNext()){
            return it.next();
        }
        return null;
    }

    public String getBackgroundColor(){
        return "#CCCCCC";
    }
}
