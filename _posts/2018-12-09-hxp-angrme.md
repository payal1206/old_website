---
layout: post
title: angrme
date: 2018-12-09
ctf: hxp2018
author: neko3
---

The title of the challenge, **angrme** was a big clue as to what is expected: use the [angr](http://angr.io) framework to solve it!

Running the binary with some random input makes it print `:(`. A quick look at the binary in [IDA](https://www.hex-rays.com/products/ida/support/download_freeware.shtml) shows a lot of comparisons; if any of them fails, it will output `:(`, and if they are all successful, it will output `:)`. So, we need to tell angr to symbolically execute until it reaches the basic block which outputs `:)`.

Solve:
{% highlight python %}
import angr

proj = angr.Project("angrme")
simgr = proj.factory.simgr()
simgr.explore(find=lambda s: b":)" in s.posix.dumps(1))
s = simgr.found[0]
print(s.posix.dumps(0))
{% endhighlight %}


Flag:
`b'hxp{4nd_n0w_f0r_s0m3_r3al_ch4ll3ng3}'`