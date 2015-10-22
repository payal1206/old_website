---
layout: post
title: Secret Library
date: 2015-10-22
ctf: Hack.lu 2015
author: xorpse
---

The core logic of the service can be found in the subroutine at address
`0x400D6D`. The call at `0x400D8E` reads user input; the function essentially
reads a 32-bit uppercase hexadecimal integer that must be zero padded to
a length of 8. This then drives the program functionality.

Within the subroutine responsible for processing individual hex digits a
curious side-effect happens on the input of `@`:

    ; Returns the previous value of sigmask
    mov     rax, SYS_rt_sigprocmask ; sys_rt_sigprocmask
    mov     rdi, SIG_BLOCK
    mov     rsi, 0
    mov     rdx, (offset var+3)
    mov     r10, 8          ; size
    syscall
    mov     eax, 0FFFFFFFFh
    jmp     short loc_400B61

It reads the current sigmask into the memory at (var + 3).

From reverse engineering the code, we see that the 'head librarian' is allowed
to know about all books. Thus, if we use the 'library card' number of the
'head librarian', we should be able to read the book (and get the flag).

Unfortunately, directly using the 'library card' number of the 'head
librarian' is impossible since it requires guessing the output of
'/dev/urandom' for a number of successive iterations (logic starting at
`0x400E0D`).

Thus, a different method is required. Note that entering any other value for
the 'library card' number stores its result into the same global variable,
`var` as previously mentioned. The upper two bytes of `var` need to be 0;
which can be achieved by inputting an invalid hex value containing `@`.

So in order to authenticate as the head librarian, we first enter a 'library
card' number of the form: `XX278F03` and then enter an invalid input
containing `@` (values and logic derived from `0x400DCC` onward).

Using this as initial input and following it with `952A7224`, we obtain a list
of books available.

The following input:

    420B65F7
    10278F03
    @@@@@@@@
    952A7224

...gives us:

    ...
    sure! the head librarian is allowed to know about all the books!
    ------------
    16F7F4D391F030CF
    ------------

To read the book, we see that it must be entered in two parts (as the name is
a 64-bit integer) the `F1140B88` command can be used to retrieve a book's
contents (from `0x400DB1`):

    F1140B88
    16F7F4D3
    91F030CF

...which gives:

    ...
    oh, yes, we have that! here you go...
    flag{our_secret_is_that_we_really_just_have_this_one_book}


Trace of solution:

    $ nc school.fluxfingers.net 1527
    < hi! this is the secret library. if you want me to speak to you, you need to know the magic words.
    > 420B65F7
    < alright, show me your library card.
    > 10278F03
    < alright!
    > @@@@@@@@
    < warning: invalid hexchar '@'
    < you do know the magic words, right?
    > 952A7224
    < sure! the head librarian is allowed to know about all the books!
    < ------------
    < 16F7F4D391F030CF
    < ------------
    > F1140B88
    < you want me to show you a book? certainly! just tell me the name of the book.
    > 16F7F4D3
    > 91F030CF
    < oh, yes, we have that! here you go...
    < flag{our_secret_is_that_we_really_just_have_this_one_book}
    <
    < ====================
