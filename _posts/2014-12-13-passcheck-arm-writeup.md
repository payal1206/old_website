---
layout: post
title: "Advanced RISC Machine"
date: 2014-12-13
ctf: SECCON Quals 2014
author: Corax
---


##Introduction

In this exploit challenge, we have to get a flag from an ARM executable exposed on a port of a distance machine.

If we connect to the distant port, we are asked a password. Disassembling the file in IDA (or even just running `strings`) shows very easily that the password is 'holiday', but inputting the password only gets us 'OK. Read flag.txt'.

So, we have to read the file 'flag.txt' using this executable... Disassembling it is fairly easy (except that no library function is used, so we have to identify the generic functions) and the code is quite short, but there is absolutely nothing that reads from a file! In this write-up, I will use the names that I gave to the functions (based on libc's functions, f\* functions take a file descriptor argument instead of a `FILE*` though) with their address in parentheses. To follow the explanations more easily, you can ask me for the IDA database with the modified names and comments (my email address is at the end). 

Fortunately, there is a rather clear flaw in the function `fgetline()` (@0x41E8) that reads a line from a file descriptor: the size of the output buffer is not passed to the function, and there is indeed no limit. Since the output buffer is allocated on the stack of `check_pwd()` (@0x427C), we have a nice stack overflow in sight :D If you're in doubt, feed the program with a 27+ characters password, you won't have any answer because it will the saved PC and make it crash! Note, this is important, that '\0' can be included in the line (`fgetline()` will continue reading the input), however both '\n' or ^D (0xA and 0xD) end the line and thus must not be used for the exploit (fortunately, I didn't have to use either of them).

This was the easy part. I will try to develop in details what I tried, mainly to justify the final solution that looks honestly very ugly. This stuff is rather complex so the walk-through is rather long in order to make it reasonably easy to understand, for the impatient people out there...

##TL;DR

Use this message to get the flag (using Python syntax to build the string):

    message = '\x30\xf0\xff\x1f' + '\x00\x00\x00\x00' + '\x00\x00\x00\x00' + 'xxxx' * 2 + '\x05\x00\x00\x00' + 'xxxx' + '\x4c\x42\x00\x00' + 'xxxx' * 3 + '\x28\x40\x00\x00' + '\x60\x42\x00\x00' + '\xb0\x42\x00\x00' + '\x30\xf0\xff\x1f' + 'xxxx' * 6 + '\x70\x42\x00\x00' + 'flag.txt\x00' + '\n'

##The (too) obvious way

The idea is now to execute arbitrary code from the stack and jump to it. This is theoretically quite easy, because we have the start address of the stack (0x1FFFFF000, see @0x4000). As for what to execute, we don't have any library function, so we're reduced to using system calls (note: on this particular platform, system calls are done with `SVC 0xFF` instead of the normal `SVC 0x0` for ARM, no idea why); the easiest solution would be `execve("/bin/cat", { "cat", "test.txt", NULL }, { NULL });`. Unfortunately, I didn't manage to execute anything on the stack, or at least the server hasn't answered anything to my attempts :( This is where the challenge gets tricky...

##Calling a function


Our only option is now to execute existing code and make it do what we want by carefully modifying the registers. This looks hard, and it actually is... One good point is that there seems to be no dynamic loader at all: all the addresses in the binary are absolute addresses, e.g. we just have to jump at 0x427C to execute `check_pwd()` (I discovered this because the data @0x1FFF0000 is a raw pointer to 0x432C without any relocation or things like that).

Okay, we now have to understand exactly how the stack overflow works to do our exploit. `check_pwd()` is really completely broken: the beginning of the buffer passed to `getline()` matches the beginning of the registers saved on the stack, so anything written to the buffer will overwrite the saved registers! On ARM, the registers saved through `STMFD/PUSH` (here @0x4280) are saved in ascending register numbers, so this is how the registers are arranged on the stack, relatively to the start of the buffer:

    Buffer offset | 0  | 4  | 8  | 12 | 16 | 20 | 24  | 28
    Contents      | R1 | R2 | R3 | R4 | R5 | R6 | R11 | LR/PC

Right, knowing that we can call any function with anything in these registers. What follows are the main steps to build the exploit, from the "hello world" to the working exploit. I used Python for sending the messages (pro tip: bash discards '\x00' when manipulating it, wish I had realised this before...), so I will use the `n * "msg"` syntax to repeat strings. 'xxxx' strings are just padding, the contents don't matter.

##Make the server print a sent character

This is just to demonstrate the essential technique. This message will make the server say 'H':

    #         R1-R6,R11    New PC: getchar()    putchar()                   input for getchar()
    message = 'xxxx' * 7 + '\xac\x41\x00\x00' + '\xb8\x41\x00\x00' + '\n' + 'H'

Here we put 0x41AC (little endian my friends!) in the saved PC, so that when `check_pwd()` returns, the program will jump there. At 0x41AC we have the second instruction of a piece of code which behaves exactly like libc's `getchar()` (this is dead code that belongs to no function, but here it's handy). This is critical: the second instruction, i.e. we skip the instruction saving the registers, here only LR (the return address of the function), to the stack.

That way, `getchar()` will put the next character from stdin (here 'H') in R0, and then jump to the saved PC, which is really just the next word on the stack; since we skipped the saving of LR, it will actually jump to the address we provided: 0x41B8, i.e. `putchar()`. Yay, we've just read a character from stdin and printed it on stdout :)

##execve() "/bin/cat"?

Back to the challenge, let's try to get this flag. The easiest solution is still to execute `cat` on flag.txt. To do that, we need to make a system call: `sys_execve`. On ARM, the calling convention for system calls is easy: R0 is the syscall number and R1..6 are the arguments. But there is a problem: we can't set R0! Fortunately, we can work around that; @0x424C, we have a nice "register loader": `MOV R0, R6` then pop R4-R6 and PC. From now on, we will first set PC to 0x424C, put in R6 the intended value for R0, and then put on the stack some values for R4-R6 (actually we don't care, so just placeholders):

    message = <R1> <R2> <R3> <R4/R5: overwritten> <R6 = R0> <R11> 0x424C <R4> <R5> <R6> <next address to jump to>

We can now make our system call by jumping to `syscall()` @0x4014. The syscall number for execve is 0xB like on x86 (see [here](http://lxr.free-electrons.com/source/arch/arm/kernel/calls.S)). Let's try with an easy case: `ls` with no argument, i.e. `execve("/bin/ls", NULL, NULL);`. We have to put the string "/bin/ls" somewhere; we will put it at the end of the message, and it will end above the top of the stack @0x1FFFF08 (the calculation are a bit boring, you can deduce it knowing the top of the stack and thus the current SP in `check_pwd()`). We now have all we need, here is the message:

    #         R1                   R2                   R3                   Old R4-R5    R6 => R0             R11
    #         (arg 1 = &"/bin/ls") (arg 2 = NULL)       (arg 3 = NULL)                    (syscall number)
    message = '\x08\xf0\xff\x1f' + '\x00\x00\x00\x00' + '\x00\x00\x00\x00' + 'xxxx' * 2 + '\x0b\x00\x00\x00' + 'xxxx'
    #          "Regs loader"        New R4-R6    New PC: syscall()    @0x1FFFF08
    message += '\x4c\x42\x00\x00' + 'xxxx' * 3 + '\x14\x40\x00\x00' + '/bin/ls\x00' + '\n'

You think this should be complicated enough? If only... When you send this, the server will give you this error: "sim: unknown SWI encountered - b - ignoring". In other words: "hi, we disabled execve". And, after some more checking, same for `sys_fork()`. Damn...

##Doing it the even harder way: open()

At this point, I've depleted all the "quick" options I could think about. There is only one option remaining: `open()` the file, read from it to somewhere and write the result to stdout...

To begin with, we use the same trick as before to set R0. Then we still use `syscall()` (with R0 = 0x5 for `sys_open()`), but this time we need to return from the syscall to another function! For this reason, we will not directly jump to 0x4014 (because the RET pseudo-instruction restores PC from LR, not the stack), but to 0x4028, where `syscall()` is properly called with BL (setting LR) and PC is popped from the stack just after. As for the arguments, R1 will be the address of the name of the file (put at the end of the message like before), R2 is the flags (`O_RDONLY`, i.e. 0) and R3 the mode (0, unused anyway).

After returning from the syscall, R0 will be set to the file descriptor. We can now get a line from it by jumping to `fgetline()` (@0x41EC); actually it's easier to jump to `getline()` where `fgetline()` is called (@0x4260) to avoid adding padding for the saved registers of `fgetline()` (`getline()` only restores PC). The arguments of `fgetline()` are the file descriptor in R0 and the buffer in R1 (and no smaximum size, this is where the exploit comes from!). We're very lucky here: R0 is already the file descriptor, and R1 has not been modified, it still points to the name of the file that we will happily overwrite with the read data (this stack really gets messed up) :)

We're almost there! The data has now been read, all that remains is to print it. For that purpose we can use `puts()` @0x4268. Here is the final problem: how to set R0 to point to the address of the buffer? Well... I don't think we can, however we can put something in R1 instead and jump to 0x425C (skip `MOV R1, R0`)... Now, how to set R1? Go back to square one! In `check_pwd()`, the last instruction @0x42B0 pops R1-R6/R11/PC, so we can just jump to it as a new register loader! That way, we put the address of the buffer in R1, the address of `puts()` in PC, and *at last* we're done. Here is the full message:

    #         R1                   R2                   R3                   R4-R5        R6 => R0             R11
    #         (arg 1 = &"flag.txt") (arg 2 = O_RDONLY)  (arg 3 = 0)                       (syscall number)
    message = '\x30\xf0\xff\x1f' + '\x00\x00\x00\x00' + '\x00\x00\x00\x00' + 'xxxx' * 2 + '\x05\x00\x00\x00' + 'xxxx'
    #          "Regs loader"        R4-R6        syscall()            fgetline()           "Regs loader #2"
    #                                            (w/ ret from stack)
    message += '\x4c\x42\x00\x00' + 'xxxx' * 3 + '\x28\x40\x00\x00' + '\x60\x42\x00\x00' + '\xb0\x42\x00\x00'
    #          R1                   R2-6, R11    puts()               @0x1FFFF030
    #          (arg 1 = &"flag.txt")
    message += '\x30\xf0\xff\x1f' + 'xxxx' * 6 + '\x70\x42\x00\x00' + 'flag.txt\x00' + '\n'

And with this message, the miracle finally happens: the server replies with the contents of the file, i.e. the token 'SECCON{TeaBreakAtWork}'.


**Bottom line**: if you control the stack but can't directly call functions, call them in reverse by hacking the return address... Still, note that the pop @0x42B0 loading R1-R6 is a flaw placed on purpose to make the exploit possible: on ARM, R0-3 are scratch registers, therefore the callee never has to save/restore them.

Corax (corax26 [at] gmail [dot] com)
