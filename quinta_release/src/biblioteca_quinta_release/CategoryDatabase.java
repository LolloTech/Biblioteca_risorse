
package biblioteca_quinta_release;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sviluppatore
 */
public class CategoryDatabase extends ModelDatabaseContainer{
    
    public CategoryDatabase(ConcreteServer _server) {
        super(_server);
        try {
            file_categorie = this.getCategoriesHandler();
        } catch (FileNotFoundException ex) {
            file_categorie = null;
            Logger.getLogger(Info.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
    /**
     * Ritorna il file File, riferimento a "categories.txt"
     *
     * @return File
     *
     */
    private File getCategoriesHandler() throws FileNotFoundException {

        // pass the path to the file as a parameter 
        File file = new File("categories.txt");
        Scanner sc = new Scanner(file);
        Menu.printPurple("Lettura da file: ");
        while (sc.hasNextLine()) {
            Menu.printPurple(sc.nextLine());
        }
        Menu.printPurple("__________________");

        return file;
    }

    /**
     * Ritorna la lista delle categorie e sottocategorie, presenti su file. Non
     * si occupa di inserire dei dati all'interno del database; questa
     * responsabilità viene lasciata a chi decide di leggere tutte le categorie
     * presenti nel file di testo "categories.txt" tramite questo metodo.
     *
     * All'interno del file, il carattere "B" rappresenta una risorsa(contenuta
     * all'interno della relativa categoria) che consiste in un Libro.
     * 
     * All'interno del file, il carattere "F" rappresenta una risorsa(contenuta 
     * all'interno della relativa categoria) che consiste in un Film.
     * 
     * @returnArrayList<Category>
     * @throws FileNotFoundException
     */
    public ArrayList<Category> getCategoriesFromFile() throws FileNotFoundException {
        ArrayList<Category> categorie = null;
        if (this.file_categorie != null) {
            categorie = new ArrayList();
            Scanner sc = new Scanner(file_categorie);
            boolean sottocategoria = false;

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                StringTokenizer st = new StringTokenizer(line, " ");
                Menu.printGreen("tokeni: " + st.countTokens());
                if (st.countTokens() == 9) {
                    //categoria padre con figli
                    String nome_categoria = st.nextToken();
                    String identificatore = st.nextToken();
                    String is_sub = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    int maxloans = Integer.parseInt(st.nextToken());
                    Category toAdd = new Category(identificatore, nome_categoria, "", "");
                    toAdd.setParameters(loan, renew, until, maxloans);
                    categorie.add(toAdd);
                    Menu.printGreen("max_loans: " + maxloans);
                }
                if (st.countTokens() == 10) {
                    //categoria padre senza figli

                    String nome_categoria = st.nextToken();
                    String identificatore = st.nextToken();
                    String is_sub = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    String tipo = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    int maxloans = Integer.parseInt(st.nextToken());
                    Category toAdd = new Category(identificatore, nome_categoria, "", "");
                    toAdd.setParameters(loan, renew, until, maxloans);
                    categorie.add(toAdd);

                    if (tipo.equalsIgnoreCase("B")) {
                        this.addBook(risorsa, n_licenze, identificatore);
                    } else if (tipo.equalsIgnoreCase("F")) {
                        this.addFilm(risorsa, n_licenze, identificatore);
                    }
                }
                if (st.countTokens() == 11) {
                    //sottocategoria
                    String nome_subcat = st.nextToken();
                    String identificatore = st.nextToken();
                    String identificatore_padre = st.nextToken();
                    String risorsa = st.nextToken();
                    String n_licenze = st.nextToken();
                    String tipo = st.nextToken();
                    int loan = Integer.parseInt(st.nextToken());
                    int renew = Integer.parseInt(st.nextToken());
                    int until = Integer.parseInt(st.nextToken());
                    Integer maxloans = null;
                    for (int i = 0; i < categorie.size(); i++) {
                        //attenzione! questo for ha creato problemi di sovrapposizione col padre.
                        //comunque vengono iterati anche i for, e questo if salva problemi di 
                        //identificazione.
                        //if(categorie.get(i).getIdentifier().equals(identificatore_padre))
                        if (categorie.get(i).getIdentifier().equals(identificatore_padre)) {
                            categorie.get(i).addSubcategory(identificatore, nome_subcat, " - ", identificatore_padre);
                            categorie.get(i).getSubCategory().get(categorie.get(i).getSubCategory().size() - 1).setParameters(loan, renew, until);
                            //categorie.get(i).setParameters(loan, renew, until,maxloans);
                        }

                    }

                    if (tipo.equalsIgnoreCase("B")) {
                        this.addBook(risorsa, n_licenze, identificatore);
                    } else if (tipo.equalsIgnoreCase("F")) {
                        this.addFilm(risorsa, n_licenze, identificatore);
                    }

                }

            }

        }
        return categorie;
    }

    public String getCurrentDate() {
        return sdf.format(today.getTime());
    }

    /**
     * Aggiungo una categoria nel db, nella tabella category.
     *
     * @param name
     * @param desc
     * @param is_sub
     * @param identifier
     * @param parent
     * @param loan
     * @param renew
     * @param until
     * @param maxloans
     * @return boolean
     */
    public boolean addCategory(String name, String desc, boolean is_sub, String parent, String identifier,
            Integer loan, Integer renew, Integer until, Integer maxloans) {
        int myInt = is_sub ? 1 : 0;
        String maxmeans = "";
        if (maxloans != null) {
            maxmeans = maxloans.toString();
        }
        query.add("INSERT INTO category "
                + "(name,description,is_subcategory,parent,identifier,"
                + "loan_duration,renew_duration,days_until_renew,max_loans_to_person"
                + ") VALUES "
                + "('" + name + "','" + desc + "',"
                + myInt + ",'" + parent + "','" + identifier + "','"
                + loan + "','" + renew + "','" + until + "','"
                + maxmeans
                + "')");
        try {
            server.execute(query.get(0));
        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
        }
        /**
         * postcondizione
         */
        query.removeAllElements();;
        return true;
    }

    /**
     * Prendo la lista di tutte le categorie presenti nel server.
     *
     * @return ArrayList<Category>
     * @removed
     */
    /* public ArrayList<Category> getCategoriesList()
    {
        ArrayList<Category> categories = null;
        int size=0;
        //conto il numero di righe presenti nella tabella
        query.add("SELECT COUNT(*) AS size FROM category");
        //seleziono tutte le righe dalla tabella category
        query.add("SELECT * FROM category");
        try {
            //eseguo il COUNT
            result = server.execute(query.get(0));
            rs = (ResultSet)(result.getData());
            size = rs.getInt("size");
            //eseguo la SELECT
            result = server.execute(query.get(1));
            //finche' ci sono righe
            while(rs.next())
            {
                categories = new ArrayList();
                String nome = rs.getString("name");
                String desc = rs.getString("desc");
                //nome del padre della categoria, se esiste
                String parent = rs.getString("parent");
                
                //controllo se è una categoria con un parente,
                //ovvero una subcategory
                if(parent.equals(""))
                    categories.add(new Category(nome,desc)) ;
                
                //ciclo tutte le categorie, cercando le categorie padre ed
                //aggiungendovi i figli se avviene un match tra il campo
                //parent ed il campo nome della categoria padre
                for(int i = 0; i < categories.size(); i++)
                {
                    if(categories.get(i).getName().equals(parent))
                    {
                        categories.get(i).addSubcategory(nome,desc);
                    }
                }
            }
        } catch (SQLException ex) {
            Menu.print("Errore " + ex.getMessage());
            Menu.print("Errore su query: " + query.get(0));
            Menu.print("Errore su query: " + query.get(1));
        }
        
        // postcondizione
        query.removeAllElements();;
        return categories;
        
    }*/
    /**
     * Effettua l'update di una categoria indicata.
     *
     * @param wichCategory : verra' modificata la cat. con questo nome
     * @param newName : verra' impostata questa descrizione
     * @param desc : verra' impostata questa descrizione
     * @return
     */
    public boolean updateCategory(String wichCategory, String newName, String desc) {
        //int myInt = is_sub ? 1 : 0;
        query.add("UPDATE category SET "
                + "name ='" + newName + "', "
                + "description ='" + desc + "' "
                + //"is_subcategory ='" + myInt + "', " +
                //"parent ='" + parent + "' "          +
                "WHERE identifier ='" + wichCategory + "';");

        /*"(name,description,is_subcategory,parent) =" +
                "('" + name + "','" + desc + "',"                  +
                Boolean.toString(is_sub) + ",'" + parent           +
                "') WHERE name='" + name + "'") ;*/
        try {
            result = server.execute(query.get(0));
            //rs = (ResultSet)(result.getData());

        } catch (SQLException ex) {
            Menu.printCyan("Errore " + ex.getMessage());
            Menu.printCyan("Errore su query: " + query.get(0));
        }
        /**
         * postcondizione
         */
        query.removeAllElements();;
        return true;
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
     * Ritorna la lista di tutte le categorie presenti nel database.
     *
     * @return ArrayList<Category>
     * @throws IllegalArgumentException
     * @throws SQLException
     */
    public ArrayList<Category> getCategoriesList() throws IllegalArgumentException, SQLException {
        ArrayList<Category> categorie = new ArrayList();
        query.add("SELECT * from category WHERE is_subcategory=0");
        query.add("SELECT * from category WHERE is_subcategory=1");

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
        rs = (ResultSet) (result.getData());
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                if (!rs.getBoolean("is_subcategory")) {
                    String nome = rs.getString("name");
                    String desc = rs.getString("description");
                    String identifier = rs.getString("identifier");
                    String father = rs.getString("parent");
                    categorie.add(new Category(identifier, nome, desc, father));
                }
            }
        }
        //ora procedo con le sottocategorie
        result = server.execute(query.get(1));
        rs = (ResultSet) (result.getData());
        if (rs == null) {
            System.out.println("fail");
        } else {
            while (rs.next()) {
                if (rs.getBoolean("is_subcategory")) {
                    String nome = rs.getString("name");
                    String desc = rs.getString("description");
                    String identifier = rs.getString("identifier");
                    String parent = rs.getString("parent");
                    for (int i = 0; i < categorie.size(); i++) {
                        //matching tra categoria padre e figlia
                        if (categorie.get(i).getIdentifier().equals(parent)) {
                            //aggiunga del figlio al padre
                            categorie.get(i).addSubcategory(identifier, nome, desc);
                        }
                    }
                }

            }
        }

        query.removeAllElements();
        return categorie;

    }
    /**
     * Aggiunge un libro nuovo, nel database, nella tabella book. 
     * Implementazione necessaria dentro questa classe, in quanto da questa
     * stessa classe vengono aggiunge le risorse iniziali presenti nella 
     * applicazione, in quanto durante il parsing del file "categories.txt"
     * avviene il riconoscimento del tipo di risorsa. Pertanto, per causa delle 
     * nostre scelte implementative, questa funzionalità richiamata durante la 
     * creazione delle categorie viene inserita qui.
     *
     * @author
     * @param libro
     * @return boolean
     */
    private boolean addBook(String identifier, String license, String categoryIdent) {
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
     * Aggiunge un film nuovo, nel database, nella tabella film.
     * Implementazione necessaria dentro questa classe, in quanto da questa
     * stessa classe vengono aggiunge le risorse iniziali presenti nella
     * applicazione, in quanto durante il parsing del file "categories.txt"
     * avviene il riconoscimento del tipo di risorsa. Pertanto, per causa delle
     * nostre scelte implementative, questa funzionalità richiamata durante la
     * creazione delle categorie viene inserita qui.
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
    
}
