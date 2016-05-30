/**
 * Created by criss on 16/5/4.
 */

indexApp.controller('config', function($scope, $routeParams,$http) {
    var appId = $routeParams.appId;
    var childAppId = $routeParams.childAppId;
    $scope.toAppConfig = function()
    {
        location.href = "#config/"+appId+"/0";
    }
    $scope.toCommon = function()
    {
        location.href = "#config/"+appId+"/COMMON";
    }
    $scope.toRunlist = function()
    {
        location.href = "#runlist/"+appId+"/0";
    }

    $scope.clickMe = function(target)
    {
        //console.info(target);
        var status = $(target).parent().find(".top_tooltip").css("display");
        if (status == "block"){
            $(target).parent().find(".top_tooltip").css("display", "none");
        }else{
            $(target).parent().find(".top_tooltip").css("display", "block");
        }
        $(".addGroup").fancybox({"height":180});
        $(".addConfig").fancybox({"height":380});
        $(".commonRel").fancybox();
    }

    window.setTimeout("$(\".commonRel\").fancybox();",1000);


    $scope.commit = function (configId)
    {
        //加载列表
        $http({
            url: '/commitConfig.do',
            method: "POST",
            data: "configId="+configId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
            console.info(data);
            if(data.status)
            {
                location.reload();
            }else
            {
                alert(data.msg);
            }

        });
    }
    $scope.appId = appId;

    //加载列表
    $http({
        url: '/listBasic.do',
        method: "POST",
        data: "appId="+appId+"&childAppId="+childAppId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        console.info(data);
        $scope.data = data.data.configlist;
        $scope.isCommon = data.data.isCommon;
        $scope.childAppList = data.data.childApps;
        $scope.childAppId = data.data.childAppId;
    });

    $scope.deleteGroup = function(groupId)
    {
        if(confirm("你确定删除吗？"))
        {
            $.ajax({
                type: 'POST',
                url: "/deletegroup.do",
                data: {groupId:groupId},
                success: function(data){
                    if(data.status)
                    {
                        alert("删除成功");
                        location.reload();

                    }else{
                        alert("删除失败");
                    }
                },
                dataType: "json"
            });
        }
    }

    $scope.deleteConfig = function(configId)
    {
        if(confirm("你确定删除吗？"))
        {
            $.ajax({
                type: 'POST',
                url: "/deleteConfig.do",
                data: {configId:configId},
                success: function(data){
                    if(data.status)
                    {
                        alert("删除成功");
                        location.reload();

                    }else{
                        alert("删除失败");
                    }
                },
                dataType: "json"
            });
        }
    }


});


indexApp.controller('runlist', function($scope, $routeParams,$http) {

    var appId = $routeParams.appId;
    var childAppId = $routeParams.childAppId;

    $scope.toAppConfig = function()
    {
        location.href = "#config/"+appId+"/0";
    }
    $scope.toCommon = function()
    {
        location.href = "#config/"+appId+"/COMMON";
    }
    $scope.toRunlist = function()
    {
        location.href = "#runlist/"+appId+"/0";
    }

    //加载列表
    $http({
        url: '/listRun.do',
        method: "POST",
        data: "appId="+appId+"&childAppId="+childAppId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        console.info(data);
        $scope.data = data.data.list;
        $scope.appId = appId;
        $scope.childAppList = data.data.childApps;
        $scope.childAppId = data.data.childAppId;
        $scope.appCode = data.data.appCode;
    });

    window.setTimeout("$(\".iframe\").fancybox();",1000);

});

indexApp.controller('runval', function($scope, $routeParams,$http) {

    window.setTimeout("$(\".iframe\").fancybox();",1000);
    var childAppId = $routeParams.childAppId;
    var appId = $routeParams.appId;
    var runId = $routeParams.runId;

    $scope.toAppConfig = function()
    {
        location.href = "#config/"+appId+"/0";
    }
    $scope.toCommon = function()
    {
        location.href = "#config/"+appId+"/COMMON";
    }
    $scope.toRunlist = function()
    {
        location.href = "#runlist/"+appId+"/0";
    }

    //加载列表
    $http({
        url: '/listRunVal.do',
        method: "POST",
        data: "runId="+runId+"&childAppId="+childAppId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        console.info(data);
        $scope.data = data.data.configlist;
        $scope.childAppId = data.data.childAppId;
        $scope.runId = data.data.runId;
        $scope.runKey = data.data.runKey;
    });

    $scope.clickMe = function(target)
    {
        //console.info(target);
        var status = $(target).parent().find(".top_tooltip").css("display");
        if (status == "block"){
            $(target).parent().find(".top_tooltip").css("display", "none");
        }else{
            $(target).parent().find(".top_tooltip").css("display", "block");
        }
        $(".commonRel").fancybox();
    }

});
