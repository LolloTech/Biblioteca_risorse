
package biblioteca_seconda_release;

import java.util.HashMap;

/**
 *
 * @author 
 * @attributi: 
 * - id
 * - identifier
 * - titolo
 * - anno_pubblicazione
 * - descrizione
 * - lingua
 * - genere
 * - numero_pagine
 * - autori[]
 * - licenze
 * - casa_editrice
 * - is_avaiable: indica se e' disponibile considerando il n. di licenze
 * - is_active: indica se e' disponile nell'archivio
 *
 */
public class Book extends Resource {
    
    Category category;

    public Book()
    {
        attributi = new HashMap<String, String>();
        attributi.put("id", "");
        attributi.put("identifier", "");
        attributi.put("titolo", "");
        attributi.put("anno_pubblicazione", "");
        attributi.put("genere", "");
        attributi.put("numero_pagine", "");
        attributi.put("autori", "");
        attributi.put("licenze", "");
        attributi.put("is_avaiable", "");
        attributi.put("is_active", "");
        attributi.put("casa_editrice", "");
        attributi.put("descrizione", "");
        attributi.put("lingua", "");
 
        
    }
    @Override
    public boolean find() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public String getId(){return attributi.get("id");}
    //public String getNome(){return attributi.get("nome");}
    public String getTitolo(){return attributi.get("titolo");}
    public String getAnno(){return attributi.get("anno_pubblicazione");}
    public String getGenere(){return attributi.get("genere");}
    public String getNumeroPagine(){return attributi.get("numero_pagine");}
    public String getAutori(){return attributi.get("autori");}
    public String getLicenze(){return attributi.get("licenze");}
    public String getCasaEditrice(){return attributi.get("casa_editrice");}
    public String getIdentifier(){return attributi.get("identifier");}
    public String getDescrizione(){return attributi.get("descrizione");}
    public String getLingua(){return attributi.get("lingua");}
    public Category getCategoria(){return this.category;}
    public boolean isAvaiable()
    {
        if(attributi.get("is_avaiable").equals("1"))
            return true;
        else
            return false;
    }
    public boolean isActive()
    {
        if(attributi.get("is_active").equals("1"))
            return true;
        else
            return false;
    }
    
    public boolean setId(String value)
    {
        if(attributi.containsKey("id"))
        {
            attributi.put("id",value);
            return true;
            
        }
        return false;
    }
    public boolean setNome(String value)
    {
        if(attributi.containsKey("nome"))
        {
            attributi.put("nome",value);
            return true;
            
        }
        return false;
    }
    public boolean setTitolo(String value)
    {
        if(attributi.containsKey("titolo"))
        {
            attributi.put("titolo",value);
            return true;
            
        }
        return false;
    }
    public boolean setAnno(String value)
    {
        if(attributi.containsKey("anno_pubblicazione"))
        {
            attributi.put("anno_pubblicazione",value);
            return true;
            
        }
        return false;
    }
    public boolean setGenere(String value)
    {
        if(attributi.containsKey("genere"))
        {
            attributi.put("genere",value);
            return true;
            
        }
        return false;
    }
    public boolean setNumeroPagine(String value)
    {
        if(attributi.containsKey("numero_pagine"))
        {
            attributi.put("numero_pagine",value);
            return true;
            
        }
        return false;
    }
    public boolean setAutori(String value)
    {
        if(attributi.containsKey("autori"))
        {
            attributi.put("autori",value);
            return true;
            
        }
        return false;
    }
    public boolean setLicenze(String value)
    {
        if(attributi.containsKey("licenze"))
        {
            attributi.put("licenze",value);
            return true;
            
        }
        return false;
    }
    public boolean setCasaEditrice(String value)
    {
        if(attributi.containsKey("casa_editrice"))
        {
            attributi.put("casa_editrice",value);
            return true;
            
        }
        return false;
    }
    public boolean setIsAvaiable(String value)
    {
        if(attributi.containsKey("is_avaiable"))
        {
            attributi.put("is_avaiable",value);
            return true;
            
        }
        return false;
    }
    public boolean setIsActive(String value)
    {
        if(attributi.containsKey("is_active"))
        {
            attributi.put("is_active",value);
            return true;
            
        }
        return false;
    }
    public boolean setIdentifier(String value)
    {
        if(attributi.containsKey("identifier"))
        {
            attributi.put("identifier",value);
            return true;
            
        }
        return false;
    }
    protected void initialize()
    {
        
    }
    public void insertCategory(String identifier)
    {
        category = new Category(identifier);
    }
    public void setCategory(String identifier,String name)
    {
        category = new Category(identifier,name);
    }
    public void setCategory(Category c)
    {
        category = c;
    }
    protected boolean insertKeyValue(String key, String value)
    {
        if(this.attributi.containsKey(key))
        {
            this.attributi.put(key, value);
            return true;
        }
        return false;
    }
    
}
