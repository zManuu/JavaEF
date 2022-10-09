package example;

import javaEF.Database;
import javaEF.Table;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static List<Player> Players = new ArrayList<>();

    public static void main(String[] args) {

        // Create the Database Root Object by using the Constructor
        var db = new Database("localhost", "root", "", "javaef", 3306);

        // Import a Table by using the Constructor
        var playerTable = new Table<Player>(db, "players", Player.class);

        // Set up a cached version of the table, so you don't have to fetch data everytime...
        Players = playerTable.getAll();

        // Update some data as you like to (plain Java)...
        for (var player : Players) {
            player.setIp(player.ip + 1);
        }

        // Save the table after editing data (Table#save(Entity) is recommendet if you didn't edit everything!)
        playerTable.save(Players);

        // There are currently three ways of getting live Entity data from the DB...
        var byId = playerTable.getById(1); // The fastest but also least customizable: Get an Entity by its ID
        var find = playerTable.getAll().stream().filter(e -> e.name.equals("Manu")).findAny().orElse(null); // Slower but 100% Customizable
        var query = playerTable.query(e -> e.name.equals("Manu")); // Uses the find method above but packs in into less code

        // Compare entities
        var equals = byId.equalsId(find);
        var equals2 = byId.equalsId(query);

        // Insert a new Entity
        var newPlayer = new Player(-1, "new player entry", 222, false);
        playerTable.insert(newPlayer);
    }

}
