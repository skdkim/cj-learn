[![Build Status](https://api.travis-ci.org/skdkim/cj-learn.svg?branch=master)](https://travis-ci.org/skdkim/cj-learn)

# Inventory

[_CJ Powered_](https://engineering.cj.com)

Sample application for learning object-oriented programming and test-driven
design.

## The Problem

We are Ace Dry Goods, an Internet store selling packaged foods that store
well and taste great. Unfortunately, we keep running out of the stuff we
sell.

We need to build an inventory management system right away. Some company
managers got together and fleshed out the inventory rules. Our ordering
system developers came up with a Java interface, `InventoryManager` that
will give them all they need to place orders each day as long as we
implement it according to the rules.  They also gave us an interface to
the database they've been using to manually track items and levels,
`InventoryDatabase`.

Some of the rules involve information from the marketing department,
including information about items on sale and seasons. Their developers
put together a Java interface `MarketingInfo` we will use to integrate
with them.

Before you got here, the ordering department and marketing department
developers put together a skeleton of the application, a classs called
`AceInventoryManager`. Good thing you're here, though; they need to get
back to their own development projects as quickly as possible.

Use the other departments' offerings as much as you need. If you can show
where any of those interfaces are insufficient, don't hesitate to ask for
interface changes. They will review any requests and enhance the interfaces
if necessary.


## How to Proceed

0. Clone this repo and get your development environment working. You will
know it's all working when you can run the existing unit test suite.
0. _Bonus: have travis-ci.org automatically build and test your clone_
    - sign up to travis-ci.org if you haven't already
    - activate your repository on the Travis-CI website
    - replace the badge in this readme
0. Start working on the rules as if they are new features, one-by-one.
0. You may be advised of new rules or changes to the rules as you progress.

## Tips for Success

- Focus on one feature at a time. Don't worry about any feature except for
the one you are working on
- Practice red-green-refactor. Write a failing test, get it to pass, commit
the code, refactor if you like, commit the code. Never refactor when you are
not passing
- Don't forget to test your edge cases. Each feature may require several unit
tests
- Only write code that is necessary to pass tests. That way you ensure your
tests cover a high proportion of your code
- Don't let requirements changes throw you. Keep focused on the current
feature, no matter how the requirements change. Keep working on features in
priority order even if that priority changes
- If requirements are incomplete or in conflict, your teacher will act as
the product owner and help clarify

## The Business Rules

Here is the company's best shot at the inventory rules
you should implement. As mentioned, they are sure to discover other rules or
even change their minds as you give them more and more working code.

These are in priority order. Unless it is blocked by something else, you
should always work on the highest priority item first.

0. For each item we stock, order enough to bring the quantity on hand to its
specified inventory level
0. When an item goes on sale, keep an additional 20 units on hand
0. For seasonal items, keep double the normal inventory on hand during the
high-demand season. Each seasonal item has one high-demand season.
0. When the required inventory level is above normal due to a sale, season,
or other reason, use the highest calculated level. In other words, modifiers
do not stack
0. Some items can only be ordered on the first day of the month, do not issue
any orders for those items except on the first of the month
0. Some items can only be ordered in packages containing multiple units. we
can stock more than the normal inventory level for those items if necessary
0. We want to know how many orders are in order. For new orders only create 
additional orders to fill the requirement.
