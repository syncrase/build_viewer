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
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
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
    private String captionColor;
    private String captionTextColor;

    private LinkedHashSet<ViewEntryColors> COLOR_SETTINGS;
    private HashSet<String> prefixesSeparators;

    private boolean groupedByPrefix = false;
    private String captionText;

    private int captionSize;

    @DataBoundConstructor
    public BuildViewer(String name, String backgroundColor, String captionColor, String captionTextColor,
                       LinkedHashSet<ViewEntryColors> COLOR_SETTINGS, String captionText, int captionSize, HashSet<String> prefixesSeparators) {
        super(name);
        this.backgroundColor = backgroundColor;
        this.captionColor = captionColor;
        this.captionTextColor = captionTextColor;
        this.COLOR_SETTINGS = COLOR_SETTINGS;
        this.captionText = captionText;
        this.captionSize = captionSize;
//        if(prefixesSeparators == null){
//            this.prefixesSeparators = new HashSet<String>();
//        }
        this.prefixesSeparators = prefixesSeparators;

    }

    public LinkedHashSet<ViewEntryColors> getCOLOR_SETTINGS() {
//        if (COLOR_SETTINGS == null) {
//            initializeColorSettings();
//        }
        return COLOR_SETTINGS;
    }

    public void setDefaultColorSettings(boolean b) {
        if (b == true) {
            COLOR_SETTINGS = new LinkedHashSet<ViewEntryColors>();
            COLOR_SETTINGS.add(new ViewEntryColors(Result.SUCCESS.toString(), "#2BFF00", "#000000"));//green
            COLOR_SETTINGS.add(new ViewEntryColors(Result.UNSTABLE.toString(), "#FFFF00", "#000000"));//yellow
            COLOR_SETTINGS.add(new ViewEntryColors(Result.FAILURE.toString(), "#FF0000", "#000000"));//red
            COLOR_SETTINGS.add(new ViewEntryColors(Result.NOT_BUILT.toString(), "#737373", "#000000"));//grey
            COLOR_SETTINGS.add(new ViewEntryColors(Result.ABORTED.toString(), "#000000", "#000000"));//dark
        }

    }

    public HashSet<String> getPrefixesSeparators() {
        return prefixesSeparators;
    }

    public String getCaptionText() {
        return captionText;
    }

    public int getCaptionSize() {
        return captionSize;
    }

    public static int getDEFAULT_SCREEN_HEIGHT() {
        return Toolkit.getDefaultToolkit().getScreenSize().height;
    }

    public static int getDEFAULT_SCREEN_WIDTH() {
        return Toolkit.getDefaultToolkit().getScreenSize().width;
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

    public List<ProjectImpl> getContents() {
        List<ProjectImpl> contents;
        contents = new ArrayList<ProjectImpl>();

//        Map<hudson.model.Queue.Item, Integer> placeInQueue = new HashMap<hudson.model.Queue.Item, Integer>();
//        int j = 1;
//        for ( hudson.model.Queue.Item i : Hudson.getInstance().getQueue().getItems() ) {
//            placeInQueue.put(i, j++);
//        }
//super.
        ProjectImpl project;
        for ( TopLevelItem item : super.getItems() ) {

            if (item instanceof AbstractProject) {
                //AbstractProject project = (AbstractProject) item;

                project = new ProjectImpl(this, (AbstractProject) item);
                if (!project.getAbstractProject().isDisabled()) {
////                    IViewEntry entry = new JobViewEntry(this, project);
////                    contents.addBuild(entry);
                    contents.add(project);
                }
            }
        }

        return contents;
    }

    public Dashboard getDashboard() {
        Dashboard dashboard;
        List<ProjectImpl> contents = getContents();
//
//        dashboard = new Dashboard(DashboardUtils.computeTheBestSquareRepartition(contents.size()));
        dashboard = new Dashboard(this, contents);
//        , getDEFAULT_SCREEN_HEIGHT() - this.captionSize, getDEFAULT_SCREEN_WIDTH()
        if (!groupedByPrefix) {

        } else {

        }

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

        COLOR_SETTINGS = new LinkedHashSet<ViewEntryColors>();
        for ( int i = 0; i < ves_states.length; i++ ) {
            if (!ves_states[i].equals(DEFAULT_STATE_NAME)) {
                COLOR_SETTINGS.add(new ViewEntryColors(ves_states[i].toUpperCase(), ves_backgroundColors[i], ves_fontColors[i]));
            }
        }

        // Desired prefixs
        String[] separatos = req.getParameter("prefixesSeparators").split(" ");
        if (separatos.length >= 1) {
            prefixesSeparators = new HashSet<String>();
            for ( String s : separatos ) {
                if (!s.equals("")) {
                    prefixesSeparators.add(s);
                }
            }
        }

        //        Enumeration en = req.getParameterNames();
//        String test;
//        String[] strarr;
//        while (en.hasMoreElements()) {
//            test = (String) en.nextElement();
//            strarr = req.getParameterValues(test);
//            strarr[0] = strarr[0];
//        }
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
            return "Displayed name of my view";
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
