package com.googlecode.mcvaadin.helpers;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.mcvaadin.McApplication;
import com.googlecode.mcvaadin.McComponent;
import com.googlecode.mcvaadin.McEvent;
import com.googlecode.mcvaadin.McListener;
import com.googlecode.mcvaadin.McWindow;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.Action;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Form;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Layout.AlignmentHandler;

/**
 * User interface factory class.
 *
 * This class offers factory functions to build a user interface in a linear
 * way. Each Vaadin component (and McVaadin extension component) has a function
 * counterpart in this class that creates a new instance of it and adds it to
 * selected component container (layout etc). Effectively this makes the
 * creation of user interfaces more simple. This:
 * <code>Label label = new Label("Hello"); layout.addComponent(label);</code>
 * becomes simply this: <code>label("Hello")</code>
 * <p>
 * The layout or component container can be selected using the
 * {@link #with(ComponentContainer)} function and previous component containers
 * with {@link #endWith()} function.
 * <p>
 * There also is a set of functions to help displaying user messages more
 * conveniently as specified in {@link UserMessages} class.
 * <p>
 * As you may use this class separately to build UI in any Vaadin application,
 * it is meant to be used to through the {@link McApplication}, {@link McWindow}
 * and {@link McComponent}. They all implement delegate functions for this class
 * to minimize the amount of code needed.
 */
public class UIBuilder extends VaadinBuilder implements Serializable {

    private static final long serialVersionUID = 3266450463409789028L;

    private UserMessages msg;

    private Translator translator;

    public UIBuilder(ComponentContainer cc) {
        super(cc);
        msg = McApplication.current().getMsg();
        translator = McApplication.current().getTranslator();
    }

    /** Create new builder with given root component container. */
    public UIBuilder build(ComponentContainer cc) {
        return new UIBuilder(cc);
    }

    // The following functions require thread local pattern

    /**
     * Get current application instance. This uses the thread local pattern and
     * requires that application is inherited from the MCApplication class.
     *
     * @see #getWin()
     * @return application instance or null if not found.
     */
    public McApplication getApp() {
        return McApplication.current();
    }

    /**
     * Get the main window of current application. This is effectively same as
     * getApp().getMainWindow(). See requirements for application in
     * {@link #getApp()}.
     *
     * @see #getApp()
     * @return Main window instance or null if not found.
     */
    public Window getMainWin() {
        McApplication a = getApp();
        if (a != null) {
            return a.getMainWindow();
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- Custom component constructors
    // ---------------------------------------------------------------------------------------------------

    /**
     * Override window for better defaults.
     *
     */
    @Override
    public Window window() {
        Window w = super.window();
        // Set margin on by defaults
        ((Layout) w.getContent()).setMargin(true);
        return w;
    }

    public Window window(String caption, int width, int height) {
        Window c = window(caption);
        c.setWidth(width + "px");
        c.setHeight(height + "px");
        return c;
    }

    public Label h1(String str) {
        return labelWithStyle(str, "h1");
    }

    public Label h2(String str) {
        return labelWithStyle(str, "h2");
    }

    public Label hr() {
        return html("<hr />");
    }

    public Label pre(String str) {
        Label c = label();
        c.setContentMode(Label.CONTENT_PREFORMATTED);
        c.setValue(str);
        return c;
    }

    public Label html(String str) {
        Label c = label();
        c.setContentMode(Label.CONTENT_RAW);
        c.setValue(str);
        return c;
    }

    /**
     * We set the size undefined by default.
     *
     */
    @Override
    public Label label() {
        Label c = super.label();
        c.setSizeUndefined();
        return c;
    }

    /**
     * Override to set the content instead of the caption.
     */
    @Override
    public Label label(String str) {
        Label c = label();
        c.setValue(str);
        return c;
    }

    private Label labelWithStyle(String str, String style) {
        Label c = label(str);
        c.setStyleName(style);
        return c;
    }

    /**
     * Create a textfield of given caption and default value.
     *
     * @param caption
     * @param value
     * @return
     */
    public TextField textfield(String caption, String value) {
        TextField c = textfield(caption);
        c.setValue(value);
        return c;
    }

    /**
     * Create a textfield of given size and default value.
     *
     * @param caption
     * @param value
     * @param cols
     * @return
     */
    public TextField textfield(String caption, String value, int cols) {
        TextField c = textfield(caption);
        c.setValue(value);
        c.setColumns(cols);
        return c;
    }

    /**
     * Create a textfield of given size and default value.
     *
     * @param caption
     * @param value
     * @param cols
     * @param rows
     * @return
     */
    public TextField textfield(String caption, String value, int cols, int rows) {
        TextField c = textfield(caption);
        c.setValue(value);
        c.setColumns(cols);
        c.setRows(rows);
        return c;
    }

    public GridLayout gridlayout(int w, int h) {
        GridLayout c = new GridLayout(w, h);
        add(c);
        return c;
    }

    /**
     * Alias for {@link #label(String)}.
     */
    public Label text(String text) {
        return label(text);
    }

    /**
     * Create a "confirmed button". The user is presented a confirmation message
     * before the event is sent and listener invoked.
     *
     * @param caption
     * @param description
     * @param okCaption
     * @param cancelCaption
     * @param listener
     * @return
     */
    public Button confirmbutton(final String caption, final String description,
            final String okCaption, final String cancelCaption,
            final McListener listener) {
        Button b = button(caption, new McListener() {

            @Override
            public void exec(McEvent e) throws Exception {
                confirm(caption, description, okCaption, cancelCaption,
                        listener);
            }
        });
        return b;
    }

    /**
     * Create a "confirmed" button. The user is presented a confirmation before
     * the click event is sent.
     *
     * @param caption
     * @param description
     * @param listener
     * @return
     */
    public Button confirmbutton(final String caption, final String description,
            final McListener listener) {
        return confirmbutton(caption, description, null, null, listener);
    }

    /**
     * Add an {@link URIHandler} to current window.
     *
     * @param handler
     */
    public void addUriHandler(URIHandler handler) {
        Window w = cc.getWindow();
        if (w == null) {
            w = getMainWin();
        }

        if (w != null && handler != null) {
            w.addURIHandler(handler);
        }
    }

    /**
     * Add a {@link ParameterHandler} to current window.
     *
     * @param handler
     */
    public void addParamHandler(ParameterHandler handler) {
        Window w = cc.getWindow();
        if (w == null) {
            w = getMainWin();
        }

        if (w != null && handler != null) {
            w.addParameterHandler(handler);
        }
    }

    /**
     * Create a new link.
     *
     * @param linkText
     * @param linkUrl
     * @param targetName
     * @return
     */
    public Link link(String linkText, String linkUrl, String targetName) {
        Link c = link(linkText);
        c.setResource(new ExternalResource(linkUrl));
        c.setTargetName(targetName);
        return c;
    }

    /**
     * Override to put spacing on by default.
     *
     */
    @Override
    public Panel panel() {
        Panel c = super.panel();
        VerticalLayout root = (VerticalLayout) c.getContent();
        root.setSpacing(true);
        return c;
    }

    /**
     * Set component alignment.
     *
     * If the currently focused component container implements the
     * {@link AlignmentHandler}, this function applies the requested alignment
     * to given sub-component.
     *
     * @param component
     * @param alignment
     */
    public void align(Component component, Alignment alignment) {
        if (cc instanceof Layout.AlignmentHandler) {
            Layout.AlignmentHandler l = (Layout.AlignmentHandler) cc;
            l.setComponentAlignment(component, alignment);
        }
    }

    /**
     * Set component expand ratio.
     *
     * If the currently focused component container implements the
     * {@link AbstractOrderedLayout}, this function applies the requested expand
     * ratio for the given sub-component.
     *
     * @param component
     * @param ratio
     * @see AbstractOrderedLayout
     */
    public void expand(Component component, float ratio) {
        if (cc instanceof AbstractOrderedLayout) {
            AbstractOrderedLayout l = (AbstractOrderedLayout) cc;
            l.setExpandRatio(component, ratio);
        }
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- Key Binding Helpers
    // ---------------------------------------------------------------------------------------------------

    /**
     * Bind a keyboard key to a button.
     *
     */
    public Button bindKey(Button b, int key) {
        return bindKey(b, key, null);
    }

    /**
     * Bind a keyboard key to a button.
     *
     */
    public Button bindKey(Button b, int key, int modifier) {
        return bindKey(b, key, new int[] {modifier});
    }

    /**
     * Bind a keyboard key to a button. This finds the {@link Action.Container}
     * parent of this button that supports keyboard listener and installs a
     * listener there to invoke a button click when desired key combination is
     * pressed.
     *
     * @param b
     * @param key
     * @param modifiers
     * @return
     */
    public Button bindKey(final Button b, int key, int[] modifiers) {
        if (b == null) {
            return b;
        }

        Action.Container ac = (Panel) Utils.findParent(b,
                Action.Container.class);
        if (ac == null) {
            ac = McApplication.current().getMainWindow();
        }

        if (ac == null) {
            return b;
        }

        bindKey(ac, key, modifiers, new McListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void exec(McEvent e) throws Exception {
                // We click the button by simulating a client-side event.
                Map vars = new HashMap();
                vars.put("state",new Boolean(!(Boolean)b.getValue()));
                b.changeVariables(this, vars);
            }
        });

        return b;
    }

    /**
     * Bind a keyboard key to a button. This finds the parent of this button
     * that supports keyboard listener and installs a listener there to invoke
     * when desired key combination is pressed.
     *
     * @param actionContainer
     * @param key
     * @param modifiers
     */
    public void bindKey(Action.Container actionContainer, int key,
            int[] modifiers, McListener listener) {
        actionContainer.addActionHandler(new KeyboardHandlerImpl(key,
                modifiers, listener));
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- Data Binding Helpers
    // ---------------------------------------------------------------------------------------------------
    public Table bindData(Table table, Collection<?> data) {
        BeanItemContainer<Object> bic = new BeanItemContainer<Object>(data
                .iterator().next().getClass());
        for (Object object : data) {
            bic.addBean(object);
        }
        table.setContainerDataSource(bic);
        table.setReadThrough(true);
        return table;
    }

    public AbstractSelect bindData(AbstractSelect select, Collection<?> data,
            String captionPropertyId) {
        BeanItemContainer<Object> bic = new BeanItemContainer<Object>(data
                .iterator().next().getClass());
        for (Object object : data) {
            bic.addBean(object);
        }
        select.setContainerDataSource(bic);
        select.setItemCaptionPropertyId(captionPropertyId);
        return select;
    }

    public Form bindData(Form form, Object data, String[] properties) {
        if (data != null) {
            form.setItemDataSource(new BeanItem(data));
            form.setVisibleItemProperties(properties);
        } else {
            form.setItemDataSource(null);
        }
        return form;
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- Delegate Functions for translator
    // ---------------------------------------------------------------------------------------------------

    public String getResourceBundleName() {
        return translator.getResourceBundleName();
    }

    public void setResourceBundleName(String resourceBundleName) {
        translator.setResourceBundleName(resourceBundleName);
    }

    public String tr(String str, Object... arguments) {
        return translator.tr(str, arguments);
    }

    public String tr(String str) {
        return translator.tr(str);
    }

    // ---------------------------------------------------------------------------------------------------
    // --------------------------------- Delegate Functions for user messages
    // ---------------------------------------------------------------------------------------------------

    public void alert(String message, String description) {
        msg.alert(message, description);
    }

    public void alert(String message) {
        msg.alert(message);
    }

    public Window confirm(String message, McListener listener) {
        return msg.confirm(message, listener);
    }

    public Window confirm(String title, String message, McListener listener) {
        return msg.confirm(title, message, listener);
    }

    public Window confirm(String title, String message, String okTitle,
            String cancelTitle, McListener listener) {
        return msg.confirm(title, message, okTitle, cancelTitle, listener);
    }

    public void error(String message, String description, Throwable t) {
        msg.error(message, description, t);
    }

    public void error(String message, String description) {
        msg.error(message, description);
    }

    public void error(String message, Throwable t) {
        msg.error(message, t);
    }

    public void error(String message) {
        msg.error(message);
    }

    public void error(Throwable e) {
        msg.error(e);
    }

    public void notification(String message, String description) {
        msg.notification(message, description);
    }

    public void notification(String message) {
        msg.notification(message);
    }

    @Override
    public HorizontalLayout horizontallayout() {
        HorizontalLayout c = super.horizontallayout();
        c.setSpacing(true);
        return c;
    }

    @Override
    public VerticalLayout verticallayout() {
        VerticalLayout c = super.verticallayout();
        c.setSpacing(true);
        return c;
    }

    /**
     * Create and select a HorizontalLayout.
     *
     * This is the same as <code>with(horizontallayout())</code>.
     *
     * @return
     */
    public HorizontalLayout horizontal() {
        HorizontalLayout c = horizontallayout();
        with(c);
        return c;
    }

    /**
     * Create and select a HorizontalLayout.
     *
     * This is the same as <code>with(verticallayout())</code>.
     *
     * @return
     */
    public VerticalLayout vertical() {
        VerticalLayout c = verticallayout();
        with(c);
        return c;
    }

    @Override
    public Table table() {
        Table c = super.table();
        c.setSelectable(true);
        c.setSortDisabled(false);
        return c;
    }

}
