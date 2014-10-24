
# Challenge #19: Barmixing-Bot

## Description

Author: freddy
Category: Misc

There's a fun and quirky IRC bot to play with. It responds to commands in
private chat but also in #hacklu-saloon on freenode. We think it's involved in
a devious scheme that distracts people to get their money pickpocketed. So be
careful!

## Solution

Author: Sam Thomas / xorpse

By starting a query with the bot and issuing a `!help` command, we get some
information on how the bot functions:

```
-!- Irssi: Starting query in FreeNode with barmixing-bot
<xorpse> !help
<barmixing-bot> Send messages to the bot or the channel starting with an
exclamation mark. Known commands are list, status, karma, math, base64,
base64d, rot13, ping, hack, request, list
```

Sending `!status` gives us a list of channels in which the bot resides; this
includes a channel called `#hacklu-secret-channel`:

```
My name is barmixing-bot, my uptime is 12 hours 35 minutes and 42 seconds. I
am on the following channels: #hacklu-saloon, #hacklu-secret-channel, ...
```

The intuition is that the flag might be accessible by joining
`#hacklu-secret-channel` (since it's not possible to join it without an invite).

After testing some of the commands, we see that `!base64d` will cause the bot to
echo whatever it decodes verbatim. The IRC command for inviting someone to a
channel is:

```
invite <name> <channel>
```

Using the bot we encode the command (note: this will be echoed as a PRIVMSG by
the bot rather than an INVITE command):

```
<xorpse> !base64 invite xorpse #hacklu-secret-channel
<barmixing-bot> aW52aXRlIHhvcnBzZSAjaGFja2x1LXNlY3JldC1jaGFubmVs
```

And then have it decode the command:

```
<xorpse> !base64d aW52aXRlIHhvcnBzZSAjaGFja2x1LXNlY3JldC1jaGFubmVs
-!- barmixing-bot invites you to #hacklu-secret-channel
<barmixing-bot> invite xorpse #hacklu-secret-channel
```

It seems the bot must scan what it decodes for an invite command and then act
accordingly.

The topic in `#hacklu-secret-channel` is the flag:

```
FLAG: GfeBNmN5XjwDvQB64qoqaEEeYogk4rGH3ikZ0qtc3B3HKLDoAH
```
