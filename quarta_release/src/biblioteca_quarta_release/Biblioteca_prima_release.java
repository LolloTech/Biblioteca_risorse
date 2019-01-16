
package biblioteca_quarta_release;

/**
 * 
 * Questo e' il punto di accesso al programma, il main e' cotenuto qui.
 * @author Sviluppatore
 * 
 */




import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Biblioteca_prima_release {

    public static void print(Object x)
    {
        System.out.println(x.toString());
    }
    
    
    
    public static void main(String[] args) throws IllegalArgumentException, SQLException, ParseException 
    {
        Menu.print("--> QUARTA RELEASE");
        ConcreteServer x = new ConcreteServer();
        boolean is_operator = false;
        boolean is_user = false;
        boolean is_logged = false;
        
        String risposta;
        User utente = null;
        
        
        Info info = new Info(x);
        
        
        for(int l=0;l<3;l++)
            try {
                x.insertDumb();
            } catch (SQLException ex) {
                Menu.printDebug( "insert dumb fail: " + ex.getMessage());
            }
        try {
        ResultSet rs = (ResultSet) x.execute("").getData();
            while (rs.next()) {
                Menu.printDebug(rs.getInt("id")          +  "\t"     + 
                                   rs.getString("name")     +  "\t"     +
                                   rs.getString("surname"));
            }
        } catch (SQLException e) {
            Menu.printDebug("from main: " + e.getMessage());
            
            
        }
        try {
            ResultSet rs = (ResultSet) x.execute("SELECT * FROM person LIMIT 0,30").getData();
            //sets the directionability of the scroll of the iterator of the results
            
            while (rs.next()) {
                Menu.printGreen( rs.getString("password")          +  "\t"     + 
                                   rs.getString("name")     +  "\t"     +
                                   rs.getString("surname"));
            }
 
            
        } catch (SQLException e) 
        {
            Menu.print("from main: " + e.getMessage());    
        }
        
        try 
        {
            Vector<String> lista = info.getPersonList();
            
            for(int l=0; l < lista.size(); l++)
            {
                Menu.printPurple(lista.get(l));
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Biblioteca_prima_release.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /*
        * Invoco il metodo che mi ritorna tutte le categorie e sottocategorie
        * presenti nel file "categories.txt". getCategoriesFromFile() preleva
        * le categorie dal file, mentre getCategoriesHandler() ritorna il
        * riferimento al file "categories.txt", che viene utilizzato da 
        * getCategoriesFromFile() per leggere le categorie e sottocategorie
        * dal file. e ritornarle in un ArrayList<Category>.
        *
        * addCategory(): inserisce una categoria dentro al database.
        * updateCategory(): modifica una categoria all'interno del database.
        */
        ArrayList<Category> array = null;
        try {
            array = info.getCategoriesFromFile();
        } catch (FileNotFoundException ex) {
            Menu.print("File non trovato! ");
            System.exit(-1);
        }
        for(int l=0;l< array.size();l++) {
            Menu.printPurple(" lalala - " + array.get(l).toString());
            for(int duda=0;duda < array.get(l).getSubCategory().size();duda++)
                Menu.printPurple(" memem - " + array.get(l).getSubCategory().get(duda).toString());
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
	String date = sdf.format(new Date()); 
	System.out.println(date);
        
        sdf.getCalendar().add(Calendar.DAY_OF_MONTH, 30);  	
        
        if(array != null)
            for(int l=0;l< array.size();l++)
            {
                //controllo se effettivamente non sia una sottocategoria
                info.addCategory(
                        array.get(l).getName(),
                        array.get(l).getDesc(), 
                        false,
                        "",
                        array.get(l).getIdentifier(),
                        array.get(l).getLoanDuration(),
                        array.get(l).getRenewDuration(),
                        array.get(l).getDaysUntilRenew(),
                        array.get(l).getMaxLoansToPerson());
                        Menu.printGreen("qui sono nel for, loans=" + array.get(l).getMaxLoansToPerson());
                
                //nel caso in cui fosse una sottocategoria
                for(int m=0; m < array.get(l).getSubCategory().size();m++)
                    //if()
                            info.addCategory(
                        array.get(l).getSubCategory().get(m).getName(),
                        array.get(l).getSubCategory().get(m).getDesc(), 
                        true,
                        array.get(l).getIdentifier(),
                        array.get(l).getSubCategory().get(m).getIdentifier(),
                        //array.get(l).getLoanDuration(),
                        array.get(l).getSubCategory().get(m).getLoanDuration(),
                       // array.get(l).getRenewDuration(),
                        array.get(l).getSubCategory().get(m).getRenewDuration(),
                        //array.get(l).getDaysUntilRenew(),
                        array.get(l).getSubCategory().get(m).getDaysUntilRenew(),
                        //array.get(l).getMaxLoansToPerson());
                        array.get(l).getSubCategory().get(m).getMaxLoansToPerson()
                );
                        Menu.printGreen("qui sono nel for m, loans=" + array.get(l).getMaxLoansToPerson());
            }
        
        ArrayList<Category> prova = info.getCategoriesList();
        for(int l=0;l < prova.size();l++) {
            Menu.printPurple(" prova_lalala - " + prova.get(l).toString());
            for(int duda=0;duda < prova.get(l).getSubCategory().size();duda++)
                Menu.printPurple(" prova_memem - " + prova.get(l).getSubCategory().get(duda).toString());
        }
        //info.addCategory("categoria", "di prova",false, "");
        //info.updateCategory("categoria", "gigaedit", false, "lel");
        info.addOperator(new User("default","default","default","default"));


        ArrayList<Book> libri = info.getBooksList();
        for(int i=0;i < libri.size(); i ++)
        {
            Menu.printRed("libro: " + libri.get(i).getTitolo() + " " +
                    libri.get(i).getLicenze() + " cat: " +
                    libri.get(i).getCategory().getIdentifier());
        }
        //stampo a video il menu' principale di tutto il programma.
        Menu menu = new Menu();
        Menu.print("Oggi e' il " + info.getCurrentDate());
        //Loop principale del programma        
        //confronto tra interi, non tra booleani
        
        ArrayList<Loan> elementi = info.getBooksLoan(new User("dada","dada","dada","dada"));
        for(int dudendango = 0; dudendango < elementi.size(); dudendango++)
            Menu.printGreen("id prestito: " + elementi.get(dudendango).id_loan +
                            "inizio: " + elementi.get(dudendango).data_inizio_prestito +
                            " fine: " + elementi.get(dudendango).data_fine_prestito +
                            " is_renewed: " + elementi.get(dudendango).is_renewed +
                            "inizio proroga: " + elementi.get(dudendango).data_inizio_prestito +
                            " fine proroga: " + elementi.get(dudendango).data_fine_prestito +
                            " is_renewed: " + elementi.get(dudendango).is_renewed +
                            " book name: " + elementi.get(dudendango).libro.getIdentifier()
                            );
        while(menu.getStatus() == 1)
        {
            
            //E' un operatore loggato
            if(is_operator && is_logged)
            {
                risposta = Menu.operatorMenu();
                //visualizza gli utenti registrati
                if(risposta.equals("1"))
                {
                    String attivo = null;
                    ArrayList attivi =
                            new ArrayList(info.getActiveUserList());
                    Menu.print("Nome - Cognome - Username");
                    for(int m = 0; m < attivi.size(); m++)
                        Menu.print(attivi.get(m).toString());
                    
                }
                //aggiunta operatore
                else if(risposta.equals("2"))
                {
                    String input =
                        Menu.getLine("Inserisci il nome, cognome, username e " +
                        "password, separati da uno spazio");
                    if(info.addOperator(new User(input)))
                        Menu.print("Nuovo operatore aggiunto con successo!");
                    else
                        Menu.print("Errore in fase di aggiunta!");
                    
                }
                //mostro a video le categorie, e prelevo la scelta
                else if(risposta.equals("3"))
                {
                int indice=0;
                ArrayList<Category> categorie = info.getCategoriesList();
                for(int i=0;i < categorie.size();i++)
                {
                    indice++;
                    Menu.print("indice - riferimento - nome categoria - descrizione");
                    Menu.print(indice + " - " + categorie.get(i).getIdentifier() 
                            + " - " + categorie.get(i).getName()
                            + " - " + categorie.get(i).getDesc());
                    //ciclo tutte le sottocategorie j-esime della categoria i-esima
                    for(int j=0; j < categorie.get(i).getSubCategory().size(); j++)
                        Menu.print(((indice++)+1) + " - " + categorie.get(i).getSubCategory().get(j).getIdentifier()
                        + " - " + categorie.get(i).getSubCategory().get(j).getName()
                        + " - " + categorie.get(i).getSubCategory().get(j).getDesc());
                }
                String scelta =
                        Menu.getLine("Inserisci il numero della categoria che vuoi modificare");
                if(Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= indice)
                {
                    String nome = 
                            menu.getLine("Inserisci il nuovo della categoria "
                            + "indicata precedentemente");
                    String desc = 
                            menu.getLine("Inserisci la nuova"
                            + "descrizione della categoria indicata" +
                            " precedentemente");

                    //ciclo tutte le categorie e sottocategorie, fino a trovare quella cercata
                    //avrei potuto fare una hashtable, una mappa chiave->valore, per associare
                    //ad ogni posizione intera una categoria
                    int indice_di_confronto = 0;
                    //confronto indice ed indice_di_confronto. Cosi' facendo
                    //possso capire quando è il momento in cui mi trovo
                    //nella categoria, o sottocategoria, corretta.

                    //++indice incrementa subito indice, invece indice++
                    //la incrementa al prossimo ciclo

                    for(int i = 0;i < categorie.size(); i++)
                    {
                        indice_di_confronto++;
                        int scelta_input = Integer.parseInt(scelta);
                        if(indice_di_confronto == (scelta_input))
                        {
                            info.updateCategory(
                            categorie.get(i).getIdentifier(),
                                    nome,
                                    desc            
                            );    
                            //aggiorno le categorie, che ora saranno editate
                            info.getCategoriesList();
                        }
                        else
                        for(int j = 0; j < categorie.get(i).getSubCategory().size();j++)
                        {
                            indice_di_confronto++;
                            if(indice_di_confronto == (scelta_input))
                            {
                                info.updateCategory(
                                categorie.get(i).getSubCategory().get(j).
                                    getIdentifier(),
                                nome,
                                desc
                                );
                                //aggiorno le categorie, che ora saranno editate
                                info.getCategoriesList();
                            }
                        }                         
                    }
                }
                else
                    Menu.print("La scelta verra' ignorata.");
                }
                else if(risposta.equals("4"))
                {
                int indice=0;
                ArrayList<Book> listaLibri = info.getBooksList();
                
                ArrayList<String> chiavi = new ArrayList();
                Book libro_input = new Book();
                chiavi.add("titolo");
                chiavi.add("descrizione");
                chiavi.add("lingua");
                chiavi.add("anno_pubblicazione");
                chiavi.add("genere");
                chiavi.add("numero_pagine");
                chiavi.add("autori");
                chiavi.add("casa_editrice");
                chiavi.add("licenze");
                chiavi.add("is_avaiable");
                chiavi.add("is_active");
                
                Menu.print("identificatore - titolo - autori - anno");
                for(int i=0;i < listaLibri.size();i++)
                {
                    indice++;
                    Book attuale = listaLibri.get(i);
                    Menu.print((i+1) + ") " +attuale.getIdentifier() + " - " +
                            attuale.getTitolo() + " - " +
                            attuale.getAutori() + " - " +
                            attuale.getAnno() + " - " );
                }
                String scelta =
                        Menu.getLine("Inserisci il numero della risorsa che vuoi modificare");
                // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                if(Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= indice)
                {
                    ArrayList<String> inputs = new ArrayList();
                    for(int i=0;i<chiavi.size()-2;i++)
                    {
                        inputs.add(menu.getLine("inserisci il valore del campo " +
                                chiavi.get(i)));
                    }
                    inputs.add(menu.getLine("inserisci il valore del campo " +
                                "is_avaiable(0 per falso, 1 per vero)"));  
                    inputs.add(menu.getLine("inserisci il valore del campo " +
                                "is_active(0 per falso, 1 per vero)"));

                    //INSERISCO i dati in libro
                    for(int i = 0;i < chiavi.size();i++)
                        libro_input.insertKeyValue(chiavi.get(i), inputs.get(i));
                    libro_input.setIdentifier(
                            listaLibri.get(Integer.parseInt(scelta)-1).getIdentifier());
                    libro_input.setId(
                            listaLibri.get(Integer.parseInt(scelta)-1).getId());
                    
                    if(info.updateBook(libro_input))
                        Menu.print("Update effettuato con successo!");
                    
                }
                else
                    Menu.print("La scelta verra' ignorata.");
                    
                }
                else if(risposta.equals("5"))
                {
                    Film input_film = new Film();
                    ArrayList<String> chiaviFilm = new ArrayList();
                    ArrayList<Film> film = info.getFilmList();
                    for (String key : film.get(0).attributi.keySet()) 
                        chiaviFilm.add(key);
                    Menu.print("identificatore - titolo - attori - anno di produzione");
                    for (int i = 0; i < film.size(); i++) {
                        Film attuale = film.get(i);
                        Menu.print((i + 1) + ") " + attuale.getIdentifier() + " - "
                                + attuale.getTitolo() + " - "
                                + attuale.getAttori() + " - "
                                + attuale.getAnno() + " - ");
                    }
                    String scelta
                            = Menu.getLine("Inserisci il numero della risorsa che vuoi modificare");
                    // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                    if (Integer.parseInt(scelta) > 0 && Integer.parseInt(scelta) <= film.size()) {
                        for (int m = 0; m < chiaviFilm.size(); m++) 
                            Menu.print((m + 1) + "-> " + chiaviFilm.get(m));

                        for (String key : film.get(0).attributi.keySet()) {
                            if(key!="identifier" && key!="id"){
                            String scelta_input = Menu.getLine("Inserisci il valore "
                                    + "desiderato per il campo: " + key);
                            input_film.insertKeyValue(key,scelta_input);
                            }
                            input_film.setIdentifier(film.get(Integer.parseInt(scelta)-1).getIdentifier());
                            input_film.setId(film.get(Integer.parseInt(scelta) - 1).getId());
                        }
                        if(info.updateFilm(input_film))
                            Menu.print("Update effettuato con successo!");
                        
                    }
                    else
                        Menu.print("La scelta verra' ignorata.");

                }
                //logout
                else if(risposta.equals("6"))
                {
                    is_operator = false;
                    is_logged = false;
                    
                }
            }
            //E' un utente loggato
            else if(is_user && is_logged)
            {
                Menu.print("Gentile " + utente.getUsername() +
                            ", benvenuto nel sistema!");

                
                GregorianCalendar anno_attuale = new GregorianCalendar(),
                            anno_registrazione = new GregorianCalendar();
                //memorizzo le date che mi serviranno
                String data_attuale = info.getCurrentDate();

                String data_reg = info.getSubscriptionInfo(utente);

                
                //prelevo le parti dei dati che mi interessano
                StringTokenizer st = new StringTokenizer(data_reg, " ");
                //scorriamo l'indice fino all'anno di registrazione
                st.nextElement();st.nextElement();
                data_reg = st.nextElement().toString();
                //prelevo anni, mesi e giorni dalle date
                StringTokenizer st_attuale = new StringTokenizer(data_attuale, "/");
                StringTokenizer st_registrazione = new StringTokenizer(data_reg, "-");

                String anno = st_attuale.nextToken();
                String mese = st_attuale.nextToken();
                String giorno = st_attuale.nextToken();
                anno_attuale.set(Integer.parseInt(anno),
                        Integer.parseInt(mese), 
                        Integer.parseInt(giorno));

                anno = st_registrazione.nextToken();
                mese = st_registrazione.nextToken();
                giorno = st_registrazione.nextToken();
                anno_registrazione.set(Integer.parseInt(anno),
                        Integer.parseInt(mese), 
                        Integer.parseInt(giorno));
                /////
                //Menu.printGreen(anno_attuale.toString());
                //Menu.printGreen(anno_registrazione.toString());

                //calcolo della differenza in giorno tra le due date
                long differenza =
                        info.getDiffDate(anno_attuale.getTime(),
                                         anno_registrazione.getTime(),TimeUnit.DAYS);
                Menu.printCyan("differenza in days: " + differenza);

                // testiamo artificialmente la scadenza: 
                //differenza-= 1827;
                if(info.isSubscribtionActive(utente)){

                    //controlliamo la differenza in giorni delle scandenze.
                    if(differenza > 10)
                    {
                        Menu.print("La tua iscrizione è valida fino al " +
                                data_reg + "(anno-mese-giorno)");
                    }
                    else if(differenza >= 0)
                    {
                        Menu.print("Devi rinnovare la tua iscrizione, hai " + 
                                differenza + " giorni di tempo!");
                        String sceglimi = Menu.getLine("Inserisci 1 per rinnovare,"
                                + "o un qualsiasi carattere per proseguire");
                        if(sceglimi.equals("1"))
                                info.renew(utente);
                    }
                    else
                    {
                        Menu.print("Purtroppo la tua registrazione è scaduta!");
                        Menu.print("La tua iscrizione verra' automaticamente " +
                                "disattivata!");
                        Menu.printCyan("rimosso: " + info.removeSubscribtion(utente));
                    }
                }
                //ricontrollo che l'utente sia attivo, a causa del rinnovo
                //della parte sopra di codice, in quanto ora potrebbe essere 
                //non piu' attivo
                if(info.isSubscribtionActive(utente)){

                //+++++++++++++++++ ritiro automatico dei dati
                ArrayList<Loan> libri_scaduti = info.getBooksLoan(utente);
                for (int i = 0; i < libri_scaduti.size(); i++) {
                    //cerco tra tutti i libri, e rimuovo quelli col
                    //prestito scaduto.
                    if (info.removeLoan(utente,
                            libri_scaduti.get(i).libro,
                            libri_scaduti.get(i).id_loan)) {
                        Menu.print("Il libro identificato con "
                                + libri_scaduti.get(i).libro.getIdentifier()
                                + " ti e' stato rimosso dai prestiti, in "
                                + "quanto e' stata superata la scadenza.");
                    }
                }
                ArrayList<FilmLoan> film_scaduti = info.getFilmsLoan(utente);
                for (int i = 0; i < film_scaduti.size(); i++) {
                    //cerco tra tutti i libri, e rimuovo quelli col
                    //prestito scaduto.
                    if (info.removeLoan(utente,
                            film_scaduti.get(i).film,
                            film_scaduti.get(i).id_loan)) {
                        Menu.print("Il film identificato con "
                                + film_scaduti.get(i).film.getIdentifier()
                                + " ti e' stato rimosso dai prestiti, in "
                                + "quanto e' stata superata la scadenza.");
                    }
                }
                //+++++++++++++++++ fine ritiro automatico dei dati
                
                risposta = Menu.loggedUserMenu();
                
                if(risposta.equals("1"))
                {
                    String data = info.getSubscriptionInfo(utente);
                    StringTokenizer sp = new StringTokenizer(data, " ");
                    //scorriamo l'indice fino all'anno di registrazione
                    sp.nextElement();sp.nextElement();
                    data = sp.nextElement().toString();
                    Menu.print("La tua registrazione scadrà il " + data 
                    + "(anno-mese-giorno)");     
                }
                else if(risposta.equals("2")) 
                {
                    Menu.print("La tua iscrizione è valida fino al " +
                            data_reg + "(anno-mese-giorno)");
                    if(differenza > 10)
                    Menu.print("La tua iscrizione non puo' essere prorogata per"
                            + " adesso!");
                    else if(differenza >= 0 && differenza <= 10)
                    {
                        info.renew(utente);
                        Menu.print("Iscrizione rinnovata di altri 5 anni!");
                    }
                    


                }
                else if(risposta.equals("3"))
                {
                    //stampo a video tutti i libri
                    ArrayList<Book> books = info.getBooksList();
                    for(int i = 0; i < books.size(); i++)
                        Menu.print((i+1) + ") identificativo libro: " + books.get(i).getIdentifier()
                        + " titolo: " + books.get(i).getTitolo());
                    String scelta =
                        Menu.getLine("Inserisci il numero della risorsa che vuoi prendere in prestito");
                // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                int intScelta = Integer.parseInt(scelta);
                //importante: considero se appunto il libro e' disponibile al prestito, ovvero se il numero totale
                //di prestiti del libro da parte di tutti gli utenti e' inferiore al numero delle sue licenze.
                if(intScelta > 0 && intScelta <= books.size())
                {
                    //scelgo il libro che l'utente ha indicato
                    Book elemento = books.get(intScelta-1);
                    
                    //if utente ha libri minori del massimo prestabili
                    if(info.isBookAvaiableForLoan(books.get(intScelta - 1)) 
                            && info.addBookLoan(utente, elemento ))
                        Menu.print("Il libro che hai selezionato ti e' stato concesso in prestito");
                    else
                        Menu.print("Non puoi prendere in prestito altri libri di"
                                + " questa categoria, sei gia' al limite");
                
                    //qui verifichiamo se i nostri libri non sono risultati liberi
                    //per il prestito, ed avvertiamo il fruitore
                    if(!info.isBookAvaiableForLoan(books.get(intScelta - 1)))
                        Menu.print("Purtroppo il libro non e' prestabile, a causa delle licenze non sufficienti");
                }
                else
                    Menu.print("Questa scelta verrà ignorata!");
                
                }
                else if(risposta.equals("4"))
                {
                    
                    //stampo a video tutti i libri
                    ArrayList<Film> films = info.getFilmList();
                    for (int i = 0; i < films.size(); i++) {
                        Menu.print((i + 1) + ") identificativo film: " + films.get(i).getIdentifier()
                                + " titolo: " + films.get(i).getTitolo());
                    }
                    String scelta
                            = Menu.getLine("Inserisci il numero della risorsa che vuoi prendere in prestito");
                    // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                    int intScelta = Integer.parseInt(scelta);
                    //importante: considero se appunto il libro e' disponibile al prestito, ovvero se il numero totale
                    //di prestiti del libro da parte di tutti gli utenti e' inferiore al numero delle sue licenze.
                    if (intScelta > 0 && intScelta <= films.size()) {
                        //scelgo il libro che l'utente ha indicato
                        Film elemento = films.get(intScelta - 1);

                        //if utente ha libri minori del massimo prestabili
                        if (info.isFilmAvaiableForLoan(films.get(intScelta - 1)) 
                                && info.addFilmLoan(utente, elemento)) {
                            Menu.print("Il film che hai selezionato ti e' stato concesso in prestito");
                        } else {
                            Menu.print("Non puoi prendere in prestito altri films di"
                                    + " questa categoria, sei gia' al limite");
                        }
                        //qui verifichiamo se i nostri libri non sono risultati liberi
                        //per il prestito, ed avvertiamo il fruitore
                        if (!info.isFilmAvaiableForLoan(films.get(intScelta - 1))) {
                            Menu.print("Purtroppo il film non e' prestabile, a causa delle licenze non sufficienti");
                        }
                    }
                    else
                        Menu.print("Questa scelta verrà ignorata!");
  
                }
                else if(risposta.equals("5"))
                {
                    ///visualizzo solo i libri in prestito
                    
                    //stampo a video tutti i libri
                    ArrayList<Loan> loans = info.getBooksLoan(utente);
                    Menu.print("identificativo risorsa - data inizio prestito - data fine prestito - se prorogato");
                    for(int i = 0; i < loans.size(); i++)
                    {
                        if (loans.get(i).is_renewed) {
                            Menu.print((i + 1) + " - " + loans.get(i).libro.getIdentifier()
                                    + " - " + loans.get(i).data_inizio_proroga
                                    + " - " + loans.get(i).data_fine_proroga
                                    + " - " + "si'"
                            );
                        } else {
                            Menu.print((i + 1) + " - " + loans.get(i).libro.getIdentifier()
                                    + " - " + loans.get(i).data_inizio_prestito
                                    + " - " + loans.get(i).data_fine_prestito
                                    + " - " + "no"
                            );
                        }
                    }
                    
                }
                else if (risposta.equals("6")) 
                {
                    ///visualizzo solo i films in prestito
                    //stampo a video tutti i films
                    ArrayList<FilmLoan> loans = info.getFilmsLoan(utente);
                    Menu.print("identificativo risorsa - data inizio prestito - data fine prestito - se prorogato");
                    for (int i = 0; i < loans.size(); i++) {
                        if (loans.get(i).is_renewed) {
                            Menu.print((i + 1) + " - " + loans.get(i).film.getIdentifier()
                                    + " - " + loans.get(i).data_inizio_proroga
                                    + " - " + loans.get(i).data_fine_proroga
                                    + " - " + "si'"
                            );
                        } else {
                            Menu.print((i + 1) + " - " + loans.get(i).film.getIdentifier()
                                    + " - " + loans.get(i).data_inizio_prestito
                                    + " - " + loans.get(i).data_fine_prestito
                                    + " - " + "no"
                            );
                        }
                    }
                }
                else if(risposta.equals("7"))
                {
                    ArrayList<Loan> loans = info.getBooksLoan(utente);
             
                    ///Mostro tutti i libri, e mostro quelli rinnovabili

                    //stampo a video tutti i libri
                    loans = info.getBooksLoan(utente);
                    Menu.print("identificativo risorsa - data inizio prestito - data fine prestito - se prorogato");
                    for(int i = 0; i < loans.size(); i++){
                        //if(info.isBookAvaiableForLoan(loans.get(i).libro)){
                            if(loans.get(i).is_renewed)
                                Menu.print((i+1) +  " - " + loans.get(i).libro.getIdentifier() +
                                        " - " + loans.get(i).data_inizio_proroga +
                                        " - " + loans.get(i).data_fine_proroga + 
                                        " - " + "si'"
                                );
                            else
                                Menu.print((i+1) +  " - " + loans.get(i).libro.getIdentifier() +
                                    " - " + loans.get(i).data_inizio_prestito +
                                    " - " + loans.get(i).data_fine_prestito + 
                                    " - " + "no"
                                );
                            //}
                    }
                    
                    String scelta =
                        Menu.getLine("Inserisci il numero della risorsa che vuoi prorogare");
                    // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                
                
                    int intScelta = Integer.parseInt(scelta);
                    if(intScelta > 0 && intScelta <= loans.size())
                    {
                        //if utente ha libri minori del massimo prestabili
                        Book elemento = loans.get(intScelta-1).libro;
                        if(info.renewLoan(utente, elemento,loans.get(intScelta-1).id_loan))
                            Menu.print("Rinnovo avvenuto con successo!");
                        else
                            Menu.print("Il libro non puo' ancora essere "
                                    + "rinnovato! E' ancora troppo presto. "
                                    + "Inoltre, ti avvisiamo che puoi solamente"
                                    + " prorogare una volta il prestito di"
                                    + " un libro.");
                    }
                
                }
                else if(risposta.equals("8"))
                {
                   
                    ArrayList<FilmLoan> loans = info.getFilmsLoan(utente);


                    ///Mostro tutti i libri, e mostro quelli rinnovabili
                    //stampo a video tutti i libri
                    loans = info.getFilmsLoan(utente);
                    Menu.print("identificativo risorsa - data inizio prestito - data fine prestito - se prorogato");
                    for (int i = 0; i < loans.size(); i++) {
                        //if (info.isFilmAvaiableForLoan(loans.get(i).film)) {
                            if (loans.get(i).is_renewed) {
                                Menu.print((i + 1) + " - " + loans.get(i).film.getIdentifier()
                                        + " - " + loans.get(i).data_inizio_proroga
                                        + " - " + loans.get(i).data_fine_proroga
                                        + " - " + "si'"
                                );
                            } else {
                                Menu.print((i + 1) + " - " + loans.get(i).film.getIdentifier()
                                        + " - " + loans.get(i).data_inizio_prestito
                                        + " - " + loans.get(i).data_fine_prestito
                                        + " - " + "no"
                                );
                            }
                        //}
                    }

                    String scelta
                            = Menu.getLine("Inserisci il numero della risorsa che vuoi prorogare");
                    // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.

                    int intScelta = Integer.parseInt(scelta);
                    if (intScelta > 0 && intScelta <= loans.size()) {
                        //if utente ha libri minori del massimo prestabili
                        Film elemento = loans.get(intScelta - 1).film;
                        if (info.renewLoan(utente, elemento, loans.get(intScelta - 1).id_loan)) {
                            Menu.print("Rinnovo avvenuto con successo!");
                        } else {
                            Menu.print("Il film non puo' ancora essere "
                                    + "rinnovato! E' ancora troppo presto. "
                                    + "Inoltre, ti avvisiamo che puoi solamente"
                                    + " prorogare una volta il prestito di"
                                    + " un film.");
                        }
                    }

                }
                else if(risposta.equals("9"))
                {
                    //logout dal sistema
                    is_logged = false;
                    is_user = false;
                }
                
                }
                else
                {
                    Menu.print("Il tuo profilo non ha una iscrizione attiva");
                    is_logged = false;
                }
                    
            }
            //E' un utente che deve fare il login
            else
            {
                risposta = Menu.introMenu();
                
                //con questo, in caso di "5" termino il ciclo principale.
                if(risposta.equals("5"))
                    menu.setStatus(0);
                else if(risposta.equals("1"))
                {
                    risposta = Menu.loginRequestMenu();
                    Menu.printCyan(risposta);
                    //check errori da implementare
                    utente = new User(risposta);
                    //Menu.printGreen(utente.toString());
                    utente.toString();
                    is_operator = info.isOperator(utente);
                    is_user = info.isUser(utente);

                    if(info.login(utente))
                        Menu.print("Hai effettuato il login nel sistema! ");
                    else
                        Menu.print("Questo utente non esiste!");
                    Menu.printPurple("e' operatore: " + is_operator +
                                    (" e' utente: " + is_user)
                                    );
                    
                    
                    //Menu.printCyan("risultato iscrizione: " + info.getSubscriptionInfo(utente));

                    if(info.login(utente))
                    {
                        is_logged = true;
                        is_user = true;
                        if(info.isOperator(utente)) is_operator = true;
                        if(is_operator)
                        {

                        }
                        else
                        {


                        }

                    }

                }
                else if(risposta.equals("2"))
                {
                    risposta = Menu.registrationRequestMenu();
                    Menu.printCyan(risposta);
                    utente = new User(risposta);
                    utente.toString();
                    if(info.addUser(utente))
                        Menu.print("Gentile " + utente.getName() +
                                " sei stato inserito nel sistema!");
                    if(info.subscribe(utente))
                        Menu.print("Iscrizione avvenuta con successo");
                    
                }
                else if(risposta.equals("3"))
                {
                    
                    ArrayList<Book> listaLibri = info.getBooksList();
                    ArrayList<Film> listaFilms = info.getFilmList();
                    ArrayList<String> chiavi = new ArrayList();
                    ArrayList<String> chiaviFilm = new ArrayList();
                    Book libro_input = new Book();
                    Film film = new Film();
                    

                    
                    
                    Menu.print("1) cerca tra i libri");
                    Menu.print("2) cerca tra i films");
                    String scegli_cosa = Menu.getLine("");
                    
                    if(scegli_cosa.equals("1"))
                        
                    {
                        

                        chiavi.add("titolo");
                        chiavi.add("descrizione");
                        chiavi.add("lingua");
                        chiavi.add("anno_pubblicazione");
                        chiavi.add("genere");
                        chiavi.add("numero_pagine");
                        chiavi.add("autori");
                        chiavi.add("casa_editrice");
                        chiavi.add("licenze");
                        chiavi.add("is_avaiable");
                        chiavi.add("is_active");

                        Menu.print("identificatore - titolo - autori - anno");
                        for (int i = 0; i < listaLibri.size(); i++) {
                            Book attuale = listaLibri.get(i);
                            Menu.print((i + 1) + ") " + attuale.getIdentifier() + " - "
                                    + attuale.getTitolo() + " - "
                                    + attuale.getAutori() + " - "
                                    + attuale.getAnno() + " - ");
                        }
                        for(int m = 0; m < chiavi.size(); m++)
                            Menu.print((m+1) + "-> " + chiavi.get(m));
                        String scelta_parametro =
                                Menu.getLine("Inserisci il numero della parametro che vuoi cercare, tra"
                                    + "questi mostrati a video");                  
                        String valore_parametro
                                = Menu.getLine("Inserisci il valore del parametro che vuoi cercare, tra"
                                        + "questi mostrati a video");
                        //scelta_parametro e' stringa
                        int scelta_int = Integer.parseInt(scelta_parametro);
                        if(scelta_int > 0 && scelta_int <= chiavi.size())
                        {
                            for(int n = 0; n < listaLibri.size();n++)
                            {
                                if(((Resource)(listaLibri.get(n))).find(chiavi.get(scelta_int-1),valore_parametro))
                                    Menu.print("Il libro identificato con l'identificatore-> " 
                                        + listaLibri.get(n).getIdentifier() + " contiene, nel parametro-> "
                                        + chiavi.get(scelta_int-1) + " il valore cercato, ovvero ->"
                                        + valore_parametro);
                            }
                        }
                    }
                    else if(scegli_cosa.equals("2"))
                    {
                        for (String key : film.attributi.keySet()) {
                            chiaviFilm.add(key);
                        }
                        Menu.print("identificatore - titolo - attori - anno di produzione");
                        for (int i = 0; i < listaFilms.size(); i++) {
                            Film attuale = listaFilms.get(i);
                            Menu.print((i + 1) + ") " + attuale.getIdentifier() + " - "
                                    + attuale.getTitolo() + " - "
                                    + attuale.getAttori() + " - "
                                    + attuale.getAnno() + " - ");
                        }
                        for (int m = 0; m < chiaviFilm.size(); m++) {
                            Menu.print((m + 1) + "-> " + chiaviFilm.get(m));
                        }
                        String scelta_parametro
                                = Menu.getLine("Inserisci il numero della parametro che vuoi cercare, tra"
                                        + "questi mostrati a video");
                        String valore_parametro
                                = Menu.getLine("Inserisci il valore del parametro che vuoi cercare, tra"
                                        + "questi mostrati a video");
                        //scelta_parametro e' stringa
                        int scelta_int = Integer.parseInt(scelta_parametro);
                        if (scelta_int > 0 && scelta_int <= chiaviFilm.size()) {
                            for (int n = 0; n < listaFilms.size(); n++) {
                                if (((Resource) (listaFilms.get(n))).find(chiaviFilm.get(scelta_int - 1), valore_parametro)) {
                                    Menu.print("Il libro identificato con l'identificatore-> "
                                            + listaFilms.get(n).getIdentifier() + " contiene, nel parametro-> "
                                            + chiaviFilm.get(scelta_int - 1) + " il valore cercato, ovvero ->"
                                            + valore_parametro);
                                }
                            }
                        }
                    }
                    
                }
                else if(risposta.equals("4"))
                {
                    ArrayList<Book> libri_disponibili = info.getBooksList();
                    ArrayList<Film> films_disponibili = info.getFilmList();
                    String disponibile = "";
                    
                    Menu.print("indice" + " - " + "identificatore"
                            + " - " + "id_interno" + " - " + "licenze" 
                            + " - " + "disponibile");
                    for(int l = 0; l < libri_disponibili.size(); l++)
                    {
                        if(info.isBookAvaiableForLoan(libri_disponibili.get(l)))
                            disponibile = "si";
                        else
                            disponibile = "no";
                        Menu.print("Libro " + (l+1) + " - " + "identificatore: " 
                            + libri_disponibili.get(l).getIdentifier() + " - "  
                            + " id_interno: " + libri_disponibili.get(l).getId()
                            + " - " + "licenze: " + libri_disponibili.get(l).getLicenze()
                            + " - " + disponibile);
                    }
                    Menu.print("indice" + " - " + "identificatore"
                            + " - " + "id_interno" + " - " + "licenze"
                            + " - " + "disponibile");
                    for (int l = 0; l < films_disponibili.size(); l++) {
                        if (info.isFilmAvaiableForLoan(films_disponibili.get(l))) {
                            disponibile = "si";
                        } else {
                            disponibile = "no";
                        }
                        Menu.print("Film " + (l + 1) + " - " + "identificatore: "
                                + films_disponibili.get(l).getIdentifier() + " - "
                                + " id_interno: " + films_disponibili.get(l).getId()
                                + " - " + "licenze: " + films_disponibili.get(l).getLicenze()
                                + " - " + disponibile);
                    }
                    
                }


            }
        }
        x.close();
    }
    
}
