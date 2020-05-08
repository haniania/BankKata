package bank;


import java.sql.*;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:8889/mysql?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "root";
    private static final String TABLE_NAME = "accounts";

    private Connection c;
    //private static final String TABLE_NAME = "accounts";  // Stock the name of the Table
    private static Pattern pattern;
    private static Matcher matcher;

    public Bank() {
        initDb();
    }

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("base de donnée Ouverte avec succès");
            // Create the accounts table
            try (Statement s = this.c.createStatement()) {
                s.executeUpdate("CREATE TABLE " + TABLE_NAME + "(\n" +
                        "noun VARCHAR(255) NOT NULL,\n" +
                        "scales INT NOT NULL,\n" +
                        "limite INT NOT NULL,\n" +
                        "bolt BOOLEAN NOT NULL DEFAULT false,\n" +   // Account non blocké par defaut
                        "PRIMARY KEY (noun))");
                System.out.println("Table 'accounts' à bien été créée");
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("N'a pas pu fermer la base de données : " + e);
        }
    }

    void dropAllTables() {
        // Supprime la table account
        try (Statement s = c.createStatement()) {
            s.executeUpdate(
                    "DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public Account CheckAccount(String noun) {
        String resquest1 = "SELECT * FROM " + TABLE_NAME + " WHERE noun = '" + noun + "'";
        try (Statement s = c.createStatement()) {
            ResultSet set = s.executeQuery(resquest1);
            // verifie si le compte existe déjà
            if (set.next()) return new Account(set.getString(1),
                    set.getInt(2),
                    set.getInt(3),
                    set.getBoolean(4));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public void createNewAccount(String noun, int scales, int limite) {
        Pattern Name = Pattern.compile("([a-zA-Z]*([ ]|-)?)*");
        if (Name.matcher(noun).matches()) {  // vérifie si la synthaxe du nom est bonne
            Account account = CheckAccount(noun);
            if (account == null) {  // vérifie que le nom n'est pas déjà utilisé
                if (limite <= 0) { // vérifie que la limite est bien négative
                    String request = "INSERT INTO " + TABLE_NAME + " (noun,scales,limite) " +
                            "VALUES ('" + noun + "','" + scales + "','" + limite + "')";
                    try (Statement s = c.createStatement()) {
                        s.executeUpdate(request);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                } else System.out.println("Limite ne peut être supérieur à 0 ");
            }
            else
                System.out.println("Le nom : " +noun+" est déjà utilisé");
        } else
            System.out.println("Le nom ne respecte pas la synthaxe");
    }

    public String printAllAccounts() {
        String resquest = "SELECT * FROM " + TABLE_NAME;
        StringBuilder accounts = new StringBuilder();
        try (Statement s = c.createStatement()) {
            ResultSet r = s.executeQuery(resquest);
            while (r.next()) {
                //Création d'un objet account
                accounts.append((new Account(r.getString(1), r.getInt(2), r.getInt(3), r.getBoolean(4))).toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return accounts.toString();
    }

    public void changeBalanceByName(String noun, int scalesModifier) {
        String request2 = "UPDATE " + TABLE_NAME + " SET scales = scales + " + scalesModifier + " WHERE noun = '" + noun + "'";
        try (Statement s = c.createStatement()) {
            Account NewAccount = CheckAccount(noun);
            if (NewAccount != null) {
                if (!NewAccount.isBolt() && scalesModifier >= NewAccount.getLimite()) // verifie que le compte n'est pas bloqué
                    s.executeUpdate(request2); // Mise à jour du champ balance
            } else
                System.out.println("This account" + noun + "doen't exist");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void blockAccount(String noun) {
        String request = "UPDATE " + TABLE_NAME + " SET bolt = 1 WHERE noun = '" + noun + "'";
        try (Statement s = c.createStatement()) {
            if (CheckAccount(noun) != null) s.executeUpdate(request);
            else
                System.out.println("il n'existe pas de compte au nom de: " + noun);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }



    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        StringBuilder res = new StringBuilder();

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res.append(Arrays.toString(currentRow));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res.toString();
    }

}