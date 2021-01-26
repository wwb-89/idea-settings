//    字数限制 公共的，可以提到公共JS里面
$.fn.extend({
    displayPart: function (displayLength) {
        // var displayLength = 58;
        // displayLength = this.attr("displayLength") || displayLength;
        var text = this.text();
        if (!text) return "";

        var result = "";
        var count = 0;
        for (var i = 0; i < displayLength; i++) {
            var _char = text.charAt(i);
            if (count >= displayLength) break;
            if (/[^x00-xff]/.test(_char)) count++;

            result += _char;
            count++;
        }
        if (result.length < text.length) {
            result += "...";
        }
        this.text(result);
    },
    //计数加法
    wordCount: function (maxLength, wordWrapper) {

        var self = this;
        $(self).attr("maxlength", maxLength);
        showWordCount();
        $(this).on("input propertychange", showWordCount);
        function showWordCount() {
            var curLength = $(self).val().length;
            // console.log(curLength)
            // console.log(curLength)
            // var leaveCount = maxLength - curLength;
            wordWrapper.html(curLength);
        }
    },
    //计数减法
    wordCountsubtraction: function (maxLength, wordWrapper) {
        var self = this;
        $(self).attr("maxlength", maxLength);
        showWordCount();
        $(this).on("input propertychange", showWordCount);

        function showWordCount() {
            var curLength = $(self).val().length;
            var leaveCount = maxLength - curLength;
            wordWrapper.html(leaveCount);
        }
    }
});
/*页面图片裁剪*/
function cropImgFun(elem) {
    var imgs;
    if (elem.length == 0) {
        return
    }
    if (elem instanceof jQuery) {
        imgs = elem.find('.js-crop-img img');
    } else if (typeof(elem) == 'string') {
        imgs = $(elem).find('.js-crop-img img');
    }
    if (imgs.length == 0) {
        return
    }
    imgs.each(function () {
        if ($(this).attr("src")) {
            var image = new Image();
            var $this = $(this);
            image.src = $(this).attr("src");
            console.log( image.src)
            image.onload = function () {
                $this.jqthumb({
                    width: '100%',
                    height: '100%',
                    before: function (img) {
                        var imgUrl = $(img).attr('src');
                        var newUrl = imgUrl.replace(/\\/g, "/");
                        $(img).attr('src', newUrl);
                    }
                });
            }
        }
    });
}