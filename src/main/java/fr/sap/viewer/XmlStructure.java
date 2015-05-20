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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author I310911
 */
public class XmlStructure {


    private Testcases objectRepresentingTheXmlFile;

}





/**
 * A testcases item contains as many testcase items as there are testcase in
 * the builded project
 */
class Testcases {

    private List<Testcase> testcases;

    public Testcases() {
        testcases = new ArrayList<Testcase>();
        // Get items in the xml file
        
    }
}


/**
 * A testcase item contains a list of key/value and sometimes a defect
 */
class Testcase {

    public static String[] testcaseAttributesNames = {"actualStatus", "classname", "message", "name", "status"};
    private Map<String, String> testcaseAttributesMap;
    private Defect defect;

    public Testcase() {
        testcaseAttributesMap = new HashMap<String, String>(testcaseAttributesNames.length);
        for(String s : testcaseAttributesNames){
            testcaseAttributesMap.put(s, "");
        }
    }
}


/**
 *
 */
class Defect {

    public static String[] defectAttributesNames = {"expectedMessage", "link", "status", "type"};
    private Map<String, String> defectAttributesMap;

    public Defect() {
        defectAttributesMap = new HashMap<String, String>(defectAttributesNames.length);
        for(String s : defectAttributesNames){
            defectAttributesMap.put(s, "");
        }
    }
}
