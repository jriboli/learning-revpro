package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    private AccountRepository accountRepo;
    private MessageRepository messageRepo;

    public MessageService(AccountRepository accountRepo, MessageRepository messageRepo){
        this.accountRepo = accountRepo;
        this.messageRepo = messageRepo;
    }

    public Message createMessage(Message message) {

        // Check postedBy is existing user
        boolean accountExists = accountExists(message.getPostedBy());
        if(!accountExists)
            throw new NoSuchElementException("No matching account with that Id");

        Message savedMessage = messageRepo.save(message);
        return savedMessage;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        messages = messageRepo.findAll();

        return messages;
    }

    public Message getMessageById(int messageId) {
        Message savedMessage = messageRepo.findById(messageId).orElse(null);

        return savedMessage;
    }

    public List<Message> getAllMessageForAccount(int accountId) {
        List<Message> messages = getAllMessages();
        List<Message> filteredMessages = new ArrayList<>();
        for(Message m : messages) {
            if(m.getPostedBy() == accountId) 
                filteredMessages.add(m);
        }

        return filteredMessages;
    }

    public int removeMessageById(int messageId) {
        Message savedMessage = messageRepo.findById(messageId).orElse(null);

        if(Objects.isNull(savedMessage)) {
            return 0;
        } else {
            messageRepo.delete(savedMessage);
            return 1;
        }        
    }

    public int updateMessage(Message message, int messageId) {
        // Check messageId exists
        boolean messageExists = messageExists(messageId);
        if(!messageExists)
            throw new NoSuchElementException("No matching account with that Id");

        messageRepo.save(message);
        return 1;
    }


    // PRIVATE --------------------------------------------//
    private boolean accountExists(int accountId) {
        Account savedAccount = accountRepo.findById(accountId).get();
        
        if(Objects.isNull(savedAccount))
            return false;

        return true;
    }

    private boolean messageExists(int messageId) {
        Message savedMessage = messageRepo.findById(messageId).get();

        if(Objects.isNull(savedMessage))
            return false;

        return true;
    }
}
