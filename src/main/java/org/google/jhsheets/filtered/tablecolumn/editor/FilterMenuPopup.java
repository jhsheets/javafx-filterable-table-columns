/*
 * Copyright (c) 2013, jhsheets@gmail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.google.jhsheets.filtered.tablecolumn.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PopupControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import com.sun.javafx.css.StyleManager;

/**
 * A menu for displaying column filter settings.
 * There is a {@link #saveButton}, {@link #cancelButton} and {@link #resetButton}.
 * Only one instance of this popup will be visible at one time.
 * @author jhs
 *
 */
public class FilterMenuPopup 
extends PopupControl
{
	/**
	 * Use context-menu's CSS settings as a base, and override with our filter-popup-menu settings
	 */
	private static final String[] DEFAULT_STYLE_CLASS = { "filter-popup-menu", "context-menu" };
	
	static {
        StyleManager.getInstance().addUserAgentStylesheet(FilterMenuPopup.class.getResource(FilterMenuPopup.class.getSimpleName() + ".css").toString());
    }
	
	private static FilterMenuPopup currentlyVisibleMenu;
	
    private final ObjectProperty<Node> contentNode;
    private final SimpleObjectProperty<Button> saveButton;
    private final SimpleObjectProperty<Button> resetButton;
    private final SimpleObjectProperty<Button> cancelButton;
    private final SimpleStringProperty title;
    
    /**
     * Popup constructor
     */
    public FilterMenuPopup(String title) 
    {
    	setHideOnEscape(true);
		setAutoHide(true);
		
    	// Listen for ESC key events; hide/cancel if one's caught
    	final EventHandler<KeyEvent> cancelEvent = new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					hide();
				}
			}
		};
		
		contentNode = new SimpleObjectProperty<>();
		
    	this.title = new SimpleStringProperty(title);
		
    	final Button sButton = new Button("Save");
    	saveButton = new SimpleObjectProperty<>(sButton);
    	sButton.getStyleClass().add("save-button");
    	sButton.addEventFilter(KeyEvent.KEY_PRESSED, cancelEvent);
    	sButton.setDefaultButton(true);
    	
    	final Button rButton = new Button("Reset");
    	resetButton = new SimpleObjectProperty<>(rButton);
    	rButton.getStyleClass().add("reset-button");
    	rButton.addEventFilter(KeyEvent.KEY_PRESSED, cancelEvent);
    	
    	final Button cButton = new Button("Cancel");
    	cancelButton = new SimpleObjectProperty<>(cButton);
    	cButton.getStyleClass().add("cancel-button");
    	cButton.addEventFilter(KeyEvent.KEY_PRESSED, cancelEvent);
    	cButton.setCancelButton(true);
    	cButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				hide();
			}
		});
    	
    	getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }
    
    public ObjectProperty<Node> contentNodeProperty() 
    {
        return contentNode;
    }
    
    /**
     * Set the content to display in the filter menu
     * @param value
     */
    public final void setContentNode(Node value) 
    {
        contentNodeProperty().set(value);
    }

    public final Node getContentNode() 
    {
        return contentNode.get();
    }
    
    public SimpleStringProperty titleProperty()
    {
    	return title;
    }
    
    public String getTitle()
    {
    	return title.get();
    }
    
    public void setTitle(String title)
    {
    	this.title.set(title);
    }
    
    public SimpleObjectProperty<Button> saveButtonProperty()
    {
    	return saveButton;
    }
    
    public Button getSaveButton()
    {
    	return saveButton.get();
    }
    
    public SimpleObjectProperty<Button> cancelButtonProperty()
    {
    	return saveButton;
    }
    
    public Button getCancelButton()
    {
    	return cancelButton.get();
    }
    
    public SimpleObjectProperty<Button> resetButtonProperty()
    {
    	return saveButton;
    }
    
    public Button getResetButton()
    {
    	return resetButton.get();
    }
    
    /**
     * Set the event to fire when the save button is pressed
     * @param event
     */
    public void setSaveEvent(EventHandler<ActionEvent> event)
    {
    	saveButton.get().setOnAction(event);
    }
    
    /**
     * Set the event to fire when the reset button is pressed
     * @param event
     */
    public void setResetEvent(EventHandler<ActionEvent> event)
    {
    	resetButton.get().setOnAction(event);
    }
    
    @Override
    protected void show() 
    {
        highlander();
        super.show();
    }

    @Override
    public void show(Window window) 
    {
        highlander();
        super.show(window);
    }

    @Override
    public void show(Window window, double d, double d1) 
    {
        highlander();
        super.show(window, d, d1);
    }
    
    @Override
    public void show(Node node, double d, double d1) 
    {
        highlander();
        super.show(node, d, d1);
    }
    
    @Override
    public void hide() 
    {
        if (currentlyVisibleMenu == this)
            currentlyVisibleMenu = null;
        super.hide();
    }
    
    /**
     * There can be only one... visible FilterMenuPopup
     */
    private void highlander()
    {
        if (currentlyVisibleMenu != null && currentlyVisibleMenu != this)
        {
            currentlyVisibleMenu.hide();
        }
        currentlyVisibleMenu = this;
    }
}