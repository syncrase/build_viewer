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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author I310911
 */
public class XmlWrapper {
    private ArrayList<XmlExceptionItem> xmlExceptionList;
    private final BuildViewer bv;

    public XmlWrapper(BuildViewer bv) {
        this.bv = bv;
        
        

//        String[] fileNameArray = {"junitResult.xml", "test-defects.xml"};
        List<String> filenameList = new ArrayList<String>();
        
        Iterator<XmlExceptionItem> it = bv.getXmlExceptionList().iterator();
        XmlExceptionItem xmlExceptionItem;
        while(it.hasNext()){
            xmlExceptionItem = it.next();
            filenameList.add(xmlExceptionItem.getFilename());
        }
        
        
//        File xmlFile;
//        for ( int i = 0; i < fileNameArray.length; i++ ) {
//            xmlFile = getXmlFile(new File(abstractProject.getBuildDir().getPath()), fileNameArray[i]);
//            applyXPtah(xmlFile, expression);
////            searchForXml(new File(this.abstractProject.getBuildDir().getPath()), fileNameArray[i]);
//        }

    }

    private static File getXmlFile(File root, String filename) {//TODO not necessary if the file has to be stored in the builds/archive folder
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            for ( File file : files ) {
                getXmlFile(file, filename);
            }
        } else if (root.isFile() && root.getName().equals(filename)) {
            return new File(root.getPath());
        }
        return null;
    }

    private static void applyXPtah(File xmlFile, String expression) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
            Node n;

            if (nodeList.getLength() > 0) {
                for ( int i = 0; i < nodeList.getLength(); i++ ) {
                    n = nodeList.item(i);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) n;
                        System.out.println(e.getTextContent());
                    } else if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                        Attr attr = (Attr) n;
                        System.out.println(attr.getValue());
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ProjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(ProjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ProjectWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

