package Shop.Office.Conditions;

	import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Shop.Office.Administration.ManagerInterface;

import Shop.Office.Persons.Person;

	public class HsqlPersonManager 
		implements ManagerInterface<Person>
	{

		Connection connection;
		private String url="jdbc:hsqldb:hsql://localhost/workdb";

		String createTable = "CREATE TABLE Persons(" +
				"id bigint GENERATED BY DEFAULT AS IDENTITY," +
				"name VARCHAR(20)," +
				"pesel VARCHAR(20))";

		Statement stmt;
		PreparedStatement savePerson;
		PreparedStatement getAll;
		PreparedStatement deletePerson;
		PreparedStatement getPerson;
		public HsqlPersonManager()
		{
			try {
				connection=DriverManager.getConnection(url);
				stmt = connection.createStatement();

				//utworzenie tabeli
				ResultSet rs = connection.getMetaData().getTables(null, null, null, null);
				boolean tableExists = false;

				while(rs.next())
				{
					if(rs.getString("TABLE_NAME").equalsIgnoreCase("Persons"))
					{
						tableExists=true;
						break;
					}
				}
				if(!tableExists)
				{
					stmt.executeUpdate(createTable);
				}

				//zapytania na bazie
				getAll=connection.prepareStatement("" +
						"Select * From Persons");
				deletePerson=connection.prepareStatement("" +
						"DELETE From Persons where name=?");
				getPerson = connection.prepareStatement("" +
						"SELECT * FROM Persons WHERE id=?");

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		public Person get(int id) {
			Person result=null;
			try {
				getPerson.setInt(1, id);
				ResultSet rs = getPerson.executeQuery();
				while(rs.next()){
					result = new Person(rs.getString("name"),rs.getString("pesel"));
					break;
				}
				return result;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}

		public List<Person> getAll() {

			List<Person> result = new ArrayList<Person>();

			try {
				ResultSet rs= getAll.executeQuery();
				while(rs.next())
					result.add(new Person(rs.getString("Name"),rs.getString("Pesel")));

				return result;

			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

		}

		public boolean save(Person obj) {
			try {
				savePerson = connection.prepareStatement("" +
						"INSERT INTO Persons(name,pesel)" +
						"VALUES(?,?)");
				savePerson.setString(1, obj.getName());
				savePerson.setString(2, obj.getPesel());

				return savePerson.execute();
			} 
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}

		}

		public boolean delete(Person obj) {
			try {
				deletePerson.setString(1, obj.getName());
				deletePerson.executeUpdate();
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		}

	}