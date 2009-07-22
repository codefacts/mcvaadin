package com.googlecode.mcvaadin;

import java.net.URL;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;

/**
 * Generic event wrapper for Vaadin events.
 *
 * This class implements a general event that is used in conjunction with
 * {@link McListener}. It is a part of the pattern that a single listener can be
 * used to catch any type of Vaadin events.
 *
 * @see McListener
 * @see McUserListener
 */
public class McEvent {

    // Original events
    private Button.ClickEvent clickEvent;
    private Property.ValueChangeEvent valueChangeEvent;
    private FinishedEvent finishedEvent;

    // Other data
    private Map<String, String[]> params;
    private URL contextURL;
    private String URI;
    private byte[] data;

    // Returned data
    private DownloadStream returnStream;
    private byte[] returnData;
    private String returnDataType;
    private String returnDataName;
    private Component returnComponent;

    // For Table.ColumnGenerator
    private Table table;
    private Object itemId;
    private Object columnId;

    public McEvent(Upload.FinishedEvent event, byte[] data) {
        finishedEvent = event;
        this.data = data;
    }

    public McEvent(Button.ClickEvent event) {
        clickEvent = event;
    }

    public McEvent(Property.ValueChangeEvent event) {
        valueChangeEvent = event;
    }

    public McEvent(Map<String, String[]> parameters) {
        params = parameters;
    }

    public McEvent(URL context, String relativeUri) {
        contextURL = context;
        URI = relativeUri;
    }

    public McEvent(Table source, Object itemId, Object columnId) {
        table = source;
        this.itemId = itemId;
        this.columnId = columnId;
    }

    public String getURI() {
        return URI;
    }

    public URL getContextURL() {
        return contextURL;
    }

    public Map<String, String[]> getParams() {
        return params;
    }

    public Button.ClickEvent getClickEvent() {
        return clickEvent;
    }

    public Property.ValueChangeEvent getValueChangeEvent() {
        return valueChangeEvent;
    }

    public String getParam(String key) {
        if (params != null) {
            String[] val = params.get(key);
            if (val != null && val.length > 0) {
                return val[0];
            }
        }
        return null;
    }

    public void returnStream(DownloadStream returnStream) {
        this.returnStream = returnStream;
    }

    public DownloadStream getReturnStream() {
        return returnStream;
    }

    public void returnData(byte[] returnData) {
        this.returnData = returnData;
    }

    public void returnDataType(String returnDataType) {
        this.returnDataType = returnDataType;
    }

    public void returnDataName(String returnDataName) {
        this.returnDataName = returnDataName;
    }

    public byte[] getReturnData() {
        return returnData;
    }

    public String getReturnDataType() {
        return returnDataType;
    }

    public String getReturnDataName() {
        return returnDataName;
    }

    public Object getValue() {
        Object v = null;
        if (valueChangeEvent != null && valueChangeEvent.getProperty() != null) {
            v = valueChangeEvent.getProperty().getValue();
        }
        return v == null ? null : v;
    }

    public String getStringValue() {
        Object v = getValue();
        return v == null ? null : v.toString();
    }

    public byte[] getData() {
        return data;
    }

    public FinishedEvent getFinishedEvent() {
        return finishedEvent;
    }

    public String getText() {
        return getStringValue();
    }

    public Component getReturnComponent() {
        return returnComponent;
    }

    public void returnComponent(Component returnComponent) {
        this.returnComponent = returnComponent;
    }

    public Table getTable() {
        return table;
    }

    public Object getItemId() {
        return itemId;
    }

    public Object getColumnId() {
        return columnId;
    }

    /**
     * Shorthand for getClickEvent().getButton().
     *
     * @return
     */
    public Button getButton() {
        return clickEvent != null ? clickEvent.getButton() : null;
    }

}
