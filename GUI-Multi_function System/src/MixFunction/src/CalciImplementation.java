


import java.rmi.*;
import java.rmi.server.*;
import java.util.Stack;

class CalciImplementation  extends UnicastRemoteObject implements CalciInterface{

    CalciImplementation()throws RemoteException
    {
        super();
    }
    @Override
    public float add(float n1, float n2) throws RemoteException {
        return n1+n2;
    }

    @Override
    public float sub(float n1, float n2) throws RemoteException {
        return n1-n2;
    }

    @Override
    public float div(float n1, float n2) throws RemoteException {
        return n1/n2;
    }

    @Override
    public float mul(float n1, float n2) throws RemoteException {
        return n1*n2;
    }
    
    @Override
    public String exp(String exprision) throws RemoteException {        
        /* Create stacks for operators and operands */
        Stack<Integer> op  = new Stack<>();
        Stack<Double> val = new Stack<>();
        /* Create temporary stacks for operators and operands */
        Stack<Integer> optmp  = new Stack<>();
        Stack<Double> valtmp = new Stack<>();
        /* Accept expression */       
        String input = exprision;
        input = "0" + input;
        input = input.replaceAll("-","+-");
        System.out.println(input);
        /* Store operands and operators in respective stacks */
        String temp = "";
        System.out.println(input.length());
        for (int i = 0;i < input.length();i++)
        {
            char ch = input.charAt(i);
            if (ch == '-'){
                temp = "-" + temp;
            System.out.println("val1 <"+(i+1)+"> "+ch+" <"+temp+"> <"+(int)ch+">");}
            else if (ch != '+' &&  ch != '*' && ch != '/'){
               temp = temp + ch;
            System.out.println("val2 <"+(i+1)+"> "+ch+" <"+temp+"> <"+(int)ch+">");}
            else
            {
                System.out.println("val3 <"+(i+1)+"> "+ch+" <"+temp+"> <"+(int)ch+">");
                val.push(Double.parseDouble(temp));
                op.push((int)ch);
                temp = "";
            }
        }
        val.push(Double.parseDouble(temp));
        /* Create char array of operators as per precedence */
        /* -ve sign is already taken care of while storing */
        char operators[] = {'/','*','+'};
        /* Evaluation of expression */
        for (int i = 0; i < 3; i++)
        {
            boolean it = false;
            while (!op.isEmpty())
            {
                int optr = op.pop();
                double v1 = val.pop();
                double v2 = val.pop();
                if (optr == operators[i])
                {
                    /* if operator matches evaluate and store in temporary stack */
                    if (i == 0)
                    {
                        valtmp.push(v2 / v1);
                        it = true;
                        break;
                    }
                    else if (i == 1)
                    {
                        valtmp.push(v2 * v1);
                        it = true;
                        break;
                    }
                    else if (i == 2)
                    {
                        valtmp.push(v2 + v1);
                        it = true;
                        break;
                    }                                        
                }
                else
                {
                    valtmp.push(v1);
                    val.push(v2);
                    optmp.push(optr);
                }                
            }    
            /* Push back all elements from temporary stacks to main stacks */            
            while (!valtmp.isEmpty())
                val.push(valtmp.pop());
            while (!optmp.isEmpty())
                op.push(optmp.pop());
            /* Iterate again for same operator */
            if (it)
                i--;                            
        }    
        return("= "+val.pop());
    }  
}
