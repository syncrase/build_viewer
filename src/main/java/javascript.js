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

alert("hello");
document.getElementsByTagName('body').defineProperties();
var table = document.getElementById(tableId);
//        <!--// Create an empty <tr> element and add it to the 1st position of the table:-->
var row = table.insertRow(table.rows.length - 2);
//        <!--// Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:-->
var cell = row.insertCell(0);
function resize() {

    var i = 0, j = 0;
    var length = document.styleSheets.length;
    var tab = [""];
    var returnedStr = "";
    var styleSheet;
    for (i = 0; i < length; i++) {
        styleSheet = document.styleSheets[i];
        var ruleName;
        if (styleSheet.title == "myInnerCSS") {
            length = styleSheet.cssRules.length;
            for (j = 0; j < length; j++) {
                var ruleName = styleSheet.cssRules[j].selectorText;
                if (ruleName == "#main-panel-content") {
                    alert(styleSheet.cssRules[j].cssText);
                    styleSheet.insertRule(styleSheet.cssRules[j].cssText + "{height:979px;}", 0);
                    alert(styleSheet.cssRules[j].cssText);
                }
            }
        }
    }
    alert(returnedStr);
}

function addCSSRule() {
    alert("test 658472");
    var styleEl = document.createElement('style');
    document.head.appendChild(styleEl);
    styleEl.sheet.insertRule("#main-panel-content{height:979px;}", 0);
}


