McVaadin is a combination of factory pattern, new components and helper classes and functions to you write [Vaadin applications](http://vaadin.com) much simpler and quicker. It is just a jar that sits in your WEB-INF/lib.

## Background ##
McVaadin was inspired by scripting languages in the sense that it uses more functions and focuses on code brevity.

Otherwise, it was written to help to make some things easier than they currently are in the base Vaadin framework. The unified event listener and more HTML-like user notifications (alert, confirm functions) are examples of this.

You'll like McVaadin if you like:
  * Vaadin - Good-looking, server-driven web applications in Java.
  * More compact Java code with more function calls and less variables and less instances.
  * Code autocomplete feature of your IDE.
  * You have missed an easy way of using button keyboard bindings, user notifications, confirm dialogs, translations, html content, etc in Vaadin.
  * Small things. McVaadin is some 12 classes in a 110k jar.

McVaadin is currently based on Vaadin 6.1.5.

## What does it do then ##

First, instead of this HelloWorld sample:

```
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class HelloWorld extends com.vaadin.Application {
    @Override
    public void init() {
        final Window main = new Window("Hello window");
        setMainWindow(main);
        main.addComponent(new Label("Hello World!"));
    }
}
```

You would write this:

```
import com.googlecode.mcvaadin.McApplication;

public class HelloWorld extends McApplication {
    @Override
    public void ui() {
        label("Hello World");
    }
}
```

Every application has a main window so why would you do it every time? The McApplication class takes care of this stuff. This really isn't so much help, but together with other similar small ideas and fixes it makes the mcvaadin.jar quite a powerful addition to Vaadin.

## UI Builder factory functions ##

The brevity of above sample is also because of the "UI builder functions" pattern. For every component in Vaadin framework there is a factory function that creates it and adds it to a selected layout (or ComponentContainer). You can select an layout using `with(someLayout)` function and traverse back using `endWith()`.

As the factory function automatically adds the component to layout you are less likely to need a local variable for every component. For example, you can simply write something like:

```
label("Contact Information").setStyleName("h1"); // Note: there is also function: h1("some text")
horizontal(); // create and select a new HorizontalLayout. This is equivalent to with(horizontallayout())
textfield("First name");
textfield("Last name");
endWith(); // Select back to the previous VerticalLayout
textfield("Street address").setColumns(30);
horizontal(); // create and select a new HorizontalLayout. This is equivalent to with(horizontallayout())  
textfield("Postal Code").setColumns(10);
textfield("City").setColumns(20);
endWith(); // Select back to the previous VerticalLayout
```

No need for local references in simple cases like this.

Also, to make the UI builder functions available everywhere there are three delegate implementations of the UIBuilder class:
  * McApplication - Extended application class with UI builder functions (+ threadlocal implementation)
  * McWindow - Extended window with UI builder functions
  * McComponent - Extended CustomComponent with UI builder functions

Remember, that it is always possible to use the traditional way of `new Label("text")` in conjunction with the UI builder.

UI Builder factory offers functions for at least the default component constructor as well as the constructor with a caption. In addition to these, some commonly used components have extra factory functions. Like the `textfield(caption, initialContent, size)`. There is likely to be a lot more of these in the future.

## One type of listener for all the events ##

Another key patterns of McVaadin is that it uses single event listener McListener for all events. So regardless whether it is a button click or a value change you implement the listener the same way:

```
button.addListener(new McListener() {
	public void exec(McEvent evt) {
		// Do something here
	}
});
```

To avoid unwanted event recursion (happens if you change the component state in listeners) the McListener implementation allows a single listener implementation to be executed only once in the same thread.

Furthermore, there are situations you like to respond to only the first (user initiated) event - not the ones caused by your code. You can then use the McUserListener. It allows only the first of the McUserListeners to be invoked in a single thread, effectively avoiding unnecessary checks in you code.

Again, you choose. You can use the original event listeners if the above model does not fit into your application.

## Other Extensions ##
  * Automatic creation of the main window. There always should be at least one window per application, so it is created automatically.
  * Implementation of the "ThreadLocal pattern" - a static way of accessing application instance: McApplication.current()
  * Helper functions for showing user notifications, error messages and for confirm dialogs.
  * Everything is immediate. For historical reasons every component in Vaadin is "off-line" unless specifically told to synchronize with the server "immediately". This default is reversed in McVaadin. Use setImmediate(false) if you don't want this.


## What McVaadin does not offer (at least for now) ##
  * Only simple data binding helpers. There clearly is demand for more of them, but they require too much work to fit in here (work in progress)
  * A language translation mechanism has been planned, but is only partly implemented.
  * Keyboard helpers have been planned, but not yet implemented.
  * Some components are missing in the UI builder. Namely: PopupView, Upload, URIFragmentUrility, CustomComponent and CustomLayout.

## Getting started ##
First of all you need the Vaadin library. [Download it](http://vaadin.com/download) or if using Eclipse IDE [use the plugin](http://vaadin.com/eclipse).

Then [download the mcvaadin.jar](http://code.google.com/p/mcvaadin/downloads/list) and put it under you WebContent/WEB-INF/lib.

Start coding. You can test it with the following "Embedded HelloWorld Panel" code right in you Vaadin application init:
```
win.addComponent(new McComponent() {
    public void ui() {
        with(panel("HelloWorld Sample"));
        label("Hello McVaadin User");
        }
    });
```
This makes an inline subclass of McComponent (which is a subclass of CustomComponent) and you initialize the content of that composite within the ui() function. Your IDE should suggest the right imports for you and fix if you don't have a variable named _win_ that I used here as an example.

### Next Steps ###

That was very simple example. From here you have a few options:
  * '''Inherit the McApplication to create new Vaadin application'''. If writing a new application this is the best way to do. Also it is not so complex task to change the inheritance order and rename the init()-function to ui().
  * '''Inherit and extend the McWindow class'''. If you consider using McVaadin only in a part of your application, this might be a good option.
  * '''Inherit and extend the McComponent class'''. This is a good way of doing logical UI composites.
  * Mix and use all the above. This is in practice what you would do. But start small.

And have fun! You are more productive now.