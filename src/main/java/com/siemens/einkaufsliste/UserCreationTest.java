package com.siemens.einkaufsliste;

import java.time.LocalDate;

import com.siemens.einkaufsliste.database.model.User;
import com.siemens.einkaufsliste.database.repository.Database;
import com.siemens.einkaufsliste.database.repository.UserRepository;

public final class UserCreationTest {

	private UserCreationTest() {
		Database.connect();
		
		UserRepository users = Database.getUsers();
		
		// User 1-10
		users.registerUser(new User(0, "Max", "Müller", LocalDate.of(1990, 3, 15), User.Gender.MALE, "max.mueller@email.de", "Pass123!", true));
		users.registerUser(new User(0, "Anna", "Schmidt", LocalDate.of(1985, 7, 22), User.Gender.FEMALE, "anna.schmidt@email.de", "Secure456", false));
		users.registerUser(new User(0, "Thomas", "Weber", LocalDate.of(1992, 11, 8), User.Gender.MALE, "thomas.weber@email.de", "MyPass789", true));
		users.registerUser(new User(0, "Julia", "Wagner", LocalDate.of(1988, 5, 30), User.Gender.FEMALE, "julia.wagner@email.de", "Julia2023!", false));
		users.registerUser(new User(0, "Michael", "Becker", LocalDate.of(1995, 1, 12), User.Gender.MALE, "michael.becker@email.de", "MikeB123", true));
		users.registerUser(new User(0, "Sarah", "Hoffmann", LocalDate.of(1991, 9, 25), User.Gender.FEMALE, "sarah.hoffmann@email.de", "SarahH456", true));
		users.registerUser(new User(0, "David", "Schulz", LocalDate.of(1987, 4, 18), User.Gender.MALE, "david.schulz@email.de", "Dave789!", false));
		users.registerUser(new User(0, "Lisa", "Koch", LocalDate.of(1993, 12, 3), User.Gender.FEMALE, "lisa.koch@email.de", "LisaK2024", true));
		users.registerUser(new User(0, "Daniel", "Richter", LocalDate.of(1989, 6, 14), User.Gender.MALE, "daniel.richter@email.de", "DanR123!", false));
		users.registerUser(new User(0, "Laura", "Klein", LocalDate.of(1994, 2, 27), User.Gender.FEMALE, "laura.klein@email.de", "LauraK456", true));

		// User 11-20
		users.registerUser(new User(0, "Sebastian", "Wolf", LocalDate.of(1986, 8, 9), User.Gender.MALE, "sebastian.wolf@email.de", "SebW789", false));
		users.registerUser(new User(0, "Marie", "Schröder", LocalDate.of(1990, 10, 16), User.Gender.FEMALE, "marie.schroeder@email.de", "Marie123!", true));
		users.registerUser(new User(0, "Florian", "Neumann", LocalDate.of(1992, 3, 21), User.Gender.MALE, "florian.neumann@email.de", "Flo456!", false));
		users.registerUser(new User(0, "Sophie", "Schwarz", LocalDate.of(1988, 7, 5), User.Gender.FEMALE, "sophie.schwarz@email.de", "Sophie789", true));
		users.registerUser(new User(0, "Jan", "Zimmermann", LocalDate.of(1991, 11, 28), User.Gender.MALE, "jan.zimmermann@email.de", "JanZ2023", true));
		users.registerUser(new User(0, "Emma", "Braun", LocalDate.of(1995, 5, 13), User.Gender.FEMALE, "emma.braun@email.de", "EmmaB123!", false));
		users.registerUser(new User(0, "Lukas", "Hofmann", LocalDate.of(1987, 1, 7), User.Gender.MALE, "lukas.hofmann@email.de", "LukasH456", true));
		users.registerUser(new User(0, "Hannah", "Hartmann", LocalDate.of(1993, 9, 19), User.Gender.FEMALE, "hannah.hartmann@email.de", "Hannah789!", false));
		users.registerUser(new User(0, "Tim", "Lange", LocalDate.of(1989, 4, 24), User.Gender.MALE, "tim.lange@email.de", "TimL2024", true));
		users.registerUser(new User(0, "Lena", "Schmitt", LocalDate.of(1994, 12, 11), User.Gender.FEMALE, "lena.schmitt@email.de", "LenaS123", false));

		// User 21-30
		users.registerUser(new User(0, "Felix", "Werner", LocalDate.of(1986, 6, 2), User.Gender.MALE, "felix.werner@email.de", "FelixW456!", true));
		users.registerUser(new User(0, "Mia", "Krause", LocalDate.of(1990, 2, 17), User.Gender.FEMALE, "mia.krause@email.de", "MiaK789", true));
		users.registerUser(new User(0, "Paul", "Meier", LocalDate.of(1992, 10, 29), User.Gender.MALE, "paul.meier@email.de", "PaulM123!", false));
		users.registerUser(new User(0, "Lea", "Lehmann", LocalDate.of(1988, 8, 6), User.Gender.FEMALE, "lea.lehmann@email.de", "LeaL456", true));
		users.registerUser(new User(0, "Jonas", "Köhler", LocalDate.of(1991, 3, 14), User.Gender.MALE, "jonas.koehler@email.de", "JonasK789!", false));
		users.registerUser(new User(0, "Nina", "Herrmann", LocalDate.of(1995, 11, 23), User.Gender.FEMALE, "nina.herrmann@email.de", "NinaH2023", true));
		users.registerUser(new User(0, "Moritz", "König", LocalDate.of(1987, 7, 1), User.Gender.MALE, "moritz.koenig@email.de", "MoritzK123", false));
		users.registerUser(new User(0, "Emily", "Walter", LocalDate.of(1993, 1, 26), User.Gender.FEMALE, "emily.walter@email.de", "EmilyW456!", true));
		users.registerUser(new User(0, "Leon", "Huber", LocalDate.of(1989, 9, 10), User.Gender.MALE, "leon.huber@email.de", "LeonH789", true));
		users.registerUser(new User(0, "Amelie", "Kaiser", LocalDate.of(1994, 5, 20), User.Gender.FEMALE, "amelie.kaiser@email.de", "AmelieK123!", false));

		// User 31-40
		users.registerUser(new User(0, "Niklas", "Fuchs", LocalDate.of(1986, 12, 4), User.Gender.MALE, "niklas.fuchs@email.de", "NiklasF456", true));
		users.registerUser(new User(0, "Charlotte", "Lang", LocalDate.of(1990, 4, 15), User.Gender.FEMALE, "charlotte.lang@email.de", "CharlotteL789!", false));
		users.registerUser(new User(0, "Simon", "Peters", LocalDate.of(1992, 8, 27), User.Gender.MALE, "simon.peters@email.de", "SimonP2024", true));
		users.registerUser(new User(0, "Sophia", "Jung", LocalDate.of(1988, 2, 9), User.Gender.FEMALE, "sophia.jung@email.de", "SophiaJ123", true));
		users.registerUser(new User(0, "Philipp", "Hahn", LocalDate.of(1991, 6, 18), User.Gender.MALE, "philipp.hahn@email.de", "PhilippH456!", false));
		users.registerUser(new User(0, "Isabella", "Schubert", LocalDate.of(1995, 10, 31), User.Gender.FEMALE, "isabella.schubert@email.de", "IsabellaS789", true));
		users.registerUser(new User(0, "Maximilian", "Vogel", LocalDate.of(1987, 5, 7), User.Gender.MALE, "maximilian.vogel@email.de", "MaxV123!", false));
		users.registerUser(new User(0, "Johanna", "Friedrich", LocalDate.of(1993, 11, 12), User.Gender.FEMALE, "johanna.friedrich@email.de", "JohannaF456", true));
		users.registerUser(new User(0, "Alexander", "Stein", LocalDate.of(1989, 3, 25), User.Gender.MALE, "alexander.stein@email.de", "AlexS789!", false));
		users.registerUser(new User(0, "Clara", "Gross", LocalDate.of(1994, 7, 8), User.Gender.FEMALE, "clara.gross@email.de", "ClaraG2023", true));

		// User 41-50
		users.registerUser(new User(0, "Benjamin", "Roth", LocalDate.of(1986, 1, 19), User.Gender.MALE, "benjamin.roth@email.de", "BenR123!", true));
		users.registerUser(new User(0, "Emilia", "Berger", LocalDate.of(1990, 9, 22), User.Gender.FEMALE, "emilia.berger@email.de", "EmiliaB456", false));
		users.registerUser(new User(0, "Fabian", "Winkler", LocalDate.of(1992, 5, 3), User.Gender.MALE, "fabian.winkler@email.de", "FabianW789!", true));
		users.registerUser(new User(0, "Lina", "Lorenz", LocalDate.of(1988, 12, 16), User.Gender.FEMALE, "lina.lorenz@email.de", "LinaL2024", false));
		users.registerUser(new User(0, "Tobias", "Baumann", LocalDate.of(1991, 8, 30), User.Gender.MALE, "tobias.baumann@email.de", "TobiasB123", true));
		users.registerUser(new User(0, "Maja", "Ludwig", LocalDate.of(1995, 4, 11), User.Gender.FEMALE, "maja.ludwig@email.de", "MajaL456!", true));
		users.registerUser(new User(0, "Elias", "Krüger", LocalDate.of(1987, 10, 24), User.Gender.MALE, "elias.krueger@email.de", "EliasK789", false));
		users.registerUser(new User(0, "Frieda", "Böhm", LocalDate.of(1993, 6, 5), User.Gender.FEMALE, "frieda.boehm@email.de", "FriedaB123!", true));
		users.registerUser(new User(0, "Noah", "Winter", LocalDate.of(1989, 2, 14), User.Gender.MALE, "noah.winter@email.de", "NoahW456", false));
		users.registerUser(new User(0, "Mathilda", "Sommer", LocalDate.of(1994, 11, 1), User.Gender.FEMALE, "mathilda.sommer@email.de", "MathildaS789!", true));

		// User 51-60
		users.registerUser(new User(0, "Robin", "Möller", LocalDate.of(1986, 7, 13), User.Gender.OTHER, "robin.moeller@email.de", "RobinM2023", false));
		users.registerUser(new User(0, "Valentina", "Fischer", LocalDate.of(1990, 3, 28), User.Gender.FEMALE, "valentina.fischer@email.de", "ValentinaF123!", true));
		users.registerUser(new User(0, "Adrian", "Bauer", LocalDate.of(1992, 11, 9), User.Gender.MALE, "adrian.bauer@email.de", "AdrianB456", true));
		users.registerUser(new User(0, "Greta", "Keller", LocalDate.of(1988, 5, 21), User.Gender.FEMALE, "greta.keller@email.de", "GretaK789!", false));
		users.registerUser(new User(0, "Vincent", "Jung", LocalDate.of(1991, 1, 4), User.Gender.MALE, "vincent.jung@email.de", "VincentJ2024", true));
		users.registerUser(new User(0, "Nora", "Graf", LocalDate.of(1995, 9, 17), User.Gender.FEMALE, "nora.graf@email.de", "NoraG123", false));
		users.registerUser(new User(0, "Oscar", "Heinrich", LocalDate.of(1987, 4, 29), User.Gender.MALE, "oscar.heinrich@email.de", "OscarH456!", true));
		users.registerUser(new User(0, "Paula", "Schuster", LocalDate.of(1993, 12, 10), User.Gender.FEMALE, "paula.schuster@email.de", "PaulaS789", true));
		users.registerUser(new User(0, "Henry", "Simon", LocalDate.of(1989, 8, 23), User.Gender.MALE, "henry.simon@email.de", "HenryS123!", false));
		users.registerUser(new User(0, "Ida", "Frank", LocalDate.of(1994, 4, 6), User.Gender.FEMALE, "ida.frank@email.de", "IdaF456", true));

		// User 61-67
		users.registerUser(new User(0, "Anton", "Albrecht", LocalDate.of(1986, 10, 18), User.Gender.MALE, "anton.albrecht@email.de", "AntonA789!", false));
		users.registerUser(new User(0, "Ella", "Schulze", LocalDate.of(1990, 6, 1), User.Gender.FEMALE, "ella.schulze@email.de", "EllaS2023", true));
		users.registerUser(new User(0, "Samuel", "Engel", LocalDate.of(1992, 2, 12), User.Gender.MALE, "samuel.engel@email.de", "SamuelE123!", true));
		users.registerUser(new User(0, "Marlene", "Zimmermann", LocalDate.of(1988, 9, 26), User.Gender.FEMALE, "marlene.zimmermann@email.de", "MarleneZ456", false));
		users.registerUser(new User(0, "Theo", "Vogt", LocalDate.of(1991, 5, 8), User.Gender.MALE, "theo.vogt@email.de", "TheoV789!", true));
		users.registerUser(new User(0, "Matilda", "Pohl", LocalDate.of(1995, 1, 20), User.Gender.FEMALE, "matilda.pohl@email.de", "MatildaP2024", false));
		users.registerUser(new User(0, "Emil", "Seidel", LocalDate.of(1987, 11, 2), User.Gender.MALE, "emil.seidel@email.de", "EmilS123!", true));
		
		Database.disconnect();
	}

	public static void main(String[] args) {
		new UserCreationTest();
	}

}
