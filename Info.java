package biblioteca_seconda_release;



/**
 * 
 * @author Sviluppatore
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Info {

    boolean result_connection,result_closing;	
    String name;
    String surname;
    String username;
    String password;
    String giorno;
    String mese;
    String anno;
    String anno_finale;
    String start_date;
    String end_date;

    Vector <String> query = new Vector<String>();
    Vector <String> risultato_select = new Vector<String>();
    Result result = new Result();
    ConcreteServer server; 
    GregorianCalendar gc = new GregorianCalendar();
    GregorianCalendar today = new GregorianCalendar();
    SimpleDateFormat sdf;
    ResultSet rs;
    
    //Contenitore delle categorie
    File file_categorie;
    
    

    // INVARIANTE server != null
    public Info(ConcreteServer _server){
            server =_server;

            //sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            sdf = new SimpleDateFormat("dd/MM/yyyy");
            
        try {
            file_categorie = this.getCategoriesHandler();
        } catch (FileNotFoundException ex) {
            file_categorie = null;
            Logger.getLogger(Info.class.getName()).log(Level.SEVERE, null, ex);
            
        }

    }

    /**
     * Ritorna il file File, riferimento a "categories.txt"
     * 
     * 
     */
    private File getCategoriesHandler() throws FileNotFoundException
    {
        
    // pass the path to the file as a parameter 
    File file = new File("categories.txt"); 
    Scanner sc = new Scanner(file);
    Menu.printPurple("Lettura da file: ");
    while (sc.hasNextLine()) 
      Menu.printPurple(sc.nextLine()); 
    Menu.printPurple("__________________");
    
    return file;
    }
    /**
     * Ritorna la lista delle categorie e sottocategorie, presenti su file.
     * @return
     * @throws FileNotFoundException 
     */
    public ArrayList<Category> getCategoriesFromFile() throws FileNotFoundException
    {
        ArrayList<Category> categorie = null;
        if(this.file_categorie != null)
        {
            categorie = new ArrayList();
            Scanner sc = new Scanner(file_categorie);
            boolean sottocategoria = false;
            
            while (sc.hasNextLine()) 
            {
                String line = sc.nextLine();
                StringTokenizer st = new StringTokenizer(line," ");
                Menu.printGreen("tokeni: " + st.countTokens());
                if(st.countTokens() == 5)
                {
                    String categoria = st.nextToken();
                    String nome = st.nextToken();
                    String has_sub = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    categorie.add(new Category(nome));
                }
                if(st.countTokens() == 6)
                {
                    String sub_categoria = st.nextToken();
                    String nome = st.nextToken();
                    String categoria_padre = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    st.nextToken();
                    for(int i = 0; i < categorie.size(); i++)
                    {
                        if(categorie.get(i).getName().equals(categoria_padre))
                            categorie.get(i).addSubcategory(nome);
                    }
                }
                
            }
            
        }
        return categorie;
    }
    public boolean login (User _user) throws SQLException{
            if(_user == null) 
                    throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!"); 

            else
            {
                username = _user.getUsername();
                password = _user.getPassword();

                query.add("SELECT username" +
                          " FROM person" +
                          " WHERE username = '" + username + "' AND" + " password = '" + password +"'");
                Menu.printPurple(query.firstElement());

                if(!server.isConnected())
                {

                    query.removeAllElements();
                    Menu.print("ERRORE DI CONNESSIONE IN LOGIN");
                    return false; // ritorna FALSE se non e' connesso, chi 
                                      // ATTENZIONE !!chiama questo metodo e riceve false non sa se si 
                                    //tratta di un errore di connessione o di un login falso
                }

                try
                {
                        result = server.execute(query.get(0));
                }
                catch (SQLException e)
                {

                    query.removeAllElements();
                    System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    return false ; //ATTENZIONE !!! riceve false ma non sa se si tratta di un errore
                                              // o di login falso
                }

                rs = (ResultSet)result.getData();

                if(rs == null){

                        query.removeAllElements();
                        throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                }

                if(rs.next()!=false){ 

                        query.removeAllElements();
                        return true;  // se rs contiene tuple allora le credenziali
                                                 //  sono giuste e ritorna true
                }

                query.removeAllElements();
                return false;
            }
    }

    public boolean addOperator(User _user) throws SQLException
    {

        if(_user == null) 
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!"); 

        else
        {
            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            password = _user.getPassword();

            query.add("SELECT username " +
                            "FROM person " +
                            "WHERE username = '" + username +"'");

            if(!server.isConnected())
            {

                    query.removeAllElements();
                    Menu.print("ERRORE DI CONNESSIONE IN ADDUSER");
                    return false; // ritorna false se non e' connesso,
                                              // chi chiama questo metodo deve controllare se riceve false
            }


            try
            {

                    result = server.execute(query.get(0));
            }
            catch (SQLException e)
            {

                    query.removeAllElements();
                    Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                    return false ;
            }

            rs = (ResultSet)result.getData();

            if(rs==null)
            {

                    query.removeAllElements();
                    throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if(rs.next()!=false)
            { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                    query.removeAllElements();
                    throw new IllegalArgumentException("ATTENTION PLEASE! Username gia' esistente!");
            }

            query.add("INSERT INTO person (name,surname,username,password,is_active) VALUES (" +
                            "'" + name + "'" 
                            + "," 
                            + "'" + surname + "'" 
                            + "," 
                            + "'" + username + "'" 
                            + ","
                            + "'" + password + "'" 
                            + ",1" 
                            + ")"); // ATTENZIONE !!! L'HO MESSO SUBITO ATTIVO
            query.add("INSERT INTO is_operator (id_person,is_admin) VALUES " 
                            + "('" + username + "'," + 0 + ")");

                            // mettere al posto di 2 una costante che indica il numero di query,la i parte da 1 perche' devo saltare la prima query
                            // ATTENZIONE !!! gestire il fatto che se va a buon fine la prima query ma va a puttane la seconda query c'e' la riga person che non ha la corrispondeza con la riga della tabella is_user
            for(int i=0; i < query.size(); i++)
            {
                    try{

                            result = server.execute(query.get(i));	
                    }
                    catch (SQLException e){

                            query.removeAllElements();

                            Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                            Menu.print(e.getMessage());
                            return false;
                    }
            }

            query.removeAllElements();

            return true;
        }

    }

    public boolean isUser(User _user) throws SQLException
    {

            if(_user==null) 
                    throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!"); 

            else
            {

                    username = _user.getUsername();

                    query.add("SELECT username " +
                                    "FROM person,is_user " +
                                    "WHERE username = '" + username + "' AND username = id_person" );

                    if(!server.isConnected())
                    {

                            query.removeAllElements();
                            Menu.print("ERRORE DI CONNESSIONE IN ADDUSER");
                            return false; // ritorna false se non e' connesso,
                                                      // chi chiama questo metodo deve controllare se riceve false
                    }

                    try
                    {

                    result = server.execute(query.get(0));
                    }
                    catch (SQLException e)
                    {

                            query.removeAllElements();
                            Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                            return false ;
                    }

                    rs = (ResultSet)result.getData();

                    if(rs==null){

                            query.removeAllElements();
                            throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                    }

                    if(rs.next()==false){ // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                            query.removeAllElements();
                            return false;
                    }
                    query.removeAllElements();
                    return true;
            }

    }

public boolean isOperator(User _user) throws SQLException{

    if(_user == null) 
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!"); 

    else{

        username = _user.getUsername();

        query.add("SELECT username " +
                        "FROM person,is_operator " +
                        "WHERE username = '" + username + "' AND username = id_person" );

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.print("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso,
                                          // chi chiama questo metodo deve controllare se riceve false
        }

        try{

                result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                return false ;
        }

        rs = (ResultSet)result.getData();

        if(rs==null){

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }

        if(rs.next()==false){ // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                query.removeAllElements();
                return false;
        }
        
        query.removeAllElements();
        return true;
    }
}
    public Vector<String> getPersonList() throws SQLException{

        //query.add("SELECT name, surname, username FROM person");
            query.add("SELECT name, surname, username, is_active FROM person");

            if(!server.isConnected()){

                    query.removeAllElements();
                    Menu.print("ERRORE DI CONNESSIONE IN GETUSERLIST");
                    return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{

                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    System.out.println(
                            e.getSQLState() + " _ " + 
                            e.getMessage() + " _ " +  
                            e.getErrorCode()
                                    );

                    System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    return null;
            }

            rs = (ResultSet)result.getData();

            if(rs==null)
            {
                    ///utente inesistente o password errata
                    query.removeAllElements();
                    throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }
            while(rs.next())
            {
                risultato_select.add(rs.getString("name") + "\t" 
                                + rs.getString("surname") + "\t" 
                                + rs.getString("username") + "\t" 
                                + rs.getBoolean("is_active")
                );
            }

            query.removeAllElements();
            return risultato_select;
    }
    public Vector<String> getUserList() throws SQLException
    {

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person");

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.print("ERRORE DI CONNESSIONE IN GETUSERLIST");
                return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{

                result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                System.out.println(
                        e.getSQLState() + " _ " + 
                        e.getMessage() + " _ " +  
                        e.getErrorCode()
                                );

                System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return null;
        }

        rs = (ResultSet)result.getData();

        if(rs==null){
                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }
        while(rs.next()){
                risultato_select.add(rs.getString("name") + "\t" 
                                    + rs.getString("surname") + "\t" 
                                    + rs.getString("username")
                            );
        }

        query.removeAllElements();
        return risultato_select;
    }
    public Vector<String> getActiveUserList() throws SQLException
    {
        //molto sottile &&
        if(risultato_select != null && !risultato_select.isEmpty())
            risultato_select.clear();

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person AND is_active = 1");

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.print("ERRORE DI CONNESSIONE IN GETUSERLIST");
                return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{

                result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                System.out.println(
                        e.getSQLState() + " _ " + 
                        e.getMessage() + " _ " +  
                        e.getErrorCode()
                                );

                System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return null;
        }

        rs = (ResultSet)result.getData();

        if(rs == null){
                query.removeAllElements();
        throw new
        IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }
        for(int i=1;i< rs.getMetaData().getColumnCount();i++)
            Menu.printGreen("col: " + rs.getMetaData().getColumnName(i));
        while(rs.next()){
                risultato_select.add(rs.getString("name") + "\t" 
                                    + rs.getString("surname") + "\t" 
                                    + rs.getString("username")
                                                        );
        }

        query.removeAllElements();
        return risultato_select;
    }

    public boolean addUser(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null) 
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!"); 

        else{

                name = _user.getName();
                surname = _user.getSurname();
                username = _user.getUsername();
                password = _user.getPassword();

                query.add("SELECT username " +
                                "FROM person " +
                                "WHERE username = '" + username +"'");

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.print("ERRORE DI CONNESSIONE IN ADDUSER");
                        return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                        return false ;
                }


                rs = (ResultSet)result.getData();

                if(rs==null){

                        query.removeAllElements();
                        throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                }

                if(rs.next()!=false){ // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                        query.removeAllElements();
                        throw new IllegalArgumentException("ATTENTION PLEASE! Username gia' esistente!");
                }				


                query.add("INSERT INTO person (name,surname,username,password,is_active) VALUES (" +
                "'" + name + "'" 
                + "," 
                + "'" + surname + "'" + "," 
                + "'" + username + "'" 
                + ","
                + "'" + password + "'" 
                + ",1" 
                + ")");
                query.add("INSERT INTO is_user (id_person) VALUES " + "('" + username + "')");

                // mettere al posto di 2 una costante che indica il numero di query,la i parte da 1 perche' devo saltare la prima query
                // attenzione gestire il fatto che se va a buon fine la prima query ma va a puttane la seconda query c'� la riga person che non ha la corrispondeza con la riga della tabella is_user
                for(int i=0; i < query.size(); i++){
                        try{

                        result = server.execute(query.get(i));	
                        }
                        catch (SQLException e){

                                query.removeAllElements();

                                Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                                Menu.print(e.getMessage());
                                return false;
                        }
                }

                query.removeAllElements();

                //return result.getStatus();
                return true;
        }

    }

    // ISCRIZIONE
    public boolean subscribe(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null) 
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{

                username = _user.getUsername();

                giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
                mese = String.valueOf(gc.get(Calendar.MONTH)).toString();
                anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
                anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

                start_date = giorno + "-" + mese + "-" + anno;
                end_date = giorno + "-" + mese + "-" + anno_finale;

                // id credo si incrementi da solo

                query.add("INSERT INTO subscription " + 
                        "(start_date,end_date,id_person,is_renewed) " +
                        "VALUES (" + "'" + start_date + "'" + "," + "'"+
                        end_date + "'" + "," + "'" + username + "'" + ",0)");

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.print("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return false; // ritorna false se non � connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        return false ;
                }
                query.removeAllElements();
                return result.getStatus();
        }
    }

    //rinnovare

    /**
     * @deprecated
     * @param _user
     * @return
     * @throws IllegalArgumentException
     * @throws SQLException 
     */
    public boolean renew2(User _user) throws IllegalArgumentException, SQLException
    {


        if(_user==null) 
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else
        {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH)).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            start_date = giorno + "-" + mese + "-" + anno;
            end_date = giorno + "-" + mese + "-" + anno_finale;

            query.add("UPDATE subscription SET start_date = '" + start_date + "', end_date = '" + end_date + "'");

            if(!server.isConnected()){
                    query.removeAllElements();
                    Menu.print("ERRORE DI CONNESSIONE IN RENEW");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    return false ;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    public boolean renew(User _user) throws IllegalArgumentException, SQLException
    {
        if(_user == null) 
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else
        {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            Menu.printPurple(username);

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH)).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            start_date = giorno + "-" + mese + "-" + anno;
            end_date = giorno + "-" + mese + "-" + anno_finale;

            query.add("SELECT * FROM subscription WHERE id_person='" +
                    username + "'");


            if(!server.isConnected()){
                    query.removeAllElements();
                    Menu.print("ERRORE DI CONNESSIONE IN RENEW");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.print("ATTENZIONE!! Non esistono username!");
                    Menu.print(e.getMessage());
                    return false ;
            }
            rs = (ResultSet)result.getData();
            //memorizzo l'id della subscription
            int id = -1;
            if(rs.next()) id = rs.getInt("id");
            query.removeAllElements();

            //allora esiste la subscription con tale username
            //aggiunto parametro is_renewed in subscription
            query.add("UPDATE subscription SET is_renewed = '" +
                    1 + "' WHERE id_person='" + username + "'");
            try
            {
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.print("ATTENZIONE!! Errore update status sub!");
                    Menu.print(e.getMessage());
                    return false ;
            }
            query.removeAllElements();
            //ora inserisco nella tabella renew, il rinnovo della 
            //iscrizione
            query.add("INSERT INTO renew_subscription " +
                    "(id_sub,start_date,end_date) VALUES " +
                    "('" + id + "','" + start_date + "','" +
                    end_date + "')") ;
            try
            {
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.print("ATTENZIONE!! Errore update in renew_sub!");
                    Menu.print(e.getMessage());
                    return false ;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    //IsActive
    //getUserInfo
    //hasRenewed
    //

    public String getSubscriptionInfo(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null) 
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{

                username = _user.getUsername();

                giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
                mese = String.valueOf(gc.get(Calendar.MONTH)).toString();
                anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
                anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

                start_date = giorno + "-" + mese + "-" + anno;
                end_date = giorno + "-" + mese + "-" + anno_finale;

                // id credo si incrementi da solo

                query.add("SELECT start_date,end_date,id_person,id,is_renewed"
                        + " FROM subscription WHERE id_person = '" 
                        + username + "'" );

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.print("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        System.out.println(query.size());
                        result = server.execute(query.get(0));

                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        return null ;
                }
                query.removeAllElements();
                rs = (ResultSet)(result.getData());
                if(rs == null) System.out.println("fail");
                else if(rs.next())
                {
                    if(!rs.getBoolean("is_renewed"))
                    //if(true)
                    {
                    query.removeAllElements();
                    return rs.getString("id_person")  + " " + 
                           rs.getString("start_date") + " " +
                           rs.getString("end_date")   + " " +
                           rs.getInt("id") + " " + rs.getBoolean("is_renewed");
                    }
                    else
                    {
                        String id_person = rs.getString("id_person");
                        int id = rs.getInt("id"); 
                        boolean is_renewed = rs.getBoolean("is_renewed");
                        query.add("SELECT * FROM renew_subscription WHERE " +
                                "id_sub=" + id
                                );
                        
                        try{
                        System.out.println(query.size());
                        result = server.execute(query.get(0));
                        query.removeAllElements();
                        }
                        catch (SQLException e){
                                query.removeAllElements();
                                Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                                return null ;
                        }
                        query.removeAllElements();
                        rs = (ResultSet)(result.getData());
                        String final_result = 
                         id_person  + " " + 
                           rs.getString("start_date") + " " +
                           rs.getString("end_date")   + " " +
                           id + " " + is_renewed;
                        
                        return final_result;
                    }
                }
                return null;
        }
    }
    public String getCurrentDate(){return sdf.format(today.getTime());}
    /**
     * Aggiungo una categoria nel db, nella tabella category.
     * @param name 
     * @param desc
     * @param has_sub
     * @param parent
     * @return 
     */
    public boolean addCategory(String name, String desc,boolean is_sub,String parent)
    {
        int myInt = is_sub ? 1 : 0;
        query.add("INSERT INTO category " +
                "(name,description,is_subcategory,parent) VALUES " +
                "('" + name + "','" + desc + "',"                 +
                myInt + ",'" + parent + "')") ;
        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.print("Errore " + ex.getMessage());
            Menu.print("Errore su query: " + query.get(0));
        }
        /** postcondizione*/
        query.removeAllElements();;
        return true;
    }
    /**
     * Prendo la lista di tutte le categorie presenti nel server.
     * @return ArrayList<Category>
     * @removed
     */
   /* public ArrayList<Category> getCategoriesList()
    {
        ArrayList<Category> categories = null;
        int size=0;
        //conto il numero di righe presenti nella tabella
        query.add("SELECT COUNT(*) AS size FROM category");
        //seleziono tutte le righe dalla tabella category
        query.add("SELECT * FROM category");
        try {
            //eseguo il COUNT
            result = server.execute(query.get(0));
            rs = (ResultSet)(result.getData());
            size = rs.getInt("size");
            //eseguo la SELECT
            result = server.execute(query.get(1));
            //finche' ci sono righe
            while(rs.next())
            {
                categories = new ArrayList();
                String nome = rs.getString("name");
                String desc = rs.getString("desc");
                //nome del padre della categoria, se esiste
                String parent = rs.getString("parent");
                
                //controllo se è una categoria con un parente,
                //ovvero una subcategory
                if(parent.equals(""))
                    categories.add(new Category(nome,desc)) ;
                
                //ciclo tutte le categorie, cercando le categorie padre ed
                //aggiungendovi i figli se avviene un match tra il campo
                //parent ed il campo nome della categoria padre
                for(int i = 0; i < categories.size(); i++)
                {
                    if(categories.get(i).getName().equals(parent))
                    {
                        categories.get(i).addSubcategory(nome,desc);
                    }
                }
            }
        } catch (SQLException ex) {
            Menu.print("Errore " + ex.getMessage());
            Menu.print("Errore su query: " + query.get(0));
            Menu.print("Errore su query: " + query.get(1));
        }
        
        // postcondizione
        query.removeAllElements();;
        return categories;
        
    }*/
    /**
     * Effettua l'update di una categoria indicata.
     * @param wichCategory : verra' modificata la cat. con questo nome
     * @param newName : verra' impostata questa descrizione
     * @param desc : verra' impostata questa descrizione
     * @return 
     */
    public boolean updateCategory(String wichCategory, String newName, String desc)
    {
        //int myInt = is_sub ? 1 : 0;
        query.add("UPDATE category SET "             +
                "name ='" + newName + "', "             + 
                "description ='" + desc + "' "      +
                //"is_subcategory ='" + myInt + "', " +
                //"parent ='" + parent + "' "          +
                "WHERE name ='" + wichCategory + "';");
                
                /*"(name,description,is_subcategory,parent) =" +
                "('" + name + "','" + desc + "',"                  +
                Boolean.toString(is_sub) + ",'" + parent           +
                "') WHERE name='" + name + "'") ;*/
        try {
            result = server.execute(query.get(0));
            //rs = (ResultSet)(result.getData());
            
        } catch (SQLException ex) {
            Menu.print("Errore " + ex.getMessage());
            Menu.print("Errore su query: " + query.get(0));
        }
        /** postcondizione*/
        query.removeAllElements();;
        return true;
    }
    
    
    
    public ArrayList<Category> getCategoriesList() throws IllegalArgumentException, SQLException
    {
        ArrayList<Category> categorie = new ArrayList();
        query.add("SELECT * from category WHERE is_subcategory=0" );
        query.add("SELECT * from category WHERE is_subcategory=1" );

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.print("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{
            System.out.println(query.size());
            result = server.execute(query.get(0));
        }
        catch (SQLException e){
            query.removeAllElements();
            Menu.print("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null ;
        }
        rs = (ResultSet)(result.getData());
        if(rs == null) System.out.println("fail");
        else 
            while(rs.next())
            {
                if(!rs.getBoolean("is_subcategory")){
                    String nome = rs.getString("name");
                    String desc = rs.getString("description");
                    categorie.add(new Category(nome,desc));
                }
            }
        //ora procedo con le sottocategorie
        result = server.execute(query.get(1));
        rs = (ResultSet)(result.getData());
        if(rs == null) System.out.println("fail");
        else 
            while(rs.next())
            {
                if(rs.getBoolean("is_subcategory")){
                    String nome = rs.getString("name");
                    String desc = rs.getString("description");
                    //categorie.add(new Category(nome,desc));
                    String parent = rs.getString("parent");
                    for(int i=0;i < categorie.size();i++)
                    {
                        //matching tra categoria padre e figlia
                        if(categorie.get(i).getName().equals(parent)){
                            //aggiunga del figlio al padre
                            categorie.get(i).addSubcategory(nome,desc);
                        }
                    }
                }
                
            }
        
        query.removeAllElements();
        return categorie;

}
    
    public long getDiffDate(Date date1, Date date2, TimeUnit timeUnit) 
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
