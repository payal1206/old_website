---
layout: post
title: "Gunslinger Joe's Gold Stash"
date: 2014-10-24
ctf: Hack.lu 2014
author: xorpse
---

## Description

Author: cutz

Category: Reversing

Silly Gunslinger Joe has learned from his mistakes with his private terminal
and now tries to remember passwords. But he's gotten more paranoid and chose
to develope an additional method: protect all his private stuff with a secure
locking mechanism that no one would be able to figure out! He's so confident
with this new method that he even started using it to protect all his precious
gold. So... we better steal all of it!

SSH: joes_gold@wildwildweb.fluxfingers.net

PORT: 1415

PASSWORD: 1gs67uendsx71xmma8

## Solution

Author: Sam Thomas / xorpse

After logging in, we see that there are two files in the home directory:

{% highlight bash %}
joes_gold@goldstash:~$ ls -l
total 20
-r-------- 1 gold gold    46 Oct  6 23:04 FLAG
-rwsr-sr-x 1 gold gold 13186 Oct  6 23:03 gold_stash
{% endhighlight %}

Notice the setuid and setgid bits on `gold_stash`: when executed, `gold_stash`
will run as if started by the user `gold`. Note also that `FLAG` is only
readable by the user `gold`.

After transferring `gold_stash` to a local directory and executing it, we are
presented with a basic login prompt:

{% highlight console %}   
.
          (_/-------------_______________________)
          `|  /~~~~~~~~~~\                       |
           ;  |--------(-||______________________|
           ;  |--------(-| ____________|
           ;  \__________/'
         _/__         ___;
      ,~~    |  __--~~       Gunslinger Joe's
     '        ~~| (  |       Private Stash of Gold
    '      '~~  `____'
   '      '
  '      `            Password Protection activated!
 '       `
'--------`
Username:
{% endhighlight %}

Disassembling the main function, gives us the credential input routine:

{% highlight console %}
.text:000000000040093E                 mov     edi, offset format ; "Username: "
.text:0000000000400943                 mov     eax, 0
.text:0000000000400948                 call    _printf
.text:000000000040094D                 mov     rax, cs:__bss_start
.text:0000000000400954                 mov     rdi, rax        ; stream
.text:0000000000400957                 call    _fflush
.text:000000000040095C                 lea     rax, [rbp+s]
.text:0000000000400963                 mov     edx, 0FFh       ; nbytes
.text:0000000000400968                 mov     rsi, rax        ; buf
.text:000000000040096B                 mov     edi, 0          ; fd
.text:0000000000400970                 call    _read
.text:0000000000400975                 mov     edi, offset aPassword ; "Password: "
.text:000000000040097A                 mov     eax, 0
.text:000000000040097F                 call    _printf
.text:0000000000400984                 mov     rax, cs:__bss_start
.text:000000000040098B                 mov     rdi, rax        ; stream
.text:000000000040098E                 call    _fflush
.text:0000000000400993                 lea     rax, [rbp+buf]
.text:000000000040099A                 mov     edx, 0FFh       ; nbytes
.text:000000000040099F                 mov     rsi, rax        ; buf
.text:00000000004009A2                 mov     edi, 0          ; fd
.text:00000000004009A7                 call    _read
{% endhighlight %}

Followed by an uninteresting section of code for removing trailing newlines
(due to the use of read).

And finally, the authentication check using hardcoded credentials:

{% highlight console %}
.text:0000000000400A0C                 lea     rax, [rbp+s]
.text:0000000000400A13                 mov     esi, offset s2  ; "Joe"
.text:0000000000400A18                 mov     rdi, rax        ; s1
.text:0000000000400A1B                 call    _strcmp
.text:0000000000400A20                 test    eax, eax
.text:0000000000400A22                 jnz     short loc_400A9A ; To authentication failed
.text:0000000000400A24                 lea     rax, [rbp+buf]
.text:0000000000400A2B                 mov     esi, offset aOmg_joe_is_so_ ; "omg_joe_is_so_rich"
.text:0000000000400A30                 mov     rdi, rax        ; s1
.text:0000000000400A33                 call    _strcmp
.text:0000000000400A38                 test    eax, eax
.text:0000000000400A3A                 jnz     short loc_400A9A ; To authentication failed
.text:0000000000400A3C                 mov     edi, offset aAccessGranted ; "Access granted!"
{% endhighlight %}

So with the user/password combination (Joe/omg_joe_is_so_rich), we try to gain
access locally:

{% highlight console %}
Username: Joe
Password: omg_joe_is_so_rich
Access granted!
sh-4.3$
{% endhighlight %}

Success! However, applying the same method on the challenge server yields
unexpected results:

{% highlight console %}
Username: Joe
Password: omg_joe_is_so_rich
Authentication failed!
{% endhighlight %}

What could be wrong? If we debug with gdb on the challenge server we get the
expected result. However, gdb executes `gold_stash` as the user `joes_gold`
rather than `gold`. Ergo, one of `strcmp` or `read` must be behaving
differently when executed as the user `gold`.

A look into the `libc.so.6` binary on the system reveals nothing untoward;
another possibilty is that a system call has been hijacked, and an `ls` of the
kernel modules directory seems to point in that direction (notice the `joe`
directory):

{% highlight bash %}
joes_gold@goldstash:~$ ls -l /lib/modules/3.13.0-36-generic/kernel/
total 40
drwxr-xr-x  3 root root 4096 Oct  6 21:28 arch
drwxr-xr-x  3 root root 4096 Oct  6 21:28 crypto
drwxr-xr-x 77 root root 4096 Oct  6 21:28 drivers
drwxr-xr-x 55 root root 4096 Oct  6 21:28 fs
drwxr-xr-x  2 root root 4096 Oct  6 23:08 joe
drwxr-xr-x  6 root root 4096 Oct  6 21:28 lib
drwxr-xr-x  2 root root 4096 Oct  6 21:29 mm
drwxr-xr-x 51 root root 4096 Oct  6 21:28 net
drwxr-xr-x 13 root root 4096 Oct  6 21:28 sound
drwxr-xr-x  4 root root 4096 Oct  6 21:28 ubuntu
{% endhighlight %}

...which contains a module `joe.ko`. Such module should contain an `init`
function. In this case, `joe_init`. In order to hijack a system call, the
system call table must be located:

{% highlight console %}
.text:00000000000001E6                 mov     rax, 0FFFFFFFF81000000h
.text:00000000000001ED                 mov     rbp, rsp
.text:00000000000001F0                 jmp     short loc_204
.text:00000000000001F0 ; ---------------------------------------------------------------------------
.text:00000000000001F2                 align 8
.text:00000000000001F8
.text:00000000000001F8 loc_1F8:                                ; CODE XREF: joe_init+2Cj
.text:00000000000001F8                 add     rax, 8
.text:00000000000001FC                 cmp     rax, 0FFFFFFFFA2000000h
.text:0000000000000202                 jz      short loc_268
.text:0000000000000204
.text:0000000000000204 loc_204:                                ; CODE XREF: joe_init+10j
.text:0000000000000204                 cmp     qword ptr [rax+18h], offset sys_close
.text:000000000000020C                 jnz     short loc_1F8
.text:000000000000020E                 test    rax, rax
.text:0000000000000211                 mov     cs:sct, rax
{% endhighlight %}

Once found, it's stored within `sct`. `read` is the first system call, (i.e. at
offset 0 within `sct`) and is replaced with a function called `joe`:

{% highlight console %}
.text:0000000000000231                 mov     rdx, cs:sct
.text:0000000000000238                 mov     rax, offset joe
.text:000000000000023F                 xchg    rax, [rdx]
.text:0000000000000242                 mov     cs:o_read, rax
{% endhighlight %}

The address of the original read function is saved to `o_read`.

The function `joe` (from a high-level) works as:

- Read buffer using `o_read`;
- Copy buffer from user to kernel using `copy_from_user`;
- If uid == 1001: encode first 18 characters using:

{% highlight c %}
char *pad = "123456789012445678";
char *input = ...

for (int i = 0; i < 18; i++)
  input[i] = pad[i] ^ (input[i] - 4);
{% endhighlight %}

- If the original string compared equal to 'omg_joe_is_so_rich' or if
  the original string had the prefix 'omg_joe_is_so_rich' then the encoded
  value replaces it (via copy_to_user).

To get the desired string we need to encode 'omg_joe_is_so_rich':

{% highlight c %}
char *pad = "123456789012445678";
char *input = "omg_joe_is_so_rich";

for (int i = 0; i < 18; i++) {
  input[i] = (input[i] ^ pad[i]) + 4;
}
{% endhighlight %}

Which gives: bcXoc]VkTGrE_oKcXT as the encoded password.

The FLAG file can then be read:
{% highlight console %}
Username: Joe
Password: bcXoc]VkTGrE_oKcXT
Access granted!
$ cat FLAG
flag{joe_thought_youd_never_find_that_module}
{% endhighlight %}
