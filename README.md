# BeerService
Java SOAP Service 

Use Java to create a SOAP service named BeerService.  You should include code for a service layer, a business layer, and a data layer. You will need to create a BeerPrices MySQL database that has three tables: a beers table with the beer names and prices, a users table that will have username, password, age and access level and a token table that will have a token, a foreign key to a user and a token expiration date. Note: you will need to put the database driver jar file into your glassfish/lib directory.

Requirements
Services
Method Signature	Method Description
public String getToken(String username,String Password)	Takes a string denoting the username, a string denoting a password and returns a "token" string if the username and password match.
public Vector getMethods()	Takes no arguments and returns a list of the methods contained in the service.
public Double getPrice(String beer, string token)	Takes a string denoting the beer brand and a string denoting a token and returns a double representing the beer price if the token is valid.
public Boolean setPrice(String beer, Double price, string token)	Takes a string denoting the beer brand, a double denoting the price and a string denoting a token returns true or false depending on success and if the token is valid.
public Vector getBeers(string token)	Takes a string denoting a token and returns a list of the known beers if the token is valid.
public String getCheapest(string token)	Takes a string denoting a token and returns the name of the least expensive beer if the token is valid.
public String getCostliest(string token)	Takes a string denoting a token and returns the name of the most expensive beer if the token is valid.
Data
Beer	Price
Budweiser	$10.49
Coors	$9.99
Corona	$13.49
Genesee	$5.99
Guiness	$14.99
Labatt	$8.99
Sam Adams	$13.99
 

Business Rules
Anyone can use the getMethods operation
A valid token can only be used once and expires 5 minutes after it was created (you may have to extend the time if performance getting the token and making the follow-up request is an issue). You will use the token to make sure that the user presenting the token is authorized to perform the requested operation. You need to delete the token after it has expired. The token should be random enough so that it cannot be predicted. 
No one under the age of 21 may use the other operations
No operations will work between midnight and 10 am.
Only "Admin" can use the setPrice operation
You must have a user in your database with username "test" and password "testing"
