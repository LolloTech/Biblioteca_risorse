
package biblioteca_prima_release;

/**
 *
 * @author Sviluppatore
 */


import java.sql.SQLException;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
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
    
    
    
    public static void main(String[] args) throws IllegalArgumentException, SQLException {
        
        
        
        Menu.print("PRIMA RELEASE");
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
        
        
        //info.addUser(new User("utente","cognome","123stella","123stella"));
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
        
        
        
        Menu menu = new Menu();
        //Loop principale del programma        
        //confronto tra interi, non tra booleani
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
                //***da implementare
                //logout
                else if(risposta.equals("3"))
                {
                    is_operator = false;
                    is_logged = false;
                    
                }
                
            }
            //E' un utente loggato
            else if(is_user && is_logged)
            {
                risposta = Menu.loggedUserMenu();
                
                if(risposta.equals("1"))
                {
                    String data = info.getSubscriptionInfo(utente);
                    StringTokenizer st = new StringTokenizer(data, " ");
                        //scorriamo l'indice fino all'anno di registrazione
                        st.nextElement();st.nextElement();
                        data = st.nextElement().toString();
                        Menu.print("La tua registrazione scadrà il " + data);
                    
                    
                }
                else if(risposta.equals("2")) 
                {
                    GregorianCalendar anno_attuale = new GregorianCalendar(),
                                anno_registrazione = new GregorianCalendar();
                    //memorizzo le date che mi serviranno
                    String data_attuale = info.getCurrentDate();

                    String data_reg = info.getSubscriptionInfo(utente);

                    Menu.printCyan(data_reg);
                    //prelevo le parti dei dati che mi interessano
                    StringTokenizer st = new StringTokenizer(data_reg, " ");
                    //scorriamo l'indice fino all'anno di registrazione
                    st.nextElement();st.nextElement();
                    data_reg = st.nextElement().toString();
                    //prelevo anni, mesi e giorni dalle date
                    StringTokenizer st_attuale = new StringTokenizer(data_attuale, "/");
                    StringTokenizer st_registrazione = new StringTokenizer(data_reg, "-");

                    String giorno = st_attuale.nextToken();
                    String mese = st_attuale.nextToken();
                    String anno = st_attuale.nextToken();
                    anno_attuale.set(Integer.parseInt(anno),
                            Integer.parseInt(mese), 
                            Integer.parseInt(giorno));

                    giorno = st_registrazione.nextToken();
                    mese = st_registrazione.nextToken();
                    anno = st_registrazione.nextToken();
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

                    // testiamo artificialmente la scadenza: differenza-= 1790;
                    Menu.print("Gentile " + utente.getUsername()+
                            " hai effettuato il login nel sistema!");
                    if(differenza > 10)
                    {
                        Menu.print("La tua iscrizione è valida fino al " +
                                data_reg);
                    }
                    else if(differenza >= 0)
                    {
                        Menu.print("Devi rinnovare la tua iscrizione, hai " + 
                                differenza + " giorni di tempo!");
                        Menu.print("La tua scadenza verra' automaticamente " +
                                "prorogata di 5 anni!");
                        info.renew(utente);
                    }
                    else
                    {
                        Menu.print("Purtroppo la tua registrazione è scaduta!");
                    }




                }
                else if(risposta.equals("3"))
                {
                    is_logged = false;
                    is_user = false;
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
