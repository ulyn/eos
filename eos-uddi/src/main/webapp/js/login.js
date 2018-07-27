var sampleApp = angular.module('sampleApp', ['ngResource', 'ngRoute']);
sampleApp.run(function($rootScope) {
    $rootScope.signText = "Sign Up";
    $rootScope.login = function()
    {
        var username = $("#username").val();
        var pwd = $("#pwd").val();
        $.ajax({
            type : "post",
            url : "/login.do",
            data : "username="+username+"&pwd="+pwd,
            dataType: "json",
            success : function(data) {
                if(data.status)
                {
                    if(data.data.role=="" || data.data.role==null)
                    {
                        alert("你的账号还没有开通，请联系管理员");
                    }else
                    {
                        location.href="index.html#"+data.data.url;
                    }
                }else
                {
                    alert(data.msg);
                }
            }
        });
    }
    $rootScope.fixInput = function(e)
    {
        if(e.which == 13)
        {
            $rootScope.login();
        }
    }
});

sampleApp.controller('mainController', function($scope, $routeParams) {

    //$scope.order_id = $routeParams.orderId;
    $scope.clickSignUp = function()
    {
        //alert($scope.loginShow);
        $scope.loginShow = !$scope.loginShow;
        if($scope.loginShow)
        {
            $scope.signText = "Sign Up";
        }else
        {
            $scope.signText = "Login";
        }
    };

});

sampleApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/sign_up', {
                templateUrl: 'templates/signup.html',
                //template:'criss',
                controller: 'ShowOrderController'
            });
    }]);


sampleApp.controller('ShowOrderController', function($scope, $routeParams,$http) {

    $scope.saveUser = function()
    {
        var username = $('#regname').val();
        var pwd = $('#hehe').val();
        var email = $('#email').val();
        if(username == "")
        {
            alert("用户名不能空");
            return;
        }
        if(pwd == "")
        {
            alert("密码不能空");
            return;
        }
        if(email == "")
        {
            alert("email不能为空");
            return;
        }
        $http({
            url: '/saveUser.do',
            method: "POST",
            data: "username="+username+"&pwd="+pwd+"&email="+email,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {

                if(data.status)
                {
                    alert("保存成功,请联系管理员开通账号");
                    location.href="Login.html";
                }else
                {
                    alert(data.msg);
                }

            });

    }

});
