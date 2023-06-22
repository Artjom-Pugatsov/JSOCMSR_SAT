package Checker;

import java.util.List;

public class SAT_Answer {

    public List<Variable_Assignment> assignments;

    public SAT_Answer(List<Variable_Assignment> assignments) {
        this.assignments = assignments;
    }

    public boolean getAssignment(int varID) {
        int id = assignments.get(varID).variableID;
        assert id == varID;
        return assignments.get(varID).isTrue;
    }
}
