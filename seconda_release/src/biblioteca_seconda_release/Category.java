
package biblioteca_seconda_release;

import java.util.ArrayList;

/**
 * Classe che rappresenta le categorie delle risorse.
 * 
 * @author Sviluppatore
 * 
 */
public class Category {
    private ArrayList<Category> sottocategorie;
    private String name;
    private String identifier;
    private String description;
    Category(String identifier){
        this.identifier = identifier;
        this.sottocategorie = new ArrayList();
        this.description = "";
    }
    
    Category(String identifier, String name){
        this.name = name;
        this.identifier = identifier;
        this.sottocategorie = new ArrayList();
        this.description = "";
    }
    Category(String identifier, String name, String desc){
        this.name = name;
        this.identifier = identifier;
        this.description = desc;
        this.sottocategorie = new ArrayList();
    }
    
    public String getName(){return this.name;}
    public String getDesc(){ return this.description;}
    public String getIdentifier(){ return this.identifier;}
    /**
     * 
     * Aggiungo una sottocategoria alla categoria principale.
     * @param name
     * @return Success. 
     */
    public boolean addSubcategory(String name)
    {
        return sottocategorie.add(new Category(name));
    }
    public boolean addSubcategory(String identifier,String name)
    {
        return sottocategorie.add(new Category(identifier,name));
    }
    public boolean addSubcategory(String identifier,String name, String desc)
    {
        return sottocategorie.add(new Category(identifier,name,desc));
    }
    /**
     * Ottengo la lista delle sottocategorie.
     * 
     * @return La lista delle sottocategorie di questa categoria, se non nulla.
     */
    public ArrayList<Category> getSubCategory()
    {
        return this.sottocategorie;
    }
    
    @Override
    public String toString(){return "id_"
            + this.identifier 
            + " " 
            + this.name 
            + " " 
            + this.description;}
    
}
