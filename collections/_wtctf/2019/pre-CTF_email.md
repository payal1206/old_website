---
layout: post
date: 2019-03-25
title: pre-CTF Email
category: wtctf19
author: kitkat
---

### Challenge type
misc

### Description

> You were given a flag in the pre-CTF email. Now's your chance to get those points.


### Challenge solve

The initial emails sent out to all competitors included a final line which said:
>
> PS Here is your first flag - crack it and keep it safe:
{% highlight shell %}
VGxOQlFscDdSM1V4Wmw4eFpsOXFkVFJuWHpSZmMzazBkRjk1TURCNFpsOTVkbmh5ZlE9PQ==
{% endhighlight %}
>

The first thing to notice is that the flag ends with two equals signs. If you're not sure what that could be try googling 'string ending with --'. The first hit says:

> ![google search](/assets/ctf-files/wtctf19/base_64.png){:width="700px"}

> Why does a base 64 encoded string have an = sign at the end.

Ah! So it's base 64 encoded!

At this point we can open up [CyberChef](https://gchq.github.io/CyberChef), paste the flag in and select 'from base64'. The following output now appears: 

{% highlight shell %}
TlNBQlp7R3UxZl8xZl9qdTRnXzRfc3k0dF95MDB4Zl95dnhyfQ==
{% endhighlight %}

Hmm, *another* string with two equals signs. Let's add into cyber chef *another* 'from base64' recipe. Now our output is:


{% highlight shell %}
NSABZ{Gu1f_1f_ju4g_4_sy4t_y00xf_yvxr}
{% endhighlight %}

This is looking a lot like our flag. We know most flags begin: 'AFNOM' so we can see that A->N and N->A. Ah, it's a rotation cipher.

Once again, cyberchef has a rotation recipe: ROTxx. I'll try the one at the top, ROT13 first.

And out pops our flag:
`AFNOM{Th1s_1s_wh4t_4_fl4g_l00ks_like}`

---

#### Other useful resources:
* [Base What? A Practical Introduction to Base Encoding](https://code.tutsplus.com/tutorials/base-what-a-practical-introduction-to-base-encoding--net-27590)
