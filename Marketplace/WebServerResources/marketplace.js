window.onload = function() {
    var links = document.links || document.getElementsByTagName('a');
    var n = links.length;
    for (var i = 0; i < n; i++) {
        if (links[i].title && links[i].title != '') {
            links[i].innerHTML += '<span>'+links[i].title+'</span>'; 
            links[i].title = '';
        }
    }
}
