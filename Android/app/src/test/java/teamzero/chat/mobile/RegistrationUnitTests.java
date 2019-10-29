package teamzero.chat.mobile;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegistrationUnitTests {
    @Test
    public void usernameIsValid() {
        Registration x = new Registration();
        assertTrue(x.validateUsername("UsernameX96"));
        assertTrue(x.validateUsername("Username_X96"));
        assertTrue(x.validateUsername("Username"));
        assertTrue(x.validateUsername("username"));
        assertTrue(x.validateUsername("UsErNAme_X_96"));
        assertTrue(x.validateUsername("UsErNAme_X_96Test"));
        assertTrue(x.validateUsername("UsEr.NAme_X_96Test"));
        assertTrue(x.validateUsername("UsEr-NAme_X_96Test"));
    }
    @Test
    public void passwordIsValid() {
        Registration x = new Registration();
        assertTrue(x.validatePasswords("ExamplePass23!", "ExamplePass23!"));
        assertTrue(x.validatePasswords("ExAmPLePASS23!", "ExAmPLePASS23!"));
        assertTrue(x.validatePasswords("examplePass23!", "examplePass23!"));
        assertTrue(x.validatePasswords("EXAMPLEpASS23!", "EXAMPLEpASS23!"));
        assertTrue(x.validatePasswords("example_paSs23!", "example_paSs23!"));
        assertTrue(x.validatePasswords("ex#ample_Pass23!", "ex#ample_Pass23!"));
        assertTrue(x.validatePasswords("23!ex##lE_pa$$", "23!ex##lE_pa$$"));
        assertTrue(x.validatePasswords("123456789!aA", "123456789!aA"));
        assertTrue(x.validatePasswords("aA!123456789", "aA!123456789"));

    }
}
