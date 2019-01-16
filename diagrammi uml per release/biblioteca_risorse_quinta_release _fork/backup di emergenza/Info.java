package biblioteca_prima_release;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

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

    // INVARIANTE server != null
    public Info(ConcreteServer _server){
            server =_server;

            //sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            sdf = new SimpleDateFormat("dd/MM/yyyy");

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
                        throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                }

                if(rs.next()!=false)
                { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                        query.removeAllElements();
                        throw new IllegalArgumentException("ATENTION PLEASE! Username gia' esistente!");
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
                            throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                    }

                    if(rs.next()==false){ // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                            query.removeAllElements();
                            return false;
                    }

                    return true;
            }

    }

    public boolean isOperator(User _user) throws SQLException{

            if(_user==null) 
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
                            throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
                    }

                    if(rs.next()==false){ // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                            query.removeAllElements();
                            return false;
                    }

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
                    throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
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
                throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
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

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person AND is_active = 0");

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
                throw new IllegalArgumentException("ATENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
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

    public boolean addUser(User _user) throws IllegalArgumentException, SQLException
    {

        if(_user==null) 
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
                        return false; // ritorna false se non � connesso, chi chiama questo metodo deve controllare se riceve null
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

    public long getDiffDate(Date date1, Date date2, TimeUnit timeUnit) 
    {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }
}
