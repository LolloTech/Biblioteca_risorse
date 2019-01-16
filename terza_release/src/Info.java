package biblioteca_terza_release;



/**
 * La classe Info consiste di un insieme di metodi il cui scopo e' di prendere
 * in input una serie di parametri, elaborarli tramite query utilizzando anche
 * ConcreteServer, e ritornare i risultati in oggetti opportunatamente creati
 * per poter contenere l'elaborazione effettuata.
 *
 * @author Sviluppatore
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
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

    Vector <String> query ;
    Vector <String> risultato_select ;
    Result result ;
    ConcreteServer server;
    GregorianCalendar gc;
    GregorianCalendar today;
    SimpleDateFormat sdf;
    ResultSet rs;

    //Contenitore delle categorie
    File file_categorie;


    SimpleDateFormat sdf_today;
    SimpleDateFormat sdf_future;
    String date;

    Calendar cal_today;
    Calendar cal_future;


    // INVARIANTE server != null
    public Info(ConcreteServer _server){
        server =_server;
        sdf = new SimpleDateFormat("yyyy/MM/dd");

        query = new Vector<String>();
        risultato_select = new Vector<String>();
        Result result = new Result();
        ConcreteServer server;
        gc = new GregorianCalendar();
        today = new GregorianCalendar();
        sdf_today = new SimpleDateFormat("yyyy-MM-dd");
        sdf_future = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf_today.format(new Date());



        cal_today = new GregorianCalendar();
        cal_future = new GregorianCalendar();
        sdf_today.setTimeZone(cal_today.getTimeZone());
        sdf_future.setTimeZone(cal_future.getTimeZone());



        //System.out.println();
        //sdf_today.getCalendar().add(Calendar.DAY_OF_MONTH, 30);

        try {
            file_categorie = this.getCategoriesHandler();
        } catch (FileNotFoundException ex) {
            file_categorie = null;
            Logger.getLogger(Info.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * Ritorna il file File, riferimento a "categories.txt"
     * @return File
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
     * Non si occupa di inserire dei dati all'interno del database; questa
     * responsabilità viene lasciata a chi decide di leggere tutte le
     * categorie presenti nel file di testo "categories.txt" tramite questo
     * metodo.
     * @returnArrayList<Category>
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
                if(st.countTokens() == 9)
                {
                    //categoria padre con figli
                    String nome_categoria = st.nextToken();
                    String identificatore = st.nextToken();
                    String is_sub = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    int maxloans = Integer.parseInt(st.nextToken());
                    Category toAdd = new Category(identificatore,nome_categoria,"","");
                    toAdd.setParameters(loan, renew, until, maxloans);
                    categorie.add(toAdd);
                    Menu.printGreen("max_loans: " + maxloans);
                }
                if(st.countTokens() == 10)
                {
                    //categoria padre senza figli

                    String nome_categoria = st.nextToken();
                    String identificatore = st.nextToken();
                    String is_sub = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    String tipo = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    int maxloans = Integer.parseInt(st.nextToken());
                    Category toAdd = new Category(identificatore,nome_categoria,"","");
                    toAdd.setParameters(loan, renew, until, maxloans);
                    categorie.add(toAdd);

                    if(tipo.equalsIgnoreCase("B"))
                        this.addBook(risorsa, n_licenze, identificatore);
                }
                if(st.countTokens() == 11)
                {
                    //sottocategoria
                    String nome_subcat = st.nextToken();
                    String identificatore = st.nextToken();
                    String identificatore_padre = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    String tipo = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    Integer maxloans = null;
                    for(int i = 0; i < categorie.size(); i++)
                    {
                        //attenzione! questo for ha creato problemi di sovrapposizione col padre.
                        //comunque vengono iterati anche i for, e questo if salva problemi di
                        //identificazione.
                        //if(categorie.get(i).getIdentifier().equals(identificatore_padre))
                        if(categorie.get(i).getIdentifier().equals(identificatore_padre))
                        {
                            categorie.get(i).addSubcategory(identificatore,nome_subcat," - ",identificatore_padre);
                            categorie.get(i).getSubCategory().get(categorie.get(i).getSubCategory().size()-1).setParameters(loan, renew, until);
                            //categorie.get(i).setParameters(loan, renew, until,maxloans);
                        }

                    }

                    if(tipo.equalsIgnoreCase("B"))
                        this.addBook(risorsa, n_licenze, identificatore);
                }

            }

        }
        return categorie;
    }
    /**
     * Questo metodo si occupa della parte di login del sistema, ovvero
     * verifica se sono presenti nel database gli identificativi contenuti
     * all'interno dell'utente passato come parametro.
     * @param _user
     * @return boolean
     * @throws SQLException
     */
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
                    Menu.printCyan("ERRORE DI CONNESSIONE IN LOGIN");
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

    /**
     * Aggiunge un utente Operatore all'interno del database, impostando
     * un record per la tabella person, ed un record nella tabella is_operator.
     * @param _user
     * @return boolean
     * @throws SQLException
     */
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
                    Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
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
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
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
                    //throw new IllegalArgumentException("ATTENTION PLEASE! Username gia' esistente!");
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

                            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                            Menu.printCyan(e.getMessage());
                            return false;
                    }
            }

            query.removeAllElements();

            return true;
        }

    }

    /**
     * Verifica se un certo utente e' un fruitore.
     * @param _user
     * @return
     * @throws SQLException
     */
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
                            Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
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
                            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
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
/**
 * Verifica se un certo utente e' un operatore.
 * @param _user
 * @return boolean
 * @throws SQLException
 */
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
                Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso,
                                          // chi chiama questo metodo deve controllare se riceve false
        }

        try{

                result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
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
/**
 * Ritorna la lista di tutte le persone, sia operatrici che fruitori, presenti
 * nel database nella tabella person.
 * @return
 * @throws SQLException
 */
    public Vector<String> getPersonList() throws SQLException{

        //query.add("SELECT name, surname, username FROM person");
            query.add("SELECT name, surname, username, is_active FROM person");

            if(!server.isConnected()){

                    query.removeAllElements();
                    Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
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
    /**
     * Ritorna la lista di tutti gli utenti che sono fruitori del sistema.
     * @return Vector<String>
     * @throws SQLException
     */
    public Vector<String> getUserList() throws SQLException
    {

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person");

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
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
    /**
     * Ritorna la lista di tutti gli utenti attivi, ovvero che hanno il
     * parametro is_active = 1.
     * @return Vector<String>
     * @throws SQLException
     */
    public Vector<String> getActiveUserList() throws SQLException
    {
        //molto sottile &&
        if(risultato_select != null && !risultato_select.isEmpty())
            risultato_select.clear();

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person AND is_active = 1");

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
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

    /**
     * Inserisce un nuovo utente nel sistema, aggiungendo un record nella
     * tabella user.
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */

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
                        Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                        return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
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

                                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                                Menu.printCyan(e.getMessage());
                                return false;
                        }
                }

                query.removeAllElements();

                //return result.getStatus();
                return true;
        }

    }

    /**
     * Iscrive un utente al sistema, inserendo un nuovo record nella tabella
     * subscribe.
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean subscribe(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null)
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{

                username = _user.getUsername();

                giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
                mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
                anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
                anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

                start_date = anno + "-" + mese + "-" + giorno;
                end_date = anno_finale + "-" + mese + "-" + giorno;

                // id credo si incrementi da solo

                query.add("INSERT INTO subscription " +
                        "(start_date,end_date,id_person,is_renewed) " +
                        "VALUES (" + "'" + start_date + "'" + "," + "'"+
                        end_date + "'" + "," + "'" + username + "'" + ",0)");

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return false; // ritorna false se non � connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
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
            mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("UPDATE subscription SET start_date = '" + start_date + "', end_date = '" + end_date + "'");

            if(!server.isConnected()){
                    query.removeAllElements();
                    Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    return false ;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    /**
     * Rinnova l'iscrizione di una persoa gia' iscritta.
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
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
            mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT * FROM subscription WHERE id_person='" +
                    username + "'");


            if(!server.isConnected()){
                    query.removeAllElements();
                    Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! Non esistono username!");
                    Menu.printCyan(e.getMessage());
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
                    Menu.printCyan("ATTENZIONE!! Errore update status sub!");
                    Menu.printCyan(e.getMessage());
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
                    Menu.printCyan("ATTENZIONE!! Errore update in renew_sub!");
                    Menu.printCyan(e.getMessage());
                    return false ;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }
    /**
     * Rimuove una iscrizione effettuata da un utente. Per rimozione, si
     * intende imposta a 0 il parametro is_active nella tabella subscribtion
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
public boolean removeSubscribtion(User _user) throws IllegalArgumentException, SQLException
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
            mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT * FROM subscription WHERE id_person='" +
                    username + "'");


            if(!server.isConnected()){
                    query.removeAllElements();
                    Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! Non esistono username!");
                    Menu.printCyan(e.getMessage());
                    return false ;
            }
            rs = (ResultSet)result.getData();
            //memorizzo l'id della subscription
            int id = -1;
            if(rs.next()) id = rs.getInt("id");
            query.removeAllElements();

            //allora esiste la subscription con tale username
            //aggiunto parametro is_renewed in subscription
            query.add("UPDATE subscription SET is_active = '" +
                    0 + "' WHERE id_person='" + username + "'");
            try
            {
                    result = server.execute(query.get(0));
            }
            catch (SQLException e){

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! Errore update status sub!");
                    Menu.printCyan(e.getMessage());
                    return false ;
            }
            query.removeAllElements();
            //ora inserisco nella tabella renew, il rinnovo della
            //iscrizione
            return true;
        }
    }
    //IsActive
    //getUserInfo
    //hasRenewed
    //
/**
 * Ritorna una stringa contenente delle informazioni inerenti alla iscrizione
 * effettuata da un certo utente, passato come parametro.
 * @param _user
 * @return
 * @throws IllegalArgumentException
 * @throws SQLException
 */
    public String getSubscriptionInfo(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null)
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{

                username = _user.getUsername();

                giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
                mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
                anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
                anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

                //start_date = giorno + "-" + mese + "-" + anno;
                start_date = anno + "-" + mese + "-" + giorno;
                end_date = anno_finale + "-" + mese + "-" + giorno;

                // id credo si incrementi da solo

                query.add("SELECT start_date,end_date,id_person,id,is_renewed"
                        + " FROM subscription WHERE id_person = '"
                        + username + "'" );

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try{

                        //System.out.println(query.size());
                        result = server.execute(query.get(0));

                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
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
                        //System.out.println(query.size());
                        result = server.execute(query.get(0));
                        query.removeAllElements();
                        }
                        catch (SQLException e){
                                query.removeAllElements();
                                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
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
    /**
     * Verifica se un certo utente ha una iscrizione attiva ed inserita nel
     * database, nella tabella subscription.
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean isSubscribtionActive(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user == null)
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{
            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH)+1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR)+5).toString();

            //start_date = giorno + "-" + mese + "-" + anno;
            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT start_date,end_date,id_person,id,is_renewed,is_active"
                    + " FROM subscription WHERE id_person = '"
                    + username + "'" );

            if(!server.isConnected()){

                    query.removeAllElements();
                    Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                    return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try{
                // dimensione query System.out.println(query.size());
                result = server.execute(query.get(0));
            }
            catch (SQLException e){
                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false ;
            }
            query.removeAllElements();
            rs = (ResultSet)(result.getData());
            if(rs == null) System.out.println("fail");
            else if(rs.next())
            {
                return rs.getBoolean("is_active");
            }

        }
        return false;
    }
    public String getCurrentDate(){return sdf.format(today.getTime());}
    /**
     * Aggiungo una categoria nel db, nella tabella category.
     * @param name
     * @param desc
     * @param is_sub
     * @param identifier
     * @param parent
     * @param loan
     * @param renew
     * @param until
     * @param maxloans
     * @return boolean
     */
    public boolean addCategory(String name, String desc,boolean is_sub,String parent,String identifier,
            Integer loan,Integer renew,Integer until,Integer maxloans)
    {
        int myInt = is_sub ? 1 : 0;
        String maxmeans = "";
        if(maxloans != null) maxmeans = maxloans.toString();
        query.add("INSERT INTO category " +
                "(name,description,is_subcategory,parent,identifier,"
                + "loan_duration,renew_duration,days_until_renew,max_loans_to_person"
                + ") VALUES " +
                "('" + name + "','" + desc + "',"                 +
                myInt + ",'" + parent + "','" + identifier + "','"
                + loan + "','" + renew + "','" + until + "','"
                + maxmeans




                + "')") ;
        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
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
                "name ='" + newName + "', "          +
                "description ='" + desc + "' "       +
                //"is_subcategory ='" + myInt + "', " +
                //"parent ='" + parent + "' "          +
                "WHERE identifier ='" + wichCategory + "';");

                /*"(name,description,is_subcategory,parent) =" +
                "('" + name + "','" + desc + "',"                  +
                Boolean.toString(is_sub) + ",'" + parent           +
                "') WHERE name='" + name + "'") ;*/
        try {
            result = server.execute(query.get(0));
            //rs = (ResultSet)(result.getData());

        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
        }
        /** postcondizione*/
        query.removeAllElements();;
        return true;
    }

    /**
     * Ritorna una determinata categoria, individuata da uno specifico
     * identificatore.
     * @param identifier
     * @return Category
     * @throws SQLException
     */
    public Category getCategory(String identifier) throws SQLException
    {
        ArrayList<Category> categorie = new ArrayList();
        String richiesta = "SELECT * from category WHERE identifier='" + identifier +
                "'";
        Result result1 = new Result();
        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{
            //System.out.println(query.size());
            result1 = server.execute(richiesta);
        }
        catch (SQLException e){
            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null ;
        }
        ResultSet rs1 = (ResultSet)(result1.getData());
        String nome = null;
        String desc = null;
        String ident = null;
        String father = null;
        if(rs1 == null) System.out.println("fail");
        else
            while(rs1.next())
            {
                nome = rs1.getString("name");
                desc = rs1.getString("description");
                ident = rs1.getString("identifier");
                father = rs1.getString("parent");

            }
        return new Category(ident,nome,desc, father);

    }
    /**
     * Ritorna la lista di tutte le categorie presenti nel database.
     * @return ArrayList<Category>
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public ArrayList<Category> getCategoriesList() throws IllegalArgumentException, SQLException
    {
        ArrayList<Category> categorie = new ArrayList();
        query.add("SELECT * from category WHERE is_subcategory=0" );
        query.add("SELECT * from category WHERE is_subcategory=1" );

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{
            //System.out.println(query.size());
            result = server.execute(query.get(0));
        }
        catch (SQLException e){
            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
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
                    String identifier = rs.getString("identifier");
                    String father = rs.getString("parent");
                    categorie.add(new Category(identifier,nome,desc,father));
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
                    String identifier = rs.getString("identifier");
                    String parent = rs.getString("parent");
                    for(int i=0;i < categorie.size();i++)
                    {
                        //matching tra categoria padre e figlia
                        if(categorie.get(i).getIdentifier().equals(parent)){
                            //aggiunga del figlio al padre
                            categorie.get(i).addSubcategory(identifier,nome,desc);
                        }
                    }
                }

            }

        query.removeAllElements();
        return categorie;

}
    /**
     * Ritorna una lista di tutti i libri presenti nel database, dalla tabella
     * book.
     * @return ArrayList<Book>
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public ArrayList<Book> getBooksList() throws IllegalArgumentException, SQLException
    {
        ArrayList<Book> libri = new ArrayList();
        query.add("SELECT * from book" );
        //query.add("SELECT * from book WHERE is_active=1" );

        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{

            result = server.execute(query.get(0));
        }
        catch (SQLException e){
            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null ;
        }
        rs = (ResultSet)(result.getData());
        Book elemento = null;
        if(rs == null) System.out.println("fail");
        else
            while(rs.next())
            {
                elemento = new Book();
                elemento.setAnno(rs.getString("publication_year"));
                elemento.setAutori(rs.getString("authors"));
                elemento.setCasaEditrice(rs.getString("publisher"));
                elemento.setGenere(rs.getString("genre"));
                elemento.setId(rs.getString("id"));
                elemento.setIsActive(rs.getString("is_active"));
                elemento.setIsAvaiable(rs.getString("is_avaiable"));
                elemento.setLicenze(rs.getString("license_number"));
                elemento.setNumeroPagine(rs.getString("page_number"));
                elemento.setTitolo(rs.getString("title"));
                elemento.setIdentifier(rs.getString("identifier"));
                //attenzione
                elemento.setCategory(this.getCategory(rs.getString("category_ident")));
                libri.add(elemento);
            }
        //ora procedo con le sottocategorie

        query.removeAllElements();
        return libri;

}

    /**
     * Aggiunge un libro nuovo, nel database, nella tabella book.
     *
     * @author
     * @param libro
     * @return boolean
     */
    public boolean addBook(String identifier, String license, String categoryIdent)
    {
        //int myInt = is_sub ? 1 : 0;
        query.add("INSERT INTO book " +
                "(identifier, license_number, title, category_ident) VALUES " +
                "('" + identifier + "','"
                + license + "','"
                + identifier + "','"
                + categoryIdent + "')"
        ) ;
        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
        }
        /** postcondizione*/
        query.removeAllElements();
        return true;
    }

    /**
     * Aggiunge un libro nuovo, nel database, nella tabella book.
     * @author
     * @param libro
     * @return  boolean
     * @attributi:
    * - id
    * - identifier
    * - titolo
    * - anno_pubblicazione
    * - descrizione
    * - lingua
    * - genere
    * - numero_pagine
    * - autori[]
    * - licenze
    * - casa_editrice
    * - is_avaiable: indica se e' disponibile considerando il n. di licenze
    * - is_active: indica se e' disponile nell'archivio
    *
    */
    public boolean addBook(Book libro)
    {
        int is_act = libro.isActive() ? 1 : 0;
        int is_ava = libro.isAvaiable() ? 1 : 0;
        query.add("INSERT INTO book " +
                "(id,identifier, title, publication_year, description, languange, "
                + "genre, page_number, authors, license_number, publisher, is_avaiable, is_active) VALUES " +
                "('"
                + libro.getId() + "','"
                + libro.getIdentifier() + "','"
                + libro.getTitolo() + "','"
                + libro.getAnno()+ "','"
                + libro.getDescrizione() + "','"
                + libro.getLingua() + "','"
                + libro.getGenere() + "','"
                + libro.getNumeroPagine() + "','"
                + libro.getAutori() + "','"
                + libro.getLicenze() + "','"
                + libro.getCasaEditrice() + "','"
                + is_ava + "','"
                + is_act + "')"
                );

        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
            return false;
        }
        /** postcondizione*/
        query.removeAllElements();
        return true;
    }
    /**
     * Effettua l'update dei dati del libro passato, facendo un update nel
     * database, nella tabella book.
     * @param libro
     * @return boolean
     */
    public boolean updateBook(Book libro)
    {
        int is_act = libro.isActive() ? 1 : 0;
        int is_ava = libro.isAvaiable() ? 1 : 0;
        query.add("UPDATE book SET "
                + "title='" + libro.getTitolo() + "',"
                + "publication_year='" + libro.getAnno()+ "',"
                + "description='" + libro.getDescrizione() + "',"
                + "language='" +libro.getLingua() + "',"
                + "genre='" + libro.getGenere() + "',"
                + "page_number='" + libro.getNumeroPagine() + "',"
                + "authors='" + libro.getAutori() + "',"
                + "page_number='" + libro.getLicenze() + "',"
                + "publisher='" + libro.getCasaEditrice() + "',"
                + "is_avaiable='" + is_ava + "',"
                + "is_active='" + is_act
                + "' WHERE identifier='"
                + libro.getIdentifier() + "';"
                );

        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
            return false;
        }
        /** postcondizione*/
        query.removeAllElements();;
        return true;
    }
    /**
     * Prende due date come ingresso, e ritorna la differenza tra la seconda
     * data e la prima data.
     * @param date1
     * @param date2
     * @param timeUnit
     * @return long
     */
    public long getDiffDate(Date date1, Date date2, TimeUnit timeUnit)
    {
        Menu.printCyan(date1.toString());
        Menu.printCyan(date2.toString());
        Menu.printCyan("formatto...");
        Menu.printCyan(date1.getTime());
        Menu.printCyan(date2.getTime());

        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
    /**
     * Crea un nuovo prestito, ovvero un nuovo record nella tabella loan,
     * inserendo dati dell'utente e del libro passatogli per parametro.
     * @param _user
     * @param _book
     * @return boolean
     * @throws SQLException
     */
    public boolean addBookLoan(User _user, Book _book) throws SQLException
    {


        if(_user == null)
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{
                String checkLoan = "SELECT count(*) as dim FROM loan, book, person WHERE loan.id_book = book.identifier"
                    + " AND loan.id_person = person.username and loan.id_person ='" + _user.getUsername()
                    + "' AND loan.id_book='" + _book.getIdentifier() + "';";


                //questo avviene quando un utente ha gia' in prestito
                //tale libro, ed il prestito gli viene impedito.
                result = server.execute(checkLoan);
                rs = (ResultSet) (result.getData());
                if(rs.next())
                    if(rs.getInt("dim") > 0)
                        return false;


                // questa query seleziona quanti libri di una data categoria, data da _book,
                // ha preso in prestito l'utente _user
                query.add("select count(*) as dim from loan, person, book, category where book.identifier = loan.id_book"
                        + " and book.category_ident = category.identifier and person.username = loan.id_person\n"
                        + "and person.username = '" + _user.getUsername() + "' "
                        + "and loan.is_active = 1 and "
                        + "category_ident ='" + _book.getCategory().getIdentifier() + "'"

                );
                String identifier ;
                // questo if serve in quanto le categorie padri non hanno un padre
                // con un riferimento corretto
                if(_book.getCategory().getFather().getIdentifier().equals(""))
                    identifier = _book.getCategory().getIdentifier();
                else
                    identifier = _book.getCategory().getFather().getIdentifier();
                // seleziona la massima durata di prestito da una categoria data
                query.add("select max_loans_to_person from category where category.identifier"
                        + " = '"
                        + identifier + "'");

                // seleziona la durata di un prestito di un certo libro, data da
                // una certa categoria
                query.add("select category.loan_duration from loan, book, category where "
                        + "book.category_ident = category.identifier and "
                        + "book.identifier='" + _book.getIdentifier()
                        + "'");


                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                //memorizzo il risultato della prima query
                int count_libri_utente = -1;
                //memorizzo il risultato della seconda query, ovvero quanti libri potro' prestare
                int count_libri_prestabili = -1;

                int durata_giorni = 0;
                //eseguo la prima query
                result = server.execute(query.get(0));

                rs = (ResultSet)(result.getData());
                if(rs == null)
                    System.out.println("fail");
                else
                    count_libri_utente = rs.getInt("dim");
                rs.close();
                //eseguo la seconda query
                result = server.execute(query.get(1));

                rs = (ResultSet)(result.getData());
                if(rs == null)
                    System.out.println("fail");
                else
                    count_libri_prestabili = Integer.parseInt(rs.getString("max_loans_to_person"));
                rs.close();
                //eseguo la terza query
                result = server.execute(query.get(2));

                rs = (ResultSet)(result.getData());
                if(rs == null)
                    System.out.println("fail");
                else
                    durata_giorni = Integer.parseInt(rs.getString("loan_duration"));
                Menu.printRed("giorni");

                System.out.println(durata_giorni);

                cal_future.add(Calendar.DAY_OF_MONTH, durata_giorni);

                // calcolo agilmente le date da inserire nel database
                start_date = sdf_today.format(cal_today.getTime());
                end_date = sdf_future.format(cal_future.getTime());
                rs.close();
                query.add("INSERT INTO loan "
                    + "(id_person,id_book,start_date,end_date,is_renewed,is_active) "
                    + "VALUES ("
                    + "'" + _user.getUsername() + "',"
                    + "'" + _book.getIdentifier() + "',"
                    + "'" + start_date + "',"
                    + "'" + end_date + "',"
                    + "'" + 0 + "',"
                    + "'" + 1 + "')"
                    );

                Menu.printRed("oggi: " + start_date);
                Menu.printRed("fuguro: " + end_date);


                Menu.printCyan("libri utente e prestabili : " + count_libri_utente + " " +
                        "count_libri_prestabili : " + count_libri_prestabili);
                if(count_libri_prestabili > count_libri_utente)
                {
                    Menu.print("La risorsa e' prestabile!");
                    //qui posso prestare il libro all'utente
                    result = server.execute(query.get(3));
                    rs = (ResultSet)(result.getData());
                    query.removeAllElements();
                    return true;
                }
                rs.close();
                query.removeAllElements();
                return false;
        }
        //query.removeAllElements();

    }
    /**
     * Crea un rinnovo per un utente, impostando il parametro is_renewed nella
     * tabella loan ad 1, e creando un nuovo record nella tabella renew_loan
     * @param _user
     * @param _book
     * @param _id_loan
     * @return boolean
     * @throws SQLException
     * @throws ParseException
     */
    public boolean renewLoan(User _user, Book _book, int _id_loan) throws SQLException, ParseException
    {
        if(_user == null)
                throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");

        else{

                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                int differenza_giorni = 0;
                username = _user.getUsername();
                String query_start_date = "";
                String query_end_date = "";
                Menu.printRed("dudu: " + _book.getCategory().RENEW_DURATION);
                cal_future.add(Calendar.DAY_OF_MONTH, _book.getCategory().RENEW_DURATION);

                // calcolo agilmente le date da inserire nel database
                start_date = sdf.format(cal_today.getTime());
                end_date = sdf.format(cal_future.getTime());

                Date end = new Date();

                //controllo di sicurezza dell'esistenza del prestito
                query.add("SELECT start_date,end_date FROM loan"
                    + " WHERE id_book ='" + _book.getIdentifier()
                    + "' AND is_active=1 AND id_person='"
                    + _user.getUsername()+ "'");


                //controllo di sicurezza dell'esistenza del prestito
                query.add("SELECT count(*) AS dim FROM renew_loan "
                + "WHERE id_book ='" + _book.getIdentifier() +
                        "' AND is_active=1 AND id_loan =" +_id_loan);

                query.add("UPDATE loan SET "
                        + "is_renewed='" + 1        + "'"
                        + "WHERE id_person ='"
                        + _user.getUsername() + "' AND id=" + _id_loan + " AND "
                        + "id_book ='" +
                        _book.getIdentifier() + "';"
                    );

                //query per il rinnovo del prestito del libro
                query.add("INSERT INTO renew_loan " +
                    "(id_book,start_date,end_date,is_active,id_loan) " +
                    "VALUES ("
                    + "'" + _book.getIdentifier()      + "',"
                    + "'" + start_date      + "',"
                    + "'" + end_date        + "',"
                    + "'" + 1               + "',"
                    + _id_loan  + ")"
                );

                if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
                }

                try {

                    result = server.execute(query.get(0));
                    rs = (ResultSet) (result.getData());
                    if (rs == null)
                        System.out.println("fail");
                    else if (rs.next())
                    {
                        //prendo le date della riga ottenuta nella tabella prestiti,
                        //che poi utilizzero' per confrontare i giorni
                        query_start_date = rs.getString("start_date");
                        query_end_date = rs.getString("end_date");
                        //Questa e' la data chemi serve
                        end = sdf.parse(query_end_date);


                    }
                    else
                    {
                        query.removeAllElements();
                        return false;
                    }

                    differenza_giorni =(int) this.getDiffDate(
                        cal_today.getTime(),
                        end,
                        TimeUnit.DAYS
                    );
                } catch (SQLException e) {

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    Menu.printCyan(e.getMessage());
                    return false;
                }



                try
                {
                    result = server.execute(query.get(1));
                }
                catch (SQLException e){

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    Menu.printCyan(e.getMessage());
                    return false ;
                }
                rs = (ResultSet)(result.getData());
                if(rs == null) System.out.println("fail");
                else if(rs.next())
                {
                    //UPDATE loan

                    //output di sicurezza
                    Menu.printGreen("differenza in giorni: " + differenza_giorni);
                    Menu.printGreen("tempo categoria: " + _book.getCategory().DAYS_UNTIL_RENEW);
                    Menu.printGreen("start date : " + start_date);
                    Menu.printGreen("end_date: " + end_date);

                    Menu.printGreen("qstart date : " + query_start_date);
                    Menu.printGreen("qend_date: " + query_end_date);
                    if(rs.getInt("dim") > 0)
                    {
                        query.removeAllElements();
                        return false;
                    }
                    //INSERT in renew_loan

                    try
                    {
                        if(differenza_giorni >= 0 && differenza_giorni <= _book.getCategory().DAYS_UNTIL_RENEW)
                        {
                            result = server.execute(query.get(2));
                            result = server.execute(query.get(3));
                            rs = (ResultSet)(result.getData());
                        }
                        else
                        {
                            query.removeAllElements();
                            return false;
                        }
                    }
                    catch (SQLException e)
                    {
                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        Menu.printCyan(e.getMessage());
                        return false;
                    }
                    query.removeAllElements();
                    return true;

               }
            query.removeAllElements();
            return false;
        }


    }

    /**
     * Verifica se un libro passato per parametro e' disponibile al prestito.
     * Funziona settando ad 1 il parametro is_active, della tabella loan e
     * renew_loan
     * @param book
     * @return boolean
     * @throws SQLException
     */
    public boolean isBookAvaiable(Book book) throws SQLException
    {
        query.add("select *,count(*) as dim from loan, book where loan.id_book = book.identifier "
                + "and loan.is_active = 1 and book.identifier = '" + book.getIdentifier()+
                "' group by book.identifier");
        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{
                result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false ;
        }


        rs = (ResultSet)(result.getData());

        if(rs == null)
            System.out.println("fail");
        if(rs.next())
            if(Integer.parseInt(book.getLicenze()) > rs.getInt("dim"))
            {
                query.removeAllElements();
                return true;
            }
        query.removeAllElements();
        return false;

    }
    /**
     * Rimuove un prestito da parte di un utente e di un certo libro, sia
     * dalla tabella loan che dalla tabella renew_loan
     * @param user
     * @param book
     * @param id_loan
     * @return boolean
     * @throws SQLException
     * @throws ParseException
     */
    public boolean removeLoan(User user, Book book, int id_loan) throws SQLException, ParseException
    {
        boolean flag=false;
        //per i libri che non hanno avuto rinnovo
        query.add("select loan.id_person, loan.start_date as 'l.start', loan.end_date as 'l.end',"
                + "book.identifier ,loan.is_active,loan.is_renewed from loan, book \n"
                + "where loan.id_book = book.identifier and loan.is_active=1 and is_renewed=0 "
                + "and loan.id_person ='" + user.getUsername()
                + "' and loan.id_book='" + book.getIdentifier()+ "'");
        //per i libri che hanno avuto un rinnovo
        query.add("select loan.id_person, loan.start_date as 'l.start', loan.end_date as 'l.end'"
                + ", renew_loan.start_date as 'r.start', renew_loan.end_date as 'r.end',"
                + " book.identifier ,loan.is_active,loan.is_renewed from loan, book,renew_loan"
                + " where loan.id_book = book.identifier and renew_loan.id_book = book.identifier and"
                + " loan.is_renewed = 1 and loan.is_active = 1 and"
                + " loan.id_person ='" + user.getUsername() + "' and book.identifier='" + book.getIdentifier()
                + "'");
        query.add("update loan\n" +
                "set is_active = 0\n" +
                "where loan.id IN ( select loan.id from book, loan, person \n" +
                "where book.identifier = loan.id_book \n" +
                "and person.username = loan.id_person\n" +
                "and person.username = '"
                + user.getUsername() + "' "
                + "and book.identifier ='" + book.getIdentifier()
                + "')");
        query.add("update renew_loan\n" +
                "set is_active = 0\n"
                + "where renew_loan.id IN ( select renew_loan.id from book, loan, person,renew_loan \n"
                + "where loan.id_book = renew_loan.id_book\n "
                + "and book.identifier = loan.id_book \n"
                + "and person.username = loan.id_person\n"
                + "and person.username = '"
                + user.getUsername() + "' "
                + "and book.identifier ='" + book.getIdentifier()
                + "' and renew_loan.id_loan=" + id_loan
                + ")");
        if(!server.isConnected()){

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try{
            //prima i libri senza proroghe
            result = server.execute(query.get(0));
        }
        catch (SQLException e){

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                Menu.printRed(e.getMessage());
                return false ;
        }


        rs = (ResultSet)(result.getData());

        if(rs == null)
            Menu.printRed("rs null in update, da controllare");


        while(rs.next()){
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //Date start = format.parse(cal_today.getTime());
            Date end = format.parse(rs.getString("l.end"));
            // Confronto le due date, per capire se una delle due sia piu' grande
            // dell' altra.
            // Falso mi indica che la data _start_ e' dopo _end_ .

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = (Date) formatter.parse(rs.getString("l.end"));



            long differenza = this.getDiffDate(cal_today.getTime(),
                    date, TimeUnit.DAYS);
            boolean confronto = end.after(cal_today.getTime());
            Menu.printGreen("differenza tra le due date:" + differenza);
            if(confronto)
                Menu.printCyan("confronto falso!");
            else
            {

                Menu.printRed("confronto vero!");
                try {
                    result = server.execute(query.get(2));
                    result = server.execute(query.get(3));
                } catch (SQLException e) {

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    Menu.printRed(e.getMessage());
                    Menu.printCyan(e.getMessage());
                    query.removeAllElements();
                    return false;
                }
                flag = true;
            }
        }
        try {
            //prima i libri senza proroghe
            result = server.execute(query.get(1));
        } catch (SQLException e) {

            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            Menu.printRed(e.getMessage());
            return false;
        }

        rs = (ResultSet) (result.getData());
        if (rs == null)
        {
            Menu.printRed("rs null in update, da controllare");
        }

        while (rs.next())
        {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //Date start = format.parse(cal_today.getTime());
            Date end = format.parse(rs.getString("r.end"));
            // Confronto le due date, per capire se una delle due sia piu' grande
            // dell' altra.
            // Falso mi indica che la data _start_ e' dopo _end_ .

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = (Date) formatter.parse(rs.getString("r.end"));

            long differenza = this.getDiffDate(cal_today.getTime(),
                    date, TimeUnit.DAYS);
            boolean confronto = end.after(cal_today.getTime());
            Menu.printGreen("lold edsi:" + differenza);
            if (confronto)
                Menu.printCyan("confronto falso!");
            else
            {
                flag = true;
                Menu.printRed("confronto vero! R");
                try {
                    result = server.execute(query.get(2));
                    result = server.execute(query.get(3));
                } catch (SQLException e) {

                    query.removeAllElements();
                    Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                    Menu.printRed(e.getMessage());
                    Menu.printCyan(e.getMessage());
                    query.removeAllElements();
                    return false;
                }

            }


        }
        query.removeAllElements();
        return flag;

    }
    /**
     * Ritorna la lista di tutti i libri che un certo utente ha in prestito
     * @param user
     * @return ArrayList<Loan>
     * @throws SQLException
     */
    public ArrayList<Loan> getBooksLoan(User user) throws SQLException
    {
        query.add("select person.username, person.name, book.identifier, book.license_number, loan.start_date, loan.id,"
                + "loan.end_date, loan.is_active as active, loan.is_renewed as renewed, category.renew_duration, category.loan_duration,"
                + "category.days_until_renew, category.identifier as cat_identifier from book, loan, person , category "
                + "where person.username = loan.id_person and loan.id_book = book.identifier and loan.is_active = 1 and" +
                " category.identifier = book.category_ident and person.username = '"
                + user.getUsername() + "'");

        Menu.printCyan(query.get(0));

        Loan element = null;
        ArrayList<Loan> elementi = null;


        if(!server.isConnected()){

                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return null;
                }

                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        return null ;
                }
                rs = (ResultSet)(result.getData());
                elementi = new ArrayList<>();
                if(rs == null)
                    System.out.println("fail");
                else while(rs.next())
                {
                    element = new Loan();
                    element.id_loan = rs.getInt("id");
                    element.data_inizio_prestito = rs.getString("start_date");
                    element.data_fine_prestito = rs.getString("end_date");
                    element.is_active = rs.getBoolean("active");
                    element.is_renewed = rs.getBoolean("renewed");
                    element.user = new User(rs.getString("name"),rs.getString("username"));
                    element.libro = new Book();
                    element.libro.setLicenze(rs.getString("license_number"));
                    element.libro.setIdentifier(rs.getString("identifier"));
                    element.libro.setCategory(new Category(rs.getString("cat_identifier")));
                    element.libro.getCategory().setParameters(Integer.parseInt(rs.getString("loan_duration")),
                                                              Integer.parseInt(rs.getString("renew_duration")),
                                                              Integer.parseInt(rs.getString("days_until_renew")));
                    elementi.add(element);
                }
                for(int i=0;i<elementi.size();i++)
                {

                    if(elementi.get(i).is_renewed)
                    {
                        result = server.execute(
                        "select person.username ,book.identifier, book.title, loan.is_active, loan.is_renewed, \n" +
                        "renew_loan.is_active as renew_is_active, renew_loan.is_renewed as renew_is_renew,\n" +
                        "renew_loan.start_date, renew_loan.end_date from book, loan, person, renew_loan\n" +
                        " where person.username = loan.id_person and book.identifier = loan.id_book \n" +
                        "AND loan.is_active = 1 and id_person = '" + elementi.get(i).user.getUsername() +
                        "' and book.identifier ='" + elementi.get(i).libro.getIdentifier() + "'");
                        rs = (ResultSet)(result.getData());
                        if(rs.next())
                        {
                            elementi.get(i).data_inizio_proroga =rs.getString("start_date");
                            elementi.get(i).data_fine_proroga =rs.getString("end_date");
                        }
                    }
                }

        query.removeAllElements();
        return elementi;
    }
    public Integer getLoanedBook(User user, Book book) throws SQLException
    {
        query.add("select count(*) as dim from loan, person, book where book.identifier = loan.id_book and person.username = loan.id_person\n" +
                    "and person.username = '" + user.getUsername() + "'and loan.is_active = 1");

        if(!server.isConnected()){
                        query.removeAllElements();
                        Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                        return null;
                }
                try{

                        result = server.execute(query.get(0));
                }
                catch (SQLException e){

                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        return null;
                }
                rs = (ResultSet)(result.getData());
                Integer dim = null;
                if(rs == null)
                    System.out.println("fail");
                else while(rs.next())
                {
                    dim = rs.getInt("dim");
                }

        query.removeAllElements();
        return dim;
    }
    /**
     * Verifica se il libro passato come parametro e' disponibile al prestito
     *
     * @param book
     * @return boolean
     * @throws SQLException
     */
    public boolean isBookAvaiableForLoan(Book book) throws SQLException
    {
        query.add("select count(*) as dim, book.identifier from loan, book where loan.id_book = book.identifier"
                + " and loan.is_active = 1 and book.identifier = '"
                + book.getIdentifier() +"' group by book.identifier");

        try {
            result = server.execute(query.get(0));
        } catch (SQLException ex) {
            query.removeAllElements();
        }
        rs = (ResultSet)result.getData();
        if(rs.next())
        {
            int dim = rs.getInt("dim");
            //was Integer.parseInt(book.getLicenze()) >= dim
            if(Integer.parseInt(book.getLicenze()) > dim)
            {
                query.removeAllElements();
                return true;
            }
            query.removeAllElements();
            return false;
        }
        //qui posso far ritornare true, in quanto se il risultato della query e' un insieme vuoto,
        //significa che tale libro e' disponibile, in quanto non e' in prestito a nessuno( in
        //loans, non abbiamo un record inerente).
        query.removeAllElements();
        return true;
    }
}
