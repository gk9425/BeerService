package datalayer;

import java.util.*;
import java.sql.*;
import datalayer.data.*;

// Data layer for Beer service

public class BeerDL {

  // Variables having values related to Database
   private String dbName = "beerprices";
   private String user = "test";
   private String pswd = "testing";
   private String host = "localhost";
   private String port = "3306";

  
   private boolean isError = false;
   private String  errorMsg = "";
   
   // object for DatabaseAccess class
   private DatabaseAccess db = null;
   
   public BeerDL(){
   
      try{
         db = new DatabaseAccess(dbName, user, pswd, host, port);      
      }
      catch(SQLException e) {     
         isError = true;
         errorMsg = "Error: SQLException occurred";
      }
      catch(ClassNotFoundException e) {     
         isError = true;
         errorMsg = "Error: ClassNotFoundException occurred";
      }     
   }
            
   public ArrayList<ArrayList<String>> getBeerCollection(){
   
      ArrayList<ArrayList<String>> res = null;
                                
      // check db object 
         if(db != null){
         
            String sql = "Select beername from beers order by price desc";
            try{          
               res = db.getData(sql);
               if(res == null){          
                  isError = true;
                  errorMsg = "Error: Null reference to a database access object";
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg = "Error: Null reference to a database access object";
         }          
      return res;           
   }
   
   
   // Gets price for a given beer
   public double getPrice(String beerName){
      double price = 0.0;
      
                           
         // check db access 
         if(db != null){
            
            String sql = "Select price from beers where beername=?";
            ArrayList<String> params = new ArrayList<String>();
            params.add(beerName);
             
            try{          
               ArrayList<ArrayList<String>> res = db.getDataPS(sql, params);
               if(res == null){          
                  isError = true;
                  errorMsg = "Error: Beer name " + beerName +" does not exist in the database";
               }
               else{
                  for (ArrayList<String> value :res){  
                     price = Double.parseDouble(value.get(0));
                  }
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg =  "Error: Null reference to a database access object";
         }              
      return price;          
   }
   
   // Method retuns cheapest / costliest beer depending of the cheapest flag.
   // cheapest = true; returns cheapest beername
   // cheapest = false; returns costliest beername
   public String getCheapestCostliestBeer(boolean cheapest){
   
      String beerName = null;
                       
      // check db access 
         if(db != null){
         
            String sql = (cheapest)? "Select beername, price from beers order by price Limit 1" 
                                   : "Select beername, price from beers order by price desc Limit 1";
            try{          
               ArrayList<HashMap<String,String>> res = db.getDataWithColNames(sql);
               for(HashMap<String,String> row : res){
                  beerName = row.get("beername");
               }
               if(res == null){          
                  isError = true;
                  // error message depending on the flag value
                  errorMsg = (cheapest)? "Error: Can't fetch cheapest beer from the database" 
                                       : "Error: Can't fetch costliest beer from the database";
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg = "Error: Null reference to a database access object";
         }      
      return beerName;          
   }
   
  // Update price for a given beer
   public int setBeerPrice(String beerName, double beerPrice){
   
      int affectedRows = 0;
      ArrayList<String> params = new ArrayList<String>();
      params.add(Double.toString(beerPrice));
      params.add(beerName);
    
      String sql = "Update beers set price = ? where beerName = ?";
      try{
         affectedRows = db.nonSelect(sql, params);
         
         if(!(affectedRows > 0)){
         
         isError = true;
         errorMsg = "Error: Beer price for " + beerName + " can not be updated due to an error. Kindly check if "+ beerName +" exists";
         
         }
      }
      catch(SQLException e){
         isError = true;
         errorMsg = "Error: SQLException occurred";      
      }
      return  affectedRows;  
   }
   
  // Authenticate user based on his username and password
  // If user id valid returns user ID.
   public ArrayList<ArrayList<String>> authenticateUser(String uname, String pswd){
   
      ArrayList<ArrayList<String>> res = null;     
      ArrayList<String> params = new ArrayList<String>();               
                   
      // check db access 
         if(db != null){
         
            String sql = "Select id from user where username = ? and password = ?";
            params.add(uname);
            params.add(pswd);
            try{          
               res = db.getDataPS(sql, params);
               if(res == null){          
                  isError = true;
                  errorMsg = "Error: Invalid username /password";
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg = "Error: Null reference to a database access object";
         }
              
      return res;        
   }
    
   // Based on user token the method retuns details for a valid user           
   public ArrayList<HashMap<String,String>> getUserDetails(String token){
     
      ArrayList<HashMap<String,String>> res = null;     
      ArrayList<String> params = new ArrayList<String>();
          
                        
      // check db access 
         if(db != null){
         
            String sql = "Select a.* from user a join token b on a.id = b.userID where b.tokenID = ?";
            params.add(token);
            try{          
               res = db.getDataPSWithColNames(sql, params);
               if(res == null){          
                  isError = true;
                  errorMsg = "Error: User details unavailable";
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg = "Error: Null reference to a database access object";
         }
            
      return res;           
   }
  
  
  // Generates a unique security token for a valid user
   public ArrayList<ArrayList<String>> generateToken(int userID){
   
      ArrayList<ArrayList<String>> res = null;     
      ArrayList<String> params = new ArrayList<String>();
          
                         
      // check db access 
         if(db != null){
         
         // delete token if it already exists for the user
         
          deleteTokenIDForUser(userID);
         
           //Random token generation using MYSQL
            String sql = "Select left(UUID(),8) as token";
                   
            try{          
               res = db.getDataPS(sql, params);
               if(res == null){          
                  isError = true;
                  errorMsg = "Error: Unable to generate security token due to an error";
               }
               else{
               // Generated token being saved in the database
                  sql = "Insert into token (tokenID, userID, ExpirationDate) Values (?, ? ,now())";
                  params.add(res.get(0).get(0));
                  params.add(Integer.toString(userID));
                  int affectedRows = db.nonSelect(sql, params);  
                  if(!(affectedRows > 0)){
                     isError = true;
                     errorMsg = "Error:Unable to save generated token due to an error.";
                  }          
               }
            }
            catch(SQLException e)
            {
               isError = true;
               errorMsg = "Error: SQLException occurred";
            }           
         }
         else{
            isError = true;
            errorMsg = "Error: Null reference to a database access object";        
      }          
      return res;        
   }
  
  
   // Validates if the passed token is Valid and has  not expired
   
   public boolean validateToken(String tokenID, int timeDuration){
   
      boolean isValid = false;
      ArrayList<ArrayList<String>> res = null;     
      ArrayList<String> params = new ArrayList<String>();
                                  
      // check db access 
      if(db != null){
      
         String sql = "Select  id from token where tokenid = ? and TIMESTAMPDIFF(MINUTE, expirationdate,now()) <= ?";
         params.add(tokenID);
         params.add(Integer.toString(timeDuration));
         
         try{          
            res = db.getDataPS(sql, params);
            isValid = (res != null)?true:false;
              
            if(! isValid){
               isError = true;
               errorMsg = "Error: Invalid token or the token has expired";
            }
                                   
         }
         catch(SQLException e)
         {
            isError = true;
            errorMsg = "Error: SQLException occurred";
         }           
      }
      else{
         isError = true;
         errorMsg = "Error: Null reference to a database access object"; 
      }
            
      return isValid;        
   }

   //Deletes token for a user if it already exists irrespective of whether it has expired or not
   public void deleteTokenIDForUser(int userID){   
       
      ArrayList<String> params = new ArrayList<String>();
                                        
      // check db access 
      if(db != null){
      
         String sql = "Delete from token where userid = ?";
         params.add(Integer.toString(userID));
                 
         try{          
            db.nonSelect(sql, params);                                                                          
         }
         catch(SQLException e)
         {
            isError = true;
            errorMsg = "Error: SQLException occurred";
         }           
      }
      else{
         isError = true;
         errorMsg = "Error: Null reference to a database access object"; 
      }                        
   }



  // Returns true if there is an error                     
   public boolean checkError(){
      return this.isError;
   }
  
  //// Returnserror message for a given error
   public String getErrorMessage(){
      return (this.isError)?this.errorMsg:"";
   }

}

