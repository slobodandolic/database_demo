package klasa;

import java.sql.SQLException;

public class DatabaseMain extends DatabaseInit {

	public static void main(String[] args) {

		DatabaseInit db = new DatabaseInit();

		db.dbConnect();

		db.fillDbase();

		db.checkAward();
		
		// db.selectLast();

		
		
		
		
		
		//CRUD metode rade na tabeli kodova tj. dbkodovi.tabela
		// db.update();

		// db.select();

		// db.delete();

		// db.insert();

		try {
			db.connect.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.sc.close();

	}

}
