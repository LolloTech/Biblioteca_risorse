package biblioteca_quinta_release;



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
import java.util.HashMap;
import java.util.Map;
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
            


    }

    
    
    
    
    
    

}
