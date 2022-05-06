package com.company;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LaptopsInterface {
    @WebMethod int numberOfLaptopsByProducer(String producer);
    @WebMethod String[] listOfProducers();
    @WebMethod String[][] listOfLaptopsByMatrix(String matrix);
    @WebMethod int numberOfLaptopsByMatrixSize(String proportion);
}
