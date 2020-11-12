
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
