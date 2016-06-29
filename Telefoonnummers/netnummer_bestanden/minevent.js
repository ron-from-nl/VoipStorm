/*
 * $Id: minevent.js 56163 2008-12-05 05:40:52Z berickson $
 * $URL: https://build.corp.localmatters.com/subversion/DestinationSearch/edpl/trunk/src/main/webapp/template/js/minevent.js $
 */

/**
 * minimal event library
 * only the bare minimum needed for dropdowns
 */
LMI.MinEvents = (function() {
    var safariKeys = {
        63232: 38, // up
        63233: 40, // down
        63234: 37, // left
        63235: 39, // right
        63273: 36, // home
        63275: 35, // end
        63276: 33, // pgup
        63277: 34  // pgdn
    };
    /**
     * @method addEvent
     * @param {HTMLElemnt} el
     * @param {String} type
     * @param {Function} func
     */
    function addEvent( el, type, func ) {
        if( el.addEventListener ) {
            el.addEventListener( type, func, false );
        } else if( el.attachEvent ) {
            el.attachEvent( "on" + type, func );
        } else {
            // worthless error message just to give me a string to search for if it ever happens
            throw new Error( 'addEvent: unsupported browser' );
        }
    }
    /**
     * @method removeEvent
     * @param {HTMLElemnt} el
     * @param {String} type
     * @param {Function} func
     */
    function removeEvent( el, type, func ) {
        if( el.removeEventListener ) {
            el.removeEventListener( type, func, false );
        } else if( el.detachEvent ) {
            el.detachEvent( "on" + type, func );
        } else {
            // worthless error message just to give me a string to search for if it ever happens
            throw new Error( 'removeEvent: unsupported browser' );
        }
    }

    /**
     * @method addWindowLoadEvent
     * @param {Function} func
     */
    function addWindowLoadEvent( func ) {
        var oldFunc;

        if( window.onload ) {
            oldFunc = window.onload;
        }
        window.onload = function() {
            if( typeof oldFunc === "function" ) {
                oldFunc();
            }
            func();
        };
    }

    /**
     * stops propagation, and prevents default
     * @method stopEvent
     * @param {Event} e
     */
    function stopEvent( e ) {
        if( e.stopPropagation ){
            e.stopPropagation();
        } else {
            e.cancelBubble = true;
        }

        if( e.preventDefault ){
            e.preventDefault();
        } else {
            e.returnValue = false;
        }
    }
    /**
     * returns the code of the key that was pressed normalized for safari
     * @method getKeyCode
     * @param {Event} e
     */
    function getKeyCode( e ) {
        var key = 0,
            c = e.charCode;
        if( c > 60000 && ( c in safariKeys ) ) {
            key = safariKeys[c];
        } else if( ! c ) {
            key = e.keyCode;
        }
        return key;
    }
    /**
     * gets the target of the event and searches up the tree looking for an element of type <code>type</code>
     * @method findTarget
     * @param {Event} e
     * @param {String} type
     */
    function findTarget( e, type ) {
        var el = e.target || e.srcElement;
        type = type.toUpperCase();
        while( el && el.nodeName !== type ) {
            el = el.parentNode;
        }
        return el;
    }

    return {
        on: addEvent,
        addEvent: addEvent,
        addWindowLoadEvent: addWindowLoadEvent,
        stopEvent: stopEvent,
        getKeyCode: getKeyCode,
        findTarget: findTarget,
        removeEvent: removeEvent
    };
})();
