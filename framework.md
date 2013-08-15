Introduction
============

During the development of BeatButcherMPC I have learned an awful lot about Object Oriented Programming. I already had a firm basis in OOP coding, and had previously used it extensively to solve large problems, and each time I used it, I realised what a useful tool it is. BeatButcherMPC is the largest Java GUI I have ever written, and it was all coded from scratch, without the use of a form designer, in Eclipse using Swing.

The Model-View-Controller design pattern is something that is
taught quite early on in Java courses, and whilst I think this is great, it took me a while to fully realise the benefits of the pattern. In particular it took writing an application of a scale where I could not simply write spaghetti code to *force* myself to write a fully OOP solution. That meant no static variable "get outs", or huge switch statements, or badly delegated code. In fact, once I had finished designing the MVC classes, I realised that a lot of the basic
functionality, which would be common to many reasonable scale GUI based Java applications, could be easily refactored into a seperate package.

Explanation
===========

The package consists of a set of interfaces, the class FunctionManager, and some utility classes. At a basic level, the framework allows you to define a set of Function classes which simply contain an execute method and a name, in a FunctionProvider class (the Controller) which would be created with a reference to the Model and View. The functions of the controller can then be called from any GUI component by name with only a reference to FunctionManager. Assuming that your
FunctionProvider State and View models are seperate classes, this forces the MVC pattern upon you and your unassuming minions.

The side effect of this structure is that it becomes very easy to track GUI bugs, because you can add a FunctionListener to the FunctionManager and output all GUI function calls (which are nice verbal names) to a log file. This means you can repeat the same steps to find exact bugs. Note though that this still requires a structured approach, since every single GUI function must correspond to a Function class (which may well be anonymous).

The framework also easily takes care of stack based Edit Undo/Redo functionality. UndoFunction extends the Function interface, adding the undo() and redo() methods. Whenever a Function is called within FunctionManager, the type is checked. If the class is an UndoFunction then it is pushed onto the function history stack where it may be undone and consequently redone. The advantage of an integrated system like this is that it enables you to maintain integrity of the Model state

Since every function call which may modify data passes through the FunctionManager, it knows whether or not the program is still in a state where an Undo would be valid. To do this, it checks the type of Function. If the function is a Function rather than an UndoFunction then the state is assumed to have changed and the history stack cleared. If a Function does not affect the state, then it may inherit ImmutableFunction, which indicates to the FunctionManager that the model state will
not be affected by the operation. In this way the history stack is guaranteed to maintain its integrity in synchronization with the Model state.

The limitations of the system are firstly that parameters may not be passed to Function methods, so all data must be "pulled" from GUI components within Function methods rather than "pushed" into them as parameters. In practice this is not a problem, but requires a slight shift in thinking. Secondly, any Function which affects the Model State (i.e a class which inherits Function rather than one of its derivatives) will clear the Function history, even if the state change would not
affect the Functions on the history stack. To get around this, and couple of additional interfaces could be defined: a State interface containing no methods, simply defining any class as a data provider, and an interface like StateCheck, containing a method State[] getAffectedStates(). This would allow a Function to provide the FunctionManager with a set of State classes which it edits, allowing it to check these with the methods on the FunctionStack. If all Functions implemented the
interface then no history would ever be lost unnecessarily. For now though, it is easier to either provide functions which do not clear the Function history with undo/redo methods, or to make the functions Immutable. 

The framework contains a couple of utility Swing classes, such as FunctionMenuItem which extends JMenuItem to removes the need to add a basic functionManager.executeFunction("Foo"); actionListener to every menu item. FunctionHistoryWindow provides a Dialog to display the Function history stacks. Another utility class worth notin is DefaultFunctionFactory. A Function object must be created each time a GUI component calls FunctionManager if it is to be put on the
history stack (so it can store previous state information etc), so I decided to use the Factory design pattern. The alternative to this was to use Class<Function>.createInstance(), however that involves trusting the programmer not to use Function objects with a non default constructor, something which I wanted to avoid. As a result, within a FunctionProvider, all Functions are created from a factory. Since ImmutableFunction and Function never need to store state information
(since they will never be called again), there is no need to create a new instance every time getFunction() is called. Consequently the DefaultFunctionFactory provides a one line solution to wrapping a non mutating function.

Future
======
I may also add a StateListener interface and StateChangeEvent event, allowing Views to be added as listeners to State objects. This would mean only the minimum amount of GUI components are refreshed on state changes. A design dilema is whether the State object should fire change events when its mutator methods are called, or whether the FunctionManager should fire the events when a non immutable function acts upon the State (or does not implement the StateCheck interface). 

The State object implementation is probably more correct as it matches the Swing event model more closely, though it means that every mutator method in implementing classes must fire an event. Since the only purpose of the StateListener would be for the View to update, this seems like unnecassary code. On the other hand it leaves the responsibility of the updating to the developer writing the functions rather than the developer writing the state objects.

