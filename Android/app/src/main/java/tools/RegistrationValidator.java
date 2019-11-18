package tools;

import java.util.regex.PatternSyntaxException;

public class RegistrationValidator {

    public boolean validateUsername(String username) {
        /* Check if:
            1) The field is not empty
            2) It does not contain illegal characters (anything except '-', '.' and '_')
            3) It does not start with numbers
            4) It is between 3 and 30 characters
         */

        try {
            boolean valid = (username != null) && username.matches("\\b[a-zA-Z][a-zA-Z0-9\\-._]{3,30}\\b");

            if (!valid)
                return false;

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
        }
        return true;
    }

    public boolean validatePassword(String password) {
        /* Check if:
            1) Password contains at least 1 uppercase character     (?=.*[A-Z])
            2) Password contains at least 1 lowercase character     (?=.*[a-z])
            3) Password contains at least 1 digit                   (?=.*[0-9])
            4) Password contains at least 1 special character       (?=.*[@#!?$%^&+])
            5) Password must not contain white spaces               (?=\S+$)
            6) Password is between 6 and 30 characters              {6,30}
         */

        // We check 1-6 only for one password (no need for both)

        try {
            boolean valid = (password != null) && password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?$%^&+])(?=\\S+$).{6,30}");

            if (!valid) {
                return false;
            }

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean passwordsAreEqual(String password, String confirmPassword) {

        // Check if passwords are the same
        try {

            if(!password.equals(confirmPassword))
                return false;

        } catch(NullPointerException ex1) {
            ex1.printStackTrace();
            return false;
        }

        return true;
    }

    // Make this method RFC822 compliant? (http://www.ex-parrot.com/~pdw/Mail-RFC822-Address/Mail-RFC822-Address.html)
    public boolean validateEmail(String email) {
        /* Check if:
            1) E-mail contains only one '@' character
            2) E-mail contains at least a dot ('.')
            3) E-mail contains between 2 and 6 characters after a dot ('.')
         */

        try {
            boolean valid = (email != null) && email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");

            if (!valid)
                return false;

        } catch (PatternSyntaxException ex) {
            // Invalid regex
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean emailsAreEqual(String email, String confirmEmail) {
        // Check if emails are the same
        try {
            if(!email.equals(confirmEmail))
                return false;

        } catch(NullPointerException ex1) {
            ex1.printStackTrace();
            return false;
        }

        return true;
    }
}
