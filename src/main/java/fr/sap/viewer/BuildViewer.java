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

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
import hudson.model.ListView;
import hudson.model.Result;
import hudson.model.TopLevelItem;
import hudson.model.ViewDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author I310911
 */
public class BuildViewer extends ListView {

    private static final int DEFAULT_CAPTION_SIZE = 36;
    public static final String DEFAULT_STATE_NAME = "STATE_NAME";
    public static final String DEFAULT_VE_BACKGROUND_COLOR = "#FFFFFF";
    public static final String DEFAULT_VE_FONT_COLOR = "#FFFFFF";
    public static final String DEFAULT_CAPTION_FONT_COLOR = "#000000";
    public static final String DEFAULT_CAPTION_BACKGROUND_COLOR = "#FFFFFF";
    public static final String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";

    private String backgroundColor;

    private LinkedHashSet<ViewEntryColor> viewEntryColors;//TODO
    private HashSet<String> prefixesSeparators;
    private ArrayList<XmlExceptionItem> xmlExceptionList;

    private String captionText;
    private int captionSize;
    private String captionColor;
    private String captionTextColor;

    @DataBoundConstructor
    public BuildViewer(String name, String backgroundColor, String captionColor, String captionTextColor,
                       LinkedHashSet<ViewEntryColor> viewEntryColors, String captionText, int captionSize, HashSet<String> prefixesSeparators, ArrayList<XmlExceptionItem> xmlExceptionList) {
        super(name);
        this.backgroundColor = backgroundColor;
        this.captionColor = captionColor;
        this.captionTextColor = captionTextColor;
        this.viewEntryColors = viewEntryColors;
        this.captionText = captionText;
        this.captionSize = captionSize;
//        if(prefixesSeparators == null){
//            this.prefixesSeparators = new HashSet<String>();
//        }
        this.prefixesSeparators = prefixesSeparators;
        this.xmlExceptionList = xmlExceptionList;

    }

    public LinkedHashSet<ViewEntryColor> getViewEntryColors() {//TODO
//        if (viewEntryColors == null) {
//            initializeColorSettings();
//        }
        return viewEntryColors;
    }

    public void setDefaultColorSettings(boolean b) {
        if (b == true) {
            viewEntryColors = new LinkedHashSet<ViewEntryColor>();
            viewEntryColors.add(new ViewEntryColor(Result.SUCCESS.toString(), "#32BA00", "#000000"));//green
            viewEntryColors.add(new ViewEntryColor(Result.UNSTABLE.toString(), "#FFFF00", "#000000"));//yellow
            viewEntryColors.add(new ViewEntryColor(Result.FAILURE.toString(), "#BA0000", "#000000"));//red
            viewEntryColors.add(new ViewEntryColor(Result.NOT_BUILT.toString(), "#737373", "#000000"));//grey
            viewEntryColors.add(new ViewEntryColor(Result.ABORTED.toString(), "#000000", "#000000"));//dark
            viewEntryColors.add(new ViewEntryColor("CLAIMED", "#F2B32C", "#000000"));//dark
        }

    }

    public HashSet<String> getPrefixesSeparators() {
        return prefixesSeparators;
    }

    public ArrayList<XmlExceptionItem> getXmlExceptionList() {
        return xmlExceptionList;
    }

    public String getCaptionText() {
        return captionText;
    }

    public int getCaptionSize() {
        return captionSize;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getCaptionColor() {
        return captionColor;
    }

    public String getCaptionTextColor() {
        return captionTextColor;
    }

    public List<ProjectWrapper> getContents() {
        List<ProjectWrapper> contents;
        contents = new ArrayList<ProjectWrapper>();
        ProjectWrapper project;
        for ( TopLevelItem item : super.getItems() ) {
            if (item instanceof AbstractProject) {
                project = new ProjectWrapper(this, (AbstractProject) item);
                if (!project.getAbstractProject().isDisabled()) {
                    contents.add(project);
                }
            }
        }
        return contents;
    }

    public Dashboard getDashboard() {
        Dashboard dashboard;
        List<ProjectWrapper> contents = getContents();
        dashboard = new Dashboard(this, contents);
        return dashboard;
    }

    /**
     * Method called when the user click on submit
     * <p>
     * @param req
     *            <p>
     * @throws ServletException
     * @throws IOException
     * @throws hudson.model.Descriptor.FormException
     */
    @Override
    protected void submit(StaplerRequest req) throws ServletException, IOException, FormException {

        super.submit(req);

        // Caption parameters
        this.captionText = req.getParameter("captionText");
        this.captionColor = req.getParameter("captionColor");
        this.captionTextColor = req.getParameter("captionTextColor");
        try {
            this.captionSize = Integer.parseInt(req.getParameter("captionSize"));
        } catch (NumberFormatException e) {
            this.captionSize = DEFAULT_CAPTION_SIZE;
        }

        // Background color parameter
        this.backgroundColor = req.getParameter("backgroundColor");

        // States configuration
        String[] ves_states = req.getParameterValues("ve_state");
        String[] ves_backgroundColors = req.getParameterValues("ve_backgroundColor");
        String[] ves_fontColors = req.getParameterValues("ve_fontColor");

        viewEntryColors = new LinkedHashSet<ViewEntryColor>();
        for ( int i = 0; i < ves_states.length; i++ ) {
            if (!ves_states[i].equals(DEFAULT_STATE_NAME)) {
                viewEntryColors.add(new ViewEntryColor(ves_states[i].toUpperCase(), ves_backgroundColors[i], ves_fontColors[i]));
            }
        }

        // Desired prefix
        String[] separators = req.getParameter("prefixesSeparators").split(" ");
        if (separators.length >= 1) {
            prefixesSeparators = new HashSet<String>();
            for ( String s : separators ) {
                if (!s.equals("")) {
                    prefixesSeparators.add(s);
                }
            }
        }

        Enumeration en = req.getParameterNames();

        // xml exceptions
        String[] xmlex_filenames = req.getParameterValues("xmlex_filename");
        String[] xmlex_expressions = req.getParameterValues("xmlex_expression");
//        String[] xmlex_colors = req.getParameterValues("xmlex_color");

        String[] xmlex_returnedValues;
        String[] xmlex_statuses;
        String[] xmlex_backgroundColors;
        String[] xmlex_fontColors;

        xmlExceptionList = new ArrayList<XmlExceptionItem>();
        XmlExceptionItem xmlExceptionItem;
        HashMap<String, ViewEntryColor> propertiesMap;
        String suffix;
        for ( int i = 0; i < xmlex_filenames.length; i++ ) {
            xmlExceptionItem = new XmlExceptionItem(xmlex_expressions[i], xmlex_filenames[i], new HashMap<String, ViewEntryColor>());
            if (!contains(xmlExceptionList, xmlExceptionItem)) {//resticts doubled
                suffix = "#" + xmlExceptionItem.getEscapedFilename() + "#" + xmlExceptionItem.getEscapedExpression();
                xmlex_returnedValues = req.getParameterValues("xmlex_returnedValue"+suffix);
                xmlex_statuses = req.getParameterValues("xmlex_status"+suffix);
                xmlex_backgroundColors = req.getParameterValues("xmlex_backgroundColor"+suffix);
                xmlex_fontColors = req.getParameterValues("xmlex_fontColor"+suffix);

                // first step of the table completion provide no one of variables
                if (xmlex_returnedValues != null && xmlex_statuses != null && xmlex_backgroundColors != null && xmlex_fontColors != null) {
                    propertiesMap = new HashMap<String, ViewEntryColor>();

                    for ( int j = 0; j < xmlex_returnedValues.length; j++ ) {
                        propertiesMap.put(xmlex_returnedValues[j], new ViewEntryColor(xmlex_statuses[j], xmlex_backgroundColors[j], xmlex_fontColors[j]));
                    }
                    xmlExceptionItem.setStatusPerReturnedValue(propertiesMap);
                }

                xmlExceptionList.add(xmlExceptionItem);
            } else {
                // si doublon => vérifier si les xml exception sont les mêmes sinon ajouter au champ déjà existant les exceptions de la seconde
            }
        }

        // TODO add new xml status to the list of status
//        String test;
//        String[] strarr;
//        while (en.hasMoreElements()) {
//            test = (String) en.nextElement();
//            strarr = req.getParameterValues(test);
//            strarr[0] = strarr[0];
//        }
    }

    private boolean contains(ArrayList<XmlExceptionItem> xmlExceptionList, XmlExceptionItem xmlExceptionItem) {
        for ( XmlExceptionItem x : xmlExceptionList ) {
            if (x.getExpression().equals(xmlExceptionItem.getExpression()) && x.getFilename().equals(xmlExceptionItem.getFilename())) {
                return true;
            }
        }
        return false;
    }
    /*
    
    
    
    
    
    
    
    
    
    
     ****************************************************************************
    
    
    
     DESCRIPTOR
    
    
    
     ****************************************************************************
    
    
    
    
    
    
    
    
    
    
    
     */

    /**
     *
     */
    @Extension
    public static final class DescriptorImpl extends ViewDescriptor {

        public DescriptorImpl() {
            super(BuildViewer.class);
        }

        /**
         * If the security is not enabled, there's no point in having this type
         * of views.
         */
        //@Override
        //public boolean isInstantiable() {
        //    return Jenkins.getInstance().isUseSecurity();
        //}
        /**
         *
         * @return The name displayed on the new view page. The description
         *         associated is in files newViewDetails[.jelly ||
         *         _fr.properties]
         */
        @Override
        public String getDisplayName() {
            return "SAP Radiator";
        }

        /**
         *
         * @param sr
         * @param jsono
         *              <p>
         * @return
         */
        @Override
        public boolean configure(StaplerRequest sr, JSONObject jsono) throws FormException {
            sr.bindJSON(this, jsono);
//            String a = "d";
//            a+="b";
//            int i = jsono.optInt("dashboardWidth");
            // serializing
            save();
            return super.configure(sr, jsono);
        }
//
//        @Override
//        public List<Descriptor<ViewJobFilter>> getJobFiltersDescriptors() {
//            String a = "d";
//            a+="b";
//            return super.getJobFiltersDescriptors(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public List<Descriptor<ListViewColumn>> getColumnsDescriptors() {
//            String a = "d";
//            a+="b";
//            return super.getColumnsDescriptors(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public AutoCompletionCandidates doAutoCompleteCopyNewItemFrom(String string) {
//            String a = "d";
//            a+="b";
//            return super.doAutoCompleteCopyNewItemFrom(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public boolean isInstantiable() {
//            String a = "d";
//            a+="b";
//            return super.isInstantiable(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public void doHelp(StaplerRequest sr, StaplerResponse sr1) throws IOException, ServletException {
//            String a = "d";
//            a+="b";
//            super.doHelp(sr, sr1); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected PluginWrapper getPlugin() {
//            String a = "d";
//            a+="b";
//            return super.getPlugin(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected XmlFile getConfigFile() {
//            String a = "d";
//            a+="b";
//            return super.getConfigFile(); //To change body of generated methods, choose Tools | Templates.
//        }
//

        @Override
        public synchronized void load() {
//            String a = "d";
//            a+="b";
            super.load(); //To change body of generated methods, choose Tools | Templates.
        }
//
//        @Override
//        public synchronized void save() {
//            String a = "d";
//            a+="b";
//            super.save(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected List<String> getPossibleViewNames(String string) {
//            String a = "d";
//            a+="b";
//            return super.getPossibleViewNames(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String getGlobalConfigPage() {
//            String a = "d";
//            a+="b";
//            return super.getGlobalConfigPage(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String getConfigPage() {
//            String a = "d";
//            a+="b";
//            return super.getConfigPage(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public boolean configure(StaplerRequest sr) throws FormException {
//            String a = "d";
//            a+="b";
//            return super.configure(sr); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        protected void addHelpFileRedirect(String string, Class<? extends Describable> type, String string1) {
//            String a = "d";
//            a+="b";
//            super.addHelpFileRedirect(string, type, string1); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * Fourth method called before access to the configuration page
//         * when access to viewer 5 
//         * @param klass
//         * @param string
//         * @return 
//         */
//        @Override
//        public String getHelpFile(Klass<?> klass, String string) {
//            String a = "d";
//            a+="b";
//            return super.getHelpFile(klass, string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * Second method ,called before access to the configure page
//         * when access to viewer 3rd
//         * @param string
//         * @return 
//         */
//        @Override
//        public String getHelpFile(String string) {
//            String a = "d";
//            a+="b";
//            return super.getHelpFile(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String getHelpFile() {
//            String a = "d";
//            a+="b";
//            return super.getHelpFile(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * Third method called before access to the configure page
//         * when access to viewer : 4th
//         * @return 
//         */
//        @Override
//        public Klass<?> getKlass() {
//            String a = "d";
//            a+="b";
//            return super.getKlass(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public View newInstance(StaplerRequest sr, JSONObject jsono) throws FormException {
//            String a = "d";
//            a+="b";
//            return super.newInstance(sr, jsono); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public View newInstance(StaplerRequest sr) throws FormException {
//            String a = "d";
//            a+="b";
//            return super.newInstance(sr); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public PropertyType getGlobalPropertyType(String string) {
//            String a = "d";
//            a+="b";
//            return super.getGlobalPropertyType(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public PropertyType getPropertyType(String string) {
//            String a = "d";
//            a+="b";
//            return super.getPropertyType(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public PropertyType getPropertyTypeOrDie(Object o, String string) {
//            String a = "d";
//            a+="b";
//            return super.getPropertyTypeOrDie(o, string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public PropertyType getPropertyType(Object o, String string) {
//            String a = "d";
//            a+="b";
//            return super.getPropertyType(o, string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * Seventh method called before access to the configure page ( and tenth, 12, 14, 16
//         * When access to viewer : 1st, 8, 10, 12, 14, 16
//         * @param string
//         * @param map 
//         */
//        @Override
//        public void calcAutoCompleteSettings(String string, Map<String, Object> map) {
//            String a = "d";
//            a+="b";
//            super.calcAutoCompleteSettings(string, map); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public void calcFillSettings(String string, Map<String, Object> map) {
//            String a = "d";
//            a+="b";
//            super.calcFillSettings(string, map); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * First method called before access to the configure page (and sixth and nine and 11, 13, 15, 17)
//         * when access to viewer 2nd, 7, 9, 11, 13, 15, 17
//         * @param string
//         * @return 
//         */
//        @Override
//        public FormValidation.CheckMethod getCheckMethod(String string) {
//            String a = "d";
//            a+="b";
//            return super.getCheckMethod(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String getCheckUrl(String string) {
//            String a = "d";
//            a+="b";
//            return super.getCheckUrl(string); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String getDescriptorUrl() {
//            String a = "d";
//            a+="b";
//            return super.getDescriptorUrl(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * First method called, even before the starting of Jenkins
//         * Eight method called before access to the configure page ( and 18
//         * @return 
//         */
//        @Override
//        public Class<View> getT() {
//            String a = "d";
//            a+="b";
//            return super.getT(); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        /**
//         * method called before starting Jenkins
//         * Fifth method called before access to the configuration page
//         * viewer access 6
//         * @return 
//         */
//        @Override
//        public String getId() {
//            String a = "d";
//            a+="b";
//            return super.getId(); //To change body of generated methods, choose Tools | Templates.
//        }
    }
}
