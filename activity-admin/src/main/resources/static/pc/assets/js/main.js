<!--选项卡-->
//选项卡--first
function For(c, d) {
    function stopPropagation(e) {
        e = e || window.event;
        if (e.stopPropagation) { //W3C阻止冒泡方法
            e.stopPropagation();
        } else {
            e.cancelBubble = true; //IE阻止冒泡方法
        }
    };
    for (var i = 0; i < c.length; i++) {
        c[i].index = i;
        c[i].onclick = function(e) {
            stopPropagation(e)
            for (var j = 0; j < c.length; j++) {
                c[j].className = "";
                d[j].className = "hide";
            }
            this.className = "current";
            d[this.index].className = "";
        }
    }
}
function tab(a, b) {
    var aLi = document.getElementById(a).getElementsByTagName('li');
    var aDiv = document.getElementById(b).getElementsByTagName('section');
    For(aLi, aDiv)
}
// Tab切换--second
function EW_tab(option) {
    this.oTab_btn = this.getDom(option.tabBtn);
    this.oTab_clist = this.getDom(option.tabCon);
    if (!this.oTab_btn || !this.oTab_clist) return;
    this.sCur = option.cur;
    this.type = option.type || 'click';
    this.nLen = this.oTab_btn.length;
    this.int();
}
EW_tab.prototype = {
    getId: function(id) {
        return document.getElementById(id);
    },
    getByClassName: function(className, parent) {
        var elem = [],
            node = parent != undefined && parent.nodeType == 1 ? parent.getElementsByTagName('*') : document.getElementsByTagName('*'),
            p = new RegExp("(^|\\s)" + className + "(\\s|$)");
        for (var n = 0, i = node.length; n < i; n++) {
            if (p.test(node[n].className)) {
                elem.push(node[n]);
            }
        }
        return elem;
    },
    getDom: function(s) {
        var nodeName = s.split(' '),
            p = this.getId(nodeName[0].slice(1)),
            c = this.getByClassName(nodeName[1].slice(1), p);
        if (!p || c.length == 0) return null;
        return c;
    },
    change: function() {
        var cur = new RegExp(this.sCur, 'g');
        for (var n = 0; n < this.nLen; n++) {
            this.oTab_clist[n].style.display = 'none';
            this.oTab_btn[n].className = this.oTab_btn[n].className.replace(cur, '');
        }
    },
    int: function() {
        var that = this;
        this.oTab_btn[0].className += ' ' + this.sCur;
        this.oTab_clist[0].style.display = 'block';
        for (var n = 0; n < this.nLen; n++) {
            this.oTab_btn[n].index = n;
            this.oTab_btn[n]['on' + this.type] = function() {
                that.change();
                that.oTab_btn[this.index].className += ' ' + that.sCur;
                that.oTab_clist[this.index].style.display = 'block';
            };
        }
    }
};


// 美化滚动条的设置参数
var customScroll = {
    cursorborder: "",
    cursorwidth: 8,
    cursorcolor: "#CAD5E6",
    boxzoom: false,
    autohidemode: true,
    railpadding: {top: 0, right: 2, left: 0, bottom: 0}
};

//禁止滚动条滚动
function unScroll() {
    $('body').addClass('popOverflow');
}

//移除禁止
function removeUnScroll() {
    $('body').removeClass('popOverflow');
}

// 关闭弹窗
function closePop() {
    $('.maskDiv').hide();
}
// 导出成功弹窗
function exportSuccess() {
    $('#popExportSuccess').show();
}
