Vue.component("vue-rich-text", {
    template: "<div class='dialog-box5' v-show='show'>\n" +
        "            <div class='dailog dialog-wrap edit-prize'>\n" +
        "                <div class='header'>\n" +
        "                    <span class='color333'>富文本编辑</span>\n" +
        "                    <img :src='closeImgUrl' class='close' @click='close'>\n" +
        "                </div>\n" +
        "                <div class='dialog-content' style='height: auto'>\n" +
        "                    <div class='iframe-content' style='min-height: auto'>\n" +
        "                        <div class='input-box notice'>\n" +
        "                            <el-input type='textarea' placeholder='请输入' :autosize='{ minRows: 5, maxRows: 5}' v-model='content' resize='none' maxlength='100' show-word-limit></el-input>\n" +
        "                        </div>\n" +
        "                        <div class='uplode-list'>\n" +
        "                            <div class='uplode-img' id='rich-uploader'></div>\n" +
        "                            <template v-if='cloudIds && cloudIds.length > 0'>\n" +
        "                                <div class='img-box js-crop-img' v-for='cloudId in cloudIds'>\n" +
        "                                    <img :src='cloudDomain + \"/star3/380_160c/\" + cloudId'>\n" +
        "                                    <i class='delete'></i>\n" +
        "                                    <div class='hoverBg'>\n" +
        "                                        <i class='icon-zoom'></i>\n" +
        "                                    </div>\n" +
        "                                </div>\n" +
        "                            </template>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <div class='footer'>\n" +
        "                    <div class='btn btn-border' @click='close'>取消</div>\n" +
        "                    <div class='btn' :class='content ? \"btn-normal\" : \"btn-forbid\"' @click='confirm'>确定</div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>",
    data: function () {
        return {
            show: false,
            ctx: ctx,
            closeImgUrl: ctx + '/pc/assets/images/close.png',
            content: '',
            cloudIds: [],
            uploader: null,
            uploading: false
        }
    },
    methods: {
        // 初始化文件上传
        initUploader: function () {
            var $this = this;
            $this.uploader = WebUploader.create({
                // swf文件路径
                swf: ctx + "/pc/assets/lib/webuploader/Uploader.swf",
                // 文件接收服务端。
                server: ctx + "/api/upload/img",
                // 选择文件的按钮。可选。
                // 内部根据当前运行是创建，可能是input元素，也可能是flash.
                pick: "#rich-uploader",
                // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
                resize: false,
                auto: true,
                duplicate: true,
                // 只允许选择图片文件。
                accept: {
                    title: 'Images',
                    extensions: 'gif,jpg,jpeg,bmp,png',
                    mimeTypes: 'image/*'
                }
            });
            $this.uploader.on("startUpload", function (file, response) {
                // 上传完成
                $this.uploading = true;
            });
            $this.uploader.on("uploadSuccess", function (file, response) {
                if (response.success) {
                    var data = response.data;
                    data = activityApp.getJsonObject(data);
                    var result = data.result;
                    if (result == 1) {
                        var cloudId = data.objectid;
                        $this.$set($this.activity, "coverUrl", "");
                        $this.$set($this.activity, "coverCloudId", cloudId);
                        $this.$nextTick(function () {
                            $this.handleImg();
                        });
                    } else {
                        app.showMsg("上传失败");
                    }
                } else {
                    var errorMessage = response.message;
                    if (activityApp.isEmpty(errorMessage)) {
                        errorMessage = "上传失败";
                    }
                    app.showMsg(errorMessage);
                }
            });
            $this.uploader.on("uploadComplete", function (file, response) {
                // 上传完成
                $this.uploading = false;
            });
            $this.uploader.on("uploadError", function (file, reason) {
                app.showMsg(reason);
            });
        },
        destroyUploader: function () {
            var $this = this;
        },
        showWindow: function (content, cloudIds) {
            var $this = this;
            $this.content = content || '';
            $this.cloudIds = $.extend(true, [], cloudIds || []);

            $this.$nextTick(function () {
                $this.initUploader();
            });

            $this.show = true;
        },
        close: function () {
            var $this = this;
            $this.show = false;
        },
        confirm: function () {
            var $this = this;
            if (activityApp.isEmpty($this.content)) {
                app.showMsg("文本内容不能为空")
                return;
            }

            $this.$emit("callback", $this.content, $this.cloudIds);
            $this.close();

        }
    }
})