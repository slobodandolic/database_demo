package klasa;

import java.sql.SQLException;

public class DatabaseMain extends DatabaseInit {

	public static void main(String[] args) {

		DatabaseInit db = new DatabaseInit();
		
		db.dbConnect(); 

		db.fillDbase();
		
		//db.checkAward();

		//System.out.println("Prazni kodovi se nalaze na id mestima: " + db.ponovljeniKodovi.toString());
		
		//db.checkDuplicates();
		
		//db.update();
		
		//db.select();
		
		//db.delete();
		
		//db.insert();
		
		
		
		try {
			db.connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.sc.close();
	
	

	}

}
