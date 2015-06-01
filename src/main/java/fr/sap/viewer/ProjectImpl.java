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

import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixRun;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Result;
import hudson.model.Run;
import java.util.Date;
import java.util.HashSet;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author I310911
 */
public class ProjectImpl {

    /**
     * This value is used when there's no prefix set up
     */
    public static final String NO_PREFIX_AVAILABLE = "NO_PREFIX_AVAILABLE";

    private final AbstractProject abstractProject;
    private final String prefixe;
    private BuildViewer bv;
    private ClaimWrapper claimWrapper;

    public ProjectImpl(BuildViewer bv, AbstractProject abstractProject) {
        this.abstractProject = abstractProject;
        this.bv = bv;
        this.prefixe = computePrefix(this.getName());
    }

    /**
     *
     * @return
     */
    public AbstractProject getAbstractProject() {
        return abstractProject;
    }

    public String getName() {
        return abstractProject.getName();
    }

    public String getLastBuildUrl() {
        return abstractProject.getSearchUrl();
    }

    /**
     *
     * @return "SUCCESS", "UNSTABLE", "FAILURE", "NOT_BUILT", "ABORTED"<br/>
     * Default values for Jenkins
     * <p>
     */
    public String getLatestBuildResult() {

        return getLatestCompletedRun().getResult().toString();

    }

    /**
     * Return the state of the project (Jenkins, claim, TODO...)
     * <p>
     * @return
     */
    public String getResult() {
        //Parcourir la liste !?
        // Si j'ajoute des config de couleur c'est ici que je renvoie la bonne clé
        //  1   result job
        //  2   if claimed
        //  3   unknown
        return this.isClaimed() ? "CLAIMED" : this.getLatestBuildResult();
    }

    /**
     *
     * @return The build number.
     */
    public int getBuildNumber() {
        return getLatestCompletedRun().getNumber();
    }

    public String getIconUrl() {
        try {
            return abstractProject.getLastCompletedBuild().getBuildStatusUrl();
        } catch (Throwable t) {
            return "Getting the build Status throws an exception:\n" + t.getMessage();
        }

    }

    /**
     *
     * @return The found prefix of this job
     */
    public String getPrefix() {
        return prefixe;
    }

    /**
     *
     * @param name The name of the job
     * <p>
     * @return the prefix use for this job<br/>null if no prefix has been found
     *         in the project name
     */
    private String computePrefix(String name) {
        HashSet<String> prefixes = bv.getPrefixesSeparators();
        if (prefixes != null) {
            if (prefixes.size() > 0) {
                for ( String prefix : prefixes ) {
                    if (name.contains(prefix)) {
                        return StringUtils.substringBefore(name, prefix);
                    }
                }
                return null;
            } else {
                return null;
            }
        }
        return null;
    }

    public String getClaim() {
        // check we have claim plugin
        if (Hudson.getInstance().getPlugin("claim") == null) {
            return null;
        }
        Run<?, ?> lastBuild = getLatestCompletedRun();
        if (lastBuild == null) {
            return null;
        }
        // find the claim
        String claim = "";
        if (lastBuild instanceof hudson.matrix.MatrixBuild) {
            MatrixBuild matrixBuild = (hudson.matrix.MatrixBuild) lastBuild;
            claim = buildMatrixClaimString(matrixBuild, true);
        } else {
            claimWrapper = ClaimWrapper.builder(lastBuild);
            if (claimWrapper != null && claimWrapper.isClaimed()) {
                StringBuilder sb = new StringBuilder();
                if (claimWrapper.getReason() != null) {
                    sb.append(claimWrapper.getReason()).append(" ");
                }
                sb.append("(");
                sb.append(claimWrapper.getClaimedByName());
                sb.append(").");
                claim = sb.toString();
            } else {
                claim = "NOT_CLAIMED";
            }
        }
        return claim;
    }

    private Run<?, ?> getLatestCompletedRun() {
        Run<?, ?> run = abstractProject.getLastBuild();
        while (run != null && run.isBuilding()) {
            // claims can only be made against builds once they've finished,
            // so check the previous build if currently building.
            run = run.getPreviousBuild();
        }
        return run;
    }

    private String buildMatrixClaimString(MatrixBuild matrixBuild, boolean includeClaimed) {
        StringBuilder claimed = new StringBuilder();
        StringBuilder unclaimed = new StringBuilder();
        for ( MatrixRun combination : matrixBuild.getRuns() ) {
            if (matrixBuild.getNumber() != combination.getNumber()) {
                continue;
            }
            Result result = combination.getResult();
            if (!(Result.FAILURE.equals(result) || Result.UNSTABLE.equals(result))) {
                continue;
            }
            ClaimWrapper claimWrapper = ClaimWrapper.builder(combination);
            if (claimWrapper != null && claimWrapper.isClaimed()) {
                claimed.append(combination.getParent().getCombination()
                        .toString());
                claimed.append(": ");
                if (claimWrapper.getReason() != null) {
                    claimed.append(claimWrapper.getReason()).append(" ");
                }
                claimed.append("(");
                claimed.append(claimWrapper.getClaimedByName());
                claimed.append(").<br/>");
            } else {
                unclaimed.append(combination.getParent().getCombination().toString());
                unclaimed.append(": ").append("NOT_CLAIMED").append("<br/>");
            }
        }

        String claims = unclaimed.toString();
        if (includeClaimed) {
            claims += claimed.toString();
        }
        return claims;
    }

    public boolean isClaimed() {
        if (Hudson.getInstance().getPlugin("claim") == null) {
            return false;
        }
        Run<?, ?> lastBuild = getLatestCompletedRun();
        if (lastBuild == null) {
            return false;
        }
        if (!(lastBuild instanceof hudson.matrix.MatrixBuild)) {
            // TODO handle claimMatrix
            claimWrapper = ClaimWrapper.builder(lastBuild);

        }
        return claimWrapper != null ? claimWrapper.isClaimed() : false;
//        return !"NOT_CLAIMED".equals(getClaim());
    }

    /**
     * How long is the project claimed?
     * <p>
     * @return The duration of the claim
     */
    public String getClaimDuration() {

        //TODO
        if (isClaimed()) {

            return "";
        }
        return "";
    }

    public String getFailDuration() {

        // If the latest build is failed
        Run<?, ?> latestRun = getLatestCompletedRun();
        Run<?, ?> firstFailedRun = null;
        Run<?, ?> previousRun = null;

        if (validateRunState(latestRun, Result.FAILURE.toString())) {
            firstFailedRun = latestRun;
            do {
                firstFailedRun = previousRun != null ? previousRun : firstFailedRun;
                previousRun = firstFailedRun.getPreviousBuild();
            } while (previousRun != null && validateRunState(previousRun, Result.FAILURE.toString()));
//            firstFailedRun = firstFailedRun.getNextBuild();
            long timeEllapse=latestRun.getTimeInMillis() - firstFailedRun.getTimeInMillis();
            return " duration : "+firstFailedRun.getDurationString() +
                   " <br/>estimate duration : " + firstFailedRun.getEstimatedDuration() + 
                   " <br/>time : " + firstFailedRun.getTime() + 
                   " <br/>time in milli : "+firstFailedRun.getTimeInMillis() + 
                   " <br/>time stamp"+firstFailedRun.getTimestamp().toString()+
                   " \n#### datetoString : "+(new Date(firstFailedRun.getTimeInMillis()*1000)).toString()+
                   " \n#### date: "+(new Date(firstFailedRun.getTimeInMillis()*1000)).getYear();
//            errorDuration = latestBuildTime - firstCrashedBuildTime
//            return "";
        }
        return null;
    }

    /**
     *
     * @param run   run to check
     * @param state desired native Jenkins state to check ("SUCCESS",
     *              "UNSTABLE", "FAILURE", "NOT_BUILT", "ABORTED")<br/>
     * For instance state = Result.FAILURE.toString()
     * <p>
     * @return
     */
    public boolean validateRunState(Run<?, ?> run, String state) {

        return run.getResult().toString().equals(state);

    }
    
    
    private int[] convertMilliToDate(long timeInMilli){
        long seconds = timeInMilli/1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return new int[]{1,5,9};
    }

    
//    public String abstractProject_Info() {
//        String s = "";
//        s += "getAbsoluteUrl : " + abstractProject.getAbsoluteUrl();
//        s += "<br\\>getAssignedLabelString : " + abstractProject.getAssignedLabelString();
//        s += "<br\\>getBuildNowText : " + abstractProject.getBuildNowText();
//        s += "<br\\>getBuildStatusUrl : " + abstractProject.getBuildStatusUrl();
//        s += "<br\\>getCustomWorkspace : " + abstractProject.getCustomWorkspace();
//        s += "<br\\>getDescription : " + abstractProject.getDescription();
//        s += "<br\\>getDisplayName : " + abstractProject.getDisplayName();
//        s += "<br\\>getDisplayNameOrNull : " + abstractProject.getDisplayNameOrNull();
//        s += "<br\\>getFullDisplayName : " + abstractProject.getFullDisplayName();
//        s += "<br\\>getFullName : " + abstractProject.getFullName();
//        s += "<br\\>getName : " + abstractProject.getName();
//        s += "<br\\>getPronoun : " + abstractProject.getPronoun();
//        s += "<br\\>getSearchName : " + abstractProject.getSearchName();
//        s += "<br\\>getSearchUrl : " + abstractProject.getSearchUrl();
//        s += "<br\\>getShortUrl : " + abstractProject.getShortUrl();
//        s += "<br\\>getUrl : " + abstractProject.getUrl();
//        s += "<br\\>getWhyBlocked : " + abstractProject.getWhyBlocked();
//
//        return s;
//    }
//
//    public String run_Info() {
//        String s = "";
//        Run r = this.abstractProject.getLastCompletedBuild();
//
//        s += "getAbsoluteUrl : " + r.getAbsoluteUrl() + "\n";
//        s += "getBuildStatusUrl : " + r.getBuildStatusUrl() + "\n";
//        s += "getDescription : " + r.getDescription() + "\n";
//        s += "getDisplayName : " + r.getDisplayName() + "\n";
//        s += "getDurationString : " + r.getDurationString() + "\n";
//        s += "getExternalizableId : " + r.getExternalizableId() + "\n";
//        s += "getFullDisplayName : " + r.getFullDisplayName() + "\n";
//        s += "getId : " + r.getId() + "\n";
//        try {
//            s += "getLog : " + r.getLog() + "\n";
//        } catch (IOException ex) {
//            Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        s += "getSearchName : " + r.getSearchName() + "\n";
//        s += "getSearchUrl : " + r.getSearchUrl() + "\n";
//        s += "getTimestampString : " + r.getTimestampString() + "\n";
//        s += "getTimestampString2 : " + r.getTimestampString2() + "\n";
//        s += "getTruncatedDescription : " + r.getTruncatedDescription() + "\n";
//        s += "getUrl : " + r.getUrl() + "\n";
//        s += "getWhyKeepLog : " + r.getWhyKeepLog() + "\n";
//
//        return s;
//    }
//
//    public String build_Info() {
//        String s = "";
//        File f = this.abstractProject.getBuildDir();
//
//        s += "getAbsolutePath : " + f.getAbsolutePath() + "\n";
//        try {
//            s += "getCanonicalPath : " + f.getCanonicalPath() + "\n";
//        } catch (IOException ex) {
//            Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        s += "getName : " + f.getName() + "\n";
//        s += "getParent : " + f.getParent() + "\n";
//        s += "getPath : " + f.getPath() + "\n";
//
//        return s;
//    }
//
//    public String test() {
//        //String s = "";
//
//        String path = "C:\\Users\\I310911\\perforce\\testWRKSPC\\testPlugin\\appexample5\\work\\jobs\\mavenTest\\" + "modules\\utilitaires$mavenTest.artifactid\\builds\\";
//        File f = new File(path);
//        //s=f.list().toString();
//
//        //get folder item
//        String[] tab = f.list();
//        String content = "";
//        for ( String tab1 : tab ) {
//            //s += "1: " + tab[i] + "   _|_   ";
//            f = new File(path + tab1);
//            if (f.isDirectory()) {
//                // Get xml
//                f = new File(path + tab1 + "\\junitResult.xml");
//                try {
//                    content = Files.toString(f, Charsets.UTF_8);
//                } catch (IOException ex) {
//                    Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                break;
//            }
//        }
//        if (!"".equals(content)) {
//            this.setTextContentInFile(true, content, "monFichierXml.xml");
//            return content;
//        }
//        return "Fail to access file";
//
//    }
//
//    public void setTextContentInFile(boolean setHeader, String content, String filename) {
//        // source folder --> c:\\Program files (x86)\Jenkins\
//        String pathstr = "C:\\Users\\I310911\\jenkinsNetBeans\\" + filename;
//        // Créer et écrire dans un fichier
//        BufferedWriter out;
//        try {
//            File f = new File(pathstr);
//            f.createNewFile();
//            out = new BufferedWriter(new FileWriter(pathstr, false));
//            if (setHeader == true) {
//                out.write("*********************** :-)  " + new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz").format(new Date())
//                          + "  (-: ***********************\n");
//            }
//            out.write(new SimpleDateFormat("hh:mm:ss a zzz").format(new Date()) + content + "\n");
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    
}
