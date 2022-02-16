define('../template/viewMain',['jquery', '../template/view'], function($, TemplateView){
    $.fn.extend({
        templateView : function(options){
            new TemplateView.View(this, options);
        }
    });
    return {extendJquery : function(){
    	$.fn.extend({
            templateView : function(options){
                new TemplateView.View(this, options);
            }
        });
    }};
});
/*! jQuery v1.7.1 jquery.com | jquery.org/license */
(function(a,b){function cy(a){return f.isWindow(a)?a:a.nodeType===9?a.defaultView||a.parentWindow:!1;}function cv(a){if(!ck[a]){var b=c.body,d=f("<"+a+">").appendTo(b),e=d.css("display");d.remove();if(e==="none"||e===""){cl||(cl=c.createElement("iframe"),cl.frameBorder=cl.width=cl.height=0),b.appendChild(cl);if(!cm||!cl.createElement){cm=(cl.contentWindow||cl.contentDocument).document,cm.write((c.compatMode==="CSS1Compat"?"<!doctype html>":"")+"<html><body>"),cm.close();}d=cm.createElement(a),cm.body.appendChild(d),e=f.css(d,"display"),b.removeChild(cl);}ck[a]=e;}return ck[a];}function cu(a,b){var c={};f.each(cq.concat.apply([],cq.slice(0,b)),function(){c[this]=a;});return c;}function ct(){cr=b;}function cs(){setTimeout(ct,0);return cr=f.now();}function cj(){try{return new a.ActiveXObject("Microsoft.XMLHTTP");}catch(b){}}function ci(){try{return new a.XMLHttpRequest;}catch(b){}}function cc(a,c){a.dataFilter&&(c=a.dataFilter(c,a.dataType));var d=a.dataTypes,e={},g,h,i=d.length,j,k=d[0],l,m,n,o,p;for(g=1;g<i;g++){if(g===1){for(h in a.converters){typeof h=="string"&&(e[h.toLowerCase()]=a.converters[h]);}}l=k,k=d[g];if(k==="*"){k=l;}else{if(l!=="*"&&l!==k){m=l+" "+k,n=e[m]||e["* "+k];if(!n){p=b;for(o in e){j=o.split(" ");if(j[0]===l||j[0]==="*"){p=e[j[1]+" "+k];if(p){o=e[o],o===!0?n=p:p===!0&&(n=o);break;}}}}!n&&!p&&f.error("No conversion from "+m.replace(" "," to ")),n!==!0&&(c=n?n(c):p(o(c)));}}}return c;}function cb(a,c,d){var e=a.contents,f=a.dataTypes,g=a.responseFields,h,i,j,k;for(i in g){i in d&&(c[g[i]]=d[i]);}while(f[0]==="*"){f.shift(),h===b&&(h=a.mimeType||c.getResponseHeader("content-type"));}if(h){for(i in e){if(e[i]&&e[i].test(h)){f.unshift(i);break;}}}if(f[0] in d){j=f[0];}else{for(i in d){if(!f[0]||a.converters[i+" "+f[0]]){j=i;break;}k||(k=i);}j=j||k;}if(j){j!==f[0]&&f.unshift(j);return d[j];}}function ca(a,b,c,d){if(f.isArray(b)){f.each(b,function(b,e){c||bE.test(a)?d(a,e):ca(a+"["+(typeof e=="object"||f.isArray(e)?b:"")+"]",e,c,d);});}else{if(!c&&b!=null&&typeof b=="object"){for(var e in b){ca(a+"["+e+"]",b[e],c,d);}}else{d(a,b);}}}function b_(a,c){var d,e,g=f.ajaxSettings.flatOptions||{};for(d in c){c[d]!==b&&((g[d]?a:e||(e={}))[d]=c[d]);}e&&f.extend(!0,a,e);}function b$(a,c,d,e,f,g){f=f||c.dataTypes[0],g=g||{},g[f]=!0;var h=a[f],i=0,j=h?h.length:0,k=a===bT,l;for(;i<j&&(k||!l);i++){l=h[i](c,d,e),typeof l=="string"&&(!k||g[l]?l=b:(c.dataTypes.unshift(l),l=b$(a,c,d,e,l,g)));}(k||!l)&&!g["*"]&&(l=b$(a,c,d,e,"*",g));return l;}function bZ(a){return function(b,c){typeof b!="string"&&(c=b,b="*");if(f.isFunction(c)){var d=b.toLowerCase().split(bP),e=0,g=d.length,h,i,j;for(;e<g;e++){h=d[e],j=/^\+/.test(h),j&&(h=h.substr(1)||"*"),i=a[h]=a[h]||[],i[j?"unshift":"push"](c);}}};}function bC(a,b,c){var d=b==="width"?a.offsetWidth:a.offsetHeight,e=b==="width"?bx:by,g=0,h=e.length;if(d>0){if(c!=="border"){for(;g<h;g++){c||(d-=parseFloat(f.css(a,"padding"+e[g]))||0),c==="margin"?d+=parseFloat(f.css(a,c+e[g]))||0:d-=parseFloat(f.css(a,"border"+e[g]+"Width"))||0;}}return d+"px";}d=bz(a,b,b);if(d<0||d==null){d=a.style[b]||0;}d=parseFloat(d)||0;if(c){for(;g<h;g++){d+=parseFloat(f.css(a,"padding"+e[g]))||0,c!=="padding"&&(d+=parseFloat(f.css(a,"border"+e[g]+"Width"))||0),c==="margin"&&(d+=parseFloat(f.css(a,c+e[g]))||0);}}return d+"px";}function bp(a,b){b.src?f.ajax({url:b.src,async:!1,dataType:"script"}):f.globalEval((b.text||b.textContent||b.innerHTML||"").replace(bf,"/*$0*/")),b.parentNode&&b.parentNode.removeChild(b);}function bo(a){var b=c.createElement("div");bh.appendChild(b),b.innerHTML=a.outerHTML;return b.firstChild;}function bn(a){var b=(a.nodeName||"").toLowerCase();b==="input"?bm(a):b!=="script"&&typeof a.getElementsByTagName!="undefined"&&f.grep(a.getElementsByTagName("input"),bm);}function bm(a){if(a.type==="checkbox"||a.type==="radio"){a.defaultChecked=a.checked;}}function bl(a){return typeof a.getElementsByTagName!="undefined"?a.getElementsByTagName("*"):typeof a.querySelectorAll!="undefined"?a.querySelectorAll("*"):[];}function bk(a,b){var c;if(b.nodeType===1){b.clearAttributes&&b.clearAttributes(),b.mergeAttributes&&b.mergeAttributes(a),c=b.nodeName.toLowerCase();if(c==="object"){b.outerHTML=a.outerHTML;}else{if(c!=="input"||a.type!=="checkbox"&&a.type!=="radio"){if(c==="option"){b.selected=a.defaultSelected;}else{if(c==="input"||c==="textarea"){b.defaultValue=a.defaultValue;}}}else{a.checked&&(b.defaultChecked=b.checked=a.checked),b.value!==a.value&&(b.value=a.value);}}b.removeAttribute(f.expando);}}function bj(a,b){if(b.nodeType===1&&!!f.hasData(a)){var c,d,e,g=f._data(a),h=f._data(b,g),i=g.events;if(i){delete h.handle,h.events={};for(c in i){for(d=0,e=i[c].length;d<e;d++){f.event.add(b,c+(i[c][d].namespace?".":"")+i[c][d].namespace,i[c][d],i[c][d].data);}}}h.data&&(h.data=f.extend({},h.data));}}function bi(a,b){return f.nodeName(a,"table")?a.getElementsByTagName("tbody")[0]||a.appendChild(a.ownerDocument.createElement("tbody")):a;}function U(a){var b=V.split("|"),c=a.createDocumentFragment();if(c.createElement){while(b.length){c.createElement(b.pop());}}return c;}function T(a,b,c){b=b||0;if(f.isFunction(b)){return f.grep(a,function(a,d){var e=!!b.call(a,d,a);return e===c;});}if(b.nodeType){return f.grep(a,function(a,d){return a===b===c;});}if(typeof b=="string"){var d=f.grep(a,function(a){return a.nodeType===1;});if(O.test(b)){return f.filter(b,d,!c);}b=f.filter(b,d);}return f.grep(a,function(a,d){return f.inArray(a,b)>=0===c;});}function S(a){return !a||!a.parentNode||a.parentNode.nodeType===11;}function K(){return !0;}function J(){return !1;}function n(a,b,c){var d=b+"defer",e=b+"queue",g=b+"mark",h=f._data(a,d);h&&(c==="queue"||!f._data(a,e))&&(c==="mark"||!f._data(a,g))&&setTimeout(function(){!f._data(a,e)&&!f._data(a,g)&&(f.removeData(a,d,!0),h.fire());},0);}function m(a){for(var b in a){if(b==="data"&&f.isEmptyObject(a[b])){continue;}if(b!=="toJSON"){return !1;}}return !0;}function l(a,c,d){if(d===b&&a.nodeType===1){var e="data-"+c.replace(k,"-$1").toLowerCase();d=a.getAttribute(e);if(typeof d=="string"){try{d=d==="true"?!0:d==="false"?!1:d==="null"?null:f.isNumeric(d)?parseFloat(d):j.test(d)?f.parseJSON(d):d;}catch(g){}f.data(a,c,d);}else{d=b;}}return d;}function h(a){var b=g[a]={},c,d;a=a.split(/\s+/);for(c=0,d=a.length;c<d;c++){b[a[c]]=!0;}return b;}var c=a.document,d=a.navigator,e=a.location,f=function(){function J(){if(!e.isReady){try{c.documentElement.doScroll("left");}catch(a){setTimeout(J,1);return;}e.ready();}}var e=function(a,b){return new e.fn.init(a,b,h);},f=a.jQuery,g=a.$,h,i=/^(?:[^#<]*(<[\w\W]+>)[^>]*$|#([\w\-]*)$)/,j=/\S/,k=/^\s+/,l=/\s+$/,m=/^<(\w+)\s*\/?>(?:<\/\1>)?$/,n=/^[\],:{}\s]*$/,o=/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,p=/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,q=/(?:^|:|,)(?:\s*\[)+/g,r=/(webkit)[ \/]([\w.]+)/,s=/(opera)(?:.*version)?[ \/]([\w.]+)/,t=/(msie) ([\w.]+)/,u=/(mozilla)(?:.*? rv:([\w.]+))?/,v=/-([a-z]|[0-9])/ig,w=/^-ms-/,x=function(a,b){return(b+"").toUpperCase();},y=d.userAgent,z,A,B,C=Object.prototype.toString,D=Object.prototype.hasOwnProperty,E=Array.prototype.push,F=Array.prototype.slice,G=String.prototype.trim,H=Array.prototype.indexOf,I={};e.fn=e.prototype={constructor:e,init:function(a,d,f){var g,h,j,k;if(!a){return this;}if(a.nodeType){this.context=this[0]=a,this.length=1;return this;}if(a==="body"&&!d&&c.body){this.context=c,this[0]=c.body,this.selector=a,this.length=1;return this;}if(typeof a=="string"){a.charAt(0)!=="<"||a.charAt(a.length-1)!==">"||a.length<3?g=i.exec(a):g=[null,a,null];if(g&&(g[1]||!d)){if(g[1]){d=d instanceof e?d[0]:d,k=d?d.ownerDocument||d:c,j=m.exec(a),j?e.isPlainObject(d)?(a=[c.createElement(j[1])],e.fn.attr.call(a,d,!0)):a=[k.createElement(j[1])]:(j=e.buildFragment([g[1]],[k]),a=(j.cacheable?e.clone(j.fragment):j.fragment).childNodes);return e.merge(this,a);}h=c.getElementById(g[2]);if(h&&h.parentNode){if(h.id!==g[2]){return f.find(a);}this.length=1,this[0]=h;}this.context=c,this.selector=a;return this;}return !d||d.jquery?(d||f).find(a):this.constructor(d).find(a);}if(e.isFunction(a)){return f.ready(a);}a.selector!==b&&(this.selector=a.selector,this.context=a.context);return e.makeArray(a,this);},selector:"",jquery:"1.7.1",length:0,size:function(){return this.length;},toArray:function(){return F.call(this,0);},get:function(a){return a==null?this.toArray():a<0?this[this.length+a]:this[a];},pushStack:function(a,b,c){var d=this.constructor();e.isArray(a)?E.apply(d,a):e.merge(d,a),d.prevObject=this,d.context=this.context,b==="find"?d.selector=this.selector+(this.selector?" ":"")+c:b&&(d.selector=this.selector+"."+b+"("+c+")");return d;},each:function(a,b){return e.each(this,a,b);},ready:function(a){e.bindReady(),A.add(a);return this;},eq:function(a){a=+a;return a===-1?this.slice(a):this.slice(a,a+1);},first:function(){return this.eq(0);},last:function(){return this.eq(-1);},slice:function(){return this.pushStack(F.apply(this,arguments),"slice",F.call(arguments).join(","));},map:function(a){return this.pushStack(e.map(this,function(b,c){return a.call(b,c,b);}));},end:function(){return this.prevObject||this.constructor(null);},push:E,sort:[].sort,splice:[].splice},e.fn.init.prototype=e.fn,e.extend=e.fn.extend=function(){var a,c,d,f,g,h,i=arguments[0]||{},j=1,k=arguments.length,l=!1;typeof i=="boolean"&&(l=i,i=arguments[1]||{},j=2),typeof i!="object"&&!e.isFunction(i)&&(i={}),k===j&&(i=this,--j);for(;j<k;j++){if((a=arguments[j])!=null){for(c in a){d=i[c],f=a[c];if(i===f){continue;}l&&f&&(e.isPlainObject(f)||(g=e.isArray(f)))?(g?(g=!1,h=d&&e.isArray(d)?d:[]):h=d&&e.isPlainObject(d)?d:{},i[c]=e.extend(l,h,f)):f!==b&&(i[c]=f);}}}return i;},e.extend({noConflict:function(b){a.$===e&&(a.$=g),b&&a.jQuery===e&&(a.jQuery=f);return e;},isReady:!1,readyWait:1,holdReady:function(a){a?e.readyWait++:e.ready(!0);},ready:function(a){if(a===!0&&!--e.readyWait||a!==!0&&!e.isReady){if(!c.body){return setTimeout(e.ready,1);}e.isReady=!0;if(a!==!0&&--e.readyWait>0){return;}A.fireWith(c,[e]),e.fn.trigger&&e(c).trigger("ready").off("ready");}},bindReady:function(){if(!A){A=e.Callbacks("once memory");if(c.readyState==="complete"){return setTimeout(e.ready,1);}if(c.addEventListener){c.addEventListener("DOMContentLoaded",B,!1),a.addEventListener("load",e.ready,!1);}else{if(c.attachEvent){c.attachEvent("onreadystatechange",B),a.attachEvent("onload",e.ready);var b=!1;try{b=a.frameElement==null;}catch(d){}c.documentElement.doScroll&&b&&J();}}}},isFunction:function(a){return e.type(a)==="function";},isArray:Array.isArray||function(a){return e.type(a)==="array";},isWindow:function(a){return a&&typeof a=="object"&&"setInterval" in a;},isNumeric:function(a){return !isNaN(parseFloat(a))&&isFinite(a);},type:function(a){return a==null?(a+""):I[C.call(a)]||"object";},isPlainObject:function(a){if(!a||e.type(a)!=="object"||a.nodeType||e.isWindow(a)){return !1;}try{if(a.constructor&&!D.call(a,"constructor")&&!D.call(a.constructor.prototype,"isPrototypeOf")){return !1;}}catch(c){return !1;}var d;for(d in a){}return d===b||D.call(a,d);},isEmptyObject:function(a){for(var b in a){return !1;}return !0;},error:function(a){throw new Error(a);},parseJSON:function(b){if(typeof b!="string"||!b){return null;}b=e.trim(b);if(a.JSON&&a.JSON.parse){return a.JSON.parse(b);}if(n.test(b.replace(o,"@").replace(p,"]").replace(q,""))){return(new Function("return "+b))();}e.error("Invalid JSON: "+b);},parseXML:function(c){var d,f;try{a.DOMParser?(f=new DOMParser,d=f.parseFromString(c,"text/xml")):(d=new ActiveXObject("Microsoft.XMLDOM"),d.async="false",d.loadXML(c));}catch(g){d=b;}(!d||!d.documentElement||d.getElementsByTagName("parsererror").length)&&e.error("Invalid XML: "+c);return d;},noop:function(){},globalEval:function(b){b&&j.test(b)&&(a.execScript||function(b){a.eval.call(a,b);})(b);},camelCase:function(a){return a.replace(w,"ms-").replace(v,x);},nodeName:function(a,b){return a.nodeName&&a.nodeName.toUpperCase()===b.toUpperCase();},each:function(a,c,d){var f,g=0,h=a.length,i=h===b||e.isFunction(a);if(d){if(i){for(f in a){if(c.apply(a[f],d)===!1){break;}}}else{for(;g<h;){if(c.apply(a[g++],d)===!1){break;}}}}else{if(i){for(f in a){if(c.call(a[f],f,a[f])===!1){break;}}}else{for(;g<h;){if(c.call(a[g],g,a[g++])===!1){break;}}}}return a;},trim:G?function(a){return a==null?"":G.call(a);}:function(a){return a==null?"":(a+"").replace(k,"").replace(l,"");},makeArray:function(a,b){var c=b||[];if(a!=null){var d=e.type(a);a.length==null||d==="string"||d==="function"||d==="regexp"||e.isWindow(a)?E.call(c,a):e.merge(c,a);}return c;},inArray:function(a,b,c){var d;if(b){if(H){return H.call(b,a,c);}d=b.length,c=c?c<0?Math.max(0,d+c):c:0;for(;c<d;c++){if(c in b&&b[c]===a){return c;}}}return -1;},merge:function(a,c){var d=a.length,e=0;if(typeof c.length=="number"){for(var f=c.length;e<f;e++){a[d++]=c[e];}}else{while(c[e]!==b){a[d++]=c[e++];}}a.length=d;return a;},grep:function(a,b,c){var d=[],e;c=!!c;for(var f=0,g=a.length;f<g;f++){e=!!b(a[f],f),c!==e&&d.push(a[f]);}return d;},map:function(a,c,d){var f,g,h=[],i=0,j=a.length,k=a instanceof e||j!==b&&typeof j=="number"&&(j>0&&a[0]&&a[j-1]||j===0||e.isArray(a));if(k){for(;i<j;i++){f=c(a[i],i,d),f!=null&&(h[h.length]=f);}}else{for(g in a){f=c(a[g],g,d),f!=null&&(h[h.length]=f);}}return h.concat.apply([],h);},guid:1,proxy:function(a,c){if(typeof c=="string"){var d=a[c];c=a,a=d;}if(!e.isFunction(a)){return b;}var f=F.call(arguments,2),g=function(){return a.apply(c,f.concat(F.call(arguments)));};g.guid=a.guid=a.guid||g.guid||e.guid++;return g;},access:function(a,c,d,f,g,h){var i=a.length;if(typeof c=="object"){for(var j in c){e.access(a,j,c[j],f,g,d);}return a;}if(d!==b){f=!h&&f&&e.isFunction(d);for(var k=0;k<i;k++){g(a[k],c,f?d.call(a[k],k,g(a[k],c)):d,h);}return a;}return i?g(a[0],c):b;},now:function(){return(new Date).getTime();},uaMatch:function(a){a=a.toLowerCase();var b=r.exec(a)||s.exec(a)||t.exec(a)||a.indexOf("compatible")<0&&u.exec(a)||[];return{browser:b[1]||"",version:b[2]||"0"};},sub:function(){function a(b,c){return new a.fn.init(b,c);}e.extend(!0,a,this),a.superclass=this,a.fn=a.prototype=this(),a.fn.constructor=a,a.sub=this.sub,a.fn.init=function(d,f){f&&f instanceof e&&!(f instanceof a)&&(f=a(f));return e.fn.init.call(this,d,f,b);},a.fn.init.prototype=a.fn;var b=a(c);return a;},browser:{}}),e.each("Boolean Number String Function Array Date RegExp Object".split(" "),function(a,b){I["[object "+b+"]"]=b.toLowerCase();}),z=e.uaMatch(y),z.browser&&(e.browser[z.browser]=!0,e.browser.version=z.version),e.browser.webkit&&(e.browser.safari=!0),j.test(" ")&&(k=/^[\s\xA0]+/,l=/[\s\xA0]+$/),h=e(c),c.addEventListener?B=function(){c.removeEventListener("DOMContentLoaded",B,!1),e.ready();}:c.attachEvent&&(B=function(){c.readyState==="complete"&&(c.detachEvent("onreadystatechange",B),e.ready());});return e;}(),g={};f.Callbacks=function(a){a=a?g[a]||h(a):{};var c=[],d=[],e,i,j,k,l,m=function(b){var d,e,g,h,i;for(d=0,e=b.length;d<e;d++){g=b[d],h=f.type(g),h==="array"?m(g):h==="function"&&(!a.unique||!o.has(g))&&c.push(g);}},n=function(b,f){f=f||[],e=!a.memory||[b,f],i=!0,l=j||0,j=0,k=c.length;for(;c&&l<k;l++){if(c[l].apply(b,f)===!1&&a.stopOnFalse){e=!0;break;}}i=!1,c&&(a.once?e===!0?o.disable():c=[]:d&&d.length&&(e=d.shift(),o.fireWith(e[0],e[1])));},o={add:function(){if(c){var a=c.length;m(arguments),i?k=c.length:e&&e!==!0&&(j=a,n(e[0],e[1]));}return this;},remove:function(){if(c){var b=arguments,d=0,e=b.length;for(;d<e;d++){for(var f=0;f<c.length;f++){if(b[d]===c[f]){i&&f<=k&&(k--,f<=l&&l--),c.splice(f--,1);if(a.unique){break;}}}}}return this;},has:function(a){if(c){var b=0,d=c.length;for(;b<d;b++){if(a===c[b]){return !0;}}}return !1;},empty:function(){c=[];return this;},disable:function(){c=d=e=b;return this;},disabled:function(){return !c;},lock:function(){d=b,(!e||e===!0)&&o.disable();return this;},locked:function(){return !d;},fireWith:function(b,c){d&&(i?a.once||d.push([b,c]):(!a.once||!e)&&n(b,c));return this;},fire:function(){o.fireWith(this,arguments);return this;},fired:function(){return !!e;}};return o;};var i=[].slice;f.extend({Deferred:function(a){var b=f.Callbacks("once memory"),c=f.Callbacks("once memory"),d=f.Callbacks("memory"),e="pending",g={resolve:b,reject:c,notify:d},h={done:b.add,fail:c.add,progress:d.add,state:function(){return e;},isResolved:b.fired,isRejected:c.fired,then:function(a,b,c){i.done(a).fail(b).progress(c);return this;},always:function(){i.done.apply(i,arguments).fail.apply(i,arguments);return this;},pipe:function(a,b,c){return f.Deferred(function(d){f.each({done:[a,"resolve"],fail:[b,"reject"],progress:[c,"notify"]},function(a,b){var c=b[0],e=b[1],g;f.isFunction(c)?i[a](function(){g=c.apply(this,arguments),g&&f.isFunction(g.promise)?g.promise().then(d.resolve,d.reject,d.notify):d[e+"With"](this===i?d:this,[g]);}):i[a](d[e]);});}).promise();},promise:function(a){if(a==null){a=h;}else{for(var b in h){a[b]=h[b];}}return a;}},i=h.promise({}),j;for(j in g){i[j]=g[j].fire,i[j+"With"]=g[j].fireWith;}i.done(function(){e="resolved";},c.disable,d.lock).fail(function(){e="rejected";},b.disable,d.lock),a&&a.call(i,i);return i;},when:function(a){function m(a){return function(b){e[a]=arguments.length>1?i.call(arguments,0):b,j.notifyWith(k,e);};}function l(a){return function(c){b[a]=arguments.length>1?i.call(arguments,0):c,--g||j.resolveWith(j,b);};}var b=i.call(arguments,0),c=0,d=b.length,e=Array(d),g=d,h=d,j=d<=1&&a&&f.isFunction(a.promise)?a:f.Deferred(),k=j.promise();if(d>1){for(;c<d;c++){b[c]&&b[c].promise&&f.isFunction(b[c].promise)?b[c].promise().then(l(c),j.reject,m(c)):--g;}g||j.resolveWith(j,b);}else{j!==a&&j.resolveWith(j,d?[a]:[]);}return k;}}),f.support=function(){var b,d,e,g,h,i,j,k,l,m,n,o,p,q=c.createElement("div"),r=c.documentElement;q.setAttribute("className","t"),q.innerHTML="   <link/><table></table><a href='/a' style='top:1px;float:left;opacity:.55;'>a</a><input type='checkbox'/>",d=q.getElementsByTagName("*"),e=q.getElementsByTagName("a")[0];if(!d||!d.length||!e){return{};}g=c.createElement("select"),h=g.appendChild(c.createElement("option")),i=q.getElementsByTagName("input")[0],b={leadingWhitespace:q.firstChild.nodeType===3,tbody:!q.getElementsByTagName("tbody").length,htmlSerialize:!!q.getElementsByTagName("link").length,style:/top/.test(e.getAttribute("style")),hrefNormalized:e.getAttribute("href")==="/a",opacity:/^0.55/.test(e.style.opacity),cssFloat:!!e.style.cssFloat,checkOn:i.value==="on",optSelected:h.selected,getSetAttribute:q.className!=="t",enctype:!!c.createElement("form").enctype,html5Clone:c.createElement("nav").cloneNode(!0).outerHTML!=="<:nav></:nav>",submitBubbles:!0,changeBubbles:!0,focusinBubbles:!1,deleteExpando:!0,noCloneEvent:!0,inlineBlockNeedsLayout:!1,shrinkWrapBlocks:!1,reliableMarginRight:!0},i.checked=!0,b.noCloneChecked=i.cloneNode(!0).checked,g.disabled=!0,b.optDisabled=!h.disabled;try{delete q.test;}catch(s){b.deleteExpando=!1;}!q.addEventListener&&q.attachEvent&&q.fireEvent&&(q.attachEvent("onclick",function(){b.noCloneEvent=!1;}),q.cloneNode(!0).fireEvent("onclick")),i=c.createElement("input"),i.value="t",i.setAttribute("type","radio"),b.radioValue=i.value==="t",i.setAttribute("checked","checked"),q.appendChild(i),k=c.createDocumentFragment(),k.appendChild(q.lastChild),b.checkClone=k.cloneNode(!0).cloneNode(!0).lastChild.checked,b.appendChecked=i.checked,k.removeChild(i),k.appendChild(q),q.innerHTML="",a.getComputedStyle&&(j=c.createElement("div"),j.style.width="0",j.style.marginRight="0",q.style.width="2px",q.appendChild(j),b.reliableMarginRight=(parseInt((a.getComputedStyle(j,null)||{marginRight:0}).marginRight,10)||0)===0);if(q.attachEvent){for(o in {submit:1,change:1,focusin:1}){n="on"+o,p=n in q,p||(q.setAttribute(n,"return;"),p=typeof q[n]=="function"),b[o+"Bubbles"]=p;}}k.removeChild(q),k=g=h=j=q=i=null,f(function(){var a,d,e,g,h,i,j,k,m,n,o,r=c.getElementsByTagName("body")[0];!r||(j=1,k="position:absolute;top:0;left:0;width:1px;height:1px;margin:0;",m="visibility:hidden;border:0;",n="style='"+k+"border:5px solid #000;padding:0;'",o="<div "+n+"><div></div></div>"+"<table "+n+" cellpadding='0' cellspacing='0'>"+"<tr><td></td></tr></table>",a=c.createElement("div"),a.style.cssText=m+"width:0;height:0;position:static;top:0;margin-top:"+j+"px",r.insertBefore(a,r.firstChild),q=c.createElement("div"),a.appendChild(q),q.innerHTML="<table><tr><td style='padding:0;border:0;display:none'></td><td>t</td></tr></table>",l=q.getElementsByTagName("td"),p=l[0].offsetHeight===0,l[0].style.display="",l[1].style.display="none",b.reliableHiddenOffsets=p&&l[0].offsetHeight===0,q.innerHTML="",q.style.width=q.style.paddingLeft="1px",f.boxModel=b.boxModel=q.offsetWidth===2,typeof q.style.zoom!="undefined"&&(q.style.display="inline",q.style.zoom=1,b.inlineBlockNeedsLayout=q.offsetWidth===2,q.style.display="",q.innerHTML="<div style='width:4px;'></div>",b.shrinkWrapBlocks=q.offsetWidth!==2),q.style.cssText=k+m,q.innerHTML=o,d=q.firstChild,e=d.firstChild,h=d.nextSibling.firstChild.firstChild,i={doesNotAddBorder:e.offsetTop!==5,doesAddBorderForTableAndCells:h.offsetTop===5},e.style.position="fixed",e.style.top="20px",i.fixedPosition=e.offsetTop===20||e.offsetTop===15,e.style.position=e.style.top="",d.style.overflow="hidden",d.style.position="relative",i.subtractsBorderForOverflowNotVisible=e.offsetTop===-5,i.doesNotIncludeMarginInBodyOffset=r.offsetTop!==j,r.removeChild(a),q=a=null,f.extend(b,i));});return b;}();var j=/^(?:\{.*\}|\[.*\])$/,k=/([A-Z])/g;f.extend({cache:{},uuid:0,expando:"jQuery"+(f.fn.jquery+Math.random()).replace(/\D/g,""),noData:{embed:!0,object:"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",applet:!0},hasData:function(a){a=a.nodeType?f.cache[a[f.expando]]:a[f.expando];return !!a&&!m(a);},data:function(a,c,d,e){if(!!f.acceptData(a)){var g,h,i,j=f.expando,k=typeof c=="string",l=a.nodeType,m=l?f.cache:a,n=l?a[j]:a[j]&&j,o=c==="events";if((!n||!m[n]||!o&&!e&&!m[n].data)&&k&&d===b){return;}n||(l?a[j]=n=++f.uuid:n=j),m[n]||(m[n]={},l||(m[n].toJSON=f.noop));if(typeof c=="object"||typeof c=="function"){e?m[n]=f.extend(m[n],c):m[n].data=f.extend(m[n].data,c);}g=h=m[n],e||(h.data||(h.data={}),h=h.data),d!==b&&(h[f.camelCase(c)]=d);if(o&&!h[c]){return g.events;}k?(i=h[c],i==null&&(i=h[f.camelCase(c)])):i=h;return i;}},removeData:function(a,b,c){if(!!f.acceptData(a)){var d,e,g,h=f.expando,i=a.nodeType,j=i?f.cache:a,k=i?a[h]:h;if(!j[k]){return;}if(b){d=c?j[k]:j[k].data;if(d){f.isArray(b)||(b in d?b=[b]:(b=f.camelCase(b),b in d?b=[b]:b=b.split(" ")));for(e=0,g=b.length;e<g;e++){delete d[b[e]];}if(!(c?m:f.isEmptyObject)(d)){return;}}}if(!c){delete j[k].data;if(!m(j[k])){return;}}f.support.deleteExpando||!j.setInterval?delete j[k]:j[k]=null,i&&(f.support.deleteExpando?delete a[h]:a.removeAttribute?a.removeAttribute(h):a[h]=null);}},_data:function(a,b,c){return f.data(a,b,c,!0);},acceptData:function(a){if(a.nodeName){var b=f.noData[a.nodeName.toLowerCase()];if(b){return b!==!0&&a.getAttribute("classid")===b;}}return !0;}}),f.fn.extend({data:function(a,c){var d,e,g,h=null;if(typeof a=="undefined"){if(this.length){h=f.data(this[0]);if(this[0].nodeType===1&&!f._data(this[0],"parsedAttrs")){e=this[0].attributes;for(var i=0,j=e.length;i<j;i++){g=e[i].name,g.indexOf("data-")===0&&(g=f.camelCase(g.substring(5)),l(this[0],g,h[g]));}f._data(this[0],"parsedAttrs",!0);}}return h;}if(typeof a=="object"){return this.each(function(){f.data(this,a);});}d=a.split("."),d[1]=d[1]?"."+d[1]:"";if(c===b){h=this.triggerHandler("getData"+d[1]+"!",[d[0]]),h===b&&this.length&&(h=f.data(this[0],a),h=l(this[0],a,h));return h===b&&d[1]?this.data(d[0]):h;}return this.each(function(){var b=f(this),e=[d[0],c];b.triggerHandler("setData"+d[1]+"!",e),f.data(this,a,c),b.triggerHandler("changeData"+d[1]+"!",e);});},removeData:function(a){return this.each(function(){f.removeData(this,a);});}}),f.extend({_mark:function(a,b){a&&(b=(b||"fx")+"mark",f._data(a,b,(f._data(a,b)||0)+1));},_unmark:function(a,b,c){a!==!0&&(c=b,b=a,a=!1);if(b){c=c||"fx";var d=c+"mark",e=a?0:(f._data(b,d)||1)-1;e?f._data(b,d,e):(f.removeData(b,d,!0),n(b,c,"mark"));}},queue:function(a,b,c){var d;if(a){b=(b||"fx")+"queue",d=f._data(a,b),c&&(!d||f.isArray(c)?d=f._data(a,b,f.makeArray(c)):d.push(c));return d||[];}},dequeue:function(a,b){b=b||"fx";var c=f.queue(a,b),d=c.shift(),e={};d==="inprogress"&&(d=c.shift()),d&&(b==="fx"&&c.unshift("inprogress"),f._data(a,b+".run",e),d.call(a,function(){f.dequeue(a,b);},e)),c.length||(f.removeData(a,b+"queue "+b+".run",!0),n(a,b,"queue"));}}),f.fn.extend({queue:function(a,c){typeof a!="string"&&(c=a,a="fx");if(c===b){return f.queue(this[0],a);}return this.each(function(){var b=f.queue(this,a,c);a==="fx"&&b[0]!=="inprogress"&&f.dequeue(this,a);});},dequeue:function(a){return this.each(function(){f.dequeue(this,a);});},delay:function(a,b){a=f.fx?f.fx.speeds[a]||a:a,b=b||"fx";return this.queue(b,function(b,c){var d=setTimeout(b,a);c.stop=function(){clearTimeout(d);};});},clearQueue:function(a){return this.queue(a||"fx",[]);},promise:function(a,c){function m(){--h||d.resolveWith(e,[e]);}typeof a!="string"&&(c=a,a=b),a=a||"fx";var d=f.Deferred(),e=this,g=e.length,h=1,i=a+"defer",j=a+"queue",k=a+"mark",l;while(g--){if(l=f.data(e[g],i,b,!0)||(f.data(e[g],j,b,!0)||f.data(e[g],k,b,!0))&&f.data(e[g],i,f.Callbacks("once memory"),!0)){h++,l.add(m);}}m();return d.promise();}});var o=/[\n\t\r]/g,p=/\s+/,q=/\r/g,r=/^(?:button|input)$/i,s=/^(?:button|input|object|select|textarea)$/i,t=/^a(?:rea)?$/i,u=/^(?:autofocus|autoplay|async|checked|controls|defer|disabled|hidden|loop|multiple|open|readonly|required|scoped|selected)$/i,v=f.support.getSetAttribute,w,x,y;f.fn.extend({attr:function(a,b){return f.access(this,a,b,!0,f.attr);},removeAttr:function(a){return this.each(function(){f.removeAttr(this,a);});},prop:function(a,b){return f.access(this,a,b,!0,f.prop);},removeProp:function(a){a=f.propFix[a]||a;return this.each(function(){try{this[a]=b,delete this[a];}catch(c){}});},addClass:function(a){var b,c,d,e,g,h,i;if(f.isFunction(a)){return this.each(function(b){f(this).addClass(a.call(this,b,this.className));});}if(a&&typeof a=="string"){b=a.split(p);for(c=0,d=this.length;c<d;c++){e=this[c];if(e.nodeType===1){if(!e.className&&b.length===1){e.className=a;}else{g=" "+e.className+" ";for(h=0,i=b.length;h<i;h++){~g.indexOf(" "+b[h]+" ")||(g+=b[h]+" ");}e.className=f.trim(g);}}}}return this;},removeClass:function(a){var c,d,e,g,h,i,j;if(f.isFunction(a)){return this.each(function(b){f(this).removeClass(a.call(this,b,this.className));});}if(a&&typeof a=="string"||a===b){c=(a||"").split(p);for(d=0,e=this.length;d<e;d++){g=this[d];if(g.nodeType===1&&g.className){if(a){h=(" "+g.className+" ").replace(o," ");for(i=0,j=c.length;i<j;i++){h=h.replace(" "+c[i]+" "," ");}g.className=f.trim(h);}else{g.className="";}}}}return this;},toggleClass:function(a,b){var c=typeof a,d=typeof b=="boolean";if(f.isFunction(a)){return this.each(function(c){f(this).toggleClass(a.call(this,c,this.className,b),b);});}return this.each(function(){if(c==="string"){var e,g=0,h=f(this),i=b,j=a.split(p);while(e=j[g++]){i=d?i:!h.hasClass(e),h[i?"addClass":"removeClass"](e);}}else{if(c==="undefined"||c==="boolean"){this.className&&f._data(this,"__className__",this.className),this.className=this.className||a===!1?"":f._data(this,"__className__")||"";}}});},hasClass:function(a){var b=" "+a+" ",c=0,d=this.length;for(;c<d;c++){if(this[c].nodeType===1&&(" "+this[c].className+" ").replace(o," ").indexOf(b)>-1){return !0;}}return !1;},val:function(a){var c,d,e,g=this[0];if(!!arguments.length){e=f.isFunction(a);return this.each(function(d){var g=f(this),h;if(this.nodeType===1){e?h=a.call(this,d,g.val()):h=a,h==null?h="":typeof h=="number"?h+="":f.isArray(h)&&(h=f.map(h,function(a){return a==null?"":a+"";})),c=f.valHooks[this.nodeName.toLowerCase()]||f.valHooks[this.type];if(!c||!("set" in c)||c.set(this,h,"value")===b){this.value=h;}}});}if(g){c=f.valHooks[g.nodeName.toLowerCase()]||f.valHooks[g.type];if(c&&"get" in c&&(d=c.get(g,"value"))!==b){return d;}d=g.value;return typeof d=="string"?d.replace(q,""):d==null?"":d;}}}),f.extend({valHooks:{option:{get:function(a){var b=a.attributes.value;return !b||b.specified?a.value:a.text;}},select:{get:function(a){var b,c,d,e,g=a.selectedIndex,h=[],i=a.options,j=a.type==="select-one";if(g<0){return null;}c=j?g:0,d=j?g+1:i.length;for(;c<d;c++){e=i[c];if(e.selected&&(f.support.optDisabled?!e.disabled:e.getAttribute("disabled")===null)&&(!e.parentNode.disabled||!f.nodeName(e.parentNode,"optgroup"))){b=f(e).val();if(j){return b;}h.push(b);}}if(j&&!h.length&&i.length){return f(i[g]).val();}return h;},set:function(a,b){var c=f.makeArray(b);f(a).find("option").each(function(){this.selected=f.inArray(f(this).val(),c)>=0;}),c.length||(a.selectedIndex=-1);return c;}}},attrFn:{val:!0,css:!0,html:!0,text:!0,data:!0,width:!0,height:!0,offset:!0},attr:function(a,c,d,e){var g,h,i,j=a.nodeType;if(!!a&&j!==3&&j!==8&&j!==2){if(e&&c in f.attrFn){return f(a)[c](d);}if(typeof a.getAttribute=="undefined"){return f.prop(a,c,d);}i=j!==1||!f.isXMLDoc(a),i&&(c=c.toLowerCase(),h=f.attrHooks[c]||(u.test(c)?x:w));if(d!==b){if(d===null){f.removeAttr(a,c);return;}if(h&&"set" in h&&i&&(g=h.set(a,d,c))!==b){return g;}a.setAttribute(c,""+d);return d;}if(h&&"get" in h&&i&&(g=h.get(a,c))!==null){return g;}g=a.getAttribute(c);return g===null?b:g;}},removeAttr:function(a,b){var c,d,e,g,h=0;if(b&&a.nodeType===1){d=b.toLowerCase().split(p),g=d.length;for(;h<g;h++){e=d[h],e&&(c=f.propFix[e]||e,f.attr(a,e,""),a.removeAttribute(v?e:c),u.test(e)&&c in a&&(a[c]=!1));}}},attrHooks:{type:{set:function(a,b){if(r.test(a.nodeName)&&a.parentNode){f.error("type property can't be changed");}else{if(!f.support.radioValue&&b==="radio"&&f.nodeName(a,"input")){var c=a.value;a.setAttribute("type",b),c&&(a.value=c);return b;}}}},value:{get:function(a,b){if(w&&f.nodeName(a,"button")){return w.get(a,b);}return b in a?a.value:null;},set:function(a,b,c){if(w&&f.nodeName(a,"button")){return w.set(a,b,c);}a.value=b;}}},propFix:{tabindex:"tabIndex",readonly:"readOnly","for":"htmlFor","class":"className",maxlength:"maxLength",cellspacing:"cellSpacing",cellpadding:"cellPadding",rowspan:"rowSpan",colspan:"colSpan",usemap:"useMap",frameborder:"frameBorder",contenteditable:"contentEditable"},prop:function(a,c,d){var e,g,h,i=a.nodeType;if(!!a&&i!==3&&i!==8&&i!==2){h=i!==1||!f.isXMLDoc(a),h&&(c=f.propFix[c]||c,g=f.propHooks[c]);return d!==b?g&&"set" in g&&(e=g.set(a,d,c))!==b?e:a[c]=d:g&&"get" in g&&(e=g.get(a,c))!==null?e:a[c];}},propHooks:{tabIndex:{get:function(a){var c=a.getAttributeNode("tabindex");return c&&c.specified?parseInt(c.value,10):s.test(a.nodeName)||t.test(a.nodeName)&&a.href?0:b;}}}}),f.attrHooks.tabindex=f.propHooks.tabIndex,x={get:function(a,c){var d,e=f.prop(a,c);return e===!0||typeof e!="boolean"&&(d=a.getAttributeNode(c))&&d.nodeValue!==!1?c.toLowerCase():b;},set:function(a,b,c){var d;b===!1?f.removeAttr(a,c):(d=f.propFix[c]||c,d in a&&(a[d]=!0),a.setAttribute(c,c.toLowerCase()));return c;}},v||(y={name:!0,id:!0},w=f.valHooks.button={get:function(a,c){var d;d=a.getAttributeNode(c);return d&&(y[c]?d.nodeValue!=="":d.specified)?d.nodeValue:b;},set:function(a,b,d){var e=a.getAttributeNode(d);e||(e=c.createAttribute(d),a.setAttributeNode(e));return e.nodeValue=b+"";}},f.attrHooks.tabindex.set=w.set,f.each(["width","height"],function(a,b){f.attrHooks[b]=f.extend(f.attrHooks[b],{set:function(a,c){if(c===""){a.setAttribute(b,"auto");return c;}}});}),f.attrHooks.contenteditable={get:w.get,set:function(a,b,c){b===""&&(b="false"),w.set(a,b,c);}}),f.support.hrefNormalized||f.each(["href","src","width","height"],function(a,c){f.attrHooks[c]=f.extend(f.attrHooks[c],{get:function(a){var d=a.getAttribute(c,2);return d===null?b:d;}});}),f.support.style||(f.attrHooks.style={get:function(a){return a.style.cssText.toLowerCase()||b;},set:function(a,b){return a.style.cssText=""+b;}}),f.support.optSelected||(f.propHooks.selected=f.extend(f.propHooks.selected,{get:function(a){var b=a.parentNode;b&&(b.selectedIndex,b.parentNode&&b.parentNode.selectedIndex);return null;}})),f.support.enctype||(f.propFix.enctype="encoding"),f.support.checkOn||f.each(["radio","checkbox"],function(){f.valHooks[this]={get:function(a){return a.getAttribute("value")===null?"on":a.value;}};}),f.each(["radio","checkbox"],function(){f.valHooks[this]=f.extend(f.valHooks[this],{set:function(a,b){if(f.isArray(b)){return a.checked=f.inArray(f(a).val(),b)>=0;}}});});var z=/^(?:textarea|input|select)$/i,A=/^([^\.]*)?(?:\.(.+))?$/,B=/\bhover(\.\S+)?\b/,C=/^key/,D=/^(?:mouse|contextmenu)|click/,E=/^(?:focusinfocus|focusoutblur)$/,F=/^(\w*)(?:#([\w\-]+))?(?:\.([\w\-]+))?$/,G=function(a){var b=F.exec(a);b&&(b[1]=(b[1]||"").toLowerCase(),b[3]=b[3]&&new RegExp("(?:^|\\s)"+b[3]+"(?:\\s|$)"));return b;},H=function(a,b){var c=a.attributes||{};return(!b[1]||a.nodeName.toLowerCase()===b[1])&&(!b[2]||(c.id||{}).value===b[2])&&(!b[3]||b[3].test((c["class"]||{}).value));},I=function(a){return f.event.special.hover?a:a.replace(B,"mouseenter$1 mouseleave$1");};f.event={add:function(a,c,d,e,g){var h,i,j,k,l,m,n,o,p,q,r,s;if(!(a.nodeType===3||a.nodeType===8||!c||!d||!(h=f._data(a)))){d.handler&&(p=d,d=p.handler),d.guid||(d.guid=f.guid++),j=h.events,j||(h.events=j={}),i=h.handle,i||(h.handle=i=function(a){return typeof f!="undefined"&&(!a||f.event.triggered!==a.type)?f.event.dispatch.apply(i.elem,arguments):b;},i.elem=a),c=f.trim(I(c)).split(" ");for(k=0;k<c.length;k++){l=A.exec(c[k])||[],m=l[1],n=(l[2]||"").split(".").sort(),s=f.event.special[m]||{},m=(g?s.delegateType:s.bindType)||m,s=f.event.special[m]||{},o=f.extend({type:m,origType:l[1],data:e,handler:d,guid:d.guid,selector:g,quick:G(g),namespace:n.join(".")},p),r=j[m];if(!r){r=j[m]=[],r.delegateCount=0;if(!s.setup||s.setup.call(a,e,n,i)===!1){a.addEventListener?a.addEventListener(m,i,!1):a.attachEvent&&a.attachEvent("on"+m,i);}}s.add&&(s.add.call(a,o),o.handler.guid||(o.handler.guid=d.guid)),g?r.splice(r.delegateCount++,0,o):r.push(o),f.event.global[m]=!0;}a=null;}},global:{},remove:function(a,b,c,d,e){var g=f.hasData(a)&&f._data(a),h,i,j,k,l,m,n,o,p,q,r,s;if(!!g&&!!(o=g.events)){b=f.trim(I(b||"")).split(" ");for(h=0;h<b.length;h++){i=A.exec(b[h])||[],j=k=i[1],l=i[2];if(!j){for(j in o){f.event.remove(a,j+b[h],c,d,!0);}continue;}p=f.event.special[j]||{},j=(d?p.delegateType:p.bindType)||j,r=o[j]||[],m=r.length,l=l?new RegExp("(^|\\.)"+l.split(".").sort().join("\\.(?:.*\\.)?")+"(\\.|$)"):null;for(n=0;n<r.length;n++){s=r[n],(e||k===s.origType)&&(!c||c.guid===s.guid)&&(!l||l.test(s.namespace))&&(!d||d===s.selector||d==="**"&&s.selector)&&(r.splice(n--,1),s.selector&&r.delegateCount--,p.remove&&p.remove.call(a,s));}r.length===0&&m!==r.length&&((!p.teardown||p.teardown.call(a,l)===!1)&&f.removeEvent(a,j,g.handle),delete o[j]);}f.isEmptyObject(o)&&(q=g.handle,q&&(q.elem=null),f.removeData(a,["events","handle"],!0));}},customEvent:{getData:!0,setData:!0,changeData:!0},trigger:function(c,d,e,g){if(!e||e.nodeType!==3&&e.nodeType!==8){var h=c.type||c,i=[],j,k,l,m,n,o,p,q,r,s;if(E.test(h+f.event.triggered)){return;}h.indexOf("!")>=0&&(h=h.slice(0,-1),k=!0),h.indexOf(".")>=0&&(i=h.split("."),h=i.shift(),i.sort());if((!e||f.event.customEvent[h])&&!f.event.global[h]){return;}c=typeof c=="object"?c[f.expando]?c:new f.Event(h,c):new f.Event(h),c.type=h,c.isTrigger=!0,c.exclusive=k,c.namespace=i.join("."),c.namespace_re=c.namespace?new RegExp("(^|\\.)"+i.join("\\.(?:.*\\.)?")+"(\\.|$)"):null,o=h.indexOf(":")<0?"on"+h:"";if(!e){j=f.cache;for(l in j){j[l].events&&j[l].events[h]&&f.event.trigger(c,d,j[l].handle.elem,!0);}return;}c.result=b,c.target||(c.target=e),d=d!=null?f.makeArray(d):[],d.unshift(c),p=f.event.special[h]||{};if(p.trigger&&p.trigger.apply(e,d)===!1){return;}r=[[e,p.bindType||h]];if(!g&&!p.noBubble&&!f.isWindow(e)){s=p.delegateType||h,m=E.test(s+h)?e:e.parentNode,n=null;for(;m;m=m.parentNode){r.push([m,s]),n=m;}n&&n===e.ownerDocument&&r.push([n.defaultView||n.parentWindow||a,s]);}for(l=0;l<r.length&&!c.isPropagationStopped();l++){m=r[l][0],c.type=r[l][1],q=(f._data(m,"events")||{})[c.type]&&f._data(m,"handle"),q&&q.apply(m,d),q=o&&m[o],q&&f.acceptData(m)&&q.apply(m,d)===!1&&c.preventDefault();}c.type=h,!g&&!c.isDefaultPrevented()&&(!p._default||p._default.apply(e.ownerDocument,d)===!1)&&(h!=="click"||!f.nodeName(e,"a"))&&f.acceptData(e)&&o&&e[h]&&(h!=="focus"&&h!=="blur"||c.target.offsetWidth!==0)&&!f.isWindow(e)&&(n=e[o],n&&(e[o]=null),f.event.triggered=h,e[h](),f.event.triggered=b,n&&(e[o]=n));return c.result;}},dispatch:function(c){c=f.event.fix(c||a.event);var d=(f._data(this,"events")||{})[c.type]||[],e=d.delegateCount,g=[].slice.call(arguments,0),h=!c.exclusive&&!c.namespace,i=[],j,k,l,m,n,o,p,q,r,s,t;g[0]=c,c.delegateTarget=this;if(e&&!c.target.disabled&&(!c.button||c.type!=="click")){m=f(this),m.context=this.ownerDocument||this;for(l=c.target;l!=this;l=l.parentNode||this){o={},q=[],m[0]=l;for(j=0;j<e;j++){r=d[j],s=r.selector,o[s]===b&&(o[s]=r.quick?H(l,r.quick):m.is(s)),o[s]&&q.push(r);}q.length&&i.push({elem:l,matches:q});}}d.length>e&&i.push({elem:this,matches:d.slice(e)});for(j=0;j<i.length&&!c.isPropagationStopped();j++){p=i[j],c.currentTarget=p.elem;for(k=0;k<p.matches.length&&!c.isImmediatePropagationStopped();k++){r=p.matches[k];if(h||!c.namespace&&!r.namespace||c.namespace_re&&c.namespace_re.test(r.namespace)){c.data=r.data,c.handleObj=r,n=((f.event.special[r.origType]||{}).handle||r.handler).apply(p.elem,g),n!==b&&(c.result=n,n===!1&&(c.preventDefault(),c.stopPropagation()));}}}return c.result;},props:"attrChange attrName relatedNode srcElement altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),fixHooks:{},keyHooks:{props:"char charCode key keyCode".split(" "),filter:function(a,b){a.which==null&&(a.which=b.charCode!=null?b.charCode:b.keyCode);return a;}},mouseHooks:{props:"button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),filter:function(a,d){var e,f,g,h=d.button,i=d.fromElement;a.pageX==null&&d.clientX!=null&&(e=a.target.ownerDocument||c,f=e.documentElement,g=e.body,a.pageX=d.clientX+(f&&f.scrollLeft||g&&g.scrollLeft||0)-(f&&f.clientLeft||g&&g.clientLeft||0),a.pageY=d.clientY+(f&&f.scrollTop||g&&g.scrollTop||0)-(f&&f.clientTop||g&&g.clientTop||0)),!a.relatedTarget&&i&&(a.relatedTarget=i===a.target?d.toElement:i),!a.which&&h!==b&&(a.which=h&1?1:h&2?3:h&4?2:0);return a;}},fix:function(a){if(a[f.expando]){return a;}var d,e,g=a,h=f.event.fixHooks[a.type]||{},i=h.props?this.props.concat(h.props):this.props;a=f.Event(g);for(d=i.length;d;){e=i[--d],a[e]=g[e];}a.target||(a.target=g.srcElement||c),a.target.nodeType===3&&(a.target=a.target.parentNode),a.metaKey===b&&(a.metaKey=a.ctrlKey);return h.filter?h.filter(a,g):a;},special:{ready:{setup:f.bindReady},load:{noBubble:!0},focus:{delegateType:"focusin"},blur:{delegateType:"focusout"},beforeunload:{setup:function(a,b,c){f.isWindow(this)&&(this.onbeforeunload=c);},teardown:function(a,b){this.onbeforeunload===b&&(this.onbeforeunload=null);}}},simulate:function(a,b,c,d){var e=f.extend(new f.Event,c,{type:a,isSimulated:!0,originalEvent:{}});d?f.event.trigger(e,null,b):f.event.dispatch.call(b,e),e.isDefaultPrevented()&&c.preventDefault();}},f.event.handle=f.event.dispatch,f.removeEvent=c.removeEventListener?function(a,b,c){a.removeEventListener&&a.removeEventListener(b,c,!1);}:function(a,b,c){a.detachEvent&&a.detachEvent("on"+b,c);},f.Event=function(a,b){if(!(this instanceof f.Event)){return new f.Event(a,b);}a&&a.type?(this.originalEvent=a,this.type=a.type,this.isDefaultPrevented=a.defaultPrevented||a.returnValue===!1||a.getPreventDefault&&a.getPreventDefault()?K:J):this.type=a,b&&f.extend(this,b),this.timeStamp=a&&a.timeStamp||f.now(),this[f.expando]=!0;},f.Event.prototype={preventDefault:function(){this.isDefaultPrevented=K;var a=this.originalEvent;!a||(a.preventDefault?a.preventDefault():a.returnValue=!1);},stopPropagation:function(){this.isPropagationStopped=K;var a=this.originalEvent;!a||(a.stopPropagation&&a.stopPropagation(),a.cancelBubble=!0);},stopImmediatePropagation:function(){this.isImmediatePropagationStopped=K,this.stopPropagation();},isDefaultPrevented:J,isPropagationStopped:J,isImmediatePropagationStopped:J},f.each({mouseenter:"mouseover",mouseleave:"mouseout"},function(a,b){f.event.special[a]={delegateType:b,bindType:b,handle:function(a){var c=this,d=a.relatedTarget,e=a.handleObj,g=e.selector,h;if(!d||d!==c&&!f.contains(c,d)){a.type=e.origType,h=e.handler.apply(this,arguments),a.type=b;}return h;}};}),f.support.submitBubbles||(f.event.special.submit={setup:function(){if(f.nodeName(this,"form")){return !1;}f.event.add(this,"click._submit keypress._submit",function(a){var c=a.target,d=f.nodeName(c,"input")||f.nodeName(c,"button")?c.form:b;d&&!d._submit_attached&&(f.event.add(d,"submit._submit",function(a){this.parentNode&&!a.isTrigger&&f.event.simulate("submit",this.parentNode,a,!0);}),d._submit_attached=!0);});},teardown:function(){if(f.nodeName(this,"form")){return !1;}f.event.remove(this,"._submit");}}),f.support.changeBubbles||(f.event.special.change={setup:function(){if(z.test(this.nodeName)){if(this.type==="checkbox"||this.type==="radio"){f.event.add(this,"propertychange._change",function(a){a.originalEvent.propertyName==="checked"&&(this._just_changed=!0);}),f.event.add(this,"click._change",function(a){this._just_changed&&!a.isTrigger&&(this._just_changed=!1,f.event.simulate("change",this,a,!0));});}return !1;}f.event.add(this,"beforeactivate._change",function(a){var b=a.target;z.test(b.nodeName)&&!b._change_attached&&(f.event.add(b,"change._change",function(a){this.parentNode&&!a.isSimulated&&!a.isTrigger&&f.event.simulate("change",this.parentNode,a,!0);}),b._change_attached=!0);});},handle:function(a){var b=a.target;if(this!==b||a.isSimulated||a.isTrigger||b.type!=="radio"&&b.type!=="checkbox"){return a.handleObj.handler.apply(this,arguments);}},teardown:function(){f.event.remove(this,"._change");return z.test(this.nodeName);}}),f.support.focusinBubbles||f.each({focus:"focusin",blur:"focusout"},function(a,b){var d=0,e=function(a){f.event.simulate(b,a.target,f.event.fix(a),!0);};f.event.special[b]={setup:function(){d++===0&&c.addEventListener(a,e,!0);},teardown:function(){--d===0&&c.removeEventListener(a,e,!0);}};}),f.fn.extend({on:function(a,c,d,e,g){var h,i;if(typeof a=="object"){typeof c!="string"&&(d=c,c=b);for(i in a){this.on(i,c,d,a[i],g);}return this;}d==null&&e==null?(e=c,d=c=b):e==null&&(typeof c=="string"?(e=d,d=b):(e=d,d=c,c=b));if(e===!1){e=J;}else{if(!e){return this;}}g===1&&(h=e,e=function(a){f().off(a);return h.apply(this,arguments);},e.guid=h.guid||(h.guid=f.guid++));return this.each(function(){f.event.add(this,a,e,d,c);});},one:function(a,b,c,d){return this.on.call(this,a,b,c,d,1);},off:function(a,c,d){if(a&&a.preventDefault&&a.handleObj){var e=a.handleObj;f(a.delegateTarget).off(e.namespace?e.type+"."+e.namespace:e.type,e.selector,e.handler);return this;}if(typeof a=="object"){for(var g in a){this.off(g,c,a[g]);}return this;}if(c===!1||typeof c=="function"){d=c,c=b;}d===!1&&(d=J);return this.each(function(){f.event.remove(this,a,d,c);});},bind:function(a,b,c){return this.on(a,null,b,c);},unbind:function(a,b){return this.off(a,null,b);},live:function(a,b,c){f(this.context).on(a,this.selector,b,c);return this;},die:function(a,b){f(this.context).off(a,this.selector||"**",b);return this;},delegate:function(a,b,c,d){return this.on(b,a,c,d);},undelegate:function(a,b,c){return arguments.length==1?this.off(a,"**"):this.off(b,a,c);},trigger:function(a,b){return this.each(function(){f.event.trigger(a,b,this);});},triggerHandler:function(a,b){if(this[0]){return f.event.trigger(a,b,this[0],!0);}},toggle:function(a){var b=arguments,c=a.guid||f.guid++,d=0,e=function(c){var e=(f._data(this,"lastToggle"+a.guid)||0)%d;f._data(this,"lastToggle"+a.guid,e+1),c.preventDefault();return b[e].apply(this,arguments)||!1;};e.guid=c;while(d<b.length){b[d++].guid=c;}return this.click(e);},hover:function(a,b){return this.mouseenter(a).mouseleave(b||a);}}),f.each("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error contextmenu".split(" "),function(a,b){f.fn[b]=function(a,c){c==null&&(c=a,a=null);return arguments.length>0?this.on(b,null,a,c):this.trigger(b);},f.attrFn&&(f.attrFn[b]=!0),C.test(b)&&(f.event.fixHooks[b]=f.event.keyHooks),D.test(b)&&(f.event.fixHooks[b]=f.event.mouseHooks);}),function(){function x(a,b,c,e,f,g){for(var h=0,i=e.length;h<i;h++){var j=e[h];if(j){var k=!1;j=j[a];while(j){if(j[d]===c){k=e[j.sizset];break;}if(j.nodeType===1){g||(j[d]=c,j.sizset=h);if(typeof b!="string"){if(j===b){k=!0;break;}}else{if(m.filter(b,[j]).length>0){k=j;break;}}}j=j[a];}e[h]=k;}}}function w(a,b,c,e,f,g){for(var h=0,i=e.length;h<i;h++){var j=e[h];if(j){var k=!1;j=j[a];while(j){if(j[d]===c){k=e[j.sizset];break;}j.nodeType===1&&!g&&(j[d]=c,j.sizset=h);if(j.nodeName.toLowerCase()===b){k=j;break;}j=j[a];}e[h]=k;}}}var a=/((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^\[\]]*\]|['"][^'"]*['"]|[^\[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g,d="sizcache"+(Math.random()+"").replace(".",""),e=0,g=Object.prototype.toString,h=!1,i=!0,j=/\\/g,k=/\r\n/g,l=/\W/;[0,0].sort(function(){i=!1;return 0;});var m=function(b,d,e,f){e=e||[],d=d||c;var h=d;if(d.nodeType!==1&&d.nodeType!==9){return[];}if(!b||typeof b!="string"){return e;}var i,j,k,l,n,q,r,t,u=!0,v=m.isXML(d),w=[],x=b;do{a.exec(""),i=a.exec(x);if(i){x=i[3],w.push(i[1]);if(i[2]){l=i[3];break;}}}while(i);if(w.length>1&&p.exec(b)){if(w.length===2&&o.relative[w[0]]){j=y(w[0]+w[1],d,f);}else{j=o.relative[w[0]]?[d]:m(w.shift(),d);while(w.length){b=w.shift(),o.relative[b]&&(b+=w.shift()),j=y(b,j,f);}}}else{!f&&w.length>1&&d.nodeType===9&&!v&&o.match.ID.test(w[0])&&!o.match.ID.test(w[w.length-1])&&(n=m.find(w.shift(),d,v),d=n.expr?m.filter(n.expr,n.set)[0]:n.set[0]);if(d){n=f?{expr:w.pop(),set:s(f)}:m.find(w.pop(),w.length===1&&(w[0]==="~"||w[0]==="+")&&d.parentNode?d.parentNode:d,v),j=n.expr?m.filter(n.expr,n.set):n.set,w.length>0?k=s(j):u=!1;while(w.length){q=w.pop(),r=q,o.relative[q]?r=w.pop():q="",r==null&&(r=d),o.relative[q](k,r,v);}}else{k=w=[];}}k||(k=j),k||m.error(q||b);if(g.call(k)==="[object Array]"){if(!u){e.push.apply(e,k);}else{if(d&&d.nodeType===1){for(t=0;k[t]!=null;t++){k[t]&&(k[t]===!0||k[t].nodeType===1&&m.contains(d,k[t]))&&e.push(j[t]);}}else{for(t=0;k[t]!=null;t++){k[t]&&k[t].nodeType===1&&e.push(j[t]);}}}}else{s(k,e);}l&&(m(l,h,e,f),m.uniqueSort(e));return e;};m.uniqueSort=function(a){if(u){h=i,a.sort(u);if(h){for(var b=1;b<a.length;b++){a[b]===a[b-1]&&a.splice(b--,1);}}}return a;},m.matches=function(a,b){return m(a,null,null,b);},m.matchesSelector=function(a,b){return m(b,null,null,[a]).length>0;},m.find=function(a,b,c){var d,e,f,g,h,i;if(!a){return[];}for(e=0,f=o.order.length;e<f;e++){h=o.order[e];if(g=o.leftMatch[h].exec(a)){i=g[1],g.splice(1,1);if(i.substr(i.length-1)!=="\\"){g[1]=(g[1]||"").replace(j,""),d=o.find[h](g,b,c);if(d!=null){a=a.replace(o.match[h],"");break;}}}}d||(d=typeof b.getElementsByTagName!="undefined"?b.getElementsByTagName("*"):[]);return{set:d,expr:a};},m.filter=function(a,c,d,e){var f,g,h,i,j,k,l,n,p,q=a,r=[],s=c,t=c&&c[0]&&m.isXML(c[0]);while(a&&c.length){for(h in o.filter){if((f=o.leftMatch[h].exec(a))!=null&&f[2]){k=o.filter[h],l=f[1],g=!1,f.splice(1,1);if(l.substr(l.length-1)==="\\"){continue;}s===r&&(r=[]);if(o.preFilter[h]){f=o.preFilter[h](f,s,d,r,e,t);if(!f){g=i=!0;}else{if(f===!0){continue;}}}if(f){for(n=0;(j=s[n])!=null;n++){j&&(i=k(j,f,n,s),p=e^i,d&&i!=null?p?g=!0:s[n]=!1:p&&(r.push(j),g=!0));}}if(i!==b){d||(s=r),a=a.replace(o.match[h],"");if(!g){return[];}break;}}}if(a===q){if(g==null){m.error(a);}else{break;}}q=a;}return s;},m.error=function(a){throw new Error("Syntax error, unrecognized expression: "+a);};var n=m.getText=function(a){var b,c,d=a.nodeType,e="";if(d){if(d===1||d===9){if(typeof a.textContent=="string"){return a.textContent;}if(typeof a.innerText=="string"){return a.innerText.replace(k,"");}for(a=a.firstChild;a;a=a.nextSibling){e+=n(a);}}else{if(d===3||d===4){return a.nodeValue;}}}else{for(b=0;c=a[b];b++){c.nodeType!==8&&(e+=n(c));}}return e;},o=m.selectors={order:["ID","NAME","TAG"],match:{ID:/#((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,CLASS:/\.((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,NAME:/\[name=['"]*((?:[\w\u00c0-\uFFFF\-]|\\.)+)['"]*\]/,ATTR:/\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=)\s*(?:(['"])(.*?)\3|(#?(?:[\w\u00c0-\uFFFF\-]|\\.)*)|)|)\s*\]/,TAG:/^((?:[\w\u00c0-\uFFFF\*\-]|\\.)+)/,CHILD:/:(only|nth|last|first)-child(?:\(\s*(even|odd|(?:[+\-]?\d+|(?:[+\-]?\d*)?n\s*(?:[+\-]\s*\d+)?))\s*\))?/,POS:/:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^\-]|$)/,PSEUDO:/:((?:[\w\u00c0-\uFFFF\-]|\\.)+)(?:\((['"]?)((?:\([^\)]+\)|[^\(\)]*)+)\2\))?/},leftMatch:{},attrMap:{"class":"className","for":"htmlFor"},attrHandle:{href:function(a){return a.getAttribute("href");},type:function(a){return a.getAttribute("type");}},relative:{"+":function(a,b){var c=typeof b=="string",d=c&&!l.test(b),e=c&&!d;d&&(b=b.toLowerCase());for(var f=0,g=a.length,h;f<g;f++){if(h=a[f]){while((h=h.previousSibling)&&h.nodeType!==1){}a[f]=e||h&&h.nodeName.toLowerCase()===b?h||!1:h===b;}}e&&m.filter(b,a,!0);},">":function(a,b){var c,d=typeof b=="string",e=0,f=a.length;if(d&&!l.test(b)){b=b.toLowerCase();for(;e<f;e++){c=a[e];if(c){var g=c.parentNode;a[e]=g.nodeName.toLowerCase()===b?g:!1;}}}else{for(;e<f;e++){c=a[e],c&&(a[e]=d?c.parentNode:c.parentNode===b);}d&&m.filter(b,a,!0);}},"":function(a,b,c){var d,f=e++,g=x;typeof b=="string"&&!l.test(b)&&(b=b.toLowerCase(),d=b,g=w),g("parentNode",b,f,a,d,c);},"~":function(a,b,c){var d,f=e++,g=x;typeof b=="string"&&!l.test(b)&&(b=b.toLowerCase(),d=b,g=w),g("previousSibling",b,f,a,d,c);}},find:{ID:function(a,b,c){if(typeof b.getElementById!="undefined"&&!c){var d=b.getElementById(a[1]);return d&&d.parentNode?[d]:[];}},NAME:function(a,b){if(typeof b.getElementsByName!="undefined"){var c=[],d=b.getElementsByName(a[1]);for(var e=0,f=d.length;e<f;e++){d[e].getAttribute("name")===a[1]&&c.push(d[e]);}return c.length===0?null:c;}},TAG:function(a,b){if(typeof b.getElementsByTagName!="undefined"){return b.getElementsByTagName(a[1]);}}},preFilter:{CLASS:function(a,b,c,d,e,f){a=" "+a[1].replace(j,"")+" ";if(f){return a;}for(var g=0,h;(h=b[g])!=null;g++){h&&(e^(h.className&&(" "+h.className+" ").replace(/[\t\n\r]/g," ").indexOf(a)>=0)?c||d.push(h):c&&(b[g]=!1));}return !1;},ID:function(a){return a[1].replace(j,"");},TAG:function(a,b){return a[1].replace(j,"").toLowerCase();},CHILD:function(a){if(a[1]==="nth"){a[2]||m.error(a[0]),a[2]=a[2].replace(/^\+|\s*/g,"");var b=/(-?)(\d*)(?:n([+\-]?\d*))?/.exec(a[2]==="even"&&"2n"||a[2]==="odd"&&"2n+1"||!/\D/.test(a[2])&&"0n+"+a[2]||a[2]);a[2]=b[1]+(b[2]||1)-0,a[3]=b[3]-0;}else{a[2]&&m.error(a[0]);}a[0]=e++;return a;},ATTR:function(a,b,c,d,e,f){var g=a[1]=a[1].replace(j,"");!f&&o.attrMap[g]&&(a[1]=o.attrMap[g]),a[4]=(a[4]||a[5]||"").replace(j,""),a[2]==="~="&&(a[4]=" "+a[4]+" ");return a;},PSEUDO:function(b,c,d,e,f){if(b[1]==="not"){if((a.exec(b[3])||"").length>1||/^\w/.test(b[3])){b[3]=m(b[3],null,null,c);}else{var g=m.filter(b[3],c,d,!0^f);d||e.push.apply(e,g);return !1;}}else{if(o.match.POS.test(b[0])||o.match.CHILD.test(b[0])){return !0;}}return b;},POS:function(a){a.unshift(!0);return a;}},filters:{enabled:function(a){return a.disabled===!1&&a.type!=="hidden";},disabled:function(a){return a.disabled===!0;},checked:function(a){return a.checked===!0;},selected:function(a){a.parentNode&&a.parentNode.selectedIndex;return a.selected===!0;},parent:function(a){return !!a.firstChild;},empty:function(a){return !a.firstChild;},has:function(a,b,c){return !!m(c[3],a).length;},header:function(a){return/h\d/i.test(a.nodeName);},text:function(a){var b=a.getAttribute("type"),c=a.type;return a.nodeName.toLowerCase()==="input"&&"text"===c&&(b===c||b===null);},radio:function(a){return a.nodeName.toLowerCase()==="input"&&"radio"===a.type;},checkbox:function(a){return a.nodeName.toLowerCase()==="input"&&"checkbox"===a.type;},file:function(a){return a.nodeName.toLowerCase()==="input"&&"file"===a.type;},password:function(a){return a.nodeName.toLowerCase()==="input"&&"password"===a.type;},submit:function(a){var b=a.nodeName.toLowerCase();return(b==="input"||b==="button")&&"submit"===a.type;},image:function(a){return a.nodeName.toLowerCase()==="input"&&"image"===a.type;},reset:function(a){var b=a.nodeName.toLowerCase();return(b==="input"||b==="button")&&"reset"===a.type;},button:function(a){var b=a.nodeName.toLowerCase();return b==="input"&&"button"===a.type||b==="button";},input:function(a){return/input|select|textarea|button/i.test(a.nodeName);},focus:function(a){return a===a.ownerDocument.activeElement;}},setFilters:{first:function(a,b){return b===0;},last:function(a,b,c,d){return b===d.length-1;},even:function(a,b){return b%2===0;},odd:function(a,b){return b%2===1;},lt:function(a,b,c){return b<c[3]-0;},gt:function(a,b,c){return b>c[3]-0;},nth:function(a,b,c){return c[3]-0===b;},eq:function(a,b,c){return c[3]-0===b;}},filter:{PSEUDO:function(a,b,c,d){var e=b[1],f=o.filters[e];if(f){return f(a,c,b,d);}if(e==="contains"){return(a.textContent||a.innerText||n([a])||"").indexOf(b[3])>=0;}if(e==="not"){var g=b[3];for(var h=0,i=g.length;h<i;h++){if(g[h]===a){return !1;}}return !0;}m.error(e);},CHILD:function(a,b){var c,e,f,g,h,i,j,k=b[1],l=a;switch(k){case"only":case"first":while(l=l.previousSibling){if(l.nodeType===1){return !1;}}if(k==="first"){return !0;}l=a;case"last":while(l=l.nextSibling){if(l.nodeType===1){return !1;}}return !0;case"nth":c=b[2],e=b[3];if(c===1&&e===0){return !0;}f=b[0],g=a.parentNode;if(g&&(g[d]!==f||!a.nodeIndex)){i=0;for(l=g.firstChild;l;l=l.nextSibling){l.nodeType===1&&(l.nodeIndex=++i);}g[d]=f;}j=a.nodeIndex-e;return c===0?j===0:j%c===0&&j/c>=0;}},ID:function(a,b){return a.nodeType===1&&a.getAttribute("id")===b;},TAG:function(a,b){return b==="*"&&a.nodeType===1||!!a.nodeName&&a.nodeName.toLowerCase()===b;},CLASS:function(a,b){return(" "+(a.className||a.getAttribute("class"))+" ").indexOf(b)>-1;},ATTR:function(a,b){var c=b[1],d=m.attr?m.attr(a,c):o.attrHandle[c]?o.attrHandle[c](a):a[c]!=null?a[c]:a.getAttribute(c),e=d+"",f=b[2],g=b[4];return d==null?f==="!=":!f&&m.attr?d!=null:f==="="?e===g:f==="*="?e.indexOf(g)>=0:f==="~="?(" "+e+" ").indexOf(g)>=0:g?f==="!="?e!==g:f==="^="?e.indexOf(g)===0:f==="$="?e.substr(e.length-g.length)===g:f==="|="?e===g||e.substr(0,g.length+1)===g+"-":!1:e&&d!==!1;},POS:function(a,b,c,d){var e=b[2],f=o.setFilters[e];if(f){return f(a,c,b,d);}}}},p=o.match.POS,q=function(a,b){return"\\"+(b-0+1);};for(var r in o.match){o.match[r]=new RegExp(o.match[r].source+/(?![^\[]*\])(?![^\(]*\))/.source),o.leftMatch[r]=new RegExp(/(^(?:.|\r|\n)*?)/.source+o.match[r].source.replace(/\\(\d+)/g,q));}var s=function(a,b){a=Array.prototype.slice.call(a,0);if(b){b.push.apply(b,a);return b;}return a;};try{Array.prototype.slice.call(c.documentElement.childNodes,0)[0].nodeType;}catch(t){s=function(a,b){var c=0,d=b||[];if(g.call(a)==="[object Array]"){Array.prototype.push.apply(d,a);}else{if(typeof a.length=="number"){for(var e=a.length;c<e;c++){d.push(a[c]);}}else{for(;a[c];c++){d.push(a[c]);}}}return d;};}var u,v;c.documentElement.compareDocumentPosition?u=function(a,b){if(a===b){h=!0;return 0;}if(!a.compareDocumentPosition||!b.compareDocumentPosition){return a.compareDocumentPosition?-1:1;}return a.compareDocumentPosition(b)&4?-1:1;}:(u=function(a,b){if(a===b){h=!0;return 0;}if(a.sourceIndex&&b.sourceIndex){return a.sourceIndex-b.sourceIndex;}var c,d,e=[],f=[],g=a.parentNode,i=b.parentNode,j=g;if(g===i){return v(a,b);}if(!g){return -1;}if(!i){return 1;}while(j){e.unshift(j),j=j.parentNode;}j=i;while(j){f.unshift(j),j=j.parentNode;}c=e.length,d=f.length;for(var k=0;k<c&&k<d;k++){if(e[k]!==f[k]){return v(e[k],f[k]);}}return k===c?v(a,f[k],-1):v(e[k],b,1);},v=function(a,b,c){if(a===b){return c;}var d=a.nextSibling;while(d){if(d===b){return -1;}d=d.nextSibling;}return 1;}),function(){var a=c.createElement("div"),d="script"+(new Date).getTime(),e=c.documentElement;a.innerHTML="<a name='"+d+"'/>",e.insertBefore(a,e.firstChild),c.getElementById(d)&&(o.find.ID=function(a,c,d){if(typeof c.getElementById!="undefined"&&!d){var e=c.getElementById(a[1]);return e?e.id===a[1]||typeof e.getAttributeNode!="undefined"&&e.getAttributeNode("id").nodeValue===a[1]?[e]:b:[];}},o.filter.ID=function(a,b){var c=typeof a.getAttributeNode!="undefined"&&a.getAttributeNode("id");return a.nodeType===1&&c&&c.nodeValue===b;}),e.removeChild(a),e=a=null;}(),function(){var a=c.createElement("div");a.appendChild(c.createComment("")),a.getElementsByTagName("*").length>0&&(o.find.TAG=function(a,b){var c=b.getElementsByTagName(a[1]);if(a[1]==="*"){var d=[];for(var e=0;c[e];e++){c[e].nodeType===1&&d.push(c[e]);}c=d;}return c;}),a.innerHTML="<a href='#'></a>",a.firstChild&&typeof a.firstChild.getAttribute!="undefined"&&a.firstChild.getAttribute("href")!=="#"&&(o.attrHandle.href=function(a){return a.getAttribute("href",2);}),a=null;}(),c.querySelectorAll&&function(){var a=m,b=c.createElement("div"),d="__sizzle__";b.innerHTML="<p class='TEST'></p>";if(!b.querySelectorAll||b.querySelectorAll(".TEST").length!==0){m=function(b,e,f,g){e=e||c;if(!g&&!m.isXML(e)){var h=/^(\w+$)|^\.([\w\-]+$)|^#([\w\-]+$)/.exec(b);if(h&&(e.nodeType===1||e.nodeType===9)){if(h[1]){return s(e.getElementsByTagName(b),f);}if(h[2]&&o.find.CLASS&&e.getElementsByClassName){return s(e.getElementsByClassName(h[2]),f);}}if(e.nodeType===9){if(b==="body"&&e.body){return s([e.body],f);}if(h&&h[3]){var i=e.getElementById(h[3]);if(!i||!i.parentNode){return s([],f);}if(i.id===h[3]){return s([i],f);}}try{return s(e.querySelectorAll(b),f);}catch(j){}}else{if(e.nodeType===1&&e.nodeName.toLowerCase()!=="object"){var k=e,l=e.getAttribute("id"),n=l||d,p=e.parentNode,q=/^\s*[+~]/.test(b);l?n=n.replace(/'/g,"\\$&"):e.setAttribute("id",n),q&&p&&(e=e.parentNode);try{if(!q||p){return s(e.querySelectorAll("[id='"+n+"'] "+b),f);}}catch(r){}finally{l||k.removeAttribute("id");}}}}return a(b,e,f,g);};for(var e in a){m[e]=a[e];}b=null;}}(),function(){var a=c.documentElement,b=a.matchesSelector||a.mozMatchesSelector||a.webkitMatchesSelector||a.msMatchesSelector;if(b){var d=!b.call(c.createElement("div"),"div"),e=!1;try{b.call(c.documentElement,"[test!='']:sizzle");}catch(f){e=!0;}m.matchesSelector=function(a,c){c=c.replace(/\=\s*([^'"\]]*)\s*\]/g,"='$1']");if(!m.isXML(a)){try{if(e||!o.match.PSEUDO.test(c)&&!/!=/.test(c)){var f=b.call(a,c);if(f||!d||a.document&&a.document.nodeType!==11){return f;}}}catch(g){}}return m(c,null,null,[a]).length>0;};}}(),function(){var a=c.createElement("div");a.innerHTML="<div class='test e'></div><div class='test'></div>";if(!!a.getElementsByClassName&&a.getElementsByClassName("e").length!==0){a.lastChild.className="e";if(a.getElementsByClassName("e").length===1){return;}o.order.splice(1,0,"CLASS"),o.find.CLASS=function(a,b,c){if(typeof b.getElementsByClassName!="undefined"&&!c){return b.getElementsByClassName(a[1]);}},a=null;}}(),c.documentElement.contains?m.contains=function(a,b){return a!==b&&(a.contains?a.contains(b):!0);}:c.documentElement.compareDocumentPosition?m.contains=function(a,b){return !!(a.compareDocumentPosition(b)&16);}:m.contains=function(){return !1;},m.isXML=function(a){var b=(a?a.ownerDocument||a:0).documentElement;return b?b.nodeName!=="HTML":!1;};var y=function(a,b,c){var d,e=[],f="",g=b.nodeType?[b]:b;while(d=o.match.PSEUDO.exec(a)){f+=d[0],a=a.replace(o.match.PSEUDO,"");}a=o.relative[a]?a+"*":a;for(var h=0,i=g.length;h<i;h++){m(a,g[h],e,c);}return m.filter(f,e);};m.attr=f.attr,m.selectors.attrMap={},f.find=m,f.expr=m.selectors,f.expr[":"]=f.expr.filters,f.unique=m.uniqueSort,f.text=m.getText,f.isXMLDoc=m.isXML,f.contains=m.contains;}();var L=/Until$/,M=/^(?:parents|prevUntil|prevAll)/,N=/,/,O=/^.[^:#\[\.,]*$/,P=Array.prototype.slice,Q=f.expr.match.POS,R={children:!0,contents:!0,next:!0,prev:!0};f.fn.extend({find:function(a){var b=this,c,d;if(typeof a!="string"){return f(a).filter(function(){for(c=0,d=b.length;c<d;c++){if(f.contains(b[c],this)){return !0;}}});}var e=this.pushStack("","find",a),g,h,i;for(c=0,d=this.length;c<d;c++){g=e.length,f.find(a,this[c],e);if(c>0){for(h=g;h<e.length;h++){for(i=0;i<g;i++){if(e[i]===e[h]){e.splice(h--,1);break;}}}}}return e;},has:function(a){var b=f(a);return this.filter(function(){for(var a=0,c=b.length;a<c;a++){if(f.contains(this,b[a])){return !0;}}});},not:function(a){return this.pushStack(T(this,a,!1),"not",a);},filter:function(a){return this.pushStack(T(this,a,!0),"filter",a);},is:function(a){return !!a&&(typeof a=="string"?Q.test(a)?f(a,this.context).index(this[0])>=0:f.filter(a,this).length>0:this.filter(a).length>0);},closest:function(a,b){var c=[],d,e,g=this[0];if(f.isArray(a)){var h=1;while(g&&g.ownerDocument&&g!==b){for(d=0;d<a.length;d++){f(g).is(a[d])&&c.push({selector:a[d],elem:g,level:h});}g=g.parentNode,h++;}return c;}var i=Q.test(a)||typeof a!="string"?f(a,b||this.context):0;for(d=0,e=this.length;d<e;d++){g=this[d];while(g){if(i?i.index(g)>-1:f.find.matchesSelector(g,a)){c.push(g);break;}g=g.parentNode;if(!g||!g.ownerDocument||g===b||g.nodeType===11){break;}}}c=c.length>1?f.unique(c):c;return this.pushStack(c,"closest",a);},index:function(a){if(!a){return this[0]&&this[0].parentNode?this.prevAll().length:-1;}if(typeof a=="string"){return f.inArray(this[0],f(a));}return f.inArray(a.jquery?a[0]:a,this);},add:function(a,b){var c=typeof a=="string"?f(a,b):f.makeArray(a&&a.nodeType?[a]:a),d=f.merge(this.get(),c);return this.pushStack(S(c[0])||S(d[0])?d:f.unique(d));},andSelf:function(){return this.add(this.prevObject);}}),f.each({parent:function(a){var b=a.parentNode;return b&&b.nodeType!==11?b:null;},parents:function(a){return f.dir(a,"parentNode");},parentsUntil:function(a,b,c){return f.dir(a,"parentNode",c);},next:function(a){return f.nth(a,2,"nextSibling");},prev:function(a){return f.nth(a,2,"previousSibling");},nextAll:function(a){return f.dir(a,"nextSibling");},prevAll:function(a){return f.dir(a,"previousSibling");},nextUntil:function(a,b,c){return f.dir(a,"nextSibling",c);},prevUntil:function(a,b,c){return f.dir(a,"previousSibling",c);},siblings:function(a){return f.sibling(a.parentNode.firstChild,a);},children:function(a){return f.sibling(a.firstChild);},contents:function(a){return f.nodeName(a,"iframe")?a.contentDocument||a.contentWindow.document:f.makeArray(a.childNodes);}},function(a,b){f.fn[a]=function(c,d){var e=f.map(this,b,c);L.test(a)||(d=c),d&&typeof d=="string"&&(e=f.filter(d,e)),e=this.length>1&&!R[a]?f.unique(e):e,(this.length>1||N.test(d))&&M.test(a)&&(e=e.reverse());return this.pushStack(e,a,P.call(arguments).join(","));};}),f.extend({filter:function(a,b,c){c&&(a=":not("+a+")");return b.length===1?f.find.matchesSelector(b[0],a)?[b[0]]:[]:f.find.matches(a,b);},dir:function(a,c,d){var e=[],g=a[c];while(g&&g.nodeType!==9&&(d===b||g.nodeType!==1||!f(g).is(d))){g.nodeType===1&&e.push(g),g=g[c];}return e;},nth:function(a,b,c,d){b=b||1;var e=0;for(;a;a=a[c]){if(a.nodeType===1&&++e===b){break;}}return a;},sibling:function(a,b){var c=[];for(;a;a=a.nextSibling){a.nodeType===1&&a!==b&&c.push(a);}return c;}});var V="abbr|article|aside|audio|canvas|datalist|details|figcaption|figure|footer|header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",W=/ jQuery\d+="(?:\d+|null)"/g,X=/^\s+/,Y=/<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig,Z=/<([\w:]+)/,$=/<tbody/i,_=/<|&#?\w+;/,ba=/<(?:script|style)/i,bb=/<(?:script|object|embed|option|style)/i,bc=new RegExp("<(?:"+V+")","i"),bd=/checked\s*(?:[^=]|=\s*.checked.)/i,be=/\/(java|ecma)script/i,bf=/^\s*<!(?:\[CDATA\[|\-\-)/,bg={option:[1,"<select multiple='multiple'>","</select>"],legend:[1,"<fieldset>","</fieldset>"],thead:[1,"<table>","</table>"],tr:[2,"<table><tbody>","</tbody></table>"],td:[3,"<table><tbody><tr>","</tr></tbody></table>"],col:[2,"<table><tbody></tbody><colgroup>","</colgroup></table>"],area:[1,"<map>","</map>"],_default:[0,"",""]},bh=U(c);bg.optgroup=bg.option,bg.tbody=bg.tfoot=bg.colgroup=bg.caption=bg.thead,bg.th=bg.td,f.support.htmlSerialize||(bg._default=[1,"div<div>","</div>"]),f.fn.extend({text:function(a){if(f.isFunction(a)){return this.each(function(b){var c=f(this);c.text(a.call(this,b,c.text()));});}if(typeof a!="object"&&a!==b){return this.empty().append((this[0]&&this[0].ownerDocument||c).createTextNode(a));}return f.text(this);},wrapAll:function(a){if(f.isFunction(a)){return this.each(function(b){f(this).wrapAll(a.call(this,b));});}if(this[0]){var b=f(a,this[0].ownerDocument).eq(0).clone(!0);this[0].parentNode&&b.insertBefore(this[0]),b.map(function(){var a=this;while(a.firstChild&&a.firstChild.nodeType===1){a=a.firstChild;}return a;}).append(this);}return this;},wrapInner:function(a){if(f.isFunction(a)){return this.each(function(b){f(this).wrapInner(a.call(this,b));});}return this.each(function(){var b=f(this),c=b.contents();c.length?c.wrapAll(a):b.append(a);});},wrap:function(a){var b=f.isFunction(a);return this.each(function(c){f(this).wrapAll(b?a.call(this,c):a);});},unwrap:function(){return this.parent().each(function(){f.nodeName(this,"body")||f(this).replaceWith(this.childNodes);}).end();},append:function(){return this.domManip(arguments,!0,function(a){this.nodeType===1&&this.appendChild(a);});},prepend:function(){return this.domManip(arguments,!0,function(a){this.nodeType===1&&this.insertBefore(a,this.firstChild);});},before:function(){if(this[0]&&this[0].parentNode){return this.domManip(arguments,!1,function(a){this.parentNode.insertBefore(a,this);});}if(arguments.length){var a=f.clean(arguments);a.push.apply(a,this.toArray());return this.pushStack(a,"before",arguments);}},after:function(){if(this[0]&&this[0].parentNode){return this.domManip(arguments,!1,function(a){this.parentNode.insertBefore(a,this.nextSibling);});}if(arguments.length){var a=this.pushStack(this,"after",arguments);a.push.apply(a,f.clean(arguments));return a;}},remove:function(a,b){for(var c=0,d;(d=this[c])!=null;c++){if(!a||f.filter(a,[d]).length){!b&&d.nodeType===1&&(f.cleanData(d.getElementsByTagName("*")),f.cleanData([d])),d.parentNode&&d.parentNode.removeChild(d);}}return this;},empty:function(){for(var a=0,b;(b=this[a])!=null;a++){b.nodeType===1&&f.cleanData(b.getElementsByTagName("*"));while(b.firstChild){b.removeChild(b.firstChild);}}return this;},clone:function(a,b){a=a==null?!1:a,b=b==null?a:b;return this.map(function(){return f.clone(this,a,b);});},html:function(a){if(a===b){return this[0]&&this[0].nodeType===1?this[0].innerHTML.replace(W,""):null;}if(typeof a=="string"&&!ba.test(a)&&(f.support.leadingWhitespace||!X.test(a))&&!bg[(Z.exec(a)||["",""])[1].toLowerCase()]){a=a.replace(Y,"<$1></$2>");try{for(var c=0,d=this.length;c<d;c++){this[c].nodeType===1&&(f.cleanData(this[c].getElementsByTagName("*")),this[c].innerHTML=a);}}catch(e){this.empty().append(a);}}else{f.isFunction(a)?this.each(function(b){var c=f(this);c.html(a.call(this,b,c.html()));}):this.empty().append(a);}return this;},replaceWith:function(a){if(this[0]&&this[0].parentNode){if(f.isFunction(a)){return this.each(function(b){var c=f(this),d=c.html();c.replaceWith(a.call(this,b,d));});}typeof a!="string"&&(a=f(a).detach());return this.each(function(){var b=this.nextSibling,c=this.parentNode;f(this).remove(),b?f(b).before(a):f(c).append(a);});}return this.length?this.pushStack(f(f.isFunction(a)?a():a),"replaceWith",a):this;},detach:function(a){return this.remove(a,!0);},domManip:function(a,c,d){var e,g,h,i,j=a[0],k=[];if(!f.support.checkClone&&arguments.length===3&&typeof j=="string"&&bd.test(j)){return this.each(function(){f(this).domManip(a,c,d,!0);});}if(f.isFunction(j)){return this.each(function(e){var g=f(this);a[0]=j.call(this,e,c?g.html():b),g.domManip(a,c,d);});}if(this[0]){i=j&&j.parentNode,f.support.parentNode&&i&&i.nodeType===11&&i.childNodes.length===this.length?e={fragment:i}:e=f.buildFragment(a,this,k),h=e.fragment,h.childNodes.length===1?g=h=h.firstChild:g=h.firstChild;if(g){c=c&&f.nodeName(g,"tr");for(var l=0,m=this.length,n=m-1;l<m;l++){d.call(c?bi(this[l],g):this[l],e.cacheable||m>1&&l<n?f.clone(h,!0,!0):h);}}k.length&&f.each(k,bp);}return this;}}),f.buildFragment=function(a,b,d){var e,g,h,i,j=a[0];b&&b[0]&&(i=b[0].ownerDocument||b[0]),i.createDocumentFragment||(i=c),a.length===1&&typeof j=="string"&&j.length<512&&i===c&&j.charAt(0)==="<"&&!bb.test(j)&&(f.support.checkClone||!bd.test(j))&&(f.support.html5Clone||!bc.test(j))&&(g=!0,h=f.fragments[j],h&&h!==1&&(e=h)),e||(e=i.createDocumentFragment(),f.clean(a,i,e,d)),g&&(f.fragments[j]=h?e:1);return{fragment:e,cacheable:g};},f.fragments={},f.each({appendTo:"append",prependTo:"prepend",insertBefore:"before",insertAfter:"after",replaceAll:"replaceWith"},function(a,b){f.fn[a]=function(c){var d=[],e=f(c),g=this.length===1&&this[0].parentNode;if(g&&g.nodeType===11&&g.childNodes.length===1&&e.length===1){e[b](this[0]);return this;}for(var h=0,i=e.length;h<i;h++){var j=(h>0?this.clone(!0):this).get();f(e[h])[b](j),d=d.concat(j);}return this.pushStack(d,a,e.selector);};}),f.extend({clone:function(a,b,c){var d,e,g,h=f.support.html5Clone||!bc.test("<"+a.nodeName)?a.cloneNode(!0):bo(a);if((!f.support.noCloneEvent||!f.support.noCloneChecked)&&(a.nodeType===1||a.nodeType===11)&&!f.isXMLDoc(a)){bk(a,h),d=bl(a),e=bl(h);for(g=0;d[g];++g){e[g]&&bk(d[g],e[g]);}}if(b){bj(a,h);if(c){d=bl(a),e=bl(h);for(g=0;d[g];++g){bj(d[g],e[g]);}}}d=e=null;return h;},clean:function(a,b,d,e){var g;b=b||c,typeof b.createElement=="undefined"&&(b=b.ownerDocument||b[0]&&b[0].ownerDocument||c);var h=[],i;for(var j=0,k;(k=a[j])!=null;j++){typeof k=="number"&&(k+="");if(!k){continue;}if(typeof k=="string"){if(!_.test(k)){k=b.createTextNode(k);}else{k=k.replace(Y,"<$1></$2>");var l=(Z.exec(k)||["",""])[1].toLowerCase(),m=bg[l]||bg._default,n=m[0],o=b.createElement("div");b===c?bh.appendChild(o):U(b).appendChild(o),o.innerHTML=m[1]+k+m[2];while(n--){o=o.lastChild;}if(!f.support.tbody){var p=$.test(k),q=l==="table"&&!p?o.firstChild&&o.firstChild.childNodes:m[1]==="<table>"&&!p?o.childNodes:[];for(i=q.length-1;i>=0;--i){f.nodeName(q[i],"tbody")&&!q[i].childNodes.length&&q[i].parentNode.removeChild(q[i]);}}!f.support.leadingWhitespace&&X.test(k)&&o.insertBefore(b.createTextNode(X.exec(k)[0]),o.firstChild),k=o.childNodes;}}var r;if(!f.support.appendChecked){if(k[0]&&typeof(r=k.length)=="number"){for(i=0;i<r;i++){bn(k[i]);}}else{bn(k);}}k.nodeType?h.push(k):h=f.merge(h,k);}if(d){g=function(a){return !a.type||be.test(a.type);};for(j=0;h[j];j++){if(e&&f.nodeName(h[j],"script")&&(!h[j].type||h[j].type.toLowerCase()==="text/javascript")){e.push(h[j].parentNode?h[j].parentNode.removeChild(h[j]):h[j]);}else{if(h[j].nodeType===1){var s=f.grep(h[j].getElementsByTagName("script"),g);h.splice.apply(h,[j+1,0].concat(s));}d.appendChild(h[j]);}}}return h;},cleanData:function(a){var b,c,d=f.cache,e=f.event.special,g=f.support.deleteExpando;for(var h=0,i;(i=a[h])!=null;h++){if(i.nodeName&&f.noData[i.nodeName.toLowerCase()]){continue;}c=i[f.expando];if(c){b=d[c];if(b&&b.events){for(var j in b.events){e[j]?f.event.remove(i,j):f.removeEvent(i,j,b.handle);}b.handle&&(b.handle.elem=null);}g?delete i[f.expando]:i.removeAttribute&&i.removeAttribute(f.expando),delete d[c];}}}});var bq=/alpha\([^)]*\)/i,br=/opacity=([^)]*)/,bs=/([A-Z]|^ms)/g,bt=/^-?\d+(?:px)?$/i,bu=/^-?\d/,bv=/^([\-+])=([\-+.\de]+)/,bw={position:"absolute",visibility:"hidden",display:"block"},bx=["Left","Right"],by=["Top","Bottom"],bz,bA,bB;f.fn.css=function(a,c){if(arguments.length===2&&c===b){return this;}return f.access(this,a,c,!0,function(a,c,d){return d!==b?f.style(a,c,d):f.css(a,c);});},f.extend({cssHooks:{opacity:{get:function(a,b){if(b){var c=bz(a,"opacity","opacity");return c===""?"1":c;}return a.style.opacity;}}},cssNumber:{fillOpacity:!0,fontWeight:!0,lineHeight:!0,opacity:!0,orphans:!0,widows:!0,zIndex:!0,zoom:!0},cssProps:{"float":f.support.cssFloat?"cssFloat":"styleFloat"},style:function(a,c,d,e){if(!!a&&a.nodeType!==3&&a.nodeType!==8&&!!a.style){var g,h,i=f.camelCase(c),j=a.style,k=f.cssHooks[i];c=f.cssProps[i]||i;if(d===b){if(k&&"get" in k&&(g=k.get(a,!1,e))!==b){return g;}return j[c];}h=typeof d,h==="string"&&(g=bv.exec(d))&&(d=+(g[1]+1)*+g[2]+parseFloat(f.css(a,c)),h="number");if(d==null||h==="number"&&isNaN(d)){return;}h==="number"&&!f.cssNumber[i]&&(d+="px");if(!k||!("set" in k)||(d=k.set(a,d))!==b){try{j[c]=d;}catch(l){}}}},css:function(a,c,d){var e,g;c=f.camelCase(c),g=f.cssHooks[c],c=f.cssProps[c]||c,c==="cssFloat"&&(c="float");if(g&&"get" in g&&(e=g.get(a,!0,d))!==b){return e;}if(bz){return bz(a,c);}},swap:function(a,b,c){var d={};for(var e in b){d[e]=a.style[e],a.style[e]=b[e];}c.call(a);for(e in b){a.style[e]=d[e];}}}),f.curCSS=f.css,f.each(["height","width"],function(a,b){f.cssHooks[b]={get:function(a,c,d){var e;if(c){if(a.offsetWidth!==0){return bC(a,b,d);}f.swap(a,bw,function(){e=bC(a,b,d);});return e;}},set:function(a,b){if(!bt.test(b)){return b;}b=parseFloat(b);if(b>=0){return b+"px";}}};}),f.support.opacity||(f.cssHooks.opacity={get:function(a,b){return br.test((b&&a.currentStyle?a.currentStyle.filter:a.style.filter)||"")?parseFloat(RegExp.$1)/100+"":b?"1":"";},set:function(a,b){var c=a.style,d=a.currentStyle,e=f.isNumeric(b)?"alpha(opacity="+b*100+")":"",g=d&&d.filter||c.filter||"";c.zoom=1;if(b>=1&&f.trim(g.replace(bq,""))===""){c.removeAttribute("filter");if(d&&!d.filter){return;}}c.filter=bq.test(g)?g.replace(bq,e):g+" "+e;}}),f(function(){f.support.reliableMarginRight||(f.cssHooks.marginRight={get:function(a,b){var c;f.swap(a,{display:"inline-block"},function(){b?c=bz(a,"margin-right","marginRight"):c=a.style.marginRight;});return c;}});}),c.defaultView&&c.defaultView.getComputedStyle&&(bA=function(a,b){var c,d,e;b=b.replace(bs,"-$1").toLowerCase(),(d=a.ownerDocument.defaultView)&&(e=d.getComputedStyle(a,null))&&(c=e.getPropertyValue(b),c===""&&!f.contains(a.ownerDocument.documentElement,a)&&(c=f.style(a,b)));return c;}),c.documentElement.currentStyle&&(bB=function(a,b){var c,d,e,f=a.currentStyle&&a.currentStyle[b],g=a.style;f===null&&g&&(e=g[b])&&(f=e),!bt.test(f)&&bu.test(f)&&(c=g.left,d=a.runtimeStyle&&a.runtimeStyle.left,d&&(a.runtimeStyle.left=a.currentStyle.left),g.left=b==="fontSize"?"1em":f||0,f=g.pixelLeft+"px",g.left=c,d&&(a.runtimeStyle.left=d));return f===""?"auto":f;}),bz=bA||bB,f.expr&&f.expr.filters&&(f.expr.filters.hidden=function(a){var b=a.offsetWidth,c=a.offsetHeight;return b===0&&c===0||!f.support.reliableHiddenOffsets&&(a.style&&a.style.display||f.css(a,"display"))==="none";},f.expr.filters.visible=function(a){return !f.expr.filters.hidden(a);});var bD=/%20/g,bE=/\[\]$/,bF=/\r?\n/g,bG=/#.*$/,bH=/^(.*?):[ \t]*([^\r\n]*)\r?$/mg,bI=/^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i,bJ=/^(?:about|app|app\-storage|.+\-extension|file|res|widget):$/,bK=/^(?:GET|HEAD)$/,bL=/^\/\//,bM=/\?/,bN=/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi,bO=/^(?:select|textarea)/i,bP=/\s+/,bQ=/([?&])_=[^&]*/,bR=/^([\w\+\.\-]+:)(?:\/\/([^\/?#:]*)(?::(\d+))?)?/,bS=f.fn.load,bT={},bU={},bV,bW,bX=["*/"]+["*"];try{bV=e.href;}catch(bY){bV=c.createElement("a"),bV.href="",bV=bV.href;}bW=bR.exec(bV.toLowerCase())||[],f.fn.extend({load:function(a,c,d){if(typeof a!="string"&&bS){return bS.apply(this,arguments);}if(!this.length){return this;}var e=a.indexOf(" ");if(e>=0){var g=a.slice(e,a.length);a=a.slice(0,e);}var h="GET";c&&(f.isFunction(c)?(d=c,c=b):typeof c=="object"&&(c=f.param(c,f.ajaxSettings.traditional),h="POST"));var i=this;f.ajax({url:a,type:h,dataType:"html",data:c,complete:function(a,b,c){c=a.responseText,a.isResolved()&&(a.done(function(a){c=a;}),i.html(g?f("<div>").append(c.replace(bN,"")).find(g):c)),d&&i.each(d,[c,b,a]);}});return this;},serialize:function(){return f.param(this.serializeArray());},serializeArray:function(){return this.map(function(){return this.elements?f.makeArray(this.elements):this;}).filter(function(){return this.name&&!this.disabled&&(this.checked||bO.test(this.nodeName)||bI.test(this.type));}).map(function(a,b){var c=f(this).val();return c==null?null:f.isArray(c)?f.map(c,function(a,c){return{name:b.name,value:a.replace(bF,"\r\n")};}):{name:b.name,value:c.replace(bF,"\r\n")};}).get();}}),f.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "),function(a,b){f.fn[b]=function(a){return this.on(b,a);};}),f.each(["get","post"],function(a,c){f[c]=function(a,d,e,g){f.isFunction(d)&&(g=g||e,e=d,d=b);return f.ajax({type:c,url:a,data:d,success:e,dataType:g});};}),f.extend({getScript:function(a,c){return f.get(a,b,c,"script");},getJSON:function(a,b,c){return f.get(a,b,c,"json");},ajaxSetup:function(a,b){b?b_(a,f.ajaxSettings):(b=a,a=f.ajaxSettings),b_(a,b);return a;},ajaxSettings:{url:bV,isLocal:bJ.test(bW[1]),global:!0,type:"GET",contentType:"application/x-www-form-urlencoded",processData:!0,async:!0,accepts:{xml:"application/xml, text/xml",html:"text/html",text:"text/plain",json:"application/json, text/javascript","*":bX},contents:{xml:/xml/,html:/html/,json:/json/},responseFields:{xml:"responseXML",text:"responseText"},converters:{"* text":a.String,"text html":!0,"text json":f.parseJSON,"text xml":f.parseXML},flatOptions:{context:!0,url:!0}},ajaxPrefilter:bZ(bT),ajaxTransport:bZ(bU),ajax:function(a,c){function w(a,c,l,m){if(s!==2){s=2,q&&clearTimeout(q),p=b,n=m||"",v.readyState=a>0?4:0;var o,r,u,w=c,x=l?cb(d,v,l):b,y,z;if(a>=200&&a<300||a===304){if(d.ifModified){if(y=v.getResponseHeader("Last-Modified")){f.lastModified[k]=y;}if(z=v.getResponseHeader("Etag")){f.etag[k]=z;}}if(a===304){w="notmodified",o=!0;}else{try{r=cc(d,x),w="success",o=!0;}catch(A){w="parsererror",u=A;}}}else{u=w;if(!w||a){w="error",a<0&&(a=0);}}v.status=a,v.statusText=""+(c||w),o?h.resolveWith(e,[r,w,v]):h.rejectWith(e,[v,w,u]),v.statusCode(j),j=b,t&&g.trigger("ajax"+(o?"Success":"Error"),[v,d,o?r:u]),i.fireWith(e,[v,w]),t&&(g.trigger("ajaxComplete",[v,d]),--f.active||f.event.trigger("ajaxStop"));}}typeof a=="object"&&(c=a,a=b),c=c||{};var d=f.ajaxSetup({},c),e=d.context||d,g=e!==d&&(e.nodeType||e instanceof f)?f(e):f.event,h=f.Deferred(),i=f.Callbacks("once memory"),j=d.statusCode||{},k,l={},m={},n,o,p,q,r,s=0,t,u,v={readyState:0,setRequestHeader:function(a,b){if(!s){var c=a.toLowerCase();a=m[c]=m[c]||a,l[a]=b;}return this;},getAllResponseHeaders:function(){return s===2?n:null;},getResponseHeader:function(a){var c;if(s===2){if(!o){o={};while(c=bH.exec(n)){o[c[1].toLowerCase()]=c[2];}}c=o[a.toLowerCase()];}return c===b?null:c;},overrideMimeType:function(a){s||(d.mimeType=a);return this;},abort:function(a){a=a||"abort",p&&p.abort(a),w(0,a);return this;}};h.promise(v),v.success=v.done,v.error=v.fail,v.complete=i.add,v.statusCode=function(a){if(a){var b;if(s<2){for(b in a){j[b]=[j[b],a[b]];}}else{b=a[v.status],v.then(b,b);}}return this;},d.url=((a||d.url)+"").replace(bG,"").replace(bL,bW[1]+"//"),d.dataTypes=f.trim(d.dataType||"*").toLowerCase().split(bP),d.crossDomain==null&&(r=bR.exec(d.url.toLowerCase()),d.crossDomain=!(!r||r[1]==bW[1]&&r[2]==bW[2]&&(r[3]||(r[1]==="http:"?80:443))==(bW[3]||(bW[1]==="http:"?80:443)))),d.data&&d.processData&&typeof d.data!="string"&&(d.data=f.param(d.data,d.traditional)),b$(bT,d,c,v);if(s===2){return !1;}t=d.global,d.type=d.type.toUpperCase(),d.hasContent=!bK.test(d.type),t&&f.active++===0&&f.event.trigger("ajaxStart");if(!d.hasContent){d.data&&(d.url+=(bM.test(d.url)?"&":"?")+d.data,delete d.data),k=d.url;if(d.cache===!1){var x=f.now(),y=d.url.replace(bQ,"$1_="+x);d.url=y+(y===d.url?(bM.test(d.url)?"&":"?")+"_="+x:"");}}(d.data&&d.hasContent&&d.contentType!==!1||c.contentType)&&v.setRequestHeader("Content-Type",d.contentType),d.ifModified&&(k=k||d.url,f.lastModified[k]&&v.setRequestHeader("If-Modified-Since",f.lastModified[k]),f.etag[k]&&v.setRequestHeader("If-None-Match",f.etag[k])),v.setRequestHeader("Accept",d.dataTypes[0]&&d.accepts[d.dataTypes[0]]?d.accepts[d.dataTypes[0]]+(d.dataTypes[0]!=="*"?", "+bX+"; q=0.01":""):d.accepts["*"]);for(u in d.headers){v.setRequestHeader(u,d.headers[u]);}if(d.beforeSend&&(d.beforeSend.call(e,v,d)===!1||s===2)){v.abort();return !1;}for(u in {success:1,error:1,complete:1}){v[u](d[u]);}p=b$(bU,d,c,v);if(!p){w(-1,"No Transport");}else{v.readyState=1,t&&g.trigger("ajaxSend",[v,d]),d.async&&d.timeout>0&&(q=setTimeout(function(){v.abort("timeout");},d.timeout));try{s=1,p.send(l,w);}catch(z){if(s<2){w(-1,z);}else{throw z;}}}return v;},param:function(a,c){var d=[],e=function(a,b){b=f.isFunction(b)?b():b,d[d.length]=encodeURIComponent(a)+"="+encodeURIComponent(b);};c===b&&(c=f.ajaxSettings.traditional);if(f.isArray(a)||a.jquery&&!f.isPlainObject(a)){f.each(a,function(){e(this.name,this.value);});}else{for(var g in a){ca(g,a[g],c,e);}}return d.join("&").replace(bD,"+");}}),f.extend({active:0,lastModified:{},etag:{}});var cd=f.now(),ce=/(\=)\?(&|$)|\?\?/i;f.ajaxSetup({jsonp:"callback",jsonpCallback:function(){return f.expando+"_"+cd++;}}),f.ajaxPrefilter("json jsonp",function(b,c,d){var e=b.contentType==="application/x-www-form-urlencoded"&&typeof b.data=="string";if(b.dataTypes[0]==="jsonp"||b.jsonp!==!1&&(ce.test(b.url)||e&&ce.test(b.data))){var g,h=b.jsonpCallback=f.isFunction(b.jsonpCallback)?b.jsonpCallback():b.jsonpCallback,i=a[h],j=b.url,k=b.data,l="$1"+h+"$2";b.jsonp!==!1&&(j=j.replace(ce,l),b.url===j&&(e&&(k=k.replace(ce,l)),b.data===k&&(j+=(/\?/.test(j)?"&":"?")+b.jsonp+"="+h))),b.url=j,b.data=k,a[h]=function(a){g=[a];},d.always(function(){a[h]=i,g&&f.isFunction(i)&&a[h](g[0]);}),b.converters["script json"]=function(){g||f.error(h+" was not called");return g[0];},b.dataTypes[0]="json";return"script";}}),f.ajaxSetup({accepts:{script:"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"},contents:{script:/javascript|ecmascript/},converters:{"text script":function(a){f.globalEval(a);return a;}}}),f.ajaxPrefilter("script",function(a){a.cache===b&&(a.cache=!1),a.crossDomain&&(a.type="GET",a.global=!1);}),f.ajaxTransport("script",function(a){if(a.crossDomain){var d,e=c.head||c.getElementsByTagName("head")[0]||c.documentElement;return{send:function(f,g){d=c.createElement("script"),d.async="async",a.scriptCharset&&(d.charset=a.scriptCharset),d.src=a.url,d.onload=d.onreadystatechange=function(a,c){if(c||!d.readyState||/loaded|complete/.test(d.readyState)){d.onload=d.onreadystatechange=null,e&&d.parentNode&&e.removeChild(d),d=b,c||g(200,"success");}},e.insertBefore(d,e.firstChild);},abort:function(){d&&d.onload(0,1);}};}});var cf=a.ActiveXObject?function(){for(var a in ch){ch[a](0,1);}}:!1,cg=0,ch;f.ajaxSettings.xhr=a.ActiveXObject?function(){return !this.isLocal&&ci()||cj();}:ci,function(a){f.extend(f.support,{ajax:!!a,cors:!!a&&"withCredentials" in a});}(f.ajaxSettings.xhr()),f.support.ajax&&f.ajaxTransport(function(c){if(!c.crossDomain||f.support.cors){var d;return{send:function(e,g){var h=c.xhr(),i,j;c.username?h.open(c.type,c.url,c.async,c.username,c.password):h.open(c.type,c.url,c.async);if(c.xhrFields){for(j in c.xhrFields){h[j]=c.xhrFields[j];}}c.mimeType&&h.overrideMimeType&&h.overrideMimeType(c.mimeType),!c.crossDomain&&!e["X-Requested-With"]&&(e["X-Requested-With"]="XMLHttpRequest");try{for(j in e){h.setRequestHeader(j,e[j]);}}catch(k){}h.send(c.hasContent&&c.data||null),d=function(a,e){var j,k,l,m,n;try{if(d&&(e||h.readyState===4)){d=b,i&&(h.onreadystatechange=f.noop,cf&&delete ch[i]);if(e){h.readyState!==4&&h.abort();}else{j=h.status,l=h.getAllResponseHeaders(),m={},n=h.responseXML,n&&n.documentElement&&(m.xml=n),m.text=h.responseText;try{k=h.statusText;}catch(o){k="";}!j&&c.isLocal&&!c.crossDomain?j=m.text?200:404:j===1223&&(j=204);}}}catch(p){e||g(-1,p);}m&&g(j,k,m,l);},!c.async||h.readyState===4?d():(i=++cg,cf&&(ch||(ch={},f(a).unload(cf)),ch[i]=d),h.onreadystatechange=d);},abort:function(){d&&d(0,1);}};}});var ck={},cl,cm,cn=/^(?:toggle|show|hide)$/,co=/^([+\-]=)?([\d+.\-]+)([a-z%]*)$/i,cp,cq=[["height","marginTop","marginBottom","paddingTop","paddingBottom"],["width","marginLeft","marginRight","paddingLeft","paddingRight"],["opacity"]],cr;f.fn.extend({show:function(a,b,c){var d,e;if(a||a===0){return this.animate(cu("show",3),a,b,c);}for(var g=0,h=this.length;g<h;g++){d=this[g],d.style&&(e=d.style.display,!f._data(d,"olddisplay")&&e==="none"&&(e=d.style.display=""),e===""&&f.css(d,"display")==="none"&&f._data(d,"olddisplay",cv(d.nodeName)));}for(g=0;g<h;g++){d=this[g];if(d.style){e=d.style.display;if(e===""||e==="none"){d.style.display=f._data(d,"olddisplay")||"";}}}return this;},hide:function(a,b,c){if(a||a===0){return this.animate(cu("hide",3),a,b,c);}var d,e,g=0,h=this.length;for(;g<h;g++){d=this[g],d.style&&(e=f.css(d,"display"),e!=="none"&&!f._data(d,"olddisplay")&&f._data(d,"olddisplay",e));}for(g=0;g<h;g++){this[g].style&&(this[g].style.display="none");}return this;},_toggle:f.fn.toggle,toggle:function(a,b,c){var d=typeof a=="boolean";f.isFunction(a)&&f.isFunction(b)?this._toggle.apply(this,arguments):a==null||d?this.each(function(){var b=d?a:f(this).is(":hidden");f(this)[b?"show":"hide"]();}):this.animate(cu("toggle",3),a,b,c);return this;},fadeTo:function(a,b,c,d){return this.filter(":hidden").css("opacity",0).show().end().animate({opacity:b},a,c,d);},animate:function(a,b,c,d){function g(){e.queue===!1&&f._mark(this);var b=f.extend({},e),c=this.nodeType===1,d=c&&f(this).is(":hidden"),g,h,i,j,k,l,m,n,o;b.animatedProperties={};for(i in a){g=f.camelCase(i),i!==g&&(a[g]=a[i],delete a[i]),h=a[g],f.isArray(h)?(b.animatedProperties[g]=h[1],h=a[g]=h[0]):b.animatedProperties[g]=b.specialEasing&&b.specialEasing[g]||b.easing||"swing";if(h==="hide"&&d||h==="show"&&!d){return b.complete.call(this);}c&&(g==="height"||g==="width")&&(b.overflow=[this.style.overflow,this.style.overflowX,this.style.overflowY],f.css(this,"display")==="inline"&&f.css(this,"float")==="none"&&(!f.support.inlineBlockNeedsLayout||cv(this.nodeName)==="inline"?this.style.display="inline-block":this.style.zoom=1));}b.overflow!=null&&(this.style.overflow="hidden");for(i in a){j=new f.fx(this,b,i),h=a[i],cn.test(h)?(o=f._data(this,"toggle"+i)||(h==="toggle"?d?"show":"hide":0),o?(f._data(this,"toggle"+i,o==="show"?"hide":"show"),j[o]()):j[h]()):(k=co.exec(h),l=j.cur(),k?(m=parseFloat(k[2]),n=k[3]||(f.cssNumber[i]?"":"px"),n!=="px"&&(f.style(this,i,(m||1)+n),l=(m||1)/j.cur()*l,f.style(this,i,l+n)),k[1]&&(m=(k[1]==="-="?-1:1)*m+l),j.custom(l,m,n)):j.custom(l,h,""));}return !0;}var e=f.speed(b,c,d);if(f.isEmptyObject(a)){return this.each(e.complete,[!1]);}a=f.extend({},a);return e.queue===!1?this.each(g):this.queue(e.queue,g);},stop:function(a,c,d){typeof a!="string"&&(d=c,c=a,a=b),c&&a!==!1&&this.queue(a||"fx",[]);return this.each(function(){function h(a,b,c){var e=b[c];f.removeData(a,c,!0),e.stop(d);}var b,c=!1,e=f.timers,g=f._data(this);d||f._unmark(!0,this);if(a==null){for(b in g){g[b]&&g[b].stop&&b.indexOf(".run")===b.length-4&&h(this,g,b);}}else{g[b=a+".run"]&&g[b].stop&&h(this,g,b);}for(b=e.length;b--;){e[b].elem===this&&(a==null||e[b].queue===a)&&(d?e[b](!0):e[b].saveState(),c=!0,e.splice(b,1));}(!d||!c)&&f.dequeue(this,a);});}}),f.each({slideDown:cu("show",1),slideUp:cu("hide",1),slideToggle:cu("toggle",1),fadeIn:{opacity:"show"},fadeOut:{opacity:"hide"},fadeToggle:{opacity:"toggle"}},function(a,b){f.fn[a]=function(a,c,d){return this.animate(b,a,c,d);};}),f.extend({speed:function(a,b,c){var d=a&&typeof a=="object"?f.extend({},a):{complete:c||!c&&b||f.isFunction(a)&&a,duration:a,easing:c&&b||b&&!f.isFunction(b)&&b};d.duration=f.fx.off?0:typeof d.duration=="number"?d.duration:d.duration in f.fx.speeds?f.fx.speeds[d.duration]:f.fx.speeds._default;if(d.queue==null||d.queue===!0){d.queue="fx";}d.old=d.complete,d.complete=function(a){f.isFunction(d.old)&&d.old.call(this),d.queue?f.dequeue(this,d.queue):a!==!1&&f._unmark(this);};return d;},easing:{linear:function(a,b,c,d){return c+d*a;},swing:function(a,b,c,d){return(-Math.cos(a*Math.PI)/2+0.5)*d+c;}},timers:[],fx:function(a,b,c){this.options=b,this.elem=a,this.prop=c,b.orig=b.orig||{};}}),f.fx.prototype={update:function(){this.options.step&&this.options.step.call(this.elem,this.now,this),(f.fx.step[this.prop]||f.fx.step._default)(this);},cur:function(){if(this.elem[this.prop]!=null&&(!this.elem.style||this.elem.style[this.prop]==null)){return this.elem[this.prop];}var a,b=f.css(this.elem,this.prop);return isNaN(a=parseFloat(b))?!b||b==="auto"?0:b:a;},custom:function(a,c,d){function h(a){return e.step(a);}var e=this,g=f.fx;this.startTime=cr||cs(),this.end=c,this.now=this.start=a,this.pos=this.state=0,this.unit=d||this.unit||(f.cssNumber[this.prop]?"":"px"),h.queue=this.options.queue,h.elem=this.elem,h.saveState=function(){e.options.hide&&f._data(e.elem,"fxshow"+e.prop)===b&&f._data(e.elem,"fxshow"+e.prop,e.start);},h()&&f.timers.push(h)&&!cp&&(cp=setInterval(g.tick,g.interval));},show:function(){var a=f._data(this.elem,"fxshow"+this.prop);this.options.orig[this.prop]=a||f.style(this.elem,this.prop),this.options.show=!0,a!==b?this.custom(this.cur(),a):this.custom(this.prop==="width"||this.prop==="height"?1:0,this.cur()),f(this.elem).show();},hide:function(){this.options.orig[this.prop]=f._data(this.elem,"fxshow"+this.prop)||f.style(this.elem,this.prop),this.options.hide=!0,this.custom(this.cur(),0);},step:function(a){var b,c,d,e=cr||cs(),g=!0,h=this.elem,i=this.options;if(a||e>=i.duration+this.startTime){this.now=this.end,this.pos=this.state=1,this.update(),i.animatedProperties[this.prop]=!0;for(b in i.animatedProperties){i.animatedProperties[b]!==!0&&(g=!1);}if(g){i.overflow!=null&&!f.support.shrinkWrapBlocks&&f.each(["","X","Y"],function(a,b){h.style["overflow"+b]=i.overflow[a];}),i.hide&&f(h).hide();if(i.hide||i.show){for(b in i.animatedProperties){f.style(h,b,i.orig[b]),f.removeData(h,"fxshow"+b,!0),f.removeData(h,"toggle"+b,!0);}}d=i.complete,d&&(i.complete=!1,d.call(h));}return !1;}i.duration==Infinity?this.now=e:(c=e-this.startTime,this.state=c/i.duration,this.pos=f.easing[i.animatedProperties[this.prop]](this.state,c,0,1,i.duration),this.now=this.start+(this.end-this.start)*this.pos),this.update();return !0;}},f.extend(f.fx,{tick:function(){var a,b=f.timers,c=0;for(;c<b.length;c++){a=b[c],!a()&&b[c]===a&&b.splice(c--,1);}b.length||f.fx.stop();},interval:13,stop:function(){clearInterval(cp),cp=null;},speeds:{slow:600,fast:200,_default:400},step:{opacity:function(a){f.style(a.elem,"opacity",a.now);},_default:function(a){a.elem.style&&a.elem.style[a.prop]!=null?a.elem.style[a.prop]=a.now+a.unit:a.elem[a.prop]=a.now;}}}),f.each(["width","height"],function(a,b){f.fx.step[b]=function(a){f.style(a.elem,b,Math.max(0,a.now)+a.unit);};}),f.expr&&f.expr.filters&&(f.expr.filters.animated=function(a){return f.grep(f.timers,function(b){return a===b.elem;}).length;});var cw=/^t(?:able|d|h)$/i,cx=/^(?:body|html)$/i;"getBoundingClientRect" in c.documentElement?f.fn.offset=function(a){var b=this[0],c;if(a){return this.each(function(b){f.offset.setOffset(this,a,b);});}if(!b||!b.ownerDocument){return null;}if(b===b.ownerDocument.body){return f.offset.bodyOffset(b);}try{c=b.getBoundingClientRect();}catch(d){}var e=b.ownerDocument,g=e.documentElement;if(!c||!f.contains(g,b)){return c?{top:c.top,left:c.left}:{top:0,left:0};}var h=e.body,i=cy(e),j=g.clientTop||h.clientTop||0,k=g.clientLeft||h.clientLeft||0,l=i.pageYOffset||f.support.boxModel&&g.scrollTop||h.scrollTop,m=i.pageXOffset||f.support.boxModel&&g.scrollLeft||h.scrollLeft,n=c.top+l-j,o=c.left+m-k;return{top:n,left:o};}:f.fn.offset=function(a){var b=this[0];if(a){return this.each(function(b){f.offset.setOffset(this,a,b);});}if(!b||!b.ownerDocument){return null;}if(b===b.ownerDocument.body){return f.offset.bodyOffset(b);}var c,d=b.offsetParent,e=b,g=b.ownerDocument,h=g.documentElement,i=g.body,j=g.defaultView,k=j?j.getComputedStyle(b,null):b.currentStyle,l=b.offsetTop,m=b.offsetLeft;while((b=b.parentNode)&&b!==i&&b!==h){if(f.support.fixedPosition&&k.position==="fixed"){break;}c=j?j.getComputedStyle(b,null):b.currentStyle,l-=b.scrollTop,m-=b.scrollLeft,b===d&&(l+=b.offsetTop,m+=b.offsetLeft,f.support.doesNotAddBorder&&(!f.support.doesAddBorderForTableAndCells||!cw.test(b.nodeName))&&(l+=parseFloat(c.borderTopWidth)||0,m+=parseFloat(c.borderLeftWidth)||0),e=d,d=b.offsetParent),f.support.subtractsBorderForOverflowNotVisible&&c.overflow!=="visible"&&(l+=parseFloat(c.borderTopWidth)||0,m+=parseFloat(c.borderLeftWidth)||0),k=c;}if(k.position==="relative"||k.position==="static"){l+=i.offsetTop,m+=i.offsetLeft;}f.support.fixedPosition&&k.position==="fixed"&&(l+=Math.max(h.scrollTop,i.scrollTop),m+=Math.max(h.scrollLeft,i.scrollLeft));return{top:l,left:m};},f.offset={bodyOffset:function(a){var b=a.offsetTop,c=a.offsetLeft;f.support.doesNotIncludeMarginInBodyOffset&&(b+=parseFloat(f.css(a,"marginTop"))||0,c+=parseFloat(f.css(a,"marginLeft"))||0);return{top:b,left:c};},setOffset:function(a,b,c){var d=f.css(a,"position");d==="static"&&(a.style.position="relative");var e=f(a),g=e.offset(),h=f.css(a,"top"),i=f.css(a,"left"),j=(d==="absolute"||d==="fixed")&&f.inArray("auto",[h,i])>-1,k={},l={},m,n;j?(l=e.position(),m=l.top,n=l.left):(m=parseFloat(h)||0,n=parseFloat(i)||0),f.isFunction(b)&&(b=b.call(a,c,g)),b.top!=null&&(k.top=b.top-g.top+m),b.left!=null&&(k.left=b.left-g.left+n),"using" in b?b.using.call(a,k):e.css(k);}},f.fn.extend({position:function(){if(!this[0]){return null;}var a=this[0],b=this.offsetParent(),c=this.offset(),d=cx.test(b[0].nodeName)?{top:0,left:0}:b.offset();c.top-=parseFloat(f.css(a,"marginTop"))||0,c.left-=parseFloat(f.css(a,"marginLeft"))||0,d.top+=parseFloat(f.css(b[0],"borderTopWidth"))||0,d.left+=parseFloat(f.css(b[0],"borderLeftWidth"))||0;return{top:c.top-d.top,left:c.left-d.left};},offsetParent:function(){return this.map(function(){var a=this.offsetParent||c.body;while(a&&!cx.test(a.nodeName)&&f.css(a,"position")==="static"){a=a.offsetParent;}return a;});}}),f.each(["Left","Top"],function(a,c){var d="scroll"+c;f.fn[d]=function(c){var e,g;if(c===b){e=this[0];if(!e){return null;}g=cy(e);return g?"pageXOffset" in g?g[a?"pageYOffset":"pageXOffset"]:f.support.boxModel&&g.document.documentElement[d]||g.document.body[d]:e[d];}return this.each(function(){g=cy(this),g?g.scrollTo(a?f(g).scrollLeft():c,a?c:f(g).scrollTop()):this[d]=c;});};}),f.each(["Height","Width"],function(a,c){var d=c.toLowerCase();f.fn["inner"+c]=function(){var a=this[0];return a?a.style?parseFloat(f.css(a,d,"padding")):this[d]():null;},f.fn["outer"+c]=function(a){var b=this[0];return b?b.style?parseFloat(f.css(b,d,a?"margin":"border")):this[d]():null;},f.fn[d]=function(a){var e=this[0];if(!e){return a==null?null:this;}if(f.isFunction(a)){return this.each(function(b){var c=f(this);c[d](a.call(this,b,c[d]()));});}if(f.isWindow(e)){var g=e.document.documentElement["client"+c],h=e.document.body;return e.document.compatMode==="CSS1Compat"&&g||h&&h["client"+c]||g;}if(e.nodeType===9){return Math.max(e.documentElement["client"+c],e.body["scroll"+c],e.documentElement["scroll"+c],e.body["offset"+c],e.documentElement["offset"+c]);}if(a===b){var i=f.css(e,d),j=parseFloat(i);return f.isNumeric(j)?j:i;}return this.css(d,typeof a=="string"?a:a+"px");};}),a.jQuery=a.$=f,typeof define=="function"&&define.amd&&define.amd.jQuery&&define("jquery",[],function(){return f;});})(window);
if(!this.JSON2){this.JSON2={}}(function(){function f(n){return n<10?"0"+n:n}if(typeof Date.prototype.toJSON!=="function"){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+"-"+f(this.getUTCMonth()+1)+"-"+f(this.getUTCDate())+"T"+f(this.getUTCHours())+":"+f(this.getUTCMinutes())+":"+f(this.getUTCSeconds())+"Z":null};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf()}}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==="string"?c:"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})+'"':'"'+string+'"'}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==="object"&&typeof value.toJSON==="function"){value=value.toJSON(key)}if(typeof rep==="function"){value=rep.call(holder,key,value)}switch(typeof value){case"string":return quote(value);case"number":return isFinite(value)?String(value):"null";case"boolean":case"null":return String(value);case"object":if(!value){return"null"}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==="[object Array]"){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||"null"}v=partial.length===0?"[]":gap?"[\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"]":"["+partial.join(",")+"]";gap=mind;return v}if(rep&&typeof rep==="object"){length=rep.length;for(i=0;i<length;i+=1){k=rep[i];if(typeof k==="string"){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}else{for(k in value){if(Object.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?": ":":")+v)}}}}v=partial.length===0?"{}":gap?"{\n"+gap+partial.join(",\n"+gap)+"\n"+mind+"}":"{"+partial.join(",")+"}";gap=mind;return v}}if(typeof JSON2.stringify!=="function"){JSON2.stringify=function(value,replacer,space){var i;gap="";indent="";if(typeof space==="number"){for(i=0;i<space;i+=1){indent+=" "}}else{if(typeof space==="string"){indent=space}}rep=replacer;if(replacer&&typeof replacer!=="function"&&(typeof replacer!=="object"||typeof replacer.length!=="number")){throw new Error("JSON2.stringify")}return str("",{"":value})}}if(typeof JSON2.parse!=="function"){JSON2.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==="object"){for(k in value){if(Object.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v}else{delete value[k]}}}}return reviver.call(holder,key,value)}text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return"\\u"+("0000"+a.charCodeAt(0).toString(16)).slice(-4)})}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""))){j=eval("("+text+")");return typeof reviver==="function"?walk({"":j},""):j}throw new SyntaxError("JSON2.parse")}}}());
define("JSON2", function(){});

//     Underscore.js 1.5.1
//     http://underscorejs.org
//     (c) 2009-2013 Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
//     Underscore may be freely distributed under the MIT license.

(function() {

  // Baseline setup
  // --------------

  // Establish the root object, `window` in the browser, or `global` on the server.
  var root = this;

  // Save the previous value of the `_` variable.
  var previousUnderscore = root._;

  // Establish the object that gets returned to break out of a loop iteration.
  var breaker = {};

  // Save bytes in the minified (but not gzipped) version:
  var ArrayProto = Array.prototype, ObjProto = Object.prototype, FuncProto = Function.prototype;

  // Create quick reference variables for speed access to core prototypes.
  var
    push             = ArrayProto.push,
    slice            = ArrayProto.slice,
    concat           = ArrayProto.concat,
    toString         = ObjProto.toString,
    hasOwnProperty   = ObjProto.hasOwnProperty;

  // All **ECMAScript 5** native function implementations that we hope to use
  // are declared here.
  var
    nativeForEach      = ArrayProto.forEach,
    nativeMap          = ArrayProto.map,
    nativeReduce       = ArrayProto.reduce,
    nativeReduceRight  = ArrayProto.reduceRight,
    nativeFilter       = ArrayProto.filter,
    nativeEvery        = ArrayProto.every,
    nativeSome         = ArrayProto.some,
    nativeIndexOf      = ArrayProto.indexOf,
    nativeLastIndexOf  = ArrayProto.lastIndexOf,
    nativeIsArray      = Array.isArray,
    nativeKeys         = Object.keys,
    nativeBind         = FuncProto.bind;

  // Create a safe reference to the Underscore object for use below.
  var _ = function(obj) {
    if (obj instanceof _) return obj;
    if (!(this instanceof _)) return new _(obj);
    this._wrapped = obj;
  };

  // Export the Underscore object for **Node.js**, with
  // backwards-compatibility for the old `require()` API. If we're in
  // the browser, add `_` as a global object via a string identifier,
  // for Closure Compiler "advanced" mode.
  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = _;
    }
    exports._ = _;
  } else {
    root._ = _;
  }

  // Current version.
  _.VERSION = '1.5.1';

  // Collection Functions
  // --------------------

  // The cornerstone, an `each` implementation, aka `forEach`.
  // Handles objects with the built-in `forEach`, arrays, and raw objects.
  // Delegates to **ECMAScript 5**'s native `forEach` if available.
  var each = _.each = _.forEach = function(obj, iterator, context) {
    if (obj == null) return;
    if (nativeForEach && obj.forEach === nativeForEach) {
      obj.forEach(iterator, context);
    } else if (obj.length === +obj.length) {
      for (var i = 0, l = obj.length; i < l; i++) {
        if (iterator.call(context, obj[i], i, obj) === breaker) return;
      }
    } else {
      for (var key in obj) {
        if (_.has(obj, key)) {
          if (iterator.call(context, obj[key], key, obj) === breaker) return;
        }
      }
    }
  };

  // Return the results of applying the iterator to each element.
  // Delegates to **ECMAScript 5**'s native `map` if available.
  _.map = _.collect = function(obj, iterator, context) {
    var results = [];
    if (obj == null) return results;
    if (nativeMap && obj.map === nativeMap) return obj.map(iterator, context);
    each(obj, function(value, index, list) {
      results.push(iterator.call(context, value, index, list));
    });
    return results;
  };

  var reduceError = 'Reduce of empty array with no initial value';

  // **Reduce** builds up a single result from a list of values, aka `inject`,
  // or `foldl`. Delegates to **ECMAScript 5**'s native `reduce` if available.
  _.reduce = _.foldl = _.inject = function(obj, iterator, memo, context) {
    var initial = arguments.length > 2;
    if (obj == null) obj = [];
    if (nativeReduce && obj.reduce === nativeReduce) {
      if (context) iterator = _.bind(iterator, context);
      return initial ? obj.reduce(iterator, memo) : obj.reduce(iterator);
    }
    each(obj, function(value, index, list) {
      if (!initial) {
        memo = value;
        initial = true;
      } else {
        memo = iterator.call(context, memo, value, index, list);
      }
    });
    if (!initial) throw new TypeError(reduceError);
    return memo;
  };

  // The right-associative version of reduce, also known as `foldr`.
  // Delegates to **ECMAScript 5**'s native `reduceRight` if available.
  _.reduceRight = _.foldr = function(obj, iterator, memo, context) {
    var initial = arguments.length > 2;
    if (obj == null) obj = [];
    if (nativeReduceRight && obj.reduceRight === nativeReduceRight) {
      if (context) iterator = _.bind(iterator, context);
      return initial ? obj.reduceRight(iterator, memo) : obj.reduceRight(iterator);
    }
    var length = obj.length;
    if (length !== +length) {
      var keys = _.keys(obj);
      length = keys.length;
    }
    each(obj, function(value, index, list) {
      index = keys ? keys[--length] : --length;
      if (!initial) {
        memo = obj[index];
        initial = true;
      } else {
        memo = iterator.call(context, memo, obj[index], index, list);
      }
    });
    if (!initial) throw new TypeError(reduceError);
    return memo;
  };

  // Return the first value which passes a truth test. Aliased as `detect`.
  _.find = _.detect = function(obj, iterator, context) {
    var result;
    any(obj, function(value, index, list) {
      if (iterator.call(context, value, index, list)) {
        result = value;
        return true;
      }
    });
    return result;
  };

  // Return all the elements that pass a truth test.
  // Delegates to **ECMAScript 5**'s native `filter` if available.
  // Aliased as `select`.
  _.filter = _.select = function(obj, iterator, context) {
    var results = [];
    if (obj == null) return results;
    if (nativeFilter && obj.filter === nativeFilter) return obj.filter(iterator, context);
    each(obj, function(value, index, list) {
      if (iterator.call(context, value, index, list)) results.push(value);
    });
    return results;
  };

  // Return all the elements for which a truth test fails.
  _.reject = function(obj, iterator, context) {
    return _.filter(obj, function(value, index, list) {
      return !iterator.call(context, value, index, list);
    }, context);
  };

  // Determine whether all of the elements match a truth test.
  // Delegates to **ECMAScript 5**'s native `every` if available.
  // Aliased as `all`.
  _.every = _.all = function(obj, iterator, context) {
    iterator || (iterator = _.identity);
    var result = true;
    if (obj == null) return result;
    if (nativeEvery && obj.every === nativeEvery) return obj.every(iterator, context);
    each(obj, function(value, index, list) {
      if (!(result = result && iterator.call(context, value, index, list))) return breaker;
    });
    return !!result;
  };

  // Determine if at least one element in the object matches a truth test.
  // Delegates to **ECMAScript 5**'s native `some` if available.
  // Aliased as `any`.
  var any = _.some = _.any = function(obj, iterator, context) {
    iterator || (iterator = _.identity);
    var result = false;
    if (obj == null) return result;
    if (nativeSome && obj.some === nativeSome) return obj.some(iterator, context);
    each(obj, function(value, index, list) {
      if (result || (result = iterator.call(context, value, index, list))) return breaker;
    });
    return !!result;
  };

  // Determine if the array or object contains a given value (using `===`).
  // Aliased as `include`.
  _.contains = _.include = function(obj, target) {
    if (obj == null) return false;
    if (nativeIndexOf && obj.indexOf === nativeIndexOf) return obj.indexOf(target) != -1;
    return any(obj, function(value) {
      return value === target;
    });
  };

  // Invoke a method (with arguments) on every item in a collection.
  _.invoke = function(obj, method) {
    var args = slice.call(arguments, 2);
    var isFunc = _.isFunction(method);
    return _.map(obj, function(value) {
      return (isFunc ? method : value[method]).apply(value, args);
    });
  };

  // Convenience version of a common use case of `map`: fetching a property.
  _.pluck = function(obj, key) {
    return _.map(obj, function(value){ return value[key]; });
  };

  // Convenience version of a common use case of `filter`: selecting only objects
  // containing specific `key:value` pairs.
  _.where = function(obj, attrs, first) {
    if (_.isEmpty(attrs)) return first ? void 0 : [];
    return _[first ? 'find' : 'filter'](obj, function(value) {
      for (var key in attrs) {
        if (attrs[key] !== value[key]) return false;
      }
      return true;
    });
  };

  // Convenience version of a common use case of `find`: getting the first object
  // containing specific `key:value` pairs.
  _.findWhere = function(obj, attrs) {
    return _.where(obj, attrs, true);
  };

  // Return the maximum element or (element-based computation).
  // Can't optimize arrays of integers longer than 65,535 elements.
  // See [WebKit Bug 80797](https://bugs.webkit.org/show_bug.cgi?id=80797)
  _.max = function(obj, iterator, context) {
    if (!iterator && _.isArray(obj) && obj[0] === +obj[0] && obj.length < 65535) {
      return Math.max.apply(Math, obj);
    }
    if (!iterator && _.isEmpty(obj)) return -Infinity;
    var result = {computed : -Infinity, value: -Infinity};
    each(obj, function(value, index, list) {
      var computed = iterator ? iterator.call(context, value, index, list) : value;
      computed > result.computed && (result = {value : value, computed : computed});
    });
    return result.value;
  };

  // Return the minimum element (or element-based computation).
  _.min = function(obj, iterator, context) {
    if (!iterator && _.isArray(obj) && obj[0] === +obj[0] && obj.length < 65535) {
      return Math.min.apply(Math, obj);
    }
    if (!iterator && _.isEmpty(obj)) return Infinity;
    var result = {computed : Infinity, value: Infinity};
    each(obj, function(value, index, list) {
      var computed = iterator ? iterator.call(context, value, index, list) : value;
      computed < result.computed && (result = {value : value, computed : computed});
    });
    return result.value;
  };

  // Shuffle an array.
  _.shuffle = function(obj) {
    var rand;
    var index = 0;
    var shuffled = [];
    each(obj, function(value) {
      rand = _.random(index++);
      shuffled[index - 1] = shuffled[rand];
      shuffled[rand] = value;
    });
    return shuffled;
  };

  // An internal function to generate lookup iterators.
  var lookupIterator = function(value) {
    return _.isFunction(value) ? value : function(obj){ return obj[value]; };
  };

  // Sort the object's values by a criterion produced by an iterator.
  _.sortBy = function(obj, value, context) {
    var iterator = lookupIterator(value);
    return _.pluck(_.map(obj, function(value, index, list) {
      return {
        value : value,
        index : index,
        criteria : iterator.call(context, value, index, list)
      };
    }).sort(function(left, right) {
      var a = left.criteria;
      var b = right.criteria;
      if (a !== b) {
        if (a > b || a === void 0) return 1;
        if (a < b || b === void 0) return -1;
      }
      return left.index < right.index ? -1 : 1;
    }), 'value');
  };

  // An internal function used for aggregate "group by" operations.
  var group = function(obj, value, context, behavior) {
    var result = {};
    var iterator = lookupIterator(value == null ? _.identity : value);
    each(obj, function(value, index) {
      var key = iterator.call(context, value, index, obj);
      behavior(result, key, value);
    });
    return result;
  };

  // Groups the object's values by a criterion. Pass either a string attribute
  // to group by, or a function that returns the criterion.
  _.groupBy = function(obj, value, context) {
    return group(obj, value, context, function(result, key, value) {
      (_.has(result, key) ? result[key] : (result[key] = [])).push(value);
    });
  };

  // Counts instances of an object that group by a certain criterion. Pass
  // either a string attribute to count by, or a function that returns the
  // criterion.
  _.countBy = function(obj, value, context) {
    return group(obj, value, context, function(result, key) {
      if (!_.has(result, key)) result[key] = 0;
      result[key]++;
    });
  };

  // Use a comparator function to figure out the smallest index at which
  // an object should be inserted so as to maintain order. Uses binary search.
  _.sortedIndex = function(array, obj, iterator, context) {
    iterator = iterator == null ? _.identity : lookupIterator(iterator);
    var value = iterator.call(context, obj);
    var low = 0, high = array.length;
    while (low < high) {
      var mid = (low + high) >>> 1;
      iterator.call(context, array[mid]) < value ? low = mid + 1 : high = mid;
    }
    return low;
  };

  // Safely create a real, live array from anything iterable.
  _.toArray = function(obj) {
    if (!obj) return [];
    if (_.isArray(obj)) return slice.call(obj);
    if (obj.length === +obj.length) return _.map(obj, _.identity);
    return _.values(obj);
  };

  // Return the number of elements in an object.
  _.size = function(obj) {
    if (obj == null) return 0;
    return (obj.length === +obj.length) ? obj.length : _.keys(obj).length;
  };

  // Array Functions
  // ---------------

  // Get the first element of an array. Passing **n** will return the first N
  // values in the array. Aliased as `head` and `take`. The **guard** check
  // allows it to work with `_.map`.
  _.first = _.head = _.take = function(array, n, guard) {
    if (array == null) return void 0;
    return (n != null) && !guard ? slice.call(array, 0, n) : array[0];
  };

  // Returns everything but the last entry of the array. Especially useful on
  // the arguments object. Passing **n** will return all the values in
  // the array, excluding the last N. The **guard** check allows it to work with
  // `_.map`.
  _.initial = function(array, n, guard) {
    return slice.call(array, 0, array.length - ((n == null) || guard ? 1 : n));
  };

  // Get the last element of an array. Passing **n** will return the last N
  // values in the array. The **guard** check allows it to work with `_.map`.
  _.last = function(array, n, guard) {
    if (array == null) return void 0;
    if ((n != null) && !guard) {
      return slice.call(array, Math.max(array.length - n, 0));
    } else {
      return array[array.length - 1];
    }
  };

  // Returns everything but the first entry of the array. Aliased as `tail` and `drop`.
  // Especially useful on the arguments object. Passing an **n** will return
  // the rest N values in the array. The **guard**
  // check allows it to work with `_.map`.
  _.rest = _.tail = _.drop = function(array, n, guard) {
    return slice.call(array, (n == null) || guard ? 1 : n);
  };

  // Trim out all falsy values from an array.
  _.compact = function(array) {
    return _.filter(array, _.identity);
  };

  // Internal implementation of a recursive `flatten` function.
  var flatten = function(input, shallow, output) {
    if (shallow && _.every(input, _.isArray)) {
      return concat.apply(output, input);
    }
    each(input, function(value) {
      if (_.isArray(value) || _.isArguments(value)) {
        shallow ? push.apply(output, value) : flatten(value, shallow, output);
      } else {
        output.push(value);
      }
    });
    return output;
  };

  // Return a completely flattened version of an array.
  _.flatten = function(array, shallow) {
    return flatten(array, shallow, []);
  };

  // Return a version of the array that does not contain the specified value(s).
  _.without = function(array) {
    return _.difference(array, slice.call(arguments, 1));
  };

  // Produce a duplicate-free version of the array. If the array has already
  // been sorted, you have the option of using a faster algorithm.
  // Aliased as `unique`.
  _.uniq = _.unique = function(array, isSorted, iterator, context) {
    if (_.isFunction(isSorted)) {
      context = iterator;
      iterator = isSorted;
      isSorted = false;
    }
    var initial = iterator ? _.map(array, iterator, context) : array;
    var results = [];
    var seen = [];
    each(initial, function(value, index) {
      if (isSorted ? (!index || seen[seen.length - 1] !== value) : !_.contains(seen, value)) {
        seen.push(value);
        results.push(array[index]);
      }
    });
    return results;
  };

  // Produce an array that contains the union: each distinct element from all of
  // the passed-in arrays.
  _.union = function() {
    return _.uniq(_.flatten(arguments, true));
  };

  // Produce an array that contains every item shared between all the
  // passed-in arrays.
  _.intersection = function(array) {
    var rest = slice.call(arguments, 1);
    return _.filter(_.uniq(array), function(item) {
      return _.every(rest, function(other) {
        return _.indexOf(other, item) >= 0;
      });
    });
  };

  // Take the difference between one array and a number of other arrays.
  // Only the elements present in just the first array will remain.
  _.difference = function(array) {
    var rest = concat.apply(ArrayProto, slice.call(arguments, 1));
    return _.filter(array, function(value){ return !_.contains(rest, value); });
  };

  // Zip together multiple lists into a single array -- elements that share
  // an index go together.
  _.zip = function() {
    var length = _.max(_.pluck(arguments, "length").concat(0));
    var results = new Array(length);
    for (var i = 0; i < length; i++) {
      results[i] = _.pluck(arguments, '' + i);
    }
    return results;
  };

  // Converts lists into objects. Pass either a single array of `[key, value]`
  // pairs, or two parallel arrays of the same length -- one of keys, and one of
  // the corresponding values.
  _.object = function(list, values) {
    if (list == null) return {};
    var result = {};
    for (var i = 0, l = list.length; i < l; i++) {
      if (values) {
        result[list[i]] = values[i];
      } else {
        result[list[i][0]] = list[i][1];
      }
    }
    return result;
  };

  // If the browser doesn't supply us with indexOf (I'm looking at you, **MSIE**),
  // we need this function. Return the position of the first occurrence of an
  // item in an array, or -1 if the item is not included in the array.
  // Delegates to **ECMAScript 5**'s native `indexOf` if available.
  // If the array is large and already in sort order, pass `true`
  // for **isSorted** to use binary search.
  _.indexOf = function(array, item, isSorted) {
    if (array == null) return -1;
    var i = 0, l = array.length;
    if (isSorted) {
      if (typeof isSorted == 'number') {
        i = (isSorted < 0 ? Math.max(0, l + isSorted) : isSorted);
      } else {
        i = _.sortedIndex(array, item);
        return array[i] === item ? i : -1;
      }
    }
    if (nativeIndexOf && array.indexOf === nativeIndexOf) return array.indexOf(item, isSorted);
    for (; i < l; i++) if (array[i] === item) return i;
    return -1;
  };

  // Delegates to **ECMAScript 5**'s native `lastIndexOf` if available.
  _.lastIndexOf = function(array, item, from) {
    if (array == null) return -1;
    var hasIndex = from != null;
    if (nativeLastIndexOf && array.lastIndexOf === nativeLastIndexOf) {
      return hasIndex ? array.lastIndexOf(item, from) : array.lastIndexOf(item);
    }
    var i = (hasIndex ? from : array.length);
    while (i--) if (array[i] === item) return i;
    return -1;
  };

  // Generate an integer Array containing an arithmetic progression. A port of
  // the native Python `range()` function. See
  // [the Python documentation](http://docs.python.org/library/functions.html#range).
  _.range = function(start, stop, step) {
    if (arguments.length <= 1) {
      stop = start || 0;
      start = 0;
    }
    step = arguments[2] || 1;

    var len = Math.max(Math.ceil((stop - start) / step), 0);
    var idx = 0;
    var range = new Array(len);

    while(idx < len) {
      range[idx++] = start;
      start += step;
    }

    return range;
  };

  // Function (ahem) Functions
  // ------------------

  // Reusable constructor function for prototype setting.
  var ctor = function(){};

  // Create a function bound to a given object (assigning `this`, and arguments,
  // optionally). Delegates to **ECMAScript 5**'s native `Function.bind` if
  // available.
  _.bind = function(func, context) {
    var args, bound;
    if (nativeBind && func.bind === nativeBind) return nativeBind.apply(func, slice.call(arguments, 1));
    if (!_.isFunction(func)) throw new TypeError;
    args = slice.call(arguments, 2);
    return bound = function() {
      if (!(this instanceof bound)) return func.apply(context, args.concat(slice.call(arguments)));
      ctor.prototype = func.prototype;
      var self = new ctor;
      ctor.prototype = null;
      var result = func.apply(self, args.concat(slice.call(arguments)));
      if (Object(result) === result) return result;
      return self;
    };
  };

  // Partially apply a function by creating a version that has had some of its
  // arguments pre-filled, without changing its dynamic `this` context.
  _.partial = function(func) {
    var args = slice.call(arguments, 1);
    return function() {
      return func.apply(this, args.concat(slice.call(arguments)));
    };
  };

  // Bind all of an object's methods to that object. Useful for ensuring that
  // all callbacks defined on an object belong to it.
  _.bindAll = function(obj) {
    var funcs = slice.call(arguments, 1);
    if (funcs.length === 0) throw new Error("bindAll must be passed function names");
    each(funcs, function(f) { obj[f] = _.bind(obj[f], obj); });
    return obj;
  };

  // Memoize an expensive function by storing its results.
  _.memoize = function(func, hasher) {
    var memo = {};
    hasher || (hasher = _.identity);
    return function() {
      var key = hasher.apply(this, arguments);
      return _.has(memo, key) ? memo[key] : (memo[key] = func.apply(this, arguments));
    };
  };

  // Delays a function for the given number of milliseconds, and then calls
  // it with the arguments supplied.
  _.delay = function(func, wait) {
    var args = slice.call(arguments, 2);
    return setTimeout(function(){ return func.apply(null, args); }, wait);
  };

  // Defers a function, scheduling it to run after the current call stack has
  // cleared.
  _.defer = function(func) {
    return _.delay.apply(_, [func, 1].concat(slice.call(arguments, 1)));
  };

  // Returns a function, that, when invoked, will only be triggered at most once
  // during a given window of time. Normally, the throttled function will run
  // as much as it can, without ever going more than once per `wait` duration;
  // but if you'd like to disable the execution on the leading edge, pass
  // `{leading: false}`. To disable execution on the trailing edge, ditto.
  _.throttle = function(func, wait, options) {
    var context, args, result;
    var timeout = null;
    var previous = 0;
    options || (options = {});
    var later = function() {
      previous = options.leading === false ? 0 : new Date;
      timeout = null;
      result = func.apply(context, args);
    };
    return function() {
      var now = new Date;
      if (!previous && options.leading === false) previous = now;
      var remaining = wait - (now - previous);
      context = this;
      args = arguments;
      if (remaining <= 0) {
        clearTimeout(timeout);
        timeout = null;
        previous = now;
        result = func.apply(context, args);
      } else if (!timeout && options.trailing !== false) {
        timeout = setTimeout(later, remaining);
      }
      return result;
    };
  };

  // Returns a function, that, as long as it continues to be invoked, will not
  // be triggered. The function will be called after it stops being called for
  // N milliseconds. If `immediate` is passed, trigger the function on the
  // leading edge, instead of the trailing.
  _.debounce = function(func, wait, immediate) {
    var result;
    var timeout = null;
    return function() {
      var context = this, args = arguments;
      var later = function() {
        timeout = null;
        if (!immediate) result = func.apply(context, args);
      };
      var callNow = immediate && !timeout;
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
      if (callNow) result = func.apply(context, args);
      return result;
    };
  };

  // Returns a function that will be executed at most one time, no matter how
  // often you call it. Useful for lazy initialization.
  _.once = function(func) {
    var ran = false, memo;
    return function() {
      if (ran) return memo;
      ran = true;
      memo = func.apply(this, arguments);
      func = null;
      return memo;
    };
  };

  // Returns the first function passed as an argument to the second,
  // allowing you to adjust arguments, run code before and after, and
  // conditionally execute the original function.
  _.wrap = function(func, wrapper) {
    return function() {
      var args = [func];
      push.apply(args, arguments);
      return wrapper.apply(this, args);
    };
  };

  // Returns a function that is the composition of a list of functions, each
  // consuming the return value of the function that follows.
  _.compose = function() {
    var funcs = arguments;
    return function() {
      var args = arguments;
      for (var i = funcs.length - 1; i >= 0; i--) {
        args = [funcs[i].apply(this, args)];
      }
      return args[0];
    };
  };

  // Returns a function that will only be executed after being called N times.
  _.after = function(times, func) {
    return function() {
      if (--times < 1) {
        return func.apply(this, arguments);
      }
    };
  };

  // Object Functions
  // ----------------

  // Retrieve the names of an object's properties.
  // Delegates to **ECMAScript 5**'s native `Object.keys`
  _.keys = nativeKeys || function(obj) {
    if (obj !== Object(obj)) throw new TypeError('Invalid object');
    var keys = [];
    for (var key in obj) if (_.has(obj, key)) keys.push(key);
    return keys;
  };

  // Retrieve the values of an object's properties.
  _.values = function(obj) {
    var values = [];
    for (var key in obj) if (_.has(obj, key)) values.push(obj[key]);
    return values;
  };

  // Convert an object into a list of `[key, value]` pairs.
  _.pairs = function(obj) {
    var pairs = [];
    for (var key in obj) if (_.has(obj, key)) pairs.push([key, obj[key]]);
    return pairs;
  };

  // Invert the keys and values of an object. The values must be serializable.
  _.invert = function(obj) {
    var result = {};
    for (var key in obj) if (_.has(obj, key)) result[obj[key]] = key;
    return result;
  };

  // Return a sorted list of the function names available on the object.
  // Aliased as `methods`
  _.functions = _.methods = function(obj) {
    var names = [];
    for (var key in obj) {
      if (_.isFunction(obj[key])) names.push(key);
    }
    return names.sort();
  };

  // Extend a given object with all the properties in passed-in object(s).
  _.extend = function(obj) {
    each(slice.call(arguments, 1), function(source) {
      if (source) {
        for (var prop in source) {
          obj[prop] = source[prop];
        }
      }
    });
    return obj;
  };

  // Return a copy of the object only containing the whitelisted properties.
  _.pick = function(obj) {
    var copy = {};
    var keys = concat.apply(ArrayProto, slice.call(arguments, 1));
    each(keys, function(key) {
      if (key in obj) copy[key] = obj[key];
    });
    return copy;
  };

   // Return a copy of the object without the blacklisted properties.
  _.omit = function(obj) {
    var copy = {};
    var keys = concat.apply(ArrayProto, slice.call(arguments, 1));
    for (var key in obj) {
      if (!_.contains(keys, key)) copy[key] = obj[key];
    }
    return copy;
  };

  // Fill in a given object with default properties.
  _.defaults = function(obj) {
    each(slice.call(arguments, 1), function(source) {
      if (source) {
        for (var prop in source) {
          if (obj[prop] === void 0) obj[prop] = source[prop];
        }
      }
    });
    return obj;
  };

  // Create a (shallow-cloned) duplicate of an object.
  _.clone = function(obj) {
    if (!_.isObject(obj)) return obj;
    return _.isArray(obj) ? obj.slice() : _.extend({}, obj);
  };

  // Invokes interceptor with the obj, and then returns obj.
  // The primary purpose of this method is to "tap into" a method chain, in
  // order to perform operations on intermediate results within the chain.
  _.tap = function(obj, interceptor) {
    interceptor(obj);
    return obj;
  };

  // Internal recursive comparison function for `isEqual`.
  var eq = function(a, b, aStack, bStack) {
    // Identical objects are equal. `0 === -0`, but they aren't identical.
    // See the [Harmony `egal` proposal](http://wiki.ecmascript.org/doku.php?id=harmony:egal).
    if (a === b) return a !== 0 || 1 / a == 1 / b;
    // A strict comparison is necessary because `null == undefined`.
    if (a == null || b == null) return a === b;
    // Unwrap any wrapped objects.
    if (a instanceof _) a = a._wrapped;
    if (b instanceof _) b = b._wrapped;
    // Compare `[[Class]]` names.
    var className = toString.call(a);
    if (className != toString.call(b)) return false;
    switch (className) {
      // Strings, numbers, dates, and booleans are compared by value.
      case '[object String]':
        // Primitives and their corresponding object wrappers are equivalent; thus, `"5"` is
        // equivalent to `new String("5")`.
        return a == String(b);
      case '[object Number]':
        // `NaN`s are equivalent, but non-reflexive. An `egal` comparison is performed for
        // other numeric values.
        return a != +a ? b != +b : (a == 0 ? 1 / a == 1 / b : a == +b);
      case '[object Date]':
      case '[object Boolean]':
        // Coerce dates and booleans to numeric primitive values. Dates are compared by their
        // millisecond representations. Note that invalid dates with millisecond representations
        // of `NaN` are not equivalent.
        return +a == +b;
      // RegExps are compared by their source patterns and flags.
      case '[object RegExp]':
        return a.source == b.source &&
               a.global == b.global &&
               a.multiline == b.multiline &&
               a.ignoreCase == b.ignoreCase;
    }
    if (typeof a != 'object' || typeof b != 'object') return false;
    // Assume equality for cyclic structures. The algorithm for detecting cyclic
    // structures is adapted from ES 5.1 section 15.12.3, abstract operation `JO`.
    var length = aStack.length;
    while (length--) {
      // Linear search. Performance is inversely proportional to the number of
      // unique nested structures.
      if (aStack[length] == a) return bStack[length] == b;
    }
    // Objects with different constructors are not equivalent, but `Object`s
    // from different frames are.
    var aCtor = a.constructor, bCtor = b.constructor;
    if (aCtor !== bCtor && !(_.isFunction(aCtor) && (aCtor instanceof aCtor) &&
                             _.isFunction(bCtor) && (bCtor instanceof bCtor))) {
      return false;
    }
    // Add the first object to the stack of traversed objects.
    aStack.push(a);
    bStack.push(b);
    var size = 0, result = true;
    // Recursively compare objects and arrays.
    if (className == '[object Array]') {
      // Compare array lengths to determine if a deep comparison is necessary.
      size = a.length;
      result = size == b.length;
      if (result) {
        // Deep compare the contents, ignoring non-numeric properties.
        while (size--) {
          if (!(result = eq(a[size], b[size], aStack, bStack))) break;
        }
      }
    } else {
      // Deep compare objects.
      for (var key in a) {
        if (_.has(a, key)) {
          // Count the expected number of properties.
          size++;
          // Deep compare each member.
          if (!(result = _.has(b, key) && eq(a[key], b[key], aStack, bStack))) break;
        }
      }
      // Ensure that both objects contain the same number of properties.
      if (result) {
        for (key in b) {
          if (_.has(b, key) && !(size--)) break;
        }
        result = !size;
      }
    }
    // Remove the first object from the stack of traversed objects.
    aStack.pop();
    bStack.pop();
    return result;
  };

  // Perform a deep comparison to check if two objects are equal.
  _.isEqual = function(a, b) {
    return eq(a, b, [], []);
  };

  // Is a given array, string, or object empty?
  // An "empty" object has no enumerable own-properties.
  _.isEmpty = function(obj) {
    if (obj == null) return true;
    if (_.isArray(obj) || _.isString(obj)) return obj.length === 0;
    for (var key in obj) if (_.has(obj, key)) return false;
    return true;
  };

  // Is a given value a DOM element?
  _.isElement = function(obj) {
    return !!(obj && obj.nodeType === 1);
  };

  // Is a given value an array?
  // Delegates to ECMA5's native Array.isArray
  _.isArray = nativeIsArray || function(obj) {
    return toString.call(obj) == '[object Array]';
  };

  // Is a given variable an object?
  _.isObject = function(obj) {
    return obj === Object(obj);
  };

  // Add some isType methods: isArguments, isFunction, isString, isNumber, isDate, isRegExp.
  each(['Arguments', 'Function', 'String', 'Number', 'Date', 'RegExp'], function(name) {
    _['is' + name] = function(obj) {
      return toString.call(obj) == '[object ' + name + ']';
    };
  });

  // Define a fallback version of the method in browsers (ahem, IE), where
  // there isn't any inspectable "Arguments" type.
  if (!_.isArguments(arguments)) {
    _.isArguments = function(obj) {
      return !!(obj && _.has(obj, 'callee'));
    };
  }

  // Optimize `isFunction` if appropriate.
  if (typeof (/./) !== 'function') {
    _.isFunction = function(obj) {
      return typeof obj === 'function';
    };
  }

  // Is a given object a finite number?
  _.isFinite = function(obj) {
    return isFinite(obj) && !isNaN(parseFloat(obj));
  };

  // Is the given value `NaN`? (NaN is the only number which does not equal itself).
  _.isNaN = function(obj) {
    return _.isNumber(obj) && obj != +obj;
  };

  // Is a given value a boolean?
  _.isBoolean = function(obj) {
    return obj === true || obj === false || toString.call(obj) == '[object Boolean]';
  };

  // Is a given value equal to null?
  _.isNull = function(obj) {
    return obj === null;
  };

  // Is a given variable undefined?
  _.isUndefined = function(obj) {
    return obj === void 0;
  };

  // Shortcut function for checking if an object has a given property directly
  // on itself (in other words, not on a prototype).
  _.has = function(obj, key) {
    return hasOwnProperty.call(obj, key);
  };

  // Utility Functions
  // -----------------

  // Run Underscore.js in *noConflict* mode, returning the `_` variable to its
  // previous owner. Returns a reference to the Underscore object.
  _.noConflict = function() {
    root._ = previousUnderscore;
    return this;
  };

  // Keep the identity function around for default iterators.
  _.identity = function(value) {
    return value;
  };

  // Run a function **n** times.
  _.times = function(n, iterator, context) {
    var accum = Array(Math.max(0, n));
    for (var i = 0; i < n; i++) accum[i] = iterator.call(context, i);
    return accum;
  };

  // Return a random integer between min and max (inclusive).
  _.random = function(min, max) {
    if (max == null) {
      max = min;
      min = 0;
    }
    return min + Math.floor(Math.random() * (max - min + 1));
  };

  // List of HTML entities for escaping.
  var entityMap = {
    escape: {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#x27;',
      '/': '&#x2F;'
    }
  };
  entityMap.unescape = _.invert(entityMap.escape);

  // Regexes containing the keys and values listed immediately above.
  var entityRegexes = {
    escape:   new RegExp('[' + _.keys(entityMap.escape).join('') + ']', 'g'),
    unescape: new RegExp('(' + _.keys(entityMap.unescape).join('|') + ')', 'g')
  };

  // Functions for escaping and unescaping strings to/from HTML interpolation.
  _.each(['escape', 'unescape'], function(method) {
    _[method] = function(string) {
      if (string == null) return '';
      return ('' + string).replace(entityRegexes[method], function(match) {
        return entityMap[method][match];
      });
    };
  });

  // If the value of the named `property` is a function then invoke it with the
  // `object` as context; otherwise, return it.
  _.result = function(object, property) {
    if (object == null) return void 0;
    var value = object[property];
    return _.isFunction(value) ? value.call(object) : value;
  };

  // Add your own custom functions to the Underscore object.
  _.mixin = function(obj) {
    each(_.functions(obj), function(name){
      var func = _[name] = obj[name];
      _.prototype[name] = function() {
        var args = [this._wrapped];
        push.apply(args, arguments);
        return result.call(this, func.apply(_, args));
      };
    });
  };

  // Generate a unique integer id (unique within the entire client session).
  // Useful for temporary DOM ids.
  var idCounter = 0;
  _.uniqueId = function(prefix) {
    var id = ++idCounter + '';
    return prefix ? prefix + id : id;
  };

  // By default, Underscore uses ERB-style template delimiters, change the
  // following template settings to use alternative delimiters.
  _.templateSettings = {
    evaluate    : /<%([\s\S]+?)%>/g,
    interpolate : /<%=([\s\S]+?)%>/g,
    escape      : /<%-([\s\S]+?)%>/g
  };

  // When customizing `templateSettings`, if you don't want to define an
  // interpolation, evaluation or escaping regex, we need one that is
  // guaranteed not to match.
  var noMatch = /(.)^/;

  // Certain characters need to be escaped so that they can be put into a
  // string literal.
  var escapes = {
    "'":      "'",
    '\\':     '\\',
    '\r':     'r',
    '\n':     'n',
    '\t':     't',
    '\u2028': 'u2028',
    '\u2029': 'u2029'
  };

  var escaper = /\\|'|\r|\n|\t|\u2028|\u2029/g;

  // JavaScript micro-templating, similar to John Resig's implementation.
  // Underscore templating handles arbitrary delimiters, preserves whitespace,
  // and correctly escapes quotes within interpolated code.
  _.template = function(text, data, settings) {
    var render;
    settings = _.defaults({}, settings, _.templateSettings);

    // Combine delimiters into one regular expression via alternation.
    var matcher = new RegExp([
      (settings.escape || noMatch).source,
      (settings.interpolate || noMatch).source,
      (settings.evaluate || noMatch).source
    ].join('|') + '|$', 'g');

    // Compile the template source, escaping string literals appropriately.
    var index = 0;
    var source = "__p+='";
    text.replace(matcher, function(match, escape, interpolate, evaluate, offset) {
      source += text.slice(index, offset)
        .replace(escaper, function(match) { return '\\' + escapes[match]; });

      if (escape) {
        source += "'+\n((__t=(" + escape + "))==null?'':_.escape(__t))+\n'";
      }
      if (interpolate) {
        source += "'+\n((__t=(" + interpolate + "))==null?'':__t)+\n'";
      }
      if (evaluate) {
        source += "';\n" + evaluate + "\n__p+='";
      }
      index = offset + match.length;
      return match;
    });
    source += "';\n";

    // If a variable is not specified, place data values in local scope.
    if (!settings.variable) source = 'with(obj||{}){\n' + source + '}\n';

    source = "var __t,__p='',__j=Array.prototype.join," +
      "print=function(){__p+=__j.call(arguments,'');};\n" +
      source + "return __p;\n";

    try {
      render = new Function(settings.variable || 'obj', '_', source);
    } catch (e) {
      e.source = source;
      throw e;
    }

    if (data) return render(data, _);
    var template = function(data) {
      return render.call(this, data, _);
    };

    // Provide the compiled function source as a convenience for precompilation.
    template.source = 'function(' + (settings.variable || 'obj') + '){\n' + source + '}';

    return template;
  };

  // Add a "chain" function, which will delegate to the wrapper.
  _.chain = function(obj) {
    return _(obj).chain();
  };

  // OOP
  // ---------------
  // If Underscore is called as a function, it returns a wrapped object that
  // can be used OO-style. This wrapper holds altered versions of all the
  // underscore functions. Wrapped objects may be chained.

  // Helper function to continue chaining intermediate results.
  var result = function(obj) {
    return this._chain ? _(obj).chain() : obj;
  };

  // Add all of the Underscore functions to the wrapper object.
  _.mixin(_);

  // Add all mutator Array functions to the wrapper.
  each(['pop', 'push', 'reverse', 'shift', 'sort', 'splice', 'unshift'], function(name) {
    var method = ArrayProto[name];
    _.prototype[name] = function() {
      var obj = this._wrapped;
      method.apply(obj, arguments);
      if ((name == 'shift' || name == 'splice') && obj.length === 0) delete obj[0];
      return result.call(this, obj);
    };
  });

  // Add all accessor Array functions to the wrapper.
  each(['concat', 'join', 'slice'], function(name) {
    var method = ArrayProto[name];
    _.prototype[name] = function() {
      return result.call(this, method.apply(this._wrapped, arguments));
    };
  });

  _.extend(_.prototype, {

    // Start chaining a wrapped Underscore object.
    chain: function() {
      this._chain = true;
      return this;
    },

    // Extracts the result from a wrapped and chained object.
    value: function() {
      return this._wrapped;
    }

  });

}).call(this);

define("Underscore", (function (global) {
    return function () {
        var ret, fn;
        return ret || global._;
    };
}(this)));

//     Backbone.js 1.0.0

//     (c) 2010-2013 Jeremy Ashkenas, DocumentCloud Inc.
//     Backbone may be freely distributed under the MIT license.
//     For all details and documentation:
//     http://backbonejs.org

(function(){

  // Initial Setup
  // -------------

  // Save a reference to the global object (`window` in the browser, `exports`
  // on the server).
  var root = this;

  // Save the previous value of the `Backbone` variable, so that it can be
  // restored later on, if `noConflict` is used.
  var previousBackbone = root.Backbone;

  // Create local references to array methods we'll want to use later.
  var array = [];
  var push = array.push;
  var slice = array.slice;
  var splice = array.splice;

  // The top-level namespace. All public Backbone classes and modules will
  // be attached to this. Exported for both the browser and the server.
  var Backbone;
  if (typeof exports !== 'undefined') {
    Backbone = exports;
  } else {
    Backbone = root.Backbone = {};
  }

  // Current version of the library. Keep in sync with `package.json`.
  Backbone.VERSION = '1.0.0';

  // Require Underscore, if we're on the server, and it's not already present.
  var _ = root._;
  if (!_ && (typeof require !== 'undefined')) _ = require('underscore');

  // For Backbone's purposes, jQuery, Zepto, Ender, or My Library (kidding) owns
  // the `$` variable.
  Backbone.$ = root.jQuery || root.Zepto || root.ender || root.$;

  // Runs Backbone.js in *noConflict* mode, returning the `Backbone` variable
  // to its previous owner. Returns a reference to this Backbone object.
  Backbone.noConflict = function() {
    root.Backbone = previousBackbone;
    return this;
  };

  // Turn on `emulateHTTP` to support legacy HTTP servers. Setting this option
  // will fake `"PUT"` and `"DELETE"` requests via the `_method` parameter and
  // set a `X-Http-Method-Override` header.
  Backbone.emulateHTTP = false;

  // Turn on `emulateJSON` to support legacy servers that can't deal with direct
  // `application/json` requests ... will encode the body as
  // `application/x-www-form-urlencoded` instead and will send the model in a
  // form param named `model`.
  Backbone.emulateJSON = false;

  // Backbone.Events
  // ---------------

  // A module that can be mixed in to *any object* in order to provide it with
  // custom events. You may bind with `on` or remove with `off` callback
  // functions to an event; `trigger`-ing an event fires all callbacks in
  // succession.
  //
  //     var object = {};
  //     _.extend(object, Backbone.Events);
  //     object.on('expand', function(){ alert('expanded'); });
  //     object.trigger('expand');
  //
  var Events = Backbone.Events = {

    // Bind an event to a `callback` function. Passing `"all"` will bind
    // the callback to all events fired.
    on: function(name, callback, context) {
      if (!eventsApi(this, 'on', name, [callback, context]) || !callback) return this;
      this._events || (this._events = {});
      var events = this._events[name] || (this._events[name] = []);
      events.push({callback: callback, context: context, ctx: context || this});
      return this;
    },

    // Bind an event to only be triggered a single time. After the first time
    // the callback is invoked, it will be removed.
    once: function(name, callback, context) {
      if (!eventsApi(this, 'once', name, [callback, context]) || !callback) return this;
      var self = this;
      var once = _.once(function() {
        self.off(name, once);
        callback.apply(this, arguments);
      });
      once._callback = callback;
      return this.on(name, once, context);
    },

    // Remove one or many callbacks. If `context` is null, removes all
    // callbacks with that function. If `callback` is null, removes all
    // callbacks for the event. If `name` is null, removes all bound
    // callbacks for all events.
    off: function(name, callback, context) {
      var retain, ev, events, names, i, l, j, k;
      if (!this._events || !eventsApi(this, 'off', name, [callback, context])) return this;
      if (!name && !callback && !context) {
        this._events = {};
        return this;
      }

      names = name ? [name] : _.keys(this._events);
      for (i = 0, l = names.length; i < l; i++) {
        name = names[i];
        if (events = this._events[name]) {
          this._events[name] = retain = [];
          if (callback || context) {
            for (j = 0, k = events.length; j < k; j++) {
              ev = events[j];
              if ((callback && callback !== ev.callback && callback !== ev.callback._callback) ||
                  (context && context !== ev.context)) {
                retain.push(ev);
              }
            }
          }
          if (!retain.length) delete this._events[name];
        }
      }

      return this;
    },

    // Trigger one or many events, firing all bound callbacks. Callbacks are
    // passed the same arguments as `trigger` is, apart from the event name
    // (unless you're listening on `"all"`, which will cause your callback to
    // receive the true name of the event as the first argument).
    trigger: function(name) {
      if (!this._events) return this;
      var args = slice.call(arguments, 1);
      if (!eventsApi(this, 'trigger', name, args)) return this;
      var events = this._events[name];
      var allEvents = this._events.all;
      if (events) triggerEvents(events, args);
      if (allEvents) triggerEvents(allEvents, arguments);
      return this;
    },

    // Tell this object to stop listening to either specific events ... or
    // to every object it's currently listening to.
    stopListening: function(obj, name, callback) {
      var listeners = this._listeners;
      if (!listeners) return this;
      var deleteListener = !name && !callback;
      if (typeof name === 'object') callback = this;
      if (obj) (listeners = {})[obj._listenerId] = obj;
      for (var id in listeners) {
        listeners[id].off(name, callback, this);
        if (deleteListener) delete this._listeners[id];
      }
      return this;
    }

  };

  // Regular expression used to split event strings.
  var eventSplitter = /\s+/;

  // Implement fancy features of the Events API such as multiple event
  // names `"change blur"` and jQuery-style event maps `{change: action}`
  // in terms of the existing API.
  var eventsApi = function(obj, action, name, rest) {
    if (!name) return true;

    // Handle event maps.
    if (typeof name === 'object') {
      for (var key in name) {
        obj[action].apply(obj, [key, name[key]].concat(rest));
      }
      return false;
    }

    // Handle space separated event names.
    if (eventSplitter.test(name)) {
      var names = name.split(eventSplitter);
      for (var i = 0, l = names.length; i < l; i++) {
        obj[action].apply(obj, [names[i]].concat(rest));
      }
      return false;
    }

    return true;
  };

  // A difficult-to-believe, but optimized internal dispatch function for
  // triggering events. Tries to keep the usual cases speedy (most internal
  // Backbone events have 3 arguments).
  var triggerEvents = function(events, args) {
    var ev, i = -1, l = events.length, a1 = args[0], a2 = args[1], a3 = args[2];
    switch (args.length) {
      case 0: while (++i < l) (ev = events[i]).callback.call(ev.ctx); return;
      case 1: while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1); return;
      case 2: while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2); return;
      case 3: while (++i < l) (ev = events[i]).callback.call(ev.ctx, a1, a2, a3); return;
      default: while (++i < l) (ev = events[i]).callback.apply(ev.ctx, args);
    }
  };

  var listenMethods = {listenTo: 'on', listenToOnce: 'once'};

  // Inversion-of-control versions of `on` and `once`. Tell *this* object to
  // listen to an event in another object ... keeping track of what it's
  // listening to.
  _.each(listenMethods, function(implementation, method) {
    Events[method] = function(obj, name, callback) {
      var listeners = this._listeners || (this._listeners = {});
      var id = obj._listenerId || (obj._listenerId = _.uniqueId('l'));
      listeners[id] = obj;
      if (typeof name === 'object') callback = this;
      obj[implementation](name, callback, this);
      return this;
    };
  });

  // Aliases for backwards compatibility.
  Events.bind   = Events.on;
  Events.unbind = Events.off;

  // Allow the `Backbone` object to serve as a global event bus, for folks who
  // want global "pubsub" in a convenient place.
  _.extend(Backbone, Events);

  // Backbone.Model
  // --------------

  // Backbone **Models** are the basic data object in the framework --
  // frequently representing a row in a table in a database on your server.
  // A discrete chunk of data and a bunch of useful, related methods for
  // performing computations and transformations on that data.

  // Create a new model with the specified attributes. A client id (`cid`)
  // is automatically generated and assigned for you.
  var Model = Backbone.Model = function(attributes, options) {
    var defaults;
    var attrs = attributes || {};
    options || (options = {});
    this.cid = _.uniqueId('c');
    this.attributes = {};
    _.extend(this, _.pick(options, modelOptions));
    if (options.parse) attrs = this.parse(attrs, options) || {};
    if (defaults = _.result(this, 'defaults')) {
      attrs = _.defaults({}, attrs, defaults);
    }
    this.set(attrs, options);
    this.changed = {};
    this.initialize.apply(this, arguments);
  };

  // A list of options to be attached directly to the model, if provided.
  var modelOptions = ['url', 'urlRoot', 'collection'];

  // Attach all inheritable methods to the Model prototype.
  _.extend(Model.prototype, Events, {

    // A hash of attributes whose current and previous value differ.
    changed: null,

    // The value returned during the last failed validation.
    validationError: null,

    // The default name for the JSON `id` attribute is `"id"`. MongoDB and
    // CouchDB users may want to set this to `"_id"`.
    idAttribute: 'id',

    // Initialize is an empty function by default. Override it with your own
    // initialization logic.
    initialize: function(){},

    // Return a copy of the model's `attributes` object.
    toJSON: function(options) {
      return _.clone(this.attributes);
    },

    // Proxy `Backbone.sync` by default -- but override this if you need
    // custom syncing semantics for *this* particular model.
    sync: function() {
      return Backbone.sync.apply(this, arguments);
    },

    // Get the value of an attribute.
    get: function(attr) {
      return this.attributes[attr];
    },

    // Get the HTML-escaped value of an attribute.
    escape: function(attr) {
      return _.escape(this.get(attr));
    },

    // Returns `true` if the attribute contains a value that is not null
    // or undefined.
    has: function(attr) {
      return this.get(attr) != null;
    },

    // Set a hash of model attributes on the object, firing `"change"`. This is
    // the core primitive operation of a model, updating the data and notifying
    // anyone who needs to know about the change in state. The heart of the beast.
    set: function(key, val, options) {
      var attr, attrs, unset, changes, silent, changing, prev, current;
      if (key == null) return this;

      // Handle both `"key", value` and `{key: value}` -style arguments.
      if (typeof key === 'object') {
        attrs = key;
        options = val;
      } else {
        (attrs = {})[key] = val;
      }

      options || (options = {});

      // Run validation.
      if (!this._validate(attrs, options)) return false;

      // Extract attributes and options.
      unset           = options.unset;
      silent          = options.silent;
      changes         = [];
      changing        = this._changing;
      this._changing  = true;

      if (!changing) {
        this._previousAttributes = _.clone(this.attributes);
        this.changed = {};
      }
      current = this.attributes, prev = this._previousAttributes;

      // Check for changes of `id`.
      if (this.idAttribute in attrs) this.id = attrs[this.idAttribute];

      // For each `set` attribute, update or delete the current value.
      for (attr in attrs) {
        val = attrs[attr];
        if (!_.isEqual(current[attr], val)) changes.push(attr);
        if (!_.isEqual(prev[attr], val)) {
          this.changed[attr] = val;
        } else {
          delete this.changed[attr];
        }
        unset ? delete current[attr] : current[attr] = val;
      }

      // Trigger all relevant attribute changes.
      if (!silent) {
        if (changes.length) this._pending = true;
        for (var i = 0, l = changes.length; i < l; i++) {
          this.trigger('change:' + changes[i], this, current[changes[i]], options);
        }
      }

      // You might be wondering why there's a `while` loop here. Changes can
      // be recursively nested within `"change"` events.
      if (changing) return this;
      if (!silent) {
        while (this._pending) {
          this._pending = false;
          this.trigger('change', this, options);
        }
      }
      this._pending = false;
      this._changing = false;
      return this;
    },

    // Remove an attribute from the model, firing `"change"`. `unset` is a noop
    // if the attribute doesn't exist.
    unset: function(attr, options) {
      return this.set(attr, void 0, _.extend({}, options, {unset: true}));
    },

    // Clear all attributes on the model, firing `"change"`.
    clear: function(options) {
      var attrs = {};
      for (var key in this.attributes) attrs[key] = void 0;
      return this.set(attrs, _.extend({}, options, {unset: true}));
    },

    // Determine if the model has changed since the last `"change"` event.
    // If you specify an attribute name, determine if that attribute has changed.
    hasChanged: function(attr) {
      if (attr == null) return !_.isEmpty(this.changed);
      return _.has(this.changed, attr);
    },

    // Return an object containing all the attributes that have changed, or
    // false if there are no changed attributes. Useful for determining what
    // parts of a view need to be updated and/or what attributes need to be
    // persisted to the server. Unset attributes will be set to undefined.
    // You can also pass an attributes object to diff against the model,
    // determining if there *would be* a change.
    changedAttributes: function(diff) {
      if (!diff) return this.hasChanged() ? _.clone(this.changed) : false;
      var val, changed = false;
      var old = this._changing ? this._previousAttributes : this.attributes;
      for (var attr in diff) {
        if (_.isEqual(old[attr], (val = diff[attr]))) continue;
        (changed || (changed = {}))[attr] = val;
      }
      return changed;
    },

    // Get the previous value of an attribute, recorded at the time the last
    // `"change"` event was fired.
    previous: function(attr) {
      if (attr == null || !this._previousAttributes) return null;
      return this._previousAttributes[attr];
    },

    // Get all of the attributes of the model at the time of the previous
    // `"change"` event.
    previousAttributes: function() {
      return _.clone(this._previousAttributes);
    },

    // Fetch the model from the server. If the server's representation of the
    // model differs from its current attributes, they will be overridden,
    // triggering a `"change"` event.
    fetch: function(options) {
      options = options ? _.clone(options) : {};
      if (options.parse === void 0) options.parse = true;
      var model = this;
      var success = options.success;
      options.success = function(resp) {
        if (!model.set(model.parse(resp, options), options)) return false;
        if (success) success(model, resp, options);
        model.trigger('sync', model, resp, options);
      };
      wrapError(this, options);
      return this.sync('read', this, options);
    },

    // Set a hash of model attributes, and sync the model to the server.
    // If the server returns an attributes hash that differs, the model's
    // state will be `set` again.
    save: function(key, val, options) {
      var attrs, method, xhr, attributes = this.attributes;

      // Handle both `"key", value` and `{key: value}` -style arguments.
      if (key == null || typeof key === 'object') {
        attrs = key;
        options = val;
      } else {
        (attrs = {})[key] = val;
      }

      // If we're not waiting and attributes exist, save acts as `set(attr).save(null, opts)`.
      if (attrs && (!options || !options.wait) && !this.set(attrs, options)) return false;

      options = _.extend({validate: true}, options);

      // Do not persist invalid models.
      if (!this._validate(attrs, options)) return false;

      // Set temporary attributes if `{wait: true}`.
      if (attrs && options.wait) {
        this.attributes = _.extend({}, attributes, attrs);
      }

      // After a successful server-side save, the client is (optionally)
      // updated with the server-side state.
      if (options.parse === void 0) options.parse = true;
      var model = this;
      var success = options.success;
      options.success = function(resp) {
        // Ensure attributes are restored during synchronous saves.
        model.attributes = attributes;
        var serverAttrs = model.parse(resp, options);
        if (options.wait) serverAttrs = _.extend(attrs || {}, serverAttrs);
        if (_.isObject(serverAttrs) && !model.set(serverAttrs, options)) {
          return false;
        }
        if (success) success(model, resp, options);
        model.trigger('sync', model, resp, options);
      };
      wrapError(this, options);

      method = this.isNew() ? 'create' : (options.patch ? 'patch' : 'update');
      if (method === 'patch') options.attrs = attrs;
      xhr = this.sync(method, this, options);

      // Restore attributes.
      if (attrs && options.wait) this.attributes = attributes;

      return xhr;
    },

    // Destroy this model on the server if it was already persisted.
    // Optimistically removes the model from its collection, if it has one.
    // If `wait: true` is passed, waits for the server to respond before removal.
    destroy: function(options) {
      options = options ? _.clone(options) : {};
      var model = this;
      var success = options.success;

      var destroy = function() {
        model.trigger('destroy', model, model.collection, options);
      };

      options.success = function(resp) {
        if (options.wait || model.isNew()) destroy();
        if (success) success(model, resp, options);
        if (!model.isNew()) model.trigger('sync', model, resp, options);
      };

      if (this.isNew()) {
        options.success();
        return false;
      }
      wrapError(this, options);

      var xhr = this.sync('delete', this, options);
      if (!options.wait) destroy();
      return xhr;
    },

    // Default URL for the model's representation on the server -- if you're
    // using Backbone's restful methods, override this to change the endpoint
    // that will be called.
    url: function() {
      var base = _.result(this, 'urlRoot') || _.result(this.collection, 'url') || urlError();
      if (this.isNew()) return base;
      return base + (base.charAt(base.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.id);
    },

    // **parse** converts a response into the hash of attributes to be `set` on
    // the model. The default implementation is just to pass the response along.
    parse: function(resp, options) {
      return resp;
    },

    // Create a new model with identical attributes to this one.
    clone: function() {
      return new this.constructor(this.attributes);
    },

    // A model is new if it has never been saved to the server, and lacks an id.
    isNew: function() {
      return this.id == null;
    },

    // Check if the model is currently in a valid state.
    isValid: function(options) {
      return this._validate({}, _.extend(options || {}, { validate: true }));
    },

    // Run validation against the next complete set of model attributes,
    // returning `true` if all is well. Otherwise, fire an `"invalid"` event.
    _validate: function(attrs, options) {
      if (!options.validate || !this.validate) return true;
      attrs = _.extend({}, this.attributes, attrs);
      var error = this.validationError = this.validate(attrs, options) || null;
      if (!error) return true;
      this.trigger('invalid', this, error, _.extend(options || {}, {validationError: error}));
      return false;
    }

  });

  // Underscore methods that we want to implement on the Model.
  var modelMethods = ['keys', 'values', 'pairs', 'invert', 'pick', 'omit'];

  // Mix in each Underscore method as a proxy to `Model#attributes`.
  _.each(modelMethods, function(method) {
    Model.prototype[method] = function() {
      var args = slice.call(arguments);
      args.unshift(this.attributes);
      return _[method].apply(_, args);
    };
  });

  // Backbone.Collection
  // -------------------

  // If models tend to represent a single row of data, a Backbone Collection is
  // more analagous to a table full of data ... or a small slice or page of that
  // table, or a collection of rows that belong together for a particular reason
  // -- all of the messages in this particular folder, all of the documents
  // belonging to this particular author, and so on. Collections maintain
  // indexes of their models, both in order, and for lookup by `id`.

  // Create a new **Collection**, perhaps to contain a specific type of `model`.
  // If a `comparator` is specified, the Collection will maintain
  // its models in sort order, as they're added and removed.
  var Collection = Backbone.Collection = function(models, options) {
    options || (options = {});
    if (options.url) this.url = options.url;
    if (options.model) this.model = options.model;
    if (options.comparator !== void 0) this.comparator = options.comparator;
    this._reset();
    this.initialize.apply(this, arguments);
    if (models) this.reset(models, _.extend({silent: true}, options));
  };

  // Default options for `Collection#set`.
  var setOptions = {add: true, remove: true, merge: true};
  var addOptions = {add: true, merge: false, remove: false};

  // Define the Collection's inheritable methods.
  _.extend(Collection.prototype, Events, {

    // The default model for a collection is just a **Backbone.Model**.
    // This should be overridden in most cases.
    model: Model,

    // Initialize is an empty function by default. Override it with your own
    // initialization logic.
    initialize: function(){},

    // The JSON representation of a Collection is an array of the
    // models' attributes.
    toJSON: function(options) {
      return this.map(function(model){ return model.toJSON(options); });
    },

    // Proxy `Backbone.sync` by default.
    sync: function() {
      return Backbone.sync.apply(this, arguments);
    },

    // Add a model, or list of models to the set.
    add: function(models, options) {
      return this.set(models, _.defaults(options || {}, addOptions));
    },

    // Remove a model, or a list of models from the set.
    remove: function(models, options) {
      models = _.isArray(models) ? models.slice() : [models];
      options || (options = {});
      var i, l, index, model;
      for (i = 0, l = models.length; i < l; i++) {
        model = this.get(models[i]);
        if (!model) continue;
        delete this._byId[model.id];
        delete this._byId[model.cid];
        index = this.indexOf(model);
        this.models.splice(index, 1);
        this.length--;
        if (!options.silent) {
          options.index = index;
          model.trigger('remove', model, this, options);
        }
        this._removeReference(model);
      }
      return this;
    },

    // Update a collection by `set`-ing a new list of models, adding new ones,
    // removing models that are no longer present, and merging models that
    // already exist in the collection, as necessary. Similar to **Model#set**,
    // the core operation for updating the data contained by the collection.
    set: function(models, options) {
      options = _.defaults(options || {}, setOptions);
      if (options.parse) models = this.parse(models, options);
      if (!_.isArray(models)) models = models ? [models] : [];
      var i, l, model, attrs, existing, sort;
      var at = options.at;
      var sortable = this.comparator && (at == null) && options.sort !== false;
      var sortAttr = _.isString(this.comparator) ? this.comparator : null;
      var toAdd = [], toRemove = [], modelMap = {};

      // Turn bare objects into model references, and prevent invalid models
      // from being added.
      for (i = 0, l = models.length; i < l; i++) {
        if (!(model = this._prepareModel(models[i], options))) continue;

        // If a duplicate is found, prevent it from being added and
        // optionally merge it into the existing model.
        if (existing = this.get(model)) {
          if (options.remove) modelMap[existing.cid] = true;
          if (options.merge) {
            existing.set(model.attributes, options);
            if (sortable && !sort && existing.hasChanged(sortAttr)) sort = true;
          }

        // This is a new model, push it to the `toAdd` list.
        } else if (options.add) {
          toAdd.push(model);

          // Listen to added models' events, and index models for lookup by
          // `id` and by `cid`.
          model.on('all', this._onModelEvent, this);
          this._byId[model.cid] = model;
          if (model.id != null) this._byId[model.id] = model;
        }
      }

      // Remove nonexistent models if appropriate.
      if (options.remove) {
        for (i = 0, l = this.length; i < l; ++i) {
          if (!modelMap[(model = this.models[i]).cid]) toRemove.push(model);
        }
        if (toRemove.length) this.remove(toRemove, options);
      }

      // See if sorting is needed, update `length` and splice in new models.
      if (toAdd.length) {
        if (sortable) sort = true;
        this.length += toAdd.length;
        if (at != null) {
          splice.apply(this.models, [at, 0].concat(toAdd));
        } else {
          push.apply(this.models, toAdd);
        }
      }

      // Silently sort the collection if appropriate.
      if (sort) this.sort({silent: true});

      if (options.silent) return this;

      // Trigger `add` events.
      for (i = 0, l = toAdd.length; i < l; i++) {
        (model = toAdd[i]).trigger('add', model, this, options);
      }

      // Trigger `sort` if the collection was sorted.
      if (sort) this.trigger('sort', this, options);
      return this;
    },

    // When you have more items than you want to add or remove individually,
    // you can reset the entire set with a new list of models, without firing
    // any granular `add` or `remove` events. Fires `reset` when finished.
    // Useful for bulk operations and optimizations.
    reset: function(models, options) {
      options || (options = {});
      for (var i = 0, l = this.models.length; i < l; i++) {
        this._removeReference(this.models[i]);
      }
      options.previousModels = this.models;
      this._reset();
      this.add(models, _.extend({silent: true}, options));
      if (!options.silent) this.trigger('reset', this, options);
      return this;
    },

    // Add a model to the end of the collection.
    push: function(model, options) {
      model = this._prepareModel(model, options);
      this.add(model, _.extend({at: this.length}, options));
      return model;
    },

    // Remove a model from the end of the collection.
    pop: function(options) {
      var model = this.at(this.length - 1);
      this.remove(model, options);
      return model;
    },

    // Add a model to the beginning of the collection.
    unshift: function(model, options) {
      model = this._prepareModel(model, options);
      this.add(model, _.extend({at: 0}, options));
      return model;
    },

    // Remove a model from the beginning of the collection.
    shift: function(options) {
      var model = this.at(0);
      this.remove(model, options);
      return model;
    },

    // Slice out a sub-array of models from the collection.
    slice: function(begin, end) {
      return this.models.slice(begin, end);
    },

    // Get a model from the set by id.
    get: function(obj) {
      if (obj == null) return void 0;
      return this._byId[obj.id != null ? obj.id : obj.cid || obj];
    },

    // Get the model at the given index.
    at: function(index) {
      return this.models[index];
    },

    // Return models with matching attributes. Useful for simple cases of
    // `filter`.
    where: function(attrs, first) {
      if (_.isEmpty(attrs)) return first ? void 0 : [];
      return this[first ? 'find' : 'filter'](function(model) {
        for (var key in attrs) {
          if (attrs[key] !== model.get(key)) return false;
        }
        return true;
      });
    },

    // Return the first model with matching attributes. Useful for simple cases
    // of `find`.
    findWhere: function(attrs) {
      return this.where(attrs, true);
    },

    // Force the collection to re-sort itself. You don't need to call this under
    // normal circumstances, as the set will maintain sort order as each item
    // is added.
    sort: function(options) {
      if (!this.comparator) throw new Error('Cannot sort a set without a comparator');
      options || (options = {});

      // Run sort based on type of `comparator`.
      if (_.isString(this.comparator) || this.comparator.length === 1) {
        this.models = this.sortBy(this.comparator, this);
      } else {
        this.models.sort(_.bind(this.comparator, this));
      }

      if (!options.silent) this.trigger('sort', this, options);
      return this;
    },

    // Figure out the smallest index at which a model should be inserted so as
    // to maintain order.
    sortedIndex: function(model, value, context) {
      value || (value = this.comparator);
      var iterator = _.isFunction(value) ? value : function(model) {
        return model.get(value);
      };
      return _.sortedIndex(this.models, model, iterator, context);
    },

    // Pluck an attribute from each model in the collection.
    pluck: function(attr) {
      return _.invoke(this.models, 'get', attr);
    },

    // Fetch the default set of models for this collection, resetting the
    // collection when they arrive. If `reset: true` is passed, the response
    // data will be passed through the `reset` method instead of `set`.
    fetch: function(options) {
      options = options ? _.clone(options) : {};
      if (options.parse === void 0) options.parse = true;
      var success = options.success;
      var collection = this;
      options.success = function(resp) {
        var method = options.reset ? 'reset' : 'set';
        collection[method](resp, options);
        if (success) success(collection, resp, options);
        collection.trigger('sync', collection, resp, options);
      };
      wrapError(this, options);
      return this.sync('read', this, options);
    },

    // Create a new instance of a model in this collection. Add the model to the
    // collection immediately, unless `wait: true` is passed, in which case we
    // wait for the server to agree.
    create: function(model, options) {
      options = options ? _.clone(options) : {};
      if (!(model = this._prepareModel(model, options))) return false;
      if (!options.wait) this.add(model, options);
      var collection = this;
      var success = options.success;
      options.success = function(resp) {
        if (options.wait) collection.add(model, options);
        if (success) success(model, resp, options);
      };
      model.save(null, options);
      return model;
    },

    // **parse** converts a response into a list of models to be added to the
    // collection. The default implementation is just to pass it through.
    parse: function(resp, options) {
      return resp;
    },

    // Create a new collection with an identical list of models as this one.
    clone: function() {
      return new this.constructor(this.models);
    },

    // Private method to reset all internal state. Called when the collection
    // is first initialized or reset.
    _reset: function() {
      this.length = 0;
      this.models = [];
      this._byId  = {};
    },

    // Prepare a hash of attributes (or other model) to be added to this
    // collection.
    _prepareModel: function(attrs, options) {
      if (attrs instanceof Model) {
        if (!attrs.collection) attrs.collection = this;
        return attrs;
      }
      options || (options = {});
      options.collection = this;
      var model = new this.model(attrs, options);
      if (!model._validate(attrs, options)) {
        this.trigger('invalid', this, attrs, options);
        return false;
      }
      return model;
    },

    // Internal method to sever a model's ties to a collection.
    _removeReference: function(model) {
      if (this === model.collection) delete model.collection;
      model.off('all', this._onModelEvent, this);
    },

    // Internal method called every time a model in the set fires an event.
    // Sets need to update their indexes when models change ids. All other
    // events simply proxy through. "add" and "remove" events that originate
    // in other collections are ignored.
    _onModelEvent: function(event, model, collection, options) {
      if ((event === 'add' || event === 'remove') && collection !== this) return;
      if (event === 'destroy') this.remove(model, options);
      if (model && event === 'change:' + model.idAttribute) {
        delete this._byId[model.previous(model.idAttribute)];
        if (model.id != null) this._byId[model.id] = model;
      }
      this.trigger.apply(this, arguments);
    }

  });

  // Underscore methods that we want to implement on the Collection.
  // 90% of the core usefulness of Backbone Collections is actually implemented
  // right here:
  var methods = ['forEach', 'each', 'map', 'collect', 'reduce', 'foldl',
    'inject', 'reduceRight', 'foldr', 'find', 'detect', 'filter', 'select',
    'reject', 'every', 'all', 'some', 'any', 'include', 'contains', 'invoke',
    'max', 'min', 'toArray', 'size', 'first', 'head', 'take', 'initial', 'rest',
    'tail', 'drop', 'last', 'without', 'indexOf', 'shuffle', 'lastIndexOf',
    'isEmpty', 'chain'];

  // Mix in each Underscore method as a proxy to `Collection#models`.
  _.each(methods, function(method) {
    Collection.prototype[method] = function() {
      var args = slice.call(arguments);
      args.unshift(this.models);
      return _[method].apply(_, args);
    };
  });

  // Underscore methods that take a property name as an argument.
  var attributeMethods = ['groupBy', 'countBy', 'sortBy'];

  // Use attributes instead of properties.
  _.each(attributeMethods, function(method) {
    Collection.prototype[method] = function(value, context) {
      var iterator = _.isFunction(value) ? value : function(model) {
        return model.get(value);
      };
      return _[method](this.models, iterator, context);
    };
  });

  // Backbone.View
  // -------------

  // Backbone Views are almost more convention than they are actual code. A View
  // is simply a JavaScript object that represents a logical chunk of UI in the
  // DOM. This might be a single item, an entire list, a sidebar or panel, or
  // even the surrounding frame which wraps your whole app. Defining a chunk of
  // UI as a **View** allows you to define your DOM events declaratively, without
  // having to worry about render order ... and makes it easy for the view to
  // react to specific changes in the state of your models.

  // Creating a Backbone.View creates its initial element outside of the DOM,
  // if an existing element is not provided...
  var View = Backbone.View = function(options) {
    this.cid = _.uniqueId('view');
    this._configure(options || {});
    this._ensureElement();
    this.initialize.apply(this, arguments);
    this.delegateEvents();
  };

  // Cached regex to split keys for `delegate`.
  var delegateEventSplitter = /^(\S+)\s*(.*)$/;

  // List of view options to be merged as properties.
  var viewOptions = ['model', 'collection', 'el', 'id', 'attributes', 'className', 'tagName', 'events'];

  // Set up all inheritable **Backbone.View** properties and methods.
  _.extend(View.prototype, Events, {

    // The default `tagName` of a View's element is `"div"`.
    tagName: 'div',

    // jQuery delegate for element lookup, scoped to DOM elements within the
    // current view. This should be prefered to global lookups where possible.
    $: function(selector) {
      return this.$el.find(selector);
    },

    // Initialize is an empty function by default. Override it with your own
    // initialization logic.
    initialize: function(){},

    // **render** is the core function that your view should override, in order
    // to populate its element (`this.el`), with the appropriate HTML. The
    // convention is for **render** to always return `this`.
    render: function() {
      return this;
    },

    // Remove this view by taking the element out of the DOM, and removing any
    // applicable Backbone.Events listeners.
    remove: function() {
      this.$el.remove();
      this.stopListening();
      return this;
    },

    // Change the view's element (`this.el` property), including event
    // re-delegation.
    setElement: function(element, delegate) {
      if (this.$el) this.undelegateEvents();
      this.$el = element instanceof Backbone.$ ? element : Backbone.$(element);
      this.el = this.$el[0];
      if (delegate !== false) this.delegateEvents();
      return this;
    },

    // Set callbacks, where `this.events` is a hash of
    //
    // *{"event selector": "callback"}*
    //
    //     {
    //       'mousedown .title':  'edit',
    //       'click .button':     'save'
    //       'click .open':       function(e) { ... }
    //     }
    //
    // pairs. Callbacks will be bound to the view, with `this` set properly.
    // Uses event delegation for efficiency.
    // Omitting the selector binds the event to `this.el`.
    // This only works for delegate-able events: not `focus`, `blur`, and
    // not `change`, `submit`, and `reset` in Internet Explorer.
    delegateEvents: function(events) {
      if (!(events || (events = _.result(this, 'events')))) return this;
      this.undelegateEvents();
      for (var key in events) {
        var method = events[key];
        if (!_.isFunction(method)) method = this[events[key]];
        if (!method) continue;

        var match = key.match(delegateEventSplitter);
        var eventName = match[1], selector = match[2];
        method = _.bind(method, this);
        eventName += '.delegateEvents' + this.cid;
        if (selector === '') {
          this.$el.on(eventName, method);
        } else {
          this.$el.on(eventName, selector, method);
        }
      }
      return this;
    },

    // Clears all callbacks previously bound to the view with `delegateEvents`.
    // You usually don't need to use this, but may wish to if you have multiple
    // Backbone views attached to the same DOM element.
    undelegateEvents: function() {
      this.$el.off('.delegateEvents' + this.cid);
      return this;
    },

    // Performs the initial configuration of a View with a set of options.
    // Keys with special meaning *(e.g. model, collection, id, className)* are
    // attached directly to the view.  See `viewOptions` for an exhaustive
    // list.
    _configure: function(options) {
      if (this.options) options = _.extend({}, _.result(this, 'options'), options);
      _.extend(this, _.pick(options, viewOptions));
      this.options = options;
    },

    // Ensure that the View has a DOM element to render into.
    // If `this.el` is a string, pass it through `$()`, take the first
    // matching element, and re-assign it to `el`. Otherwise, create
    // an element from the `id`, `className` and `tagName` properties.
    _ensureElement: function() {
      if (!this.el) {
        var attrs = _.extend({}, _.result(this, 'attributes'));
        if (this.id) attrs.id = _.result(this, 'id');
        if (this.className) attrs['class'] = _.result(this, 'className');
        var $el = Backbone.$('<' + _.result(this, 'tagName') + '>').attr(attrs);
        this.setElement($el, false);
      } else {
        this.setElement(_.result(this, 'el'), false);
      }
    }

  });

  // Backbone.sync
  // -------------

  // Override this function to change the manner in which Backbone persists
  // models to the server. You will be passed the type of request, and the
  // model in question. By default, makes a RESTful Ajax request
  // to the model's `url()`. Some possible customizations could be:
  //
  // * Use `setTimeout` to batch rapid-fire updates into a single request.
  // * Send up the models as XML instead of JSON.
  // * Persist models via WebSockets instead of Ajax.
  //
  // Turn on `Backbone.emulateHTTP` in order to send `PUT` and `DELETE` requests
  // as `POST`, with a `_method` parameter containing the true HTTP method,
  // as well as all requests with the body as `application/x-www-form-urlencoded`
  // instead of `application/json` with the model in a param named `model`.
  // Useful when interfacing with server-side languages like **PHP** that make
  // it difficult to read the body of `PUT` requests.
  Backbone.sync = function(method, model, options) {
    var type = methodMap[method];

    // Default options, unless specified.
    _.defaults(options || (options = {}), {
      emulateHTTP: Backbone.emulateHTTP,
      emulateJSON: Backbone.emulateJSON
    });

    // Default JSON-request options.
    var params = {type: type, dataType: 'json'};

    // Ensure that we have a URL.
    if (!options.url) {
      params.url = _.result(model, 'url') || urlError();
    }

    // Ensure that we have the appropriate request data.
    if (options.data == null && model && (method === 'create' || method === 'update' || method === 'patch')) {
      params.contentType = 'application/json';
      params.data = JSON.stringify(options.attrs || model.toJSON(options));
    }

    // For older servers, emulate JSON by encoding the request into an HTML-form.
    if (options.emulateJSON) {
      params.contentType = 'application/x-www-form-urlencoded';
      params.data = params.data ? {model: params.data} : {};
    }

    // For older servers, emulate HTTP by mimicking the HTTP method with `_method`
    // And an `X-HTTP-Method-Override` header.
    if (options.emulateHTTP && (type === 'PUT' || type === 'DELETE' || type === 'PATCH')) {
      params.type = 'POST';
      if (options.emulateJSON) params.data._method = type;
      var beforeSend = options.beforeSend;
      options.beforeSend = function(xhr) {
        xhr.setRequestHeader('X-HTTP-Method-Override', type);
        if (beforeSend) return beforeSend.apply(this, arguments);
      };
    }

    // Don't process data on a non-GET request.
    if (params.type !== 'GET' && !options.emulateJSON) {
      params.processData = false;
    }

    // If we're sending a `PATCH` request, and we're in an old Internet Explorer
    // that still has ActiveX enabled by default, override jQuery to use that
    // for XHR instead. Remove this line when jQuery supports `PATCH` on IE8.
    if (params.type === 'PATCH' && window.ActiveXObject &&
          !(window.external && window.external.msActiveXFilteringEnabled)) {
      params.xhr = function() {
        return new ActiveXObject("Microsoft.XMLHTTP");
      };
    }

    // Make the request, allowing the user to override any Ajax options.
    var xhr = options.xhr = Backbone.ajax(_.extend(params, options));
    model.trigger('request', model, xhr, options);
    return xhr;
  };

  // Map from CRUD to HTTP for our default `Backbone.sync` implementation.
  var methodMap = {
    'create': 'POST',
    'update': 'PUT',
    'patch':  'PATCH',
    'delete': 'DELETE',
    'read':   'GET'
  };

  // Set the default implementation of `Backbone.ajax` to proxy through to `$`.
  // Override this if you'd like to use a different library.
  Backbone.ajax = function() {
    return Backbone.$.ajax.apply(Backbone.$, arguments);
  };

  // Backbone.Router
  // ---------------

  // Routers map faux-URLs to actions, and fire events when routes are
  // matched. Creating a new one sets its `routes` hash, if not set statically.
  var Router = Backbone.Router = function(options) {
    options || (options = {});
    if (options.routes) this.routes = options.routes;
    this._bindRoutes();
    this.initialize.apply(this, arguments);
  };

  // Cached regular expressions for matching named param parts and splatted
  // parts of route strings.
  var optionalParam = /\((.*?)\)/g;
  var namedParam    = /(\(\?)?:\w+/g;
  var splatParam    = /\*\w+/g;
  var escapeRegExp  = /[\-{}\[\]+?.,\\\^$|#\s]/g;

  // Set up all inheritable **Backbone.Router** properties and methods.
  _.extend(Router.prototype, Events, {

    // Initialize is an empty function by default. Override it with your own
    // initialization logic.
    initialize: function(){},

    // Manually bind a single named route to a callback. For example:
    //
    //     this.route('search/:query/p:num', 'search', function(query, num) {
    //       ...
    //     });
    //
    route: function(route, name, callback) {
      if (!_.isRegExp(route)) route = this._routeToRegExp(route);
      if (_.isFunction(name)) {
        callback = name;
        name = '';
      }
      if (!callback) callback = this[name];
      var router = this;
      Backbone.history.route(route, function(fragment) {
        var args = router._extractParameters(route, fragment);
        callback && callback.apply(router, args);
        router.trigger.apply(router, ['route:' + name].concat(args));
        router.trigger('route', name, args);
        Backbone.history.trigger('route', router, name, args);
      });
      return this;
    },

    // Simple proxy to `Backbone.history` to save a fragment into the history.
    navigate: function(fragment, options) {
      Backbone.history.navigate(fragment, options);
      return this;
    },

    // Bind all defined routes to `Backbone.history`. We have to reverse the
    // order of the routes here to support behavior where the most general
    // routes can be defined at the bottom of the route map.
    _bindRoutes: function() {
      if (!this.routes) return;
      this.routes = _.result(this, 'routes');
      var route, routes = _.keys(this.routes);
      while ((route = routes.pop()) != null) {
        this.route(route, this.routes[route]);
      }
    },

    // Convert a route string into a regular expression, suitable for matching
    // against the current location hash.
    _routeToRegExp: function(route) {
      route = route.replace(escapeRegExp, '\\$&')
                   .replace(optionalParam, '(?:$1)?')
                   .replace(namedParam, function(match, optional){
                     return optional ? match : '([^\/]+)';
                   })
                   .replace(splatParam, '(.*?)');
      return new RegExp('^' + route + '$');
    },

    // Given a route, and a URL fragment that it matches, return the array of
    // extracted decoded parameters. Empty or unmatched parameters will be
    // treated as `null` to normalize cross-browser behavior.
    _extractParameters: function(route, fragment) {
      var params = route.exec(fragment).slice(1);
      return _.map(params, function(param) {
        return param ? decodeURIComponent(param) : null;
      });
    }

  });

  // Backbone.History
  // ----------------

  // Handles cross-browser history management, based on either
  // [pushState](http://diveintohtml5.info/history.html) and real URLs, or
  // [onhashchange](https://developer.mozilla.org/en-US/docs/DOM/window.onhashchange)
  // and URL fragments. If the browser supports neither (old IE, natch),
  // falls back to polling.
  var History = Backbone.History = function() {
    this.handlers = [];
    _.bindAll(this, 'checkUrl');

    // Ensure that `History` can be used outside of the browser.
    if (typeof window !== 'undefined') {
      this.location = window.location;
      this.history = window.history;
    }
  };

  // Cached regex for stripping a leading hash/slash and trailing space.
  var routeStripper = /^[#\/]|\s+$/g;

  // Cached regex for stripping leading and trailing slashes.
  var rootStripper = /^\/+|\/+$/g;

  // Cached regex for detecting MSIE.
  var isExplorer = /msie [\w.]+/;

  // Cached regex for removing a trailing slash.
  var trailingSlash = /\/$/;

  // Has the history handling already been started?
  History.started = false;

  // Set up all inheritable **Backbone.History** properties and methods.
  _.extend(History.prototype, Events, {

    // The default interval to poll for hash changes, if necessary, is
    // twenty times a second.
    interval: 50,

    // Gets the true hash value. Cannot use location.hash directly due to bug
    // in Firefox where location.hash will always be decoded.
    getHash: function(window) {
      var match = (window || this).location.href.match(/#(.*)$/);
      return match ? match[1] : '';
    },

    // Get the cross-browser normalized URL fragment, either from the URL,
    // the hash, or the override.
    getFragment: function(fragment, forcePushState) {
      if (fragment == null) {
        if (this._hasPushState || !this._wantsHashChange || forcePushState) {
          fragment = this.location.pathname;
          var root = this.root.replace(trailingSlash, '');
          if (!fragment.indexOf(root)) fragment = fragment.substr(root.length);
        } else {
          fragment = this.getHash();
        }
      }
      return fragment.replace(routeStripper, '');
    },

    // Start the hash change handling, returning `true` if the current URL matches
    // an existing route, and `false` otherwise.
    start: function(options) {
      if (History.started) throw new Error("Backbone.history has already been started");
      History.started = true;

      // Figure out the initial configuration. Do we need an iframe?
      // Is pushState desired ... is it available?
      this.options          = _.extend({}, {root: '/'}, this.options, options);
      this.root             = this.options.root;
      this._wantsHashChange = this.options.hashChange !== false;
      this._wantsPushState  = !!this.options.pushState;
      this._hasPushState    = !!(this.options.pushState && this.history && this.history.pushState);
      var fragment          = this.getFragment();
      var docMode           = document.documentMode;
      var oldIE             = (isExplorer.exec(navigator.userAgent.toLowerCase()) && (!docMode || docMode <= 7));

      // Normalize root to always include a leading and trailing slash.
      this.root = ('/' + this.root + '/').replace(rootStripper, '/');

      if (oldIE && this._wantsHashChange) {
        this.iframe = Backbone.$('<iframe src="javascript:0" tabindex="-1" />').hide().appendTo('body')[0].contentWindow;
        this.navigate(fragment);
      }

      // Depending on whether we're using pushState or hashes, and whether
      // 'onhashchange' is supported, determine how we check the URL state.
      if (this._hasPushState) {
        Backbone.$(window).on('popstate', this.checkUrl);
      } else if (this._wantsHashChange && ('onhashchange' in window) && !oldIE) {
        Backbone.$(window).on('hashchange', this.checkUrl);
      } else if (this._wantsHashChange) {
        this._checkUrlInterval = setInterval(this.checkUrl, this.interval);
      }

      // Determine if we need to change the base url, for a pushState link
      // opened by a non-pushState browser.
      this.fragment = fragment;
      var loc = this.location;
      var atRoot = loc.pathname.replace(/[^\/]$/, '$&/') === this.root;

      // If we've started off with a route from a `pushState`-enabled browser,
      // but we're currently in a browser that doesn't support it...
      if (this._wantsHashChange && this._wantsPushState && !this._hasPushState && !atRoot) {
        this.fragment = this.getFragment(null, true);
        this.location.replace(this.root + this.location.search + '#' + this.fragment);
        // Return immediately as browser will do redirect to new url
        return true;

      // Or if we've started out with a hash-based route, but we're currently
      // in a browser where it could be `pushState`-based instead...
      } else if (this._wantsPushState && this._hasPushState && atRoot && loc.hash) {
        this.fragment = this.getHash().replace(routeStripper, '');
        this.history.replaceState({}, document.title, this.root + this.fragment + loc.search);
      }

      if (!this.options.silent) return this.loadUrl();
    },

    // Disable Backbone.history, perhaps temporarily. Not useful in a real app,
    // but possibly useful for unit testing Routers.
    stop: function() {
      Backbone.$(window).off('popstate', this.checkUrl).off('hashchange', this.checkUrl);
      clearInterval(this._checkUrlInterval);
      History.started = false;
    },

    // Add a route to be tested when the fragment changes. Routes added later
    // may override previous routes.
    route: function(route, callback) {
      this.handlers.unshift({route: route, callback: callback});
    },

    // Checks the current URL to see if it has changed, and if it has,
    // calls `loadUrl`, normalizing across the hidden iframe.
    checkUrl: function(e) {
      var current = this.getFragment();
      if (current === this.fragment && this.iframe) {
        current = this.getFragment(this.getHash(this.iframe));
      }
      if (current === this.fragment) return false;
      if (this.iframe) this.navigate(current);
      this.loadUrl() || this.loadUrl(this.getHash());
    },

    // Attempt to load the current URL fragment. If a route succeeds with a
    // match, returns `true`. If no defined routes matches the fragment,
    // returns `false`.
    loadUrl: function(fragmentOverride) {
      var fragment = this.fragment = this.getFragment(fragmentOverride);
      var matched = _.any(this.handlers, function(handler) {
        if (handler.route.test(fragment)) {
          handler.callback(fragment);
          return true;
        }
      });
      return matched;
    },

    // Save a fragment into the hash history, or replace the URL state if the
    // 'replace' option is passed. You are responsible for properly URL-encoding
    // the fragment in advance.
    //
    // The options object can contain `trigger: true` if you wish to have the
    // route callback be fired (not usually desirable), or `replace: true`, if
    // you wish to modify the current URL without adding an entry to the history.
    navigate: function(fragment, options) {
      if (!History.started) return false;
      if (!options || options === true) options = {trigger: options};
      fragment = this.getFragment(fragment || '');
      if (this.fragment === fragment) return;
      this.fragment = fragment;
      var url = this.root + fragment;

      // If pushState is available, we use it to set the fragment as a real URL.
      if (this._hasPushState) {
        this.history[options.replace ? 'replaceState' : 'pushState']({}, document.title, url);

      // If hash changes haven't been explicitly disabled, update the hash
      // fragment to store history.
      } else if (this._wantsHashChange) {
        this._updateHash(this.location, fragment, options.replace);
        if (this.iframe && (fragment !== this.getFragment(this.getHash(this.iframe)))) {
          // Opening and closing the iframe tricks IE7 and earlier to push a
          // history entry on hash-tag change.  When replace is true, we don't
          // want this.
          if(!options.replace) this.iframe.document.open().close();
          this._updateHash(this.iframe.location, fragment, options.replace);
        }

      // If you've told us that you explicitly don't want fallback hashchange-
      // based history, then `navigate` becomes a page refresh.
      } else {
        return this.location.assign(url);
      }
      if (options.trigger) this.loadUrl(fragment);
    },

    // Update the hash location, either replacing the current entry, or adding
    // a new one to the browser history.
    _updateHash: function(location, fragment, replace) {
      if (replace) {
        var href = location.href.replace(/(javascript:|#).*$/, '');
        location.replace(href + '#' + fragment);
      } else {
        // Some browsers require that `hash` contains a leading #.
        location.hash = '#' + fragment;
      }
    }

  });

  // Create the default Backbone.history.
  Backbone.history = new History;

  // Helpers
  // -------

  // Helper function to correctly set up the prototype chain, for subclasses.
  // Similar to `goog.inherits`, but uses a hash of prototype properties and
  // class properties to be extended.
  var extend = function(protoProps, staticProps) {
    var parent = this;
    var child;

    // The constructor function for the new subclass is either defined by you
    // (the "constructor" property in your `extend` definition), or defaulted
    // by us to simply call the parent's constructor.
    if (protoProps && _.has(protoProps, 'constructor')) {
      child = protoProps.constructor;
    } else {
      child = function(){ return parent.apply(this, arguments); };
    }

    // Add static properties to the constructor function, if supplied.
    _.extend(child, parent, staticProps);

    // Set the prototype chain to inherit from `parent`, without calling
    // `parent`'s constructor function.
    var Surrogate = function(){ this.constructor = child; };
    Surrogate.prototype = parent.prototype;
    child.prototype = new Surrogate;

    // Add prototype properties (instance properties) to the subclass,
    // if supplied.
    if (protoProps) _.extend(child.prototype, protoProps);

    // Set a convenience property in case the parent's prototype is needed
    // later.
    child.__super__ = parent.prototype;

    return child;
  };

  // Set up inheritance for the model, collection, router, view and history.
  Model.extend = Collection.extend = Router.extend = View.extend = History.extend = extend;

  // Throw an error when a URL is needed, and none is supplied.
  var urlError = function() {
    throw new Error('A "url" property or function must be specified');
  };

  // Wrap an optional error callback with a fallback error event.
  var wrapError = function (model, options) {
    var error = options.error;
    options.error = function(resp) {
      if (error) error(model, resp, options);
      model.trigger('error', model, resp, options);
    };
  };

}).call(this);

define("Backbone", ["jquery","Underscore"], (function (global) {
    return function () {
        var ret, fn;
        return ret || global.Backbone;
    };
}(this)));

//     uuid.js
//
//     Copyright (c) 2010-2012 Robert Kieffer
//     MIT License - http://opensource.org/licenses/mit-license.php

(function() {
  var _global = this;

  // Unique ID creation requires a high quality random # generator.  We feature
  // detect to determine the best RNG source, normalizing to a function that
  // returns 128-bits of randomness, since that's what's usually required
  var _rng;

  // Node.js crypto-based RNG - http://nodejs.org/docs/v0.6.2/api/crypto.html
  //
  // Moderately fast, high quality
  if (typeof(require) == 'function') {
    try {
      var _rb = require('crypto').randomBytes;
      _rng = _rb && function() {return _rb(16);};
    } catch(e) {}
  }

  if (!_rng && _global.crypto && crypto.getRandomValues) {
    // WHATWG crypto-based RNG - http://wiki.whatwg.org/wiki/Crypto
    //
    // Moderately fast, high quality
    var _rnds8 = new Uint8Array(16);
    _rng = function whatwgRNG() {
      crypto.getRandomValues(_rnds8);
      return _rnds8;
    };
  }

  if (!_rng) {
    // Math.random()-based (RNG)
    //
    // If all else fails, use Math.random().  It's fast, but is of unspecified
    // quality.
    var  _rnds = new Array(16);
    _rng = function() {
      for (var i = 0, r; i < 16; i++) {
        if ((i & 0x03) === 0) r = Math.random() * 0x100000000;
        _rnds[i] = r >>> ((i & 0x03) << 3) & 0xff;
      }

      return _rnds;
    };
  }

  // Buffer class to use
  var BufferClass = typeof(Buffer) == 'function' ? Buffer : Array;

  // Maps for number <-> hex string conversion
  var _byteToHex = [];
  var _hexToByte = {};
  for (var i = 0; i < 256; i++) {
    _byteToHex[i] = (i + 0x100).toString(16).substr(1);
    _hexToByte[_byteToHex[i]] = i;
  }

  // **`parse()` - Parse a UUID into it's component bytes**
  function parse(s, buf, offset) {
    var i = (buf && offset) || 0, ii = 0;

    buf = buf || [];
    s.toLowerCase().replace(/[0-9a-f]{2}/g, function(oct) {
      if (ii < 16) { // Don't overflow!
        buf[i + ii++] = _hexToByte[oct];
      }
    });

    // Zero out remaining bytes if string was short
    while (ii < 16) {
      buf[i + ii++] = 0;
    }

    return buf;
  }

  // **`unparse()` - Convert UUID byte array (ala parse()) into a string**
  function unparse(buf, offset) {
    var i = offset || 0, bth = _byteToHex;
    return  bth[buf[i++]] + bth[buf[i++]] +
            bth[buf[i++]] + bth[buf[i++]] + '-' +
            bth[buf[i++]] + bth[buf[i++]] + '-' +
            bth[buf[i++]] + bth[buf[i++]] + '-' +
            bth[buf[i++]] + bth[buf[i++]] + '-' +
            bth[buf[i++]] + bth[buf[i++]] +
            bth[buf[i++]] + bth[buf[i++]] +
            bth[buf[i++]] + bth[buf[i++]];
  }

  // **`v1()` - Generate time-based UUID**
  //
  // Inspired by https://github.com/LiosK/UUID.js
  // and http://docs.python.org/library/uuid.html

  // random #'s we need to init node and clockseq
  var _seedBytes = _rng();

  // Per 4.5, create and 48-bit node id, (47 random bits + multicast bit = 1)
  var _nodeId = [
    _seedBytes[0] | 0x01,
    _seedBytes[1], _seedBytes[2], _seedBytes[3], _seedBytes[4], _seedBytes[5]
  ];

  // Per 4.2.2, randomize (14 bit) clockseq
  var _clockseq = (_seedBytes[6] << 8 | _seedBytes[7]) & 0x3fff;

  // Previous uuid creation time
  var _lastMSecs = 0, _lastNSecs = 0;

  // See https://github.com/broofa/node-uuid for API details
  function v1(options, buf, offset) {
    var i = buf && offset || 0;
    var b = buf || [];

    options = options || {};

    var clockseq = options.clockseq != null ? options.clockseq : _clockseq;

    // UUID timestamps are 100 nano-second units since the Gregorian epoch,
    // (1582-10-15 00:00).  JSNumbers aren't precise enough for this, so
    // time is handled internally as 'msecs' (integer milliseconds) and 'nsecs'
    // (100-nanoseconds offset from msecs) since unix epoch, 1970-01-01 00:00.
    var msecs = options.msecs != null ? options.msecs : new Date().getTime();

    // Per 4.2.1.2, use count of uuid's generated during the current clock
    // cycle to simulate higher resolution clock
    var nsecs = options.nsecs != null ? options.nsecs : _lastNSecs + 1;

    // Time since last uuid creation (in msecs)
    var dt = (msecs - _lastMSecs) + (nsecs - _lastNSecs)/10000;

    // Per 4.2.1.2, Bump clockseq on clock regression
    if (dt < 0 && options.clockseq == null) {
      clockseq = clockseq + 1 & 0x3fff;
    }

    // Reset nsecs if clock regresses (new clockseq) or we've moved onto a new
    // time interval
    if ((dt < 0 || msecs > _lastMSecs) && options.nsecs == null) {
      nsecs = 0;
    }

    // Per 4.2.1.2 Throw error if too many uuids are requested
    if (nsecs >= 10000) {
      throw new Error('uuid.v1(): Can\'t create more than 10M uuids/sec');
    }

    _lastMSecs = msecs;
    _lastNSecs = nsecs;
    _clockseq = clockseq;

    // Per 4.1.4 - Convert from unix epoch to Gregorian epoch
    msecs += 12219292800000;

    // `time_low`
    var tl = ((msecs & 0xfffffff) * 10000 + nsecs) % 0x100000000;
    b[i++] = tl >>> 24 & 0xff;
    b[i++] = tl >>> 16 & 0xff;
    b[i++] = tl >>> 8 & 0xff;
    b[i++] = tl & 0xff;

    // `time_mid`
    var tmh = (msecs / 0x100000000 * 10000) & 0xfffffff;
    b[i++] = tmh >>> 8 & 0xff;
    b[i++] = tmh & 0xff;

    // `time_high_and_version`
    b[i++] = tmh >>> 24 & 0xf | 0x10; // include version
    b[i++] = tmh >>> 16 & 0xff;

    // `clock_seq_hi_and_reserved` (Per 4.2.2 - include variant)
    b[i++] = clockseq >>> 8 | 0x80;

    // `clock_seq_low`
    b[i++] = clockseq & 0xff;

    // `node`
    var node = options.node || _nodeId;
    for (var n = 0; n < 6; n++) {
      b[i + n] = node[n];
    }

    return buf ? buf : unparse(b);
  }

  // **`v4()` - Generate random UUID**

  // See https://github.com/broofa/node-uuid for API details
  function v4(options, buf, offset) {
    // Deprecated - 'format' argument, as supported in v1.2
    var i = buf && offset || 0;

    if (typeof(options) == 'string') {
      buf = options == 'binary' ? new BufferClass(16) : null;
      options = null;
    }
    options = options || {};

    var rnds = options.random || (options.rng || _rng)();

    // Per 4.4, set bits for version and `clock_seq_hi_and_reserved`
    rnds[6] = (rnds[6] & 0x0f) | 0x40;
    rnds[8] = (rnds[8] & 0x3f) | 0x80;

    // Copy bytes to buffer, if provided
    if (buf) {
      for (var ii = 0; ii < 16; ii++) {
        buf[i + ii] = rnds[ii];
      }
    }

    return buf || unparse(rnds);
  }

  // Export public API
  var uuid = v4;
  uuid.v1 = v1;
  uuid.v4 = v4;
  uuid.parse = parse;
  uuid.unparse = unparse;
  uuid.BufferClass = BufferClass;

  if (typeof define === 'function' && define.amd) {
    // Publish as AMD module
    define('UUID',[],function() {return uuid;});
  } else if (typeof(module) != 'undefined' && module.exports) {
    // Publish as node.js module
    module.exports = uuid;
  } else {
    // Publish as global (in browsers)
    var _previousRoot = _global.uuid;

    // **`noConflict()` - (browser only) to reset global 'uuid' var**
    uuid.noConflict = function() {
      _global.uuid = _previousRoot;
      return uuid;
    };

    _global.uuid = uuid;
  }
}).call(this);

define('template/property',[],function(){

    var componentType = {};

    var refreshStates = {};

	var templateProperties = [{
        componentId: 'text',
        componentName: '文本框',
        properties: [{
            id: 'display',
            name: '显示文字',
            default: '文本框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '宽度',
            nullText: 200,
            type: 'widget',
            isRefresh: true,
            editor: 'digits',
        }, {
            id: 'newline',
            name: '换行',
            default: 'true',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            default: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }]
    }, {
        componentId: 'hidden',
        componentName: '隐藏域',
        properties: [{
            id: 'name',
            name: '唯一标识',
            default: '隐藏域',
            type: 'widget',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }]
    }, {
        componentId: 'date',
        componentName: '日期框',
        properties: [{
            id: 'display',
            name: '显示文字',
            default: '日期框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '宽度',
            nullText: 200,
            type: 'widget',
            isRefresh: true,
            editor: 'digits'
        }, {
            id: 'newline',
            name: '换行',
            default: 'true',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            default: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, {
            id: 'showTime',
            name: '显示时间',
            default: 'false',
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'format',
            name: '格式化',
            nullText: 'yyyy-MM-dd hh:mm',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }]
    }, {
        componentId: 'select',
        componentName: '下拉框',
        properties: [{
            id: 'display',
            name: '显示文字',
            default: '下拉框',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, 
        // {//ligerForm会把name属性复制给valueFieldID当隐藏域的ID使用
        //     id: 'valueFieldID',
        //     name: '隐藏域的ID',
        //     type: 'options',
        //     isRefresh: false,
        //     editor: 'text'
        // }, 
        {
            id: 'width',
            name: '宽度',
            nullText: 200,
            type: 'widget',
            isRefresh: true,
            editor: 'digits'
        }, {
            id: 'newline',
            name: '换行',
            default: 'true',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            default: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, {
            id: 'value',
            name: '默认值',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }, 
//        {
//            id: 'selectBoxWidth',
//            name: '下拉框宽度',
//            nullText: '自动',
//            type: 'options',
//            isRefresh: false,
//            editor: 'text'
//        }, {
//            id: 'selectBoxHeight',
//            name: '下拉框高度',
//            nullText: '自动',
//            type: 'options',
//            isRefresh: false,
//            editor: 'text'
//        }, 
        {
            id: 'datasource',
            name: '数据源',
            type: 'options',
            isRefresh: false,
            editor: 'popup'
        }]
    }, {
        componentId: 'combobox',
        componentName: '数据树',
        properties: [{
            id: 'display',
            name: '显示文字',
            default: '数据树',
            type: 'widget',
            isRefresh: true,
            editor: 'text'
        }, {
            id: 'name',
            name: '唯一标识',
            type: 'widget',
            isRefresh: false,
            editor: 'text',
            validate: {
                required: true
            }
        }, {
            id: 'width',
            name: '宽度',
            nullText: 200,
            type: 'widget',
            isRefresh: true,
            editor: 'digits'
        }, {
            id: 'newline',
            name: '换行',
            default: 'true',
            type: 'widget',
            isRefresh: true,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'disabled',
            name: '禁用',
            default: 'null', //建议给布尔值类型的字段设置默认值
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'disabled', text: '是'}, {id: 'null', text: '否'}]
        }, 
//        {
//            id: 'selectBoxWidth',
//            name: '下拉框宽度',
//            nullText: '自动',
//            type: 'options',
//            isRefresh: false,
//            editor: 'text'
//        }, {
//            id: 'selectBoxHeight',
//            name: '下拉框高度',
//            nullText: '自动',
//            type: 'options',
//            isRefresh: false,
//            editor: 'text'
//        }, 
        {
            id: 'tree_checkbox',
            name: '支持复选',
            default: 'true',
            type: 'options',
            isRefresh: false,
            editor: 'select',
            options: [{id: 'true', text: '是'}, {id: 'false', text: '否'}]
        }, {
            id: 'tree_url',
            name: '数据源URL',
            type: 'options',
            isRefresh: false,
            editor: 'text'
        }]
    }];

    function getDefinition (componentId) {
        var i = 0;
        for (i = 0; i < templateProperties.length; i++) {
            if (templateProperties[i]['componentId'] === componentId) {
                return templateProperties[i];
            }
        }
    };

    //{widget:[], html: []}
    function getComponentType (componentId) {
        var properties, p , i;
        if (componentType[componentId] === undefined) {
            properties = getDefinition(componentId)['properties'];
            p = {};
            for (i = 0 ; i < properties.length; i++) {
                if (p[properties[i]['type']] === undefined) {
                    p[properties[i]['type']] = [];
                }
                p[properties[i]['type']].push(properties[i]['id']);
            }
            componentType[componentId] = p;
        }   
        return componentType[componentId];
    };

    //{id: true, name: false}
    function getRefreshStates (componentId) {
        var properties, p , i;
        if (refreshStates[componentId] === undefined) {
            properties = getDefinition(componentId)['properties'];
            p = {};
            for (i = 0 ; i < properties.length; i++) {
                p[properties[i]['id']] = properties[i]['isRefresh'];
            }
            refreshStates[componentId] = p;
        }   
        return refreshStates[componentId];
    };

    return {
        getDefinition: getDefinition,
        getComponentType: getComponentType,
        getRefreshStates: getRefreshStates
    };
});
define('template/model',['jquery', 'JSON2', 'Underscore', 'Backbone', 'UUID', 'template/property'], function ($, JSON2, _, Backbone, UUID, Property) {
    var Component = Backbone.Model.extend({

        config: null,

        refreshStates: 1,

        initialize: function (options) {
            var type = options.type, i = 0;
            this.config = Property.getDefinition(type);
            this.refreshStates = Property.getRefreshStates(type);
            this.setInitDefaultValue();
            for (i = 0; i < this.config["properties"].length; i++) {
                if (this.get(this.config["properties"][i].id) === undefined && this.config["properties"][i].default != undefined) {
                    this.set(this.config["properties"][i].id, this.config["properties"][i].default);
                }
            }
        },

        setInitDefaultValue: function(){
            var defaultName = null;           
            if (!this.get("id")) {
            	this.set("id", UUID.v1());
            }
            if ((defaultName = this.get("name")) === undefined) {
            	defaultName = ("component" + Component.index++);
            	this.set("name", defaultName);
            }
            Component.ids[this.cid] = defaultName;
        },

        validate: function (attrs) {
            if (attrs.name === "") {
                return "唯一标识不能为空！";
            }
            if (Component.ids[this.cid] !== attrs.name && _.contains(_.values(Component.ids), attrs.name)) {
                return "唯一标识设置为" + attrs.name + "时，导致唯一标识重复！";
            } else {
                Component.ids[this.cid] = attrs.name;
            }
        },

        toFormPorperties: function () {
            var obj = {};
            this.setDefaultValue(obj);
            this.setModelValue(obj);
            return obj;
        },

        setDefaultValue: function (obj) {
            obj['type'] = this.config['componentId'];
            obj['display'] = this.config['componentName'];
            obj['cid'] = this.cid;
        },

        setModelValue: function (obj) {
            var key = null, attrs = this.attributes;    
            for(key in attrs){
                obj[key] = attrs[key];
            }
        },

        getAttrs: function () {
            var properties = this.config["properties"], attrs = [], i = 0, val;
            for(i = 0 ; i < properties.length ; i++){
                
                var attr = {};
                attr["display"] = properties[i]["name"];
                attr["name"] = properties[i]["id"];
                attr["editor"] = properties[i]["editor"];
                attr["nullText"] = properties[i]["nullText"] === undefined ? "" : properties[i]["nullText"];
                attr["validate"] = JSON.stringify(properties[i]["validate"] === undefined ? {} : properties[i]["validate"]);
                
                attr["options"] = properties[i]["options"];
                this.resetOptions(attr["options"], this.get(properties[i]["id"]));
                
                this.setValue(attr, properties[i]["id"]);

                attrs.push(attr);
            }
            return attrs;
        },

        resetOptions: function (options, value) {
            if (options) {
                for(var i = 0 ; i < options.length ; i++ ){
                    var option = options[i];
                    if (option["id"] == value) {
                        option["selected"] = "selected";
                    } else {
                        option["selected"] = "";
                    }
                }
            }
        },

        setValue: function (attr, id) {
            var val = this.get(id) || (id === 'display' && this.config['componentName']);
            if(val){
                attr["value"] = val;
            } else {
            	attr["value"] = "";
            }
        },

        isRefresh: function (attr) {
            return this.refreshStates[attr];
        }
    }, {index: 1, ids: {}});
    var Componentes = Backbone.Collection.extend({

        model: Component,

        toFormPorperties: function(options) {
            return this.map(function(model){ return model.toFormPorperties(options); });
        }

    });
    var Template = Backbone.Model.extend({

        initialize: function () {
            this.componentes = new Componentes();
        },

        _getComponentes: function () {
            return this.componentes;
        },

        _getComponentesWhere: function (options) {
            return this.componentes.where(options);
        },

        _getComponentesWhereAndGet: function (options, attr) {
            var comp =  this.componentes.where(options);
            if (comp) {
                if (comp.length == 1) {
                    return comp.get(attr);
                } else {
                    var results = [], i = 0;
                    for (i = 0 ; i < comp.length ; i++) {
                        results.put(comp[i].get(attr));
                    }
                    return results;
                }
            } else {
                return undefined;
            }
        },

        createComponent: function (type, compAttr) {
            var c = new Component({"type": type});
            if (compAttr) {
                c.set(compAttr);
            }
            return c;
        },

        getComponent: function (cid) {
            return this.componentes.get(cid);
        },

        addComponent: function (type, compAttr) {
            this.componentes.add(this.createComponent(type, compAttr));
        },

        cloneComponent: function (component) {
            return component.clone();
        },

        indexOfComponent: function (component) {
            return this.componentes.indexOf(component);
        },

        insertComponent: function (component, index) {
            if(index < 0){
                throw{
                    name: "Error",
                    message: "insert " + component + " to " + index + " doesn't exist "
                };
            }
            this.componentes.add(component, {at: index});
        },

        removeComponent: function (component) {
            delete Component.ids[component.cid];
            this.componentes.remove(component);
            this._removeChildSelectDatasource(component);
        },
        
        _removeChildSelectDatasource : function (component) {
        	if (component.get('type') === 'select') {
				var id = component.id, selectes = this.componentes.where({type : 'select'}), i = 0, datasource = null;
				for (i = 0; i < selectes.length; i++) {
					datasource = selectes[i].get('datasource');
					if (datasource && JSON.parse(datasource).puuid === id) {
						selectes[i].set('datasource', '');
					}
				}
			}
        },

        updateComponent: function (cid, compAttr) {
            this.componentes.get(cid).reset(compAttr);
        }

    });

    return {
        Component: Component,
        Componentes : Componentes,
        Template : Template
    };
});
define('template/util',[],function() {

	var getContextPath = function() {
		var contextPath = document.location.pathname;
		var index = contextPath.substr(1).indexOf("/");
		contextPath = contextPath.substr(0, index + 1);
		delete index;
		return contextPath;
	}

	return {getContextPath : getContextPath};
});
﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{


    //ligerui 继承方法
    Function.prototype.ligerExtend = function (parent, overrides)
    {
        if (typeof parent != 'function') return this;
        //保存对父类的引用
        this.base = parent.prototype;
        this.base.constructor = parent;
        //继承
        var f = function () { };
        f.prototype = parent.prototype;
        this.prototype = new f();
        this.prototype.constructor = this;
        //附加属性方法
        if (overrides) $.extend(this.prototype, overrides);
    };
    //延时加载
    Function.prototype.ligerDefer = function (o, defer, args)
    {
        var fn = this;
        return setTimeout(function () { fn.apply(o, args || []); }, defer);
    };

    // 核心对象
    window.liger = $.ligerui = {
        version: 'V1.2.0',
        managerCount: 0,
        //组件管理器池
        managers: {},
        managerIdPrev: 'ligerui',
        //管理器id已经存在时自动创建新的
        autoNewId : true,
        //错误提示
        error: {
            managerIsExist: '管理器id已经存在'
        },
        pluginPrev: 'liger',
        getId: function (prev)
        {
            prev = prev || this.managerIdPrev;
            var id = prev + (1000 + this.managerCount);
            this.managerCount++;
            return id;
        },
        add: function (manager)
        {
            if (arguments.length == 2)
            {
                var m = arguments[1];
                m.id = m.id || m.options.id || arguments[0].id;
                this.addManager(m);
                return;
            }
            if (!manager.id) manager.id = this.getId(manager.__idPrev());
            if (this.managers[manager.id]) manager.id = this.getId(manager.__idPrev());
            if (this.managers[manager.id]) {
                throw new Error(this.error.managerIsExist);
            }
            this.managers[manager.id] = manager;
        },
        remove: function (arg)
        {
            if (typeof arg == "string" || typeof arg == "number")
            { 
                delete liger.managers[arg];
            }
            else if (typeof arg == "object")
            {
                if (arg instanceof liger.core.Component)
                {
                    delete liger.managers[arg.id];
                }
                else
                {
                    if (!$(arg).attr(this.idAttrName)) return false;
                    delete liger.managers[$(arg).attr(this.idAttrName)];
                }
            }
        },
        //获取ligerui对象
        //1,传入ligerui ID
        //2,传入Dom Object
        get: function (arg, idAttrName)
        {
            idAttrName = idAttrName || "ligeruiid";
            if (typeof arg == "string" || typeof arg == "number")
            {
                return liger.managers[arg];
            }
            else if (typeof arg == "object")
            {
                var domObj = arg.length ? arg[0] : arg;
                var id = domObj[idAttrName] || $(domObj).attr(idAttrName);
                if (!id) return null;
                return liger.managers[id];
            }
            return null;
        },
        //根据类型查找某一个对象
        find: function (type)
        {
            var arr = [];
            for (var id in this.managers)
            {
                var manager = this.managers[id];
                if (type instanceof Function)
                {
                    if (manager instanceof type)
                    {
                        arr.push(manager);
                    }
                }
                else if (type instanceof Array)
                {
                    if ($.inArray(manager.__getType(), type) != -1)
                    {
                        arr.push(manager);
                    }
                }
                else
                {
                    if (manager.__getType() == type)
                    {
                        arr.push(manager);
                    }
                }
            }
            return arr;
        },
        //$.fn.liger{Plugin} 和 $.fn.ligerGet{Plugin}Manager
        //会调用这个方法,并传入作用域(this)
        //@parm [plugin]  插件名
        //@parm [args] 参数(数组)
        //@parm [ext] 扩展参数,定义命名空间或者id属性名
        run: function (plugin, args, ext)
        {
            if (!plugin) return;
            ext = $.extend({
                defaultsNamespace: 'ligerDefaults',
                methodsNamespace: 'ligerMethods',
                controlNamespace: 'controls',
                idAttrName: 'ligeruiid',
                isStatic: false,
                hasElement: true,           //是否拥有element主体(比如drag、resizable等辅助性插件就不拥有)
                propertyToElemnt: null      //链接到element的属性名
            }, ext || {});
            plugin = plugin.replace(/^ligerGet/, '');
            plugin = plugin.replace(/^liger/, '');
            if (this == null || this == window || ext.isStatic)
            {
                if (!liger.plugins[plugin])
                {
                    liger.plugins[plugin] = {
                        fn: $[liger.pluginPrev + plugin],
                        isStatic: true
                    };
                }
                return new $.ligerui[ext.controlNamespace][plugin]($.extend({}, $[ext.defaultsNamespace][plugin] || {}, $[ext.defaultsNamespace][plugin + 'String'] || {}, args.length > 0 ? args[0] : {}));
            }
            if (!liger.plugins[plugin])
            {
                liger.plugins[plugin] = {
                    fn: $.fn[liger.pluginPrev + plugin],
                    isStatic: false
                };
            }
            if (/Manager$/.test(plugin)) return liger.get(this, ext.idAttrName);
            this.each(function ()
            {
                if (this[ext.idAttrName] || $(this).attr(ext.idAttrName))
                {
                    var manager = liger.get(this[ext.idAttrName] || $(this).attr(ext.idAttrName));
                    if (manager && args.length > 0) manager.set(args[0]);
                    //已经执行过 
                    return;
                }
                if (args.length >= 1 && typeof args[0] == 'string') return;
                //只要第一个参数不是string类型,都执行组件的实例化工作
                var options = args.length > 0 ? args[0] : null;
                var p = $.extend({}, $[ext.defaultsNamespace][plugin], $[ext.defaultsNamespace][plugin + 'String'], options);
                if (ext.propertyToElemnt) p[ext.propertyToElemnt] = this;
                if (ext.hasElement)
                {
                    new $.ligerui[ext.controlNamespace][plugin](this, p);
                }
                else
                {
                    new $.ligerui[ext.controlNamespace][plugin](p);
                }
            });
            if (this.length == 0) return null;
            if (args.length == 0) return liger.get(this, ext.idAttrName);
            if (typeof args[0] == 'object') return liger.get(this, ext.idAttrName);
            if (typeof args[0] == 'string')
            {
                var manager = liger.get(this, ext.idAttrName);
                if (manager == null) return;
                if (args[0] == "option")
                {
                    if (args.length == 2)
                        return manager.get(args[1]);  //manager get
                    else if (args.length >= 3)
                        return manager.set(args[1], args[2]);  //manager set
                }
                else
                {
                    var method = args[0];
                    if (!manager[method]) return; //不存在这个方法
                    var parms = Array.apply(null, args);
                    parms.shift();
                    return manager[method].apply(manager, parms);  //manager method
                }
            }
            return null;
        },

        //扩展
        //1,默认参数     
        //2,本地化扩展 
        defaults: {},
        //3,方法接口扩展
        methods: {},
        //命名空间
        //核心控件,封装了一些常用方法
        core: {},
        //命名空间
        //组件的集合
        controls: {},
        //plugin 插件的集合
        plugins: {}
    };


    //扩展对象
    $.ligerDefaults = {};

    //扩展对象
    $.ligerMethos = {};

    //关联起来
    liger.defaults = $.ligerDefaults;
    liger.methods = $.ligerMethos;

    //获取ligerui对象
    //@parm [plugin]  插件名,可为空
    $.fn.liger = function (plugin)
    {
        if (plugin)
        {
            return liger.run.call(this, plugin, arguments);
        }
        else
        {
            return liger.get(this);
        }
    };


    //组件基类
    //1,完成定义参数处理方法和参数属性初始化的工作
    //2,完成定义事件处理方法和事件属性初始化的工作
    liger.core.Component = function (options)
    {
        //事件容器
        this.events = this.events || {};
        //配置参数
        this.options = options || {};
        //子组件集合索引
        this.children = {};
    };
    $.extend(liger.core.Component.prototype, {
        __getType: function ()
        {
            return 'liger.core.Component';
        },
        __idPrev: function ()
        {
            return 'ligerui';
        },

        //设置属性
        // arg 属性名    value 属性值 
        // arg 属性/值   value 是否只设置事件
        set: function (arg, value)
        {
            if (!arg) return;
            if (typeof arg == 'object')
            {
                var tmp;
                if (this.options != arg)
                {
                    $.extend(this.options, arg);
                    tmp = arg;
                }
                else
                {
                    tmp = $.extend({}, arg);
                }
                if (value == undefined || value == true)
                {
                    for (var p in tmp)
                    {
                        if (p.indexOf('on') == 0)
                            this.set(p, tmp[p]);
                    }
                }
                if (value == undefined || value == false)
                {
                    for (var p in tmp)
                    {
                        if (p.indexOf('on') != 0)
                            this.set(p, tmp[p]);
                    }
                }
                return;
            }
            var name = arg;
            //事件参数
            if (name.indexOf('on') == 0)
            {
                if (typeof value == 'function')
                    this.bind(name.substr(2), value);
                return;
            }
            if (!this.options) this.options = {};
            if (this.trigger('propertychange', [arg, value]) == false) return;
            this.options[name] = value;
            var pn = '_set' + name.substr(0, 1).toUpperCase() + name.substr(1);
            if (this[pn])
            {
                this[pn].call(this, value);
            }
            this.trigger('propertychanged', [arg, value]);
        },

        //获取属性
        get: function (name)
        {
            var pn = '_get' + name.substr(0, 1).toUpperCase() + name.substr(1);
            if (this[pn])
            {
                return this[pn].call(this, name);
            }
            return this.options[name];
        },

        hasBind: function (arg)
        {
            var name = arg.toLowerCase();
            var event = this.events[name];
            if (event && event.length) return true;
            return false;
        },

        //触发事件
        //data (可选) Array(可选)传递给事件处理函数的附加参数
        trigger: function (arg, data)
        {
            if (!arg) return;
            var name = arg.toLowerCase();
            var event = this.events[name];
            if (!event) return;
            data = data || [];
            if ((data instanceof Array) == false)
            {
                data = [data];
            }
            for (var i = 0; i < event.length; i++)
            {
                var ev = event[i];
                if (ev.handler.apply(ev.context, data) == false)
                    return false;
            }
        },

        //绑定事件
        bind: function (arg, handler, context)
        {
            if (typeof arg == 'object')
            {
                for (var p in arg)
                {
                    this.bind(p, arg[p]);
                }
                return;
            }
            if (typeof handler != 'function') return false;
            var name = arg.toLowerCase();
            var event = this.events[name] || [];
            context = context || this;
            event.push({ handler: handler, context: context });
            this.events[name] = event;
        },

        //取消绑定
        unbind: function (arg, handler)
        {
            if (!arg)
            {
                this.events = {};
                return;
            }
            var name = arg.toLowerCase();
            var event = this.events[name];
            if (!event || !event.length) return;
            if (!handler)
            {
                delete this.events[name];
            }
            else
            {
                for (var i = 0, l = event.length; i < l; i++)
                {
                    if (event[i].handler == handler)
                    {
                        event.splice(i, 1);
                        break;
                    }
                }
            }
        },
        destroy: function ()
        {
            liger.remove(this);
        }
    });


    //界面组件基类, 
    //1,完成界面初始化:设置组件id并存入组件管理器池,初始化参数
    //2,渲染的工作,细节交给子类实现
    //@parm [element] 组件对应的dom element对象
    //@parm [options] 组件的参数
    liger.core.UIComponent = function (element, options)
    {
        liger.core.UIComponent.base.constructor.call(this, options);
        var extendMethods = this._extendMethods();
        if (extendMethods) $.extend(this, extendMethods);
        this.element = element;
        this._init();
        this._preRender();
        this.trigger('render');
        this._render();
        this.trigger('rendered');
        this._rendered();
    };
    liger.core.UIComponent.ligerExtend(liger.core.Component, {
        __getType: function ()
        {
            return 'liger.core.UIComponent';
        },
        //扩展方法
        _extendMethods: function ()
        {

        },
        _init: function ()
        {
            this.type = this.__getType();
            if (!this.element)
            {
                this.id = this.options.id || liger.getId(this.__idPrev());
            }
            else
            {
                this.id = this.options.id || this.element.id || liger.getId(this.__idPrev());
            }
            //存入管理器池
            liger.add(this);

            if (!this.element) return;

            //读取attr方法,并加载到参数,比如['url']
            var attributes = this.attr();
            if (attributes && attributes instanceof Array)
            {
                for (var i = 0; i < attributes.length; i++)
                {
                    var name = attributes[i];
                    this.options[name] = $(this.element).attr(name);
                }
            }
            //读取ligerui这个属性，并加载到参数，比如 ligerui = "width:120,heigth:100"
            var p = this.options;
            if ($(this.element).attr("ligerui"))
            {
                try
                {
                    var attroptions = $(this.element).attr("ligerui");
                    if (attroptions.indexOf('{') != 0) attroptions = "{" + attroptions + "}";
                    eval("attroptions = " + attroptions + ";");
                    if (attroptions) $.extend(p, attroptions);
                }
                catch (e) { }
            }
        },
        //预渲染,可以用于继承扩展
        _preRender: function ()
        {

        },
        _render: function ()
        {

        },
        _rendered: function ()
        {
            if (this.element)
            {
                $(this.element).attr("ligeruiid", this.id);
            }
        },
        //返回要转换成ligerui参数的属性,比如['url']
        attr: function ()
        {
            return [];
        },
        destroy: function ()
        {
            if (this.element)
            {
                $(this.element).remove();
            }
            this.options = null;
            liger.remove(this);
        }
    });


    //表单控件基类
    liger.controls.Input = function (element, options)
    {
        liger.controls.Input.base.constructor.call(this, element, options);
    };

    liger.controls.Input.ligerExtend(liger.core.UIComponent, {
        __getType: function ()
        {
            return 'liger.controls.Input';
        },
        attr: function ()
        {
            return ['nullText'];
        },
        setValue: function (value)
        {
            return this.set('value', value);
        },
        getValue: function ()
        {
            return this.get('value');
        },
        //设置只读
        _setReadonly: function (readonly)
        {
            var wrapper = this.wrapper || this.text;
            if (!wrapper || !wrapper.hasClass("l-text")) return;
            var inputText = this.inputText;
            //modify by tanxu at 2013-8-28
            if (readonly === true)
            //end modify
            {
                if (inputText) inputText.attr("readonly", "readonly");
                wrapper.addClass("l-text-readonly");
            //modify by tanxu at 2013-8-28
            } else if (readonly === false)
            //end modify
            {
                if (inputText) inputText.removeAttr("readonly");
                wrapper.removeClass("l-text-readonly");
            } 
        },
        setEnabled: function ()
        {
            return this.set('disabled', false);
        },
        setDisabled: function ()
        {
            return this.set('disabled', true);
        },
        updateStyle: function ()
        {

        },
        resize: function (width, height) {
            this.set({ width: width, height: height });
        }
    });

    //全局窗口对象
    liger.win = {
        //顶端显示
        top: false,

        //遮罩
        mask: function (win)
        {
            function setHeight()
            {
                if (!liger.win.windowMask) return;
                var h = $(window).height() + $(window).scrollTop();
                liger.win.windowMask.height(h);
            }
            if (!this.windowMask)
            {
                this.windowMask = $("<div class='l-window-mask' style='display: block;'></div>").appendTo('body');
                $(window).bind('resize.ligeruiwin', setHeight);
                $(window).bind('scroll', setHeight);
            }
            this.windowMask.show();
            setHeight();
            this.masking = true;
        },

        //取消遮罩
        unmask: function (win)
        {
            var jwins = $("body > .l-dialog:visible,body > .l-window:visible");
            for (var i = 0, l = jwins.length; i < l; i++)
            {
                var winid = jwins.eq(i).attr("ligeruiid");
                if (win && win.id == winid) continue;
                //获取ligerui对象
                var winmanager = liger.get(winid);
                if (!winmanager) continue;
                //是否模态窗口
                var modal = winmanager.get('modal');
                //如果存在其他模态窗口，那么不会取消遮罩
                if (modal) return;
            }
            if (this.windowMask)
                this.windowMask.hide();
            this.masking = false;
        },

        //显示任务栏
        createTaskbar: function ()
        {
            if (!this.taskbar)
            {
                this.taskbar = $('<div class="l-taskbar"><div class="l-taskbar-tasks"></div><div class="l-clear"></div></div>').appendTo('body');
                if (this.top) this.taskbar.addClass("l-taskbar-top");
                this.taskbar.tasks = $(".l-taskbar-tasks:first", this.taskbar);
                this.tasks = {};
            }
            this.taskbar.show();
            this.taskbar.animate({ bottom: 0 });
            return this.taskbar;
        },

        //关闭任务栏
        removeTaskbar: function ()
        {
            var self = this;
            self.taskbar.animate({ bottom: -32 }, function ()
            {
                self.taskbar.remove();
                self.taskbar = null;
            });
        },
        activeTask: function (win)
        {
            for (var winid in this.tasks)
            {
                var t = this.tasks[winid];
                if (winid == win.id)
                {
                    t.addClass("l-taskbar-task-active");
                }
                else
                {
                    t.removeClass("l-taskbar-task-active");
                }
            }
        },

        //获取任务
        getTask: function (win)
        {
            var self = this;
            if (!self.taskbar) return;
            if (self.tasks[win.id]) return self.tasks[win.id];
            return null;
        },


        //增加任务
        addTask: function (win)
        {
            var self = this;
            if (!self.taskbar) self.createTaskbar();
            if (self.tasks[win.id]) return self.tasks[win.id];
            var title = win.get('title');
            var task = self.tasks[win.id] = $('<div class="l-taskbar-task"><div class="l-taskbar-task-icon"></div><div class="l-taskbar-task-content">' + title + '</div></div>');
            self.taskbar.tasks.append(task);
            self.activeTask(win);
            task.bind('click', function ()
            {
                self.activeTask(win);
                if (win.actived)
                    win.min();
                else
                    win.active();
            }).hover(function ()
            {
                $(this).addClass("l-taskbar-task-over");
            }, function ()
            {
                $(this).removeClass("l-taskbar-task-over");
            });
            return task;
        },

        hasTask: function ()
        {
            for (var p in this.tasks)
            {
                if (this.tasks[p])
                    return true;
            }
            return false;
        },

        //移除任务
        removeTask: function (win)
        {
            var self = this;
            if (!self.taskbar) return;
            if (self.tasks[win.id])
            {
                self.tasks[win.id].unbind();
                self.tasks[win.id].remove();
                delete self.tasks[win.id];
            }
            if (!self.hasTask())
            {
                self.removeTaskbar();
            }
        },

        //前端显示
        setFront: function (win)
        {
            var wins = liger.find(liger.core.Win);
            for (var i in wins)
            {
                var w = wins[i];
                if (w == win)
                {
                    $(w.element).css("z-index", "9200");
                    this.activeTask(w);
                }
                else
                {
                    $(w.element).css("z-index", "9100");
                }
            }
        }
    };


    //窗口基类 window、dialog
    liger.core.Win = function (element, options)
    {
        liger.core.Win.base.constructor.call(this, element, options);
    };

    liger.core.Win.ligerExtend(liger.core.UIComponent, {
        __getType: function ()
        {
            return 'liger.controls.Win';
        },
        mask: function ()
        {
            if (this.options.modal)
                liger.win.mask(this);
        },
        unmask: function ()
        {
            if (this.options.modal)
                liger.win.unmask(this);
        },
        min: function ()
        {
        },
        max: function ()
        {
        },
        active: function ()
        {
        }
    });


    liger.draggable = {
        dragging: false
    };

    liger.resizable = {
        reszing: false
    };


    liger.toJSON = typeof JSON === 'object' && JSON.stringify ? JSON.stringify : function (o)
    {
        var f = function (n)
        {
            return n < 10 ? '0' + n : n;
        },
		escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
		quote = function (value)
		{
		    escapable.lastIndex = 0;
		    return escapable.test(value) ?
				'"' + value.replace(escapable, function (a)
				{
				    var c = meta[a];
				    return typeof c === 'string' ? c :
						'\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
				}) + '"' :
				'"' + value + '"';
		};
        if (o === null) return 'null';
        var type = typeof o;
        if (type === 'undefined') return undefined;
        if (type === 'string') return quote(o);
        if (type === 'number' || type === 'boolean') return '' + o;
        if (type === 'object')
        {
            if (typeof o.toJSON === 'function')
            {
                return liger.toJSON(o.toJSON());
            }
            if (o.constructor === Date)
            {
                return isFinite(this.valueOf()) ?
                   this.getUTCFullYear() + '-' +
                 f(this.getUTCMonth() + 1) + '-' +
                 f(this.getUTCDate()) + 'T' +
                 f(this.getUTCHours()) + ':' +
                 f(this.getUTCMinutes()) + ':' +
                 f(this.getUTCSeconds()) + 'Z' : null;
            }
            var pairs = [];
            if (o.constructor === Array)
            {
                for (var i = 0, l = o.length; i < l; i++)
                {
                    pairs.push(liger.toJSON(o[i]) || 'null');
                }
                return '[' + pairs.join(',') + ']';
            }
            var name, val;
            for (var k in o)
            {
                type = typeof k;
                if (type === 'number')
                {
                    name = '"' + k + '"';
                } else if (type === 'string')
                {
                    name = quote(k);
                } else
                {
                    continue;
                }
                type = typeof o[k];
                if (type === 'function' || type === 'undefined')
                {
                    continue;
                }
                val = liger.toJSON(o[k]);
                pairs.push(name + ':' + val);
            }
            return '{' + pairs.join(',') + '}';
        }
    };

   
})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    /*
    以html的方式加载组件
    程序会查询以 liger-插件名 类名的Dom,从dom加载相应的参数并调用插件
        比如遇到 .liger-grid 的dom，会找到 liger.defaults.Grid 加载需要的参数
        而在config.Grid中配置了这些参数的类型,会动态得加载data,而columns会设置为数组
    参数处理的优先级为：
    1,ignores 忽略不处理的参数
    2,dom存在 {属性名} 的类名 ,比如 <ul class="columns"></ul> ,便会将这个参数设置为复杂属性(object或array):找到相应的defaults和config来加载
         defaults是先找$.liger.inject.defaults,找不到再找liger.defaults的
         config为{父配置}.{属性名},比如 config.Grid.columns
    3,直接读取 data-{属性名} 或者 {属性名} 的dom属性
    */
    liger.inject = {

        prev: 'liger-',

        /* 
        命名规则：插件名_属性名(包括第N级的属性) (插件名首字母大写,属性名首字母小写) 
        获取规则：获取default时会先找这里,找不到再找liger.defaults,比如 liger.defaults.Grid_columns 
        备注：这里只定义了参数的列表
        */
        defaults: {
            Grid_detail: {
                height: null,
                onShowDetail: null
            },
            Grid_editor: 'ComboBox,DateEditor,Spinner,TextBox,PopupEdit,Grid_editor',
            Grid_popup: 'PopupEdit',
            Grid_grid: 'Grid',
            Grid_condition: 'Form',
            Grid_toolbar: 'Toolbar',
            Grid_fields: 'Form_fields',
            Form_editor: 'ComboBox,DateEditor,Spinner,TextBox,PopupEdit,Form_editor',
            Form_grid: 'Grid',
            Form_columns: 'Grid_columns',
            Form_condition: 'Form',
            Form_popup: 'PopupEdit',
            Form_buttons: 'Button'
        },
        /*
        config里面配置了某插件参数或者复杂属性参数的类型(动态加载、数组、默认参数)
        */
        config: {
            Grid: {
                //动态
                dynamics: 'data,isChecked,detail,rowDraggingRender,toolbar',
                //数组
                arrays: 'columns',
                //复杂属性columns
                columns: {
                    dynamics: 'render,totalSummary,headerRender,columns,editor',
                    arrays: 'columns',
                    textProperty: 'display',
                    columns: 'liger.inject.config.Grid.columns',
                    editor: {
                        dynamics: 'data,columns,render,renderItem,grid,condition,ext',
                        grid: 'liger.inject.config.Grid',
                        condition: 'liger.inject.config.Form'
                    }
                },
                toolbar: {
                    arrays: 'items'
                }
            },
            Form: {
                arrays: 'fields,buttons',
                fields: {
                    textProperty: 'label',
                    editor: {
                        dynamics: 'data,columns,render,renderItem,grid,condition,attr',
                        grid: 'liger.inject.config.Grid',
                        condition: 'liger.inject.config.Form'
                    }
                },
                buttons: 'liger.inject.config.Button'
            },
            PopupEdit: {
                dynamics: 'grid,condition'
            },
            Button: {
                textProperty: 'text',
                dynamics: 'click'
            },
            ComboBox: {
                dynamics: 'columns,data,tree,grid,condition,render,parms,renderItem'
            },
            ListBox: {
                dynamics: 'columns,data,render,parms'
            },
            RadioList: {
                dynamics: 'data,parms'
            },
            CheckBoxList: {
                dynamics: 'data,parms'
            }
        },

        parse: function (code)
        {
            try
            {
                if (code == null) return null;
                return new Function("return " + code + ";")();
            } catch (e)
            {
                return null;
            }
        },

        parseDefault: function (value)
        {
            var g = this;
            if (!value) return value;
            var result = {};
            $(value.split(',')).each(function (index, name)
            {
                if (!name) return;
                name = name.substr(0, 1).toUpperCase() + name.substr(1);
                $.extend(result, g.parse("liger.defaults." + name));
            });
            return result;
        },

        fotmatValue: function (value, type)
        {
            if (type == "boolean")
                return value == "true" || value == "1";
            if (type == "number" && value)
                return parseFloat(value.toString());
            return value;
        },

        getOptions: function (e)
        {
            var jelement = e.jelement, defaults = e.defaults, config = e.config;
            config = $.extend({
                ignores: "",
                dynamics: "",
                arrays: ""
            }, config);
            var g = this, options = {}, value;
            if (config.textProperty) options[config.textProperty] = jelement.text();
            for (var proName in defaults)
            {
                var className = proName.toLowerCase();
                var subElement = $("> ." + className, jelement);
                //忽略
                if ($.inArray(proName, config.ignores.split(',')) != -1) continue;
                //判断子节点 (复杂属性) 
                if (subElement.length)
                {
                    var defaultName = e.controlName + "_" + proName;
                    var subDefaults = g.defaults[defaultName] || liger.defaults[defaultName], subConfig = config[proName];
                    if (typeof (subDefaults) == "string") subDefaults = g.parseDefault(subDefaults);
                    else if (typeof (subDefaults) == "funcion") subDefaults = subDefaults();
                    if (typeof (subConfig) == "string") subConfig = g.parse(subConfig);
                    else if (typeof (subConfig) == "funcion") subConfig = subConfig();
                    if (subDefaults)
                    {
                        if ($.inArray(proName, config.arrays.split(',')) != -1)
                        {
                            value = [];
                            $(">div,>li,>input", subElement).each(function ()
                            {
                                value.push(g.getOptions({
                                    defaults: subDefaults,
                                    controlName: e.controlName,
                                    config: subConfig,
                                    jelement: $(this)
                                }));
                            });
                            options[proName] = value;
                        } else
                        {
                            options[proName] = g.getOptions({
                                defaults: subDefaults,
                                controlName: e.controlName,
                                config: subConfig,
                                jelement: subElement
                            });
                        }
                    }
                    subElement.remove();
                }
                    //动态值
                else if ($.inArray(proName, config.dynamics.split(',')) != -1 || proName.indexOf('on') == 0)
                {
                    value = g.parse(jelement.attr("data-" + proName) || jelement.attr(proName));
                    if (value)
                    {
                        options[proName] = g.fotmatValue(value, typeof (defaults[proName]));
                    }
                }
                    //默认处理
                else
                {
                    value = jelement.attr("data-" + proName) || jelement.attr(proName);
                    if (value)
                    {
                        options[proName] = g.fotmatValue(value, typeof (defaults[proName]));
                    }
                }
            }
            var dataOptions = jelement.attr("data-options") || jelement.attr("data-property");
            if (dataOptions) dataOptions = g.parse("{" + dataOptions + "}");
            if (dataOptions) $.extend(options, dataOptions);
            return options;
        },

        init: function ()
        {
            var g = this, configs = this.config;
            for (var name in g.defaults)
            {
                if (typeof (g.defaults[name]) == "string")
                {
                    g.defaults[name] = g.parseDefault(g.defaults[name]);
                }
            }
            for (var controlName in liger.controls)
            {
                var config = configs[controlName] || {};
                var className = g.prev + controlName.toLowerCase();
                $("." + className).each(function ()
                {
                    var jelement = $(this), value;
                    var defaults = liger.defaults[controlName];
                    var options = g.getOptions({
                        defaults: defaults,
                        controlName: controlName,
                        config: config,
                        jelement: jelement
                    }); 
                    jelement[liger.pluginPrev + controlName](options);
                });
            }
        }

    }

    $(function ()
    {
        liger.inject.init();
    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerAccordion = function (options)
    {
        return $.ligerui.run.call(this, "ligerAccordion", arguments);
    };

    $.fn.ligerGetAccordionManager = function ()
    {
        return $.ligerui.get(this);
    };

    $.ligerDefaults.Accordion = {
        height: null,
        speed: "normal",
        changeHeightOnResize: false,
        heightDiff: 0 // 高度补差  
    };
    $.ligerMethos.Accordion = {};

    $.ligerui.controls.Accordion = function (element, options)
    {
        $.ligerui.controls.Accordion.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Accordion.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Accordion';
        },
        __idPrev: function ()
        {
            return 'Accordion';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Accordion;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.accordion = $(g.element);
            if (!g.accordion.hasClass("l-accordion-panel")) g.accordion.addClass("l-accordion-panel");
            var selectedIndex = 0;
            if ($("> div[lselected=true]", g.accordion).length > 0)
                selectedIndex = $("> div", g.accordion).index($("> div[lselected=true]", g.accordion));

            $("> div", g.accordion).each(function (i, box)
            {
                var header = $('<div class="l-accordion-header"><div class="l-accordion-toggle"></div><div class="l-accordion-header-inner"></div></div>');
                if (i == selectedIndex)
                    $(".l-accordion-toggle", header).addClass("l-accordion-toggle-open");
                if ($(box).attr("title"))
                {
                    $(".l-accordion-header-inner", header).html($(box).attr("title"));
                    $(box).attr("title", "");
                }
                $(box).before(header);
                if (!$(box).hasClass("l-accordion-content")) $(box).addClass("l-accordion-content");
            });

            //add Even
            $(".l-accordion-toggle", g.accordion).each(function ()
            {
                if (!$(this).hasClass("l-accordion-toggle-open") && !$(this).hasClass("l-accordion-toggle-close"))
                {
                    $(this).addClass("l-accordion-toggle-close");
                }
                if ($(this).hasClass("l-accordion-toggle-close"))
                {
                    $(this).parent().next(".l-accordion-content:visible").hide();
                }
            });
            $(".l-accordion-header", g.accordion).hover(function ()
            {
                $(this).addClass("l-accordion-header-over");
            }, function ()
            {
                $(this).removeClass("l-accordion-header-over");
            });
            $(".l-accordion-toggle", g.accordion).hover(function ()
            {
                if ($(this).hasClass("l-accordion-toggle-open"))
                    $(this).addClass("l-accordion-toggle-open-over");
                else if ($(this).hasClass("l-accordion-toggle-close"))
                    $(this).addClass("l-accordion-toggle-close-over");
            }, function ()
            {
                if ($(this).hasClass("l-accordion-toggle-open"))
                    $(this).removeClass("l-accordion-toggle-open-over");
                else if ($(this).hasClass("l-accordion-toggle-close"))
                    $(this).removeClass("l-accordion-toggle-close-over");
            });
            $(">.l-accordion-header", g.accordion).click(function ()
            {
                var togglebtn = $(".l-accordion-toggle:first", this);
                if (togglebtn.hasClass("l-accordion-toggle-close"))
                {
                    togglebtn.removeClass("l-accordion-toggle-close")
                    .removeClass("l-accordion-toggle-close-over l-accordion-toggle-open-over")
                    togglebtn.addClass("l-accordion-toggle-open");
                    $(this).next(".l-accordion-content")
                    .show(p.speed)
                    .siblings(".l-accordion-content:visible").hide(p.speed);
                    $(this).siblings(".l-accordion-header").find(".l-accordion-toggle").removeClass("l-accordion-toggle-open").addClass("l-accordion-toggle-close");
                }
                else
                {
                    togglebtn.removeClass("l-accordion-toggle-open")
                    .removeClass("l-accordion-toggle-close-over l-accordion-toggle-open-over")
                    .addClass("l-accordion-toggle-close");
                    $(this).next(".l-accordion-content").hide(p.speed);
                }
            });
            //init
            g.headerHoldHeight = 0;
            $("> .l-accordion-header", g.accordion).each(function ()
            {
                g.headerHoldHeight += $(this).height();
            });
            if (p.height && typeof (p.height) == 'string' && p.height.indexOf('%') > 0)
            {
                g.onResize();
                if (p.changeHeightOnResize)
                {
                    $(window).resize(function ()
                    {
                        g.onResize();
                    });
                }
            }
            else
            {
                if (p.height)
                {
                    g.height = p.heightDiff + p.height;
                    g.accordion.height(g.height);
                    g.setHeight(p.height);
                }
                else
                {
                    g.header = g.accordion.height();
                }
            }

            g.set(p);
        },
        onResize: function ()
        {
            var g = this, p = this.options;
            if (!p.height || typeof (p.height) != 'string' || p.height.indexOf('%') == -1) return false;
            //set accordion height
            if (g.accordion.parent()[0].tagName.toLowerCase() == "body")
            {
                var windowHeight = $(window).height();
                windowHeight -= parseInt(g.layout.parent().css('paddingTop'));
                windowHeight -= parseInt(g.layout.parent().css('paddingBottom'));
                g.height = p.heightDiff + windowHeight * parseFloat(g.height) * 0.01;
            }
            else
            {
                g.height = p.heightDiff + (g.accordion.parent().height() * parseFloat(p.height) * 0.01);
            }
            g.accordion.height(g.height);
            g.setContentHeight(g.height - g.headerHoldHeight);
        },
        setHeight: function (height)
        {
            var g = this, p = this.options;
            g.accordion.height(height);
            height -= g.headerHoldHeight;
            $("> .l-accordion-content", g.accordion).height(height);
        }
    });


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerButton = function (options)
    {
        return $.ligerui.run.call(this, "ligerButton", arguments);
    };
    $.fn.ligerGetButtonManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetButtonManager", arguments);
    };

    $.ligerDefaults.Button = {
        width: 60,
        text: 'Button',
        disabled: false,
        click: null,
        icon : null
    };

    $.ligerMethos.Button = {};

    $.ligerui.controls.Button = function (element, options)
    {
        $.ligerui.controls.Button.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Button.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'Button';
        },
        __idPrev: function ()
        {
            return 'Button';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Button;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.button = $(g.element);
            g.button.addClass("l-button");
            g.button.append('<div class="l-button-l"></div><div class="l-button-r"></div><span></span>');
            g.button.hover(function () {
                if (p.disabled) return;
                g.button.addClass("l-button-over");
            }, function () {
                if (p.disabled) return;
                g.button.removeClass("l-button-over");
            });
            p.click && g.button.click(function ()
            {
                if (!p.disabled)
                    p.click();
            });
            g.set(p);
        },
        _setIcon : function(url)
        {
            var g = this;
            if (!url)
            {
                g.button.removeClass("l-button-hasicon");
                g.button.find('img').remove();
            } else
            {
                g.button.addClass("l-button-hasicon");
                g.button.append('<img src="' + url + '" />');
            }
        },
        _setEnabled: function (value)
        {
            if (value)
                this.button.removeClass("l-button-disabled");
        },
        _setDisabled: function (value)
        {
            if (value) {
                this.button.addClass("l-button-disabled");
                this.options.disabled = true;
            } else {
                this.button.removeClass("l-button-disabled");
                this.options.disabled = false;
            }
        },
        _setWidth: function (value)
        {
            this.button.width(value);
        },
        _setText: function (value)
        {
            $("span", this.button).html(value);
        },
        setValue: function (value)
        {
            this.set('text', value);
        },
        getValue: function ()
        {
            return this.options.text;
        },
        setEnabled: function ()
        {
            this.set('disabled', false);
        },
        setDisabled: function ()
        {
            this.set('disabled', true);
        }
    }); 


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerCheckBox = function (options)
    {
        return $.ligerui.run.call(this, "ligerCheckBox", arguments);
    };
    $.fn.ligerGetCheckBoxManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetCheckBoxManager", arguments);
    };
    $.ligerDefaults.CheckBox = {
        disabled: false,
        readonly : false //只读
    };

    $.ligerMethos.CheckBox = {};

    $.ligerui.controls.CheckBox = function (element, options)
    {
        $.ligerui.controls.CheckBox.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.CheckBox.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'CheckBox';
        },
        __idPrev: function ()
        {
            return 'CheckBox';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.CheckBox;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.input = $(g.element);
            g.link = $('<a class="l-checkbox"></a>');
            g.wrapper = g.input.addClass('l-hidden').wrap('<div class="l-checkbox-wrapper"></div>').parent();
            g.wrapper.prepend(g.link);
            g.link.click(function ()
            {
                if (g.input.attr('disabled') || g.input.attr('readonly')) { return false; }
                if (p.disabled || p.readonly) return false;
                if (g.trigger('beforeClick', [g.element]) == false) return false; 
                if ($(this).hasClass("l-checkbox-checked"))
                {
                    g._setValue(false);
                }
                else
                {
                    g._setValue(true);
                }
                g.input.trigger("change");
            });
            g.wrapper.hover(function ()
            {
                if (!p.disabled)
                    $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
            this.set(p);
            this.updateStyle();
        },
        _setCss: function (value)
        {
            this.wrapper.css(value);
        },
        _setValue: function (value)
        {
            var g = this, p = this.options;
            if (!value)
            {
                g.input[0].checked = false;
                g.link.removeClass('l-checkbox-checked');
            }
            else
            {
                g.input[0].checked = true;
                g.link.addClass('l-checkbox-checked');
            }
        },
        _setDisabled: function (value)
        {
            if (value)
            {
                this.input.attr('disabled', true);
                this.wrapper.addClass("l-disabled");
            }
            else
            {
                this.input.attr('disabled', false);
                this.wrapper.removeClass("l-disabled");
            }
        },
        _getValue: function ()
        {
            return this.element.checked;
        },
        updateStyle: function ()
        {
            if (this.input.attr('disabled'))
            {
                this.wrapper.addClass("l-disabled");
                this.options.disabled = true;
            }
            if (this.input[0].checked)
            {
                this.link.addClass('l-checkbox-checked');
            }
            else
            {
                this.link.removeClass('l-checkbox-checked');
            }
        }
    });
})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerCheckBoxList = function (options)
    {
        return $.ligerui.run.call(this, "ligerCheckBoxList", arguments);
    }; 

    $.ligerDefaults.CheckBoxList = {  
        rowSize: 3,            //每行显示元素数   
        valueField: 'id',       //值成员
        textField: 'text',      //显示成员 
        valueFieldID:null,      //隐藏域
        name : null,            //表单名
        split: ";",             //分隔符
        data: null,             //数据  
        parms: null,            //ajax提交表单 
        url: null,              //数据源URL(需返回JSON)
        onSuccess: null,
        onError: null,  
        css: null,               //附加css  
        value: null            //值 
    };

    //扩展方法
    $.ligerMethos.CheckBoxList = $.ligerMethos.CheckBoxList || {};


    $.ligerui.controls.CheckBoxList = function (element, options)
    {
        $.ligerui.controls.CheckBoxList.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.CheckBoxList.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'CheckBoxList';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.CheckBoxList;
        },
        _init: function ()
        {
            $.ligerui.controls.CheckBoxList.base._init.call(this); 
        },
        _render: function ()
        {
            var g = this, p = this.options; 
            g.data = p.data;    
            g.valueField = null; //隐藏域(保存值) 
               
            if (p.valueFieldID)
            {
                g.valueField = $("#" + p.valueFieldID + ":input,[name=" + p.valueFieldID + "]:input");
                if (g.valueField.length == 0) g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = p.valueFieldID;
            }
            else
            {
                g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = g.id + "_val";
            }
            if (g.valueField[0].name == null) g.valueField[0].name = g.valueField[0].id;
            g.valueField.attr("data-ligerid", g.id);
            g.checkboxList = $(this.element);
            g.checkboxList.html('<div class="l-checkboxlist-inner"><table cellpadding="0" cellspacing="0" border="0" class="l-checkboxlist-table"></table></div>').addClass("l-checkboxlist").append(g.valueField);
            g.checkboxList.table = $("table:first", g.checkboxList); 
              
            g.set(p); 

            g._addClickEven();
        },
        destroy: function ()
        { 
            if (this.checkboxList) this.checkboxList.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        clear : function()
        {
            this._changeValue("");
            this.trigger('clear');
        }, 
        _setCss : function(css)
        {
            if (css) {
                this.checkboxList.addClass(css);
            } 
        }, 
        _setDisabled: function (value)
        {
            //禁用样式
            if (value)
            {
                this.checkboxList.addClass('l-checkboxlist-disabled');
                $("input:checkbox", this.radioList).attr("disabled", true);

            } else
            {
                this.checkboxList.removeClass('l-checkboxlist-disabled');
                $("input:checkbox", this.radioList).removeAttr("disabled");
            }
        }, 
        _setWidth: function (value)
        {
            this.checkboxList.width(value);
        },
        _setHeight: function (value)
        {
            this.checkboxList.height(value);
        },  
        indexOf : function(item)
        {
            var g = this, p = this.options;
            if (!g.data) return -1;
            for (var i = 0, l = g.data.length; i < l; i++)
            {
                if (typeof (item) == "object")
                {
                    if (g.data[i] == item) return i;
                } else
                {
                    if (g.data[i][p.valueField].toString() == item.toString()) return i;
                }
            }
            return -1;
        },
        removeItems : function(items)
        {
            var g = this;
            if (!g.data) return;
            $(items).each(function (i,item)
            {
                var index = g.indexOf(item);
                if (index == -1) return;
                g.data.splice(index, 1);
            });
            g.refresh();
        },
        removeItem: function (item)
        {
            if (!this.data) return;
            var index = this.indexOf(item);
            if (index == -1) return;
            this.data.splice(index, 1);
            this.refresh();
        },
        insertItem: function (item,index)
        {
            var g = this;
            if (!g.data) g.data = []; 
            g.data.splice(index, 0, item);
            g.refresh();
        },
        addItems: function (items)
        {
            var g = this;
            if (!g.data) g.data = [];
            $(items).each(function (i, item)
            {
                g.data.push(item);
            });
            g.refresh();
        },
        addItem: function (item)
        {
            var g = this;
            if (!g.data) g.data = [];
            g.data.push(item);
            g.refresh();
        },  
        _setValue: function (value)
        {
            var g = this, p = this.options; 
            p.value = value;
            this._dataInit();
        },
        setValue: function (value)
        { 
            this._setValue(value);
        }, 
        _setUrl: function (url) {
            if (!url) return;
            var g = this, p = this.options; 
            $.ajax({
                type: 'post',
                url: url,
                data: p.parms,
                cache: false,
                dataType: 'json',
                success: function (data) { 
                    g.setData(data);
                    g.trigger('success', [data]);
                },
                error: function (XMLHttpRequest, textStatus) {
                    g.trigger('error', [XMLHttpRequest, textStatus]);
                }
            });
        },
        setUrl: function (url) {
            return this._setUrl(url);
        },
        setParm: function (name, value) {
            if (!name) return;
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            parms[name] = value;
            g.set('parms', parms); 
        },
        clearContent: function ()
        {
            var g = this, p = this.options;
            $("table", g.checkboxList).html(""); 
        }, 
        _setData : function(data)
        {
            this.setData(data);
        },
        setData: function (data)
        {
            var g = this, p = this.options; 
            if (!data || !data.length) return;
            g.data = data;
            g.refresh();
        },
        refresh:function()
        {
            var g = this, p = this.options, data = this.data; 
            this.clearContent();
            if (!data) return; 
            var out = [], rowSize = p.rowSize, appendRowStart = false, name = p.name || g.id;
            for (var i = 0; i < data.length; i++)
            {
                var val = data[i][p.valueField], txt = data[i][p.textField], id = g.id + "-" + i;
                var newRow = i % rowSize == 0;
                //0,5,10
                if (newRow)
                {
                    if (appendRowStart) out.push('</tr>'); 
                    out.push("<tr>");
                    appendRowStart = true;
                }
                out.push("<td><input type='checkbox' name='" + name + "' value='" + val + "' id='" + id + "'/><label for='" + id + "'>" + txt + "</label></td>");
            }
            if (appendRowStart) out.push('</tr>');
            g.checkboxList.table.append(out.join(''));
        },
        _getValue: function ()
        { 
            var g = this, p = this.options, name = p.name || g.id;
            var values = [];
            $('input:checkbox[name="' + name + '"]:checked').each(function ()
            {
                values.push(this.value);
            });
            return values.join(p.split);
        },
        getValue: function ()
        {
            //获取值
            return this._getValue();
        },  
        updateStyle: function ()
        { 
            this._dataInit();
        },
        _dataInit: function ()
        {
            var g = this, p = this.options; 
            var value = g.valueField.val() || g._getValue() || p.value;
            g._changeValue(value);
        },
        //设置值到 隐藏域
        _changeValue: function (value)
        {
            var g = this, p = this.options, name = p.name || g.id;
            var valueArr = value ? value.split(p.split) : [];
            $("input:checkbox[name='" + name + "']", g.checkboxList).each(function ()
            { 
                this.checked = $.inArray(this.value, valueArr) > -1;
            });
            g.valueField.val(value);
            g.selectedValue = value;
        },
        _addClickEven: function ()
        {
            var g = this, p = this.options;
            //选项点击
            g.checkboxList.click(function (e)
            {  
                var value = g.getValue(); 
                if (value) g.valueField.val(value);
            });
        } 
    });
      

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{

    $.fn.ligerComboBox = function (options)
    {
        return $.ligerui.run.call(this, "ligerComboBox", arguments);
    };

    $.fn.ligerGetComboBoxManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetComboBoxManager", arguments);
    };

    $.ligerDefaults.ComboBox = {
        resize: true,           //是否调整大小
        isMultiSelect: false,   //是否多选
        isShowCheckBox: false,  //是否选择复选框
        columns: null,       //表格状态
        selectBoxWidth: null, //宽度
        selectBoxHeight: null, //高度
        onBeforeSelect: false, //选择前事件
        onSelected: null, //选择值事件 
        initValue: null,
        initText: null,
        valueField: 'id',
        textField: 'text',
        valueFieldID: null,
        //modify by songxf
        //slide: true,           //是否以动画的形式显示
        slide: true,           //是否以动画的形式显示
        //modify end
        split: ";",
        data: null,
        tree: null,            //下拉框以树的形式显示，tree的参数跟LigerTree的参数一致 
        treeLeafOnly: true,   //是否只选择叶子
        condition: null,       //列表条件搜索 参数同 ligerForm
        grid: null,              //表格 参数同 ligerGrid
        onStartResize: null,
        onEndResize: null,
        hideOnLoseFocus: false,
        url: null,              //数据源URL(需返回JSON)
        onSuccess: null,
        onError: null,
        onBeforeOpen: null,      //打开下拉框前事件，可以通过return false来阻止继续操作，利用这个参数可以用来调用其他函数，比如打开一个新窗口来选择值
        render: null,            //文本框显示html函数
        absolute: true,         //选择框是否在附加到body,并绝对定位
        //modify by songxf at 2013-7-10
        //cancelable: true,      //可取消选择
        cancelable: false,      //默认不可取消选择
        //modify end
        css: null,            //附加css
        parms: null,         //ajax提交表单 
        renderItem : null,   //选项自定义函数
        autocomplete: false,  //自动完成 
        readonly: false,              //是否只读
        ajaxType : 'post'
        //modify by songxf at 2013-7-24
        ,filter:false       //是否对下拉内容进行过滤
        //modify end       
    };

    //扩展方法
    $.ligerMethos.ComboBox = $.ligerMethos.ComboBox || {};


    $.ligerui.controls.ComboBox = function (element, options)
    {
        $.ligerui.controls.ComboBox.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.ComboBox.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'ComboBox';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.ComboBox;
        },
        _init: function ()
        {
            $.ligerui.controls.ComboBox.base._init.call(this);
            var p = this.options;
            if (p.columns)
            {
                p.isShowCheckBox = true;
            }
            if (p.isMultiSelect)
            {
                p.isShowCheckBox = true;
            } 
        },
        _render: function ()
        {
            var g = this, p = this.options; 
            g.data = p.data;
            g.inputText = null;
            g.select = null;
            g.textFieldID = "";
            g.valueFieldID = "";
            g.valueField = null; //隐藏域(保存值) 
            //文本框初始化
            if (this.element.tagName.toLowerCase() == "input")
            {
                this.element.readOnly = true;
                g.inputText = $(this.element);
                g.textFieldID = this.element.id;
            }
            else if (this.element.tagName.toLowerCase() == "select")
            {
                $(this.element).hide();
                g.select = $(this.element);
                p.isMultiSelect = false;
                p.isShowCheckBox = false;
                p.cancelable = false;
                g.textFieldID = this.element.id + "_txt";
                g.inputText = $('<input type="text" readonly="true"/>');
                g.inputText.attr("id", g.textFieldID).insertAfter($(this.element));
            }  
            if (g.inputText[0].name == undefined) g.inputText[0].name = g.textFieldID; 
            //隐藏域初始化
            g.valueField = null;
            if (p.valueFieldID)
            {
                g.valueField = $("#" + p.valueFieldID + ":input,[name=" + p.valueFieldID + "]:input");
                if (g.valueField.length == 0) g.valueField = $('<input type="hidden" ltype="combobox"/>');
                g.valueField[0].id = g.valueField[0].name = p.valueFieldID;
            }
            else
            {
                g.valueField = $('<input type="hidden" ltype="combobox"/>');
                g.valueField[0].id = g.valueField[0].name = g.textFieldID + "_val";
            }
            if (g.valueField[0].name == undefined) g.valueField[0].name = g.valueField[0].id;
            g.valueField.attr("data-ligerid", g.id);
            //开关
            g.link = $('<div class="l-trigger"><div class="l-trigger-icon"></div></div>');
           
            //modify by songxf at 2013-7-17
            //下拉框
            if(p.filter&&!p.grid&&!p.tree){
	            g.selectBox = $('<div class="l-box-select" style="display:none"><div class="l-box-select-search"><input class="l-box-select-search-input" type="text"/></div><div class="l-box-select-inner"><table cellpadding="0" cellspacing="0" border="0" class="l-box-select-table"></table></div></div>');
	            g.selectBox.search = $(".l-box-select-search", g.selectBox);
	            g.selectBox.inner = $(".l-box-select-inner", g.selectBox);
	            g.selectBox.table = $("table:first", g.selectBox);
            }else{
	            //下拉框
	            g.selectBox = $('<div class="l-box-select" style="display:none"><div class="l-box-select-inner"><table cellpadding="0" cellspacing="0" border="0" class="l-box-select-table"></table></div></div>');
	            g.selectBox.inner = $(".l-box-select-inner", g.selectBox);
	            g.selectBox.table = $("table:first", g.selectBox);
            }
            //modify end
            //外层
            g.wrapper = g.inputText.wrap('<div class="l-text l-text-combobox"></div>').parent();
            g.wrapper.append('<div class="l-text-l"></div><div class="l-text-r"></div>'); 
            g.wrapper.append(g.link);
            //添加个包裹，
            g.textwrapper = g.wrapper.wrap('<div class="l-text-wrapper"></div>').parent();

            if (p.absolute)
                g.selectBox.appendTo('body').addClass("l-box-select-absolute");
            else
                g.textwrapper.append(g.selectBox);

            g.textwrapper.append(g.valueField);
            g.inputText.addClass("l-text-field");
            if (p.isShowCheckBox && !g.select)
            {
                $("table", g.selectBox).addClass("l-table-checkbox");
            } else
            {
                p.isShowCheckBox = false;
                $("table", g.selectBox).addClass("l-table-nocheckbox");
            }  
			//modify by  songxf at 2013-6-26
            //开关 事件
            //g.link.hover(function ()
            //{
            //    if (p.disabled || p.readonly) return;
            //    this.className = "l-trigger-hover";
            //}, function ()
            //{
            //    if (p.disabled || p.readonly) return;
            //    this.className = "l-trigger";
            //}).mousedown(function ()
            //{
            //    if (p.disabled || p.readonly) return;
            //    this.className = "l-trigger-pressed";
            //}).mouseup(function ()
            //{
            //    if (p.disabled || p.readonly) return;
            //    this.className = "l-trigger-hover";
            //}).click(function ()
            //{
            //    if (p.disabled || p.readonly) return;
            //    if (g.trigger('beforeOpen') == false) return false;
            //    g._toggleSelectBox(g.selectBox.is(":visible"));
            //});
			g.link.click(function ()
            {
                if (p.disabled || p.readonly) return;
                if (g.trigger('beforeOpen') == false) return false;
                g._toggleSelectBox(g.selectBox.is(":visible"));
			});
			//modify end
            g.inputText.click(function ()
            {
                if (p.disabled || p.readonly) return;
                if (g.trigger('beforeOpen') == false) return false;
                g._toggleSelectBox(g.selectBox.is(":visible"));
            }).blur(function ()
            {
                if (p.disabled) return;
                g.wrapper.removeClass("l-text-focus");
            }).focus(function ()
            {
                if (p.disabled || p.readonly) return;
                g.wrapper.addClass("l-text-focus");
            });
            g.wrapper.hover(function ()
            {
                if (p.disabled || p.readonly) return;
                g.wrapper.addClass("l-text-over");
            }, function ()
            {
                if (p.disabled || p.readonly) return;
                g.wrapper.removeClass("l-text-over");
            });
            g.resizing = false;
            g.selectBox.hover(null, function (e)
            {
                if (p.hideOnLoseFocus && g.selectBox.is(":visible") && !g.boxToggling && !g.resizing)
                {
                    g._toggleSelectBox(true);
                }
            });

            //下拉框内容初始化
            g.bulidContent();
            g.set(p); 
            //下拉框宽度、高度初始化   
            if (p.selectBoxWidth)
            {
                g.selectBox.width(p.selectBoxWidth);
            }
            else
            {
                g.selectBox.css('width', g.wrapper.css('width'));
            }

            if (p.grid) {
                g.bind('show', function () {
                    if (!g.grid) {
                        g.setGrid(p.grid);
                        g.set('SelectBoxHeight', p.selectBoxHeight);
                    }
                });
            }
            g.updateSelectBoxPosition();
            //modify by songxf at 2013-7-23
            if(p.filter&&!p.grid&&!p.tree){
	            g.selectBox.search.bind("keyup",function(){
		            	p.searchText=$("input:first",g.selectBox.search).val();
		            	g.searching=true;
                        g.set({ data: g.data });
		            	g.searching=false;
	            });
            }
            if(!p.grid&&!p.tree){
                var itemsleng = $("tr", g.selectBox.table).length;
	            if (!p.selectBoxHeight && itemsleng < 8) 
	           	{
	            		p.selectBoxHeight = itemsleng * 25;
	            }else if(!p.selectBoxHeight && itemsleng >= 8){
	            		p.selectBoxHeight = 8 * 25;
	            }
	            
	            if (p.filter&&p.selectBoxHeight){
	            	g.selectBox.height(p.selectBoxHeight+18);
	            	g.selectBox.inner.height(p.selectBoxHeight);
	            }else if (p.selectBoxHeight) {
	                g.selectBox.height(p.selectBoxHeight);
	                g.selectBox.inner.height(p.selectBoxHeight);
	            }
	        }    
            $(document).bind('mousedown.combobox', function(e){
            	if(g.selectBox.is(":visible")){
                	g._toggleSelectBox(true);
                }
			});
			g.selectBox.bind('mousedown.combobox', function(e){
				return false;
			});
			if (g.selectBox.search){
				g.selectBox.search.bind('click', function(e){
					$("input:first",g.selectBox.search).focus();
				});
			}
			g.link.bind('mousedown.combobox', function(e){
				return false;
			});
			g.inputText.bind('mousedown.combobox', function(e){
				return false;
			});
			//modify end
        },
        destroy: function ()
        {
            if (this.wrapper) this.wrapper.remove();
            if (this.selectBox) this.selectBox.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        clear : function()
        {
            this._changeValue("", "");
            this.trigger('clear');
            //modify by songxf at 2013-7-10
            $(".l-selected", this.selectBox).removeClass("l-selected");
            //modify end
        },
        _setSelectBoxHeight: function (height) {
            if (!height) return;
            var g = this, p = this.options;
            if (p.grid) {
                g.grid && g.grid.set('height', g.getGridHeight(height));
            }
            //modify by songxf at 2013-7-30
            else if(p.tree){
                    g.selectBox.height(height);
            }else{
	            if (p.filter&&p.selectBoxHeight){
	            	g.selectBox.height(height+18);
	            	g.selectBox.inner.height(height);
	            }else if (p.selectBoxHeight) {
	                g.selectBox.height(p.height);
	            	g.selectBox.inner.height(p.height); 
	            }   
	        }   
            //modify end
        }, 
        _setCss : function(css)
        {
            if (css) {
                this.wrapper.addClass(css);
            } 
        }, 
        //取消选择 
        _setCancelable: function (value)
        {
            var g = this, p = this.options;
            if (!value && g.unselect) {
                g.unselect.remove();
                g.unselect = null;
            }
            if (!value && !g.unselect) return;
            g.unselect = $('<div class="l-trigger l-trigger-cancel"><div class="l-trigger-icon"></div></div>').hide();
            g.wrapper.hover(function () { 
                g.unselect.show();
            }, function () { 
                g.unselect.hide();
            })
            if (!p.disabled && !p.readonly && p.cancelable) {
                g.wrapper.append(g.unselect);
            }
            g.unselect.hover(function () {
                this.className = "l-trigger-hover l-trigger-cancel";
            }, function () {
                this.className = "l-trigger l-trigger-cancel";
            }).click(function () {
                g.clear();
            });
        },
        _setDisabled: function (value)
        {
            //禁用样式
            if (value)
            {
                this.wrapper.addClass('l-text-disabled');
            } else
            {
                this.wrapper.removeClass('l-text-disabled');
            }
        },
        _setReadonly: function (readonly)
        { 
            if (readonly)
            { 
                this.wrapper.addClass("l-text-readonly");
            } else
            { 
                this.wrapper.removeClass("l-text-readonly");
            }
        },
        _setLable: function (label)
        {
            var g = this, p = this.options;
            if (label)
            {
                if (g.labelwrapper)
                {
                    g.labelwrapper.find(".l-text-label:first").html(label + ':&nbsp');
                }
                else
                {
                    g.labelwrapper = g.textwrapper.wrap('<div class="l-labeltext"></div>').parent();
                    g.labelwrapper.prepend('<div class="l-text-label" style="float:left;display:inline;">' + label + ':&nbsp</div>');
                    g.textwrapper.css('float', 'left');
                }
                if (!p.labelWidth)
                {
                    p.labelWidth = $('.l-text-label', g.labelwrapper).outerWidth();
                }
                else
                {
                    $('.l-text-label', g.labelwrapper).outerWidth(p.labelWidth);
                }
                $('.l-text-label', g.labelwrapper).width(p.labelWidth);
                $('.l-text-label', g.labelwrapper).height(g.wrapper.height());
                g.labelwrapper.append('<br style="clear:both;" />');
                if (p.labelAlign)
                {
                    $('.l-text-label', g.labelwrapper).css('text-align', p.labelAlign);
                }
                g.textwrapper.css({ display: 'inline' });
                g.labelwrapper.width(g.wrapper.outerWidth() + p.labelWidth + 2);
            }
        },
        _setWidth: function (value)
        {
            var g = this, p = this.options;
            if (value > 20)
            {
                g.wrapper.css({ width: value });
                g.inputText.css({ width: value - 20 });
                g.textwrapper.css({ width: value });
                if (!p.selectBoxWidth) {
                    g.selectBox.css({ width: value });
                }
            }
        },
        _setHeight: function (value)
        {
            var g = this;
            if (value > 10)
            {
                g.wrapper.height(value);
                g.inputText.height(value - 2); 
                g.textwrapper.css({ width: value });
            }
        },
        _setResize: function (resize)
        {
            var g = this, p = this.options; 
            if (p.columns) {
                return;
            }
            //调整大小支持
            if (resize && $.fn.ligerResizable)
            { 
                g.selectBox.ligerResizable({ handles: 'se,s,e', onStartResize: function ()
                {
                    g.resizing = true;
                    g.trigger('startResize');
                }, onEndResize: function (current,e)
                {
                    g.resizing = false;
                    if (g.trigger('endResize') == false)
                        return false;
                }, onStopResize: function (current, e) {
                    if (g.grid) {
                        if (current.newWidth) {
                            g.selectBox.width(current.newWidth);
                        }
                        if (current.newHeight) {
                            g.set({ selectBoxHeight: current.newHeight });
                        }
                        g.grid.refreshSize();
                        g.trigger('endResize');
                        return false;
                    }
                    //modify by songxf at 2013-7-31
                    else if(p.tree){
                    	g.selectBox.height(current.newHeight);
                    }else{
	                    if (p.filter){
			            	g.selectBox.height(current.newHeight);
			            	g.selectBox.inner.height(current.newHeight-18);
			            }else{
			                g.selectBox.height(current.newHeight);
			            	g.selectBox.inner.height(current.newHeight); 
			            }   
                    }
                    //modify end
                    return true;
                }
                });
                g.selectBox.append("<div class='l-btn-nw-drop'></div>");
            }
        },
        //查找Text,适用多选和单选
        findTextByValue: function (value)
        {
            var g = this, p = this.options;
            if (value == null) return "";
            var texts = "";
            var contain = function (checkvalue)
            {
                var targetdata = value.toString().split(p.split);
                for (var i = 0; i < targetdata.length; i++)
                {
                    if (targetdata[i] == checkvalue) return true;
                }
                return false;
            };
            $(g.data).each(function (i, item)
            {
                var val = item[p.valueField];
                var txt = item[p.textField];
                if (contain(val))
                {
                    texts += txt + p.split;
                }
            });
            if (texts.length > 0) texts = texts.substr(0, texts.length - 1);
            return texts;
        },
        //查找Value,适用多选和单选
        findValueByText: function (text)
        {
            var g = this, p = this.options;
            if (!text && text == "") return "";
            var contain = function (checkvalue)
            {
                var targetdata = text.toString().split(p.split);
                for (var i = 0; i < targetdata.length; i++)
                {
                    if (targetdata[i] == checkvalue) return true;
                }
                return false;
            };
            var values = "";
            $(g.data).each(function (i, item)
            {
                var val = item[p.valueField];
                var txt = item[p.textField];
                if (contain(txt))
                {
                    values += val + p.split;
                }
            });
            if (values.length > 0) values = values.substr(0, values.length - 1);
            return values;
        },
        removeItem: function ()
        {
        },
        insertItem: function ()
        {
        },
        addItem: function ()
        {

        },
        _setValue: function (value,text)
        {
            var g = this, p = this.options;  
            text = g.findTextByValue(value);
            if (p.tree)
            {
                g.selectValueByTree(value);
            }
            else if (!p.isMultiSelect)
            {
                g._changeValue(value, text);
                $("tr[value='" + value + "'] td", g.selectBox).addClass("l-selected");
                $("tr[value!='" + value + "'] td", g.selectBox).removeClass("l-selected");
            }
            else
            {
                g._changeValue(value, text);
                if (value != null) {
                    var targetdata = value.toString().split(p.split);
                    $("table.l-table-checkbox :checkbox", g.selectBox).each(function () { this.checked = false; });
                    for (var i = 0; i < targetdata.length; i++) {
                        $("table.l-table-checkbox tr[value=" + targetdata[i] + "] :checkbox", g.selectBox).each(function () { this.checked = true; });
                    }
                }
            }
        },
        selectValue: function (value)
        {
            this._setValue(value);
        },
        bulidContent: function ()
        {
            var g = this, p = this.options;
            this.clearContent();
            if (g.select)
            {
                g.setSelect();
            } 
            else if (p.tree)
            {
                g.setTree(p.tree);
            }  
        },
        _setUrl: function (url) {
            if (!url) return;
            var g = this, p = this.options; 
            $.ajax({
                type: p.ajaxType,
                url: url,
                data: p.parms,
                cache: false,
                dataType: 'json',
                success: function (data) { 
                    g.setData(data);
                    //modify by songxf at 2013-6-25
                    //若是url形式获取data，异步获取完成后，更新下拉框高度
		            if(!p.grid&&!p.tree){
		                var itemsleng = $("tr", g.selectBox.table).length;
			            if (!p.selectBoxHeight && itemsleng < 8) 
			           	{
			            		p.selectBoxHeight = itemsleng * 25;
			            }else if (!p.selectBoxHeight && itemsleng >= 8) {
			            		p.selectBoxHeight = 8 * 25;
			            }
			            if (p.filter&&p.selectBoxHeight){
			            	g.selectBox.height(p.selectBoxHeight+18);
			            	g.selectBox.inner.height(p.selectBoxHeight);
			            }else if (p.selectBoxHeight) {
			                g.selectBox.height(p.selectBoxHeight);
			                g.selectBox.inner.height(p.selectBoxHeight);
			            }
			        }   
                    //modify end
                    g.trigger('success', [data]);
                },
                error: function (XMLHttpRequest, textStatus) {
                    g.trigger('error', [XMLHttpRequest, textStatus]);
                }
            });
        },
        setUrl: function (url) {
            return this._setUrl(url);
        },
        setParm: function (name, value) {
            if (!name) return;
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            parms[name] = value;
            g.set('parms', parms); 
        },
        clearContent: function ()
        {
            var g = this, p = this.options;
            $("table", g.selectBox).html("");
            //g.inputText.val("");
            //g.valueField.val("");
        },
        setSelect: function ()
        {
            var g = this, p = this.options;
            this.clearContent();
            $('option', g.select).each(function (i)
            {
                var val = $(this).val();
                var txt = $(this).html();
                var tr = $("<tr><td index='" + i + "' value='" + val + "' text='" + txt + "'>" + txt + "</td>");
                $("table.l-table-nocheckbox", g.selectBox).append(tr);
                $("td", tr).hover(function ()
                {
                    $(this).addClass("l-over");
                }, function ()
                {
                    $(this).removeClass("l-over");
                });
            });
            $('td:eq(' + g.select[0].selectedIndex + ')', g.selectBox).each(function ()
            {
                if ($(this).hasClass("l-selected"))
                {
                    g.selectBox.hide();
                    return;
                }
                $(".l-selected", g.selectBox).removeClass("l-selected");
                $(this).addClass("l-selected");
                if (g.select[0].selectedIndex != $(this).attr('index') && g.select[0].onchange)
                {
                    g.select[0].selectedIndex = $(this).attr('index'); g.select[0].onchange();
                }
                var newIndex = parseInt($(this).attr('index'));
                g.select[0].selectedIndex = newIndex;
                g.select.trigger("change");
                g.selectBox.hide();
                var value = $(this).attr("value");
                var text = $(this).html();
                if (p.render)
                {
                    g.inputText.val(p.render(value, text));
                }
                else
                {
                    g.inputText.val(text);
                }
            });
            g._addClickEven();
        },
        _setData : function(data)
        {
            this.setData(data);
        },
        setData: function (data)
        {
            var g = this, p = this.options; 
            if (!data || !data.length) return;
            if (g.data != data) g.data = data;
            this.clearContent();
            if (p.columns)
            {
                g.selectBox.table.headrow = $("<tr class='l-table-headerow'><td width='18px'></td></tr>");
                g.selectBox.table.append(g.selectBox.table.headrow);
                g.selectBox.table.addClass("l-box-select-grid");
                for (var j = 0; j < p.columns.length; j++)
                {
                    var headrow = $("<td columnindex='" + j + "' columnname='" + p.columns[j].name + "'>" + p.columns[j].header + "</td>");
                    if (p.columns[j].width)
                    {
                        headrow.width(p.columns[j].width);
                    }
                    g.selectBox.table.headrow.append(headrow);

                }
            }
            var out = [];
            for (var i = 0; i < data.length; i++)
            {
                var val = data[i][p.valueField];
                var txt = data[i][p.textField];
                //modify by songxf at 2013-7-17
                if(p.searchText&&txt.indexOf(p.searchText)==-1)continue;
                //modify end
                if (!p.columns)
                {
                    out.push("<tr value='" + val + "'>");
                    if(p.isShowCheckBox){
                        out.push("<td style='width:18px;'  index='" + i + "' value='" + val + "' text='" + txt + "' ><input type='checkbox' /></td>");
                    }
                    var itemHtml = txt;
                    if (p.renderItem) {
                        itemHtml = p.renderItem.call(g, {
                            data: data[i],
                            value: val,
                            text: txt,
                            key: g.inputText.val()
                        });
                    } else if (p.autocomplete)
                    {
                        itemHtml = g._highLight(txt, g.inputText.val());
                    }
                    out.push("<td index='" + i + "' value='" + val + "' text='" + txt + "' align='left'>" + itemHtml + "</td></tr>");
                } else
                {
                    out.push("<tr value='" + val + "'><td style='width:18px;'  index='" + i + "' value='" + val + "' text='" + txt + "' ><input type='checkbox' /></td>");
                    for (var j = 0; j < p.columns.length; j++) {
                        var columnname = p.columns[j].name;
                        out.push("<td>" + data[i][columnname] + "</td>");
                    }
                    out.push('</tr>');  
                }
            } 
            if (!p.columns) {
                if (p.isShowCheckBox) {
                    $("table.l-table-checkbox", g.selectBox).append(out.join(''));
                }else{
                    $("table.l-table-nocheckbox", g.selectBox).append(out.join(''));
                }
            } else { 
                g.selectBox.table.append(out.join(''));
            }
            //自定义复选框支持
            if (p.isShowCheckBox && $.fn.ligerCheckBox)
            {
                $("table input:checkbox", g.selectBox).ligerCheckBox();
            }
            $(".l-table-checkbox input:checkbox", g.selectBox).change(function ()
            {
                if (this.checked && g.hasBind('beforeSelect'))
                {
                    var parentTD = null;
                    if ($(this).parent().get(0).tagName.toLowerCase() == "div")
                    {
                        parentTD = $(this).parent().parent();
                    } else
                    {
                        parentTD = $(this).parent();
                    }
                    if (parentTD != null && g.trigger('beforeSelect', [parentTD.attr("value"), parentTD.attr("text")]) == false)
                    {
                        g.selectBox.slideToggle("fast");
                        return false;
                    }
                }
                if (!p.isMultiSelect)
                {
                    if (this.checked)
                    {
                        $("input:checked", g.selectBox).not(this).each(function ()
                        {
                            this.checked = false;
                            $(".l-checkbox-checked", $(this).parent()).removeClass("l-checkbox-checked");
                        });
                        g.selectBox.slideToggle("fast");
                    }
                }
                g._checkboxUpdateValue();
            });
            $("table.l-table-nocheckbox td", g.selectBox).hover(function ()
            {
                $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
            g._addClickEven();
            //modify by songxf at 2013-7-17
            //选择项初始化
            if (!p.autocomplete&&!g.searching) {
                g._dataInit();
            }
            //modify end
        },
        //树
        setTree: function (tree)
        {
            var g = this, p = this.options;
            this.clearContent();
            g.selectBox.table.remove();
            if (tree.checkbox != false)
            {
                tree.onCheck = function ()
                {
                    var nodes = g.treeManager.getChecked();
                    var value = [];
                    var text = [];
                    $(nodes).each(function (i, node)
                    {
                        if (p.treeLeafOnly && node.data.children) return;
                        value.push(node.data[p.valueField]);
                        text.push(node.data[p.textField]);
                    });
                    g._changeValue(value.join(p.split), text.join(p.split));
                };
            }
            else
            {
                tree.onSelect = function (node)
                {
                    if (g.trigger('BeforeSelect'[node]) == false) return;
                    if (p.treeLeafOnly && node.data.children) return;
                    var value = node.data[p.valueField];
                    var text = node.data[p.textField];
                    g._changeValue(value, text);
                };
                tree.onCancelSelect = function (node)
                {
                    g._changeValue("", "");
                };
            }
            tree.onAfterAppend = function (domnode, nodedata)
            {
                if (!g.treeManager) return;
                var value = null;
                if (p.initValue) value = p.initValue;
                else if (g.valueField.val() != "") value = g.valueField.val();
                g.selectValueByTree(value);
            };
            g.tree = $("<ul></ul>");
            $("div:first", g.selectBox).append(g.tree);
            g.tree.ligerTree(tree);
            g.treeManager = g.tree.ligerGetTreeManager();
        },
        selectValueByTree: function (value)
        {
            var g = this, p = this.options;
            if (value != null)
            {
                var text = "";
                var valuelist = value.toString().split(p.split);
                $(valuelist).each(function (i, item)
                {
                    g.treeManager.selectNode(item.toString());
                    text += g.treeManager.getTextByID(item);
                    if (i < valuelist.length - 1) text += p.split;
                });
                g._changeValue(value, text);
            }
        },
        //表格
        setGrid: function (grid)
        {
            var g = this, p = this.options;
            if (g.grid) return;
            p.hideOnLoseFocus = false;
            this.clearContent();
            g.selectBox.addClass("l-box-select-lookup");
            g.selectBox.table.remove();
            var panel = $("div:first", g.selectBox);
            var conditionPanel = $("<div></div>").appendTo(panel);
            var gridPanel = $("<div></div>").appendTo(panel);
            g.conditionPanel = conditionPanel;
            //搜索框
            if (p.condition) {
                var conditionParm = $.extend({
                    labelWidth: 60,
                    space: 20
                }, p.condition);
                g.condition = conditionPanel.ligerForm(conditionParm);
            } else {
                conditionPanel.remove();
            }
            //列表
            grid = $.extend({
                columnWidth: 120,
                alternatingRow: false,
                frozen: true,
                rownumbers: true,
                allowUnSelectRow:true
            }, grid, {
                width: "100%",
                height: g.getGridHeight(),
                inWindow: false,
                isChecked: function (rowdata) {
                    var value = g.getValue();
                    if (!value) return false;
                    if (!p.valueField || !rowdata[p.valueField]) return false;
                    return $.inArray(rowdata[p.valueField].toString(), value.split(p.split)) != -1;
                }
            });
            g.grid = g.gridManager = gridPanel.ligerGrid(grid);
            var selecteds = [], onGridSelect = function () { 
                var value = [], text = [];
                $(selecteds).each(function (i, rowdata) {
                    value.push(rowdata[p.valueField]);
                    text.push(rowdata[p.textField]);
                }); 
                g._changeValue(value.join(p.split), text.join(p.split));
                g.trigger('gridSelect', {
                    value: value.join(p.split),
                    text: text.join(p.split),
                    data: selecteds
                });
            }, removeSelected = function (rowdata) {
                for (var i = selecteds.length - 1; i >= 0; i--) {
                    if (selecteds[i][p.valueField] == rowdata[p.valueField]) {
                        selecteds.splice(i, 1);
                    }
                }
            }, addSelected = function (rowdata) {
                for (var i = selecteds.length - 1; i >= 0; i--) {
                    if (selecteds[i][p.valueField] == rowdata[p.valueField]) {
                        return;
                    }
                }
                selecteds.push(rowdata);
            };
            if (grid.checkbox)
            {
                var onCheckRow = function (checked, rowdata) {
                    checked && addSelected(rowdata);
                    !checked && removeSelected(rowdata);
                };
                g.grid.bind('CheckAllRow', function (checked) {
                    $(g.grid.rows).each(function (rowdata) {
                        onCheckRow(checked, rowdata);
                    });
                    onGridSelect();
                });
                g.grid.bind('CheckRow', function (checked, rowdata) {
                    onCheckRow(checked, rowdata);
                    onGridSelect();
                });
            }
            else
            {
                g.grid.bind('SelectRow', function (rowdata) {
                    selecteds = [rowdata]; 
                    onGridSelect();
                });
                g.grid.bind('UnSelectRow', function () {
                    selecteds = [];
                    onGridSelect();
                });
            }
            g.bind('show', function () {
                g.grid.refreshSize();
            });
            g.bind("clear", function () {
                selecteds = [];
                g.grid.selecteds = [];
                g.grid._showData();
            });
            if (p.condition) {
                var containerBtn1 = $('<li style="margin-right:9px"><div></div></li>');
                var containerBtn2 = $('<li style="margin-right:9px;float:right"><div></div></li>');
                $("ul:first", conditionPanel).append(containerBtn1).append(containerBtn2).after('<div class="l-clear"></div>');
                $("div", containerBtn1).ligerButton({
                    text: '搜索', width: 40,
                    click: function () {
                        var rules = $.ligerui.getConditions(conditionPanel);
                        g.grid.setParm('condition', $.ligerui.toJSON(rules));
                        g.grid.reload();
                    }
                });
                $("div", containerBtn2).ligerButton({
                    text: '关闭',width:40,
                    click: function () {
                        g.selectBox.hide();
                    }
                });
            }
            g.grid.refreshSize();
        },
        getGridHeight: function (height) {
            var g = this, p = this.options;
            height = height || g.selectBox.height();
            height -= g.conditionPanel.height();
            return height;
        },
        _getValue: function ()
        {
            return $(this.valueField).val();
        },
        getValue: function ()
        {
            //获取值
            return this._getValue();
        }, 
        getText: function () {
            return this.inputText.val();
        },
        setText: function (value) {
            this.inputText.val(value);
        },
        updateStyle: function ()
        {
            var g = this, p = this.options;
            p.initValue = g._getValue();
            g._dataInit();
        },
        _dataInit: function ()
        {
            var g = this, p = this.options;
            var value = null; 
            //modify by songxf at 2013-7-29
            	if(g.selectedValue&&g.selectedText){
            		p.initValue=g.selectedValue;
            		p.initText=g.selectedText;
            	}
            //modify end
            if (p.initValue != null && p.initText != null)
            {
                g._changeValue(p.initValue, p.initText);
            }
            //根据值来初始化
			if (p.initValue != null)
            {
                value = p.initValue;
                if (p.tree)
                {
                    if(value)
                        g.selectValueByTree(value);
                }
                else if (g.data)
                {
                    var text = g.findTextByValue(value);
                    g._changeValue(value, text);
                }
            } 
            else if (g.valueField.val() != "")
            {
                value = g.valueField.val();
                if (p.tree)
                {
                    if(value)
                        g.selectValueByTree(value);
                }
                else if(g.data)
                {
                    var text = g.findTextByValue(value);
                    g._changeValue(value, text);
                }
            }
            if (!p.isShowCheckBox)
            {
                $("table tr", g.selectBox).find("td:first").each(function ()
                {
                    if (value != null && value == $(this).attr("value"))
                    {
                        $(this).addClass("l-selected");
                    } else
                    {
                        $(this).removeClass("l-selected");
                    }
                });
            }
            else
            { 
                $(":checkbox", g.selectBox).each(function ()
                {
                    var parentTD = null;
                    var checkbox = $(this);
                    if (checkbox.parent().get(0).tagName.toLowerCase() == "div")
                    {
                        parentTD = checkbox.parent().parent();
                    } else
                    {
                        parentTD = checkbox.parent();
                    }
                    if (parentTD == null) return;
                    $(".l-checkbox", parentTD).removeClass("l-checkbox-checked");
                    checkbox[0].checked = false;
                    var valuearr = (value || "").toString().split(p.split);
                    $(valuearr).each(function (i, item)
                    {
                        if (value != null && item == parentTD.attr("value"))
                        {
                            $(".l-checkbox", parentTD).addClass("l-checkbox-checked");
                            checkbox[0].checked = true;
                        }
                    });
                });
            }
        },
        //设置值到 文本框和隐藏域
        _changeValue: function (newValue, newText)
        {
            var g = this, p = this.options; 
            g.valueField.val(newValue);
            if (p && p.render)
            {
                g.inputText.val(p.render(newValue, newText));
            }
            else
            {
                g.inputText.val(newText);
            }
            g.selectedValue = newValue;
            g.selectedText = newText;
            g.inputText.trigger("change").focus(); 
            g.trigger('selected', [newValue, newText]); 
            //modify by songxf at 2013-6-25
            g.inputText.blur();
            //modify end
        },
        //更新选中的值(复选框)
        _checkboxUpdateValue: function ()
        {
            var g = this, p = this.options;
            var valueStr = "";
            var textStr = "";
            $("input:checked", g.selectBox).each(function ()
            {
                var parentTD = null;
                if ($(this).parent().get(0).tagName.toLowerCase() == "div")
                {
                    parentTD = $(this).parent().parent();
                } else
                {
                    parentTD = $(this).parent();
                }
                if (!parentTD) return;
                valueStr += parentTD.attr("value") + p.split;
                textStr += parentTD.attr("text") + p.split;
            });
            if (valueStr.length > 0) valueStr = valueStr.substr(0, valueStr.length - 1);
            if (textStr.length > 0) textStr = textStr.substr(0, textStr.length - 1);
            g._changeValue(valueStr, textStr);
        },
        _addClickEven: function ()
        {
            var g = this, p = this.options;
            //选项点击
            $(".l-table-nocheckbox td", g.selectBox).click(function ()
            {
                var value = $(this).attr("value");
                var index = parseInt($(this).attr('index'));
                var text = $(this).attr("text");
                if (g.hasBind('beforeSelect') && g.trigger('beforeSelect', [value, text]) == false)
                {
                    if (p.slide) g.selectBox.slideToggle("fast");
                    else g.selectBox.hide();
                    return false;
                }
                if ($(this).hasClass("l-selected"))
                {
                    if (p.slide) g.selectBox.slideToggle("fast");
                    else g.selectBox.hide();
                    return;
                }
                $(".l-selected", g.selectBox).removeClass("l-selected");
                $(this).addClass("l-selected");
                if (g.select)
                {
                    if (g.select[0].selectedIndex != index)
                    {
                        g.select[0].selectedIndex = index;
                        g.select.trigger("change");
                    }
                }
                if (p.slide)
                {
                    g.boxToggling = true;
                    g.selectBox.hide("fast", function ()
                    {
                        g.boxToggling = false;
                    })
                } else g.selectBox.hide();
                g._changeValue(value, text);
            });
        },
        updateSelectBoxPosition: function ()
        {
            var g = this, p = this.options;
            if (p.absolute)
            {
                var contentHeight = $(document).height();
                if (Number(g.wrapper.offset().top + 1 + g.wrapper.outerHeight() + g.selectBox.height()) > contentHeight
            			&& contentHeight > Number(g.selectBox.height() + 1))
                {
                    //若下拉框大小超过当前document下边框,且当前document上留白大于下拉内容高度,下拉内容向上展现
                    g.selectBox.css({ left: g.wrapper.offset().left, top: g.wrapper.offset().top - 1 - g.selectBox.height() });
                } else
                {
                	//modify by songxf at 2013-6-25
                    //g.selectBox.css({ left: g.wrapper.offset().left, top: g.wrapper.offset().top + 1 + g.wrapper.outerHeight() });
                	var contentHeight = $(document).height();
                	if(Number(g.wrapper.offset().top + 1 + g.wrapper.outerHeight()+g.selectBox.height()) > contentHeight
                			&& contentHeight > Number(g.selectBox.height() + 1)){
                		//若下拉框大小超过当前document下边框,且当前document上留白大于下拉内容高度,下拉内容向上展现
                		g.selectBox.css({ left: g.wrapper.offset().left, top: g.wrapper.offset().top - 1 - g.selectBox.height() });
                	}else{            		
                		g.selectBox.css({ left: g.wrapper.offset().left, top: g.wrapper.offset().top + 1 + g.wrapper.outerHeight() });
                	}
                	//modify end 
                } 
            }
            else
            {
                var topheight = g.wrapper.offset().top - $(window).scrollTop();
                var selfheight = g.selectBox.height() + textHeight + 4;
                if (topheight + selfheight > $(window).height() && topheight > selfheight)
                {
                    g.selectBox.css("marginTop", -1 * (g.selectBox.height() + textHeight + 5));
                }
            }
        },
        _toggleSelectBox: function (isHide)
        {
            var g = this, p = this.options;
            var textHeight = g.wrapper.height();  
            //modify by songxf at 2013-7-4
               	var managers = $.ligerui.find($.ligerui.controls.ComboBox);
				for ( var i = 0, l = managers.length; i < l; i++) {
					var o = managers[i];
					if(o.id!=g.id){
						if(o.selectBox.is(":visible")!=null&&o.selectBox.is(":visible")){
							o.selectBox.hide();
						}
					}
				}
				managers = $.ligerui.find($.ligerui.controls.DateEditor);
				for ( var i = 0, l = managers.length; i < l; i++) {
					var o = managers[i];
					if(o.id!=g.id){
						if(o.dateeditor.is(":visible")!=null&&o.dateeditor.is(":visible")){
							o.dateeditor.hide();
						}
					}
				}
        	//modify end      
            g.boxToggling = true;
            if (isHide)
            {
                if (p.slide)
                {
                    g.selectBox.slideToggle('fast', function ()
                    {
                        g.boxToggling = false;
                    });
                }
                else
                {
                    g.selectBox.hide();
                    g.boxToggling = false;
                }
            }
            else
            {
                //modify by songxf at 2013-7-29 清除搜索内容
	            if(p.filter&&!p.grid&&!p.tree){
	            	var searchText=$("input:first",g.selectBox.search).val();
	            	if(searchText&&searchText!=""){
	            		$("input:first",g.selectBox.search).val("");
	            		p.searchText="";
	            		g.set({ data: g.data });
		            }
	            }
            	//modify end
                g.updateSelectBoxPosition();
                if (p.slide)
                {
                    g.selectBox.slideToggle('fast', function ()
                    {
                        g.boxToggling = false;
                        if (!p.isShowCheckBox && $('td.l-selected', g.selectBox).length > 0)
                        {
                            var offSet = ($('td.l-selected', g.selectBox).offset().top - g.selectBox.offset().top);
                            $(".l-box-select-inner", g.selectBox).animate({ scrollTop: offSet });
                        }
                    });
                }
                else
                {
                    g.selectBox.show();
                    g.boxToggling = false;
                    if (!g.tree && !g.grid && !p.isShowCheckBox && $('td.l-selected', g.selectBox).length > 0)
                    {
                        var offSet = ($('td.l-selected', g.selectBox).offset().top - g.selectBox.offset().top);
                        $(".l-box-select-inner", g.selectBox).animate({ scrollTop: offSet });
                    }
                }
            }
            g.isShowed = g.selectBox.is(":visible");
            g.trigger('toggle', [isHide]);
            g.trigger(isHide ? 'hide' : 'show');
        }, 
        //modify by songxf at 2013-6-25
        setDisabled: function ()
        {
            var g = this, p = this.options;
            g.link.unbind();
            g.inputText.unbind();
            this.wrapper.addClass('l-text-disabled');
        },
        setEnabled: function ()
        {
            var g = this, p = this.options;
            g.link.unbind();
            g.inputText.unbind();
 			//modify by songxf at 2013-6-26
			//g.link
 			//.hover(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-hover";
            //}, function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger";
            //}).mousedown(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-pressed";
            //}).mouseup(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-hover";
            //})
            //.click(function ()
            //{
            //    if (p.disabled) return;
            //    if (g.trigger('beforeOpen') == false) return false;
            //    g._toggleSelectBox(g.selectBox.is(":visible"));
            //});
			g.link.click(function ()
            {
                if (p.disabled) return;
                if (g.trigger('beforeOpen') == false) return false;
                g._toggleSelectBox(g.selectBox.is(":visible"));
            });
			g.inputText.click(function ()
            {
                if (p.disabled) return;
                if (g.trigger('beforeOpen') == false) return false;
                g._toggleSelectBox(g.selectBox.is(":visible"));
            }).blur(function ()
            {
                if (p.disabled) return;
                g.wrapper.removeClass("l-text-focus");
            }).focus(function ()
            {
                if (p.disabled) return;
                g.wrapper.addClass("l-text-focus");
            });
             g.wrapper.removeClass("l-text-disabled");
        },
        //modify end
        _highLight: function (str, key)
        {
            var index = str.indexOf(key);
            if (index == -1) return str;
            return str.substring(0, index) + "<span class='l-highLight'>" + key + "</span>" + str.substring(key.length + index);
        },
        _setAutocomplete: function (value) {
            var g = this, p = this.options;
            if (!value) return;
            g.inputText.removeAttr("readonly");
            var lastText = g.inputText.val();
            g.inputText.keyup(function ()
            {
                setTimeout(function ()
                {
                    if (lastText == g.inputText.val()) return;
                    p.initValue = "";
                    g.valueField.val("");
                    if (p.url)
                    {
                        g.setParm('key', g.inputText.val());
                        g.set('url', p.url);
                        g.selectBox.show();
                    } else if (p.grid)
                    {
                        g.grid.setParm('key', g.inputText.val());
                        g.grid.reload();
                    } 
                    lastText = g.inputText.val();
                }, 1);
            });
        }
    });

    $.ligerui.controls.ComboBox.prototype.setValue = $.ligerui.controls.ComboBox.prototype.selectValue;
    //设置文本框和隐藏控件的值
    $.ligerui.controls.ComboBox.prototype.setInputValue = $.ligerui.controls.ComboBox.prototype._changeValue;
    

})(jQuery);
/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerDateEditor = function ()
    {
        return $.ligerui.run.call(this, "ligerDateEditor", arguments);
    };

    $.fn.ligerGetDateEditorManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetDateEditorManager", arguments);
    };

    $.ligerDefaults.DateEditor = {
        format: "yyyy-MM-dd hh:mm",
        showTime: false,
        onChangeDate: false,
        absolute: true,  //选择框是否在附加到body,并绝对定位
        cancelable: true,      //可取消选择
        //modify by tanxu at 2013-8-28
        readonly: null              //是否只读
        //end modify
    };
    $.ligerDefaults.DateEditorString = {
        dayMessage: ["日", "一", "二", "三", "四", "五", "六"],
        monthMessage: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
        todayMessage: "今天",
        closeMessage: "关闭"
    };
    $.ligerMethos.DateEditor = {};

    $.ligerui.controls.DateEditor = function (element, options)
    {
        $.ligerui.controls.DateEditor.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.DateEditor.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'DateEditor';
        },
        __idPrev: function ()
        {
            return 'DateEditor';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.DateEditor;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            if (!p.showTime && p.format.indexOf(" hh:mm") > -1)
                p.format = p.format.replace(" hh:mm", "");
            if (this.element.tagName.toLowerCase() != "input" || this.element.type != "text")
                return;
            g.inputText = $(this.element);
            if (!g.inputText.hasClass("l-text-field"))
                g.inputText.addClass("l-text-field");
            g.link = $('<div class="l-trigger"><div class="l-trigger-icon"></div></div>');
            g.text = g.inputText.wrap('<div class="l-text l-text-date"></div>').parent();
            g.text.append('<div class="l-text-l"></div><div class="l-text-r"></div>');
            g.text.append(g.link);
            //添加个包裹，
            g.textwrapper = g.text.wrap('<div class="l-text-wrapper"></div>').parent();
            var dateeditorHTML = "";
            dateeditorHTML += "<div class='l-box-dateeditor' style='display:none'>";
            dateeditorHTML += "    <div class='l-box-dateeditor-header'>";
            dateeditorHTML += "        <div class='l-box-dateeditor-header-btn l-box-dateeditor-header-prevyear'><span></span></div>";
            dateeditorHTML += "        <div class='l-box-dateeditor-header-btn l-box-dateeditor-header-prevmonth'><span></span></div>";
            dateeditorHTML += "        <div class='l-box-dateeditor-header-text'><a class='l-box-dateeditor-header-month'></a> , <a  class='l-box-dateeditor-header-year'></a></div>";
            dateeditorHTML += "        <div class='l-box-dateeditor-header-btn l-box-dateeditor-header-nextmonth'><span></span></div>";
            dateeditorHTML += "        <div class='l-box-dateeditor-header-btn l-box-dateeditor-header-nextyear'><span></span></div>";
            dateeditorHTML += "    </div>";
            dateeditorHTML += "    <div class='l-box-dateeditor-body'>";
            dateeditorHTML += "        <table cellpadding='0' cellspacing='0' border='0' class='l-box-dateeditor-calendar'>";
            dateeditorHTML += "            <thead>";
            dateeditorHTML += "                <tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr>";
            dateeditorHTML += "            </thead>";
            dateeditorHTML += "            <tbody>";
            dateeditorHTML += "                <tr class='l-first'><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr><tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr><tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr><tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr><tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr><tr><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td><td align='center'></td></tr>";
            dateeditorHTML += "            </tbody>";
            dateeditorHTML += "        </table>";
            dateeditorHTML += "        <ul class='l-box-dateeditor-monthselector'><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li></ul>";
            dateeditorHTML += "        <ul class='l-box-dateeditor-yearselector'><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li></ul>";
            dateeditorHTML += "        <ul class='l-box-dateeditor-hourselector'><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li></ul>";
            dateeditorHTML += "        <ul class='l-box-dateeditor-minuteselector'><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li><li></li></ul>";
            dateeditorHTML += "    </div>";
            dateeditorHTML += "    <div class='l-box-dateeditor-toolbar'>";
            dateeditorHTML += "        <div class='l-box-dateeditor-time'></div>";
            dateeditorHTML += "        <div class='l-button l-button-today'></div>";
            dateeditorHTML += "        <div class='l-button l-button-close'></div>";
            dateeditorHTML += "        <div class='l-clear'></div>";
            dateeditorHTML += "    </div>";
            dateeditorHTML += "</div>";
            g.dateeditor = $(dateeditorHTML);
            if (p.absolute)
                g.dateeditor.appendTo('body').addClass("l-box-dateeditor-absolute");
            else
                g.textwrapper.append(g.dateeditor);
            g.header = $(".l-box-dateeditor-header", g.dateeditor);
            g.body = $(".l-box-dateeditor-body", g.dateeditor);
            g.toolbar = $(".l-box-dateeditor-toolbar", g.dateeditor);

            g.body.thead = $("thead", g.body);
            g.body.tbody = $("tbody", g.body);
            g.body.monthselector = $(".l-box-dateeditor-monthselector", g.body);
            g.body.yearselector = $(".l-box-dateeditor-yearselector", g.body);
            g.body.hourselector = $(".l-box-dateeditor-hourselector", g.body);
            g.body.minuteselector = $(".l-box-dateeditor-minuteselector", g.body);

            g.toolbar.time = $(".l-box-dateeditor-time", g.toolbar);
            g.toolbar.time.hour = $("<a></a>");
            g.toolbar.time.minute = $("<a></a>");
            g.buttons = {
                btnPrevYear: $(".l-box-dateeditor-header-prevyear", g.header),
                btnNextYear: $(".l-box-dateeditor-header-nextyear", g.header),
                btnPrevMonth: $(".l-box-dateeditor-header-prevmonth", g.header),
                btnNextMonth: $(".l-box-dateeditor-header-nextmonth", g.header),
                btnYear: $(".l-box-dateeditor-header-year", g.header),
                btnMonth: $(".l-box-dateeditor-header-month", g.header),
                btnToday: $(".l-button-today", g.toolbar),
                btnClose: $(".l-button-close", g.toolbar)
            };
            var nowDate = new Date();
            g.now = {
                year: nowDate.getFullYear(),
                month: nowDate.getMonth() + 1, //注意这里
                day: nowDate.getDay(),
                date: nowDate.getDate(),
                hour: nowDate.getHours(),
                minute: nowDate.getMinutes()
            };
            //当前的时间
            g.currentDate = {
                year: nowDate.getFullYear(),
                month: nowDate.getMonth() + 1,
                day: nowDate.getDay(),
                date: nowDate.getDate(),
                hour: nowDate.getHours(),
                minute: nowDate.getMinutes()
            };
            //选择的时间
            g.selectedDate = null;
            //使用的时间
            g.usedDate = null;



            //初始化数据
            //设置周日至周六
            $("td", g.body.thead).each(function (i, td)
            {
                $(td).html(p.dayMessage[i]);
            });
            //设置一月到十一二月
            $("li", g.body.monthselector).each(function (i, li)
            {
                $(li).html(p.monthMessage[i]);
            });
            //设置按钮
            g.buttons.btnToday.html(p.todayMessage);
            g.buttons.btnClose.html(p.closeMessage);
            //设置时间
            if (p.showTime)
            {
                g.toolbar.time.show();
                g.toolbar.time.append(g.toolbar.time.hour).append(":").append(g.toolbar.time.minute);
                $("li", g.body.hourselector).each(function (i, item)
                {
                    var str = i;
                    if (i < 10) str = "0" + i.toString();
                    $(this).html(str);
                });
                $("li", g.body.minuteselector).each(function (i, item)
                {
                    var str = i;
                    if (i < 10) str = "0" + i.toString();
                    $(this).html(str);
                });
            }
            //设置主体
            g.bulidContent();
            //初始化   
            if (g.inputText.val() != "")
                g.onTextChange();
            /**************
            **bulid evens**
            *************/
            //modify by songxf at 2013-7-15
            g.onLink=false;//鼠标在下拉按钮上
            g.onDateEditor=false;//鼠标在日期选择框上
            g.dateeditor.hover(function(){
            	g.onDateEditor=true;
            }, function (e)
            {
            	g.onDateEditor=false;
                if (g.dateeditor.is(":visible") && !g.editorToggling)
                {
                    g.toggleDateEditor(true);
                }
            });
            //toggle even
            //g.link.hover(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-hover";
            //}, function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger";
            //}).mousedown(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-pressed";
            //}).mouseup(function ()
            //{
            //    if (p.disabled) return;
            //    this.className = "l-trigger-hover";
            //}).click(function ()
            //{
            //    if (p.disabled) return;
            //    g.bulidContent();
            //    g.toggleDateEditor(g.dateeditor.is(":visible"));
            //});
			g.link.click(function ()
            {
                if (p.disabled) return;
                g.bulidContent();
                g.toggleDateEditor(g.dateeditor.is(":visible"));
            });
			//modify end
            //不可用属性时处理
            if (p.disabled)
            {
                g.inputText.attr("readonly", "readonly");
                g.text.addClass('l-text-disabled');
            }
            //初始值
            if (p.initValue)
            {
                g.inputText.val(p.initValue);
            }
            g.buttons.btnClose.click(function ()
            {
                g.toggleDateEditor(true);
            });
            //日期 点击
            $("td", g.body.tbody).hover(function ()
            {
                if ($(this).hasClass("l-box-dateeditor-today")) return;
                $(this).addClass("l-box-dateeditor-over");
            }, function ()
            {
                $(this).removeClass("l-box-dateeditor-over");
            }).click(function ()
            {
                $(".l-box-dateeditor-selected", g.body.tbody).removeClass("l-box-dateeditor-selected");
                if (!$(this).hasClass("l-box-dateeditor-today"))
                    $(this).addClass("l-box-dateeditor-selected");
                g.currentDate.date = parseInt($(this).html());
                g.currentDate.day = new Date(g.currentDate.year, g.currentDate.month - 1, 1).getDay();
                if ($(this).hasClass("l-box-dateeditor-out"))
                {
                    if ($("tr", g.body.tbody).index($(this).parent()) == 0)
                    {
                        if (--g.currentDate.month == 0)
                        {
                            g.currentDate.month = 12;
                            g.currentDate.year--;
                        }
                    } else
                    {
                        if (++g.currentDate.month == 13)
                        {
                            g.currentDate.month = 1;
                            g.currentDate.year++;
                        }
                    }
                }
                g.selectedDate = {
                    year: g.currentDate.year,
                    month: g.currentDate.month,
                    date: g.currentDate.date
                };
                g.showDate();
                g.editorToggling = true;
                g.dateeditor.slideToggle('fast', function ()
                {
                    g.editorToggling = false;
                });
            });

            $(".l-box-dateeditor-header-btn", g.header).hover(function ()
            {
                $(this).addClass("l-box-dateeditor-header-btn-over");
            }, function ()
            {
                $(this).removeClass("l-box-dateeditor-header-btn-over");
            });
            //选择年份
            g.buttons.btnYear.click(function ()
            {
                //build year list
                if (!g.body.yearselector.is(":visible"))
                {
                    $("li", g.body.yearselector).each(function (i, item)
                    {
                        var currentYear = g.currentDate.year + (i - 4);
                        if (currentYear == g.currentDate.year)
                            $(this).addClass("l-selected");
                        else
                            $(this).removeClass("l-selected");
                        $(this).html(currentYear);
                    });
                }

                g.body.yearselector.slideToggle();
            });
            g.body.yearselector.hover(function () { }, function ()
            {
                $(this).slideUp();
            });
            $("li", g.body.yearselector).click(function ()
            {
                g.currentDate.year = parseInt($(this).html());
                g.body.yearselector.slideToggle();
                g.bulidContent();
            });
            //select month
            g.buttons.btnMonth.click(function ()
            {
                $("li", g.body.monthselector).each(function (i, item)
                {
                    //add selected style
                    if (g.currentDate.month == i + 1)
                        $(this).addClass("l-selected");
                    else
                        $(this).removeClass("l-selected");
                });
                g.body.monthselector.slideToggle();
            });
            g.body.monthselector.hover(function () { }, function ()
            {
                $(this).slideUp("fast");
            });
            $("li", g.body.monthselector).click(function ()
            {
                var index = $("li", g.body.monthselector).index(this);
                g.currentDate.month = index + 1;
                g.body.monthselector.slideToggle();
                g.bulidContent();
            });

            //选择小时
            g.toolbar.time.hour.click(function ()
            {
                $("li", g.body.hourselector).each(function (i, item)
                {
                    //add selected style
                    if (g.currentDate.hour == i)
                        $(this).addClass("l-selected");
                    else
                        $(this).removeClass("l-selected");
                });
                g.body.hourselector.slideToggle();
            });
            g.body.hourselector.hover(function () { }, function ()
            {
                $(this).slideUp("fast");
            });
            $("li", g.body.hourselector).click(function ()
            {
                var index = $("li", g.body.hourselector).index(this);
                g.currentDate.hour = index;
                g.body.hourselector.slideToggle();
                g.bulidContent();
                g.showDate();
            });
            //选择分钟
            g.toolbar.time.minute.click(function ()
            {
                $("li", g.body.minuteselector).each(function (i, item)
                {
                    //add selected style
                    if (g.currentDate.minute == i)
                        $(this).addClass("l-selected");
                    else
                        $(this).removeClass("l-selected");
                });
                g.body.minuteselector.slideToggle("fast", function ()
                {
                    var index = $("li", this).index($('li.l-selected', this));
                    if (index > 29)
                    {
                        var offSet = ($('li.l-selected', this).offset().top - $(this).offset().top);
                        $(this).animate({ scrollTop: offSet });
                    }
                });
            });
            g.body.minuteselector.hover(function () { }, function ()
            {
                $(this).slideUp("fast");
            });
            $("li", g.body.minuteselector).click(function ()
            {
                var index = $("li", g.body.minuteselector).index(this);
                g.currentDate.minute = index;
                g.body.minuteselector.slideToggle("fast");
                g.bulidContent();
                g.showDate();
            });

            //上个月
            g.buttons.btnPrevMonth.click(function ()
            {
                if (--g.currentDate.month == 0)
                {
                    g.currentDate.month = 12;
                    g.currentDate.year--;
                }
                g.bulidContent();
            });
            //下个月
            g.buttons.btnNextMonth.click(function ()
            {
                if (++g.currentDate.month == 13)
                {
                    g.currentDate.month = 1;
                    g.currentDate.year++;
                }
                g.bulidContent();
            });
            //上一年
            g.buttons.btnPrevYear.click(function ()
            {
                g.currentDate.year--;
                g.bulidContent();
            });
            //下一年
            g.buttons.btnNextYear.click(function ()
            {
                g.currentDate.year++;
                g.bulidContent();
            });
            //今天
            g.buttons.btnToday.click(function ()
            {
                g.currentDate = {
                    year: g.now.year,
                    month: g.now.month,
                    day: g.now.day,
                    date: g.now.date
                };
                g.selectedDate = {
                    year: g.now.year,
                    month: g.now.month,
                    day: g.now.day,
                    date: g.now.date
                };
                g.showDate();
                g.dateeditor.slideToggle("fast");
            });
            //文本框
            g.inputText.change(function ()
            {
                g.onTextChange();
            }).blur(function ()
            {
                g.text.removeClass("l-text-focus");
            }).focus(function ()
            {
                g.text.addClass("l-text-focus");
            });
            g.text.hover(function ()
            {
                g.text.addClass("l-text-over");
            }, function ()
            {
                g.text.removeClass("l-text-over");
            });
            //LEABEL 支持
            if (p.label)
            {
                g.labelwrapper = g.textwrapper.wrap('<div class="l-labeltext"></div>').parent();
                g.labelwrapper.prepend('<div class="l-text-label" style="float:left;display:inline;">' + p.label + ':&nbsp</div>');
                g.textwrapper.css('float', 'left');
                if (!p.labelWidth)
                {
                    p.labelWidth = $('.l-text-label', g.labelwrapper).outerWidth();
                } else
                {
                    $('.l-text-label', g.labelwrapper).outerWidth(p.labelWidth);
                }
                $('.l-text-label', g.labelwrapper).width(p.labelWidth);
                $('.l-text-label', g.labelwrapper).height(g.text.height());
                g.labelwrapper.append('<br style="clear:both;" />');
                if (p.labelAlign)
                {
                    $('.l-text-label', g.labelwrapper).css('text-align', p.labelAlign);
                }
                g.textwrapper.css({ display: 'inline' });
                g.labelwrapper.width(g.text.outerWidth() + p.labelWidth + 2);
            }

            g.set(p);
            
            //modify by songxf at 2013-7-23
            $(document).bind('mousedown.dateeditor', function(e){
              if(g.dateeditor.is(":visible")){
                     g.toggleDateEditor(true);
              }
            });
            g.dateeditor.bind('mousedown.dateeditor', function(e){
                     return false;
            });
            g.link.bind('mousedown.dateeditor', function(e){
                     return false;
            });
            g.inputText.bind('mousedown.dateeditor', function(e){
                     return false;
            });
            //modify end
            
        },
        destroy: function ()
        {
            if (this.textwrapper) this.textwrapper.remove();
            if (this.dateeditor) this.dateeditor.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        bulidContent: function ()
        {
            var g = this, p = this.options;
            //当前月第一天星期
            var thismonthFirstDay = new Date(g.currentDate.year, g.currentDate.month - 1, 1).getDay();
            //当前月天数
            var nextMonth = g.currentDate.month;
            var nextYear = g.currentDate.year;
            if (++nextMonth == 13)
            {
                nextMonth = 1;
                nextYear++;
            }
            var monthDayNum = new Date(nextYear, nextMonth - 1, 0).getDate();
            //当前上个月天数
            var prevMonthDayNum = new Date(g.currentDate.year, g.currentDate.month - 1, 0).getDate();

            g.buttons.btnMonth.html(p.monthMessage[g.currentDate.month - 1]);
            g.buttons.btnYear.html(g.currentDate.year);
            g.toolbar.time.hour.html(g.currentDate.hour);
            g.toolbar.time.minute.html(g.currentDate.minute);
            if (g.toolbar.time.hour.html().length == 1)
                g.toolbar.time.hour.html("0" + g.toolbar.time.hour.html());
            if (g.toolbar.time.minute.html().length == 1)
                g.toolbar.time.minute.html("0" + g.toolbar.time.minute.html());
            $("td", this.body.tbody).each(function () { this.className = "" });
            $("tr", this.body.tbody).each(function (i, tr)
            {
                $("td", tr).each(function (j, td)
                {
                    var id = i * 7 + (j - thismonthFirstDay);
                    var showDay = id + 1;
                    if (g.selectedDate && g.currentDate.year == g.selectedDate.year &&
                            g.currentDate.month == g.selectedDate.month &&
                            id + 1 == g.selectedDate.date)
                    {
                        if (j == 0 || j == 6)
                        {
                            $(td).addClass("l-box-dateeditor-holiday")
                        }
                        $(td).addClass("l-box-dateeditor-selected");
                        $(td).siblings().removeClass("l-box-dateeditor-selected");
                    }
                    else if (g.currentDate.year == g.now.year &&
                            g.currentDate.month == g.now.month &&
                            id + 1 == g.now.date)
                    {
                        if (j == 0 || j == 6)
                        {
                            $(td).addClass("l-box-dateeditor-holiday")
                        }
                        $(td).addClass("l-box-dateeditor-today");
                    }
                    else if (id < 0)
                    {
                        showDay = prevMonthDayNum + showDay;
                        $(td).addClass("l-box-dateeditor-out")
                                .removeClass("l-box-dateeditor-selected");
                    }
                    else if (id > monthDayNum - 1)
                    {
                        showDay = showDay - monthDayNum;
                        $(td).addClass("l-box-dateeditor-out")
                                .removeClass("l-box-dateeditor-selected");
                    }
                    else if (j == 0 || j == 6)
                    {
                        $(td).addClass("l-box-dateeditor-holiday")
                                .removeClass("l-box-dateeditor-selected");
                    }
                    else
                    {
                        td.className = "";
                    }

                    $(td).html(showDay);
                });
            });
        },
        updateSelectBoxPosition: function ()
        {
            var g = this, p = this.options;
            if (p.absolute)
            { 
                var contentHeight = $(document).height();
                if (Number(g.text.offset().top + 1 + g.text.outerHeight() + g.dateeditor.height()) > contentHeight
            			&& contentHeight > Number(g.dateeditor.height() + 1))
                {
                    //若下拉框大小超过当前document下边框,且当前document上留白大于下拉内容高度,下拉内容向上展现
                    g.dateeditor.css({ left: g.text.offset().left, top: g.text.offset().top - 1 - g.dateeditor.height() });
                } else
                {
                    g.dateeditor.css({ left: g.text.offset().left, top: g.text.offset().top + 1 + g.text.outerHeight() });
                }
            }
            else
            {
                if (g.text.offset().top + 4 > g.dateeditor.height() && g.text.offset().top + g.dateeditor.height() + textHeight + 4 - $(window).scrollTop() > $(window).height())
                {
                    g.dateeditor.css("marginTop", -1 * (g.dateeditor.height() + textHeight + 5));
                    g.showOnTop = true;
                }
                else
                {
                    g.showOnTop = false;
                }
            }
        },
        toggleDateEditor: function (isHide)
        {
            var g = this, p = this.options;
            var textHeight = g.text.height();
            //modify by songxf at 2013-7-4
               	var managers = $.ligerui.find($.ligerui.controls.ComboBox);
				for ( var i = 0, l = managers.length; i < l; i++) {
					var o = managers[i];
					if(o.id!=g.id){
						if(o.selectBox.is(":visible")!=null&&o.selectBox.is(":visible")){
							o.selectBox.hide();
						}
					}
				}
				managers = $.ligerui.find($.ligerui.controls.DateEditor);
				for ( var i = 0, l = managers.length; i < l; i++) {
					var o = managers[i];
					if(o.id!=g.id){
						if(o.dateeditor.is(":visible")!=null&&o.dateeditor.is(":visible")){
							o.dateeditor.hide();
						}
					}
				}
        	//modify end  
            g.editorToggling = true;
            if (isHide)
            {
                g.dateeditor.hide('fast', function ()
                {
                    g.editorToggling = false;
                });
                
            }
            else
            {
                g.updateSelectBoxPosition();
                g.dateeditor.slideDown('fast', function ()
                {
                    g.editorToggling = false;
                });
            }
        },
        showDate: function ()
        {
            var g = this, p = this.options;
            if (!this.currentDate) return;
            this.currentDate.hour = parseInt(g.toolbar.time.hour.html(), 10);
            this.currentDate.minute = parseInt(g.toolbar.time.minute.html(), 10);
            var dateStr = this.currentDate.year + '/' + this.currentDate.month + '/' + this.currentDate.date + ' ' + this.currentDate.hour + ':' + this.currentDate.minute;
            var myDate = new Date(dateStr);
            dateStr = g.getFormatDate(myDate);
            this.inputText.val(dateStr);
            this.onTextChange();
        },
        isDateTime: function (dateStr)
        {
            var g = this, p = this.options;
            var r = dateStr.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            if (r == null) return false;
            var d = new Date(r[1], r[3] - 1, r[4]);
            if (d == "NaN") return false;
            return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
        },
        isLongDateTime: function (dateStr)
        {
            var g = this, p = this.options;
            var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2})$/;
            var r = dateStr.match(reg);
            if (r == null) return false;
            var d = new Date(r[1], r[3] - 1, r[4], r[5], r[6]);
            if (d == "NaN") return false;
            return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4] && d.getHours() == r[5] && d.getMinutes() == r[6]);
        },
        getFormatDate: function (date)
        {
            var g = this, p = this.options;
            if (date == "NaN") return null;
            var format = p.format;
            var o = {
                "M+": date.getMonth() + 1,
                "d+": date.getDate(),
                "h+": date.getHours(),
                "m+": date.getMinutes(),
                "s+": date.getSeconds(),
                "q+": Math.floor((date.getMonth() + 3) / 3),
                "S": date.getMilliseconds()
            }
            if (/(y+)/.test(format))
            {
                format = format.replace(RegExp.$1, (date.getFullYear() + "")
                .substr(4 - RegExp.$1.length));
            }
            for (var k in o)
            {
                if (new RegExp("(" + k + ")").test(format))
                {
                    format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                    : ("00" + o[k]).substr(("" + o[k]).length));
                }
            }
            return format;
        },
        clear: function () {
            this.set('value', '');
        },
        //取消选择 
        _setCancelable: function (value) {
            var g = this, p = this.options;
            if (!value && g.unselect) {
                g.unselect.remove();
                g.unselect = null; 
            }
            if (!value && !g.unselect) return; 
            g.unselect = $('<div class="l-trigger l-trigger-cancel"><div class="l-trigger-icon"></div></div>').hide();
            g.text.hover(function () {
                g.unselect.show();
            }, function () {
                g.unselect.hide();
            })
            if (!p.disabled && p.cancelable) {
                g.text.append(g.unselect);
            }
            g.unselect.hover(function () {
                this.className = "l-trigger-hover l-trigger-cancel";
            }, function () {
                this.className = "l-trigger l-trigger-cancel";
            }).click(function () {
                g.clear();
            });
        },
        //恢复
        _rever: function ()
        {
            var g = this, p = this.options;
            if (!g.usedDate)
            {
                g.inputText.val("");
            } else
            {
                g.inputText.val(g.getFormatDate(g.usedDate));
            }
        },
        _getMatch: function (format)
        {
            var r = [-1, -1, -1, -1, -1, -1], groupIndex = 0, regStr = "^", str = format || this.options.format;
            while (true)
            {
                var tmp_r = str.match(/^yyyy|MM|dd|mm|hh|HH|ss|-|\/|:|\s/);
                if (tmp_r)
                {
                    var c = tmp_r[0].charAt(0);
                    var mathLength = tmp_r[0].length;
                    var index = 'yMdhms'.indexOf(c);
                    if (index != -1)
                    {
                        r[index] = groupIndex + 1;
                        regStr += "(\\d{1," + mathLength + "})";
                    } else
                    {
                        var st = c == ' ' ? '\\s' : c;
                        regStr += "(" + st + ")";
                    }
                    groupIndex++;
                    if (mathLength == str.length)
                    {
                        regStr += "$";
                        break;
                    }
                    str = str.substring(mathLength);
                } else
                {
                    return null;
                }
            }
            return { reg: new RegExp(regStr), position: r };
        },
        _bulidDate: function (dateStr)
        {
            var g = this, p = this.options;
            var r = this._getMatch();
            if (!r) return null;
            var t = dateStr.match(r.reg);
            if (!t) return null;
            var tt = {
                y: r.position[0] == -1 ? 1900 : t[r.position[0]],
                M: r.position[1] == -1 ? 0 : parseInt(t[r.position[1]],10) - 1,
                d: r.position[2] == -1 ? 1 : parseInt(t[r.position[2]],10),
                h: r.position[3] == -1 ? 0 : parseInt(t[r.position[3]],10),
                m: r.position[4] == -1 ? 0 : parseInt(t[r.position[4]],10),
                s: r.position[5] == -1 ? 0 : parseInt(t[r.position[5]], 10)
            }; 
            if (tt.M < 0 || tt.M > 11 || tt.d < 0 || tt.d > 31) return null;
            var d = new Date(tt.y, tt.M, tt.d);
            if (p.showTime)
            {
                if (tt.m < 0 || tt.m > 59 || tt.h < 0 || tt.h > 23 || tt.s < 0 || tt.s > 59) return null;
                d.setHours(tt.h);
                d.setMinutes(tt.m);
                d.setSeconds(tt.s);
            }
            return d;
        },
        updateStyle: function ()
        {
            this.onTextChange();
        },
        onTextChange: function ()
        {
            var g = this, p = this.options;
            var val = g.inputText.val();
            if (!val)
            {
                g.selectedDate = null;
                return true;
            }
            var newDate = g._bulidDate(val);
            if (!newDate)
            {
                g._rever();
                return;
            }
            else
            {
                g.usedDate = newDate;
            }
            g.selectedDate = {
                year: g.usedDate.getFullYear(),
                month: g.usedDate.getMonth() + 1, //注意这里
                day: g.usedDate.getDay(),
                date: g.usedDate.getDate(),
                hour: g.usedDate.getHours(),
                minute: g.usedDate.getMinutes()
            };
            g.currentDate = {
                year: g.usedDate.getFullYear(),
                month: g.usedDate.getMonth() + 1, //注意这里
                day: g.usedDate.getDay(),
                date: g.usedDate.getDate(),
                hour: g.usedDate.getHours(),
                minute: g.usedDate.getMinutes()
            };
            var formatVal = g.getFormatDate(newDate);
            g.inputText.val(formatVal);
            g.trigger('changeDate', [formatVal]);
            if ($(g.dateeditor).is(":visible"))
                g.bulidContent();
        },
        _setHeight: function (value)
        {
            var g = this;
            if (value > 4)
            {
                g.text.css({ height: value });
                g.inputText.css({ height: value });
                g.textwrapper.css({ height: value });
            }
        },
        _setWidth: function (value)
        {
            var g = this;
            if (value > 20)
            {
                g.text.css({ width: value });
                g.inputText.css({ width: value - 20 });
                g.textwrapper.css({ width: value });
            }
        },
        _setValue: function (value)
        {
            var g = this;
            if (!value) g.inputText.val('');
            if (typeof value == "string")
            {
                if (/^\/Date/.test(value))
                {
                    value = value.replace(/^\//, "new ").replace(/\/$/, "");
                    eval("value = " + value);
                }
                else
                {
                    g.inputText.val(value);
                }
            }
            if (typeof value == "object")
            {
                if (value instanceof Date)
                {
                    g.inputText.val(g.getFormatDate(value));
                    g.onTextChange();
                }
            }
        },
        _getValue: function ()
        {
            return this.usedDate;
        },
        setEnabled: function ()
        {
            var g = this, p = this.options;
            this.inputText.removeAttr("readonly");
            this.text.removeClass('l-text-disabled');
            p.disabled = false;
        },
        setDisabled: function ()
        {
            var g = this, p = this.options;
            this.inputText.attr("readonly", "readonly");
            this.text.addClass('l-text-disabled');
            p.disabled = true;
        }
    });


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{
    var l = $.ligerui;

    //全局事件
    $(".l-dialog-btn").live('mouseover', function ()
    {
        $(this).addClass("l-dialog-btn-over");
    }).live('mouseout', function ()
    {
        $(this).removeClass("l-dialog-btn-over");
    });
    $(".l-dialog-tc .l-dialog-close").live('mouseover', function ()
    {
        $(this).addClass("l-dialog-close-over");
    }).live('mouseout', function ()
    {
        $(this).removeClass("l-dialog-close-over");
    });


    $.ligerDialog = function ()
    {
        return l.run.call(null, "ligerDialog", arguments, { isStatic: true });
    };

    //dialog 图片文件夹的路径 预加载
    $.ligerui.DialogImagePath = "../../lib/ligerUI/skins/Aqua/images/win/";

    function prevImage(paths)
    {
        for (var i in paths)
        {
            $('<img />').attr('src', l.DialogImagePath + paths[i]);
        }
    }
    //prevImage(['dialog.gif', 'dialog-winbtns.gif', 'dialog-bc.gif', 'dialog-tc.gif']);

    $.ligerDefaults.Dialog = {
        cls: null,       //给dialog附加css class
        id: null,        //给dialog附加id
        buttons: null, //按钮集合 
        isDrag: true,   //是否拖动
        width: 280,     //宽度
        height: null,   //高度，默认自适应 
        content: '',    //内容
        target: null,   //目标对象，指定它将以appendTo()的方式载入
        url: null,      //目标页url，默认以iframe的方式载入
        load: false,     //是否以load()的方式加载目标页的内容 
        type: 'none',   //类型 warn、success、error、question
        left: null,     //位置left
        top: null,      //位置top
        modal: true,    //是否模态对话框
        name: null,     //创建iframe时 作为iframe的name和id 
        isResize: false, // 是否调整大小
        allowClose: true, //允许关闭
        opener: null,
        timeParmName: null,  //是否给URL后面加上值为new Date().getTime()的参数，如果需要指定一个参数名即可
        closeWhenEnter: null, //回车时是否关闭dialog
        //isHidden: true,        //关闭对话框时是否只是隐藏，还是销毁对话框
        //modify by songxf at 2013-6-25
        isHidden: false,        //关闭对话框时是否只是隐藏，还是销毁对话框
        //modify end
        show: true,          //初始化时是否马上显示
        title: '提示',        //头部 
        showMax: false,                             //是否显示最大化按钮 
        showToggle: false,                          //是否显示收缩窗口按钮
        showMin: false,                             //是否显示最小化按钮
        slide: $.browser.msie ? false : true,        //是否以动画的形式显示 
        fixedType: null,            //在固定的位置显示, 可以设置的值有n, e, s, w, ne, se, sw, nw
        showType: null,             //显示类型,可以设置为slide(固定显示时有效)
        onLoaded: null,
        onExtend: null,
        onExtended:null,
        onCollapse:null,
        onCollapseed: null,
        onContentHeightChange: null,
        onClose: null,
        onClosed: null,
        onStopResize : null
    };
    $.ligerDefaults.DialogString = {
        titleMessage: '提示',                     //提示文本标题
        ok: '确定',
        yes: '是',
        no: '否',
        cancel: '取消',
        waittingMessage: '正在等待中,请稍候...'
    };

    $.ligerMethos.Dialog = $.ligerMethos.Dialog || {};


    l.controls.Dialog = function (options)
    {
        l.controls.Dialog.base.constructor.call(this, null, options);
    };
    l.controls.Dialog.ligerExtend(l.core.Win, {
        __getType: function ()
        {
            return 'Dialog';
        },
        __idPrev: function ()
        {
            return 'Dialog';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Dialog;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            var tmpId = "";
            g.set(p, true);
            var dialog = $('<div class="l-dialog"><table class="l-dialog-table" cellpadding="0" cellspacing="0" border="0"><tbody><tr><td class="l-dialog-tl"></td><td class="l-dialog-tc"><div class="l-dialog-tc-inner"><div class="l-dialog-icon"></div><div class="l-dialog-title"></div><div class="l-dialog-winbtns"><div class="l-dialog-winbtn l-dialog-close"></div></div></div></td><td class="l-dialog-tr"></td></tr><tr><td class="l-dialog-cl"></td><td class="l-dialog-cc"><div class="l-dialog-body"><div class="l-dialog-image"></div> <div class="l-dialog-content"></div><div class="l-dialog-buttons"><div class="l-dialog-buttons-inner"></div></td><td class="l-dialog-cr"></td></tr><tr><td class="l-dialog-bl"></td><td class="l-dialog-bc"></td><td class="l-dialog-br"></td></tr></tbody></table></div>');
            $('body').append(dialog);
            g.dialog = dialog;
            g.element = dialog[0];
            g.dialog.body = $(".l-dialog-body:first", g.dialog);
            g.dialog.header = $(".l-dialog-tc-inner:first", g.dialog);
            g.dialog.winbtns = $(".l-dialog-winbtns:first", g.dialog.header);
            g.dialog.buttons = $(".l-dialog-buttons:first", g.dialog);
            g.dialog.content = $(".l-dialog-content:first", g.dialog);
            g.set(p, false);

            if (p.allowClose == false) $(".l-dialog-close", g.dialog).remove();
            if (p.target || p.url || p.type == "none")
            {
                p.type = null;
                g.dialog.addClass("l-dialog-win");
            }
            if (p.cls) g.dialog.addClass(p.cls);
            if (p.id) g.dialog.attr("id", p.id); 
            //设置锁定屏幕、拖动支持 和设置图片
            g.mask();
            if (p.isDrag)
                g._applyDrag();
            if (p.isResize)
                g._applyResize();
            if (p.type)
                g._setImage();
            else
            {
                $(".l-dialog-image", g.dialog).remove();
                g.dialog.content.addClass("l-dialog-content-noimage");
            }
            if (!p.show)
            {
                g.unmask();
                g.dialog.hide();
            }
            //设置主体内容
            if (p.target)
            {
                g.dialog.content.prepend(p.target);
                $(p.target).show();
            }
            else if (p.url)
            {
                if (p.timeParmName)
                {
                    p.url += p.url.indexOf('?') == -1 ? "?" : "&";
                    p.url += p.timeParmName + "=" + new Date().getTime();
                }
                if (p.load)
                {
                    g.dialog.body.load(p.url, function ()
                    {
                        g._saveStatus();
                        g.trigger('loaded');
                    });
                }
                else
                {
                    g.jiframe = $("<iframe frameborder='0'></iframe>");
                    var framename = p.name ? p.name : "ligerwindow" + new Date().getTime();
                    g.jiframe.attr("name", framename);
                    g.jiframe.attr("id", framename);
                    g.dialog.content.prepend(g.jiframe);
                    g.dialog.content.addClass("l-dialog-content-nopadding");
                    setTimeout(function ()
                    {
                        g.jiframe.attr("src", p.url);
                        g.frame = window.frames[g.jiframe.attr("name")];
                    }, 0);
                    // 为了解决ie下对含有iframe的div窗口销毁不正确，进而导致第二次打开时焦点不在当前图层的问题
                    // 加入以下代码 
                    tmpId = 'jquery_ligerui_' + new Date().getTime();
                    g.tmpInput = $("<input></input>");
                    g.tmpInput.attr("id", tmpId);
                    g.dialog.content.prepend(g.tmpInput);
                }
            }
            if (p.opener) g.dialog.opener = p.opener;
            //设置按钮
            if (p.buttons)
            {
                $(p.buttons).each(function (i, item)
                {
                    var btn = $('<div class="l-dialog-btn"><div class="l-dialog-btn-l"></div><div class="l-dialog-btn-r"></div><div class="l-dialog-btn-inner"></div></div>');
                    $(".l-dialog-btn-inner", btn).html(item.text);
                    $(".l-dialog-buttons-inner", g.dialog.buttons).prepend(btn);
                    item.width && btn.width(item.width);
                    item.onclick && btn.click(function () { item.onclick(item, g, i) });
                });
            } else
            {
                g.dialog.buttons.remove();
            }
            $(".l-dialog-buttons-inner", g.dialog.buttons).append("<div class='l-clear'></div>");


            $(".l-dialog-title", g.dialog)
            .bind("selectstart", function () { return false; });
            g.dialog.click(function ()
            {
                l.win.setFront(g);
            });

            //设置事件
            $(".l-dialog-tc .l-dialog-close", g.dialog).click(function ()
            {
                if (p.isHidden)
                    g.hide();
                else
                    g.close();
            });
            if (!p.fixedType)
            {
                //位置初始化
                var left = 0;
                var top = 0;
                var width = p.width || g.dialog.width();
                if (p.slide == true) p.slide = 'fast';
                if (p.left != null) left = p.left;
                else p.left = left = 0.5 * ($(window).width() - width);
                if (p.top != null) top = p.top;
                else p.top = top = 0.5 * ($(window).height() - g.dialog.height()) + $(window).scrollTop() - 10;
                if (left < 0) p.left = left = 0;
                if (top < 0) p.top = top = 0; 
                g.dialog.css({ left: left, top: top });
            }
            g.show();
            $('body').bind('keydown.dialog', function (e)
            {
                var key = e.which;
                if (key == 13)
                {
                    g.enter();
                }
                else if (key == 27)
                {
                    g.esc();
                }
            });

            g._updateBtnsWidth();
            g._saveStatus();
            g._onReisze();
            if (tmpId != "")
            {
                $("#" + tmpId).focus();
                $("#" + tmpId).remove();
            }
        },
        _borderX: 12,
        _borderY: 32,
        doMax: function (slide)
        {
            var g = this, p = this.options;
            //modify by songxf at 2013-6-25
            //var width = $(window).width(), height = $(window).height(), left = 0, top = 0;
            var width = $(document).width(), height = $(document).height(), left = 0, top = 0;
            //modify end
            if (l.win.taskbar)
            {
                height -= l.win.taskbar.outerHeight();
                if (l.win.top) top += l.win.taskbar.outerHeight();
            }
            if (slide)
            {
                g.dialog.body.animate({ width: width - g._borderX }, p.slide);
                g.dialog.animate({ left: left, top: top }, p.slide);
                g.dialog.content.animate({ height: height - g._borderY - g.dialog.buttons.outerHeight() }, p.slide, function ()
                {
                    g._onReisze();
                });
            }
            else
            {
                g.set({ width: width, height: height, left: left, top: top });
                g._onReisze();
            }
        },
        //最大化
        max: function ()
        {
            var g = this, p = this.options;
            if (g.winmax)
            {
                g.winmax.addClass("l-dialog-recover");
                g.doMax(p.slide);
                if (g.wintoggle)
                {
                    if (g.wintoggle.hasClass("l-dialog-extend"))
                        g.wintoggle.addClass("l-dialog-toggle-disabled l-dialog-extend-disabled");
                    else
                        g.wintoggle.addClass("l-dialog-toggle-disabled l-dialog-collapse-disabled");
                }
                if (g.resizable) g.resizable.set({ disabled: true });
                if (g.draggable) g.draggable.set({ disabled: true });
                g.maximum = true;

                $(window).bind('resize.dialogmax', function ()
                {
                    g.doMax(false);
                });
            }
        },

        //恢复
        recover: function ()
        {
            var g = this, p = this.options;
            if (g.winmax)
            {
                g.winmax.removeClass("l-dialog-recover");
                if (p.slide)
                {
                    g.dialog.body.animate({ width: g._width - g._borderX }, p.slide);
                    g.dialog.animate({ left: g._left, top: g._top }, p.slide);
                    //modify by songxf at 2013-6-25
                    //g.dialog.content.animate({ height: g._height - g._borderY - g.dialog.buttons.outerHeight() }, p.slide, function ()
                    g.dialog.content.animate({ height: g._height }, p.slide, function ()
                    //modify end
                    {
                        g._onReisze();
                    });
                }
                else
                {	
                	//modify by songxf at 2013-6-25
                    //g.set({ width: g._width, height: g._height, left: g._left, top: g._top });
                	 g.set({ width: g._width, height: g._height+this._borderY, left: g._left, top: g._top });
                	//modify end
                    g._onReisze();
                }
                if (g.wintoggle)
                {
                    g.wintoggle.removeClass("l-dialog-toggle-disabled l-dialog-extend-disabled l-dialog-collapse-disabled");
                }

                $(window).unbind('resize.dialogmax');
            }
            if (this.resizable) this.resizable.set({ disabled: false });
            if (g.draggable) g.draggable.set({ disabled: false });
            g.maximum = false;
        },

        //最小化
        min: function ()
        {
            var g = this, p = this.options;
            var task = l.win.getTask(this);
            if (p.slide)
            {
                g.dialog.body.animate({ width: 1 }, p.slide);
                task.y = task.offset().top + task.height();
                task.x = task.offset().left + task.width() / 2;
                g.dialog.animate({ left: task.x, top: task.y }, p.slide, function ()
                {
                    g.dialog.hide();
                });
            }
            else
            {
                g.dialog.hide();
            }
            g.unmask();
            g.minimize = true;
            g.actived = false;
        },

        active: function ()
        {
            var g = this, p = this.options;
            if (g.minimize)
            {
                var width = g._width, height = g._height, left = g._left, top = g._top;
                if (g.maximum)
                {
                	//modify by songxf at 2013-6-25
                    //width = $(window).width();
                    //height = $(window).height();
                    width = $(document).width();
                    height = $(document).height();
                    //modify end
                    left = top = 0;
                    if (l.win.taskbar)
                    {
                        height -= l.win.taskbar.outerHeight();
                        if (l.win.top) top += l.win.taskbar.outerHeight();
                    }
                }
                if (p.slide)
                {
                    g.dialog.body.animate({ width: width - g._borderX }, p.slide);
                    g.dialog.animate({ left: left, top: top }, p.slide);
                }
                else
                {
                    g.set({ width: width, height: height, left: left, top: top });
                }
            }
            g.actived = true;
            g.minimize = false;
            l.win.setFront(g);
            g.show();
        },

        //展开 收缩
        toggle: function ()
        {

            var g = this, p = this.options;
            if (!g.wintoggle) return;
            if (g.wintoggle.hasClass("l-dialog-extend"))
                g.extend();
            else
                g.collapse();
        },

        //收缩
        collapse: function ()
        {
            var g = this, p = this.options;
            if (!g.wintoggle) return;
            if (p.slide)
                g.dialog.content.animate({ height: 1 }, p.slide);
            else
                g.dialog.content.height(1);
            if (this.resizable) this.resizable.set({ disabled: true });
        },

        //展开
        extend: function ()
        {
            var g = this, p = this.options;
            if (!g.wintoggle) return;
            var contentHeight = g._height - g._borderY - g.dialog.buttons.outerHeight();
            if (p.slide)
                g.dialog.content.animate({ height: contentHeight }, p.slide);
            else
            	//modify by songxf at 2013-6-25
                //g.dialog.content.height(contentHeight);
            	  g.dialog.content.height(g._height);
            	//modify end
            if (this.resizable) this.resizable.set({ disabled: false });
        },
        _updateBtnsWidth: function ()
        {
            var g = this;
            var btnscount = $(">div", g.dialog.winbtns).length;
            g.dialog.winbtns.width(22 * btnscount);
        },
        _setLeft: function (value)
        {
            if (!this.dialog) return;
            if (value != null)
                this.dialog.css({ left: value });
        },
        _setTop: function (value)
        {
            if (!this.dialog) return;
            if (value != null)
                this.dialog.css({ top: value });
        },
        _setWidth: function (value)
        {
            if (!this.dialog) return;
            if (value >= this._borderX)
            {
                this.dialog.body.width(value - this._borderX);
            }
        },
        _setHeight: function (value)
        {
            var g = this, p = this.options;
            if (!this.dialog) return;
            if (value >= this._borderY)
            {
            	//modify by songxf at 2013-6-25
                //var height = value - this._borderY - g.dialog.buttons.outerHeight();
            	 var height = value - this._borderY ;
            	//modify end
                if (g.trigger('ContentHeightChange', [height]) == false) return;
                g.dialog.content.height(height);
                g.trigger('ContentHeightChanged', [height]);
            }
        },
        _setShowMax: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                if (!g.winmax)
                {
                    g.winmax = $('<div class="l-dialog-winbtn l-dialog-max"></div>').appendTo(g.dialog.winbtns)
                    .hover(function ()
                    {
                        if ($(this).hasClass("l-dialog-recover"))
                            $(this).addClass("l-dialog-recover-over");
                        else
                            $(this).addClass("l-dialog-max-over");
                    }, function ()
                    {
                        $(this).removeClass("l-dialog-max-over l-dialog-recover-over");
                    }).click(function ()
                    {
                        if ($(this).hasClass("l-dialog-recover"))
                            g.recover();
                        else
                            g.max();
                    });
                }
            }
            else if (g.winmax)
            {
                g.winmax.remove();
                g.winmax = null;
            }
            g._updateBtnsWidth();
        },
        _setShowMin: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                if (!g.winmin)
                {
                    g.winmin = $('<div class="l-dialog-winbtn l-dialog-min"></div>').appendTo(g.dialog.winbtns)
                    .hover(function ()
                    {
                        $(this).addClass("l-dialog-min-over");
                    }, function ()
                    {
                        $(this).removeClass("l-dialog-min-over");
                    }).click(function ()
                    {
                        g.min();
                    });
                    l.win.addTask(g);
                }
            }
            else if (g.winmin)
            {
                g.winmin.remove();
                g.winmin = null;
            }
            g._updateBtnsWidth();
        },
        _setShowToggle: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                if (!g.wintoggle)
                {
                    g.wintoggle = $('<div class="l-dialog-winbtn l-dialog-collapse"></div>').appendTo(g.dialog.winbtns)
                   .hover(function ()
                   {
                       if ($(this).hasClass("l-dialog-toggle-disabled")) return;
                       if ($(this).hasClass("l-dialog-extend"))
                           $(this).addClass("l-dialog-extend-over");
                       else
                           $(this).addClass("l-dialog-collapse-over");
                   }, function ()
                   {
                       $(this).removeClass("l-dialog-extend-over l-dialog-collapse-over");
                   }).click(function ()
                   {
                       if ($(this).hasClass("l-dialog-toggle-disabled")) return;
                       if (g.wintoggle.hasClass("l-dialog-extend"))
                       {
                           if (g.trigger('extend') == false) return;
                           g.wintoggle.removeClass("l-dialog-extend");
                           g.extend();
                           g.trigger('extended');
                       }
                       else
                       {
                           if (g.trigger('collapse') == false) return;
                           g.wintoggle.addClass("l-dialog-extend");
                           g.collapse();
                           g.trigger('collapseed')
                       }
                   });
                }
            }
            else if (g.wintoggle)
            {
                g.wintoggle.remove();
                g.wintoggle = null;
            }
        },
        //按下回车
        enter: function ()
        {
            var g = this, p = this.options;
            var isClose;
            if (p.closeWhenEnter != undefined)
            {
                isClose = p.closeWhenEnter;
            }
            else if (p.type == "warn" || p.type == "error" || p.type == "success" || p.type == "question")
            {
                isClose = true;
            }
            if (isClose)
            {
                g.close();
            }
        },
        esc: function ()
        {

        },
        _removeDialog: function ()
        {
            var g = this, p = this.options;
            if (p.showType && p.fixedType)
            {
                g.dialog.animate({ bottom: -1 * p.height }, function ()
                {
                    remove();
                });
            }
            else
            {
                remove();
            }
            function remove()
            {
                var jframe = $('iframe', g.dialog);
                if (jframe.length)
                {
                    var frame = jframe[0];
                    frame.src = "about:blank";
                    frame.contentWindow.document.write('');
                    $.browser.msie && CollectGarbage();
                    jframe.remove();
                }
                g.dialog.remove();
            }
        },
        close: function ()
        { 
            var g = this, p = this.options;
            if (g.trigger('Close') == false) return;
            g.doClose();
            if (g.trigger('Closed') == false) return;
        },
        doClose : function()
        {
            var g = this;
            l.win.removeTask(this);
            g.unmask();
            g._removeDialog();
            $('body').unbind('keydown.dialog');
        },
        _getVisible: function ()
        {
            return this.dialog.is(":visible");
        },
        _setUrl: function (url)
        {
            var g = this, p = this.options;
            p.url = url;
            if (p.load)
            {
                g.dialog.body.html("").load(p.url, function ()
                {
                    g.trigger('loaded');
                });
            }
            else if (g.jiframe)
            {
                g.jiframe.attr("src", p.url);
            }
        },
        _setContent: function (content)
        {
            this.dialog.content.html(content);
        },
        _setTitle: function (value)
        {
            var g = this; var p = this.options;
            if (value)
            {
                $(".l-dialog-title", g.dialog).html(value);
            }
        },
        _hideDialog: function ()
        {
            var g = this, p = this.options;
            if (p.showType && p.fixedType)
            {
                g.dialog.animate({ bottom: -1 * p.height }, function ()
                {
                    g.dialog.hide();
                });
            } else
            {
                g.dialog.hide();
            }
        },
        hidden: function ()
        { 
            var g = this;
            l.win.removeTask(g);
            g.dialog.hide();
            g.unmask();
        },
        show: function ()
        {
            var g = this, p = this.options;
            g.mask();
            if (p.fixedType)
            {
                if (p.showType)
                {
                    g.dialog.css({ bottom: -1 * p.height }).addClass("l-dialog-fixed");
                    g.dialog.show().animate({ bottom: 0 });
                }
                else
                {
                    g.dialog.show().css({ bottom: 0 });
                }
            }
            else
            {
                g.dialog.show();
            }
            //前端显示 
            $.ligerui.win.setFront.ligerDefer($.ligerui.win, 100, [g]);
        },
        setUrl: function (url)
        {
            this._setUrl(url);
        },
        _saveStatus: function ()
        {
            var g = this;
            g._width = g.dialog.body.width();
            g._height = g.dialog.body.height();
            var top = 0;
            var left = 0;
            if (!isNaN(parseInt(g.dialog.css('top'))))
                top = parseInt(g.dialog.css('top'));
            if (!isNaN(parseInt(g.dialog.css('left'))))
                left = parseInt(g.dialog.css('left'));
            g._top = top;
            g._left = left;
        },
        _applyDrag: function ()
        {
            var g = this, p = this.options;
            if ($.fn.ligerDrag)
                g.draggable = g.dialog.ligerDrag({ handler: '.l-dialog-title', animate: false,
                    onStartDrag: function ()
                    {
                        l.win.setFront(g);
			g.dialog.content.addClass('l-dialog-content-dragging');
                    },
                    onDrag: function (current,e)
                    {
                        var pageY = e.pageY || e.screenY;
                        if (pageY < 0) return false;
                    },
                    onStopDrag: function ()
                    { 
			g.dialog.content.removeClass('l-dialog-content-dragging');
                        if (p.target)
                        {
                            var triggers1 = l.find($.ligerui.controls.DateEditor);
                            var triggers2 = l.find($.ligerui.controls.ComboBox);
                            //更新所有下拉选择框的位置
                            $($.merge(triggers1, triggers2)).each(function ()
                            {
                                if (this.updateSelectBoxPosition)
                                    this.updateSelectBoxPosition();
                            });
                        }
                        g._saveStatus();
                    }
                });
        },
        _onReisze: function ()
        {
            var g = this, p = this.options;
            if (p.target)
            {
                var manager = $(p.target).liger();
                if (!manager) manager = $(p.target).find(":first").liger();
                if (!manager) return;
                var contentHeight = g.dialog.content.height();
                var contentWidth = g.dialog.content.width();
                manager.trigger('resize', [{ width: contentWidth, height: contentHeight}]);
            }
        },
        _applyResize: function ()
        {
            var g = this, p = this.options;
            if ($.fn.ligerResizable)
            {
                g.resizable = g.dialog.ligerResizable({
                    onStopResize: function (current, e)
                    {
                        var top = 0;
                        var left = 0;
                        if (!isNaN(parseInt(g.dialog.css('top'))))
                            top = parseInt(g.dialog.css('top'));
                        if (!isNaN(parseInt(g.dialog.css('left'))))
                            left = parseInt(g.dialog.css('left'));
                        if (current.diffLeft)
                        {
                            g.set({ left: left + current.diffLeft });
                        }
                        if (current.diffTop)
                        {
                            g.set({ top: top + current.diffTop });
                        }
                        if (current.newWidth)
                        {
                            g.set({ width: current.newWidth });
                            g.dialog.body.css({ width: current.newWidth - g._borderX });
                        }
                        if (current.newHeight)
                        { 
                            g.set({ height: current.newHeight });
                        }
                        g._onReisze();
                        g._saveStatus();
                        g.trigger('stopResize');
                        return false;
                    }, animate: false
                });
            }
        },
        _setImage: function ()
        {
            var g = this, p = this.options;
            if (p.type)
            {
                if (p.type == 'success' || p.type == 'donne' || p.type == 'ok')
                {
                    $(".l-dialog-image", g.dialog).addClass("l-dialog-image-donne").show();
                    g.dialog.content.css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'error')
                {
                    $(".l-dialog-image", g.dialog).addClass("l-dialog-image-error").show();
                    g.dialog.content.css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'warn')
                {
                    $(".l-dialog-image", g.dialog).addClass("l-dialog-image-warn").show();
                    g.dialog.content.css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'question')
                {
                    $(".l-dialog-image", g.dialog).addClass("l-dialog-image-question").show();
                    g.dialog.content.css({ paddingLeft: 64, paddingBottom: 40 });
                }
            }
        }
    });
    l.controls.Dialog.prototype.hide = l.controls.Dialog.prototype.hidden;



    $.ligerDialog.open = function (p)
    {
        return $.ligerDialog(p);
    };
    $.ligerDialog.close = function ()
    {
        var dialogs = l.find(l.controls.Dialog.prototype.__getType());
        for (var i in dialogs)
        {
            var d = dialogs[i];
            d.destroy.ligerDefer(d, 5);
        }
        l.win.unmask();
    };
    $.ligerDialog.show = function (p)
    {
        var dialogs = l.find(l.controls.Dialog.prototype.__getType());
        if (dialogs.length)
        {
            for (var i in dialogs)
            {
                dialogs[i].show();
                return;
            }
        }
        return $.ligerDialog(p);
    };
    $.ligerDialog.hide = function ()
    {
        var dialogs = l.find(l.controls.Dialog.prototype.__getType());
        for (var i in dialogs)
        {
            var d = dialogs[i];
            d.hide();
        }
    };
    $.ligerDialog.tip = function (options)
    {
        options = $.extend({
            showType: 'slide',
            width: 240,
            modal: false,
            height: 100
        }, options || {});

        $.extend(options, {
            fixedType: 'se',
            type: 'none',
            isDrag: false,
            isResize: false,
            showMax: false,
            showToggle: false,
            showMin: false
        });
        return $.ligerDialog.open(options);
    };
    $.ligerDialog.alert = function (content, title, type, callback)
    {
        content = content || "";
        if (typeof (title) == "function")
        { 
            callback = title;
            type = null;
        }
        else if (typeof (type) == "function")
        {
            callback = type;
        }
        var btnclick = function (item, Dialog, index)
        {
            Dialog.close();
            if (callback)
                callback(item, Dialog, index);
        };
        p = {
            content: content, 
            buttons: [{ text: $.ligerDefaults.DialogString.ok, onclick: btnclick}]
        };
        if (typeof (title) == "string" && title != "") p.title = title;
        if (typeof (type) == "string" && type != "") p.type = type;
        $.extend(p, {
            showMax: false,
            showToggle: false,
            showMin: false
        });
        return $.ligerDialog(p);
    };

    $.ligerDialog.confirm = function (content, title, callback)
    {
        if (typeof (title) == "function")
        {
            callback = title;
            type = null;
        }
        var btnclick = function (item, Dialog)
        {
            Dialog.close();
            if (callback)
            {
                callback(item.type == 'ok');
            }
        };
        p = {
            type: 'question',
            content: content,
            buttons: [{ text: $.ligerDefaults.DialogString.yes, onclick: btnclick, type: 'ok' }, { text: $.ligerDefaults.DialogString.no, onclick: btnclick, type: 'no'}]
        };
        if (typeof (title) == "string" && title != "") p.title = title;
        $.extend(p, {
            showMax: false,
            showToggle: false,
            showMin: false
        });
        return $.ligerDialog(p);
    };
    $.ligerDialog.warning = function (content, title, callback)
    {
        if (typeof (title) == "function")
        {
            callback = title;
            type = null;
        }
        var btnclick = function (item, Dialog)
        {
            Dialog.close();
            if (callback)
            {
                callback(item.type);
            }
        };
        p = {
            type: 'question',
            content: content,
            buttons: [{ text: $.ligerDefaults.DialogString.yes, onclick: btnclick, type: 'yes' }, { text: $.ligerDefaults.DialogString.no, onclick: btnclick, type: 'no' }, { text: $.ligerDefaults.DialogString.cancel, onclick: btnclick, type: 'cancel'}]
        };
        if (typeof (title) == "string" && title != "") p.title = title;
        $.extend(p, {
            showMax: false,
            showToggle: false,
            showMin: false
        });
        return $.ligerDialog(p);
    };
    $.ligerDialog.waitting = function (title)
    {
        title = title || $.ligerDefaults.Dialog.waittingMessage;
        return $.ligerDialog.open({ cls: 'l-dialog-waittingdialog', type: 'none', content: '<div style="padding:4px">' + title + '</div>', allowClose: false });
    };
    $.ligerDialog.closeWaitting = function ()
    {
        var dialogs = l.find(l.controls.Dialog);
        for (var i in dialogs)
        {
            var d = dialogs[i];
            if (d.dialog.hasClass("l-dialog-waittingdialog"))
                d.close();
        }
    };
    $.ligerDialog.success = function (content, title, onBtnClick)
    {
        return $.ligerDialog.alert(content, title, 'success', onBtnClick);
    };
    $.ligerDialog.error = function (content, title, onBtnClick)
    {
        return $.ligerDialog.alert(content, title, 'error', onBtnClick);
    };
    $.ligerDialog.warn = function (content, title, onBtnClick)
    {
        return $.ligerDialog.alert(content, title, 'warn', onBtnClick);
    };
    $.ligerDialog.question = function (content, title)
    {
        return $.ligerDialog.alert(content, title, 'question');
    };


    $.ligerDialog.prompt = function (title, value, multi, callback)
    {
        var target = $('<input type="text" class="l-dialog-inputtext"/>');
        if (typeof (multi) == "function")
        {
            callback = multi;
        }
        if (typeof (value) == "function")
        {
            callback = value;
        }
        else if (typeof (value) == "boolean")
        {
            multi = value;
        }
        if (typeof (multi) == "boolean" && multi)
        {
            target = $('<textarea class="l-dialog-textarea"></textarea>');
        }
        if (typeof (value) == "string" || typeof (value) == "int")
        {
            target.val(value);
        }
        var btnclick = function (item, Dialog, index)
        {
            Dialog.close();
            if (callback)
            {
                callback(item.type == 'yes', target.val());
            }
        }
        p = {
            title: title,
            target: target,
            width: 320,
            buttons: [{ text: $.ligerDefaults.DialogString.ok, onclick: btnclick, type: 'yes' }, { text: $.ligerDefaults.DialogString.cancel, onclick: btnclick, type: 'cancel'}]
        };
        return $.ligerDialog(p);
    };


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{
    var l = $.ligerui;

    $.fn.ligerDrag = function (options)
    {
        return l.run.call(this, "ligerDrag", arguments,
        {
            idAttrName: 'ligeruidragid', hasElement: false, propertyToElemnt: 'target'
        }
        );
    };

    $.fn.ligerGetDragManager = function ()
    {
        return l.run.call(this, "ligerGetDragManager", arguments,
        {
            idAttrName: 'ligeruidragid', hasElement: false, propertyToElemnt: 'target'
        });
    };

    $.ligerDefaults.Drag = {
        onStartDrag: false,
        onDrag: false,
        onStopDrag: false,
        handler: null,
        //代理 拖动时的主体,可以是'clone'或者是函数,放回jQuery 对象
        proxy: true,
        revert: false,
        animate: true,
        onRevert: null,
        onEndRevert: null,
        //接收区域 jQuery对象或者jQuery选择字符
        receive: null,
        //进入区域
        onDragEnter: null,
        //在区域移动
        onDragOver: null,
        //离开区域
        onDragLeave: null,
        //在区域释放
        onDrop: null,
        disabled: false,
        proxyX: null,     //代理相对鼠标指针的位置,如果不设置则对应target的left
        proxyY: null
    };


    l.controls.Drag = function (options)
    {
        l.controls.Drag.base.constructor.call(this, null, options);
    };

    l.controls.Drag.ligerExtend(l.core.UIComponent, {
        __getType: function ()
        {
            return 'Drag';
        },
        __idPrev: function ()
        {
            return 'Drag';
        },
        _render: function ()
        {
            var g = this, p = this.options;
            this.set(p);
            g.cursor = "move";
            g.handler.css('cursor', g.cursor);
            g.handler.bind('mousedown.drag', function (e)
            {
                if (p.disabled) return;
                if (e.button == 2) return;
                g._start.call(g, e);
            }).bind('mousemove.drag', function ()
            {
                if (p.disabled) return;
                g.handler.css('cursor', g.cursor);
            });
        },
        _rendered: function ()
        {
            this.options.target.ligeruidragid = this.id;
        },
        _start: function (e)
        {
            var g = this, p = this.options;
            if (g.reverting) return;
            if (p.disabled) return;
            g.current = {
                target: g.target,
                left: g.target.offset().left,
                top: g.target.offset().top,
                startX: e.pageX || e.screenX,
                startY: e.pageY || e.clientY
            };
            if (g.trigger('startDrag', [g.current, e]) == false) return false;
            g.cursor = "move";
            g._createProxy(p.proxy, e);
            //代理没有创建成功
            if (p.proxy && !g.proxy) return false;
            (g.proxy || g.handler).css('cursor', g.cursor);
            $(document).bind("selectstart.drag", function () { return false; });
            $(document).bind('mousemove.drag', function ()
            {
                g._drag.apply(g, arguments);
            });
            l.draggable.dragging = true;
            $(document).bind('mouseup.drag', function ()
            {
                l.draggable.dragging = false;
                g._stop.apply(g, arguments);
            });
        },
        _drag: function (e)
        {
            var g = this, p = this.options;
            if (!g.current) return;
            var pageX = e.pageX || e.screenX;
            var pageY = e.pageY || e.screenY;
            g.current.diffX = pageX - g.current.startX;
            g.current.diffY = pageY - g.current.startY;
            (g.proxy || g.handler).css('cursor', g.cursor);
            if (g.receive)
            {
                g.receive.each(function (i, obj)
                {
                    var receive = $(obj);
                    var xy = receive.offset();
                    if (pageX > xy.left && pageX < xy.left + receive.width()
                    && pageY > xy.top && pageY < xy.top + receive.height())
                    {
                        if (!g.receiveEntered[i])
                        {
                            g.receiveEntered[i] = true;
                            g.trigger('dragEnter', [obj, g.proxy || g.target, e]);
                        }
                        else
                        {
                            g.trigger('dragOver', [obj, g.proxy || g.target, e]);
                        }
                    }
                    else if (g.receiveEntered[i])
                    {
                        g.receiveEntered[i] = false;
                        g.trigger('dragLeave', [obj, g.proxy || g.target, e]);
                    }
                });
            }
            if (g.hasBind('drag'))
            {
                if (g.trigger('drag', [g.current, e]) != false)
                {
                    g._applyDrag();
                }
                else
                {
                    if (g.proxy)
                    {
                        g._removeProxy();
                    } else
                    {
                        g._stop();
                    }
                }
            }
            else
            {
                g._applyDrag();
            }
        },
        _stop: function (e)
        {
            var g = this, p = this.options;
            $(document).unbind('mousemove.drag');
            $(document).unbind('mouseup.drag');
            $(document).unbind("selectstart.drag");
            if (g.receive)
            {
                g.receive.each(function (i, obj)
                {
                    if (g.receiveEntered[i])
                    {
                        g.trigger('drop', [obj, g.proxy || g.target, e]);
                    }
                });
            }
            if (g.proxy)
            {
                if (p.revert)
                {
                    if (g.hasBind('revert'))
                    {
                        if (g.trigger('revert', [g.current, e]) != false)
                            g._revert(e);
                        else
                            g._removeProxy();
                    }
                    else
                    {
                        g._revert(e);
                    }
                }
                else
                {
                    g._applyDrag(g.target);
                    g._removeProxy();
                }
            }
            g.cursor = 'move';
            g.trigger('stopDrag', [g.current, e]);
            g.current = null;
            g.handler.css('cursor', g.cursor);
        },
        _revert: function (e)
        {
            var g = this;
            g.reverting = true;
            g.proxy.animate({
                left: g.current.left,
                top: g.current.top
            }, function ()
            {
                g.reverting = false;
                g._removeProxy();
                g.trigger('endRevert', [g.current, e]);
                g.current = null;
            });
        },
        _applyDrag: function (applyResultBody)
        {
            var g = this, p = this.options;
            applyResultBody = applyResultBody || g.proxy || g.target;
            var cur = {}, changed = false;
            var noproxy = applyResultBody == g.target;
            if (g.current.diffX)
            {
                if (noproxy || p.proxyX == null)
                    cur.left = g.current.left + g.current.diffX;
                else
                    cur.left = g.current.startX + p.proxyX + g.current.diffX;
                changed = true;
            }
            if (g.current.diffY)
            {
                if (noproxy || p.proxyY == null)
                    cur.top = g.current.top + g.current.diffY;
                else
                    cur.top = g.current.startY + p.proxyY + g.current.diffY;
                changed = true;
            }
            if (applyResultBody == g.target && g.proxy && p.animate)
            {
                g.reverting = true;
                applyResultBody.animate(cur, function ()
                {
                    g.reverting = false;
                });
            }
            else
            {
                applyResultBody.css(cur);
            }
        },
        _setReceive: function (receive)
        {
            this.receiveEntered = {};
            if (!receive) return;
            if (typeof receive == 'string')
                this.receive = $(receive);
            else
                this.receive = receive;
        },
        _setHandler: function (handler)
        {
            var g = this, p = this.options;
            if (!handler)
                g.handler = $(p.target);
            else
                g.handler = (typeof handler == 'string' ? $(handler, p.target) : handler);
        },
        _setTarget: function (target)
        {
            this.target = $(target);
        },
        _setCursor: function (cursor)
        {
            this.cursor = cursor;
            (this.proxy || this.handler).css('cursor', cursor);
        },
        _createProxy: function (proxy, e)
        {
            if (!proxy) return;
            var g = this, p = this.options;
            if (typeof proxy == 'function')
            {
                g.proxy = proxy.call(this.options.target, g, e);
            }
            else if (proxy == 'clone')
            {
                g.proxy = g.target.clone().css('position', 'absolute');
                g.proxy.appendTo('body');
            }
            else
            {
                g.proxy = $("<div class='l-draggable'></div>");
                g.proxy.width(g.target.width()).height(g.target.height())
                g.proxy.attr("dragid", g.id).appendTo('body');
            }
            g.proxy.css({
                left: p.proxyX == null ? g.current.left : g.current.startX + p.proxyX,
                top: p.proxyY == null ? g.current.top : g.current.startY + p.proxyY
            }).show();
        },
        _removeProxy: function ()
        {
            var g = this;
            if (g.proxy)
            {
                g.proxy.remove();
                g.proxy = null;
            }
        }

    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerEasyTab = function ()
    {
        return $.ligerui.run.call(this, "ligerEasyTab", arguments);
    };
    $.fn.ligerGetEasyTabManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetEasyTabManager", arguments);
    };

    $.ligerDefaults.EasyTab = {};

    $.ligerMethos.EasyTab = {};

    $.ligerui.controls.EasyTab = function (element, options)
    {
        $.ligerui.controls.EasyTab.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.EasyTab.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'EasyTab';
        },
        __idPrev: function ()
        {
            return 'EasyTab';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.EasyTab;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.tabs = $(this.element);
            g.tabs.addClass("l-easytab");
            var selectedIndex = 0;
            if ($("> div[lselected=true]", g.tabs).length > 0)
                selectedIndex = $("> div", g.tabs).index($("> div[lselected=true]", g.tabs));
            g.tabs.ul = $('<ul class="l-easytab-header"></ul>');
            $("> div", g.tabs).each(function (i, box)
            {
                var li = $('<li><span></span></li>');
                if (i == selectedIndex)
                    $("span", li).addClass("l-selected");
                if ($(box).attr("title"))
                    $("span", li).html($(box).attr("title"));
                g.tabs.ul.append(li);
                if (!$(box).hasClass("l-easytab-panelbox")) $(box).addClass("l-easytab-panelbox");
            });
            g.tabs.ul.prependTo(g.tabs);
            //init  
            $(".l-easytab-panelbox:eq(" + selectedIndex + ")", g.tabs).show().siblings(".l-easytab-panelbox").hide();
            //add even 
            $("> ul:first span", g.tabs).click(function ()
            {
                if ($(this).hasClass("l-selected")) return;
                var i = $("> ul:first span", g.tabs).index(this);
                $(this).addClass("l-selected").parent().siblings().find("span.l-selected").removeClass("l-selected");
                $(".l-easytab-panelbox:eq(" + i + ")", g.tabs).show().siblings(".l-easytab-panelbox").hide();
            }).not("l-selected").hover(function ()
            {
                $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
            g.set(p);
        }
    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerFilter = function ()
    {
        return $.ligerui.run.call(this, "ligerFilter", arguments);
    };

    $.fn.ligerGetFilterManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetFilterManager", arguments);
    };

    $.ligerDefaults.Filter = {
        //字段列表
        fields: [],
        //字段类型 - 运算符 的对应关系
        operators: {},
        //自定义输入框(如下拉框、日期)
        editors: {}
    };
    $.ligerDefaults.FilterString = {
        strings: {
            "and": "并且",
            "or": "或者",
            "equal": "相等",
            "notequal": "不相等",
            "startwith": "以..开始",
            "endwith": "以..结束",
            "like": "相似",
            "greater": "大于",
            "greaterorequal": "大于或等于",
            "less": "小于",
            "lessorequal": "小于或等于",
            "in": "包括在...",
            "notin": "不包括...",
            "addgroup": "增加分组",
            "addrule": "增加条件",
            "deletegroup": "删除分组"
        }
    };

    $.ligerDefaults.Filter.operators['string'] =
    $.ligerDefaults.Filter.operators['text'] =
    ["equal", "notequal", "startwith", "endwith", "like", "greater", "greaterorequal", "less", "lessorequal", "in", "notin"];

    $.ligerDefaults.Filter.operators['number'] =
    $.ligerDefaults.Filter.operators['int'] =
    $.ligerDefaults.Filter.operators['float'] =
    $.ligerDefaults.Filter.operators['date'] =
    ["equal", "notequal", "greater", "greaterorequal", "less", "lessorequal", "in", "notin"];

    $.ligerDefaults.Filter.editors['string'] =
    {
        create: function (container, field)
        {
            var input = $("<input type='text'/>");
            container.append(input);
            input.ligerTextBox(field.editor.options || {});
            return input;
        },
        setValue: function (input, value)
        {
            input.val(value);
        },
        getValue: function (input)
        {
            return input.liger('option', 'value');
        },
        destroy: function (input)
        {
            input.liger('destroy');
        }
    };

    $.ligerDefaults.Filter.editors['date'] =
    {
        create: function (container, field)
        {
            var input = $("<input type='text'/>");
            container.append(input);
            input.ligerDateEditor(field.editor.options || {});
            return input;
        },
        setValue: function (input, value)
        {
            input.liger('option', 'value', value);
        },
        getValue: function (input, field)
        {
            return input.liger('option', 'value');
        },
        destroy: function (input)
        {
            input.liger('destroy');
        }
    };

    $.ligerDefaults.Filter.editors['number'] =
    {
        create: function (container, field)
        {
            var input = $("<input type='text'/>");
            container.append(input);
            var options = {
                minValue: field.editor.minValue,
                maxValue: field.editor.maxValue
            };
            input.ligerSpinner($.extend(options, field.editor.options || {}));
            return input;
        },
        setValue: function (input, value)
        {
            input.val(value);
        },
        getValue: function (input, field)
        {
            var isInt = field.editor.type == "int";
            if (isInt)
                return parseInt(input.val(), 10);
            else
                return parseFloat(input.val());
        },
        destroy: function (input)
        {
            input.liger('destroy');
        }
    };

    $.ligerDefaults.Filter.editors['combobox'] =
    {
        create: function (container, field)
        {
            var input = $("<input type='text'/>");
            container.append(input);
            var options = {
                data: field.data,
                slide: false,
                valueField: field.editor.valueField || field.editor.valueColumnName,
                textField: field.editor.textField || field.editor.displayColumnName
            };
            $.extend(options, field.editor.options || {});
            input.ligerComboBox(options);
            return input;
        },
        setValue: function (input, value)
        {
            input.liger('option', 'value', value);
        },
        getValue: function (input)
        {
            return input.liger('option', 'value');
        },
        destroy: function (input)
        {
            input.liger('destroy');
        }
    };

    //过滤器组件
    $.ligerui.controls.Filter = function (element, options)
    {
        $.ligerui.controls.Filter.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.Filter.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Filter'
        },
        __idPrev: function ()
        {
            return 'Filter';
        },
        _init: function ()
        {
            $.ligerui.controls.Filter.base._init.call(this);
        },
        _render: function ()
        {
            var g = this, p = this.options;

            g.set(p);

            //事件：增加分组
            $("#" + g.id + " .addgroup").live('click', function ()
            {
                var jtable = $(this).parent().parent().parent().parent();
                g.addGroup(jtable);
            });
            //事件：删除分组
            $("#" + g.id + " .deletegroup").live('click', function ()
            {
                var jtable = $(this).parent().parent().parent().parent();
                g.deleteGroup(jtable);
            });
            //事件：增加条件
            $("#" + g.id + " .addrule").live('click', function ()
            {
                var jtable = $(this).parent().parent().parent().parent();
                g.addRule(jtable);
            });
            //事件：删除条件
            $("#" + g.id + " .deleterole").live('click', function ()
            {
                var rulerow = $(this).parent().parent();
                g.deleteRule(rulerow);
            });

        },

        //设置字段列表
        _setFields: function (fields)
        {
            var g = this, p = this.options;
            if (g.group) g.group.remove();
            g.group = $(g._bulidGroupTableHtml()).appendTo(g.element);
        },

        //输入框列表
        editors: {},

        //输入框计算器
        editorCounter: 0,

        //增加分组
        //@parm [jgroup] jQuery对象(主分组的table dom元素)
        addGroup: function (jgroup)
        {
            var g = this, p = this.options;
            jgroup = $(jgroup || g.group);
            var lastrow = $(">tbody:first > tr:last", jgroup);
            var groupHtmlArr = [];
            groupHtmlArr.push('<tr class="l-filter-rowgroup"><td class="l-filter-cellgroup" colSpan="4">');
            var altering = !jgroup.hasClass("l-filter-group-alt");
            groupHtmlArr.push(g._bulidGroupTableHtml(altering, true));
            groupHtmlArr.push('</td></tr>');
            var row = $(groupHtmlArr.join(''));
            lastrow.before(row);
            return row.find("table:first");
        },

        //删除分组 
        deleteGroup: function (group)
        {
            var g = this, p = this.options;
            $("td.l-filter-value", group).each(function ()
            {
                var rulerow = $(this).parent();
                $("select.fieldsel", rulerow).unbind();
                g.removeEditor(rulerow);
            });
            $(group).parent().parent().remove();
        },


        //删除编辑器
        removeEditor: function (rulerow)
        {
            var g = this, p = this.options;
            var type = $(rulerow).attr("editortype");
            var id = $(rulerow).attr("editorid");
            var editor = g.editors[id];
            if (editor) p.editors[type].destroy(editor);
            $("td.l-filter-value:first", rulerow).html("");
        },

        //设置规则
        //@parm [group] 分组数据
        //@parm [jgruop] 分组table dom jQuery对象
        setData: function (group, jgroup)
        {
            var g = this, p = this.options;
            jgroup = jgroup || g.group;
            var lastrow = $(">tbody:first > tr:last", jgroup);
            jgroup.find(">tbody:first > tr").not(lastrow).remove();
            $("select:first", lastrow).val(group.op);
            if (group.rules)
            {
                $(group.rules).each(function ()
                {
                    var rulerow = g.addRule(jgroup);
                    rulerow.attr("fieldtype", this.type || "string");
                    $("select.opsel", rulerow).val(this.op);
                    $("select.fieldsel", rulerow).val(this.field).trigger('change');
                    var editorid = rulerow.attr("editorid");
                    if (editorid && g.editors[editorid])
                    {
                        var field = g.getField(this.field);
                        if (field && field.editor)
                        {
                            p.editors[field.editor.type].setValue(g.editors[editorid], this.value, field);
                        }
                    }
                    else
                    {
                        $(":text", rulerow).val(this.value);
                    }
                });
            }
            if (group.groups)
            {
                $(group.groups).each(function ()
                {
                    var subjgroup = g.addGroup(jgroup);
                    g.setData(this, subjgroup);
                });
            }
        },

        //增加一个条件
        //@parm [jgroup] 分组的jQuery对象
        addRule: function (jgroup)
        {
            var g = this, p = this.options;
            jgroup = jgroup || g.group;
            var lastrow = $(">tbody:first > tr:last", jgroup);
            var rulerow = $(g._bulidRuleRowHtml());
            lastrow.before(rulerow);
            if (p.fields.length)
            {
                //如果第一个字段启用了自定义输入框
                g.appendEditor(rulerow, p.fields[0]);
            }

            //事件：字段列表改变时
            $("select.fieldsel", rulerow).bind('change', function ()
            {
                var jopsel = $(this).parent().next().find("select:first");
                var fieldName = $(this).val();
                if (!fieldName) return;
                var field = g.getField(fieldName);
                //字段类型处理
                var fieldType = field.type || "string";
                var oldFieldtype = rulerow.attr("fieldtype");
                if (fieldType != oldFieldtype)
                {
                    jopsel.html(g._bulidOpSelectOptionsHtml(fieldType));
                    rulerow.attr("fieldtype", fieldType);
                }
                //当前的编辑器
                var editorType = null;
                //上一次的编辑器
                var oldEditorType = rulerow.attr("editortype");
                if (g.enabledEditor(field)) editorType = field.editor.type;
                if (oldEditorType)
                {
                    //如果存在旧的输入框 
                    g.removeEditor(rulerow);
                }
                if (editorType)
                {
                    //如果当前选择的字段定义了输入框
                    g.appendEditor(rulerow, field);
                } else
                {
                    rulerow.removeAttr("editortype").removeAttr("editorid");
                    $("td.l-filter-value:first", rulerow).html('<input type="text" class="valtxt" />');
                }
            });
            return rulerow;
        },

        //删除一个条件
        deleteRule: function (rulerow)
        {
            $("select.fieldsel", rulerow).unbind();
            this.removeEditor(rulerow);
            $(rulerow).remove();
        },

        //附加一个输入框
        appendEditor: function (rulerow, field)
        {
            var g = this, p = this.options;
            if (g.enabledEditor(field))
            {
                var cell = $("td.l-filter-value:first", rulerow).html("");
                var editor = p.editors[field.editor.type];
                g.editors[++g.editorCounter] = editor.create(cell, field);
                rulerow.attr("editortype", field.editor.type).attr("editorid", g.editorCounter);
            }
        },

        //获取分组数据
        getData: function (group)
        {
            var g = this, p = this.options;
            group = group || g.group;

            var groupData = {};

            $("> tbody > tr", group).each(function (i, row)
            {
                var rowlast = $(row).hasClass("l-filter-rowlast");
                var rowgroup = $(row).hasClass("l-filter-rowgroup");
                if (rowgroup)
                {
                    var groupTable = $("> td:first > table:first", row);
                    if (groupTable.length)
                    {
                        if (!groupData.groups) groupData.groups = [];
                        groupData.groups.push(g.getData(groupTable));
                    }
                }
                else if (rowlast)
                {
                    groupData.op = $(".groupopsel:first", row).val();
                }
                else
                {
                    var fieldName = $("select.fieldsel:first", row).val();
                    var field = g.getField(fieldName);
                    var op = $(".opsel:first", row).val();
                    var value = g._getRuleValue(row, field);
                    var type = $(row).attr("fieldtype") || "string";
                    if (!groupData.rules) groupData.rules = [];
                    groupData.rules.push({
                        field: fieldName, op: op, value: value, type: type
                    });
                }
            });

            return groupData;
        },

        _getRuleValue: function (rulerow, field)
        {
            var g = this, p = this.options;
            var editorid = $(rulerow).attr("editorid");
            var editortype = $(rulerow).attr("editortype");
            var editor = g.editors[editorid];
            if (editor)
                return p.editors[editortype].getValue(editor, field);
            return $(".valtxt:first", rulerow).val();
        },

        //判断某字段是否启用自定义的输入框  
        enabledEditor: function (field)
        {
            var g = this, p = this.options;
            if (!field.editor || !field.editor.type) return false;
            return (field.editor.type in p.editors);
        },

        //根据fieldName 获取 字段
        getField: function (fieldname)
        {
            var g = this, p = this.options;
            for (var i = 0, l = p.fields.length; i < l; i++)
            {
                var field = p.fields[i];
                if (field.name == fieldname) return field;
            }
            return null;
        },

        //获取一个分组的html
        _bulidGroupTableHtml: function (altering, allowDelete)
        {
            var g = this, p = this.options;
            var tableHtmlArr = [];
            tableHtmlArr.push('<table cellpadding="0" cellspacing="0" border="0" class="l-filter-group');
            if (altering)
                tableHtmlArr.push(' l-filter-group-alt');
            tableHtmlArr.push('"><tbody>');
            tableHtmlArr.push('<tr class="l-filter-rowlast"><td class="l-filter-rowlastcell" align="right" colSpan="4">');
            //and or
            tableHtmlArr.push('<select class="groupopsel">');
            tableHtmlArr.push('<option value="and">' + p.strings['and'] + '</option>');
            tableHtmlArr.push('<option value="or">' + p.strings['or'] + '</option>');
            tableHtmlArr.push('</select>');

            //add group
            tableHtmlArr.push('<input type="button" value="' + p.strings['addgroup'] + '" class="addgroup">');
            //add rule
            tableHtmlArr.push('<input type="button" value="' + p.strings['addrule'] + '" class="addrule">');
            if (allowDelete)
                tableHtmlArr.push('<input type="button" value="' + p.strings['deletegroup'] + '" class="deletegroup">');

            tableHtmlArr.push('</td></tr>');

            tableHtmlArr.push('</tbody></table>');
            return tableHtmlArr.join('');
        },

        //获取字段值规则的html
        _bulidRuleRowHtml: function (fields)
        {
            var g = this, p = this.options;
            fields = fields || p.fields;
            var rowHtmlArr = [];
            var fieldType = fields[0].type || "string";
            rowHtmlArr.push('<tr fieldtype="' + fieldType + '"><td class="l-filter-column">');
            rowHtmlArr.push('<select class="fieldsel">');
            for (var i = 0, l = fields.length; i < l; i++)
            {
                var field = fields[i];
                rowHtmlArr.push('<option value="' + field.name + '"');
                if (i == 0) rowHtmlArr.push(" selected ");
                rowHtmlArr.push('>');
                rowHtmlArr.push(field.display);
                rowHtmlArr.push('</option>');
            }
            rowHtmlArr.push("</select>");
            rowHtmlArr.push('</td>');

            rowHtmlArr.push('<td class="l-filter-op">');
            rowHtmlArr.push('<select class="opsel">');
            rowHtmlArr.push(g._bulidOpSelectOptionsHtml(fieldType));
            rowHtmlArr.push('</select>');
            rowHtmlArr.push('</td>');
            rowHtmlArr.push('<td class="l-filter-value">');
            rowHtmlArr.push('<input type="text" class="valtxt" />');
            rowHtmlArr.push('</td>');
            rowHtmlArr.push('<td>');
            rowHtmlArr.push('<div class="l-icon-cross deleterole"></div>');
            rowHtmlArr.push('</td>');
            rowHtmlArr.push('</tr>');
            return rowHtmlArr.join('');
        },

        //获取一个运算符选择框的html
        _bulidOpSelectOptionsHtml: function (fieldType)
        {
            var g = this, p = this.options;
            var ops = p.operators[fieldType];
            var opHtmlArr = [];
            for (var i = 0, l = ops.length; i < l; i++)
            {
                var op = ops[i];
                opHtmlArr[opHtmlArr.length] = '<option value="' + op + '">';
                opHtmlArr[opHtmlArr.length] = p.strings[op];
                opHtmlArr[opHtmlArr.length] = '</option>';
            }
            return opHtmlArr.join('');
        }


    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerForm = function ()
    {
        return $.ligerui.run.call(this, "ligerForm", arguments);
    };

    $.ligerui.getConditions = function (form)
    {
        if (!form) return null;
        var conditions = [];
        $(":input", form).filter(".condition,.field").each(function ()
        {
            var value = $(this).val() || $(this).attr("value");
            if (!this.name || !value) return;
            conditions.push({
                op: $(this).attr("op") || "like",
                field: this.name,
                value: value,
                type: $(this).attr("vt") || "string"
            });
        });
        return conditions;
    };

    $.ligerDefaults = $.ligerDefaults || {};
    $.ligerDefaults.Form = {
        //控件宽度
        inputWidth: 180,
        //标签宽度
        labelWidth: 90,
        //间隔宽度
        space: 40,
        rightToken: '：',
        //标签对齐方式
        labelAlign: 'left',
        //控件对齐方式
        align: 'left',
        //字段
        fields: [],
        //创建的表单元素是否附加ID
        appendID: true,
        //生成表单元素ID的前缀
        prefixID: "",
        //json解析函数
        toJSON: $.ligerui.toJSON,
        labelCss: null,
        fieldCss: null,
        spaceCss: null,
        onAfterSetFields: null,
        buttons: null,              //按钮组
        readonly:false              //是否只读
    };

    $.ligerDefaults.Form_fields = {
        name: null,             //字段name
        type: null,             //表单类型
        editor: null,           //编辑器扩展类型
        label: null,            //Label
        newline: true,          //换行显示
        op: null,               //操作符 附加到input
        vt: null,               //值类型 附加到input
        attr: null             //属性列表 附加到input
    };

    $.ligerDefaults.Form_editor = {
        textFieldName: null    //文本框name 
    };

    //@description 默认表单编辑器构造器扩展(如果创建的表单效果不满意 建议重载)
    //@param {jinput} 表单元素jQuery对象 比如input、select、textarea 
    $.ligerDefaults.Form.editorBulider = function (jinput)
    {
        //这里this就是form的ligerui对象
        var g = this, p = this.options;
        var options = {}, field = null;
        var fieldIndex = jinput.attr("fieldindex"), ltype = jinput.attr("ltype");
        if (fieldIndex != null)
        {
            field = g.getField(fieldIndex); 
            if (field && g.editors && g.editors[field.type])
            { 
                g.editors[field.type].call(g, jinput, field);
                return;
            }
        }
        field = field || {};
        if (p.readonly) options.readonly = true;
        options = $.extend({
            width: (field.width || p.inputWidth) - 2
        }, field.options, field.editor, options);
        if (ltype == "autocomplete")
            options.autocomplete = true;
        if (jinput.is("select"))
        {
            jinput.ligerComboBox(options);
        }
        else if (jinput.is(":password"))
        {
            jinput.ligerTextBox(options);
        }
        else if (jinput.is(":text"))
        { 
            switch (ltype)
            {
                case "select":
                case "combobox":
                case "autocomplete": 
                    jinput.ligerComboBox(options);
                    break;
                case "spinner":
                    jinput.ligerSpinner(options);
                    break;
                case "date":
                    jinput.ligerDateEditor(options);
                    break;
                case "popup":
                    jinput.ligerPopupEdit(options);
                    break;
                case "currency":
                    options.currency = true;
                case "float":
                case "number":
                    options.number = true;
                    jinput.ligerTextBox(options);
                    break;
                case "int":
                case "digits":
                    options.digits = true;
                default:
                    jinput.ligerTextBox(options);
                    break;
            }
        }
        else if (jinput.is(":hidden"))
        { 
            //只读状态，显示文本框的形式
            if (options.readonly)
            { 
                if (field.textField)
                { 
                    var textInput = $("<input type='text' name='" + field.textField + "' />").insertAfter(jinput);
                    if (p.appendID)
                        textInput.attr("id", field.textField);
                    textInput.ligerTextBox(options);
                }
            }
            else
            {
                if ($.inArray(ltype, ["select", "combobox", "autocomplete", "popup", "radiolist", "checkboxlist", "listbox"]) != -1)
                {
                    if (!jinput.attr("id")) jinput.attr("id", liger.getId('hidden'));
                    options.valueFieldID = jinput.attr("id");
                }
                switch (ltype)
                {
                    case "select":
                    case "combobox":
                    case "autocomplete":
                    case "popup":
                        var textField = field.textField || field.comboboxName || liger.getId();
                        var textInput = $("[name='" + textField + "']", g.element);
                        if (!textInput.length)
                            textInput = $("<input type='text' name='" + textField + "' />").insertAfter(jinput);
                        if (p.appendID)
                            textInput.attr("id", textField);
                        if(ltype == "popup")
                            textInput.ligerPopupEdit(options);
                        else
                            textInput.ligerComboBox(options);
                        break;
                    case "checkboxlist":
                        $("<div></div>").insertAfter(jinput).ligerCheckBoxList(options);
                        break;
                    case "radiolist":
                        $("<div></div>").insertAfter(jinput).ligerRadioList(options);
                        break;
                    case "listbox":
                        $("<div></div>").insertAfter(jinput).ligerListBox(options);
                        break;
                }
            } 
        }
        else if (jinput.is(":radio"))
        {
            jinput.ligerRadio(options);
        }
        else if (jinput.is(":checkbox"))
        {
            jinput.ligerCheckBox(options);
        }
        else if (jinput.is("textarea"))
        {
            jinput.addClass("l-textarea");
        }
    }

    //表单组件
    $.ligerui.controls.Form = function (element, options)
    {
        $.ligerui.controls.Form.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.Form.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Form'
        },
        __idPrev: function ()
        {
            return 'Form';
        },
        _init: function ()
        {
            $.ligerui.controls.Form.base._init.call(this);
        },
        getField: function (index)
        {
            var g = this, p = this.options;
            if (!p.fields) return null;
            return p.fields[index];
        },
        toConditions: function ()
        {
            return $.ligerui.getConditions(this.element);
        },
        //预处理字段 , 排序和分组
        _preSetFields: function (fields)
        {
            var g = this, p = this.options, lastVisitedGroup = null, lastVisitedGroupIcon = null;
            //分组： 先填充没有设置分组的字段，然后按照分组排序
            $(p.fields).each(function (i, field)
            {
                if (field.type == "hidden") return;
                if (field.newline == null) field.newline = true;
                if (lastVisitedGroup && !field.group)
                {
                    field.group = lastVisitedGroup;
                    field.groupicon = lastVisitedGroupIcon;
                }
                if (field.group)
                {
                    //trim
                    field.group = field.group.toString().replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' );
                    lastVisitedGroup = field.group;
                    lastVisitedGroupIcon = field.groupicon;
                }
            }); 
           
        },
        _setFields: function (fields)
        {
            var g = this, p = this.options;
            var jform = $(this.element);
            $(".l-form-container", jform).remove();
            //自动创建表单
            if (fields && fields.length)
            {
                g._preSetFields(fields);
                if (!jform.hasClass("l-form"))
                    jform.addClass("l-form");
                var out = ['<div class="l-form-container">'];
                var appendULStartTag = false, lastVisitedGroup = null;
                var groups = [];
                $(fields).each(function (index, field)
                {
                    if ($.inArray(field.group, groups) == -1)
                        groups.push(field.group);
                }); 
                $(groups).each(function (groupIndex, group)
                {
                    $(fields).each(function (i, field)
                    {
                        if (field.group != group) return;
                        var index = $.inArray(field, fields);
                        var name = field.name || field.id, newline = field.newline;
                        if (!name) return;
                        if (field.type == "hidden")
                        {
                            //modify by tanxu at 2013-9-27
                        	 var options = field.options;
                             if (options && options['value']) {
                                 out.push(g._buliderControl(field, index).replace("/>", ' value＝"' + options['value'] + "\"/>"));
                             } else {
                                 out.push(g._buliderControl(field, index));
                             }
                            //modify end
                            return;
                        }
                        var toAppendGroupRow = field.group && field.group != lastVisitedGroup;
                        if (index == 0 || toAppendGroupRow) newline = true;
                        if (newline)
                        {
                            if (appendULStartTag)
                            {
                                out.push('</ul>');
                                appendULStartTag = false;
                            }
                            if (toAppendGroupRow)
                            {
                                out.push('<div class="l-group');
                                if (field.groupicon)
                                    out.push(' l-group-hasicon');
                                out.push('">');
                                if (field.groupicon)
                                out.push('<img src="' + field.groupicon + '" />');
                                out.push('<span>' + field.group + '</span></div>');
                                lastVisitedGroup = field.group;
                            }
                            out.push('<ul width="100%">');
                            appendULStartTag = true;
                        }
                        out.push('<li class="l-fieldcontainer');
                        if (newline)
                        {
                            out.push(' l-fieldcontainer-first');
                        }
                        out.push('"');
                        out.push(' fieldindex=' + index);
                        out.push('><ul>');
                        //append label
                        out.push(g._buliderLabelContainer(field, index));
                        //append input 
                        out.push(g._buliderControlContainer(field, index));
                        //append space
                        out.push(g._buliderSpaceContainer(field, index));
                        out.push('</ul></li>');

                    });
                }); 
                if (appendULStartTag)
                {
                    out.push('</ul>');
                    appendULStartTag = false;
                }
                out.push('</div>');
                jform.append(out.join(''));

                $(".l-group .togglebtn", jform).remove(); 
                $(".l-group", jform).width(jform.width() * 0.95).append("<div class='togglebtn'></div>");
            }
            //生成ligerui表单样式
            $(".l-form-container", jform).find("input,select,textarea").each(function ()
            {
                p.editorBulider.call(g, $(this));
            });
            g.trigger('afterSetFields');
        },
        _render: function ()
        {
            var g = this, p = this.options;
            var jform = $(this.element);
            //生成ligerui表单样式
            $("input,select,textarea", jform).each(function ()
            {
                p.editorBulider.call(g, $(this));
            });
            g.set(p);
            if (p.buttons)
            {
                var jbuttons = $('<ul class="l-form-buttons"></ul>').appendTo(jform);
                $(p.buttons).each(function ()
                {
                    var jbutton = $('<li><div></div></li>').appendTo(jbuttons);
                    $("div:first", jbutton).ligerButton(this);
                });
            }
        },
        //标签部分
        _buliderLabelContainer: function (field)
        {
            var g = this, p = this.options;
            var label = field.label || field.display;
            var labelWidth = field.labelWidth || field.labelwidth || p.labelWidth;
            var labelAlign = field.labelAlign || p.labelAlign;
            if (label) label += p.rightToken;
            var out = [];
            out.push('<li');
            if (p.labelCss)
            {
                out.push(' class="' + p.labelCss + '"');
            }
            out.push(' style="');
            if (labelWidth)
            {
                out.push('width:' + labelWidth + 'px;');
            }
            if (labelAlign)
            {
                out.push('text-align:' + labelAlign + ';');
            }

            out.push('">');
            if (label)
            {
                out.push(label);
            }
            out.push('</li>');
            return out.join('');
        },
        //控件部分
        _buliderControlContainer: function (field, fieldIndex)
        {
            var g = this, p = this.options;
            var width = field.width || p.inputWidth;
            var align = field.align || field.textAlign || field.textalign || p.align;
            var out = [];
            out.push('<li');
            if (p.fieldCss)
            {
                out.push(' class="' + p.fieldCss + '"');
            }
            out.push(' style="');
            if (width)
            {
                out.push('width:' + width + 'px;');
            }
            if (align)
            {
                out.push('text-align:' + align + ';');
            }
            out.push('">');
            out.push(g._buliderControl(field, fieldIndex));
            out.push('</li>');
            return out.join('');
        },
        //间隔部分
        _buliderSpaceContainer: function (field)
        {
            var g = this, p = this.options;
            var spaceWidth = field.space || field.spaceWidth || p.space;
            var out = [];
            out.push('<li');
            if (p.spaceCss)
            {
                out.push(' class="' + p.spaceCss + '"');
            }
            out.push(' style="');
            if (spaceWidth)
            {
                out.push('width:' + spaceWidth + 'px;');
            }
            out.push('">'); 
            if (field.validate && field.validate.required)
            {
                out.push("<span class='l-star'>*</span>");
            }
            out.push('</li>');
            return out.join('');
        },
        _buliderControl: function (field, fieldIndex)
        {
            var g = this, p = this.options;
            var width = field.width || p.inputWidth,
            name = field.name || field.id,
            type = (field.type || "text").toLowerCase(),
            readonly = (field.readonly || (field.editor && field.editor.readonly)) ? true : false;
            var out = [];   
            if (type == "textarea" || type == "htmleditor")
            { 
                out.push('<textarea '); 
            }
            else if ($.inArray(type, ["checkbox", "radio", "password", "file"]) != -1)
            {
                out.push('<input type="' + type + '" ');
            }
            //modify by songxf at 2013-8-26 修正隐藏域不能传值的问题
            //else if ($.inArray(type, ["select", "combobox", "autocomplete", "popup", "radiolist", "checkboxlist", "listbox"]) != -1)
            else if ($.inArray(type, ["select", "combobox", "autocomplete", "popup", "radiolist", "checkboxlist", "listbox" , "hidden"]) != -1)
            //modify end
            {
                out.push('<input type="hidden" ');
            }
            else
            {
                out.push('<input type="text" ');
            }
            out.push('name="' + name + '" ');
            out.push('fieldindex="' + fieldIndex + '" '); 
            field.cssClass && out.push('class="' + field.cssClass + '" ');
            p.appendID  && out.push(' id="' + name + '" '); 
            out.push(g._getInputAttrHtml(field)); 
            if (field.validate && !readonly)
            {
                out.push(" validate='" + p.toJSON(field.validate) + "' ");
                g.validate = g.validate || {};
                g.validate.rules = g.validate.rules || {};
                g.validate.rules[name] = field.validate;
                if (field.validateMessage)
                {
                    g.validate.messages = g.validate.messages || {};
                    g.validate.messages[name] = field.validateMessage;
                }
            }
            out.push(' />');
            return out.join('');
        },
        _getInputAttrHtml: function (field)
        {
            var out = [], type = (field.type || "text").toLowerCase();
            if (type == "textarea")
            {
                field.cols && out.push('cols="' + field.cols + '" ');
                field.rows && out.push('rows="' + field.rows + '" ');
            }
            out.push('ltype="' + type + '" ');
            field.op && out.push('op="' + field.op + '" ');
            field.vt && out.push('vt="' + field.vt + '" ');
            if (field.attr)
            {
                for (var attrp in field.attr)
                {
                    out.push(attrp + '="' + field.attr[attrp] + '" ');
                }
            }
            return out.join('');
        }
    });
      
    //分组 收缩/展开
    $(".l-form .l-group .togglebtn").live('click', function ()
    {
        if ($(this).hasClass("togglebtn-down")) $(this).removeClass("togglebtn-down");
        else $(this).addClass("togglebtn-down");
        var boxs = $(this).parent().nextAll("ul,div");
        for (var i = 0; i < boxs.length; i++)
        {
            var jbox = $(boxs[i]);
            if (jbox.hasClass("l-group")) break;
            jbox.toggle();
        }
    });
})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{
    var l = $.ligerui;

    $.fn.ligerGrid = function (options)
    {
        return $.ligerui.run.call(this, "ligerGrid", arguments);
    };

    $.fn.ligerGetGridManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetGridManager", arguments);
    };

    $.ligerDefaults.Grid = {
        title: null,
        width: 'auto',                          //宽度值
        height: 'auto',                          //宽度值
        columnWidth: null,                      //默认列宽度
        resizable: true,                        //table是否可伸缩
        url: false,                             //ajax url
        data: null,                            //初始化数据
        usePager: true,                         //是否分页
        page: 1,                                //默认当前页 
        pageSize: 10,                           //每页默认的结果数
        pageSizeOptions: [10, 20, 30, 40, 50],  //可选择设定的每页结果数
        parms: [],                         //提交到服务器的参数
        columns: [],                          //数据源
        minColToggle: 1,                        //最小显示的列
        dataType: 'server',                     //数据源：本地(local)或(server),本地是将读取p.data。不需要配置，取决于设置了data或是url
        dataAction: 'server',                    //提交数据的方式：本地(local)或(server),选择本地方式时将在客服端分页、排序。 
        showTableToggleBtn: false,              //是否显示'显示隐藏Grid'按钮 
        switchPageSizeApplyComboBox: false,     //切换每页记录数是否应用ligerComboBox
        allowAdjustColWidth: true,              //是否允许调整列宽     
        checkbox: false,                         //是否显示复选框
        allowHideColumn: true,                 //是否显示'切换列层'按钮
        enabledEdit: false,                      //是否允许编辑
        isScroll: true,                         //是否滚动 
        dateFormat: 'yyyy-MM-dd',              //默认时间显示格式
        inWindow: true,                        //是否以窗口的高度为准 height设置为百分比时可用
        statusName: '__status',                    //状态名
        method: 'post',                         //提交方式
        async: true,
        fixedCellHeight: true,                       //是否固定单元格的高度
        heightDiff: 0,                         //高度补差,当设置height:100%时，可能会有高度的误差，可以通过这个属性调整
        cssClass: null,                    //类名
        root: 'Rows',                       //数据源字段名
        record: 'Total',                     //数据源记录数字段名
        pageParmName: 'page',               //页索引参数名，(提交给服务器)
        pagesizeParmName: 'pagesize',        //页记录数参数名，(提交给服务器)
        sortnameParmName: 'sortname',        //页排序列名(提交给服务器)
        sortorderParmName: 'sortorder',      //页排序方向(提交给服务器) 
        allowUnSelectRow: false,           //是否允许反选行 
        alternatingRow: true,           //奇偶行效果
        mouseoverRowCssClass: 'l-grid-row-over',
        enabledSort: true,                      //是否允许排序
        rowAttrRender: null,                  //行自定义属性渲染器(包括style，也可以定义)
        groupColumnName: null,                 //分组 - 列名
        groupColumnDisplay: '分组',             //分组 - 列显示名字
        groupRender: null,                     //分组 - 渲染器
        totalRender: null,                       //统计行(全部数据)
        delayLoad: false,                        //初始化时是否不加载
        where: null,                           //数据过滤查询函数,(参数一 data item，参数二 data item index)
        selectRowButtonOnly: false,            //复选框模式时，是否只允许点击复选框才能选择行 
        whenRClickToSelect: false,                //右击行时是否选中
        contentType: null,                     //Ajax contentType参数
        checkboxColWidth: 27,                  //复选框列宽度
        detailColWidth: 29,                     //明细列宽度
        clickToEdit: true,                      //是否点击单元格的时候就编辑
        detailToEdit: false,                     //是否点击明细的时候进入编辑
        onEndEdit: null,
        minColumnWidth: 80,
        tree: null,                            //treeGrid模式
        isChecked: null,                       //复选框 初始化函数
        isSelected: null,                       //选择 初始化函数
        frozen: true,                          //是否固定列
        frozenDetail: false,                    //明细按钮是否在固定列中
        frozenCheckbox: true,                  //复选框按钮是否在固定列中
        detail: null,
        detailHeight: 260,
        rownumbers: false,                         //是否显示行序号
        frozenRownumbers: true,                  //行序号是否在固定列中
        rownumbersColWidth: 26,
        colDraggable: false,                       //是否允许表头拖拽
        rowDraggable: false,                         //是否允许行拖拽
        rowDraggingRender: null,
        autoCheckChildren: true,                  //是否自动选中子节点
        onRowDragDrop: null,                    //行拖拽事件
        rowHeight: 22,                           //行默认的高度
        headerRowHeight: 23,                    //表头行的高度
        toolbar: null,                           //工具条,参数同 ligerToolbar的
        headerImg: null,                        //表格头部图标
        onDragCol: null,                       //拖动列事件
        onToggleCol: null,                     //切换列事件
        onChangeSort: null,                    //改变排序事件
        onSuccess: null,                       //成功获取服务器数据的事件
        onDblClickRow: null,                     //双击行事件
        onSelectRow: null,                    //选择行事件
        onUnSelectRow: null,                   //取消选择行事件
        onBeforeCheckRow: null,                 //选择前事件，可以通过return false阻止操作(复选框)
        onCheckRow: null,                    //选择事件(复选框) 
        onBeforeCheckAllRow: null,              //选择前事件，可以通过return false阻止操作(复选框 全选/全不选)
        onCheckAllRow: null,                    //选择事件(复选框 全选/全不选)onextend
        onBeforeShowData: null,                  //显示数据前事件，可以通过reutrn false阻止操作
        onAfterShowData: null,                 //显示完数据事件
        onError: null,                         //错误事件
        onSubmit: null,                         //提交前事件
        onReload: null,                    //刷新事件，可以通过return false来阻止操作
        onToFirst: null,                     //第一页，可以通过return false来阻止操作
        onToPrev: null,                      //上一页，可以通过return false来阻止操作
        onToNext: null,                      //下一页，可以通过return false来阻止操作
        onToLast: null,                      //最后一页，可以通过return false来阻止操作
        onAfterAddRow: null,                     //增加行后事件
        onBeforeEdit: null,                      //编辑前事件
        onBeforeSubmitEdit: null,               //验证编辑器结果是否通过
        onAfterEdit: null,                       //结束编辑后事件
        onLoading: null,                        //加载时函数
        onLoaded: null,                          //加载完函数
        onContextmenu: null,                   //右击事件
        onBeforeCancelEdit: null,                 //取消编辑前事件
        onAfterSubmitEdit: null,                   //提交后事件
        onRowDragDrop: null,                       //行拖拽后事件
        onGroupExtend: null,                        //分组展开事件
        onGroupCollapse: null,                     //分组收缩事件
        onLoadData : null                       //加载数据前事件
    };
    $.ligerDefaults.GridString = {
        errorMessage: '发生错误',
        pageStatMessage: '显示从{from}到{to}，总 {total} 条 。每页显示：{pagesize}',
        pageTextMessage: 'Page',
        loadingMessage: '加载中...',
        findTextMessage: '查找',
        noRecordMessage: '没有符合条件的记录存在',
        isContinueByDataChanged: '数据已经改变,如果继续将丢失数据,是否继续?',
        cancelMessage: '取消',
        saveMessage: '保存',
        applyMessage: '应用',
        draggingMessage: '{count}行'
    };

    $.ligerDefaults.Grid_columns = {
        id: null,
        name: null,
        totalSummary: null,
        display: null,
        headerRender: null,
        isAllowHide: true,
        isSort: false,
        type: null,
        columns: null,
        width: 120,
        minWidth: 80,
        format: null,
        align: 'left',
        hide: false,
        editor: null,
        render: null,
        textField: null  //真正显示的字段名,如果设置了，在编辑状态时,会调用创建编辑器的setText和getText方法
    };
    $.ligerDefaults.Grid_editor = {
        type: null,
        ext: null,
        onChange: null,
        onChanged: null
    };
    //接口方法扩展
    $.ligerMethos.Grid = $.ligerMethos.Grid || {};

    //排序器扩展
    $.ligerDefaults.Grid.sorters = $.ligerDefaults.Grid.sorters || {};

    //格式化器扩展
    $.ligerDefaults.Grid.formatters = $.ligerDefaults.Grid.formatters || {};

    //编辑器扩展
    $.ligerDefaults.Grid.editors = $.ligerDefaults.Grid.editors || {};


    $.ligerDefaults.Grid.sorters['date'] = function (val1, val2)
    {
        return val1 < val2 ? -1 : val1 > val2 ? 1 : 0;
    };
    $.ligerDefaults.Grid.sorters['int'] = function (val1, val2)
    {
        return parseInt(val1) < parseInt(val2) ? -1 : parseInt(val1) > parseInt(val2) ? 1 : 0;
    };
    $.ligerDefaults.Grid.sorters['float'] = function (val1, val2)
    {
        return parseFloat(val1) < parseFloat(val2) ? -1 : parseFloat(val1) > parseFloat(val2) ? 1 : 0;
    };
    $.ligerDefaults.Grid.sorters['string'] = function (val1, val2)
    {
        if (!val1) return false;
        return val1.localeCompare(val2);
    };


    $.ligerDefaults.Grid.formatters['date'] = function (value, column)
    {
		//modify by  songxf at 2013-6-26
       // function getFormatDate(date, dateformat)
	   function getFormatDate(dateobj, dateformat)
	   //modify end
        {
            var g = this, p = this.options;
			//modify by songxf at 2013-6-26
            if (isNaN(dateobj)) return null;
			var date=null;
			if(dateobj.time){
                  date=new Date(dateobj.time);
            }else{
                  date=new Date(dateobj);
            }
			//modify end
            var format = dateformat;
            var o = {
                "M+": date.getMonth() + 1,
                "d+": date.getDate(),
                "h+": date.getHours(),
                "m+": date.getMinutes(),
                "s+": date.getSeconds(),
                "q+": Math.floor((date.getMonth() + 3) / 3),
                "S": date.getMilliseconds()
            }
            if (/(y+)/.test(format))
            {
                format = format.replace(RegExp.$1, (date.getFullYear() + "")
            .substr(4 - RegExp.$1.length));
            }
            for (var k in o)
            {
                if (new RegExp("(" + k + ")").test(format))
                {
                    format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
                : ("00" + o[k]).substr(("" + o[k]).length));
                }
            }
            return format;
        }
        if (!value) return "";
        // /Date(1328423451489)/
        if (typeof (value) == "string" && /^\/Date/.test(value))
        {
            value = value.replace(/^\//, "new ").replace(/\/$/, "");
            eval("value = " + value);
        }
		//modify by songxf at 2013-6-26
		//if (value instanceof Date)
		if (value.time|| typeof(value) == "number")
		//modify end
        {
            var format = column.format || this.options.dateFormat || "yyyy-MM-dd";
            return getFormatDate(value, format);
        }
        else
        {
            return value.toString();
        }
    }
    //获取 默认的编辑器构造函数
    function getEditorBuilder(e)
    {
        var type = e.type, control = e.control;
        if (!type || !control) return null;
        control = control.substr(0, 1).toUpperCase() + control.substr(1);
        return $.extend({
            create: function (container, editParm)
            {
                var column = editParm.column;
                var input = $("<input type='text'/>").appendTo(container);
                var p = $.extend({}, column.editor.options);
                if (column.editor.valueColumnName) p.valueField = column.editor.valueColumnName;
                if (column.editor.displayColumnName) p.textField = column.editor.displayColumnName;
                var defaults = liger.defaults[control];
                for (var proName in defaults)
                {
                    if (proName in column.editor)
                    {
                        p[proName] = column.editor[proName];
                    }
                }
                //可扩展参数,支持动态加载
                var ext = column.editor.p || column.editor.ext;
                ext = typeof (ext) == 'function' ? ext(editParm) : ext;
                $.extend(p, ext);
                input['liger' + control](p);
                return input;
            },
            getValue: function (input, editParm)
            {
                var obj = liger.get(input);
                if (obj) return obj.getValue(); 
            },
            setValue: function (input, value, editParm)
            {
                var obj = liger.get(input);
                if (obj) obj.setValue(value); 
            },
            resize: function (input, width, height, editParm)
            {
                var obj = liger.get(input);
                if (obj) obj.resize(width, height); 
            },
            destroy: function (input, editParm)
            {
                var obj = liger.get(input);
                if (obj) obj.destroy();
            }
        }, e); 
    }
    //几个默认的编辑器构造函数
    var defaultEditorBuilders = {
        "text": {
            control: 'TextBox'
        },
        "date": {
            control: 'DateEditor',
            setValue: function (input, value, editParm)
            {
                // /Date(1328423451489)/
                if (typeof value == "string" && /^\/Date/.test(value))
                {
                    value = value.replace(/^\//, "new ").replace(/\/$/, "");
                    eval("value = " + value);
                }
                liger.get(input).setValue(value);
            }
        },
        "combobox": {
            control: 'ComboBox',
            getText: function (input, editParm)
            { 
                return liger.get(input).getText();
            },
            setText: function (input, value, editParm)
            { 
                liger.get(input).setText(value);
            }
        },
        "int": {
            control: 'Spinner',
            getValue: function (input, editParm)
            {
                return parseInt(input.val(), 10);
            }
        },
        "spinner": {
            control: 'Spinner',
            getValue: function (input, editParm)
            {
                return parseFloat(input.val());
            }
        },
        "checkbox": {
            control: 'CheckBox',
            getValue: function (input, editParm)
            {
                return input[0].checked ? 1 : 0;
            },
            setValue: function (input, value, editParm)
            {
                input.val(value ? true : false);
            }
        },
        "popup": {
            control: 'PopupEdit',
            getText: function (input, editParm)
            {
                return liger.get(input).getText();
            },
            setText: function (input, value, editParm)
            { 
                liger.get(input).setText(value);
            }
        }
    };
    defaultEditorBuilders["string"] = defaultEditorBuilders["text"];
    defaultEditorBuilders["select"] = defaultEditorBuilders["combobox"];
    defaultEditorBuilders["float"] = defaultEditorBuilders["spinner"];
    defaultEditorBuilders["chk"] = defaultEditorBuilders["checkbox"];

    //页面初始化以后才加载
    $(function ()
    {
        for (var type in defaultEditorBuilders)
        {
            var p = defaultEditorBuilders[type];
            if (!p || !p.control || type in $.ligerDefaults.Grid.editors) continue;
            $.ligerDefaults.Grid.editors[type] = getEditorBuilder($.extend({
                type: type
            }, p));
        }
    });

    $.ligerui.controls.Grid = function (element, options)
    {
        $.ligerui.controls.Grid.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.Grid.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return '$.ligerui.controls.Grid';
        },
        __idPrev: function ()
        {
            return 'grid';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Grid;
        },
        _init: function ()
        {
            $.ligerui.controls.Grid.base._init.call(this);
            var g = this, p = this.options;
            p.dataType = p.url ? "server" : "local";
            if (p.dataType == "local")
            {
                p.data = p.data || [];
                p.dataAction = "local";
            }
            if (p.isScroll == false)
            {
                p.height = 'auto';
            }
            if (!p.frozen)
            {
                p.frozenCheckbox = false;
                p.frozenDetail = false;
                p.frozenRownumbers = false;
            }
            if (p.detailToEdit)
            {
                p.enabledEdit = true;
                p.clickToEdit = false;
                p.detail = {
                    height: 'auto',
                    onShowDetail: function (record, container, callback)
                    {
                        $(container).addClass("l-grid-detailpanel-edit");
                        g.beginEdit(record, function (rowdata, column)
                        {
                            var editContainer = $("<div class='l-editbox'></div>");
                            editContainer.width(120).height(p.rowHeight + 1);
                            editContainer.appendTo(container);
                            return editContainer;
                        });
                        function removeRow()
                        {
                            $(container).parent().parent().remove();
                            g.collapseDetail(record);
                        }
                        $("<div class='l-clear'></div>").appendTo(container);
                        $("<div class='l-button'>" + p.saveMessage + "</div>").appendTo(container).click(function ()
                        {
                            g.endEdit(record);
                            removeRow();
                        });
                        $("<div class='l-button'>" + p.applyMessage + "</div>").appendTo(container).click(function ()
                        {
                            g.submitEdit(record);
                        });
                        $("<div class='l-button'>" + p.cancelMessage + "</div>").appendTo(container).click(function ()
                        {
                            g.cancelEdit(record);
                            removeRow();
                        });
                    }
                };
            }
            if (p.tree)//启用分页模式
            {
                p.tree.childrenName = p.tree.childrenName || "children";
                p.tree.isParent = p.tree.isParent || function (rowData)
                {
                    var exist = p.tree.childrenName in rowData;
                    return exist;
                };
                p.tree.isExtend = p.tree.isExtend || function (rowData)
                {
                    if ('isextend' in rowData && rowData['isextend'] == false)
                        return false;
                    return true;
                };
            }
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.grid = $(g.element);
            g.grid.addClass("l-panel");
            var gridhtmlarr = [];
            gridhtmlarr.push("        <div class='l-panel-header'><span class='l-panel-header-text'></span></div>");
            gridhtmlarr.push("                    <div class='l-grid-loading'></div>");
            gridhtmlarr.push("        <div class='l-panel-topbar'></div>");
            gridhtmlarr.push("        <div class='l-panel-bwarp'>");
            gridhtmlarr.push("            <div class='l-panel-body'>");
            gridhtmlarr.push("                <div class='l-grid'>");
            gridhtmlarr.push("                    <div class='l-grid-dragging-line'></div>");
            gridhtmlarr.push("                    <div class='l-grid-popup'><table cellpadding='0' cellspacing='0'><tbody></tbody></table></div>");

            gridhtmlarr.push("                  <div class='l-grid1'>");
            gridhtmlarr.push("                      <div class='l-grid-header l-grid-header1'>");
            gridhtmlarr.push("                          <div class='l-grid-header-inner'><table class='l-grid-header-table' cellpadding='0' cellspacing='0'><tbody></tbody></table></div>");
            gridhtmlarr.push("                      </div>");
            gridhtmlarr.push("                      <div class='l-grid-body l-grid-body1'>");
            gridhtmlarr.push("                      </div>");
            gridhtmlarr.push("                  </div>");

            gridhtmlarr.push("                  <div class='l-grid2'>");
            gridhtmlarr.push("                      <div class='l-grid-header l-grid-header2'>");
            gridhtmlarr.push("                          <div class='l-grid-header-inner'><table class='l-grid-header-table' cellpadding='0' cellspacing='0'><tbody></tbody></table></div>");
            gridhtmlarr.push("                      </div>");
            gridhtmlarr.push("                      <div class='l-grid-body l-grid-body2 l-scroll'>");
            
            gridhtmlarr.push("                      </div>");
            gridhtmlarr.push("                  </div>");


            gridhtmlarr.push("                 </div>");
            gridhtmlarr.push("              </div>");
            gridhtmlarr.push("         </div>");
            gridhtmlarr.push("         <div class='l-panel-bar'>");
            gridhtmlarr.push("            <div class='l-panel-bbar-inner'>");
            gridhtmlarr.push("                <div class='l-bar-message'><span class='l-bar-text'></span></div>");
            gridhtmlarr.push("            <div class='l-bar-group l-bar-selectpagesize'></div>");
            gridhtmlarr.push("                <div class='l-bar-separator'></div>");
            gridhtmlarr.push("                <div class='l-bar-group'>");
            gridhtmlarr.push("                    <div class='l-bar-button l-bar-btnfirst'><span></span></div>");
            gridhtmlarr.push("                    <div class='l-bar-button l-bar-btnprev'><span></span></div>");
            gridhtmlarr.push("                </div>");
            gridhtmlarr.push("                <div class='l-bar-separator'></div>");
            gridhtmlarr.push("                <div class='l-bar-group'><span class='pcontrol'> <input type='text' size='4' value='1' style='width:20px' maxlength='3' /> / <span></span></span></div>");
            gridhtmlarr.push("                <div class='l-bar-separator'></div>");
            gridhtmlarr.push("                <div class='l-bar-group'>");
            gridhtmlarr.push("                     <div class='l-bar-button l-bar-btnnext'><span></span></div>");
            gridhtmlarr.push("                    <div class='l-bar-button l-bar-btnlast'><span></span></div>");
            gridhtmlarr.push("                </div>");
            gridhtmlarr.push("                <div class='l-bar-separator'></div>");
            gridhtmlarr.push("                <div class='l-bar-group'>");
            gridhtmlarr.push("                     <div class='l-bar-button l-bar-btnload'><span></span></div>");
            gridhtmlarr.push("                </div>");
            gridhtmlarr.push("                <div class='l-bar-separator'></div>");

            gridhtmlarr.push("                <div class='l-clear'></div>");
            gridhtmlarr.push("            </div>");
            gridhtmlarr.push("         </div>");
            g.grid.html(gridhtmlarr.join(''));
            //头部
            g.header = $(".l-panel-header:first", g.grid);
            //主体
            g.body = $(".l-panel-body:first", g.grid);
            //底部工具条         
            g.toolbar = $(".l-panel-bar:first", g.grid);
            //显示/隐藏列      
            g.popup = $(".l-grid-popup:first", g.grid);
            //加载中
            g.gridloading = $(".l-grid-loading:first", g.grid);
            //调整列宽层 
            g.draggingline = $(".l-grid-dragging-line", g.grid);
            //顶部工具栏
            g.topbar = $(".l-panel-topbar:first", g.grid);

            g.gridview = $(".l-grid:first", g.grid);
            g.gridview.attr("id", g.id + "grid");
            g.gridview1 = $(".l-grid1:first", g.gridview);
            g.gridview2 = $(".l-grid2:first", g.gridview);
            //表头     
            g.gridheader = $(".l-grid-header:first", g.gridview2);
            //表主体     
            g.gridbody = $(".l-grid-body:first", g.gridview2);

            //frozen
            g.f = {};
            //表头     
            g.f.gridheader = $(".l-grid-header:first", g.gridview1);
            //表主体     
            g.f.gridbody = $(".l-grid-body:first", g.gridview1);

            g.currentData = null;
            g.changedCells = {};
            g.editors = {};                 //多编辑器同时存在
            g.editor = { editing: false };  //单编辑器,配置clickToEdit
            if (p.height == "auto")
            {
                g.bind("SysGridHeightChanged", function ()
                {
                    if (g.enabledFrozen())
                        g.gridview.height(Math.max(g.gridview1.height(), g.gridview2.height()));
                });
            }

            var pc = $.extend({}, p);

            this._bulid();
            this._setColumns(p.columns);

            delete pc['columns'];
            delete pc['data'];
            delete pc['url'];
            g.set(pc);
            if (!p.delayLoad)
            {
                if (p.url)
                    g.set({ url: p.url });
                else if (p.data)
                    g.set({ data: p.data });
            }
        },
        _setFrozen: function (frozen)
        {
            if (frozen)
                this.grid.addClass("l-frozen");
            else
                this.grid.removeClass("l-frozen");
        },
        _setCssClass: function (value)
        {
            this.grid.addClass(value);
        },
        _setLoadingMessage: function (value)
        {
            this.gridloading.html(value);
        },
        _setToolbar: function (value)
        {
            var g = this, p = this.options;
            if (value && $.fn.ligerToolBar)
            {
                g.topbar.show();
                g.toolbarManager = g.topbar.ligerToolBar(value);
            }
        },
        _setHeight: function (h)
        {
            var g = this, p = this.options;
            g.unbind("SysGridHeightChanged");
            if (h == "auto")
            {
                g.bind("SysGridHeightChanged", function ()
                {
                    if (g.enabledFrozen())
                        g.gridview.height(Math.max(g.gridview1.height(), g.gridview2.height()));
                });
                return;
            }
            if (typeof h == "string" && h.indexOf('%') > 0)
            {
                if (p.inWindow)
                    h = $(window).height() * parseFloat(h) * 0.01;
                else
                    h = g.grid.parent().height() * parseFloat(h) * 0.01;
            }
			//modify by songxf at 2013-6-26
            //if (p.title) h -= 24;
            //if (p.usePager) h -= 32;
            //if (p.totalRender) h -= 25;
            //if (p.toolbar) h -= g.topbar.outerHeight();
			if (p.title) h -= 29;
            if (p.usePager) h -= 29;
            if (p.totalRender) h -= 25;
            if (p.toolbar) h -= g.topbar.outerHeight()+1;
			//modify end
            var gridHeaderHeight = p.headerRowHeight * (g._columnMaxLevel - 1) + p.headerRowHeight - 1;
            h -= gridHeaderHeight;
            if (h > 0)
            {
                g.gridbody.height(h);
                if (h > 18) g.f.gridbody.height(h - 18);
                g.gridview.height(h + gridHeaderHeight);
            }
        },
        _updateFrozenWidth: function ()
        {
            var g = this, p = this.options;
            if (g.enabledFrozen())
            {
                g.gridview1.width(g.f.gridtablewidth);
                var view2width = g.gridview.width() - g.f.gridtablewidth;
                g.gridview2.css({ left: g.f.gridtablewidth });
                if (view2width > 0) g.gridview2.css({ width: view2width });
            }
        },
        _setWidth: function (value)
        {
            var g = this, p = this.options;
            if (g.enabledFrozen()) g._onResize();
        },
        _setUrl: function (value)
        {
            this.options.url = value;
            if (value)
            {
                this.options.dataType = "server";
                this.loadData(true);
            }
            else
            {
                this.options.dataType = "local";
            }
        },
        removeParm : function(name)
        {
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            if (parms instanceof Array)
            {
                removeArrItem(parms, function (p) { return p.name == name; }); 
            } else
            {
                delete parms[name];
            }
            g.set('parms', parms);
        },
        setParm: function (name, value)
        {
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            if (parms instanceof Array)
            {
                removeArrItem(parms, function (p) { return p.name == name; });
                parms.push({ name: name, value: value });
            } else
            {
                parms[name] = value;
            }
            g.set('parms', parms); 
        },
        _setData: function (value)
        {
            this.loadData(this.options.data);
        },
        //刷新数据
        loadData: function (loadDataParm)
        {
            var g = this, p = this.options;
            g.loading = true; 
            g.trigger('loadData');
            var clause = null;
            var loadServer = true;
            if (typeof (loadDataParm) == "function")
            {
                clause = loadDataParm;
                loadServer = false;
            }
            else if (typeof (loadDataParm) == "boolean")
            {
                loadServer = loadDataParm;
            }
            else if (typeof (loadDataParm) == "object" && loadDataParm)
            {
                loadServer = false;
                p.dataType = "local";
                p.data = loadDataParm;
            }
            //参数初始化
            if (!p.newPage) p.newPage = 1;
            if (p.dataAction == "server")
            {
                if (!p.sortOrder) p.sortOrder = "asc";
            }
            var param = [];
            if (p.parms)
            {
                if (p.parms.length)
                {
                    $(p.parms).each(function ()
                    {
                        param.push({ name: this.name, value: this.value });
                    });
                }
                else if (typeof p.parms == "object")
                {
                    for (var name in p.parms)
                    {
                        param.push({ name: name, value: p.parms[name] });
                    }
                }
            }
            if (p.dataAction == "server")
            {
                if (p.usePager)
                {
                    param.push({ name: p.pageParmName, value: p.newPage });
                    param.push({ name: p.pagesizeParmName, value: p.pageSize });
                }
                if (p.sortName)
                {
                    param.push({ name: p.sortnameParmName, value: p.sortName });
                    param.push({ name: p.sortorderParmName, value: p.sortOrder });
                }
            };
            $(".l-bar-btnload span", g.toolbar).addClass("l-disabled");
            if (p.dataType == "local")
            {
            	g.filteredData = g.data = p.data;
                if (clause)
                    g.filteredData[p.root] = g._searchData(g.filteredData[p.root], clause);
                if (p.usePager)
                    g.currentData = g._getCurrentPageData(g.filteredData);
                else
                {
                    g.currentData = g.filteredData;
                }
                g._showData();
            }
            else if (p.dataAction == "local" && !loadServer)
            {
                if (g.data && g.data[p.root])
                {
                    g.filteredData = g.data;
                    if (clause)
                        g.filteredData[p.root] = g._searchData(g.filteredData[p.root], clause);
                    g.currentData = g._getCurrentPageData(g.filteredData);
                    g._showData();
                }
            }
            else
            {
                g.loadServerData(param, clause);
                //g.loadServerData.ligerDefer(g, 10, [param, clause]);
            }
            g.loading = false;
        },
        loadServerData: function (param, clause)
        {
            var g = this, p = this.options;
            var ajaxOptions = {
                type: p.method,
                url: p.url,
                data: param,
                async: p.async,
                dataType: 'json',
                beforeSend: function ()
                {
                    if (g.hasBind('loading'))
                    {
                        g.trigger('loading');
                    }
                    else
                    {
                        g.toggleLoading(true);
                    }
                },
                success: function (data)
                {
                    g.trigger('success', [data, g]);
                    if (!data || !data[p.root] || !data[p.root].length)
                    {
                        g.currentData = g.data = {};
                        g.currentData[p.root] = g.data[p.root] = [];
                        if (data && data[p.record])
                        {
                            g.currentData[p.record] = g.data[p.record] = data[p.record];
                        } else
                        {
                            g.currentData[p.record] = g.data[p.record] = 0;
                        }
                        g._showData();
                        return;
                    }
                    g.data = data;
                    if (p.dataAction == "server")
                    {
                        g.currentData = g.data;
                    }
                    else
                    {
                        g.filteredData = g.data;
                        if (clause) g.filteredData[p.root] = g._searchData(g.filteredData[p.root], clause);
                        if (p.usePager)
                            g.currentData = g._getCurrentPageData(g.filteredData);
                        else
                            g.currentData = g.filteredData;
                    }
                    g._showData.ligerDefer(g, 10, [g.currentData]);
                },
                complete: function ()
                {
                    g.trigger('complete', [g]);
                    if (g.hasBind('loaded'))
                    {
                        g.trigger('loaded', [g]);
                    }
					//modify by songxf at 2013-6-26
                    //else
                    //{
                    //    g.toggleLoading.ligerDefer(g, 10, [false]);
                    //}
					g.toggleLoading.ligerDefer(g, 10, [false]);
					//modify end
                },
                error: function (XMLHttpRequest, textStatus, errorThrown)
                {
                    g.currentData = g.data = {};
                    g.currentData[p.root] = g.data[p.root] = [];
                    g.currentData[p.record] = g.data[p.record] = 0;
                    g.toggleLoading.ligerDefer(g, 10, [false]);
                    $(".l-bar-btnload span", g.toolbar).removeClass("l-disabled");
                    g.trigger('error', [XMLHttpRequest, textStatus, errorThrown]);
                }
            };
            if (p.contentType) ajaxOptions.contentType = p.contentType;
            $.ajax(ajaxOptions);
        },
        toggleLoading: function (show)
        {
            this.gridloading[show ? 'show' : 'hide']();
        },
        _createEditor: function (editor, container, editParm, width, height)
        {
			//modify by songxf at 2013-6-26
            //var editorInput = editor.create.call(this, container, editParm); 
			var g = this;
            var column = editParm.column;
            var columnWidth = g._getColumnWidth(column);
			var editorInput = editor.create.call(this, container, editParm,columnWidth);
			//modify end
            if (editor.setValue) editor.setValue.call(this, editorInput, editParm.value, editParm);
            if (editor.setText && editParm.column.textField) editor.setText.call(this, editorInput, editParm.text, editParm);
            if (editor.resize) editor.resize.call(this, editorInput, width, height, editParm);
            return editorInput;
        },
        /*
        @description 使一行进入编辑状态
        @param  {rowParm} rowindex或者rowdata
        @param {containerBulider} 编辑器填充层构造器
        */
        beginEdit: function (rowParm, containerBulider)
        {
            var g = this, p = this.options;
            if (!p.enabledEdit || p.clickToEdit) return;
            var rowdata = g.getRow(rowParm);
            if (rowdata._editing) return;
            if (g.trigger('beginEdit', { record: rowdata, rowindex: rowdata['__index'] }) == false) return;
            g.editors[rowdata['__id']] = {};
            rowdata._editing = true;
            g.reRender({ rowdata: rowdata });
            containerBulider = containerBulider || function (rowdata, column)
            {
                var cellobj = g.getCellObj(rowdata, column);
                var container = $(cellobj).html("");
                g.setCellEditing(rowdata, column, true);
                return container;
            };
            for (var i = 0, l = g.columns.length; i < l; i++)
            {
                var column = g.columns[i];
                if (!column.name || !column.editor || !column.editor.type || !p.editors[column.editor.type]) continue;
                var editor = p.editors[column.editor.type];
                var editParm = {
                    record: rowdata,
                    value: g._getValueByName(rowdata, column.name),
                    column: column,
                    rowindex: rowdata['__index'],
                    grid: g
                };
                var container = containerBulider(rowdata, column);
                var width = container.width(), height = container.height();
                var editorInput = g._createEditor(editor, container, editParm, width, height);
                g.editors[rowdata['__id']][column['__id']] = { editor: editor, input: editorInput, editParm: editParm, container: container };
            }
            g.trigger('afterBeginEdit', { record: rowdata, rowindex: rowdata['__index'] });

        },
        cancelEdit: function (rowParm)
        {
            var g = this;
            if (rowParm == undefined)
            {
                for (var rowid in g.editors)
                {
                    g.cancelEdit(rowid);
                }
            }
            else
            {
                var rowdata = g.getRow(rowParm);
                if (!g.editors[rowdata['__id']]) return;
                if (g.trigger('beforeCancelEdit', { record: rowdata, rowindex: rowdata['__index'] }) == false) return;
                for (var columnid in g.editors[rowdata['__id']])
                {
                    var o = g.editors[rowdata['__id']][columnid];
                    if (o.editor.destroy) o.editor.destroy(o.input, o.editParm);
                }
                delete g.editors[rowdata['__id']];
                delete rowdata['_editing'];
                g.reRender({ rowdata: rowdata });
            }
        },
        addEditRow: function (rowdata)
        {
            this.submitEdit();
            rowdata = this.add(rowdata);
            this.beginEdit(rowdata);
        },
        submitEdit: function (rowParm)
        {
            var g = this, p = this.options;
            if (rowParm == undefined)
            {
                for (var rowid in g.editors)
                {
                    g.submitEdit(rowid);
                }
            }
            else
            {
                var rowdata = g.getRow(rowParm);
                var newdata = {};
                if (!g.editors[rowdata['__id']]) return; 
                for (var columnid in g.editors[rowdata['__id']])
                {
                    var o = g.editors[rowdata['__id']][columnid];
                    var column = o.editParm.column;
                    if (column.name)
                    {
                        newdata[column.name] = o.editor.getValue(o.input, o.editParm);
                    }
                    if (column.textField && o.editor.getText)
                    {
                        newdata[column.textField] = o.editor.getText(o.input, o.editParm);
                    }
                }
                if (g.trigger('beforeSubmitEdit', { record: rowdata, rowindex: rowdata['__index'], newdata: newdata }) == false)
                    return false;
                g.updateRow(rowdata, newdata);
                g.trigger('afterSubmitEdit', { record: rowdata, rowindex: rowdata['__index'], newdata: newdata });
            }
        },
        endEdit: function (rowParm)
        {
            var g = this, p = this.options; 
            if (g.editor.editing)
            {
                var o = g.editor;
                g.trigger('sysEndEdit', [g.editor.editParm]);
                g.trigger('endEdit', [g.editor.editParm]);
                if (o.editor.destroy) o.editor.destroy(o.input, o.editParm);
                g.editor.container.remove();
                g.reRender({ rowdata: g.editor.editParm.record, column: g.editor.editParm.column });
                g.trigger('afterEdit', [g.editor.editParm]);
                g.editor = { editing: false };
            }
            else if (rowParm != undefined)
            {
                var rowdata = g.getRow(rowParm);
                if (!g.editors[rowdata['__id']]) return;
                if (g.submitEdit(rowParm) == false) return false;
                for (var columnid in g.editors[rowdata['__id']])
                {
                    var o = g.editors[rowdata['__id']][columnid];
                    if (o.editor.destroy) o.editor.destroy(o.input, o.editParm);
                }
                delete g.editors[rowdata['__id']];
                delete rowdata['_editing'];
                g.trigger('afterEdit', { record: rowdata, rowindex: rowdata['__index'] });
            }
            else
            {
                for (var rowid in g.editors)
                {
                    g.endEdit(rowid);
                }
            }
        },
        setWidth: function (w)
        {
            return this._setWidth(w);
        },
        setHeight: function (h)
        {
            return this._setHeight(h);
        },
        //是否启用复选框列
        enabledCheckbox: function ()
        {
            return this.options.checkbox ? true : false;
        },
        //是否固定列
        enabledFrozen: function ()
        {
            var g = this, p = this.options;
            if (!p.frozen) return false;
            var cols = g.columns || [];
            if (g.enabledDetail() && p.frozenDetail || g.enabledCheckbox() && p.frozenCheckbox
            || p.frozenRownumbers && p.rownumbers) return true;
            for (var i = 0, l = cols.length; i < l; i++)
            {
                if (cols[i].frozen)
                {
                    return true;
                }
            }
            this._setFrozen(false);
            return false;
        },
        //是否启用明细编辑
        enabledDetailEdit: function ()
        {
            if (!this.enabledDetail()) return false;
            return this.options.detailToEdit ? true : false;
        },
        //是否启用明细列
        enabledDetail: function ()
        {
            if (this.options.detail && this.options.detail.onShowDetail) return true;
            return false;
        },
        //是否启用分组
        enabledGroup: function ()
        {
            return this.options.groupColumnName ? true : false;
        },
        deleteSelectedRow: function ()
        {
            if (!this.selected) return;
            for (var i in this.selected)
            {
                var o = this.selected[i];
                if (o['__id'] in this.records)
                    this._deleteData.ligerDefer(this, 10, [o]);
            }
            this.reRender.ligerDefer(this, 20);
        },
        removeRange: function (rowArr)
        {
            var g = this, p = this.options;
            $.each(rowArr, function ()
            {
                g._removeData(this);
            });
            g.reRender();
        },
        remove: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            g._removeData(rowParm);
            g.reRender();
        },
        deleteRange: function (rowArr)
        {
            var g = this, p = this.options;
            $.each(rowArr, function ()
            {
                g._deleteData(this);
            });
            g.reRender();
        },
        deleteRow: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata) return;
            g._deleteData(rowdata);
            g.reRender();
            g.isDataChanged = true;
        },
        _deleteData: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            rowdata[p.statusName] = 'delete';
            if (p.tree)
            {
                var children = g.getChildren(rowdata, true);
                if (children)
                {
                    for (var i = 0, l = children.length; i < l; i++)
                    {
                        children[i][p.statusName] = 'delete';
                    }
                }
            }
            g.deletedRows = g.deletedRows || [];
            g.deletedRows.push(rowdata);
            g._removeSelected(rowdata);
        },
        /*
        @param  {arg} column index、column name、column、单元格
        @param  {value} 值
        @param  {rowParm} rowindex或者rowdata
        */
        updateCell: function (arg, value, rowParm)
        {
            var g = this, p = this.options;
            var column, cellObj, rowdata;
            if (typeof (arg) == "string") //column name
            {
                for (var i = 0, l = g.columns.length; i < l; i++)
                {
                    if (g.columns[i].name == arg)
                    {
                        g.updateCell(i, value, rowParm);
                    }
                }
                return;
            }
            if (typeof (arg) == "number")
            {
                column = g.columns[arg];
                rowdata = g.getRow(rowParm);
                cellObj = g.getCellObj(rowdata, column);
            }
            else if (typeof (arg) == "object" && arg['__id'])
            {
                column = arg;
                rowdata = g.getRow(rowParm);
                cellObj = g.getCellObj(rowdata, column);
            }
            else
            {
                cellObj = arg;
                var ids = cellObj.id.split('|');
                var columnid = ids[ids.length - 1];
                column = g._columns[columnid];
                var row = $(cellObj).parent();
                rowdata = rowdata || g.getRow(row[0]);
            }
            if (value != null && column.name)
            {
                g._setValueByName(rowdata, column.name, value);
                if (rowdata[p.statusName] != 'add')
                    rowdata[p.statusName] = 'update';
                g.isDataChanged = true;
            }
            g.reRender({ rowdata: rowdata, column: column });
        },
        addRows: function (rowdataArr, neardata, isBefore, parentRowData)
        {
            var g = this, p = this.options;
            $(rowdataArr).each(function ()
            {
                g.addRow(this, neardata, isBefore, parentRowData);
            });
        },
        _createRowid: function ()
        {
            return "r" + (1000 + this.recordNumber);
        },
        _isRowId: function (str)
        {
            return (str in this.records);
        },
        _addNewRecord: function (o, previd, pid)
        {
            var g = this, p = this.options;
            g.recordNumber++;
            o['__id'] = g._createRowid();
            o['__previd'] = previd;
            if (previd && previd != -1)
            {
                var prev = g.records[previd];
                if (prev['__nextid'] && prev['__nextid'] != -1)
                {
                    var prevOldNext = g.records[prev['__nextid']];
                    if (prevOldNext)
                        prevOldNext['__previd'] = o['__id'];
                }
                prev['__nextid'] = o['__id'];
                o['__index'] = prev['__index'] + 1;
            }
            else
            {
                o['__index'] = 0;
            }
            if (p.tree)
            {
                if (pid && pid != -1)
                {
                    var parent = g.records[pid];
                    o['__pid'] = pid;
                    o['__level'] = parent['__level'] + 1;
                }
                else
                {
                    o['__pid'] = -1;
                    o['__level'] = 1;
                }
                o['__hasChildren'] = o[p.tree.childrenName] ? true : false;
            }
            if (o[p.statusName] != "add")
                o[p.statusName] = "nochanged";
            g.rows[o['__index']] = o;
            g.records[o['__id']] = o;
            return o;
        },
        //将原始的数据转换成适合 grid的行数据 
        _getRows: function (data)
        {
            var g = this, p = this.options;
            var targetData = [];
            function load(data)
            {
                if (!data || !data.length) return;
                for (var i = 0, l = data.length; i < l; i++)
                {
                    var o = data[i];
                    targetData.push(o);
                    if (o[p.tree.childrenName])
                    {
                        load(o[p.tree.childrenName]);
                    }
                }
            }
            load(data);
            return targetData;
        },
        _updateGridData: function ()
        {
            var g = this, p = this.options;
            g.recordNumber = 0;
            g.rows = [];
            g.records = {};
            var previd = -1;
            function load(data, pid)
            {
                if (!data || !data.length) return;
                for (var i = 0, l = data.length; i < l; i++)
                {
                    var o = data[i];
                    g.formatRecord(o);
                    if (o[p.statusName] == "delete") continue;
                    g._addNewRecord(o, previd, pid);
                    previd = o['__id'];
                    if (o['__hasChildren'])
                    {
                        load(o[p.tree.childrenName], o['__id']);
                    }
                }
            }
            load(g.currentData[p.root], -1);
            return g.rows;
        },
        _moveData: function (from, to, isAfter)
        {
            var g = this, p = this.options;
            var fromRow = g.getRow(from);
            var toRow = g.getRow(to);
            var fromIndex, toIndex;
            var listdata = g._getParentChildren(fromRow);
            fromIndex = $.inArray(fromRow, listdata);
            listdata.splice(fromIndex, 1);
            listdata = g._getParentChildren(toRow);
            toIndex = $.inArray(toRow, listdata);
            listdata.splice(toIndex + (isAfter ? 1 : 0), 0, fromRow);
        },
        move: function (from, to, isAfter)
        {
            this._moveData(from, to, isAfter);
            this.reRender();
        },
        moveRange: function (rows, to, isAfter)
        {
            for (var i in rows)
            {
                this._moveData(rows[i], to, isAfter);
            }
            this.reRender();
        },
        up: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var listdata = g._getParentChildren(rowdata);
            var index = $.inArray(rowdata, listdata);
            if (index == -1 || index == 0) return;
            var selected = g.getSelected();
            g.move(rowdata, listdata[index - 1], false);
            g.select(selected);
        },
        down: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var listdata = g._getParentChildren(rowdata);
            var index = $.inArray(rowdata, listdata);
            if (index == -1 || index == listdata.length - 1) return;
            var selected = g.getSelected();
            g.move(rowdata, listdata[index + 1], true);
            g.select(selected);
        },
        addRow: function (rowdata, neardata, isBefore, parentRowData)
        {
            var g = this, p = this.options;
            rowdata = rowdata || {};
            g._addData(rowdata, parentRowData, neardata, isBefore);
            g.reRender();
            //标识状态
            rowdata[p.statusName] = 'add';
            if (p.tree)
            {
                var children = g.getChildren(rowdata, true);
                if (children)
                {
                    for (var i = 0, l = children.length; i < l; i++)
                    {
                        children[i][p.statusName] = 'add';
                    }
                }
            }
            g.isDataChanged = true;
            p.total = p.total ? (p.total + 1) : 1;
            p.pageCount = Math.ceil(p.total / p.pageSize);
            g._buildPager();
            g.trigger('SysGridHeightChanged');
            g.trigger('afterAddRow', [rowdata]);
            return rowdata;
        },
        updateRow: function (rowDom, newRowData)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowDom);
            //标识状态
            g.isDataChanged = true;
            $.extend(rowdata, newRowData || {});
            if (rowdata[p.statusName] != 'add')
                rowdata[p.statusName] = 'update';
            g.reRender.ligerDefer(g, 10, [{ rowdata: rowdata }]);
            return rowdata;
        },
        setCellEditing: function (rowdata, column, editing)
        {
            var g = this, p = this.options;
            var cell = g.getCellObj(rowdata, column);
            var methodName = editing ? 'addClass' : 'removeClass';
            $(cell)[methodName]("l-grid-row-cell-editing");
            if (rowdata['__id'] != 0)
            {
                var prevrowobj = $(g.getRowObj(rowdata['__id'])).prev();
                if (!prevrowobj.length) return;
                var prevrow = g.getRow(prevrowobj[0]);
                var cellprev = g.getCellObj(prevrow, column);
                if (!cellprev) return;
                $(cellprev)[methodName]("l-grid-row-cell-editing-topcell");
            }
            if (column['__previd'] != -1 && column['__previd'] != null)
            {
                var cellprev = $(g.getCellObj(rowdata, column)).prev();
                $(cellprev)[methodName]("l-grid-row-cell-editing-leftcell");
            }
        },
        reRender: function (e)
        {
            var g = this, p = this.options;
            e = e || {};
            var rowdata = e.rowdata, column = e.column;
            if (column && (column.isdetail || column.ischeckbox)) return;
            if (rowdata && rowdata[p.statusName] == "delete") return;
            if (rowdata && column)
            {
                var cell = g.getCellObj(rowdata, column);
                $(cell).html(g._getCellHtml(rowdata, column));
                if (!column.issystem)
                    g.setCellEditing(rowdata, column, false);
            }
            else if (rowdata)
            {
                $(g.columns).each(function () { g.reRender({ rowdata: rowdata, column: this }); });
            }
            else if (column)
            {
                for (var rowid in g.records) { g.reRender({ rowdata: g.records[rowid], column: column }); }
                for (var i = 0; i < g.totalNumber; i++)
                {
                    var tobj = document.getElementById(g.id + "|total" + i + "|" + column['__id']);
                    $("div:first", tobj).html(g._getTotalCellContent(column, g.groups && g.groups[i] ? g.groups[i] : g.currentData[p.root]));
                }
            }
            else
            {
                g._showData();
            }
        },
        getData: function (status, removeStatus)
        {
            var g = this, p = this.options;
            var data = [];
            if (removeStatus == undefined) removeStatus = true;
            for (var rowid in g.records)
            {
                var o = $.extend(true, {}, g.records[rowid]);
                if (o[p.statusName] == status || status == undefined)
                {
                    data.push(g.formatRecord(o, removeStatus));
                }
            }
            return data;
        },
        //格式化数据
        formatRecord: function (o, removeStatus)
        {
            delete o['__id'];
            delete o['__previd'];
            delete o['__nextid'];
            delete o['__index'];
            if (this.options.tree)
            {
                delete o['__pid'];
                delete o['__level'];
                delete o['__hasChildren'];
            }
            if (removeStatus) delete o[this.options.statusName];
            return o;
        },
        getUpdated: function ()
        {
            return this.getData('update', true);
        },
        getDeleted: function ()
        {
            return this.deletedRows;
        },
        getAdded: function ()
        {
            return this.getData('add', true);
        },
        getChanges: function ()
        {
            var g = this, p = this.options;
            var data = [];
            if (this.deletedRows)
            {
                $(this.deletedRows).each(function ()
                {
                    var o = $.extend(true, {}, this);
                    data.push(g.formatRecord(o, true));
                });
            }
            $.merge(data, g.getUpdated());
            $.merge(data, g.getAdded());
            return data;
        },
        getColumn: function (columnParm)
        {
            var g = this, p = this.options;
            if (typeof columnParm == "string") // column id
            {
                if (g._isColumnId(columnParm))
                    return g._columns[columnParm];
                else
                    return g.columns[parseInt(columnParm)];
            }
            else if (typeof (columnParm) == "number") //column index
            {
                return g.columns[columnParm];
            }
            else if (typeof columnParm == "object" && columnParm.nodeType == 1) //column header cell
            {
                var ids = columnParm.id.split('|');
                var columnid = ids[ids.length - 1];
                return g._columns[columnid];
            }
            return columnParm;
        },
        getColumnType: function (columnname)
        {
            var g = this, p = this.options;
            for (i = 0; i < g.columns.length; i++)
            {
                if (g.columns[i].name == columnname)
                {
                    if (g.columns[i].type) return g.columns[i].type;
                    return "string";
                }
            }
            return null;
        },
        //是否包含汇总
        isTotalSummary: function ()
        {
            var g = this, p = this.options;
            for (var i = 0; i < g.columns.length; i++)
            {
                if (g.columns[i].totalSummary) return true;
            }
            return false;
        },
        //根据层次获取列集合
        //如果columnLevel为空，获取叶节点集合
        getColumns: function (columnLevel)
        {
            var g = this, p = this.options;
            var columns = [];
            for (var id in g._columns)
            {
                var col = g._columns[id];
                if (columnLevel != undefined)
                {
                    if (col['__level'] == columnLevel) columns.push(col);
                }
                else
                {
                    if (col['__leaf']) columns.push(col);
                }
            }
            return columns;
        },
        //改变排序
        changeSort: function (columnName, sortOrder)
        {
            var g = this, p = this.options;
            if (g.loading) return true;
            if (p.dataAction == "local")
            {
                var columnType = g.getColumnType(columnName);
                if (!g.sortedData)
                    g.sortedData = g.filteredData;
                if (!g.sortedData || !g.sortedData[p.root])
                    return;
                if (p.sortName == columnName)
                {
                    g.sortedData[p.root].reverse();
                } else
                {
                    g.sortedData[p.root].sort(function (data1, data2)
                    {
                        return g._compareData(data1, data2, columnName, columnType);
                    });
                }
                if (p.usePager)
                    g.currentData = g._getCurrentPageData(g.sortedData);
                else
                    g.currentData = g.sortedData;
                g._showData();
            }
            p.sortName = columnName;
            p.sortOrder = sortOrder;
            if (p.dataAction == "server")
            {
                g.loadData(p.where);
            }
        },
        //改变分页
        changePage: function (ctype)
        {
            var g = this, p = this.options;
            if (g.loading) return true;
            if (p.dataAction != "local" && g.isDataChanged && !confirm(p.isContinueByDataChanged))
                return false;
            p.pageCount = parseInt($(".pcontrol span", g.toolbar).html());
            switch (ctype)
            {
                case 'first': if (p.page == 1) return; p.newPage = 1; break;
                case 'prev': if (p.page == 1) return; if (p.page > 1) p.newPage = parseInt(p.page) - 1; break;
                case 'next': if (p.page >= p.pageCount) return; p.newPage = parseInt(p.page) + 1; break;
                case 'last': if (p.page >= p.pageCount) return; p.newPage = p.pageCount; break;
                case 'input':
                    var nv = parseInt($('.pcontrol input', g.toolbar).val());
                    if (isNaN(nv)) nv = 1;
                    if (nv < 1) nv = 1;
                    else if (nv > p.pageCount) nv = p.pageCount;
                    $('.pcontrol input', g.toolbar).val(nv);
                    p.newPage = nv;
                    break;
            }
            if (p.newPage == p.page) return false;
            if (p.newPage == 1)
            {
                $(".l-bar-btnfirst span", g.toolbar).addClass("l-disabled");
                $(".l-bar-btnprev span", g.toolbar).addClass("l-disabled");
            }
            else
            {
                $(".l-bar-btnfirst span", g.toolbar).removeClass("l-disabled");
                $(".l-bar-btnprev span", g.toolbar).removeClass("l-disabled");
            }
            if (p.newPage == p.pageCount)
            {
                $(".l-bar-btnlast span", g.toolbar).addClass("l-disabled");
                $(".l-bar-btnnext span", g.toolbar).addClass("l-disabled");
            }
            else
            {
                $(".l-bar-btnlast span", g.toolbar).removeClass("l-disabled");
                $(".l-bar-btnnext span", g.toolbar).removeClass("l-disabled");
            }
            g.trigger('changePage', [p.newPage]);
            if (p.dataAction == "server")
            {
                g.loadData(p.where);
            }
            else
            {
                g.currentData = g._getCurrentPageData(g.filteredData);
                g._showData();
            }
        },
        getSelectedRow: function ()
        {
            for (var i in this.selected)
            {
                var o = this.selected[i];
                if (o['__id'] in this.records)
                    return o;
            }
            return null;
        },
        getSelectedRows: function ()
        {
            var arr = [];
            for (var i in this.selected)
            {
                var o = this.selected[i];
                if (o['__id'] in this.records)
                    arr.push(o);
            }
            return arr;
        },
        getSelectedRowObj: function ()
        {
            for (var i in this.selected)
            {
                var o = this.selected[i];
                if (o['__id'] in this.records)
                    return this.getRowObj(o);
            }
            return null;
        },
        getSelectedRowObjs: function ()
        {
            var arr = [];
            for (var i in this.selected)
            {
                var o = this.selected[i];
                if (o['__id'] in this.records)
                    arr.push(this.getRowObj(o));
            }
            return arr;
        },
        getCellObj: function (rowParm, column)
        {
            var rowdata = this.getRow(rowParm);
            column = this.getColumn(column);
            return document.getElementById(this._getCellDomId(rowdata, column));
        },
        getRowObj: function (rowParm, frozen)
        {
            var g = this, p = this.options;
            if (rowParm == null) return null;
            if (typeof (rowParm) == "string")
            {
                if (g._isRowId(rowParm))
                    return document.getElementById(g.id + (frozen ? "|1|" : "|2|") + rowParm);
                else
                    return document.getElementById(g.id + (frozen ? "|1|" : "|2|") + g.rows[parseInt(rowParm)]['__id']);
            }
            else if (typeof (rowParm) == "number")
            {
                return document.getElementById(g.id + (frozen ? "|1|" : "|2|") + g.rows[rowParm]['__id']);
            }
            else if (typeof (rowParm) == "object" && rowParm['__id']) //rowdata
            {
                return g.getRowObj(rowParm['__id'], frozen);
            }
            return rowParm;
        },
        getRow: function (rowParm)
        {
            var g = this, p = this.options;
            if (rowParm == null) return null;
            if (typeof (rowParm) == "string")
            {
                if (g._isRowId(rowParm))
                    return g.records[rowParm];
                else
                    return g.rows[parseInt(rowParm)];
            }
            else if (typeof (rowParm) == "number")
            {
                return g.rows[parseInt(rowParm)];
            }
            else if (typeof (rowParm) == "object" && rowParm.nodeType == 1 && !rowParm['__id']) //dom对象
            {
                return g._getRowByDomId(rowParm.id);
            }
            return rowParm;
        },
        _setColumnVisible: function (column, hide)
        {
            var g = this, p = this.options;
            if (!hide)  //显示
            {
                column._hide = false;
                document.getElementById(column['__domid']).style.display = "";
                //判断分组列是否隐藏,如果隐藏了则显示出来
                if (column['__pid'] != -1)
                {
                    var pcol = g._columns[column['__pid']];
                    if (pcol._hide)
                    {
                        document.getElementById(pcol['__domid']).style.display = "";
                        this._setColumnVisible(pcol, hide);
                    }
                }
            }
            else //隐藏
            {
                column._hide = true;
                document.getElementById(column['__domid']).style.display = "none";
                //判断同分组的列是否都隐藏,如果是则隐藏分组列
                if (column['__pid'] != -1)
                {
                    var hideall = true;
                    var pcol = this._columns[column['__pid']];
                    for (var i = 0; pcol && i < pcol.columns.length; i++)
                    {
                        if (!pcol.columns[i]._hide)
                        {
                            hideall = false;
                            break;
                        }
                    }
                    if (hideall)
                    {
                        pcol._hide = true;
                        document.getElementById(pcol['__domid']).style.display = "none";
                        this._setColumnVisible(pcol, hide);
                    }
                }
            }
        },
        //显示隐藏列
        toggleCol: function (columnparm, visible, toggleByPopup)
        {
            var g = this, p = this.options;
            var column;
            if (typeof (columnparm) == "number")
            {
                column = g.columns[columnparm];
            }
            else if (typeof (columnparm) == "object" && columnparm['__id'])
            {
                column = columnparm;
            }
            else if (typeof (columnparm) == "string")
            {
                if (g._isColumnId(columnparm)) // column id
                {
                    column = g._columns[columnparm];
                }
                else  // column name
                {
                    $(g.columns).each(function ()
                    {
                        if (this.name == columnparm)
                            g.toggleCol(this, visible, toggleByPopup);
                    });
                    return;
                }
            }
            if (!column) return;
            var columnindex = column['__leafindex'];
            var headercell = document.getElementById(column['__domid']);
            if (!headercell) return;
            headercell = $(headercell);
            var cells = [];
            for (var i in g.rows)
            {
                var obj = g.getCellObj(g.rows[i], column);
                if (obj) cells.push(obj);
            }
            for (var i = 0; i < g.totalNumber; i++)
            {
                var tobj = document.getElementById(g.id + "|total" + i + "|" + column['__id']);
                if (tobj) cells.push(tobj);
            }
            var colwidth = column._width;
            //显示列
            if (visible && column._hide)
            {
                if (column.frozen)
                    g.f.gridtablewidth += (parseInt(colwidth) + 1);
                else
                    g.gridtablewidth += (parseInt(colwidth) + 1);
                g._setColumnVisible(column, false);
                $(cells).show();
            }
                //隐藏列
            else if (!visible && !column._hide)
            {
                if (column.frozen)
                    g.f.gridtablewidth -= (parseInt(colwidth) + 1);
                else
                    g.gridtablewidth -= (parseInt(colwidth) + 1);
                g._setColumnVisible(column, true);
                $(cells).hide();
            }
            if (column.frozen)
            {
                $("div:first", g.f.gridheader).width(g.f.gridtablewidth);
                $("div:first", g.f.gridbody).width(g.f.gridtablewidth);
            }
            else
            {
                $("div:first", g.gridheader).width(g.gridtablewidth + 40);
                $("div:first", g.gridbody).width(g.gridtablewidth);
            }
            g._updateFrozenWidth();
            if (!toggleByPopup)
            {
                $(':checkbox[columnindex=' + columnindex + "]", g.popup).each(function ()
                {
                    this.checked = visible;
                    if ($.fn.ligerCheckBox)
                    {
                        var checkboxmanager = $(this).ligerGetCheckBoxManager();
                        if (checkboxmanager) checkboxmanager.updateStyle();
                    }
                });
            }
        },
        //设置列宽
        setColumnWidth: function (columnparm, newwidth)
        {
            var g = this, p = this.options;
            if (!newwidth) return;
            newwidth = parseInt(newwidth, 10);
            var column;
            if (typeof (columnparm) == "number")
            {
                column = g.columns[columnparm];
            }
            else if (typeof (columnparm) == "object" && columnparm['__id'])
            {
                column = columnparm;
            }
            else if (typeof (columnparm) == "string")
            {
                if (g._isColumnId(columnparm)) // column id
                {
                    column = g._columns[columnparm];
                }
                else  // column name
                {
                    $(g.columns).each(function ()
                    {
                        if (this.name == columnparm)
                            g.setColumnWidth(this, newwidth);
                    });
                    return;
                }
            }
            if (!column) return;
            var mincolumnwidth = p.minColumnWidth;
            if (column.minWidth) mincolumnwidth = column.minWidth;
            newwidth = newwidth < mincolumnwidth ? mincolumnwidth : newwidth;
            var diff = newwidth - column._width;
            if (g.trigger('beforeChangeColumnWidth', [column, newwidth]) == false) return;
            column._width = newwidth;
            if (column.frozen)
            {
                g.f.gridtablewidth += diff;
                $("div:first", g.f.gridheader).width(g.f.gridtablewidth);
                $("div:first", g.f.gridbody).width(g.f.gridtablewidth);
            }
            else
            {
                g.gridtablewidth += diff;
                $("div:first", g.gridheader).width(g.gridtablewidth + 40);
                $("div:first", g.gridbody).width(g.gridtablewidth);
            }
            $(document.getElementById(column['__domid'])).css('width', newwidth);
            var cells = [];
            for (var rowid in g.records)
            {
                var obj = g.getCellObj(g.records[rowid], column);
                if (obj) cells.push(obj);

                if (!g.enabledDetailEdit() && g.editors[rowid] && g.editors[rowid][column['__id']])
                {
                    var o = g.editors[rowid][column['__id']];
                    if (o.editor.resize) o.editor.resize(o.input, newwidth, o.container.height(), o.editParm);
                }
            }
            for (var i = 0; i < g.totalNumber; i++)
            {
                var tobj = document.getElementById(g.id + "|total" + i + "|" + column['__id']);
                if (tobj) cells.push(tobj);
            }
            $(cells).css('width', newwidth).find("> div.l-grid-row-cell-inner:first").css('width', newwidth - 8);

            g._updateFrozenWidth();


            g.trigger('afterChangeColumnWidth', [column, newwidth]);
        },
        //改变列表头内容
        changeHeaderText: function (columnparm, headerText)
        {
            var g = this, p = this.options;
            var column;
            if (typeof (columnparm) == "number")
            {
                column = g.columns[columnparm];
            }
            else if (typeof (columnparm) == "object" && columnparm['__id'])
            {
                column = columnparm;
            }
            else if (typeof (columnparm) == "string")
            {
                if (g._isColumnId(columnparm)) // column id
                {
                    column = g._columns[columnparm];
                }
                else  // column name
                {
                    $(g.columns).each(function ()
                    {
                        if (this.name == columnparm)
                            g.changeHeaderText(this, headerText);
                    });
                    return;
                }
            }
            if (!column) return;
            var columnindex = column['__leafindex'];
            var headercell = document.getElementById(column['__domid']);
            $(".l-grid-hd-cell-text", headercell).html(headerText);
            if (p.allowHideColumn)
            {
                $(':checkbox[columnindex=' + columnindex + "]", g.popup).parent().next().html(headerText);
            }
        },
        //改变列的位置
        changeCol: function (from, to, isAfter)
        {
            var g = this, p = this.options;
            if (!from || !to) return;
            var fromCol = g.getColumn(from);
            var toCol = g.getColumn(to);
            fromCol.frozen = toCol.frozen;
            var fromColIndex, toColIndex;
            var fromColumns = fromCol['__pid'] == -1 ? p.columns : g._columns[fromCol['__pid']].columns;
            var toColumns = toCol['__pid'] == -1 ? p.columns : g._columns[toCol['__pid']].columns;
            fromColIndex = $.inArray(fromCol, fromColumns);
            toColIndex = $.inArray(toCol, toColumns);
            var sameParent = fromColumns == toColumns;
            var sameLevel = fromCol['__level'] == toCol['__level'];
            toColumns.splice(toColIndex + (isAfter ? 1 : 0), 0, fromCol);
            if (!sameParent)
            {
                fromColumns.splice(fromColIndex, 1);
            }
            else
            {
                if (isAfter) fromColumns.splice(fromColIndex, 1);
                else fromColumns.splice(fromColIndex + 1, 1);
            }
            g._setColumns(p.columns);
            g.reRender();
        },


        collapseDetail: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata) return;
            for (var i = 0, l = g.columns.length; i < l; i++)
            {
                if (g.columns[i].isdetail)
                {
                    var row = g.getRowObj(rowdata);
                    var cell = g.getCellObj(rowdata, g.columns[i]);
                    $(row).next("tr.l-grid-detailpanel").hide();
                    $(".l-grid-row-cell-detailbtn:first", cell).removeClass("l-open");
                    g.trigger('SysGridHeightChanged');
                    return;
                }
            }
        },
        extendDetail: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata) return;
            for (var i = 0, l = g.columns; i < l; i++)
            {
                if (g.columns[i].isdetail)
                {
                    var row = g.getRowObj(rowdata);
                    var cell = g.getCellObj(rowdata, g.columns[i]);
                    $(row).next("tr.l-grid-detailpanel").show();
                    $(".l-grid-row-cell-detailbtn:first", cell).addClass("l-open");
                    g.trigger('SysGridHeightChanged');
                    return;
                }
            }
        },
        getParent: function (rowParm)
        {
            var g = this, p = this.options;
            if (!p.tree) return null;
            var rowdata = g.getRow(rowParm);
            if (!rowdata) return null;
            if (rowdata['__pid'] in g.records) return g.records[rowdata['__pid']];
            else return null;
        },
        getChildren: function (rowParm, deep)
        {
            var g = this, p = this.options;
            if (!p.tree) return null;
            var rowData = g.getRow(rowParm);
            if (!rowData) return null;
            var arr = [];
            function loadChildren(data)
            {
                if (data[p.tree.childrenName])
                {
                    for (var i = 0, l = data[p.tree.childrenName].length; i < l; i++)
                    {
                        var o = data[p.tree.childrenName][i];
                        if (o['__status'] == 'delete') continue;
                        arr.push(o);
                        if (deep)
                            loadChildren(o);
                    }
                }
            }
            loadChildren(rowData);
            return arr;
        },
        isLeaf: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata) return;
            return rowdata['__hasChildren'] ? false : true;
        },
        hasChildren: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = this.getRow(rowParm);
            if (!rowdata) return;
            return (rowdata[p.tree.childrenName] && rowdata[p.tree.childrenName].length) ? true : false;
        },
        existRecord: function (record)
        {
            for (var rowid in this.records)
            {
                if (this.records[rowid] == record) return true;
            }
            return false;
        },
        _removeSelected: function (rowdata)
        {
            var g = this, p = this.options;
            if (p.tree)
            {
                var children = g.getChildren(rowdata, true);
                if (children)
                {
                    for (var i = 0, l = children.length; i < l; i++)
                    {
                        var index2 = $.inArray(children[i], g.selected);
                        if (index2 != -1) g.selected.splice(index2, 1);
                    }
                }
            }
            var index = $.inArray(rowdata, g.selected);
            if (index != -1) g.selected.splice(index, 1);
        },
        _getParentChildren: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var listdata;
            if (p.tree && g.existRecord(rowdata) && rowdata['__pid'] in g.records)
            {
                listdata = g.records[rowdata['__pid']][p.tree.childrenName];
            }
            else
            {
                listdata = g.currentData[p.root];
            }
            return listdata;
        },
        _removeData: function (rowdata)
        {
            var g = this, p = this.options;
            var listdata = g._getParentChildren(rowdata);
            var index = $.inArray(rowdata, listdata);
            if (index != -1)
            {
                listdata.splice(index, 1);
            }
            g._removeSelected(rowdata);
        },
        _addData: function (rowdata, parentdata, neardata, isBefore)
        {
            var g = this, p = this.options;
            if (!g.currentData) g.currentData = {};
            if (!g.currentData[p.root]) g.currentData[p.root] = [];
            var listdata = g.currentData[p.root];
            if (neardata)
            {
                if (p.tree)
                {
                    if (parentdata)
                        listdata = parentdata[p.tree.childrenName];
                    else if (neardata['__pid'] in g.records)
                        listdata = g.records[neardata['__pid']][p.tree.childrenName];
                }
                var index = $.inArray(neardata, listdata);
                listdata.splice(index == -1 ? -1 : index + (isBefore ? 0 : 1), 0, rowdata);
            }
            else
            {
                if (p.tree && parentdata)
                {
                    listdata = parentdata[p.tree.childrenName];
                }
                listdata.push(rowdata);
            }
        },
        //移动数据(树)
        //@parm [parentdata] 附加到哪一个节点下级
        //@parm [neardata] 附加到哪一个节点的上方/下方
        //@parm [isBefore] 是否附加到上方
        _appendData: function (rowdata, parentdata, neardata, isBefore)
        {
            var g = this, p = this.options;
            rowdata[p.statusName] = "update";
            g._removeData(rowdata);
            g._addData(rowdata, parentdata, neardata, isBefore);
        },
        appendRange: function (rows, parentdata, neardata, isBefore)
        {
            var g = this, p = this.options;
            var toRender = false;
            $.each(rows, function (i, item)
            {
                if (item['__id'] && g.existRecord(item))
                {
                    if (g.isLeaf(parentdata)) g.upgrade(parentdata);
                    g._appendData(item, parentdata, neardata, isBefore);
                    toRender = true;
                }
                else
                {
                    g.appendRow(item, parentdata, neardata, isBefore);
                }
            });
            if (toRender) g.reRender();

        },
        appendRow: function (rowdata, parentdata, neardata, isBefore)
        {
            var g = this, p = this.options;
            if ($.isArray(rowdata))
            {
                g.appendRange(rowdata, parentdata, neardata, isBefore);
                return;
            }
            if (rowdata['__id'] && g.existRecord(rowdata))
            {
                g._appendData(rowdata, parentdata, neardata, isBefore);
                g.reRender();
                return;
            }
            if (parentdata && g.isLeaf(parentdata)) g.upgrade(parentdata);
            g.addRow(rowdata, neardata, isBefore ? true : false, parentdata);
        },
        upgrade: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata || !p.tree) return;
            rowdata[p.tree.childrenName] = rowdata[p.tree.childrenName] || [];
            rowdata['__hasChildren'] = true;
            var rowobjs = [g.getRowObj(rowdata)];
            if (g.enabledFrozen()) rowobjs.push(g.getRowObj(rowdata, true));
            $("> td > div > .l-grid-tree-space:last", rowobjs).addClass("l-grid-tree-link l-grid-tree-link-open");
        },
        demotion: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            if (!rowdata || !p.tree) return;
            var rowobjs = [g.getRowObj(rowdata)];
            if (g.enabledFrozen()) rowobjs.push(g.getRowObj(rowdata, true));
            $("> td > div > .l-grid-tree-space:last", rowobjs).removeClass("l-grid-tree-link l-grid-tree-link-open l-grid-tree-link-close");
            if (g.hasChildren(rowdata))
            {
                var children = g.getChildren(rowdata);
                for (var i = 0, l = children.length; i < l; i++)
                {
                    g.deleteRow(children[i]);
                }
            }
            rowdata['__hasChildren'] = false;
        },
        collapse: function (rowParm)
        {
            var g = this, p = this.options;
            var targetRowObj = g.getRowObj(rowParm);
            var linkbtn = $(".l-grid-tree-link", targetRowObj);
            if (linkbtn.hasClass("l-grid-tree-link-close")) return;
            g.toggle(rowParm);
        },
        expand: function (rowParm)
        {
            var g = this, p = this.options;
            var targetRowObj = g.getRowObj(rowParm);
            var linkbtn = $(".l-grid-tree-link", targetRowObj);
            if (linkbtn.hasClass("l-grid-tree-link-open")) return;
            g.toggle(rowParm);
        },
        toggle: function (rowParm)
        {
            if (!rowParm) return;
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var targetRowObj = [g.getRowObj(rowdata)];
            if (g.enabledFrozen()) targetRowObj.push(g.getRowObj(rowdata, true));
            var level = rowdata['__level'], indexInCollapsedRows;
            var linkbtn = $(".l-grid-tree-link:first", targetRowObj);
            var opening = true;
            g.collapsedRows = g.collapsedRows || [];
            if (linkbtn.hasClass("l-grid-tree-link-close")) //收缩
            {
                linkbtn.removeClass("l-grid-tree-link-close").addClass("l-grid-tree-link-open");
                indexInCollapsedRows = $.inArray(rowdata, g.collapsedRows);
                if (indexInCollapsedRows != -1) g.collapsedRows.splice(indexInCollapsedRows, 1);
            }
            else //折叠
            {
                opening = false;
                linkbtn.addClass("l-grid-tree-link-close").removeClass("l-grid-tree-link-open");
                indexInCollapsedRows = $.inArray(rowdata, g.collapsedRows);
                if (indexInCollapsedRows == -1) g.collapsedRows.push(rowdata);
            }
            var children = g.getChildren(rowdata, true);
            for (var i = 0, l = children.length; i < l; i++)
            {
                var o = children[i];
                var currentRow = $([g.getRowObj(o['__id'])]);
                if (g.enabledFrozen()) currentRow = currentRow.add(g.getRowObj(o['__id'], true));
                if (opening)
                {
                    $(".l-grid-tree-link", currentRow).removeClass("l-grid-tree-link-close").addClass("l-grid-tree-link-open");
                    currentRow.show();
                }
                else
                {
                    $(".l-grid-tree-link", currentRow).removeClass("l-grid-tree-link-open").addClass("l-grid-tree-link-close");
                    currentRow.hide();
                }
            }
        },
        _bulid: function ()
        {
            var g = this;
            g._clearGrid();
            //创建头部
            g._initBuildHeader();
            //宽度高度初始化
            g._initHeight();
            //创建底部工具条
            g._initFootbar();
            //创建分页
            g._buildPager();
            //创建事件
            g._setEvent();
        },
        _setColumns: function (columns)
        {
            var g = this;
            //初始化列
            g._initColumns();
            //创建表头
            g._initBuildGridHeader();
            //创建 显示/隐藏 列 列表
            g._initBuildPopup();
        },
        _initBuildHeader: function ()
        {
            var g = this, p = this.options;
            if (p.title)
            {
                $(".l-panel-header-text", g.header).html(p.title);
                if (p.headerImg)
                    g.header.append("<img src='" + p.headerImg + "' />").addClass("l-panel-header-hasicon");
            }
            else
            {
                g.header.hide();
            }
            if (p.toolbar)
            {
                if ($.fn.ligerToolBar)
                    g.toolbarManager = g.topbar.ligerToolBar(p.toolbar);
            }
            else
            {
                g.topbar.remove();
            }
        },
        _createColumnId: function (column)
        {
            if (column.id != null && column.id != "") return column.id.toString();
            return "c" + (100 + this._columnCount);
        },
        _isColumnId: function (str)
        {
            return (str in this._columns);
        },
        _initColumns: function ()
        {
            var g = this, p = this.options;
            g._columns = {};             //全部列的信息  
            g._columnCount = 0;
            g._columnLeafCount = 0;
            g._columnMaxLevel = 1;
            if (!p.columns) return;
            function removeProp(column, props)
            {
                for (var i in props)
                {
                    if (props[i] in column)
                        delete column[props[i]];
                }
            }
            //设置id、pid、level、leaf，返回叶节点数,如果是叶节点，返回1
            function setColumn(column, level, pid, previd)
            {
                removeProp(column, ['__id', '__pid', '__previd', '__nextid', '__domid', '__leaf', '__leafindex', '__level', '__colSpan', '__rowSpan']);
                if (level > g._columnMaxLevel) g._columnMaxLevel = level;
                g._columnCount++;
                column['__id'] = g._createColumnId(column);
                column['__domid'] = g.id + "|hcell|" + column['__id'];
                g._columns[column['__id']] = column;
                if (!column.columns || !column.columns.length)
                    column['__leafindex'] = g._columnLeafCount++;
                column['__level'] = level;
                column['__pid'] = pid;
                column['__previd'] = previd;
                if (!column.columns || !column.columns.length)
                {
                    column['__leaf'] = true;
                    return 1;
                }
                var leafcount = 0;
                var newid = -1;
                for (var i = 0, l = column.columns.length; i < l; i++)
                {
                    var col = column.columns[i];
                    leafcount += setColumn(col, level + 1, column['__id'], newid);
                    newid = col['__id'];
                }
                column['__leafcount'] = leafcount;
                return leafcount;
            }
            var lastid = -1;
            //行序号
            if (p.rownumbers)
            {
                var frozenRownumbers = g.enabledGroup() ? false : p.frozen && p.frozenRownumbers;
                var col = { isrownumber: true, issystem: true, width: p.rownumbersColWidth, frozen: frozenRownumbers };
                setColumn(col, 1, -1, lastid);
                lastid = col['__id'];
            }
            //明细列
            if (g.enabledDetail())
            {
                var frozenDetail = g.enabledGroup() ? false : p.frozen && p.frozenDetail;
                var col = { isdetail: true, issystem: true, width: p.detailColWidth, frozen: frozenDetail };
                setColumn(col, 1, -1, lastid);
                lastid = col['__id'];
            }
            //复选框列
            if (g.enabledCheckbox())
            {
                var frozenCheckbox = g.enabledGroup() ? false : p.frozen && p.frozenCheckbox;
                var col = { ischeckbox: true, issystem: true, width: p.detailColWidth, frozen: frozenCheckbox };
                setColumn(col, 1, -1, lastid);
                lastid = col['__id'];
            }
            for (var i = 0, l = p.columns.length; i < l; i++)
            {
                var col = p.columns[i];
                setColumn(col, 1, -1, lastid);
                lastid = col['__id'];
            }
            //设置colSpan和rowSpan
            for (var id in g._columns)
            {
                var col = g._columns[id];
                if (col['__leafcount'] > 1)
                {
                    col['__colSpan'] = col['__leafcount'];
                }
                if (col['__leaf'] && col['__level'] != g._columnMaxLevel)
                {
                    col['__rowSpan'] = g._columnMaxLevel - col['__level'] + 1;
                }
            }
            //叶级别列的信息  
            g.columns = g.getColumns();
            $(g.columns).each(function (i, column)
            {
                column.columnname = column.name;
                column.columnindex = i;
                column.type = column.type || "string";
                column.islast = i == g.columns.length - 1;
                column.isSort = column.isSort == false ? false : true;
                column.frozen = column.frozen ? true : false;
                column._width = g._getColumnWidth(column);
                column._hide = column.hide ? true : false;
            });
        },
        _getColumnWidth: function (column)
        {
            var g = this, p = this.options;
            if (column._width) return column._width;
            var colwidth;
            if (column.width)
            {
                colwidth = column.width;
            }
            else if (p.columnWidth)
            {
                colwidth = p.columnWidth;
            }
            if (!colwidth)
            {
                var lwidth = 4;
                if (g.enabledCheckbox()) lwidth += p.checkboxColWidth;
                if (g.enabledDetail()) lwidth += p.detailColWidth;
                colwidth = parseInt((g.grid.width() - lwidth) / g.columns.length);
            }
            if (typeof (colwidth) == "string" && colwidth.indexOf('%') > 0)
            {
                column._width = colwidth = parseInt(parseInt(colwidth) * 0.01 * (g.grid.width() - g.columns.length));
            }
            if (column.minWidth && colwidth < column.minWidth) colwidth = column.minWidth;
            if (column.maxWidth && colwidth > column.maxWidth) colwidth = column.maxWidth;
            column._width = colwidth;
            return colwidth;
        },
        _createHeaderCell: function (column)
        {
            var g = this, p = this.options;
            var jcell = $("<td class='l-grid-hd-cell'><div class='l-grid-hd-cell-inner'><span class='l-grid-hd-cell-text'></span></div></td>");
            jcell.attr("id", column['__domid']);
            if (!column['__leaf'])
                jcell.addClass("l-grid-hd-cell-mul");
            if (column.columnindex == g.columns.length - 1)
            {
                jcell.addClass("l-grid-hd-cell-last");
            }
            if (column.isrownumber)
            {
                jcell.addClass("l-grid-hd-cell-rownumbers");
                jcell.html("<div class='l-grid-hd-cell-inner'></div>");
            }
            if (column.ischeckbox)
            {
                jcell.addClass("l-grid-hd-cell-checkbox");
                jcell.html("<div class='l-grid-hd-cell-inner'><div class='l-grid-hd-cell-text l-grid-hd-cell-btn-checkbox'></div></div>");
            }
            if (column.isdetail)
            {
                jcell.addClass("l-grid-hd-cell-detail");
                jcell.html("<div class='l-grid-hd-cell-inner'><div class='l-grid-hd-cell-text l-grid-hd-cell-btn-detail'></div></div>");
            }
            if (column.heightAlign)
            {
                $(".l-grid-hd-cell-inner:first", jcell).css("textAlign", column.heightAlign);
            }
            if (column['__colSpan']) jcell.attr("colSpan", column['__colSpan']);
            if (column['__rowSpan'])
            {
                jcell.attr("rowSpan", column['__rowSpan']);
                jcell.height(p.headerRowHeight * column['__rowSpan']);
            } else
            {
                jcell.height(p.headerRowHeight);
            }
            if (column['__leaf'])
            {
                jcell.width(column['_width']);
                jcell.attr("columnindex", column['__leafindex']);
            }
            var cellHeight = jcell.height();
            if(cellHeight > 10) $(">div:first", jcell).height(cellHeight);
            if (column._hide) jcell.hide();
            if (column.name) jcell.attr({ columnname: column.name });
            var headerText = "";
            if (column.display && column.display != "")
                headerText = column.display;
            else if (column.headerRender)
                headerText = column.headerRender(column);
            else
                headerText = "&nbsp;";
            $(".l-grid-hd-cell-text:first", jcell).html(headerText);
            if (!column.issystem && column['__leaf'] && column.resizable !== false && $.fn.ligerResizable && p.allowAdjustColWidth)
            {
                g.colResizable[column['__id']] = jcell.ligerResizable({
                    handles: 'e',
                    onStartResize: function (e, ev)
                    {
                        this.proxy.hide();
                        g.draggingline.css({ height: g.body.height(), top: 0, left: ev.pageX - g.grid.offset().left + parseInt(g.body[0].scrollLeft) }).show();
                    },
                    onResize: function (e, ev)
                    {
                        g.colresizing = true;
                        g.draggingline.css({ left: ev.pageX - g.grid.offset().left + parseInt(g.body[0].scrollLeft) });
                        $('body').add(jcell).css('cursor', 'e-resize');
                    },
                    onStopResize: function (e)
                    {
                        g.colresizing = false;
                        $('body').add(jcell).css('cursor', 'default');
                        g.draggingline.hide();
                        g.setColumnWidth(column, parseInt(column._width) + e.diffX);
                        return false;
                    }
                });
            }
            return jcell;
        },
        _initBuildGridHeader: function ()
        {
            var g = this, p = this.options;
            g.gridtablewidth = 0;
            g.f.gridtablewidth = 0;
            if (g.colResizable)
            {
                for (var i in g.colResizable)
                {
                    g.colResizable[i].destroy();
                }
                g.colResizable = null;
            }
            g.colResizable = {};
            $("tbody:first", g.gridheader).html("");
            $("tbody:first", g.f.gridheader).html("");
            for (var level = 1; level <= g._columnMaxLevel; level++)
            {
                var columns = g.getColumns(level);           //获取level层次的列集合
                var islast = level == g._columnMaxLevel;     //是否最末级
                var tr = $("<tr class='l-grid-hd-row'></tr>");
                var trf = $("<tr class='l-grid-hd-row'></tr>");
                if (!islast) tr.add(trf).addClass("l-grid-hd-mul");
                $("tbody:first", g.gridheader).append(tr);
                $("tbody:first", g.f.gridheader).append(trf);
                $(columns).each(function (i, column)
                {
                    (column.frozen ? trf : tr).append(g._createHeaderCell(column));
                    if (column['__leaf'])
                    {
                        var colwidth = column['_width'];
                        if (!column.frozen)
                            g.gridtablewidth += (parseInt(colwidth) ? parseInt(colwidth) : 0) + 1;
                        else
                            g.f.gridtablewidth += (parseInt(colwidth) ? parseInt(colwidth) : 0) + 1;
                    }
                });
            }
            if (g._columnMaxLevel > 0)
            {
                var h = p.headerRowHeight * g._columnMaxLevel;
                g.gridheader.add(g.f.gridheader).height(h);
                if (p.rownumbers && p.frozenRownumbers) g.f.gridheader.find("td:first").height(h);
            }
            g._updateFrozenWidth();
            $("div:first", g.gridheader).width(g.gridtablewidth + 40);
        },
        _initBuildPopup: function ()
        {
            var g = this, p = this.options;
            $(':checkbox', g.popup).unbind();
            $('tbody tr', g.popup).remove();
            $(g.columns).each(function (i, column)
            {
                if (column.issystem) return;
                if (column.isAllowHide == false) return;
                var chk = 'checked="checked"';
                if (column._hide) chk = '';
                var header = column.display;
                $('tbody', g.popup).append('<tr><td class="l-column-left"><input type="checkbox" ' + chk + ' class="l-checkbox" columnindex="' + i + '"/></td><td class="l-column-right">' + header + '</td></tr>');
            });
            if ($.fn.ligerCheckBox)
            {
                $('input:checkbox', g.popup).ligerCheckBox(
                {
                    onBeforeClick: function (obj)
                    {
                        if (!obj.checked) return true;
                        if ($('input:checked', g.popup).length <= p.minColToggle)
                            return false;
                        return true;
                    }
                });
            }
            //表头 - 显示/隐藏'列控制'按钮事件
            if (p.allowHideColumn)
            {
                $('tr', g.popup).hover(function ()
                {
                    $(this).addClass('l-popup-row-over');
                },
                function ()
                {
                    $(this).removeClass('l-popup-row-over');
                });
                var onPopupCheckboxChange = function ()
                {
                    if ($('input:checked', g.popup).length + 1 <= p.minColToggle)
                    {
                        return false;
                    }
                    g.toggleCol(parseInt($(this).attr("columnindex")), this.checked, true);
                };
                if ($.fn.ligerCheckBox)
                    $(':checkbox', g.popup).bind('change', onPopupCheckboxChange);
                else
                    $(':checkbox', g.popup).bind('click', onPopupCheckboxChange);
            }
        },
        _initHeight: function ()
        {
            var g = this, p = this.options;
            if (p.height == 'auto')
            {
                g.gridbody.height('auto');
                g.f.gridbody.height('auto');
            }
            if (p.width)
            {
                g.grid.width(p.width);
            }
            g._onResize.call(g);
        },
        _initFootbar: function ()
        {
            var g = this, p = this.options;
            if (p.usePager)
            {
                //创建底部工具条 - 选择每页显示记录数
                var optStr = "";
                var selectedIndex = -1;
                $(p.pageSizeOptions).each(function (i, item)
                {
                    var selectedStr = "";
                    if (p.pageSize == item) selectedIndex = i;
                    optStr += "<option value='" + item + "' " + selectedStr + " >" + item + "</option>";
                });

                $('.l-bar-selectpagesize', g.toolbar).append("<select name='rp'>" + optStr + "</select>");
                if (selectedIndex != -1) $('.l-bar-selectpagesize select', g.toolbar)[0].selectedIndex = selectedIndex;
                if (p.switchPageSizeApplyComboBox && $.fn.ligerComboBox)
                {
                    $(".l-bar-selectpagesize select", g.toolbar).ligerComboBox(
                    {
                        onBeforeSelect: function ()
                        {
                            if (p.url && g.isDataChanged && !confirm(p.isContinueByDataChanged))
                                return false;
                            return true;
                        },
                        width: 45
                    });
                }
            }
            else
            {
                g.toolbar.hide();
            }
        },
        _searchData: function (data, clause)
        {
            var g = this, p = this.options;
            var newData = new Array();
            for (var i = 0; i < data.length; i++)
            {
                if (clause(data[i], i))
                {
                    newData[newData.length] = data[i];
                }
            }
            return newData;
        },
        _clearGrid: function ()
        {
            var g = this, p = this.options;
            for (var i in g.rows)
            {
                var rowobj = $(g.getRowObj(g.rows[i]));
                if (g.enabledFrozen())
                    rowobj = rowobj.add(g.getRowObj(g.rows[i], true));
                rowobj.unbind();
            }
            //清空数据
            g.gridbody.html("");
            g.f.gridbody.html("");
            g.recordNumber = 0;
            g.records = {};
            g.rows = [];
            //清空选择的行
            g.selected = [];
            g.totalNumber = 0;
            //编辑器计算器
            g.editorcounter = 0;
        },
        _fillGridBody: function (data, frozen)
        {
            var g = this, p = this.options;
            //加载数据 
            var gridhtmlarr = ['<div class="l-grid-body-inner"><table class="l-grid-body-table" cellpadding=0 cellspacing=0><tbody>'];
            //modify by songxf at 2013-7-9
            if(data.length==0){
            	gridhtmlarr.push('<table><tr height="1" width="1"><td></td></tr></table>');
            }
            //modify end 
            if (g.enabledGroup()) //启用分组模式
            {
                var groups = []; //分组列名数组
                var groupsdata = []; //切成几块后的数据
                g.groups = groupsdata;
                for (var rowparm in data)
                {
                    var item = data[rowparm];
                    var groupColumnValue = item[p.groupColumnName];
                    var valueIndex = $.inArray(groupColumnValue, groups);
                    if (valueIndex == -1)
                    {
                        groups.push(groupColumnValue);
                        valueIndex = groups.length - 1;
                        groupsdata.push([]);
                    }
                    groupsdata[valueIndex].push(item);
                }
                $(groupsdata).each(function (i, item)
                {
                    if (groupsdata.length == 1)
                        gridhtmlarr.push('<tr class="l-grid-grouprow l-grid-grouprow-last l-grid-grouprow-first"');
                    if (i == groupsdata.length - 1)
                        gridhtmlarr.push('<tr class="l-grid-grouprow l-grid-grouprow-last"');
                    else if (i == 0)
                        gridhtmlarr.push('<tr class="l-grid-grouprow l-grid-grouprow-first"');
                    else
                        gridhtmlarr.push('<tr class="l-grid-grouprow"');
                    gridhtmlarr.push(' groupindex"=' + i + '" >');
                    gridhtmlarr.push('<td colSpan="' + g.columns.length + '" class="l-grid-grouprow-cell">');
                    gridhtmlarr.push('<span class="l-grid-group-togglebtn">&nbsp;&nbsp;&nbsp;&nbsp;</span>');
                    if (p.groupRender)
                        gridhtmlarr.push(p.groupRender(groups[i], item, p.groupColumnDisplay));
                    else
                        gridhtmlarr.push(p.groupColumnDisplay + ':' + groups[i]);


                    gridhtmlarr.push('</td>');
                    gridhtmlarr.push('</tr>');

                    gridhtmlarr.push(g._getHtmlFromData(item, frozen));
                    //汇总
                    if (g.isTotalSummary())
                        gridhtmlarr.push(g._getTotalSummaryHtml(item, "l-grid-totalsummary-group", frozen));
                });
            }
            else
            {
                gridhtmlarr.push(g._getHtmlFromData(data, frozen));
            }
            gridhtmlarr.push('</tbody></table></div>');
            (frozen ? g.f.gridbody : g.gridbody).html(gridhtmlarr.join(''));
            //分组时不需要            
            if (!g.enabledGroup())
            {
                //创建汇总行
                g._bulidTotalSummary(frozen);
            }
            $("> div:first", g.gridbody).width(g.gridtablewidth);
            g._onResize();
        },
        _showData: function ()
        {
            var g = this, p = this.options;
            g.changedCells = {};
            var data = g.currentData[p.root];
            if (p.usePager)
            {
                //更新总记录数
                if (p.dataAction == "server" && g.data && g.data[p.record])
                    p.total = g.data[p.record];
                else if (g.filteredData && g.filteredData[p.root])
                    p.total = g.filteredData[p.root].length;
                else if (g.data && g.data[p.root])
                    p.total = g.data[p.root].length;
                else if (data)
                    p.total = data.length;

                p.page = p.newPage;
                if (!p.total) p.total = 0;
                if (!p.page) p.page = 1;
                p.pageCount = Math.ceil(p.total / p.pageSize);
                if (!p.pageCount) p.pageCount = 1;
                //更新分页
                g._buildPager();
            }
            //加载中
            $('.l-bar-btnloading:first', g.toolbar).removeClass('l-bar-btnloading');
            if (g.trigger('beforeShowData', [g.currentData]) == false) return;
            g._clearGrid();
            g.isDataChanged = false;
            if (!data) return;
            $(".l-bar-btnload:first span", g.toolbar).removeClass("l-disabled");
            g._updateGridData();
            if (g.enabledFrozen())
                g._fillGridBody(g.rows, true);
            g._fillGridBody(g.rows, false);
            g.trigger('SysGridHeightChanged');
            if (p.totalRender)
            {
                $(".l-panel-bar-total", g.element).remove();
                $(".l-panel-bar", g.element).before('<div class="l-panel-bar-total">' + p.totalRender(g.data, g.filteredData) + '</div>');
            }
            if (p.mouseoverRowCssClass)
            {
                for (var i in g.rows)
                {
                    var rowobj = $(g.getRowObj(g.rows[i]));
                    if (g.enabledFrozen())
                        rowobj = rowobj.add(g.getRowObj(g.rows[i], true));
                    rowobj.bind('mouseover.gridrow', function ()
                    {
                        g._onRowOver(this, true);
                    }).bind('mouseout.gridrow', function ()
                    {
                        g._onRowOver(this, false);
                    });
                }
            }
            g._fixHeight();
            g.gridbody.trigger('scroll.grid'); 
            g.trigger('afterShowData', [g.currentData]);
        },
        _fixHeight : function()
        {
            var g = this, p = this.options;
            if (p.fixedCellHeight || !p.frozen) return;
            var column1,column2;
            for (var i in g.columns)
            {
                var column = g.columns[i];
                if(column1 && column2) break;
                if (column.frozen && !column1)
                { 
                    column1 = column; 
                    continue;
                }
                if (!column.frozen && !column2)
                {
                    column2 = column; 
                    continue;
                } 
            }
            if (!column1 || !column2) return;
            for (var rowid in g.records)
            {
                var cell1 = g.getCellObj(rowid, column1), cell2 = g.getCellObj(rowid, column2);
                var height = Math.max($(cell1).height(), ($(cell2).height()));
                $(cell1).add(cell2).height(height);
            }
        },
        _getRowDomId: function (rowdata, frozen)
        {
            return this.id + "|" + (frozen ? "1" : "2") + "|" + rowdata['__id'];
        },
        _getCellDomId: function (rowdata, column)
        {
            return this._getRowDomId(rowdata, column.frozen) + "|" + column['__id'];
        },
        _getHtmlFromData: function (data, frozen)
        {
            if (!data) return "";
            var g = this, p = this.options;
            var gridhtmlarr = []; 
            for (var i = 0, l = data.length; i < l; i++)
            {
                var item = data[i];
                var rowid = item['__id'];
                if (!item) continue;
                gridhtmlarr.push('<tr');
                gridhtmlarr.push(' id="' + g._getRowDomId(item, frozen) + '"');
                gridhtmlarr.push(' class="l-grid-row'); //class start 
                if (!frozen && g.enabledCheckbox() && p.isChecked && p.isChecked(item))
                {
                    g.select(item);
                    gridhtmlarr.push(' l-selected');
                }
                else if (g.isSelected(item))
                {
                    gridhtmlarr.push(' l-selected');
                }
                else if (p.isSelected && p.isSelected(item))
                {
                    g.select(item);
                    gridhtmlarr.push(' l-selected');
                }
                if (item['__index'] % 2 == 1 && p.alternatingRow)
                    gridhtmlarr.push(' l-grid-row-alt');
                gridhtmlarr.push('" ');  //class end
                if (p.rowAttrRender) gridhtmlarr.push(p.rowAttrRender(item, rowid));
                if (p.tree && g.collapsedRows && g.collapsedRows.length)
                {
                    var isHide = function ()
                    {
                        var pitem = g.getParent(item);
                        while (pitem)
                        {
                            if ($.inArray(pitem, g.collapsedRows) != -1) return true;
                            pitem = g.getParent(pitem);
                        }
                        return false;
                    };
                    if (isHide()) gridhtmlarr.push(' style="display:none;" ');
                }
                gridhtmlarr.push('>');
                $(g.columns).each(function (columnindex, column)
                {
                    if (frozen != column.frozen) return;
                    gridhtmlarr.push('<td');
                    gridhtmlarr.push(' id="' + g._getCellDomId(item, this) + '"');
                    //如果是行序号(系统列)
                    if (this.isrownumber)
                    {
                        gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-rownumbers" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
                        if (p.fixedCellHeight)
                            gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
                        else
                            gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
                        gridhtmlarr.push('>' + (parseInt(item['__index']) + 1) + '</div></td>');
                        return;
                    }
                    //如果是复选框(系统列)
                    if (this.ischeckbox)
                    {
                        gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-checkbox" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
                        if (p.fixedCellHeight)
                            gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
                        else
                            gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
                        gridhtmlarr.push('><span class="l-grid-row-cell-btn-checkbox"></span></div></td>');
                        return;
                    }
                        //如果是明细列(系统列)
                    else if (this.isdetail)
                    {
                        gridhtmlarr.push(' class="l-grid-row-cell l-grid-row-cell-detail" style="width:' + this.width + 'px"><div class="l-grid-row-cell-inner"');
                        if (p.fixedCellHeight)
                            gridhtmlarr.push(' style = "height:' + p.rowHeight + 'px;" ');
                        else
                            gridhtmlarr.push(' style = "min-height:' + p.rowHeight + 'px;" ');
                        gridhtmlarr.push('><span class="l-grid-row-cell-detailbtn"></span></div></td>');
                        return;
                    }
                    var colwidth = this._width;
                    gridhtmlarr.push(' class="l-grid-row-cell ');
                    if (g.changedCells[rowid + "_" + this['__id']]) gridhtmlarr.push("l-grid-row-cell-edited ");
                    if (this.islast)
                        gridhtmlarr.push('l-grid-row-cell-last ');
                    gridhtmlarr.push('"');
                    //if (this.columnname) gridhtmlarr.push('columnname="' + this.columnname + '"');
                    gridhtmlarr.push(' style = "');
                    gridhtmlarr.push('width:' + colwidth + 'px; ');
                    if (column._hide)
                    {
                        gridhtmlarr.push('display:none;');
                    }
                    gridhtmlarr.push(' ">');
                    gridhtmlarr.push(g._getCellHtml(item, column));
                    gridhtmlarr.push('</td>');
                });
                gridhtmlarr.push('</tr>');
            }
            return gridhtmlarr.join('');
        },
        _getCellHtml: function (rowdata, column)
        {
            var g = this, p = this.options;
            if (column.isrownumber)
                return '<div class="l-grid-row-cell-inner">' + (parseInt(rowdata['__index']) + 1) + '</div>';
            var htmlarr = [];
            htmlarr.push('<div class="l-grid-row-cell-inner"');
            //htmlarr.push('<div');
            htmlarr.push(' style = "width:' + parseInt(column._width - 8) + 'px;');
            if (p.fixedCellHeight) htmlarr.push('height:' + p.rowHeight + 'px;');
            htmlarr.push('min-height:' + p.rowHeight + 'px; ');
            if (column.align) htmlarr.push('text-align:' + column.align + ';');
            var content = g._getCellContent(rowdata, column);
            htmlarr.push('">' + content + '</div>');
            return htmlarr.join('');
        },
        _setValueByName: function (rowdata, columnname, value)
        {
            if (!rowdata || !columnname) return null;
            try
            {
                new Function("rowdata,value", "rowdata." + columnname + "=value;")(rowdata, value);
            }
            catch (e)
            {
            }
        },
        _getValueByName: function (rowdata, columnname)
        {
            if (!rowdata || !columnname) return null;
            try
            {
                return new Function("rowdata", "return rowdata." + columnname + ";")(rowdata);
            }
            catch (e)
            {
                return null;
            }
        },
        _getCellContent: function (rowdata, column)
        {
            var g = this, p = this.options;
            if (!rowdata || !column) return "";
            if (column.isrownumber) return parseInt(rowdata['__index']) + 1;
            var rowid = rowdata['__id'];
            var rowindex = rowdata['__index'];
            var value = g._getValueByName(rowdata, column.name);
            var text = g._getValueByName(rowdata, column.textField);
            var content = "";
            if (column.render)
            {
                content = column.render.call(g, rowdata, rowindex, value, column);
            }
            else if (p.formatters[column.type])
            {
                content = p.formatters[column.type].call(g, value, column);
            }
            else if (text != null)
            {
                content = text.toString();
            }
            else if (value != null)
            {
                content = value.toString();
            }
            if (p.tree && (p.tree.columnName != null && p.tree.columnName == column.name || p.tree.columnId != null && p.tree.columnId == column.id))
            {
                content = g._getTreeCellHtml(content, rowdata);
            }
            return content || "";
        },
        _getTreeCellHtml: function (oldContent, rowdata)
        {
            var level = rowdata['__level'];
            var g = this, p = this.options;
            //var isExtend = p.tree.isExtend(rowdata);
            var isExtend = $.inArray(rowdata, g.collapsedRows || []) == -1;
            var isParent = p.tree.isParent(rowdata);
            var content = "";
            level = parseInt(level) || 1;
            for (var i = 1; i < level; i++)
            {
                content += "<div class='l-grid-tree-space'></div>";
            }
            if (isExtend && isParent)
                content += "<div class='l-grid-tree-space l-grid-tree-link l-grid-tree-link-open'></div>";
            else if (isParent)
                content += "<div class='l-grid-tree-space l-grid-tree-link l-grid-tree-link-close'></div>";
            else
                content += "<div class='l-grid-tree-space'></div>";
            content += "<span class='l-grid-tree-content'>" + oldContent + "</span>";
            return content;
        },
        _applyEditor: function (obj)
        {
            var g = this, p = this.options;
            var rowcell = obj, ids = rowcell.id.split('|');
            var columnid = ids[ids.length - 1], column = g._columns[columnid];
            var row = $(rowcell).parent(), rowdata = g.getRow(row[0]), rowid = rowdata['__id'], rowindex = rowdata['__index'];
            if (!column || !column.editor) return;
            var columnname = column.name, columnindex = column.columnindex;
            if (column.editor.type && p.editors[column.editor.type])
            {
                var currentdata = g._getValueByName(rowdata, columnname);
                var editParm = { record: rowdata, value: currentdata, column: column, rowindex: rowindex };
                if (column.textField) editParm.text = g._getValueByName(rowdata, column.textField);
                if (g.trigger('beforeEdit', [editParm]) == false) return false;
                g.lastEditRow = rowdata;
                var editor = p.editors[column.editor.type];
                var jcell = $(rowcell), offset = $(rowcell).offset();
                jcell.html("");
                g.setCellEditing(rowdata, column, true);
                var width = $(rowcell).width(), height = $(rowcell).height();
                var container = $("<div class='l-grid-editor'></div>").appendTo(g.grid);
                var left = 0, top = 0;
                var pc = jcell.position();
                var pb = g.gridbody.position();
                var pv = g.gridview2.position();
                var topbarHeight = p.toolbar ? g.topbar.height() : 0;
                left = pc.left + pb.left + pv.left;
                top = pc.top + pb.top + pv.top + topbarHeight;

                if ($.browser.mozilla)
                    container.css({ left: left, top: top }).show();
                else
                    container.css({ left: left, top: top + 1 }).show();
                if (column.textField) editParm.text = g._getValueByName(rowdata, column.textField);
                var editorInput = g._createEditor(editor, container, editParm, width, height);
                g.editor = { editing: true, editor: editor, input: editorInput, editParm: editParm, container: container };
                g.unbind('sysEndEdit');
                g.bind('sysEndEdit', function ()
                {
                    var newValue = editor.getValue(editorInput, editParm);
                    if (newValue != currentdata)
                    {
                        $(rowcell).addClass("l-grid-row-cell-edited");
                        g.changedCells[rowid + "_" + column['__id']] = true; 
                        editParm.value = newValue;
                        if (column.textField && editor.getText)
                        {
                            editParm.text = editor.getText(editorInput, editParm);
                        }
                        if (editor.onChange) editor.onChange(editParm);
                        if (g._checkEditAndUpdateCell(editParm))
                        {
                            if (editor.onChanged) editor.onChanged(editParm);
                        }
                    }
                });
            }
        },
        _checkEditAndUpdateCell: function (editParm)
        {
            var g = this, p = this.options;
            if (g.trigger('beforeSubmitEdit', [editParm]) == false) return false;
            var column = editParm.column;
            if (editParm.text && column.textField) g._setValueByName(editParm.record, column.textField, editParm.text);
            g.updateCell(column, editParm.value, editParm.record);
            if (column.render || g.enabledTotal()) g.reRender({ column: column });
            g.reRender({ rowdata: editParm.record });
            return true;
        },
        _getCurrentPageData: function (source)
        {
            var g = this, p = this.options;
            var data = {};
            data[p.root] = [];
            if (!source || !source[p.root] || !source[p.root].length)
            {
                data[p.record] = 0;
                return data;
            }
            data[p.record] = source[p.root].length;
            if (!p.newPage) p.newPage = 1;
            for (i = (p.newPage - 1) * p.pageSize; i < source[p.root].length && i < p.newPage * p.pageSize; i++)
            {
                data[p.root].push(source[p.root][i]);
            }
            return data;
        },
        //比较某一列两个数据
        _compareData: function (data1, data2, columnName, columnType)
        {
            var g = this, p = this.options;
            var val1 = data1[columnName], val2 = data2[columnName];
            if (val1 == null && val2 != null) return 1;
            else if (val1 == null && val2 == null) return 0;
            else if (val1 != null && val2 == null) return -1;
            if (p.sorters[columnType])
                return p.sorters[columnType].call(g, val1, val2);
            else
                return val1 < val2 ? -1 : val1 > val2 ? 1 : 0;
        },
        _getTotalCellContent: function (column, data)
        {
            var g = this, p = this.options;
            var totalsummaryArr = [];
            if (column.totalSummary)
            {
                var isExist = function (type)
                {
                    for (var i = 0; i < types.length; i++)
                        if (types[i].toLowerCase() == type.toLowerCase()) return true;
                    return false;
                };
                var sum = 0, count = 0, avg = 0;
                var max = parseFloat(data[0][column.name]);
                var min = parseFloat(data[0][column.name]);
                for (var i = 0; i < data.length; i++)
                {
                    count += 1;
                    var value = parseFloat(data[i][column.name]);
                    if (!value) continue;
                    sum += value;
                    if (value > max) max = value;
                    if (value < min) min = value;
                }
                avg = sum * 1.0 / data.length;
                if (column.totalSummary.render)
                {
                    var renderhtml = column.totalSummary.render({
                        sum: sum,
                        count: count,
                        avg: avg,
                        min: min,
                        max: max
                    }, column, g.data);
                    totalsummaryArr.push(renderhtml);
                }
                else if (column.totalSummary.type)
                {
                    var types = column.totalSummary.type.split(',');
                    if (isExist('sum'))
                        totalsummaryArr.push("<div>Sum=" + sum.toFixed(2) + "</div>");
                    if (isExist('count'))
                        totalsummaryArr.push("<div>Count=" + count + "</div>");
                    if (isExist('max'))
                        totalsummaryArr.push("<div>Max=" + max.toFixed(2) + "</div>");
                    if (isExist('min'))
                        totalsummaryArr.push("<div>Min=" + min.toFixed(2) + "</div>");
                    if (isExist('avg'))
                        totalsummaryArr.push("<div>Avg=" + avg.toFixed(2) + "</div>");
                }
            }
            return totalsummaryArr.join('');
        },
        _getTotalSummaryHtml: function (data, classCssName, frozen)
        {
            var g = this, p = this.options;
            var totalsummaryArr = [];
            if (classCssName)
                totalsummaryArr.push('<tr class="l-grid-totalsummary ' + classCssName + '">');
            else
                totalsummaryArr.push('<tr class="l-grid-totalsummary">');
            $(g.columns).each(function (columnindex, column)
            {
                if (this.frozen != frozen) return;
                //如果是行序号(系统列)
                if (this.isrownumber)
                {
                    totalsummaryArr.push('<td class="l-grid-totalsummary-cell l-grid-totalsummary-cell-rownumbers" style="width:' + this.width + 'px"><div>&nbsp;</div></td>');
                    return;
                }
                //如果是复选框(系统列)
                if (this.ischeckbox)
                {
                    totalsummaryArr.push('<td class="l-grid-totalsummary-cell l-grid-totalsummary-cell-checkbox" style="width:' + this.width + 'px"><div>&nbsp;</div></td>');
                    return;
                }
                    //如果是明细列(系统列)
                else if (this.isdetail)
                {
                    totalsummaryArr.push('<td class="l-grid-totalsummary-cell l-grid-totalsummary-cell-detail" style="width:' + this.width + 'px"><div>&nbsp;</div></td>');
                    return;
                }
                totalsummaryArr.push('<td class="l-grid-totalsummary-cell');
                if (this.islast)
                    totalsummaryArr.push(" l-grid-totalsummary-cell-last");
                totalsummaryArr.push('" ');
                totalsummaryArr.push('id="' + g.id + "|total" + g.totalNumber + "|" + column.__id + '" ');
                totalsummaryArr.push('width="' + this._width + '" ');
                columnname = this.columnname;
                if (columnname)
                {
                    totalsummaryArr.push('columnname="' + columnname + '" ');
                }
                totalsummaryArr.push('columnindex="' + columnindex + '" ');
                totalsummaryArr.push('><div class="l-grid-totalsummary-cell-inner"');
                if (column.align)
                    totalsummaryArr.push(' style="text-Align:' + column.align + ';"');
                totalsummaryArr.push('>');
                totalsummaryArr.push(g._getTotalCellContent(column, data));
                totalsummaryArr.push('</div></td>');
            });
            totalsummaryArr.push('</tr>');
            if (!frozen) g.totalNumber++;
            return totalsummaryArr.join('');
        },
        _bulidTotalSummary: function (frozen)
        {
            var g = this, p = this.options;
            if (!g.isTotalSummary()) return false;
            if (!g.currentData || g.currentData[p.root].length == 0) return false;
            var totalRow = $(g._getTotalSummaryHtml(g.currentData[p.root], null, frozen));
            $("tbody:first", frozen ? g.f.gridbody : g.gridbody).append(totalRow);
        },
        _buildPager: function ()
        {
            var g = this, p = this.options;
            $('.pcontrol input', g.toolbar).val(p.page);
            if (!p.pageCount) p.pageCount = 1;
            $('.pcontrol span', g.toolbar).html(p.pageCount);
            var r1 = parseInt((p.page - 1) * p.pageSize) + 1.0;
            var r2 = parseInt(r1) + parseInt(p.pageSize) - 1;
            if (!p.total) p.total = 0;
            if (p.total < r2) r2 = p.total;
            if (!p.total) r1 = r2 = 0;
            if (r1 < 0) r1 = 0;
            if (r2 < 0) r2 = 0;
            var stat = p.pageStatMessage;
            stat = stat.replace(/{from}/, r1);
            stat = stat.replace(/{to}/, r2);
            stat = stat.replace(/{total}/, p.total);
            stat = stat.replace(/{pagesize}/, p.pageSize);
            $('.l-bar-text', g.toolbar).html(stat);
            if (!p.total)
            {
                $(".l-bar-btnfirst span,.l-bar-btnprev span,.l-bar-btnnext span,.l-bar-btnlast span", g.toolbar)
                    .addClass("l-disabled");
            }
            if (p.page == 1)
            {
                $(".l-bar-btnfirst span", g.toolbar).addClass("l-disabled");
                $(".l-bar-btnprev span", g.toolbar).addClass("l-disabled");
            }
            else if (p.page > p.pageCount && p.pageCount > 0)
            {
                $(".l-bar-btnfirst span", g.toolbar).removeClass("l-disabled");
                $(".l-bar-btnprev span", g.toolbar).removeClass("l-disabled");
            }
            if (p.page == p.pageCount)
            {
                $(".l-bar-btnlast span", g.toolbar).addClass("l-disabled");
                $(".l-bar-btnnext span", g.toolbar).addClass("l-disabled");
            }
            else if (p.page < p.pageCount && p.pageCount > 0)
            {
                $(".l-bar-btnlast span", g.toolbar).removeClass("l-disabled");
                $(".l-bar-btnnext span", g.toolbar).removeClass("l-disabled");
            }
        },
        _getRowIdByDomId: function (domid)
        {
            var ids = domid.split('|');
            var rowid = ids[2];
            return rowid;
        },
        _getRowByDomId: function (domid)
        {
            return this.records[this._getRowIdByDomId(domid)];
        },
        //在外部点击的时候，判断是否在编辑状态，比如弹出的层点击的，如果自定义了编辑器，而且生成的层没有包括在grid内部，建议重载
        _isEditing: function (jobjs)
        {
            return jobjs.hasClass("l-box-dateeditor") || jobjs.hasClass("l-box-select");
        },
        _getSrcElementByEvent: function (e)
        {
            var g = this;
            var obj = (e.target || e.srcElement);
            var jobjs = $(obj).parents().add(obj);
            var fn = function (parm)
            {
                for (var i = 0, l = jobjs.length; i < l; i++)
                {
                    if (typeof parm == "string")
                    {
                        if ($(jobjs[i]).hasClass(parm)) return jobjs[i];
                    }
                    else if (typeof parm == "object")
                    {
                        if (jobjs[i] == parm) return jobjs[i];
                    }
                }
                return null;
            };
            if (fn("l-grid-editor")) return { editing: true, editor: fn("l-grid-editor") };
            if (jobjs.index(this.element) == -1)
            {
                if (g._isEditing(jobjs)) return { editing: true };
                else return { out: true };
            }
            var indetail = false;
            if (jobjs.hasClass("l-grid-detailpanel") && g.detailrows)
            {
                for (var i = 0, l = g.detailrows.length; i < l; i++)
                {
                    if (jobjs.index(g.detailrows[i]) != -1)
                    {
                        indetail = true;
                        break;
                    }
                }
            }
            var r = {
                grid: fn("l-panel"),
                indetail: indetail,
                frozen: fn(g.gridview1[0]) ? true : false,
                header: fn("l-panel-header"), //标题
                gridheader: fn("l-grid-header"), //表格头 
                gridbody: fn("l-grid-body"),
                total: fn("l-panel-bar-total"), //总汇总 
                popup: fn("l-grid-popup"),
                toolbar: fn("l-panel-bar")
            };
            if (r.gridheader)
            {
                r.hrow = fn("l-grid-hd-row");
                r.hcell = fn("l-grid-hd-cell");
                r.hcelltext = fn("l-grid-hd-cell-text");
                r.checkboxall = fn("l-grid-hd-cell-checkbox");
                if (r.hcell)
                {
                    var columnid = r.hcell.id.split('|')[2];
                    r.column = g._columns[columnid];
                }
            }
            if (r.gridbody)
            {
                r.row = fn("l-grid-row");
                r.cell = fn("l-grid-row-cell");
                r.checkbox = fn("l-grid-row-cell-btn-checkbox");
                r.groupbtn = fn("l-grid-group-togglebtn");
                r.grouprow = fn("l-grid-grouprow");
                r.detailbtn = fn("l-grid-row-cell-detailbtn");
                r.detailrow = fn("l-grid-detailpanel");
                r.totalrow = fn("l-grid-totalsummary");
                r.totalcell = fn("l-grid-totalsummary-cell");
                r.rownumberscell = $(r.cell).hasClass("l-grid-row-cell-rownumbers") ? r.cell : null;
                r.detailcell = $(r.cell).hasClass("l-grid-row-cell-detail") ? r.cell : null;
                r.checkboxcell = $(r.cell).hasClass("l-grid-row-cell-checkbox") ? r.cell : null;
                r.treelink = fn("l-grid-tree-link");
                r.editor = fn("l-grid-editor");
                if (r.row) r.data = this._getRowByDomId(r.row.id);
                if (r.cell) r.editing = $(r.cell).hasClass("l-grid-row-cell-editing");
                if (r.editor) r.editing = true;
                if (r.editing) r.out = false;
            }
            if (r.toolbar)
            {
                r.first = fn("l-bar-btnfirst");
                r.last = fn("l-bar-btnlast");
                r.next = fn("l-bar-btnnext");
                r.prev = fn("l-bar-btnprev");
                r.load = fn("l-bar-btnload");
                r.button = fn("l-bar-button");
            }

            return r;
        },
        _setEvent: function ()
        {
            var g = this, p = this.options;
            g.grid.bind("mousedown.grid", function (e)
            {
                g._onMouseDown.call(g, e);
            });
            g.grid.bind("dblclick.grid", function (e)
            {
                g._onDblClick.call(g, e);
            });
            g.grid.bind("contextmenu.grid", function (e)
            {
                return g._onContextmenu.call(g, e);
            });
            $(document).bind("mouseup.grid", function (e)
            {
                g._onMouseUp.call(g, e);
            });
            $(document).bind("click.grid", function (e)
            {
                g._onClick.call(g, e);
            });
            $(window).bind("resize.grid", function (e)
            {
                g._onResize.call(g);
            });
            $(document).bind("keydown.grid", function (e)
            {
                if (e.ctrlKey) g.ctrlKey = true;
            });
            $(document).bind("keyup.grid", function (e)
            {
                delete g.ctrlKey;
            });
            //表体 - 滚动联动事件 
            g.gridbody.bind('scroll.grid', function ()
            {
                var scrollLeft = g.gridbody.scrollLeft();
                var scrollTop = g.gridbody.scrollTop();
                if (scrollLeft != null)
                    g.gridheader[0].scrollLeft = scrollLeft;
                if (scrollTop != null)
                    g.f.gridbody[0].scrollTop = scrollTop;
                g.endEdit();
                g.trigger('SysGridHeightChanged');
            });
            //工具条 - 切换每页记录数事件
            $('select', g.toolbar).change(function ()
            {
                if (g.isDataChanged && p.dataAction != "local" && !confirm(p.isContinueByDataChanged))
                    return false;
                p.newPage = 1;
                p.pageSize = this.value;
                g.loadData(p.where);
            });
            //工具条 - 切换当前页事件
            $('span.pcontrol :text', g.toolbar).blur(function (e)
            {
                g.changePage('input');
            });
            $("div.l-bar-button", g.toolbar).hover(function ()
            {
                $(this).addClass("l-bar-button-over");
            }, function ()
            {
                $(this).removeClass("l-bar-button-over");
            });
            //列拖拽支持
            if ($.fn.ligerDrag && p.colDraggable)
            {
                g.colDroptip = $("<div class='l-drag-coldroptip' style='display:none'><div class='l-drop-move-up'></div><div class='l-drop-move-down'></div></div>").appendTo('body');
                g.gridheader.add(g.f.gridheader).ligerDrag({
                    revert: true, animate: false,
                    proxyX: 0, proxyY: 0,
                    proxy: function (draggable, e)
                    {
                        var src = g._getSrcElementByEvent(e);
                        if (src.hcell && src.column)
                        {
                            var content = $(".l-grid-hd-cell-text:first", src.hcell).html();
                            var proxy = $("<div class='l-drag-proxy' style='display:none'><div class='l-drop-icon l-drop-no'></div></div>").appendTo('body');
                            proxy.append(content);
                            return proxy;
                        }
                    },
                    onRevert: function () { return false; },
                    onRendered: function ()
                    {
                        this.set('cursor', 'default');
                        g.children[this.id] = this;
                    },
                    onStartDrag: function (current, e)
                    {
                        if (e.button == 2) return false;
                        if (g.colresizing) return false;
                        this.set('cursor', 'default');
                        var src = g._getSrcElementByEvent(e);
                        if (!src.hcell || !src.column || src.column.issystem || src.hcelltext) return false;
                        if ($(src.hcell).css('cursor').indexOf('resize') != -1) return false;
                        this.draggingColumn = src.column;
                        g.coldragging = true;

                        var gridOffset = g.grid.offset();
                        this.validRange = {
                            top: gridOffset.top,
                            bottom: gridOffset.top + g.gridheader.height(),
                            left: gridOffset.left - 10,
                            right: gridOffset.left + g.grid.width() + 10
                        };
                    },
                    onDrag: function (current, e)
                    {
                        this.set('cursor', 'default');
                        var column = this.draggingColumn;
                        if (!column) return false;
                        if (g.colresizing) return false;
                        if (g.colDropIn == null)
                            g.colDropIn = -1;
                        var pageX = e.pageX;
                        var pageY = e.pageY;
                        var visit = false;
                        var gridOffset = g.grid.offset();
                        var validRange = this.validRange;
                        if (pageX < validRange.left || pageX > validRange.right
                            || pageY > validRange.bottom || pageY < validRange.top)
                        {
                            g.colDropIn = -1;
                            g.colDroptip.hide();
                            this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes").addClass("l-drop-no");
                            return;
                        }
                        for (var colid in g._columns)
                        {
                            var col = g._columns[colid];
                            if (column == col)
                            {
                                visit = true;
                                continue;
                            }
                            if (col.issystem) continue;
                            var sameLevel = col['__level'] == column['__level'];
                            var isAfter = !sameLevel ? false : visit ? true : false;
                            if (column.frozen != col.frozen) isAfter = col.frozen ? false : true;
                            if (g.colDropIn != -1 && g.colDropIn != colid) continue;
                            var cell = document.getElementById(col['__domid']);
                            var offset = $(cell).offset();
                            var range = {
                                top: offset.top,
                                bottom: offset.top + $(cell).height(),
                                left: offset.left - 10,
                                right: offset.left + 10
                            };
                            if (isAfter)
                            {
                                var cellwidth = $(cell).width();
                                range.left += cellwidth;
                                range.right += cellwidth;
                            }
                            if (pageX > range.left && pageX < range.right && pageY > range.top && pageY < range.bottom)
                            {
                                var height = p.headerRowHeight;
                                if (col['__rowSpan']) height *= col['__rowSpan'];
                                g.colDroptip.css({
                                    left: range.left + 5,
                                    top: range.top - 9,
                                    height: height + 9 * 2
                                }).show();
                                g.colDropIn = colid;
                                g.colDropDir = isAfter ? "right" : "left";
                                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no").addClass("l-drop-yes");
                                break;
                            }
                            else if (g.colDropIn != -1)
                            {
                                g.colDropIn = -1;
                                g.colDroptip.hide();
                                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes").addClass("l-drop-no");
                            }
                        }
                    },
                    onStopDrag: function (current, e)
                    {
                        var column = this.draggingColumn;
                        g.coldragging = false;
                        if (g.colDropIn != -1)
                        {
                            g.changeCol.ligerDefer(g, 0, [column, g.colDropIn, g.colDropDir == "right"]);
                            g.colDropIn = -1;
                        }
                        g.colDroptip.hide();
                        this.set('cursor', 'default');
                    }
                });
            }
            //行拖拽支持
            if ($.fn.ligerDrag && p.rowDraggable)
            {
                g.rowDroptip = $("<div class='l-drag-rowdroptip' style='display:none'></div>").appendTo('body');
                g.gridbody.add(g.f.gridbody).ligerDrag({
                    revert: true, animate: false,
                    proxyX: 0, proxyY: 0,
                    proxy: function (draggable, e)
                    {
                        var src = g._getSrcElementByEvent(e);
                        if (src.row)
                        {
                            var content = p.draggingMessage.replace(/{count}/, draggable.draggingRows ? draggable.draggingRows.length : 1);
                            if (p.rowDraggingRender)
                            {
                                content = p.rowDraggingRender(draggable.draggingRows, draggable, g);
                            }
                            var proxy = $("<div class='l-drag-proxy' style='display:none'><div class='l-drop-icon l-drop-no'></div>" + content + "</div>").appendTo('body');
                            return proxy;
                        }
                    },
                    onRevert: function () { return false; },
                    onRendered: function ()
                    {
                        this.set('cursor', 'default');
                        g.children[this.id] = this;
                    },
                    onStartDrag: function (current, e)
                    {
                        if (e.button == 2) return false;
                        if (g.colresizing) return false;
                        if (!g.columns.length) return false;
                        this.set('cursor', 'default');
                        var src = g._getSrcElementByEvent(e);
                        if (!src.cell || !src.data || src.checkbox) return false;
                        var ids = src.cell.id.split('|');
                        var column = g._columns[ids[ids.length - 1]];
                        if (src.rownumberscell || src.detailcell || src.checkboxcell || column == g.columns[0])
                        {
                            if (g.enabledCheckbox())
                            {
                                this.draggingRows = g.getSelecteds();
                                if (!this.draggingRows || !this.draggingRows.length) return false;
                            }
                            else
                            {
                                this.draggingRows = [src.data];
                            }
                            this.draggingRow = src.data;
                            this.set('cursor', 'move');
                            g.rowdragging = true;
                            this.validRange = {
                                top: g.gridbody.offset().top,
                                bottom: g.gridbody.offset().top + g.gridbody.height(),
                                left: g.grid.offset().left - 10,
                                right: g.grid.offset().left + g.grid.width() + 10
                            };
                        }
                        else
                        {
                            return false;
                        }
                    },
                    onDrag: function (current, e)
                    {
                        var rowdata = this.draggingRow;
                        if (!rowdata) return false;
                        var rows = this.draggingRows ? this.draggingRows : [rowdata];
                        if (g.colresizing) return false;
                        if (g.rowDropIn == null) g.rowDropIn = -1;
                        var pageX = e.pageX;
                        var pageY = e.pageY;
                        var visit = false;
                        var validRange = this.validRange;
                        if (pageX < validRange.left || pageX > validRange.right
                            || pageY > validRange.bottom || pageY < validRange.top)
                        {
                            g.rowDropIn = -1;
                            g.rowDroptip.hide();
                            this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes l-drop-add").addClass("l-drop-no");
                            return;
                        }
                        for (var i in g.rows)
                        {
                            var rd = g.rows[i];
                            var rowid = rd['__id'];
                            if (rowdata == rd) visit = true;
                            if ($.inArray(rd, rows) != -1) continue;
                            var isAfter = visit ? true : false;
                            if (g.rowDropIn != -1 && g.rowDropIn != rowid) continue;
                            var rowobj = g.getRowObj(rowid);
                            var offset = $(rowobj).offset();
                            var range = {
                                top: offset.top - 4,
                                bottom: offset.top + $(rowobj).height() + 4,
                                left: g.grid.offset().left,
                                right: g.grid.offset().left + g.grid.width()
                            };
                            if (pageX > range.left && pageX < range.right && pageY > range.top && pageY < range.bottom)
                            {
                                var lineTop = offset.top;
                                if (isAfter) lineTop += $(rowobj).height();
                                g.rowDroptip.css({
                                    left: range.left,
                                    top: lineTop,
                                    width: range.right - range.left
                                }).show();
                                g.rowDropIn = rowid;
                                g.rowDropDir = isAfter ? "bottom" : "top";
                                if (p.tree && pageY > range.top + 5 && pageY < range.bottom - 5)
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-yes").addClass("l-drop-add");
                                    g.rowDroptip.hide();
                                    g.rowDropInParent = true;
                                }
                                else
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-add").addClass("l-drop-yes");
                                    g.rowDroptip.show();
                                    g.rowDropInParent = false;
                                }
                                break;
                            }
                            else if (g.rowDropIn != -1)
                            {
                                g.rowDropIn = -1;
                                g.rowDropInParent = false;
                                g.rowDroptip.hide();
                                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes  l-drop-add").addClass("l-drop-no");
                            }
                        }
                    },
                    onStopDrag: function (current, e)
                    {
                        var rows = this.draggingRows;
                        g.rowdragging = false;
                        for (var i = 0; i < rows.length; i++)
                        {
                            var children = rows[i].children;
                            if (children)
                            {
                                rows = $.grep(rows, function (node, i)
                                {
                                    var isIn = $.inArray(node, children) == -1;
                                    return isIn;
                                });
                            }
                        }
                        if (g.rowDropIn != -1)
                        {
                            if (p.tree)
                            {
                                var neardata, prow;
                                if (g.rowDropInParent)
                                {
                                    prow = g.getRow(g.rowDropIn);
                                }
                                else
                                {
                                    neardata = g.getRow(g.rowDropIn);
                                    prow = g.getParent(neardata);
                                }
                                g.appendRange(rows, prow, neardata, g.rowDropDir != "bottom");
                                g.trigger('rowDragDrop', {
                                    rows: rows,
                                    parent: prow,
                                    near: neardata,
                                    after: g.rowDropDir == "bottom"
                                });
                            }
                            else
                            {
                                g.moveRange(rows, g.rowDropIn, g.rowDropDir == "bottom");
                                g.trigger('rowDragDrop', {
                                    rows: rows,
                                    parent: prow,
                                    near: g.getRow(g.rowDropIn),
                                    after: g.rowDropDir == "bottom"
                                });
                            }

                            g.rowDropIn = -1;
                        }
                        g.rowDroptip.hide();
                        this.set('cursor', 'default');
                    }
                });
            }
        },
        _onRowOver: function (rowParm, over)
        {
            if (l.draggable.dragging) return;
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var methodName = over ? "addClass" : "removeClass";
            if (g.enabledFrozen())
                $(g.getRowObj(rowdata, true))[methodName](p.mouseoverRowCssClass);
            $(g.getRowObj(rowdata, false))[methodName](p.mouseoverRowCssClass);
        },
        _onMouseUp: function (e)
        {
            var g = this, p = this.options;
            if (l.draggable.dragging)
            {
                var src = g._getSrcElementByEvent(e);

                //drop in header cell
                if (src.hcell && src.column)
                {
                    g.trigger('dragdrop', [{ type: 'header', column: src.column, cell: src.hcell }, e]);
                }
                else if (src.row)
                {
                    g.trigger('dragdrop', [{ type: 'row', record: src.data, row: src.row }, e]);
                }
            }
        },
        _onMouseDown: function (e)
        {
            var g = this, p = this.options;
        },
        _onContextmenu: function (e)
        {
            var g = this, p = this.options;
            var src = g._getSrcElementByEvent(e);
            if (src.row)
            {
                if (p.whenRClickToSelect)
                    g.select(src.data);
                if (g.hasBind('contextmenu'))
                {
                    return g.trigger('contextmenu', [{ data: src.data, rowindex: src.data['__index'], row: src.row }, e]);
                }
            }
            else if (src.hcell)
            {
                if (!p.allowHideColumn) return true;
                var columnindex = $(src.hcell).attr("columnindex");
                if (columnindex == undefined) return true;
                var left = (e.pageX - g.body.offset().left + parseInt(g.body[0].scrollLeft));
                if (columnindex == g.columns.length - 1) left -= 50;
                g.popup.css({ left: left, top: g.gridheader.height() + 1 });
                g.popup.toggle();
                return false;
            }
        },
        _onDblClick: function (e)
        {
            var g = this, p = this.options;
            var src = g._getSrcElementByEvent(e);
            if (src.row)
            {
                g.trigger('dblClickRow', [src.data, src.data['__id'], src.row]);
            }
        },
        _onClick: function (e)
        {
            var obj = (e.target || e.srcElement);
            var g = this, p = this.options;
            var src = g._getSrcElementByEvent(e);
            if (src.out)
            {
                if (g.editor.editing && !$.ligerui.win.masking) g.endEdit();
                if (p.allowHideColumn) g.popup.hide();
                return;
            }
            if (src.indetail || src.editing)
            {
                return;
            }
            if (g.editor.editing)
            {
                g.endEdit();
            }
            if (p.allowHideColumn)
            {
                if (!src.popup)
                {
                    g.popup.hide();
                }
            }
            if (src.checkboxall) //复选框全选
            {
                var row = $(src.hrow);
                var uncheck = row.hasClass("l-checked");
                if (g.trigger('beforeCheckAllRow', [!uncheck, g.element]) == false) return false;
                if (uncheck)
                {
                    row.removeClass("l-checked");
                }
                else
                {
                    row.addClass("l-checked");
                }
                g.selected = [];
                for (var rowid in g.records)
                {
                    if (uncheck)
                        g.unselect(g.records[rowid]);
                    else
                        g.select(g.records[rowid]);
                }
                g.trigger('checkAllRow', [!uncheck, g.element]);
            }
            else if (src.hcelltext) //排序
            {
                var hcell = $(src.hcelltext).parent().parent();
                if (!p.enabledSort || !src.column) return;
                if (src.column.isSort == false) return;
                if (p.url && p.dataAction != "local" && g.isDataChanged && !confirm(p.isContinueByDataChanged)) return;
                var sort = $(".l-grid-hd-cell-sort:first", hcell);
                //modify by songxf at 2013-7-31  给column增加sortname参数，解决列英文名发生改变时无法完成排序的问题
                if(src.column.sortname){
                	var columnName=src.column.sortname;
                }else{
                	var columnName = src.column.name;
                }
                //modify end
                if (!columnName) return;
                if (sort.length > 0)
                {
                    if (sort.hasClass("l-grid-hd-cell-sort-asc"))
                    {
                        sort.removeClass("l-grid-hd-cell-sort-asc").addClass("l-grid-hd-cell-sort-desc");
                        hcell.removeClass("l-grid-hd-cell-asc").addClass("l-grid-hd-cell-desc");
                        g.changeSort(columnName, 'desc');
                    }
                    else if (sort.hasClass("l-grid-hd-cell-sort-desc"))
                    {
                        sort.removeClass("l-grid-hd-cell-sort-desc").addClass("l-grid-hd-cell-sort-asc");
                        hcell.removeClass("l-grid-hd-cell-desc").addClass("l-grid-hd-cell-asc");
                        g.changeSort(columnName, 'asc');
                    }
                }
                else
                {
                    hcell.removeClass("l-grid-hd-cell-desc").addClass("l-grid-hd-cell-asc");
                    $(src.hcelltext).after("<span class='l-grid-hd-cell-sort l-grid-hd-cell-sort-asc'>&nbsp;&nbsp;</span>");
                    g.changeSort(columnName, 'asc');
                }
                $(".l-grid-hd-cell-sort", g.gridheader).add($(".l-grid-hd-cell-sort", g.f.gridheader)).not($(".l-grid-hd-cell-sort:first", hcell)).remove();
            }
                //明细
            else if (src.detailbtn && p.detail)
            {
                var item = src.data;
                var row = $([g.getRowObj(item, false)]);
                if (g.enabledFrozen()) row = row.add(g.getRowObj(item, true));
                var rowid = item['__id'];
                if ($(src.detailbtn).hasClass("l-open"))
                {
                    if (p.detail.onCollapse)
                        p.detail.onCollapse(item, $(".l-grid-detailpanel-inner:first", nextrow)[0]);
                    row.next("tr.l-grid-detailpanel").hide();
                    $(src.detailbtn).removeClass("l-open");
                }
                else
                {
                    var nextrow = row.next("tr.l-grid-detailpanel");
                    if (nextrow.length > 0)
                    {
                        nextrow.show();
                        if (p.detail.onExtend)
                            p.detail.onExtend(item, $(".l-grid-detailpanel-inner:first", nextrow)[0]);
                        $(src.detailbtn).addClass("l-open");
                        g.trigger('SysGridHeightChanged');
                        return;
                    }
                    $(src.detailbtn).addClass("l-open");
                    var frozenColNum = 0;
                    for (var i = 0; i < g.columns.length; i++)
                        if (g.columns[i].frozen) frozenColNum++;
                    var detailRow = $("<tr class='l-grid-detailpanel'><td><div class='l-grid-detailpanel-inner' style='display:none'></div></td></tr>");
                    var detailFrozenRow = $("<tr class='l-grid-detailpanel'><td><div class='l-grid-detailpanel-inner' style='display:none'></div></td></tr>");
                    detailRow.attr("id", g.id + "|detail|" + rowid);
                    g.detailrows = g.detailrows || [];
                    g.detailrows.push(detailRow[0]);
                    g.detailrows.push(detailFrozenRow[0]);
                    var detailRowInner = $("div:first", detailRow);
                    detailRowInner.parent().attr("colSpan", g.columns.length - frozenColNum);
                    row.eq(0).after(detailRow);
                    if (frozenColNum > 0)
                    {
                        detailFrozenRow.find("td:first").attr("colSpan", frozenColNum);
                        row.eq(1).after(detailFrozenRow);
                    }
                    if (p.detail.onShowDetail)
                    {
                        p.detail.onShowDetail(item, detailRowInner[0], function ()
                        {
                            g.trigger('SysGridHeightChanged');
                        });
                        $("div:first", detailFrozenRow).add(detailRowInner).show().height(p.detail.height || p.detailHeight);
                    }
                    else if (p.detail.render)
                    {
                        detailRowInner.append(p.detail.render());
                        detailRowInner.show();
                    }
                    g.trigger('SysGridHeightChanged');
                }
            }
            else if (src.groupbtn)
            {
                var grouprow = $(src.grouprow);
                var opening = true;
                if ($(src.groupbtn).hasClass("l-grid-group-togglebtn-close"))
                {
                    $(src.groupbtn).removeClass("l-grid-group-togglebtn-close");

                    if (grouprow.hasClass("l-grid-grouprow-last"))
                    {
                        $("td:first", grouprow).width('auto');
                    }
                }
                else
                {
                    opening = false;
                    $(src.groupbtn).addClass("l-grid-group-togglebtn-close");
                    if (grouprow.hasClass("l-grid-grouprow-last"))
                    {
                        $("td:first", grouprow).width(g.gridtablewidth);
                    }
                }
                var currentRow = grouprow.next(".l-grid-row,.l-grid-totalsummary-group,.l-grid-detailpanel");
                while (true)
                {
                    if (currentRow.length == 0) break;
                    if (opening)
                    {
                        currentRow.show();
                        //如果是明细展开的行，并且之前的状态已经是关闭的，隐藏之
                        if (currentRow.hasClass("l-grid-detailpanel") && !currentRow.prev().find("td.l-grid-row-cell-detail:first span.l-grid-row-cell-detailbtn:first").hasClass("l-open"))
                        {
                            currentRow.hide();
                        }
                    }
                    else
                    {
                        currentRow.hide();
                    }
                    currentRow = currentRow.next(".l-grid-row,.l-grid-totalsummary-group,.l-grid-detailpanel");
                }
                g.trigger(opening ? 'groupExtend' : 'groupCollapse');
                g.trigger('SysGridHeightChanged');
            }
                //树 - 伸展/收缩节点
            else if (src.treelink)
            {
                g.toggle(src.data);
            }
            else if (src.row && g.enabledCheckbox()) //复选框选择行
            {
                //复选框
                var selectRowButtonOnly = p.selectRowButtonOnly ? true : false;
                if (p.enabledEdit) selectRowButtonOnly = true;
                if (src.checkbox || !selectRowButtonOnly)
                {
                    var row = $(src.row);
                    var uncheck = row.hasClass("l-selected");
                    if (g.trigger('beforeCheckRow', [!uncheck, src.data, src.data['__id'], src.row]) == false)
                        return false;
                    var met = uncheck ? 'unselect' : 'select';
                    g[met](src.data);
                    if (p.tree && p.autoCheckChildren)
                    {
                        var children = g.getChildren(src.data, true);
                        for (var i = 0, l = children.length; i < l; i++)
                        {
                            g[met](children[i]);
                        }
                    }
                    g.trigger('checkRow', [!uncheck, src.data, src.data['__id'], src.row]);
                }
                if (!src.checkbox && src.cell && p.enabledEdit && p.clickToEdit)
                {
                    g._applyEditor(src.cell);
                }
            }
            else if (src.row && !g.enabledCheckbox())
            {
                if (src.cell && p.enabledEdit && p.clickToEdit)
                {
                    g._applyEditor(src.cell);
                }

                //选择行
                if ($(src.row).hasClass("l-selected"))
                {
                    if (!p.allowUnSelectRow)
                    {
                        $(src.row).addClass("l-selected-again");
                        return;
                    }
                    g.unselect(src.data);
                }
                else
                {
                    g.select(src.data);
                }
            }
            else if (src.toolbar)
            {
                if (src.first)
                {
                    if (g.trigger('toFirst', [g.element]) == false) return false;
                    g.changePage('first');
                }
                else if (src.prev)
                {
                    if (g.trigger('toPrev', [g.element]) == false) return false;
                    g.changePage('prev');
                }
                else if (src.next)
                {
                    if (g.trigger('toNext', [g.element]) == false) return false;
                    g.changePage('next');
                }
                else if (src.last)
                {
                    if (g.trigger('toLast', [g.element]) == false) return false;
                    g.changePage('last');
                }
                else if (src.load)
                {
                    if ($("span", src.load).hasClass("l-disabled")) return false;
                    if (g.trigger('reload', [g.element]) == false) return false;
                    if (p.url && g.isDataChanged && !confirm(p.isContinueByDataChanged))
                        return false;
                    g.loadData(p.where);
                }
            }
        },
        select: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var rowid = rowdata['__id'];
            var rowobj = g.getRowObj(rowid);
            var rowobj1 = g.getRowObj(rowid, true);
            if (!g.enabledCheckbox() && !g.ctrlKey) //单选
            {
                for (var i in g.selected)
                {
                    var o = g.selected[i];
                    if (o['__id'] in g.records)
                    {
                        $(g.getRowObj(o)).removeClass("l-selected l-selected-again");
                        if (g.enabledFrozen())
                            $(g.getRowObj(o, true)).removeClass("l-selected l-selected-again");
                    }
                }
                g.selected = [];
            }
            if (rowobj) $(rowobj).addClass("l-selected");
            if (rowobj1) $(rowobj1).addClass("l-selected");
            g.selected[g.selected.length] = rowdata;
            g.trigger('selectRow', [rowdata, rowid, rowobj]);
        },
        unselect: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            var rowid = rowdata['__id'];
            var rowobj = g.getRowObj(rowid);
            var rowobj1 = g.getRowObj(rowid, true);
            $(rowobj).removeClass("l-selected l-selected-again");
            if (g.enabledFrozen())
                $(rowobj1).removeClass("l-selected l-selected-again");
            g._removeSelected(rowdata);
            g.trigger('unSelectRow', [rowdata, rowid, rowobj]);
        },
        isSelected: function (rowParm)
        {
            var g = this, p = this.options;
            var rowdata = g.getRow(rowParm);
            for (var i in g.selected)
            {
                if (g.selected[i] == rowdata) return true;
            }
            return false;
        },
        _onResize: function ()
        {
            var g = this, p = this.options;
            if (p.height && p.height != 'auto')
            {
                var windowHeight = $(window).height();
                //if(g.windowHeight != undefined && g.windowHeight == windowHeight) return;

                var h = 0;
                var parentHeight = null;
                if (typeof (p.height) == "string" && p.height.indexOf('%') > 0)
                {
                    var gridparent = g.grid.parent();
                    if (p.inWindow)
                    {
                        parentHeight = windowHeight;
                        parentHeight -= parseInt($('body').css('paddingTop'));
                        parentHeight -= parseInt($('body').css('paddingBottom'));
                    }
                    else
                    {
                        parentHeight = gridparent.height();
                    }
                    h = parentHeight * parseFloat(p.height) * 0.01;
                    if (p.inWindow || gridparent[0].tagName.toLowerCase() == "body")
                        h -= (g.grid.offset().top - parseInt($('body').css('paddingTop')));
                }
                else
                {
                    h = parseInt(p.height);
                }

                h += p.heightDiff;
                g.windowHeight = windowHeight;
                g._setHeight(h);
            }
            if (g.enabledFrozen())
            {
                var gridView1Width = g.gridview1.width();
                var gridViewWidth = g.gridview.width()
                g.gridview2.css({
                    width: gridViewWidth - gridView1Width
                });
            }

            g.trigger('SysGridHeightChanged');
        }
    });

    $.ligerui.controls.Grid.prototype.enabledTotal = $.ligerui.controls.Grid.prototype.isTotalSummary;
    $.ligerui.controls.Grid.prototype.add = $.ligerui.controls.Grid.prototype.addRow;
    $.ligerui.controls.Grid.prototype.update = $.ligerui.controls.Grid.prototype.updateRow;
    $.ligerui.controls.Grid.prototype.append = $.ligerui.controls.Grid.prototype.appendRow;
    $.ligerui.controls.Grid.prototype.getSelected = $.ligerui.controls.Grid.prototype.getSelectedRow;
    $.ligerui.controls.Grid.prototype.getSelecteds = $.ligerui.controls.Grid.prototype.getSelectedRows;
    $.ligerui.controls.Grid.prototype.getCheckedRows = $.ligerui.controls.Grid.prototype.getSelectedRows;
    $.ligerui.controls.Grid.prototype.getCheckedRowObjs = $.ligerui.controls.Grid.prototype.getSelectedRowObjs;
    $.ligerui.controls.Grid.prototype.setOptions = $.ligerui.controls.Grid.prototype.set;
    $.ligerui.controls.Grid.prototype.reload = $.ligerui.controls.Grid.prototype.loadData;
    $.ligerui.controls.Grid.prototype.refreshSize = $.ligerui.controls.Grid.prototype._onResize;


    function removeArrItem(arr, filterFn)
    {
        for (var i = arr.length - 1; i >= 0; i--)
        {
            if (filterFn(arr[i]))
            {
                arr.splice(i, 1);
            }
        }
    }
})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerLayout = function (options)
    {
        return $.ligerui.run.call(this, "ligerLayout", arguments);
    };

    $.fn.ligerGetLayoutManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetLayoutManager", arguments);
    };


    $.ligerDefaults.Layout = {
        topHeight: 50,
        bottomHeight: 50,
        leftWidth: 110,
        centerWidth: 300,
        rightWidth: 170,
        InWindow: true,     //是否以窗口的高度为准 height设置为百分比时可用
        heightDiff: 0,     //高度补差
        height: '100%',      //高度
        onHeightChanged: null,
        isLeftCollapse: false,      //初始化时 左边是否隐藏
        isRightCollapse: false,     //初始化时 右边是否隐藏
        allowLeftCollapse: true,      //是否允许 左边可以隐藏
        allowRightCollapse: true,     //是否允许 右边可以隐藏
        allowLeftResize: true,      //是否允许 左边可以调整大小
        allowRightResize: true,     //是否允许 右边可以调整大小
        allowTopResize: true,      //是否允许 头部可以调整大小
        allowBottomResize: true,     //是否允许 底部可以调整大小
        space: 3, //间隔 
        onEndResize: null,          //调整大小结束事件
        minLeftWidth: 80,            //调整左侧宽度时的最小允许宽度
        minRightWidth: 80           //调整右侧宽度时的最小允许宽度
    };

    $.ligerMethos.Layout = {};

    $.ligerui.controls.Layout = function (element, options)
    {
        $.ligerui.controls.Layout.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Layout.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Layout';
        },
        __idPrev: function ()
        {
            return 'Layout';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Layout;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.layout = $(this.element);
            g.layout.addClass("l-layout");
            g.width = g.layout.width();
            //top
            if ($("> div[position=top]", g.layout).length > 0)
            {
                g.top = $("> div[position=top]", g.layout).wrap('<div class="l-layout-top" style="top:0px;"></div>').parent();
                g.top.content = $("> div[position=top]", g.top);
                if (!g.top.content.hasClass("l-layout-content"))
                    g.top.content.addClass("l-layout-content");
                g.topHeight = p.topHeight;
                if (g.topHeight)
                {
                    g.top.height(g.topHeight);
                }
            }
            //bottom
            if ($("> div[position=bottom]", g.layout).length > 0)
            {
                g.bottom = $("> div[position=bottom]", g.layout).wrap('<div class="l-layout-bottom"></div>').parent();
                g.bottom.content = $("> div[position=bottom]", g.bottom);
                if (!g.bottom.content.hasClass("l-layout-content"))
                    g.bottom.content.addClass("l-layout-content");

                g.bottomHeight = p.bottomHeight;
                if (g.bottomHeight)
                {
                    g.bottom.height(g.bottomHeight);
                }
                //set title
                var bottomtitle = g.bottom.content.attr("title");
                if (bottomtitle)
                {
                    g.bottom.header = $('<div class="l-layout-header"></div>');
                    g.bottom.prepend(g.bottom.header);
                    g.bottom.header.html(bottomtitle);
                    g.bottom.content.attr("title", "");
                }
            }
            //left
            if ($("> div[position=left]", g.layout).length > 0)
            {
                g.left = $("> div[position=left]", g.layout).wrap('<div class="l-layout-left" style="left:0px;"></div>').parent();
                g.left.header = $('<div class="l-layout-header"><div class="l-layout-header-toggle"></div><div class="l-layout-header-inner"></div></div>');
                g.left.prepend(g.left.header);
                g.left.header.toggle = $(".l-layout-header-toggle", g.left.header);
                g.left.content = $("> div[position=left]", g.left);
                if (!g.left.content.hasClass("l-layout-content"))
                    g.left.content.addClass("l-layout-content");
                if (!p.allowLeftCollapse) $(".l-layout-header-toggle", g.left.header).remove();
                //set title
                var lefttitle = g.left.content.attr("title");
                if (lefttitle)
                {
                    g.left.content.attr("title", "");
                    $(".l-layout-header-inner", g.left.header).html(lefttitle);
                }
                //set width
                g.leftWidth = p.leftWidth;
                if (g.leftWidth)
                    g.left.width(g.leftWidth);
            }
            //center
            if ($("> div[position=center]", g.layout).length > 0)
            {
                g.center = $("> div[position=center]", g.layout).wrap('<div class="l-layout-center" ></div>').parent();
                g.center.content = $("> div[position=center]", g.center);
                g.center.content.addClass("l-layout-content");
                //set title
                var centertitle = g.center.content.attr("title");
                if (centertitle)
                {
                    g.center.content.attr("title", "");
                    g.center.header = $('<div class="l-layout-header"></div>');
                    g.center.prepend(g.center.header);
                    g.center.header.html(centertitle);
                }
                //set width
                g.centerWidth = p.centerWidth;
                if (g.centerWidth)
                    g.center.width(g.centerWidth);
            }
            //right
            if ($("> div[position=right]", g.layout).length > 0)
            {
                g.right = $("> div[position=right]", g.layout).wrap('<div class="l-layout-right"></div>').parent();

                g.right.header = $('<div class="l-layout-header"><div class="l-layout-header-toggle"></div><div class="l-layout-header-inner"></div></div>');
                g.right.prepend(g.right.header);
                g.right.header.toggle = $(".l-layout-header-toggle", g.right.header);
                if (!p.allowRightCollapse) $(".l-layout-header-toggle", g.right.header).remove();
                g.right.content = $("> div[position=right]", g.right);
                if (!g.right.content.hasClass("l-layout-content"))
                    g.right.content.addClass("l-layout-content");

                //set title
                var righttitle = g.right.content.attr("title");
                if (righttitle)
                {
                    g.right.content.attr("title", "");
                    $(".l-layout-header-inner", g.right.header).html(righttitle);
                }
                //set width
                g.rightWidth = p.rightWidth;
                if (g.rightWidth)
                    g.right.width(g.rightWidth);
            }
            //lock
            g.layout.lock = $("<div class='l-layout-lock'></div>");
            g.layout.append(g.layout.lock);
            //DropHandle
            g._addDropHandle();

            //Collapse
            g.isLeftCollapse = p.isLeftCollapse;
            g.isRightCollapse = p.isRightCollapse;
            g.leftCollapse = $('<div class="l-layout-collapse-left" style="display: none; "><div class="l-layout-collapse-left-toggle"></div></div>');
            g.rightCollapse = $('<div class="l-layout-collapse-right" style="display: none; "><div class="l-layout-collapse-right-toggle"></div></div>');
            g.layout.append(g.leftCollapse).append(g.rightCollapse);
            g.leftCollapse.toggle = $("> .l-layout-collapse-left-toggle", g.leftCollapse);
            g.rightCollapse.toggle = $("> .l-layout-collapse-right-toggle", g.rightCollapse);
            g._setCollapse();
            //init
            g._bulid();
            $(window).resize(function ()
            {
                g._onResize();
            });
            g.set(p);
        },
        setLeftCollapse: function (isCollapse)
        {
            var g = this, p = this.options;
            if (!g.left) return false;
            g.isLeftCollapse = isCollapse;
            if (g.isLeftCollapse)
            {
                g.leftCollapse.show();
                g.leftDropHandle && g.leftDropHandle.hide();
                g.left.hide();
            }
            else
            {
                g.leftCollapse.hide();
                g.leftDropHandle && g.leftDropHandle.show();
                g.left.show();
            }
            g._onResize();
        },
        setRightCollapse: function (isCollapse)
        {
            var g = this, p = this.options;
            if (!g.right) return false;
            g.isRightCollapse = isCollapse;
            g._onResize();
            if (g.isRightCollapse)
            {
                g.rightCollapse.show();
                g.rightDropHandle && g.rightDropHandle.hide();
                g.right.hide();
            }
            else
            {
                g.rightCollapse.hide();
                g.rightDropHandle && g.rightDropHandle.show();
                g.right.show();
            }
            g._onResize();
        },
        _bulid: function ()
        {
            var g = this, p = this.options;
            $("> .l-layout-left .l-layout-header,> .l-layout-right .l-layout-header", g.layout).hover(function ()
            {
                $(this).addClass("l-layout-header-over");
            }, function ()
            {
                $(this).removeClass("l-layout-header-over");

            });
            $(".l-layout-header-toggle", g.layout).hover(function ()
            {
                $(this).addClass("l-layout-header-toggle-over");
            }, function ()
            {
                $(this).removeClass("l-layout-header-toggle-over");

            });
            $(".l-layout-header-toggle", g.left).click(function ()
            {
                g.setLeftCollapse(true);
            });
            $(".l-layout-header-toggle", g.right).click(function ()
            {
                g.setRightCollapse(true);
            });
            //set top
            g.middleTop = 0;
            if (g.top)
            {
                g.middleTop += g.top.height();
                g.middleTop += parseInt(g.top.css('borderTopWidth'));
                g.middleTop += parseInt(g.top.css('borderBottomWidth'));
                g.middleTop += p.space;
            }
            if (g.left)
            {
                g.left.css({ top: g.middleTop });
                g.leftCollapse.css({ top: g.middleTop });
            }
            if (g.center) g.center.css({ top: g.middleTop });
            if (g.right)
            {
                g.right.css({ top: g.middleTop });
                g.rightCollapse.css({ top: g.middleTop });
            }
            //set left
            if (g.left) g.left.css({ left: 0 });
            g._onResize();
            g._onResize();
        },
        _setCollapse: function ()
        {
            var g = this, p = this.options;
            g.leftCollapse.hover(function ()
            {
                $(this).addClass("l-layout-collapse-left-over");
            }, function ()
            {
                $(this).removeClass("l-layout-collapse-left-over");
            });
            g.leftCollapse.toggle.hover(function ()
            {
                $(this).addClass("l-layout-collapse-left-toggle-over");
            }, function ()
            {
                $(this).removeClass("l-layout-collapse-left-toggle-over");
            });
            g.rightCollapse.hover(function ()
            {
                $(this).addClass("l-layout-collapse-right-over");
            }, function ()
            {
                $(this).removeClass("l-layout-collapse-right-over");
            });
            g.rightCollapse.toggle.hover(function ()
            {
                $(this).addClass("l-layout-collapse-right-toggle-over");
            }, function ()
            {
                $(this).removeClass("l-layout-collapse-right-toggle-over");
            });
            g.leftCollapse.toggle.click(function ()
            {
                g.setLeftCollapse(false);
            });
            g.rightCollapse.toggle.click(function ()
            {
                g.setRightCollapse(false);
            });
            if (g.left && g.isLeftCollapse)
            {
                g.leftCollapse.show();
                g.leftDropHandle && g.leftDropHandle.hide();
                g.left.hide();
            }
            if (g.right && g.isRightCollapse)
            {
                g.rightCollapse.show();
                g.rightDropHandle && g.rightDropHandle.hide();
                g.right.hide();
            }
        },
        _addDropHandle: function ()
        {
            var g = this, p = this.options;
            if (g.left && p.allowLeftResize)
            {
                g.leftDropHandle = $("<div class='l-layout-drophandle-left'></div>");
                g.layout.append(g.leftDropHandle);
                g.leftDropHandle && g.leftDropHandle.show();
                g.leftDropHandle.mousedown(function (e)
                {
                    g._start('leftresize', e);
                });
            }
            if (g.right && p.allowRightResize)
            {
                g.rightDropHandle = $("<div class='l-layout-drophandle-right'></div>");
                g.layout.append(g.rightDropHandle);
                g.rightDropHandle && g.rightDropHandle.show();
                g.rightDropHandle.mousedown(function (e)
                {
                    g._start('rightresize', e);
                });
            }
            if (g.top && p.allowTopResize)
            {
                g.topDropHandle = $("<div class='l-layout-drophandle-top'></div>");
                g.layout.append(g.topDropHandle);
                g.topDropHandle.show();
                g.topDropHandle.mousedown(function (e)
                {
                    g._start('topresize', e);
                });
            }
            if (g.bottom && p.allowBottomResize)
            {
                g.bottomDropHandle = $("<div class='l-layout-drophandle-bottom'></div>");
                g.layout.append(g.bottomDropHandle);
                g.bottomDropHandle.show();
                g.bottomDropHandle.mousedown(function (e)
                {
                    g._start('bottomresize', e);
                });
            }
            g.draggingxline = $("<div class='l-layout-dragging-xline'></div>");
            g.draggingyline = $("<div class='l-layout-dragging-yline'></div>");
            g.layout.append(g.draggingxline).append(g.draggingyline);
        },
        _setDropHandlePosition: function ()
        {
            var g = this, p = this.options;
            if (g.leftDropHandle)
            {
                g.leftDropHandle.css({ left: g.left.width() + parseInt(g.left.css('left')), height: g.middleHeight, top: g.middleTop });
            }
            if (g.rightDropHandle)
            {
                g.rightDropHandle.css({ left: parseInt(g.right.css('left')) - p.space, height: g.middleHeight, top: g.middleTop });
            }
            if (g.topDropHandle)
            {
                g.topDropHandle.css({ top: g.top.height() + parseInt(g.top.css('top')), width: g.top.width() });
            }
            if (g.bottomDropHandle)
            {
                g.bottomDropHandle.css({ top: parseInt(g.bottom.css('top')) - p.space, width: g.bottom.width() });
            }
        },
        _onResize: function ()
        { 
            var g = this, p = this.options;
            var oldheight = g.layout.height();
            //set layout height 
            var h = 0;
            var windowHeight = $(window).height();
            var parentHeight = null;
            if (typeof (p.height) == "string" && p.height.indexOf('%') > 0)
            {
                var layoutparent = g.layout.parent();
                if (p.InWindow || layoutparent[0].tagName.toLowerCase() == "body")
                {
                    parentHeight = windowHeight;
                    parentHeight -= parseInt($('body').css('paddingTop'));
                    parentHeight -= parseInt($('body').css('paddingBottom'));
                }
                else
                {
                    parentHeight = layoutparent.height();
                }
                h = parentHeight * parseFloat(p.height) * 0.01;
                if (p.InWindow || layoutparent[0].tagName.toLowerCase() == "body")
                    h -= (g.layout.offset().top - parseInt($('body').css('paddingTop')));
            }
            else
            {
                h = parseInt(p.height);
            }
            h += p.heightDiff;
            g.layout.height(h);
            g.layoutHeight = g.layout.height();
            g.middleWidth = g.layout.width();
            g.middleHeight = g.layout.height();
            if (g.top)
            {
                g.middleHeight -= g.top.height();
                g.middleHeight -= parseInt(g.top.css('borderTopWidth'));
                g.middleHeight -= parseInt(g.top.css('borderBottomWidth'));
                g.middleHeight -= p.space;
            }
            if (g.bottom)
            {
                g.middleHeight -= g.bottom.height();
                g.middleHeight -= parseInt(g.bottom.css('borderTopWidth'));
                g.middleHeight -= parseInt(g.bottom.css('borderBottomWidth'));
                g.middleHeight -= p.space;
            }
            //specific
            g.middleHeight -= 2;

            if (g.hasBind('heightChanged') && g.layoutHeight != oldheight)
            {
                g.trigger('heightChanged', [{ layoutHeight: g.layoutHeight, diff: g.layoutHeight - oldheight, middleHeight: g.middleHeight}]);
            }

            if (g.center)
            {
                g.centerWidth = g.middleWidth;
                if (g.left)
                {
                    if (g.isLeftCollapse)
                    {
                        g.centerWidth -= g.leftCollapse.width();
                        g.centerWidth -= parseInt(g.leftCollapse.css('borderLeftWidth'));
                        g.centerWidth -= parseInt(g.leftCollapse.css('borderRightWidth'));
                        g.centerWidth -= parseInt(g.leftCollapse.css('left'));
                        g.centerWidth -= p.space;
                    }
                    else
                    {
                        g.centerWidth -= g.leftWidth;
                        g.centerWidth -= parseInt(g.left.css('borderLeftWidth'));
                        g.centerWidth -= parseInt(g.left.css('borderRightWidth'));
                        g.centerWidth -= parseInt(g.left.css('left'));
                        g.centerWidth -= p.space;
                    }
                }
                if (g.right)
                {
                    if (g.isRightCollapse)
                    {
                        g.centerWidth -= g.rightCollapse.width();
                        g.centerWidth -= parseInt(g.rightCollapse.css('borderLeftWidth'));
                        g.centerWidth -= parseInt(g.rightCollapse.css('borderRightWidth'));
                        g.centerWidth -= parseInt(g.rightCollapse.css('right'));
                        g.centerWidth -= p.space;
                    }
                    else
                    {
                        g.centerWidth -= g.rightWidth;
                        g.centerWidth -= parseInt(g.right.css('borderLeftWidth'));
                        g.centerWidth -= parseInt(g.right.css('borderRightWidth'));
                        g.centerWidth -= p.space;
                    }
                }
                g.centerLeft = 0;
                if (g.left)
                {
                    if (g.isLeftCollapse)
                    {
                        g.centerLeft += g.leftCollapse.width();
                        g.centerLeft += parseInt(g.leftCollapse.css('borderLeftWidth'));
                        g.centerLeft += parseInt(g.leftCollapse.css('borderRightWidth'));
                        g.centerLeft += parseInt(g.leftCollapse.css('left'));
                        g.centerLeft += p.space;
                    }
                    else
                    {
                        g.centerLeft += g.left.width();
                        g.centerLeft += parseInt(g.left.css('borderLeftWidth'));
                        g.centerLeft += parseInt(g.left.css('borderRightWidth'));
                        g.centerLeft += p.space;
                    }
                }
                g.center.css({ left: g.centerLeft });
                g.center.width(g.centerWidth);
                g.center.height(g.middleHeight);
                var contentHeight = g.middleHeight;
                if (g.center.header) contentHeight -= g.center.header.height();
                g.center.content.height(contentHeight);
            }
            if (g.left)
            {
                g.leftCollapse.height(g.middleHeight);
                g.left.height(g.middleHeight);
            }
            if (g.right)
            {
                g.rightCollapse.height(g.middleHeight);
                g.right.height(g.middleHeight);
                //set left
                g.rightLeft = 0;

                if (g.left)
                {
                    if (g.isLeftCollapse)
                    {
                        g.rightLeft += g.leftCollapse.width();
                        g.rightLeft += parseInt(g.leftCollapse.css('borderLeftWidth'));
                        g.rightLeft += parseInt(g.leftCollapse.css('borderRightWidth'));
                        g.rightLeft += p.space;
                    }
                    else
                    {
                        g.rightLeft += g.left.width();
                        g.rightLeft += parseInt(g.left.css('borderLeftWidth'));
                        g.rightLeft += parseInt(g.left.css('borderRightWidth'));
                        g.rightLeft += parseInt(g.left.css('left'));
                        g.rightLeft += p.space;
                    }
                }
                if (g.center)
                {
                    g.rightLeft += g.center.width();
                    g.rightLeft += parseInt(g.center.css('borderLeftWidth'));
                    g.rightLeft += parseInt(g.center.css('borderRightWidth'));
                    g.rightLeft += p.space;
                }
                g.right.css({ left: g.rightLeft });
            }
            if (g.bottom)
            {
                g.bottomTop = g.layoutHeight - g.bottom.height() - 2;
                g.bottom.css({ top: g.bottomTop });
            }
            g._setDropHandlePosition();

        },
        _start: function (dragtype, e)
        {
            var g = this, p = this.options;
            g.dragtype = dragtype;
            if (dragtype == 'leftresize' || dragtype == 'rightresize')
            {
                g.xresize = { startX: e.pageX };
                g.draggingyline.css({ left: e.pageX - g.layout.offset().left, height: g.middleHeight, top: g.middleTop }).show();
                $('body').css('cursor', 'col-resize');
            }
            else if (dragtype == 'topresize' || dragtype == 'bottomresize')
            {
                g.yresize = { startY: e.pageY };
                g.draggingxline.css({ top: e.pageY - g.layout.offset().top, width: g.layout.width() }).show();
                $('body').css('cursor', 'row-resize');
            }
            else
            {
                return;
            }

            g.layout.lock.width(g.layout.width());
            g.layout.lock.height(g.layout.height());
            g.layout.lock.show();
            if ($.browser.msie || $.browser.safari) $('body').bind('selectstart', function () { return false; }); // 不能选择

            $(document).bind('mouseup', function ()
            {
                g._stop.apply(g, arguments);
            });
            $(document).bind('mousemove', function ()
            {
                g._drag.apply(g, arguments);
            });
        },
        _drag: function (e)
        {
            var g = this, p = this.options;
            if (g.xresize)
            {
                g.xresize.diff = e.pageX - g.xresize.startX;
                g.draggingyline.css({ left: e.pageX - g.layout.offset().left });
                $('body').css('cursor', 'col-resize');
            }
            else if (g.yresize)
            {
                g.yresize.diff = e.pageY - g.yresize.startY;
                g.draggingxline.css({ top: e.pageY - g.layout.offset().top });
                $('body').css('cursor', 'row-resize');
            }
        },
        _stop: function (e)
        {
            var g = this, p = this.options;
            var diff;
            if (g.xresize && g.xresize.diff != undefined)
            {
                diff = g.xresize.diff;
                if (g.dragtype == 'leftresize')
                {
                    if (p.minLeftWidth)
                    {
                        if (g.leftWidth + g.xresize.diff < p.minLeftWidth)
                            return;
                    }
                    g.leftWidth += g.xresize.diff;
                    g.left.width(g.leftWidth);
                    if (g.center)
                        g.center.width(g.center.width() - g.xresize.diff).css({ left: parseInt(g.center.css('left')) + g.xresize.diff });
                    else if (g.right)
                        g.right.width(g.left.width() - g.xresize.diff).css({ left: parseInt(g.right.css('left')) + g.xresize.diff });
                }
                else if (g.dragtype == 'rightresize')
                {
                    if (p.minRightWidth)
                    {
                        if (g.rightWidth - g.xresize.diff < p.minRightWidth)
                            return;
                    }
                    g.rightWidth -= g.xresize.diff;
                    g.right.width(g.rightWidth).css({ left: parseInt(g.right.css('left')) + g.xresize.diff });
                    if (g.center)
                        g.center.width(g.center.width() + g.xresize.diff);
                    else if (g.left)
                        g.left.width(g.left.width() + g.xresize.diff);
                }
            }
            else if (g.yresize && g.yresize.diff != undefined)
            {
                diff = g.yresize.diff;
                if (g.dragtype == 'topresize')
                {
                    g.top.height(g.top.height() + g.yresize.diff);
                    g.middleTop += g.yresize.diff;
                    g.middleHeight -= g.yresize.diff;
                    if (g.left)
                    {
                        g.left.css({ top: g.middleTop }).height(g.middleHeight);
                        g.leftCollapse.css({ top: g.middleTop }).height(g.middleHeight);
                    }
                    if (g.center) g.center.css({ top: g.middleTop }).height(g.middleHeight);
                    if (g.right)
                    {
                        g.right.css({ top: g.middleTop }).height(g.middleHeight);
                        g.rightCollapse.css({ top: g.middleTop }).height(g.middleHeight);
                    }
                }
                else if (g.dragtype == 'bottomresize')
                {
                    g.bottom.height(g.bottom.height() - g.yresize.diff);
                    g.middleHeight += g.yresize.diff;
                    g.bottomTop += g.yresize.diff;
                    g.bottom.css({ top: g.bottomTop });
                    if (g.left)
                    {
                        g.left.height(g.middleHeight);
                        g.leftCollapse.height(g.middleHeight);
                    }
                    if (g.center) g.center.height(g.middleHeight);
                    if (g.right)
                    {
                        g.right.height(g.middleHeight);
                        g.rightCollapse.height(g.middleHeight);
                    }
                }
            }
            g.trigger('endResize', [{
                direction: g.dragtype ? g.dragtype.replace(/resize/, '') : '',
                diff: diff
            }, e]);
            g._setDropHandlePosition();
            g.draggingxline.hide();
            g.draggingyline.hide();
            g.xresize = g.yresize = g.dragtype = false;
            g.layout.lock.hide();
            if ($.browser.msie || $.browser.safari)
                $('body').unbind('selectstart');
            $(document).unbind('mousemove', g._drag);
            $(document).unbind('mouseup', g._stop);
            $('body').css('cursor', '');

        }
    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerListBox = function (options)
    {
        return $.ligerui.run.call(this, "ligerListBox", arguments);
    }; 

    $.ligerDefaults.ListBox = { 
        isMultiSelect: false,   //是否多选
        isShowCheckBox: false,  //是否选择复选框
        columns: null,          //表格状态
        width: 150,            //宽度
        height: 100,           //高度
        onSelect: false,        //选择前事件
        onSelected: null,       //选择值事件  
        valueField: 'id',       //值成员
        textField: 'text',      //显示成员
        valueFieldID: null,     //值 隐藏域 表单名 
        split: ";",             //分隔符
        data: null,             //数据  
        parms: null,            //ajax提交表单 
        url: null,              //数据源URL(需返回JSON)
        onSuccess: null,
        onError: null, 
        render: null,            //显示html自定义函数 
        css: null,               //附加css  
        value: null            //值 
    };

    //扩展方法
    $.ligerMethos.ListBox = $.ligerMethos.ListBox || {};


    $.ligerui.controls.ListBox = function (element, options)
    {
        $.ligerui.controls.ListBox.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.ListBox.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'ListBox';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.ListBox;
        },
        _init: function ()
        {
            $.ligerui.controls.ListBox.base._init.call(this); 
        },
        _render: function ()
        {
            var g = this, p = this.options; 
            g.data = p.data;    
            g.valueField = null; //隐藏域(保存值) 
               
            if (p.valueFieldID)
            {
                g.valueField = $("#" + p.valueFieldID + ":input,[name=" + p.valueFieldID + "]:input");
                if (g.valueField.length == 0) g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = p.valueFieldID;
            }
            else
            {
                g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = g.id + "_val";
            }
            if (g.valueField[0].name == null) g.valueField[0].name = g.valueField[0].id;
            g.valueField.attr("data-ligerid", g.id);
            //选择框框
            g.selectBox = $(this.element);
            g.selectBox.html('<div class="l-listbox-inner"><table cellpadding="0" cellspacing="0" border="0" class="l-listbox-table"></table></div>').addClass("l-listbox").append(g.valueField);
            g.selectBox.table = $("table:first", g.selectBox); 
              
            g.set(p); 

            g._addClickEven();
        },
        destroy: function ()
        { 
            if (this.selectBox) this.selectBox.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        clear : function()
        {
            this._changeValue("");
            this.trigger('clear');
        },
        _setIsShowCheckBox : function(value)
        {
            if (value)
            {
                $("table", this.selectBox).addClass("l-table-checkbox");
            } else
            { 
                $("table", this.selectBox).addClass("l-table-nocheckbox");
            }
        },
        _setCss : function(css)
        {
            if (css) {
                this.selectBox.addClass(css);
            } 
        }, 
        _setDisabled: function (value)
        {
            //禁用样式
            if (value)
            {
                this.selectBox.addClass('l-text-disabled');
            } else
            {
                this.selectBox.removeClass('l-text-disabled');
            }
        }, 
        _setWidth: function (value)
        {
            this.selectBox.width(value);
        },
        _setHeight: function (value)
        {
            this.selectBox.height(value);
        }, 
        //查找Text,适用多选和单选
        findTextByValue: function (value)
        {
            var g = this, p = this.options;
            if (value == null) return "";
            var texts = "";
            var contain = function (checkvalue)
            {
                var targetdata = value.toString().split(p.split);
                for (var i = 0; i < targetdata.length; i++)
                {
                    if (targetdata[i] == checkvalue) return true;
                }
                return false;
            };
            $(g.data).each(function (i, item)
            {
                var val = item[p.valueField];
                var txt = item[p.textField];
                if (contain(val))
                {
                    texts += txt + p.split;
                }
            });
            if (texts.length > 0) texts = texts.substr(0, texts.length - 1);
            return texts;
        },
        indexOf : function(item)
        {
            var g = this, p = this.options;
            if (!g.data) return -1;
            for (var i = 0, l = g.data.length; i < l; i++)
            {
                if (typeof (item) == "object")
                {
                    if (g.data[i] == item) return i;
                } else
                {
                    if (g.data[i][p.valueField].toString() == item.toString()) return i;
                }
            }
            return -1;
        },
        removeItems : function(items)
        {
            var g = this;
            if (!g.data) return;
            $(items).each(function (i,item)
            {
                var index = g.indexOf(item);
                if (index == -1) return;
                g.data.splice(index, 1);
            });
            g.refresh();
        },
        removeItem: function (item)
        {
            if (!this.data) return;
            var index = this.indexOf(item);
            if (index == -1) return;
            this.data.splice(index, 1);
            this.refresh();
        },
        insertItem: function (item,index)
        {
            var g = this;
            if (!g.data) g.data = []; 
            g.data.splice(index, 0, item);
            g.refresh();
        },
        addItems: function (items)
        {
            var g = this;
            if (!g.data) g.data = [];
            $(items).each(function (i, item)
            {
                g.data.push(item);
            });
            g.refresh();
        },
        addItem: function (item)
        {
            var g = this;
            if (!g.data) g.data = [];
            g.data.push(item);
            g.refresh();
        }, 
        getSelectedItems: function()
        {
            var g = this, p = this.options;
            if (!g.data) return null;
            var value = g.getValue();
            if (!value) return null;
            var items = [];
            $(value.split(p.split)).each(function ()
            {
                var index = g.indexOf(this.toString());
                if (index != -1) items.push(g.data[index]);
            });
            return items;
        },
        _setValue: function (value)
        {
            var g = this, p = this.options; 
            p.value = value;
            this._dataInit();
        },
        setValue: function (value)
        { 
            this._setValue(value);
        }, 
        _setUrl: function (url) {
            if (!url) return;
            var g = this, p = this.options; 
            $.ajax({
                type: 'post',
                url: url,
                data: p.parms,
                cache: false,
                dataType: 'json',
                success: function (data) { 
                    g.setData(data);
                    g.trigger('success', [data]);
                },
                error: function (XMLHttpRequest, textStatus) {
                    g.trigger('error', [XMLHttpRequest, textStatus]);
                }
            });
        },
        setUrl: function (url) {
            return this._setUrl(url);
        },
        setParm: function (name, value) {
            if (!name) return;
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            parms[name] = value;
            g.set('parms', parms); 
        },
        clearContent: function ()
        {
            var g = this, p = this.options;
            $("table", g.selectBox).html(""); 
        },
        _setColumns : function(columns)
        {
            var g = this, p = this.options;
            p.columns = columns;
            g.refresh();
        },
        _setData : function(data)
        {
            this.setData(data);
        },
        setData: function (data)
        {
            var g = this, p = this.options; 
            if (!data || !data.length) return;
            g.data = data;
            g.refresh();
        },
        refresh:function()
        {
            var g = this, p = this.options, data = this.data; 
            this.clearContent();
            if (!data) return;
            if (p.columns)
            {
                g.selectBox.table.headrow = $("<tr class='l-table-headerow'><td width='18px' class='l-checkboxrow'></td></tr>");
                g.selectBox.table.append(g.selectBox.table.headrow);
                g.selectBox.table.addClass("l-listbox-grid");
                for (var j = 0; j < p.columns.length; j++)
                {
                    var headrow = $("<td columnindex='" + j + "' columnname='" + p.columns[j].name + "'>" + p.columns[j].header + "</td>");
                    if (p.columns[j].width)
                    {
                        headrow.width(p.columns[j].width);
                    }
                    g.selectBox.table.headrow.append(headrow);

                }
            }
            var out = [];
            for (var i = 0; i < data.length; i++)
            {
                var val = data[i][p.valueField];
                var txt = data[i][p.textField];
                var valueIndexStr = " value='" + val + "' index='" + i + "'";
                if (!p.columns)
                {
                    out.push("<tr " + valueIndexStr + ">");
                    out.push("<td style='width:18px;' class='l-checkboxrow'><input type='checkbox'" + valueIndexStr + "/></td>");
                    var itemHtml = txt;
                    if (p.render) {
                        itemHtml = p.render({
                            data: data[i],
                            value: val,
                            text: txt 
                        });
                    } 
                    out.push("<td align='left'>" + itemHtml + "</td></tr>");
                } else
                {
                    out.push("<tr " + valueIndexStr + "><td style='width:18px;' class='l-checkboxrow'><input type='checkbox' " + valueIndexStr + "/></td>");
                    for (var j = 0; j < p.columns.length; j++) {
                        var columnname = p.columns[j].name;
                        out.push("<td>" + data[i][columnname] + "</td>");
                    }
                    out.push('</tr>');  
                }
            } 
            g.selectBox.table.append(out.join(''));
        },
        _getValue: function ()
        {
            return $(this.valueField).val();
        },
        getValue: function ()
        {
            //获取值
            return this._getValue();
        },  
        updateStyle: function ()
        { 
            g._dataInit();
        },
        _dataInit: function ()
        {
            var g = this, p = this.options;
            var value = p.value;
            //根据值来初始化
            if (value != null)
            {
                g._changeValue(value);
            } 
            else if (g.valueField.val() != "")
            {
                p.value = g.valueField.val();  
            } 
            var valueArr = (value || "").toString().split(p.split);

            $("tr.l-selected", g.selectBox)
                   .removeClass("l-selected")
                   .find(":checkbox").each(function () { this.checked = false; });
            $(valueArr).each(function (i, item)
            {
                $("tr[value='" + item + "']", g.selectBox)
                    .addClass("l-selected")
                    .find(":checkbox").each(function () { this.checked = true; });
            }); 
        },
        //设置值到 隐藏域
        _changeValue: function (newValue)
        {
            var g = this, p = this.options; 
            g.valueField.val(newValue); 
            g.selectedValue = newValue;
        },
        //更新值到隐藏域
        _updateValue: function ()
        {
            var g = this, p = this.options;
            var values = [];
            $("tr", g.selectBox).each(function ()
            {
                var jrow = $(this);
                if (jrow.hasClass("l-selected"))
                {
                    values.push(jrow.attr("value"));
                }
            }); 
            g._changeValue(values.join(p.split));
        },
        _addClickEven: function ()
        {
            var g = this, p = this.options;
            //选项点击
            g.selectBox.click(function (e)
            {  
                var obj = (e.target || e.srcElement); 
                var jrow = $(obj).parents("tr:first");
                if (!jrow.length) return;
                var value = jrow.attr("value");
                if (g.hasBind('select'))
                {
                    var text = g.findTextByValue(value);
                    if (g.trigger('select', [value, text]) == false)
                    {
                        return false;
                    }
                }
                if (!p.isMultiSelect)
                {
                    $("tr.l-selected", g.selectBox).not(jrow)
                        .removeClass("l-selected")
                        .find(":checkbox").each(function () { this.checked = false });
                }
                if (jrow.hasClass("l-selected"))
                {
                    jrow.removeClass("l-selected");
                } else
                {
                    jrow.addClass("l-selected");
                }
                jrow.find(":checkbox").each(function () { this.checked = jrow.hasClass("l-selected"); });
                g._updateValue();
            });
        } 
    });
      

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.ligerMenu = function (options)
    {
        return $.ligerui.run.call(null, "ligerMenu", arguments);
    };

    $.ligerDefaults.Menu = {
        width: 120,
        top: 0,
        left: 0,
        items: null,
        shadow: true,
		//modify by songxf at 2013-6-26
		contextmenu:false
		//modify end
    };

    $.ligerMethos.Menu = {};

    $.ligerui.controls.Menu = function (options)
    {
        $.ligerui.controls.Menu.base.constructor.call(this, null, options);
    };
    $.ligerui.controls.Menu.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Menu';
        },
        __idPrev: function ()
        {
            return 'Menu';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Menu;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.menuItemCount = 0;
            //全部菜单
            g.menus = {};
            //顶级菜单
            g.menu = g.createMenu();
            g.element = g.menu[0];
            g.menu.css({ top: p.top, left: p.left, width: p.width });

            p.items && $(p.items).each(function (i, item)
            {
                g.addItem(item);
            });
			//modify by songxf at 2013-6-26
            //$(document).bind('click.menu', function ()
            //{
            //    for (var menuid in g.menus)
            //    {
            //        var menu = g.menus[menuid];
            //        if (!menu) return;
            //        menu.hide();
            //        if (menu.shadow) menu.shadow.hide();
            //    }
            //});
			if(p.contextmenu){
	                 $(document).bind('click.menu', function ()
            		  {
		                for (var menuid in g.menus)
		                {
		                    var menu = g.menus[menuid];
		                    if (!menu) return;
		                    menu.hide();
		                    if (menu.shadow) menu.shadow.hide();
		                }
           		 	  });
	        };
			//modify end
            g.set(p);
        },
        show: function (options, menu)
        {
            var g = this, p = this.options;
            if (menu == undefined) menu = g.menu;
            if (options && options.left != undefined)
            {
                menu.css({ left: options.left });
            }
            if (options && options.top != undefined)
            {
                menu.css({ top: options.top });
            }
            menu.show();
            g.updateShadow(menu);
        },
        updateShadow: function (menu)
        {
            var g = this, p = this.options;
            if (!p.shadow) return;
            menu.shadow.css({
                left: menu.css('left'),
                top: menu.css('top'),
                width: menu.outerWidth(),
                height: menu.outerHeight()
            });
            if (menu.is(":visible"))
                menu.shadow.show();
            else
                menu.shadow.hide();
        },
        hide: function (menu)
        {
            var g = this, p = this.options;
            if (menu == undefined) menu = g.menu;
            g.hideAllSubMenu(menu);
            menu.hide();
            g.updateShadow(menu);
        },
        toggle: function ()
        {
            var g = this, p = this.options;
            g.menu.toggle();
            g.updateShadow(g.menu);
        },
        removeItem: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-menu-item[menuitemid=" + itemid + "]", g.menu.items).remove();
        },
        setEnabled: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-menu-item[menuitemid=" + itemid + "]", g.menu.items).removeClass("l-menu-item-disable");
        },
        setDisabled: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-menu-item[menuitemid=" + itemid + "]", g.menu.items).addClass("l-menu-item-disable");
        },
        isEnable: function (itemid)
        {
            var g = this, p = this.options;
            return !$("> .l-menu-item[menuitemid=" + itemid + "]", g.menu.items).hasClass("l-menu-item-disable");
        },
        getItemCount: function ()
        {
            var g = this, p = this.options;
            return $("> .l-menu-item", g.menu.items).length;
        },
        addItem: function (item, menu)
        {
            var g = this, p = this.options;
            if (!item) return;
            if (menu == undefined) menu = g.menu;

            if (item.line)
            {
                menu.items.append('<div class="l-menu-item-line"></div>');
                return;
            }
            var ditem = $('<div class="l-menu-item"><div class="l-menu-item-text"></div> </div>');
            var itemcount = $("> .l-menu-item", menu.items).length;
            menu.items.append(ditem);
            ditem.attr("ligeruimenutemid", ++g.menuItemCount);
            item.id && ditem.attr("menuitemid", item.id);
            item.text && $(">.l-menu-item-text:first", ditem).html(item.text);
            item.icon && ditem.prepend('<div class="l-menu-item-icon l-icon-' + item.icon + '"></div>');
            if (item.disable || item.disabled)
                ditem.addClass("l-menu-item-disable");
            if (item.children)
            {
                ditem.append('<div class="l-menu-item-arrow"></div>');
                var newmenu = g.createMenu(ditem.attr("ligeruimenutemid"));
                g.menus[ditem.attr("ligeruimenutemid")] = newmenu;
                newmenu.width(p.width);
                newmenu.hover(null, function ()
                {
                    if (!newmenu.showedSubMenu)
                        g.hide(newmenu);
                });
                $(item.children).each(function ()
                {
                    g.addItem(this, newmenu);
                });
            }
			//modify by songxf at 2013-6-26
            //item.click && ditem.click(function ()
            //{
            //    if ($(this).hasClass("l-menu-item-disable")) return;
            //    item.click(item, itemcount);
            //});
			
            //item.dblclick && ditem.dblclick(function ()
            //{
            //    if ($(this).hasClass("l-menu-item-disable")) return;
            //    item.dblclick(item, itemcount);
            //});
            item.click && ditem.click(function ()

            {
                 for (var menuid in g.menus)
                    {
                        var menu = g.menus[menuid];
                        if (!menu) return;
                       menu.hide();
                        if (menu.shadow) menu.shadow.hide();
                    }
                    if (menu.shadow) menu.shadow.hide();
                if ($(this).hasClass("l-menu-item-disable")) return;
                item.click(item, itemcount);

            });
            item.dblclick && ditem.dblclick(function ()

            {
                 for (var menuid in g.menus)
                    {
                        var menu = g.menus[menuid];
                        if (!menu) return;
                       menu.hide();
                        if (menu.shadow) menu.shadow.hide();
                    }
                    if (menu.shadow) menu.shadow.hide();
                if ($(this).hasClass("l-menu-item-disable")) return;
                item.dblclick(item, itemcount);

            });
			//modify end

            var menuover = $("> .l-menu-over:first", menu);
            ditem.hover(function ()
            {
                if ($(this).hasClass("l-menu-item-disable")) return;
                var itemtop = $(this).offset().top;
                var top = itemtop - menu.offset().top;
                menuover.css({ top: top });
                g.hideAllSubMenu(menu);
                if (item.children)
                {
                    var ligeruimenutemid = $(this).attr("ligeruimenutemid");
                    if (!ligeruimenutemid) return;
                    if (g.menus[ligeruimenutemid])
                    {
						//modify by songxf at 2013-6-26
						//g.show({ top: itemtop, left: $(this).offset().left + $(this).width() - 5 }, g.menus[ligeruimenutemid]);
	                   	var subtop=itemtop;
	                    if(p.parent){
	                    	subtop=subtop-p.parent.offset().top;
	                    }	
	                    g.show({ top: subtop, left: $(this).width()+6 }, g.menus[ligeruimenutemid]);
						//modify end
                        menu.showedSubMenu = true;
                    }
                }
            }, function ()
            {
                if ($(this).hasClass("l-menu-item-disable")) return;
                var ligeruimenutemid = $(this).attr("ligeruimenutemid");
                if (item.children)
                {
                    var ligeruimenutemid = $(this).attr("ligeruimenutemid");
                    if (!ligeruimenutemid) return;
                };
            });
        },
        hideAllSubMenu: function (menu)
        {
            var g = this, p = this.options;
            if (menu == undefined) menu = g.menu;
            $("> .l-menu-item", menu.items).each(function ()
            {
                if ($("> .l-menu-item-arrow", this).length > 0)
                {
                    var ligeruimenutemid = $(this).attr("ligeruimenutemid");
                    if (!ligeruimenutemid) return;
                    g.menus[ligeruimenutemid] && g.hide(g.menus[ligeruimenutemid]);
                }
            });
            menu.showedSubMenu = false;
        },
		//modify by songxf at 2013-6-26
        //createMenu: function (parentMenuItemID)
        //{
        //    var g = this, p = this.options;
        //    var menu = $('<div class="l-menu" style="display:none"><div class="l-menu-yline"></div><div class="l-menu-over"><div class="l-menu-over-l"></div> <div class="l-menu-over-r"></div></div><div class="l-menu-inner"></div></div>');
        //    parentMenuItemID && menu.attr("ligeruiparentmenuitemid", parentMenuItemID);
        //    menu.items = $("> .l-menu-inner:first", menu);
        //    menu.appendTo('body');
        //    if (p.shadow)
        //    {
        //        menu.shadow = $('<div class="l-menu-shadow"></div>').insertAfter(menu);
        //        g.updateShadow(menu);
        //    }
        //    menu.hover(null, function ()
        //    {
        //        if (!menu.showedSubMenu)
        //            $("> .l-menu-over:first", menu).css({ top: -24 });
        //    });
        //    if (parentMenuItemID)
        //        g.menus[parentMenuItemID] = menu;
        //    else
        //        g.menus[0] = menu;
        //    return menu;
        //}
		createMenu: function (parentMenuItemID)
        {
            var g = this, p = this.options;
            var menu = $('<div class="l-menu" style="display:none;"><div class="l-menu-yline"></div><div class="l-menu-over"><div class="l-menu-over-l"></div> <div class="l-menu-over-r"></div></div><div class="l-menu-inner"></div></div>');
            parentMenuItemID && menu.attr("ligeruiparentmenuitemid", parentMenuItemID);
            menu.items = $("> .l-menu-inner:first", menu);
            if(p.parent){
            menu.appendTo(p.parent);
            }else{
            menu.appendTo('body');
            }
        
            if (p.shadow)
            {
                menu.shadow = $('<div class="l-menu-shadow"></div>').insertAfter(menu);
                g.updateShadow(menu);
            }

            if (parentMenuItemID)
                g.menus[parentMenuItemID] = menu;
            else
                g.menus[0] = menu;
            return menu;
        }
	    //modify end
    });
    //旧写法保留
    $.ligerui.controls.Menu.prototype.setEnable = $.ligerui.controls.Menu.prototype.setEnabled;
    $.ligerui.controls.Menu.prototype.setDisable = $.ligerui.controls.Menu.prototype.setDisabled;



})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerMenuBar = function (options)
    {
        return $.ligerui.run.call(this, "ligerMenuBar", arguments);
    };
    $.fn.ligerGetMenuBarManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetMenuBarManager", arguments);
    };

    $.ligerDefaults.MenuBar = {};

    $.ligerMethos.MenuBar = {};

    $.ligerui.controls.MenuBar = function (element, options)
    {
        $.ligerui.controls.MenuBar.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.MenuBar.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'MenuBar';
        },
        __idPrev: function ()
        {
            return 'MenuBar';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.MenuBar;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.menubar = $(this.element);
            if (!g.menubar.hasClass("l-menubar")) g.menubar.addClass("l-menubar");
            if (p && p.items)
            {
                $(p.items).each(function (i, item)
                {
                    g.addItem(item);
                });
            }
            $(document).click(function ()
            {
                $(".l-panel-btn-selected", g.menubar).removeClass("l-panel-btn-selected");
            });
            g.set(p);
        },
        addItem: function (item)
        {
            var g = this, p = this.options;
            var ditem = $('<div class="l-menubar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
            g.menubar.append(ditem);
            item.id && ditem.attr("menubarid", item.id);
            item.text && $("span:first", ditem).html(item.text);
            item.disable && ditem.addClass("l-menubar-item-disable");
            item.click && ditem.click(function () { item.click(item); });
            if (item.menu)
            {
                var menu = $.ligerMenu(item.menu);
                ditem.hover(function ()
                {
                    g.actionMenu && g.actionMenu.hide();
                    var left = $(this).offset().left;
                    var top = $(this).offset().top + $(this).height();
                    menu.show({ top: top, left: left });
                    g.actionMenu = menu;
                    $(this).addClass("l-panel-btn-over l-panel-btn-selected").siblings(".l-menubar-item").removeClass("l-panel-btn-selected");
                }, function ()
                {
                    $(this).removeClass("l-panel-btn-over");
                });
            }
            else
            {
                ditem.hover(function ()
                {
                    $(this).addClass("l-panel-btn-over");
                }, function ()
                {
                    $(this).removeClass("l-panel-btn-over");
                });
                $(".l-menubar-item-down", ditem).remove();
            }

        }
    });

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.ligerMessageBox = function (options)
    {
        return $.ligerui.run.call(null, "ligerMessageBox", arguments, { isStatic: true });
    };


    $.ligerDefaults.MessageBox = {
        isDrag: true
    };

    $.ligerMethos.MessageBox = {};

    $.ligerui.controls.MessageBox = function (options)
    {
        $.ligerui.controls.MessageBox.base.constructor.call(this, null, options);
    };
    $.ligerui.controls.MessageBox.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'MessageBox';
        },
        __idPrev: function ()
        {
            return 'MessageBox';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.MessageBox;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            var messageBoxHTML = "";
            messageBoxHTML += '<div class="l-messagebox">';
            messageBoxHTML += '        <div class="l-messagebox-lt"></div><div class="l-messagebox-rt"></div>';
            messageBoxHTML += '        <div class="l-messagebox-l"></div><div class="l-messagebox-r"></div> ';
            messageBoxHTML += '        <div class="l-messagebox-image"></div>';
            messageBoxHTML += '        <div class="l-messagebox-title">';
            messageBoxHTML += '            <div class="l-messagebox-title-inner"></div>';
            messageBoxHTML += '            <div class="l-messagebox-close"></div>';
            messageBoxHTML += '        </div>';
            messageBoxHTML += '        <div class="l-messagebox-content">';
            messageBoxHTML += '        </div>';
            messageBoxHTML += '        <div class="l-messagebox-buttons"><div class="l-messagebox-buttons-inner">';
            messageBoxHTML += '        </div></div>';
            messageBoxHTML += '    </div>';
            g.messageBox = $(messageBoxHTML);
            $('body').append(g.messageBox);
            g.messageBox.close = function ()
            {
                g._removeWindowMask();
                g.messageBox.remove();
            };
            //设置参数属性
            p.width && g.messageBox.width(p.width);
            p.title && $(".l-messagebox-title-inner", g.messageBox).html(p.title);
            p.content && $(".l-messagebox-content", g.messageBox).html(p.content);
            if (p.buttons)
            {
                $(p.buttons).each(function (i, item)
                {
                    var btn = $('<div class="l-messagebox-btn"><div class="l-messagebox-btn-l"></div><div class="l-messagebox-btn-r"></div><div class="l-messagebox-btn-inner"></div></div>');
                    $(".l-messagebox-btn-inner", btn).html(item.text);
                    $(".l-messagebox-buttons-inner", g.messageBox).append(btn);
                    item.width && btn.width(item.width);
                    item.onclick && btn.click(function () { item.onclick(item, i, g.messageBox) });
                });
                $(".l-messagebox-buttons-inner", g.messageBox).append("<div class='l-clear'></div>");
            }
            var boxWidth = g.messageBox.width();
            var sumBtnWidth = 0;
            $(".l-messagebox-buttons-inner .l-messagebox-btn", g.messageBox).each(function ()
            {
                sumBtnWidth += $(this).width();
            });
            $(".l-messagebox-buttons-inner", g.messageBox).css({ marginLeft: parseInt((boxWidth - sumBtnWidth) * 0.5) });
            //设置背景、拖动支持 和设置图片
            g._applyWindowMask();
            g._applyDrag();
            g._setImage();

            //位置初始化
            var left = 0;
            var top = 0;
            var width = p.width || g.messageBox.width();
            if (p.left != null) left = p.left;
            else p.left = left = 0.5 * ($(window).width() - width);
            if (p.top != null) top = p.top;
            else p.top = top = 0.5 * ($(window).height() - g.messageBox.height()) + $(window).scrollTop() - 10;
            if (left < 0) p.left = left = 0;
            if (top < 0) p.top = top = 0;
            g.messageBox.css({ left: left, top: top });

            //设置事件
            $(".l-messagebox-btn", g.messageBox).hover(function ()
            {
                $(this).addClass("l-messagebox-btn-over");
            }, function ()
            {
                $(this).removeClass("l-messagebox-btn-over");
            });
            $(".l-messagebox-close", g.messageBox).hover(function ()
            {
                $(this).addClass("l-messagebox-close-over");
            }, function ()
            {
                $(this).removeClass("l-messagebox-close-over");
            }).click(function ()
            {
                g.messageBox.close();
            });
            g.set(p);
        },
        close: function ()
        {
            var g = this, p = this.options;
            this.g._removeWindowMask();
            this.messageBox.remove();
        },
        _applyWindowMask: function ()
        {
            var g = this, p = this.options;
            $(".l-window-mask").remove();
            $("<div class='l-window-mask' style='display: block;'></div>").appendTo($("body"));
        },
        _removeWindowMask: function ()
        {
            var g = this, p = this.options;
            $(".l-window-mask").remove();
        },
        _applyDrag: function ()
        {
            var g = this, p = this.options;
            if (p.isDrag && $.fn.ligerDrag)
                g.messageBox.ligerDrag({ handler: '.l-messagebox-title-inner', animate: false });
        },
        _setImage: function ()
        {
            var g = this, p = this.options;
            if (p.type)
            {
                if (p.type == 'success' || p.type == 'donne')
                {
                    $(".l-messagebox-image", g.messageBox).addClass("l-messagebox-image-donne").show();
                    $(".l-messagebox-content", g.messageBox).css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'error')
                {
                    $(".l-messagebox-image", g.messageBox).addClass("l-messagebox-image-error").show();
                    $(".l-messagebox-content", g.messageBox).css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'warn')
                {
                    $(".l-messagebox-image", g.messageBox).addClass("l-messagebox-image-warn").show();
                    $(".l-messagebox-content", g.messageBox).css({ paddingLeft: 64, paddingBottom: 30 });
                }
                else if (p.type == 'question')
                {
                    $(".l-messagebox-image", g.messageBox).addClass("l-messagebox-image-question").show();
                    $(".l-messagebox-content", g.messageBox).css({ paddingLeft: 64, paddingBottom: 40 });
                }
            }
        }
    });


    $.ligerMessageBox.show = function (p)
    {
        return $.ligerMessageBox(p);
    };
    $.ligerMessageBox.alert = function (title, content, type, onBtnClick)
    {
        title = title || "";
        content = content || title;
        var onclick = function (item, index, messageBox)
        {
            messageBox.close();
            if (onBtnClick)
                onBtnClick(item, index, messageBox);
        };
        p = {
            title: title,
            content: content,
            buttons: [{ text: '确定', onclick: onclick}]
        };
        if (type) p.type = type;
        return $.ligerMessageBox(p);
    };
    $.ligerMessageBox.confirm = function (title, content, callback)
    {
        var onclick = function (item, index, messageBox)
        {
            messageBox.close();
            if (callback)
            {
                callback(index == 0);
            }
        };
        p = {
            type: 'question',
            title: title,
            content: content,
            buttons: [{ text: '是', onclick: onclick }, { text: '否', onclick: onclick}]
        };
        return $.ligerMessageBox(p);
    };
    $.ligerMessageBox.success = function (title, content, onBtnClick)
    {
        return $.ligerMessageBox.alert(title, content, 'success', onBtnClick);
    };
    $.ligerMessageBox.error = function (title, content, onBtnClick)
    {
        return $.ligerMessageBox.alert(title, content, 'error', onBtnClick);
    };
    $.ligerMessageBox.warn = function (title, content, onBtnClick)
    {
        return $.ligerMessageBox.alert(title, content, 'warn', onBtnClick);
    };
    $.ligerMessageBox.question = function (title, content)
    {
        return $.ligerMessageBox.alert(title, content, 'question');
    };


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerPopupEdit = function (options)
    {
        return $.ligerui.run.call(this, "ligerPopupEdit", arguments);
    };

    $.fn.ligerGetPopupEditManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetPopupEditManager", arguments);
    };

    $.ligerDefaults.PopupEdit = { 
        valueFieldID: null,     //生成的value input:hidden 字段名
        css : null,             //附加css
        onButtonClick: null,    //利用这个参数来调用其他函数，比如打开一个新窗口来选择值 
        nullText: null,         //不能为空时的提示
        disabled: false,        //是否无效
        cancelable: true,
        width: 200,
        heigth: null, 
        render: null,        //显示函数   
        split: ';',
        grid: null,       //在 可查询、可分页列表的弹出框 中选择值 
        condition: null,  // 条件字段,比如 {fields:[{ name : 'Title' ,op : 'like', vt : 'string',type:'text' }]}
        valueField: 'id', //值字段
        textField: 'text',   //显示字段
        onSelect: null,    //选择事件,可阻止
        onSelected : null  //选择后事件
    };
     

    //扩展方法
    $.ligerMethos.PopupEdit = $.ligerMethos.PopupEdit || {};
     
    $.ligerui.controls.PopupEdit = function (element, options)
    {
        $.ligerui.controls.PopupEdit.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.PopupEdit.ligerExtend($.ligerui.controls.Input, {
        __getType: function () {
            return 'PopupEdit';
        },
        _extendMethods: function () {
            return $.ligerMethos.PopupEdit;
        },
        _init: function () {
            $.ligerui.controls.PopupEdit.base._init.call(this);
        },
        _render: function () {
            var g = this, p = this.options; 
            g.inputText = null;
            //文本框初始化
            if (this.element.tagName.toLowerCase() == "input") {
                this.element.readOnly = true;
                g.inputText = $(this.element);
                g.textFieldID = this.element.id;
            }
            if (g.inputText[0].name == undefined) g.inputText[0].name = g.textFieldID;
            //隐藏域初始化
            g.valueField = null;
            if (p.valueFieldID) {
                g.valueField = $("#" + p.valueFieldID + ":input");
                if (g.valueField.length == 0) g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = p.valueFieldID;
            }
            else {
                g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = g.textFieldID + "_val";
            }
            if (g.valueField[0].name == undefined) g.valueField[0].name = g.valueField[0].id;
            //开关
            g.link = $('<div class="l-trigger"><div class="l-trigger-icon"></div></div>');
            //外层
            g.wrapper = g.inputText.wrap('<div class="l-text l-text-popup"></div>').parent();
            g.wrapper.append('<div class="l-text-l"></div><div class="l-text-r"></div>');
            g.wrapper.append(g.link);
            g.wrapper.append(g.valueField);
            g.inputText.addClass("l-text-field");
            //modify by  songxf at 2013-6-26
            //开关 事件
//            g.link.hover(function ()
//            {
//                if (p.disabled) return;
//                this.className = "l-trigger-hover";
//            }, function ()
//            {
//                if (p.disabled) return;
//                this.className = "l-trigger";
//            }).mousedown(function ()
//            {
//                if (p.disabled) return;
//                this.className = "l-trigger-pressed";
//            }).mouseup(function ()
//            {
//                if (p.disabled) return;
//                this.className = "l-trigger-hover";
//            }).click(function (){
//                if (p.disabled) return;
//                if (g.trigger('buttonClick') == false) return false;
//            });
            g.link.click(function (){
              if (p.disabled) return;
              if (g.trigger('buttonClick') == false) return false;
            });
            //modify end
            g.inputText.click(function () {
                if (p.disabled) return; 
            }).blur(function () {
                if (p.disabled) return;
                g.wrapper.removeClass("l-text-focus");
            }).focus(function () {
                if (p.disabled) return;
                g.wrapper.addClass("l-text-focus");
            });
            g.wrapper.hover(function () {
                if (p.disabled) return;
                g.wrapper.addClass("l-text-over");
            }, function () {
                if (p.disabled) return;
                g.wrapper.removeClass("l-text-over");
            });

            g.set(p);
        },
        destroy: function () {
            if (this.wrapper) this.wrapper.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        clear: function () {
            var g = this, p = this.options;
            g.inputText.val("");
            g.valueField.val("");
        },
        _setCss: function (css) {
            if (css) {
                this.wrapper.addClass(css);
            }
        },
        //取消选择 
        _setCancelable: function (value) {
            var g = this, p = this.options;
            if (!value && g.unselect) {
                g.unselect.remove();
                g.unselect = null;
            }
            if (!value && !g.unselect) return;
            g.unselect = $('<div class="l-trigger l-trigger-cancel"><div class="l-trigger-icon"></div></div>').hide();
            g.wrapper.hover(function () {
                g.unselect.show();
            }, function () {
                g.unselect.hide();
            })
            if (!p.disabled && p.cancelable) {
                g.wrapper.append(g.unselect);
            }
            g.unselect.hover(function () {
                this.className = "l-trigger-hover l-trigger-cancel";
            }, function () {
                this.className = "l-trigger l-trigger-cancel";
            }).click(function () {
                g.clear();
            });
        },
        _setDisabled: function (value) { 
            if (value) {
                this.wrapper.addClass('l-text-disabled');
            } else {
                this.wrapper.removeClass('l-text-disabled');
            }
        },
        _setWidth: function (value) {
            var g = this; 
            if (value > 20) {
                g.wrapper.css({ width: value });
                g.inputText.css({ width: value - 20 }); 
            }
        },
        _setHeight: function (value) {
            var g = this;
            if (value > 10) {
                g.wrapper.height(value);
                g.inputText.height(value - 2);  
            }
        },
        _getText: function () {
            return $(this.inputText).val();
        },
        _getValue: function () {
            return $(this.valueField).val();
        },
        getValue: function () {
            return this._getValue();
        },
        getText: function () {
            return this._getText();
        },
        //设置值到  隐藏域
        setValue: function (value, text) {
            var g = this, p = this.options;
            if (arguments.length >= 2) {
                g.setValue(value);
                g.setText(text);
                return;
            }
            g.valueField.val(value);
        },
        //设置值到 文本框 
        setText: function (text) {
            var g = this, p = this.options; 
            if (p.render) {
                g.inputText.val(p.render(text));
            }
            else {
                g.inputText.val(text);
            }
        }, 
        addValue: function (value,text) {
            var g = this, p = this.options;
            if (!value) return;
            var v = g.getValue(), t = g.getText();
            if (!v) {
                g.setValue(value);
                g.setText(text);
            } else { 
                var arrV = [], arrT = [], old = v.split(p.split), value = value.split(p.split), text = text.split(p.split);
                for (var i = 0, l = value.length; i < l; i++) {
                    if ($.inArray(value[i], old) == -1) {
                        arrV.push(value[i]);
                        arrT.push(text[i]);
                    }
                }
                if (arrV.length) {
                    g.setValue(v + p.split + arrV.join(p.split));
                    g.setText(t + p.split + arrT.join(p.split));
                }
            }
        }, 
        removeValue: function (value, text) {
            var g = this, p = this.options;
            if (!value) return;
            var v = g.getValue(), t = g.getText();
            if (!v) return; 
            var oldV = v.split(p.split), oldT = t.split(p.split), value = value.split(p.split);
            for (var i = 0, index = -1, l = value.length; i < l ; i++) {
                if ((index = $.inArray(value[i], oldV)) != -1) {
                    oldV.splice(index, 1);
                    oldT.splice(index, 1);
                }
            }
            g.setValue(oldV.join(p.split));
            g.setText(oldT.join(p.split));
        }, 
        _setGrid: function (value) {
            if (!value) return;
            var g = this, p = this.options;
            this.bind('buttonClick', function () {
                if (!g.popupFn) {
                    var options = {
                        grid: p.grid,
                        condition: p.condition,
                        valueField: p.valueField,
                        textField: p.textField,
                        split: p.split,
                        onSelect: function (e) {
                            if (g.trigger('select', e) == false) return;
                            if (p.grid.checkbox) {
                                g.addValue(e.value, e.text);
                                g.removeValue(e.remvoeValue, e.remvoeText);
                            } else {
                                g.setValue(e.value);
                                g.setText(e.text);
                            }
                            g.trigger('selected', e);
                        },
                        selectInit: function (rowdata)
                        { 
                            var value = g.getValue();
                            if (!value) return false;
                            if (!p.valueField || !rowdata[p.valueField]) return false;
                            return $.inArray(rowdata[p.valueField].toString(), value.split(p.split)) != -1;
                        }
                    };
                    g.popupFn = $.ligerui.getPopupFn(options);
                }
                g.popupFn();
            });
        }
    });
    


    //创建一个可查询、可分页列表的选取弹出框 需要dialog,grid,form等插件的支持
    $.ligerui.getPopupFn = function (p) { 
        p = $.extend({
            title: '选择数据',     //窗口标题
            width: 700,            //窗口宽度     
            height: 320,           //列表高度
            top: null,
            left: null,
            split : ';',
            valueField: null,    //接收表格的value字段名
            textField: null,     //接收表格的text字段名
            grid: null,          //表格的参数 同ligerGrid
            condition: null,     //搜索表单的参数 同ligerForm
            onSelect: function (p) { },   //选取函数 
            selectInit: function (rowdata) { return false }  //选择初始化
        }, p); 
        if (!p.grid) return; 
        var win, grid, condition, lastSelected = [];
        return function () { 
            show();
            return false;
        };
        function show() {
            function getGridHeight(height) {
                height = height || p.height;
                height -= conditionPanel.height();
                return height;
            }
            if (win) { 
                grid._showData();
                win.show();
                grid.refreshSize(); 
                lastSelected = grid.selected.concat();
                return;
            }
            var panle = $("<div></div>");
            var conditionPanel = $("<div></div>");
            var gridPanel = $("<div></div>");
            panle.append(conditionPanel).append(gridPanel);
            if (p.condition) {
                var conditionParm = $.extend({
                    labelWidth: 60, 
                    space: 20
                }, p.condition);
                condition = conditionPanel.ligerForm(conditionParm);
            } else {
                conditionPanel.remove();
            }
            var gridParm = $.extend({
                columnWidth: 120,
                alternatingRow: false,
                frozen: true,
                rownumbers: true
            }, p.grid, {
                width: "100%",
                height: getGridHeight(),
                isChecked: p.selectInit,
                isSelected : p.selectInit,
                inWindow : false
            }); 
            //grid
            grid = gridPanel.ligerGrid(gridParm);
            //搜索按钮
            if (p.condition) {
                var containerBtn1 = $('<li style="margin-right:9px"><div></div></li>');
                $("ul:first", conditionPanel).append(containerBtn1).after('<div class="l-clear"></div>');
                $("div", containerBtn1).ligerButton({
                    text : '搜索',
                    click: function () {
                        var rules = $.ligerui.getConditions(conditionPanel);
                        grid.setParm('condition', $.ligerui.toJSON(rules));
                        grid.reload();
                    }
                });
            }
            //dialog
            win = $.ligerDialog.open({
                title: p.title,
                width: p.width,
                height: 'auto',
                top: p.top,
                left: p.left,
                target: panle,
                isResize: true,
                cls: 'l-selectorwin',
                onContentHeightChange: function (height) {
                    grid.set('height', getGridHeight(height));
                    return false;
                },
                onStopResize: function () {
                    grid.refreshSize();
                },
                buttons: [
                 { text: '选择', onclick: function (item, dialog) { toSelect(); dialog.hide(); } },
                 { text: '取消', onclick: function (item, dialog) { dialog.hide(); } }
                ]
            });

            grid.refreshSize();
        }
        function toSelect() {
            var selected = grid.selected || []; 
            var value = [], text = [], data = [];
            $(selected).each(function (i, rowdata) {
                p.valueField && value.push(rowdata[p.valueField]);
                p.textField && text.push(rowdata[p.textField]);
                var o = $.extend(true, {}, this);
                grid.formatRecord(o, true);
                data.push(o);
            });
            var unSelected = [];
            $(lastSelected).each(function () {
                if ($.inArray(this, selected) == -1 && $.inArray(this,grid.rows) != -1) {
                    unSelected.push(this);
                }
            });  
            var removeValue = [], removeText = [], removeData = [];
            $(unSelected).each(function (i, rowdata) {
                p.valueField && removeValue.push(rowdata[p.valueField]);
                p.textField && removeText.push(rowdata[p.textField]);
                var o = $.extend(true, {}, this);
                grid.formatRecord(o, true);
                removeData.push(o);
            });  
            p.onSelect({
                value: value.join(p.split),
                text: text.join(p.split),
                data: data,
                remvoeValue: removeValue.join(p.split),
                remvoeText: removeText.join(p.split),
                removeData : removeData 
            }); 
        }
    };

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{

    $.fn.ligerRadio = function ()
    {
        return $.ligerui.run.call(this, "ligerRadio", arguments);
    };

    $.fn.ligerGetRadioManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetRadioManager", arguments);
    };

    $.ligerDefaults.Radio = { disabled: false };

    $.ligerMethos.Radio = {};

    $.ligerui.controls.Radio = function (element, options)
    {
        $.ligerui.controls.Radio.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Radio.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'Radio';
        },
        __idPrev: function ()
        {
            return 'Radio';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Radio;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.input = $(this.element);
            g.link = $('<a href="javascript:void(0)" class="l-radio"></a>');
            g.wrapper = g.input.addClass('l-hidden').wrap('<div class="l-radio-wrapper"></div>').parent();
            g.wrapper.prepend(g.link);
            g.input.change(function ()
            {
                if (this.checked)
                {
                    g.link.addClass('l-radio-checked');
                }
                else
                {
                    g.link.removeClass('l-radio-checked');
                }
                return true;
            });
            g.link.click(function ()
            {
                g._doclick();
            });
            g.wrapper.hover(function ()
            {
                if (!p.disabled)
                    $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
            this.element.checked && g.link.addClass('l-radio-checked');

            if (this.element.id)
            {
                $("label[for=" + this.element.id + "]").click(function ()
                {
                    g._doclick();
                });
            }
            g.set(p);
        },
        setValue: function (value)
        {
            var g = this, p = this.options;
            if (!value)
            {
                g.input[0].checked = false;
                g.link.removeClass('l-radio-checked');
            }
            else
            {
                g.input[0].checked = true;
                g.link.addClass('l-radio-checked');
            }
        },
        getValue: function ()
        {
            return this.input[0].checked;
        },
        setEnabled: function ()
        {
            this.input.attr('disabled', false);
            this.wrapper.removeClass("l-disabled");
            this.options.disabled = false;
        },
        setDisabled: function ()
        {
            this.input.attr('disabled', true);
            this.wrapper.addClass("l-disabled");
            this.options.disabled = true;
        },
        updateStyle: function ()
        {
            if (this.input.attr('disabled'))
            {
                this.wrapper.addClass("l-disabled");
                this.options.disabled = true;
            }
            if (this.input[0].checked)
            {
                this.link.addClass('l-checkbox-checked');
            }
            else
            {
                this.link.removeClass('l-checkbox-checked');
            }
        },
        _doclick: function ()
        {
            var g = this, p = this.options;
            if (g.input.attr('disabled')) { return false; }
            g.input.trigger('click').trigger('change');
            var formEle;
            if (g.input[0].form) formEle = g.input[0].form;
            else formEle = document;
            $("input:radio[name=" + g.input[0].name + "]", formEle).not(g.input).trigger("change");
            return false;
        }
    });


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerRadioList = function (options)
    {
        return $.ligerui.run.call(this, "ligerRadioList", arguments);
    }; 

    $.ligerDefaults.RadioList = {  
        rowSize: 3,            //每行显示元素数   
        valueField: 'id',       //值成员
        textField: 'text',      //显示成员 
        valueFieldID:null,      //隐藏域
        name : null,            //表单名 
        data: null,             //数据  
        parms: null,            //ajax提交表单 
        url: null,              //数据源URL(需返回JSON)
        onSuccess: null,
        onError: null,  
        css: null,               //附加css  
        value: null            //值 
    };

    //扩展方法
    $.ligerMethos.RadioList = $.ligerMethos.RadioList || {};


    $.ligerui.controls.RadioList = function (element, options)
    {
        $.ligerui.controls.RadioList.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.RadioList.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'RadioList';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.RadioList;
        },
        _init: function ()
        {
            $.ligerui.controls.RadioList.base._init.call(this);
        },
        _render: function ()
        {
            var g = this, p = this.options; 
            g.data = p.data;    
            g.valueField = null; //隐藏域(保存值) 
               
            if (p.valueFieldID)
            {
                g.valueField = $("#" + p.valueFieldID + ":input,[name=" + p.valueFieldID + "]:input");
                if (g.valueField.length == 0) g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = p.valueFieldID;
            }
            else
            {
                g.valueField = $('<input type="hidden"/>');
                g.valueField[0].id = g.valueField[0].name = g.id + "_val";
            }
            if (g.valueField[0].name == null) g.valueField[0].name = g.valueField[0].id;
            g.valueField.attr("data-ligerid", g.id);
            g.radioList = $(this.element);
            g.radioList.html('<div class="l-radiolist-inner"><table cellpadding="0" cellspacing="0" border="0" class="l-radiolist-table"></table></div>').addClass("l-radiolist").append(g.valueField);
            g.radioList.table = $("table:first", g.radioList); 
              

            p.value = g.valueField.val() || p.value;

            g.set(p); 

            g._addClickEven();
        },
        destroy: function ()
        { 
            if (this.radioList) this.radioList.remove();
            this.options = null;
            $.ligerui.remove(this);
        },
        clear : function()
        {
            this._changeValue("");
            this.trigger('clear');
        }, 
        _setCss : function(css)
        {
            if (css) {
                this.radioList.addClass(css);
            } 
        }, 
        _setDisabled: function (value)
        {
            //禁用样式
            if (value)
            {
                this.radioList.addClass('l-radiolist-disabled');
                $("input:radio", this.radioList).attr("disabled", true);
            } else
            {
                this.radioList.removeClass('l-radiolist-disabled');
                $("input:radio", this.radioList).removeAttr("disabled");
            }
        }, 
        _setWidth: function (value)
        {
            this.radioList.width(value);
        },
        _setHeight: function (value)
        {
            this.radioList.height(value);
        },  
        indexOf : function(item)
        {
            var g = this, p = this.options;
            if (!g.data) return -1;
            for (var i = 0, l = g.data.length; i < l; i++)
            {
                if (typeof (item) == "object")
                {
                    if (g.data[i] == item) return i;
                } else
                {
                    if (g.data[i][p.valueField].toString() == item.toString()) return i;
                }
            }
            return -1;
        },
        removeItems : function(items)
        {
            var g = this;
            if (!g.data) return;
            $(items).each(function (i,item)
            {
                var index = g.indexOf(item);
                if (index == -1) return;
                g.data.splice(index, 1);
            });
            g.refresh();
        },
        removeItem: function (item)
        {
            if (!this.data) return;
            var index = this.indexOf(item);
            if (index == -1) return;
            this.data.splice(index, 1);
            this.refresh();
        },
        insertItem: function (item,index)
        {
            var g = this;
            if (!g.data) g.data = []; 
            g.data.splice(index, 0, item);
            g.refresh();
        },
        addItems: function (items)
        {
            var g = this;
            if (!g.data) g.data = [];
            $(items).each(function (i, item)
            {
                g.data.push(item);
            });
            g.refresh();
        },
        addItem: function (item)
        {
            var g = this;
            if (!g.data) g.data = [];
            g.data.push(item);
            g.refresh();
        },  
        _setValue: function (value)
        { 
            var g = this, p = this.options;
            p.value = value;
            this._dataInit(); 
        },
        setValue: function (value)
        { 
            this._setValue(value);
        }, 
        _setUrl: function (url) {
            if (!url) return;
            var g = this, p = this.options; 
            $.ajax({
                type: 'post',
                url: url,
                data: p.parms,
                cache: false,
                dataType: 'json',
                success: function (data) { 
                    g.setData(data);
                    g.trigger('success', [data]);
                },
                error: function (XMLHttpRequest, textStatus) {
                    g.trigger('error', [XMLHttpRequest, textStatus]);
                }
            });
        },
        setUrl: function (url) {
            return this._setUrl(url);
        },
        setParm: function (name, value) {
            if (!name) return;
            var g = this;
            var parms = g.get('parms');
            if (!parms) parms = {};
            parms[name] = value;
            g.set('parms', parms); 
        },
        clearContent: function ()
        {
            var g = this, p = this.options;
            $("table", g.radioList).html(""); 
        }, 
        _setData : function(data)
        {
            this.setData(data);
        },
        setData: function (data)
        {
            var g = this, p = this.options; 
            if (!data || !data.length) return;
            g.data = data;
            g.refresh();
        },
        refresh:function()
        {
            var g = this, p = this.options, data = this.data; 
            this.clearContent();
            if (!data) return; 
            var out = [], rowSize = p.rowSize, appendRowStart = false, name = p.name || g.id;
            for (var i = 0; i < data.length; i++)
            {
                var val = data[i][p.valueField], txt = data[i][p.textField], id = g.id + "-" + i;
                var newRow = i % rowSize == 0;
                //0,5,10
                if (newRow)
                {
                    if (appendRowStart) out.push('</tr>'); 
                    out.push("<tr>");
                    appendRowStart = true;
                }
                out.push("<td><input type='radio' name='" + name + "' value='" + val + "' id='" + id + "'/><label for='" + id + "'>" + txt + "</label></td>");
            }
            if (appendRowStart) out.push('</tr>');
            g.radioList.table.append(out.join(''));
        },
        _getValue: function ()
        { 
            var g = this, p = this.options, name = p.name || g.id;
            return $('input:radio[name="' + name + '"]:checked').val();
        },
        getValue: function ()
        {
            //获取值
            return this._getValue();
        },  
        updateStyle: function ()
        { 
           this._dataInit();
        },
        _dataInit: function ()
        {
            var g = this, p = this.options;
            var value = g.valueField.val() || g._getValue() || p.value;
            g._changeValue(value);
        },
        //设置值到 隐藏域
        _changeValue: function (newValue)
        {
            var g = this, p = this.options, name = p.name || g.id;
            $("input:radio[name='" + name + "']", g.radioList).each(function ()
            {
                this.checked = this.value == newValue;
            });
            g.valueField.val(newValue);
            g.selectedValue = newValue;
        },
        _addClickEven: function ()
        {
            var g = this, p = this.options;
            //选项点击
            g.radioList.click(function (e)
            {  
                var value = g.getValue();
                if (value) g.valueField.val(value);
            });
        } 
    });
      

})(jQuery);/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerResizable = function (options)
    {
        return $.ligerui.run.call(this, "ligerResizable", arguments,
        {
            idAttrName: 'ligeruiresizableid', hasElement: false, propertyToElemnt: 'target'
        });
    };

    $.fn.ligerGetResizableManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetResizableManager", arguments,
        {
            idAttrName: 'ligeruiresizableid', hasElement: false, propertyToElemnt: 'target'
        });
    };


    $.ligerDefaults.Resizable = {
        handles: 'n, e, s, w, ne, se, sw, nw',
        maxWidth: 2000,
        maxHeight: 2000,
        minWidth: 20,
        minHeight: 20,
        scope: 3,
        animate: false,
        onStartResize: function (e) { },
        onResize: function (e) { },
        onStopResize: function (e) { },
        onEndResize: null
    };

    $.ligerui.controls.Resizable = function (options)
    {
        $.ligerui.controls.Resizable.base.constructor.call(this, null, options);
    };

    $.ligerui.controls.Resizable.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Resizable';
        },
        __idPrev: function ()
        {
            return 'Resizable';
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.target = $(p.target);
            g.set(p);

            g.target.mousemove(function (e)
            {
                if (p.disabled) return;
                g.dir = g._getDir(e);
                if (g.dir)
                    g.target.css('cursor', g.dir + '-resize');
                else if (g.target.css('cursor').indexOf('-resize') > 0)
                    g.target.css('cursor', 'default');
                if (p.target.ligeruidragid)
                {
                    var drag = $.ligerui.get(p.target.ligeruidragid);
                    if (drag && g.dir)
                    {
                        drag.set('disabled', true);
                    } else if (drag)
                    {
                        drag.set('disabled', false);
                    }
                }
            }).mousedown(function (e)
            {
                if (p.disabled) return;
                if (g.dir)
                {
                    g._start(e);
                }
            });
        },
        _rendered: function ()
        {
            this.options.target.ligeruiresizableid = this.id;
        },
        _getDir: function (e)
        {
            var g = this, p = this.options;
            var dir = '';
            var xy = g.target.offset();
            var width = g.target.width();
            var height = g.target.height();
            var scope = p.scope;
            var pageX = e.pageX || e.screenX;
            var pageY = e.pageY || e.screenY;
            if (pageY >= xy.top && pageY < xy.top + scope)
            {
                dir += 'n';
            }
            else if (pageY <= xy.top + height && pageY > xy.top + height - scope)
            {
                dir += 's';
            }
            if (pageX >= xy.left && pageX < xy.left + scope)
            {
                dir += 'w';
            }
            else if (pageX <= xy.left + width && pageX > xy.left + width - scope)
            {
                dir += 'e';
            }
            if (p.handles == "all" || dir == "") return dir;
            if ($.inArray(dir, g.handles) != -1) return dir;
            return '';
        },
        _setHandles: function (handles)
        {
            if (!handles) return;
            this.handles = handles.replace(/(\s*)/g, '').split(',');
        },
        _createProxy: function ()
        {
            var g = this;
            g.proxy = $('<div class="l-resizable"></div>');
            g.proxy.width(g.target.width()).height(g.target.height())
            g.proxy.attr("resizableid", g.id).appendTo('body');
        },
        _removeProxy: function ()
        {
            var g = this;
            if (g.proxy)
            {
                g.proxy.remove();
                g.proxy = null;
            }
        },
        _start: function (e)
        {
            var g = this, p = this.options;
            g._createProxy();
            g.proxy.css({
                left: g.target.offset().left,
                top: g.target.offset().top,
                position: 'absolute'
            });
            g.current = {
                dir: g.dir,
                left: g.target.offset().left,
                top: g.target.offset().top,
                startX: e.pageX || e.screenX,
                startY: e.pageY || e.clientY,
                width: g.target.width(),
                height: g.target.height()
            };
            $(document).bind("selectstart.resizable", function () { return false; });
            $(document).bind('mouseup.resizable', function ()
            {
                g._stop.apply(g, arguments);
            });
            $(document).bind('mousemove.resizable', function ()
            {
                g._drag.apply(g, arguments);
            });
            g.proxy.show();
            g.trigger('startResize', [g.current, e]);
        },
        changeBy: {
            t: ['n', 'ne', 'nw'],
            l: ['w', 'sw', 'nw'],
            w: ['w', 'sw', 'nw', 'e', 'ne', 'se'],
            h: ['n', 'ne', 'nw', 's', 'se', 'sw']
        },
        _drag: function (e)
        {
            var g = this, p = this.options;
            if (!g.current) return;
            if (!g.proxy) return;
            g.proxy.css('cursor', g.current.dir == '' ? 'default' : g.current.dir + '-resize');
            var pageX = e.pageX || e.screenX;
            var pageY = e.pageY || e.screenY;
            g.current.diffX = pageX - g.current.startX;
            g.current.diffY = pageY - g.current.startY;
            g._applyResize(g.proxy);
            g.trigger('resize', [g.current, e]);
        },
        _stop: function (e)
        {
            var g = this, p = this.options; 
            if (g.hasBind('stopResize'))
            {
                if (g.trigger('stopResize', [g.current, e]) != false)
                    g._applyResize();
            }
            else
            {
                g._applyResize();
            }
            g._removeProxy();
            g.trigger('endResize', [g.current, e]);
            $(document).unbind("selectstart.resizable");
            $(document).unbind('mousemove.resizable');
            $(document).unbind('mouseup.resizable');
        },
        _applyResize: function (applyResultBody)
        {
            var g = this, p = this.options;
            var cur = {
                left: g.current.left,
                top: g.current.top,
                width: g.current.width,
                height: g.current.height
            };
            var applyToTarget = false;
            if (!applyResultBody)
            {
                applyResultBody = g.target;
                applyToTarget = true;
                if (!isNaN(parseInt(g.target.css('top'))))
                    cur.top = parseInt(g.target.css('top'));
                else
                    cur.top = 0;
                if (!isNaN(parseInt(g.target.css('left'))))
                    cur.left = parseInt(g.target.css('left'));
                else
                    cur.left = 0;
            }
            if ($.inArray(g.current.dir, g.changeBy.l) > -1)
            {
                cur.left += g.current.diffX;
                g.current.diffLeft = g.current.diffX;

            }
            else if (applyToTarget)
            {
                delete cur.left;
            }
            if ($.inArray(g.current.dir, g.changeBy.t) > -1)
            {
                cur.top += g.current.diffY;
                g.current.diffTop = g.current.diffY;
            }
            else if (applyToTarget)
            {
                delete cur.top;
            }
            if ($.inArray(g.current.dir, g.changeBy.w) > -1)
            {
                cur.width += (g.current.dir.indexOf('w') == -1 ? 1 : -1) * g.current.diffX;
                g.current.newWidth = cur.width;
            }
            else if (applyToTarget)
            {
                delete cur.width;
            }
            if ($.inArray(g.current.dir, g.changeBy.h) > -1)
            {
                cur.height += (g.current.dir.indexOf('n') == -1 ? 1 : -1) * g.current.diffY;
                g.current.newHeight = cur.height;
            }
            else if (applyToTarget)
            {
                delete cur.height;
            }
            if (applyToTarget && p.animate)
                applyResultBody.animate(cur);
            else
                applyResultBody.css(cur);
        }
    });



})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerSpinner = function ()
    {
        return $.ligerui.run.call(this, "ligerSpinner", arguments);
    };
    $.fn.ligerGetSpinnerManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetSpinnerManager", arguments);
    };

    $.ligerDefaults.Spinner = {
        type: 'float',     //类型 float:浮点数 int:整数 time:时间
        isNegative: true, //是否负数
        decimalplace: 2,   //小数位 type=float时起作用
        step: 0.1,         //每次增加的值
        interval: 50,      //间隔，毫秒
        onChangeValue: false,    //改变值事件
        minValue: null,        //最小值
        maxValue: null,         //最大值
        disabled: false,
        readonly: false              //是否只读
    };

    $.ligerMethos.Spinner = {};

    $.ligerui.controls.Spinner = function (element, options)
    {
        $.ligerui.controls.Spinner.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Spinner.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'Spinner';
        },
        __idPrev: function ()
        {
            return 'Spinner';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Spinner;
        },
        _init: function ()
        {
            $.ligerui.controls.Spinner.base._init.call(this);
            var p = this.options;
            if (p.type == 'float')
            {
                p.step = 0.1;
                p.interval = 50;
            } else if (p.type == 'int')
            {
                p.step = 1;
                p.interval = 100;
            } else if (p.type == 'time')
            {
                p.step = 1;
                p.interval = 100;
            } else
            {
                p.type = "int";
                p.step = 1;
                p.interval = 100;
            }
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.interval = null;
            g.inputText = null;
            g.value = null;
            g.textFieldID = "";
            if (this.element.tagName.toLowerCase() == "input" && this.element.type && this.element.type == "text")
            {
                g.inputText = $(this.element);
                if (this.element.id)
                    g.textFieldID = this.element.id;
            }
            else
            {
                g.inputText = $('<input type="text"/>');
                g.inputText.appendTo($(this.element));
            }
            if (g.textFieldID == "" && p.textFieldID)
                g.textFieldID = p.textFieldID;

            g.link = $('<div class="l-trigger"><div class="l-spinner-up"><div class="l-spinner-icon"></div></div><div class="l-spinner-split"></div><div class="l-spinner-down"><div class="l-spinner-icon"></div></div></div>');
            g.wrapper = g.inputText.wrap('<div class="l-text"></div>').parent();
            g.wrapper.append('<div class="l-text-l"></div><div class="l-text-r"></div>');
            g.wrapper.append(g.link).after(g.selectBox).after(g.valueField);
            g.link.up = $(".l-spinner-up", g.link);
            g.link.down = $(".l-spinner-down", g.link);
            g.inputText.addClass("l-text-field");

            if (p.disabled)
            {
                g.wrapper.addClass("l-text-disabled");
            }
            //初始化
            if (!g._isVerify(g.inputText.val()))
            {
                g.value = g._getDefaultValue();
                g._showValue(g.value);
            }
            //事件
            g.link.up.hover(function ()
            {
                if (!p.disabled)
                    $(this).addClass("l-spinner-up-over");
            }, function ()
            {
                clearInterval(g.interval);
                $(document).unbind("selectstart.spinner");
                $(this).removeClass("l-spinner-up-over");
            }).mousedown(function ()
            { 
                if (!p.disabled)
                {
                    g._uping.call(g);
                    g.interval = setInterval(function ()
                    {
                        g._uping.call(g);
                    }, p.interval);
                    $(document).bind("selectstart.spinner", function () { return false; });
                }
            }).mouseup(function ()
            {
                clearInterval(g.interval);
                g.inputText.trigger("change").focus();
                $(document).unbind("selectstart.spinner");
            });
            g.link.down.hover(function ()
            {
                if (!p.disabled)
                    $(this).addClass("l-spinner-down-over");
            }, function ()
            {
                clearInterval(g.interval);
                $(document).unbind("selectstart.spinner");
                $(this).removeClass("l-spinner-down-over");
            }).mousedown(function ()
            {
                if (!p.disabled)
                {
                    g.interval = setInterval(function ()
                    {
                        g._downing.call(g);
                    }, p.interval);
                    $(document).bind("selectstart.spinner", function () { return false; });
                }
            }).mouseup(function ()
            {
                clearInterval(g.interval);
                g.inputText.trigger("change").focus();
                $(document).unbind("selectstart.spinner");
            });

            g.inputText.change(function ()
            {
                var value = g.inputText.val();
                g.value = g._getVerifyValue(value);
                g.trigger('changeValue', [g.value]);
                g._showValue(g.value);
            }).blur(function ()
            {
                g.wrapper.removeClass("l-text-focus");
            }).focus(function ()
            {
                g.wrapper.addClass("l-text-focus");
            });
            g.wrapper.hover(function ()
            {
                if (!p.disabled)
                    g.wrapper.addClass("l-text-over");
            }, function ()
            {
                g.wrapper.removeClass("l-text-over");
            });
            g.set(p);
        },
        _setWidth: function (value)
        {
            var g = this;
            if (value > 20)
            {
                g.wrapper.css({ width: value });
                g.inputText.css({ width: value - 20 });
            }
        },
        _setHeight: function (value)
        {
            var g = this;
            if (value > 10)
            {
                g.wrapper.height(value);
                g.inputText.height(value - 2);
                g.link.height(value - 4);
            }
        },
        _setDisabled: function (value)
        {
            if (value)
            {
                this.wrapper.addClass("l-text-disabled");
            }
            else
            {
                this.wrapper.removeClass("l-text-disabled");
            }
        },
        _showValue: function (value)
        {
            var g = this, p = this.options;
            if (!value || value == "NaN") value = 0;
            if (p.type == 'float')
            {
                value = parseFloat(value).toFixed(p.decimalplace);
            }
            this.inputText.val(value)
        },
        _setValue: function (value)
        {
            this._showValue(value);
        },
        setValue: function (value)
        {
            this._showValue(value);
        },
        getValue: function ()
        {
            return this.inputText.val();
        },
        _round: function (v, e)
        {
            var g = this, p = this.options;
            var t = 1;
            for (; e > 0; t *= 10, e--) { }
            for (; e < 0; t /= 10, e++) { }
            return Math.round(v * t) / t;
        },
        _isInt: function (str)
        {
            var g = this, p = this.options;
            var strP = p.isNegative ? /^-?\d+$/ : /^\d+$/;
            if (!strP.test(str)) return false;
            if (parseFloat(str) != str) return false;
            return true;
        },
        _isFloat: function (str)
        {
            var g = this, p = this.options;
            var strP = p.isNegative ? /^-?\d+(\.\d+)?$/ : /^\d+(\.\d+)?$/;
            if (!strP.test(str)) return false;
            if (parseFloat(str) != str) return false;
            return true;
        },
        _isTime: function (str)
        {
            var g = this, p = this.options;
            var a = str.match(/^(\d{1,2}):(\d{1,2})$/);
            if (a == null) return false;
            if (a[1] > 24 || a[2] > 60) return false;
            return true;

        },
        _isVerify: function (str)
        {
            var g = this, p = this.options;
            if (p.type == 'float')
            {
                if (!g._isFloat(str)) return false;
                var value = parseFloat(str);
                if (p.minValue != undefined && p.minValue > value) return false;
                if (p.maxValue != undefined && p.maxValue < value) return false;
                return true;
            } else if (p.type == 'int')
            {
                if (!g._isInt(str)) return false;
                var value = parseInt(str);
                if (p.minValue != undefined && p.minValue > value) return false;
                if (p.maxValue != undefined && p.maxValue < value) return false;
                return true;
            } else if (p.type == 'time')
            {
                return g._isTime(str);
            }
            return false;
        },
        _getVerifyValue: function (value)
        {
            var g = this, p = this.options;
            var newvalue = null;
            if (p.type == 'float')
            {
                newvalue = g._round(value, p.decimalplace);
            }
            else if (p.type == 'int')
            {
                newvalue = parseInt(value);
            } else if (p.type == 'time')
            {
                newvalue = value;
            }
            if (!g._isVerify(newvalue))
            {
                return g.value;
            } else
            {
                return newvalue;
            }
        },
        _isOverValue: function (value)
        {
            var g = this, p = this.options;
            if (p.minValue != null && p.minValue > value) return true;
            if (p.maxValue != null && p.maxValue < value) return true;
            return false;
        },
        _getDefaultValue: function ()
        {
            var g = this, p = this.options;
            if (p.type == 'float' || p.type == 'int') { return 0; }
            else if (p.type == 'time') { return "00:00"; }
        },
        _addValue: function (num)
        {
            var g = this, p = this.options; 
            var value = g.inputText.val();
            value = parseFloat(value) + num;
            if (g._isOverValue(value)) return;
            g._showValue(value);
            g.inputText.trigger("change");
        },
        _addTime: function (minute)
        {
            var g = this, p = this.options;
            var value = g.inputText.val();
            var a = value.match(/^(\d{1,2}):(\d{1,2})$/);
            newminute = parseInt(a[2]) + minute;
            if (newminute < 10) newminute = "0" + newminute;
            value = a[1] + ":" + newminute;
            if (g._isOverValue(value)) return;
            g._showValue(value);
            g.inputText.trigger("change");
        },
        _uping: function ()
        {
            var g = this, p = this.options;
            if (p.type == 'float' || p.type == 'int')
            {
                g._addValue(p.step);
            } else if (p.type == 'time')
            {
                g._addTime(p.step);
            }
        },
        _downing: function ()
        {
            var g = this, p = this.options;
            if (p.type == 'float' || p.type == 'int')
            {
                g._addValue(-1 * p.step);
            } else if (p.type == 'time')
            {
                g._addTime(-1 * p.step);
            }
        },
        _isDateTime: function (dateStr)
        {
            var g = this, p = this.options;
            var r = dateStr.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            if (r == null) return false;
            var d = new Date(r[1], r[3] - 1, r[4]);
            if (d == "NaN") return false;
            return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
        },
        _isLongDateTime: function (dateStr)
        {
            var g = this, p = this.options;
            var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2})$/;
            var r = dateStr.match(reg);
            if (r == null) return false;
            var d = new Date(r[1], r[3] - 1, r[4], r[5], r[6]);
            if (d == "NaN") return false;
            return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4] && d.getHours() == r[5] && d.getMinutes() == r[6]);
        }
    });


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerTab = function (options)
    {
        return $.ligerui.run.call(this, "ligerTab", arguments);
    };

    $.fn.ligerGetTabManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetTabManager", arguments);
    };

    $.ligerDefaults.Tab = {
        height: null,
        heightDiff: 0, // 高度补差 
        changeHeightOnResize: false,
        contextmenu: true,
        dblClickToClose: false, //是否双击时关闭
        dragToMove: false,    //是否允许拖动时改变tab项的位置
		moveToNew:true,            // 是否自动将焦点移动至新添加tab上
        onBeforeOverrideTabItem: null,
        onAfterOverrideTabItem: null,
        onBeforeRemoveTabItem: null,
        onAfterRemoveTabItem: null,
        onBeforeAddTabItem: null,
        onAfterAddTabItem: null,
        onBeforeSelectTabItem: null,
        onAfterSelectTabItem: null
    };
    $.ligerDefaults.TabString = {
        closeMessage: "关闭当前页",
        closeOtherMessage: "关闭其他",
        closeAllMessage: "关闭所有",
        reloadMessage: "刷新"
    };

    $.ligerMethos.Tab = {};

    $.ligerui.controls.Tab = function (element, options)
    {
        $.ligerui.controls.Tab.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.Tab.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Tab';
        },
        __idPrev: function ()
        {
            return 'Tab';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Tab;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            if (p.height) g.makeFullHeight = true;
            g.tab = $(this.element);
            g.tab.addClass("l-tab");
            if (p.contextmenu && $.ligerMenu)
            {
				//modify by songxf at 2013-6-26
                //g.tab.menu = $.ligerMenu({ width: 100, items: [
				g.tab.menu = $.ligerMenu({ width: 100,contextmenu:true, items: [
				//modify end
                    { text: p.closeMessage, id: 'close', click: function ()
                    {
                        g._menuItemClick.apply(g, arguments);
                    }
                    },
                    { text: p.closeOtherMessage, id: 'closeother', click: function ()
                    {
                        g._menuItemClick.apply(g, arguments);
                    }
                    },
                    { text: p.closeAllMessage, id: 'closeall', click: function ()
                    {
                        g._menuItemClick.apply(g, arguments);
                    }
                    },
                    { text: p.reloadMessage, id: 'reload', click: function ()
                    {
                        g._menuItemClick.apply(g, arguments);
                    }
                    }
                ]
                });
            }
            g.tab.content = $('<div class="l-tab-content"></div>');
            $("> div", g.tab).appendTo(g.tab.content);
            g.tab.content.appendTo(g.tab);
            g.tab.links = $('<div class="l-tab-links"><ul style="left: 0px; "></ul></div>');
            g.tab.links.prependTo(g.tab);
            g.tab.links.ul = $("ul", g.tab.links);
            var lselecteds = $("> div[lselected=true]", g.tab.content);
            var haslselected = lselecteds.length > 0;
            g.selectedTabId = lselecteds.attr("tabid");
            $("> div", g.tab.content).each(function (i, box)
            {
                var li = $('<li class=""><a></a><div class="l-tab-links-item-left"></div><div class="l-tab-links-item-right"></div></li>');
                var contentitem = $(this);
                if (contentitem.attr("title"))
                {
                    $("> a", li).html(contentitem.attr("title"));
                    contentitem.attr("title", "");
                }
                var tabid = contentitem.attr("tabid");
                if (tabid == undefined)
                {
                    tabid = g.getNewTabid();
                    contentitem.attr("tabid", tabid);
                    if (contentitem.attr("lselected"))
                    {
                        g.selectedTabId = tabid;
                    }
                }
                li.attr("tabid", tabid);
                if (!haslselected && i == 0) g.selectedTabId = tabid;
                var showClose = contentitem.attr("showClose");
                if (showClose)
                {
                    li.append("<div class='l-tab-links-item-close'></div>");
                }
                $("> ul", g.tab.links).append(li);
                if (!contentitem.hasClass("l-tab-content-item")) contentitem.addClass("l-tab-content-item");
                if (contentitem.find("iframe").length > 0)
                {
                    var iframe = $("iframe:first", contentitem);
                    if (iframe[0].readyState != "complete")
                    {
                        if (contentitem.find(".l-tab-loading:first").length == 0)
                            contentitem.prepend("<div class='l-tab-loading' style='display:block;'></div>");
                        var iframeloading = $(".l-tab-loading:first", contentitem);
                        iframe.bind('load.tab', function ()
                        {
                            iframeloading.hide();
                        });
                    }
                }
            });
            //init 
            g.selectTabItem(g.selectedTabId);
            //set content height
            if (p.height)
            {
                if (typeof (p.height) == 'string' && p.height.indexOf('%') > 0)
                {
                    g.onResize();
                    if (p.changeHeightOnResize)
                    {
                        $(window).resize(function ()
                        {
                            g.onResize.call(g);
                        });
                    }
                } else
                {
                    g.setHeight(p.height);
                }
            }
            if (g.makeFullHeight)
                g.setContentHeight();
            //add even 
            $("li", g.tab.links).each(function ()
            {
                g._addTabItemEvent($(this));
            });
            g.tab.bind('dblclick.tab', function (e)
            {
                if (!p.dblClickToClose) return;
                g.dblclicking = true;
                var obj = (e.target || e.srcElement);
                var tagName = obj.tagName.toLowerCase();
                if (tagName == "a")
                {
                    var tabid = $(obj).parent().attr("tabid");
                    var allowClose = $(obj).parent().find("div.l-tab-links-item-close").length ? true : false;
                    if (allowClose)
                    {
                        g.removeTabItem(tabid);
                    }
                }
                g.dblclicking = false;
            });

            g.set(p);
        },
        _applyDrag: function (tabItemDom)
        {
            var g = this, p = this.options;
            g.droptip = g.droptip || $("<div class='l-tab-drag-droptip' style='display:none'><div class='l-drop-move-up'></div><div class='l-drop-move-down'></div></div>").appendTo('body');
            var drag = $(tabItemDom).ligerDrag(
            {
                revert: true, animate: false,
                proxy: function ()
                {
                    var name = $(this).find("a").html();
                    g.dragproxy = $("<div class='l-tab-drag-proxy' style='display:none'><div class='l-drop-icon l-drop-no'></div></div>").appendTo('body');
                    g.dragproxy.append(name);
                    return g.dragproxy;
                },
                onRendered: function ()
                {
                    this.set('cursor', 'pointer');
                },
                onStartDrag: function (current, e)
                {
                    if (!$(tabItemDom).hasClass("l-selected")) return false;
                    if (e.button == 2) return false;
                    var obj = e.srcElement || e.target;
                    if ($(obj).hasClass("l-tab-links-item-close")) return false;
                },
                onDrag: function (current, e)
                {
                    if (g.dropIn == null)
                        g.dropIn = -1;
                    var tabItems = g.tab.links.ul.find('>li');
                    var targetIndex = tabItems.index(current.target);
                    tabItems.each(function (i, item)
                    {
                        if (targetIndex == i)
                        {
                            return;
                        }
                        var isAfter = i > targetIndex;
                        if (g.dropIn != -1 && g.dropIn != i) return;
                        var offset = $(this).offset();
                        var range = {
                            top: offset.top,
                            bottom: offset.top + $(this).height(),
                            left: offset.left - 10,
                            right: offset.left + 10
                        };
                        if (isAfter)
                        {
                            range.left += $(this).width();
                            range.right += $(this).width();
                        }
                        var pageX = e.pageX || e.screenX;
                        var pageY = e.pageY || e.screenY;
                        if (pageX > range.left && pageX < range.right && pageY > range.top && pageY < range.bottom)
                        {
                            g.droptip.css({
                                left: range.left + 5,
                                top: range.top - 9
                            }).show();
                            g.dropIn = i;
                            g.dragproxy.find(".l-drop-icon").removeClass("l-drop-no").addClass("l-drop-yes");
                        }
                        else
                        {
                            g.dropIn = -1;
                            g.droptip.hide();
                            g.dragproxy.find(".l-drop-icon").removeClass("l-drop-yes").addClass("l-drop-no");
                        }
                    });
                },
                onStopDrag: function (current, e)
                {
                    if (g.dropIn > -1)
                    {
                        var to = g.tab.links.ul.find('>li:eq(' + g.dropIn + ')').attr("tabid");
                        var from = $(current.target).attr("tabid");
                        setTimeout(function ()
                        {
                            g.moveTabItem(from, to);
                        }, 0);
                        g.dropIn = -1;
                        g.dragproxy.remove();
                    }
                    g.droptip.hide();
                    this.set('cursor', 'default');
                }
            });
            return drag;
        },
        _setDragToMove: function (value)
        {
            if (!$.fn.ligerDrag) return; //需要ligerDrag的支持
            var g = this, p = this.options;
            if (value)
            {
                if (g.drags) return;
                g.drags = g.drags || [];
                g.tab.links.ul.find('>li').each(function ()
                {
                    g.drags.push(g._applyDrag(this));
                });
            }
        },
        moveTabItem: function (fromTabItemID, toTabItemID)
        {
            var g = this;
            var from = g.tab.links.ul.find(">li[tabid=" + fromTabItemID + "]");
            var to = g.tab.links.ul.find(">li[tabid=" + toTabItemID + "]");
            var index1 = g.tab.links.ul.find(">li").index(from);
            var index2 = g.tab.links.ul.find(">li").index(to);
            if (index1 < index2)
            {
                to.after(from);
            }
            else
            {
                to.before(from);
            }
        },
        //设置tab按钮(左和右),显示返回true,隐藏返回false
        setTabButton: function ()
        {
            var g = this, p = this.options;
            var sumwidth = 0;
            $("li", g.tab.links.ul).each(function ()
            {
                sumwidth += $(this).width() + 2;
            });
            var mainwidth = g.tab.width();
            if (sumwidth > mainwidth)
            {
                g.tab.links.append('<div class="l-tab-links-left"></div><div class="l-tab-links-right"></div>');
                g.setTabButtonEven();
                return true;
            } else
            {
                g.tab.links.ul.animate({ left: 0 });
                $(".l-tab-links-left,.l-tab-links-right", g.tab.links).remove();
                return false;
            }
        },
        //设置左右按钮的事件 标签超出最大宽度时，可左右拖动
        setTabButtonEven: function ()
        {
            var g = this, p = this.options;
            $(".l-tab-links-left", g.tab.links).hover(function ()
            {
                $(this).addClass("l-tab-links-left-over");
            }, function ()
            {
                $(this).removeClass("l-tab-links-left-over");
            }).click(function ()
            {
                g.moveToPrevTabItem();
            });
            $(".l-tab-links-right", g.tab.links).hover(function ()
            {
                $(this).addClass("l-tab-links-right-over");
            }, function ()
            {
                $(this).removeClass("l-tab-links-right-over");
            }).click(function ()
            {
                g.moveToNextTabItem();
            });
        },
        //切换到上一个tab
        moveToPrevTabItem: function ()
        {
            var g = this, p = this.options;
            var btnWitdth = $(".l-tab-links-left", g.tab.links).width();
            var leftList = new Array(); //记录每个tab的left,由左到右
            $("li", g.tab.links).each(function (i, item)
            {
                var currentItemLeft = -1 * btnWitdth;
                if (i > 0)
                {
                    currentItemLeft = parseInt(leftList[i - 1]) + $(this).prev().width() + 2;
                }
                leftList.push(currentItemLeft);
            });
            var currentLeft = -1 * parseInt(g.tab.links.ul.css("left"));
            for (var i = 0; i < leftList.length - 1; i++)
            {
                if (leftList[i] < currentLeft && leftList[i + 1] >= currentLeft)
                {
                    g.tab.links.ul.animate({ left: -1 * parseInt(leftList[i]) });
                    return;
                }
            }
        },
        //切换到下一个tab
        moveToNextTabItem: function ()
        {
            var g = this, p = this.options;
            var btnWitdth = $(".l-tab-links-right", g.tab).width();
            var sumwidth = 0;
            var tabItems = $("li", g.tab.links.ul);
            tabItems.each(function ()
            {
                sumwidth += $(this).width() + 2;
            });
            var mainwidth = g.tab.width();
            var leftList = new Array(); //记录每个tab的left,由右到左 
            for (var i = tabItems.length - 1; i >= 0; i--)
            {
                var currentItemLeft = sumwidth - mainwidth + btnWitdth + 2;
                if (i != tabItems.length - 1)
                {
                    currentItemLeft = parseInt(leftList[tabItems.length - 2 - i]) - $(tabItems[i + 1]).width() - 2;
                }
                leftList.push(currentItemLeft);
            }
            var currentLeft = -1 * parseInt(g.tab.links.ul.css("left"));
            for (var j = 1; j < leftList.length; j++)
            {
                if (leftList[j] <= currentLeft && leftList[j - 1] > currentLeft)
                {
                    g.tab.links.ul.animate({ left: -1 * parseInt(leftList[j - 1]) });
                    return;
                }
            }
        },
        getTabItemCount: function ()
        {
            var g = this, p = this.options;
            return $("li", g.tab.links.ul).length;
        },
        getSelectedTabItemID: function ()
        {
            var g = this, p = this.options;
            return $("li.l-selected", g.tab.links.ul).attr("tabid");
        },
        removeSelectedTabItem: function ()
        {
            var g = this, p = this.options;
            g.removeTabItem(g.getSelectedTabItemID());
        },
        //覆盖选择的tabitem
        overrideSelectedTabItem: function (options)
        {
            var g = this, p = this.options;
            g.overrideTabItem(g.getSelectedTabItemID(), options);
        },
        //覆盖
        overrideTabItem: function (targettabid, options)
        {
            var g = this, p = this.options;
            if (g.trigger('beforeOverrideTabItem', [targettabid]) == false)
                return false;
            var tabid = options.tabid;
            if (tabid == undefined) tabid = g.getNewTabid();
            var url = options.url;
            var content = options.content;
            var target = options.target;
            var text = options.text;
            var showClose = options.showClose;
            var height = options.height;
            //如果已经存在
            if (g.isTabItemExist(tabid))
            {
                return;
            }
            var tabitem = $("li[tabid=" + targettabid + "]", g.tab.links.ul);
            var contentitem = $(".l-tab-content-item[tabid=" + targettabid + "]", g.tab.content);
            if (!tabitem || !contentitem) return;
            tabitem.attr("tabid", tabid);
            contentitem.attr("tabid", tabid);
            if ($("iframe", contentitem).length == 0 && url)
            {
                contentitem.html("<iframe frameborder='0'></iframe>");
            }
            else if (content)
            {
                contentitem.html(content);
            }
            $("iframe", contentitem).attr("name", tabid);
            if (showClose == undefined) showClose = true;
            if (showClose == false) $(".l-tab-links-item-close", tabitem).remove();
            else
            {
                if ($(".l-tab-links-item-close", tabitem).length == 0)
                    tabitem.append("<div class='l-tab-links-item-close'></div>");
            }
            if (text == undefined) text = tabid;
            if (height) contentitem.height(height);
            $("a", tabitem).text(text);
            $("iframe", contentitem).attr("src", url);


            g.trigger('afterOverrideTabItem', [targettabid]);
        },
        //设置页签项标题
        setHeader: function(tabid,header)
        { 
            $("li[tabid=" + tabid + "] a", this.tab.links.ul).text(header);
        },
        //选中tab项
        selectTabItem: function (tabid)
        {
            var g = this, p = this.options;
            if (g.trigger('beforeSelectTabItem', [tabid]) == false)
                return false;
            g.selectedTabId = tabid;
            $("> .l-tab-content-item[tabid=" + tabid + "]", g.tab.content).show().siblings().hide();
            $("li[tabid=" + tabid + "]", g.tab.links.ul).addClass("l-selected").siblings().removeClass("l-selected");
            g.trigger('afterSelectTabItem', [tabid]);
        },
        //移动到最后一个tab
        moveToLastTabItem: function ()
        {
            var g = this, p = this.options;
            var sumwidth = 0;
            $("li", g.tab.links.ul).each(function ()
            {
                sumwidth += $(this).width() + 2;
            });
            var mainwidth = g.tab.width();
            if (sumwidth > mainwidth)
            {
                var btnWitdth = $(".l-tab-links-right", g.tab.links).width();
                g.tab.links.ul.animate({ left: -1 * (sumwidth - mainwidth + btnWitdth + 2) });
            }
        },
        //判断tab是否存在
        isTabItemExist: function (tabid)
        {
            var g = this, p = this.options;
            return $("li[tabid=" + tabid + "]", g.tab.links.ul).length > 0;
        },
        //增加一个tab
        addTabItem: function (options)
        {
            var g = this, p = this.options;
            if (g.trigger('beforeAddTabItem', [tabid]) == false)
                return false;
            var tabid = options.tabid;
            if (tabid == undefined) tabid = g.getNewTabid();
            var url = options.url;
            var content = options.content;
            var text = options.text;
            var showClose = options.showClose;
            var height = options.height;
            //如果已经存在
            if (g.isTabItemExist(tabid))
            {
                g.selectTabItem(tabid);
                return;
            }
            var tabitem = $("<li><a></a><div class='l-tab-links-item-left'></div><div class='l-tab-links-item-right'></div><div class='l-tab-links-item-close'></div></li>");
            var contentitem = $("<div class='l-tab-content-item'><div class='l-tab-loading' style='display:block;'></div><iframe frameborder='0'></iframe></div>");
            var iframeloading = $("div:first", contentitem);
            var iframe = $("iframe:first", contentitem);
            if (g.makeFullHeight)
            {
                var newheight = g.tab.height() - g.tab.links.height();
                contentitem.height(newheight);
            }
            tabitem.attr("tabid", tabid);
            contentitem.attr("tabid", tabid);
            if (url)
            {
                iframe.attr("name", tabid)
                 .attr("id", tabid)
                 .attr("src", url)
                 .bind('load.tab', function ()
                 {
                     iframeloading.hide();
                     if (options.callback)
                         options.callback();
                 });
            }
            else
            {
                iframe.remove(); 
                iframeloading.remove();
            }
            if (content)
            {
                contentitem.html(content);
            }
            else if (options.target)
            {
                contentitem.append(options.target);
            }
            if (showClose == undefined) showClose = true;
            if (showClose == false) $(".l-tab-links-item-close", tabitem).remove();
            if (text == undefined) text = tabid;
            if (height) contentitem.height(height);
			//modify by songxf at 2013-6-26
            //$("a", tabitem).text(text);
			$("a", tabitem).html(text);
			//modify end
            g.tab.links.ul.append(tabitem);
            g.tab.content.append(contentitem);
            g.selectTabItem(tabid);
			//modify by songxf at 2013-6-26
            //if (g.setTabButton())
			if (g.setTabButton() && p.moveToNew)
			//modify end
            {
                g.moveToLastTabItem();
            }
            //增加事件
            g._addTabItemEvent(tabitem);
            if (p.dragToMove && $.fn.ligerDrag)
            {
                g.drags = g.drags || [];
                tabitem.each(function ()
                {
                    g.drags.push(g._applyDrag(this));
                });
            }
            g.trigger('afterAddTabItem', [tabid]);
        },
        _addTabItemEvent: function (tabitem)
        {
            var g = this, p = this.options;
            tabitem.click(function ()
            {
                var tabid = $(this).attr("tabid");
                g.selectTabItem(tabid);
            });
            //右键事件支持
            g.tab.menu && g._addTabItemContextMenuEven(tabitem);
            $(".l-tab-links-item-close", tabitem).hover(function ()
            {
                $(this).addClass("l-tab-links-item-close-over");
            }, function ()
            {
                $(this).removeClass("l-tab-links-item-close-over");
            }).click(function ()
            {
                var tabid = $(this).parent().attr("tabid");
                g.removeTabItem(tabid);
            });

        },
        //移除tab项
        removeTabItem: function (tabid)
        {
            var g = this, p = this.options;
            if (g.trigger('beforeRemoveTabItem', [tabid]) == false)
                return false;
            var currentIsSelected = $("li[tabid=" + tabid + "]", g.tab.links.ul).hasClass("l-selected");
            if (currentIsSelected)
            {
                $(".l-tab-content-item[tabid=" + tabid + "]", g.tab.content).prev().show();
                $("li[tabid=" + tabid + "]", g.tab.links.ul).prev().addClass("l-selected").siblings().removeClass("l-selected");
            }
            var contentItem = $(".l-tab-content-item[tabid=" + tabid + "]", g.tab.content); 
            var jframe = $('iframe', contentItem); 
            if (jframe.length)
            {
                var frame = jframe[0];
                frame.src = "about:blank";
                frame.contentWindow.document.write('');
                $.browser.msie && CollectGarbage();
                jframe.remove();
            } 
            contentItem.remove();
            $("li[tabid=" + tabid + "]", g.tab.links.ul).remove();
            g.setTabButton();
            g.trigger('afterRemoveTabItem', [tabid]);
        },
        addHeight: function (heightDiff)
        {
            var g = this, p = this.options;
            var newHeight = g.tab.height() + heightDiff;
            g.setHeight(newHeight);
        },
        setHeight: function (height)
        {
            var g = this, p = this.options;
            g.tab.height(height);
            g.setContentHeight();
        },
        setContentHeight: function ()
        {
            var g = this, p = this.options;
            var newheight = g.tab.height() - g.tab.links.height();
            g.tab.content.height(newheight);
            $("> .l-tab-content-item", g.tab.content).height(newheight);
        },
        getNewTabid: function ()
        {
            var g = this, p = this.options;
            g.getnewidcount = g.getnewidcount || 0;
            return 'tabitem' + (++g.getnewidcount);
        },
        //notabid 过滤掉tabid的
        //noclose 过滤掉没有关闭按钮的
        getTabidList: function (notabid, noclose)
        {
            var g = this, p = this.options;
            var tabidlist = [];
            $("> li", g.tab.links.ul).each(function ()
            {
                if ($(this).attr("tabid")
                        && $(this).attr("tabid") != notabid
                        && (!noclose || $(".l-tab-links-item-close", this).length > 0))
                {
                    tabidlist.push($(this).attr("tabid"));
                }
            });
            return tabidlist;
        },
        removeOther: function (tabid, compel)
        {
            var g = this, p = this.options;
            var tabidlist = g.getTabidList(tabid, true);
            $(tabidlist).each(function ()
            {
                g.removeTabItem(this);
            });
        },
        reload: function (tabid)
        {
            var g = this, p = this.options;
            var contentitem = $(".l-tab-content-item[tabid=" + tabid + "]");
            var iframeloading = $(".l-tab-loading:first", contentitem);
            var iframe = $("iframe:first", contentitem);
            var url = $(iframe).attr("src");
            iframeloading.show();
            iframe.attr("src", url).unbind('load.tab').bind('load.tab', function ()
            {
                iframeloading.hide();
            });
        },
        removeAll: function (compel)
        {
            var g = this, p = this.options;
            var tabidlist = g.getTabidList(null, true);
            $(tabidlist).each(function ()
            {
                g.removeTabItem(this);
            });
        },
        onResize: function ()
        {
            var g = this, p = this.options;
            if (!p.height || typeof (p.height) != 'string' || p.height.indexOf('%') == -1) return false;
            //set tab height
            if (g.tab.parent()[0].tagName.toLowerCase() == "body")
            {
                var windowHeight = $(window).height();
                windowHeight -= parseInt(g.tab.parent().css('paddingTop'));
                windowHeight -= parseInt(g.tab.parent().css('paddingBottom'));
                g.height = p.heightDiff + windowHeight * parseFloat(g.height) * 0.01;
            }
            else
            {
                g.height = p.heightDiff + (g.tab.parent().height() * parseFloat(p.height) * 0.01);
            }
            g.tab.height(g.height);
            g.setContentHeight();
        },
        _menuItemClick: function (item)
        {
            var g = this, p = this.options;
            if (!item.id || !g.actionTabid) return;
            switch (item.id)
            {
                case "close":
                    g.removeTabItem(g.actionTabid);
                    g.actionTabid = null;
                    break;
                case "closeother":
                    g.removeOther(g.actionTabid);
                    break;
                case "closeall":
                    g.removeAll();
                    g.actionTabid = null;
                    break;
                case "reload":
                    g.selectTabItem(g.actionTabid);
                    g.reload(g.actionTabid);
                    break;
            }
        },
        _addTabItemContextMenuEven: function (tabitem)
        {
            var g = this, p = this.options;
            tabitem.bind("contextmenu", function (e)
            {
                if (!g.tab.menu) return;
                g.actionTabid = tabitem.attr("tabid");
                g.tab.menu.show({ top: e.pageY, left: e.pageX });
                if ($(".l-tab-links-item-close", this).length == 0)
                {
                    g.tab.menu.setDisabled('close');
                }
                else
                {
                    g.tab.menu.setEnabled('close');
                }
                return false;
            });
        }
    });



})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerTextBox = function ()
    {
        return $.ligerui.run.call(this, "ligerTextBox", arguments);
    };

    $.fn.ligerGetTextBoxManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetTextBoxManager", arguments);
    };

    $.ligerDefaults.TextBox = {
        onChangeValue: null,
        onMouseOver: null,
        onMouseOut: null,
        onBlur: null,
        onFocus: null,
        width: null,
        disabled: false, 
        value: null,     //初始化值 
        nullText: null,   //不能为空时的提示
        digits: false,     //是否限定为数字输入框
        number: false,    //是否限定为浮点数格式输入框
        currency: false,     //是否显示为货币形式
        //modify by tanxu at 2013-8-28
        readonly: null              //是否只读
        //end modify
    };


    $.ligerui.controls.TextBox = function (element, options)
    {
        $.ligerui.controls.TextBox.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.TextBox.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'TextBox'
        },
        __idPrev: function ()
        {
            return 'TextBox';
        },
        _init: function ()
        {
            $.ligerui.controls.TextBox.base._init.call(this);
            var g = this, p = this.options;
            if (!p.width)
            {
                p.width = $(g.element).width();
            }
            if ($(this.element).attr("readonly"))
            {
                p.readonly = true;
            } else if (p.readonly)
            {
                $(this.element).attr("readonly", true);
            }
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.inputText = $(this.element);
            //外层
            g.wrapper = g.inputText.wrap('<div class="l-text"></div>').parent();
            g.wrapper.append('<div class="l-text-l"></div><div class="l-text-r"></div>');
            if (!g.inputText.hasClass("l-text-field"))
                g.inputText.addClass("l-text-field");
            //modify by songxf at 2013-6-26
            //modify by tanxu at 2013-8-30
            if(p.disabled){
                this.setDisabled();
            }
            //modify end
            this._setEvent();
            g.set(p);
            g.checkValue();
        },
        _getValue: function ()
        {
            return this.inputText.val();
        },
        _setNullText: function ()
        {
            this.checkNotNull();
        },
        checkValue: function ()
        {
            var g = this, p = this.options;
            var v = g.inputText.val() || "";
            if (p.currency) v = v.replace(/\$|\,/g, '');
            var isFloat = p.number || p.currency, isDigits = p.digits;
            if (isFloat && !/^-?(?:\d+|\d{1,3}(?:,\d{3})+)(?:\.\d+)?$/.test(v) || isDigits && !/^\d+$/.test(v))
            {
                //不符合,恢复到原来的值
                g.inputText.val(g.value || 0);
                p.currency && g.inputText.val(currencyFormatter(g.value));
                return;
            }
            g.value = v;
            p.currency && g.inputText.val(currencyFormatter(g.value));
        },
        checkNotNull: function ()
        {
            var g = this, p = this.options;
            if (p.nullText && !p.disabled)
            {
                if (!g.inputText.val())
                {
                    g.inputText.addClass("l-text-field-null").val(p.nullText);
                }
            }
        },
        _setEvent: function ()
        {
            var g = this, p = this.options;
            g.inputText.bind('blur.textBox', function ()
            {
                g.trigger('blur');
                g.checkNotNull();
                g.checkValue();
                g.wrapper.removeClass("l-text-focus");
            }).bind('focus.textBox', function ()
            {
                g.trigger('focus');
                if (p.nullText)
                {
                    if ($(this).hasClass("l-text-field-null"))
                    {
                        $(this).removeClass("l-text-field-null").val("");
                    }
                }
                g.wrapper.addClass("l-text-focus");
            })
            .change(function ()
            {
                g.trigger('changeValue', [this.value]);
            });
            g.wrapper.hover(function ()
            {
                g.trigger('mouseOver');
                g.wrapper.addClass("l-text-over");
            }, function ()
            {
                g.trigger('mouseOut');
                g.wrapper.removeClass("l-text-over");
            });
        },
        _setDisabled: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                this.inputText.attr("readonly", "readonly");
                this.wrapper.addClass("l-text-disabled");
            }
            else if(!p.readonly)
            {
                this.inputText.removeAttr("readonly");
                this.wrapper.removeClass('l-text-disabled');
            }
        }, 
        _setWidth: function (value)
        {
            if (value > 20)
            {
                this.wrapper.css({ width: value });
                this.inputText.css({ width: value - 4 });
            }
        },
        _setHeight: function (value)
        {
            if (value > 10)
            {
                this.wrapper.height(value);
                this.inputText.height(value - 2);
            }
        },
        _setValue: function (value)
        {
            if (value != null)
                this.inputText.val(value);
        },
        _setLabel: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper)
            {
                g.labelwrapper = g.wrapper.wrap('<div class="l-labeltext"></div>').parent();
                var lable = $('<div class="l-text-label" style="float:left;">' + value + ':&nbsp</div>');
                g.labelwrapper.prepend(lable);
                g.wrapper.css('float', 'left');
                if (!p.labelWidth)
                {
                    p.labelWidth = lable.width();
                }
                else
                {
                    g._setLabelWidth(p.labelWidth);
                }
                lable.height(g.wrapper.height());
                if (p.labelAlign)
                {
                    g._setLabelAlign(p.labelAlign);
                }
                g.labelwrapper.append('<br style="clear:both;" />');
                g.labelwrapper.width(p.labelWidth + p.width + 2);
            }
            else
            {
                g.labelwrapper.find(".l-text-label").html(value + ':&nbsp');
            }
        },
        _setLabelWidth: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper) return;
            g.labelwrapper.find(".l-text-label").width(value);
        },
        _setLabelAlign: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper) return;
            g.labelwrapper.find(".l-text-label").css('text-align', value);
        },
        updateStyle: function ()
        {
            var g = this, p = this.options;
            if (g.inputText.attr('readonly'))
            {
                g.wrapper.addClass("l-text-readonly");
                p.disabled = true;
            }
            else
            {
                g.wrapper.removeClass("l-text-readonly");
                p.disabled = false;
            }
            if (g.inputText.attr('disabled'))
            {
                g.wrapper.addClass("l-text-disabled");
                p.disabled = true;
            }
            else
            {
                g.wrapper.removeClass("l-text-disabled");
                p.disabled = false;
            }
            if (g.inputText.hasClass("l-text-field-null") && g.inputText.val() != p.nullText)
            {
                g.inputText.removeClass("l-text-field-null");
            }
            g.checkValue();
        }
    });

    function currencyFormatter(num)
    {
        if (!num) return "0.00";
        num = num.toString().replace(/\$|\,/g, '');
        if (isNaN(num))
            num = "0.00";
        sign = (num == (num = Math.abs(num)));
        num = Math.floor(num * 100 + 0.50000000001);
        cents = num % 100;
        num = Math.floor(num / 100).toString();
        if (cents < 10)
            cents = "0" + cents;
        for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3) ; i++)
            num = num.substring(0, num.length - (4 * i + 3)) + ',' +
            num.substring(num.length - (4 * i + 3));
        return "" + (((sign) ? '' : '-') + '' + num + '.' + cents);
    }

})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/

(function ($)
{
    //气泡,可以在制定位置显示
    $.ligerTip = function (p)
    {
        return $.ligerui.run.call(null, "ligerTip", arguments);
    };

    //在指定Dom Element右侧显示气泡
    //target：将ligerui对象ID附加上
    $.fn.ligerTip = function (options)
    {
        this.each(function ()
        {
            var p = $.extend({}, $.ligerDefaults.ElementTip, options || {});
            p.target = p.target || this;
            //如果是自动模式：鼠标经过时显示，移开时关闭
            if (p.auto || options == undefined)
            {
                if (!p.content)
                {
                    p.content = this.title;
                    if (p.removeTitle)
                        $(this).removeAttr("title");
                }
                p.content = p.content || this.title;
                $(this).bind('mouseover.tip', function ()
                {
					//modify  by songxf at 2013-6-26
                    //p.x = $(this).offset().left + $(this).width() + (p.distanceX || 0);
					var winwidth=$(document).width();
                    if(winwidth-($(this).offset().left + $(this).width() )<170){
                          p.position='left';
                          p.x = $(this).offset().left - $(this).width()-150- (p.distanceX || 0);
                    }else{
                          p.position='right';
                          p.x = $(this).offset().left + $(this).width() + (p.distanceX || 0);
                    }
					//modify  end
                    p.y = $(this).offset().top + (p.distanceY || 0);
                    $.ligerTip(p);
                }).bind('mouseout.tip', function ()
                {

                    var tipmanager = $.ligerui.managers[this.ligeruitipid];
                    if (tipmanager)
                    {
                        tipmanager.remove();
                    }
                });
            }
            else
            {
                if (p.target.ligeruitipid) return;
                p.x = $(this).offset().left + $(this).width() + (p.distanceX || 0);
                p.y = $(this).offset().top + (p.distanceY || 0);
                p.x = p.x || 0;
                p.y = p.y || 0;
				//modify by songxf at 2013-6-26
				p.position='right';
				//modify end
                $.ligerTip(p);
            }
        });
        return $.ligerui.get(this, 'ligeruitipid');
    };
    //关闭指定在Dom Element(附加了ligerui对象ID,属性名"ligeruitipid")显示的气泡
    $.fn.ligerHideTip = function (options)
    {
        return this.each(function ()
        {
            var p = options || {};
            if (p.isLabel == undefined)
            {
                //如果是lable，将查找指定的input，并找到ligerui对象ID
                p.isLabel = this.tagName.toLowerCase() == "label" && $(this).attr("for") != null;
            }
            var target = this;
            if (p.isLabel)
            {
                var forele = $("#" + $(this).attr("for"));
                if (forele.length == 0) return;
                target = forele[0];
            }
            var tipmanager = $.ligerui.managers[target.ligeruitipid];
            if (tipmanager)
            {
                tipmanager.remove();
            }
        }).unbind('mouseover.tip').unbind('mouseout.tip');
    };


    $.fn.ligerGetTipManager = function ()
    {
        return $.ligerui.get(this);
    };


    $.ligerDefaults = $.ligerDefaults || {};


    //隐藏气泡
    $.ligerDefaults.HideTip = {};

    //气泡
    $.ligerDefaults.Tip = {
        content: null,
        callback: null,
        width: 150,
        height: null,
        x: 0,
        y: 0,
        appendIdTo: null,       //保存ID到那一个对象(jQuery)(待移除)
        target: null,
        auto: null,             //是否自动模式，如果是，那么：鼠标经过时显示，移开时关闭,并且当content为空时自动读取attr[title]
        removeTitle: true        //自动模式时，默认是否移除掉title
    };

    //在指定Dom Element右侧显示气泡,通过$.fn.ligerTip调用
    $.ligerDefaults.ElementTip = {
        distanceX: 1,
        distanceY: -3,
        auto: null,
        removeTitle: true
    };

    $.ligerMethos.Tip = {};

    $.ligerui.controls.Tip = function (options)
    {
        $.ligerui.controls.Tip.base.constructor.call(this, null, options);
    };
    $.ligerui.controls.Tip.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'Tip';
        },
        __idPrev: function ()
        {
            return 'Tip';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Tip;
        },
		//modify by songxf at 2013-6-26
        //_render: function ()
        //{
        //    var g = this, p = this.options;
        //    var tip = $('<div class="l-verify-tip"><div class="l-verify-tip-corner"></div><div class="l-verify-tip-content"></div></div>');
        //    g.tip = tip;
        //    g.tip.attr("id", g.id);
        //    if (p.content)
        //    {
        //        $("> .l-verify-tip-content:first", tip).html(p.content);
        //        tip.appendTo('body');
        //    }
        //    else
        //    {
        //        return;
        //    }
        //    tip.css({ left: p.x, top: p.y }).show();
        //    p.width && $("> .l-verify-tip-content:first", tip).width(p.width - 8);
        //    p.height && $("> .l-verify-tip-content:first", tip).width(p.height);
        //    eee = p.appendIdTo;
        //    if (p.appendIdTo)
        //    {
        //        p.appendIdTo.attr("ligerTipId", g.id);
        //    }
        //    if (p.target)
        //    {
        //        $(p.target).attr("ligerTipId", g.id);
        //        p.target.ligeruitipid = g.id;
        //    }
        //    p.callback && p.callback(tip);
        //    g.set(p);
        //},
		_render: function ()
        {
            var g = this, p = this.options;
            var tip=null;
            if(p.position&&p.position=='left'){
                 tip=$('<div class="l-verify-tip"><div class="l-verify-tip-content-left"></div><div class="l-verify-tip-corner-left"></div></div>');
            }else{
                 tip = $('<div class="l-verify-tip"><div class="l-verify-tip-corner"></div><div class="l-verify-tip-content"></div></div>');
            }
            g.tip = tip;
            g.tip.attr("id", g.id);
            if (p.content)
            {
                  if(p.position&&p.position=='left')
                     $("> .l-verify-tip-content-left:first", tip).html(p.content);
                else
                         $("> .l-verify-tip-content:first", tip).html(p.content);
                tip.appendTo('body');
            }
            else
            {
                return;
            }
               tip.css({ left: p.x, top: p.y }).show();
               if(p.position&&p.position=='left'){
                 p.width && $("> .l-verify-tip-content-left:first", tip).width(p.width-8);
                     p.height && $("> .l-verify-tip-content-left:first", tip).width(p.height);
            }else{
                 p.width && $("> .l-verify-tip-content:first", tip).width(p.width - 8);
                     p.height && $("> .l-verify-tip-content:first", tip).width(p.height);
            }
           
            //modify by songxuefeng 解决验证浮动框不能关闭的问题
            tip.bind('mouseout', function ()
            {
                 tip.remove();        
            });
            //modify finished
            if (p.appendIdTo)
            {
                p.appendIdTo.attr("ligerTipId", g.id);
            }
            if (p.target)
            {
                $(p.target).attr("ligerTipId", g.id);
                p.target.ligeruitipid = g.id;
            }
           
            p.callback && p.callback(tip);
            g.set(p);
        },
		//modify end
        _setContent: function (content)
        {
            $("> .l-verify-tip-content:first", this.tip).html(content);
        },
        remove: function ()
        {
            if (this.options.appendIdTo)
            {
                this.options.appendIdTo.removeAttr("ligerTipId");
            }
            if (this.options.target)
            {
                $(this.options.target).removeAttr("ligerTipId");
                this.options.target.ligeruitipid = null;
            }
            this.tip.remove();
        }
    });
})(jQuery);﻿﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    $.fn.ligerToolBar = function (options)
    {
        return $.ligerui.run.call(this, "ligerToolBar", arguments);
    };

    $.fn.ligerGetToolBarManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetToolBarManager", arguments);
    };

    $.ligerDefaults.ToolBar = {};

    $.ligerMethos.ToolBar = {};

    $.ligerui.controls.ToolBar = function (element, options)
    {
        $.ligerui.controls.ToolBar.base.constructor.call(this, element, options);
    };
    $.ligerui.controls.ToolBar.ligerExtend($.ligerui.core.UIComponent, {
        __getType: function ()
        {
            return 'ToolBar';
        },
        __idPrev: function ()
        {
            return 'ToolBar';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.ToolBar;
        },
        _render: function ()
        {
            var g = this, p = this.options;
			g.toolbarItemCount = 0;
            g.toolBar = $(this.element);
            g.toolBar.addClass("l-toolbar");
            g.set(p);
        },
        _setItems: function (items)
        {
            var g = this;
            g.toolBar.html("");
            $(items).each(function (i, item)
            {
                g.addItem(item);
            });
        },
		removeItem: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-toolbar-item[toolbarid=" + itemid + "]", g.toolBar).remove();
        },
        setEnabled: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-toolbar-item[toolbarid=" + itemid + "]", g.toolBar).removeClass("l-toolbar-item-disable");
        },
        setDisabled: function (itemid)
        {
            var g = this, p = this.options;
            $("> .l-toolbar-item[toolbarid=" + itemid + "]", g.toolBar).addClass("l-toolbar-item-disable");
        },
        isEnable: function (itemid)
        {
            var g = this, p = this.options;
            return !$("> .l-toolbar-item[toolbarid=" + itemid + "]", g.toolBar).hasClass("l-toolbar-item-disable");
        },
		//modify by songxf at 2013-6-26
        //addItem: function (item)
        //{
        //    var g = this, p = this.options;
        //    if (item.line)
        //    {
        //        g.toolBar.append('<div class="l-bar-separator"></div>');
        //        return;
        //    }
        //    var ditem = $('<div class="l-toolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div></div>');
        //    g.toolBar.append(ditem);
        //    if(!item.id) item.id = 'item-'+(++g.toolbarItemCount);
		//	ditem.attr("toolbarid", item.id);
        //    if (item.img)
        //    {
        //        ditem.append("<img src='" + item.img + "' />");
        //        ditem.addClass("l-toolbar-item-hasicon");
        //    }
        //    else if (item.icon)
        //    {
        //        ditem.append("<div class='l-icon l-icon-" + item.icon + "'></div>");
        //        ditem.addClass("l-toolbar-item-hasicon");
        //    }
		//	else if (item.color)
		//	{
		//		ditem.append("<div class='l-toolbar-item-color' style='background:"+item.color+"'></div>");
        //        ditem.addClass("l-toolbar-item-hasicon");
		//	}
        //    item.text && $("span:first", ditem).html(item.text);
        //    item.disable && ditem.addClass("l-toolbar-item-disable");
        //    item.click && ditem.click(function () { if ($(this).hasClass("l-toolbar-item-disable")) return;item.click(item); });
		//	if (item.menu)
        //    {
        //        item.menu = $.ligerMenu(item.menu);
        //        ditem.hover(function ()
        //        {
		//			if ($(this).hasClass("l-toolbar-item-disable")) return;
        //            g.actionMenu && g.actionMenu.hide();
        //            var left = $(this).offset().left;
        //            var top = $(this).offset().top + $(this).height();
        //            item.menu.show({ top: top, left: left });
        //            g.actionMenu = item.menu;
        //            $(this).addClass("l-panel-btn-over");
        //        }, function ()
        //        {
		//			if ($(this).hasClass("l-toolbar-item-disable")) return;
        //            $(this).removeClass("l-panel-btn-over");
        //        });
        //    }
        //    else
        //    {
        //        ditem.hover(function ()
		//		{
		//			if ($(this).hasClass("l-toolbar-item-disable")) return;
		//			$(this).addClass("l-panel-btn-over");
		//		}, function ()
		//		{
		//			if ($(this).hasClass("l-toolbar-item-disable")) return;
		//			$(this).removeClass("l-panel-btn-over");
		//		});
        //    }
        //}
		addItem : function(item) {
            var g = this, p = this.options;
            if (item.line) {
                 g.toolBar
                           .append('<div class="l-toolbar-separator"></div>');
                 return;
            }
            if (item.menu) {
                 var menucontainer=$('<div class="l-menubar-item"></div>');
                 var ditem = $('<div class="l-toolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
                 menucontainer.append(ditem);
                 g.toolBar.append(menucontainer);
                 item.id && ditem.attr("menubarid", item.id);
                 item.text
                           && $("span:first", ditem).html(
                                     item.text);
                 item.disable
                           && ditem
                                     .addClass("l-menubar-item-disable");
                 //item.click && ditem.click(function() {
                 //     item.click(item);
                 //});
                 if (item.img) {
                      ditem.append("<img src='" + item.img
                                + "' />");
                      ditem.addClass("l-toolbar-item-hasicon");
                 } else if (item.icon) {
                      ditem.append("<div class='l-icon l-icon-"
                                + item.icon + "'></div>");
                      ditem.addClass("l-toolbar-item-hasicon");
                 }
                 item.menu.parent=menucontainer;
                 var menu = $.ligerMenu(item.menu);
                 ditem.hover(
                           function() {
                                if(g.premenu!=undefined){
                                     g.premenu.hide();
                                }
                                g.premenu=menu;
                                $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                                $(this).addClass("l-panel-btn-over");
                           },function(){
            //                    menu.hide();
            //                    $(this).removeClass("l-panel-btn-over");
                           });
                 ditem.click(
                           function(){
                                g.actionMenu&& g.actionMenu.hide();
                                var left = 5;
                                var top = g.toolBar.height();
                                menu.show({
                                     top : top,
                                     left : left
                                });
                                g.actionMenu = menu;
                 });
                 g.toolBar.mouseleave(function(){
                                menu.hide();
                                $(".l-panel-btn-over").removeClass('l-panel-btn-over');

                });
            } else {
                 var ditem = $('<div class="l-toolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div></div>');
                 g.toolBar.append(ditem);
                 item.id && ditem.attr("toolbarid", item.id);
                 if (item.img) {
                      ditem.append("<img src='" + item.img
                                + "' />");
                      ditem.addClass("l-toolbar-item-hasicon");
                 } else if (item.icon) {
                      ditem.append("<div class='l-icon l-icon-"
                                + item.icon + "'></div>");
                      ditem.addClass("l-toolbar-item-hasicon");
                 }
                 item.text
                           && $("span:first", ditem).html(
                                     item.text);
                 item.disable
                           && ditem
                                     .addClass("l-toolbar-item-disable");
                 item.click && ditem.click(function() {
                      item.click(item);
                 });

                 ditem.hover(function() {
                      $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                      $(this).addClass("l-panel-btn-over");
                 }, function() {
                      $(this).removeClass('l-panel-btn-over');
                 });
            }
		}
		//modify end
	});	
	//旧写法保留
    $.ligerui.controls.ToolBar.prototype.setEnable = $.ligerui.controls.ToolBar.prototype.setEnabled;
    $.ligerui.controls.ToolBar.prototype.setDisable = $.ligerui.controls.ToolBar.prototype.setDisabled;
})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{
    $.fn.ligerTree = function (options)
    {
        return $.ligerui.run.call(this, "ligerTree", arguments);
    };

    $.fn.ligerGetTreeManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetTreeManager", arguments);
    };

    $.ligerDefaults.Tree = {
        url: null,
        data: null,
        checkbox: true,
        autoCheckboxEven: true,
        parentIcon: 'folder',
        childIcon: 'leaf',
        textFieldName: 'text',
        attribute: ['id', 'url'],
        treeLine: true,            //是否显示line
        nodeWidth: 90,
        statusName: '__status',
        isLeaf: null,              //是否子节点的判断函数
        single: false,               //是否单选
		needCancel: true,			//已选的是否需要取消操作
        onBeforeExpand: function () { },
        onContextmenu: function () { },
        onExpand: function () { },
        onBeforeCollapse: function () { },
        onCollapse: function () { },
        onBeforeSelect: function () { },
        onSelect: function () { },
        onBeforeCancelSelect: function () { },
        onCancelselect: function () { },
        onCheck: function () { },
        onSuccess: function () { },
        onError: function () { },
        onClick: function () { },
        idFieldName: 'id',
        parentIDFieldName: null,
        topParentIDValue: 0,
        onBeforeAppend: function () { },        //加载数据前事件，可以通过return false取消操作
        onAppend: function () { },             //加载数据时事件，对数据进行预处理以后
        onAfterAppend: function () { },         //加载数据完事件
        slide: true,          //是否以动画的形式显示
        iconFieldName: 'icon',
        nodeDraggable: false,             //是否允许拖拽
        nodeDraggingRender: null,
        btnClickToToggleOnly: true     //是否点击展开/收缩 按钮时才有效
    };

    $.ligerui.controls.Tree = function (element, options)
    {
        $.ligerui.controls.Tree.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.Tree.ligerExtend($.ligerui.core.UIComponent, {
        _init: function ()
        {
            $.ligerui.controls.Tree.base._init.call(this);
            var g = this, p = this.options;
            if (p.single) p.autoCheckboxEven = false;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.set(p, true);
            g.tree = $(g.element);
            g.tree.addClass('l-tree');
            g.sysAttribute = ['isexpand', 'ischecked', 'href', 'style'];
            g.loading = $("<div class='l-tree-loading'></div>");
            g.tree.after(g.loading);
            g.data = [];
            g.maxOutlineLevel = 1;
            g.treedataindex = 0;
            g._applyTree();
            g._setTreeEven();

            g.set(p, false);
        },
        _setTreeLine: function (value)
        {
            if (value) this.tree.removeClass("l-tree-noline");
            else this.tree.addClass("l-tree-noline");
        },
        _setUrl: function (url)
        {
            if (url) this.loadData(null, url);
        },
        _setData: function (data)
        {
            if (data) this.append(null, data);
        },
        setData: function (data)
        {
            this.set('data', data);
        },
        getData: function ()
        {
            return this.data;
        },
        //是否包含子节点
        hasChildren: function (treenodedata)
        {
            if (this.options.isLeaf) return this.options.isLeaf(treenodedata);
            return treenodedata.children ? true : false;
        },
        //获取父节点 数据
        getParent: function (treenode, level)
        {
            var g = this;
            treenode = g.getNodeDom(treenode);
            var parentTreeNode = g.getParentTreeItem(treenode, level);
            if (!parentTreeNode) return null;
            var parentIndex = $(parentTreeNode).attr("treedataindex");
            return g._getDataNodeByTreeDataIndex(parentIndex);
        },
        //获取父节点
        getParentTreeItem: function (treenode, level)
        {
            var g = this;
            treenode = g.getNodeDom(treenode);
            var treeitem = $(treenode);
            if (treeitem.parent().hasClass("l-tree"))
                return null;
            if (level == undefined)
            {
                if (treeitem.parent().parent("li").length == 0)
                    return null;
                return treeitem.parent().parent("li")[0];
            }
            var currentLevel = parseInt(treeitem.attr("outlinelevel"));
            var currenttreeitem = treeitem;
            for (var i = currentLevel - 1; i >= level; i--)
            {
                currenttreeitem = currenttreeitem.parent().parent("li");
            }
            return currenttreeitem[0];
        },
        getChecked: function ()
        {
            var g = this, p = this.options;
            if (!this.options.checkbox) return null;
            var nodes = [];
            $(".l-checkbox-checked", g.tree).parent().parent("li").each(function ()
            {
                var treedataindex = parseInt($(this).attr("treedataindex"));
                nodes.push({ target: this, data: g._getDataNodeByTreeDataIndex(g.data, treedataindex) });
            });
            return nodes;
        },
        getSelected: function ()
        {
            var g = this, p = this.options;
            var node = {};
            node.target = $(".l-selected", g.tree).parent("li")[0];
            if (node.target)
            {
                var treedataindex = parseInt($(node.target).attr("treedataindex"));
                node.data = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                return node;
            }
            return null;
        },
        //升级为父节点级别
        upgrade: function (treeNode)
        {
            var g = this, p = this.options;
            $(".l-note", treeNode).each(function ()
            {
                $(this).removeClass("l-note").addClass("l-expandable-open");
            });
            $(".l-note-last", treeNode).each(function ()
            {
                $(this).removeClass("l-note-last").addClass("l-expandable-open");
            });
            $("." + g._getChildNodeClassName(), treeNode).each(function ()
            {
                $(this)
                        .removeClass(g._getChildNodeClassName())
                        .addClass(g._getParentNodeClassName(true));
            });
        },
        //降级为叶节点级别
        demotion: function (treeNode)
        {
            var g = this, p = this.options;
            if (!treeNode && treeNode[0].tagName.toLowerCase() != 'li') return;
            var islast = $(treeNode).hasClass("l-last");
            $(".l-expandable-open", treeNode).each(function ()
            {
                $(this).removeClass("l-expandable-open")
                        .addClass(islast ? "l-note-last" : "l-note");
            });
            $(".l-expandable-close", treeNode).each(function ()
            {
                $(this).removeClass("l-expandable-close")
                        .addClass(islast ? "l-note-last" : "l-note");
            });
            $("." + g._getParentNodeClassName(true), treeNode).each(function ()
            {
                $(this)
                        .removeClass(g._getParentNodeClassName(true))
                        .addClass(g._getChildNodeClassName());
            });
        },
        collapseAll: function ()
        {
            var g = this, p = this.options;
            $(".l-expandable-open", g.tree).click();
        },
        expandAll: function ()
        {
            var g = this, p = this.options;
            $(".l-expandable-close", g.tree).click();
        },
        loadData: function (node, url, param)
        {
            var g = this, p = this.options;
            g.loading.show();
            var ajaxtype = param ? "post" : "get";
            param = param || [];
            //请求服务器
            $.ajax({
                type: ajaxtype,
                url: url,
                data: param,
                dataType: 'json',
                success: function (data)
                {
                    if (!data) return;
                    g.loading.hide();
                    g.append(node, data);
                    g.trigger('success', [data]);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown)
                {
                    try
                    {
                        g.loading.hide();
                        g.trigger('error', [XMLHttpRequest, textStatus, errorThrown]);
                    }
                    catch (e)
                    {

                    }
                }
            });
        },
        //清空
        clear: function ()
        {
            var g = this, p = this.options;
            //g.tree.html("");
            $("> li", g.tree).each(function () { g.remove(this); });
        },
        //@parm [treeNode] dom节点(li)、节点数据 或者节点 dataindex
        getNodeDom: function (nodeParm)
        {
            var g = this, p = this.options;
            if (nodeParm == null) return nodeParm;
            if (typeof (nodeParm) == "string" || typeof (nodeParm) == "number")
            {
                return $("li[treedataindex=" + nodeParm + "]", g.tree).get(0);
            }
            else if (typeof (nodeParm) == "object" && 'treedataindex' in nodeParm) //nodedata
            {
                return g.getNodeDom(nodeParm['treedataindex']);
            }
            return nodeParm;
        },
        //@parm [treeNode] dom节点(li)、节点数据 或者节点 dataindex
        remove: function (treeNode)
        {
            var g = this, p = this.options;
            treeNode = g.getNodeDom(treeNode);
            var treedataindex = parseInt($(treeNode).attr("treedataindex"));
            var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
            if (treenodedata) g._setTreeDataStatus([treenodedata], 'delete');
            var parentNode = g.getParentTreeItem(treeNode);
            //复选框处理
            if (p.checkbox)
            {
                g._setParentCheckboxStatus($(treeNode));
            }
            $(treeNode).remove();
            g._updateStyle(parentNode ? $("ul:first", parentNode) : g.tree);
        },
        _updateStyle: function (ul)
        {
            var g = this, p = this.options;
            var itmes = $(" > li", ul);
            var treeitemlength = itmes.length;
            if (!treeitemlength) return;
            //遍历设置子节点的样式
            itmes.each(function (i, item)
            {
                if (i == 0 && !$(this).hasClass("l-first"))
                    $(this).addClass("l-first");
                if (i == treeitemlength - 1 && !$(this).hasClass("l-last"))
                    $(this).addClass("l-last");
                if (i == 0 && i == treeitemlength - 1)
                    $(this).addClass("l-onlychild");
                $("> div .l-note,> div .l-note-last", this)
                           .removeClass("l-note l-note-last")
                           .addClass(i == treeitemlength - 1 ? "l-note-last" : "l-note");
                g._setTreeItem(this, { isLast: i == treeitemlength - 1 });
            });
        },
        //@parm [domnode] dom节点(li)、节点数据 或者节点 dataindex
        update: function (domnode, newnodedata)
        {
            var g = this, p = this.options;
            domnode = g.getNodeDom(domnode);
            var treedataindex = parseInt($(domnode).attr("treedataindex"));
            nodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
            for (var attr in newnodedata)
            {
                nodedata[attr] = newnodedata[attr];
                if (attr == p.textFieldName)
                {
                    $("> .l-body > span", domnode).text(newnodedata[attr]);
                }
            }
        },
        //增加节点集合
        //@parm [newdata] 数据集合 Array
        //@parm [parentNode] dom节点(li)、节点数据 或者节点 dataindex
        //@parm [nearNode] 附加到节点的上方/下方(非必填)
        //@parm [isAfter] 附加到节点的下方(非必填)
        append: function (parentNode, newdata, nearNode, isAfter)
        {
            var g = this, p = this.options;
            parentNode = g.getNodeDom(parentNode);
            if (g.trigger('beforeAppend', [parentNode, newdata]) == false) return false;
            if (!newdata || !newdata.length) return false;
            if (p.idFieldName && p.parentIDFieldName)
                newdata = g.arrayToTree(newdata, p.idFieldName, p.parentIDFieldName);
            g._addTreeDataIndexToData(newdata);
            g._setTreeDataStatus(newdata, 'add');
            if (nearNode != null)
            {
                nearNode = g.getNodeDom(nearNode);
            }
            g.trigger('append', [parentNode, newdata])
            g._appendData(parentNode, newdata);
            if (parentNode == null)//增加到根节点
            {
                var gridhtmlarr = g._getTreeHTMLByData(newdata, 1, [], true);
                gridhtmlarr[gridhtmlarr.length - 1] = gridhtmlarr[0] = "";
                if (nearNode != null)
                {
                    $(nearNode)[isAfter ? 'after' : 'before'](gridhtmlarr.join(''));
                    g._updateStyle(parentNode ? $("ul:first", parentNode) : g.tree);
                }
                else
                {
                    //remove last node class
                    if ($("> li:last", g.tree).length > 0)
                        g._setTreeItem($("> li:last", g.tree)[0], { isLast: false });
                    g.tree.append(gridhtmlarr.join(''));
                }
                $(".l-body", g.tree).hover(function ()
                {
                    $(this).addClass("l-over");
                }, function ()
                {
                    $(this).removeClass("l-over");
                });

                g._upadteTreeWidth();
                g.trigger('afterAppend', [parentNode, newdata])
                return;
            }
            var treeitem = $(parentNode);
            var outlineLevel = parseInt(treeitem.attr("outlinelevel"));

            var hasChildren = $("> ul", treeitem).length > 0;
            if (!hasChildren)
            {
                treeitem.append("<ul class='l-children'></ul>");
                //设置为父节点
                g.upgrade(parentNode);
            }
            var isLast = [];
            for (var i = 1; i <= outlineLevel - 1; i++)
            {
                var currentParentTreeItem = $(g.getParentTreeItem(parentNode, i));
                isLast.push(currentParentTreeItem.hasClass("l-last"));
            }
            isLast.push(treeitem.hasClass("l-last"));
            var gridhtmlarr = g._getTreeHTMLByData(newdata, outlineLevel + 1, isLast, true);
            gridhtmlarr[gridhtmlarr.length - 1] = gridhtmlarr[0] = "";
            if (nearNode != null)
            {
                $(nearNode)[isAfter ? 'after' : 'before'](gridhtmlarr.join(''));
                g._updateStyle(parentNode ? $("ul:first", parentNode) : g.tree);
            }
            else
            {
                //remove last node class  
                if ($("> .l-children > li:last", treeitem).length > 0)
                    g._setTreeItem($("> .l-children > li:last", treeitem)[0], { isLast: false });
                $(">.l-children", parentNode).append(gridhtmlarr.join(''));
            }
            g._upadteTreeWidth();
            $(">.l-children .l-body", parentNode).hover(function ()
            {
                $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
            g.trigger('afterAppend', [parentNode, newdata]);
        },
        //@parm [nodeParm] dom节点(li)、节点数据 或者节点 dataindex
        cancelSelect: function (nodeParm)
        {
            var g = this, p = this.options;
            var domNode = g.getNodeDom(nodeParm);
            var treeitem = $(domNode);
            var treedataindex = parseInt(treeitem.attr("treedataindex"));
            var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
            var treeitembody = $(">div:first", treeitem);
            if (p.checkbox)
                $(".l-checkbox", treeitembody).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");
            else
                treeitembody.removeClass("l-selected");
            g.trigger('cancelSelect', [{ data: treenodedata, target: treeitem[0]}]);
        },
        //选择节点(参数：条件函数、Dom节点或ID值)
        selectNode: function (selectNodeParm)
        {
            var g = this, p = this.options;
            var clause = null;
            if (typeof (selectNodeParm) == "function")
            {
                clause = selectNodeParm;
            }
            else if (typeof (selectNodeParm) == "object")
            {
                var treeitem = $(selectNodeParm);
                var treedataindex = parseInt(treeitem.attr("treedataindex"));
                var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                var treeitembody = $(">div:first", treeitem);
                if (p.checkbox)
                    $(".l-checkbox", treeitembody).removeClass("l-checkbox-unchecked").addClass("l-checkbox-checked");
                else
                    treeitembody.addClass("l-selected");

                g.trigger('select', [{ data: treenodedata, target: treeitem[0]}]);
                return;
            }
            else
            {
                clause = function (data)
                {
                    if (!data[p.idFieldName]) return false;
                    return data[p.idFieldName].toString() == selectNodeParm.toString();
                };
            }
            $("li", g.tree).each(function ()
            {
                var treeitem = $(this);
                var treedataindex = parseInt(treeitem.attr("treedataindex"));
                var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                if (clause(treenodedata, treedataindex))
                {
                    g.selectNode(this);
                }
                else
                {
                    g.cancelSelect(this);
                }
            });
        },
        getTextByID: function (id)
        {
            var g = this, p = this.options;
            var data = g.getDataByID(id);
            if (!data) return null;
            return data[p.textFieldName];
        },
        getDataByID: function (id)
        {
            var g = this, p = this.options;
            var data = null;
            $("li", g.tree).each(function ()
            {
                if (data) return;
                var treeitem = $(this);
                var treedataindex = parseInt(treeitem.attr("treedataindex"));
                var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                if (treenodedata[p.idFieldName].toString() == id.toString())
                {
                    data = treenodedata;
                }
            });
            return data;
        },
        arrayToTree: function (data, id, pid)      //将ID、ParentID这种数据格式转换为树格式
        {
            if (!data || !data.length) return [];
            var targetData = [];                    //存储数据的容器(返回) 
            var records = {};
            var itemLength = data.length;           //数据集合的个数
            for (var i = 0; i < itemLength; i++)
            {
                var o = data[i];
                records[o[id]] = o;
            }
            for (var i = 0; i < itemLength; i++)
            {
                var currentData = data[i];
                var parentData = records[currentData[pid]];
                if (!parentData)
                {
                    targetData.push(currentData);
                    continue;
                }
                parentData.children = parentData.children || [];
                parentData.children.push(currentData);
            }
            return targetData;
        },
        //根据数据索引获取数据
        _getDataNodeByTreeDataIndex: function (data, treedataindex)
        {
            var g = this, p = this.options;
            for (var i = 0; i < data.length; i++)
            {
                if (data[i].treedataindex == treedataindex)
                    return data[i];
                if (data[i].children)
                {
                    var targetData = g._getDataNodeByTreeDataIndex(data[i].children, treedataindex);
                    if (targetData) return targetData;
                }
            }
            return null;
        },
        //设置数据状态
        _setTreeDataStatus: function (data, status)
        {
            var g = this, p = this.options;
            $(data).each(function ()
            {
                this[p.statusName] = status;
                if (this.children)
                {
                    g._setTreeDataStatus(this.children, status);
                }
            });
        },
        //设置data 索引
        _addTreeDataIndexToData: function (data)
        {
            var g = this, p = this.options;
            $(data).each(function ()
            {
                if (this.treedataindex != undefined) return;
                this.treedataindex = g.treedataindex++;
                if (this.children)
                {
                    g._addTreeDataIndexToData(this.children);
                }
            });
        },
        _addToNodes: function (data)
        {
            var g = this, p = this.options;
            g.nodes = g.nodes || [];
            if ($.inArray(data, g.nodes) == -1)
                g.nodes.push(data);
            if (data.children)
            {
                $(data.children).each(function (i, item)
                {
                    g._addToNodes(item);
                });
            }
        },
        //添加项到g.data
        _appendData: function (treeNode, data)
        {
            var g = this, p = this.options;

            var treedataindex = parseInt($(treeNode).attr("treedataindex"));
            var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
            if (g.treedataindex == undefined) g.treedataindex = 0;
            if (treenodedata && treenodedata.children == undefined) treenodedata.children = [];
            $(data).each(function (i, item)
            {
                if (treenodedata)
                    treenodedata.children[treenodedata.children.length] = item;
                else
                    g.data[g.data.length] = item;
                g._addToNodes(item);
            });
        },
        _setTreeItem: function (treeNode, options)
        {
            var g = this, p = this.options;
            if (!options) return;
            treeNode = g.getNodeDom(treeNode);
            var treeItem = $(treeNode);
            var outlineLevel = parseInt(treeItem.attr("outlinelevel"));
            if (options.isLast != undefined)
            {
                if (options.isLast == true)
                {
                    treeItem.removeClass("l-last").addClass("l-last");
                    $("> div .l-note", treeItem).removeClass("l-note").addClass("l-note-last");
                    $(".l-children li", treeItem)
                            .find(".l-box:eq(" + (outlineLevel - 1) + ")")
                            .removeClass("l-line");
                }
                else if (options.isLast == false)
                {
                    treeItem.removeClass("l-last");
                    $("> div .l-note-last", treeItem).removeClass("l-note-last").addClass("l-note");

                    $(".l-children li", treeItem)
                            .find(".l-box:eq(" + (outlineLevel - 1) + ")")
                            .removeClass("l-line")
                            .addClass("l-line");
                }
            }
        },
        _upadteTreeWidth: function ()
        {
            var g = this, p = this.options;
            var treeWidth = g.maxOutlineLevel * 22;
            if (p.checkbox) treeWidth += 22;
            if (p.parentIcon || p.childIcon) treeWidth += 22;
            treeWidth += p.nodeWidth;
            g.tree.width(treeWidth);
        },
        _getChildNodeClassName: function ()
        {
            var g = this, p = this.options;
            return 'l-tree-icon-' + p.childIcon;
        },
        _getParentNodeClassName: function (isOpen)
        {
            var g = this, p = this.options;
            var nodeclassname = 'l-tree-icon-' + p.parentIcon;
            if (isOpen) nodeclassname += '-open';
            return nodeclassname;
        },
        //根据data生成最终完整的tree html
        _getTreeHTMLByData: function (data, outlineLevel, isLast, isExpand)
        {
            var g = this, p = this.options;
            if (g.maxOutlineLevel < outlineLevel)
                g.maxOutlineLevel = outlineLevel;
            isLast = isLast || [];
            outlineLevel = outlineLevel || 1;
            var treehtmlarr = [];
            if (!isExpand) treehtmlarr.push('<ul class="l-children" style="display:none">');
            else treehtmlarr.push("<ul class='l-children'>");
            for (var i = 0; i < data.length; i++)
            {
                var isFirst = i == 0;
                var isLastCurrent = i == data.length - 1;
                var isExpandCurrent = true;
                var o = data[i];
                if (o.isexpand == false || o.isexpand == "false") isExpandCurrent = false;

                treehtmlarr.push('<li ');
                if (o.treedataindex != undefined)
                    treehtmlarr.push('treedataindex="' + o.treedataindex + '" ');
                if (isExpandCurrent)
                    treehtmlarr.push('isexpand=' + o.isexpand + ' ');
                treehtmlarr.push('outlinelevel=' + outlineLevel + ' ');
                //增加属性支持
                for (var j = 0; j < g.sysAttribute.length; j++)
                {
                    if ($(this).attr(g.sysAttribute[j]))
                        data[dataindex][g.sysAttribute[j]] = $(this).attr(g.sysAttribute[j]);
                }
                for (var j = 0; j < p.attribute.length; j++)
                {
                    if (o[p.attribute[j]])
                        treehtmlarr.push(p.attribute[j] + '="' + o[p.attribute[j]] + '" ');
                }

                //css class
                treehtmlarr.push('class="');
                isFirst && treehtmlarr.push('l-first ');
                isLastCurrent && treehtmlarr.push('l-last ');
                isFirst && isLastCurrent && treehtmlarr.push('l-onlychild ');
                treehtmlarr.push('"');
                treehtmlarr.push('>');
                treehtmlarr.push('<div class="l-body">');
                for (var k = 0; k <= outlineLevel - 2; k++)
                {
                    if (isLast[k]) treehtmlarr.push('<div class="l-box"></div>');
                    else treehtmlarr.push('<div class="l-box l-line"></div>');
                }
                if (g.hasChildren(o))
                {
                    if (isExpandCurrent) treehtmlarr.push('<div class="l-box l-expandable-open"></div>');
                    else treehtmlarr.push('<div class="l-box l-expandable-close"></div>');
                    if (p.checkbox)
                    {
                        if (o.ischecked)
                            treehtmlarr.push('<div class="l-box l-checkbox l-checkbox-checked"></div>');
                        else
                            treehtmlarr.push('<div class="l-box l-checkbox l-checkbox-unchecked"></div>');
                    }
                    if (p.parentIcon)
                    {
                        //node icon
                        treehtmlarr.push('<div class="l-box l-tree-icon ');
                        treehtmlarr.push(g._getParentNodeClassName(p.parentIcon ? true : false) + " ");
                        if (p.iconFieldName && o[p.iconFieldName])
                            treehtmlarr.push('l-tree-icon-none');
                        treehtmlarr.push('">');
                        if (p.iconFieldName && o[p.iconFieldName])
                            treehtmlarr.push('<img src="' + o[p.iconFieldName] + '" />');
                        treehtmlarr.push('</div>');
                    }
                }
                else
                {
                    if (isLastCurrent) treehtmlarr.push('<div class="l-box l-note-last"></div>');
                    else treehtmlarr.push('<div class="l-box l-note"></div>');
                    if (p.checkbox)
                    {
                        if (o.ischecked)
                            treehtmlarr.push('<div class="l-box l-checkbox l-checkbox-checked"></div>');
                        else
                            treehtmlarr.push('<div class="l-box l-checkbox l-checkbox-unchecked"></div>');
                    }
                    if (p.childIcon)
                    {
                        //node icon 
                        treehtmlarr.push('<div class="l-box l-tree-icon ');
                        treehtmlarr.push(g._getChildNodeClassName() + " ");
                        if (p.iconFieldName && o[p.iconFieldName])
                            treehtmlarr.push('l-tree-icon-none');
                        treehtmlarr.push('">');
                        if (p.iconFieldName && o[p.iconFieldName])
                            treehtmlarr.push('<img src="' + o[p.iconFieldName] + '" />');
                        treehtmlarr.push('</div>');
                    }
                }

                treehtmlarr.push('<span>' + o[p.textFieldName] + '</span></div>');
                if (g.hasChildren(o))
                {
                    var isLastNew = [];
                    for (var k = 0; k < isLast.length; k++)
                    {
                        isLastNew.push(isLast[k]);
                    }
                    isLastNew.push(isLastCurrent);
                    treehtmlarr.push(g._getTreeHTMLByData(o.children, outlineLevel + 1, isLastNew, isExpandCurrent).join(''));
                }
                treehtmlarr.push('</li>');
            }
            treehtmlarr.push("</ul>");
            return treehtmlarr;

        },
        //根据简洁的html获取data
        _getDataByTreeHTML: function (treeDom)
        {
            var g = this, p = this.options;
            var data = [];
            $("> li", treeDom).each(function (i, item)
            {
                var dataindex = data.length;
                data[dataindex] =
                        {
                            treedataindex: g.treedataindex++
                        };
                data[dataindex][p.textFieldName] = $("> span,> a", this).html();
                for (var j = 0; j < g.sysAttribute.length; j++)
                {
                    if ($(this).attr(g.sysAttribute[j]))
                        data[dataindex][g.sysAttribute[j]] = $(this).attr(g.sysAttribute[j]);
                }
                for (var j = 0; j < p.attribute.length; j++)
                {
                    if ($(this).attr(p.attribute[j]))
                        data[dataindex][p.attribute[j]] = $(this).attr(p.attribute[j]);
                }
                if ($("> ul", this).length > 0)
                {
                    data[dataindex].children = g._getDataByTreeHTML($("> ul", this));
                }
            });
            return data;
        },
        _applyTree: function ()
        {
            var g = this, p = this.options;
            g.data = g._getDataByTreeHTML(g.tree);
            var gridhtmlarr = g._getTreeHTMLByData(g.data, 1, [], true);
            gridhtmlarr[gridhtmlarr.length - 1] = gridhtmlarr[0] = "";
            g.tree.html(gridhtmlarr.join(''));
            g._upadteTreeWidth();
            $(".l-body", g.tree).hover(function ()
            {
                $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
        },
        _applyTreeEven: function (treeNode)
        {
            var g = this, p = this.options;
            $("> .l-body", treeNode).hover(function ()
            {
                $(this).addClass("l-over");
            }, function ()
            {
                $(this).removeClass("l-over");
            });
        },
        _getSrcElementByEvent: function (e)
        {
            var g = this;
            var obj = (e.target || e.srcElement);
            var tag = obj.tagName.toLowerCase();
            var jobjs = $(obj).parents().add(obj);
            var fn = function (parm)
            {
                for (var i = jobjs.length - 1; i >= 0; i--)
                {
                    if ($(jobjs[i]).hasClass(parm)) return jobjs[i];
                }
                return null;
            };
            if (jobjs.index(this.element) == -1) return { out: true };
            var r = {
                tree: fn("l-tree"),
                node: fn("l-body"),
                checkbox: fn("l-checkbox"),
                icon: fn("l-tree-icon"),
                text: tag == "span"
            };
            if (r.node)
            {
                var treedataindex = parseInt($(r.node).parent().attr("treedataindex"));
                r.data = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
            }
            return r;
        },
        _setTreeEven: function ()
        {
            var g = this, p = this.options;
            if (g.hasBind('contextmenu'))
            {
                g.tree.bind("contextmenu", function (e)
                {
                    var obj = (e.target || e.srcElement);
                    var treeitem = null;
                    if (obj.tagName.toLowerCase() == "a" || obj.tagName.toLowerCase() == "span" || $(obj).hasClass("l-box"))
                        treeitem = $(obj).parent().parent();
                    else if ($(obj).hasClass("l-body"))
                        treeitem = $(obj).parent();
                    else if (obj.tagName.toLowerCase() == "li")
                        treeitem = $(obj);
                    if (!treeitem) return;
                    var treedataindex = parseInt(treeitem.attr("treedataindex"));
                    var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                    return g.trigger('contextmenu', [{ data: treenodedata, target: treeitem[0] }, e]);
                });
            }
            g.tree.click(function (e)
            { 
                var obj = (e.target || e.srcElement);
                var treeitem = null;
                if (obj.tagName.toLowerCase() == "a" || obj.tagName.toLowerCase() == "span" || $(obj).hasClass("l-box"))
                    treeitem = $(obj).parent().parent();
                else if ($(obj).hasClass("l-body"))
                    treeitem = $(obj).parent();
                else
                    treeitem = $(obj);
                if (!treeitem) return;
                var treedataindex = parseInt(treeitem.attr("treedataindex"));
                var treenodedata = g._getDataNodeByTreeDataIndex(g.data, treedataindex);
                var treeitembtn = $("div.l-body:first", treeitem).find("div.l-expandable-open:first,div.l-expandable-close:first");
                var clickOnTreeItemBtn = $(obj).hasClass("l-expandable-open") || $(obj).hasClass("l-expandable-close");
                if (!$(obj).hasClass("l-checkbox") && !clickOnTreeItemBtn)
                {
                    if ($(">div:first", treeitem).hasClass("l-selected") && p.needCancel)
                    {
                        if (g.trigger('beforeCancelSelect', [{ data: treenodedata, target: treeitem[0]}]) == false)
                            return false;

                        $(">div:first", treeitem).removeClass("l-selected");
                        g.trigger('cancelSelect', [{ data: treenodedata, target: treeitem[0]}]);
                    }
                    else
                    {
                        if (g.trigger('beforeSelect', [{ data: treenodedata, target: treeitem[0]}]) == false)
                            return false;
                        $(".l-body", g.tree).removeClass("l-selected");
                        $(">div:first", treeitem).addClass("l-selected");
                        g.trigger('select', [{ data: treenodedata, target: treeitem[0]}])
                    }
                }
                //chekcbox even
                if ($(obj).hasClass("l-checkbox"))
                {
                    if (p.autoCheckboxEven)
                    {
                        //状态：未选中
                        if ($(obj).hasClass("l-checkbox-unchecked"))
                        {
                            $(obj).removeClass("l-checkbox-unchecked").addClass("l-checkbox-checked");
                            $(".l-children .l-checkbox", treeitem)
                                    .removeClass("l-checkbox-incomplete l-checkbox-unchecked")
                                    .addClass("l-checkbox-checked");
                            g.trigger('check', [{ data: treenodedata, target: treeitem[0] }, true]);
                        }
                        //状态：选中
                        else if ($(obj).hasClass("l-checkbox-checked"))
                        {
                            $(obj).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");
                            $(".l-children .l-checkbox", treeitem)
                                    .removeClass("l-checkbox-incomplete l-checkbox-checked")
                                    .addClass("l-checkbox-unchecked");
                            g.trigger('check', [{ data: treenodedata, target: treeitem[0] }, false]);
                        }
                        //状态：未完全选中
                        else if ($(obj).hasClass("l-checkbox-incomplete"))
                        {
                            $(obj).removeClass("l-checkbox-incomplete").addClass("l-checkbox-checked");
                            $(".l-children .l-checkbox", treeitem)
                                    .removeClass("l-checkbox-incomplete l-checkbox-unchecked")
                                    .addClass("l-checkbox-checked");
                            g.trigger('check', [{ data: treenodedata, target: treeitem[0] }, true]);
                        }
                        g._setParentCheckboxStatus(treeitem);
                    }
                    else
                    {
                        //状态：未选中
                        if ($(obj).hasClass("l-checkbox-unchecked"))
                        {
                            $(obj).removeClass("l-checkbox-unchecked").addClass("l-checkbox-checked");
                            //是否单选
                            if (p.single)
                            {
                                $(".l-checkbox", g.tree).not(obj).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");
                            }
                            g.trigger('check', [{ data: treenodedata, target: treeitem[0] }, true]);
                        }
                        //状态：选中
                        else if ($(obj).hasClass("l-checkbox-checked"))
                        {
                            $(obj).removeClass("l-checkbox-checked").addClass("l-checkbox-unchecked");
                            g.trigger('check', [{ data: treenodedata, target: treeitem[0] }, false]);
                        }
                    }
                }
                //状态：已经张开
                else if (treeitembtn.hasClass("l-expandable-open") && (!p.btnClickToToggleOnly || clickOnTreeItemBtn))
                {
                    if (g.trigger('beforeCollapse', [{ data: treenodedata, target: treeitem[0]}]) == false)
                        return false;
                    treeitembtn.removeClass("l-expandable-open").addClass("l-expandable-close");
                    if (p.slide)
                        $("> .l-children", treeitem).slideToggle('fast');
                    else
                        $("> .l-children", treeitem).toggle();
                    $("> div ." + g._getParentNodeClassName(true), treeitem)
                            .removeClass(g._getParentNodeClassName(true))
                            .addClass(g._getParentNodeClassName());
                    g.trigger('collapse', [{ data: treenodedata, target: treeitem[0]}]);
                }
                //状态：没有张开
                else if (treeitembtn.hasClass("l-expandable-close") && (!p.btnClickToToggleOnly || clickOnTreeItemBtn))
                {
                    if (g.trigger('beforeExpand', [{ data: treenodedata, target: treeitem[0]}]) == false)
                        return false;
                    treeitembtn.removeClass("l-expandable-close").addClass("l-expandable-open");
                    var callback = function ()
                    {
                        g.trigger('expand', [{ data: treenodedata, target: treeitem[0]}]);
                    };
                    if (p.slide)
                    {
                        $("> .l-children", treeitem).slideToggle('fast', callback);
                    }
                    else
                    {
                        $("> .l-children", treeitem).toggle();
                        callback();
                    }
                    $("> div ." + g._getParentNodeClassName(), treeitem)
                            .removeClass(g._getParentNodeClassName())
                            .addClass(g._getParentNodeClassName(true));
                }
                g.trigger('click', [{ data: treenodedata, target: treeitem[0]}]);
            });

            //节点拖拽支持
            if ($.fn.ligerDrag && p.nodeDraggable)
            {
                g.nodeDroptip = $("<div class='l-drag-nodedroptip' style='display:none'></div>").appendTo('body');
                g.tree.ligerDrag({ revert: true, animate: false,
                    proxyX: 20, proxyY: 20,
                    proxy: function (draggable, e)
                    {
                        var src = g._getSrcElementByEvent(e);
                        if (src.node)
                        {
                            var content = "dragging";
                            if (p.nodeDraggingRender)
                            {
                                content = p.nodeDraggingRender(draggable.draggingNodes, draggable, g);
                            }
                            else
                            {
                                content = "";
                                var appended = false;
                                for (var i in draggable.draggingNodes)
                                {
                                    var node = draggable.draggingNodes[i];
                                    if (appended) content += ",";
                                    content += node.text;
                                    appended = true;
                                }
                            }
                            var proxy = $("<div class='l-drag-proxy' style='display:none'><div class='l-drop-icon l-drop-no'></div>" + content + "</div>").appendTo('body');
                            return proxy;
                        }
                    },
                    onRevert: function () { return false; },
                    onRendered: function ()
                    {
                        this.set('cursor', 'default');
                        g.children[this.id] = this;
                    },
                    onStartDrag: function (current, e)
                    {
                        if (e.button == 2) return false;
                        this.set('cursor', 'default');
                        var src = g._getSrcElementByEvent(e);
                        if (src.checkbox) return false;
                        if (p.checkbox)
                        {
                            var checked = g.getChecked();
                            this.draggingNodes = [];
                            for (var i in checked)
                            {
                                this.draggingNodes.push(checked[i].data);
                            }
                            if (!this.draggingNodes || !this.draggingNodes.length) return false;
                        }
                        else
                        {
                            this.draggingNodes = [src.data];
                        }
                        this.draggingNode = src.data;
                        this.set('cursor', 'move');
                        g.nodedragging = true;
                        this.validRange = {
                            top: g.tree.offset().top,
                            bottom: g.tree.offset().top + g.tree.height(),
                            left: g.tree.offset().left,
                            right: g.tree.offset().left + g.tree.width()
                        };
                    },
                    onDrag: function (current, e)
                    {
                        var nodedata = this.draggingNode;
                        if (!nodedata) return false;
                        var nodes = this.draggingNodes ? this.draggingNodes : [nodedata];
                        if (g.nodeDropIn == null) g.nodeDropIn = -1;
                        var pageX = e.pageX;
                        var pageY = e.pageY;
                        var visit = false;
                        var validRange = this.validRange;
                        if (pageX < validRange.left || pageX > validRange.right
                            || pageY > validRange.bottom || pageY < validRange.top)
                        {

                            g.nodeDropIn = -1;
                            g.nodeDroptip.hide();
                            this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes l-drop-add").addClass("l-drop-no");
                            return;
                        }
                        for (var i = 0, l = g.nodes.length; i < l; i++)
                        {
                            var nd = g.nodes[i];
                            var treedataindex = nd['treedataindex'];
                            if (nodedata['treedataindex'] == treedataindex) visit = true;
                            if ($.inArray(nd, nodes) != -1) continue;
                            var isAfter = visit ? true : false;
                            if (g.nodeDropIn != -1 && g.nodeDropIn != treedataindex) continue;
                            var jnode = $("li[treedataindex=" + treedataindex + "] div:first", g.tree);
                            var offset = jnode.offset();
                            var range = {
                                top: offset.top,
                                bottom: offset.top + jnode.height(),
                                left: g.tree.offset().left,
                                right: g.tree.offset().left + g.tree.width()
                            };
                            if (pageX > range.left && pageX < range.right && pageY > range.top && pageY < range.bottom)
                            {
                                var lineTop = offset.top;
                                if (isAfter) lineTop += jnode.height();
                                g.nodeDroptip.css({
                                    left: range.left,
                                    top: lineTop,
                                    width: range.right - range.left
                                }).show();
                                g.nodeDropIn = treedataindex;
                                g.nodeDropDir = isAfter ? "bottom" : "top";
                                if (pageY > range.top + 7 && pageY < range.bottom - 7)
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-yes").addClass("l-drop-add");
                                    g.nodeDroptip.hide();
                                    g.nodeDropInParent = true;
                                }
                                else
                                {
                                    this.proxy.find(".l-drop-icon:first").removeClass("l-drop-no l-drop-add").addClass("l-drop-yes");
                                    g.nodeDroptip.show();
                                    g.nodeDropInParent = false;
                                }
                                break;
                            }
                            else if (g.nodeDropIn != -1)
                            {
                                g.nodeDropIn = -1;
                                g.nodeDropInParent = false;
                                g.nodeDroptip.hide();
                                this.proxy.find(".l-drop-icon:first").removeClass("l-drop-yes  l-drop-add").addClass("l-drop-no");
                            }
                        }
                    },
                    onStopDrag: function (current, e)
                    {
                        var nodes = this.draggingNodes;
                        g.nodedragging = false;
                        if (g.nodeDropIn != -1)
                        {
                            for (var i = 0; i < nodes.length; i++)
                            {
                                var children = nodes[i].children;
                                if (children)
                                {
                                    nodes = $.grep(nodes, function (node, i)
                                    {
                                        var isIn = $.inArray(node, children) == -1;
                                        return isIn;
                                    });
                                }
                            }
                            for (var i in nodes)
                            {
                                var node = nodes[i];
                                if (g.nodeDropInParent)
                                {
                                    g.remove(node);
                                    g.append(g.nodeDropIn, [node]);
                                }
                                else
                                {
                                    g.remove(node);
                                    g.append(g.getParent(g.nodeDropIn), [node], g.nodeDropIn, g.nodeDropDir == "bottom")
                                }
                            }
                            g.nodeDropIn = -1;
                        }
                        g.nodeDroptip.hide();
                        this.set('cursor', 'default');
                    }
                });
            }
        },
        //递归设置父节点的状态
        _setParentCheckboxStatus: function (treeitem)
        {
            var g = this, p = this.options;
            //当前同级别或低级别的节点是否都选中了
            var isCheckedComplete = $(".l-checkbox-unchecked", treeitem.parent()).length == 0;
            //当前同级别或低级别的节点是否都没有选中
            var isCheckedNull = $(".l-checkbox-checked", treeitem.parent()).length == 0;
            if (isCheckedComplete)
            {
                treeitem.parent().prev().find(".l-checkbox")
                                    .removeClass("l-checkbox-unchecked l-checkbox-incomplete")
                                    .addClass("l-checkbox-checked");
            }
            else if (isCheckedNull)
            {
                treeitem.parent().prev().find("> .l-checkbox")
                                    .removeClass("l-checkbox-checked l-checkbox-incomplete")
                                    .addClass("l-checkbox-unchecked");
            }
            else
            {
                treeitem.parent().prev().find("> .l-checkbox")
                                    .removeClass("l-checkbox-unchecked l-checkbox-checked")
                                    .addClass("l-checkbox-incomplete");
            }
            if (treeitem.parent().parent("li").length > 0)
                g._setParentCheckboxStatus(treeitem.parent().parent("li"));
        }
    });


})(jQuery);﻿/**
* jQuery ligerUI 1.2.0
* 
* http://ligerui.com
*  
* Author daomi 2013 [ gd_star@163.com ] 
* 
*/
(function ($)
{

    var l = $.ligerui;

    l.windowCount = 0;

    $.ligerWindow = function (options)
    {
        return l.run.call(null, "ligerWindow", arguments, { isStatic: true });
    };

    $.ligerWindow.show = function (p)
    {
        return $.ligerWindow(p);
    };

    $.ligerDefaults.Window = {
        showClose: true,
        showMax: true,
        showToggle: true,
        showMin: true,
        title: 'window',
        load: false,
        onLoaded: null,
        onClose: null,
        onRegain: null,
        onMax:null,
        modal: false     //是否模态窗口
    };

    $.ligerMethos.Window = {};

    l.controls.Window = function (options)
    {
        l.controls.Window.base.constructor.call(this, null, options);
    };
    l.controls.Window.ligerExtend(l.core.Win, {
        __getType: function ()
        {
            return 'Window';
        },
        __idPrev: function ()
        {
            return 'Window';
        },
        _extendMethods: function ()
        {
            return $.ligerMethos.Window;
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.window = $('<div class="l-window"><div class="l-window-header"><div class="l-window-header-buttons"><div class="l-window-toggle"></div><div class="l-window-max"></div><div class="l-window-close"></div><div class="l-clear"></div></div><div class="l-window-header-inner"></div></div><div class="l-window-content"></div></div>');
            g.element = g.window[0];
            g.window.content = $(".l-window-content", g.window);
            g.window.header = $(".l-window-header", g.window);
            g.window.buttons = $(".l-window-header-buttons:first", g.window);
            if (p.url)
            {
                if (p.load)
                {
                    g.window.content.load(p.url, function ()
                    {
                        g.trigger('loaded');
                    });
                    g.window.content.addClass("l-window-content-scroll");
                }
                else
                {
                    var iframe = $("<iframe frameborder='0' src='" + p.url + "'></iframe>");
                    var framename = "ligeruiwindow" + l.windowCount++;
                    if (p.name) framename = p.name;
                    iframe.attr("name", framename).attr("id", framename);
                    p.framename = framename;
                    iframe.appendTo(g.window.content);
                    g.iframe = iframe;
                }
            }
            else if (p.content)
            {
                var content = $("<div>" + p.content + "</div>");
                content.appendTo(g.window.content);
            }
            else if (p.target)
            {
                g.window.content.append(p.target);
                p.target.show();
            }



            this.mask();

            g.active();

            $('body').append(g.window);

            g.set({ width: p.width, height: p.height });
            //位置初始化
            var left = 0;
            var top = 0;
            if (p.left != null) left = p.left;
            else p.left = left = 0.5 * ($(window).width() - g.window.width());
            if (p.top != null) top = p.top;
            else p.top = top = 0.5 * ($(window).height() - g.window.height()) + $(window).scrollTop() - 10;
            if (left < 0) p.left = left = 0;
            if (top < 0) p.top = top = 0;


            g.set(p);

            p.framename && $(">iframe", g.window.content).attr('name', p.framename);
            if (!p.showToggle) $(".l-window-toggle", g.window).remove();
            if (!p.showMax) $(".l-window-max", g.window).remove();
            if (!p.showClose) $(".l-window-close", g.window).remove();

            g._saveStatus();

            //拖动支持
            if ($.fn.ligerDrag)
            {
                g.draggable = g.window.drag = g.window.ligerDrag({ handler: '.l-window-header-inner', onStartDrag: function ()
                {
                    g.active();
                }, onStopDrag: function ()
                {
                    g._saveStatus();
                }, animate: false
                });
            }
            //改变大小支持
            if ($.fn.ligerResizable)
            {
                g.resizeable = g.window.resizable = g.window.ligerResizable({
                    onStartResize: function ()
                    {
                        g.active();
                        $(".l-window-max", g.window).removeClass("l-window-regain");
                    },
                    onStopResize: function (current, e)
                    {
                        var top = 0;
                        var left = 0;
                        if (!isNaN(parseInt(g.window.css('top'))))
                            top = parseInt(g.window.css('top'));
                        if (!isNaN(parseInt(g.window.css('left'))))
                            left = parseInt(g.window.css('left'));
                        if (current.diffTop)
                            g.window.css({ top: top + current.diffTop });
                        if (current.diffLeft)
                            g.window.css({ left: left + current.diffLeft });
                        if (current.newWidth)
                            g.window.width(current.newWidth);
                        if (current.newHeight)
                            g.window.content.height(current.newHeight - 28);

                        g._saveStatus();
                        return false;
                    }
                });
                g.window.append("<div class='l-btn-nw-drop'></div>");
            }
            //设置事件 
            $(".l-window-toggle", g.window).click(function ()
            {
                if ($(this).hasClass("l-window-toggle-close"))
                {
                    g.collapsed = false;
                    $(this).removeClass("l-window-toggle-close");
                } else
                {
                    g.collapsed = true;
                    $(this).addClass("l-window-toggle-close");
                }
                g.window.content.slideToggle();
            }).hover(function ()
            {
                if (g.window.drag)
                    g.window.drag.set('disabled', true);
            }, function ()
            {
                if (g.window.drag)
                    g.window.drag.set('disabled', false);
            });
            $(".l-window-close", g.window).click(function ()
            {
                if (g.trigger('close') == false) return false;
                g.window.hide();
                l.win.removeTask(g);
            }).hover(function ()
            {
                if (g.window.drag)
                    g.window.drag.set('disabled', true);
            }, function ()
            {
                if (g.window.drag)
                    g.window.drag.set('disabled', false);
            });
            $(".l-window-max", g.window).click(function ()
            {
                if ($(this).hasClass("l-window-regain"))
                {
                    if (g.trigger('regain') == false) return false;
                    g.window.width(g._width).css({ left: g._left, top: g._top });
                    g.window.content.height(g._height - 28);
                    $(this).removeClass("l-window-regain");
                }
                else
                {
                    if (g.trigger('max') == false) return false;
                    g.window.width($(window).width() - 2).css({ left: 0, top: 0 });
                    g.window.content.height($(window).height() - 28).show();
                    $(this).addClass("l-window-regain");
                }
            });
        },
        _saveStatus: function ()
        {
            var g = this;
            g._width = g.window.width();
            g._height = g.window.height();
            var top = 0;
            var left = 0;
            if (!isNaN(parseInt(g.window.css('top'))))
                top = parseInt(g.window.css('top'));
            if (!isNaN(parseInt(g.window.css('left'))))
                left = parseInt(g.window.css('left'));
            g._top = top;
            g._left = left;
        },
        min: function ()
        {
            this.window.hide();
            this.minimize = true;
            this.actived = false;
        },
        _setShowMin: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                if (!g.winmin)
                {
                    g.winmin = $('<div class="l-window-min"></div>').prependTo(g.window.buttons)
                    .click(function ()
                    {
                        g.min();
                    });
                    l.win.addTask(g);
                }
            }
            else if (g.winmin)
            {
                g.winmin.remove();
                g.winmin = null;
            }
        },
        _setLeft: function (value)
        {
            if (value != null)
                this.window.css({ left: value });
        },
        _setTop: function (value)
        {
            if (value != null)
                this.window.css({ top: value });
        },
        _setWidth: function (value)
        {
            if (value > 0)
                this.window.width(value);
        },
        _setHeight: function (value)
        {
            if (value > 28)
                this.window.content.height(value - 28);
        },
        _setTitle: function (value)
        {
            if (value)
                $(".l-window-header-inner", this.window.header).html(value);
        },
        _setUrl: function (url)
        {
            var g = this, p = this.options;
            p.url = url;
            if (p.load)
            {
                g.window.content.html("").load(p.url, function ()
                {
                    if (g.trigger('loaded') == false) return false;
                });
            }
            else if (g.jiframe)
            {
                g.jiframe.attr("src", p.url);
            }
        },
        hide: function ()
        {
            var g = this, p = this.options;
            this.unmask();
            this.window.hide();
        },
        show: function ()
        {
            var g = this, p = this.options;
            this.mask();
            this.window.show();
        },
        remove: function ()
        {
            var g = this, p = this.options;
            this.unmask();
            this.window.remove();
        },
        active: function ()
        {
            var g = this, p = this.options;
            if (g.minimize)
            {
                var width = g._width, height = g._height, left = g._left, top = g._top;
                if (g.maximum)
                {
                    width = $(window).width();
                    height = $(window).height();
                    left = top = 0;
                    if (l.win.taskbar)
                    {
                        height -= l.win.taskbar.outerHeight();
                        if (l.win.top) top += l.win.taskbar.outerHeight();
                    }
                }
                g.set({ width: width, height: height, left: left, top: top });
            }
            g.actived = true;
            g.minimize = false;
            l.win.setFront(g);
            g.show();
            l.win.setFront(this);
        },
        setUrl: function (url)
        {
            return _setUrl(url);
        }
    });

})(jQuery);
//modify by songxf at 2013-6-26
//补充ligerTreeToolBar组件
(function ($)
{
     $.fn.ligerTreeToolBar = function(options) {
          return $.ligerui.run.call(this, "ligerTreeToolBar", arguments);
     };

     $.fn.ligerGetTreeToolBarManager = function() {
          return $.ligerui.run.call(this, "ligerGetTreeToolBarManager", arguments);
     };

     $.ligerDefaults.TreeToolBar = {};

     $.ligerMethos.TreeToolBar = {};

     $.ligerui.controls.TreeToolBar = function(element, options) {
          $.ligerui.controls.TreeToolBar.base.constructor
                    .call(this, element, options);
     };
     $.ligerui.controls.TreeToolBar
               .ligerExtend(
                         $.ligerui.core.UIComponent,
                         {
                              __getType : function() {
                                   return 'TreeToolBar';
                              },
                              __idPrev : function() {
                                   return 'TreeToolBar';
                              },
                              _extendMethods : function() {
                                   return $.ligerMethos.TreeToolBar;
                              },
                              _render : function() {
                                   var g = this, p = this.options;
                                   g.toolBar = $(this.element);
                                   g.toolBar.addClass("l-treetoolbar");
                                   g.set(p);
                              },
                              _setItems : function(items) {
                                   var g = this, p = this.options;
                                   $(items).each(function(i, item) {
                                        g.addItem(item);
                                   });
                                   if(p.treemenu){
                                        var treemenu = {items:
                                         [
                                             { text: '保存', click: itemclick ,icon:'add'}
                                         ]
                                          };
                                      var item={
                                           icon:'settings',
                                           menu:treemenu,
                                           align:right
                                      };
                                        g.addItem(item);
                                   }
                              },
                              addItem : function(item) {
                                   var g = this, p = this.options;
                                   if (item.line) {
                                        g.toolBar
                                                  .append('<div class="l-toolbar-separator"></div>');
                                        return;
                                   }
                                   if (item.menu) {
                                        var menucontainer=$('<div class="l-treemenubar-item"></div>');
                                        var ditem = $('<div class="l-treetoolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
                                        if(item.align&&item.align==right){
                                             menucontainer=$('<div class="l-treemenubar-item-right"></div>');
                                             ditem = $('<div class="l-treetoolbar-item-right l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
                                        }
                                        menucontainer.append(ditem);
                                        g.toolBar.append(menucontainer);
                                        item.id && ditem.attr("menubarid", item.id);
                                        item.text
                                                  && $("span:first", ditem).html(
                                                            item.text);
                                        item.disable
                                                  && ditem
                                                            .addClass("l-menubar-item-disable");
                                        //item.click && ditem.click(function() {
                                        //     item.click(item);
                                        //});
                                        if (item.img) {
                                             ditem.append("<img src='" + item.img
                                                       + "' />");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        } else if (item.icon) {
                                             ditem.append("<div class='l-icon l-icon-"
                                                       + item.icon + "'></div>");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        }
                                        item.menu.parent=menucontainer;
                                        var menu = $.ligerMenu(item.menu);
                                        ditem.hover(
                                                  function() {
                                                       if(g.premenu!=undefined){
                                                            g.premenu.hide();
                                                       }
                                                       g.premenu=menu;
                                                       $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                                                       $(this).addClass("l-panel-btn-over");
                                                  },function(){
                                   //                    menu.hide();
                                   //                    $(this).removeClass("l-panel-btn-over");
                                                  });
                                        ditem.click(
                                                  function(){
                                                       g.actionMenu&& g.actionMenu.hide();
                                                       var left = 0;
                                                       var top = g.toolBar.height();
                                                       menu.show({
                                                            top : top,
                                                            left : left
                                                       });
                                                       g.actionMenu = menu;
                                        });
                                        g.toolBar.mouseleave(function(){
                                                       menu.hide();
                                                       $(".l-panel-btn-over").removeClass('l-panel-btn-over');

                                       });
                                   } else {
                                        var ditem = $('<div class="l-treetoolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div></div>');
                                        g.toolBar.append(ditem);
                                        item.id && ditem.attr("toolbarid", item.id);
                                        if (item.img) {
                                             ditem.append("<img src='" + item.img
                                                       + "' />");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        } else if (item.icon) {
                                             ditem.append("<div class='l-icon l-icon-"
                                                       + item.icon + "'></div>");
                                             if(item.text){
                                                  ditem.addClass("l-treetoolbar-item-hasicon");
                                             }else{
                                                  ditem.addClass("l-treetoolbar-item-onlyicon");
                                             }
                                        }
                                        item.text
                                                  && $("span:first", ditem).html(
                                                            item.text);
                                        item.disable
                                                  && ditem
                                                            .addClass("l-toolbar-item-disable");
                                        item.click && ditem.click(function() {
                                             item.click(item);
                                        });

                                        ditem.hover(function() {
                                             $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                                             $(this).addClass("l-panel-btn-over");
                                        }, function() {
                                             $(this).removeClass('l-panel-btn-over');
                                        });
                                   }
                              }
                         });
})(jQuery);
//modify end;
define("Ligerui", ["jquery","JSON2"], function(){});

define('template/convertor',['jquery', 'JSON2', 'template/property', 'template/util', 'Ligerui'], function ($, JSON2, Property, Util) {

	var dbExecutorUrl = Util.getContextPath() + "/report/frame/param/commonComboBox/executeSQL.json";

	function toLigerForm (componentes, viewType) {
		var ligerFormObj = {
			inputWidth: 200,
			labelWidth: 90,
			space: 40
		}, fields = [], i = 0, manager = ViewValueManager.factory(viewType);

		for (i = 0 ; i < componentes.length ; i++) {
			fields.push(manager._buildField(componentes[i], viewType));
		}
		ligerFormObj['fields'] = fields;
		return ligerFormObj;
	};

	var ViewValueManager = ViewValueManager || {};

	ViewValueManager.value = ViewValueManager.value || {};

	$.extend(ViewValueManager, {
		factory : function (viewType) {
			if(typeof ViewValueManager.value[viewType] !== 'object'){
		      	throw{
		        	name: "Error",
		        	message: viewType + " doesn't exist "
		      	};
		    }
			return ViewValueManager.value[viewType];
		},

		_createAttres : function (viewType, component, componentType, type, options) {
			var i = 0, result = {}, attres = componentType[type], manager = ViewValueManager.factory(viewType);
			for (i = 0; attres && i < attres.length ; i++ ) {
				if (attres[i].indexOf('_') > 0) {
					var ps = attres[i].split("_"), subObj = ps[0], subObjAttr = ps[1];
					if(result[subObj] === undefined) result[subObj] = {};
					manager.setCommonFieldValue(result[subObj], subObjAttr, component[attres[i]]);
				} else {
					manager.setCommonFieldValue(result, attres[i], component[attres[i]]);
				}
			}
			manager.setOptions(result, options);
			return result;
		}
	});

	$.extend(ViewValueManager.value, {

		design : {

			_buildField : function (component, viewType) {
				var componentType = Property.getComponentType(component['type']), field = null;
				field = ViewValueManager._createAttres(viewType, component, componentType, 'widget', {type : component["type"]});
				field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options');
				field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', {cid : component["cid"], uuid : component["id"]});
				return field;
			},

			setCommonFieldValue : function (field, key, value) {
				if (key === 'disabled') {
					field[key] = 'disabled';
					return;
				}
				if (value === undefined || value === null || value === 'null' || key === 'value' || key === 'data') {
					return;
				}
				if (value == "true") {
		            field[key] = true;
		        } else if (value == "false") {
		            field[key] = false;
		        } else {
		            field[key] = value;
		        }
			},

			setOptions : function (field, options) {
				var key = null, value = null;
				for (key in options) {
					if (options.hasOwnProperty(key)) {
						value = options[key];
						if (key === 'type' && value === 'hidden') {
							field['display'] = '隐藏域';
							field[key] = 'text';
						} else {
							field[key] = value;
						}
					}
				}
			}
		},

		real : {

			_buildField : function (component, viewType) {
				var componentType = Property.getComponentType(component['type']), field = null;
				field = ViewValueManager._createAttres(viewType, component, componentType, 'widget', {type : component["type"]});
				if ('select' === component['type']) {
					var attrs = this._getExtendAtrres(component);
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options', attrs['options']);
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', attrs['attr']);
				} else {
					field["options"] = ViewValueManager._createAttres(viewType, component, componentType, 'options');
					field["attr"] = ViewValueManager._createAttres(viewType, component, componentType, 'attr', {uuid : component["id"]});
				}
				return field;
			},

			// _buildOtherType : function (field, componentType, component) {
			// 	var alreadyHandleType = ['widget', 'options', 'attr'], key, attres, result, i;
			// 	for (key in componentType) {
			// 		if (componentType.hasOwnProperty(key) && $.inArray(key, alreadyHandleType) < 0) {
			// 			attres = componentType[key], result = {};
			// 			for (var i = 0; attres && i < attres.length ; i++ ) {
			// 				this.setCommonFieldValue(result, attres[i].replace(key + "_", ""), component[attres[i]]);
			// 			}
			// 			field[key] = result;
			// 		}
			// 	}
			// },

			_onSelected: function (selectedValue, selectedText) {
				var $parent = this.valueField, uuid = $parent.attr('uuid');
				$('input[puuid=' + uuid + ']').each(function () {
					var $this = $(this), options = $this.attr('options');
					ViewValueManager.value.real._setChildData(options, selectedValue, selectedText, $this.attr('data-ligerid'));
				});
			},

			_setChildData : function (options, selectedValue, selectedText, ligeruiID) {
				if (typeof options === 'string') {
					options = JSON.parse(options.replace(/'/g, "\""));
				}
				if (options['data']) {
					var newData = new Array();
                    for (i = 0; i < options['data'].length; i++)
                    {
                        if (options['data'][i].pid === selectedValue)
                        {
                            newData.push(options['data'][i]);
                        }
                    }
                    $.ligerui.get(ligeruiID).setData(newData);
				} else if (options['url']) {
					$.ajax({
	                    type: options['ajaxType'],
	                    url: options['url'],
	                    cache: false,
	                    dataType: 'json',
	                    data: {
	                    	value : selectedValue,
	                    	text : selectedText
	                    },
	                    success: function (data){
	                        $.ligerui.get(ligeruiID).setData(data);
	                    }
	                });
				} else if (options['sql']) {
					$.ajax({
	                    type: "post",
	                    url: dbExecutorUrl,
	                    cache: false,
	                    dataType: 'json',
	                    data: {
	                    	sql : options['sql'],
	                    	db : options['db'],
	                    	value : selectedValue,
	                    	text : selectedText
	                    },
	                    success: function (data){
	                        $.ligerui.get(ligeruiID).setData(data);
	                    }
	                });
				} else {
				}
			},

			setCommonFieldValue : function (field, key, value) {
				if (value === undefined || value === null || value === 'null' ) {
					return;
				}
				if (value == "true") {
		            field[key] = true;
		        } else if (value == "false") {
		            field[key] = false;
		        } else {
		            field[key] = value;
		        }
			},

			_getExtendAtrres : function (component) {
				var attres = {};
				attres['attr'] = {};
				attres['options'] = {};
				attres['attr']['uuid'] = component["id"];
				if (component['datasource']) {
					attres['options']['onSelected'] = this._onSelected;
					var datasource = JSON.parse(component['datasource']);
					if (datasource['puuid']) {
						attres['attr']['puuid'] = datasource['puuid'];
						attres['attr']['options'] = JSON.stringify(datasource['options']).replace(/\"/g, "'");
					} else {
						this._convertOptions2Data(attres['options'], datasource['options']);
					}
				}
				return attres;
			},

			_convertOptions2Data : function (attresOptions, options) {
				if (typeof options === 'string') {
					options = JSON.parse(options);
				}
				if (options['data']) {//{"options":{"data" : [{ "id": "1", "text": "广东" },{ "id": "2", "text": "福建"}]}}
					attresOptions['data'] = options['data'];
				} else if (options['url']) {
					attresOptions['url'] = options['url'];
				} else if (options['sql']) {
					attresOptions['url'] = dbExecutorUrl;
					attresOptions['parms'] = {sql : options['sql'], db : options['db']};
				} else {
					attresOptions['data'] = [];
				}
			},

			setOptions : function (field, options) {
				var key = null;
				for (key in options) {
					if (options.hasOwnProperty(key) && options[key] !== undefined && options[key] !== null) {
						field[key] = options[key];
					}
				}
			}
		}

	});

	return {
		convert: toLigerForm
	};
});
/*
默认参数 扩展
 */

$.extend($.ligerDefaults.Grid, {
	checkbox : true,
	fixedCellHeight : false,
	frozen : false,
	async : true,
	allowUnSelectRow : true,
	onError : function(result, b) {
		BIONE.tip('发现系统错误 ' + b);
	}
});

$.extend($.ligerDefaults.Tab, {
	contextmenu : false
});

/*
 * 表格 扩展
 */

$.extend($.ligerui.controls.Grid.prototype, {
	_initBuildHeader : function() {
		var g = this, p = this.options;
		if (p.title) {
			$(".l-panel-header-text", g.header).html(p.title);
			if (p.headerImg)
				g.header.append("<img src='" + p.headerImg + "' />").addClass(
						"l-panel-header-hasicon");
		} else {
			g.header.hide();
		}
		if (p.toolbar) {
			if ($.fn.ligerToolBar)
				g.toolbarManager = g.topbar.ligerToolBar(p.toolbar);
		} else {
			g.topbar.remove();
		}
	},
	addEditRow : function(rowdata) {
		var g = this;
		rowdata = g.add(rowdata);
		return g.beginEdit(rowdata);
	},
	getEditingRow : function() {
		var g = this;
		for ( var i = 0, l = g.rows.length; i < l; i++) {
			if (g.rows[i]._editing)
				return g.rows[i];
		}
		return null;
	},
	getChangedRows : function() {
		var g = this, changedRows = [];
		pushRows(g.getDeleted(), 'delete');
		pushRows(g.getUpdated(), 'update');
		pushRows(g.getAdded(), 'add');
		return changedRows;

		function pushRows(rows, status) {
			if (!rows || !rows instanceof Array)
				return;
			for ( var i = 0, l = rows.length; i < l; i++) {
				changedRows.push($.extend({}, rows[i], {
					__status : status
				}));
			}
		}

	}
});

/*
 * 表格格式化函数扩展
 */

// 扩展 percent 百分比 类型的格式化函数(0到1之间)
$.ligerDefaults.Grid.formatters['percent'] = function(value, column) {
	if (value < 0)
		value = 0;
	if (value > 1)
		value = 1;
	var precision = column.editor.precision || 0;
	return (value * 100).toFixed(precision) + "%";
};

// 扩展 numberbox 类型的格式化函数
$.ligerDefaults.Grid.formatters['numberbox'] = function(value, column) {
	var precision = column.editor.precision || 0;
	return value.toFixed(precision);
};
// 扩展currency类型的格式化函数
$.ligerDefaults.Grid.formatters['currency'] = function(num, column) {
	// num 当前的值
	// column 列信息
	if (!num)
		return "0.00";
	num = num.toString().replace(/\$|\,/g, '');
	if (isNaN(num))
		num = "0.00";
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num * 100 + 0.50000000001);
	cents = num % 100;
	num = Math.floor(num / 100).toString();
	if (cents < 10)
		cents = "0" + cents;
	for ( var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + ','
				+ num.substring(num.length - (4 * i + 3));
	return "" + (((sign) ? '' : '-') + '' + num + '.' + cents);
};

/*
 * 表格编辑器
 */

// 扩展一个 百分比输入框 的编辑器(0到1之间)
$.ligerDefaults.Grid.editors['percent'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision || 0;
		var input = $("<input type='text' style='text-align:right' class='l-text' />");
		input.bind('keypress', function(e) {
			var keyCode = window.event ? e.keyCode : e.which;
			return keyCode >= 48 && keyCode <= 57 || keyCode == 46
					|| keyCode == 8;
		});
		input.bind('blur', function() {
			var showVal = input.val();
			showVal.replace('%', '');
			input.val(parseFloat(showVal).toFixed(precision));
		});
		container.append(input);
		return input;
	},
	getValue : function(input, editParm) {
		var showVal = input.val();
		showVal.replace('%', '');
		var value = parseFloat(showVal) * 0.01;
		if (value < 0)
			value = 0;
		if (value > 1)
			value = 1;
		return value;
	},
	setValue : function(input, value, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision || 0;
		if (value < 0)
			value = 0;
		if (value > 1)
			value = 1;
		var showVal = (value * 100).toFixed(precision) + "%";
		input.val(showVal);
	},
	resize : function(input, width, height, editParm) {
		input.width(width).height(height);
	}
};

// 扩展一个 数字输入 的编辑器
$.ligerDefaults.Grid.editors['numberbox'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision;
		var input = $("<input type='text' style='text-align:right' class='l-text' />");
		input.bind('keypress', function(e) {
			var keyCode = window.event ? e.keyCode : e.which;
			return keyCode >= 48 && keyCode <= 57 || keyCode == 46
					|| keyCode == 8;
		});
		input.bind('blur', function() {
			var value = input.val();
			input.val(parseFloat(value).toFixed(precision));
		});
		container.append(input);
		return input;
	},
	getValue : function(input, editParm) {
		return parseFloat(input.val());
	},
	setValue : function(input, value, editParm) {
		var column = editParm.column;
		var precision = column.editor.precision;
		input.val(value.toFixed(precision));
	},
	resize : function(input, width, height, editParm) {
		input.width(width).height(height);
	}
};

$.ligerDefaults.Grid.editors['date'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		var options = {};
		var ext = column.editor.p || column.editor.ext;
		if (ext) {
			var tmp = typeof (ext) == 'function' ? ext(editParm.record,
					editParm.rowindex, editParm.value, column) : ext;
			$.extend(options, tmp);
		}
		input.ligerDateEditor(options);
		return input;
	},
	getValue : function(input, editParm) {
		return input.liger('option', 'value');
	},
	setValue : function(input, value, editParm) {
		input.liger('option', 'value', value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width);
		input.liger('option', 'height', height);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['select'] = $.ligerDefaults.Grid.editors['combobox'] = {
	create : function(container, editParm, columnWidth) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		var options = {
			data : column.editor.data,
			slide : false,
			valueField : column.editor.valueField
					|| column.editor.valueColumnName,
			textField : column.editor.textField
					|| column.editor.displayColumnName,
			selectBoxWidth : false
		};
		if (columnWidth) {
			options.selectBoxWidth = columnWidth;
		}
		var ext = column.editor.p || column.editor.ext;
		if (ext) {
			var tmp = typeof (ext) == 'function' ? ext(editParm.record,
					editParm.rowindex, editParm.value, column) : ext;
			$.extend(options, tmp);
		}
		input.ligerComboBox(options);
		return input;
	},
	getValue : function(input, editParm) {
		return input.liger('option', 'value');
	},
	setValue : function(input, value, editParm) {
		input.liger('option', 'value', value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width - 1);
		input.liger('option', 'height', height - 2);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['int'] = $.ligerDefaults.Grid.editors['float'] = $.ligerDefaults.Grid.editors['spinner'] = {
	create : function(container, editParm) {
		var column = editParm.column;
		var input = $("<input type='text'/>");
		container.append(input);
		input.css({
			border : '#6E90BE'
		});
		var options = {
			type : column.editor.type == 'float' ? 'float' : 'int'
		};
		if (column.editor.minValue != undefined)
			options.minValue = column.editor.minValue;
		if (column.editor.maxValue != undefined)
			options.maxValue = column.editor.maxValue;
		input.ligerSpinner(options);
		return input;
	},
	getValue : function(input, editParm) {
		var column = editParm.column;
		var isInt = column.editor.type == "int";
		if (isInt)
			return parseInt(input.val(), 10);
		else
			return parseFloat(input.val());
	},
	setValue : function(input, value, editParm) {
		input.val(value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width);
		input.liger('option', 'height', height);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

$.ligerDefaults.Grid.editors['string'] = $.ligerDefaults.Grid.editors['text'] = {
	create : function(container, editParm) {
		var input = $("<input type='text' class='l-text-editing'/>");
		if (typeof (editParm.column.validate) == "string") {
			input.attr("validate", editParm.column.validate);
		} else if (editParm.column.validate
				&& typeof (editParm.column.validate) == "object") {
			input.attr("validate", JSON2.stringify(editParm.column.validate));
		}
		if (editParm.grid) {
			var id = editParm.grid.id + "_editor_"
					+ editParm.grid.editorcounter++ + "_"
					+ new Date().getTime();
			input.attr("name", id).attr("id", id);
		}
		container.append(input);
		input.ligerTextBox();
		return input;
	},
	getValue : function(input, editParm) {
		return input.val();
	},
	setValue : function(input, value, editParm) {
		input.val(value);
	},
	resize : function(input, width, height, editParm) {
		input.liger('option', 'width', width - 2);
		input.liger('option', 'height', 19);
	},
	destroy : function(input, editParm) {
		input.liger('destroy');
	}
};

// 扩展 ligerGrid 的 搜索功能(高级自定义查询)
$.ligerui.controls.Grid.prototype.showFilter = function() {
	var g = this, p = this.options;
	if (g.winfilter) {
		g.winfilter.show();
		return;
	}
	var filtercontainer = $('<div id="' + g.id + '_filtercontainer"></div>')
			.width(380).height(120).hide();
	var fields = [];
	$(g.columns).each(
			function() {
				var o = {
					name : this.name,
					display : this.display
				};
				var isNumber = this.type == "int" || this.type == "number"
						|| this.type == "float";
				var isDate = this.type == "date";
				if (isNumber)
					o.type = "number";
				if (isDate)
					o.type = "date";
				if (this.editor) {
					o.editor = this.editor;
				}
				fields.push(o);
			});
	var filter = filtercontainer.ligerFilter({
		fields : fields
	});
	g.winfilter = $.ligerDialog.open({
		width : 420,
		height : 208,
		target : filtercontainer,
		isResize : true,
		top : 50,
		buttons : [ {
			text : '确定',
			onclick : function(item, dialog) {
				loadFilterData();
				dialog.hide();
			}
		}, {
			text : '取消',
			onclick : function(item, dialog) {
				dialog.hide();
			}
		} ]
	});

	var historyPanle = $('<div class="historypanle"><select class="selhistory"><option value="0">历史查询记录</options></select><input type="button" value="删除" class="deletehistory" /><input type="button" value="保存" class="savehistory" /></div>');
	filtercontainer.append(historyPanle);

	var historySelect = $(".selhistory", historyPanle).change(function() {
		if (this.value == "0")
			return;
		var rule = getHistoryRule(this.value);
		if (rule)
			filter.setData(rule);
	});

	$(".deletehistory", historyPanle).click(function() {
		if (historySelect.val() == "0")
			return;
		$.ligerDialog.confirm('确定删除吗', function(yes) {
			if (yes) {
				removeHistory(historySelect.val());
				reLoadHistory();
			}
		});
	});

	$(".savehistory", historyPanle).click(
			function() {
				$.ligerDialog.prompt('输入保存名字', JSON2.stringify(new Date())
						.replace(/["-\.:]/g, ''), false, function(yes, name) {
					if (yes && name) {
						addHistory(name);
						reLoadHistory();
						historySelect.val(name);
					}
				});
			});

	reLoadHistory();

	function getKey() {
		return encodeURIComponent(p.url.replace(/(.+)?view=/, ''));
	}

	function reLoadHistory() {
		historySelect.html('<option value="0">历史查询记录</options>');
		var key = getKey();
		var history = BIONE.cookies.get(key);
		if (history) {
			var data = JSON2.parse(history);
			$(data).each(
					function() {
						historySelect.append('<option value="' + this.name
								+ '">' + this.name + '</options>');
					});
		}
	}
	function removeHistory(name) {
		var key = getKey();
		var data;
		var history = BIONE.cookies.get(key);
		if (history) {
			data = JSON2.parse(history);
			for ( var i = 0, l = data.length; i < l; i++) {
				if (data[i].name == name) {
					data.splice(i, 1);
					BIONE.cookies.set(key, JSON2.stringify(data));
					return;
				}
			}
		}
	}

	function addHistory(name) {
		var key = getKey();
		var data;
		var history = BIONE.cookies.get(key);
		if (history) {
			data = JSON2.parse(history);
			data.push({
				name : name,
				value : filter.getData()
			});

		} else {
			data = [ {
				name : name,
				value : filter.getData()
			} ];
		}
		BIONE.cookies.set(key, JSON2.stringify(data));
	}

	function getHistoryRule(name) {
		var key = getKey();
		var history = BIONE.cookies.get(key);
		if (history) {
			var data = JSON2.parse(history);
			for ( var i = 0, l = data.length; i < l; i++) {
				if (data[i].name == name)
					return data[i].value;
			}
		}
		return null;
	}

	function loadFilterData() {
		var data = filter.getData();
		if (data && data.rules && data.rules.length) {
			g.set('parms', {
				where : JSON2.stringify(data)
			});
		} else {
			g.set('parms', {});
		}
		g.loadData();
	}
};

/*
 * 表单 扩展
 */

$.extend($.ligerui.controls.TextBox.prototype, {
	checkNotNull : function() {
		var g = this, p = this.options;
		if (p.nullText && !p.disabled) {
			if (!g.inputText.val()) {
				g.inputText.addClass("l-text-field-null").val(p.nullText);
			}
		}
	}
});

$.extend($.ligerui.controls.ComboBox.prototype, {
	_setHeight : function(value) {
		var g = this;
		if (value > 10) {
			g.wrapper.height(value);
			g.inputText.height(value);
			g.link.height(value);
			g.textwrapper.css({
				width : value
			});
		}
	}
});

// 扩展 DateEditor 的updateStyle方法
$.ligerui.controls.DateEditor.prototype.updateStyle = function() {
	var g = this, p = this.options;
	// Grid的date默认格式化函数就有对日期的处理
	var v = $.ligerDefaults.Grid.formatters['date'](g.inputText.val(), {
		format : p.format
	});
	g.inputText.val(v);
};

/*
 * 下拉框 combobox
 */

// 下拉框 加载文本值(有的时候在数据库只是返回了id值，并没有加载文本值，需要调用这个方法，远程获取)
$.ligerui.controls.ComboBox.prototype.loadText = function(options) {
	var g = this, p = this.options;
	options = $.extend({
		url : '../handler/select.ashx',
		view : null,
		idfield : null,
		textfield : null
	}, options || {});
	var value = options.value || g.getValue();
	var where = {
		op : 'and',
		rules : [ {
			field : options.idfield,
			op : 'equal',
			value : value
		} ]
	};
	$.ajax({
		cache : false,
		async : true,
		dataType : 'json',
		type : 'post',
		url : options.url,
		data : {
			view : options.view,
			idfield : options.idfield,
			textfield : options.textfield,
			where : JSON2.stringify(where)
		},
		success : function(data) {
			if (!data || !data.length)
				return;
			g._changeValue(data[0]['id'], data[0]['text']);
		}
	});
};

define("LigeruiExpand", ["Ligerui"], function(){});

require(['jquery', 'JSON2', 'Underscore', 'Backbone', 'template/model', 'template/convertor', 'Ligerui', 'LigeruiExpand'], function ($, JSON2, _, Backbone, Model, Convertor) {

    var View = Backbone.View.extend({

        initialize: function ($target, options) {
            this.el = $target;
            this.template = new Model.Template();
            this.componentes = this.template._getComponentes();
            // var data = [{"type":"text","name":"component1","newline":"true","disabled":"null"},{"type":"text","name":"component2","newline":"true","disabled":"null"}];
            this.componentes.add(options['data']);
            this.render();
        },

        render: function () {
            this.el.ligerForm(Convertor.convert(this.componentes.toFormPorperties(), 'real'));
            return this;
        }
    });
    
    $.fn.extend({
        templateView : function(options){
            new View(this, options);
        }
    });
});
define("template/view", function(){});

//require.config({
//    // baseUrl: '../../../../js',
//    paths: { 
//        JSON2: 'bione/json2.min',
//        Ligerui: 'ligerUI/ligerui.all',
//        LigeruiExpand: 'ligerUI/ligerui.expand',
//        Underscore: 'backbone/underscore',
//        Backbone: 'backbone/backbone',
//        Validate: 'jquery/jquery.validate.min',
//        ValidateExpand: 'jquery/jquery.validate.expand.min',
//        ValidateMessage: 'jquery/jquery.validate.messages_cn.min',
//        ValidateExpandMessage: 'jquery/jquery.validate.expand.messages_cn.min',
//        Metadata: 'jquery/jquery.metadata',
//        UUID: 'uuid/uuid'
//    },
//    shim: {
//        JqueryUI: {
//            deps: ['jquery']
//        },
//        Ligerui: {
//            deps: ['jquery', 'JSON2']
//        },
//        LigeruiExpand: {
//            deps: ['Ligerui']
//        },
//        Underscore: {
//            exports: '_'
//        },
//        Backbone: {
//            deps: ['jquery', 'Underscore'],
//            exports: 'Backbone'
//        },
//        UUID: {
//            exports: 'UUID'
//        }
//    }
//});
