
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
public class BookDatabase extends ModelDatabaseContainer {
    
    public BookDatabase(ConcreteServer _server) {
        super(_server);
    }
    
    /**
     * Ritorna una lista di tutti i libri presenti nel database, dalla tabella
     * book.
     *
     * @return ArrayList<Book>
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public ArrayList<Book> getBooksList() throws IllegalArgumentException, SQLException {
        ArrayList<Book> libri = new ArrayList();
        query.add("SELECT * from book");
        //query.add("SELECT * from book WHERE is_active=1" );

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
        Book elemento = null;
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
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
                elemento.setDesc(rs.getString("description"));
                //attenzione
                elemento.setCategory(this.getCategory(rs.getString("category_ident")));
                libri.add(elemento);
            }
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
    public boolean addBook(String identifier, String license, String categoryIdent) {
        //int myInt = is_sub ? 1 : 0;
        query.add("INSERT INTO book "
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
     * Aggiunge un libro nuovo, nel database, nella tabella book.
     *
     * @author
     * @param libro
     * @return boolean
     * @attributi: - id - identifier - titolo - anno_pubblicazione - descrizione
     * - lingua - genere - numero_pagine - autori[] - licenze - casa_editrice -
     * is_avaiable: indica se e' disponibile considerando il n. di licenze -
     * is_active: indica se e' disponile nell'archivio
     *
     */
    public boolean addBook(Book libro) {
        int is_act = libro.isActive() ? 1 : 0;
        int is_ava = libro.isAvaiable() ? 1 : 0;
        query.add("INSERT INTO book "
                + "(id,identifier, title, publication_year, description, languange, "
                + "genre, page_number, authors, license_number, publisher, is_avaiable, is_active) VALUES "
                + "('"
                + libro.getId() + "','"
                + libro.getIdentifier() + "','"
                + libro.getTitolo() + "','"
                + libro.getAnno() + "','"
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
        /**
         * postcondizione
         */
        query.removeAllElements();
        return true;
    }

    /**
     * Effettua l'update dei dati del libro passato, facendo un update nel
     * database, nella tabella book.
     *
     * @param libro
     * @return boolean
     */
    public boolean updateBook(Book libro) {
        int is_act = libro.isActive() ? 1 : 0;
        int is_ava = libro.isAvaiable() ? 1 : 0;
        query.add("UPDATE book SET "
                + "title='" + libro.getTitolo() + "',"
                + "publication_year='" + libro.getAnno() + "',"
                + "description='" + libro.getDescrizione() + "',"
                + "language='" + libro.getLingua() + "',"
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
        /**
         * postcondizione
         */
        query.removeAllElements();;
        return true;
    }
    /**
     * Crea un nuovo prestito, ovvero un nuovo record nella tabella loan,
     * inserendo dati dell'utente e del libro passatogli per parametro.
     *
     * @param _user
     * @param _book
     * @return boolean
     * @throws SQLException
     */
    public boolean addBookLoan(User _user, Book _book) throws SQLException {

        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {
            String checkLoan = "SELECT count(*) as dim FROM loan, book, person WHERE loan.id_book = book.identifier"
                    + " AND loan.id_person = person.username and loan.id_person ='" + _user.getUsername()
                    + "' AND loan.id_book='" + _book.getIdentifier() + "';";

            //questo avviene quando un utente ha gia' in prestito
            //tale libro, ed il prestito gli viene impedito.
            result = server.execute(checkLoan);
            rs = (ResultSet) (result.getData());
            if (rs.next()) {
                if (rs.getInt("dim") > 0) {
                    return false;
                }
            }

            // questa query seleziona quanti libri di una data categoria, data da _book,
            // ha preso in prestito l'utente _user
            query.add("select count(*) as dim from loan, person, book, category where book.identifier = loan.id_book"
                    + " and book.category_ident = category.identifier and person.username = loan.id_person\n"
                    + "and person.username = '" + _user.getUsername() + "' "
                    + "and loan.is_active = 1 and "
                    + "category_ident ='" + _book.getCategory().getIdentifier() + "'"
            );
            String identifier;
            // questo if serve in quanto le categorie padri non hanno un padre
            // con un riferimento corretto
            if (_book.getCategory().getFather().getIdentifier().equals("")) {
                identifier = _book.getCategory().getIdentifier();
            } else {
                identifier = _book.getCategory().getFather().getIdentifier();
            }
            // seleziona la massima durata di prestito da una categoria data
            query.add("select max_loans_to_person from category where category.identifier"
                    + " = '"
                    + identifier + "'");

            // seleziona la durata di un prestito di un certo libro, data da 
            // una certa categoria
            //query.add("select category.loan_duration from loan, book, category where "
            query.add("select category.loan_duration from book, category where "
                    + "book.category_ident = category.identifier and "
                    + "book.identifier='" + _book.getIdentifier()
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
    public boolean renewLoan(User _user, Book _book, int _id_loan) throws SQLException, ParseException {
        if (_user == null) {
            throw new IllegalArgumentException("Il parametro inserito ha un riferimento a NULL!");
        } else {

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
                    + _user.getUsername() + "'");

            //controllo di sicurezza dell'esistenza del prestito
            query.add("SELECT count(*) AS dim FROM renew_loan "
                    + "WHERE id_book ='" + _book.getIdentifier()
                    + "' AND is_active=1 AND id_loan =" + _id_loan);

            query.add("UPDATE loan SET "
                    + "is_renewed='" + 1 + "'"
                    + "WHERE id_person ='"
                    + _user.getUsername() + "' AND id=" + _id_loan + " AND "
                    + "id_book ='"
                    + _book.getIdentifier() + "';"
            );

            //query per il rinnovo del prestito del libro
            query.add("INSERT INTO renew_loan "
                    + "(id_book,start_date,end_date,is_active,id_loan) "
                    + "VALUES ("
                    + "'" + _book.getIdentifier() + "',"
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
                Menu.printGreen("tempo categoria: " + _book.getCategory().DAYS_UNTIL_RENEW);
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
                    if (differenza_giorni >= 0 && differenza_giorni <= _book.getCategory().DAYS_UNTIL_RENEW) {
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
     * Verifica se un libro passato per parametro e' disponibile al prestito.
     * Funziona settando ad 1 il parametro is_active, della tabella loan e
     * renew_loan
     *
     * @param book
     * @return boolean
     * @throws SQLException
     */
    public boolean isBookAvaiable(Book book) throws SQLException {
        query.add("select *,count(*) as dim from loan, book where loan.id_book = book.identifier "
                + "and loan.is_active = 1 and book.identifier = '" + book.getIdentifier()
                + "' group by book.identifier");
        if (!server.isConnected()) {

            query.removeAllElements();
            Menu.printCyan("ERRORE DI CONNESSIONE IN SUBSCRIBE");
            return false; // ritorna false se non e' connesso, chi chiama questo metodo deve controllare se riceve null
        }

        try {
            result = server.execute(query.get(0));
        } catch (SQLException e) {

            query.removeAllElements();
            Menu.printCyan("ATTENZIONE!! ERRORE DI ESECUZIONE DELLA QUERY!!");
            return false;
        }

        rs = (ResultSet) (result.getData());

        if (rs == null) {
            System.out.println("fail");
        }
        if (rs.next()) {
            if (Integer.parseInt(book.getLicenze()) > rs.getInt("dim")) {
                query.removeAllElements();
                return true;
            }
        }
        query.removeAllElements();
        return false;

    }

    /**
     * Rimuove un prestito da parte di un utente e di un certo libro, sia dalla
     * tabella loan che dalla tabella renew_loan
     *
     * @param user
     * @param book
     * @param id_loan
     * @return boolean
     * @throws SQLException
     * @throws ParseException
     */
    public Integer getLoanedBook(User user, Book book) throws SQLException {
        query.add("select count(*) as dim from loan, person, book where book.identifier = loan.id_book and person.username = loan.id_person\n"
                + "and person.username = '" + user.getUsername() + "'and loan.is_active = 1");

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
     * Ritorna la lista di tutti i libri che un certo utente ha in prestito
     *
     * @param user
     * @return ArrayList<Loan>
     * @throws SQLException
     */
    public ArrayList<Loan> getBooksLoan(User user) throws SQLException {
        query.add("select person.username, person.name, book.identifier, book.license_number, loan.start_date, loan.id,"
                + "loan.end_date, loan.is_active as active, loan.is_renewed as renewed, category.renew_duration, category.loan_duration,"
                + "category.days_until_renew, category.identifier as cat_identifier from book, loan, person , category "
                + "where person.username = loan.id_person and loan.id_book = book.identifier and loan.is_active = 1 and"
                + " category.identifier = book.category_ident and person.username = '"
                + user.getUsername() + "'");

        Menu.printCyan(query.get(0));

        Loan element = null;
        ArrayList<Loan> elementi = null;

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
        elementi = new ArrayList<>();
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                element = new Loan();
                element.id_loan = rs.getInt("id");
                element.data_inizio_prestito = rs.getString("start_date");
                element.data_fine_prestito = rs.getString("end_date");
                element.is_active = rs.getBoolean("active");
                element.is_renewed = rs.getBoolean("renewed");
                element.user = new User(rs.getString("name"), rs.getString("username"));
                element.libro = new Book();
                element.libro.setLicenze(rs.getString("license_number"));
                element.libro.setIdentifier(rs.getString("identifier"));
                element.libro.setCategory(new Category(rs.getString("cat_identifier")));
                element.libro.getCategory().setParameters(Integer.parseInt(rs.getString("loan_duration")),
                        Integer.parseInt(rs.getString("renew_duration")),
                        Integer.parseInt(rs.getString("days_until_renew")));
                elementi.add(element);
            }
        }
        for (int i = 0; i < elementi.size(); i++) {

            if (elementi.get(i).is_renewed) {
                result = server.execute(
                        "select person.username ,book.identifier, book.title, loan.is_active, loan.is_renewed, \n"
                        + "renew_loan.is_active as renew_is_active, renew_loan.is_renewed as renew_is_renew,\n"
                        + "renew_loan.start_date, renew_loan.end_date from book, loan, person, renew_loan\n"
                        + " where person.username = loan.id_person and book.identifier = loan.id_book \n"
                        + "AND loan.is_active = 1 and id_person = '" + elementi.get(i).user.getUsername()
                        + "' and book.identifier ='" + elementi.get(i).libro.getIdentifier() + "'");
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

    /**
     * Verifica se il libro passato come parametro e' disponibile al prestito
     *
     * @param book
     * @return boolean
     * @throws SQLException
     */
    public boolean isBookAvaiableForLoan(Book book) throws SQLException {
        query.add("select count(*) as dim, book.identifier from loan, book where loan.id_book = book.identifier"
                + " and loan.is_active = 1 and book.identifier = '"
                + book.getIdentifier() + "' group by book.identifier");

        try {
            result = server.execute(query.get(0));
        } catch (SQLException ex) {
            query.removeAllElements();
        }
        rs = (ResultSet) result.getData();
        if (rs.next()) {
            int dim = rs.getInt("dim");
            if (Integer.parseInt(book.getLicenze()) > dim) {
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

    public boolean removeLoan(User user, Book book, int id_loan) throws SQLException, ParseException {
        boolean flag = false;
        //per i libri che non hanno avuto rinnovo
        query.add("select loan.id_person, loan.start_date as 'l.start', loan.end_date as 'l.end',"
                + "book.identifier ,loan.is_active,loan.is_renewed from loan, book \n"
                + "where loan.id_book = book.identifier and loan.is_active=1 and is_renewed=0 "
                + "and loan.id_person ='" + user.getUsername()
                + "' and loan.id_book='" + book.getIdentifier() + "'");
        //per i libri che hanno avuto un rinnovo
        query.add("select loan.id_person, loan.start_date as 'l.start', loan.end_date as 'l.end'"
                + ", renew_loan.start_date as 'r.start', renew_loan.end_date as 'r.end',"
                + " book.identifier ,loan.is_active,loan.is_renewed from loan, book,renew_loan"
                + " where loan.id_book = book.identifier and renew_loan.id_book = book.identifier and"
                + " loan.is_renewed = 1 and loan.is_active = 1 and"
                + " loan.id_person ='" + user.getUsername() + "' and book.identifier='" + book.getIdentifier()
                + "'");
        query.add("update loan\n"
                + "set is_active = 0\n"
                + "where loan.id IN ( select loan.id from book, loan, person \n"
                + "where book.identifier = loan.id_book \n"
                + "and person.username = loan.id_person\n"
                + "and person.username = '"
                + user.getUsername() + "' "
                + "and book.identifier ='" + book.getIdentifier()
                + "')");
        query.add("update renew_loan\n"
                + "set is_active = 0\n"
                + "where renew_loan.id IN ( select renew_loan.id from book, loan, person,renew_loan \n"
                + "where loan.id_book = renew_loan.id_book\n "
                + "and book.identifier = loan.id_book \n"
                + "and person.username = loan.id_person\n"
                + "and person.username = '"
                + user.getUsername() + "' "
                + "and book.identifier ='" + book.getIdentifier()
                + "' and renew_loan.id_loan=" + id_loan
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
    public Map<String, String> getMostLoanedBook(String year) throws SQLException {
        Map<String, String> attributi = new HashMap<>();
        String query = "select count(*) as dim, id_book from loan group by loan.id_book "
                + "having loan.start_date between '" + year + "-01-01' and '"
                + year + "12-31'" + " order by count(*) desc";
        try {
            result = server.execute(query);
            rs = (ResultSet) (result.getData());

        } catch (SQLException ex) {
            Menu.printRed(ex.getMessage());
        }
        if (rs.next()) {
            try {
                attributi.put("dim", rs.getString("dim"));
                attributi.put("id_book", rs.getString("id_book"));
            } catch (SQLException ex) {
                Menu.printRed(ex.getMessage());
            }
        }
        return attributi;
    }
    /**
     * *
     * Ritorna il numero di libri prestati per utente, per un certo anno solare.
     *
     * @param year
     * @return ArrayList<Map<String,String>>
     * @throws SQLException
     */
    public ArrayList<Map<String, String>> getBookLoansPerUser(String year) throws SQLException {
        ArrayList<Map<String, String>> array = new ArrayList<>();
        String query = "select count(*) as dim, loan.id_person from loan "
                + "group by loan.id_person having loan.start_date between '"
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
     * Ritorna il numero di prestiti di libri per anno solare.
     *
     * @param year
     * @return
     * @throws SQLException
     */
    public Integer getBookLoansForYear(String year) throws SQLException {
        query.add("select count(*) as dim from loan where loan.start_date between '"
                + year + "-01-01' and '" + year + "-12-31'");
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
     * Ritorna il numero di proroghe di prestiti di libri, per anno solare.
     *
     * @param year
     * @return
     * @throws SQLException
     */
    public Integer getBookRenewLoansForYear(String year) throws SQLException {
        query.add("select count(*) as dim from renew_loan where renew_loan.start_date "
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
