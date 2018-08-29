'''
Created on Mar 15, 2018

@author: cristi
'''

import os

from antlr4 import InputStream, CommonTokenStream

from stl import stlLexer, stlParser, STLAbstractSyntaxTreeExtractor, Operation,\
                RelOperation

program_string = '''
from ana_STL import STL_computation
from ana_STL import directed_distance

F=STL_computation({no_variables},{bound})

{formula1_construction}
{formula2_construction}

r=directed_distance(F, {formula1}, {formula2})
print(r)
'''

formula_string = 'f_{formula_index}'
pred_string = 'f_{formula_index} = F.add_predicate({variable},"{relation}",{threshold})'
conj_string = 'f_{formula_index} = F.Conj([{left}, {right}])'
disj_string = 'f_{formula_index} = F.Disj([{left}, {right}])'
alw_string = 'f_{formula_index} = F.G(range({low}, {high}+1), {child})'
evt_string = 'f_{formula_index} = F.F(range({low}, {high}+1), {child})'

def code_from_stl(stl_ast, variables, count=0):
    '''TODO:
    '''
    if stl_ast.op == Operation.PRED:
        var_index = variables.index(stl_ast.variable) + 1
        return (pred_string.format(formula_index=count, variable=var_index,
                        relation=RelOperation.getString(stl_ast.relation),
                        threshold=stl_ast.threshold),
                formula_string.format(formula_index=count),
                count+1)
    elif stl_ast.op in (Operation.AND, Operation.OR):
        l_constr_string, l_formula, count = code_from_stl(stl_ast.left,
                                                          variables, count)
        r_constr_string, r_formula, count = code_from_stl(stl_ast.right,
                                                          variables, count)

        if stl_ast.op == Operation.AND:
            constr_string = conj_string.format(formula_index=count,
                                                left=l_formula, right=r_formula)
        else:
            constr_string = disj_string.format(formula_index=count,
                                                left=l_formula, right=r_formula)

        return ('\n'.join((l_constr_string, r_constr_string, constr_string)),
                formula_string.format(formula_index=count),
                count+1)
    elif stl_ast.op in (Operation.NOT, Operation.IMPLIES):
        raise ValueError()
    elif stl_ast.op == Operation.UNTIL:
        raise NotImplementedError
    elif stl_ast.op in (Operation.ALWAYS, Operation.EVENT):
        ch_constr_string, ch_formula, count = code_from_stl(stl_ast.child,
                                                            variables, count)

        ### HACK: multiply by 10
        low = int(stl_ast.low*10)
        high = int(stl_ast.high*10)
        if stl_ast.op == Operation.ALWAYS:
            constr_string = alw_string.format(formula_index=count, low=low,
                                              high=high, child=ch_formula)
        else:
            constr_string = evt_string.format(formula_index=count, low=low,
                                              high=high, child=ch_formula)

        return ('\n'.join((ch_constr_string, constr_string)),
                formula_string.format(formula_index=count),
                count+1)

def translate(formula1, formula2):
    '''TODO:
    '''
    lexer = stlLexer(InputStream(formula1))
    tokens = CommonTokenStream(lexer)
    parser = stlParser(tokens)
    t = parser.stlProperty()
    ast1 = STLAbstractSyntaxTreeExtractor().visit(t)
    print 'AST 1:', ast1
    pnf1 = ast1.pnf()
    print 'PNF 1:', pnf1

    lexer = stlLexer(InputStream(formula2))
    tokens = CommonTokenStream(lexer)
    parser = stlParser(tokens)
    t = parser.stlProperty()
    ast2 = STLAbstractSyntaxTreeExtractor().visit(t)
    print 'AST 2:', ast2
    pnf2 = ast2.pnf()
    print 'PNF 2:', pnf2

    variables = list(pnf1.variables() | pnf2.variables())

    formula1_constr, formula1, count = code_from_stl(pnf1, variables, 0)
    formula2_constr, formula2, count = code_from_stl(pnf2, variables, count)

    ### HACK: multiply by 10
    bound = int(round(10*max(pnf1.bound(), pnf2.bound())))
    program_12 = program_string.format(bound=bound, no_variables=len(variables),
                    formula1_construction=formula1_constr,
                    formula2_construction=formula2_constr,
                    formula1=formula1, formula2=formula2)

    program_21 = program_string.format(bound=bound, no_variables=len(variables),
                    formula1_construction=formula1_constr,
                    formula2_construction=formula2_constr,
                    formula1=formula2, formula2=formula1)

    return program_12, program_21

if __name__ == '__main__':

    with open('gt.txt', 'r') as fin:
        ground_truth = fin.readline()
    with open('thresh6.txt', 'r') as fin:
        phi = fin.readline()
    p12, p21 = translate(ground_truth, phi)
    with open('pyscripts/gt_grid6.py'
              , 'w') as fout:
        print>>fout, p12
    with open('pyscripts/grid6_gt.py'
              , 'w') as fout:
        print>>fout, p21
    