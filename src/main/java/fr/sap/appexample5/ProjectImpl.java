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
package fr.sap.appexample5;

import com.google.common.io.Files;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.Run;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.Charsets;

/**
 *
 * @author I310911
 */
public class ProjectImpl {

    
    
    private final AbstractProject abstractProject;

    public ProjectImpl(AbstractProject abstractProject) {
        this.abstractProject = abstractProject;
    }

    /**
     *
     * @return
     */
    public AbstractProject getAbstractProject() {
        return abstractProject;
    }

    public String getName() {
        //Used in job.jelly
        return abstractProject.getName();
    }

    public String abstractProject_Info() {
        String s = "";
        s += "getAbsoluteUrl : " + abstractProject.getAbsoluteUrl();
        s += "<br\\>getAssignedLabelString : " + abstractProject.getAssignedLabelString();
        s += "<br\\>getBuildNowText : " + abstractProject.getBuildNowText();
        s += "<br\\>getBuildStatusUrl : " + abstractProject.getBuildStatusUrl();
        s += "<br\\>getCustomWorkspace : " + abstractProject.getCustomWorkspace();
        s += "<br\\>getDescription : " + abstractProject.getDescription();
        s += "<br\\>getDisplayName : " + abstractProject.getDisplayName();
        s += "<br\\>getDisplayNameOrNull : " + abstractProject.getDisplayNameOrNull();
        s += "<br\\>getFullDisplayName : " + abstractProject.getFullDisplayName();
        s += "<br\\>getFullName : " + abstractProject.getFullName();
        s += "<br\\>getName : " + abstractProject.getName();
        s += "<br\\>getPronoun : " + abstractProject.getPronoun();
        s += "<br\\>getSearchName : " + abstractProject.getSearchName();
        s += "<br\\>getSearchUrl : " + abstractProject.getSearchUrl();
        s += "<br\\>getShortUrl : " + abstractProject.getShortUrl();
        s += "<br\\>getUrl : " + abstractProject.getUrl();
        s += "<br\\>getWhyBlocked : " + abstractProject.getWhyBlocked();

        return s;
    }

    public String run_Info() {
        String s = "";
        Run r = this.abstractProject.getLastCompletedBuild();

        s += "getAbsoluteUrl : " + r.getAbsoluteUrl() + "\n";
        s += "getBuildStatusUrl : " + r.getBuildStatusUrl() + "\n";
        s += "getDescription : " + r.getDescription() + "\n";
        s += "getDisplayName : " + r.getDisplayName() + "\n";
        s += "getDurationString : " + r.getDurationString() + "\n";
        s += "getExternalizableId : " + r.getExternalizableId() + "\n";
        s += "getFullDisplayName : " + r.getFullDisplayName() + "\n";
        s += "getId : " + r.getId() + "\n";
        try {
            s += "getLog : " + r.getLog() + "\n";
        } catch (IOException ex) {
            Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        s += "getSearchName : " + r.getSearchName() + "\n";
        s += "getSearchUrl : " + r.getSearchUrl() + "\n";
        s += "getTimestampString : " + r.getTimestampString() + "\n";
        s += "getTimestampString2 : " + r.getTimestampString2() + "\n";
        s += "getTruncatedDescription : " + r.getTruncatedDescription() + "\n";
        s += "getUrl : " + r.getUrl() + "\n";
        s += "getWhyKeepLog : " + r.getWhyKeepLog() + "\n";

        return s;
    }

    public String build_Info() {
        String s = "";
        File f = this.abstractProject.getBuildDir();

        s += "getAbsolutePath : " + f.getAbsolutePath() + "\n";
        try {
            s += "getCanonicalPath : " + f.getCanonicalPath() + "\n";
        } catch (IOException ex) {
            Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        s += "getName : " + f.getName() + "\n";
        s += "getParent : " + f.getParent() + "\n";
        s += "getPath : " + f.getPath() + "\n";

        return s;
    }

    public String test() {
        //String s = "";

        String path = "C:\\Users\\I310911\\perforce\\testWRKSPC\\testPlugin\\appexample5\\work\\jobs\\mavenTest\\" + "modules\\utilitaires$mavenTest.artifactid\\builds\\";
        File f = new File(path);
        //s=f.list().toString();

        //get folder item
        String[] tab = f.list();
        String content = "";
        for ( String tab1 : tab ) {
            //s += "1: " + tab[i] + "   _|_   ";
            f = new File(path + tab1);
            if (f.isDirectory()) {
                // Get xml
                f = new File(path + tab1 + "\\junitResult.xml");
                try {
                    content = Files.toString(f, Charsets.UTF_8);
                } catch (IOException ex) {
                    Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        if (!"".equals(content)) {
            this.setTextContentInFile(true, content, "monFichierXml.xml");
            return content;
        }
        return "Fail to access file";

    }

    public void setTextContentInFile(boolean setHeader, String content, String filename) {
        // source folder --> c:\\Program files (x86)\Jenkins\
        String pathstr = "C:\\Users\\I310911\\jenkinsNetBeans\\" + filename;
        // Créer et écrire dans un fichier
        BufferedWriter out;
        try {
            File f = new File(pathstr);
            f.createNewFile();
            out = new BufferedWriter(new FileWriter(pathstr, false));
            if (setHeader == true) {
                out.write("*********************** :-)  " + new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz").format(new Date())
                          + "  (-: ***********************\n");
            }
            out.write(new SimpleDateFormat("hh:mm:ss a zzz").format(new Date()) + content + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public String getFontColor() {
//        //Used in job.jelly
//        // retourne une couleur en fonction du résultat
//        // Les différents résultats possibles sont stockés dans la liste
//
//        return "#88ff88";
//    }

    public String getLastBuildUrl() {
        return abstractProject.getSearchUrl();
    }

    
    /**
     *
     * @return "SUCCESS", "UNSTABLE", "FAILURE", "NOT_BUILT", "ABORTED"<br/>
     * null if the job cannot be accessed
     */
    public String getResult() {
        /*        @SuppressWarnings("unchecked")*/
        Collection<Job> colJob = abstractProject.getAllJobs();
        Iterator<Job> it = colJob.iterator();
        if (it.hasNext()) {
            Job job = it.next();
            try {
                return job.getLastBuild().getResult().toString();
            } catch (java.lang.NullPointerException e) {
                return "NOT_BUILT";
            }
        }
        return null;
    }

    /**
     *
     * @return The build number. 0 if there's no build. -1 if there's no job
     */
    public int getBuildNumber() {

        Iterator<Job> it = abstractProject.getAllJobs().iterator();
        if (it.hasNext()) {
            Job job = it.next();
            try {
                return job.getLastBuild().getNumber();

            } catch (java.lang.NullPointerException e) {
                return 0;
            }
        }
        return -1;
    }
    
    public String getIconUrl(){
        
        return abstractProject.getLastCompletedBuild().getBuildStatusUrl();
    }
}
