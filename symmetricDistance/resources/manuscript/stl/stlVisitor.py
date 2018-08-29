# Generated from /home/cristi/Dropbox/work/workspace_linux_precision5520/python-stl/stl/stl.g4 by ANTLR 4.7.1
from antlr4 import *

'''
 Copyright (C) 2015-2018 Cristian Ioan Vasile <cvasile@mit.edu>,
                         Prashant Vaidyanathan <prash@bu.edu>,
                         Curtis Madsen <ckmadsen@bu.edu>
 Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 Cross Disciplinary Integration for Design Automation Research (CIDAR Lab), Boston University
 See license.txt file for license information.
'''


# This class defines a complete generic visitor for a parse tree produced by stlParser.

class stlVisitor(ParseTreeVisitor):

    # Visit a parse tree produced by stlParser#booleanPred.
    def visitBooleanPred(self, ctx):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by stlParser#formula.
    def visitFormula(self, ctx):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by stlParser#parprop.
    def visitParprop(self, ctx):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by stlParser#expr.
    def visitExpr(self, ctx):
        return self.visitChildren(ctx)


    # Visit a parse tree produced by stlParser#booleanExpr.
    def visitBooleanExpr(self, ctx):
        return self.visitChildren(ctx)


