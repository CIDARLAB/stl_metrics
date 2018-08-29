'''
 Copyright (C) 2015-2016 Cristian Ioan Vasile <cvasile@bu.edu>
 Hybrid and Networked Systems (HyNeSs) Group, BU Robotics Lab, Boston University
 See license.txt file for license information.
'''

# import itertools as it

import numpy as np
from antlr4 import InputStream, CommonTokenStream

from stlLexer import stlLexer
from stlParser import stlParser
from stlVisitor import stlVisitor


class Operation(object):
    NOP, NOT, OR, AND, IMPLIES, UNTIL, EVENT, ALWAYS, PRED, BOOL = range(10)
    opnames = [None, '!', '||', '&&', '=>', 'U', 'F', 'G', 'predicate', 'bool']
    opcodes = {'!': NOT, '&&': AND, '||' : OR, '=>': IMPLIES,
               'U': UNTIL, 'F': EVENT, 'G': ALWAYS}
    negop = (NOP, NOP, AND, OR, AND, NOP, ALWAYS, EVENT, PRED, BOOL)

    @classmethod
    def getCode(cls, text):
        ''' Gets the code corresponding to the string representation.
        @param text string representation of an operation
        @return the operation code of this operation
        '''
        return cls.opcodes.get(text, cls.NOP)

    @classmethod
    def getString(cls, op):
        '''Gets custom string representation for each operation.
        @param op the operation code of an operation
        @return string representation of this operation
        '''
        return cls.opnames[op]


class RelOperation(object):
    NOP, LT, LE, GT, GE, EQ, NQ = range(7)
    opnames = [None, '<', '<=', '>', '>=', '=', '!=']
    opcodes = {'<': LT, '<=': LE, '>' : GT, '>=': GE, '=': EQ, '!=': NQ}
    negop = (NOP, GE, GT, LE, LT, NQ, EQ)

    @classmethod
    def getCode(cls, text):
        return cls.opcodes.get(text, cls.NOP)

    @classmethod
    def getString(cls, rop):
        return cls.opnames[rop]


class STLFormula(object):
    ''' TODO:
    '''
    maximumRobustness = 1

    def __init__(self, operation, **kwargs):
        '''TODO:
        '''
        self.op = operation

        if self.op == Operation.PRED:
            self.relation = kwargs['relation']
            self.variable = kwargs['variable']
            self.threshold = kwargs['threshold']
        elif self.op in (Operation.AND, Operation.OR, Operation.IMPLIES):
            self.left = kwargs['left']
            self.right = kwargs['right']
        elif self.op == Operation.NOT:
            self.child = kwargs['child']
        elif self.op in(Operation.ALWAYS, Operation.EVENT):
            self.low = kwargs['low']
            self.high = kwargs['high']
            self.child = kwargs['child']
        elif self.op == Operation.UNTIL:
            self.low = kwargs['low']
            self.high = kwargs['high']
            self.left = kwargs['left']
            self.right = kwargs['right']

    def robustness(self, s, t):
        '''TODO:
        '''
        if self.op == Operation.PRED:
            value = s.value(self.variable, t)
            if self.relation in (RelOperation.GE, RelOperation.GT):
                return  value - self.threshold
            elif self.relation in (RelOperation.LE, RelOperation.LT):
                return self.threshold - value
            else:
                return -abs(value - self.threshold)
        elif self.op == Operation.AND:
            return min(self.left.robustness(s, t), self.right.robustness(s, t))
        elif self.op == Operation.OR:
            return max(self.left.robustness(s, t), self.right.robustness(s, t))
        elif self.op == Operation.IMPLIES:
            return max(-self.left.robustness(s, t), self.right.robustness(s, t))
        elif self.op == Operation.NOT:
            return -self.child.robustness(s, t)
        elif self.op == Operation.UNTIL:
#             r_acc = min((self.left.robustness(s, t+tau) for tau in np.arange(self.low+1)))
#             rleft = (self.left.robustness(s, t+tau) for tau in np.arange(self.low, self.high+1))
#             rright = (self.right.robustness(s, t+tau) for tau in np.arange(self.low, self.high+1))
# 
#             value = -STLFormula.maximumRobustness
#             for rl, rr in it.izip(rleft, rright):
#                 r_acc = min(r_acc, rl)
#                 value = 
            raise NotImplementedError
        elif self.op == Operation.ALWAYS:
            return min( (self.child.robustness(s, t+tau)
                                for tau in np.arange(self.low, self.high+1)))
        elif self.op == Operation.EVENT:
            return max( (self.child.robustness(s, t+tau)
                                for tau in np.arange(self.low, self.high+1)))

    def negate(self):
        '''TODO:
        '''
        if self.op == Operation.PRED:
            self.relation = RelOperation.negop[self.relation]
        elif self.op in (Operation.AND, Operation.OR, Operation.IMPLIES):
            self.left = self.left.negate()
            self.right = self.right.negate()
        elif self.op == Operation.NOT:
            return self.child
        elif self.op == Operation.UNTIL:
            raise NotImplementedError
        elif self.op in (Operation.ALWAYS, Operation.EVENT):
            self.child = self.child.negate()
        self.op = Operation.negop[self.op]
        return self

    def pnf(self):
        '''TODO:
        '''
        if self.op == Operation.PRED:
            pass
        elif self.op in (Operation.AND, Operation.OR):
            self.left = self.left.pnf()
            self.right = self.right.pnf()
        elif self.op == Operation.IMPLIES:
            self.left = self.left.negate()
            self.right = self.right.pnf()
            self.op = Operation.OR
        elif self.op == Operation.NOT:
            return self.child.negate()
        elif self.op == Operation.UNTIL:
            raise NotImplementedError
        elif self.op in (Operation.ALWAYS, Operation.EVENT):
            self.child = self.child.pnf()
        return self

    def bound(self):
        '''TODO:
        '''
        if self.op == Operation.PRED:
            return 0
        elif self.op in (Operation.AND, Operation.OR, Operation.IMPLIES):
            return max(self.left.bound(), self.right.bound())
        elif self.op == Operation.NOT:
            return self.child.bound()
        elif self.op == Operation.UNTIL:
            return self.high + max(self.left.bound(), self.right.bound())
        elif self.op in (Operation.ALWAYS, Operation.EVENT):
            return self.high + self.child.bound()

    def variables(self):
        '''TODO:
        '''
        if self.op == Operation.PRED:
            return {self.variable}
        elif self.op in (Operation.AND, Operation.OR, Operation.IMPLIES,
                         Operation.UNTIL):
            return self.left.variables() | self.right.variables()
        elif self.op in (Operation.NOT, Operation.ALWAYS, Operation.EVENT):
            return self.child.variables()

    def __eq__(self, other):
        #TODO:
        return False

    def __ne__(self, other):
        return not self.__eq__(other)

    def __str__(self):
        opname = Operation.getString(self.op)
        if self.op == Operation.PRED:
            return '({v} {rel} {th})'.format(v=self.variable, th=self.threshold,
                                    rel=RelOperation.getString(self.relation))
        if self.op in (Operation.AND, Operation.OR, Operation.IMPLIES):
            return '{left} {op} {right}'.format(left=self.left, op=opname,
                                                right=self.right)
        elif self.op == Operation.NOT:
            return '{op} {child}'.format(op=opname, child=self.child)
        elif self.op == Operation.UNTIL:
            return '({left} {op}[{low}, {high}] {right})'.format(op=opname,
                left=self.left, right=self.right, low=self.low, high=self.high)
        elif self.op in (Operation.ALWAYS, Operation.EVENT):
            return '({op}[{low}, {high}] {child})'.format(op=opname,
                                low=self.low, high=self.high, child=self.child)
        return ''


class STLAbstractSyntaxTreeExtractor(stlVisitor):

    def visitFormula(self, ctx):
        op = Operation.getCode(ctx.op.text)
        ret = None
        low = -1
        high = -1
        if op in (Operation.AND, Operation.OR, Operation.IMPLIES):
            ret = STLFormula(op, left=self.visit(ctx.left),
                             right=self.visit(ctx.right))
        elif op == Operation.NOT:
            ret = STLFormula(op, child=self.visit(ctx.child))
        elif op == Operation.UNTIL:
            low = float(ctx.low.text)
            high = float(ctx.high.text)
            ret = STLFormula(op, left=self.visit(ctx.left),
                             right=self.visit(ctx.right), low=low, high=high)
        elif op in (Operation.ALWAYS, Operation.EVENT):
            low = float(ctx.low.text)
            high = float(ctx.high.text)
            ret = STLFormula(op, child=self.visit(ctx.child),
                             low=low, high=high)
        else:
            print('Error: unknown operation!')
        return ret

    def visitBooleanPred(self, ctx):
#         print 'BooleanPred'
        return self.visit(ctx.booleanExpr())

    def visitBooleanExpr(self, ctx):
#         print "BEXPR", ctx.op.text, ctx.left.getText(), ctx.right.getText()
        return STLFormula(Operation.PRED,
            relation=RelOperation.getCode(ctx.op.text),
            variable=ctx.left.getText(), threshold=float(ctx.right.getText()))

    def visitParprop(self, ctx):
#         print 'PARS'
        return self.visit(ctx.child);


class Trace(object):
    '''TODO:
    '''

    def __init__(self, variables, timePoints, data):
        '''TODO:
        '''
        self.variables = list(variables)
        self.timePoints = list(timePoints)
        self.data = np.array(data)

    def value(self, variable, t):
        '''TODO:
        '''
        var_idx = self.variables.index(variable)
        t_idx = self.timePoints.index(t)
        return self.data[var_idx][t_idx]

    def values(self, variable):
        '''TODO:
        '''
        var_idx = self.variables.index(variable)
        return self.data[var_idx]

    def __str__(self):
#         String trace = "((\"time\"";
#         for (String variable : variables) {
#             trace += ", \"" + variable + "\"";
#         }
#         trace += ")";
#         for (int i = 0; i < timePoints.length; i++) {
#             trace += ",(" + timePoints[i];
#             for (double value : data[i]) {
#                 trace += ", " + value;
#             }
#             trace += ")";
#         }
#         trace += ")";
#         return trace;
        raise NotImplementedError

if __name__ == '__main__':
    lexer = stlLexer(InputStream("!(x < 10) && F[0, 2] y > 2 || G[1, 3] z<=8"))
    tokens = CommonTokenStream(lexer)

    parser = stlParser(tokens)
    t = parser.stlProperty()
    print t.toStringTree()

    ast = STLAbstractSyntaxTreeExtractor().visit(t)
    print 'AST:', ast

    pnf = ast.pnf()
    print pnf

    varnames = ['x', 'y', 'z']
    data = [[8, 8, 11, 11, 11], [2, 3, 1, 2, 2], [3, 9, 8, 9, 9]]
    timepoints = [0,1,2,3]
    s = Trace(varnames, timepoints, data)

    print 'r:', ast.robustness(s, 0)
