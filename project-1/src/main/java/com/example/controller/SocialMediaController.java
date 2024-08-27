package com.example.controller;

import javax.persistence.EntityExistsException;
import javax.security.auth.login.FailedLoginException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {

    private AccountService accountService;
    private MessageService messageService;

    public SocialMediaController(AccountService accountService, MessageService messageService) {
        this.accountService = accountService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {

        try{
            // Check username not blank
            if(account.getUsername().isEmpty())
                throw new IllegalArgumentException("Username is empty");

            // Check password at lest 4 char long
            if(account.getPassword().length() < 4)
                throw new IllegalArgumentException("Password must be more than 4 characters");

            Account savedAccount = accountService.createAccount(account);
            // SHOULD have been a 201
            //return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
            return new ResponseEntity<>(savedAccount, HttpStatus.OK);
        }
        catch(EntityExistsException ex) {
            // Status 409
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        catch(Exception ex) {
            // Status 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }        
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {

        try{
            Account savedAccount = accountService.getAccountByUsername(account.getUsername());
            // Verify credentials
            if(!savedAccount.getPassword().equals(account.getPassword()))
                throw new FailedLoginException("Invalid username and/or password");

            return new ResponseEntity<>(savedAccount, HttpStatus.OK);
        }
        catch(Exception ex) {
            // Return 401 - Unauthorized
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }        
    }

    @PostMapping("/messages") 
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {

        try{
            // Check messageTest is not blank
            if(message.getMessageText().isEmpty())
                throw new IllegalArgumentException("The message can not be empty or blank");

            // Check messageText less than 255
            if(message.getMessageText().length() > 255)
                throw new IllegalArgumentException("The message can not be over 255 characters");

            Message savedMessage = messageService.createMessage(message);
            // Should have been 201 created
            //return new ResponseEntity<>(savedMessage, HttpStatus.CREATED);
            return new ResponseEntity<>(savedMessage, HttpStatus.OK);
        }
        catch(Exception ex) {
            // Failure - 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }        
    }

    @GetMapping("/messages") 
    public ResponseEntity<List<Message>> getMessages() {

        // Always 200 - even if empty List of messages
        List<Message> messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable int messageId) {

        // Always 200 - even if empty
        Message savedMessage = messageService.getMessageById(messageId);
        return new ResponseEntity<>(savedMessage, HttpStatus.OK);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<String> removeMessageById(@PathVariable int messageId) {

        String response = "";
        // Return number of rows affects (1) 
        int numOfAffectedRows = messageService.removeMessageById(messageId);
        // Always 200 - even is 0 rows affected
        if(numOfAffectedRows > 0)
            response = numOfAffectedRows + "";

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@RequestBody Message message, @PathVariable int messageId) {
        
        try{
            // Check messageTest is not blank
            if(message.getMessageText().isEmpty())
                throw new IllegalArgumentException("The message can not be empty or blank");

            // Check messageText less than 255
            if(message.getMessageText().length() > 255)
                throw new IllegalArgumentException("The message can not be over 255 characters");

            // Return number of rows affects (1) - 200
            int numOfAffectedRows = messageService.updateMessage(message, messageId);
            return new ResponseEntity<>(numOfAffectedRows, HttpStatus.OK);
        }
        catch(Exception ex) {
            // Other erro - 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }        
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessageByUser(@PathVariable int accountId) {

        // Always 200 - even if empty List of messages
        List<Message> messages = messageService.getAllMessageForAccount(accountId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
