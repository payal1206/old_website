afnom.github.io
===

The official website for A Finite Number of Monkeys.

Steps to Publish
---
* Make changes in the source branch
* Build and test the site locally (`jekyll --server`)
* Commit changes to source branch
* Generate the final site in master.
  * `git branch -D master`
  * `git checkout -b master`
  * `git filter-branch --subdirectory-filter \_site/ -f`
  * `git checkout source`
  * `git push --all origin`

_Source: [https://github.com/rson/rson.github.com/blob/source/README.md](https://github.com/rson/rson.github.com/blob/source/README.md)_
