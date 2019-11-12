package teamzero.javaweb.service;

import teamzero.javaweb.vo.MessageVo;

public interface MessageService {

    //send message to one chat
    Boolean sendMessage(MessageVo content);
}
