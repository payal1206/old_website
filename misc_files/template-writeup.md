`---
layout: post
title: <challenge title>
date: <YYYY-MM-DD>
ctf: <CTF name
author: <your nickname>
---

IMPORTANT: Remove the ` from the first line! That's just a work-around because of Jekyll -__-''

Your content goes here.

Tips:
Don't add the challenge name again, it'll be generated from the header. Don't use # please. Start from ## (equivalent to <h2>).

If anyone wants a crash course into markdown, this website was useful for me https://daringfireball.net/projects/markdown/basics

Code is correctly parsed if it's 4 spaces indented (tab works) or if you want a special highlight please put it between the tags (sorry Sam)

	{% highlight LANGUAGE %}
	code goes here....
	{% endhighlight %}

where LANGUAGE is the programming language you wanna use.

A full list of short names for them is available at
http://pygments.org/docs/lexers/
As far as I noticed, the names are the common ones.

It'll be preferable you name your file following the convention

YYYY-MM-DD-Name-of-Challenge.md