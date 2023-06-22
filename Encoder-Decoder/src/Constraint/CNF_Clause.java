package Constraint;

import Variable.VariableInConstraint;

import java.util.List;

public class CNF_Clause {

    public List<VariableInConstraint> vars;
    public int weight = 0;

    public CNF_Clause(List<VariableInConstraint> vars) {
        this.vars = vars;
    }


    public void addWeight(int weight) {
        this.weight = weight;
    }
}
