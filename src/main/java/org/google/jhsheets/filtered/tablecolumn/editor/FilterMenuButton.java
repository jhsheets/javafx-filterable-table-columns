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

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.PopupControl;
import javafx.stage.Window;

/**
 * A button that controls displaying the filter menu when clicked
 * 
 * @author jhs
 *
 */
public class FilterMenuButton 
extends Button
{
	private final SimpleBooleanProperty active = new SimpleBooleanProperty();
	
	public FilterMenuButton(final FilterMenuPopup popup)
	{
		getStyleClass().add("filter-menu-button");
		
		// When the active propery is true, append an active class to this button
		active.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                if (newVal == Boolean.TRUE) {
                	FilterMenuButton.this.getStyleClass().add("active");
                } else {
                	FilterMenuButton.this.getStyleClass().remove("active");
                }
            }
        });
		
		// Toggle popup display when clicked
		setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (popup.isShowing())
				{
					popup.hide();
				}
				else
				{
					final Control c = (Control)event.getSource();
					final Bounds b = c.localToScene(c.getLayoutBounds());
					final PopupControl menu = popup;
					
					final Scene scene = c.getScene();
					final Window window = scene.getWindow();
					menu.show(c, window.getX() + scene.getX() + b.getMinX(), window.getY() + scene.getY() + b.getMaxY());
				}
			}
		});
		
	}
	
	public String getUserAgentStylesheet()
	{
		return FilterMenuButton.class.getResource(FilterMenuButton.class.getSimpleName() + ".css").toString();
	}
	
	public SimpleBooleanProperty activeProperty()
	{
		return active;
	}
	
	public void setActive(boolean b)
	{
		active.set(b);
	}
	
	public boolean isActive()
	{
		return active.get();
	}
}
