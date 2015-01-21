---
layout: post
title: "Wanted: Translator"
date: 2014-10-23
ctf: Hack.lu 2014
author: Break
---

## Description

We are in desperate need of a translator who understands the languages of varoius Indian tribes. We already know how to speak to the Apache tribe via HTTP but we have some stuff missing. We offer 5$ per successfully translated language.

(That means you can send us 7 solutions to this challenge and will receive 5 points for each.)

	SMTP
    GOPHER
    POP3
    FINGER
    TFTP
    IRC
    NNTP

## Solution

We realised that there was a tftp server running since it was possible to connect with the
following command.

	tftp wildwildweb.fluxfingers.net

Afterwards we used the default ports and programs to connect to the other protocols:


### SMTP

*   connect to port 25 with telnet
*   send mail to `fluxfingers@rub.de`

### 3.2.2. GOPHER

*   connection: `telnet wildwildweb.fluxfingers.net gopher`
*   send `\r\n`
*   reconnect
*   send `0\r\n`

### 3.2.3. POP3

*   `USER flux`
*   `PASS flux`
*   `LIST`
*   `RETR 1`

### 3.2.4. FINGER

*   connected to port 79 using telnet
*   send `\r\n` to get user list
*   reconnect
*   send `r00t\r\n` to get details for the user r00t

### 3.2.5. TFTP

The port was open but TFTP does not support a list command, so we wrote a script.
Guessing the name of the file is another solution.

{% highlight perl %}
perl -MNet::TFTP -e '$tftp = Net::TFTP->new("wildwildweb.fluxfingers.net"); $tftp->get("flag", \*STDOUT);'
{% endhighlight %}

connection to tftp:
{% highlight perl %}
tftp wildwildweb.fluxfingers.net
tftp> get flag
{% endhighlight %}

[Here](http://afnom.net/assets/2014/tftp.txt) is the file, including a bonus text adventure.

### 3.2.6. IRC

*   connected to port 6667
*   listed the channels with /list
*   joined channel #flagchannel
*   send a private message to flagbot.

### 3.2.7. NNTP

*   connection: `telnet wildwildweb.fluxfingers.net nntp`
*   `GROUP apache.chitchat`
*   `NEXT`
*   `ARTICLE`

5 points per challenge makes 35 points in total.

(joint work Chris, Joe, Mike, Sam)