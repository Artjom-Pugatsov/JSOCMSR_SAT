import Checker.*;
import Constraint.AtMostOneTrueBasicEncoding;
import Constraint.AtMostOneTrueMatrixEncoding;
import Constraint.*;
import MyFileWriter.MyFileWriter;
import ProblemParser.JSOCMSR_parser;
import ProblemParser.PC_JSOCMSR_parser;
import ProblemToContsrints.CNF_And_Key;
import ProblemToContsrints.JSOCMSREncoder;
import Problems.JSOCMSR;
import Problems.PC_JSOCMSR;
import Variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String instanceFolderPath = "";
    static final String outputFolderPath = "";
    public static void main(String[] args) {

        //Below is the code for encoding JSOCMSR instances into SAT

        //Parameters of the instances to encode
        int[] sizes = new int[]{10,20,50,100,150,200};
        String[] types = new String[]{ "B", "S"};
        int[] numResources = new int[]{3,5};

        //The reading of .seq files and encoding them to CNF
        for (String tpe: types) {
            for (int rNum: numResources) {
                for (int numOfJobs: sizes) {
                    for (int i  = 1; i <=20; i++) {
                        String t;
                        if (tpe.equals("B")) {
                            t = "t8";
                        } else {
                            t = "t9";
                        }
                        //Parse the problem
                        JSOCMSR instance = JSOCMSR_parser.parseJSOCMSR_file(instanceFolderPath+tpe+"\\m"+rNum+"\\"+t+"-m"+rNum+"_" + toTripDigitString(i) + "_.seq", numOfJobs);

                        //Scales all instances by integer dividing them proportionally. Else the generated encoding files are too large
                        instance.scale(150);
                        //Make a filename
                        String filename = tpe+"_"+rNum+"_"+numOfJobs + "_" + i ;
                        CNF_And_Key encoded = JSOCMSREncoder.jsocmsrToCnf(instance);
                        MyFileWriter.createAndWriteToFile(outputFolderPath+"\\"+filename +".wcnf", encoded.cnfForm.toString());
                        MyFileWriter.createAndWriteToFile(outputFolderPath+"\\"+filename + ".key", toSingleString(encoded.key));

                        //Important to reset the variable ids before making a new encoding
                        Variable.reset();
                    }
                }
            }
        }

        //COMMENT-OUT THE CODE BELOW TO ONLY RUN THE ENCODING. It is here to solve as an example for checking correctness of a solution
        //To check the correctness of resulting instance, provide the path to the key generated while encoding, the original instance itself and the
        //solution file. Note that parser for the solution file is based on the specifics of Pumpkin solver and needs to be modified for other solver instances
        String mappingKeyFile = "";
        String instanceFilePath = "";
        String solutionFilePath = "";

        JSOCMSR instance = JSOCMSR_parser.parseJSOCMSR_file( instanceFilePath + toTripDigitString(1) + "_.seq", 100);
        //Apply the same scaling as used for encoding
        instance.scale(150);
        SolutionKeyJSOCMSR key = JSOCMSRKeyParser.parseJSOCMSRKey_file(mappingKeyFile);
        SAT_Answer answer = SATAnserParser.parseSAT_Answer(solutionFilePath);
        JSOCMSR_Checker.check(instance, key, answer);



    }

    //Helper method to format strings and more easily loop over the names of the original problem instances
    private static String toSingleString(List<String> toConcat) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : toConcat) {
            stringBuilder.append(str);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    private static String toTripDigitString(int toTransform) {
        if (toTransform < 10 & toTransform >= 0) {
            return "00" + toTransform;
        } else if (toTransform < 99) {
            return ("0" + toTransform);
        } else {
            return "" + (toTransform % 1000);
        }
    }

}