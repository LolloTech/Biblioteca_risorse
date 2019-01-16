
package biblioteca_prima_release;

/**
 *
 * @author Sviluppatore
 */


import java.sql.SQLException;

import java.sql.ResultSet;
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
    
    
    /*public static Connection connect() {
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

    public static void main(String[] args) throws IllegalArgumentException, SQLException {
        
        
        
        System.out.println("hello");
        ConcreteServer x = new ConcreteServer();
        boolean is_operator = false;
        boolean is_user = false;
        
        
        Info info = new Info(x);
        
        
        for(int l=0;l<3;l++)
            try {
                x.insertDumb();
            } catch (SQLException ex) {
                Menu.print( "insert dumb fail: " + ex.getMessage());
            }
        try {
        ResultSet rs = (ResultSet) x.execute("").getData();
            while (rs.next()) {
                System.out.println(rs.getInt("id")          +  "\t"     + 
                                   rs.getString("name")     +  "\t"     +
                                   rs.getString("surname"));
            }
        } catch (SQLException e) {
            Menu.print("from main: " + e.getMessage());
            
            
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
            
            
        } catch (SQLException e) {
            Menu.print("from main: " + e.getMessage());
            
        }
        
        Menu.print("Test di info");
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
            String risposta;
            risposta = Menu.introMenu();
            if(risposta.equals("3"))
                menu.setStatus(0);
            else if(risposta.equals("1"))
            {
                risposta = Menu.loginRequestMenu();
                Menu.printCyan(risposta);
                //check errori da implementare
                User utente = new User(risposta);
                //Menu.printGreen(utente.toString());
                utente.toString();
                is_operator = info.isOperator(utente);
                is_user = info.isUser(utente);
                
                Menu.printCyan("risultato login: " + info.login(utente));
                Menu.printPurple("e' operatore: " + is_operator +
                                (" e' utente: " + is_user)
                                );
                info.subscribe(utente);
                Menu.printCyan("qui ci siamo");
                Menu.printCyan("risultato iscrizione: " + info.getSubscriptionInfo(utente));
                
                if(info.login(utente))
                {
                    if(is_operator)
                    {
                        
                    }
                    else
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
                    
                }
                
                
                
            }
            else if(risposta.equals("2"))
            {
                risposta = Menu.registrationRequestMenu();
                Menu.printCyan(risposta);
                User utente = new User(risposta);
                utente.toString();
                Menu.printCyan("risultato registrazione: " + info.addUser(utente));
            }
            
            
        }
        
        x.close();
    }
    
}
