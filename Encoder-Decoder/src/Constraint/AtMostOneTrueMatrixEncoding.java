package Constraint;

import Variable.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AtMostOneTrueMatrixEncoding implements Constraint {

    public List<Variable> varsFromWhichAtMostOneIsTrue;

    private final List<Variable> rowVariable;
    private final List<Variable> columnVariable;

    public AtMostOneTrueMatrixEncoding(List<Variable> varsFromWhichAtMostOneIsTrue) {
        this.varsFromWhichAtMostOneIsTrue = varsFromWhichAtMostOneIsTrue;
        long numVars = varsFromWhichAtMostOneIsTrue.size();
        //Possible conversion errors?
        long rowLength = ((long) Math.ceil(Math.sqrt(numVars)));
        long columnLength = ((long) Math.ceil((double) numVars /rowLength));
        rowVariable = new ArrayList<Variable>();
        for (int i = 0; i < rowLength; i++) {
            rowVariable.add(new Variable());
        }
        columnVariable = new ArrayList<Variable>();
        for (int i = 0; i < columnLength; i++) {
            columnVariable.add(new Variable());
        }
    }

    @Override
    public CNFForm toCNFForm() {
        Constraint oneFromRow = new AtMostOneTrueBasicEncoding(rowVariable);
        Constraint oneFromColumn = new AtMostOneTrueBasicEncoding(columnVariable);

        List<Constraint> mapToSecondaryVariables = new ArrayList<>();

        for (int i = 0; i < varsFromWhichAtMostOneIsTrue.size(); i++) {
            int indexRow = i % rowVariable.size();
            int columnIndex = i / rowVariable.size();
            mapToSecondaryVariables.add(new Implication(varsFromWhichAtMostOneIsTrue.get(i), rowVariable.get(indexRow)));
            mapToSecondaryVariables.add(new Implication(varsFromWhichAtMostOneIsTrue.get(i), columnVariable.get(columnIndex)));
        }
        CNFForm toRet = new CNFForm();
        toRet.addAnotherCNF(oneFromRow.toCNFForm());
        toRet.addAnotherCNF(oneFromColumn.toCNFForm());
        toRet.addAllCNFs(mapToSecondaryVariables.stream().map(x -> x.toCNFForm()).collect(Collectors.toList()));
        return toRet;
    }
}
