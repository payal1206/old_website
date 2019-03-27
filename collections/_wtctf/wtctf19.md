---
layout: post
title: WhatTheCTF 2019
date: 2019-03-25
category: index
permalink: /wtctf19/
---

## Event

On 23rd of March 2019 we have organised our first CTF ever! 

Watch this space as we'll post more info and photos from the event!

## Writeups

We would like to provide official writeups for some of the challenges. If you solved them in a different way, we'd be happy to add your writeup to the list, so send us an email or a tweet! 

### Official writeups
<table class="table-fixed table-striped">
  <thead>
    <td> Challenge </td>
    <td> Writeup author </td>
  </thead>
{% for writeup in site.wtctf %}
{% if writeup.category == "wtctf19" %}
  <tr>
    <td><a href="{{ writeup.url | prepend: site.url }}" target="_new">{{ writeup.title }}</a></td>
    <td>{{ writeup.author }}</td>
  </tr>
{% endif %}
{% endfor %}
</table>
