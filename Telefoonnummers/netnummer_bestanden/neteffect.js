// ###################################################################
// ## NAMESPACE														##
// ###################################################################
NetEffect = {};
// Contains
// 1. FormValidator Class
// 2. AltToolTip Class


// ###################################################################
// ## FORMVALIDATOR CLASS											##
// ###################################################################
NetEffect.FormValidator = Class.create();
NetEffect.FormValidator.prototype = {
	"initialize" : function(formObj,params) {
		this._observers = {};
		this.formObj = $(formObj);
		this.formFields = this.formObj.getElements().select(function(o,i){
			return o.className.include("js-validate");
		});
		this.formObj.observe("submit",(function(e){
			Event.stop(e);
			this.validate();
		}).bind(this));
	},
	"observe" : function(type,observer) {
		// Events: valid, invalid, validatefield, done
		if(!this._observers[type])
			this._observers[type] = [observer];
		else
			this._observers[type].push(observer);
	},
	"handleObservers" : function(type,fld) {
		if(!!this._observers[type]) {
			this._observers[type].each(function(o){
				o.apply(null,[{"field":fld,validator:this}]);
			}.bind(this));
		}
	},
	"validate" : function() {
	
		var setInvalid = (function(o) {
			o.isValid = false;
			this.errors++;
			this.handleObservers("invalid",o);
		}).bind(this);
		
		var setValid = (function(o) {
			o.isValid = true;
			this.handleObservers("valid",o);
		}).bind(this);
		
		this.errors = 0;
		
		this.formFields.each((function(o,i){
			
			this.handleObservers("validatefield",o);
			var v = $F(o);
			
			if(o.hasClassName("js-validate-string-en"))
				( this.isText(v) && !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-number-en"))
				( this.isNumber(v) && !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-email-en"))
				( this.isEmail(v)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-select-en"))
				( !this.isEmpty(v)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-radio-en"))
				( this.isRadioSelected(o)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-number2-en"))
				( !this.isPhoneNumber(v) ) ? setValid(o) : setInvalid(o);
				
			if(o.hasClassName("js-validate-notempty-browser-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-OS-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-description-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-name-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-address-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-postalcode-en"))
				( !this.isEmpty(v) && this.isLength(v,4,5) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-city-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-e-mail-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-telephone-en"))
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-mobile-en"))
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-reason-for-referral-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);	
			if(o.hasClassName("js-validate-notempty-your-idea-or-comment-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);			
			if( o.hasClassName("js-validate-notempty-digits-telephone-en") )
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-branche-en"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);	
				
				
			if(o.hasClassName("js-validate-notempty-browser-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-OS-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-description-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-name-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-address-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-postalcode-dk"))
				( !this.isEmpty(v) && this.isLength(v,4,5) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-city-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-e-mail-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-telephone-dk"))
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-mobile-dk"))
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-reason-for-referral-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-your-idea-or-comment-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);	
			if(o.hasClassName("js-validate-notempty-digits-telephone-dk"))
				( !this.isEmpty(v) && this.isLength(v,8) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-email-dk"))
				( this.isEmail(v)) ? setValid(o) : setInvalid(o);	
			if(o.hasClassName("js-validate-string-dk"))
				( this.isText(v) && !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);	
			if(o.hasClassName("js-validate-notempty-branche-dk"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);		
				
				
	
			if(o.hasClassName("js-validate-string-pl"))
				( this.isText(v) && !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);	
			if(o.hasClassName("js-validate-number-pl"))
				( this.isNumber(v) && !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-email-pl"))
				( this.isEmail(v)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-select-pl"))
				( !this.isEmpty(v)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-radio-pl"))
				( this.isRadioSelected(o)) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-notempty-pl"))
				( !this.isEmpty(v) ) ? setValid(o) : setInvalid(o);
			if(o.hasClassName("js-validate-number2-pl"))
				( !this.isPhoneNumber(v) ) ? setValid(o) : setInvalid(o);
				
			
				
			this.handleObservers("fieldvalidated",o);
			
		}).bind(this));
		
		this.handleObservers("done",this.formObj);
		 
		if(this.errors == 0) {
			this.formObj.submit();
		}
		
	},
	// String Test functions, return true or false;
	"isText" : function(value){
		return /\D/.test(value) ? true : false;
	},
	"isNumber" : function(value){
		return !isNaN(value);
	},
	"isEmail" : function(value){
		if(!/[@]/.test(value)) return false;
		var bits = value.split("@");
		if(bits.length > 2 || bits.length <= 1) return false;
		if(!bits[1].include(".") && !/\D/.test(bits[1].split(".").last()) ) return false;
		return true;
	},
	"isEmpty" : function(value) {
		return value.replace(/\s/gi,"").length == 0 ? true : false;
	},
	"isRadioSelected" : function(control) {
		var n = $(control).getAttribute("name");
		var els = $$("input[name="+n+"]");
		for(var i=0;i<els.length;i+=1)
			if($F(els[i])) return true;
		return false;
	
	},	
	"isPhoneNumber" : function(value) {
		return isNaN(value);
//		(/[a-zA-Z]/.test(value)) return false;
//		if(value.length  < 7) return false;
//		return true;
	},
	"isLength" : function(value, min, max) {
		if(!isNaN(min) && value.length < min)
			return false;
		if(!isNaN(max) && value.length > max)
			return false;
		return true;
	}
	
	
}

// ###################################################################
// ## ALTTOOLTIP CLASS												##
// ###################################################################
NetEffect.AltToolTip = Class.create();
NetEffect.AltToolTip.prototype = {
	"initialize" : function(params) {
		this.box = document.createElement("div");
		this.box = $(document.body.appendChild(this.box));
		// Default style
		this.box.setStyle({
			"zIndex" : "10000",
			"background" : "#FFFFFF",
			"width" : "200px",
			"border" : "1px solid #000",
			"padding" : "2px"
		});
		Position.absolutize(this.box)
		this.box.hide();
		if(params) {
			params.style ? this.box.setStyle(params.style) : null;
			this.activeClassName = params.activeClassName ? params.activeClassName : null;
			this.inactiveClassName = params.inactiveClassName ? params.inactiveClassName : null;
		}
	},
	"showTip" : function(e){
		var obj = $(e.target);
		if(!obj.tttext) {
			obj.tttext = obj.alt;
			obj.alt = "";
		}
		if(obj.tttext.length > 0) {
			this.box.show();
			this.box.innerHTML = obj.tttext;
			obj.alt = "";
			
			var ch = self.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
			var cw = self.innerWidth || document.documentElement.clientWidth || document.body.clienWidth;
			var coords = Position.cumulativeOffset(obj);
			var x = (coords[0] + obj.getWidth());
			if(x > (cw/2))
				x = (x - this.box.getWidth()) - obj.getWidth();
			var y = (coords[1] + obj.getHeight());
			if(y < (ch/2))
				y = (y - this.box.getHeight()) - obj.getHeight();
			this.box.setStyle({
				"left" : x+"px",
				"top" : y+"px",
				"height" : "auto"
			});
			if(this.activeClassName)
				this.box.addClassName(this.activeClassName);
			if(this.inactiveClassName)
				this.box.removeClassName(this.inactiveClassName);
		}
	},
	"hideTip" : function(e) {
		this.box.innerHTML = "";
		this.box.hide();
		if(this.activeClassName)
			this.box.addClassName(this.inactiveClassName);
		if(this.inactiveClassName)
			this.box.removeClassName(this.activeClassName);
	}
}

