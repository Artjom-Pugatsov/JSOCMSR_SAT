package Constraint;

import Variable.Variable;
import Variable.VariableInConstraint;

import java.util.ArrayList;
import java.util.List;

public class Implication implements Constraint{

    Variable thisVariable, impliesThisVariable;
    public Implication(Variable thisVariable, Variable impliesThisVariable) {
        this.thisVariable = thisVariable;
        this.impliesThisVariable = impliesThisVariable;
    }

    @Override
    public CNFForm toCNFForm() {
        CNFForm toRet = new CNFForm();
        List<VariableInConstraint> toAdd = new ArrayList<>();
        toAdd.add(new VariableInConstraint(thisVariable, false));
        toAdd.add(new VariableInConstraint(impliesThisVariable, true));
        toRet.addAND(new CNF_Clause(toAdd));
        return toRet;
    }
}
