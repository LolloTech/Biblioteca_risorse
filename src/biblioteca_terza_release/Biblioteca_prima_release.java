
package biblioteca_terza_release;

/**
 *
 * @author Sviluppatore
 * 
 */



/*

Map<String,ArrayList<String>> xD = new HashMap<String, ArrayList<String>>();
ArrayList<String> test = new ArrayList();
test.add("primo");
test.add("secondo");

xD.put("prova", test);
for(int i = 0;i<xD.get("prova").size();i++)
    Menu.printCyan(xD.get("prova").get(i));


*/


import java.io.FileNotFoundException;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.util.ArrayList;
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
    
    
    
    public static void main(String[] args) throws IllegalArgumentException, SQLException 
    {
        Menu.print("--> SECONDA RELEASE");
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
            //print(connect1.isClosed());
            //print(connect2.isClosed());
            
            ResultSet rs = (ResultSet) x.execute("SELECT * FROM person LIMIT 0,30").getData();
            //sets the directionability of the scroll of the iterator of the results
            
            while (rs.next()) {
                Menu.printGreen( rs.getString("password")          +  "\t"     + 
                                   rs.getString("name")     +  "\t"     +
                                   rs.getString("surname"));
            }
            
            
            ///CHECK DEI DATI
            /*
            rs = (ResultSet) x.execute("SELECT * FROM person LIMIT 0,30").getData();
            int column = 1;
            while (column <= rs.getMetaData().getColumnCount()) {
                System.out.print(rs.getMetaData().getColumnTypeName(column));
                System.out.print(" \t" + rs.getMetaData().getColumnName(column));
                System.out.println("\tCOLONNA: " + rs.getMetaData().getColumnTypeName(column));
                column++;            }
            rs = (ResultSet) x.execute("SELECT * FROM subscription").getData();
            column = 0;
            while (column <= rs.getMetaData().getColumnCount()) {
                Menu.printGreen(" valore bool falso? = " + rs.next());
                System.out.print(rs.getMetaData().getColumnTypeName(column));
                System.out.print(" \t" + rs.getMetaData().getColumnName(column));
                column++;
            }
            rs = (ResultSet) x.execute("SELECT * FROM renew_subscription LIMIT 1").getData();
            column = 1;
            while (column <= rs.getMetaData().getColumnCount()) {
                System.out.print(rs.getMetaData().getColumnTypeName(column));
                System.out.print(" \t" + rs.getMetaData().getColumnName(column));
                column++;
            }
            rs = (ResultSet) x.execute("SELECT * FROM is_operator LIMIT 0,30").getData();
            column = 1;
            while (column <= rs.getMetaData().getColumnCount()) {
                System.out.print(rs.getMetaData().getColumnTypeName(column));
                System.out.print(" \t" + rs.getMetaData().getColumnName(column));
                column++;
            }
            rs = (ResultSet) x.execute("SELECT * FROM is_user LIMIT 1").getData();
            column = 1;
            while (column <= rs.getMetaData().getColumnCount()) {
                System.out.print(rs.getMetaData().getColumnName(column));
                System.out.print(" \t" + rs.getMetaData().getColumnName(column));
                column++;
            }
            */
            //////////////////////////////////////////////////////////////////
            
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
                
                //nel caso in cui fosse una sottocategoria
                for(int m=0; m < array.get(l).getSubCategory().size();m++)
                    //if()
                            info.addCategory(
                        array.get(l).getSubCategory().get(m).getName(),
                        array.get(l).getSubCategory().get(m).getDesc(), 
                        true,
                        array.get(l).getIdentifier(),
                        array.get(l).getSubCategory().get(m).getIdentifier(),
                        array.get(l).getLoanDuration(),
                        array.get(l).getRenewDuration(),
                        array.get(l).getDaysUntilRenew(),
                        array.get(l).getMaxLoansToPerson());
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
                    libri.get(i).getCategoria().getIdentifier());
        }
        //stampo a video il menu' principale di tutto il programma.
        Menu menu = new Menu();
        Menu.print("Oggi e' il " + info.getCurrentDate());
        //Loop principale del programma        
        //confronto tra interi, non tra booleani
        
        ArrayList<Loan> elementi = info.getBooksLoan(new User("default","default","default","default"));
        for(int dudendango = 0; dudendango < elementi.size(); dudendango++)
            Menu.printGreen(
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
                
                
                /*
                @attributi: 
                * - id
                * - identifier
                * - titolo
                * - anno_pubblicazione
                * - genere
                * - numero_pagine
                * - autori[]
                * - licenze
                * - casa_editrice
                * - is_avaiable: indica se e' disponibile considerando il n. di licenze
                * - is_active: indica se e' disponile nell'archivio
                *
                */
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
                //logout
                else if(risposta.equals("5"))
                {
                    is_operator = false;
                    is_logged = false;
                    
                }
                
            }
            //E' un utente loggato
            else if(is_user && is_logged)
            {
                Menu.print("Gentile " + utente.getUsername()+
                            " hai effettuato il login nel sistema!");
                
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
                            data_reg + "(anno-mese-giorno");
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
                        Menu.print((i+1) + "identificativo: " + books.get(i).getIdentifier());
                    String scelta =
                        Menu.getLine("Inserisci il numero della risorsa che vuoi modificare");
                // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                int intScelta = Integer.parseInt(scelta);
                if(intScelta > 0 && intScelta <= books.size())
                {
                    //if utente ha libri minori del massimo prestabili
                    if(info.addBookLoan(utente, books.get(intScelta)))
                        Menu.printCyan("tutto ok!");
                    else
                        Menu.printRed("mmm...fallito");
                }
                }
                else if(risposta.equals("4"))
                {
                    //stampo a video tutti i libri
                    ArrayList<Loan> loans = info.getBooksLoan(utente);
                    Menu.print("identificativo risorsa - data inizio prestito - data fine prestito");
                    for(int i = 0; i < loans.size(); i++)
                        Menu.print((i+1) +  " " + loans.get(i).libro.getIdentifier() +
                                " " + loans.get(i).data_inizio_prestito +
                                " " + loans.get(i).data_fine_prestito);
                    String scelta =
                        Menu.getLine("Inserisci il numero della risorsa che vuoi prorogare");
                // > 0 non casuale; e' perche' l'input parte da 1, e non da 0.
                int intScelta = Integer.parseInt(scelta);
                if(intScelta > 0 && intScelta <= loans.size())
                {
                    //if utente ha libri minori del massimo prestabili
                    
                }
                }
                else if(risposta.equals("5"))
                {
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
                if(risposta.equals("3"))
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
                        Menu.printCyan("Gentile " + utente.getName() +
                                " sei stato inserito nel sistema");
                    if(info.subscribe(utente))
                        Menu.print("Iscrizione avvenuta con successo!!");
                    
                }


            }
        }
        x.close();
    }
    
}
