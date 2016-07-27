package servicelayer;

import java.util.*;
import java.sql.*;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import businesslayer.*;


@WebService(serviceName = "beerservice")
public class beerservice {
         
  @WebMethod(operationName = "getToken")
  public String getToken(String username,String password)throws beerException {
  
   String token = "";
   BeerBL businessObject = new BeerBL();
   if(username.trim().length() > 0 && password.trim().length() > 0 ){      
      token = businessObject.getToken(username, password);
            
      if(businessObject.checkError()){
         throw new beerException("Beerservice Error", businessObject.getErrorMessage());
      }
   }
   else{
      throw new beerException("Beerservice Error", "Invalid argument exception");
   }
   return token;  
  }
   
   @WebMethod(operationName = "getMethods")
   public ArrayList<String> getMethods()throws beerException{
     
      ArrayList<String> methodList = new ArrayList<String>();
      methodList.add("String getToken(String,String)");
      methodList.add("Double getPrice(String,String)");
      methodList.add("Boolean setPrice(String, Double,String)");
      methodList.add("Vector getBeers(String)");
      methodList.add("String getCheapest(String)");
      methodList.add("String getCostliest(String)");  
      return methodList;     
   }
   
   @WebMethod(operationName = "getPrice")   
   public Double getPrice(@WebParam(name = "beer")String beer
                        , @WebParam(name = "token")String token) throws beerException{
   
      double price = -1.0;
      BeerBL businessObject = new BeerBL();
      if(beer.trim().length()>0 && token.trim().length() > 0){
       price = businessObject.getPrice(beer, token); 
          if(businessObject.checkError()){
            throw new beerException("Beerservice Error", businessObject.getErrorMessage());
         }
      }
      else{
         throw new beerException("Beerservice Error", "Invalid argument exception");
      }
     
      return price;    
   }
   
   @WebMethod(operationName = "setPrice")
   public Boolean setPrice( @WebParam(name = "beer")  String beer
                          , @WebParam(name = "price") Double price
                          , @WebParam(name = "token") String token) throws beerException{
   
      boolean success = false;
      BeerBL businessObject = new BeerBL();
      if(beer.trim().length()>0 && token.trim().length() > 0 && price > 0.0){
      
         success = businessObject.setPrice(beer, price, token); 
          if(businessObject.checkError()){
            throw new beerException("Beerservice Error", businessObject.getErrorMessage());
         }
 
      }
      else{
         throw new beerException("Beerservice Error", "Invalid argument exception");
      }
       
      return success;         
   }
   
   @WebMethod(operationName = "getBeers")
   public ArrayList<String> getBeers(@WebParam(name = "token")String token) throws beerException{
    
      ArrayList<String> beerList = null;
      BeerBL businessObject = new BeerBL();
      beerList = businessObject.getBeers(token); 
       if(businessObject.checkError()){
            throw new beerException("Beerservice Error", businessObject.getErrorMessage());
         }
         
      return beerList;         
   }
   
   @WebMethod(operationName = "getCheapest")
   public String getCheapest(@WebParam(name = "token")String token)throws beerException{
   
     String cheapestBeer = "";
     BeerBL businessObject = new BeerBL();
     cheapestBeer = businessObject.getCheapest(token);
       if(businessObject.checkError()){
            throw new beerException("Beerservice Error", businessObject.getErrorMessage());
         }
 
      return cheapestBeer;
   
   }
   
   @WebMethod(operationName = "getCostliest")
   public String getCostliest(@WebParam(name = "token") String token)throws beerException{
      
      String costliestBeer = "";
      BeerBL businessObject = new BeerBL();
      costliestBeer = businessObject.getCostliest(token); 
       if(businessObject.checkError()){
            throw new beerException("Beerservice Error", businessObject.getErrorMessage());
         }
     
      return costliestBeer;
   }
   
}