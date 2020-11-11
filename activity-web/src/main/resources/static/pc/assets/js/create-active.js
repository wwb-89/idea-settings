function showImg(input) {
    var file = input.files[0];
    var reader = new FileReader()
    reader.onload = function(e) {
        document.querySelector(".cover-img").src=e.target.result
    }
    reader.readAsDataURL(file)
}
function isShow(e,target){
    if (e.is(":checked")) {
        target.show()
    }else{
        target.hide()
    }
}
isShow($('.switch'),$('.setForm'))
$('.switch').on("change",function(){
    isShow($(this),$('.setForm'))
})
// $('.active-name').on("input",$.fn.wordCount(20,$('.max')))