var indexApp = angular.module('indexApp', ['ngResource', 'ngRoute']);

indexApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/applist', {
                templateUrl: 'templates/index/app.html',
                //template:'criss',
                controller: 'showApp'
            }).when('/appAdd', {
                templateUrl: 'templates/index/addApp.html',
                //template:'criss',
                controller: 'appAdd'
            }).when('/updateAdd/:id', {
                templateUrl: 'templates/index/addApp.html',
                //template:'criss',
                controller: 'updateAdd'
            }).when('/servicelist/:appId/:moduleId',{
                templateUrl: 'templates/service/service.html',
                //template:'criss',
                controller: 'servicelist'
            }).when('/serviceAdd/:appId',{
                templateUrl: 'templates/service/serviceAdd.html',
                //template:'criss',
                controller: 'serviceAdd'
            }).when('/delete/:appId/:serviceId',{
                templateUrl: 'templates/service/deleteService.html',
                //template:'criss',
                controller: 'serviceDelete'
            }).when('/method/:appId/:serviceId/:version',{
                templateUrl: 'templates/service/test.html',
                //template:'criss',
                controller: 'method'
            }).when('/getjava/:appId/:versionId',{
                templateUrl: 'templates/service/java.html',
                //template:'criss',
                controller: 'java'
            }).when('/userlist',{
                templateUrl: 'templates/user/user.html',
                //template:'criss',
                controller: 'userlist'
            }).when('/userEdit/:id',{
                templateUrl: 'templates/user/userAdd.html',
                //template:'criss',
                controller: 'userEdit'
            }).when('/blank',{
                templateUrl: 'templates/service/blank.html'
                //template:'criss',
                //controller: 'userEdit'
            }).when('/monitor',{
                templateUrl: 'templates/monitor/monitor.html',
                //template:'criss',
                controller: 'monitor'
            }).when('/monitorservice/:eosId',{
                templateUrl: 'templates/monitor/service.html',
                //template:'criss',
                controller: 'monitorservice'
            }).when('/import',{
                templateUrl: 'templates/index/import.html',
                //template:'criss',
                controller: 'import'
            }).when('/',{
                templateUrl: 'templates/service/blank.html'
                //template:'criss',
                //controller: 'userEdit'
            });
//            .otherwise({redirectTo: '/servicelist'});
    }]);


indexApp.controller('showApp', function($scope,$routeParams,$http) {

    $http.post('/applist.do', {}).success(function(data){
        //var d = data.data;
        console.info(data);
        if(data.status)
        {
            var applist = data.data;
            for(var i=0;i<applist.length;i++)
            {
                if(i%2==0)
                    applist[i].rowclass = "odd";
                else
                    applist[i].rowclass = "even";
            }
            $scope.applist = applist;
        }else
        {
            alert(data.msg);
        }
    });

    $scope.addApp = function()
    {
        location.href = "#appAdd";
    }

    $scope.edit = function(id)
    {
        location.href = "#updateAdd/"+id;
    }

    $scope.import = function()
    {
        location.href = "#import";
    }

    $scope.export = function()
    {
        var app = document.getElementsByName("selectApp");
        var selectApp = "";
        for(var i=0;i<app.length;i++)
        {
            if(app[i].checked)
            {
                selectApp+=app[i].value+",";
            }
        }
        if(selectApp=="")
        {
            alert("请选择APP");
            return;
        }else
        {
            selectApp = selectApp.substr(0,selectApp.length-1);
        }
        location.href = "/export.do?apps="+selectApp;
    }

    $scope.commitAllCommit = function(){
        $http({
            url: '/commitAllCommit.do',
            method: "GET",
            data: "",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("审批成功");

                }else
                {
                    alert(data.msg);
                }

            }).error(function (data, status, headers, config) {

            });
    }

});

indexApp.controller('import', function($scope,$routeParams,$http) {



});


indexApp.controller('appAdd', function($scope, $routeParams,$http) {



    $scope.saveApp = function(){
        //saveApp.do
        var app_en = $("#app_en").val();
        var app_cn = $("#app_cn").val();
        var app_modules = $("#app_modules").val();
        $http({
            url: '/saveApp.do',
            method: "POST",
            data: "app_en="+app_en+"&app_cn="+app_cn+"&app_modules="+app_modules,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("保存成功");
                    location.href="#applist";
                }else
                {
                    alert(data.msg);
                }
            }).error(function (data, status, headers, config) {

            });
    }
});

indexApp.controller('updateAdd', function($scope, $routeParams,$http) {
    var id = $routeParams.id;
    $http({
        url: '/loadApp.do',
        method: "POST",
        data: "appId="+id,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            if(data.status)
            {
                var app = data.data;
                var appId = app.appId;
                var appName = app.appName;
                var appCode = app.appCode;
                var modules = app.modules;
                var module = "";
                for(var i=0;i<modules.length;i++)
                {
                    module+=modules[i].moduleName+",";
                }
                if(module.length>0)
                {
                    if(module.charAt(module.length-1)==",")
                    {
                        module = module.substr(0,module.length-1);
                    }
                }
                $scope.appId = appId;
                $scope.appName = appName;
                $scope.appCode = appCode;
                $scope.module = module;

            }else
            {
                alert(data.msg);
            }
        });


    $scope.saveApp = function(){
        var app_en = $("#app_en").val();
        var app_cn = $("#app_cn").val();
        var app_modules = $("#app_modules").val();
        var id = $scope.appId;
        $http({
            url: '/updateApp.do',
            method: "POST",
            data: "id="+id+"&app_en="+app_en+"&app_cn="+app_cn+"&app_modules="+app_modules,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("保存成功");
                    location.href="#applist";
                }else
                {
                    alert(data.msg);
                }
            }).error(function (data, status, headers, config) {

            });
    };
});




indexApp.controller('appController', function($scope, $http,$rootScope) {

    $scope.init=function()
    {
        $http.post('/getUser.do', {}).success(function(data){
            if(data.status)
            {
                console.info(data.data);
                var d = data.data;
                $rootScope.loginUser = d.userName;

            $scope.user = d;
            }else
            {
                alert(data.msg);
            }
        });
    }

});

function clickMenu(obj)
{
    console.info(obj)
    var arr = $(".js-menu");
    for(var i=0;i<arr.length;i++)
    {
        if(obj==arr[i])
        {
            $(arr[i]).addClass("active_tab");
        }else
        {
            $(arr[i]).removeClass("active_tab");
        }
    }
}

//-----------服务-------------------------------------
indexApp.controller('servicelist', function($scope, $routeParams,$http) {
    var appId = $routeParams.appId;
    var moduleId = $routeParams.moduleId;
    if(appId==null || appId=="")
    {
        alert("必须包含appId")
        return;
    }
    if(appId==null)
    {
        appId = "";
    }
    if(moduleId==null)
    {
        moduleId = "";
    }
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

    $http.post('/servicelist.do?appId='+appId+"&module="+moduleId, {}).success(function(data){
        console.info(data);
        if(data.status)
        {
            var list = data.data;
            for(var i=0;i<list.length;i++)
            {
                if(i%2==0)
                    list[i].rowclass = "odd";
                else
                    list[i].rowclass = "even";

                if(list[i].test=="1")
                {
                    list[i].color = "green";
                }else
                {
                    list[i].color = "red";
                }
                //处理审批状态
                var versions = list[i].versions;
                for(var j=0;j<versions.length;j++)
                {
                    if(versions[j].status=="0")
                    {
                        versions[j].class = "nocommit";
                    }else
                    {
                        versions[j].class = "commit";
                    }
                }

            }
            $scope.serviceList = list;
        }else
        {
            alert(data.msg);
        }
    });

    $scope.test = function()
    {
        alert(serviceId);
    }

    $scope.addService = function()
    {
        location.href="#serviceAdd/"+appId;
    }

    $scope.pakage = function()
    {
//        $http.post('/downloadjar.do?appId='+appId, {}).success(function(data){
//            if(data.status)
//            {
//                alert("成功");
//            }else
//            {
//                alert(data.msg);
//            }
//        });
        location.href = '/downloadjar.do?appId='+appId;
    }
    $scope.publish = function () {
        $http({
            url: '/getNpmVersion.do?appId=' + appId,
            method: "get"
        }).success(function (data, status, headers, config) {
                if (data.status) {
                    var version = data.data.v;
                    var name = data.data.name;
                    var tip = "当前cnpm版本(" + name + " " + (version == "" ? "未发布" : version) + "),确定发布新版本吗？";
                    if (confirm(tip)) {
                        $http({
                            url: '/publishCNPM.do?appId=' + appId + "&v=" + version,
                            method: "get"
                        }).success(function (data) {
                                if (data.status) {
                                    //success
                                    alert("发布服务成功：" + data.data.v);
                                } else {
                                    alert("发布服务失败：" + data.msg);
                                }
                            });
                    }
                } else {
                    alert("查询版本异常:" + data.msg);
                }
            });
    }
});

indexApp.controller('serviceAdd', function($scope, $routeParams,$http) {

    var appId = $routeParams.appId;

    $http({
        url: '/loadApp.do',
        method: "POST",
        data: "appId="+appId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            if(data.status)
            {
                var app = data.data;
                var modules = app.modules;
                console.info(modules);
                $scope.modules = modules;
                //window.setTimeout("$(\".simple_form\").uniform();",200);
            }else
            {
                alert(data.msg);
            }
        });

    $scope.saveService = function()
    {
        $("#appId").val(appId);
        document.getElementById("serviceform").submit();
    }
    $scope.returnlist = function()
    {
        location.href = "#servicelist/"+appId+"/0";
    }

});

indexApp.controller('serviceDelete', function($scope, $routeParams,$http) {

    var appId = $routeParams.appId;
    var serviceId = $routeParams.serviceId;

    $scope.delete = function()
    {
        $http({
            url: '/delete.do',
            method: "POST",
            data: "serviceId="+serviceId,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
            if(data.status)
            {
                alert("删除成功");
                location.href = "#servicelist/"+appId+"/0";
                //window.setTimeout("$(\".simple_form\").uniform();",200);
            }else
            {
                alert(data.msg);
            }
        });
    }



    $scope.returnlist = function()
    {
        location.href = "#servicelist/"+appId+"/0";
    }

});

indexApp.controller('method', function($scope, $routeParams,$http) {
    var appId=$routeParams.appId;
    var serviceId=$routeParams.serviceId;
    var version = $routeParams.version;

    $scope.appId = appId;

    $http({
        url: '/getmothod.do',
        method: "POST",
        data: "appId="+appId+"&serviceId="+serviceId+"&version="+version,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            if(data.status)
            {
                var o = data.data;
                console.info(o);

                $scope.methods = o;
            }else
            {
                alert(data.msg);
            }

        }).error(function (data, status, headers, config) {

        });

    $scope.returnlist = function()
    {
        location.href = "#servicelist/"+appId+"/0";
    }

    $scope.saveMethod = function()
    {
        var arr = document.getElementsByTagName("textarea");
        var r = "";
        for(var i=0;i<arr.length;i++)
        {
            r += arr[i].name+"="+arr[i].value+"&";
        }
        if(r.length>0)
        {
            r = r.substr(0, r.length-1);
        }

        $http({
            url: '/saveMethod.do',
            method: "POST",
            data: r,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("保存成功");
                }else
                {
                    alert(data.msg);
                }

            }).error(function (data, status, headers, config) {

            });

    }

});

indexApp.controller('java', function($scope, $routeParams,$http) {
    var appId=$routeParams.appId;
    var versionId=$routeParams.versionId;

    $http({
        url: '/getJava.do',
        method: "POST",
        data: "versionId="+versionId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            if(data.status)
            {
                //$scope.java = data.data;
                document.getElementById("content").innerHTML = data.data;
                hljs.tabReplace = '    ';
                hljs.initHighlightingOnLoad();

            }else
            {
                alert(data.msg);
            }

        }).error(function (data, status, headers, config) {

        });

    $scope.returnlist = function()
    {
        location.href = "#servicelist/"+appId+"/0";
    }

    $scope.downloadJava = function()
    {
        location.href = "download.do?versionId="+versionId;
    }
    $scope.test = function()
    {
        $http({
            url: '/changeTest.do?versionId='+versionId,
            method: "GET",
            data: "",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("服务可测试");

                }else
                {
                    alert(data.msg);
                }

            }).error(function (data, status, headers, config) {

            });
    }

    $scope.commit = function()
    {
        $http({
            url: '/commit.do?versionId='+versionId,
            method: "GET",
            data: "",
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if(data.status)
                {
                    alert("审批成功");

                }else
                {
                    alert(data.msg);
                }

            }).error(function (data, status, headers, config) {

            });
    }

});


//-----------用户管理-------
indexApp.controller('userlist', function($scope, $routeParams,$http) {

    $http.post('/userlist.do', {}).success(function(data){
        //var d = data.data;
        console.info(data);
        if(data.status)
        {
            var userlist = data.data;
            for(var i=0;i<userlist.length;i++)
            {
                if(i%2==0)
                    userlist[i].rowclass = "odd";
                else
                    userlist[i].rowclass = "even";
            }
            $scope.userlist = userlist;
        }else
        {
            alert(data.msg);
        }
    });

});

indexApp.controller('userEdit', function($scope, $routeParams,$http) {

    var id = $routeParams.id;

    $http({
        url: '/userEdit.do',
        method: "POST",
        data: "id="+id,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            if(data.status)
            {
                var arr= ["","",""];
                if(data.data.role=="3")
                {
                    document.getElementById("gly").checked = true;
                }if(data.data.role=="1")
                {
                    document.getElementById("cxy").checked = true;
                }if(data.data.role=="2")
                {
                    document.getElementById("xzz").checked = true;
                }
                $scope.role = arr;
                $scope.user = data.data;
                var selectApp = data.data.userApps;
                var selectAppStr = "";
                for(var j=0;j<selectApp.length;j++)
                {
                    selectAppStr += (selectApp[j].app.appName)+",";
                    for (var x = 0, l = data.data.apps.length; x < l; x++) {
                        if (data.data.apps[x].appId == selectApp[j].app.appId) {
                            data.data.apps[x].checked = true;
                        }
                    }
                }
                $scope.selectApp = selectAppStr;
                $scope.apps = data.data.apps;
                window.setTimeout("$(\".simple_form\").uniform();",200);
            }else
            {
                alert(data.msg);
            }
        }).error(function (data, status, headers, config) {

        });

    $scope.updateUser = function()
    {
        var roles = document.getElementsByName("role");
        var role = "";
        for(var i=0;i<roles.length;i++)
        {
            if(roles[i].checked)
            {
                role = roles[i].value;
            }
        }

        var app = "";
        var apps = document.getElementsByName("app");
        for(var i=0;i<apps.length;i++)
        {
            if(apps[i].checked)
            {
                app += apps[i].value+",";
            }
        }

        if(app!="")
        {
            app = app.substr(0,app.length-1);
        }

        $http({
            url: '/updateUser.do',
            method: "POST",
            data: "id="+id+"&role="+role+"&apps="+app,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {

                if(data.status)
                {
                    alert("更新成功");
                    location.href = "#userlist";
                }else
                {
                    alert(data.msg);
                }

        });

    }



});


function upload(o,appId)
{
    if(o.status==false)
    {
        alert(o.msg);
    }else
    {
        alert("上传成功");
        //location.href = "/index.html#servicelist/"+appId+"/0";
    }
}

function test(o)
{
    alert(o);
}

function changemodule()
{
    var appId = document.getElementById("appId").value;
    location.href = "#servicelist/"+appId+"/"+document.getElementById("mo").value;
}
//----------------------监控

indexApp.controller('monitor', function($scope, $routeParams,$http) {
    $http({
        url: '/eosState.do',
        method: "POST",
        data: "",
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
            console.info(data.data);
            if(data.status==false)
            {
                alert(o.msg);
            }else
            {
                var k = data.data;
                for(var i=0;i< k.length;i++)
                {
                    if(k[i].status=="online")
                    {
                        k[i].class = "users_stats";
                    }else
                    {
                        k[i].class = "orders_stats";
                    }
                }
                $scope.eoss = k;
            }


    });

    $scope.click = function(i)
    {
        location.href="#monitorservice/"+($scope.eoss[i].eosId);
    }


});


indexApp.controller('monitorservice', function($scope, $routeParams,$http) {

    var eosId = $routeParams.eosId;

    $http({
        url: '/getServices.do',
        method: "POST",
        data: "eosId="+eosId,
        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    }).success(function (data, status, headers, config) {
        if(data.status)
        {
            var userlist = data.data;
            for(var i=0;i<userlist.length;i++)
            {
                if(i%2==0)
                    userlist[i].rowclass = "odd";
                else
                    userlist[i].rowclass = "even";
            }
            $scope.serviceList = userlist;
        }else
        {
            alert(data.msg);
        }
    });

    $scope.back = function()
    {
        location.href = "#monitor";
    }

});