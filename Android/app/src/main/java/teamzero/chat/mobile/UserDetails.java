package teamzero.chat.mobile;

import java.util.ArrayList;
import java.util.HashMap;

public class UserDetails {

    static String username = "";
    static String password = "";
    static String avatar = "";
    static String time = "";
    static String messageContent = "";
    static String messageFrom = "";
    static HashMap<String, ArrayList<String>> messages = new HashMap<>();
    static String chatWith = "";
    static boolean historyIsHidden = false;
}