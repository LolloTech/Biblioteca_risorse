
package biblioteca_terza_release;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author 291
 * 
 */
public abstract class Resource 
{
    protected Map<String,String> attributi;
    public Category categoria;
    
    public abstract boolean find();
    public abstract Category getCategory();
    
    
    
    
    
    
}
