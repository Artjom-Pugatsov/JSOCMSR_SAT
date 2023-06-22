package Constraint;

import Variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ExactlyOneTrue implements Constraint{

    List<Variable> amongstOneExactlyTrue;

    public ExactlyOneTrue(List<Variable> amongstOneExactlyTrue) {
        this.amongstOneExactlyTrue = amongstOneExactlyTrue;
    }


    @Override
    public CNFForm toCNFForm() {
        CNFForm toRet = new CNFForm();
        List<CNFForm> subForms = new ArrayList<>();
        subForms.add(new AtLeastOneTrue(amongstOneExactlyTrue).toCNFForm());
        subForms.add(new AtMostOneTrueMatrixEncoding(amongstOneExactlyTrue).toCNFForm());
        toRet.addAllCNFs(subForms);
        return toRet;
    }
}
