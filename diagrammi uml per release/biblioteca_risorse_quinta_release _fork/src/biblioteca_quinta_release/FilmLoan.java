package biblioteca_quinta_release;

/**
 * La classe Loan si occupa di fare da modello per immagazzinare molto agilmente
 * dei dati ritornati da dei metodi della classe Info, per poter in questo modo
 * accedere in maniera agile a questo parametri ritornati, senza aver bisogno di
 * utilizzare metodi più dispendiosi del database locale.
 *
 * @author Sviluppatore
 */
public class FilmLoan {

    int id_loan;
    public boolean is_active;
    public boolean is_renewed;
    public boolean is_avaiable;
    public Film film;
    public User user;
    public String data_inizio_prestito;
    public String data_fine_prestito;
    public String data_inizio_proroga;
    public String data_fine_proroga;
    public String licenze;

}
