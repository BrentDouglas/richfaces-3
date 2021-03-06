if (!window.Richfaces) window.Richfaces = {};
Richfaces.ComboBox = Class.create();


Richfaces.ComboBox.prototype = {
	
	//default values
	filterNewValues : true,
	defaultLabel : "",
	
	initialize: function(id, options) {
		options = options || {};
		Object.extend(this, options.fields);
		this.combobox = $(id); 
		this.comboValue = document.getElementById(id + "comboboxValue"); 
		this.field = document.getElementById(id + "comboboxField");  
		this.tempItem;

		this.BUTTON_WIDTH = 17; //px
		this.BUTTON_LEFT_BORDER = 1; //px
		this.BUTTON_RIGHT_BORDER = 1; //px

		this.classes = Richfaces.mergeStyles(options.userStyles,new Richfaces.ComboBoxStyles().getCommonStyles());
		
		
		this.button = document.getElementById(id + "comboboxButton");   
		this.buttonBG = document.getElementById(id + "comboBoxButtonBG");  
		
		this.setInputWidth();
		
		var listOptions = options.listOptions || {};
		listOptions.listWidth = listOptions.listWidth || this.getCurrentWidth();
		this.comboList = new Richfaces.ComboBoxList(id, this.filterNewValues, this.classes.combolist, listOptions, "comboboxField");
		if (Richfaces.browser.isIE6) {
			this.comboList.createIframe(this.comboList.listParent.parentNode, this.comboList.listWidth, id, 
										"rich-combobox-list-width rich-combobox-list-scroll rich-combobox-list-position");										
		}
	
		if (options.value) {
			var item = this.comboList.findItemBySubstr(options.value);
			if (item) {
				this.comboList.doSelectItem(item);
			}
			// RF-5056 
			this.comboValue.value = options.value; 
			
		} else {
			if (this.defaultLabel) {
				this.applyDefaultText();
			}
		}
		this.isSelection = true;
		if (this.onselected) {
			this.combobox.observe("rich:onselect", this.onselected);
		}
		if (this.disabled) {
			this.disable(); //TODO rename to 'disable'
		}
		
		this.combobox.component = this;
		this.initHandlers();
		this["rich:destructor"] = "destroy";
	},
	
	destroy: function () {
	
		Event.stopObserving(this.combobox, 'rich:onselect');
		
		Event.stopObserving(this.button, 'click');
		Event.stopObserving(this.button, 'mouseup');
		Event.stopObserving(this.button, 'mousedown');
		Event.stopObserving(this.button, 'mouseover');
		Event.stopObserving(this.button, 'mouseout');
		
		Event.stopObserving(this.field, 'keydown');
		Event.stopObserving(this.field, 'blur');
		Event.stopObserving(this.field, 'focus');
		Event.stopObserving(this.field, 'keyup');
		
		Event.stopObserving(this.comboList.listParent, 'mousedown');
		Event.stopObserving(this.comboList.listParent, 'mouseup');
		Event.stopObserving(this.comboList.listParent, 'mousemove');
		Event.stopObserving(this.comboList.listParent, 'click');
		
		this.comboValue = null;
		this.button = null;
		this.buttonBG = null;
		this.field = null;
		this.classes = null;
		
		this.comboList.hide()
		delete this.comboList;
		this.combobox.component = null;
		this.combobox = null;
	},
	
	initHandlers : function() {
		if (this.onselect) {
			Event.observe(this.combobox, "rich:onselect", this.onselect.bindAsEventListener(this));
		}
		
		Event.observe(this.button, "click", this.buttonClickHandler.bindAsEventListener(this));
		Event.observe(this.button, "mouseup", this.buttonMouseUpHandler.bindAsEventListener(this));
		Event.observe(this.button, "mousedown", this.buttonMousedownHandler.bindAsEventListener(this));
		Event.observe(this.button, "mouseover", this.buttonMouseOverHandler.bindAsEventListener(this));
		Event.observe(this.button, "mouseout", this.buttonMouseOutHandler.bindAsEventListener(this));
		
		Event.observe(this.field, "keydown", this.fieldKeyDownHandler.bindAsEventListener(this));
		Event.observe(this.field, "blur", this.fieldBlurHandler.bindAsEventListener(this));
		Event.observe(this.field, "focus", this.fieldFocusHandler.bindAsEventListener(this));
		Event.observe(this.field, "keyup", this.dataUpdating.bindAsEventListener(this));
		
		Event.observe(this.comboList.listParent, "mousedown", this.listMousedownHandler.bindAsEventListener(this));
		Event.observe(this.comboList.listParent, "mouseup", this.listMouseUpHandler.bindAsEventListener(this));
		Event.observe(this.comboList.listParent, "mousemove", this.listMouseMoveHandler.bindAsEventListener(this));
		Event.observe(this.comboList.listParent, "click", this.listClickHandler.bindAsEventListener(this));
	},
	
	setInputWidth : function() {
		var width;
        if (Richfaces.browser.isIE6) {
            width = parseInt(this.field.parentNode.style.width) - this.BUTTON_WIDTH; 
        } else {
            width = parseInt(this.field.parentNode.style.width)
                - parseInt(Element.getStyle(this.field, Richfaces.borders.l))
                - parseInt(Element.getStyle(this.field, Richfaces.paddings.l))
                - parseInt(Element.getStyle(this.field, Richfaces.paddings.r))
                - parseInt(Element.getStyle(this.field, Richfaces.borders.r));
            width -= this.buttonBG.offsetWidth ? this.buttonBG.offsetWidth : this.BUTTON_WIDTH;
        }
		this.field.style.width = width + "px";
	},
	
	buttonClickHandler : function(event) {
		if (this.comboList.visible()) {
			this.comboList.hideWithDelay();
		} else {
			this.comboList.createDefaultList();
			this.comboList.showWithDelay();
			if (this.comboList.selectedItem) {
				this.comboList.scrollingUpToItem(this.comboList.selectedItem);
			}
			this.comboList.isList = false;
		}
	},
	
	buttonMouseUpHandler : function(e) {
		this.buttonBG.className = "rich-combobox-font rich-combobox-button-background rich-combobox-button";
		this.button.className = this.classes.button.classes.active + " rich-combobox-button-hovered";
		
		var styleCss = this.classes.button.style;
		if(styleCss && !styleCss.active.blank()) {
			Element.setStyle(this.button,styleCss.active);
		}
		
		this.field.focus();	
	},
	
	buttonMousedownHandler : function(e) {
		this.buttonBG.className = "rich-combobox-font rich-combobox-button-pressed-background rich-combobox-button";
		this.button.className = this.classes.button.classes.active + " rich-combobox-button-hovered";
		
		var styleCss = this.classes.button.style;
		if(styleCss && !styleCss.active.blank()) {
			Element.setStyle(this.button,styleCss.active);
		}
		
		this.comboList.isList = true;
	},
	
	buttonMouseOverHandler : function(e) {
		var classCss = this.classes.button.classes;
		var iconStyles = this.classes.buttonicon.style;
		var styleCss = this.classes.button.style;
		if (this.isActive()) { 
			this.button.className= classCss.active + " " + classCss.hovered;
			
			if(styleCss && !styleCss.active.blank()) {
				Element.setStyle(this.button,styleCss.active);
			}
			
			if (iconStyles && !iconStyles.active.blank()) {
				Element.setStyle(this.button,{backgroundImage : iconStyles.active});
			}	
		
		} else {
			this.button.className = classCss.normal + " " + classCss.hovered;
			
			if(styleCss && !styleCss.normal.blank()) {
				Element.setStyle(this.button,styleCss.normal);
			}	
			
			if (iconStyles && !iconStyles.normal.blank()) {
				Element.setStyle(this.button,{backgroundImage : iconStyles.normal});
			}	
		}
	},
	
	buttonMouseOutHandler : function(e) {
		var classCss = this.classes.button.classes;
		var styleCss = this.classes.button.style;
		var iconStyles = this.classes.buttonicon.style;
		
		if (this.isActive()) { 
			this.button.className= classCss.active;
			
			if(styleCss && !styleCss.active.blank()) {
				Element.setStyle(this.button,styleCss.active);
			}
			
			if (iconStyles && !iconStyles.active.blank()) {
				Element.setStyle(this.button,{backgroundImage : iconStyles.active});
			}	
		} else {
			this.button.className = classCss.normal;
			
			if(styleCss && !styleCss.normal.blank()) {
				Element.setStyle(this.button,styleCss.normal);
			}	
			
			if (iconStyles && !iconStyles.normal.blank()) {
				Element.setStyle(this.button,{backgroundImage : iconStyles.normal});
			}	
		}
	},
	
	listMouseMoveHandler : function(event) {
		//changes item's decoration
		var item = event.target;
		if(Element.match(item,"span")) {
			if (item && this.tempItem != item ) {
				this.comboList.doActiveItem(item);
			}
			this.tempItem = item;
		}
		
	},
	
	listMousedownHandler : function(event) {
		//https://jira.jboss.org/jira/browse/RF-4050
		if (!Prototype.Browser.Firefox) {
			if (!Element.match(event.target,"span")) {
				this.clickOnScroll = true;
			}
		} 
		this.comboList.isList = true;
	},
	
	listMouseUpHandler : function(e) {
		//https://jira.jboss.org/jira/browse/RF-4050
		//if (window.getSelection) {
			//if (window.getSelection().getRangeAt(0).toString() != '') {
				this.field.focus();	
				this.comboList.isList = false;
			//}
		//}
	},
	
	listClickHandler : function(event) {
		this.isSelection = false;
		//FF
		//this.field.focus();
		this.setValue(true);
		this.comboList.hideWithDelay();
	},
	
	fieldKeyDownHandler : function(event) {
		switch (event.keyCode) {
			case Event.KEY_RETURN : 
				this.setValue(true);
				this.comboList.hideWithDelay();
				Event.stop(event); // It is necessary for a cancelling of sending form at selecting item
				break;
			case Event.KEY_DOWN : 
				this.comboList.moveActiveItem(event);
				break;
			case Event.KEY_UP :
				this.comboList.moveActiveItem(event);
				break; 
			case Event.KEY_ESC : 
				this.field.value = this.field.value; //field must lose focus
				this.comboList.hideWithDelay();
				break;
		}
	},
	
	fieldFocusHandler : function() {
		this.doActive();
		if ((this.field.value == this.defaultLabel) && (this.comboValue.value == "")) {
			this.field.value = "";
		} else {
			if (this.isSelection) {
				Richfaces.ComboBox.textboxSelect(this.field, 0, this.field.value.length);
			}
			this.isSelection = true;
		}
	},
	
	fieldBlurHandler : function(event) {
		if (!this.comboList.isList) {
			this.enable();
			var value = this.field.value; 
			if (value.length == 0) {
				this.applyDefaultText();
			} else {
				var item = this.comboList.findItemBySubstr(value);
				if (item) {
					this.comboList.doSelectItem(item);
				}
			}
			this.comboList.hideWithDelay();
			this.setValue(false);
		} else {
			this.doActive();
		}
		
		if (this.clickOnScroll) {
			//after clicking on scroll (IE)
			this.field.focus();
			this.comboList.isList = false;
			this.clickOnScroll = false;
		}
	},
	
	dataUpdating : function(event) {
		if (Richfaces.ComboBox.SPECIAL_KEYS.indexOf(event.keyCode) == -1) {
			if (this.filterNewValues) {
				this.comboList.hideWithDelay();
				this.comboList.dataFilter(this.field.value);
				if (this.comboList.getItems() && this.comboList.getItems().length != 0) {
					var isSearchSuccessful = true;
					this.comboList.showWithDelay();					
				}
			} else {
				if (!this.comboList.visible()) {
					this.comboList.createDefaultList();
					this.comboList.showWithDelay();
				}
				
				var item = this.comboList.findItemBySubstr(this.field.value);
				if (item) {
					this.comboList.doActiveItem(item);
					this.comboList.scrollingUpToItem(this.comboList.activeItem);
					isSearchSuccessful = true;
				}
			}
			
			if (this.isValueSet(event) && isSearchSuccessful) {
				var value = this.getActiveItemValue();
				if(value && this.directInputSuggestions) {
					this.doDirectSuggestion(value);
				}	
			}
			this.comboValue.value = this.field.value; 
		}
	},
	
	getActiveItemValue: function(){
		var value;
		if (this.comboList.activeItem) { 	
			value = jQuery(this.comboList.activeItem).text();
			value = value.replace(/\xA0/g," ").strip();
		}	
		return value;
	},
	
	doDirectSuggestion: function(value) {
		var startInd = this.field.value.length; 
		var endInd = value.length;
		this.field.value = value;
		Richfaces.ComboBox.textboxSelect(this.field, startInd, endInd);
	},
	
	wasTextDeleted : function(event) {
		if ((event.keyCode == Event.KEY_BACKSPACE) 
			|| (event.keyCode == Event.KEY_DELETE) 
			|| (event.ctrlKey && (event.keyCode == 88))) {
			return true;
		}
		return false;
	},
	
	isValueSet : function(event) {
		/* if (this.field.prevValue) {
			if (this.field.prevValue.toLowerCase() != this.field.value.toLowerCase()) {
				return true;
			}
			return false;
		}
		return true;*/
		if (this.wasTextDeleted(event) 
			|| (event.keyCode == 17) 
			|| event.altKey 
			|| event.ctrlKey 
			|| event.shiftKey) {
			return false;
		}
		return true;
	},
	
	setValue : function(toSetOnly) {
//
//		if(this.comboValue.value != value) {
//			Richfaces.invokeEvent(this.onchange, this.combobox, "onchange", {value:value});
//		}	
			
//		if (this.comboList.activeItem) {
//			
//			var value = jQuery(this.comboList.activeItem).text();
//			value = value.replace(/\xA0/g," ").strip();
		var value = this.getActiveItemValue();
		if(value && toSetOnly) {
//			var oV = this.field.value;
//			if (oV == value) {
//				if (Prototype.Browser.Gecko) {
//					this.field.value = "";
//					this.comboValue.value = "";
//				}
//			}
//			this.field.prevValue = value;
//			this.field.value = value;
//			this.comboValue.value = value;
			this.comboValue.value = value;
			this.comboList.doSelectItem(this.comboList.activeItem);
			this.combobox.fire("rich:onselect", {});
		}
//			else if (this.directInputSuggestions) {
//					var startInd = this.field.value.length; 
//					var endInd = value.length;
//					this.comboValue.value = value;
//					Richfaces.ComboBox.textboxSelect(this.field, startInd, endInd);
//			}
//		}
		
		var newValue = this.comboValue.value;
		var oldValue = this.field.prevValue;

		if(newValue && (newValue != oldValue)) {
			this.field.prevValue = newValue;
			this.field.value = newValue;
			Richfaces.invokeEvent(this.onchange, this.combobox, "onchange", {value:newValue});
		} else if (newValue && (newValue != this.field.value)) {
            // https://jira.jboss.org/jira/browse/RF-8200
            this.field.value = newValue;
        }
	},
	
	applyDefaultText : function() {
		this.field.className = this.classes.field.classes.disabled;
		this.field.value = this.defaultLabel;
		this.comboValue.value = "";
	},
	
	isActive : function() {
		return (this.field.className == this.classes.field.classes.active); 
	},
	
	doActive : function() {
		if (this.button.className.indexOf(this.classes.button.classes.hovered) != -1) {
			this.button.className = this.classes.button.classes.active + " " + this.classes.button.classes.hovered;
		} else {
			this.button.className = this.classes.button.classes.active ;
		}
		
		var iconStyles = this.classes.buttonicon.style;
		if (!iconStyles.active.blank()) {
			Element.setStyle(this.button, {backgroundImage:iconStyles.active});
		}	
				
		this.field.className = this.classes.field.classes.active;
		Element.setStyle(this.field, this.classes.field.style.active);
		 
		this.disabled = false;
	},
		
	disable : function() {
		this.button.className = this.classes.button.classes.disabled;
		this.buttonBG.className = this.classes.buttonbg.classes.disabled;
		this.field.className = this.classes.field.classes.disabled;
		Element.setStyle(this.field, this.classes.field.style.disabled);
		
		var styleCss =  this.classes.button.style;
		if(styleCss && !styleCss.disabled.blank()) {
			Element.setStyle(this.button, styleCss.disabled);
		}	
		
		var iconStyles = this.classes.buttonicon.style;
		if(iconStyles && !iconStyles.disabled.blank()) {
			Element.setStyle(this.button,{backgroundImage : iconStyles.disabled});
		}	
	
		this.button.disabled = true;
		this.field.disabled = true;
		
		this.disabled = true;
	},
	
	enable : function() {
		this.button.className = this.classes.button.classes.normal;
		this.buttonBG.className = this.classes.buttonbg.classes.normal;
		this.field.className = this.classes.field.classes.normal;
		var fieldStyles = this.classes.field.style.normal;
		Element.setStyle(this.field, fieldStyles);

		var iconStyles = this.classes.buttonicon.style;
		if(!iconStyles.normal.blank()) {
			Element.setStyle(this.button,{backgroundImage : iconStyles.normal});
		}	
		
		var styleCss =  this.classes.button.style;
		if(styleCss && !styleCss.normal.blank()) {
			Element.setStyle(this.button, styleCss.normal);
		}
						
		this.button.disabled = false;
		this.field.disabled = false;
		this.disabled = false;
	},
	
	doDisable : function() {
		this.disable();
	},
	
	doNormal : function() {
		this.enable();
	},
	
	getCurrentWidth : function() {
		return this.combobox.firstChild.offsetWidth;	
	},
	
	/**
	 * user's JavaScript API
	 */
	 showList : function() {
	 	if (this.disabled) {
	 		return;
	 	}
	 	this.field.focus();
	 	this.buttonClickHandler();
	 	//this.comboList.isList = false;
	 },
	 
	 hideList : function() {
	 	this.comboList.hideWithDelay();
	 }
};


Richfaces.ComboBox.textboxSelect = function(oTextbox, iStart, iEnd) {
   if (Prototype.Browser.IE) {
       var oRange = oTextbox.createTextRange();
       oRange.moveStart("character", iStart);
       oRange.moveEnd("character", -oTextbox.value.length + iEnd);      
       oRange.select();                                              
   } else if (Prototype.Browser.Gecko) {
       oTextbox.setSelectionRange(iStart, iEnd);
   } else {
   		//FIXME
   		oTextbox.setSelectionRange(iStart, iEnd);
   }                    
} 

Richfaces.ComboBox.getSelectedText = function(oTextbox) {
	if (window.getSelection) {
		return window.getSelection().text;
	} else if (document.selection) { 
		// should come last; Opera!
		return document.selection.createRange();
	} else {
		//TODO
	}
}

Richfaces.ComboBox.SPECIAL_KEYS = [
	Event.KEY_RETURN, Event.KEY_UP, Event.KEY_DOWN, Event.KEY_RIGHT, Event.KEY_LEFT, Event.KEY_ESC, Event.KEY_TAB, 16 /* vladimir claims 16 is shift key code */ 
]