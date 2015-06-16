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

import hudson.model.Hudson;
import hudson.model.Run;
import hudson.plugins.claim.ClaimBuildAction;
import java.util.List;

/**
 *
 * @author I310911
 */
public class ClaimWrapper {

    private ClaimBuildAction claimBuildAction;

    private ClaimWrapper(ClaimBuildAction claimBuildAction) {
        this.claimBuildAction = claimBuildAction;
        
        
                
//         ClaimBuildAction claimAction = null;
//        // Get all ClaimBuildAction actions
//        List<ClaimBuildAction> claimActionList = run
//                .getActions(ClaimBuildAction.class);
//        if (claimActionList.size() == 1) {
//            claimAction = claimActionList.get(0);
//        } else if (claimActionList.size() > 1) {
////            Log.warn("Multiple ClaimBuildActions found for job ");
//        }
//        if (claimAction == null) {
//            
//        }
//        
//        
//        this.claimBuildAction = claimAction;
    }

    /**
     * Returns ClaimWrapper containing the claim for the specified run
     * if there is a single ClaimBuildAction for the run. Returns null
     * otherwise.
     *
     * @param run
     *            <p>
     * @return null if no single ClaimBuildAction for the run param.
     */
    static public ClaimWrapper builder(Run<?, ?> run) {
        ClaimBuildAction claimAction = null;
        // Get all ClaimBuildAction actions
        List<ClaimBuildAction> claimActionList = run
                .getActions(ClaimBuildAction.class);
        if (claimActionList.size() == 1) {
            claimAction = claimActionList.get(0);
        } else if (claimActionList.size() > 1) {
//            Log.warn("Multiple ClaimBuildActions found for job ");
        }
        if (claimAction == null) {
            return null;
        }
        return new ClaimWrapper(claimAction);
    }

    public String getClaimedByName() {
        return claimBuildAction.getClaimedByName();
    }

    public boolean isClaimed() {
        return claimBuildAction.isClaimed();
    }

    public String getReason() {
        return claimBuildAction.getReason();
    }

    public String getDate() {
        return claimBuildAction.getClaimDate().toString();
    }

    public String getVersion() {
        return Hudson.getInstance().getPlugin("claim").getWrapper().getVersion();
    }
    
    /**
     *
     * @return true if the plugin is available in Jenkins
     */
    public static boolean isClaimPluginAvailable() {
        return (Hudson.getInstance().getPlugin("claim") == null) ? false : true;
    }
}
