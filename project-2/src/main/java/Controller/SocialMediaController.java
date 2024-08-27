package Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    private SocialMediaService service;

    public SocialMediaController(){
        this.service = new SocialMediaService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("example-endpoint", this::exampleHandler);

        app.post("/register", this::accountRegistration);
        app.post("/login", this::accountAuthentication);

        app.post("/messages", this::createMessage);
        app.get("/messages", this::getMessages);
        app.get("/messages/{message_id}", this::getMessageById);
        app.delete("/messages/{message_id}", this::deleteMessageById);
        app.patch("/messages/{message_id}", this::updateMessageById);

        app.get("/accounts/{account_id}/messages", this::getMessagesForAccount);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


    private void accountRegistration(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        // Check body contains values
        if(!ctx.body().isEmpty()) {
            account = mapper.readValue(ctx.body(), Account.class);
        }        

        // Requested validations
        boolean userNameAvailable = service.userNameAvailable(account.getUsername());
        if(account.getUsername().isBlank() || account.getPassword().length() < 4 || !userNameAvailable) {
            ctx.status(400);
        } else {
            Account addedAccount = service.saveAccount(account);
            ctx.json(addedAccount);
        }        
    }

    private void accountAuthentication(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = new Account();
        // Check body contains values
        if(!ctx.body().isEmpty()) {
            account = mapper.readValue(ctx.body(), Account.class);
        }

        Account retrievedAccount = service.getAccount(account.getUsername());
        if(retrievedAccount == null || !account.getPassword().equals(retrievedAccount.getPassword())) {
            ctx.status(401);
        } else {
            ctx.json(retrievedAccount);
        }
    }

    private void createMessage(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = new Message();
        // Check body contains values
        if(!ctx.body().isEmpty()) {
            message = mapper.readValue(ctx.body(), Message.class);
        }

        boolean accountExists = service.accountExists(message.posted_by);
        if(message.getMessage_text().isBlank() || message.getMessage_text().length() >= 255 || !accountExists) {
            ctx.status(400);
        } else {
            Message addedMessage = service.saveMessage(message);
            ctx.json(addedMessage);
        }
    }

    private void getMessages(Context ctx) {
        List<Message> messages = service.getAllMessages();
        ctx.json(messages);
    }
    
    private void getMessageById(Context ctx) {
        int messageId = Integer.valueOf(ctx.pathParam("message_id"));
        try{
            Message message = service.getMessageById(messageId);
            ctx.json(message);
        } 
        catch (NoSuchElementException ex) {
            ctx.json("");
        }
    }

    private void deleteMessageById(Context ctx) {
        int messageId = Integer.valueOf(ctx.pathParam("message_id"));
        try{
            Message deletedMessage = service.deleteMessageById(messageId);
            ctx.json(deletedMessage);
        } 
        catch (NoSuchElementException ex) {
            ctx.json("");
        }
    }

    private void updateMessageById(Context ctx) throws JsonProcessingException {
        int messageId = Integer.valueOf(ctx.pathParam("message_id"));
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        message.setMessage_id(messageId);
        try{
            boolean messageExists = service.messageExists(messageId);
            if(message.getMessage_text().isBlank() || message.getMessage_text().length() > 255 || !messageExists) {
                ctx.status(400);
            } else {
                Message updatedMessage = service.updateMessage(message);
                ctx.json(updatedMessage);
            }
        } 
        catch (NoSuchElementException ex) {
            ctx.status(400);
        }
    }

    private void getMessagesForAccount(Context ctx) {
        int accountId = Integer.valueOf(ctx.pathParam("account_id"));
        
        boolean accountExists = service.accountExists(accountId);
        if(accountExists){
            List<Message> messages = service.getMessagesForAccount(accountId);
            ctx.json(messages);
        } else {
            ctx.json(new ArrayList<Message>());
        }
    }


}