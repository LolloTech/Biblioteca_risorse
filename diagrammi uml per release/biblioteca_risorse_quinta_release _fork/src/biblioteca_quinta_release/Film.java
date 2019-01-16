package biblioteca_quinta_release;

import java.util.HashMap;

/**
 * Questa classe rappresenta i films contenuti nel database. Viene scelto di
 * utilizzare una Hashtable per memorizzare i dati, in quanto semplifica molto
 * le operazioni di aggiunga, ricerca e modifica dei parametri presenti all'
 * interno della stessa.
 *
 * @author Sviluppatore
 * @attributi: 
 * - id 
 * - identifier 
 * - titolo 
 * - anno_produzione 
 * - descrizione
 * - lingua 
 * - genere 
 * - durata // in minuti 
 * - attori[] // separati da ,
 * - regista
 * - licenze 
 * - casa_di_produzione 
 * - is_avaiable: indica se e' disponibile considerando il n. di licenze -
 * - is_active: indica se e' disponile nell'archivio
 *
 */

public class Film extends Resource {

    public Film() {
        attributi = new HashMap<String, String>();
        attributi.put("id", "");
        attributi.put("identifier", "");
        attributi.put("titolo", "");
        attributi.put("anno_produzione", "");
        attributi.put("descrizione", "");
        attributi.put("lingua", "");
        attributi.put("genere", "");
        attributi.put("durata", "");
        attributi.put("attori", "");
        attributi.put("regista", "");
        attributi.put("licenze", "");
        attributi.put("casa_di_produzione", "");
        attributi.put("is_avaiable", "");
        attributi.put("is_active", "");
        


    }

    public Film(Category categoria) {
        this.categoria = categoria;
        attributi = new HashMap<String, String>();
        attributi.put("id", "");
        attributi.put("identifier", "");
        attributi.put("titolo", "");
        attributi.put("anno_produzione", "");
        attributi.put("descrizione", "");
        attributi.put("lingua", "");
        attributi.put("genere", "");
        attributi.put("durata", "");
        attributi.put("attori", "");
        attributi.put("regista", "");
        attributi.put("licenze", "");
        attributi.put("casa_di_produzione", "");
        attributi.put("is_avaiable", "");
        attributi.put("is_active", "");

    }

    @Override
    public boolean find(String key, String value_to_search) {
        //se e' contenuta la chiave key, e se e' contenuto nel valore
        //ritornato dalla chiave anche value_to_search, ritorna true.
        if (attributi.get(key) != null && attributi.get(key).equals(value_to_search)) {
            return true;
        }
        return false;

        //TO COMPLETE.
    }

    public Category getCategory() {
        return this.categoria;
    }

    public String getLicenseNumber() {
        return attributi.get("license_number");
    }

    public String getId() {
        return attributi.get("id");
    }

    //public String getNome(){return attributi.get("nome");}
    public String getTitolo() {
        return attributi.get("titolo");
    }

    public String getAnno() {
        return attributi.get("anno_produzione");
    }

    public String getGenere() {
        return attributi.get("genere");
    }

    public String getDurata() {
        return attributi.get("durata");
    }

    public String getAttori() {
        return attributi.get("attori");
    }

    public String getLicenze() {
        return attributi.get("licenze");
    }

    public boolean setDesc(String value) {
        if (attributi.containsKey("descrizione")) {
            attributi.put("descrizione", value);
            return true;

        }
        return false;
    }
    public String getCasaProduzione() {
        return attributi.get("casa_di_produzione");
    }

    public String getIdentifier() {
        return attributi.get("identifier");
    }

    public String getDescrizione() {
        return attributi.get("descrizione");
    }
    public String getRegista() {
        return attributi.get("regista");
    }

    public String getLingua() {
        return attributi.get("lingua");
    }

    public boolean isAvaiable() {
        if (attributi.get("is_avaiable").equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isActive() {
        if (attributi.get("is_active").equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean setId(String value) {
        if (attributi.containsKey("id")) {
            attributi.put("id", value);
            return true;

        }
        return false;
    }

    public boolean setNome(String value) {
        if (attributi.containsKey("nome")) {
            attributi.put("nome", value);
            return true;

        }
        return false;
    }

    public boolean setTitolo(String value) {
        if (attributi.containsKey("titolo")) {
            attributi.put("titolo", value);
            return true;

        }
        return false;
    }
    public boolean setLingua(String value) {
        if (attributi.containsKey("lingua")) {
            attributi.put("lingua", value);
            return true;

        }
        return false;
    }

    public boolean setAnno(String value) {
        if (attributi.containsKey("anno_pubblicazione")) {
            attributi.put("anno_pubblicazione", value);
            return true;

        }
        return false;
    }

    public boolean setGenere(String value) {
        if (attributi.containsKey("genere")) {
            attributi.put("genere", value);
            return true;

        }
        return false;
    }

    public boolean setDurata(String value) {
        if (attributi.containsKey("durata")) {
            attributi.put("durata", value);
            return true;

        }
        return false;
    }

    public boolean setAttori(String value) {
        if (attributi.containsKey("attori")) {
            attributi.put("attori", value);
            return true;

        }
        return false;
    }
    public boolean setRegista(String value) {
        if (attributi.containsKey("regista")) {
            attributi.put("regista", value);
            return true;

        }
        return false;
    }

    public boolean setLicenze(String value) {
        if (attributi.containsKey("licenze")) {
            attributi.put("licenze", value);
            return true;

        }
        return false;
    }

    public boolean setCasaProduzione(String value) {
        if (attributi.containsKey("casa_di_produzione")) {
            attributi.put("casa_di_produzione", value);
            return true;

        }
        return false;
    }

    public boolean setIsAvaiable(String value) {
        if (attributi.containsKey("is_avaiable")) {
            attributi.put("is_avaiable", value);
            return true;

        }
        return false;
    }

    public boolean setIsActive(String value) {
        if (attributi.containsKey("is_active")) {
            attributi.put("is_active", value);
            return true;

        }
        return false;
    }

    public boolean setIdentifier(String value) {
        if (attributi.containsKey("identifier")) {
            attributi.put("identifier", value);
            return true;

        }
        return false;
    }

    protected void initialize() {

    }

    public void insertCategory(String identifier) {
        categoria = new Category(identifier);
    }

    public void setCategory(String identifier, String name) {
        categoria = new Category(identifier, name);
    }

    public void setCategory(Category c) {
        categoria = c;
    }

    protected boolean insertKeyValue(String key, String value) {
        if (this.attributi.containsKey(key)) {
            this.attributi.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public String get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String set(String key, String value_to_put) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
