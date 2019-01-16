/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca_prima_release;

import java.util.Scanner;

/**
 *
 * @author Sviluppatore
 */
public class Menu {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    static Scanner scanner = new Scanner(System.in);
    private static int status = 1;
   
    public static void print(String string){System.out.println(string);}
    public static void printGreen(String string){System.out.println(ANSI_GREEN + string + ANSI_RESET);}
    public static void printPurple(String string){System.out.println(ANSI_PURPLE + string + ANSI_RESET);}
    public static void printCyan(String string){System.out.println(ANSI_CYAN + string + ANSI_RESET);}
    
    public int getStatus(){return status;}
    public void setStatus(int status){this.status = status;}
    public static String introMenu()
    {
        //status a.k.a. logged status
        print("Benvenuto!");
        print("1- login nel sistema");
        print("2- registrati nel sistema");
        print("3- esci");
        String choiche = scanner.nextLine();
        return choiche;
    }
    public static String introLoggedUserMenu(String name)
    {
        //status a.k.a. logged status
        print("Benvenuto" + name + "!");
        print("1- visualizza la scadenza del tuo profilo");
        print("2- esci");
        String choiche = scanner.nextLine();
        return choiche;
        
    }
    public static String loggedUserRenewMenu(String name, String days)
    {
        //status a.k.a. logged status
        print(name + " il tuo profilo sta per scadere, hai " + days + " per rinnovarlo!");
        print("1- rinnova il tuo profilo");
        print("2- esci");
        String choiche = scanner.nextLine();
        return choiche;        
    }
    public static String registrationRequestMenu()
    {
        print(" Inserisci il tuo nome e cognome, username e password, separato da spazi");
        String choiche = scanner.nextLine();
        return choiche;
    }
    public static String loginRequestMenu()
    {
        print("inserisci il tuo nome utente e la tua password, separati da spazi");
        String choiche = scanner.nextLine();
        return choiche;
    }
    public static String operatorMenu()
    {
        print("1- visualizza tutti gli utenti registrati");
        print("2- aggiungi un altro operatore");
        print("3- esci");
        String choiche = scanner.nextLine();
        return choiche;
    }
    public static String rootMenu()
    {
        print("1- visualizza tutti gli utenti registrati");
        print("2- aggiungi un altro operatore");
        print("3- esegui comando console(solo selezione)");
        print("4- esci");
        String choiche = scanner.nextLine();
        return choiche;
    }
    
    
}
