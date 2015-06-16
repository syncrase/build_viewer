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

import com.google.common.io.Files;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author I310911
 */
public class ProjectImpl {

    public static final String NO_PREFIX = "";

    private final AbstractProject abstractProject;
    private final String prefix;
    private final BuildViewer bv;
//    private ClaimWrapper claimWrapper;
//    private Map<String, String> buildStateDuration;

    public ProjectImpl(BuildViewer bv, AbstractProject abstractProject) {
        this.abstractProject = abstractProject;
        this.bv = bv;
        this.prefix = computePrefix(this.getName());
    }

    //**************************************************************************
    // Getters / setters
    //**************************************************************************
    /**
     *
     * @return
     */
    public AbstractProject getAbstractProject() {
        return abstractProject;
    }

    /**
     *
     * @return The found prefix of this job
     */
    public String getPrefix() {
        return prefix;
    }

    //**************************************************************************
    // AbstractProject handlers, easier to use in jellys
    //**************************************************************************
    public String getName() {
        return abstractProject.getName();
    }

    public String getSearchUrl() {
        return abstractProject.getSearchUrl();
    }

    public String getIconUrl() {
//        try {
        return abstractProject.getLastCompletedBuild().getBuildStatusUrl();
//        } catch (Throwable t) {
//            return "Getting the build Status throws an exception:\n" + t.getMessage();
//        }

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
        ClaimWrapper cw = getClaimWrapper();
//        String latestJenkinsResult = this.getLatestBuildResult();
        return (cw != null && (cw.isClaimed() == true)) ? "CLAIMED" : this.getLatestBuildResult();//TODO
    }

    /**
     *
     * @return The build number.
     */
    public int getBuildNumber() {//TODO latest
//        return getLatestCompletedRun().getNumber();
        Run<?, ?> run = getLatestCompletedRun();
        return run != null ? run.getNumber() : 0;
    }

    /**
     *
     * @return "SUCCESS", "UNSTABLE", "FAILURE", "NOT_BUILT", "ABORTED"<br/>
     * Default values for Jenkins
     * <p>
     */
    public String getLatestBuildResult() {
        Run<?, ?> run = getLatestCompletedRun();
        return run != null ? run.getResult().toString() : Result.NOT_BUILT.toString();
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

    //**************************************************************************
    // About CLAIM
    //**************************************************************************
    public ClaimWrapper getClaimWrapper() {
        if (ClaimWrapper.isClaimPluginAvailable()) {
            Run<?, ?> latestBuild = getLatestCompletedRun();
            if (latestBuild != null) {
                String claim = "";
                if (latestBuild instanceof hudson.matrix.MatrixBuild) {
                    //TODO
//                    MatrixBuild matrixBuild = (hudson.matrix.MatrixBuild) lastBuild;
//                    claim = buildMatrixClaimString(matrixBuild, true);
                } else {
                    return ClaimWrapper.builder(latestBuild);// TODO project
//                    return new ClaimWrapper(latestBuild);
                }
            }
        }
        return null;
    }

    //**************************************************************************
    // Computing
    //**************************************************************************
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
            }
        }
        return NO_PREFIX;
    }

//    /**
//     * Get the time of the current build and check how much time this state is
//     * <p>
//     * @param state One of the Result's states
//     * <p>
//     * @return
//     */
//    public String getBuildStateDurationBetweenRuns() {
//
//        // If the latest build is failed
//        Run<?, ?> latestRun = getLatestCompletedRun();
//        if (latestRun != null) {
//            Run<?, ?> firstRun = null;
//            Run<?, ?> previousRun = null;
//            String state = latestRun.getResult().toString();
//
//            firstRun = latestRun;
//            do {
//                firstRun = previousRun != null ? previousRun : firstRun;
//                previousRun = firstRun.getPreviousBuild();
//            } while (previousRun != null && validateRunState(previousRun, state));
//            long timeEllapse = latestRun.getTimeInMillis() - firstRun.getTimeInMillis();
//            return convertMillisecondsToEquivalentDuration(new BigDecimal(String.valueOf(timeEllapse)));
//        }
//        return null;
//    }
    /**
     * Get the time of the current build and check how much time this state is
     * <p>
     * @param state One of the Result's states
     * <p>
     * @return
     */
    public String getBuildStateDurationUpToNow() {
        Run<?, ?> latestRun = getLatestCompletedRun();
        if (latestRun != null) {
            Run<?, ?> firstRun = null;
            Run<?, ?> previousRun = null;
            String state = latestRun.getResult().toString();

//            for ( firstRun = latestRun; firstRun != null && firstRun.getResult().toString().equals(state); firstRun = firstRun.getPreviousBuild());
            firstRun = latestRun;
            do {
                firstRun = previousRun != null ? previousRun : firstRun;
                previousRun = firstRun.getPreviousBuild();
            } while (previousRun != null && previousRun.getResult().toString().equals(state));
            long timeEllapse = System.currentTimeMillis() - firstRun.getTimeInMillis();
            return convertMillisecondsToEquivalentDuration(new BigDecimal(String.valueOf(timeEllapse)));
        }
        return "";
    }

    /**
     *
     * @param timeInMilli
     *                    <p>
     * @return A map with the equivalent duration, keys are : days, hours,
     *         minutes and seconds
     */
    private String convertMillisecondsToEquivalentDuration(BigDecimal timeInMilli) {
        BigDecimal[] seconds = timeInMilli.divideAndRemainder(new BigDecimal("1000"));
        BigDecimal[] minutes = seconds[0].divideAndRemainder(new BigDecimal("60"));
        BigDecimal[] hours = minutes[0].divideAndRemainder(new BigDecimal("60"));
        BigDecimal[] days = hours[0].divideAndRemainder(new BigDecimal("24"));
        return (new StringBuilder()).append(days[0].toString()).append("d ").append(days[0].toString()).append("h ").append(days[1].toString()).append("m ").toString();
    }

    //**************************************************************************
    // Security
    //**************************************************************************
//    /**
//     *
//     * @param run   run to check
//     * @param state desired native Jenkins state to check ("SUCCESS",
//     *              "UNSTABLE", "FAILURE", "NOT_BUILT", "ABORTED")<br/>
//     * For instance state = Result.FAILURE.toString()
//     * <p>
//     * @return
//     */
//    private boolean validateRunState(Run<?, ?> run, String state) {
//        return run.getResult().toString().equals(state);
//    }
    public String test() {

        // Find test-result.xml (where is stored the defect.xml)
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
//        setTextContentInFile(true, build_Info(), "build_Info_"+getName()+".txt");
//        setTextContentInFile(true, abstractProject_Info(), "abstractProject_Info_"+getName()+".txt");
//        setTextContentInFile(true, run_Info(), "run_Info_"+getName()+".txt");
        String[] fileNameArray = {"junitResult.xml", "test-defects.xml"};

        for ( int i = 0; i < fileNameArray.length; i++ ) {
            searchForXml(new File(this.abstractProject.getBuildDir().getPath()), fileNameArray[i]);
        }

        return "";

    }

    /**
     *
     * @param root
     */
    private void searchForXml(File root, String filename) {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            for ( File file : files ) {
                searchForXml(file, filename);
//                String content = root.listFiles().toString() + "\n";
//                setTextContentInFile(false, content, filename.substring(0,filename.length()-4) + "_#_" + getName() + ".txt");
            }
        } else if (root.isFile() && root.getName().equals(filename)) {
            try {
                File f = new File(root.getPath());
                String content = Files.toString(f, Charsets.UTF_8);

                //Xpath objects initialization
                StringBuilder sb = new StringBuilder();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document document = builder.parse(new FileInputStream(new File(root.getPath())));
                XPath xPath = XPathFactory.newInstance().newXPath();

                if (filename.equals("test-defects.xml")) {
                    String expression = "/testcases/testcase[@actualStatus='failed'][@status='passed']/defect[@type='jira']";
                    String[] defectAttributes = {"expectedMessage", "link", "status", "type"};

                    NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
                    Node n;
                    if (nodeList.getLength() > 0) {
                        for ( int i = 0; i < nodeList.getLength(); i++ ) {
                            n = nodeList.item(i);
                            if (n.getNodeType() == Node.ELEMENT_NODE) {
                                Element e = (Element) n;
                                for ( String s : defectAttributes ) {
                                    sb.append(s + " : " + e.getAttribute(s) + "\n");
                                }
                                sb.append("name : " + ((Element) e.getParentNode()).getAttribute("name") + "\n");
                            }
                        }
                    }
                    sb.append(root.getPath());
                    setTextContentInFile(false, sb.toString(), filename.substring(0, filename.length() - 4) + ".ExtractedValues" + getName() + ".xml");

                } else if (filename.equals("junitResult.xml")) {

                    String expression = "/result/suites/suite/cases/case";
                    NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
                    Node n;

                    if (nodeList.getLength() > 0) {
                        for ( int i = 0; i < nodeList.getLength(); i++ ) {
                            n = nodeList.item(i);
                            if (n.getNodeType() == Node.ELEMENT_NODE) {
                                Element e = (Element) n;
                                sb.append(e.getElementsByTagName("className").item(0).getTextContent())
                                        .append(e.getElementsByTagName("testName").item(0).getTextContent());
                            }
                        }
                    } else {
                    }
                    sb.append(root.getPath());
                    setTextContentInFile(false, sb.toString(), filename.substring(0, filename.length() - 4) + ".ExtractedValues" + getName() + ".xml");

                }
            } catch (IOException ex) {
                Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XPathExpressionException ex) {
                Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void setTextContentInFile(boolean setHeader, String content, String filename) {
        // source folder --> c:\\Program files (x86)\Jenkins\
        String pathstr = "C:\\Users\\I310911\\jenkinsNetBeans\\" + filename;
        // Créer et écrire dans un fichier
        BufferedWriter out;
        try {
            File f = new File(pathstr);
            f.createNewFile();
            out = new BufferedWriter(new FileWriter(pathstr, true));
            if (setHeader == true) {
                out.write("*********************** :-)  " + new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz").format(new Date())
                          + "  (-: ***********************\n");
            }
            out.write(content + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public String getWorkspaceURL() {
        return "";
    }
//    private String buildMatrixClaimString(MatrixBuild matrixBuild, boolean includeClaimed) {
//        StringBuilder claimed = new StringBuilder();
//        StringBuilder unclaimed = new StringBuilder();
//        for ( MatrixRun combination : matrixBuild.getRuns() ) {
//            if (matrixBuild.getNumber() != combination.getNumber()) {
//                continue;
//            }
//            Result result = combination.getResult();
//            if (!(Result.FAILURE.equals(result) || Result.UNSTABLE.equals(result))) {
//                continue;
//            }
//            ClaimWrapper claimWrapper = ClaimWrapper.builder(combination);
//            if (claimWrapper != null && claimWrapper.isClaimed()) {
//                claimed.append(combination.getParent().getCombination()
//                        .toString());
//                claimed.append(": ");
//                if (claimWrapper.getReason() != null) {
//                    claimed.append(claimWrapper.getReason()).append(" ");
//                }
//                claimed.append("(");
//                claimed.append(claimWrapper.getClaimedByName());
//                claimed.append(").<br/>");
//            } else {
//                unclaimed.append(combination.getParent().getCombination().toString());
//                unclaimed.append(": ").append("NOT_CLAIMED").append("<br/>");
//            }
//        }
//
//        String claims = unclaimed.toString();
//        if (includeClaimed) {
//            claims += claimed.toString();
//        }
//        return claims;
//    }

//    public String abstractProject_Info() {
//        String s = "";
//        s += "getAbsoluteUrl : " + abstractProject.getAbsoluteUrl();
//        s += "\ngetAssignedLabelString : " + abstractProject.getAssignedLabelString();
//        s += "\ngetBuildNowText : " + abstractProject.getBuildNowText();
//        s += "\ngetBuildStatusUrl : " + abstractProject.getBuildStatusUrl();
//        s += "\ngetCustomWorkspace : " + abstractProject.getCustomWorkspace();
//        s += "\ngetDescription : " + abstractProject.getDescription();
//        s += "\ngetDisplayName : " + abstractProject.getDisplayName();
//        s += "\ngetDisplayNameOrNull : " + abstractProject.getDisplayNameOrNull();
//        s += "\ngetFullDisplayName : " + abstractProject.getFullDisplayName();
//        s += "\ngetFullName : " + abstractProject.getFullName();
//        s += "\ngetName : " + abstractProject.getName();
//        s += "\ngetPronoun : " + abstractProject.getPronoun();
//        s += "\ngetSearchName : " + abstractProject.getSearchName();
//        s += "\ngetSearchUrl : " + abstractProject.getSearchUrl();
//        s += "\ngetShortUrl : " + abstractProject.getShortUrl();
//        s += "\ngetUrl : " + abstractProject.getUrl();
//        s += "\ngetWhyBlocked : " + abstractProject.getWhyBlocked();
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
}
