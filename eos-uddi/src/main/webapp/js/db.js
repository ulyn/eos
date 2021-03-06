indexApp.controller('dblist', function ($scope, $routeParams, $http, $sce) {
    var appId = $routeParams.appId;
    var yw = $routeParams.yw;
    $scope.appId = appId;
    $scope.yw = yw;
    $http.post('/loadApp.do?appId=' + appId, {}).success(function (data) {
        if (data.status) {
            $scope.appname = data.data.appName;
            $scope.modules = data.data.modules;
        } else {
            alert(data.msg);
        }
    });

    $http({
              url: '/loadPdm.do',
              method: "POST",
              data: "appId=" + appId,
              headers: {'Content-Type': 'application/x-www-form-urlencoded'}
          }).success(function (data, status, headers, config) {
        $scope.pdm = data;
    });

    $scope.toServiceList = function () {
        location.href = "#servicelist/" + appId + "/0";
    };

    $scope.back = function () {
        location.href = "#applist/" + yw;
    };

    $scope.dbAddFun = function () {
        location.href = "#dbAdd/" + appId + "/ADD" + "/" + yw;
    };

    $scope.loadChange = function (changeId) {
        location.href = "#dbAdd/" + appId + "/" + changeId;
    };
    $scope.setHasSend = function (id) {
        //设置为已发包
        $http.post('/setHasSend.do?id=' + id, {}).success(function (data) {
            alert("设置成功！");
            $scope.getList("0");
        });

    }
    $scope.getList = function (status) {
        $http.post('/listDb.do?appId=' + appId + "&status=" + status, {}).success(function (data) {

            $scope.dbs = data;

            for (var i = 0; i < data.length; i++) {
                if (i % 2 == 0) {
                    data[i].rowclass = "odd";
                } else {
                    data[i].rowclass = "even";
                }

                if (data[i].checkStatus) {
                    data[i].checkColor = "green";
                } else {
                    data[i].checkColor = "red";
                }
                data[i].changeLog = $sce.trustAsHtml(data[i].changeLog);

                var version = data[i].version;
                var db = data[i].db;
                data[i].scriptName =
                    db + "." + version.substr(0, 8) + "_" + version.substr(8) + "_" + data[i].module + "_" + data[i].dbType + ".sql";

            }

        });
    };
    $scope.getList("0");

    $scope.unlock = function () {
        $http({
                  url: '/unlock.do',
                  method: "POST",
                  data: "appId=" + appId,
                  headers: {'Content-Type': 'application/x-www-form-urlencoded'}
              }).success(function (data, status, headers, config) {
            location.reload();
        });
    };

    $scope.downloadPdm = function () {
        location.href = "/downloadPdm.do?appId=" + appId;
    }
    $scope.downloadLockPdm = function () {
        // $scope.pdm.islock = true;
        window.setTimeout("location.reload();", 1000);
        location.href = "/downloadPdm.do?appId=" + appId + "&lock=" + 1;

    }
    $scope.downloadAllScript = function () {
        var begin = $("#begin").val();
        var end = $("#end").val();
        $http({
                  url: '/validateBatchDowlnload.do',
                  method: "POST",
                  data: "appId=" + appId + "&begin=" + begin + "&end=" + end,
                  headers: {'Content-Type': 'application/x-www-form-urlencoded'}
              }).success(function (data, status, headers, config) {
            if (!data.status) {
                alert(data.data);
            } else {
                location.href = "/batchDlownload.do?" + "appId=" + appId + "&begin=" + begin + "&end=" + end;
            }
        });
    }

});
indexApp.controller('dbAdd', function ($scope, $routeParams, $http, $sce) {
    $(".wysiwyg").cleditor({"width": 583});
    var appId = $routeParams.appId;
    var changeId = $routeParams.changeId;
    var yw = $routeParams.yw;
    if (changeId == "ADD") {
        changeId = "";
    }
    if (changeId != "") {
        $scope.add = false;
        //changeId = -1;
        $http({
                  url: '/loadDbChange.do',
                  method: "POST",
                  data: "changeId=" + changeId,
                  headers: {'Content-Type': 'application/x-www-form-urlencoded'}
              }).success(function (data, status, headers, config) {
            var changelog = data.changeLog;
            //$scope.changelog = changelog;
            var o = $("#changelog").cleditor()[0];
            $("#changelog").val(changelog);
            o.updateFrame();
            $scope.db = data.db;
            if (data.dbChecklistList != null) {
                for (var i = 0; i < data.dbChecklistList.length; i++) {
                    var content = data.dbChecklistList[i].checkContent;
                    content = content.replace("\n", "<br/>");
                    data.dbChecklistList[i].checkContent = $sce.trustAsHtml(content);
                }
            }
            $scope.module = data.module;
            $scope.dbType = data.dbType;
            $scope.checkList = data.dbChecklistList;
            $scope.checkStatus = data.checkStatus;
            $scope.scriptFileName = data.script;

            $scope.downloadPdm = function () {
                location.href = "downloadDb.do?name=pdm&changeId=" + changeId;
            }
            $scope.downloadScript = function () {
                location.href = "downloadDb.do?name=script&changeId=" + changeId;
            }

        });
    } else {
        $scope.add = true;
        changeId = "";
    }

    $scope.changeId = changeId;

    $scope.appId = appId;

    $scope.back = function () {
        location.href = "#dblist/" + appId + "/" + yw;
    };

    $http.post("/loginUser.do", {}).success(function (data) {
        var role = data.role;
        if (role == '1') {
            $scope.checkUser = false;
        } else {
            $scope.checkUser = true;
        }
    });

    $http({
              url: '/loadApp.do',
              method: "POST",
              data: "appId=" + appId,
              headers: {'Content-Type': 'application/x-www-form-urlencoded'}
          }).success(function (data, status, headers, config) {
        if (data.status) {
            var app = data.data;
            var dbs = app.dbs;
            var modules = app.modules;
            var dbsArr = dbs.split(",");
            var modulesArr = []
            modules.forEach(function (item) {
                modulesArr.push(item.moduleName);
            })

            $scope.pdmFileName = app.appCode + ".pdm";

            $scope.dbs = dbsArr;
            $scope.modulesArr = modulesArr;
            $scope.appCode = app.appCode;
        } else {
            alert(data.msg);
        }
    });

    $scope.savechange = function () {

        var changelog = $(".wysiwyg").val();
        var db = "";
        $("input[name='db']").each(
            function () {
                if ($(this).get(0).checked) {
                    db = $(this).get(0).getAttribute("value");
                }
            });

        var appId = $("#appId").val();
        if (changelog == "") {
            alert("请添加修订日志");
            return;
        }
        if (db == "") {
            alert("请选择所属库");
            return;
        }
        var module = "";
        $("input[name='module']").each(
            function () {
                if ($(this).get(0).checked) {
                    module = $(this).get(0).getAttribute("value");
                }
            });
        if (module == "") {
            alert("请选择模块");
            return;
        }

        var dbType = "";
        $("input[name='dbType']").each(
            function () {
                if ($(this).get(0).checked) {
                    dbType = $(this).get(0).getAttribute("value");
                }
            });
        if (dbType == "") {
            alert("请选择脚本类型");
            return;
        }

        if (changeId == "") {
            if ($("#pdm").val() == "") {
                alert("请上传PDM图");
                return;
            }
            if ($("#sql").val() == "") {
                alert("请上传脚本文件");
                return;
            }
        }

        changelog = encodeURIComponent(changelog);
        console.info(changelog);
        $http({
                  url: '/saveDbChange.do',
                  method: "POST",
                  data: "appId=" + appId + "&db=" + db + "&changelog=" + changelog + "&changeId=" + changeId + "&module=" + module
                        + "&dbType=" + dbType,
                  headers: {'Content-Type': 'application/x-www-form-urlencoded'}
              }).success(function (data, status, headers, config) {

            if (data.status) {
                alert("更新成功");
                location.href = "#dblist/" + appId + "/" + yw;
            } else {
                alert(data.msg);
            }

        });

    };

    $scope.commit = function () {
        $(".dConf").dialog("open");
    }

    $scope.dbTypeArr = [
        {"id": "SP", "label": "过程"},
        {"id": "TRG", "label": "触发器"},
        {"id": "T", "label": "表"},
        {"id": "T_SJCL", "label": "表数据处理"},
        {"id": "VW", "label": "视图"},
        {"id": "F", "label": "函数"}
    ];

    window.setTimeout("initDialog('" + appId + "','" + yw + "');", 200);

});

indexApp.controller('viewDbScript', function ($scope, $routeParams, $http) {
    var changeId = $routeParams.changeId;
    var appId = $routeParams.appId;
    var yw = $routeParams.yw;

    $http({
              url: '/getScript.do',
              method: "POST",
              data: "changeId=" + changeId,
              headers: {'Content-Type': 'application/x-www-form-urlencoded'}
          }).success(function (data, status, headers, config) {
        if (data.status) {
            //$scope.java = data.data;
            data.data = data.data.replace(/delete/g, "<font color='red'>delete</font>")
            data.data = data.data.replace(/DELETE/g, "<font color='red'>DELETE</font>")
            data.data = data.data.replace(/drop/g, "<font color='red'>drop</font>")
            data.data = data.data.replace(/DROP/g, "<font color='red'>DROP</font>")
            document.getElementById("content").innerHTML = data.data;
            hljs.tabReplace = '    ';
            hljs.initHighlightingOnLoad();

        } else {
            alert(data.msg);
        }

    }).error(function (data, status, headers, config) {

    });

    $scope.returnlist = function () {
        location.href = "#dblist/" + appId+"/"+yw;
    }

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
    if (fileId == "file1") {
        extFile = ".pdm";
    }
    if (fileId == "file2") {
        extFile = ".sql";
    }

    if ($(o).val().indexOf(extFile) == -1) {
        alert("请选择扩展名为" + extFile + "的文件");
        return;
    }

    $.ajaxFileUpload
    (
        {
            url: 'uploadDb.do?appId=' + appId + "&changeId=" + $("#changeId").val(), //用于文件上传的服务器端请求地址
            secureuri: false, //是否需要安全协议，一般设置为false
            fileElementId: fileId, //文件上传域的ID
            dataType: 'json', //返回值类型 一般设置为json
            success: function (data, status)  //服务器成功响应处理函数
            {
                console.info(data);
                var status = data.status;
                if (status) {
                    //alert(data.data);
                    alert("上传成功");
                    $("#" + fileId).next().val(data.data);
                    $("#" + fileId).next().next().html(data.data);
                } else {
                    alert("上传失败:" + data.msg);
                }
            },
            error: function (data, status, e)//服务器响应失败处理函数
            {
                alert(e);
            }
        }
    )
}
function initDialog(appId,yw) {
    $.fx.speeds._default = 400; // Adjust the dialog animation speed
    $(".ui-dialog,.ui-widget").remove();
    $(".dConf:gt(0)").remove();
    $(".dConf").dialog({
                           autoOpen: false,
                           show: "fadeIn",
                           modal: true,
                           close: function () {
                               //location.reload();
                               console.info("desc");
                           },
                           open: function () {
                               $("#checkcontent").val("");
                           },
                           buttons: {
                               "同意": function () {
                                   if ($("#checkcontent").val() == "") {
                                       alert("填写审批意见");
                                       return;
                                   }
                                   $.post("/saveChangeCheck.do", {
                                       checkStatus: 1,
                                       checkContent: $("#checkcontent").val(),
                                       changeId: $("#changeId").val()
                                   }, function (result) {
                                       alert("成功提交");
                                       //location.reload();
                                       location.href = "#dblist/" + appId + "/" + yw;
                                   });

                                   $(this).dialog("close");
                               },
                               "不同意": function () {
                                   if ($("#checkcontent").val() == "") {
                                       alert("填写审批意见");
                                       return;
                                   }
                                   $.post("/saveChangeCheck.do", {
                                       checkStatus: 2,
                                       checkContent: $("#checkcontent").val(),
                                       changeId: $("#changeId").val()
                                   }, function (result) {
                                       alert("成功提交");
                                       //location.reload();
                                       location.href = "#dblist/" + appId + "/" + yw;
                                   });
                                   $(this).dialog("close");
                               }
                           }
                       });
}

