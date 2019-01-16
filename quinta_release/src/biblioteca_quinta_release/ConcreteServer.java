
package biblioteca_quinta_release;

/**
 * 
 * Questa classe si occupa di fare da interfaccia generale per accedere ad uno
 * specifico database locale, per poter creare tabelle, ed effettuare query di
 * ricerca ed modificare e inserire records.
 *
 * @author Sviluppatore
 *
 */



import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import org.sqlite.SQLiteConfig;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.StringTokenizer;



public class ConcreteServer 
{
    private Connection conn;
    
    protected String getSaltString() 
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
    
    protected boolean isConnected() throws SQLException
    {
        return !conn.isClosed();
    }
    public ConcreteServer()
    {
        
        try {
            this.conn = this.connectServer("");
        } catch (SQLException e) {
            Menu.printRed("fail from ConcreteServer connection: " + e.getMessage());
        }
        // SQL statement for creating a new table
        String sql = 
                "CREATE TABLE IF NOT EXISTS utente (\n"
                + "     id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "     name text NOT NULL,\n"
                + "     surname text NOT NULL\n"
                + ");";
        String sql2 =                
                "CREATE TABLE IF NOT EXISTS person (\n" +
                "name text NOT NULL,\n" +
                "surname text NOT NULL,\n" +
                "password text NOT NULL,\n" +
                "username text PRIMARY KEY,\n" +
                "is_active boolean\n" +
                "\n" +
                ");";
                
                ;
        String sql3 =   
                "CREATE TABLE IF NOT EXISTS is_user (\n" +
                "\n" +
                "id_person text NOT NULL,\n" +
                "FOREIGN KEY (id_person) references person(username)\n" +
                "\n" +
                ");";
        String sql4 =   
                "CREATE TABLE IF NOT EXISTS is_operator (\n" +
                "\n" +
                "id_person text NOT NULL,\n" +
                "is_admin boolean NOT NULL,\n" +
                "FOREIGN KEY (id_person) references person(username)\n" +
                "\n" +
                ");";
        String sql5 =   
                "CREATE TABLE IF NOT EXISTS subscription (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "id_person text NOT NULL,\n" +  
                "start_date text NOT NULL,\n" +
                "end_date text NOT NULL,\n" +
                "is_renewed boolean NOT NULL,\n" +
                "is_active boolean DEFAULT 1,\n" +
                "FOREIGN KEY (id_person) references person(username)\n" +
                "\n" +
                ");";
        String sql6 =   
                "CREATE TABLE IF NOT EXISTS renew_subscription (\n" +
                "\n" +
                "id_sub INTEGER,\n" + 
                "start_date text NOT NULL,\n" +
                "end_date text NOT NULL,\n" +
                "is_renewed boolean NOT NULL,\n" +
                "is_active boolean NOT NULL,\n" +
                "FOREIGN KEY (id_sub) references subscription(id)\n" +
                "\n" +
                ");";
        String sql7 = 
                "CREATE TABLE IF NOT EXISTS person (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "name text NOT NULL,\n" +
                "surname text NOT NULL,\n" +
                "username text NOT NULL,\n" +
                "is_active boolean NOT NULL\n" +
                "\n" +
                ");";
        
        String sqlCategory =
                "CREATE TABLE IF NOT EXISTS category (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "identifier text NOT NULL UNIQUE,\n" +
                "name text NOT NULL,\n" +
                "description text,\n" +
                "parent text,\n"+
                "is_subcategory boolean NOT NULL,\n" +
                "loan_duration text NOT NULL,\n" +
                "renew_duration text NOT NULL,\n" +
                "days_until_renew text NOT NULL,\n" +
                "max_loans_to_person text \n" +
                //"is_active boolean NOT NULL\n" +
                "\n" +
                ");";
        String sqlBook =
                "CREATE TABLE IF NOT EXISTS book (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "identifier text NOT NULL UNIQUE,\n" +
                "publication_year text,\n" +
                "title text,\n" +
                "description text,\n" +
                //gli autori saranno separati da virgola
                "authors text,\n" +
                "language text,\n" +
                "publisher text,\n" +
                "genre text,\n" +
                "page_number text,\n" +
                "license_number integer,\n" +
                "is_avaiable boolean,\n" +
                "is_active boolean,\n" +
                "category_ident text\n" +
                "\n" +
                ");";
        
        String sqlBookLoan =   
                "CREATE TABLE IF NOT EXISTS loan (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "id_person text NOT NULL,\n" +  
                "id_book text NOT NULL,\n" +
                "start_date text NOT NULL,\n" +
                "end_date text NOT NULL,\n" +
                "quit_loan_date text,\n" +
                "is_renewed boolean NOT NULL,\n" +
                "is_active boolean DEFAULT 1,\n" +
                "FOREIGN KEY (id_book) references book(identifier),\n" +
                "FOREIGN KEY (id_person) references person(username)\n" +
                "\n" +
                ");";
        
        String sqlRenewLoan =   
                "CREATE TABLE IF NOT EXISTS renew_loan (\n" +
                "\n" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "id_loan integer NOT NULL,\n" +               
                "id_book text NULL,\n" +
                "start_date text NOT NULL,\n" +
                "end_date text NOT NULL,\n" +
                "quit_loan_date text,\n" +
                "is_renewed boolean NULL,\n" +
                "is_active boolean NOT NULL,\n" +
                "FOREIGN KEY (id_book) references book(identifier),\n" +
                "FOREIGN KEY (id_loan) references loan(id)" +
                "\n" +
                ");";
        String sqlFilm
                = "CREATE TABLE IF NOT EXISTS film (\n"
                + "\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "identifier text NOT NULL UNIQUE,\n"
                + "production_year text,\n"
                + "title text,\n"
                + "description text,\n"
                //gli autori saranno separati da virgola
                + "actors text,\n"
                + "film_maker,\n"
                + "language text,\n"
                + "publisher text,\n"
                + "genre text,\n"
                + "duration text,\n"
                + "license_number integer,\n"
                + "is_avaiable boolean,\n"
                + "is_active boolean,\n"
                + "category_ident text\n"
                + "\n"
                + ");";   
        String sqlFilmLoan
                = "CREATE TABLE IF NOT EXISTS film_loan (\n"
                + "\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "id_person text NOT NULL,\n"
                + "id_film text NULL,\n"
                + "start_date text NOT NULL,\n"
                + "end_date text NOT NULL,\n"
                + "quit_loan_date text,\n"
                + "is_renewed boolean NOT NULL,\n"
                + "is_active boolean DEFAULT 1,\n"
                + "FOREIGN KEY (id_film) references film(identifier),\n"
                + "FOREIGN KEY (id_person) references person(username)\n"
                + "\n"
                + ");";

        String sqlRenewFilm
                = "CREATE TABLE IF NOT EXISTS renew_film_loan (\n"
                + "\n"
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "id_loan integer NOT NULL,\n"
                + "id_film text NOT NULL,\n"
                + "start_date text NOT NULL,\n"
                + "end_date text NOT NULL,\n"
                + "quit_loan_date text,\n"
                + "is_renewed boolean NULL,\n"
                + "is_active boolean NOT NULL,\n"
                + "FOREIGN KEY (id_film) references film(identifier),\n"
                + "FOREIGN KEY (id_loan) references loan(id)"
                + "\n"
                + ");";
        try 
        {
            //this.execute(sql); utente
            //this.execute(sql7);
            this.execute(sql2);
            this.execute(sql3);
            this.execute(sql4);
            this.execute(sql5);
            this.execute(sql6);
            this.execute(sqlCategory);
            this.execute(sqlBook);
            this.execute(sqlBookLoan);
            this.execute(sqlRenewLoan);
            this.execute(sqlFilm);
            this.execute(sqlFilmLoan);
            this.execute(sqlRenewFilm);
            
        } catch (SQLException ex) {
            Menu.printRed("fail from ConcreteServer: " + ex.getMessage() + 
                    " " + ex.getSQLState() + " " + ex.getSQLState());
        }
    }
    
    
    /************DEPRECATED, ma parametro PRAGMA importante!***********
     * @param address
     * @return 
     * @throws java.sql.SQLException
     * @deprecated
    public static Connection connect() {
        //Properties properties = new Properties();
        //properties.setProperty("PRAGMA foreign_keys", "ON");
        
        SQLiteConfig config = new SQLiteConfig();  
        config.enforceForeignKeys(true);  
        // SQLite connection string
        String url = "jdbc:sqlite:C://Users/Sviluppatore/Documents/"
                + "NetBeansProjects/biblioteca_prima_release/test.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,config.toProperties());
        } catch (SQLException e) {
            
            print(e.getMessage());
        }
        //Statement stat = conn.createStatement();
        
        return conn;
    }
    */

    
    public Connection connectServer(String address) throws SQLException
    {
        
        String dir = System.getProperty("user.dir");
        dir = dir + "/database.db";
        Menu.printDebug("current dir = " + dir);
        
        //initialize connection
        SQLiteConfig config = new SQLiteConfig();  
        //activate the foreign key feature of the databases
        config.enforceForeignKeys(true);  
        // SQLite database's path connection string
        String url = "jdbc:sqlite:" + dir;
        Connection conn = null;
        conn = DriverManager.getConnection(url,config.toProperties());
        Menu.printDebug("conn address: " + conn.toString());
        
        
        return conn;
    }     
    public Result execute(String command) throws SQLException
    {
        ResultSet result = null;
        String check = "";
        StringTokenizer st = new StringTokenizer(command);
        //precondition
        if(!command.isEmpty())
            check = st.nextToken();
        Menu.printDebug("command: " + check);
        //Menu.printGreen(command);
         
         if(check.equalsIgnoreCase("insert"))
         {
            Menu.printPurple("\ninsert command : " + command + " |END");
            String sql = command;
            PreparedStatement pstmt = conn.prepareStatement(sql); 
            //pstmt.set.setString(1, name);
            //pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
            //result will be NULL
            return new Result(result);

         }
         if(check.equalsIgnoreCase("select"))
         {
             result = select(command);
             
             Menu.printPurple("\nselect command : " + command + " |END");
             Menu.printDebug("risultato: " + result);
             return new Result(result);
             
         }
         if(check.equalsIgnoreCase("update"))
         {
             update(command);
             
             Menu.printPurple("\nupdate command : " + command + " |END");
             Menu.printDebug("risultato: " + result);
             //return new Result(result);
             
             //si puo' migliorare rendendola come INSERT
             
         }
         if(check.equalsIgnoreCase("create"))
             Menu.printDebug("\ncreate command : " + command + " |END");
     
        
        String sql = command;
        PreparedStatement pstmt = conn.prepareStatement(sql); 
        //pstmt.set.setString(1, name);
        //pstmt.setDouble(2, capacity);
        pstmt.executeUpdate();        
        pstmt.close();
        return new Result(result);
        
    }
    
    public ResultSet select(String command) throws SQLException
    {
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(command);
        //NON inserire stmt.close();
        return rs;
    }
    /**
     * Questo metodo e' da chiamare per effettuare update,
     * in quanto executeQuery funziona correttamente solo per query,
     * ovvero SELECT
     * @param command
     * @return
     * @throws SQLException 
     */
    
    public Result update(String command) throws SQLException
    {
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(command);
        stmt.close();

        return new Result(rs);
    }
    public Result select2(String command) throws SQLException
    {
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(command);

        return new Result(rs);
    }
    private ResultSet executeInsert(String command) throws SQLException
    {
        ResultSet rs = null;

         Statement stmt = conn.createStatement();
        rs = stmt.executeQuery(command);
        
        return rs;
    }
    private ResultSet create(String command) throws SQLException
    {
        ResultSet result = null;
        
        Statement stmt = conn.createStatement();
        // create a new table
        stmt.execute(command);

        return result;
    }
    public void insertDumb() throws SQLException
    {
        String sql = "INSERT INTO person(name,surname) VALUES(?,?)";
 
        String sql2 = "INSERT INTO person(name,surname,password,username) VALUES('" 
                + getSaltString() + "', '" + getSaltString() + "', '" 
                + getSaltString() + "', '" +  getSaltString() + "')"
                ;

        this.execute(sql2);
    }
    public boolean close()
    {
        try {
            if(!conn.isClosed())
                conn.close();
            
        } catch (SQLException ex) {
            Menu.printDebug("not closed-> fail on close connection: " + ex.getMessage());
            return false;
        }
        return true;
    }
}
