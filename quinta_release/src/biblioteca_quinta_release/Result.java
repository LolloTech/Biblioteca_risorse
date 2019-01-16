package biblioteca_quinta_release;


/**
 * 
 * @author Sviluppatore
 * 
 */
public class Result {

	private boolean status;
	private String errorDesc;
	private Object data;
	
	public Result(boolean _status,String _errorDesc){
		status=_status;
		errorDesc=_errorDesc;
	}
        
        public Result(boolean _status,String _errorDesc, Object _blob){
		status=_status;
		errorDesc=_errorDesc;
                data = _blob;
	}
	
        public Result(Object blob){
		this.data = blob;
	}
	
        
	public Result(){}
	
	public boolean getStatus(){
		return status;
	}
	
	public void setStatus(boolean _status){
		status=_status;
	}
	
	public String getErrorDesc(){
		return errorDesc;
	}
	
	public void setErrorDesc(String _errorDesc){
		errorDesc = _errorDesc;
	}
	
	public Object getData(){
		return data;
	}
	
	public void setData(Object _data){
		data = _data;
	}
}
