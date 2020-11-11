
//获取参数
 function getUrlParam(name){
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r!=null) return unescape(r[2]); return null;
}
let type = getUrlParam("type");//由哪个页面进入
if(type == "create"){
    $('#create').show();
    $('#edit').hide();
}else{
    $('#edit').show();
    $('#create').hide();
}
//上传图片
function showImg(input) {
    var file = input.files[0];
    var reader = new FileReader()
    reader.onload = function(e) {
        document.querySelector(".cover-img").src=e.target.result
    }
    reader.readAsDataURL(file)
}
function showImg1(input) {
    var file = input.files[0];
    var reader = new FileReader()
    reader.onload = function(e) {
        document.querySelector(".add-img").src=e.target.result
    }
    reader.readAsDataURL(file)
}

$('#custom').click(function(){
    $('.dailog-box1').fadeIn()
    $('body,html').addClass('scroll');
    $('.fenlei-input').val("自定义选项")
})
$('#addFenlei').click(function(){
    $('.dailog-box1').fadeIn()
    $('body,html').addClass('scroll');
})
$('.cancle').click(function(){
    $('.dailog-box1').fadeOut();
    $('.dailog-box2').fadeOut();
    $('body,html').removeClass('scroll');
    $('.fenlei-input').val("")
})
$('.close').click(function(){
    $('.dailog-box1').fadeOut();
    $('.dailog-box2').fadeOut();
    $('body,html').removeClass('scroll');
    $('.fenlei-input').val("")
})
$('#addModle').click(function(){
    $('.dailog-box2').fadeIn();
    $('body,html').addClass('scroll');
})
$('#next-step').click(function(){
    $('.add-details').show();
    $('.add-modle').hide();
    $('#next-step').hide();
    $('#complete').show();
    $('body,html').addClass('scroll');
})
$('#complete').click(function(){
    $('.dailog-box2').fadeOut();
    $('body,html').removeClass('scroll');
})
$('.dellete').click(function(){
    $(this).parent().hide()
})


// $('.dailog-box1').click(function(e){
//     e.stopPropagation();
//     e.preventDefault();
//     $('.dailog-box1').hide();
//     $('body,html').removeClass('scroll');
// })
// $('.active-name').on("input",$.fn.wordCount(20,$('.max')))