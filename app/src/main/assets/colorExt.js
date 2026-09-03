(function() {
    var SCHEME_COLOR_PREFIX = "app-color://";
    try {
        var color = '';
        var meta = document.querySelector('meta[name="theme-color"]');
            if (meta && meta.content) {
                color = meta.content;
            } else if (document.body) {
                var style = document.defaultView ? document.defaultView.getComputedStyle(document.body, null) : null;
                if (style && style.backgroundColor) {
                    color = style.backgroundColor;
                } else if (document.body.style && document.body.style.backgroundColor) {
                    color = document.body.style.backgroundColor;
                }
            }
            if (color && color !== 'rgba(0, 0, 0, 0)' && color !== 'transparent') {
                window.location.href = SCHEME_COLOR_PREFIX + encodeURIComponent(color);
            } else {
                window.location.href = SCHEME_COLOR_PREFIX+'default';
            }
    } catch(e) {
       window.location.href = SCHEME_COLOR_PREFIX+'default';
    }
})()