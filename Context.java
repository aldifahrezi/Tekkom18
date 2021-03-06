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

import java.util.Stack;
import java.util.LinkedList;

class Context {

    private final int HASH_SIZE = 211;
    private final int INIT = -1;
    private final int MODIF_FUNC = 3;
    private final int MODIF_PROC = 2;

    public static int lexicalLevel;
    public static int orderNumber;
    public static Hash symbolHash;
    public static Stack symbolStack;
    public static Stack typeStack;
    public static String currentStr;
    public static int currentLine;
    public static int currBaseAddr;
    private boolean printSymbols;
    public int errorCount;

    public int functionType;
    public int temp;

    public String currCallName;
    public int currNumberOfParams;
    public static Stack<Integer> orderNumberStack;
    public static Stack<String> callNameStack;
    public static Stack<Integer> numberOfParamsStack;

    public Context() {
        lexicalLevel = INIT;
        orderNumber = 0;
        symbolHash = new Hash(HASH_SIZE);
        symbolStack = new Stack();
        typeStack = new Stack();
        orderNumberStack = new Stack<Integer>();
        callNameStack = new Stack<String>();
        numberOfParamsStack = new Stack<Integer>();
        printSymbols = false;
        errorCount = 0;
    }

    /**
     * This method chooses which action to be taken
     * @input : ruleNo(type:int)
     * @output: -(type:void)
     */
    public void C(int ruleNo) {
        boolean error = false;

        switch(ruleNo) {
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
                if (symbolHash.isExist(currentStr, lexicalLevel)) {
                    System.out.println("Variable declared at line " + currentLine + ": " + currentStr);
                    errorCount++;
                    System.err.println("\nProcess terminated.\nAt least " + (errorCount + parser.yylex.num_error)
                                       + " error(s) detected.");
                    System.exit(1);
                } else {
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
                if (!symbolHash.isExist(currentStr)) {
                    System.out.println("Variable undeclared at line " + currentLine + ": " + currentStr);
                    errorCount++;
                    System.err.println("\nProcess terminated.\nAt least " + (errorCount + parser.yylex.num_error)
                                       + " error(s) detected.");
                    System.exit(1);
                } else {
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
                if (((Integer)typeStack.peek()).intValue() == Bucket.UNDEFINED) {
                    System.out.println("Undefined type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (((Integer)typeStack.peek()).intValue() != Bucket.INTEGER) {
                    System.out.println("Type of integer expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 13:
                if (((Integer)typeStack.peek()).intValue() == Bucket.UNDEFINED) {
                    System.out.println("Undefined type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (((Integer)typeStack.peek()).intValue() != Bucket.BOOLEAN) {
                    System.out.println("Type of boolean expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 14:
                temp = ((Integer)typeStack.pop()).intValue();
                if (temp != ((Integer)typeStack.peek()).intValue()) {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(temp));
                break;
            case 15:
                temp = ((Integer)typeStack.pop()).intValue();
                if ((temp != Bucket.INTEGER) && ((Integer)typeStack.peek()).intValue() != Bucket.INTEGER) {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(temp));
                break;
            case 16:
                temp = symbolHash.find((String)symbolStack.peek()).getIdType();
                if (temp != ((Integer)typeStack.peek()).intValue()) {
                    System.out.println("Unmatched type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 17:
                temp = symbolHash.find((String)symbolStack.peek()).getIdType();
                if (temp != Bucket.INTEGER) {
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
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() == Bucket.UNDEFINED) {
                    System.out.println("Variable not fully defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.SCALAR) {
                    System.out.println("Scalar variable expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 21:
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() == Bucket.UNDEFINED) {
                    System.out.println("Variable not fully defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.ARRAY) {
                    System.out.println("Array variable expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 22:
                symbolHash.find(currentStr).setLLON(lexicalLevel,INIT);
                break;
            case 23:
                break;
            case 24:
                symbolHash.find(currentStr).setIdKind(Bucket.PROCEDURE);
                symbolHash.find(currentStr).setListOfParams(new LinkedList<Bucket>());
                callNameStack.push(currentStr);
                break;
            case 25:
                currCallName = callNameStack.peek();
                Bucket currParam = symbolHash.find(currentStr);
                symbolHash.find(currCallName).getListOfParams().add(currParam);
                break;
            case 26:
                symbolHash.find(currentStr).setIdKind(Bucket.FUNCTION);
                symbolHash.find(currentStr).setListOfParams(new LinkedList<Bucket>());
                callNameStack.push(currentStr);
                break;
            case 27:
                C(51);
                C(2);
                break;
            case 28:
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() == Bucket.UNDEFINED) {
                    System.out.println("Procedure is not defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.PROCEDURE) {
                    System.out.println("Procedure is expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 29:
                currNumberOfParams = symbolHash.find((String)symbolStack.peek()).getNumberOfParams();
                if(currNumberOfParams != 0) {
                    System.out.println("Function or Procedure isn't expected to have parameter(s) at line " + currentLine + ": " + currentStr);
                    System.out.println("But, " + currNumberOfParams + " parameter(s) was found.");
                    errorCount++;
                }
                break;
            case 30:
                numberOfParamsStack.push(0);
                break;
            case 31:
                currNumberOfParams = numberOfParamsStack.peek();
                int numberOfParams = symbolHash.find((String)symbolStack.peek()).getNumberOfParams();
                if(currNumberOfParams > numberOfParams) {
                    System.out.println("Number of parameters mismatched, found: " + currNumberOfParams + " expected, " + numberOfParams + " for: " + ((String)symbolStack.peek()));
                    errorCount++;
                    System.exit(1);
                } else {
                    int paramType = symbolHash.find((String)symbolStack.peek()).getListOfParams().get(numberOfParams-1).getIdType();
                    int exprType = ((Integer)typeStack.peek()).intValue();

                    if(paramType != exprType) {
                        System.out.println("Argument " + numberOfParams + " type mismatch at line " + currentLine + ": " + currentStr);
                        errorCount++;
                    }
                }
                break;
            case 32:
                currNumberOfParams = numberOfParamsStack.pop();
                numberOfParams = symbolHash.find((String)symbolStack.peek()).getNumberOfParams();
                if(currNumberOfParams != numberOfParams) {
                    System.out.println("Number of parameters mismatched, found: " + currNumberOfParams + " expected, " + numberOfParams + " for: " + ((String)symbolStack.peek()));
                    errorCount++;
                    System.exit(1);
                }
                break;
            case 33:
                if (symbolHash.find((String)symbolStack.peek()).getIdKind() == Bucket.UNDEFINED) {
                    System.out.println("Function is not defined at line " + currentLine + ": " + currentStr);
                    errorCount++;
                } else if (symbolHash.find((String)symbolStack.peek()).getIdKind() != Bucket.FUNCTION) {
                    System.out.println("Function is expected at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                break;
            case 34:
                currNumberOfParams = numberOfParamsStack.pop();
                currNumberOfParams++;
                numberOfParamsStack.push(currNumberOfParams);
                break;
            case 35:
                currCallName = callNameStack.peek();
                currNumberOfParams = numberOfParamsStack.peek();
                symbolHash.find(currCallName).setNumberOfParams(currNumberOfParams);
                break;
            case 36:
                functionType = symbolHash.find((String)symbolStack.peek()).getIdType();
                temp = ((Integer)typeStack.peek()).intValue();
                if (temp != functionType) {
                    System.out.println("Unmatched return type at line " + currentLine + ": " + currentStr);
                    errorCount++;
                }
                typeStack.push(new Integer(temp));
                break;
            case 37:
                functionType = symbolHash.find((String)symbolStack.peek()).getIdKind();
                if(functionType==Bucket.FUNCTION) C(33);
                else C(20);
                break;
            case 38:
                break;
            case 39:
                break;
            case 40:
                break;
            case 50:
                orderNumberStack.push(orderNumber);
                break;
            case 51:
                orderNumber = orderNumberStack.pop();
                break;
            case 52:
                currBaseAddr = Generate.cell;
                symbolHash.find(currentStr).setBaseAddress(currBaseAddr);
                break;
            case 53:
                callNameStack.pop();
                numberOfParamsStack.pop();
                break;
            case 54:
                currCallName = callNameStack.peek();
                temp = symbolHash.find((String)symbolStack.peek()).getIdKind();
                numberOfParams = symbolHash.find((String)currCallName).getNumberOfParams();
                LinkedList<Bucket> listOfParams = symbolHash.find(currCallName).getListOfParams();
                for(Bucket b : listOfParams) {
                    int od = b.getOrderNum();
                    int dec = numberOfParams;
                    if(temp==Bucket.FUNCTION) dec += MODIF_FUNC;
                    else dec += MODIF_PROC;
                    b.setOrderNum(od - dec);
                }
                break;
        }
    }

    /**
     * This method sets the current token and line
     * @input : str(type:int), line(type:int)
     * @output: -(type:void)
     */
    public void setCurrent(String str, int line) {
        currentStr = str;
        currentLine = line;
    }

    /**
     * This method sets symbol printing option
     * @input : bool(type:boolean)
     * @output: -(type:void)
     */
    public void setPrint(boolean bool) {
        printSymbols = bool;
    }

}