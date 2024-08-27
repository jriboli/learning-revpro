package Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import DAO.AccountRepo;
import DAO.MessageRepo;
import Model.Account;
import Model.Message;

public class SocialMediaService {
    private AccountRepo accountRepo;
    private MessageRepo messageRepo;

    public SocialMediaService() {
        this.accountRepo = new AccountRepo();
        this.messageRepo = new MessageRepo();
    }

    // ------------------ ACCOUNT -----------------------------
    public Account getAccount(String username) {
        Account account = findAccountByUsername(username);
        return account;
    }

    public Account saveAccount(Account accountData) {
        return accountRepo.save(accountData);
    }

    public List<Message> getMessagesForAccount(int accountId) {
        List<Message> messages = getAllMessages();
        List<Message> filteredMessages = new ArrayList<>();
        for(Message m : messages) {
            if(m.getPosted_by() == accountId) 
                filteredMessages.add(m);
        }

        return filteredMessages;
    }

    private Account findAccountById(int accountId) {
        return accountRepo.findById(accountId);
    }

    private Account findAccountByUsername(String username) {
        return accountRepo.findByUsername(username);
    }

    public boolean userNameAvailable(String username) {
        return Objects.isNull(accountRepo.findByUsername(username));
    }


    // ------------------ MESSAGE -----------------------------
    public List<Message> getAllMessages() {
        return messageRepo.findAll().get();
    }

    public Message getMessageById(int messageId) {
        Message message = findOrCreateMessage(messageId);
        return message;
    }

    public Message deleteMessageById(int messageId) {
        Message message = findOrCreateMessage(messageId);
        messageRepo.delete(message);

        return message;
    }

    public Message saveMessage(Message messageData) {
        int messageId = messageData.getMessage_id();
        Message message = findOrCreateMessage(messageId);
        message.setMessage_text(messageData.getMessage_text());
        message.setPosted_by(messageData.getPosted_by());
        message.setTime_posted_epoch(messageData.getTime_posted_epoch());

        return messageRepo.save(message).get();
    }

    public Message updateMessage(Message messageData) {
        int messageId = messageData.getMessage_id();
        String messageText = messageData.getMessage_text();

        return messageRepo.updatePost(messageId, messageText).get();
    }

    private Message findOrCreateMessage(int messageId) {
        Message message;
        if(messageId == 0) {
            message = new Message();
        }
        else {
            message = findMessageById(messageId);
        }

        return message;
    }

    private Message findMessageById(int messageId) {
        return messageRepo.findById(messageId).orElseThrow(() -> new NoSuchElementException("No matching Message Id."));
    }

    public boolean accountExists(int accountId) {
        return !Objects.isNull(accountRepo.findById(accountId));
    }

    public boolean messageExists(int messageId) {
        return !messageRepo.findById(messageId).isEmpty();
    }
}
