junit-contracts: A suite of tools to make contract testing easier 
=================================================================

Overview
--------

Contract tests allow developers to verify implementations of interfaces meet the requirements of the interface designer.  In java this can be problematic due to its single inheritance nature.  This package is a <a href="http://junit.org/junit4/">junit 4</a> based implementation of a contract testing framework.  This means that it runs within junit without modification.

Tutorials and Presentations
---------------------------

<a href="http://events.linuxfoundation.org/sites/events/files/slides/cloudstack.pdf">Junit-contracts: A Contract Testing Tool</a>, presented at CloudStack Collaboration Conference Europe, October 8-9, 2015, Dublin, Ireland. <a href="https://www.youtube.com/watch?v=WRf-RwWxf-w">Video available on YouTube</a>.

<a href="http://www.drdobbs.com/testing/simplifying-contract-testing/240167128">Simplifying Contract Testing</a>. Claude N. Warren, Jr., in Dr. Dobb's Journal, May 06, 2014.

Also see "How It Works" below.

Maven Repository Info 
---------------------

Release version

     Group Id: org.xenei 
     Artifact Id: contracts 

Snapshot versions are hosted at:

     https://oss.sonatype.org/content/repositories/snapshots/

Modules 
=======

The contracts project comprises three modules: junit-contracts, contract-test-maven-plugin and contract-cmdline.

junit-contracts
---------------

<a href="./junit">junit-contracts</a> is the module that encompasses the annotations and junit test
runners to execute the connection tests.

Release version

     Group Id: org.xenei 
     Artifact Id: junit-contracts 

contract-test-maven-plugin
--------------------------

<a href="./maven/">maven-contracts</a> is the module that encompasses the maven plugin that will report results and errors from running the tests.

Release version

     Group Id: org.xenei 
     Artifact Id: contract-test-maven-plugin 

contracts-cmdline
-----------------

<a href="./cmdLine/">contracts-cmdline</a> is the model that encompasses the command line tools that will generate reports equivalent ot the maven contracts plugin.

Release version

     Group Id: org.xenei 
     Artifact Id: contract-cmd-line 

Class Filters
=============

The junit-contracts module provides a set of classes to discover and filter classes on the classpath.  These may be generally useful. Among these is the ClassFilter which is patterned after the Java FileFilter class. 

Class filters are constructed in the maven and command line modules via strings.  Each string is the string representation of a filter.  For example: _Not( Wildcard( "*Test*" ) )_ will match any class name that does not have the string "test".  For more information see the <a href="classfilters.md">Class Filters</a> page. 

Contract Tests 
==============

Contract tests test the contract defined by a Java interface and the associated documentation.  The interface defines the method signatures,
but the documentation often expands upon that to define the behaviour that the interface is expected to perform. For example, the Map
interface defines put(), get(), and remove().  But the human readable documentation tells the developer that if you put() an object you must
be able to get() it unless the remove() has been called.  That last statement defines two tests in the contract test.

The basic argument for the use of contract tests is that you can prove code correctness.  That is, if every object interface is defined as
an interface, every interface has a contract test that covers all methods and their expected operation, and all objects have tests that mock
the objects they call as per the interface definition - then running the entire suite of tests demonstrates that the interconnection between
each object works.

If we know that _A_ calls _B_ properly and _B_ calls _C_ properly then we can infer that _A_ calls _C_ properly.  We can, with some work, prove that the
code is correct.

Contract tests will not discover misconfiguration.  For example, if class _A_ uses a map and expects a map that can accept null keys but the
configuration specifies a map implementation that does not accept null keys, the contract test will not detect the error.  But then the
error is a configuration error caused by a missing requirement for the configuration.


For more information on contract tests see

http://www.infoq.com/presentations/integration-tests-scam

http://www.jbrains.ca/permalink/interlude-basic-correctness

How it Works 
============

         A
         |
      ---+---.  
      |      |
      A1     A2

Most JUnit tests work vertically.  Originally they were all derived from a test class, and since version 4 they use annotations to identify
a method as a test.  However, they only work in the direct hierarchy of class inheritance. That is, if classes _A1_ and _A2_ are derived from
class _A_ and tests _A1Test_ and _A2Test_ are derived from _ATest_ then all the _ATest_ unit tests will be executed when _A1Test_ and _A2Test_ are run. 
However, with the use of interfaces there arises an orthogonal testing problem.

         A
         |
      ---+---.  
      |      |
      A1(I)  A2  B(I)

If we add interface _I_ to our class structure and define _A1_ as implementing _I_ and a new class _B_ as implementing _I_, then both _A1_ and _B_ require
the _I_ tests, but they have no classes in common in the hierarchy.  Therefore the tests for _A1_ and _B_ must implement them - this violates the
Don't-Repeat-Yourself (DRY) principle and will lead to problems if _I_ changes and the _A1_ and _B_ tests are not modified as well.

The ContractSuite solves this problem by requiring the following additions:

* an abstract test class _ITest_ for the interface _I_, that is annotated with the __@Contract( I.class )__. 
* a contract test suite that is annotated with __@RunWith( ContractSuite.class )__ and implements method annotated with __@Contract.Inject__ that creates a Producer.

Then when executing the suite: 
* discovers all of the __@Contract__ mappings between tests and interfaces. 
* finds all the implemented interfaces of the object under test and adds their contract tests to the suite. 
* executes the completed suite.

The result is that every class that has a contract suite defined will verify that its implementation of the interface is correct as per the
contract test.  If the contract test changes, the class test will use the new contract test implementation.

Samples and Examples
====================

The test code contains a number of examples and is well [documented]
(https://github.com/Claudenw/junit-contracts/tree/master/junit/src/test/java/org/xenei/junit/contract/exampleTests) for use as such.

The sample code tree includes a sample for a Serializable contract test.
