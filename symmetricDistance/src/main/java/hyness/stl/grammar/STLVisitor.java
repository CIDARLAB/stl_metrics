// Generated from STL.g4 by ANTLR 4.7

/**
 * Copyright (C) 2015  Cristian Ioan Vasile <cvasile@bu.edu>, Prashant Vaidyanathan <prash@bu.edu>, Curtis Madsen <ckmadsen@bu.edu>
 * Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 * Cross Disciplinary Integration for Design Automation Research (CIDAR Lab), Boston University        
 * See license.txt file for license information.
 */
package hyness.stl.grammar;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link STLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface STLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code booleanPred}
	 * labeled alternative in {@link STLParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanPred(STLParser.BooleanPredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code formula}
	 * labeled alternative in {@link STLParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormula(STLParser.FormulaContext ctx);
	/**
	 * Visit a parse tree produced by the {@code parprop}
	 * labeled alternative in {@link STLParser#property}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParprop(STLParser.ParpropContext ctx);
	/**
	 * Visit a parse tree produced by {@link STLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(STLParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link STLParser#booleanExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanExpr(STLParser.BooleanExprContext ctx);
}