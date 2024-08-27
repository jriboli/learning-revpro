package DAO;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Model.Message;
import Util.ConnectionUtil;

public class MessageRepo {
    private Connection dbConnection; 

    public MessageRepo() {
        dbConnection = ConnectionUtil.getConnection();
    }

    //posted_by
    //message_text
    //time_posted_long

    public Optional<Message> save(Message message) {
        try{
            String sqlStatement = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            
            if(rs.next()) {
                Message addedMessage = findById(rs.getInt(1)).get();
                return Optional.ofNullable(addedMessage);
            }
            

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Optional<List<Message>> findAll() {
        List<Message> messages = new LinkedList<>();

        try{
            String sqlStatement = "SELECT * FROM message";
            Statement s = dbConnection.createStatement();
            ResultSet rs = s.executeQuery(sqlStatement);

            while(rs.next()) {
                messages.add(new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getLong(4)));
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(messages);
    }

    public Optional<Message> findById(int messageId) {
        Message message = null;
        try{
            String sqlStatement = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setInt(1, messageId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                message = new Message(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getLong(4));
            }
            
            
        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(message);
    }

    public void delete(Message message) {
        try{
            String sqlStatement = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setInt(1, message.getMessage_id());
            ps.executeQuery();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public Optional<Message> updatePost(int messageId, String messageText) {
        Message updatedMessage = null;
        try{
            String sqlStatement = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement ps = dbConnection.prepareStatement(sqlStatement);
            ps.setString(1, messageText);
            ps.setInt(2, messageId);

            ps.executeUpdate();
                
            updatedMessage = findById(messageId).get();

        } catch(Exception ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(updatedMessage);
    }
}
