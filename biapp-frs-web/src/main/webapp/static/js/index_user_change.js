var curImageUrl = {};

//保存用户头像修改
function saveUserImageChange() {
    if(curImageUrl[0]){
        $("#user_image_left").attr('src',ctx + curImageUrl);
        $("#user_image_top").attr('src',ctx + curImageUrl);
        $("#user_image_right").attr('src',ctx + curImageUrl);
        $(".pic_box").animate({
            'top':'-1000px'
        },500);

        $.ajax({
            async : true,
            url : ctx + '/bione/admin/user/changeUserHeadImage',
            data : {
                curImageUrl : curImageUrl
            },
            type : "post",
            dataType : "json",
            success : function(result){
                BIONE.tip("个人头像保存成功");
            },
            error: function () {
                BIONE.tip("个人头像保存失败");
            }

        })

    }else{
        return false;
    }
}

$(function () {
    var userchange = {};
    userchange = '<div class=\"pic_box\" id=\"pic_box\">';
    userchange += '	<div class=\"header\">';
    userchange += '		<p style=\"padding: 4px;\">设置个人头像</p>';
    userchange += '		<span class=\"close\">x</span>';
    userchange += '	</div>';
    // userchange += '	<span class=\"xianiao\"></span>';
    userchange += '	<ul style=\"width:220px;height:220px\">';
    userchange += '		<li><img src=\"'+ctx+'/images/classics/index/users/user0.png\" style=\"width:60px;height:60px\" /></li>';
    userchange += '		<li><img src=\"'+ctx+'/images/classics/index/users/user1.png\" style=\"width:60px;height:60px\" /></li>';
    userchange += '		<li><img src=\"'+ctx+'/images/classics/index/users/user2.png\" style=\"width:60px;height:60px\" /></li>';
    userchange += '	</ul>';
    userchange += '	<div class=\"bt_box\">';
    userchange += '		<a class=\"queren\" href=\"javascript:saveUserImageChange()\">保存</a>';
    userchange += '		<a class=\"gb\" href=\"#\">取消</a>';
    userchange += '	</div>';
    userchange += '</div>';
    // $('#user_image_left').after(userchange);
    $('#content-wrapper').after(userchange);

    $('#user_image_left').hover(function () {
        $(this).css('cursor','pointer');
    });

    //添加更换用户头像事件
    $('#user_image_left').click(function(){
        $(".pic_box").animate({
            // 'top':'-1000px'
            'top':'100px'
        },500);
    });

    $(".close,.gb").click(function(){
        $(".pic_box").animate({
            'top':'-500px'
        },500);
    });

    $(".my_box").click(function(){
        $(".pic_box").animate({
            'top':'15px',
        },300);
    });

    var $box = document.getElementById('pic_box');
    var $li = $box.getElementsByTagName('li');
    var index = 0;
    for(var i=0;i<$li.length;i++){
        $li[i].index=i;
        $li[i].onclick=function(){
            $li[index].style.borderRadius="15%";
            this.style.borderRadius="50%";
            index = this.index;
        }
    }

    $(".pic_box li img").click(function(){
        var src = $(this).attr("src");
        var imageUrl = src.substring(src.indexOf("/images"), src.length);
        curImageUrl = imageUrl ;
    })


})