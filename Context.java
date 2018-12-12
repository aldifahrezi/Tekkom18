/** 
 * @class: Context
 * This class constructs Context object that has attributes : 
 * 1. lexicalLevel    : current lexical level
 * 2. orderNumber     : current order number
 * 3. symbolHash      : hash table of symbols
 * 4. symbolStack     : stack to keep symbol's name
 * 4. typeStack       : stack to keep symbol's type
 * 4. printSymbols    : choice of printing symbols
 * 4. errorCount      : error counter of context checking
 *
 * @author: DAJI Group (Dalton E. Pelawi & Jimmy)
 */

import java.util.LinkedList;
import java.util.Stack;

class Context
{
    public Context()
    {
        lexicalLevel = -1;
        orderNumber = 0;
        symbolHash = new Hash(HASH_SIZE);
        callStack = new Stack<String>();
        symbolStack = new Stack();
        typeStack = new Stack();
        printSymbols = false;
        errorCount = 0;
    }

    /**
     * This method chooses which action to be taken
     * @input : ruleNo(type:int)
     * @output: -(type:void)
     */
    public void C(int ruleNo)
    {
        boolean error = false;

        switch(ruleNo)
        {
            case 0:
                lexicalLevel++;
                orderNumber = 0;
                break;
            case 1:
                if (printSymbols)
                    symbolHash.print(lexicalLevel);
                break;
            case 2:
                symbolHash.delete(lexicalLevel);
                lexicalLevel--;
                break;
            case 3:
                if (symbolHash.isExist(currentStr, lexicalLevel))
                {
                    System.out.println("Variable declared at line " + currentLine + ": " + currentStr);
                    errorCount++;
                    System.err.println("\nProcess terminated.\nAt least " + (errorCount + parser.yylex.num_error)
                                       + " error(s) detected.");
                    System.exit(1);
                }
                else
                {
                    symbolHash.insert(new Bucket(currentStr));
                }
                symbolStack.push(currentStr);
                break;
            case 4:
                symbolHash.find(currentStr).setLLON(lexicalLevel, orderNumber);
                break;
            case 5:
                symbolHash.find(currentStr).setIdType(((Integer)typeStack.peek()).intValue());
                break;
            case 6:
                if (!symbolHash.isExist(currentStr))
                {
                    System.out.println("Variable undeclared at line " + currentLine + ": " + currentStr);
                    errorCount++;
                    System.err.println("\nProcess terminated.\nAt least " + (errorCount + parser.yylex.num_error)
                                       + " error(s) detected.");
                    System.exit(1);
                }
                else
                {
                    symbolStack.push(currentStr);
                }
                break;
            case 7:
                symbolStack.pop();
                break;
            case 8:
                typeStack.push(new Integer(symbolHash.find(currentStr).getIdType()));
                break;
            case 9:
                typeStack.push(new Integer(Bucket.INTEGER));
                break;
            case 10:
                typeStack.push(new Integer(Bucket.BOOLEAN));
                break;
            case 11:
                typeStack.pop();
                break;
            case 12:
                switch (((Integer)typeStack.peek()).intValue())
                {
                    case Bucket.BOOLEAN:
                        System.out.println("Type of integer expected at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                    case Bucket.UNDEFINED:
                        System.out.println("Undefined type at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                }
                break;
            case 13:
                switch (((Integer)typeStack.peek()).intValue())
                {
                    case Bucket.INTEGER:
                        System.out.println("Type of boolean expected at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                    case Bucket.UNDEFINED:
                        System.out.println("Undefined type at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                }
                break;
            case 14:
                int temp = ((Integer)typeStack.pop()).intValue();
                if (temp != ((Integer)typeStack.peek()).intValue())
                {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(temp));
                break;
            case 15:
                temp = ((Integer)typeStack.pop()).intValue();
                if ((temp != Bucket.INTEGER) && ((Integer)typeStack.peek()).intValue() != Bucket.INTEGER)
                {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(temp));
                break;
            case 16:
                temp = symbolHash.find((String)symbolStack.peek()).getIdType();
                if (temp != ((Integer)typeStack.peek()).intValue())
                {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 17:
                temp = symbolHash.find((String)symbolStack.peek()).getIdType();
                if (temp != Bucket.INTEGER)
                {
                    System.out.println("Type of integer expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 18:
                symbolHash.find(currentStr).setIdKind(Bucket.SCALAR);
                orderNumber++;
                break;
            case 19:
                symbolHash.find(currentStr).setIdKind(Bucket.ARRAY);
                orderNumber += 3;
                break;
            case 20:
                switch (symbolHash.find((String)symbolStack.peek()).getIdKind())
                {
                    case Bucket.UNDEFINED:
                        System.out.println("Variable not fully defined at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                    case Bucket.ARRAY:
                        System.out.println("Scalar variable expected at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                }
                break;
            case 21:
                switch (symbolHash.find((String)symbolStack.peek()).getIdKind())
                {
                    case Bucket.UNDEFINED:
                        System.out.println("Variable not fully defined at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                    case Bucket.SCALAR:
                        System.out.println("Array variable expected at line " + currentLine + ": " + currentStr);
                        errorCount++;
                        break;
                }
                break;
            case 22:
                symbolHash.find(currentStr).setLLON(lexicalLevel, orderNumber);
                symbolHash.find(currentStr).setBaseAddress(Generate.cell);
                break;
            case 23:
                symbolHash.find(currentStr).setIdType(((Integer)typeStack.peek()).intValue());
                break;
            case 24:
                symbolHash.find(currentStr).setIdKind(Bucket.PROCEDURE);
                callStack.push(currentStr);
                symbolHash.find(currentStr).setParameters(new LinkedList<Bucket>());
                break;
            case 25:
                break;
            case 26:
                symbolHash.find(currentStr).setIdKind(Bucket.FUNCTION);
                callStack.push(currentStr);
                System.out.println("Pushed " + currentStr);
                symbolHash.find(currentStr).setParameters(new LinkedList<Bucket>());
                break;
            case 27:
                break;
            case 28:
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.PROCEDURE) {
                    System.out.println("Procedure expected or is not fully defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 29:
                LinkedList<Bucket> parameters =  symbolHash.find((String)symbolStack.peek()).getParameters();                
                Integer actualNumberOfParams = (parameters == null ? 0 : parameters.size());
                if (parameters != null && parameters.size() != 0) {
                    System.out.println("Procedure or function expected zero parameters at line " + currentLine + ": " + currentStr);
                    System.out.println("But " + actualNumberOfParams + " parameters was found");
                    errorCount++;
                }
                break;
            case 30:
                break;
            case 31:
                break;
            case 32:
                break;
            case 33:
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.FUNCTION) {
                    System.out.println("Function expected or is not fully defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 34:
                break;
            case 35:
                // Skip since we can get it using Bucket.getParameters.size()
                break;
            case 36:
                Integer expressionType = ((Integer)typeStack.peek()).intValue();
                Integer functionType = symbolHash.find((String)symbolStack.peek()).getIdType();
                
                if (expressionType != functionType) {
                    System.out.println("Unmatched return type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(expressionType));
                break;
            case 37:
                if (symbolHash.find(currentStr).getIdKind() == Bucket.FUNCTION) {
                    C(33);
                } else {
                    C(20);
                }
                break;
        }
    }

    /**
     * This method sets the current token and line
     * @input : str(type:int), line(type:int)
     * @output: -(type:void)
     */
    public void setCurrent(String str, int line)
    {
        currentStr = str;
        currentLine = line;
    }

    /**
     * This method sets symbol printing option
     * @input : bool(type:boolean)
     * @output: -(type:void)
     */
    public void setPrint(boolean bool)
    {
        printSymbols = bool;
    }

    private final int HASH_SIZE = 211;

    public static int lexicalLevel;
    public static int orderNumber;
    public static Hash symbolHash;
    public static Stack symbolStack;
    private Stack typeStack;
    public static String currentStr;
    public static int currentLine;
    private boolean printSymbols;
    public int errorCount;
    
    // Added
    public static Stack<String> callStack;
}