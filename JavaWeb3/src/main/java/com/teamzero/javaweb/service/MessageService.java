package com.teamzero.javaweb.service;

import com.teamzero.javaweb.vo.MessageVo;

public interface MessageService {

    //send message to one chat
    Boolean sendMessage(MessageVo content);
}
