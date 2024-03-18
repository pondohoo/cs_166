/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Amazon {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Amazon store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Amazon

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Amazon.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Amazon esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Amazon object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Amazon (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
                System.out.println("10. View All Orders");

                //the following functionalities basically used by admins
                System.out.println("11. View all users");
                System.out.println("12. Update user");
                System.out.println("13. Delete user");



                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql,authorisedUser); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql,authorisedUser); break;
                   case 4: viewRecentOrders(esql,authorisedUser); break;
                   case 5: updateProduct(esql, authorisedUser); break;
                   case 6: viewRecentUpdates(esql,authorisedUser); break;
                   case 7: viewPopularProducts(esql,authorisedUser); break;
                   case 8: viewPopularCustomers(esql, authorisedUser); break;
                   case 9: placeProductSupplyRequests(esql, authorisedUser); break;
                   case 10: viewAllOrders(esql, authorisedUser); break;
                   case 11: viewAllUsers(esql, authorisedUser); break;
                   case 12: updateUser(esql, authorisedUser); break;
                   case 13: deleteUser(esql, authorisedUser); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice
   public static String readStringChoice() {
      String input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = in.readLine();
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT userID FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         List<List<String>> userId = esql.executeQueryAndReturnResult(query);
	 if (userId.size() > 0)
		return userId.get(0).get(0);
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Amazon esql, String authorisedUser) {
      try {
         List<List<String>> userCoords = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Users WHERE userID = %s;", authorisedUser));
         double userLat = Double.parseDouble(userCoords.get(0).get(0));
         double userLong = Double.parseDouble(userCoords.get(0).get(1));
         double maximumDistance = 30.0; 
         boolean found = false;
         List<List<String>> storeCoords = esql.executeQueryAndReturnResult(String.format("SELECT storeID, latitude, longitude FROM Store;"));
         System.out.println("Stores within 30 miles: ");
         for (int i = 0; i < storeCoords.size(); i++){
            double storeLat = Double.parseDouble(storeCoords.get(i).get(1));
            double storeLong = Double.parseDouble(storeCoords.get(i).get(2));
            double distance = esql.calculateDistance(userLat, userLong, storeLat, storeLong);
            if (distance <= maximumDistance){
               System.out.println("🔸 Store ID: " + storeCoords.get(i).get(0) + " Latitude: " + storeCoords.get(i).get(1) + " Longitude: " + storeCoords.get(i).get(2));
               found = true;
            }
         }
         if (!found){
            System.out.println("❌ No stores found");
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewProducts(Amazon esql) {
      try {
         System.out.println("Enter the storeID of a store to view its products");
         int storeId = readChoice();
         List<List<String>> storeProducts = esql.executeQueryAndReturnResult(String.format("SELECT productName, numberOfUnits, pricePerUnit FROM Product WHERE storeID = %d;", storeId));
         if (storeProducts.size() == 0){
            System.out.println("❌ No products found for storeID " + storeId);
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i < storeProducts.size(); ++i)
         {
            System.out.println("Product Name: " + storeProducts.get(i).get(0) + " Number of Units: " + storeProducts.get(i).get(1) + " Price per Unit: " + storeProducts.get(i).get(2));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void placeOrder(Amazon esql, String authorisedUser) {
      try {
         System.out.println("Enter the storeID of a store to order a product from");
         int storeId = readChoice();


         // check if user is within 30 miles of store
         List<List<String>> userCoords = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Users WHERE userID = '%s';", authorisedUser));
         double userLat = Double.parseDouble(userCoords.get(0).get(0));
         double userLong = Double.parseDouble(userCoords.get(0).get(1));
         double maximumDistance = 30.0; // 1 degree latitude is 69 miles
         List<List<String>> storeCoords = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM Store WHERE storeID = %d;", storeId));
         if (storeCoords.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         }
         double storeLat = Double.parseDouble(storeCoords.get(0).get(0));
         double storeLong = Double.parseDouble(storeCoords.get(0).get(1));
         double distance = esql.calculateDistance(userLat, userLong, storeLat, storeLong);
         if (distance > maximumDistance){
            System.out.println("❌ You are not within 30 miles of storeID " + storeId);
            return;
         }

         System.out.println("Enter the product name of a product to order");
         String productName = readStringChoice();

         // check if product is in store
         List<List<String>> productInfo = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Product WHERE storeID = %d AND productName = '%s';", storeId, productName));
         if (productInfo.size() == 0){
            System.out.println("❌ Product " + productName + " does not exist in storeID " + storeId);
            return;
         }


         System.out.println("Enter the number of units to order");
         int numberOfUnits = readChoice();
         // check if there are enough units in store
          if (numberOfUnits > Integer.parseInt(productInfo.get(0).get(2))){
             System.out.println("❌ StoreID " + storeId + " only has " + productInfo.get(0).get(2) + " units of " + productName);
             return;
          }



         // update order table
         String query = String.format("INSERT INTO Orders (customerId, storeID, productName, unitsOrdered, orderTime) VALUES ('%s', %d, '%s', %d, CURRENT_TIMESTAMP)", authorisedUser, storeId, productName, numberOfUnits);
         esql.executeUpdate(query);

         // update product table
         // query = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - %d WHERE storeID = %d AND productName = '%s'", numberOfUnits, storeId, productName);
         // esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println (e.getMessage());
         return;
      }
      System.out.println("✅ Order Placed");

   }
   public static void viewRecentOrders(Amazon esql, String authorisedUser) {
      try {
         List<List<String>> recentOrders = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Orders WHERE customerID = '%s' ORDER BY orderTime DESC LIMIT 5;", authorisedUser));
         if (recentOrders.size() == 0){
            System.out.println("❌ No recent orders found");
            return;
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i < recentOrders.size(); ++i)
         {
            System.out.println("Order ID: " + recentOrders.get(i).get(0) + " Customer ID: " + recentOrders.get(i).get(1) + " Store ID: " + recentOrders.get(i).get(2) + " Product Name: " + recentOrders.get(i).get(3) + " Order Time: " + recentOrders.get(i).get(4) + " Number of Units: " + recentOrders.get(i).get(5));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void updateProduct(Amazon esql, String authorisedUser) {
      try{
         System.out.println("Enter the storeID of a store to update a product from");
         int storeId = readChoice();
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Store WHERE storeID = %d;", storeId));
         if (store.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         }
            if(checkManagerPermission(esql, authorisedUser))
            {  
               if (!store.get(0).get(3).equals(authorisedUser)){
                  System.out.println("❌ You must be the manager of store " + storeId + " to update products");
                  return;
               }
            }
            else
            {
               return;
            }
         
         System.out.println("Enter the product name of a product to update");
         String productName = readStringChoice();
         List<List<String>> productInfo = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Product WHERE storeID = %d AND productName = '%s';", storeId, productName));
         if (productInfo.size() == 0){
            System.out.println("❌ Product " + productName + " does not exist in storeID " + storeId);
            return;
         }
         System.out.println("Enter the new number of units");
         int numberOfUnits = readChoice();
         System.out.println("Enter the new price per unit");
         int pricePerUnit = readChoice();
         String query = String.format("UPDATE Product SET numberOfUnits = %d, pricePerUnit = %d WHERE storeID = %d AND productName = '%s'", numberOfUnits, pricePerUnit, storeId, productName);
         esql.executeUpdate(query);
         query = String.format("INSERT INTO ProductUpdates (managerID, storeID, productName, updatedOn) VALUES ('%s', %d, '%s', CURRENT_TIMESTAMP)", authorisedUser, storeId, productName);
         esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println(e.getMessage());
         return;
      }
      System.out.println("✅ Product Updated");
   }
   public static void viewRecentUpdates(Amazon esql, String authorisedUser) {
      try {
         System.out.println("Enter the storeID of a store to view Product Updates Info from");
         int storeId = readChoice();
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Store WHERE storeID = %d;", storeId));
         if (store.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         }
         if(checkManagerPermission(esql, authorisedUser))
            {  
               if (!store.get(0).get(3).equals(authorisedUser)){
                  System.out.println("❌ You must be the manager of store " + storeId + " to view Product Info updates");
                  return;
               }
            }
            else
            {
               return;
            }
         List<List<String>> recentUpdates = esql.executeQueryAndReturnResult(String.format("SELECT * FROM ProductUpdates WHERE storeID = '%s' ORDER BY updatedOn DESC LIMIT 5;", storeId));
         if (recentUpdates.size() == 0){
            System.out.println("❌ No recent updates found");
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i < recentUpdates.size(); ++i)
         {
            System.out.println("Update Number: " + recentUpdates.get(i).get(0) + " Manager ID: " + recentUpdates.get(i).get(1) + " Store ID: " + recentUpdates.get(i).get(2) + " Product Name: " + recentUpdates.get(i).get(3) + " Updated On: " + recentUpdates.get(i).get(4));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
         return;
      }
   }
   public static void viewPopularProducts(Amazon esql, String authorisedUser) {
      try {
         System.out.println("Enter the storeID of a store to view popular products from");
         int storeId = readChoice();
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Store WHERE storeID = %d;", storeId));
         if (store.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         } 
         if(checkManagerPermission(esql, authorisedUser))
            {  
               if (!store.get(0).get(3).equals(authorisedUser)){
                  System.out.println("❌ You must be the manager of store " + storeId + " to view popular products");
                  return;
               }
            }
            else
            {
               return;
            }
         List<List<String>> popularProducts = esql.executeQueryAndReturnResult(String.format("SELECT productName, SUM(unitsOrdered) FROM Orders WHERE storeID = %d GROUP BY productName ORDER BY SUM(unitsOrdered) DESC LIMIT 5;", storeId));
         System.out.println(popularProducts);
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i<popularProducts.size(); ++i)
         {
            System.out.println(String.format(i+1 + ": " + "Product Name: %s Units ordered: %s", popularProducts.get(i).get(0), popularProducts.get(i).get(1)));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void viewPopularCustomers(Amazon esql, String authorisedUser) {
      try {
         System.out.println("Enter the storeID of a store to view popular customers from");
         int storeId = readChoice();
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Store WHERE storeID = %d;", storeId));
         if (store.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         } 
         if(checkManagerPermission(esql, authorisedUser))
            {  
               if (!store.get(0).get(3).equals(authorisedUser)){
                  System.out.println("❌ You must be the manager of store " + storeId + " to view popular customers");
                  return;
               }
            }
            else
            {
               return;
            }
         List<List<String>> popularCustomers = esql.executeQueryAndReturnResult(String.format("SELECT customerID, COUNT(*) FROM Orders WHERE storeID = %d GROUP BY customerID ORDER BY COUNT(*) DESC LIMIT 5;", storeId));
         
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i<popularCustomers.size(); ++i)
         {
            System.out.println(String.format(i+1 + ": " + "Customer ID: %s Number of orders: %s", popularCustomers.get(i).get(0), popularCustomers.get(i).get(1)));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
   }
   public static void placeProductSupplyRequests(Amazon esql, String authorisedUser) {
      try {
         System.out.println("Enter the storeID of a store to supply a product request to");
         int storeId = readChoice();
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM Store WHERE storeID = %d;", storeId));
         if (store.size() == 0){
            System.out.println("❌ StoreID " + storeId + " does not exist");
            return;
         } 
         if(checkManagerPermission(esql, authorisedUser))
            {  
               if (!store.get(0).get(3).equals(authorisedUser)){
                  System.out.println("❌ You must be the manager of store " + storeId + " to supply a product request");
                  return;
               }
            }
            else
            {
               return;
            }
         System.out.println("Enter the product name of a product to request");
         String productName = readStringChoice();
         System.out.println("Enter the number of units to request");
         int numberOfUnits = readChoice();
         System.out.println("Enter the warehouseID of the warehouse to request from");
         int warehouseId = readChoice();

         String checkProduct = String.format("SELECT EXISTS(SELECT * FROM product WHERE productname='%s');", productName);
         List<List<String>> productExists = esql.executeQueryAndReturnResult(checkProduct);
         if (productExists.get(0).get(0).equals("f"))
         {
            System.out.println("❌ product doesn't exist");
            return;
         }
         String checkWarehouse = String.format("SELECT EXISTS(SELECT * FROM warehouse WHERE warehouseid='%d');", warehouseId);
         List<List<String>> warehouseExists = esql.executeQueryAndReturnResult(checkWarehouse);
         if (warehouseExists.get(0).get(0).equals("f"))
         {
            System.out.println("❌ warehouse doesn't exist");
            return;
         }
         String updateQuery = String.format("INSERT INTO ProductSupplyRequests (managerID, warehouseID, storeID, productName, unitsRequested) VALUES (%s, %d, %d, '%s', %d)", authorisedUser, warehouseId, storeId, productName, numberOfUnits);
         esql.executeUpdate(updateQuery);
         updateQuery = String.format("UPDATE Product SET numberOfUnits = numberOfUnits + %d WHERE storeID = %d AND productName = '%s'", numberOfUnits, storeId, productName);
         esql.executeUpdate(updateQuery);
      } catch(Exception e){
         System.err.println ("❌"+e.getMessage());
         return;
      }
      System.out.println("✅ product request placed!!!");
   }
   public static boolean checkAdminPermission(Amazon esql, String authorisedUser)
   {
      String checkTypeQuery = "SELECT type FROM users WHERE userid = "+authorisedUser;
      try {
         List<List<String>> type = esql.executeQueryAndReturnResult(checkTypeQuery);
         if (type.size() == 0){
            System.out.println("❌ No users found");
            return false;
         }
         for (int i = 0; i < type.size(); ++i)
         {

            if(!type.get(i).get(0).contains("admin"))
            {
               System.out.println("❌ You need admin permission for this action");
               return false;
            }
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage());
         return false;
      }
      return true;
   }
   
   public static boolean checkManagerPermission(Amazon esql, String authorisedUser)
   {
      String checkTypeQuery = "SELECT type FROM users WHERE userid = "+authorisedUser;
      try {
         List<List<String>> type = esql.executeQueryAndReturnResult(checkTypeQuery);
         if (type.size() == 0){
            System.out.println("❌ No users found");
            return false;
         }
         for (int i = 0; i < type.size(); ++i)
         {

            if(!type.get(i).get(0).contains("manager"))
            {
               System.out.println("❌ You need manager permission for this action");
               return false;
            }
         }
      }
      catch(Exception e){
         System.err.println (e.getMessage());
         return false;
      }
      return true;
   }
   public static void viewAllOrders(Amazon esql, String authorisedUser)
   {
      if(!checkManagerPermission(esql, authorisedUser)) return;
      String viewOrderQuery = "SELECT o.ordernumber, u.name, s.storeid, o.productname, o.ordertime, o.unitsordered FROM orders o, store s, users u WHERE s.managerid = "+authorisedUser+" AND s.storeid = o.storeid AND u.userid = o.customerid;";
      try {
         List<List<String>> orders = esql.executeQueryAndReturnResult(viewOrderQuery);
         if (orders.size() == 0){
            System.out.println("❌ No orders found");
            return;
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i < orders.size(); ++i)
         {
            System.out.println("Order ID: " + orders.get(i).get(0) + " Customer Name: " + orders.get(i).get(1) + " Store ID: " + orders.get(i).get(2) + " Product Name: " + orders.get(i).get(3) + " Order Time: " + orders.get(i).get(4) + " Number of Units: " + orders.get(i).get(5));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
      return ;
   }
   public static void viewAllUsers(Amazon esql, String authorisedUser)
   {
      if(!checkAdminPermission( esql, authorisedUser))
         return;
      String viewUserQuery = "SELECT * FROM users;";
      try {
         List<List<String>> users = esql.executeQueryAndReturnResult(viewUserQuery);
         if (users.size() == 0){
            System.out.println("❌ No users found");
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
         for (int i = 0; i < users.size(); ++i)
         {
            System.out.println(String.format("User ID: "+users.get(i).get(0)+" \tName: "+users.get(i).get(1)+"\tlatitude: "+users.get(i).get(3)+"\tlongitude"+users.get(i).get(4)+"\ttype: "+users.get(i).get(5)));
         }
         System.out.println("⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️");
      }
      catch(Exception e){
         System.err.println (e.getMessage());
      }
      return ;
   }
   public static boolean checkUserExist(Amazon esql, String authorisedUserm, int userid)
   {  try{
         List<List<String>> store = esql.executeQueryAndReturnResult(String.format("SELECT * FROM users WHERE userid = %d;", userid));
         if (store.size() == 0){
            System.out.println("❌ user not found");
            return false;
         }
      }
      catch(Exception e){
         System.err.println(e.getMessage());
         return false;
      }
      return true;
   }
   public static void updateUser(Amazon esql, String authorisedUser) {
      if(!checkAdminPermission( esql, authorisedUser))
         return;
      try{
         System.out.println("Enter the userid");
         int userid = readChoice();
         if(!checkUserExist(esql, authorisedUser, userid)) return;
         System.out.println("Enter the new name of this user");
         String name = in.readLine();
         System.out.println("Enter the new latitude");
         String latitude = in.readLine();
         System.out.println("Enter the new longitude");
         String longitude = in.readLine();
         System.out.println("Enter the new type");
         String type = in.readLine();
         String query = String.format("UPDATE users SET name = '%s', latitude = %s, longitude = %s, type = '%s' WHERE userid = %s", name, latitude, longitude, type, userid);
         esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println(e.getMessage());
         return;
      }
      System.out.println("✅ user updated!!!");
   }
       
  
   public static void deleteUser(Amazon esql, String authorisedUser) {
      if(!checkAdminPermission( esql, authorisedUser))
         return;
      try{
         System.out.println("Enter the userid");
         int userid = readChoice();
         if(!checkUserExist(esql, authorisedUser, userid)) return;

         String query = String.format("DELETE FROM users WHERE userid = %s", userid);
         esql.executeUpdate(query);
      }
      catch(Exception e){
         System.err.println(e.getMessage());
         return;
      }
      System.out.println("✅ user deleted!!!");
   }

}//end Amazon

