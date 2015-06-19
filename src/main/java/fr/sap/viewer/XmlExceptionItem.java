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

import java.util.HashMap;

/**
 * Represents a state exception.
 * When an expression returned something the state will be changed by the
 * desired one
 * <p>
 * @author I310911
 */
public class XmlExceptionItem {

    private final String expression;
    private final String filename;
    /**
     * key : value returned by the xpath statement
     * value : colors and status to apply for
     */
    private HashMap<String, ViewEntryColor> statusPerReturnedValue;

    public XmlExceptionItem(String expression, String filename, HashMap<String, ViewEntryColor> statusPerReturnedValue) {
        this.expression = expression;
        this.filename = filename;
        this.statusPerReturnedValue = statusPerReturnedValue;
    }

    public String getExpression() {
        return expression;
    }

    public String getFilename() {
        return filename;
    }

    public HashMap<String, ViewEntryColor> getStatusPerReturnedValue() {
        return statusPerReturnedValue;
    }

    public void setStatusPerReturnedValue(HashMap<String, ViewEntryColor> statusPerReturnedValue) {
        this.statusPerReturnedValue = statusPerReturnedValue;
    }

    public String getEscapedFilename() {
        return filename.replaceAll("\"", "").replaceAll("\'", "");
    }

    public String getEscapedExpression() {
        return expression.replaceAll("\"", "").replaceAll("\'", "");
    }
    
    
    

}
