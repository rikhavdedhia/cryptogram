package application;

public class admin {

	private String id;
	private String pass;
	
	public admin(){
		id = "admin";
		pass = "qwerty123";
	}

	public boolean verifyAdmin(String id, String pass){
		if(this.id.equals(id) && this.pass.equals(pass)) return true;
		return false;
	}

}
