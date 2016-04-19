
A SHA-256 password cracker
The sample passwords were provided from a list of popular passwords.

About the txt files:

1. raw.txt - users with plain text passwords
2. password.txt - users with hashed passwords
3. phase0_dict.txt - used for the simple dictionary attack
4. phase1_dict.txt - used for various cracking strategies 

raw.txt - users with plain text passwords
password.txt - users with hashed passwords
phase0_dict.txt - used for the simple dictionary attack
phase1_dict.txt - used for various cracking strategies

To view cracked passwords, check output.txt

There are 2 main dictionaries that are used by the password cracker. The First dictionary contains common passwords, brands, actors, jobs, movies, etc. The Second dictionary is shorter in length and contains a list with the most frequently used words in English as well as brands , movies, superheroes. These dictionaries have been created by adding the most common passwords, names, movies, actors, brands and many more words describing jobs, attitudes, the environment and actions.

The first dictionary is used for the simple dictionary attack, without modifying the words, while the second one is used for various cracking strategies. These strategies are:

1. Derivation – most characters are replaced with symbols. For example password, becomes p@5sword or passw0rd and so on.

2. Concatenation of 2 words from the dictionary – 2 words are extracted from the smaller dictionary and these are concatenated. Moreover, the initials of the words are set to uppercase, then all characters are set to uppercase, hashed and then compared against the hashed value of a password. In addition, a number from 0-100 is added both at the beginning and end of the strings resulted from the modifications mentioned above.

3. Concatenation of 1 word with a number – each word is set to lowercase, uppercase, and initial to uppercase. A number is then added at the beginning and end of the word.

4. Number generation – Numbers between 0 and 10000000 are generated, hashed and compared to the hashes of the passwords.

