package businesslayer;

import java.util.*;
import java.sql.*;
import datalayer.*;
import java.text.SimpleDateFormat;


// The BeerBL is responsible for execution of business logic for the beer service
public class BeerBL {

  // define and initialize business variables
  
// Minimum age limit in years
  private int _age = 21;  
   
// No operation from midnight to morning 10 AM  
  private int _hours = 10; 
  
// Time in minutes until the token is valid  
  private int  _expirationInMinutes = 5;  
  
  
  private boolean isError = false;
  private String errorMsg = "";  
  
  // Un-initialized Beer Data layer object
  private BeerDL dl = null;

  public BeerBL (){
  // Initialize Beer data layer object  
   dl = new BeerDL();
  }
     
 

   public String getToken (String username, String password){
   
       String token = "";
                
       // Check arguments have some value
       if(username.trim().length() > 0 && password.trim().length() > 0 ){
       
          // Aunthenticate user
          ArrayList<ArrayList<String>> res = dl.authenticateUser(username, password);
          
          //Datalayer error check
          if(!(dl.checkError())){
          
           int userID = Integer.parseInt(res.get(0).get(0));
             if(userID > 0){
             
             //generate token for valid user                 
               ArrayList<ArrayList<String>> res1 =   dl.generateToken(userID);
               
               //Datalayer error check
               if(!(dl.checkError())){
                  token = res1.get(0).get(0); 
               }
               else{                 
                 isError = true;
                 errorMsg = dl.getErrorMessage();
               }               
             }
             else{
                 isError = true;
                 errorMsg = "Error: Invalid user";
             }                     
          } 
          else{
            
            isError = true;
            errorMsg = dl.getErrorMessage();
          }       
       }
       else{
          isError = true;
          errorMsg = "Error: Invalid arguments";
       }       
       return token;
   }
   
   
   
    public ArrayList<String> getBeers(String token){//String token
  
         ArrayList<String> res = new  ArrayList<String>();
         //check business rule
         if(checkBusinessRules(token)){
         
          //Check if business rules are violated

             if(! isError){
             
                ArrayList<ArrayList<String>> list = dl.getBeerCollection();
                
                if(dl.checkError()){
                     isError = true;
                     errorMsg = dl.getErrorMessage();
                  
                } // error check for data layer
               else{
                     for(ArrayList<String> arrList : list){
                     res.add(arrList.get(0));             
                     }                
               }
            
           } // error condition

         } //Business rules                 
         return res;
         
      } // getBeers Method
               

   public double getPrice(String beerName, String token){
    
    double price = 0.0;
    
    if(beerName.trim().length() > 0){
    
         //check business rule  
         if(checkBusinessRules(token)){

         //Check if business rules are  violated
         if(! isError){
            price = dl.getPrice(beerName);
            
            //Datalayer error check
            if(dl.checkError()){
               isError = true;
               errorMsg = dl.getErrorMessage();
               
            } // error check for data layer
          }
            
         }// businessRules check
            
      } // length check
      else{
       isError = true;
       errorMsg = "Error: Invalid argument";
     }
      
    return price;
   
   } // getPrice method ends

 
 public String getCostliest(String token) {
  String costliestBeer = null;
   //check business rule            
   if(checkBusinessRules(token)){
   
   //Check if business rules are  violated
      if(! isError){
         costliestBeer = dl.getCheapestCostliestBeer(false);

         //Datalayer error check
         if(dl.checkError()){
         
            isError = true;
            errorMsg = dl.getErrorMessage();
            
         } // error check for data layer
       }
      
   }// businessRules check
   return costliestBeer;                   
 }



 public String getCheapest(String token) {
 String cheapestBeer = null;
 //check business rule
  if(checkBusinessRules(token)){
    
      //Check if business rules are  violated
      if(! isError){
         cheapestBeer = dl.getCheapestCostliestBeer(true);
         
         //Datalayer error check
         if(dl.checkError()){
            isError = true;
            errorMsg = dl.getErrorMessage();         
         } // error check for data layer
       }
   
  }// businessRules check
   return cheapestBeer;       
 } 
 
 
 public boolean setPrice(String beerName, double price, String token){
 
    int affectedRows = 0; 
    //check arguments
    if(beerName.trim().length() > 0 && price >0.0 ){
      
      // check if user updating beer price is admin
        if(checkIfAdmin(token)){
        
        //Check if business rules are  violated
          if(checkBusinessRules(token)){
          
             affectedRows = dl.setBeerPrice(beerName, price);
             //check datalayer error
             if(dl.checkError()){
                isError = true;
                errorMsg = dl.getErrorMessage();
             }          
          } // business rules        
        
        }// Admin check            
    
    }// checking if parameters are valid
    else{
       isError = true;
       errorMsg = "Error: Invalid arguments";
    }
     
   return (affectedRows > 0 )?true:false;
 } //setPrice Method
  
 
 //Returns true or false to determine operations are underway or not respectively
 private boolean checkOperationHours(){  
             
     Calendar calendar = new GregorianCalendar();     
     int hour = calendar.get( Calendar.HOUR );   
     int minute = calendar.get( Calendar.MINUTE );   
     // int second = calendar.get(Calendar.SECOND);
     
     //Check time 
     // 0: AM  and 1: PM
     if( calendar.get( Calendar.AM_PM ) == 0 ){ 
     
         if(hour < _hours || (hour == _hours &&  minute == 0 )){         
            isError = true; 
            errorMsg = "Error: Operations closed between midnight and 10 am.";     
         }        
     }
    
    // If error the methods returns false
     return (! isError);
  }
   
   // Checks if user has a valid age to avail beer service  
   private boolean validateAge(String token){ 
   
      ArrayList<HashMap<String,String>> res = dl.getUserDetails(token);        
      if(! dl.checkError()){
        
         for(HashMap<String,String> row : res) {         
            String age = row.get("age");
                          
            if(Integer.parseInt(age)< _age){
               isError = true;
               errorMsg = "Under-age user";
            } // Age Violation 
                
         }// for Hashmap
      }
      else{   
         isError = true;
         errorMsg = dl.getErrorMessage();
      }
       return (!isError);
   }
 
   //Checks if the logged in user is an admin
   private boolean checkIfAdmin(String token){
    
   ArrayList<HashMap<String,String>> res = dl.getUserDetails(token);
    //Check Datalayer errror
      if( ! (dl.checkError())){    
         for(HashMap<String,String> row : res) {      
            String adminFlag = row.get("isAdmin");       
            isError = ( Integer.parseInt(adminFlag)>0 )?false:true;  
               if(isError){
                  errorMsg = "Admin privileges required to set beer price";
               }     
         }//iterating records
      }
      else{
         isError = true;
         errorMsg = dl.getErrorMessage();
      
      }
   return (! isError);
   }
 
 
// Check all business rules 
private boolean checkBusinessRules(String token){
 
    if(token.trim().length()> 0) { 
          // validate token         
          if(dl.validateToken(token, _expirationInMinutes)){ 
          //validate operations hours    
             if(checkOperationHours()){ 
             //validate age      
                validateAge(token);
              }
         }
         else{
            isError = true;
            errorMsg = dl.getErrorMessage();
         }
     }
     else{
        isError = true;
        errorMsg = "Invalid / expired  token";
     }                                         
     // fails when there is an error
   return (!isError); 
}
  
   //returns true if there is an error
   public boolean checkError(){
      return this.isError;
  }
  
  //retruns error message in case of an error
  public String getErrorMessage(){
   return (this.isError)?this.errorMsg:"";
  }
   

}
