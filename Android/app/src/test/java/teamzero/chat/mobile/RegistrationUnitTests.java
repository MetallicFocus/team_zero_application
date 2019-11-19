package teamzero.chat.mobile;

import org.junit.Test;

import tools.RegistrationValidator;

import static org.junit.Assert.*;

/**
 * Unit tests for:
 * 1) Username validation / Anti SQL Injection testing
 * 2) Password validation / Anti SQL Injection testing
 * 3) Verifying password and confirmed password are the same
 * 4) Email validation / Anti SQL Injection testing
 * 5) Verifying email and confirmed email are the same
 */

public class RegistrationUnitTests {

    @Test
    public void usernameIsValid() {
        RegistrationValidator x = new RegistrationValidator();

        assertTrue(x.validateUsername("UsernameX96"));
        assertTrue(x.validateUsername("Username_X96"));
        assertTrue(x.validateUsername("Username"));
        assertTrue(x.validateUsername("username"));
        assertTrue(x.validateUsername("UsErNAme_X_96"));
        assertTrue(x.validateUsername("UsErNAme_X_96Test"));
        assertTrue(x.validateUsername("UsEr.NAme_X_96Test"));
        assertTrue(x.validateUsername("UsEr-NAme_X_96Test"));

        assertFalse(x.validateUsername("admin'"));
        assertFalse(x.validateUsername("admin' OR 'a'='a"));
        assertFalse(x.validateUsername("invalid-username' UNION SELECT 1, username, passwords FROM members WHERE 'x'='x"));
        assertFalse(x.validateUsername("' or 1=1--"));
        assertFalse(x.validateUsername("\" or 1=1--"));
        assertFalse(x.validateUsername("or 1=1--"));
        assertFalse(x.validateUsername("1'or'1'='1"));
        assertFalse(x.validateUsername("' or 'a'='a"));
        assertFalse(x.validateUsername("\" or \"a\"=\"a"));
        assertFalse(x.validateUsername("') or ('a'='a"));
        assertFalse(x.validateUsername("\") or (\"a\"=\"a"));
    }

    @Test
    public void passwordIsValid() {
        RegistrationValidator x = new RegistrationValidator();

        assertTrue(x.validatePassword("ExamplePass23!"));
        assertTrue(x.validatePassword("ExAmPLePASS23!"));
        assertTrue(x.validatePassword("examplePass23!"));
        assertTrue(x.validatePassword("EXAMPLEpASS23!"));
        assertTrue(x.validatePassword("example_paSs23!"));
        assertTrue(x.validatePassword("ex#ample_Pass23!"));
        assertTrue(x.validatePassword("23!ex##lE_pa$$"));
        assertTrue(x.validatePassword("123456789!aA"));
        assertTrue(x.validatePassword("aA!123456789"));

        assertFalse(x.validatePassword("' or 1=1--"));
        assertFalse(x.validatePassword("\" or 1=1--"));
        assertFalse(x.validatePassword("or 1=1--"));
        assertFalse(x.validatePassword("1'or'1'='1"));
        assertFalse(x.validatePassword("' or 'a'='a"));
        assertFalse(x.validatePassword("\" or \"a\"=\"a"));
        assertFalse(x.validatePassword("') or ('a'='a"));
        assertFalse(x.validatePassword("\") or (\"a\"=\"a"));
    }

    @Test
    public void passwordsAreEqual() {
        RegistrationValidator x = new RegistrationValidator();

        assertTrue(x.passwordsAreEqual("ExamplePass23!", "ExamplePass23!"));
        assertTrue(x.passwordsAreEqual("ExAmPLePASS23!", "ExAmPLePASS23!"));
        assertTrue(x.passwordsAreEqual("examplePass23!", "examplePass23!"));
        assertTrue(x.passwordsAreEqual("EXAMPLEpASS23!", "EXAMPLEpASS23!"));
        assertTrue(x.passwordsAreEqual("example_paSs23!", "example_paSs23!"));
        assertTrue(x.passwordsAreEqual("ex#ample_Pass23!", "ex#ample_Pass23!"));
        assertTrue(x.passwordsAreEqual("23!ex##lE_pa$$", "23!ex##lE_pa$$"));
        assertTrue(x.passwordsAreEqual("123456789!aA", "123456789!aA"));
        assertTrue(x.passwordsAreEqual("aA!123456789", "aA!123456789"));

        assertFalse(x.passwordsAreEqual("ExamplePass23!", "ExmplePass23!"));
        assertFalse(x.passwordsAreEqual("ExAmPLePASS23!", "ExAmPLePSS2"));
        assertFalse(x.passwordsAreEqual("examplePass23!", "exaplePass23!"));
        assertFalse(x.passwordsAreEqual("EXAMPLEpASS23!", "EXAMPLEpASS23"));
        assertFalse(x.passwordsAreEqual("example_paSs23!", "xample_paSs23!"));
        assertFalse(x.passwordsAreEqual("ex#ample_Pass23!", "ex#amplePass23!"));
        assertFalse(x.passwordsAreEqual("23!ex##lE_pa$$", "23!ex# #lE_pa$$"));
        assertFalse(x.passwordsAreEqual("123456789!aA", "12345  6789!a A"));
        assertFalse(x.passwordsAreEqual("aA!123456789", ""));
    }

    @Test
    public void emailIsValid() {
        RegistrationValidator x = new RegistrationValidator();

        assertTrue(x.validateEmail("email@email.com"));
        assertTrue(x.validateEmail("EMAIL@EMAIL.COM"));
        assertTrue(x.validateEmail("eMaIL@eMaIL.cOm"));
        assertTrue(x.validateEmail("eMaIL@eMaIL.co.uk"));
        assertTrue(x.validateEmail("email_@EMAIL.COM"));
        assertTrue(x.validateEmail("email_email@email.co.uk"));
        assertTrue(x.validateEmail("testing_email231@email.COM"));
        assertTrue(x.validateEmail("email+100@email.com"));
        assertTrue(x.validateEmail("email+100@e-mail.com"));
        assertTrue(x.validateEmail("email+100@e-mail.co.uk"));

        assertFalse(x.validateEmail("\"'OR 1=1--\"@gmail.com"));
        assertFalse(x.validateEmail("'OR 1=1--@gmail.com"));
        assertFalse(x.validateEmail("' or 1=1--@gmail.com"));
        assertFalse(x.validateEmail("\" or 1=1--@gmail.com"));
        assertFalse(x.validateEmail("or 1=1--@gmail.com"));
        assertFalse(x.validateEmail("1'or'1'='1"));
        assertFalse(x.validateEmail("' or 'a'='a"));
        assertFalse(x.validateEmail("\" or \"a\"=\"a"));
        assertFalse(x.validateEmail("') or ('a'='a"));
        assertFalse(x.validateEmail("\") or (\"a\"=\"a"));
    }

    @Test
    public void emailsAreEqual() {
        RegistrationValidator x = new RegistrationValidator();

        assertTrue(x.emailsAreEqual("email@email.com", "email@email.com"));
        assertTrue(x.emailsAreEqual("EMAIL@EMAIL.COM", "EMAIL@EMAIL.COM"));
        assertTrue(x.emailsAreEqual("eMaIL@eMaIL.cOm", "eMaIL@eMaIL.cOm"));
        assertTrue(x.emailsAreEqual("eMaIL@eMaIL.co.uk", "eMaIL@eMaIL.co.uk"));
        assertTrue(x.emailsAreEqual("email_@EMAIL.COM", "email_@EMAIL.COM"));
        assertTrue(x.emailsAreEqual("email_email@email.co.uk", "email_email@email.co.uk"));
        assertTrue(x.emailsAreEqual("testing_email231@email.COM", "testing_email231@email.COM"));
        assertTrue(x.emailsAreEqual("email+100@email.com", "email+100@email.com"));
        assertTrue(x.emailsAreEqual("email+100@e-mail.com", "email+100@e-mail.com"));
        assertTrue(x.emailsAreEqual("email+100@e-mail.co.uk", "email+100@e-mail.co.uk"));

        assertFalse(x.emailsAreEqual("email@email.com", "email@emai.com"));
        assertFalse(x.emailsAreEqual("EMAIL@EMAIL.COM", "EMAIL@EMAIL."));
        assertFalse(x.emailsAreEqual("eMaIL@eMaIL.cOm", "eMaIL@eMaIL"));
        assertFalse(x.emailsAreEqual("eMaIL@eMaIL.co.uk", "eMaIL@eMaIL.co"));
        assertFalse(x.emailsAreEqual("email_@EMAIL.COM", "mail_@EMAIL.COM"));
        assertFalse(x.emailsAreEqual("email_email@email.co.uk", "mil_email@email.co.uk"));
        assertFalse(x.emailsAreEqual("testing_email231@email.COM", "tes  ting_email231@email.COM"));
        assertFalse(x.emailsAreEqual("email+100@email.com", "email+100@em ail.com"));
        assertFalse(x.emailsAreEqual("email+100@e-mail.com", "email+100@e-mailcom"));
        assertFalse(x.emailsAreEqual("email+100@e-mail.co.uk", ""));
    }
}
