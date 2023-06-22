package Constraint;

import Variable.*;

import java.util.ArrayList;
import java.util.List;

public class AtLeastOneTrue implements Constraint{

    List<Variable> atLeastOneAmongstNeedsToBeTrue;

    public AtLeastOneTrue(List<Variable> atLeastOneAmongstNeedsToBeTrue) {
        this.atLeastOneAmongstNeedsToBeTrue = atLeastOneAmongstNeedsToBeTrue;
    }

    @Override
    public CNFForm toCNFForm() {
        CNFForm toRet = new CNFForm();
        List<VariableInConstraint> toAdd = atLeastOneAmongstNeedsToBeTrue.stream().map((x) -> new VariableInConstraint(x, true)).toList();
        toRet.addAND(new CNF_Clause(toAdd));
        return toRet;
    }
}
