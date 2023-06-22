package Constraint;

import Variable.Variable;
import Variable.VariableInConstraint;

import java.util.ArrayList;
import java.util.List;

public class CNFForm {

    public List<CNF_Clause> andOfOrs = new ArrayList<>();

    public void addAND(CNF_Clause toAdd) {
        andOfOrs.add(toAdd);
    }

    public void addAnotherCNF(CNFForm anotherCNFForm) {
        this.andOfOrs.addAll(anotherCNFForm.andOfOrs);
    }

    public void addAllCNFs(List<CNFForm> CNFs) {
        for (CNFForm cnf : CNFs) {
            addAnotherCNF(cnf);
        }
    }

    @Override
    public String toString() {
        int hardClauseValue = 0;
        for (CNF_Clause  clause: andOfOrs) {
            if (clause.weight > hardClauseValue) {
                hardClauseValue = clause.weight;
            }
        }
        hardClauseValue++;
        String toStart = "p wcnf " + (Variable.getID()-1) + " " + andOfOrs.size() + " " + (hardClauseValue);
        StringBuilder builder = new StringBuilder(toStart);
        for (CNF_Clause  line: andOfOrs) {
            builder.append("\n");
            if (line.weight == 0) {
                builder.append(hardClauseValue);
            } else {
                builder.append(line.weight);
            }
            builder.append(" ");
            for (VariableInConstraint var : line.vars) {
                if (!var.isTrue) {
                    builder.append("-");
                }
                builder.append(var.variable.id);
                builder.append(" ");
            }
            builder.append("0");
        }
        //builder.delete(toStart.length(),toStart.length()+1);
        return builder.toString();
    }
}
