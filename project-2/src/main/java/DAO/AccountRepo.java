package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Model.Account;
import Util.ConnectionUtil;

public class AccountRepo {
    private Connection dbConnection; 

    public AccountRepo() {
        dbConnection = ConnectionUtil.getConnection();
    }

    public Account save(Account account) {
        try{
            String sqlStatement = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());

            int rowsAffected = ps.executeUpdate();
            if(rowsAffected == 1) {
                Account addedAccount = findByUsername(account.getUsername());
                return addedAccount;
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Account findById(int accountId) {
        try{
            String sqlStatement = "SELECT * FROM account WHERE account_id = ?";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Account account = new Account(rs.getInt(1), rs.getString(2), rs.getString(3));
                return account;
            }   

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Account findByUsername(String username) {
        try{
            String sqlStatement = "SELECT * FROM account WHERE username = ?";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Account account = new Account(rs.getInt(1), rs.getString(2), rs.getString(3));
                return account;
            }            

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;        
    }
}
