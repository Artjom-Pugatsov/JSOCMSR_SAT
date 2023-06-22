package Variable;

public class Variable {

    public long id;
    private static long ID = 1;

    public static long getID() {
        return Variable.ID;
    }

    public Variable() {
        this.id = ID;
        ID++;
    }

    public static void reset() {
        Variable.ID = 1;
    }
}

