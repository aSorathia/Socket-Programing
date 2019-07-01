


import java.rmi.*;

public interface CalciInterface extends Remote{
    public float add(float n1, float n2)throws RemoteException;
    public float sub(float n1, float n2)throws RemoteException;
    public float div(float n1, float n2)throws RemoteException;
    public float mul(float n1, float n2)throws RemoteException;
    public String exp(String exprision)throws RemoteException;
}
