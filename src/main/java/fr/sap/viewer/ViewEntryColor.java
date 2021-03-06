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

import hudson.model.Result;

/**
 * Representative of the displaying of the ViewEntry
 * <p>
 * @author I310911
 */
public class ViewEntryColor {

    /**
     * These values are in the hudson.model.Result Object
     */
//    public final static String[] BUILD_STATE = {Result.SUCCESS.toString(), Result.UNSTABLE.toString(), Result.FAILURE.toString(), Result.NOT_BUILT.toString(), Result.ABORTED.toString()};
    private final String ve_state;
    private final String ve_backgroundColor;
    private final String ve_fontColor;

    public ViewEntryColor(String ve_state, String ve_backgroundColor, String ve_fontColor) {
        this.ve_state = ve_state;
        this.ve_backgroundColor = ve_backgroundColor;
        this.ve_fontColor = ve_fontColor;
    }

    public String getVe_state() {
        return ve_state;
    }

    public String getVe_backgroundColor() {
        return ve_backgroundColor;
    }

    public String getVe_fontColor() {
        return ve_fontColor;
    }

}
