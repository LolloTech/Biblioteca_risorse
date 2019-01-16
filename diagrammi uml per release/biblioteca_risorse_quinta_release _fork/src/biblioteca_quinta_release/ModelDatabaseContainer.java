package biblioteca_quinta_release;

/**
 * La classe ModelDatabaseContainer consiste di una classe il cui scopo
 * è quello di fornire e raccogliere tutti i punti di accesso per salvare o
 * ottenere dati dal database implementato; questa classe sarà estesa da 
 * qualunque sottoclasse che voglia implementare al proprio interno tutti i 
 * metodi inerenti il salvataggio e l'interrogazione del database per poter
 * così accedere o salvare i dati contenuti nelle risorse(Film e Book). L'idea 
 * è che è corretto dare un punto di accesso unico per tutte gli attributi
 * ritenuti utili ed in comune ad ogni classe che interroghi il database, e che
 * ci sia una classe apposita che estenda ModelDatabaseContainer, e che 
 * implementi i propri metodi necessari all'elaborazione dei propri dati(per
 * esempio, una classe per ogni tipo di risorsa, di modo da avere una sola 
 * classe che abbia la responsabilità di interrogare il database, relativamente
 * alla propria risorsa, senza sovraccaricare di responsabilità una unica
 * enorme classe, che era quello che faceva la precedente classe Info).
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

public class ModelDatabaseContainer {

    boolean result_connection, result_closing;
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

    Vector<String> query;
    Vector<String> risultato_select;
    Result result;
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
    public ModelDatabaseContainer(ConcreteServer _server) {
        server = _server;
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


    }
    
    /**
     * Prende due date come ingresso, e ritorna la differenza tra la seconda
     * data e la prima data.
     *
     * @param date1
     * @param date2
     * @param timeUnit
     * @return long
     */
    public long getDiffDate(Date date1, Date date2, TimeUnit timeUnit) {
        Menu.printCyan(date1.toString());
        Menu.printCyan(date2.toString());
        Menu.printCyan("formatto...");
        Menu.printCyan(date1.getTime());
        Menu.printCyan(date2.getTime());

        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
