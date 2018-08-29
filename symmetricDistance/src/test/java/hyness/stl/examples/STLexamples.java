/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyness.stl.examples;

import hyness.stl.grammar.sharp.STLSharpAbstractSyntaxTreeExtractor;
import hyness.stl.metrics.Utilities;
import java.io.File;

/**
 *
 * @author prash
 */
public class STLexamples {
    public static STLSharpAbstractSyntaxTreeExtractor getSTLSharpFromFile(String filepath){
        STLSharpAbstractSyntaxTreeExtractor stl = new STLSharpAbstractSyntaxTreeExtractor();
        String filecontent = Utilities.getFileContentAsString(filepath);
        stl = STLSharpAbstractSyntaxTreeExtractor.getSTLSharpAbstractSyntaxTreeExtractor(filecontent);
        return stl;
    }
    
    
}
