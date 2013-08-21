## Current limitations
* native task support got kind of crushed
* * we've done so much support for Java using Maven it's not exactly advantageous not to use it
* * although we maintain the theoretical use-case
* task dependencies (formerly intended) are not used
* * again, there is support for huge improvements, but not enough manpower
* command-line client disappeared
* * was there ever a use-case for batch runs, what with Java-code generator tasks?
* generic evaluators are gone
* * the price you pay for totally generic user-types and storage
* * plans for future improvements (user-aided type inference)
* dbase triggers are gone, too
* * they don't make much sense with the absence of generic evaluators
* * it's kind of unclear how this would work over the persistence layer abstraction
* ...
* probably a lot more here, too; needs reordering
