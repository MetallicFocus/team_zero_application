# web client
print("Web test here...")

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.keys import Keys
import platform
import time
import random

if ("Windows" not in platform.platform()):
    exit()
driver = webdriver.Chrome(executable_path='./lib/chromedriver')

# local_username = ""
# options = webdriver.ChromeOptions()
# options.add_argument("user-data-dir=C:/Users/"+local_username+"/AppData/Local/Google/Chrome/User Data") #Path to your chrome profile
# driver = webdriver.Chrome(executable_path='./lib/chromedriver', chrome_options=options)

test_user_name = []
test_user_pwd = []

test_invalid_email = ['Abc.example.com',
                      '"\'OR 1=1--"@gmail.com',
                      '\'OR 1=1--@gmail.com',
                      '\' or 1=1--@gmail.com',
                      '" or 1=1--@gmail.com',
                      'or 1=1--@gmail.com',
                      '1\'or\'1\'=\'1',
                      '\' or \'a\'=\'a',
                      '" or "a"="a',
                      '\') or (\'a\'=\'a',
                      '") or ("a"="a']
test_invalid_username = ['admin\'',
                         'admin\' OR \'a\'=\'a',
                         'invalid-username\' UNION SELECT 1, username, passwords FROM members WHERE \'x\'=\'x',
                         '\' or 1=1--',
                         '" or 1=1--',
                         '"or 1=1--',
                         '1\'or\'1\'=\'1',
                         '\' or \'a\'=\'a',
                         '" or "a"="a',
                         '\') or (\'a\'=\'a',
                         '") or ("a"="a']
test_invalid_pwd = ['\' or 1=1--',
                    '" or 1=1--',
                    '"or 1=1--',
                    '1\'or\'1\'=\'1',
                    '\' or \'a\'=\'a',
                    '" or "a"="a',
                    '\') or (\'a\'=\'a',
                    '") or ("a"="a']

test_search_words = ['test', 'mar', 'a', '3']


def refresh(path):
    driver.get(path)


## registration
def register(email, username, pwd):
    print("Trying registering...")
    refresh("http://localhost:8080/Signup")
    path_prefix = "//label[@for = \'"
    path_suffix = "\']/following-sibling::div[1]/div/input"
    el_email = driver.find_element_by_xpath(path_prefix + 'email' + path_suffix)
    el_email.send_keys(email)
    el_username = driver.find_element_by_xpath(path_prefix + 'name' + path_suffix)
    el_username.send_keys(username)
    el_pwd = driver.find_element_by_xpath(path_prefix + 'pwd' + path_suffix)
    el_pwd.send_keys(pwd)
    driver.find_element_by_xpath("//button[@class ='el-button el-button--primary']").click()
    WebDriverWait(driver, 3).until(EC.alert_is_present())
    text = driver.switch_to.alert.text
    time.sleep(2)
    if text == "Register successfully!":
        driver.switch_to.alert.accept()
        test_user_name.append(username)
        test_user_pwd.append(pwd)
    elif text == "Please follow rules!":
        driver.switch_to.alert.accept()
        refresh("http://localhost:8080/Signup")


## login
def login(login_name, login_pwd):
    print("Trying logging in...")
    refresh("http://localhost:8080")
    username = driver.find_element_by_name("name")
    username.send_keys(login_name)
    pwd = driver.find_element_by_name("pwd")
    pwd.send_keys(login_pwd)
    path = "//div/form/div/div/div/div/button"
    signin_button = driver.find_elements_by_xpath(path)[0]
    signin_button.click()
    try:
        # Fail with alert
        WebDriverWait(driver, 3).until(EC.alert_is_present())
        time.sleep(1)
        driver.switch_to.alert.accept()
        refresh("http://localhost:8080/Signin")
    except:
        # Succeed
        time.sleep(1)


## Search for other users
def search_for_users(key_word):
    print("try searching users with keyword(\""+key_word+"\")...")
    path = "//i[@aria-haspopup]"
    dropdown = WebDriverWait(driver, 3).until(EC.element_to_be_clickable((By.XPATH, path)))
    dropdown.click()
    path = "//ul[@class = 'el-dropdown-menu el-popper']/li[2]"
    item = WebDriverWait(driver, 3).until(EC.element_to_be_clickable((By.XPATH, path)))
    item.click()
    path = "//div[@class = 'searchPanel']/div/div[1]/div/input"
    input = driver.find_elements_by_xpath(path)[0]
    input.send_keys(Keys.CONTROL+'a')
    input.send_keys(Keys.DELETE)
    input.send_keys(key_word)
    path = "//div[@class = 'searchPanel']/div/div[2]/button"
    button = driver.find_elements_by_xpath(path)[0]
    button.click()
    time.sleep(1)


## Create new chats and fetch latest histories
def create_random_new_chat():
    print('try creating new chat...')
    path = "//div[@class = 'single-user-info']"
    results = driver.find_elements_by_xpath(path)
    random_num = str(random.randint(1, len(results)))
    path = "//div[@class = 'single-user-info'][" + random_num + "]"
    single_user_info = WebDriverWait(driver, 3).until(EC.element_to_be_clickable((By.XPATH, path)))
    single_user_info.click()


## Message encryption and decryption
def send_message_to(object_user, message):
    print("try sending message (\""+message+"\") to user (\""+object_user+"\")...")
    path = "//div[@class='chat']/div/div[2]/div/div/label"
    user_names = driver.find_elements_by_xpath(path)
    for user_name in user_names:
        if object_user != user_name.text:
            continue
        user_name.find_element_by_xpath("../../../..").click()
        path = "//div[@id='main-panel']/section//div/section/header/div/div[2]/label"
        panels = driver.find_elements_by_xpath(path)
        for panel in panels:
            if panel.text != object_user:
                continue
            text_field = panel.find_element_by_xpath('../../../../footer/div/div[2]/div/input')
            text_field.send_keys(message)
            send_button = WebDriverWait(text_field, 3).until(EC.element_to_be_clickable((By.XPATH, '../../../div[3]/span//button')))
            send_button.click()
            time.sleep(0.5)



def search_and_create_chats(key_word):
    search_for_users(key_word)
    create_random_new_chat()


def test_register():
    print("Register test case 1: succeed with complying registration constrains")
    for i in range(0, 10):
        random_num = str(random.randint(10000, 99999))
        random_username = "test" + random_num
        constructed_pwd = "Aa1111!!"
        register("test" + random_num + "@test.com", random_username, constructed_pwd)

    print("Register test case 1: fail with stained inputs")
    for i in range(len(test_invalid_email) + len(test_invalid_username) + len(test_invalid_pwd)):
        random_num = str(random.randint(10000, 99999))
        random_email = "test" + random_num + "@test.com"
        random_username = "test" + random_num
        constructed_pwd = "Aa1111!!"
        if i < len(test_invalid_email):
            continue
            # Stained email address
            random_email = test_invalid_email[i]
        elif i < len(test_invalid_email) + len(test_invalid_username):
            # Stained username
            continue
            random_username = test_invalid_username[i - len(test_invalid_email)]
        else:
            # Stained password
            constructed_pwd = test_invalid_pwd[i - len(test_invalid_email) - len(test_invalid_username)]
        register(random_email, random_username, constructed_pwd)


def test_login():
    if len(test_user_pwd) == 0:
        random_num = str(random.randint(10000, 99999))
        random_username = "test" + random_num
        constructed_pwd = "Aa1111!!"
        register("test" + random_num + "@test.com", random_username, constructed_pwd)

    print("Login test case 1: succeed with local private key")
    for i in range(len(test_user_name)):
        username = test_user_name[i]
        pwd = test_user_pwd[i]
        login(username, pwd)

    print("Login test case 2: fail without local private key")
    for i in range(0, 10):
        random_num = str(random.randint(10000, 99999))
        random_username = "test" + random_num
        constructed_pwd = "Aa1111!!"
        login(random_username, constructed_pwd)

    print("Login test case 3: fail with unmatched passwords")
    for i in range(len(test_user_name)):
        username = test_user_name[i]
        pwd = "dfghjk"  # randomly constructed passwords
        login(username, pwd)


def test_search_users():
    if len(test_user_name) < 1:
        random_num = str(random.randint(10000, 99999))
        random_username = "test" + random_num
        constructed_pwd = "Aa1111!!"
        register("test" + random_num + "@test.com", random_username, constructed_pwd)

    login(test_user_name[0], test_user_pwd[0])
    for i in range(len(test_search_words)):
        search_and_create_chats(test_search_words[i])


def test_chat():
    if len(test_user_name) < 2:
        for i in range(2 - len(test_user_name)):
            random_num = str(random.randint(10000, 99999))
            random_username = "test" + random_num
            constructed_pwd = "Aa1111!!"
            register("test" + random_num + "@test.com", random_username, constructed_pwd)

    login(test_user_name[0], test_user_pwd[0])
    search_and_create_chats(test_user_name[1])
    for i in range(10):
        send_message_to(test_user_name[1], 'test_message' + str(i))

    login(test_user_name[1], test_user_pwd[1])
    send_message_to(test_user_name[0], 'test_message back')

# test_register()
test_login()
# test_search_users()
# test_chat()
