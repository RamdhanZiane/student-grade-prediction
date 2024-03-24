package com.gradeprediction.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DataInterface extends Remote {
    // String train() throws RemoteException;
    String predict(List<String> grades) throws RemoteException;

    DataExchangeCode receiveGrades(List<List<String>> grades, String school) throws RemoteException;
}
