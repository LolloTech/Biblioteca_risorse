
package biblioteca_terza_release;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Sviluppatore
 * 
 */
public abstract class Resource 
{
    protected Map<String,String> attributi;
    public Category categoria;
    
    public abstract boolean find(String key, String value_to_search);
    public abstract Category getCategory();
    
    
    
    
    
    
}
