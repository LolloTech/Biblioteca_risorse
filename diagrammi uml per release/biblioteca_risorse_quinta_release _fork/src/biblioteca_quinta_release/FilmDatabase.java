
package biblioteca_quinta_release;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Sviluppatore
 */
public class FilmDatabase  extends ModelDatabaseContainer{
   
    public FilmDatabase(ConcreteServer _server) {
        super(_server);
    }
    
    
    
    /**
     * Ritorna la lista di tutti i films che un certo utente ha in prestito
     *
     * @param user
     * @return ArrayList<FilmLoan>
     * @throws SQLException
     */
    public ArrayList<FilmLoan> getFilmsLoan(User user) throws SQLException {
        query.add("select person.username, person.name, film.identifier, film.license_number, film_loan.start_date, film_loan.id,"
                + "film_loan.end_date, film_loan.is_active as active, film_loan.is_renewed as renewed, category.renew_duration, category.loan_duration,"
                + "category.days_until_renew, category.identifier as cat_identifier from film, film_loan, person , category "
                + "where person.username = film_loan.id_person and film_loan.id_film = film.identifier and film_loan.is_active = 1 and"
                + " category.identifier = film.category_ident and person.username = '"
                + user.getUsername() + "'");

        Menu.printCyan(query.get(0));

        FilmLoan element = null;
        ArrayList<FilmLoan> elementi = null;

        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return null;
        }

        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            Menu.printCyan(e.getMessage());
            return null;
        }
        rs = (ResultSet) (result.getData());
        elementi = new ArrayList<>();
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                element = new FilmLoan();
                element.id_loan = rs.getInt("id");
                element.data_inizio_prestito = rs.getString("start_date");
                element.data_fine_prestito = rs.getString("end_date");
                element.is_active = rs.getBoolean("active");
                element.is_renewed = rs.getBoolean("renewed");
                element.user = new User(rs.getString("name"), rs.getString("username"));
                element.film = new Film();
                element.film.setLicenze(rs.getString("license_number"));
                element.film.setIdentifier(rs.getString("identifier"));
                element.film.setCategory(new Category(rs.getString("cat_identifier")));
                element.film.getCategory().setParameters(Integer.parseInt(rs.getString("loan_duration")),
                        Integer.parseInt(rs.getString("renew_duration")),
                        Integer.parseInt(rs.getString("days_until_renew")));
                elementi.add(element);
            }
        }
        for (int i = 0; i < elementi.size(); i++) {

            if (elementi.get(i).is_renewed) {
                result = server.execute(
                        "select person.username ,film.identifier, film.title, film_loan.is_active, film_loan.is_renewed, \n"
                        + "renew_film_loan.is_active as renew_is_active, renew_film_loan.is_renewed as renew_is_renew,\n"
                        + "renew_film_loan.start_date, renew_film_loan.end_date from film, film_loan, person, renew_film_loan\n"
                        + " where person.username = film_loan.id_person and film.identifier = film_loan.id_film \n"
                        + "AND film_loan.is_active = 1 and id_person = '" + elementi.get(i).user.getUsername()
                        + "' and film.identifier ='" + elementi.get(i).film.getIdentifier() + "'");
                rs = (ResultSet) (result.getData());
                if (rs.next()) {
                    elementi.get(i).data_inizio_proroga = rs.getString("start_date");
                    elementi.get(i).data_fine_proroga = rs.getString("end_date");
                }
            }
        }

        query.removeAllElements();
        return elementi;
    }

    public Integer getLoanedFilm(User user, Film film) throws SQLException {
        query.add("select count(*) as dim from film_loan, person, film where film.identifier = film_loan.id_film "
                + "and person.username = film_loan.id_person\n"
                + "and person.username = '" + user.getUsername() + "'and film_loan.is_active = 1");

        if (!server.isConnected()) {
            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return null;
        }
        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }
        rs = (ResultSet) (result.getData());
        Integer dim = null;
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                dim = rs.getInt("dim");
            }
        }

        query.removeAllElements();
        return dim;
    }

    /**
     * Verifica se il film passato come parametro e' disponibile al prestito
     *
     * @param Film
     * @return boolean
     * @throws SQLException
     */
    public boolean isFilmAvaiableForLoan(Film film) throws SQLException {
        query.add("select count(*) as dim, film.identifier from film_loan, film where film_loan.id_film = film.identifier"
                + " and film_loan.is_active = 1 and film.identifier = '"
                + film.getIdentifier() + "' group by film.identifier");

        try {
            result = server.execute(query.get(0));
        } catch (SQLException ex) {
            query.removeAllElements();
        }
        rs = (ResultSet) result.getData();
        if (rs.next()) {
            int dim = rs.getInt("dim");
            Menu.printRed("dim " + dim + " until " + film.getLicenze());
            if (Integer.parseInt(film.getLicenze()) > dim) {
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

    /**
     * Aggiunge un film nuovo, nel database, nella tabella film.
     *
     * @author
     * @param String, String, String
     * @return boolean
     */
    public boolean addFilm(String identifier, String license, String categoryIdent) {
        //int myInt = is_sub ? 1 : 0;
        query.add("INSERT INTO film "
                + "(identifier, license_number, title, category_ident) VALUES "
                + "('" + identifier + "','"
                + license + "','"
                + identifier + "','"
                + categoryIdent + "')"
        );
        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
        }
        /**
         * postcondizione
         */
        query.removeAllElements();
        return true;
    }

    /**
     * Aggiunge un film nuovo, nel database, nella tabella film.
     *
     * @author
     * @param Film
     * @return boolean
     * @attributi: - id - identifier - titolo - anno_pubblicazione - descrizione
     * - lingua - genere - numero_pagine - autori[] - licenze - casa_editrice -
     * is_avaiable: indica se e' disponibile considerando il n. di licenze -
     * is_active: indica se e' disponile nell'archivio
     *
     */
    public boolean addFilm(Film film) {
        int is_act = film.isActive() ? 1 : 0;
        int is_ava = film.isAvaiable() ? 1 : 0;
        query.add("INSERT INTO film "
                + "(id,identifier, title, production_year, description, languange, "
                + "genre, duration, actors, license_number, publisher, "
                + "film_maker, is_avaiable, is_active) VALUES "
                + "('"
                + film.getId() + "','"
                + film.getIdentifier() + "','"
                + film.getTitolo() + "','"
                + film.getAnno() + "','"
                + film.getDescrizione() + "','"
                + film.getLingua() + "','"
                + film.getGenere() + "','"
                + film.getDurata() + "','"
                + film.getAttori() + "','"
                + film.getLicenze() + "','"
                + film.getCasaProduzione() + "','"
                + film.getRegista() + "','"
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
        /**
         * postcondizione
         */
        query.removeAllElements();
        return true;
    }

    /**
     * Ritorna una lista di tutti i films presenti nel database, dalla tabella
     * film.
     *
     * @return ArrayList<Film>
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public ArrayList<Film> getFilmList() throws IllegalArgumentException, SQLException {
        ArrayList<Film> films = new ArrayList();
        query.add("SELECT * from film");
        //query.add("SELECT * from film WHERE is_active=1" );

        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {

            result = server.execute(query.get(0));
        } catch (SQLException e) {
            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }
        rs = (ResultSet) (result.getData());
        Film elemento = null;
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                elemento = new Film();
                elemento.setAnno(rs.getString("production_year"));
                elemento.setAttori(rs.getString("actors"));
                elemento.setCasaProduzione(rs.getString("publisher"));
                elemento.setGenere(rs.getString("genre"));
                elemento.setId(rs.getString("id"));
                elemento.setIsActive(rs.getString("is_active"));
                elemento.setIsAvaiable(rs.getString("is_avaiable"));
                elemento.setLicenze(rs.getString("license_number"));
                elemento.setDurata(rs.getString("duration"));
                elemento.setTitolo(rs.getString("title"));
                elemento.setIdentifier(rs.getString("identifier"));
                elemento.setRegista(rs.getString("film_maker"));
                elemento.setDesc(rs.getString("description"));
                elemento.setLingua(rs.getString("language"));
                //attenzione
                elemento.setCategory(this.getCategory(rs.getString("category_ident")));
                films.add(elemento);
            }
        }
        //ora procedo con le sottocategorie

        query.removeAllElements();
        return films;
    }

    public boolean removeLoan(User user, Film film, int id_loan) throws SQLException, ParseException {
        boolean flag = false;
        //per i libri che non hanno avuto rinnovo
        query.add("select film_loan.id_person, film_loan.start_date as 'l.start', film_loan.end_date as 'l.end',"
                + "film.identifier ,film_loan.is_active,film_loan.is_renewed from film_loan, film \n"
                + "where film_loan.id_film = film.identifier and film_loan.is_active=1 and is_renewed=0 "
                + "and film_loan.id_person ='" + user.getUsername()
                + "' and film_loan.id_film='" + film.getIdentifier() + "'");
        //per i libri che hanno avuto un rinnovo
        query.add("select film_loan.id_person, film_loan.start_date as 'l.start', film_loan.end_date as 'l.end'"
                + ", renew_film_loan.start_date as 'r.start', renew_film_loan.end_date as 'r.end',"
                + " film.identifier ,film_loan.is_active,film_loan.is_renewed from film_loan, film,renew_film_loan"
                + " where film_loan.id_film = film.identifier and renew_film_loan.id_film = film.identifier and"
                + " film_loan.is_renewed = 1 and film_loan.is_active = 1 and"
                + " film_loan.id_person ='" + user.getUsername() + "' and film.identifier='" + film.getIdentifier()
                + "'");
        query.add("update film_loan\n"
                + "set is_active = 0\n"
                + "where film_loan.id IN ( select film_loan.id from film, film_loan, person \n"
                + "where film.identifier = film_loan.id_film \n"
                + "and person.username = film_loan.id_person\n"
                + "and person.username = '"
                + user.getUsername() + "' "
                + "and film.identifier ='" + film.getIdentifier()
                + "')");
        query.add("update renew_film_loan\n"
                + "set is_active = 0\n"
                + "where renew_film_loan.id IN ( select renew_film_loan.id from film, film_loan, person,renew_film_loan \n"
                + "where film_loan.id_film = renew_film_loan.id_film\n "
                + "and film.identifier = film_loan.id_film \n"
                + "and person.username = film_loan.id_person\n"
                + "and person.username = '"
                + user.getUsername() + "' "
                + "and film.identifier ='" + film.getIdentifier()
                + "' and renew_film_loan.id_film=" + id_loan
                + ")");
        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {
            //prima i libri senza proroghe
            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            Menu.printRed(e.getMessage());
            return false;
        }

        rs = (ResultSet) (result.getData());

        if (rs == null) {
            Menu.printRed("rs null in update, da controllare");
        }

        while (rs.next()) {
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
            if (confronto) {
                Menu.printCyan("confronto falso!");
            } else {

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
        if (rs == null) {
            Menu.printRed("rs null in update, da controllare");
        }

        while (rs.next()) {
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
            if (confronto) {
                Menu.printCyan("confronto falso!");
            } else {
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
     * Effettua l'update dei dati del film passato, facendo un update nel
     * database, nella tabella film.
     *
     * @param libro
     * @return boolean
     */
    public boolean updateFilm(Film film) {
        int is_act = film.isActive() ? 1 : 0;
        int is_ava = film.isAvaiable() ? 1 : 0;
        query.add("UPDATE film SET "
                + "title='" + film.getTitolo() + "',"
                + "production_year='" + film.getAnno() + "',"
                + "description='" + film.getDescrizione() + "',"
                + "language='" + film.getLingua() + "',"
                + "film_maker='" + film.getRegista() + "',"
                + "genre='" + film.getGenere() + "',"
                + "actors='" + film.getAttori() + "',"
                + "duration='" + film.getLicenze() + "',"
                + "publisher='" + film.getCasaProduzione() + "',"
                + "is_avaiable='" + is_ava + "',"
                + "is_active='" + is_act
                + "' WHERE identifier='"
                + film.getIdentifier() + "';"
        );

        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
            return false;
        }
        /**
         * postcondizione
         */
        query.removeAllElements();
        return true;
    }

    /**
     * Crea un nuovo prestito, ovvero un nuovo record nella tabella film_loan,
     * inserendo dati dell'utente e del film passatogli per parametro.
     *
     * @param _user
     * @param _film
     * @return boolean
     * @throws SQLException
     */
    public boolean addFilmLoan(User _user, Film _film) throws SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {
            String checkLoan = "SELECT count(*) as dim FROM film_loan, film, person WHERE film_loan.id_film = film.identifier"
                    + " AND film_loan.id_person = person.username and film_loan.id_person ='" + _user.getUsername()
                    + "' AND film_loan.id_film='" + _film.getIdentifier() + "';";

            //questo avviene quando un utente ha gia' in prestito
            //tale film, ed il prestito gli viene impedito.
            result = server.execute(checkLoan);
            rs = (ResultSet) (result.getData());
            if (rs.next()) {
                if (rs.getInt("dim") > 0) {
                    return false;
                }
            }

            // questa query seleziona quanti film di una data categoria, data da _film,
            // ha preso in prestito l'utente _user
            query.add("select count(*) as dim from film_loan, person, film, category where film.identifier = film_loan.id_film"
                    + " and film.category_ident = category.identifier and person.username = film_loan.id_person\n"
                    + "and person.username = '" + _user.getUsername() + "' "
                    + "and film_loan.is_active = 1 and "
                    + "category_ident ='" + _film.getCategory().getIdentifier() + "'"
            );
            String identifier;
            // questo if serve in quanto le categorie padri non hanno un padre
            // con un riferimento corretto
            if (_film.getCategory().getFather().getIdentifier().equals("")) {
                identifier = _film.getCategory().getIdentifier();
            } else {
                identifier = _film.getCategory().getFather().getIdentifier();
            }
            // seleziona la massima durata di prestito da una categoria data
            query.add("select max_loans_to_person from category where category.identifier"
                    + " = '"
                    + identifier + "'");

            // seleziona la durata di un prestito di un certo libro, data da 
            // una certa categoria
            query.add("select category.loan_duration from film, category where "
                    + "film.category_ident = category.identifier and "
                    + "film.identifier='" + _film.getIdentifier()
                    + "'");

            if (!server.isConnected()) {

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

            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else {
                count_libri_utente = rs.getInt("dim");
            }
            rs.close();
            //eseguo la seconda query
            result = server.execute(query.get(1));

            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else {
                count_libri_prestabili = Integer.parseInt(rs.getString("max_loans_to_person"));
            }
            rs.close();
            //eseguo la terza query
            result = server.execute(query.get(2));

            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else {
                durata_giorni = Integer.parseInt(rs.getString("loan_duration"));
            }
            Menu.printRed("giorni");

            System.out.println(durata_giorni);

            cal_future.add(Calendar.DAY_OF_MONTH, durata_giorni);

            // calcolo agilmente le date da inserire nel database
            start_date = sdf_today.format(cal_today.getTime());
            end_date = sdf_future.format(cal_future.getTime());
            rs.close();
            query.add("INSERT INTO film_loan "
                    + "(id_person,id_film,start_date,end_date,is_renewed,is_active) "
                    + "VALUES ("
                    + "'" + _user.getUsername() + "',"
                    + "'" + _film.getIdentifier() + "',"
                    + "'" + start_date + "',"
                    + "'" + end_date + "',"
                    + "'" + 0 + "',"
                    + "'" + 1 + "')"
            );

            Menu.printRed("oggi: " + start_date);
            Menu.printRed("fuguro: " + end_date);

            Menu.printCyan("libri utente e prestabili : " + count_libri_utente + " "
                    + "count_libri_prestabili : " + count_libri_prestabili);
            if (count_libri_prestabili > count_libri_utente) {
                Menu.print("La risorsa e' prestabile!");
                //qui posso prestare il libro all'utente
                result = server.execute(query.get(3));
                rs = (ResultSet) (result.getData());
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
     *
     * @param _user
     * @param _book
     * @param _id_loan
     * @return boolean
     * @throws SQLException
     * @throws ParseException
     */
    public boolean renewLoan(User _user, Film _film, int _id_loan) throws SQLException, ParseException {
        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            int differenza_giorni = 0;
            username = _user.getUsername();
            String query_start_date = "";
            String query_end_date = "";
            Menu.printRed("dudu: " + _film.getCategory().RENEW_DURATION);
            cal_future.add(Calendar.DAY_OF_MONTH, _film.getCategory().RENEW_DURATION);

            // calcolo agilmente le date da inserire nel database
            start_date = sdf.format(cal_today.getTime());
            end_date = sdf.format(cal_future.getTime());

            Date end = new Date();

            //controllo di sicurezza dell'esistenza del prestito
            query.add("SELECT start_date,end_date FROM film_loan"
                    + " WHERE id_film ='" + _film.getIdentifier()
                    + "' AND is_active=1 AND id_person='"
                    + _user.getUsername() + "'");

            //controllo di sicurezza dell'esistenza del prestito
            query.add("SELECT count(*) AS dim FROM renew_film_loan "
                    + "WHERE id_film ='" + _film.getIdentifier()
                    + "' AND is_active=1 AND id_loan =" + _id_loan);

            query.add("UPDATE film_loan SET "
                    + "is_renewed='" + 1 + "'"
                    + "WHERE id_person ='"
                    + _user.getUsername() + "' AND id=" + _id_loan + " AND "
                    + "id_film ='"
                    + _film.getIdentifier() + "';"
            );

            //query per il rinnovo del prestito del libro
            query.add("INSERT INTO renew_film_loan "
                    + "(id_film,start_date,end_date,is_active,id_loan) "
                    + "VALUES ("
                    + "'" + _film.getIdentifier() + "',"
                    + "'" + start_date + "',"
                    + "'" + end_date + "',"
                    + "'" + 1 + "',"
                    + _id_loan + ")"
            );

            if (!server.isConnected()) {

                query.removeAllElements();
                Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
                return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
            }

            try {

                result = server.execute(query.get(0));
                rs = (ResultSet) (result.getData());
                if (rs == null) {
                    System.out.println("fail");
                } else if (rs.next()) {
                    //prendo le date della riga ottenuta nella tabella prestiti,
                    //che poi utilizzero' per confrontare i giorni
                    query_start_date = rs.getString("start_date");
                    query_end_date = rs.getString("end_date");
                    //Questa e' la data chemi serve
                    end = sdf.parse(query_end_date);

                } else {
                    query.removeAllElements();
                    return false;
                }

                differenza_giorni = (int) this.getDiffDate(
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

            try {
                result = server.execute(query.get(1));
            } catch (SQLException e) {

                query.removeAllElements();
                Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
                Menu.printCyan(e.getMessage());
                return false;
            }
            rs = (ResultSet) (result.getData());
            if (rs == null) {
                System.out.println("fail");
            } else if (rs.next()) {
                //UPDATE loan

                //output di sicurezza                 
                Menu.printGreen("differenza in giorni: " + differenza_giorni);
                Menu.printGreen("tempo categoria: " + _film.getCategory().DAYS_UNTIL_RENEW);
                Menu.printGreen("start date : " + start_date);
                Menu.printGreen("end_date: " + end_date);

                Menu.printGreen("qstart date : " + query_start_date);
                Menu.printGreen("qend_date: " + query_end_date);
                if (rs.getInt("dim") > 0) {
                    query.removeAllElements();
                    return false;
                }
                //INSERT in renew_loan

                try {
                    if (differenza_giorni >= 0 && differenza_giorni <= _film.getCategory().DAYS_UNTIL_RENEW) {
                        result = server.execute(query.get(2));
                        result = server.execute(query.get(3));
                        rs = (ResultSet) (result.getData());
                    } else {
                        query.removeAllElements();
                        return false;
                    }
                } catch (SQLException e) {
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
     * Ritorna una determinata categoria, individuata da uno specifico
     * identificatore.
     *
     * @param identifier
     * @return Category
     * @throws SQLException
     */
    public Category getCategory(String identifier) throws SQLException {
        ArrayList<Category> categorie = new ArrayList();
        String richiesta = "SELECT * from category WHERE identifier='" + identifier
                + "'";
        Result result1 = new Result();
        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return null; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {
            //System.out.println(query.size());
            result1 = server.execute(richiesta);
        } catch (SQLException e) {
            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return null;
        }
        ResultSet rs1 = (ResultSet) (result1.getData());
        String nome = null;
        String desc = null;
        String ident = null;
        String father = null;
        if (rs1 == null) {
            System.out.println("fail");
        } else {
            while (rs1.next()) {
                nome = rs1.getString("name");
                desc = rs1.getString("description");
                ident = rs1.getString("identifier");
                father = rs1.getString("parent");

            }
        }
        return new Category(ident, nome, desc, father);
    }
    
        /**
     * Ritorna un vettore associativo da due elementi, contenenti l'id del libro
     * piu' richiesto come prestito, e il contatore ad esso associato, per un
     * certo anno solare.
     *
     * @param year
     * @return Map<String, String>
     * @throws SQLException
     */
    public Map<String, String> getMostLoanedFilm(String year) throws SQLException {
        Map<String, String> attributi = new HashMap<>();
        String query = "select count(*) as dim, id_film from film_loan group by film_loan.id_film "
                + "having film_loan.start_date between '" + year + "-01-01' and '"
                + year + "-12-31'" + " order by count(*) desc";
        try {
            result = server.execute(query);
            rs = (ResultSet) (result.getData());

        } catch (SQLException ex) {
            Menu.printRed(ex.getMessage());
        }
        if (rs.next()) {
            try {
                attributi.put("dim", rs.getString("dim"));
                attributi.put("id_book", rs.getString("id_film"));
            } catch (SQLException ex) {
                Menu.printRed(ex.getMessage());
            }
        }
        return attributi;
    }

    /**
     * *
     * Ritorna il numero di films prestati per utente, per un certo anno solare.
     *
     * @param year
     * @return ArrayList<Map<String,String>>
     * @throws SQLException
     */
    public ArrayList<Map<String, String>> getFilmsLoansPerUser(String year) throws SQLException {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        String query = "select count(*) as dim, film_loan.id_person from film_loan "
                + "group by film_loan.id_person having film_loan.start_date between '"
                + year + "-01-01' and '" + year + "-12-31' order by count(*) desc";
        try {
            result = server.execute(query);
            rs = (ResultSet) (result.getData());
        } catch (SQLException ex) {
            Menu.printRed(ex.getMessage());
        }
        while (rs.next()) {
            Map<String, String> demo = new HashMap<>();
            demo.put("dim", rs.getString("dim"));
            demo.put("id_person", rs.getString("id_person"));

            array.add(demo);

        }
        return array;
    }

    /**
     * Ritorna il numero di prestiti di film, per anno solare.
     *
     * @param year
     * @return
     * @throws SQLException
     */
    public Integer getFilmLoansForYear(String year) throws SQLException {
        query.add("select count(*) as dim from film_loan where film_loan.start_date "
                + "between '" + year + "-01-01' and '" + year + "-12-31'");
        result = server.execute(query.get(0));
        rs = (ResultSet) (result.getData());
        query.removeAllElements();
        if (rs.next()) {
            return rs.getInt("dim");
        } else {
            return null;
        }
    }

    /**
     * Ritorna il numero di proroghe di prestiti di film, per anno solare.
     *
     * @param year
     * @return
     * @throws SQLException
     */
    public Integer getFilmRenewLoansForYear(String year) throws SQLException {
        query.add("select count(*) as dim from renew_film_loan where renew_film_loan.start_date "
                + "between '" + year + "-01-01' and '" + year + "-12-31'");
        result = server.execute(query.get(0));
        rs = (ResultSet) (result.getData());
        query.removeAllElements();
        if (rs.next()) {
            return rs.getInt("dim");
        } else {
            return null;
        }
    }
   
}
