package com.teamzero.javaweb.service;

import java.sql.Date;
import java.util.List;

public interface ChatInfoService {

    //get chat list and recent (one week) history for one user
    String getChatListAndRecentHistory(Integer user_id);

    //get more (three more days) chat history for one selected chats
    String getMoreHistory(Integer user_id, Integer chat_id, Date from_date);

    //search history by key word
    String searchHistory(String key_word);
}
