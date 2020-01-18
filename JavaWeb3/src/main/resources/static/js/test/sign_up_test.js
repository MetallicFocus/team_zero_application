function testValidateEmail() {
	var reg = /^[A-Za-z0-9_\-\.+]+\@[A-Za-z0-9_\-\.]+\.([A-Za-z]{2,6})$/; 
	var errorMsg1 = 'Unpassed valid Email address: ';
	var errorMsg2 = 'Passed invalid Email address: ';
	var validInputs = new Array('email@email.com',
								'EMAIL@EMAIL.COM',
								'eMaIL@eMaIL.cOm',
								'eMaIL@eMaIL.co.uk',
								'email_@EMAIL.COM',
								'email_email@email.co.uk',
								'testing_email231@email.COM',
								'email+100@email.com',
								'email+100@e-mail.com',
								'email+100@e-mail.co.uk');
	var invalidInputs = new Array('Abc.example.com',
								'"\'OR 1=1--"@gmail.com',
								'\'OR 1=1--@gmail.com',
								'\' or 1=1--@gmail.com',
								'" or 1=1--@gmail.com',
								'or 1=1--@gmail.com',
								'1\'or\'1\'=\'1',
								'\' or \'a\'=\'a',
								'" or "a"="a',
								'\') or (\'a\'=\'a',
								'") or ("a"="a');
	for (i in validInputs) {
		console.assert(reg.exec(validInputs[i]), errorMsg1+validInputs[i]);
	}
	for (i in invalidInputs) {
		console.assert(!reg.exec(invalidInputs[i]), errorMsg2+invalidInputs[i]);
	}
	console.log('Email address validation test finished!');
};

function testValidateUsername() {
	var reg1 = /[^a-zA-Z0-9_\-.]/; //Username can only contain number, character and ., -, _
	var reg2 = /^[0-9]/; //Username cannot start with numbers.
	var reg3 = /.{3,30}/; //The number of characters must be in between 3 and 30.
	var errorMsg1 = 'Unpassed valid Username: ';
	var errorMsg2 = 'Passed invalid Username: ';
	var validInputs = new Array('UsernameX96',
								'Username_X96',
								'Username',
								'username',
								'UsErNAme_X_96',
								'UsErNAme_X_96Test',
								'UsEr.NAme_X_96Test',
								'UsEr-NAme_X_96Test');
	var invalidInputs = new Array('admin\'',
								'admin\' OR \'a\'=\'a',
								'invalid-username\' UNION SELECT 1, username, passwords FROM members WHERE \'x\'=\'x',
								'\' or 1=1--',
								'" or 1=1--',
								'"or 1=1--',
								'1\'or\'1\'=\'1',
								'\' or \'a\'=\'a',
								'" or "a"="a',
								'\') or (\'a\'=\'a',
								'") or ("a"="a');
	for (i in validInputs) {
		console.assert(!reg1.exec(validInputs[i]) && !reg2.exec(validInputs[i]) && reg3.exec(validInputs[i]), errorMsg1+validInputs[i]);
	}
	for (i in invalidInputs) {
		console.assert(reg1.exec(invalidInputs[i]) || reg2.exec(invalidInputs[i]) || !reg3.exec(invalidInputs[i]), errorMsg2+invalidInputs[i]);
	}
	console.log('Username validation test finished!');
};

function testValidatePwd() {
	var reg1 = /(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#!?$%^&+])/;
				// Password contains at least 1 uppercase character
				// Password contains at least 1 lowercase character
				// Password contains at least 1 digit
				// Password contains at least 1 special character
	var reg2 = /\s/; // Password must not contain white spaces
	var reg3 = /.{6,30}/; // Password is between 6 and 30 characters
	var errorMsg1 = 'Unpassed valid Password: ';
	var errorMsg2 = 'Passed invalid Password: ';
	var validInputs = new Array('ExamplePass23!',
								'ExAmPLePASS23!',
								'examplePass23!',
								'EXAMPLEpASS23!',
								'example_paSs23!',
								'ex#ample_Pass23!',
								'23!ex##lE_pa$$',
								'123456789!aA',
								'aA!123456789');
	var invalidInputs = new Array('\' or 1=1--',
								'" or 1=1--',
								'"or 1=1--',
								'1\'or\'1\'=\'1',
								'\' or \'a\'=\'a',
								'" or "a"="a',
								'\') or (\'a\'=\'a',
								'") or ("a"="a');
	for (i in validInputs) {
		console.assert(reg1.exec(validInputs[i]) && !reg2.exec(validInputs[i]) && reg3.exec(validInputs[i]), errorMsg1+validInputs[i]);
	}
	for (i in invalidInputs) {
		console.assert(!reg1.exec(invalidInputs[i]) || reg2.exec(invalidInputs[i]) || !reg3.exec(invalidInputs[i]), errorMsg2+invalidInputs[i]);
	}
	console.log('Password validation test finished!');
};

function testValidateAll() {
	testValidateEmail();
	testValidateUsername();
	testValidatePwd();
};