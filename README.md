goos-by-chapter
===============

Chapter by chapter build of the AuctionSniper application from Growing Object
Oriented Software, Guided By Tests (GOOS).  Each chapter has its own tag,
starting with Chapter 10 (tag=ch10).  Each source file has citations from the
original text, stating the source chapter and pages for the code changes.

There are some important differences between this implementation and the
 original from book.  Specifically, this project
 - is Maven based to make dependency management easy.
 - uses Mockito instead of JMock.  My team is reading this book
 as part of a group study, and we have found the the "expectations" style of
 JMock to be confusing compared to the "when/then" and "verify" of Mockito.
 - uses OpenFire v3.8.  This forces a slightly different way of connecting
 via Smack.  Comments are added to the code where it differs from the original.