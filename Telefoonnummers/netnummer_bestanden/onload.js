//############################ #######################################
//## Switch radiobuttons    corrections/advertisers form            ##
//###################################################################

jQuery.noConflict();

(function($){
	
	$(document).ready(function(){
	    $('input.books')
	    .attr('checked','checked')
	    .click(function(){
	      $('#ads').slideUp('normal', function(){
	        $('#books').slideDown('normal');
	      });
	      
	    });

	    $('input.ads').click(function(){
	      $('#books').slideUp('normal', function(){
	        $('#ads').slideDown('normal');
	      });
	    });

	    $('input.books').attr('checked','checked');
	    $('#books').show()
	    if(document.correctionform){
		    document.correctionform.chkValueTwo.value="checked";
			document.correctionform.chkValueTwoSel.value = 'unchecked' ;
	    }
	    
	  });
	
	$(".js-checkbox-group").each(function(){
		
		var msgEN = "Select at least one of the options";
		var msgDK = "Select at least one of the options";
		
		var msg = /^(?:da)/i.test(lang) ? msgDK : msgEN;
		
		var group	= $("input[type=checkbox]", this);
		
		if(!$(this).data("ATTENTIONIMG")) {
			$(this).data("ATTENTIONIMG",$("<img src='"+contextPath+"/docroot/images/attentie_red_ico.gif' />").css({
				"position" : "absolute",
				"left" : "189px",
				"top" : "0",
				"width" : "16px",
				"height" : "15px"
			}));
			$(this).append($(this).data("ATTENTIONIMG"));
			$(this).data("ATTENTIONIMG").hide();
			$(this).data("ATTENTIONIMG").attr("alt",msg);
		}
		
		$(this).data("ATTENTIONIMG").mouseover(function(e){
			NetEffect.formToolTip.showTip(e);
		}).mouseout(function(e){
			NetEffect.formToolTip.hideTip(e);
		});
		
		var self 	= this;
		
		$(this).closest("form").submit(function(){
			for(var i=0,c;c=group[i];i++){
				if(c.checked){
					$(self).data("ATTENTIONIMG").hide();
					return true;
				}
			}
			
			$(self).data("ATTENTIONIMG").show();
			
			return false
		});
		
		
		
	})
	
})(jQuery);
  
 //###################################################################
//## Switch radiobuttons technical errors form             		    ##
//###################################################################
hideFields();
function hideFields()
{
	if(document.techform){
	    if(document.techform.type[6].checked)
	    {
	        document.getElementById('system').style.display = "";
	        document.getElementById('phone').style.display = 'none';
	    }
	    else
	    {
	        document.getElementById('system').style.display = 'none';
	        document.getElementById('phone').style.display = "";
	    }
	}
}

// ###################################################################
// ## FORMVALIDATOR (AUTOMATED UNOBTRUSIVE)							##
// ###################################################################
var execFormValidator = function()
{
	var formToolTip = NetEffect.formToolTip = new NetEffect.AltToolTip({
		"style" : {
			"border" : "1px solid #cecece",
			"fontWeight" : "bold"
		}
	});
	
	var preloadAttentionImg = new Image();
	preloadAttentionImg.src = (contextPath||"")+"/docroot/images/attentie_red_ico.gif";
	
	$$("form.js-validator").each(function(o){
		// Validator object
		o.validator = new NetEffect.FormValidator(o);
		// Custom Validator handlers
		// onValid
		o.validator.observe("valid",function(e){
			if(e.field.attentionImg) {
				e.field.parentNode.removeChild(e.field.attentionImg);
				e.field.attentionImg = null;
			}
		});
		
		var errorMessages = {
			// English
			"js-validate-string-en" : "Use only text here.",
			"js-validate-notempty-telephone-en" : "Telephone or mobile is a required field.",
			"js-validate-notempty-mobile-en" : "Mobile or telephone is a required field.",
			"js-validate-number-en" : "Use numeric values only.",
			"js-validate-email-en" : "Please fill out a valid email address",
			"js-validate-select-en" : "Select at least one of the options.",
			"js-validate-radio-en" : "Select one of the options",
			"js-validate-notempty-en" : "This is a required field.",
			"js-validate-number2-en" : "Use numeric values only.",
			"js-validate-captcha-true-en" : "Input doesn't match.",
			// polish
			"js-validate-string-pl" : "Użyj tylko tekst",
			"js-validate-number-pl" : "Użyj tylko wartości liczbowe.",
			"js-validate-email-pl" : "Wstaw prawidłowy adres e-mail",
			"js-validate-select-pl" : "Wybierz przynajmniej jedną z opcji",
			"js-validate-radio-pl" : "Wybierz jedną z opcji",
			"js-validate-notempty-pl" : "To pole jest wymagane",
			"js-validate-number2-pl" : "Użyj tylko wartości liczbowe.",
			// english again
			"js-validate-notempty-browser-en" : "Browser is a required field",
			"js-validate-notempty-OS-en" : "Operating system is a required field.",
			"js-validate-notempty-description-en" : "Description of patch is a required field.",
			"js-validate-notempty-name-en" : "Name is a required field.",
			"js-validate-notempty-address-en" : "Address is a required field.",
			"js-validate-postalcode-en" : "Postal code is required and must consist of 4 or 5 digits.",
			"js-validate-notempty-city-en" : "City is a required field.",
			"js-validate-notempty-e-mail-en" : "e-mail is a required field.",
			"js-validate-notempty-reason-for-referral-en" : "Reason-for-referral is a required field.",
			"js-validate-notempty-your-idea-or-comment-en" : "Your-idea-or-comment is a required field.",
			"js-validate-notempty-digits-telephone-en" : "Telephone is a required field and must consist 8 digits",
			"js-validate-notempty-branche-en" : "Line of trade is a required field",
			// danish
			"js-validate-notempty-browser-dk" : "Du skal indtaste hvilken browser du bruger(f.eks internet explorer 6.0)",
			"js-validate-notempty-OS-dk" : "Du skal indtaste hvilket operativsystem du benytter(f.eks Windows XP)",
			"js-validate-notempty-description-dk" : "Du skal komme med en beskrivelse af en evt. fejl ",
			"js-validate-notempty-name-dk" : "Du skal indtaste dit navn",
			"js-validate-notempty-address-dk" : "Du skal indtaste din adresse",
			"js-validate-notempty-postalcode-dk" : "Du skal indtaste dit postnummer",
			"js-validate-notempty-city-dk" : "Du skal indtaste den by du bor i",
			"js-validate-notempty-e-mail-dk" : "Du skal indtaste din email adresse",
			"js-validate-notempty-telephone-dk" : "Du skal indtaste dit telefonnummer",
			"js-validate-notempty-mobile-dk" : "Du skal som minimum indtaste dit telefonnummer eller dit mobilnummer",
			"js-validate-notempty-reason-for-referral-dk" : "Du skal komme med en beskrivelse af den rettelse du ønsker foretaget",
			"js-validate-notempty-your-idea-or-comment-dk" : "Du skal komme med en beskrivelse af den rettelse du ønsker foretaget",
			"js-validate-email-dk" : "Du skal indtaste din email adresse",
			"js-validate-string-dk" : "Du skal indtaste den by du bor i",
			"js-validate-postalcode-dk" : "Du skal indtaste dit postnummer",
			"js-validate-notempty-digits-telephone-dk" : "Du skal som minimum indtaste dit telefonnummer",
			"js-validate-notempty-branche-dk" : "Du skal indtaste din branche" 	
		}
		
		
		var setAttentionImg = function(field)
		{
			if(!field.attentionImg)
			{
				field.attentionImg = document.createElement("img");
				field.attentionImg.src = preloadAttentionImg.src;
				field.attentionImg = $(field.parentNode.insertBefore(field.attentionImg,field));
				field.attentionImg.setStyle({
					"position" : "relative",
					"margin" : "3px 0 0 -22px",
					"float" : "left",
					"zIndex" : "10",
					"width" : "16px",
					"height" : "15px"
				});
			}
		}
		
		o.validator.observe("done",function(e){
			var list = e.validator.formFields;
			var phone = [];
			var phonesFound = 0;
			var invalid = [];
			
			for(var i=0,c;c=list[i];i++)
			{
				if (/(?:telephone|mobile)/.test(c.id))
				{
					phonesFound++;
				}
				if(!c.isValid && /(?:telephone|mobile)/.test(c.id))
				{
					// we're gonna handle phonenumbers seperatly.
					phone.push(c);
				}
				else if(!c.isValid)
				{
					invalid.push(c);
				}
			}
			
			if(phone.length > 1 || phonesFound != 2)
			{
				invalid = invalid.concat(phone);
			}
			else if(phonesFound === 2 && phone.length === 1 && phone[0].attentionImg)
			{
				phone[0].parentNode.removeChild(phone[0].attentionImg);
				phone[0].attentionImg = null;
			}
			
			for(var i=0,c;c=invalid[i];i++)
			{
				setAttentionImg(c);
				for(var pattern in errorMessages)
				{
					if(c.hasClassName(pattern))
					{
						c.attentionImg.alt = errorMessages[pattern];
						$(c.attentionImg).observe("mouseover",formToolTip.showTip.bindAsEventListener(formToolTip));
						$(c.attentionImg).observe("mouseout",formToolTip.hideTip.bindAsEventListener(formToolTip));
					}
				}
			}
			
			if(invalid.length === 0)
			{
				e.validator.formObj.submit();
			}
			
		});
		
		
		// onValidateField
		o.validator.observe("fieldvalidated",function(e){
			if(!$(e.field.parentNode).visible() && !e.field.isValid) {
				if(e.field.attentionImg) {
					e.field.parentNode.removeChild(e.field.attentionImg);
					e.field.attentionImg = null;
				}
				e.validator.errors -=1;
			}
		});
		// onDone
		o.validator.observe("done",function(e){});
	});
};
//execFormValidator();
$(document).observe("dom:loaded",execFormValidator);


//###################################################################
//## FAQ (AUTOMATED UNOBTRUSIVE)				         		##
//###################################################################

$(document).observe("dom:loaded",function(){
	var list = $$("dl.faq");
		for(var i=0,c;c=list[i++];)
		{
			new Faq(c,0);
		}
	});

//###################################################################
//## Clear form category field static form				           ##
//###################################################################

function clear(name) {
    if(name == "category_2") {
      document.forms.update_form.category_2.value = "";
    }
  }

//###################################################################
//## Check checkboxen									           ##
//###################################################################


function checkCheckBoxes() {
	if (document.contact_representative.CHKBOX_1.checked == false &&
		document.contact_representative.CHKBOX_2.checked == false)
	{
		alert ('You didn\'t choose any of the checkboxes!');
		return false;
	}
}

function checkCheckBoxes_pl() {
	if (document.contact_representative.CHKBOX_1.checked == false &&
		document.contact_representative.CHKBOX_2.checked == false)
	{
		alert ('You didn\'t choose any of the checkboxes maar dan in het pools!');
		return false;
	}
}

