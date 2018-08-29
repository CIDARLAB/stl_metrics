grammar STL;

@header {
/**
 * Copyright (C) 2015  Cristian Ioan Vasile <cvasile@bu.edu>, Prashant Vaidyanathan <prash@bu.edu>, Curtis Madsen <ckmadsen@bu.edu>
 * Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 * Cross Disciplinary Integration for Design Automation Research (CIDAR Lab), Boston University        
 * See license.txt file for license information.
 */
package hyness.stl.grammar;
}


property:
         '(' child=property ')' #parprop
    |    booleanExpr #booleanPred
    |    op='!' child=property #formula
    |    op='F' '[' low=RATIONAL ',' high=RATIONAL ']' child=property #formula
    |    op='G' '[' low=RATIONAL ',' high=RATIONAL ']' child=property #formula
    |    left=property op='=>' right=property #formula
    |    left=property op='&&' right=property #formula
    |    left=property op='||' right=property #formula
    |    left=property op='>>' right=property #formula
    |    left=property op='U' '[' low=RATIONAL ',' high=RATIONAL ']' right=property #formula
    ;
expr:
        ('-('|'(') expr ')'
    |   expr '^' expr
    |   ('sqrt('|'log('|'ln('|'abs('|'der('|'int(') expr ')'
    |   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   RATIONAL
    |   VARIABLE
    ;
booleanExpr:
         left=expr op=('<'|'<='|'='|'>='|'>') right=expr
    |    op=BOOLEAN
    ;
BOOLEAN : ('true'|'false');
VARIABLE : ([a-z]|[A-Z])([a-z]|[A-Z]|[0-9]|'_')*;
RATIONAL : ('-')?[0-9]*('.')?[0-9]+('E'|'E-')?[0-9]* ;
WS : ( ' ' | '\t' | '\r' | '\n' )+ { skip(); };
