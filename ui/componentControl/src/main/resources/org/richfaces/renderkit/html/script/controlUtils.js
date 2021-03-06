if (!window.Richfaces) {
window.Richfaces = {};
}

if (!Richfaces.componentControl) {
	Richfaces.componentControl = {};
}

Richfaces.componentControl.eachComponent = function(forAttr, callback) {
	jQuery(forAttr)
		.each(function() {
			if (this.component) {
				callback(this.component);
			}
		});
	
};

Richfaces.componentControl.applyDecorations = function (element, forAttr, decorationCode) {
	if (decorationCode) {
		decorationCode(element);
	}
	
	Richfaces.componentControl.eachComponent(forAttr, function(component) {
		if (component.applyDecoration) {
			component.applyDecoration(element);
		}
	});
	
};

Richfaces.componentControl.attachEvent = function(attachTo, aevent, forAttr, operation, params, disableDefault) {
	jQuery(attachTo).bind(Richfaces.effectEventOnOut(aevent),function(event) {
		Richfaces.componentControl.performOperation(event, aevent, forAttr, operation, params, disableDefault);
	}).each(function() {
		Richfaces.componentControl.applyDecorations(this, forAttr, function(element) {
			//TODO: handle component decoration
		});
	});
};
	
Richfaces.componentControl.attachAvailable = function(attachTo, aevent, forAttr, operation, params, disableDefault) {
	var ids = attachTo.split(',');
	for (var i = 0; i < ids.length; i++) {
		var id = ids[i];
		Richfaces.onAvailable(id.replace(/^#|\\(:)/g, "$1"), function() {
			Richfaces.componentControl.attachEvent(id, aevent, forAttr, operation, params, disableDefault);
		});
	}
};
	
Richfaces.componentControl.attachReady = function(attachTo, aevent, forAttr, operation, params, disableDefault) {
	jQuery(document).ready(function() {
		Richfaces.componentControl.attachEvent(attachTo, aevent, forAttr, operation, params, disableDefault);
	});
};

Richfaces.componentControl.checkDisableDefault = function (ename, disableDefault) {
	if (disableDefault==undefined) {
		var en=ename.toLowerCase();
		return (en=="oncontextmenu" || en=="contextmenu" ? true : false);
	} else {
		return disableDefault;
	}
};

Richfaces.componentControl.performOperation = function(event, aevent, forAttr, operation, params, disableDefault) {
	
	// stop event before event isn't extended by prototype  
	if (Richfaces.componentControl.checkDisableDefault(aevent, disableDefault)) {
		var fixedEvent = jQuery.event.fix(event);
		fixedEvent.stopPropagation();
		fixedEvent.preventDefault();
	}
	
	Richfaces.componentControl.eachComponent(forAttr, function(component) {
		var paramsValue = params;
		if (typeof params == "function") {
			paramsValue = params();
		}
		
		component[operation](event, paramsValue);
	});
};


Richfaces.effectEventOnOut = function(ename) {
	return ename.substr(0,2) == 'on' ? ename.substr(2) : ename;
};
