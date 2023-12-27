/* Side Navbar */
function loadNav() {
    if(sessionStorage.getItem("sideNavOpen") === 'true') {
        openNav(false);
    } else {
        closeNav(false);
    }
}

function openNav(transition) {
    if(transition) {
        document.getElementById("sidenav").style.transition = "0.5s";
        document.getElementById("main").style.transition = "margin-left 0.5s";
        document.getElementsByClassName("closeButton")[0].style.transition = "left 0.5s";
    } else {
        document.getElementById("sidenav").style.transition = "none";
        document.getElementById("main").style.transition = "none";
        document.getElementsByClassName("closeButton")[0].style.transition = "none";
    }
    document.getElementById("sidenav").style.width = "250px";
    document.getElementById("main").style.marginLeft = "250px";
    document.getElementsByClassName("closeButton")[0].style.left = "180px";
    
    sessionStorage.setItem("sideNavOpen", true);
}

function closeNav(transition) {
    if(transition) {
        document.getElementById("sidenav").style.transition = "0.5s";
        document.getElementById("main").style.transition = "margin-left 0.5s";
        document.getElementsByClassName("closeButton")[0].style.transition = "left 0.5s";
    } else {
        document.getElementById("sidenav").style.transition = "none";
        document.getElementById("main").style.transition = "none";
        document.getElementsByClassName("closeButton")[0].style.transition = "none";
    }
    document.getElementById("sidenav").style.width = "0";
    document.getElementById("main").style.marginLeft = "0";
    document.getElementsByClassName("closeButton")[0].style.left = "-70px";

    sessionStorage.setItem("sideNavOpen", false);
}

// table sorting
function setupSort() {
    getCellValue = (tr, idx) => tr.children[idx].innerText || tr.children[idx].textContent;

    comparer = (idx, asc) => (a, b) => ((v1, v2) => 
        v1 !== '' && v2 !== '' && !isNaN(v1) && !isNaN(v2) ? v1 - v2 : v1.toString().localeCompare(v2)
        )(getCellValue(asc ? a : b, idx), getCellValue(asc ? b : a, idx));
    
    // do the work...
    document.querySelectorAll('th').forEach(th => th.addEventListener('click', (() => {
        table = th.closest('table');
        Array.from(table.querySelectorAll('tr:nth-child(n+2)'))
            .sort(comparer(Array.from(th.parentNode.children).indexOf(th), this.asc = !this.asc))
            .forEach(tr => table.appendChild(tr) );
    })));
}


function setLastModifiedDate() {
    const date = new Date(document.lastModified);
    document.getElementById("lastModifiedDate").innerHTML = date.toDateString();
}

// <pre><code></code></pre> tag (non usato)
function setupCode() {
    document.querySelectorAll("pre code").forEach((element) => {
        let html = element.outerHTML
        let pattern = html.match(/\s*\n[\t\s]*/)

        element.outerHTML = html.replace(new RegExp(pattern, "g"),'\n')
    });
}

function showOverlay(cb, id) {
    if(cb.checked) {
        document.getElementById(id).style.visibility = "visible";
    } else {
        document.getElementById(id).style.visibility = "hidden";
    }
}