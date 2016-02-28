indexApp.controller('dblist', function($scope, $routeParams,$http) {
    var appId = $routeParams.appId;
    $scope.appId = appId;
    $http.post('/loadApp.do?appId='+appId, {}).success(function(data){
        if(data.status)
        {
            $scope.appname = data.data.appName;
            $scope.modules = data.data.modules;
        }else
        {
            alert(data.msg);
        }
    });

    $http({
        url: '/loadPdm.do',
        method: "POST",
        data: "appId="+appId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        $scope.pdm = data;
    });

    $http.post('/listDb.do', {}).success(function(data){

        $scope.dbs = data;

        for(var i=0;i<data.length;i++)
        {
            if(i%2==0)
                data[i].rowclass = "odd";
            else
                data[i].rowclass = "even";

            if(data[i].checkStatus)
            {
                data[i].checkColor = "green";
            }else
            {
                data[i].checkColor = "red";
            }
        }

    });



    $scope.toServiceList = function()
    {
        location.href = "#servicelist/"+appId+"/0";
    };

    $scope.dbAddFun = function()
    {
        location.href = "#dbAdd/"+appId+"/ADD";
    };

    $scope.loadChange = function(changeId)
    {
      location.href = "#dbAdd/"+appId+"/"+changeId;
    };

    $scope.downloadPdm = function()
    {
        location.href = "/downloadPdm.do?appId="+appId;
    }
    $scope.downloadLockPdm = function()
    {
        location.href = "/downloadPdm.do?appId="+appId+"&lock="+1;
        location.reload();
    }

});
indexApp.controller('dbAdd', function($scope, $routeParams,$http,$sce) {
    $(".wysiwyg").cleditor({"width":583});
    var appId = $routeParams.appId;
    var changeId = $routeParams.changeId;
    if(changeId == "ADD")
    {
        changeId = "";
    }
    if(changeId != "")
    {
        $scope.add = false;
        //changeId = -1;
        $http({
            url: '/loadDbChange.do',
            method: "POST",
            data: "changeId="+changeId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
            var changelog = data.changeLog;
            //$scope.changelog = changelog;
            var o = $("#changelog").cleditor()[0];
            $("#changelog").val(changelog);
            o.updateFrame();
            $scope.db = data.db;
            if(data.dbChecklistList!=null)
            {
                for(var i=0;i<data.dbChecklistList.length;i++)
                {
                    var content = data.dbChecklistList[i].checkContent;
                    content = content.replace("\n","<br/>");
                    data.dbChecklistList[i].checkContent = $sce.trustAsHtml(content);
                }
            }
            $scope.checkList = data.dbChecklistList;
            $scope.checkStatus = data.checkStatus;

            $scope.downloadPdm = function()
            {
                location.href = "downloadDb.do?name=pdm&changeId="+changeId;
            }
            $scope.downloadScript = function()
            {
                location.href = "downloadDb.do?name=script&changeId="+changeId;
            }

        });
    }else{
        $scope.add = true;
        changeId = "";
    }

    $scope.changeId = changeId;

    $scope.appId = appId;

    $scope.back = function()
    {
        location.href = "#dblist/"+appId;
    };

    $http.post("/loginUser.do", {}).success(function(data){
        var role = data.role;
        if(role == '1')
        {
            $scope.checkUser = false;
        }else{
            $scope.checkUser = true;
        }
    });

    $http({
        url: '/loadApp.do',
        method: "POST",
        data: "appId="+appId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        if(data.status)
        {
            var app = data.data;
            var dbs = app.dbs;
            var dbsArr = dbs.split(",");
            $scope.dbs = dbsArr;
            $scope.appCode = app.appCode;
        }else
        {
            alert(data.msg);
        }
    });


    $scope.savechange = function(){

        var changelog = $(".wysiwyg").val();
        var db = "";
        $("input[name='db']").each(
            function(){
                if($(this).get(0).checked){
                    db = $(this).get(0).getAttribute("value");
                }
            });

        var appId = $("#appId").val();
        if(changelog=="")
        {
            alert("请添加修订日志");
            return;
        }
        if(db == "")
        {
            alert("请填写所属库");
            return;
        }
        if(changeId == "") {
            if ($("#pdm").val() == "") {
                alert("请上传PDM图");
                return;
            }
            if ($("#sql").val() == "") {
                alert("请上传脚本文件");
                return;
            }
        }
        $http({
            url: '/saveDbChange.do',
            method: "POST",
            data: "appId="+appId+"&db="+db+"&changelog="+changelog+"&changeId="+changeId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {

            if(data.status)
            {
                alert("更新成功");
                location.href = "#dblist/"+appId;
            }else
            {
                alert(data.msg);
            }

        });

    };



    $scope.commit = function(){
        $(".dConf").dialog( "open" );
    }

    window.setTimeout("initDialog();",200);



});

//function uploadDbSuccess(result)
//{
//    if(result.status==false)
//    {
//        alert(result.msg);
//    }else
//    {
//        alert("上传成功");
//        var destfilename = result.data;
//        if(destfilename.indexOf("pdm")!=-1)
//        {
//            $("#pdm").val(destfilename);
//        }
//        if(destfilename.indexOf("sql")!=-1)
//        {
//            $("#sql").val(destfilename);
//        }
//
//        //location.href = "/index.html#servicelist/"+appId+"/0";
//    }
//}

function changeFile(o) {
    var fileId = $(o).attr("id");
    var appId = $("#appId").val();
    var extFile = "";
    if(fileId == "file1")
    {
        extFile = ".pdm";
    }
    if(fileId == "file2")
    {
        extFile = ".sql";
    }

    if($(o).val().indexOf(extFile)==-1)
    {
        alert("请选择扩展名为"+extFile+"的文件");
        return;
    }

    $.ajaxFileUpload
    (
        {
            url: 'uploadDb.do?appId='+appId+"&changeId="+$("#changeId").val(), //用于文件上传的服务器端请求地址
            secureuri: false, //是否需要安全协议，一般设置为false
            fileElementId: fileId, //文件上传域的ID
            dataType: 'json', //返回值类型 一般设置为json
            success: function (data, status)  //服务器成功响应处理函数
            {
                console.info(data);
                var status = data.status;
                if(status)
                {
                    //alert(data.data);
                    alert("上传成功");
                    $("#"+fileId).next().val(data.data);
                }else
                {
                    alert("上传失败:"+data.msg);
                }
            },
            error: function (data, status, e)//服务器响应失败处理函数
            {
                alert(e);
            }
        }
    )
}

function initDialog() {
    $.fx.speeds._default = 400; // Adjust the dialog animation speed
    $(".dConf").dialog({
        autoOpen: false,
        show: "fadeIn",
        modal: true,
        buttons: {
            "同意": function () {
                if ($("#checkcontent").val() == "") {
                    alert("填写审批意见");
                    return;
                }
                $.post("/saveChangeCheck.do",{checkStatus:1,
                    checkContent:$("#checkcontent").val(),
                    changeId:$("#changeId").val()
                },function(result){
                    alert("成功提交");
                    location.reload();
                });

                $(this).dialog("close");
            },
            "不同意": function () {
                if ($("#checkcontent").val() == "") {
                    alert("填写审批意见");
                    return;
                }
                $.post("/saveChangeCheck.do",{checkStatus:2,
                    checkContent:$("#checkcontent").val(),
                    changeId:$("#changeId").val()
                },function(result){
                    alert("成功提交");
                    location.reload();
                });
                $(this).dialog("close");
            }
        }
    });
}

