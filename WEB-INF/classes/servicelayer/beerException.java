package servicelayer;

public class beerException extends Exception{

   private String errorDetails;
   
   public beerException(String reason, String errorDetails){
      super(reason);
      this.errorDetails = errorDetails;
   
   }

   public String getFaultInfo(){
      return errorDetails;   
   }
 }
