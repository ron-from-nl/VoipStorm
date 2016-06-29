var Faq = function(el, tog)
{
var dl = $((typeof el == "string") ? document.getElementById(id) : el);
var dt = dl.getElementsByTagName("dt");
var dd = dl.getElementsByTagName("dd");
var a = document.createElement("a");
a.id = "top";
dl.insertBefore(a, dt[0]);
for (i = 0; i < dt.length; i++){
$(dt[i]).observe("click",this.toggle.bind(this));
dt[i].className = "faqClosed";
dd[i].style.height = "0px";
dd[i].appendChild(this.createBackToTop());
}
//this.sa = new Array();
//this.sa[0] = dl.insertBefore(this.createShowAll(), dt[0]);
//this.sa[1] = dl.appendChild(this.createShowAll());
this.dt = dt;
this.dd = dd;
if (typeof tog == "number")
{
setTimeout(function(){
this.toggle(this.dt[tog]);
}.bind(this),500)
}
}
Faq.prototype = {
ID : "FaqObject",
DELTA: .1, 
DELAY: 25,
target: null,
createBackToTop: function(){
var a = document.createElement("a");
/* a.innerHTML = "BACK TO TOP";
a.className = "faqToTop";
a.href = "#top";*/
return a;
},
createShowAll: function(){
var a = document.createElement("a");
/* a.innerHTML = "Show All Answers";
a.className = "faqShow";
a.href = "";
a.onclick = this.toggleAll;*/
return a;
},
toggle: function(e){
var dt = e.nodeType ? e : e.target;
if (dt) {
var dd = $(dt).next("dd");
if (!$(dt).hasClassName("faqOpen"))
{
this.anim(dd, dd.scrollHeight, this.DELTA);
dt.className = "faqOpen";
var sw = true;
for (i = 0; i < this.dt.length; i++)
{
if (this.dt[i].className != "faqOpen")
sw = false;
}
/* if (sw) faq.toggleShowAll("Hide");*/
} 
else 
{
this.target = dt;
this.anim(dd, dd.scrollHeight, -this.DELTA, this.toggleClose.bind(this));
}
}
},
toggleClose: function(e){
this.target.className = "faqClosed";
/*faq.toggleShowAll("Show");*/
},
toggleAll: function(e){
var a = e.nodeType ? e : e.target;
if (a.className == "faqShow"){
this.toggleShowAll("Hide");
for (i = 0; i < this.dt.length; i++){
if (this.dt[i].className != "faqOpen"){
this.dt[i].className = "faqOpen";
this.anim(this.dd[i], this.dd[i].scrollHeight, .1);
}
}
} else {
this.toggleShowAll("Show");
for (i = 0; i < this.dt.length; i++){
this.dt[i].className = "faqClosed";
this.anim(this.dd[i], this.dd[i].scrollHeight, -.1);
}
}
return false;
},
/* toggleShowAll: function(showhide){
for (i = 0; i < faq.sa.length; i++){
this.sa[i].className = "faq" + showhide;
this.sa[i].innerHTML = showhide + " All Answers";
}
},*/
anim: function(el, scroll, delta, callback){
if ((parseInt(el.style.height) < scroll && delta > 0) || (parseInt(el.style.height) > 0 && delta < 0)){
var height = parseInt(el.style.height) + scroll * delta;
if (height > scroll) 
height = scroll;
if (height < 0) 
height = 0;
el.style.height = height + "px"; 
if (height < scroll && height > 0) 
setTimeout(function(){ this.anim(el, scroll, delta, callback); }.bind(this), this.DELAY);
else if(typeof callback == "function") 
callback();
}
}
};

