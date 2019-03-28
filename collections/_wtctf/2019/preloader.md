---
layout: post
date: 2019-03-26
title: Preloader
category: wtctf19
author: jedevc
---

### Challenge type

reversing

### Description

> get the flag using this one weird linker trick!
> ```
> ssh george@challenge.whatthectf.afnom.net -p 4011
> ```
> password: themanintheyellowhat

### Challenge solve

We can ssh into the machine, and have a quick look round.

```
$ ls -l
total 24
---x--x--x 1 fifi fifi 16576 Mar 20 09:57 preloader
-rw-rw-r-- 1 fifi fifi   244 Mar 20 09:57 preloader.c
$ file preloader
preloader: executable, regular file, no read permission
```

That's interesting - a binary that can be executed but can't be read, so we
can't easily extract information from it.

Let's look at the (censored) source code then:

```c
#include <stdio.h>

int main() {
	// you have to go to the binary to get your flag!
	volatile char *flag = "<censored>";

	puts("Hello!");
	printf("Here's your flag:\n");
	// printf("%s\n", flag);
	printf("Hope you got that.\n");

	return 0;
}
```

So, the flag should have been printed out - but it actually won't be, because
it's commented out! So we somehow need to trick the program into printing it
out.

When we run the program, we can confirm:

```
$ ./preloader
Hello!
Here's your flag:
Hope you got that.
```

Looking at the challenge description, and a bit of clever googling, we can
eventually find out about the [LD_PRELOAD
trick](https://stackoverflow.com/questions/426230/what-is-the-ld-preload-trick).

This trick will let us hook into a function call and dump the contents of the
stack. We can choose any function that is called by `main` but, the compiler may
have optimized the calls to `printf` to calls to `puts` as they contain no
format strings. So it makes most sense to try hooking into `puts`.

```c
#include <stdio.h>

int puts(const char *s) {
	printf("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-%s-");
	printf("hooked-%s\n", s);

	return 0;
}
```

We redefine `puts` to first dump the contents of the strings on the stack, and
then to print the actual data with the "hooked" prefix. Since we know the flag
is stored somewhere on the stack, this should dump it.

```
$ gcc solution.c -shared -o solution.so
$ LD_PRELOAD=solution.so ~/preloader
���--���-��AWI��AVI��AUA��ATL�%L,---(null)-Hello!-�1���U-H�=�--AFNOM{Seek_And_Ye_Shall_Find}-hooked-Hello!
���--���-��AWI��AVI��AUA��ATL�%L,---(null)-Hello!-�1���U-H�=�--AFNOM{Seek_And_Ye_Shall_Find}-hooked-Hello!
Segmentation fault (core dumped)
```

And there's our flag, just sitting there!

### Aside

Using this method of hooking into a function, we could read from any location in
memory and dump the assembly code. This means that it is impossible for a binary
to be only executable without being in some way readable.

Another technique for solving this challenge was using ptrace to read from
arbitrary memory addresses.
