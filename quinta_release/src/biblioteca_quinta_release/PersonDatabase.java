
package biblioteca_quinta_release;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

/**
 *
 * @author Sviluppatore
 */
public class PersonDatabase extends ModelDatabaseContainer{
    
    public PersonDatabase(ConcreteServer _server) {
        super(_server);
    }
    /**
     * Questo metodo si occupa della parte di login del sistema, ovvero verifica
     * se sono presenti nel database gli identificativi contenuti all'interno
     * dell'utente passato come parametro.
     *
     * @param _user
     * @return boolean
     * @throws SQLException
     */
    public boolean login(User _user) throws SQLException {
        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {
            username = _user.getUsername();
            password = _user.getPassword();

            query.add("SELECT username"
                    + " FROM person"
                    + " WHERE username = '" + username + "' AND" + " password = '" + password + "'");
            Menu.printPurple(query.firstElement());

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN LOGIN");
                return false; // ritorna FALSE se non e' connesso, chi 
                // ATTENZIONE !!chiama questo metodo e riceve false non sa se si 
                //tratta di un errore di connessione o di un login falso
            }

            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false; //ATTENZIONE !!! riceve false ma non sa se si tratta di un errore
                // o di login falso
            }

            rs = (ResultSet) result.getData();

            if (rs == null) {

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if (rs.next() != false) {

                query.removeAllElements();
                return true;  // se rs contiene tuple allora le credenziali
                //  sono giuste e ritorna true
            }

            query.removeAllElements();
            return false;
        }
    }

    /**
     * Aggiunge un utente Operatore all'interno del database, impostando un
     * record per la tabella person, ed un record nella tabella is_operator.
     *
     * @param _user
     * @return boolean
     * @throws SQLException
     */
    public boolean addOperator(User _user) throws SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {
            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            password = _user.getPassword();

            query.add("SELECT username "
                    + "FROM person "
                    + "WHERE username = '" + username + "'");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso,
                // chi chiama questo metodo deve controllare se riceve false
            }

            try {

                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                return false;
            }

            rs = (ResultSet) result.getData();

            if (rs == null) {

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if (rs.next() != false) { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                query.removeAllElements();
                //throw new IllegalArgumentException("ATTENTION PLEASE! Username gia' esistente!");
            }

            query.add("INSERT INTO person (name,surname,username,password,is_active) VALUES ("
                    + "'" + name + "'"
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
            for (int i = 0; i < query.size(); i++) {
                try {

                    result = server.execute(query.get(i));
                } catch (SQLException e) {

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
     *
     * @param _user
     * @return
     * @throws SQLException
     */
    public boolean isUser(User _user) throws SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            username = _user.getUsername();

            query.add("SELECT username "
                    + "FROM person,is_user "
                    + "WHERE username = '" + username + "' AND username = id_person");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso,
                // chi chiama questo metodo deve controllare se riceve false
            }

            try {

                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                return false;
            }

            rs = (ResultSet) result.getData();

            if (rs == null) {

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if (rs.next() == false) { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                query.removeAllElements();
                return false;
            }
            query.removeAllElements();
            return true;
        }

    }

    /**
     * Verifica se un certo utente e' un operatore.
     *
     * @param _user
     * @return boolean
     * @throws SQLException
     */
    public boolean isOperator(User _user) throws SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            username = _user.getUsername();

            query.add("SELECT username "
                    + "FROM person,is_operator "
                    + "WHERE username = '" + username + "' AND username = id_person");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso,
                // chi chiama questo metodo deve controllare se riceve false
            }

            try {

                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                return false;
            }

            rs = (ResultSet) result.getData();

            if (rs == null) {

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if (rs.next() == false) { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                query.removeAllElements();
                return false;
            }

            query.removeAllElements();
            return true;
        }
    }

    /**
     * Ritorna la lista di tutte le persone, sia operatrici che fruitori,
     * presenti nel database nella tabella person.
     *
     * @return
     * @throws SQLException
     */
    public Vector<String> getPersonList() throws SQLException {

        //query.add("SELECT name, surname, username FROM person");
        query.add("SELECT name, surname, username, is_active FROM person");

        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
            return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            System.out.println(
                    e.getSQLState() + " _ "
                    + e.getMessage() + " _ "
                    + e.getErrorCode()
            );

            System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }

        rs = (ResultSet) result.getData();

        if (rs == null) {
            ///utente inesistente o password errata
            query.removeAllElements();
            throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }
        while (rs.next()) {
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
     *
     * @return Vector<String>
     * @throws SQLException
     */
    public Vector<String> getUserList() throws SQLException {

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person");

        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
            return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            System.out.println(
                    e.getSQLState() + " _ "
                    + e.getMessage() + " _ "
                    + e.getErrorCode()
            );

            System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }

        rs = (ResultSet) result.getData();

        if (rs == null) {
            query.removeAllElements();
            throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }
        while (rs.next()) {
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
     *
     * @return Vector<String>
     * @throws SQLException
     */
    public Vector<String> getActiveUserList() throws SQLException {
        //molto sottile &&
        if (risultato_select != null && !risultato_select.isEmpty()) {
            risultato_select.clear();
        }

        query.add("SELECT name, surname, username FROM person,is_user WHERE person.username = is_user.id_person AND is_active = 1");

        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN GETUSERLIST");
            return null; // ritorna null se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            System.out.println(
                    e.getSQLState() + " _ "
                    + e.getMessage() + " _ "
                    + e.getErrorCode()
            );

            System.out.println("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }

        rs = (ResultSet) result.getData();

        if (rs == null) {
            query.removeAllElements();
            throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
        }
        for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
            Menu.printGreen("col: " + rs.getMetaData().getColumnName(i));
        }
        while (rs.next()) {
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
     *
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean addUser(User _user) throws IllegalArgumentException, SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            password = _user.getPassword();

            query.add("SELECT username "
                    + "FROM person "
                    + "WHERE username = '" + username + "'");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN ADDUSER");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {

                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY IN ADDUSER");
                return false;
            }

            rs = (ResultSet) result.getData();

            if (rs == null) {

                query.removeAllElements();
                throw new IllegalArgumentException("ATTENTION PLEASE! Errore di esecuzione del servizio da parte del server!");
            }

            if (rs.next() != false) { // chi usa questo metodo deve gestire IllegalArgumentException,SQLException

                query.removeAllElements();
                Menu.print("Attenzione! Username gia' esistente! Non puoi "
                        + "aggiungere un utente con questo username");
            }

            query.add("INSERT INTO person (name,surname,username,password,is_active) VALUES ("
                    + "'" + name + "'"
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
            for (int i = 0; i < query.size(); i++) {
                try {

                    result = server.execute(query.get(i));
                } catch (SQLException e) {

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
     *
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean subscribe(User _user) throws IllegalArgumentException, SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            // id credo si incrementi da solo
            query.add("INSERT INTO subscription "
                    + "(start_date,end_date,id_person,is_renewed) "
                    + "VALUES (" + "'" + start_date + "'" + "," + "'"
                    + end_date + "'" + "," + "'" + username + "'" + ",0)");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return false; // ritorna false se non � connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {

                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    //rinnovare
    /**
     * @deprecated @param _user
     * @return
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean renew2(User _user) throws IllegalArgumentException, SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("UPDATE subscription SET start_date = '" + start_date + "', end_date = '" + end_date + "'");

            if (!server.isConnected()) {
                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    /**
     * Rinnova l'iscrizione di una persoa gia' iscritta.
     *
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean renew(User _user) throws IllegalArgumentException, SQLException {
        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            Menu.printPurple(username);

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT * FROM subscription WHERE id_person='"
                    + username + "'");

            if (!server.isConnected()) {
                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! Non esistono username!");
                Menu.printCyan(e.getMessage());
                return false;
            }
            rs = (ResultSet) result.getData();
            //memorizzo l'id della subscription
            int id = -1;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            query.removeAllElements();

            //allora esiste la subscription con tale username
            //aggiunto parametro is_renewed in subscription
            query.add("UPDATE subscription SET is_renewed = '"
                    + 1 + "' WHERE id_person='" + username + "'");
            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! Errore update status sub!");
                Menu.printCyan(e.getMessage());
                return false;
            }
            query.removeAllElements();
            //ora inserisco nella tabella renew, il rinnovo della 
            //iscrizione
            query.add("INSERT INTO renew_subscription "
                    + "(id_sub,start_date,end_date) VALUES "
                    + "('" + id + "','" + start_date + "','"
                    + end_date + "')");
            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! Errore update in renew_sub!");
                Menu.printCyan(e.getMessage());
                return false;
            }
            query.removeAllElements();
            return result.getStatus();
        }
    }

    /**
     * Rimuove una iscrizione effettuata da un utente. Per rimozione, si intende
     * imposta a 0 il parametro is_active nella tabella subscribtion
     *
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean removeSubscribtion(User _user) throws IllegalArgumentException, SQLException {
        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            name = _user.getName();
            surname = _user.getSurname();
            username = _user.getUsername();
            Menu.printPurple(username);

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT * FROM subscription WHERE id_person='"
                    + username + "'");

            if (!server.isConnected()) {
                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN RENEW");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! Non esistono username!");
                Menu.printCyan(e.getMessage());
                return false;
            }
            rs = (ResultSet) result.getData();
            //memorizzo l'id della subscription
            int id = -1;
            if (rs.next()) {
                id = rs.getInt("id");
            }
            query.removeAllElements();

            //allora esiste la subscription con tale username
            //aggiunto parametro is_renewed in subscription
            query.add("UPDATE subscription SET is_active = '"
                    + 0 + "' WHERE id_person='" + username + "'");
            try {
                result = server.execute(query.get(0));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! Errore update status sub!");
                Menu.printCyan(e.getMessage());
                return false;
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
     * Ritorna una stringa contenente delle informazioni inerenti alla
     * iscrizione effettuata da un certo utente, passato come parametro.
     *
     * @param _user
     * @return
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public String getSubscriptionInfo(User _user) throws IllegalArgumentException, SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            //start_date = giorno + "-" + mese + "-" + anno;
            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            // id credo si incrementi da solo
            query.add("SELECT start_date,end_date,id_person,id,is_renewed"
                    + " FROM subscription WHERE id_person = '"
                    + username + "'");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {

                //System.out.println(query.size());
                result = server.execute(query.get(0));

            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return null;
            }
            query.removeAllElements();
            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else if (rs.next()) {
                if (!rs.getBoolean("is_renewed")) //if(true)
                {
                    query.removeAllElements();
                    return rs.getString("id_person") + " "
                            + rs.getString("start_date") + " "
                            + rs.getString("end_date") + " "
                            + rs.getInt("id") + " " + rs.getBoolean("is_renewed");
                } else {
                    String id_person = rs.getString("id_person");
                    int id = rs.getInt("id");
                    boolean is_renewed = rs.getBoolean("is_renewed");
                    query.add("SELECT * FROM renew_subscription WHERE "
                            + "id_sub=" + id
                    );

                    try {
                        //System.out.println(query.size());
                        result = server.execute(query.get(0));
                        query.removeAllElements();
                    } catch (SQLException e) {
                        query.removeAllElements();
                        Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                        return null;
                    }
                    query.removeAllElements();
                    rs = (ResultSet) (result.getData());
                    String final_result
                            = id_person + " "
                            + rs.getString("start_date") + " "
                            + rs.getString("end_date") + " "
                            + id + " " + is_renewed;

                    return final_result;
                }
            }
            return null;
        }
    }

    /**
     * Verifica se un certo utente ha una iscrizione attiva ed inserita nel
     * database, nella tabella subscription.
     *
     * @param _user
     * @return boolean
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public boolean isSubscribtionActive(User _user) throws IllegalArgumentException, SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {
            username = _user.getUsername();

            giorno = String.valueOf(gc.get(Calendar.DAY_OF_MONTH)).toString();
            mese = String.valueOf(gc.get(Calendar.MONTH) + 1).toString();
            anno = String.valueOf(gc.get(Calendar.YEAR)).toString();
            anno_finale = String.valueOf(gc.get(Calendar.YEAR) + 5).toString();

            //start_date = giorno + "-" + mese + "-" + anno;
            start_date = anno + "-" + mese + "-" + giorno;
            end_date = anno_finale + "-" + mese + "-" + giorno;

            query.add("SELECT start_date,end_date,id_person,id,is_renewed,is_active"
                    + " FROM subscription WHERE id_person = '"
                    + username + "'");

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {
                // dimensione query System.out.println(query.size());
                result = server.execute(query.get(0));
            } catch (SQLException e) {
                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                return false;
            }
            query.removeAllElements();
            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else if (rs.next()) {
                return rs.getBoolean("is_active");
            }

        }
        return false;
    }
    
}
