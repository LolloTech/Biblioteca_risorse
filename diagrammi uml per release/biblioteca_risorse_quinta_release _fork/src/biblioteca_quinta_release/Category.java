
package biblioteca_quinta_release;

import java.util.ArrayList;

/**
 * Classe che rappresenta le categorie delle risorse.
 * 
 * @author Sviluppatore
 * 
 */
public class Category {
    private ArrayList<Category> sottocategorie;
    Category father;
    private String name;
    private String identifier;
    private String description;
    public Integer LOAN_DURATION = null;
    public Integer RENEW_DURATION = null;
    public Integer DAYS_UNTIL_RENEW = null;
    public Integer MAX_LOANS_TO_PERSON = null;
    
    Category(String identifier){
        this.identifier = identifier;
        this.sottocategorie = new ArrayList();
        this.name = "";
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
    Category(String identifier, String name, String desc, String father){
        this.name = name;
        this.identifier = identifier;
        this.description = desc;
        this.sottocategorie = new ArrayList();
        this.father = new Category(father);
    }
    public void setParameters(Integer loan, Integer renew, Integer until, Integer maxloans)
    {
        this.LOAN_DURATION = loan;
        this.RENEW_DURATION = renew;
        this.DAYS_UNTIL_RENEW = until;
        this.MAX_LOANS_TO_PERSON = maxloans;
    }
    public void setParameters(Integer loan, Integer renew, Integer until)
    {
        this.LOAN_DURATION = loan;
        this.RENEW_DURATION = renew;
        this.DAYS_UNTIL_RENEW = until;
    }
    public void setFather(String identifier)
        {this.father = new Category(identifier);}
    
    public String getName(){return this.name;}
    public String getDesc(){ return this.description;}
    public String getIdentifier(){ return this.identifier;}
    public Integer getLoanDuration(){return this.LOAN_DURATION;}
    public Integer getRenewDuration(){return this.RENEW_DURATION;}
    public Integer getDaysUntilRenew(){return this.DAYS_UNTIL_RENEW;}
    public Integer getMaxLoansToPerson(){return this.MAX_LOANS_TO_PERSON;}
    
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
    public boolean addSubcategory(String identifier,String name, String desc, String parent_identificator)
    {
        return sottocategorie.add(new Category(identifier,name,desc,parent_identificator));
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
    public Category getFather()
    {
        return this.father;
    }
    @Override
    public String toString(){return "id_"
            + this.identifier 
            + " " 
            + this.name 
            + " " 
            + this.description;}
    
}
