package biblioteca_quinta_release;


/**
 * 
 * @author Sviluppatore 
 * 
*/


import java.util.StringTokenizer;

public class User {
	
	private String name;
	private String surname;
	private String username;
	private String password;
	
        
        public User(String _name,String _username){
		name = _name;		
		username = _username;
		
	}
        public User(String _name,String _username,String _surname){
		name = _name;		
		username = _username;
                surname = _surname;
		
	}
        
	public User(String _name,String _surname,String _username,String _password){
		name = _name;
		surname = _surname;
		username = _username;
		password = _password;
	}
        public User(String _all){
            StringTokenizer st = new StringTokenizer(_all, " ");
            if(st.countTokens() <= 2)
            {
                    username = st.nextToken();
                    password = st.nextToken();   
            }
            else
            {
                
		if(st.hasMoreElements())
                    name = st.nextToken();
		if(st.hasMoreElements())
                    surname = st.nextToken();
		if(st.hasMoreElements())
                    username = st.nextToken();
		if(st.hasMoreElements())
                    password = st.nextToken();
            }
	}
	
	public void setName(String _name){
		name = _name;
	}
	
	public void setSurname(String _surname){
		surname=_surname;
	}
	
	public void setUsername(String _userName){
		username = _userName;
	}
	
	public void setPassword(String _password){
		password = _password;
	}
	
	public String getName(){
		return name;
	}
	
	public String getSurname(){
		return surname;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
        
        @Override
        public String toString()
        {
            return this.name     + " " +
                   this.surname  + " " +
                   this.username + " " +
                   this.password ;
        }

}