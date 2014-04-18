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

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Skin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class FilterMenuPopupSkin extends StackPane implements Skin<FilterMenuPopup>
{
    private FilterMenuPopup popup;
    
    public FilterMenuPopupSkin(FilterMenuPopup popup)
    {
        this.popup = popup;

        final ContentStack contentStack = new ContentStack(popup.getContentNode());
        getChildren().add(contentStack);
        
        idProperty().bind( popup.idProperty() );
        styleProperty().bind( popup.styleProperty() );
        getStyleClass().setAll( popup.getStyleClass() );
    }

    @Override
    public FilterMenuPopup getSkinnable() 
    {
        return popup;
    }

    @Override
    public Node getNode() 
    {
        return this;
    }

    @Override
    public void dispose() 
    {
        popup = null;
    }
    
    class ContentStack extends BorderPane 
    {
        public ContentStack(Node contentNode) 
        {
        	getStyleClass().add("content");
        	
        	final Label titleLabel = new Label();
        	titleLabel.textProperty().bind(popup.titleProperty());
        	
        	final StackPane topPane = new StackPane();
        	topPane.getChildren().addAll(new Separator(), titleLabel);
        	topPane.getStyleClass().add("top");
        	setTop(topPane);
        	
        	contentNode.getStyleClass().add("center");
        	setCenter(contentNode);
        	
            final HBox buttons = new HBox();
            buttons.getStyleClass().add("buttons");
            buttons.setPrefWidth(USE_COMPUTED_SIZE);
            buttons.setPrefHeight(USE_COMPUTED_SIZE);
            buttons.setSpacing(4);
            buttons.getChildren().addAll(popup.getSaveButton(), popup.getResetButton(), popup.getCancelButton());
            
            final VBox bottom = new VBox();
            bottom.getStyleClass().add("bottom");
            bottom.getChildren().addAll(new Separator(), buttons);
            setBottom(bottom);
        }
    }
}