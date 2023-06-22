package ProblemToContsrints;

import Constraint.CNFForm;

import java.util.List;

public class CNF_And_Key {

    public CNFForm cnfForm;
    public List<String> key;

    public CNF_And_Key(CNFForm cnfForm, List<String> key) {
        this.cnfForm = cnfForm;
        this.key = key;
    }
}
