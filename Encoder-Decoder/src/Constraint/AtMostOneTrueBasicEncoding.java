package Constraint;

import Variable.*;

import java.util.ArrayList;
import java.util.List;

public class AtMostOneTrueBasicEncoding implements Constraint {

    public List<Variable> varsFromWhichAtMostOneIsTrue;

    public AtMostOneTrueBasicEncoding(List<Variable> varsFromWhichAtMostOneIsTrue) {
        this.varsFromWhichAtMostOneIsTrue = varsFromWhichAtMostOneIsTrue;
    }

    @Override
    public CNFForm toCNFForm() {
        CNFForm toRet = new CNFForm();
        for (int i = 0; i < varsFromWhichAtMostOneIsTrue.size(); i++) {
            for (int j = i+1; j < varsFromWhichAtMostOneIsTrue.size(); j++) {
                List<VariableInConstraint> toAdd = new ArrayList<>();
                toAdd.add(new VariableInConstraint(varsFromWhichAtMostOneIsTrue.get(i), false));
                toAdd.add(new VariableInConstraint(varsFromWhichAtMostOneIsTrue.get(j), false));
                toRet.addAND(new CNF_Clause(toAdd));
            }
        }
        return toRet;
    }
}
